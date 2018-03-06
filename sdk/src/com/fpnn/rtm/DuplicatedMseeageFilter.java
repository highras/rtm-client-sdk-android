package com.fpnn.rtm;

import java.util.*;

class DuplicatedMseeageFilter {

    private static final byte P2PMessageType = 1;
    private static final byte GroupMessageType = 2;
    private static final byte RoomMessageType = 3;
    private static final byte BroadcastMessageType = 4;

    private static int expireSecond = 30 * 60;

    private class MessageIdUnit {
        byte messageType;
        long bizId;
        long uid;
        long mid;

        MessageIdUnit(byte type, long bizId, long userId, long mid) {
            messageType = type;
            this.bizId = bizId;
            this.uid = userId;
            this.mid = mid;
        }
    }

    private class MessageIdCount {
        long activeTime;
        long hint;

        MessageIdCount() {
            activeTime = System.currentTimeMillis();
            hint = 1;
        }
    }

    private class MidComparator implements Comparator<MessageIdUnit> {
        @Override
        public int compare(MessageIdUnit mid1, MessageIdUnit mid2) {

            if (mid1.mid < mid2.mid)
                return -1;

            if (mid1.mid > mid2.mid)
                return 1;

            if (mid1.uid < mid2.uid)
                return -1;

            if (mid1.uid > mid2.uid)
                return 1;

            if (mid1.bizId < mid2.bizId)
                return -1;

            if (mid1.bizId > mid2.bizId)
                return 1;

            if (mid1.messageType < mid2.messageType)
                return -1;

            if (mid1.messageType > mid2.messageType)
                return 1;

            return 0;
        }
    }

    private TreeMap<MessageIdUnit, MessageIdCount> midCache;

    DuplicatedMseeageFilter() {
        midCache = new TreeMap<>(new MidComparator());
    }

    private boolean filter(MessageIdUnit idUnit) {

        synchronized (this) {
            MessageIdCount hint = midCache.get(idUnit);
            if (hint == null) {
                midCache.put(idUnit, new MessageIdCount());
                return true;
            }

            hint.activeTime = System.currentTimeMillis();
            hint.hint += 1;

            return false;
        }
    }

    boolean filterP2PMessage(long from, long mid) {
        MessageIdUnit idUnit = new MessageIdUnit(P2PMessageType, 0, from, mid);
        return filter(idUnit);
    }

    boolean filterGroupMessage(long groupId, long from, long mid) {
        MessageIdUnit idUnit = new MessageIdUnit(GroupMessageType, groupId, from, mid);
        return filter(idUnit);
    }

    boolean filterRoomMessage(long roomId, long from, long mid) {
        MessageIdUnit idUnit = new MessageIdUnit(RoomMessageType, roomId, from, mid);
        return filter(idUnit);
    }

    boolean filterBroadcastMessage(long from, long mid) {
        MessageIdUnit idUnit = new MessageIdUnit(BroadcastMessageType, 0, from, mid);
        return filter(idUnit);
    }

    void cleanExpiredCache() {

        long threshold = System.currentTimeMillis() - expireSecond * 1000;
        TreeSet<MessageIdUnit> tmpCache = new TreeSet<>();

        synchronized (this) {

            Iterator<Map.Entry<MessageIdUnit, MessageIdCount>> entries = midCache.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<MessageIdUnit, MessageIdCount> entry = entries.next();
                MessageIdCount idCount = entry.getValue();
                if (idCount.activeTime <= threshold) {

                    MessageIdUnit idUnit = entry.getKey();
                    tmpCache.add(idUnit);
                }
            }

            for (MessageIdUnit idUnit : tmpCache) {
                midCache.remove(idUnit);
            }
        }
    }
}
