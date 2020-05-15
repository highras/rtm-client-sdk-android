package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.*;

import java.util.ArrayList;
import java.util.List;

class RTMChat extends RTMRoom {
    private List<Byte> chatMTypes = new ArrayList<Byte>() {
        {
            add(MessageMType_Chat);
            add(MessageMType_Audio);
            add(MessageMType_Cmd);
            for (byte i = MessageMType_FileStart; i<= MessageMType_FileEnd;i++)
                add(i);
        }
    };
    //===========================[ sending Chat ]=========================//

    /****************************p2p*****************/
    public boolean sendChat(LongFunctionCallback callback, long uid, String message) {
        return sendChat(callback, uid, message, "", 0);
    }

    public boolean sendChat(LongFunctionCallback callback, long uid, String message, String attrs) {
        return sendChat(callback, uid, message, attrs, 0);
    }

    /**
     *发送p2p聊天消息(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param uid       目标用户id(NoNull)
     * @param message   p2p聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendChat(LongFunctionCallback callback, long uid, String message, String attrs, int timeout) {
        return internalSendMessage(callback, uid, MessageMType_Chat, message, attrs, timeout);
    }

    public int sendChat(LongMtime mtime, long uid, String message) {
        return sendChat(mtime, uid, message, "", 0);
    }

    public int sendChat(LongMtime mtime, long uid, String message, String attrs) {
        return sendChat(mtime, uid, message, attrs, 0);
    }

    /**
     *发送p2p聊天消息(sync)
     * @param mtime     LongMtimed对象(服务器接收时间)
     * @param uid       目标用户id(NoNull)
     * @param message   消息内容(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendChat(LongMtime mtime, long uid, String message, String attrs, int timeout) {
        return internalSendMessage(mtime, uid, MessageMType_Chat, message, attrs, timeout);
    }

    /****************************group*****************/
    public boolean sendGroupChat(LongFunctionCallback callback, long groupId, String message) {
        return sendGroupChat(callback, groupId, message, "", 0);
    }

    public boolean sendGroupChat(LongFunctionCallback callback, long groupId, String message, String attrs) {
        return sendGroupChat(callback, groupId, message, attrs, 0);
    }

    /**
     *发送群组聊天消息(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param message   群组聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendGroupChat(LongFunctionCallback callback, long groupId, String message, String attrs, int timeout) {
        return internalSendGroupMessage(callback, groupId, MessageMType_Chat, message, attrs, timeout);
    }

    public int sendGroupChat(LongMtime mtime, long groupId, String message) {
        return sendGroupChat(mtime, groupId, message, "", 0);
    }

    public int sendGroupChat(LongMtime mtime, long groupId, String message, String attrs) {
        return sendGroupChat(mtime, groupId, message, attrs, 0);
    }

    /**
     *发送群组聊天消息(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param groupId   群组id(NoNull)
     * @param message   群组聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendGroupChat(LongMtime mtime, long groupId, String message, String attrs, int timeout) {
        return internalSendGroupMessage(mtime, groupId, MessageMType_Chat, message, attrs, timeout);
    }


    /****************************room*****************/
    public boolean sendRoomChat(LongFunctionCallback callback, long roomId, String message) {
        return sendRoomChat(callback, roomId, message, "", 0);
    }

    public boolean sendRoomChat(LongFunctionCallback callback, long roomId, String message, String attrs) {
        return sendRoomChat(callback, roomId, message, attrs, 0);
    }

    /**
     *
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param roomId    房间id(NoNull)
     * @param message   房间聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendRoomChat(LongFunctionCallback callback, long roomId, String message, String attrs, int timeout) {
        return internalSendRoomMessage(callback, roomId, MessageMType_Chat, message, attrs, timeout);
    }

    public int sendRoomChat(LongMtime mtime, long roomId, String message) {
        return sendRoomChat(mtime, roomId, message, "", 0);
    }

    public int sendRoomChat(LongMtime mtime, long roomId, String message, String attrs) {
        return sendRoomChat(mtime, roomId, message, attrs, 0);
    }

    /**
     *发送群组聊天消息(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param roomId    房间id(NoNull)
     * @param message   群组聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendRoomChat(LongMtime mtime, long roomId, String message, String attrs, int timeout) {
        return internalSendRoomMessage(mtime, roomId, MessageMType_Chat, message, attrs, timeout);
    }


    //===========================[ sending Cmd ]=========================//
    public boolean sendCmd(LongFunctionCallback callback, long uid, String message) {
        return sendCmd(callback, uid, message, "", 0);
    }

    public boolean sendCmd(LongFunctionCallback callback, long uid, String message, String attrs) {
        return sendCmd(callback, uid, message, attrs, 0);
    }


    /**
     *发送p2p指令消息(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param uid       目标用户id(NoNull)
     * @param message   p2p指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendCmd(LongFunctionCallback callback, long uid, String message, String attrs, int timeout) {
        return internalSendMessage(callback, uid, MessageMType_Cmd, message, attrs, timeout);
    }

    public int sendCmd(LongMtime mtime, long uid, String message) {
        return sendCmd(mtime, uid, message, "", 0);
    }

    public int sendCmd(LongMtime mtime, long uid, String message, String attrs) {
        return sendCmd(mtime, uid, message, attrs, 0);
    }

    /**
     *发送p2p指令消息(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param uid       目标用户id(NoNull)
     * @param message   p2p指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendCmd(LongMtime mtime, long uid, String message, String attrs, int timeout) {
        return internalSendMessage(mtime, uid, MessageMType_Cmd, message, attrs, timeout);
    }

    public boolean sendGroupCmd(LongFunctionCallback callback, long groupId, String message) {
        return sendGroupCmd(callback, groupId, message, "", 0);
    }

    public boolean sendGroupCmd(LongFunctionCallback callback, long groupId, String message, String attrs) {
        return sendGroupCmd(callback, groupId, message, attrs, 0);
    }

    /**
     *发送群组指令(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param message   群组指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendGroupCmd(LongFunctionCallback callback, long groupId, String message, String attrs, int timeout) {
        return internalSendGroupMessage(callback, groupId, MessageMType_Cmd, message, attrs, timeout);
    }

    public int sendGroupCmd(LongMtime mtime, long groupId, String message) {
        return sendGroupCmd(mtime, groupId, message, "", 0);
    }

    public int sendGroupCmd(LongMtime mtime, long groupId, String message, String attrs) {
        return sendGroupCmd(mtime, groupId, message, attrs, 0);
    }

    /**
     *发送群组指令消息(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param groupId   群组id(NoNull)
     * @param message   p2p指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendGroupCmd(LongMtime mtime, long groupId, String message, String attrs, int timeout) {
        return internalSendGroupMessage(mtime, groupId, MessageMType_Cmd, message, attrs, timeout);
    }

    public boolean sendRoomCmd(LongFunctionCallback callback, long roomId, String message) {
        return sendRoomCmd(callback, roomId, message, "", 0);
    }

    public boolean sendRoomCmd(LongFunctionCallback callback, long roomId, String message, String attrs) {
        return sendRoomCmd(callback, roomId, message, attrs, 0);
    }

    /**
     *发送房间指令
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param roomId    房间id(NoNull)
     * @param message   房间指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendRoomCmd(LongFunctionCallback callback, long roomId, String message, String attrs, int timeout) {
        return internalSendRoomMessage(callback, roomId, MessageMType_Cmd, message, attrs, timeout);
    }

    public int sendRoomCmd(LongMtime mtime, long roomId, String message) {
        return sendRoomCmd(mtime, roomId, message, "", 0);
    }

    public int sendRoomCmd(LongMtime mtime, long roomId, String message, String attrs) {
        return sendRoomCmd(mtime, roomId, message, attrs, 0);
    }

    /**
     *发送房间指令消息(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param roomId   房间id(NoNull)
     * @param message   p2p指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendRoomCmd(LongMtime mtime, long roomId, String message, String attrs, int timeout) {
        return internalSendRoomMessage(mtime, roomId, MessageMType_Cmd, message, attrs, timeout);
    }

    //===========================[ sending Audio ]=========================//
    public boolean sendAudio(LongFunctionCallback callback, long uid, byte[] message) {
        return sendAudio(callback, uid, message, "", 0);
    }

    public boolean sendAudio(LongFunctionCallback callback, long uid, byte[] message, String attrs) {
        return sendAudio(callback, uid, message, attrs, 0);
    }

    /**
     *发送p2p语音(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param uid       目标用户id(NoNull)
     * @param message   语音消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendAudio(LongFunctionCallback callback, long uid, byte[] message, String attrs, int timeout) {
        return internalSendMessage(callback, uid, MessageMType_Audio, message, attrs, timeout);
    }

    public int sendAudio(LongMtime mtime, long uid, byte[] message) {
        return sendAudio(mtime, uid, message, "", 0);
    }

    public int sendAudio(LongMtime mtime, long uid, byte[] message, String attrs) {
        return sendAudio(mtime, uid, message, attrs, 0);
    }

    /**
     *发送p2p语音(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param uid       目标用户id(NoNull)
     * @param message   语音消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendAudio(LongMtime mtime, long uid, byte[] message, String attrs, int timeout) {
        return internalSendMessage(mtime, uid, MessageMType_Audio, message, attrs, timeout);
    }

    public boolean sendGroupAudio(LongFunctionCallback callback, long groupId, byte[] message) {
        return sendGroupAudio(callback, groupId, message, "", 0);
    }

    public boolean sendGroupAudio(LongFunctionCallback callback, long groupId, byte[] message, String attrs) {
        return sendGroupAudio(callback, groupId, message, attrs, 0);
    }
    /**
     *发送群组语音
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param message   群组语音(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendGroupAudio(LongFunctionCallback callback, long groupId, byte[] message, String attrs, int timeout) {
        return internalSendGroupMessage(callback, groupId, MessageMType_Audio, message, attrs, timeout);
    }

    public int sendGroupAudio(LongMtime mtime, long groupId, byte[] message) {
        return internalSendGroupMessage(mtime, groupId, MessageMType_Audio, message, "",0);
    }
    public int sendGroupAudio(LongMtime mtime, long groupId, byte[] message, String attrs) {
        return internalSendGroupMessage(mtime, groupId, MessageMType_Audio, message, attrs, 0);
    }

    /**
     *发送群组语音(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param groupId   群组id(NoNull)
     * @param message   语音消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendGroupAudio(LongMtime mtime, long groupId, byte[] message, String attrs, int timeout) {
        return internalSendGroupMessage(mtime, groupId, MessageMType_Audio, message, attrs, timeout);
    }

    public boolean sendRoomAudio(LongFunctionCallback callback, long roomId, byte[] message) {
        return sendRoomAudio(callback, roomId, message, "", 0);
    }

    public boolean sendRoomAudio(LongFunctionCallback callback, long roomId, byte[] message, String attrs) {
        return sendRoomAudio(callback, roomId, message, attrs, 0);
    }

    /**
     *发送房间语音(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param roomId    房间id(NoNull)
     * @param message   房间语音(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendRoomAudio(LongFunctionCallback callback, long roomId, byte[] message, String attrs, int timeout) {
        return internalSendRoomMessage(callback, roomId, MessageMType_Audio, message, attrs, timeout);
    }

    public int sendRoomAudio(LongMtime mtime, long roomId, byte[] message) {
        return internalSendRoomMessage(mtime, roomId, MessageMType_Audio, message, "",0);
    }
    public int sendRoomAudio(LongMtime mtime, long roomId, byte[] message, String attrs) {
        return internalSendRoomMessage(mtime, roomId, MessageMType_Audio, message, attrs, 0);
    }

    /**
     *发送房间语音(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param roomId    房间id(NoNull)
     * @param message   语音消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendRoomAudio(LongMtime mtime, long roomId, byte[] message, String attrs, int timeout) {
        return internalSendRoomMessage(mtime, roomId, MessageMType_Audio, message, attrs, timeout);
    }

    //===========================[ History Chat (Chat & Cmd & Audio) ]=========================//
    public boolean getGroupChat(HistoryMessageCallback callback, long groupId, boolean desc, int count) {
        return getGroupChat(callback, groupId, desc, count, 0, 0, 0, 0);
    }

    public boolean getGroupChat(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec) {
        return getGroupChat(callback, groupId, desc, count, beginMsec, 0, 0, 0);
    }

    public boolean getGroupChat(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec) {
        return getGroupChat(callback, groupId, desc, count, beginMsec, endMsec, 0, 0);
    }

    public boolean getGroupChat(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getGroupChat(callback, groupId, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     *获取群组聊天记录(async)
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getGroupChat(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getGroupMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    public int getGroupChat(HistoryMessageResult result, long groupId, boolean desc, int count) {
        return getGroupChat(result, groupId, desc, count, 0, 0, 0, 0);
    }

    public int getGroupChat(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec) {
        return getGroupChat(result, groupId, desc, count, beginMsec, 0, 0, 0);
    }

    public int getGroupChat(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec) {
        return getGroupChat(result, groupId, desc, count, beginMsec, endMsec, 0, 0);
    }

    public int getGroupChat(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getGroupChat(result, groupId, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     *获取群组聊天记录(sync)
     * @param result  HistoryMessageResult对象(NoNull)
     * @param groupId   群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getGroupChat(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getGroupMessage(result, groupId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    public boolean getRoomChat(HistoryMessageCallback callback, long roomId, boolean desc, int count) {
        return getRoomChat(callback, roomId, desc, count, 0, 0, 0, 0);
    }

    public boolean getRoomChat(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec) {
        return getRoomChat(callback, roomId, desc, count, beginMsec, 0, 0, 0);
    }

    public boolean getRoomChat(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec) {
        return getRoomChat(callback, roomId, desc, count, beginMsec, endMsec, 0, 0);
    }

    public boolean getRoomChat(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getRoomChat(callback, roomId, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     *
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param roomId    房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getRoomChat(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getRoomMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    public int getRoomChat(HistoryMessageResult result, long roomId, boolean desc, int count) {
        return getRoomChat(result, roomId, desc, count, 0, 0, 0, 0);
    }

    public int getRoomChat(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec) {
        return getRoomChat(result, roomId, desc, count, beginMsec, 0, 0, 0);
    }

    public int getRoomChat(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec) {
        return getRoomChat(result, roomId, desc, count, beginMsec, endMsec, 0, 0);
    }

    public int getRoomChat(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getRoomChat(result, roomId, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     *获取房间聊天记录(sync)
     * @param result  HistoryMessageResult对象(NoNull)
     * @param roomId    房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getRoomChat(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getRoomMessage(result, roomId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    public boolean getBroadcastChat(HistoryMessageCallback callback, boolean desc, int count) {
        return getBroadcastChat(callback, desc, count, 0, 0, 0, 0);
    }

    public boolean getBroadcastChat(HistoryMessageCallback callback, boolean desc, int count, long beginMsec) {
        return getBroadcastChat(callback, desc, count, beginMsec, 0, 0, 0);
    }

    public boolean getBroadcastChat(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec) {
        return getBroadcastChat(callback, desc, count, beginMsec, endMsec, 0, 0);
    }

    public boolean getBroadcastChat(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getBroadcastChat(callback, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     * 获得广播历史聊天消息(async)
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getBroadcastChat(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getBroadcastMessage(callback, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    public int getBroadcastChat(HistoryMessageResult result, boolean desc, int count) {
        return getBroadcastChat(result, desc, count, 0, 0, 0, 0);
    }

    public int getBroadcastChat(HistoryMessageResult result, boolean desc, int count, long beginMsec) {
        return getBroadcastChat(result, desc, count, beginMsec, 0, 0, 0);
    }

    public int getBroadcastChat(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec) {
        return getBroadcastChat(result, desc, count, beginMsec, endMsec, 0, 0);
    }

    public int getBroadcastChat(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getBroadcastChat(result, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     * 获得广播历史聊天消息(sync)
     * @param result  HistoryMessageResult对象(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getBroadcastChat(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getBroadcastMessage(result, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    public boolean getP2PChat(HistoryMessageCallback callback, long peerUid, boolean desc, int count) {
        return getP2PChat(callback, peerUid, desc, count, 0, 0, 0, 0);
    }

    public boolean getP2PChat(HistoryMessageCallback callback, long peerUid, boolean desc, int count, long beginMsec) {
        return getP2PChat(callback, peerUid, desc, count, beginMsec, 0, 0, 0);
    }

    public boolean getP2PChat(HistoryMessageCallback callback,  long peerUid, boolean desc, int count, long beginMsec, long endMsec) {
        return getP2PChat(callback, peerUid, desc, count, beginMsec, endMsec, 0, 0);
    }

    public boolean getP2PChat(HistoryMessageCallback callback,  long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getP2PChat(callback, peerUid, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     *获取p2p聊天记录(async)
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getP2PChat(HistoryMessageCallback callback,  long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getP2PMessage(callback, peerUid, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    public int getP2PChat(HistoryMessageResult result, long peerUid, boolean desc, int count) {
        return getP2PChat(result, peerUid, desc, count, 0, 0, 0, 0);
    }

    public int getP2PChat(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec) {
        return getP2PChat(result, peerUid, desc, count, beginMsec, 0, 0, 0);
    }

    public int getP2PChat(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec) {
        return getP2PChat(result, peerUid, desc, count, beginMsec, endMsec, 0, 0);
    }

    public int getP2PChat(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getP2PChat(result, peerUid, desc, count, beginMsec, endMsec, lastId, 0);
    }

    /**
     *获取p2p聊天记录(sync)
     * @param result  HistoryMessageResult对象(NoNull)
     * @param peerUid   用户id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getP2PChat(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        return getP2PMessage(result, peerUid, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout);
    }

    //===========================[ Unread Chat ]=========================//
    //-- Action<List<p2p_uid>, List<groupId>, errorCode>
    public boolean getUnread(UnreadCallback callback) {
        return getUnread(callback, false, 0);
    }

    public boolean getUnread(UnreadCallback callback, boolean clear) {
        return getUnread(callback, clear, 0);
    }

    /**
     *获取服务器未读消息(async)
     * @param callback  UnreadCallback回调(NoNull)
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getUnread(final UnreadCallback callback, boolean clear, int timeout) {
        Quest quest = new Quest("getunread");
        quest.param("clear", clear);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                List<Long> p2pList = new ArrayList<>();
                List<Long> groupList = new ArrayList<>();

                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    RTMUtils.wantLongList(answer,"p2p", p2pList);
                    RTMUtils.wantLongList(answer,"group", groupList);
                }
                callback.call(p2pList, groupList, errorCode);
            }
        }, timeout);
    }

    public int getUnread(List<Long> p2pList, List<Long> groupList) {
        return getUnread(p2pList, groupList, false, 0);
    }

    public int getUnread(List<Long> p2pList, List<Long> groupList, boolean clear) {
        return getUnread(p2pList, groupList, clear, 0);
    }

    /*获取服务器未读消息(sync)
     * @param p2pList  未读消息的uid集合(NoNull)
     * @param groupList  未读消息的groupid集合(NoNull)
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getUnread(List<Long> p2pList, List<Long> groupList, boolean clear, int timeout) {
        Quest quest = new Quest("getunread");
        quest.param("clear", clear);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            RTMUtils.wantLongList(answer,"p2p", p2pList);
            RTMUtils.wantLongList(answer,"group", groupList);
        }
        return code;
    }

    //===========================[ Clear Unread ]=========================//
    public boolean clearUnread(ErrorCodeCallback callback) {
        return clearUnread(callback, 0);
    }

    /**
     *清除离线提醒 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean clearUnread(ErrorCodeCallback callback, int timeout) {
        Quest quest = new Quest("cleanunread");
        return sendQuest(callback, quest, timeout);
    }

    public int clearUnread() {
        return clearUnread(0);
    }

    /*清除离线提醒 sync
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int clearUnread(int timeout) {
        Quest quest = new Quest("cleanunread");
        return sendQuestCode(quest, timeout);
    }

    //===========================[ get Session ]=========================//
    //-- Action<List<p2p_uid>, List<groupId>, errorCode>
    public boolean getSession(UnreadCallback callback) {
        return getSession(callback, 0);
    }

    /**
     * 获取和自己有过回话的用户uid和群组id集合 async
     * @param callback UnreadCallback回调(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getSession(final UnreadCallback callback, int timeout) {
        Quest quest = new Quest("getsession");
        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                List<Long> p2pList = new ArrayList<>();
                List<Long> groupList = new ArrayList<>();

                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    RTMUtils.wantLongList(answer,"p2p", p2pList);
                    RTMUtils.wantLongList(answer,"group", groupList);
                }
                callback.call(p2pList, groupList, errorCode);
            }
        }, timeout);
    }

    public int getSession(List<Long> p2pList, List<Long> groupList) {
        return getSession(p2pList, groupList, 0);
    }


    /**
     * 获取和自己有过回话的用户uid和群组id集合 sync
     * @param p2pList  未读消息的uid集合(NoNull)
     * @param groupList  未读消息的groupid集合(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getSession(List<Long> p2pList, List<Long> groupList, int timeout) {
        Quest quest = new Quest("getsession");
        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            RTMUtils.wantLongList(answer,"p2p", p2pList);
            RTMUtils.wantLongList(answer,"group", groupList);
        }
        return code;
    }

    //===========================[ delete Chat ]=========================//
    //-- xid: peer uid, or groupId, or roomId
    //-- type: 1: p2p, 2: group; 3: room

    public boolean deleteChat(ErrorCodeCallback callback, long xid, long mid, int type) {
        return deleteChat(callback, xid, mid, type, 0);
    }

    /**
     *删除单条聊天信息 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean deleteChat(ErrorCodeCallback callback, long xid, long mid, int type, int timeout) {
        return deleteMessage(callback, xid, mid, type, timeout);
    }

    public int deleteChat(long xid, long mid, int type) {
        return deleteChat(xid, mid, type, 0);
    }


    /**
     * 删除聊天消息 sync
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int deleteChat(long xid, long mid, int type, int timeout) {
        return deleteMessage(xid, mid, type, timeout);
    }

    //===========================[ get Chat ]=========================//
    //-- xid: peer uid, or groupId, or roomId
    //-- type: 1: p2p, 2: group; 3: room

    public boolean getChat(RetrievedMessageCallback callback, long xid, long mid, int type) {
        return getChat(callback, xid, mid, type, 0);
    }

    /**
     *获取单条聊天消息 async
     * @param callback RetrievedMessageCallback回调(NoNull)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getChat(RetrievedMessageCallback callback, long xid, long mid, int type, int timeout) {
        return getMessage(callback, xid, mid, type, timeout);
    }

    public int getChat(RetrievedMessage retrievedMessage, long xid, long mid, int type) {
        return getChat(retrievedMessage, xid, mid, type, 0);
    }

    /*获取单条聊天消息 sync
     * @param retrievedMessage RetrievedMessage对象(**NoNull**)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getChat(RetrievedMessage retrievedMessage, long xid, long mid, int type, int timeout) {
        return getMessage(retrievedMessage, xid, mid, type, timeout);
    }

    //===========================[ Set translated Languag ]=========================//
    public boolean setTranslatedLanguage(ErrorCodeCallback callback, String targetLanguage) {
        return setTranslatedLanguage(callback, targetLanguage, 0);
    }

    /**
     *设置目标翻译语言 async
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     * @return      true(发送成功)  false(发送失败)
     */
    public boolean setTranslatedLanguage(ErrorCodeCallback callback, String targetLanguage, int timeout) {
        Quest quest = new Quest("setlang");
        quest.param("lang", targetLanguage);
        return sendQuest(callback, quest, timeout);
    }

    public int setTranslatedLanguage(String targetLanguage) {
        return setTranslatedLanguage(targetLanguage, 0);
    }

    /**
     * 设置翻译的目标语言 sync
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     * @return      errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int setTranslatedLanguage(String targetLanguage, int timeout) {
        Quest quest = new Quest("setlang");
        quest.param("lang", targetLanguage);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ translate ]=========================//
    public enum translateType {
        Chat,
        Mail
    }

    public enum ProfanityType {
        Off,
        Stop,
        Censor
    }

    //-- Action<translatedMessage, errorCode>
    public boolean translate(TranslateCallback callback, String text, String trargetLanguage) {
        return translate(callback, text, trargetLanguage, "", RTMConfig.globalTranslateQuestTimeoutSeconds, translateType.Chat, ProfanityType.Off);
    }

    public boolean translate(TranslateCallback callback, String text, String trargetLanguage, String sourceLange) {
        return translate(callback, text, trargetLanguage, sourceLange, RTMConfig.globalTranslateQuestTimeoutSeconds, translateType.Chat, ProfanityType.Off);
    }


    /**
     *文本翻译 async
     * @param callback      TranslateCallback回调(NoNull)
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param timeout               超时时间(秒)
     * @param type                  翻译类型("mail","chat")
     * @param profanity             敏感语过滤类型
     * @return              true(发送成功)  false(发送失败)
     */
    public boolean translate(final TranslateCallback callback, String text, String destinationLanguage, String sourceLanguage, int timeout,
                             translateType type, ProfanityType profanity) {
        Quest quest = new Quest("translate");
        quest.param("text", text);
        quest.param("dst", destinationLanguage);

        if (sourceLanguage.length() > 0)
            quest.param("src", sourceLanguage);

        if (type == translateType.Mail)
            quest.param("type", "mail");
        else
            quest.param("type", "chat");

        switch (profanity) {
            case Stop:
                quest.param("profanity", "stop");
                break;
            case Censor:
                quest.param("profanity", "censor");
                break;
            case Off:
                quest.param("profanity", "off");
                break;
        }

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                TranslatedMessage tm = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    tm = new TranslatedMessage();
                    tm.source = answer.wantString("source");
                    tm.target = answer.wantString("target");
                    tm.sourceText = answer.wantString("sourceText");
                    tm.targetText = answer.wantString("targetText");
                }

                callback.call(tm, errorCode);
            }
        }, timeout);
    }

    public int translate(TranslatedMessage translatedMessage, String text, String trargetLanguage) {
        return translate(translatedMessage, text, trargetLanguage, "", RTMConfig.globalTranslateQuestTimeoutSeconds, translateType.Chat, ProfanityType.Off);
    }

    public int translate(TranslatedMessage translatedMessage, String text, String trargetLanguage, String sourceLange) {
        return translate(translatedMessage, text, trargetLanguage, sourceLange, RTMConfig.globalTranslateQuestTimeoutSeconds, translateType.Chat, ProfanityType.Off);
    }

    /**
     *文本翻译 sync
     * @param translatedMessage     TranslatedMessage对象(NoNull)
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param timeout               超时时间(秒)
     * @param type                  翻译类型("mail","chat")
     * @param profanity             敏感语过滤类型
     * @return      errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int translate(TranslatedMessage translatedMessage, String text, String destinationLanguage, String sourceLanguage, int timeout,
                         translateType type, ProfanityType profanity) {
        Quest quest = new Quest("translate");
        quest.param("text", text);
        quest.param("dst", destinationLanguage);

        if (sourceLanguage.length() > 0)
            quest.param("src", sourceLanguage);

        if (type == translateType.Mail)
            quest.param("type", "mail");
        else
            quest.param("type", "chat");

        switch (profanity) {
            case Stop:
                quest.param("profanity", "stop");
                break;
            case Censor:
                quest.param("profanity", "censor");
                break;
            case Off:
                quest.param("profanity", "off");
                break;
        }

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            translatedMessage.source = answer.wantString("source");
            translatedMessage.target = answer.wantString("target");
            translatedMessage.sourceText = answer.wantString("sourceText");
            translatedMessage.targetText = answer.wantString("targetText");
        }
        return code;
    }

    //===========================[ Profanity ]=========================//
    //-- Action<String text, List<String> classification, errorCode>
    public boolean profanity(ProfanityCallback callback, String text) {
        return profanity(callback, text, false, 0);
    }

    public boolean profanity(ProfanityCallback callback, String text, boolean classify) {
        return profanity(callback, text, classify, 0);
    }

    /**
     *文本过滤 async
     * @param callback  ProfanityCallback回调(NoNull)
     * @param text      需要过滤的文本
     * @param classify  是否进行文本分类检测
     * @param timeout   超时时间(秒)
     * @return          true(发送成功)  false(发送失败)
     */
    public boolean profanity(final ProfanityCallback callback, String text, boolean classify, int timeout) {
        Quest quest = new Quest("profanity");
        quest.param("text", text);
        quest.param("classify", classify);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String resultText = "";
                List<String> classification = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    resultText = answer.wantString("text");
                    Object obj  = answer.get("classification",null);
                    if (obj != null)
                        classification = (ArrayList<String>)obj;
                }
                callback.call(resultText, classification, errorCode);
            }
        }, timeout);
    }

    public int profanity(String text, StringBuilder resultText) {
        return profanity(text, resultText, false, null, 0);
    }

    public int profanity(String text, StringBuilder resultText, boolean classify, List<String> classification) {
        return profanity(text, resultText, classify, classification, 0);
    }

    /**
     *文本过滤 sync
     * @param text      需要过滤的文本(NoNull)
     * @param resultText    过滤后的文本(NoNull)
     * @param classify  是否进行文本分类检测
     * @param classification  文本分类结果
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int profanity(String text, StringBuilder resultText, boolean classify, List<String> classification, int timeout) {
        Quest quest = new Quest("profanity");
        quest.param("text", text);
        quest.param("classify", classify);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            resultText.append(answer.wantString("text"));
            Object obj  = answer.get("classification",null);
            if (obj != null)
                classification.addAll((ArrayList<String>) obj);
        }
        return code;
    }

    //===========================[ Transcribe ]=========================//
    //-- Action<String text, String language, errorCode>
    public boolean transcribe(DoubleStringCallback callback, byte[] audio) {
        return transcribe(callback, audio, false, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public boolean transcribe(DoubleStringCallback callback, byte[] audio, boolean profanityFilter) {
        return transcribe(callback, audio, profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }


    /**
     *语音识别 async
     * @param callback  DoubleStringCallback回调(NoNull)
     * @param audio     语音消息(NoNull)
     * @param timeout   超时时间(秒)
     * @return      true(发送成功)  false(发送失败)
     */
    public boolean transcribe(final DoubleStringCallback callback, byte[] audio, boolean profanityFilter, int timeout) {
        Quest quest = new Quest("transcribe");
        quest.param("audio", audio);
        quest.param("profanityFilter", profanityFilter);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String resultText = "";
                String resultLanguage = "";

                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    resultText = answer.wantString("text");
                    resultLanguage = answer.wantString("lang");
                }
                callback.call(resultText, resultLanguage, errorCode);
            }
        }, timeout);
    }

    public int transcribe(byte[] audio, StringBuilder resultText, StringBuilder resultLanguage) {
        return transcribe(audio, resultText, resultLanguage, false, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public int transcribe(byte[] audio, StringBuilder resultText, StringBuilder resultLanguage, boolean profanityFilter) {
        return transcribe(audio, resultText, resultLanguage, profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }
    /**
     *语音识别 sync
     * @param audio     语音消息(NoNull)
     * @param resultText    识别后的文本(NoNull)
     * @param resultLanguage    识别后的语言(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int transcribe(byte[] audio, StringBuilder resultText, StringBuilder resultLanguage, boolean profanityFilter, int timeout) {
        Quest quest = new Quest("transcribe");
        quest.param("audio", audio);
        quest.param("profanityFilter", profanityFilter);


        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            resultText.append(answer.wantString("text"));
            resultLanguage.append(answer.wantString("lang"));
        }
        return code;
    }
}
