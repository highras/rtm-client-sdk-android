package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.DuplicatedMessageFilter.MessageCategories;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class RTMChat extends RTMRoom {
    private List<Byte> chatMTypes = new ArrayList<Byte>() {
        {
            add(MessageType.CHAT);
            add(MessageType.CMD);
            add(MessageType.IMAGEFILE);
            add(MessageType.AUDIOFILE);
            add(MessageType.VIDEOFILE);
            add(MessageType.NORMALFILE);
        }
    };
    private enum CheckType {
        PIC,
        AUDIO,
        VIDEO
    }

    //图片/音频/视频检测类型
    private enum CheckSourceType {
        URL, //url地址
        CONTENT //二进制内容
    }

    //重载start
    public void sendChat(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message) {
        sendChat(callback, uid, message, "", 0);
    }

    public ModifyTimeStruct sendChat(long uid, String message){
        return sendChat(uid, message, "", 0);
    }

    public void sendGroupChat(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message) {
        sendGroupChat(callback, groupId, message, "", 0);
    }

    public ModifyTimeStruct sendGroupChat(long groupId, String message){
        return sendGroupChat(groupId, message, "", 0);
    }

    public void sendRoomChat(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message) {
        sendRoomChat(callback, roomId, message, "", 0);
    }

    public ModifyTimeStruct sendRoomChat(long roomId, String message){
        return sendRoomChat(roomId, message, "", 0);
    }

    public ModifyTimeStruct sendCmd(long uid, String message){
        return sendCmd(uid, message, "", 0);
    }

    public void sendCmd(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message) {
        sendCmd(callback,uid, message, "", 0);
    }

    public void sendGroupCmd(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message) {
        sendGroupCmd(callback, groupId, message, "", 0);
    }

    public ModifyTimeStruct sendGroupCmd(long groupId, String message) {
        return sendGroupCmd(groupId, message, "", 0);
    }

    public void sendRoomCmd(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message){
        sendRoomCmd(callback, roomId, message, "", 0);
    }
    public ModifyTimeStruct sendRoomCmd(long roomId, String message){
        return sendRoomCmd(roomId, message, "", 0);
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

    public void setTranslatedLanguage(IRTMEmptyCallback callback, TranslateLang targetLanguage) {
        setTranslatedLanguage(callback, targetLanguage, 0);
    }

    public RTMAnswer setTranslatedLanguage(String targetLanguage){
        return setTranslatedLanguage(targetLanguage, 0);
    }

    public void translate(IRTMCallback<TranslatedInfo> callback, String text, String trargetLanguage) {
        translate(callback, text, trargetLanguage, "", TranslateType.Chat, ProfanityType.Off,RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public void translate(IRTMCallback<TranslatedInfo> callback, String text, String trargetLanguage, TranslateType transtype,ProfanityType protype) {
        translate(callback, text, trargetLanguage, "", transtype, protype,RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public TranslatedInfo translate(String text, String trargetLanguage){
        return translate(text, trargetLanguage, "", TranslateType.Chat, ProfanityType.Off,RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public TranslatedInfo translate(String text, String trargetLanguage,TranslateType transtype,ProfanityType protype){
        return translate(text, trargetLanguage, "", transtype, protype,RTMConfig.globalTranslateQuestTimeoutSeconds);
    }

    public void textCheck(IRTMCallback<CheckResult> callback, String text) {
        textCheck(callback, text,0);
    }

    public CheckResult textCheck(String text){
        return textCheck(text, 0);
    }

    public void audioCheckURL(IRTMCallback<CheckResult> callback, String url, TranscribeLang lang) {
        audioCheckURL(callback, url, lang,"AMR_WB",16000);
    }

    public void audioCheck(IRTMCallback<CheckResult> callback, byte[] content, TranscribeLang lang) {
        audioCheck(callback, content, lang,"AMR_WB",16000);
    }

    public CheckResult audioCheckURL(String url, TranscribeLang lang){
        return audioCheckURL(url, lang,"AMR_WB",16000);
    }

    public CheckResult audioCheck(byte[] content, TranscribeLang lang){
        return audioCheck(content, lang,"AMR_WB",16000);
    }

    public void audioToTextURL(IRTMCallback<AudioTextStruct> callback, String url, TranscribeLang lang) {
        audioToTextURL(callback, url, lang,"AMR_WB",16000);
    }

    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, TranscribeLang lang) {
        audioToText(callback, content, lang,"AMR_WB",16000);
    }

    public AudioTextStruct audioToTextURL(String url, TranscribeLang lang){
        return audioToTextURL(url, lang,"AMR_WB",16000);
    }

    public AudioTextStruct audioToText(byte[] content, TranscribeLang lang){
        return audioToText(content, lang,"AMR_WB",16000);
    }
    //重载end

    /**图片检测 async(调用此接口需在管理系统启用图片审核系统)
     * @param callback  IRTMCallback<CheckResult>回调(NoNull)
     * @param url       url地址
     */
    public void imageCheckURL(IRTMCallback<CheckResult> callback, String url) {
        checkContentAsync(callback, url, CheckSourceType.URL, CheckType.PIC, "", null,"",0);
    }

    /**图片检测 async
     * @param callback  IRTMCallback<CheckResult>回调(NoNull)
     * @param content   图片内容
     */
    public void imageCheck(IRTMCallback<CheckResult> callback, byte[] content) {
        checkContentAsync(callback, content, CheckSourceType.CONTENT, CheckType.PIC, "", null,"",0);
    }

    /**图片检测 sync
     * @param url   url地址
     */
    public CheckResult imageCheckURL(String url) {
        return checkContentSync(url, CheckSourceType.URL, CheckType.PIC, "", null,"",0);
    }

    /**图片检测 sync
     * @param content   图片内容
     */
    public CheckResult imageCheck(byte[] content) {
        return checkContentSync(content, CheckSourceType.CONTENT, CheckType.PIC, "", null,"",0);
    }

    /**语音检测 async(调用此接口需在管理系统启用语音审核系统)
     * @param callback  IRTMCallback<CheckResult>回调(NoNull)
     * @param url       语音url地址
     * @param lang      语言
     * @param codec     音频格式
     */
    public void audioCheckURL(IRTMCallback<CheckResult> callback, String url, TranscribeLang lang, String codec, int srate) {
        checkContentAsync(callback, url, CheckSourceType.URL, CheckType.AUDIO, "", lang,codec, srate);
    }

    /**语音检测 async
     * @param callback  IRTMCallback<CheckResult>回调(NoNull)
     * @param content   语音内容
     * @param lang      语言
     * @param codec     音频格式
     */
    public void audioCheck(IRTMCallback<CheckResult> callback, byte[] content,TranscribeLang lang, String codec, int srate) {
        checkContentAsync(callback, content, CheckSourceType.CONTENT, CheckType.AUDIO, "", lang, codec, srate);
    }

    /**语音检测 sync
     * @param url    语音url地址
     * @param lang   语言
     * @param codec  音频格式
     */
    public CheckResult audioCheckURL(String url, TranscribeLang lang, String codec,int srate) {
        return checkContentSync(url, CheckSourceType.URL, CheckType.AUDIO, "", lang, codec, srate);
    }

    /**语音检测 sync
     * @param content   语音内容
     * @param lang      语言
     * @param codec     音频格式
     */
    public CheckResult audioCheck(byte[] content,TranscribeLang lang, String codec, int srate) {
        return checkContentSync(content, CheckSourceType.CONTENT, CheckType.AUDIO, "", lang,codec, srate);
    }


    /**视频检测 async(调用此接口需在管理系统启用视频审核系统)
     * @param callback  IRTMCallback<CheckResult>回调(NoNull)
     * @param url       视频url地址
     */
    public void videoCheckURL(IRTMCallback<CheckResult> callback, String url) {
        checkContentAsync(callback, url, CheckSourceType.URL, CheckType.VIDEO, "", null,"",0);
    }

    /**视频检测 async
     * @param callback  IRTMCallback<CheckResult>回调(NoNull)
     * @param content   视频内容
     * @param videoName   视频名称
     */
    public void videoCheck(IRTMCallback<CheckResult> callback, byte[] content, String videoName) {
        checkContentAsync(callback, content, CheckSourceType.CONTENT, CheckType.VIDEO, videoName, null, "",0);
    }

    /**视频检测 sync
     * @param url   视频url地址
     */
    public CheckResult videoCheckURL(String url) {
        return checkContentSync(url, CheckSourceType.URL, CheckType.VIDEO, "", null, "",0);
    }

    /**视频检测 sync
     * @param content   视频内容
     * @param videoName   视频名称
     */
    public CheckResult videoCheck(byte[] content, String videoName) {
        return checkContentSync(content, CheckSourceType.CONTENT, CheckType.VIDEO, videoName, null,"",0);
    }



    /**语音转文字 async(调用此接口需在管理系统启用语音识别系统) codec为空则默认为AMR_WB,srate为0或者空则默认为16000
     * @param callback  IRTMCallback<AudioTextStruct>回调(NoNull)
     * @param url       语音url地址(NoNull)
     * @param lang      语言(NoNull)
     * @param codec     音频格式
     * @param srate     采样率
     */
    public void audioToTextURL(IRTMCallback<AudioTextStruct> callback, String url, TranscribeLang lang, String codec, int srate) {
        audioToTextAsync(callback, url,CheckSourceType.URL, lang, codec, srate);
    }

    /**语音转文字 sync
     * @param url       语音url地址(NoNull)
     * @param lang      语言(NoNull)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     * return           AudioTextStruct结构
     */
    public AudioTextStruct audioToTextURL(String url, TranscribeLang lang, String codec, int srate) {
        return audioToTextSync(url, CheckSourceType.URL, lang, codec, srate);
    }

    /**语音转文字 sync
     * @param content   语音内容(NoNull)
     * @param lang      语言(NoNull)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     */
    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, TranscribeLang lang, String codec, int srate) {
        audioToTextAsync(callback, content, CheckSourceType.CONTENT, lang, codec, srate);
    }

    /**语音转文字 sync
     * @param content   语音内容(NoNull)
     * @param lang      语言(NoNull)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     * return           AudioTextStruct结构
     */
    public AudioTextStruct audioToText(byte[] content, TranscribeLang lang, String codec, int srate) {
        return audioToTextSync(content, CheckSourceType.CONTENT, lang, codec, srate);
    }

    /**
     *发送p2p聊天消息(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param uid       目标用户id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     */
    public void sendChat(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs, int timeout) {
        internalSendChat(callback, uid, MessageType.CHAT, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送p2p聊天消息(sync)
     * @param uid       目标用户id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendChat(long uid, String message, String attrs, int timeout){
        return internalSendChat(uid, MessageType.CHAT, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送群组聊天消息(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param groupId   群组id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     */
    public void sendGroupChat(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs, int timeout) {
        internalSendChat(callback, groupId, MessageType.CHAT, message, attrs, timeout, MessageCategories.GroupMessage);
    }

    /**
     *发送群组聊天消息(sync)
     * @param groupId   群组id(NoNull)
     * @param message   群组聊天消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendGroupChat(long groupId, String message, String attrs, int timeout) {
        return internalSendChat(groupId, MessageType.CHAT, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param roomId    房间id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     */
    public void sendRoomChat(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs, int timeout){
        internalSendChat(callback, roomId, MessageType.CHAT, message, attrs, timeout, MessageCategories.RoomMessage);
    }

    /**
     *发送房间聊天消息(sync)
     * @param roomId    房间id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendRoomChat(long roomId, String message, String attrs, int timeout){
        return internalSendChat(roomId, MessageType.CHAT, message, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *p2p指令消息(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param uid       目标用户id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     */
    public void sendCmd(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs, int timeout) {
        internalSendChat(callback, uid, MessageType.CMD, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送p2p指令消息(sync)
     * @param uid       目标用户id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendCmd(long uid, String message, String attrs, int timeout){
        return internalSendChat(uid, MessageType.CMD, message, attrs, timeout,MessageCategories.P2PMessage);
    }

    /**
     *发送群组指令(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param groupId   群组id(NoNull)
     * @param message   群组指令消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     */
    public void sendGroupCmd(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs, int timeout) {
        internalSendChat(callback, groupId, MessageType.CMD, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     *发送群组指令消息(sync)
     * @param groupId   群组id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendGroupCmd(long groupId, String message, String attrs, int timeout){
        return internalSendChat(groupId, MessageType.CMD, message, attrs, timeout,MessageCategories.GroupMessage);
    }

    /**
     *发送房间指令
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param roomId    房间id(NoNull)
     * @param message   房间指令消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)

     */
    public void sendRoomCmd(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs, int timeout){
        internalSendChat(callback, roomId, MessageType.CMD, message, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *发送房间指令消息(sync)
     * @param roomId   房间id(NoNull)
     * @param message   指令消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendRoomCmd(long roomId, String message, String attrs, int timeout){
        return internalSendChat(roomId, MessageType.CMD, message, attrs, timeout,MessageCategories.RoomMessage);
    }

    /**
     *获取p2p聊天记录(async)
     * @param callback  HistoryMessageResult类型回调(NoNull)
     * @param toUid   目标uid(NoNull)
     * @param desc      是否按时间倒叙排列(NoNull)
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param timeout   超时时间(秒)
     */
    public void getP2PHistoryChat(IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, toUid, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取p2p聊天记录(sync)
     * @param toUid   用户id(NoNull)
     * @param desc      是否按时间倒叙排列(NoNull)
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
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
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param timeout   超时时间(秒)
     */
    public void getGroupHistoryChat(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.GroupMessage);
    }

    /**
     *获取群组聊天记录(sync)
     * @param groupId   群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
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
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param timeout   超时时间(秒)
     */
    public void getRoomHistoryChat(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.RoomMessage);
    }

    /**
     *获取房间聊天记录(sync)
     * @param roomId    房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
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
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param timeout   超时时间(秒)
     */
    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout) {
        getHistoryMessage(callback, -1, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.BroadcastMessage);
    }

    /**
     * 获得广播历史聊天消息(sync)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param timeout   超时时间(秒)
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryChat(boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){
        return getHistoryMessage(-1, desc, count, beginMsec, endMsec, lastId, chatMTypes, timeout, MessageCategories.BroadcastMessage);
    }

    /**
     *获取服务器未读消息(async)
     * @param callback  IRTMCallback<Unread> 回调
     * @param clear     是否清除离线提醒(如果不传 默认为false)
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
     * @param clear     是否清除离线提醒(默认为false)
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
     * @param callback IRTMCallback<Unread>回调(NoNull)
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
    /**
     *设置目标翻译语言 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void setTranslatedLanguage(IRTMEmptyCallback callback, TranslateLang targetLanguage, int timeout) {
        Quest quest = new Quest("setlang");
        quest.param("lang", targetLanguage.getName());
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
     * @param callback      IRTMCallback<TranslatedInfo>回调(NoNull)
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param timeout               超时时间(秒)
     * @param type                  可选值为chat或mail。如未指定，则默认使用'chat'
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off
     */
    public void translate(final IRTMCallback<TranslatedInfo> callback, String text, String destinationLanguage, String sourceLanguage,
                             TranslateType type, ProfanityType profanity,int timeout) {
        Quest quest = new Quest("translate");
        quest.param("text", text);
        quest.param("dst", destinationLanguage);

        if (sourceLanguage.length() > 0)
            quest.param("src", sourceLanguage);

        if (type == TranslateType.Mail)
            quest.param("type", "mail");
        else
            quest.param("type", "chat");

        switch (profanity) {
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
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off(不进行过滤)
     * @return                  TranslatedInfo结构
     */
    public TranslatedInfo translate(String text, String destinationLanguage, String sourceLanguage,
                         TranslateType type, ProfanityType profanity,int timeout){
        Quest quest = new Quest("translate");
        quest.param("text", text);
        quest.param("dst", destinationLanguage);

        if (sourceLanguage.length() > 0)
            quest.param("src", sourceLanguage);

        if (type == TranslateType.Mail)
            quest.param("type", "mail");
        else
            quest.param("type", "chat");

        switch (profanity) {
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
     *文本检测 sync(调用此接口需在管理系统启用文本审核系统）
     * @param text          需要检测的文本(NoNull)
     * @param timeout       超时时间(秒)
     * @return              CheckResult结构
     */
    public CheckResult textCheck(String text, int timeout){
        Quest quest = new Quest("tcheck");
        quest.param("text", text);

        Answer answer = sendQuest(quest, timeout);
        CheckResult checkResult = new CheckResult();
        RTMAnswer result = genRTMAnswer(answer);
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            checkResult.result = answer.wantInt("result");
            if (checkResult.result == 2){
                List<Integer> tags = new ArrayList<>();
                List<String> wlist = new ArrayList<>();
                checkResult.text = answer.getString("text");
                RTMUtils.getIntList(answer,"tags",tags);
                RTMUtils.getStringList(answer,"wlist",wlist);
                checkResult.tags = tags;
                checkResult.wlist = wlist;
            }
        }
        checkResult.errorCode = result.errorCode;
        checkResult.errorMsg = result.errorMsg;
        return checkResult;
    }


    /**
     *文本检测 async(调用此接口需在管理系统启用文本审核系统）
     * @param callback      IRTMCallback<CheckResult>回调(NoNull)
     * @param text          需要检测的文本(NoNull)
     * @param timeout       超时时间(秒)
     */
    public void textCheck(final IRTMCallback<CheckResult> callback, String text, int timeout){
        Quest quest = new Quest("tcheck");
        quest.param("text", text);

        Answer answer = sendQuest(quest, timeout);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                CheckResult checkResult = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    checkResult = new CheckResult();
                    List<Integer> tags = new ArrayList<>();
                    List<String> wlist = new ArrayList<>();
                    checkResult.text = answer.getString("text");
                    RTMUtils.getIntList(answer,"tags",tags);
                    RTMUtils.getStringList(answer,"wlist",wlist);
                    checkResult.tags = tags;
                    checkResult.wlist = wlist;
                }
                callback.onResult(checkResult, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    void audioToTextAsync(final IRTMCallback<AudioTextStruct> callback, Object content, CheckSourceType type,TranscribeLang lang, String codec, int srate)
    {
        Quest quest = new Quest("speech2text");
        quest.param("audio", content);
        if (type == CheckSourceType.URL)
            quest.param("type", 1);
        else if (type == CheckSourceType.CONTENT)
            quest.param("type", 2);
        quest.param("lang", lang.getName());
        quest.param("codec", codec);
        quest.param("srate", srate);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                AudioTextStruct audioTextStruct = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    audioTextStruct = new AudioTextStruct();
                    audioTextStruct.text = answer.getString("text");
                    audioTextStruct.lang = answer.getString("lang");
                }
                callback.onResult(audioTextStruct, genRTMAnswer(answer,errorCode));
            }
        }, RTMConfig.globalFileQuestTimeoutSeconds);
    }

    AudioTextStruct audioToTextSync(Object content, CheckSourceType type,TranscribeLang lang, String codec, int srate)
    {
        Quest quest = new Quest("speech2text");
        quest.param("audio", content);
        if (type == CheckSourceType.URL)
            quest.param("type", 1);
        else if (type == CheckSourceType.CONTENT)
            quest.param("type", 2);
        quest.param("lang", lang.getName());
        quest.param("codec", codec);
        quest.param("srate", srate);

        Answer answer = sendQuest(quest, RTMConfig.globalFileQuestTimeoutSeconds);
        AudioTextStruct audioTextStruct = new AudioTextStruct();
        RTMAnswer result = genRTMAnswer(answer);
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            audioTextStruct.text = answer.getString("text");
            audioTextStruct.lang = answer.getString("lang");
        }
        audioTextStruct.errorCode = result.errorCode;
        audioTextStruct.errorMsg = result.errorMsg;
        return audioTextStruct;
    }


    void checkContentAsync(final IRTMCallback<CheckResult> callback, Object content, CheckSourceType type, CheckType checkType,String videoName, TranscribeLang lang, String codec, int srate)
    {
        String method = "", rucankey = "";
        int sourfeType = 1;
        if (checkType == CheckType.PIC) {
            method = "icheck";
            rucankey = "image";
        }
        else if (checkType == CheckType.AUDIO) {
            method = "acheck";
            rucankey = "audio";
        }
        else if (checkType == CheckType.VIDEO) {
            method = "vcheck";
            rucankey = "video";
        }

        if (type == CheckSourceType.CONTENT)
            sourfeType = 2;

        Quest quest = new Quest(method);
        quest.param("type", sourfeType);
        quest.param(rucankey, content);
        if (checkType == CheckType.VIDEO) {
            quest.param("videoName", videoName);
        }
        else if (checkType == CheckType.AUDIO) {
            quest.param("lang", lang.getName());
            quest.param("codec", codec);
            quest.param("srate", srate);
        }

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                CheckResult checkResult = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    checkResult = new CheckResult();
                    checkResult.result = answer.wantInt("result");
                    if (checkResult.result == 2){
                        List<Integer> tags = new ArrayList<>();
                        RTMUtils.wantIntList(answer,"tags",tags);
                        checkResult.tags = tags;
                    }
                }
                callback.onResult(checkResult, genRTMAnswer(answer,errorCode));
            }
        }, RTMConfig.globalFileQuestTimeoutSeconds);
    }

    CheckResult checkContentSync(Object content, CheckSourceType type,CheckType checkType,String videoName, TranscribeLang lang, String codec, int srate)
    {
        String method = "", rucankey = "";
        int sourfeType = 1;
        if (checkType == CheckType.PIC) {
            method = "icheck";
            rucankey = "image";
        }
        else if (checkType == CheckType.AUDIO) {
            method = "acheck";
            rucankey = "audio";
        }
        else if (checkType == CheckType.VIDEO) {
            method = "vcheck";
            rucankey = "video";
        }

        if (type == CheckSourceType.CONTENT)
            sourfeType = 2;

        Quest quest = new Quest(method);
        quest.param("type", sourfeType);
        quest.param(rucankey, content);
        if (checkType == CheckType.VIDEO && type == CheckSourceType.CONTENT) {
            quest.param("videoName", videoName);
        }
        else if (checkType == CheckType.AUDIO) {
            quest.param("lang", lang.getName());
            quest.param("codec", codec);
            quest.param("srate", srate);
        }

        Answer answer = sendQuest(quest, RTMConfig.globalFileQuestTimeoutSeconds);
        CheckResult checkResult = new CheckResult();
        RTMAnswer result = genRTMAnswer(answer);
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            checkResult.result = answer.wantInt("result");
            if (checkResult.result == 2){
                List<Integer> tags = new ArrayList<>();
                RTMUtils.wantIntList(answer,"tags",tags);
                checkResult.tags = tags;
            }
        }
        checkResult.errorCode = result.errorCode;
        checkResult.errorMsg = result.errorMsg;
        return checkResult;
    }
}
