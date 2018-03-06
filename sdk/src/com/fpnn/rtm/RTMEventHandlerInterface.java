package com.fpnn.rtm;

import java.util.Set;

public interface RTMEventHandlerInterface {

    void bye();
    void kickout();

    void recvP2PMessage(long fromUid, byte mType, byte fType, long mid, String message, String attrs);
    void recvGroupMessage(long groupId, long fromUid, byte mType, byte fType, long mid, String message, String attrs);
    void recvRoomMessage(long roomId, long fromUid, byte mType, byte fType, long mid, String message, String attrs);
    void recvBroadcastMessage(long fromUid, byte mType, byte fType, long mid, String message, String attrs);

    void recvTranslatedP2PMessage(long fromUid, long mid, long originalMid, String message);
    void recvTranslatedGroupMessage(long groupId, long fromUid, long mid, long originalMid, String message);
    void recvTranslatedRoomMessage(long roomId, long fromUid, long mid, long originalMid, String message);
    void recvTranslatedBroadcastMessage(long fromUid, long mid, long originalMid, String message);

    void unreadMessageStatus(Set<Long> uidOfUnreadP2PMessages, Set<Long> gidOfUnreadGroupMessages, boolean haveUnreadBroadcastMessages);
}
