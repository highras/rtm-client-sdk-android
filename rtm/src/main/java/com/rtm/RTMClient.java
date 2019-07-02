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

import java.util.*;
import java.lang.StringBuilder;
import java.security.MessageDigest;

public class RTMClient {

    private static class MidGenerator {

        static private long count = 0;
        static private StringBuilder sb = new StringBuilder(20);

        static public synchronized long gen() {

            long c = 0;

            if (++count >= 999) {

                count = 0;
            }

            c = count;

            sb.setLength(0);
            sb.append(System.currentTimeMillis());

            if (c < 100) {

                sb.append("0");
            }

            if (c < 10) {

                sb.append("0");
            }

            sb.append(c);

            return Long.valueOf(sb.toString());
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
    private Map<String, String> _attrs;
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
     * @param {String}                  dispatch
     * @param {int}                     pid
     * @param {long}                    uid
     * @param {String}                  token
     * @param {String}                  version
     * @param {Map(String,String)}      attrs
     * @param {boolean}                 reconnect
     * @param {int}                     timeout
     * @param {boolean}                 startTimerThread
     */
    public RTMClient(String dispatch, int pid, long uid, String token, String version, Map<String, String> attrs, boolean reconnect, int timeout, boolean startTimerThread) {

        this._dispatch = dispatch;
        this._pid = pid;
        this._uid = uid;
        this._token = token;
        this._version = version;
        this._attrs = attrs;
        this._reconnect = reconnect;
        this._timeout = timeout;
        this._startTimerThread = startTimerThread;

        this.initProcessor();
    }

    private void initProcessor() {

        final RTMClient self = this;
        this._processor.getEvent().addListener(RTMConfig.SERVER_PUSH.kickOut, new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self._isClose = true;
                self._baseClient.close();
            }
        });
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

            this._baseClient.sendQuest(data, callback, timeout);
        }
    }

    public CallbackData sendQuest(FPData data, int timeout) {

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

        if (this._endpoint != null && !this._endpoint.isEmpty()) {

            this.connectRTMGate(this._timeout);
            return;
        }

        final RTMClient self = this;

        if (this._dispatchClient == null) {

            this._dispatchClient = new DispatchClient(this._dispatch, this._timeout, this._startTimerThread);

            this._dispatchClient.getEvent().addListener("close", new FPEvent.IListener() {

                @Override
                public void fpEvent(EventData evd) {

                    System.out.println("[DispatchClient] closed!");

                    if (self._dispatchClient != null) {

                        self._dispatchClient.destroy();
                        self._dispatchClient = null;
                    }

                    if (self._endpoint == null || self._endpoint.isEmpty()) {

                        self.getEvent().fireEvent(new EventData(self, "error", new Exception("dispatch client close with err!")));
                        self.reConnect();
                    }
                }
            });
        }

        Map payload = new HashMap();

        payload.put("pid", this._pid);
        payload.put("uid", this._uid);
        payload.put("what", "rtmGated");
        payload.put("addrType", this._ipv6 ? "ipv6" : "ipv4");
        payload.put("version", this._version);

        this._dispatchClient.which(payload, this._timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Map payload = (Map) cbd.getPayload();

                if (payload != null) {

                    String endpoint = (String) payload.get("endpoint");
                    self.login(endpoint, self._ipv6);
                }

                self._dispatchClient.close(cbd.getException());
            }
        });
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
     *
     * rtmGate (2)
     *
     * @param {long}                    to
     * @param {byte}                    mtype
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {long}                    mid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map(mtime:long)}         payload
     * @param {Exception}               exception
     * @param {long}                    mid
     * </CallbackData>
     */
    public void sendMessage(long to, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback) {

        if (mid == 0) {

            mid = MidGenerator.gen();
        }

        Map payload = new HashMap();

        payload.put("to", to);
        payload.put("mid", mid);
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
        } catch (Exception ex) {

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
     * rtmGate (3)
     *
     * @param {long}                    gid
     * @param {byte}                    mtype
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {long}                    mid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map(mtime:long)}         payload
     * @param {Exception}               exception
     * @param {long}                    mid
     * </CallbackData>
     */
    public void sendGroupMessage(long gid, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback) {

        if (mid == 0) {

            mid = MidGenerator.gen();
        }

        Map payload = new HashMap();

        payload.put("gid", gid);
        payload.put("mid", mid);
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
        } catch (Exception ex) {

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
     * rtmGate (4)
     *
     * @param {long}                    rid
     * @param {byte}                    mtype
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {long}                    mid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map(mtime:long)}         payload
     * @param {Exception}               exception
     * @param {long}                    mid
     * </CallbackData>
     */
    public void sendRoomMessage(long rid, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback) {

        if (mid == 0) {

            mid = MidGenerator.gen();
        }

        Map payload = new HashMap();

        payload.put("rid", rid);
        payload.put("mid", mid);
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
        } catch (Exception ex) {

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
     * rtmGate (5)
     *
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {long}                    mid
     * @param {Exception}               exception
     * @param {Map(p2p:Map(String,int),group:Map(String,int))}         payload
     * </CallbackData>
     */
    public void getUnreadMessage(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getunreadmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (6)
     *
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {long}                    mid
     * @param {Exception}               exception
     * @param {Map}                     payload
     * </CallbackData>
     */
    public void cleanUnreadMessage(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("cleanunreadmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (7)
     *
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {long}                    mid
     * @param {Exception}               exception
     * @param {Map(p2p:Map(String,long),group:Map(String,long))}         payload
     * </CallbackData>
     */
    public void getSession(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getsession");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (8)
     *
     * @param {long}                    gid
     * @param {boolean}                 desc
     * @param {int}                     num
     * @param {long}                    begin
     * @param {long}                    end
     * @param {long}                    lastid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(num:int,lastid:long,begin:long,end:long,msgs:List(GroupMsg))} payload
     * </CallbackData>
     *
     * <GroupMsg>
     * @param {long}                    id
     * @param {long}                    from
     * @param {byte}                    mtype
     * @param {long}                    mid
     * @param {boolean}                 deleted
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {long}                    mtime
     * </GroupMsg>
     */
    public void getGroupMessage(long gid, boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("gid", gid);
        payload.put("desc", desc);
        payload.put("num", num);

        if (begin > 0) {

            payload.put("begin", begin);
        }

        if (end > 0) {

            payload.put("end", end);
        }

        if (lastid > 0) {

            payload.put("lastid", lastid);
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
        } catch (Exception ex) {

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

                    List list = (ArrayList) payload.get("msgs");

                    for (int i = 0; i < list.size(); i++) {

                        Map map = new HashMap();
                        List items = (ArrayList) list.get(i);

                        map.put("id", items.get(0));
                        map.put("from", items.get(1));
                        map.put("mtype", items.get(2));
                        map.put("mid", items.get(3));
                        map.put("deleted", items.get(4));
                        map.put("msg", items.get(5));
                        map.put("attrs", items.get(6));
                        map.put("mtime", items.get(7));

                        list.set(i, map);
                    }

                    cb.callback(new CallbackData(payload));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     *
     * ServerGate (9)
     *
     * @param {long}                    rid
     * @param {boolean}                 desc
     * @param {int}                     num
     * @param {long}                    begin
     * @param {long}                    end
     * @param {long}                    lastid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(num:int,lastid:long,begin:long,end:long,msgs:List(RoomMsg))} payload
     * </CallbackData>
     *
     * <RoomMsg>
     * @param {long}                    id
     * @param {long}                    from
     * @param {byte}                    mtype
     * @param {long}                    mid
     * @param {boolean}                 deleted
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {long}                    mtime
     * </RoomMsg>
     */
    public void getRoomMessage(long rid, boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("rid", rid);
        payload.put("desc", desc);
        payload.put("num", num);

        if (begin > 0) {

            payload.put("begin", begin);
        }

        if (end > 0) {

            payload.put("end", end);
        }

        if (lastid > 0) {

            payload.put("lastid", lastid);
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
        } catch (Exception ex) {

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

                    List list = (ArrayList) payload.get("msgs");

                    for (int i = 0; i < list.size(); i++) {

                        Map map = new HashMap();
                        List items = (ArrayList) list.get(i);

                        map.put("id", items.get(0));
                        map.put("from", items.get(1));
                        map.put("mtype", items.get(2));
                        map.put("mid", items.get(3));
                        map.put("deleted", items.get(4));
                        map.put("msg", items.get(5));
                        map.put("attrs", items.get(6));
                        map.put("mtime", items.get(7));

                        list.set(i, map);
                    }

                    cb.callback(new CallbackData(payload));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     *
     * ServerGate (10)
     *
     * @param {boolean}                 desc
     * @param {int}                     num
     * @param {long}                    begin
     * @param {long}                    end
     * @param {long}                    lastid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(num:int,lastid:long,begin:long,end:long,msgs:List(BroadcastMsg))} payload
     * </CallbackData>
     *
     * <BroadcastMsg>
     * @param {long}                    id
     * @param {long}                    from
     * @param {byte}                    mtype
     * @param {long}                    mid
     * @param {boolean}                 deleted
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {long}                    mtime
     * </BroadcastMsg>
     */
    public void getBroadcastMessage(boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("desc", desc);
        payload.put("num", num);

        if (begin > 0) {

            payload.put("begin", begin);
        }

        if (end > 0) {

            payload.put("end", end);
        }

        if (lastid > 0) {

            payload.put("lastid", lastid);
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
        } catch (Exception ex) {

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

                    List list = (ArrayList) payload.get("msgs");

                    for (int i = 0; i < list.size(); i++) {

                        Map map = new HashMap();
                        List items = (ArrayList) list.get(i);

                        map.put("id", items.get(0));
                        map.put("from", items.get(1));
                        map.put("mtype", items.get(2));
                        map.put("mid", items.get(3));
                        map.put("deleted", items.get(4));
                        map.put("msg", items.get(5));
                        map.put("attrs", items.get(6));
                        map.put("mtime", items.get(7));

                        list.set(i, map);
                    }

                    cb.callback(new CallbackData(payload));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     *
     * ServerGate (11)
     *
     * @param {long}                    ouid
     * @param {boolean}                 desc
     * @param {int}                     num
     * @param {long}                    begin
     * @param {long}                    end
     * @param {long}                    lastid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(num:int,lastid:long,begin:long,end:long,msgs:List(P2PMsg))} payload
     * </CallbackData>
     *
     * <P2PMsg>
     * @param {long}                    id
     * @param {byte}                    direction
     * @param {byte}                    mtype
     * @param {long}                    mid
     * @param {boolean}                 deleted
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {long}                    mtime
     * </P2PMsg>
     */
    public void getP2PMessage(long ouid, boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("ouid", ouid);
        payload.put("desc", desc);
        payload.put("num", num);

        if (begin > 0) {

            payload.put("begin", begin);
        }

        if (end > 0) {

            payload.put("end", end);
        }

        if (lastid > 0) {

            payload.put("lastid", lastid);
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
        } catch (Exception ex) {

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

                    List list = (ArrayList) payload.get("msgs");

                    for (int i = 0; i < list.size(); i++) {

                        Map map = new HashMap();
                        List items = (ArrayList) list.get(i);

                        map.put("id", items.get(0));
                        map.put("direction", items.get(1));
                        map.put("mtype", items.get(2));
                        map.put("mid", items.get(3));
                        map.put("deleted", items.get(4));
                        map.put("msg", items.get(5));
                        map.put("attrs", items.get(6));
                        map.put("mtime", items.get(7));

                        list.set(i, map);
                    }

                    cb.callback(new CallbackData(payload));
                    return;
                }

                cb.callback(cbd);
            }
        }, timeout);
    }

    /**
     *
     * ServerGate (12)
     *
     * @param {String}                  cmd
     * @param {List(Long)}              tos
     * @param {long}                    to
     * @param {long}                    rid
     * @param {long}                    gid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(token:string,endpoint:string)}   payload
     * </CallbackData>
     */
    public void fileToken(String cmd, List<Long> tos, long to, long rid, long gid, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("cmd", cmd);

        if (tos != null && tos.size() > 0) {

            payload.put("tos", tos);
        }

        if (to > 0) {

            payload.put("to", to);
        }

        if (rid > 0) {

            payload.put("rid", rid);
        }

        if (gid > 0) {

            payload.put("gid", gid);
        }

        this.filetoken(payload, callback, timeout);
    }


    /**
     * rtmGate (13)
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
        } catch (Exception ex) {

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
     *
     * rtmGate (14)
     *
     * @param {Map(String,String)}      attrs
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map}                     payload
     * </CallbackData>
     */
    public void addAttrs(Map<String, String> attrs, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("attrs", attrs);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("addattrs");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (15)
     *
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(attrs:List(Map))}    payload
     * </CallbackData>
     *
     * <Map>
     * @param {String}                  ce
     * @param {String}                  login
     * @param {String}                  my
     * </Map>
     */
    public void getAttrs(int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("getattrs");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (16)
     *
     * @param {String}                  msg
     * @param {String}                  attrs
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
     */
    public void addDebugLog(String msg, String attrs, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("msg", msg);
        payload.put("attrs", attrs);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("adddebuglog");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (17)
     *
     * @param {String}                  apptype
     * @param {String}                  devicetoken
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (18)
     *
     * @param {String}                  devicetoken
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (19)
     *
     * @param {String}                  targetLanguage
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (20)
     *
     * @param {String}                  originalMessage
     * @param {String}                  originalLanguage
     * @param {String}                  targetLanguage
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(stext:String,src:String,dtext:String,dst:String)}    payload
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (21)
     *
     * @param {List(Long)}              friends
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (22)
     *
     * @param {List(Long)}              friends
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (23)
     *
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {List(Long)}              payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

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
     *
     * rtmGate (24)
     *
     * @param {long}                    gid
     * @param {List(Long)}              uids
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (25)
     *
     * @param {long}                    gid
     * @param {List(Long)}              uids
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (26)
     *
     * @param {long}                    gid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {List(Long)}              payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

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
     *
     * rtmGate (27)
     *
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {List(Long)}              payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

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
     *
     * rtmGate (28)
     *
     * @param {long}                    rid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (29)
     *
     * @param {long}                    rid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (30)
     *
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {List(Long)}              payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

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
     *
     * rtmGate (31)
     *
     * @param {List(Long)}              uids
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {List(Long)}              payload
     * @param {Exception}               exception
     * </CallbackData>
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
        } catch (Exception ex) {

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
     *
     * rtmGate (32)
     *
     * @param {long}                    mid
     * @param {long}                    xid
     * @param {byte}                    type
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
     */
    public void deleteMessage(long mid, long xid, byte type, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("mid", mid);
        payload.put("xid", xid);
        payload.put("type", type);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("delmsg");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (33)
     *
     * @param {String}                  ce
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
     */
    public void kickout(String ce, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("ce", ce);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("kickout");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (34)
     *
     * @param {String}                  key
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Exception}               exception
     * @param {Map(val:String)}         payload
     * </CallbackData>
     */
    public void dbGet(String key, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("key", key);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("dbget");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * rtmGate (35)
     *
     * @param {String}                  key
     * @param {String}                  value
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map}                     payload
     * @param {Exception}               exception
     * </CallbackData>
     */
    public void dbSet(String key, String value, int timeout, FPCallback.ICallback callback) {

        Map payload = new HashMap();

        payload.put("key", key);
        payload.put("val", value);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("dbset");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    /**
     *
     * fileGate (1)
     *
     * @param {byte}                    mtype
     * @param {long}                    to
     * @param {byte[]}                  fileBytes
     * @param {long}                    mid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map(mtime:long)}         payload
     * @param {Exception}               exception
     * @param {long}                    mid
     * </CallbackData>
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
     *
     * fileGate (3)
     *
     * @param {byte}                    mtype
     * @param {long}                    gid
     * @param {byte[]}                  fileBytes
     * @param {long}                    mid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map(mtime:long)}         payload
     * @param {Exception}               exception
     * @param {long}                    mid
     * </CallbackData>
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
     *
     * fileGate (4)
     *
     * @param {byte}                    mtype
     * @param {long}                    rid
     * @param {byte[]}                  fileBytes
     * @param {long}                    mid
     * @param {int}                     timeout
     * @param {FPCallback.ICallback}    callback
     *
     * @callback
     * @param {CallbackData}            cbdata
     *
     * <CallbackData>
     * @param {Map(mtime:long)}         payload
     * @param {Exception}               exception
     * @param {long}                    mid
     * </CallbackData>
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

    // just for test
    public void connect(String endpoint, int timeout) {

        this._endpoint = endpoint;

        if (this._baseClient != null && this._baseClient.isOpen()) {

            this._baseClient.close();
            return;
        }

        this._baseClient = new BaseClient(this._endpoint, false, timeout, this._startTimerThread);

        final RTMClient self = this;

        this._baseClient.getEvent().addListener("connect", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.getEvent().fireEvent(new EventData(this, "connect"));
            }
        });

        this._baseClient.getEvent().addListener("close", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.getEvent().fireEvent(new EventData(this, "close", !self._isClose && self._reconnect));
            }
        });

        this._baseClient.getEvent().addListener("error", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.getEvent().fireEvent(new EventData(this, "error", evd.getException()));
            }
        });

        this._baseClient.getProcessor().setProcessor(this._processor);

        if (this._derKey != null && this._curve != null) {

            this._baseClient.connect(this._curve, this._derKey, false, false);
        } else {

            this._baseClient.connect();
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
        } catch (Exception ex) {

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

        if (this._baseClient != null) {

            this._baseClient.destroy();
        }

        this._baseClient = new BaseClient(this._endpoint, false, timeout, this._startTimerThread);

        final RTMClient self = this;
        final int ftimeout = timeout;

        this._baseClient.getEvent().addListener("connect", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.auth(ftimeout);
            }
        });

        this._baseClient.getEvent().addListener("close", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.getEvent().fireEvent(new EventData(this, "close", !self._isClose && self._reconnect));

                self._endpoint = null;
                self.reConnect();
            }
        });

        this._baseClient.getEvent().addListener("error", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.getEvent().fireEvent(new EventData(this, "error", evd.getException()));
            }
        });

        this._baseClient.getProcessor().setProcessor(this._processor);

        if (this._derKey != null && this._curve != null) {

            this._baseClient.connect(this._curve, this._derKey, false, false);
        } else {

            this._baseClient.connect();
        }
    }

    /**
     *
     * rtmGate (1)
     *
     */
    private void auth(int timeout) {

        Map payload = new HashMap();

        payload.put("pid", this._pid);
        payload.put("uid", this._uid);
        payload.put("token", this._token);
        payload.put("version", this._version);
        payload.put("attrs", this._attrs);

        FPData data = new FPData();
        data.setFlag(0x1);
        data.setMtype(0x1);
        data.setMethod("auth");

        byte[] bytes = new byte[0];
        PayloadPacker packer = new PayloadPacker();

        try {

            packer.pack(payload);
            bytes = packer.toByteArray();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final RTMClient self = this;
        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Exception exception = cbd.getException();

                if (exception != null) {

                    self.getEvent().fireEvent(new EventData(this, "error", exception));
                    self.reConnect();
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
                        self.reConnect();
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
    protected void addListener() {

        final DispatchClient self = this;

        this.getEvent().addListener("connect", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.onConnect();
            }
        });

        this.getEvent().addListener("error", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.onException(evd.getException());
            }
        });
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);
        this.sendQuest(data, callback, timeout);
    }

    private void onConnect() {

        System.out.println("[DispatchClient] connected!");
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
    protected void addListener() {

        final FileClient self = this;

        this.getEvent().addListener("connect", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.onConnect();
            }
        });

        this.getEvent().addListener("close", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.onClose();
            }
        });

        this.getEvent().addListener("error", new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData evd) {

                self.onException(evd.getException());
            }
        });
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
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        data.setPayload(bytes);

        final FPCallback.ICallback cb = callback;
        final FileClient self = this;
        final long fmid = (long) payload.get("mid");

        this.sendQuest(data, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                cbd.setMid(fmid);
                self.destroy();

                if (cb != null) {

                    cb.callback(cbd);
                }
            }
        }, timeout);
    }

    private void onConnect() {

        System.out.println("[FileClient] connected!");
    }

    private void onClose() {

        System.out.println("[FileClient] closed!");
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

        this.addListener();
    }

    public BaseClient(String host, int port, boolean reconnect, int timeout, boolean startTimerThread) {

        super(host, port, reconnect, timeout);

        if (startTimerThread) {

            ThreadPool.getInstance().startTimerThread();
        }

        this.addListener();
    }

    @Override
    public void sendQuest(FPData data, FPCallback.ICallback callback, int timeout) {

        super.sendQuest(data, this.questCallback(callback), timeout);
    }

    @Override
    public CallbackData sendQuest(FPData data, int timeout) {

        CallbackData cbd = null;

        try {

            cbd = super.sendQuest(data, timeout);
        }catch(Exception ex){

            this.getEvent().fireEvent(new EventData(this, "error", ex));
        }

        if (cbd != null) {

            this.checkFPCallback(cbd);
        }

        return cbd;
    }

    protected void addListener() {

    }

    public void connect(String curve, byte[] derKey, boolean streamMode, boolean reinforce) {

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
                        } catch (Exception ex) {

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
                } catch (Exception ex) {

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
