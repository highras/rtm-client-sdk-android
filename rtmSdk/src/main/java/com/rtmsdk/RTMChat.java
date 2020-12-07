package com.rtmsdk;

import androidx.annotation.NonNull;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.DuplicatedMessageFilter.MessageCategories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RTMChat extends RTMRoom {
    private String defaultCodec = "AMR_WB";
    private int sample_rate = 16000;

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
        sendChat(callback, uid, message, "");
    }

    public ModifyTimeStruct sendChat(long uid, String message){
        return sendChat(uid, message, "");
    }

    public void sendGroupChat(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message) {
        sendGroupChat(callback, groupId, message, "");
    }

    public ModifyTimeStruct sendGroupChat(long groupId, String message){
        return sendGroupChat(groupId, message, "");
    }

    public void sendRoomChat(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message) {
        sendRoomChat(callback, roomId, message, "");
    }

    public ModifyTimeStruct sendRoomChat(long roomId, String message){
        return sendRoomChat(roomId, message, "");
    }

    public ModifyTimeStruct sendCmd(long uid, String message){
        return sendCmd(uid, message, "");
    }

    public void sendCmd(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message) {
        sendCmd(callback,uid, message, "");
    }

    public void sendGroupCmd(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message) {
        sendGroupCmd(callback, groupId, message, "");
    }

    public ModifyTimeStruct sendGroupCmd(long groupId, String message) {
        return sendGroupCmd(groupId, message, "");
    }

    public void sendRoomCmd(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message){
        sendRoomCmd(callback, roomId, message, "");
    }
    public ModifyTimeStruct sendRoomCmd(long roomId, String message){
        return sendRoomCmd(roomId, message, "");
    }

    public void getUnread(IRTMCallback<Unread> callback) {
        getUnread(callback, true);
    }

    public Unread getUnread(){
        return getUnread(true);
    }


    public void translate(IRTMCallback<TranslatedInfo> callback, String text, String targetLanguage) {
        translate(callback, text, targetLanguage, "", TranslateType.Chat, ProfanityType.Off);
    }

    public void translate(IRTMCallback<TranslatedInfo> callback, String text, String targetLanguage, TranslateType transtype,ProfanityType protype) {
        translate(callback, text, targetLanguage, "", transtype, protype);
    }

    public TranslatedInfo translate(String text, String targetLanguage){
        return translate(text, targetLanguage, "", TranslateType.Chat, ProfanityType.Off);
    }

    public TranslatedInfo translate(String text, String targetLanguage,TranslateType transtype,ProfanityType protype){
        return translate(text, targetLanguage, "", transtype, protype);
    }

    public void audioCheckURL(IRTMCallback<CheckResult> callback, String url, String lang) {
        audioCheckURL(callback, url, lang,defaultCodec,sample_rate);
    }

    public void audioCheck(IRTMCallback<CheckResult> callback, byte[] content, String lang) {
        audioCheck(callback, content, lang,defaultCodec,sample_rate);
    }

    public CheckResult audioCheckURL(String url, String lang){
        return audioCheckURL(url, lang,defaultCodec,sample_rate);
    }

    public CheckResult audioCheck(byte[] content, String lang){
        return audioCheck(content, lang,defaultCodec,sample_rate);
    }

    public void audioToTextURL(IRTMCallback<AudioTextStruct> callback, String url, String lang) {
        audioToTextURL(callback, url, lang,defaultCodec,sample_rate);
    }

    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, String lang) {
        audioToText(callback, content, lang,defaultCodec,sample_rate);
    }

    public AudioTextStruct audioToTextURL(String url, String lang){
        return audioToTextURL(url, lang,defaultCodec,sample_rate);
    }

    public AudioTextStruct audioToText(byte[] content, String lang){
        return audioToText(content, lang,defaultCodec,sample_rate);
    }

    public UnreadNum getGroupUnread(HashSet<Long>gids) {
        return getGroupUnread(gids, 0, null);
    }


    public UnreadNum getGroupUnread(HashSet<Long>gids, List<Byte> messageTypes) {
        return getGroupUnread(gids,0,messageTypes);
    }

    public UnreadNum getP2PUnread(HashSet<Long>uids) {
        return getP2PUnread(uids,0, null);
    }

    public UnreadNum getP2PUnread(HashSet<Long>uids, List<Byte> messageTypes) {
        return getP2PUnread(uids,0, messageTypes);
    }

    public void getP2PUnread(final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> uids){
        getP2PUnread(callback,uids,0,null);
    }

    public void getP2PUnread(final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> uids, List<Byte> messageTypes){
        getP2PUnread(callback,uids,0,messageTypes);
    }

    public void getGroupUnread(final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> gids) {
        getGroupUnread(callback,gids,0, null);
    }

    public void getGroupUnread(final IRTMCallback<Map<String, Integer>> callback, HashSet<Long>gids,List<Byte> messageTypes) {
        getGroupUnread(callback,gids,0, messageTypes);
    }
        //重载end

    /**图片检测 async(调用此接口需在管理系统启用图片审核系统)
     * @param callback  IRTMCallback<CheckResult>回调
     * @param url       url地址
     */
    public void imageCheckURL(@NonNull IRTMCallback<CheckResult> callback, @NonNull String url) {
        checkContentAsync(callback, url, CheckSourceType.URL, CheckType.PIC, "", null,"",0);
    }

    /**图片检测 async
     * @param callback  IRTMCallback<CheckResult>回调
     * @param content   图片内容
     */
    public void imageCheck(@NonNull IRTMCallback<CheckResult> callback, @NonNull byte[] content) {
        checkContentAsync(callback, content, CheckSourceType.CONTENT, CheckType.PIC, "", null,"",0);
    }

    /**图片检测 sync
     * @param url   url地址
     */
    public CheckResult imageCheckURL(@NonNull String url) {
        return checkContentSync(url, CheckSourceType.URL, CheckType.PIC, "", null,"",0);
    }

    /**图片检测 sync
     * @param content   图片内容
     */
    public CheckResult imageCheck(@NonNull byte[] content) {
        return checkContentSync(content, CheckSourceType.CONTENT, CheckType.PIC, "", null,"",0);
    }

    /**语音检测 async(调用此接口需在管理系统启用语音审核系统)
     * @param callback  IRTMCallback<CheckResult>回调
     * @param url       语音url地址
     * @param lang      语言(详见TranscribeLang.java枚举列表)
     * @param codec     音频格式
     */
    public void audioCheckURL(@NonNull IRTMCallback<CheckResult> callback, @NonNull String url, @NonNull String lang, @NonNull String codec, int srate) {
        checkContentAsync(callback, url, CheckSourceType.URL, CheckType.AUDIO, "", lang,codec, srate);
    }

    /**语音检测 async
     * @param callback  IRTMCallback<CheckResult>回调
     * @param content   语音内容
     * @param lang      语言(详见TranscribeLang.java枚举列表)
     * @param codec     音频格式
     */
    public void audioCheck(@NonNull IRTMCallback<CheckResult> callback, @NonNull byte[] content,@NonNull String lang, @NonNull String codec, int srate) {
        checkContentAsync(callback, content, CheckSourceType.CONTENT, CheckType.AUDIO, "", lang, codec, srate);
    }

    /**语音检测 sync
     * @param url    语音url地址
     * @param lang   语言(详见TranscribeLang.java枚举列表)
     * @param codec  音频格式
     */
    public CheckResult audioCheckURL(@NonNull String url, @NonNull String lang, @NonNull String codec,int srate) {
        return checkContentSync(url, CheckSourceType.URL, CheckType.AUDIO, "", lang, codec, srate);
    }

    /**语音检测 sync
     * @param content   语音内容
     * @param lang      语言(详见TranscribeLang.java枚举列表)
     * @param codec     音频格式
     */
    public CheckResult audioCheck(@NonNull byte[] content,@NonNull String lang, @NonNull String codec, int srate) {
        return checkContentSync(content, CheckSourceType.CONTENT, CheckType.AUDIO, "", lang,codec, srate);
    }


    /**视频检测 async(调用此接口需在管理系统启用视频审核系统)
     * @param callback  IRTMCallback<CheckResult>回调
     * @param url       视频url地址
     */
    public void videoCheckURL(@NonNull IRTMCallback<CheckResult> callback, @NonNull String url) {
        checkContentAsync(callback, url, CheckSourceType.URL, CheckType.VIDEO, "", null,"",0);
    }

    /**视频检测 async
     * @param callback  IRTMCallback<CheckResult>回调
     * @param content   视频内容
     * @param videoName   视频名称
     */
    public void videoCheck(@NonNull IRTMCallback<CheckResult> callback, @NonNull byte[] content, @NonNull String videoName) {
        checkContentAsync(callback, content, CheckSourceType.CONTENT, CheckType.VIDEO, videoName, null, "",0);
    }

    /**视频检测 sync
     * @param url   视频url地址
     */
    public CheckResult videoCheckURL(@NonNull String url) {
        return checkContentSync(url, CheckSourceType.URL, CheckType.VIDEO, "", null, "",0);
    }

    /**视频检测 sync
     * @param content   视频内容
     * @param videoName   视频名称
     */
    public CheckResult videoCheck(@NonNull byte[] content, @NonNull String videoName) {
        return checkContentSync(content, CheckSourceType.CONTENT, CheckType.VIDEO, videoName, null,"",0);
    }



    /**语音转文字 async(调用此接口需在管理系统启用语音识别系统) codec为空则默认为AMR_WB,srate为0或者空则默认为16000
     * @param callback  IRTMCallback<AudioTextStruct>回调
     * @param url       语音url地址
     * @param lang      语言(详见TranscribeLang.java枚举值)
     * @param codec     音频格式
     * @param srate     采样率
     */
    public void audioToTextURL(@NonNull IRTMCallback<AudioTextStruct> callback, @NonNull String url, @NonNull String lang, @NonNull String codec, int srate) {
        audioToTextAsync(callback, url,CheckSourceType.URL, lang, codec, srate);
    }

    /**语音转文字 sync
     * @param url       语音url地址
     * @param lang      语言(详见TranscribeLang.java枚举值)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     * return           AudioTextStruct结构
     */
    public AudioTextStruct audioToTextURL(@NonNull String url, @NonNull String lang, @NonNull String codec, int srate) {
        return audioToTextSync(url, CheckSourceType.URL, lang, codec, srate);
    }

    /**语音转文字 async
     * @param callback  IRTMCallback<AudioTextStruct>
     * @param content   语音内容
     * @param lang      语言(详见TranscribeLang.java枚举值)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     */
    public void audioToText(@NonNull IRTMCallback<AudioTextStruct> callback, @NonNull byte[] content, @NonNull String lang, @NonNull String codec, int srate) {
        audioToTextAsync(callback, content, CheckSourceType.CONTENT, lang, codec, srate);
    }

    /**语音转文字 sync
     * @param content   语音内容
     * @param lang      语言(详见TranscribeLang.java枚举值)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     * return           AudioTextStruct结构
     */
    public AudioTextStruct audioToText(@NonNull byte[] content, @NonNull String lang, @NonNull String codec, int srate) {
        return audioToTextSync(content, CheckSourceType.CONTENT, lang, codec, srate);
    }

    /**
     *发送p2p聊天消息(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param uid       目标用户id
     * @param message   聊天消息
     * @param attrs     附加信息
     */
    public void sendChat(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long uid, @NonNull String message, String attrs) {
        internalSendChat(callback, uid, MessageType.CHAT, message, attrs,MessageCategories.P2PMessage);
    }

    /**
     *发送p2p聊天消息(sync)
     * @param uid       目标用户id
     * @param message   聊天消息
     * @param attrs     客户端自定义信息
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendChat(long uid, @NonNull String message, String attrs){
        return internalSendChat(uid, MessageType.CHAT, message, attrs,MessageCategories.P2PMessage);
    }

    /**
     *发送群组聊天消息(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param groupId   群组id
     * @param message   聊天消息
     * @param attrs     附加信息
     */
    public void sendGroupChat(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long groupId, @NonNull String message, String attrs) {
        internalSendChat(callback, groupId, MessageType.CHAT, message, attrs, MessageCategories.GroupMessage);
    }

    /**
     *发送群组聊天消息(sync)
     * @param groupId   群组id
     * @param message   群组聊天消息
     * @param attrs     附加信息
     
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendGroupChat(long groupId, @NonNull String message, String attrs) {
        return internalSendChat(groupId, MessageType.CHAT, message, attrs,MessageCategories.GroupMessage);
    }

    /**
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param roomId    房间id
     * @param message   聊天消息
     * @param attrs     附加信息
     
     */
    public void sendRoomChat(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long roomId, @NonNull String message, String attrs){
        internalSendChat(callback, roomId, MessageType.CHAT, message, attrs, MessageCategories.RoomMessage);
    }

    /**
     *发送房间聊天消息(sync)
     * @param roomId    房间id
     * @param message   聊天消息
     * @param attrs     附加信息
     
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendRoomChat(long roomId, @NonNull String message, String attrs){
        return internalSendChat(roomId, MessageType.CHAT, message, attrs,MessageCategories.RoomMessage);
    }

    /**
     *p2p指令消息(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param uid       目标用户id
     * @param message   指令消息
     * @param attrs     附加信息
     
     */
    public void sendCmd(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long uid, @NonNull String message, String attrs) {
        internalSendChat(callback, uid, MessageType.CMD, message, attrs,MessageCategories.P2PMessage);
    }

    /**
     *发送p2p指令消息(sync)
     * @param uid       目标用户id
     * @param message   指令消息
     * @param attrs     附加信息
     
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendCmd(long uid, @NonNull String message, String attrs){
        return internalSendChat(uid, MessageType.CMD, message, attrs,MessageCategories.P2PMessage);
    }

    /**
     *发送群组指令(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param groupId   群组id
     * @param message   群组指令消息
     * @param attrs     附加信息
     
     */
    public void sendGroupCmd(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long groupId, @NonNull String message, String attrs) {
        internalSendChat(callback, groupId, MessageType.CMD, message, attrs,MessageCategories.GroupMessage);
    }

    /**
     *发送群组指令消息(sync)
     * @param groupId   群组id
     * @param message   指令消息
     * @param attrs     附加信息
     
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendGroupCmd(long groupId, @NonNull String message, String attrs){
        return internalSendChat(groupId, MessageType.CMD, message, attrs,MessageCategories.GroupMessage);
    }

    /**
     *发送房间指令
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param roomId    房间id
     * @param message   房间指令消息
     * @param attrs     附加信息
     

     */
    public void sendRoomCmd(@NonNull IRTMDoubleValueCallback<Long,Long> callback, long roomId, @NonNull String message, String attrs){
        internalSendChat(callback, roomId, MessageType.CMD, message, attrs,MessageCategories.RoomMessage);
    }

    /**
     *发送房间指令消息(sync)
     * @param roomId   房间id
     * @param message   指令消息
     * @param attrs     附加信息
     
     * @return          ModifyTimeStruct结构
     */
    public ModifyTimeStruct sendRoomCmd(long roomId, @NonNull String message, String attrs){
        return internalSendChat(roomId, MessageType.CMD, message, attrs,MessageCategories.RoomMessage);
    }

    /**
     *获取p2p聊天记录(async)
     * @param callback  HistoryMessageResult类型回调
     * @param toUid   目标uid
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     */
    public void getP2PHistoryChat(@NonNull IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getHistoryMessage(callback, toUid, desc, count, beginMsec, endMsec, lastId, chatMTypes, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取p2p聊天记录(sync)
     * @param toUid   用户id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     * return       HistoryMessageResult结构
     */
    public HistoryMessageResult getP2PHistoryChat(long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId){
        return getHistoryMessage(toUid, desc, count, beginMsec, endMsec, lastId, chatMTypes, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    /**
     *获取群组聊天记录(async)
     * @param callback  IRTMCallback<HistoryMessageResult>接口回调(HistoryMessageResult为历史消息结构)
     * @param groupId   群组id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     */
    public void getGroupHistoryChat(@NonNull IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getHistoryMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, chatMTypes, MessageCategories.GroupMessage);
    }

    /**
     *获取群组聊天记录(sync)
     * @param groupId   群组id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     */
    public HistoryMessageResult getGroupHistoryChat(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId){
        return getHistoryMessage(groupId, desc, count, beginMsec, endMsec, lastId, chatMTypes, MessageCategories.GroupMessage);
    }

    /**
     *
     * @param callback  HistoryMessageCallback回调
     * @param roomId    房间id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     */
    public void getRoomHistoryChat(@NonNull IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getHistoryMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, chatMTypes, MessageCategories.RoomMessage);
    }

    /**
     *获取房间聊天记录(sync)
     * @param roomId    房间id
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     *return            HistoryMessageResult
     */
    public HistoryMessageResult getRoomHistoryChat(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId){
        return getHistoryMessage(roomId, desc, count, beginMsec, endMsec, lastId, chatMTypes, MessageCategories.RoomMessage);
    }

    /**
     * 获得广播历史聊天消息(async)
     * @param callback  HistoryMessageCallback回调
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     */
    public void getBroadcastHistoryChat(@NonNull IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        getHistoryMessage(callback, -1, desc, count, beginMsec, endMsec, lastId, chatMTypes, MessageCategories.BroadcastMessage);
    }

    /**
     * 获得广播历史聊天消息(sync)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryChat(boolean desc, int count, long beginMsec, long endMsec, long lastId){
        return getHistoryMessage(-1, desc, count, beginMsec, endMsec, lastId, chatMTypes, MessageCategories.BroadcastMessage);
    }

    /**
     *获取p2p未读条目数(async)
     * @param callback   IRTMCallback<Map<String, Integer>> 用户id，未读消息条目数
     * @param uids      用户id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间,重连建议传入收到消息或者拉取历史的最后一次时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天,文件相关消息类型，不包含自定义的type)
     */
    public void getP2PUnread(@NonNull final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> uids,long lastMessageTime, List<Byte> messageTypes) {
        Quest quest = new Quest("getp2punread");
        quest.param("uids",uids);
        if (lastMessageTime != 0)
            quest.param("mtime",lastMessageTime);
        if (messageTypes != null)
            quest.param("mtypes",messageTypes);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashMap<String, Integer> p2pUnread = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    p2pUnread = new HashMap<>();
                    Map<String, Integer> ob = (Map<String, Integer>)answer.want("p2p");
                    for (String uid:ob.keySet())
                        p2pUnread.put(uid,ob.get(uid));
                }
                callback.onResult(p2pUnread, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     *获取群组未读条目数(async)
     * @param callback   IRTMCallback<Map<String, Integer>> 群组id，未读消息条目数
     * @param gids      群组id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间,重连建议传入收到消息或者拉取历史的最后一次时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天,文件相关消息类型，不包含自定义的type)
     */
    public void getGroupUnread(@NonNull final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> gids,long lastMessageTime, List<Byte> messageTypes) {
        Quest quest = new Quest("getgroupunread");
        quest.param("gids", gids);
        if (lastMessageTime != 0)
            quest.param("mtime",lastMessageTime);
        if (messageTypes != null)
            quest.param("mtypes",messageTypes);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashMap<String, Integer> p2pUnread = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    p2pUnread = new HashMap<>();
                    Map<String, Integer> ob = (Map<String, Integer>)answer.want("group");
                    for (String uid:ob.keySet())
                        p2pUnread.put(uid,ob.get(uid));
                }
                callback.onResult(p2pUnread, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     *获取服务器未读消息(async)
     * @param callback  IRTMCallback<Unread> 回调
     * @param clear     是否清除离线提醒(如果不传 默认为true)
     */
    public void getUnread(@NonNull final IRTMCallback<Unread> callback, boolean clear) {
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
        });
    }

    /*获取服务器未读消息(sync)
     * @param clear     是否清除离线提醒(默认为true)
     * return           Unread 结构
     */
    public Unread getUnread( boolean clear){
        Quest quest = new Quest("getunread");
        quest.param("clear", clear);

        Answer answer = sendQuest(quest);
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
     *获取群组未读条目数(sync)
     * @param gids      群组id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间,重连建议传入收到消息或者拉取历史的最后一次时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天和文件相关消息类型，不包含自定义的type)
     *  return UnreadNum结构
     */
    public UnreadNum getGroupUnread(@NonNull HashSet<Long> gids, long lastMessageTime, List<Byte> messageTypes) {
        Quest quest = new Quest("getgroupunread");
        quest.param("gids",gids);
        if (lastMessageTime != 0)
            quest.param("mtime",lastMessageTime);
        if (messageTypes != null)
            quest.param("mtypes",messageTypes);

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        UnreadNum res = new UnreadNum();
        HashMap<String, Integer> groupUnread = new HashMap<>();

        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            Map<String, Integer> ob = (Map<String, Integer>)answer.want("group");
            for (String uid:ob.keySet())
                groupUnread.put(uid,ob.get(uid));
        }
        res.unreadInfo = groupUnread;
        res.errorCode = result.errorCode;
        res.errorMsg = result.errorMsg;
        return res;
    }

    /**
     *获取p2p未读条目数(async)
     * @param uids      用户id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间,重连建议传入收到消息或者拉取历史的最后一次时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天和文件相关消息类型，不包含自定义的type)
     *return        UnreadNum结构
     */
    public UnreadNum getP2PUnread(@NonNull HashSet<Long> uids, long lastMessageTime, List<Byte> messageTypes) {
        Quest quest = new Quest("getp2punread");
        quest.param("uids",uids);
        if (lastMessageTime != 0)
            quest.param("mtime",lastMessageTime);
        if (messageTypes != null)
            quest.param("mtypes",messageTypes);

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        UnreadNum res = new UnreadNum();
        HashMap<String, Integer> p2pUnread = new HashMap<>();

        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            Map<String, Integer> ob = (Map<String, Integer>)answer.want("p2p");
            for (String uid:ob.keySet())
                p2pUnread.put(uid,ob.get(uid));
        }
        res.unreadInfo = p2pUnread;
        res.errorCode = result.errorCode;
        res.errorMsg = result.errorMsg;
        return res;
    }

    /**
     *清除离线提醒 async
     * @param callback EmptyCallback回调
     */
    public void clearUnread(@NonNull IRTMEmptyCallback callback) {
        Quest quest = new Quest("cleanunread");
        sendQuestEmptyCallback(callback, quest);
    }

    /*清除离线提醒 sync*/
    public RTMAnswer clearUnread() {
        Quest quest = new Quest("cleanunread");
        return sendQuestEmptyResult(quest);
    }

    /**
     * 获取和自己有过会话的用户uid和群组id集合 async
     * @param callback IRTMCallback<Unread>回调
     */
    public void getSession(@NonNull final IRTMCallback<Unread> callback) {
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
        });
    }

    /**
     * 获取和自己有过会话的用户uid和群组id集合 sync
     * return           Unread 结构
     */
    public Unread getSession(){
        Quest quest = new Quest("getsession");

        Answer answer = sendQuest(quest);
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
     * @param callback  IRTMEmptyCallback回调
     * @param targetLanguage    目标语言(详见TranslateLang.java语言列表)
     */
    public void setTranslatedLanguage(@NonNull IRTMEmptyCallback callback, String targetLanguage) {
        String slang ="";
        if (targetLanguage!=null)
            slang = targetLanguage;
        Quest quest = new Quest("setlang");
        quest.param("lang", slang);
        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 设置翻译的目标语言 sync
     * @param targetLanguage    目标语言(详见TranslateLang.java语言列表)
     */
    public RTMAnswer setTranslatedLanguage(String targetLanguage){
        String slang ="";
        if (targetLanguage!=null)
            slang = targetLanguage;
        Quest quest = new Quest("setlang");
        quest.param("lang", slang);
        return sendQuestEmptyResult(quest);
    }

    /**
     *文本翻译 async(调用此接口需在管理系统启用翻译系统）
     * @param callback      IRTMCallback<TranslatedInfo>回调
     * @param text          需要翻译的内容
     * @param destinationLanguage   目标语言
     * @param sourceLanguage        源文本语言
     * @param type                  可选值为chat或mail。如未指定，则默认使用'chat'
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off
     */
    public void translate(@NonNull final IRTMCallback<TranslatedInfo> callback, String text, String destinationLanguage, String sourceLanguage,
                          TranslateType type, ProfanityType profanity) {
        Quest quest = new Quest("translate");
        quest.param("text", text);
        quest.param("dst", destinationLanguage);

        if (sourceLanguage.length() > 0)
            quest.param("src", sourceLanguage);

        if (type == TranslateType.Mail)
            quest.param("type", "mail");
        else
            quest.param("type", "chat");

        if (profanity != null) {
            switch (profanity) {
                case Censor:
                    quest.param("profanity", "censor");
                    break;
                case Off:
                    quest.param("profanity", "off");
                    break;
            }
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
        },RTMConfig.globalFileQuestTimeoutSeconds);
    }

    /**
     *文本翻译 sync(调用此接口需在管理系统启用翻译系统）
     * @param text          需要翻译的内容
     * @param destinationLanguage   目标语言
     * @param sourceLanguage        源文本语言
     * @param type                  可选值为chat或mail。如未指定，则默认使用'chat'
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off(不进行过滤)
     * @return                  TranslatedInfo结构
     */
    public TranslatedInfo translate(@NonNull String text, String destinationLanguage, @NonNull String sourceLanguage,
                                    @NonNull TranslateType type, @NonNull ProfanityType profanity){
        Quest quest = new Quest("translate");
        quest.param("text", text);
        quest.param("dst", destinationLanguage);

        if (sourceLanguage.length() > 0)
            quest.param("src", sourceLanguage);

        if (type == TranslateType.Mail)
            quest.param("type", "mail");
        else
            quest.param("type", "chat");

        if (profanity != null){
            switch (profanity) {
                case Censor:
                    quest.param("profanity", "censor");
                    break;
                case Off:
                    quest.param("profanity", "off");
                    break;
            }
        }

        Answer answer = sendQuest(quest);
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
     * @param text          需要检测的文本
     * @return              CheckResult结构
     */
    public CheckResult textCheck(@NonNull String text){
        Quest quest = new Quest("tcheck");
        quest.param("text", text);

        Answer answer = sendQuest(quest,RTMConfig.globalFileQuestTimeoutSeconds);
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
     * @param callback      IRTMCallback<CheckResult>回调
     * @param text          需要检测的文本
     */
    public void textCheck(@NonNull final IRTMCallback<CheckResult> callback, @NonNull String text){
        Quest quest = new Quest("tcheck");
        quest.param("text", text);

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
        });
    }

    private void audioToTextAsync(final IRTMCallback<AudioTextStruct> callback, Object content, CheckSourceType type,String lang, String codec, int srate)
    {
        Quest quest = new Quest("speech2text");
        quest.param("audio", content);
        if (type == CheckSourceType.URL)
            quest.param("type", 1);
        else if (type == CheckSourceType.CONTENT)
            quest.param("type", 2);
        quest.param("lang", lang);
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

    private AudioTextStruct audioToTextSync(Object content, CheckSourceType type, String lang, String codec, int srate)
    {
        Quest quest = new Quest("speech2text");
        quest.param("audio", content);
        if (type == CheckSourceType.URL)
            quest.param("type", 1);
        else if (type == CheckSourceType.CONTENT)
            quest.param("type", 2);
        quest.param("lang", lang);
        quest.param("codec", codec);
        quest.param("srate", srate);

        Answer answer = sendQuest(quest);
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


    private void checkContentAsync(final IRTMCallback<CheckResult> callback, Object content, CheckSourceType type, CheckType checkType,String videoName, String lang, String codec, int srate)
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
            quest.param("lang", lang);
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

    private CheckResult checkContentSync(Object content, CheckSourceType type,CheckType checkType,String videoName, String lang, String codec, int srate)
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
            quest.param("lang", lang);
            quest.param("codec", codec);
            quest.param("srate", srate);
        }

        Answer answer = sendQuest(quest);
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
