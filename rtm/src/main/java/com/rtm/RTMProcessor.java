package com.rtm;

import com.fpnn.FPData;
import com.fpnn.FPProcessor;
import com.fpnn.event.EventData;
import com.fpnn.event.FPEvent;
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

    private FPEvent _event;
    private Map _midMap = new HashMap();

    public RTMProcessor(FPEvent event) {

        this._event = event;
    }

    @Override
    public FPEvent getEvent() {

        return this._event;
    }

    @Override
    public void service(FPData data, FPProcessor.IAnswer answer) {

        boolean callCb = true;

        if (RTMConfig.SERVER_PUSH.kickOut == data.getMethod()) {

            callCb = false;
        }

        if (RTMConfig.SERVER_PUSH.kickOutRoom == data.getMethod()) {

            callCb = false;
        }

        Map payload = null;

        if (data.getFlag() == 0) {

            JsonHelper.IJson json = JsonHelper.getInstance().getJson();

            if (callCb) {

                answer.sendAnswer(json.toJSON(new HashMap()), false);
            }

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

            if (callCb && bytes.length > 0) {

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

            if (payload.containsKey("mid")) {

                payload.put("mid", wantLong(payload, "mid"));
            }

            if (payload.containsKey("from")) {

                payload.put("from", wantLong(payload, "from"));
            }

            if (payload.containsKey("to")) {

                payload.put("to", wantLong(payload, "to"));
            }

            if (payload.containsKey("gid")) {

                payload.put("gid", wantLong(payload, "gid"));
            }

            if (payload.containsKey("rid")) {

                payload.put("rid", wantLong(payload, "rid"));
            }

            if (payload.containsKey("mtype")) {

                payload.put("mtype", wantByte(payload, "mtype"));
            }

            try {

                RTMProcessor.class.getMethod(data.getMethod(), Map.class).invoke(this, payload);
            } catch(Exception ex) {

                this._event.fireEvent(new EventData(this, "error", ex));
            }
        }
    }

    public void destroy() {

        this._midMap.clear();
    }

    /**
     * @param {Map} data
     */
    public void kickout(Map data) {

        this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.kickOut, data));
    }

    /**
     * @param {long} data.rid
     */
    public void kickoutroom(Map data) {

        this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.kickOutRoom, data));
    }

    /**
     * @param {long}   data.from
     * @param {long}   data.to
     * @param {byte}   data.mtype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     * @param {long}   data.mtime
     */
    public void pushmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(1, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");

        if (mtype >= 40 && mtype <= 50) {

            this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvFile, data));
            return;
        }

        this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {long}   data.gid
     * @param {byte}   data.mtype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     * @param {long}   data.mtime
     */
    public void pushgroupmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(2, (long) data.get("mid"), (long) data.get("from"), (long) data.get("gid"))) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");

        if (mtype >= 40 && mtype <= 50) {

            this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvGroupFile, data));
            return;
        }

        this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvGroupMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {long}   data.rid
     * @param {byte}   data.mtype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     * @param {long}   data.mtime
     */
    public void pushroommsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(3, (long) data.get("mid"), (long) data.get("from"), (long) data.get("rid"))) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");

        if (mtype >= 40 && mtype <= 50) {

            this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvRoomFile, data));
            return;
        }

        this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvRoomMessage, data));
    }

    /**
     * @param {long}   data.from
     * @param {byte}   data.mtype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     * @param {long}   data.mtime
     */
    public void pushbroadcastmsg(Map data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(4, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");

        if (mtype >= 40 && mtype <= 50) {

            this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvBroadcastFile, data));
            return;
        }

        this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvBroadcastMessage, data));
    }

    /**
     * @param {Map} data
     */
    public void ping(Map data) {

        this._event.fireEvent(new EventData(this, RTMConfig.SERVER_PUSH.recvPing, data));
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

    public Integer wantInteger(Map data, String key) {

        return Integer.valueOf(String.valueOf(data.get(key)));
    }

    public Long wantLong(Map data, String key) {

        return Long.valueOf(String.valueOf(data.get(key)));
    }

    public Byte wantByte(Map data, String key) {

        return Byte.valueOf(String.valueOf(data.get(key)));
    }
}
