package com.fpnn.rtm;

import com.fpnn.sdk.ErrorRecorder;
import com.fpnn.sdk.TCPClient;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;

import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.util.Set;

class RTMQuestProcessor {

    private RTMEventHandlerInterface processor;
    private TCPClient rtmGated;

    RTMQuestProcessor(RTMEventHandlerInterface processor, TCPClient rtmGated) {
        this.processor = processor;
        this.rtmGated = rtmGated;
    }

    public Answer bye(Quest quest, InetSocketAddress peerAddress) {
        processor.bye();
        rtmGated.close();
        return null;
    }

    public Answer kickout(Quest quest, InetSocketAddress peerAddress) {
        processor.kickout();
        rtmGated.close();
        return null;
    }

    public Answer pushmsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long fromUid = (long)quest.want("from");
            byte mType = (byte)quest.want("mtype");
            byte fType = (byte)quest.want("ftype");
            long mid = (long)quest.want("mid");
            String message = (String)quest.want("msg");
            String attrs = (String)quest.want("attrs");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterP2PMessage(fromUid, mid))
                return null;

            processor.recvP2PMessage(fromUid, mType, fType, mid, message, attrs);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed P2P message exception.", e);
        }
        return null;
    }

    public Answer pushgroupmsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long groupId = (long)quest.want("gid");
            long fromUid = (long)quest.want("from");
            byte mType = (byte)quest.want("mtype");
            byte fType = (byte)quest.want("ftype");
            long mid = (long)quest.want("mid");
            String message = (String)quest.want("msg");
            String attrs = (String)quest.want("attrs");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterGroupMessage(groupId, fromUid, mid))
                return null;

            processor.recvGroupMessage(groupId, fromUid, mType, fType, mid, message, attrs);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed group message exception.", e);
        }
        return null;
    }
    public Answer pushroommsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long roomId = (long)quest.want("rid");
            long fromUid = (long)quest.want("from");
            byte mType = (byte)quest.want("mtype");
            byte fType = (byte)quest.want("ftype");
            long mid = (long)quest.want("mid");
            String message = (String)quest.want("msg");
            String attrs = (String)quest.want("attrs");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterRoomMessage(roomId, fromUid, mid))
                return null;

            processor.recvRoomMessage(roomId, fromUid, mType, fType, mid, message, attrs);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed room message exception.", e);
        }
        return null;
    }

    public Answer pushbroadcastmsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long fromUid = (long)quest.want("from");
            byte mType = (byte)quest.want("mtype");
            byte fType = (byte)quest.want("ftype");
            long mid = (long)quest.want("mid");
            String message = (String)quest.want("msg");
            String attrs = (String)quest.want("attrs");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterBroadcastMessage(fromUid, mid))
                return null;

            processor.recvBroadcastMessage(fromUid, mType, fType, mid, message, attrs);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed broadcast message exception.", e);
        }
        return null;
    }

    public Answer transmsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long fromUid = (long)quest.want("from");
            long mid = (long)quest.want("mid");
            long originalMid = (long)quest.want("omid");
            String message = (String)quest.want("msg");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterP2PMessage(fromUid, mid))
                return null;

            processor.recvTranslatedP2PMessage(fromUid, mid, originalMid, message);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed translated P2P message exception.", e);
        }
        return null;
    }

    public Answer transgroupmsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long groupId = (long)quest.want("gid");
            long fromUid = (long)quest.want("from");
            long mid = (long)quest.want("mid");
            long originalMid = (long)quest.want("omid");
            String message = (String)quest.want("msg");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterGroupMessage(groupId, fromUid, mid))
                return null;

            processor.recvTranslatedGroupMessage(groupId, fromUid, mid, originalMid, message);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed translated group message exception.", e);
        }
        return null;
    }

    public Answer transroommsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long roomId = (long)quest.want("rid");
            long fromUid = (long)quest.want("from");
            long mid = (long)quest.want("mid");
            long originalMid = (long)quest.want("omid");
            String message = (String)quest.want("msg");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterRoomMessage(roomId, fromUid, mid))
                return null;

            processor.recvTranslatedRoomMessage(roomId, fromUid, mid, originalMid, message);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed translated room message exception.", e);
        }
        return null;
    }

    public Answer transbroadcastmsg(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            long fromUid = (long)quest.want("from");
            long mid = (long)quest.want("mid");
            long originalMid = (long)quest.want("omid");
            String message = (String)quest.want("msg");

            if (!RTMResourceCenter.instance().getMessageIdFilter().filterBroadcastMessage(fromUid, mid))
                return null;

            processor.recvTranslatedBroadcastMessage(fromUid, mid, originalMid, message);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed translated broadcast message exception.", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Answer pushunread(Quest quest, InetSocketAddress peerAddress) {
        Answer answer = new Answer(quest);
        rtmGated.sendAnswer(answer);

        try {
            Set<Long> uidOfUnreadP2PMessages = (Set<Long>)quest.want("p2p");
            Set<Long> gidOfUnreadGroupMessages = (Set<Long>)quest.want("group");
            boolean haveUnreadBroadcastMessages = (boolean)quest.want("bc");

            processor.unreadMessageStatus(uidOfUnreadP2PMessages, gidOfUnreadGroupMessages, haveUnreadBroadcastMessages);

        } catch (NoSuchElementException | ClassCastException e) {
            ErrorRecorder.record("Decode server pushed unread message exception.", e);
        }
        return null;
    }
}
