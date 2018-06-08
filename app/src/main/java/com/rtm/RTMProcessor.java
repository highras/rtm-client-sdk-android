package com.rtm;

import com.fpnn.FPData;
import com.fpnn.FPProcessor;
import com.fpnn.event.EventData;
import com.fpnn.nio.NIOCore;
import com.rtm.json.JsonHelper;
import com.rtm.msgpack.PayloadPacker;
import com.rtm.msgpack.PayloadUnpacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RTMProcessor implements FPProcessor.IProcessor {

    private FPProcessor _fppr;
    private Map _midMap = new HashMap();

    public RTMProcessor(FPProcessor fppr) {

        this._fppr = fppr;
    }

    @Override
    public void service(FPData data, FPProcessor.IAnswer answer) {

        Map payload = null;

        if (data.getFlag() == 0) {

            JsonHelper.IJson json = JsonHelper.getInstance().getJson();
            answer.sendAnswer(json.toJSON(new HashMap()), false);

            payload = json.toMap(data.jsonPayload());
        }

        if (data.getFlag() == 1) {

            byte[] bytes = new byte[0];
            PayloadPacker packer = new PayloadPacker();

            try {

                packer.pack(new HashMap());
                bytes = packer.toByteArray();
            } catch (IOException ex) {

                ex.printStackTrace();
            }

            if (bytes.length > 0) {

                answer.sendAnswer(bytes, false);
            }

            PayloadUnpacker unpacker = new PayloadUnpacker(data.msgpackPayload());

            try {

                payload = unpacker.unpack();
            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }

        if (payload != null) {

            switch (data.getMethod()) {

                case RTMConfig.SERVER_PUSH.kickOut:
                    this.kickout(payload);
                    break;
                case RTMConfig.SERVER_PUSH.kickOutRoom:
                    this.kickoutroom(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvMessage:
                    this.pushmsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvGroupMessage:
                    this.pushgroupmsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvRoomMessage:
                    this.pushroommsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvBroadcastMessage:
                    this.pushbroadcastmsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvTranslatedMessage:
                    this.transmsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvTranslatedGroupMessage:
                    this.transgroupmsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvTranslatedRoomMessage:
                    this.transroommsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvTranslatedBroadcastMessage:
                    this.transbroadcastmsg(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvUnreadMsgStatus:
                    this.pushunread(payload);
                    break;
                case RTMConfig.SERVER_PUSH.recvPing:
                    this.ping(payload);
                    break;
                default:
                    this._fppr.getEvent().fireEvent(new EventData(this, data.getMethod(), payload));
                    break;
            }
        }
    }

    /**
     * @param {Map} data
     */
    private void kickout(Map data) {

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.kickOut, data));
    }

    /**
     * @param {long} data.rid
     */
    private void kickoutroom(Map data) {

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.kickOutRoom, data));
    }

    /**
     * @param {int}    data.pid
     * @param {long}   data.from
     * @param {long}   data.to
     * @param {byte}   data.mtype
     * @param {byte}   data.ftype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     */
    private void pushmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(1, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        if ((byte) data.get("ftype") > 0) {

            this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvFile, data));
            return;
        }

        data.remove("ftype");
        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvMessage, data));
    }

    /**
     * @param {int}    data.pid
     * @param {long}   data.from
     * @param {long}   data.gid
     * @param {byte}   data.mtype
     * @param {byte}   data.ftype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     */
    private void pushgroupmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(2, (long) data.get("mid"), (long) data.get("from"), (long) data.get("gid"))) {

                return;
            }
        }

        if ((byte) data.get("ftype") > 0) {

            this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvGroupFile, data));
            return;
        }

        data.remove("ftype");
        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvGroupMessage, data));
    }

    /**
     * @param {int}    data.pid
     * @param {long}   data.from
     * @param {long}   data.rid
     * @param {byte}   data.mtype
     * @param {byte}   data.ftype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     */
    private void pushroommsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(3, (long) data.get("mid"), (long) data.get("from"), (long) data.get("rid"))) {

                return;
            }
        }

        if ((byte) data.get("ftype") > 0) {

            this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvRoomFile, data));
            return;
        }

        data.remove("ftype");
        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvRoomMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {byte}   data.mtype
     * @param {byte}   data.ftype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     */
    private void pushbroadcastmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(4, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        if ((byte) data.get("ftype") > 0) {

            this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvBroadcastFile, data));
            return;
        }

        data.remove("ftype");
        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvBroadcastMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {long}   data.mid
     * @param {long}   data.omid
     * @param {String} data.msg
     */
    private void transmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(1, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvTranslatedMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {long}   data.gid
     * @param {long}   data.mid
     * @param {long}   data.omid
     * @param {String} data.msg
     */
    private void transgroupmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(2, (long) data.get("mid"), (long) data.get("from"), (long) data.get("gid"))) {

                return;
            }
        }

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvTranslatedGroupMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {long}   data.rid
     * @param {long}   data.mid
     * @param {long}   data.omid
     * @param {String} data.msg
     */
    private void transroommsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(3, (long) data.get("mid"), (long) data.get("from"), (long) data.get("rid"))) {

                return;
            }
        }

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvTranslatedRoomMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {long}   data.mid
     * @param {long}   data.omid
     * @param {String} data.msg
     */
    private void transbroadcastmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(4, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvTranslatedBroadcastMessage, data));
    }

    /**
     * @param {List<Long>} data.p2p
     * @param {List<Long>} data.group
     * @param {boolean}    data.bc
     */
    private void pushunread(Map data) {

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvUnreadMsgStatus, data));
    }

    /**
     * @param {Map} data
     */
    private void ping(Map data) {

        this._fppr.getEvent().fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvPing, data));
    }

    @Override
    public void onSecond(long timestamp) {

        this.checkExpire(timestamp);
    }

    private boolean checkMid(int type, long mid, long uid, long rgid) {

        String key = String.valueOf(type);

        key = key.concat("_");
        key = key.concat(String.valueOf(mid));
        key = key.concat("_");
        key = key.concat(String.valueOf(uid));

        if (rgid > 0) {

            key = key.concat("_");
            key = key.concat(String.valueOf(rgid));
        }

        synchronized (this._midMap) {

            long timestamp = NIOCore.getInstance().getTimestamp();

            if (this._midMap.containsKey(key)) {

                long expire = (long) this._midMap.get(key);

                if (expire > timestamp) {

                    return false;
                }

                this._midMap.remove(key);
            }

            this._midMap.put(key, RTMConfig.MID_TTL + timestamp);
            return true;
        }
    }

    private void checkExpire(long timestamp) {

        synchronized (this._midMap) {

            List keys = new ArrayList();

            Iterator itor = this._midMap.entrySet().iterator();
            while (itor.hasNext()) {

                Map.Entry entry = (Map.Entry) itor.next();
                String key = (String) entry.getKey();
                long expire = (long) entry.getValue();

                if (expire > timestamp) {

                    continue;
                }

                keys.add(key);
            }

            itor = keys.iterator();

            while (itor.hasNext()) {

                String key = (String) itor.next();
                this._midMap.remove(key);
            }
        }
    }
}
