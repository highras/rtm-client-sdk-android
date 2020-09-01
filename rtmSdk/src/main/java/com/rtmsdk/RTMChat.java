package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.DuplicatedMessageFilter.MessageCategories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class RTMChat extends RTMRoom {
    private List<Byte> chatMTypes = new ArrayList<Byte>() {
        {
            add(MessageType.CHAT);
            add(MessageType.AUDIO);
            add(MessageType.CMD);
            for (byte i = MessageType.IMAGEFILE; i <=MessageType.NORMALFILE; i++)
                add(i);
        }
    };
    //重载start
    public void sendChat(IRTMCallback<Long> callback, long uid, String message) {
        sendChat(callback, uid, message, "", 0);
    }

    public ModifyTimeStruct sendChat(long uid, String message){
        return sendChat(uid, message, "", 0);
    }

    public void sendGroupChat(IRTMCallback<Long> callback, long groupId, String message) {
        sendGroupChat(callback, groupId, message, "", 0);
    }

    public ModifyTimeStruct sendGroupChat(long groupId, String message){
        return sendGroupChat(groupId, message, "", 0);
    }

    public void sendRoomChat(IRTMCallback<Long> callback, long roomId, String message) {
        sendRoomChat(callback, roomId, message, "", 0);
    }

    public ModifyTimeStruct sendRoomChat(long roomId, String message){
        return sendRoomChat(roomId, message, "", 0);
    }

    public ModifyTimeStruct sendCmd(long uid, String message){
        return sendCmd(uid, message, "", 0);
    }

    public void sendCmd(IRTMCallback<Long> callback, long uid, String message) {
        sendCmd(callback,uid, message, "", 0);
    }

    public void sendGroupCmd(IRTMCallback<Long> callback, long groupId, String message) {
        sendGroupCmd(callback, groupId, message, "", 0);
    }

    public ModifyTimeStruct sendGroupCmd(long groupId, String message) {
        return sendGroupCmd(groupId, message, "", 0);
    }

    public void sendRoomCmd(IRTMCallback<Long> callback, long roomId, String message){
        sendRoomCmd(callback, roomId, message, "", 0);
    }
    public ModifyTimeStruct sendRoomCmd(long roomId, String message){
        return sendRoomCmd(roomId, message, "", 0);
    }
    public void sendAudio(IRTMCallback<Long> callback, long uid, File file) {
        sendAudio(callback, uid, file, "", 0);
    }

    public ModifyTimeStruct sendAudio(long uid, File file){
        return sendAudio(uid, file, "", 0);
    }

    public void sendGroupAudio(IRTMCallback<Long> callback, long groupId, File file) {
        sendGroupAudio(callback, groupId, file, "", 0);
    }

    public ModifyTimeStruct sendGroupAudio(long groupId, File file){
        return sendGroupAudio(groupId,file, "",0);
    }

    public void sendRoomAudio(IRTMCallback<Long> callback, long roomId, File file) {
        sendRoomAudio(callback, roomId, file, "", 0);
    }

    public ModifyTimeStruct sendRoomAudio(long roomId, File file){
        return sendRoomAudio(roomId, file, "",0);
    }

    public void getP2PHistoryChat(IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getP2PHistoryChat(callback, toUid, desc, count, beginMsec, endMsec, lastId, 0);
    }

    public HistoryMessageResult getP2PHistoryChat(long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getP2PHistoryChat(toUid, desc, count, beginMsec, endMsec, lastId, 0);
    }

    public void getGroupHistoryChat(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getGroupHistoryChat(callback, groupId, desc, count, beginMsec, endMsec, lastId, 0);
    }

    public HistoryMessageResult getGroupHistoryChat(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getGroupHistoryChat(groupId, desc,count, beginMsec, endMsec, lastId, 0);
    }

    public void getRoomHistoryChat(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getRoomHistoryChat(callback, roomId, desc,count, beginMsec, endMsec, lastId, 0);
    }

    public HistoryMessageResult getRoomHistoryChat(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getRoomHistoryChat(roomId, desc,count, beginMsec, endMsec, lastId, 0);
    }

    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getBroadcastHistoryChat(callback, desc,count, beginMsec, endMsec, lastId, 0);
    }

    public HistoryMessageResult getBroadcastHistoryChat(boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getBroadcastHistoryChat(desc,count, beginMsec, endMsec, lastId, 0);
    }

    public void getUnread(IRTMCallback<Unread> callback) {
        getUnread(callback, false, 0);
    }

    public Unread getUnread(){
        return getUnread(false, 0);
    }

    public void clearUnread(IRTMEmptyCallback callback) {
        clearUnread(callback, 0);
    }

    public RTMAnswer clearUnread() {
        return clearUnread(0);
    }

    public void getSession(IRTMCallback<Unread> callback) {
        getSession(callback, 0);
    }

    public Unread getSession(){
        return getSession(0);
    }

    public void setTranslatedLanguage(IRTMEmptyCallback callback, String targetLanguage) {
        setTranslatedLanguage(callback, targetLanguage, 0);
    }

    public RTMAnswer setTranslatedLanguage(String targetLanguage){
        return setTranslatedLanguage(targetLanguage, 0);
    }

    public void translate(IRTMCallback<TranslatedInfo> callback, String text, String trargetLanguage) {
        translate(callback, text, trargetLanguage, "", RTMConfig.globalTranslateQuestTimeoutSeconds, translateType.Chat, ProfanityType.Off);
    }

    public TranslatedInfo translate(String text, String trargetLanguage){
        return translate(text, trargetLanguage, "", RTMConfig.globalTranslateQuestTimeoutSeconds, translateType.Chat, ProfanityType.Off);
    }

    public void profanity(IRTMCallback<ProfanityStruct> callback, String text) {
        profanity(callback, text, false, 0);
    }

    public ProfanityStruct profanity(String text){
        return profanity(text, false, 0);
    }

    public void transcribe(IRTMCallback<TranscribeStruct> callback, File file) {
        transcribe(callback, file, false, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public TranscribeStruct transcribe(File file){
        return transcribe(file, false, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public void transcribeP2P(IRTMCallback<TranscribeStruct> callback, long fromUid, long toUid, long messageId,boolean profanityFilter) {
        transcribeP2P(callback, fromUid, toUid, messageId, profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public TranscribeStruct transcribeP2P(long fromUid, long toUid, long messageId,boolean profanityFilter) {
        return transcribeP2P(fromUid, toUid,messageId,profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public void transcribeGroup(IRTMCallback<TranscribeStruct> callback, long fromUid, long groupId, long messageId,boolean profanityFilter) {
        transcribeGroup(callback, fromUid, groupId, messageId, profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public TranscribeStruct transcribeGroup(long fromUid, long groupId, long messageId,boolean profanityFilter) {
        return transcribeGroup(fromUid, groupId,messageId,profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public void transcribeRoom(IRTMCallback<TranscribeStruct> callback, long fromUid, long roomId, long messageId,boolean profanityFilter) {
        transcribeRoom(callback, fromUid, roomId, messageId, profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public TranscribeStruct transcribeRoom(long fromUid, long roomId, long messageId,boolean profanityFilter) {
        return transcribeRoom(fromUid, roomId,messageId,profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public void transcribeBroadcast(IRTMCallback<TranscribeStruct> callback, long messageId,boolean profanityFilter) {
        transcribeBroadcast(callback, messageId, profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public TranscribeStruct transcribeBroadcast(long messageId,boolean profanityFilter) {
        return transcribeBroadcast(messageId,profanityFilter, RTMConfig.globalTranslateQuestTimeoutSeconds);
    }
    //重载end

    /**
     *发送p2p聊天消息(async)
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param uid       目标用户id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendChat(IRTMCallback<Long> callback, long uid, String message, String attrs, int timeout) {
        internalSendChat(callback, uid, MessageType.CHAT, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送p2p聊天消息(sync)
     * @param uid       目标用户id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendChat(long uid, String message, String attrs, int timeout){
        return internalSendChat(uid, MessageType.CHAT, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送群组聊天消息(async)
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param groupId   群组id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendGroupChat(IRTMCallback<Long> callback, long groupId, String message, String attrs, int timeout) {
        internalSendChat(callback, groupId, MessageType.CHAT, message, attrs, timeout, MessageCategories.GroupMessage);
    }

    /**
     *发送群组聊天消息(sync)
     * @param groupId   群组id(NoNull)
     * @param message   群组聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendGroupChat(long groupId, String message, String attrs, int timeout) {
        return internalSendChat(groupId, MessageType.CHAT, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     *
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param roomId    房间id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendRoomChat(IRTMCallback<Long> callback, long roomId, String message, String attrs, int timeout){
        internalSendChat(callback, roomId, MessageType.CHAT, message, attrs, timeout, MessageCategories.RoomMessage);
    }

    /**
     *发送房间聊天消息(sync)
     * @param roomId    房间id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendRoomChat(long roomId, String message, String attrs, int timeout){
        return internalSendChat(roomId, MessageType.CHAT, message, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *p2p指令消息(async)
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param uid       目标用户id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendCmd(IRTMCallback<Long> callback, long uid, String message, String attrs, int timeout) {
        internalSendChat(callback, uid, MessageType.CMD, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送p2p指令消息(sync)
     * @param uid       目标用户id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendCmd(long uid, String message, String attrs, int timeout){
        return internalSendChat(uid, MessageType.CMD, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送群组指令(async)
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param groupId   群组id(NoNull)
     * @param message   群组指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendGroupCmd(IRTMCallback<Long> callback, long groupId, String message, String attrs, int timeout) {
        internalSendChat(callback, groupId, MessageType.CMD, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     *发送群组指令消息(sync)
     * @param groupId   群组id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendGroupCmd(long groupId, String message, String attrs, int timeout){
        return internalSendChat(groupId, MessageType.CMD, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     *发送房间指令
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param roomId    房间id(NoNull)
     * @param message   房间指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)

     */
    public void sendRoomCmd(IRTMCallback<Long> callback, long roomId, String message, String attrs, int timeout){
        internalSendChat(callback, roomId, MessageType.CMD, message, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *发送房间指令消息(sync)
     * @param roomId   房间id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendRoomCmd(long roomId, String message, String attrs, int timeout){
        return internalSendChat(roomId, MessageType.CMD, message, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *发送p2p语音(async)
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param uid       目标用户id(NoNull)
     * @param file   语音文件(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendAudio(IRTMCallback<Long> callback, long uid, File file, String attrs, int timeout) {
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        internalSendChat(callback, uid, MessageType.AUDIO, data, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送p2p语音(sync)
     * @param uid       目标用户id(NoNull)
     * @param file   语音文件(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendAudio(long uid, File file, String attrs, int timeout){
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        return internalSendChat(uid, MessageType.AUDIO, data, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送群组语音
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param groupId   群组id(NoNull)
     * @param file   语音文件(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendGroupAudio(IRTMCallback<Long> callback, long groupId, File file, String attrs, int timeout) {
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        internalSendChat(callback, groupId, MessageType.AUDIO, data, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     *发送群组语音(sync)
     * @param groupId   群组id(NoNull)
     * @param file   语音文件(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendGroupAudio(long groupId, File file, String attrs, int timeout){
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        return internalSendChat(groupId, MessageType.AUDIO, data, attrs, timeout,MessageCategories.GroupMessage);
    }
    /**
     *发送房间语音(async)
     * @param callback  IRTMCallback<Long>接口回调(long为服务器返回时间)
     * @param roomId    房间id(NoNull)
     * @param file   语音文件(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
    public void sendRoomAudio(IRTMCallback<Long> callback, long roomId, File file, String attrs, int timeout) {
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        internalSendChat(callback, roomId, MessageType.AUDIO, data, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *发送房间语音(sync)
     * @param roomId    房间id(NoNull)
     * @param file   语音文件(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    public ModifyTimeStruct sendRoomAudio(long roomId, File file, String attrs, int timeout){
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        return internalSendChat(roomId, MessageType.AUDIO, data, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *获取p2p聊天记录(async)
     * @param callback  HistoryMessageResult类型回调(NoNull)
     * @param toUid   目标uid(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     */
    public void getP2PHistoryChat(IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, toUid, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取p2p聊天记录(sync)
     * @param toUid   用户id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * return       HistoryMessageResult结构
     */
    public HistoryMessageResult getP2PHistoryChat(long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){
        return getHistoryMessage(toUid, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取群组聊天记录(async)
     * @param callback  IRTMCallback<HistoryMessageResult>接口回调(HistoryMessageResult为历史消息结构)
     * @param groupId   群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     */
    public void getGroupHistoryChat(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.GroupMessage);
    }

    /**
     *获取群组聊天记录(sync)
     * @param groupId   群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     */
    public HistoryMessageResult getGroupHistoryChat(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){
        return getHistoryMessage(groupId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.GroupMessage);
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
     */
    public void getRoomHistoryChat(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.RoomMessage);
    }

    /**
     *获取房间聊天记录(sync)
     * @param roomId    房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     *return            HistoryMessageResult
     */
    public HistoryMessageResult getRoomHistoryChat(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){
        return getHistoryMessage(roomId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.RoomMessage);
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
     */
    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, -1, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.BroadcastMessage);
    }

    /**
     * 获得广播历史聊天消息(sync)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryChat(boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){
        return getHistoryMessage(-1, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.BroadcastMessage);
    }

    /**
     *获取服务器未读消息(async)
     * @param callback  IRTMCallback<Unread> 回调
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     */
    public void getUnread(final IRTMCallback<Unread> callback, boolean clear, int timeout) {
        Quest quest = new Quest("getunread");
        quest.param("clear", clear);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                Unread ret = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    ret = new Unread();
                    List<Long> p2pList = new ArrayList<>();
                    List<Long> groupList = new ArrayList<>();
                    RTMUtils.wantLongList(answer,"p2p", p2pList);
                    RTMUtils.wantLongList(answer,"group", groupList);
                    ret.p2pList = p2pList;
                    ret.groupList = groupList;
                }
                callback.onResult(ret, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /*获取服务器未读消息(sync)
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     * return           Unread 结构
     */
    public Unread getUnread( boolean clear, int timeout){
        Quest quest = new Quest("getunread");
        quest.param("clear", clear);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);

        Unread ret = new Unread();
        List<Long> p2pList = new ArrayList<>();
        List<Long> groupList = new ArrayList<>();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            RTMUtils.wantLongList(answer, "p2p", p2pList);
            RTMUtils.wantLongList(answer, "group", groupList);
        }
        ret.p2pList = p2pList;
        ret.groupList = groupList;
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        return ret;
    }

    /**
     *清除离线提醒 async
     * @param callback EmptyCallback回调(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void clearUnread(IRTMEmptyCallback callback, int timeout) {
        Quest quest = new Quest("cleanunread");
        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /*清除离线提醒 sync
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer clearUnread(int timeout) {
        Quest quest = new Quest("cleanunread");
        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 获取和自己有过会话的用户uid和群组id集合 async
     * @param callback UnreadCallback回调(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getSession(final IRTMCallback<Unread> callback, int timeout) {
        Quest quest = new Quest("getsession");
        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                Unread ret = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    ret = new Unread();
                    List<Long> p2pList = new ArrayList<>();
                    List<Long> groupList = new ArrayList<>();
                    RTMUtils.wantLongList(answer,"p2p", p2pList);
                    RTMUtils.wantLongList(answer,"group", groupList);
                    ret.p2pList = p2pList;
                    ret.groupList = groupList;
                }
                callback.onResult(ret, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取和自己有过会话的用户uid和群组id集合 sync
     * @param timeout   超时时间(秒)
     * return           Unread 结构
     */
    public Unread getSession(int timeout){
        Quest quest = new Quest("getsession");

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);

        Unread ret = new Unread();
        List<Long> p2pList = new ArrayList<>();
        List<Long> groupList = new ArrayList<>();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            RTMUtils.wantLongList(answer, "p2p", p2pList);
            RTMUtils.wantLongList(answer, "group", groupList);
        }
        ret.p2pList = p2pList;
        ret.groupList = groupList;
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        return ret;
    }

    //===========================[ 翻译,语音识别,敏感词过滤相关 ]=========================//
    public enum translateType {
        Chat,
        Mail
    }

    public enum ProfanityType {
        Off,
        Stop,
        Censor
    }
    /**
     *设置目标翻译语言 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void setTranslatedLanguage(IRTMEmptyCallback callback, String targetLanguage, int timeout) {
        Quest quest = new Quest("setlang");
        quest.param("lang", targetLanguage);
        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 设置翻译的目标语言 sync
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer setTranslatedLanguage(String targetLanguage, int timeout){
        Quest quest = new Quest("setlang");
        quest.param("lang", targetLanguage);
        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     *文本翻译 async(调用此接口需在管理系统启用翻译系统）
     * @param callback      TranslateCallback回调(NoNull)
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param timeout               超时时间(秒)
     * @param type                  可选值为chat或mail。如未指定，则默认使用'chat'
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off
     */
    public void translate(final IRTMCallback<TranslatedInfo> callback, String text, String destinationLanguage, String sourceLanguage, int timeout,
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

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                TranslatedInfo tm = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    tm = new TranslatedInfo();
                    tm.source = answer.wantString("source");
                    tm.target = answer.wantString("target");
                    tm.sourceText = answer.wantString("sourceText");
                    tm.targetText = answer.wantString("targetText");
                }
                callback.onResult(tm, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     *文本翻译 sync(调用此接口需在管理系统启用翻译系统）
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param timeout               超时时间(秒)
     * @param type                  可选值为chat或mail。如未指定，则默认使用'chat'
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off
     * @return                  TranslatedInfo结构
     */
    public TranslatedInfo translate(String text, String destinationLanguage, String sourceLanguage, int timeout,
                         translateType type, ProfanityType profanity){
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
        RTMAnswer result = genRTMAnswer(answer);

        TranslatedInfo translatedMessage = new TranslatedInfo();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            translatedMessage.source = answer.wantString("source");
            translatedMessage.target = answer.wantString("target");
            translatedMessage.sourceText = answer.wantString("sourceText");
            translatedMessage.targetText = answer.wantString("targetText");
        }
        translatedMessage.errorCode = result.errorCode;
        translatedMessage.errorMsg = result.errorMsg;
        return translatedMessage;
    }

    /**
     *敏感词过滤 async(调用此接口需在管理系统启用文本检测系统)
     * @param callback  ProfanityCallback回调(NoNull)
     * @param text      需要过滤的文本
     * @param classify  是否进行文本分类检测
     * @param timeout   超时时间(秒)
     */
    public void profanity(final IRTMCallback<ProfanityStruct> callback, String text, boolean classify, int timeout) {
        Quest quest = new Quest("profanity");
        quest.param("text", text);
        quest.param("classify", classify);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                ProfanityStruct profanityResult = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    profanityResult  = new ProfanityStruct();
                    profanityResult.text = answer.wantString("text");
                    profanityResult.classification = (ArrayList<String>)answer.get("classification",null);
                }
                callback.onResult(profanityResult, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     *敏感词过滤 sync(调用此接口需在管理系统启用文本检测系统）
     * @param text          需要过滤的文本(NoNull)
     * @param classify      是否进行文本分类检测
     * @param timeout       超时时间(秒)
     * @return              ProfanityResult
     */
    public ProfanityStruct profanity(String text, boolean classify, int timeout){
        Quest quest = new Quest("profanity");
        quest.param("text", text);
        quest.param("classify", classify);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        ProfanityStruct profanityResult = new ProfanityStruct();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            profanityResult.text = answer.wantString("text");
            profanityResult.classification = (ArrayList<String>) answer.get("classification", null);
        }
        profanityResult.errorCode = result.errorCode;
        profanityResult.errorMsg = result.errorMsg;
        return profanityResult;
    }

    /**
     *语音识别 async(调用此接口需在管理系统启用语音识别系统,调用这个接口的超时时间得加大到120s)
     * @param callback  IRTMCallback<TranscribeStruct>回调(NoNull)
     * @param file     语音文件(NoNull)
     * @param profanityFilter     是否开启敏感词过滤(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void transcribe(final IRTMCallback<TranscribeStruct> callback, File file, boolean profanityFilter, int timeout) {
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        Quest quest = new Quest("transcribe");
        quest.param("audio", data);
        quest.param("profanityFilter", profanityFilter);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                TranscribeStruct transcribInfo = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    transcribInfo = new TranscribeStruct();
                    transcribInfo.resultText = answer.wantString("text");
                    transcribInfo.resultLang = answer.wantString("lang");
                }
                callback.onResult(transcribInfo, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     *语音识别 sync(调用此接口需在管理系统启用语音识别系统,调用这个接口的超时时间得加大到120s)
     * @param file     语音文件(NoNull)
     * @param timeout   超时时间(秒)
     * @return          TranscribeStruct结构
     */
    public TranscribeStruct transcribe(File file, boolean profanityFilter, int timeout){
        byte[] data = RTMAudio.getInstance().genAudioData(file);
        Quest quest = new Quest("transcribe");
        quest.param("audio", data);
        quest.param("profanityFilter", profanityFilter);
        Answer answer = sendQuest(quest, timeout);
        TranscribeStruct transcribInfo = new TranscribeStruct();
        RTMAnswer result = genRTMAnswer(answer);
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            transcribInfo.resultText = answer.wantString("text");
            transcribInfo.resultLang = answer.wantString("lang");
        }
        transcribInfo.errorCode = result.errorCode;
        transcribInfo.errorMsg = result.errorMsg;
        return transcribInfo;
    }

    //===========================[ 语音识别  此接口只支持通过rtm发送的语音消息，无需把原始语音再一次发送，节省流量]=========================//
    /**
     /*语音识别 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid/groupId/roomId    接收者id/房间id/群组id(NoNull)
     * @param profanityFilter 是否就进行文本过滤
     * @param timeout       超时时间(秒)
     * @return              TranscribeStruct结构
     */
    public TranscribeStruct transcribeP2P(long fromUid, long toUid, long messageId, boolean profanityFilter, int timeout) {
        return stranscribe(fromUid,toUid,messageId,1, profanityFilter,timeout);
    }

    public TranscribeStruct transcribeGroup(long fromUid, long groupId, long messageId, boolean profanityFilter, int timeout) {
        return stranscribe(fromUid,groupId,messageId,2, profanityFilter,timeout);
    }

    public TranscribeStruct transcribeRoom(long fromUid, long roomId, long messageId, boolean profanityFilter, int timeout) {
        return stranscribe(fromUid,roomId,messageId,3, profanityFilter,timeout);
    }

    public TranscribeStruct transcribeBroadcast(long messageId, boolean profanityFilter, int timeout) {
        return stranscribe(-1,0,messageId,4, profanityFilter,timeout);
    }

    /**
     *语音识别 async(调用此接口需在管理系统启用语音识别系统)
     * @param callback  IRTMCallback<TranscribeStruct>回调(NoNull)
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid/groupId/roomId    接收者id/房间id/群组id(NoNull)
     * @param profanityFilter 是否就进行文本过滤
     * @param timeout       超时时间(秒)
     */
    public void transcribeP2P(IRTMCallback<TranscribeStruct> callback,long fromUid, long toUid, long messageId, boolean profanityFilter, int timeout) {
        stranscribe(callback, fromUid,toUid,messageId,1, profanityFilter,timeout);
    }

    public void transcribeGroup(IRTMCallback<TranscribeStruct> callback,long fromUid, long groupId, long messageId, boolean profanityFilter, int timeout) {
        stranscribe(callback,fromUid,groupId,messageId,2, profanityFilter,timeout);
    }

    public void transcribeRoom(IRTMCallback<TranscribeStruct> callback,long fromUid, long roomId, long messageId, boolean profanityFilter, int timeout) {
        stranscribe(callback,fromUid,roomId,messageId,3, profanityFilter,timeout);
    }

    public void transcribeBroadcast(IRTMCallback<TranscribeStruct> callback,long messageId, boolean profanityFilter, int timeout) {
        stranscribe(callback,-1,0,messageId,4, profanityFilter,timeout);
    }
    //===========================[ end]=========================//


    TranscribeStruct stranscribe(long fromUid, long xid, long messageId, int type, boolean profanityFilter,int timeout){
        Quest quest = new Quest("stranscribe");
        quest.param("mid", messageId);
        quest.param("xid", xid);
        quest.param("from", fromUid);
        quest.param("type", type);
        quest.param("profanityFilter", profanityFilter);

        Answer answer = sendQuest(quest, timeout);
        TranscribeStruct transcribInfo = new TranscribeStruct();
        RTMAnswer result = genRTMAnswer(answer);
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            transcribInfo.resultText = answer.wantString("text");
            transcribInfo.resultLang = answer.wantString("lang");
        }
        transcribInfo.errorCode = result.errorCode;
        transcribInfo.errorMsg = result.errorMsg;
        return transcribInfo;
    }

    void stranscribe(final IRTMCallback<TranscribeStruct> callback,long fromUid, long xid, long messageId, int type, boolean profanityFilter,int timeout){
        Quest quest = new Quest("stranscribe");
        quest.param("mid", messageId);
        quest.param("xid", xid);
        quest.param("from", fromUid);
        quest.param("type", type);
        quest.param("profanityFilter", profanityFilter);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                TranscribeStruct transcribInfo = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    transcribInfo = new TranscribeStruct();
                    transcribInfo.resultText = answer.wantString("text");
                    transcribInfo.resultLang = answer.wantString("lang");
                }
                callback.onResult(transcribInfo, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

}
