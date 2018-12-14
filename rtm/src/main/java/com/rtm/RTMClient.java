package com.rtm;

import com.fpnn.FPClient;
import com.fpnn.FPData;
import com.fpnn.FPPackage;
import com.fpnn.FPProcessor;
import com.fpnn.callback.CallbackData;
import com.fpnn.callback.FPCallback;
import com.fpnn.encryptor.FPEncryptor;
import com.fpnn.event.EventData;
import com.fpnn.event.FPEvent;
import com.fpnn.nio.ThreadPool;
import com.rtm.json.JsonHelper;
import com.rtm.msgpack.PayloadPacker;
import com.rtm.msgpack.PayloadUnpacker;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTMClient {

    private static class MidGenerator {

        static private long count = 0;

        static public synchronized long gen() {

            if (++count >= 999) {

                count = 0;
            }

            return Long.valueOf(String.valueOf(System.currentTimeMillis()).concat(String.valueOf(count)));
        }
    }

    private FPEvent _event = new FPEvent();
    private FPProcessor.IProcessor _processor = new RTMProcessor(_event);

    public FPEvent getEvent() {

        return this._event;
    }

    private String _dispatch;
    private int _pid;
    private long _uid;
    private String _token;
    private String _version;
    private boolean _recvUnreadMsg;
    private boolean _reconnect;
    private int _timeout;
    private boolean _startTimerThread;

    private String _endpoint;
    private boolean _ipv6;

    private String _curve;
    private byte[] _derKey;

    private boolean _isClose;

    private BaseClient _baseClient;
    private DispatchClient _dispatchClient;

    /**
     * @param {String}  dispatch
     * @param {int}     pid
     * @param {long}    uid
     * @param {String}  token
     * @param {String}  version
     * @param {boolean} recvUnreadMsg
     * @param {boolean} reconnect
     * @param {int}     timeout
     * @param {boolean} startTimerThread
     */
    public RTMClient(String dispatch, int pid, long uid, String token, String version, boolean recvUnreadMsg, boolean reconnect, int timeout, boolean startTimerThread) {

        this._dispatch = dispatch;
        this._pid = pid;
        this._uid = uid;
        this._token = token;
        this._version = version;
        this._recvUnreadMsg = recvUnreadMsg;
        this._reconnect = reconnect;
        this._timeout = timeout;
        this._startTimerThread = startTimerThread;
    }

    public FPProcessor.IProcessor getProcessor() {

        return this._processor;
    }

    public FPPackage getPackage() {

        if (this._baseClient != null) {

            return this._baseClient.getPackage();
        }

        return null;
    }

    public void sendQuest(FPData data, FPCallback.ICallback callback, int timeout) {

        if (this._baseClient != null) {

            this._baseClient.sendQuest(data, this._baseClient.questCallback(callback), timeout);
        }
    }

    public CallbackData sendQuest(FPData data, int timeout) throws InterruptedException {

        if (this._baseClient != null) {

            return this._baseClient.sendQuest(data, timeout);
        }

        return null;
    }

    public void destroy() {

        this.close();

        if (this._baseClient != null) {

            this._baseClient.destroy();
            this._baseClient = null;
        }

        if (this._dispatchClient != null) {

            this._dispatchClient.destroy();
            this._dispatchClient = null;
        }

        this._event.removeListener();
    }

    /**
     * @param {String}  endpoint
     * @param {boolean} ipv6
     */
    public void login(String endpoint, boolean ipv6) {

        this._endpoint = endpoint;
        this._ipv6 = ipv6;
        this._isClose = false;

        if (this._endpoint != null) {

            this.connectRTMGate(this._timeout);
            return;
        }

        if (this._dispatchClient == null) {

            this._dispatchClient = new DispatchClient(this._dispatch, this._timeout, this._startTimerThread);
        }

        Map payload = new HashMap();

        payload.put("pid", this._pid);
        payload.put("uid", this._uid);
        payload.put("what", "rtmGated");
        payload.put("addrType", this._ipv6 ? "ipv6" : "ipv4");
        payload.put("version", this._version);

        final RTMClient self = this;

        this._dispatchClient.which(payload, this._timeout, this._dispatchClient.questCallback(new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    self._dispatchClient.destroy();
                    self._dispatchClient = null;

                    String endpoint = (String) payload.get("endpoint");
                    self.login(endpoint, self._ipv6);
                } else {

                    self.reConnect();
                }
            }
        }));
    }

    /**
     * @param {String}  curve
     * @param {byte[]}  derKey
     * @param {String}  endpoint
     * @param {boolean} ipv6
     * @param {int}     timeout
     */
    public void login(String curve, byte[] derKey, String endpoint, boolean ipv6) {

        this._curve = curve;
        this._derKey = derKey;

        this.login(endpoint, ipv6);
    }

    /**
     * @param {long}                 to
     * @param {byte}                 mtype
     * @param {String}               msg
     * @param {String}               attrs
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendMessage(long to, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("to", to);
        payload.put("mid", mid != 0 ? mid : MidGenerator.gen());
        payload.put("mtype", mtype);
        payload.put("msg", msg);
        payload.put("attrs", attrs);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("sendmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final long fmid = (long) payload.get("mid");
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                cbd.setMid(fmid);

                if (cb != null) {

                    cb.callback(cbd);
                }
            }
        }, timeout);
    }

    /**
     * @param {List<Long>}           tos
     * @param {byte}                 mtype
     * @param {String}               msg
     * @param {String}               attrs
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendMessages(List<Long> tos, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("tos", tos);
        payload.put("mid", mid != 0 ? mid : MidGenerator.gen());
        payload.put("mtype", mtype);
        payload.put("msg", msg);
        payload.put("attrs", attrs);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("sendmsgs");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final long fmid = (long) payload.get("mid");
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                cbd.setMid(fmid);

                if (cb != null) {

                    cb.callback(cbd);
                }
            }
        }, timeout);
    }

    /**
     * @param {long                  gid
     * @param {byte}                 mtype
     * @param {String}               msg
     * @param {String}               attrs
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendGroupMessage(long gid, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("gid", gid);
        payload.put("mid", mid != 0 ? mid : MidGenerator.gen());
        payload.put("mtype", mtype);
        payload.put("msg", msg);
        payload.put("attrs", attrs);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("sendgroupmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final long fmid = (long) payload.get("mid");
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                cbd.setMid(fmid);

                if (cb != null) {

                    cb.callback(cbd);
                }
            }
        }, timeout);
    }

    /**
     * @param {long                  rid
     * @param {byte}                 mtype
     * @param {String}               msg
     * @param {String}               attrs
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendRoomMessage(long rid, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("rid", rid);
        payload.put("mid", mid != 0 ? mid : MidGenerator.gen());
        payload.put("mtype", mtype);
        payload.put("msg", msg);
        payload.put("attrs", attrs);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("sendroommsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final long fmid = (long) payload.get("mid");
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                cbd.setMid(fmid);

                if (cb != null) {

                    cb.callback(cbd);
                }
            }
        }, timeout);
    }

    /**
     *
     */
    public void close() {

        this._isClose = true;

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("bye");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final RTMClient self = this;
        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                self._baseClient.close();
            }
        }, 0);
    }

    /**
     * @param {Map}                  dict
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void addVariables(Map dict, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("var", dict);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("addvariables");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {List<Long>}           friends
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void addFriends(List<Long> friends, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("friends", friends);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("addfriends");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {List<Long>}           friends
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void deleteFriends(List<Long> friends, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("friends", friends);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("delfriends");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {List<Long>}        payload
     */
    public void getFriends(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getfriends");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {
                if (cb == null) {

                    return;
                }

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    List uids = (List<Long>) payload.get("uids");
                    cb.callback(new CallbackData(uids));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     * @param {long}                 gid
     * @param {List<Long>}           uids
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void addGroupMembers(long gid, List<Long> uids, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("gid", gid);
        payload.put("uids", uids);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("addgroupmembers");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {long}                 gid
     * @param {List<Long>}           uids
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void deleteGroupMembers(long gid, List<Long> uids, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("gid", gid);
        payload.put("uids", uids);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("delgroupmembers");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {long}                 gid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {List<Long>}        payload
     */
    public void getGroupMembers(long gid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("gid", gid);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getgroupmembers");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {
                if (cb == null) {

                    return;
                }

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    List<Long> uids = (List<Long>) payload.get("uids");
                    cb.callback(new CallbackData(uids));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {List<Long>}        payload
     */
    public void getUserGroups(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getusergroups");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {
                if (cb == null) {

                    return;
                }

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    List<Long> gids = (List<Long>) payload.get("gids");
                    cb.callback(new CallbackData(gids));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     * @param {long}                 rid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void enterRoom(long rid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("rid", rid);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("enterroom");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {long}                 rid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void leaveRoom(long rid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("rid", rid);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("leaveroom");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {List<Long>}        payload
     */
    public void getUserRooms(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getuserrooms");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {
                if (cb == null) {

                    return;
                }

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    List<Long> rids = (List<Long>) payload.get("rooms");
                    cb.callback(new CallbackData(rids));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     * @param {List<Long>}           uids
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {List<Long>}        payload
     */
    public void getOnlineUsers(List<Long> uids, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("uids", uids);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getonlineusers");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {
                if (cb == null) {

                    return;
                }

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    List<Long> uids = (List<Long>) payload.get("uids");
                    cb.callback(new CallbackData(uids));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map<p2p:List<Long>, group:List<Long>, bc:boolean>} payload
     */
    public void checkUnreadMessage(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("checkunreadmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {long}                 gid
     * @param {int}                  num
     * @param {boolean}              desc
     * @param {int}                  page
     * @param {long}                 localmid
     * @param {long}                 localid
     * @param {List<Byte>}           mtypes
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map<num:int, maxid:long, msgs:List<Map>>} payload
     */
    public void getGroupMessage(long gid, int num, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("gid", gid);
        payload.put("num", num);
        payload.put("desc", desc);
        payload.put("page", page);

        if (localmid != 0) {

            payload.put("localmid", localmid);
        }

        if (localid != 0) {

            payload.put("localid", localid);
        }

        if (mtypes != null) {

            payload.put("mtypes", mtypes);
        }

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getgroupmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {long}                 rid
     * @param {int}                  num
     * @param {boolean}              desc
     * @param {int}                  page
     * @param {long}                 localmid
     * @param {long}                 localid
     * @param {List<Byte>}           mtypes
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map<num:int, maxid:long, msgs:List<Map>>} payload
     */
    public void getRoomMessage(long rid, int num, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("rid", rid);
        payload.put("num", num);
        payload.put("desc", desc);
        payload.put("page", page);

        if (localmid != 0) {

            payload.put("localmid", localmid);
        }

        if (localid != 0) {

            payload.put("localid", localid);
        }

        if (mtypes != null) {

            payload.put("mtypes", mtypes);
        }

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getroommsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {int}                  num
     * @param {boolean}              desc
     * @param {int}                  page
     * @param {long}                 localmid
     * @param {long}                 localid
     * @param {List<Byte>}           mtypes
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map<num:int, maxid:long, msgs:List<Map>>} payload
     */
    public void getBroadcastMessage(int num, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("num", num);
        payload.put("desc", desc);
        payload.put("page", page);

        if (localmid != 0) {

            payload.put("localmid", localmid);
        }

        if (localid != 0) {

            payload.put("localid", localid);
        }

        if (mtypes != null) {

            payload.put("mtypes", mtypes);
        }

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getbroadcastmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {long}                 peeruid
     * @param {int}                  num
     * @param {int}                  direction
     * @param {boolean}              desc
     * @param {int}                  page
     * @param {long}                 localmid
     * @param {long}                 localid
     * @param {List<Byte>}           mtypes
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map<num:int, maxid:long, msgs:List<Map>>} payload
     */
    public void getP2PMessage(long peeruid, int num, int direction, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("ouid", peeruid);
        payload.put("num", num);
        payload.put("direction", direction);
        payload.put("desc", desc);
        payload.put("page", page);

        if (localmid != 0) {

            payload.put("localmid", localmid);
        }

        if (localid != 0) {

            payload.put("localid", localid);
        }

        if (mtypes != null) {

            payload.put("mtypes", mtypes);
        }

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getp2pmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {String}               apptype
     * @param {String}               devicetoken
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void addDevice(String apptype, String devicetoken, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("apptype", apptype);
        payload.put("devicetoken", devicetoken);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("adddevice");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {String}               devicetoken
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void removeDevice(String devicetoken, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("devicetoken", devicetoken);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("removedevice");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {String}               targetLanguage
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void setTranslationLanguage(String targetLanguage, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("lang", targetLanguage);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("setlang");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {String}               originalMessage
     * @param {String}               originalLanguage
     * @param {String}               targetLanguage
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map<stext:String, src:String, dtext:String, dst:String>} payload
     */
    public void translate(String originalMessage, String originalLanguage, String targetLanguage, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("text", originalMessage);
        payload.put("dst", targetLanguage);

        if (originalLanguage != null) {

            payload.put("src", originalLanguage);
        }

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("translate");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {double}               lat
     * @param {double}               lng
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void setGeo(double lat, double lng, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("lat", lat);
        payload.put("lng", lng);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("setgeo");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map<lat:double, lng:double>} payload
     */
    public void getGeo(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getgeo");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     * @param {List<Long>}           uids
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {List<ArrayList<Int64BE, double, double>>} payload
     */
    public void getGeos(List<Long> uids, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("uids", uids);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getgeos");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        final FPCallback.ICallback cb = callback;

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {
                if (cb == null) {

                    return;
                }

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    List<ArrayList> geos = (List<ArrayList>) payload.get("geos");
                    cb.callback(new CallbackData(geos));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     * @param {byte}                 mtype
     * @param {long}                 to
     * @param {byte[]}               fileBytes
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendFile(byte mtype, long to, byte[] fileBytes, long mid, int timeout, FPCallback.ICallback callback) {

        if (fileBytes == null || fileBytes.length <= 0) {

            this.getEvent().fireEvent(new EventData(this, "error", new Exception("empty file bytes!")));
            return;
        }

        Map ops = new HashMap();

        ops.put("cmd", "sendfile");
        ops.put("to", to);
        ops.put("mtype", mtype);
        ops.put("file", fileBytes);

        this.fileSendProcess(ops, mid, timeout, callback);
    }

    /**
     * @param {byte}                 mtype
     * @param {List<Long>}           tos
     * @param {byte[]}               fileBytes
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendFiles(byte mtype, List<Long> tos, byte[] fileBytes, long mid, int timeout, FPCallback.ICallback callback) {

        if (fileBytes == null || fileBytes.length <= 0) {

            this.getEvent().fireEvent(new EventData(this, "error", new Exception("empty file bytes!")));
            return;
        }

        Map ops = new HashMap();

        ops.put("cmd", "sendfiles");
        ops.put("tos", tos);
        ops.put("mtype", mtype);
        ops.put("file", fileBytes);

        this.fileSendProcess(ops, mid, timeout, callback);
    }

    /**
     * @param {byte}                 mtype
     * @param {long}                 gid
     * @param {byte[]}               fileBytes
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendGroupFile(byte mtype, long gid, byte[] fileBytes, long mid, int timeout, FPCallback.ICallback callback) {

        if (fileBytes == null || fileBytes.length <= 0) {

            this.getEvent().fireEvent(new EventData(this, "error", new Exception("empty file bytes!")));
            return;
        }

        Map ops = new HashMap();

        ops.put("cmd", "sendgroupfile");
        ops.put("gid", gid);
        ops.put("mtype", mtype);
        ops.put("file", fileBytes);

        this.fileSendProcess(ops, mid, timeout, callback);
    }

    /**
     * @param {byte}                 mtype
     * @param {long}                 rid
     * @param {byte[]}               fileBytes
     * @param {long}                 mid
     * @param {int}                  timeout
     * @param {FPCallback.ICallback} callback
     * @callback {Map}               payload
     */
    public void sendRoomFile(byte mtype, long rid, byte[] fileBytes, long mid, int timeout, FPCallback.ICallback callback) {

        if (fileBytes == null || fileBytes.length <= 0) {

            this.getEvent().fireEvent(new EventData(this, "error", new Exception("empty file bytes!")));
            return;
        }

        Map ops = new HashMap();

        ops.put("cmd", "sendroomfile");
        ops.put("rid", rid);
        ops.put("mtype", mtype);
        ops.put("file", fileBytes);

        this.fileSendProcess(ops, mid, timeout, callback);
    }

    public void connect(String endpoint, int timeout) {

        this._endpoint = endpoint;

        if (this._baseClient != null && this._baseClient.isOpen()) {

            this._baseClient.close();
            return;
        }

        this._baseClient = new BaseClient(this._endpoint, false, timeout, this._startTimerThread);

        final RTMClient self = this;
        final int ftimeout = timeout;
        FPEvent.IListener listener = new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData event) {

                switch (event.getType()) {
                    case "connect":
                        self._baseClient.getProcessor().getEvent().addListener(RTMConfig.SERVER_PUSH.kickOut, new FPEvent.IListener() {

                            @Override
                            public void fpEvent(EventData event) {

                                self._isClose = true;
                            }
                        });
                        self.getEvent().fireEvent(new EventData(this, "connect"));
                        break;
                    case "close":
                        self.getEvent().fireEvent(new EventData(this, "close", !self._isClose && self._reconnect));
                        self._baseClient.getEvent().removeListener();
                        self.reConnect();
                        break;
                    case "error":
                        self.getEvent().fireEvent(new EventData(this, "error", event.getException()));
                        break;
                }
            }
        };

        this._baseClient.getEvent().addListener("connect", listener);
        this._baseClient.getEvent().addListener("close", listener);
        this._baseClient.getEvent().addListener("error", listener);

        this._baseClient.getProcessor().setProcessor(this._processor);

        if (this._derKey != null && this._curve != null) {

            this._baseClient.enableEncryptorByData(this._curve, this._derKey, false, false);
        } else {

            this._baseClient.enableConnect();
        }
    }

    private void fileSendProcess(Map ops, long mid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("cmd", ops.get("cmd"));

        if (ops.containsKey("tos")) {

            payload.put("tos", ops.get("tos"));
        }

        if (ops.containsKey("to")) {

            payload.put("to", ops.get("to"));
        }

        if (ops.containsKey("rid")) {

            payload.put("rid", ops.get("rid"));
        }

        if (ops.containsKey("gid")) {

            payload.put("gid", ops.get("gid"));
        }

        final RTMClient self = this;
        final long fmid = mid;
        final int ftimeout = timeout;
        final Map fops = ops;
        final FPCallback.ICallback cb = callback;

        this.filetoken(payload, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Exception exception = cbd.getException();

                if (exception != null) {

                    self.getEvent().fireEvent(new EventData(self, "error", exception));
                    return;
                }

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    String token = (String) payload.get("token");
                    String endpoint = (String) payload.get("endpoint");


                    if (token.isEmpty() || endpoint.isEmpty()) {

                        self.getEvent().fireEvent(new EventData(self, "error", new Exception(obj.toString())));
                        return;
                    }


                    FileClient fileClient = new FileClient(endpoint, ftimeout, false);

                    payload = new HashMap();

                    payload.put("pid", self._pid);
                    payload.put("mtype", fops.get("mtype"));
                    payload.put("mid", fmid != 0 ? fmid : MidGenerator.gen());
                    payload.put("from", self._uid);

                    if (fops.containsKey("tos")) {

                        payload.put("tos", fops.get("tos"));
                    }

                    if (fops.containsKey("to")) {

                        payload.put("to", fops.get("to"));
                    }

                    if (fops.containsKey("rid")) {

                        payload.put("rid", fops.get("rid"));
                    }

                    if (fops.containsKey("gid")) {

                        payload.put("gid", fops.get("gid"));
                    }

                    fileClient.send((String) fops.get("cmd"), (byte[]) fops.get("file"), token, payload, ftimeout, cb);
                }
            }
        }, timeout);
    }

    private void filetoken(Map payload, FPCallback.ICallback callback, int timeout) {

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("filetoken");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    private void reConnect() {

        if (!this._reconnect) {

            return;
        }

        if (this._isClose) {

            return;
        }

        this.login(this._endpoint, this._ipv6);
    }

    private void connectRTMGate(int timeout) {

        if (this._baseClient != null && this._baseClient.isOpen()) {

            this._baseClient.close();
            return;
        }

        this._baseClient = new BaseClient(this._endpoint, false, timeout, this._startTimerThread);

        final RTMClient self = this;
        final int ftimeout = timeout;
        FPEvent.IListener listener = new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData event) {

                switch (event.getType()) {
                    case "connect":
                        self._baseClient.getProcessor().getEvent().addListener(RTMConfig.SERVER_PUSH.kickOut, new FPEvent.IListener() {

                            @Override
                            public void fpEvent(EventData event) {

                                self._isClose = true;
                                self._baseClient.close();
                            }
                        });
                        self.auth(ftimeout);
                        break;
                    case "close":
                        self.getEvent().fireEvent(new EventData(this, "close", !self._isClose && self._reconnect));
                        self._baseClient.getEvent().removeListener();
                        self.reConnect();
                        break;
                    case "error":
                        self.getEvent().fireEvent(new EventData(this, "error", event.getException()));
                        break;
                }
            }
        };

        this._baseClient.getEvent().addListener("connect", listener);
        this._baseClient.getEvent().addListener("close", listener);
        this._baseClient.getEvent().addListener("error", listener);

        this._baseClient.getProcessor().setProcessor(this._processor);

        if (this._derKey != null && this._curve != null) {

            this._baseClient.enableEncryptorByData(this._curve, this._derKey, false, false);
        } else {

            this._baseClient.enableConnect();
        }
    }

    private void auth(int timeout) {

        Map payload = new HashMap();

        payload.put("pid", this._pid);
        payload.put("uid", this._uid);
        payload.put("token", this._token);
        payload.put("version", this._version);
        payload.put("unread", this._recvUnreadMsg);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("auth");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final RTMClient self = this;
        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Exception exception = cbd.getException();

                if (exception != null) {

                    self._baseClient.close(exception);
                    return;
                }

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    boolean ok = (boolean) payload.get("ok");

                    if (ok) {

                        self.getEvent().fireEvent(new EventData(this, "login", self._endpoint));
                        return;
                    }

                    String gate = (String) payload.get("gate");

                    if (gate != null) {

                        self._endpoint = gate;
                        self._baseClient.close();
                        return;
                    }

                    if (!ok) {

                        self.getEvent().fireEvent(new EventData(this, "login", new Exception("token error!")));
                        return;
                    }
                }

                self.getEvent().fireEvent(new EventData(this, "error", new Exception(obj.toString())));
            }
        }, timeout);
    }
}

class DispatchClient extends BaseClient {

    public DispatchClient(String endpoint, int timeout, boolean startTimerThread) {

        super(endpoint, false, timeout, startTimerThread);
    }

    public DispatchClient(String host, int port, int timeout, boolean startTimerThread) {

        super(host, port, false, timeout, startTimerThread);
    }

    @Override
    protected void init(String host, int port, boolean reconnect, int timeout) {

        super.init(host, port, reconnect, timeout);

        final DispatchClient self = this;
        FPEvent.IListener listener = new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData event) {

                switch (event.getType()) {
                    case "connect":
                        self.onConnect();
                        break;
                    case "close":
                        self.onClose();
                        break;
                    case "error":
                        self.onException(event.getException());
                        break;
                }
            }
        };

        this.getEvent().addListener("connect", listener);
        this.getEvent().addListener("close", listener);
        this.getEvent().addListener("error", listener);
    }

    public void which(Map payload, int timeout, FPCallback.ICallback callback) {

        if (!this.hasConnect()) {

            this.connect();
        }

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("which");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, this.questCallback(callback), timeout);
    }

    private void onConnect() {

        System.out.println("Dispatch client connected!");
    }

    private void onClose() {

        System.out.println("Dispatch client closed!");
        this.destroy();
    }

    private void onException(Exception ex) {

        ex.printStackTrace();
    }
}

class FileClient extends BaseClient {

    public FileClient(String endpoint, int timeout, boolean startTimerThread) {

        super(endpoint, false, timeout, startTimerThread);
    }

    public FileClient(String host, int port, int timeout, boolean startTimerThread) {

        super(host, port, false, timeout, startTimerThread);
    }

    @Override
    protected void init(String host, int port, boolean reconnect, int timeout) {

        super.init(host, port, reconnect, timeout);

        final FileClient self = this;
        FPEvent.IListener listener = new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData event) {
                switch (event.getType()) {
                    case "connect":
                        self.onConnect();
                        break;
                    case "close":
                        self.onClose();
                        break;
                    case "error":
                        self.onException(event.getException());
                        break;
                }
            }
        };

        this.getEvent().addListener("connect", listener);
        this.getEvent().addListener("close", listener);
        this.getEvent().addListener("error", listener);
    }

    public void send(String method, byte[] fileBytes, String token, Map payload, int timeout, FPCallback.ICallback callback) {

        String fileMd5 = this.md5(fileBytes).toLowerCase();
        String sign = this.md5(fileMd5.concat(":").concat(token)).toLowerCase();

        if (sign.isEmpty()) {

            this.getEvent().fireEvent(new EventData(this, "error", new Exception("wrong sign!")));
            return;
        }

        if (!this.hasConnect()) {

            this.connect();
        }

        JsonHelper.IJson json = JsonHelper.getInstance().getJson();
        Map attrs = new HashMap();
        attrs.put("sign", sign);

        payload.put("token", token);
        payload.put("file", fileBytes);
        payload.put("attrs", json.toJSON(attrs));


        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod(method);

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final FPCallback.ICallback cb = callback;
        final FileClient self = this;
        final long fmid = (long) payload.get("mid");

        this.sendQuest(data, this.questCallback(new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                cbd.setMid(fmid);
                self.destroy();

                if (cb != null) {

                    cb.callback(cbd);
                }
            }
        }), timeout);
    }

    private void onConnect() {

        System.out.println("File client connected!");
    }

    private void onClose() {

        System.out.println("File client closed!");
        this.destroy();
    }

    private void onException(Exception ex) {

        ex.printStackTrace();
    }
}

class BaseClient extends FPClient {

    public BaseClient(String endpoint, boolean reconnect, int timeout, boolean startTimerThread) {

        super(endpoint, reconnect, timeout);

        if (startTimerThread) {

            ThreadPool.getInstance().startTimerThread();
        }
    }

    public BaseClient(String host, int port, boolean reconnect, int timeout, boolean startTimerThread) {

        super(host, port, reconnect, timeout);

        if (startTimerThread) {

            ThreadPool.getInstance().startTimerThread();
        }
    }

    @Override
    protected void init(String host, int port, boolean reconnect, int timeout) {

        super.init(host, port, reconnect, timeout);
    }

    public void enableEncryptorByData(String curve, byte[] derKey, boolean streamMode, boolean reinforce) {

        if (derKey != null && derKey.length > 0) {

            if (this.encryptor(curve, derKey, streamMode, reinforce, true)) {

                this.connect(new FPClient.IKeyData() {

                    @Override
                    public FPData getKeyData(FPEncryptor encryptor) {

                        byte[] bytes = new byte[0];
                        PayloadPacker packer = new PayloadPacker();

                        Map map = new HashMap();

                        map.put("publicKey", encryptor.cryptoInfo().selfPublicKey);
                        map.put("streamMode", encryptor.cryptoInfo().streamMode);
                        map.put("bits", encryptor.cryptoInfo().keyLength);

                        try {

                            packer.pack(map);
                            bytes = packer.toByteArray();
                        } catch (IOException ex) {

                            ex.printStackTrace();
                        }

                        FPData data = new FPData();

                        if (bytes.length > 0) {

                            data.setPayload(bytes);
                        }

                        return data;
                    }
                });

                return;
            }
        }

        this.enableConnect();
    }

    public void enableConnect() {

        this.connect();
    }

    public String md5(byte[] bytes) {

        byte[] md5Binary = new byte[0];

        try {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            md5Binary = md5.digest();
        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }

        return this.bytesToHexString(md5Binary, false);
    }

    public String md5(String str) {

        byte[] md5Binary = new byte[0];

        try {

            byte[] bytes = str.getBytes("UTF-8");

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            md5Binary = md5.digest();
        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }

        return this.bytesToHexString(md5Binary, false);
    }

    public String bytesToHexString(byte[] bytes, boolean isLowerCase) {

        String from = isLowerCase ? "%02x" : "%02X";
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);

        for (byte b : bytes) {

            formatter.format(from, b);
        }

        return sb.toString();
    }

    private void checkFPCallback(CallbackData cbd) {

        Map payload = null;
        FPData data = cbd.getData();

        Boolean isAnswerException = false;

        if (data != null) {

            if (data.getFlag() == 0) {

                JsonHelper.IJson json = JsonHelper.getInstance().getJson();
                payload = json.toMap(data.jsonPayload());
            }

            if (data.getFlag() == 1) {

                PayloadUnpacker unpacker = new PayloadUnpacker(data.msgpackPayload());

                try {

                    payload = unpacker.unpack();
                } catch (IOException ex) {

                    ex.printStackTrace();
                }
            }

            if (this.getPackage().isAnswer(data)) {

                isAnswerException = data.getSS() != 0;
            }
        }

        cbd.checkException(isAnswerException, payload);
    }

    public FPCallback.ICallback questCallback(FPCallback.ICallback callback) {

        final BaseClient self = this;
        final FPCallback.ICallback cb = callback;

        return new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                if (cb == null) {

                    return;
                }

                self.checkFPCallback(cbd);
                cb.callback(cbd);
            }
        };
    }
}
