package com.rtmsdk;


import com.rtmsdk.DuplicatedMessageFilter.MessageCategories;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.*;

import java.util.List;

class RTMessage extends RTMMessageCore {
    //重载start
    public void sendMessage(IRTMCallback<Long> callback, long uid, byte mtype, String message){
        sendMessage(callback, uid, mtype, message, "", 0);
    }

    public ModifyTimeStruct sendMessage(long uid, byte mtype, String message){
        return sendMessage(uid, mtype, message, "", 0);
    }

    public void sendGroupMessage(IRTMCallback<Long> callback, long groupId, byte mtype, String message) {
        sendGroupMessage(callback, groupId, mtype, message, "", 0);
    }

    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, String message){
        return sendGroupMessage(groupId, mtype, message, "", 0);
    }

    public void sendRoomMessage(IRTMCallback<Long> callback, long roomId, byte mtype, String message) {
        sendRoomMessage(callback, roomId, mtype, message, "", 0);
    }

    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, String message) throws Exception{
        return sendRoomMessage(roomId, mtype, message, "", 0);
    }

    public void getP2PHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long peerUid, boolean desc, int count) {
        getP2PHistoryMessage(callback, peerUid, desc, count, 0, 0, 0, null, 0);
    }

    public HistoryMessageResult getP2PHistoryMessage(HistoryMessageResult result, long peerUid, boolean desc, int count){
        return getP2PHistoryMessage(peerUid, desc, count, 0, 0, 0, null, 0);
    }

    public void getGroupHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count) {
        getGroupHistoryMessage(callback, groupId, desc, count, 0, 0, 0, null, 0);
    }

    public HistoryMessageResult getGroupHistoryMessage(HistoryMessageResult result, long groupId, boolean desc, int count){
        return getGroupHistoryMessage(groupId, desc, count, 0, 0, 0, null, 0);
    }

    public void  getRoomHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count) {
        getRoomHistoryMessage(callback, roomId, desc, count, 0, 0, 0, null, 0);
    }

    public HistoryMessageResult  getRoomHistoryMessage(HistoryMessageResult result, long roomId, boolean desc, int count){
        return   getRoomHistoryMessage(roomId, desc, count, 0, 0, 0, null, 0);
    }

    public void getBroadcastHistoryMessage(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count) {
        getBroadcastHistoryMessage(callback, desc, count, 0, 0, 0, null, 0);
    }
    public HistoryMessageResult getBroadcastHistoryMessage(HistoryMessageResult result, boolean desc, int count){
        return getBroadcastHistoryMessage(desc, count, 0, 0, 0, null, 0);
    }

    public void getP2PMessage(IRTMCallback<SingleMessage> callback, long fromUid, long toUid, long messageId) {
        getP2PMessage(callback, fromUid, toUid, messageId, 0);
    }

    public SingleMessage getP2PMessage(long fromUid, long toUid, long messageId){
        return getP2PMessage(fromUid, toUid, messageId, 0);
    }

    public void deleteP2PMessage(IRTMEmptyCallback callback, long fromUid, long toUid, long messageId) {
        deleteP2PMessage(callback,fromUid, toUid, messageId,0);
    }

    public RTMAnswer deleteP2PMessage(long fromUid, long toUid, long messageId){
        return deleteP2PMessage(fromUid, toUid, messageId, 0);
    }

    public void getGroupMessage(IRTMCallback<SingleMessage> callback, long fromUid, long groupId, long messageId) {
        getGroupMessage(callback, fromUid, groupId, messageId, 0);
    }

    public SingleMessage getGroupMessage(long fromUid, long groupId, long messageId){
        return getGroupMessage(fromUid, groupId, messageId, 0);
    }

    public void deleteGroupMessage(IRTMEmptyCallback callback, long fromUid, long groupId, long messageId) {
        deleteGroupMessage(callback,fromUid, groupId, messageId,0);
    }

    public RTMAnswer deleteGroupMessage(long fromUid, long groupId, long messageId){
        return deleteGroupMessage(fromUid, groupId, messageId, 0);
    }

    public void getRoomMessage(IRTMCallback<SingleMessage> callback, long fromUid, long roomId, long messageId) {
        getRoomMessage(callback, fromUid, roomId, messageId, 0);
    }

    public SingleMessage getRoomMessage(long fromUid, long roomId, long messageId){
        return getRoomMessage(fromUid, roomId, messageId, 0);
    }

    public void deleteRoomMessage(IRTMEmptyCallback callback, long fromUid, long roomId, long messageId) {
        deleteRoomMessage(callback,fromUid, roomId, messageId,0);
    }

    public RTMAnswer deleteRoomMessage(long fromUid, long roomId, long messageId){
        return deleteRoomMessage(fromUid, roomId, messageId, 0);
    }
    //重载end

    /**
     * mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE.
     */
    /**
     *发送p2p消息(async)
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param uid       目标用户id(NoNull)
     * @param mtype     消息类型
     * @param message   p2p消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendMessage(IRTMCallback<Long> callback, long uid, byte mtype, String message, String attrs, int timeout) {
        internalSendMessage(callback, uid, mtype, message, attrs, timeout, MessageCategories.P2PMessage);
    }

    /**
     *发送p2p消息(sync)
     * @param uid       目标用户id(NoNull)
     * @param mtype     消息类型
     * @param message   消息内容(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendMessage(long uid, byte mtype, String message, String attrs, int timeout){
        return internalSendMessage(uid, mtype, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送群组消息(async)
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型
     * @param message   群组消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendGroupMessage(IRTMCallback<Long> callback, long groupId, byte mtype, String message, String attrs, int timeout) {
        internalSendMessage(callback, groupId, mtype, message, attrs, timeout, MessageCategories.GroupMessage);
    }

    /**
     *发送群组消息(sync)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型
     * @param message   群组消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, String message, String attrs, int timeout){
        return internalSendMessage(groupId, mtype, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     *发送房间消息(sync)
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param roomId    房间id(NoNull)
     * @param mtype     消息类型
     * @param message   房间消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendRoomMessage(IRTMCallback<Long> callback, long roomId, byte mtype, String message, String attrs, int timeout) {
        internalSendMessage(callback, roomId, mtype, message, attrs, timeout, MessageCategories.RoomMessage);
    }

    /**
     *发送房间消息(sync)
     * @param roomId    房间id(NoNull)
     * @param mtype     消息类型
     * @param message   房间消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, String message, String attrs, int timeout){
        return internalSendMessage(roomId, mtype, message, attrs, timeout,MessageCategories.RoomMessage);
    }


    //===========================[ Sending Binary Messages ]=========================//
    /**参数说明同上
     * mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE.
     */
    public void sendMessage(IRTMCallback<Long> callback, long uid, byte mtype, byte[] message, String attrs, int timeout) {
        internalSendMessage(callback, uid, mtype, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    public ModifyTimeStruct sendMessage(long uid, byte mtype, byte[] message, String attrs, int timeout){
        return internalSendMessage(uid, mtype, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    //*****sendGroupMessage******//
    public void sendGroupMessage(IRTMCallback<Long> callback, long groupId, byte mtype, byte[] message, String attrs, int timeout) {
        internalSendMessage(callback, groupId, mtype, message, attrs, timeout, MessageCategories.GroupMessage);
    }

    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, byte[] message, String attrs, int timeout){
        return internalSendMessage(groupId, mtype, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    //*****sendRoomMessage******//
    public void sendRoomMessage(IRTMCallback<Long> callback, long roomId, byte mtype, byte[] message, String attrs, int timeout) {
        internalSendMessage(callback, roomId, mtype, message, attrs, timeout, MessageCategories.RoomMessage);
    }

    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, byte[] message, String attrs, int timeout){
        return internalSendMessage(roomId, mtype, message, attrs, timeout,MessageCategories.RoomMessage);
    }

    //===========================[ History Messages ]=========================//
    /**
     *获取p2p记录(async)
     * @param callback  IRTMCallback<HistoryMessageResult> 回调(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     */
    public void getP2PHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        getHistoryMessage(callback, peerUid, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取p2p记录(sync)
     * @param peerUid   用户id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult getP2PHistoryMessage( long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout){
        return getHistoryMessage(peerUid, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取群组历史消息(async)
     * @param callback  IRTMCallback回调(NoNull)
     * @param groupId  群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     */
    public void getGroupHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        getHistoryMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, MessageCategories.GroupMessage);
    }

    /**
     *获取群组历史消息(sync)
     * @param groupId   群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult getGroupHistoryMessage(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout){
        return getHistoryMessage(groupId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, MessageCategories.GroupMessage);
    }

    /**
     *获取房间历史消息(async)
     * @param callback  IRTMCallback回调(NoNull)
     * @param roomId    房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     */
    public void   getRoomHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        getHistoryMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, MessageCategories.RoomMessage);
    }

    /**
     *获取房间历史消息(async)
     * @param roomId    房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult   getRoomHistoryMessage(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout){
        return getHistoryMessage(roomId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, MessageCategories.RoomMessage);
    }

    /**
     *获取广播历史消息(async)
     * @param callback  IRTMCallback回调(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     */
    public void getBroadcastHistoryMessage(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        getHistoryMessage(callback, -1, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.BroadcastMessage);
    }

    /**
     *获取广播历史消息(async)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return          HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryMessage( boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout){
        return getHistoryMessage(-1, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.BroadcastMessage);
    }

    //===========================[ 获取单条历史记录 ]=========================//
    /**
     *获取p2p单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid     接收者id(NoNull)
     * @param messageId   消息id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getP2PMessage(IRTMCallback<SingleMessage> callback, long fromUid, long toUid, long messageId, int timeout) {
        getMessage(callback, fromUid, toUid, messageId, MessageCategories.P2PMessage.value(), timeout);
    }

    /*获取p2p单条聊天消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid     接收者id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public SingleMessage getP2PMessage(long fromUid, long toUid, long messageId, int timeout){
        return getMessage(fromUid, toUid, messageId, MessageCategories.P2PMessage.value(), timeout);
    }

    /**
     *删除p2p单条消息 async
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid     接收者id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void deleteP2PMessage(IRTMEmptyCallback callback, long fromUid, long toUid, long messageId,  int timeout) {
        delMessage(callback,fromUid, toUid, messageId, MessageCategories.P2PMessage.value(),timeout);
    }

    /**
     * 删除p2p单条消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid     接收者id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer deleteP2PMessage(long fromUid, long toUid, long messageId, int timeout){
        return delMessage(fromUid, toUid, messageId, MessageCategories.P2PMessage.value(),timeout);
    }


    /**
     *获取群组单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param groupId     群组id(NoNull)
     * @param messageId   消息id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getGroupMessage(IRTMCallback<SingleMessage> callback, long fromUid, long groupId, long messageId, int timeout) {
        getMessage(callback, fromUid, groupId, messageId, MessageCategories.GroupMessage.value(), timeout);
    }

    /*获取群组单条聊天消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param groupId     群组id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public SingleMessage getGroupMessage(long fromUid, long toUid, long messageId, int timeout){
        return getMessage(fromUid, toUid, messageId, MessageCategories.GroupMessage.value(), timeout);
    }

    /**
     *删除群组单条消息 async
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param groupId     群组id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void deleteGroupMessage(IRTMEmptyCallback callback, long fromUid, long groupId, long messageId,  int timeout) {
        delMessage(callback,fromUid, groupId, messageId, MessageCategories.GroupMessage.value(),timeout);
    }

    /**
     * 删除群组 单条消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param groupId     群组id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer deleteGroupMessage(long fromUid, long groupId, long messageId, int timeout){
        return delMessage(fromUid, groupId, messageId, MessageCategories.GroupMessage.value(),timeout);
    }

    /**
     *获取房间单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param roomId     房间id(NoNull)
     * @param messageId   消息id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getRoomMessage(IRTMCallback<SingleMessage> callback, long fromUid, long roomId, long messageId, int timeout) {
        getMessage(callback, fromUid, roomId, messageId, MessageCategories.RoomMessage.value(), timeout);
    }

    /*获取房间单条聊天消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param RoomId     房间id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public SingleMessage getRoomMessage(long fromUid, long roomId, long messageId, int timeout){
        return getMessage(fromUid, roomId, messageId, MessageCategories.RoomMessage.value(), timeout);
    }

    /**
     *删除房间单条消息 async
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param RoomId     房间id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void deleteRoomMessage(IRTMEmptyCallback callback, long fromUid, long RoomId, long messageId,  int timeout) {
        delMessage(callback,fromUid, RoomId, messageId, MessageCategories.RoomMessage.value(),timeout);
    }

    /**
     * 删除房间单条消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param roomId     房间id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer deleteRoomMessage(long fromUid, long roomId, long messageId, int timeout){
        return delMessage(fromUid, roomId, messageId, MessageCategories.RoomMessage.value(),timeout);
    }

    /**
     *获取广播单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调(NoNull)
     * @param messageId   消息id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getBroadcastMessage(IRTMCallback<SingleMessage> callback, long messageId, int timeout) {
        getMessage(callback, -1,0,messageId, MessageCategories.BroadcastMessage.value(), timeout);
    }

    /*获取广播单条聊天消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public SingleMessage getBroadcastMessage(long messageId, int timeout){
        return getMessage(-1,0,messageId, MessageCategories.BroadcastMessage.value(), timeout);
    }
}
