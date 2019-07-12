package com.rtm;

import com.fpnn.ErrorRecorder;
import com.fpnn.FPData;
import com.fpnn.FPProcessor;
import com.fpnn.event.EventData;
import com.fpnn.event.FPEvent;
import com.fpnn.nio.NIOCore;
import com.rtm.json.JsonHelper;
import com.rtm.msgpack.PayloadPacker;
import com.rtm.msgpack.PayloadUnpacker;

import java.util.*;

public class RTMProcessor implements FPProcessor.IProcessor {

    public interface IService {

        void Service(Map<String, Object> data);
    }

    private FPEvent _event;
    private Map<String, Long> _midMap = new HashMap<String, Long>();

    private Map<String, IService> _actionMap = new HashMap<String, IService>();

    public RTMProcessor(FPEvent event) {

        this._event = event;
    }

    @Override
    public FPEvent getEvent() {

        return this._event;
    }

    public void destroy() {

        this.clearPingTimestamp();

        this._midMap.clear();
        this._actionMap.clear();
    }

    @Override
    public void service(FPData data, FPProcessor.IAnswer answer) {

        boolean callCb = true;

        if (RTMConfig.KICKOUT == data.getMethod()) {

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
            } catch (Exception ex) {

                ErrorRecorder.getInstance().recordError(ex);
            }

            if (callCb && bytes.length > 0) {

                answer.sendAnswer(bytes, false);
            }

            PayloadUnpacker unpacker = new PayloadUnpacker(data.msgpackPayload());

            try {

                payload = unpacker.unpack();
            } catch (Exception ex) {

                ErrorRecorder.getInstance().recordError(ex);
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

    @Override
    public boolean hasPushService(String name) {

        if (name == null || name.isEmpty()) {

            return false;
        }

        return this._actionMap.containsKey(name);
    }

    public void addPushService(String name, IService is) {

        synchronized (this._actionMap) {

            if (!this._actionMap.containsKey(name)) {

                this._actionMap.put(name, is);
            } else {

                this._event.fireEvent(new EventData(this, "error", new Exception("push service exist")));
            }
        }
    }

    public void removePushService(String name) {

        synchronized (this._actionMap) {

            if (this._actionMap.containsKey(name)) {

                this._actionMap.remove(name);
            }
        }
    }

    private void pushService(String name, Map<String, Object> data) {

        synchronized (this._actionMap) {

            if (this._actionMap.containsKey(name)) {

                IService is = this._actionMap.get(name);

                if (is != null) {

                    is.Service(data);
                }
            }
        }
    }

    /**
     * @param {Map} data
     */
    public void kickout(Map<String, Object> data) {

        this.pushService(RTMConfig.KICKOUT, data);
    }

    /**
     * @param {long} data.rid
     */
    public void kickoutroom(Map<String, Object> data) {

        this.pushService(RTMConfig.SERVER_PUSH.kickOutRoom, data);
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
    public void pushmsg(Map<String, Object> data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(1, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");
        String name = RTMConfig.SERVER_PUSH.recvMessage;

        if (mtype >= 40 && mtype <= 50) {

            name = RTMConfig.SERVER_PUSH.recvFile;
        }

        this.pushService(name, data);
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
    public void pushgroupmsg(Map<String, Object> data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(2, (long) data.get("mid"), (long) data.get("from"), (long) data.get("gid"))) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");
        String name = RTMConfig.SERVER_PUSH.recvGroupMessage;

        if (mtype >= 40 && mtype <= 50) {

            name = RTMConfig.SERVER_PUSH.recvGroupFile;
        }

        this.pushService(name, data);
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
    public void pushroommsg(Map<String, Object> data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(3, (long) data.get("mid"), (long) data.get("from"), (long) data.get("rid"))) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");
        String name = RTMConfig.SERVER_PUSH.recvRoomMessage;

        if (mtype >= 40 && mtype <= 50) {

            name = RTMConfig.SERVER_PUSH.recvRoomFile;
        }

        this.pushService(name, data);
    }

    /**
     * @param {long}   data.from
     * @param {byte}   data.mtype
     * @param {long}   data.mid
     * @param {String} data.msg
     * @param {String} data.attrs
     * @param {long}   data.mtime
     */
    public void pushbroadcastmsg(Map<String, Object> data) {

        if (data.containsKey("mid")) {

            if (!this.checkMid(4, (long) data.get("mid"), (long) data.get("from"), 0)) {

                return;
            }
        }

        byte mtype = (byte) data.get("mtype");
        String name = RTMConfig.SERVER_PUSH.recvBroadcastMessage;

        if (mtype >= 40 && mtype <= 50) {

            name = RTMConfig.SERVER_PUSH.recvBroadcastFile;
        }

        this.pushService(name, data);
    }

    /**
     * @param {Map} data
     */
    public void ping(Map<String, Object> data) {

        this._lastPingTimestamp = System.currentTimeMillis();
        this.pushService(RTMConfig.SERVER_PUSH.recvPing, data);
    }

    private long _lastPingTimestamp;

    public long getPingTimestamp() {

        return this._lastPingTimestamp;
    }

    public void clearPingTimestamp() {

        this._lastPingTimestamp = 0;
    }

    public void initPingTimestamp() {

        if (this._lastPingTimestamp == 0) {

            this._lastPingTimestamp = System.currentTimeMillis();
        }
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

                long expire = this._midMap.get(key);

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

                if ((long) entry.getValue() > timestamp) {

                    continue;
                }

                keys.add(entry.getKey());
            }

            itor = keys.iterator();

            while (itor.hasNext()) {

                this._midMap.remove(itor.next());
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
