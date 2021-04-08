package com.rtmsdk;

import androidx.annotation.NonNull;

import com.rtmsdk.DuplicatedMessageFilter.MessageCategories;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.*;
import java.util.List;

class RTMessage extends RTMMessageCore {
    //重载start
    public void sendMessage(IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, String message){
        sendMessage(callback, uid, mtype, message, "");
    }

    public ModifyTimeStruct sendMessage(long uid, byte mtype, String message){
        return sendMessage(uid, mtype, message, "");
    }

    public void sendMessage(IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, byte[] message){
        sendMessage(callback, uid, mtype, message, "");
    }

    public ModifyTimeStruct sendMessage(long uid, byte mtype, byte[] message){
        return sendMessage(uid, mtype, message, "");
    }


    public void sendGroupMessage(IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, String message) {
        sendGroupMessage(callback, groupId, mtype, message, "");
    }

    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, String message){
        return sendGroupMessage(groupId, mtype, message, "");
    }

    public void sendGroupMessage(IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, byte[] message) {
        sendGroupMessage(callback, groupId, mtype, message, "");
    }

    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, byte[] message){
        return sendGroupMessage(groupId, mtype, message, "");
    }


    public void sendRoomMessage(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, String message) {
        sendRoomMessage(callback, roomId, mtype, message, "");
    }

    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, String message){
        return sendRoomMessage(roomId, mtype, message, "");
    }

    public void sendRoomMessage(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, byte[] message) {
        sendRoomMessage(callback, roomId, mtype, message, "");
    }

    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, byte[] message) {
        return sendRoomMessage(roomId, mtype, message, "");
    }
    //重载end

    /**
     * mtype MUST 51-127, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE.
     */
    /**
     *发送p2p消息(async)
     * @param callback  IRTMCallback<Long>接口回调
     * @param uid       目标用户id
     * @param mtype     消息类型
     * @param message   消息内容
     * @param attrs     客户端自定义信息
     */
    public void sendMessage(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, @NonNull String message, String attrs) {
        internalSendMessage(callback, uid, mtype, message, attrs,  MessageCategories.P2PMessage);
    }

    /**
     *发送p2p消息(sync)
     * @param uid       目标用户id
     * @param mtype     消息类型
     * @param message   消息内容
     * @param attrs     客户端自定义信息
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendMessage(long uid, byte mtype, @NonNull String message, String attrs){
        return internalSendMessage(uid, mtype, message, attrs, MessageCategories.P2PMessage);
    }

    /**
     *发送群组消息(async)
     * @param callback  IRTMCallback<Long>接口回调
     * @param groupId   群组id
     * @param mtype     消息类型
     * @param message   群组消息
     * @param attrs     客户端自定义信息
     */
    public void sendGroupMessage(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, @NonNull String message, String attrs) {
        internalSendMessage(callback, groupId, mtype, message, attrs,  MessageCategories.GroupMessage);
    }

    /**
     *发送群组消息(sync)
     * @param groupId   群组id
     * @param mtype     消息类型
     * @param message   群组消息
     * @param attrs     客户端自定义信息
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, @NonNull String message, String attrs){
        return internalSendMessage(groupId, mtype, message, attrs, MessageCategories.GroupMessage);
    }

    /**
     *发送房间消息(sync)
     * @param callback  IRTMCallback<Long>接口回调
     * @param roomId    房间id
     * @param mtype     消息类型
     * @param message   房间消息
     * @param attrs     客户端自定义信息
     */
    public void sendRoomMessage(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, @NonNull String message, String attrs) {
        internalSendMessage(callback, roomId, mtype, message, attrs,  MessageCategories.RoomMessage);
    }

    /**
     *发送房间消息(sync)
     * @param roomId    房间id
     * @param mtype     消息类型
     * @param message   房间消息
     * @param attrs     客户端自定义信息
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, @NonNull String message, String attrs){
        return internalSendMessage(roomId, mtype, message, attrs, MessageCategories.RoomMessage);
    }


    //===========================[ Sending Binary Messages ]=========================//
    /**参数说明同上
     * mtype MUST 51-127, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE.
     */
    public void sendMessage(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, @NonNull byte[] message, String attrs) {
        internalSendMessage(callback, uid, mtype, message, attrs, MessageCategories.P2PMessage);
    }

    public ModifyTimeStruct sendMessage(long uid, byte mtype, @NonNull byte[] message, String attrs){
        return internalSendMessage(uid, mtype, message, attrs, MessageCategories.P2PMessage);
    }

    //*****sendGroupMessage******//
    public void sendGroupMessage(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, @NonNull byte[] message, String attrs) {
        internalSendMessage(callback, groupId, mtype, message, attrs,  MessageCategories.GroupMessage);
    }

    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, @NonNull byte[] message, String attrs){
        return internalSendMessage(groupId, mtype, message, attrs, MessageCategories.GroupMessage);
    }

    //*****sendRoomMessage******//
    public void sendRoomMessage(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, @NonNull byte[] message, String attrs) {
        internalSendMessage(callback, roomId, mtype, message, attrs,  MessageCategories.RoomMessage);
    }

    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, @NonNull byte[] message, String attrs){
        return internalSendMessage(roomId, mtype, message, attrs, MessageCategories.RoomMessage);
    }

    //===========================[ History Messages ]=========================//
    /**
     *获取p2p记录(async)
     * @param callback  IRTMCallback<HistoryMessageResult> 回调
     * @param peerUid   目标uid
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     */
    public void getP2PHistoryMessage(@NonNull IRTMCallback<HistoryMessageResult> callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes) {
        getHistoryMessage(callback, peerUid, desc, count, beginMsec, endMsec, lastId, mtypes,  DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取p2p记录(sync)
     * @param peerUid   用户id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult getP2PHistoryMessage( long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes){
        return getHistoryMessage(peerUid, desc, count, beginMsec, endMsec, lastId, mtypes,  DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取群组历史消息(async)
     * @param callback  IRTMCallback回调
     * @param groupId  群组id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     */
    public void getGroupHistoryMessage(@NonNull IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes) {
        getHistoryMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, mtypes,  MessageCategories.GroupMessage);
    }

    /**
     *获取群组历史消息(sync)
     * @param groupId   群组id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult getGroupHistoryMessage(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes){
        return getHistoryMessage(groupId, desc, count, beginMsec, endMsec, lastId, mtypes,  MessageCategories.GroupMessage);
    }

    /**
     *获取房间历史消息(async)
     * @param callback  IRTMCallback回调
     * @param roomId    房间id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     */
    public void   getRoomHistoryMessage(@NonNull IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes) {
        getHistoryMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, mtypes,  MessageCategories.RoomMessage);
    }

    /**
     *获取房间历史消息(async)
     * @param roomId    房间id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult   getRoomHistoryMessage(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes){
        return getHistoryMessage(roomId, desc, count, beginMsec, endMsec, lastId, mtypes,  MessageCategories.RoomMessage);
    }

    /**
     *获取广播历史消息(async)
     * @param callback  IRTMCallback回调
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     */
    public void getBroadcastHistoryMessage(@NonNull IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes) {
        getHistoryMessage(callback, -1, desc, count, beginMsec, endMsec, lastId, mtypes,  DuplicatedMessageFilter.MessageCategories.BroadcastMessage);
    }

    /**
     *获取广播历史消息(async)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param mtypes    查询的消息类型
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryMessage( boolean desc, int count, long beginMsec, long endMsec, long lastId, @NonNull List<Byte> mtypes){
        return getHistoryMessage(-1, desc, count, beginMsec, endMsec, lastId, mtypes,  DuplicatedMessageFilter.MessageCategories.BroadcastMessage);
    }

    //===========================[ 获取单条历史记录 ]=========================//
    /**
     *获取p2p单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调
     * @param fromUid   发送者id
     * @param toUid     接收者id
     * @param messageId   消息id
     */
    public void getP2PMessage(@NonNull IRTMCallback<SingleMessage> callback, long fromUid, long toUid, long messageId) {
        getMessage(callback, fromUid, toUid, messageId, MessageCategories.P2PMessage.value());
    }

    /*获取p2p单条消息 sync
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param toUid     接收者id
     */
    public SingleMessage getP2PMessage(long fromUid, long toUid, long messageId){
        return getMessage(fromUid, toUid, messageId, MessageCategories.P2PMessage.value());
    }

    /**
     *删除p2p单条消息 async
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param toUid     接收者id
     */
    public void deleteP2PMessage(@NonNull IRTMEmptyCallback callback, long fromUid, long toUid, long messageId) {
        delMessage(callback,fromUid, toUid, messageId, MessageCategories.P2PMessage.value());
    }

    /**
     * 删除p2p单条消息 sync
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param toUid     接收者id
     */
    public RTMAnswer deleteP2PMessage(long fromUid, long toUid, long messageId){
        return delMessage(fromUid, toUid, messageId, MessageCategories.P2PMessage.value());
    }


    /**
     *获取群组单条消息 async
     * @param callback IRTMCallback<SingleMessage>回调
     * @param fromUid   发送者id
     * @param groupId     群组id
     * @param messageId   消息id
     */
    public void getGroupMessage(@NonNull IRTMCallback<SingleMessage> callback, long fromUid, long groupId, long messageId) {
        getMessage(callback, fromUid, groupId, messageId, MessageCategories.GroupMessage.value());
    }

    /*获取群组单条聊天消息 sync
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param groupId     群组id
     */
    public SingleMessage getGroupMessage(long fromUid, long toUid, long messageId){
        return getMessage(fromUid, toUid, messageId, MessageCategories.GroupMessage.value());
    }

    /**
     *删除群组单条消息 async
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param groupId     群组id
     */
    public void deleteGroupMessage(@NonNull IRTMEmptyCallback callback, long fromUid, long groupId, long messageId) {
        delMessage(callback,fromUid, groupId, messageId, MessageCategories.GroupMessage.value());
    }

    /**
     * 删除群组 单条消息 sync
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param groupId     群组id
     */
    public RTMAnswer deleteGroupMessage(long fromUid, long groupId, long messageId){
        return delMessage(fromUid, groupId, messageId, MessageCategories.GroupMessage.value());
    }

    /**
     *获取房间单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调
     * @param fromUid   发送者id
     * @param roomId     房间id
     * @param messageId   消息id
     */
    public void getRoomMessage(@NonNull IRTMCallback<SingleMessage> callback, long fromUid, long roomId, long messageId) {
        getMessage(callback, fromUid, roomId, messageId, MessageCategories.RoomMessage.value());
    }

    /*获取房间单条聊天消息 sync
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param RoomId     房间id
     */
    public SingleMessage getRoomMessage(long fromUid, long roomId, long messageId){
        return getMessage(fromUid, roomId, messageId, MessageCategories.RoomMessage.value());
    }

    /**
     *删除房间单条消息 async
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param RoomId     房间id
     */
    public void deleteRoomMessage(@NonNull IRTMEmptyCallback callback, long fromUid, long RoomId, long messageId) {
        delMessage(callback,fromUid, RoomId, messageId, MessageCategories.RoomMessage.value());
    }

    /**
     * 删除房间单条消息 sync
     * @param messageId   消息id
     * @param fromUid   发送者id
     * @param roomId     房间id
     */
    public RTMAnswer deleteRoomMessage(long fromUid, long roomId, long messageId){
        return delMessage(fromUid, roomId, messageId, MessageCategories.RoomMessage.value());
    }

    /**
     *获取广播单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调
     * @param messageId   消息id
     */
    public void getBroadcastMessage(@NonNull IRTMCallback<SingleMessage> callback, long messageId) {
        getMessage(callback, -1,0,messageId, MessageCategories.BroadcastMessage.value());
    }

    /*获取广播单条聊天消息 sync
     * @param messageId   消息id
     * @param fromUid   发送者id
     */
    public SingleMessage getBroadcastMessage(long messageId){
        return getMessage(-1,0,messageId, MessageCategories.BroadcastMessage.value());
    }
}
