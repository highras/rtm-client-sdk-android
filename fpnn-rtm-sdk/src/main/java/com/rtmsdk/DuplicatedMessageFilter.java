package com.rtmsdk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class DuplicatedMessageFilter {
    public enum MessageCategories {
        P2PMessage          (1),
        GroupMessage        (2),
        RoomMessage         (3),
        BroadcastMessage    (4);
        private int value;

        MessageCategories (int value) {
            this.value = value;
        }
        public int value() {
            return value;
        }
    }

    private static class MessageIdUnit {
        public MessageCategories messageType;
        public long bizId;
        public long uid;
        public long mid;

        public MessageIdUnit(MessageCategories type, long _bizId, long _uid, long _mid) {
            uid = _uid;
            mid = _mid;
            bizId = _bizId;
            messageType = type;
        }

        public int hashCode() {
            int result = 17;
            result = 31 * result + messageType.ordinal();
            result = 31 * result + (int) bizId;
            result = 31 * result + (int) uid;
            result = 31 * result + (int) mid;
            return result;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;

            if (!(o instanceof MessageIdUnit))
                return false;

            MessageIdUnit pn = (MessageIdUnit) o;
            return pn.messageType == messageType && pn.uid == uid && pn.mid == mid && pn.bizId == bizId;
        }
    }

    private final int expireSeconds = 20 * 60;

    private Map<MessageIdUnit, Long> midCache;
    private Object locker;

    public DuplicatedMessageFilter() {
        midCache = new HashMap<>();
        locker = new Object();
    }

    public boolean CheckMessage(MessageCategories type, long uid, long mid) {
        return CheckMessage(type, uid, mid, 0);
    }

    public boolean CheckMessage(MessageCategories type, long uid, long mid, long bizId) {
        synchronized (locker) {
            long now = RTMUtils.getCurrentSeconds();
            MessageIdUnit unit = new MessageIdUnit(type, bizId, uid, mid);
            if (midCache.containsKey(unit)) {
                midCache.put(unit, now);
                return false;
            } else {
                midCache.put(unit, now);
                ClearExpired(now);
                return true;
            }
        }
    }

    private void ClearExpired(long now) {
        now -= expireSeconds;
        Iterator<Map.Entry<MessageIdUnit, Long>> it = midCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<MessageIdUnit, Long> entry = it.next();
            if (entry.getValue() <= now)
                it.remove();
        }
    }
}