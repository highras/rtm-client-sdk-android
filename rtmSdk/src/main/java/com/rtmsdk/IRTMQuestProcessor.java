package com.rtmsdk;
import com.rtmsdk.RTMStruct.TranslatedMessage;

public interface IRTMQuestProcessor
{
    void sessionClosed(int ClosedByErrorCode);        //-- com.fpnn.ErrorCode & com.fpnn.rtm.ErrorCode

    void kickout();
    void kickoutRoom(long roomId);

    //-- message for String format
    void pushMessage(long fromUid, long toUid, byte mtype, long mid, String message, String attrs, long mtime);
    void pushGroupMessage(long fromUid, long groupId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushRoomMessage(long fromUid, long roomId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushBroadcastMessage(long fromUid, byte mtype, long mid, String message, String attrs, long mtime);

    //-- message for binary format
    void pushMessage(long fromUid, long toUid, byte mtype, long mid, byte[] message, String attrs, long mtime);
    void pushGroupMessage(long fromUid, long groupId, byte mtype, long mid, byte[] message, String attrs, long mtime);
    void pushRoomMessage(long fromUid, long roomId, byte mtype, long mid, byte[] message, String attrs, long mtime);
    void pushBroadcastMessage(long fromUid, byte mtype, long mid, byte[] message, String attrs, long mtime);
//
//    void pushChat(long fromUid, long toUid, long mid, String message, String attrs, long mtime);
//    void pushGroupChat(long fromUid, long groupId, long mid, String message, String attrs, long mtime);
//    void pushRoomChat(long fromUid, long roomId, long mid, String message, String attrs, long mtime);
//    void pushBroadcastChat(long fromUid, long mid, String message, String attrs, long mtime);

    void pushChat(long fromUid, long toUid, long mid, TranslatedMessage message, String attrs, long mtime);
    void pushGroupChat(long fromUid, long groupId, long mid, TranslatedMessage message, String attrs, long mtime);
    void pushRoomChat(long fromUid, long roomId, long mid, TranslatedMessage message, String attrs, long mtime);
    void pushBroadcastChat(long fromUid, long mid, TranslatedMessage message, String attrs, long mtime);

    void pushAudio(long fromUid, long toUid, long mid, byte[] message, String attrs, long mtime);
    void pushGroupAudio(long fromUid, long groupId, long mid, byte[] message, String attrs, long mtime);
    void pushRoomAudio(long fromUid, long roomId, long mid, byte[] message, String attrs, long mtime);
    void pushBroadcastAudio(long fromUid, long mid, byte[] message, String attrs, long mtime);

    void pushCmd(long fromUid, long toUid, long mid, String message, String attrs, long mtime);
    void pushGroupCmd(long fromUid, long groupId, long mid, String message, String attrs, long mtime);
    void pushRoomCmd(long fromUid, long roomId, long mid, String message, String attrs, long mtime);
    void pushBroadcastCmd(long fromUid, long mid, String message, String attrs, long mtime);

    void pushFile(long fromUid, long toUid, byte mtype, long mid, String message, String attrs, long mtime);
    void pushGroupFile(long fromUid, long groupId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushRoomFile(long fromUid, long roomId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushBroadcastFile(long fromUid, byte mtype, long mid, String message, String attrs, long mtime);
}
