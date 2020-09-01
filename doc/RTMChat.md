###  同步接口
~~~c++
以下接口统一统参数说明
    /**
     *发送p2p聊天消息(sync)
     * @param uid/groupId/roomId     用户id/群组Id/房间Id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
    
发送p2p聊天消息
    public void sendChat(IRTMCallback<Long> callback, long uid, String message, String attrs, int timeout)
     
发送群组聊天消息
    public ModifyTimeStruct sendGroupChat(long groupId, String message, String attrs, int timeout)

发送房间聊天消息
    public ModifyTimeStruct sendRoomChat(long roomId, String message, String attrs, int timeout){
     
发送p2p指令
    public ModifyTimeStruct sendCmd(long uid, String message, String attrs, int timeout){

发送群组指令
    public ModifyTimeStruct sendGroupCmd(long groupId, String message, String attrs, int timeout){

发送房间指令
    public ModifyTimeStruct sendRoomCmd(long roomId, String message, String attrs, int timeout){
    
发送p2p语音
    public ModifyTimeStruct sendAudio(long uid, File file, String attrs, int timeout){

发送群组语音
    public ModifyTimeStruct sendGroupAudio(long groupId, File file, String attrs, int timeout){
     
发送房间语音
    public ModifyTimeStruct sendRoomAudio(long roomId, File file, String attrs, int timeout){


以下接口统一参数说明
    /**
     *获取聊天记录(sync)
     * @param toUid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * return       HistoryMessageResult结构
     */

获得p2p历史聊天记录
    public HistoryMessageResult getP2PHistoryChat(long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){

获得群组历史聊天记录
    public HistoryMessageResult getGroupHistoryChat(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){

获得房间历史聊天记录
    public HistoryMessageResult getRoomHistoryChat(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){


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

    /*获取服务器未读消息(sync)
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     * return           Unread 结构
     */
    public Unread getUnread( boolean clear, int timeout){

    /*清除离线提醒
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public RTMAnswer clearUnread(int timeout)

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

     /**
     * 设置翻译的目标语言 sync
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer setTranslatedLanguage(String targetLanguage, int timeout){

     /**
     *敏感词过滤 sync(调用此接口需在管理系统启用文本检测系统）
     * @param text          需要过滤的文本(NoNull)
     * @param classify      是否进行文本分类检测
     * @param timeout       超时时间(秒)
     * @return              ProfanityResult
     */
    public ProfanityStruct profanity(String text, boolean classify, int timeout){


     /**
     *语音识别 sync(调用此接口需在管理系统启用语音识别系统)
     * @param file     语音文件(NoNull)
     * @param timeout   超时时间(秒)
     * @return          TranscribeStruct结构
     */
    public TranscribeStruct transcribe(File file, boolean profanityFilter, int timeout){
~~~

### 异步接口
~~~c++
以下接口统一统参数说明
    /*
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @param message   聊天消息/指令消息/语音消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
     
发送p2p聊天消息
    public void sendChat(IRTMCallback<Long> callback, long uid, String message, String attrs, int timeout)
     
发送群组聊天消息
    public void sendGroupChat(IRTMCallback<Long> callback, long groupId, String message, String attrs, int timeout)

发送房间聊天消息
    public void sendRoomChat(IRTMCallback<Long> callback, long roomId, String message, String attrs, int timeout){
     
发送p2p指令
    public void sendCmd(IRTMCallback<Long> callback, long uid, String message, String attrs, int timeout)

发送群组指令
    public void sendGroupCmd(IRTMCallback<Long> callback, long groupId, String message, String attrs, int timeout)

发送房间指令
    public void sendRoomCmd(IRTMCallback<Long> callback, long roomId, String message, String attrs, int timeout){
    
发送p2p语音
    public void sendAudio(IRTMCallback<Long> callback, long uid, File file, String attrs, int timeout)

发送群组语音
    public void sendGroupAudio(IRTMCallback<Long> callback, long groupId, File file, String attrs, int timeout)
     
发送房间语音
    public void sendRoomAudio(IRTMCallback<Long> callback, long roomId, File file, String attrs, int timeout)

以下接口统一参数说明
    /*
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param uid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     */
获得p2p历史聊天消息
    public void getP2PHistoryChat(IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)

获得群组历史聊天消息
    public void getGroupHistoryChat(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)

获得房间历史聊天消息
    public void getRoomHistoryChat(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)

    /**
    * 获得广播历史聊天消息
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)

    /**
     *获取服务器未读消息(async)
     * @param callback  IRTMCallback<Unread> 回调
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     */
    public void getUnread(final IRTMCallback<Unread> callback, boolean clear, int timeout)

    /**
     *清除离线提醒 async
     * @param callback EmptyCallback回调(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void clearUnread(IRTMEmptyCallback callback, int timeout)
    
    /**
     * 获取和自己有过会话的用户uid和群组id集合 async
     * @param callback UnreadCallback回调(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getSession(final IRTMCallback<Unread> callback, int timeout)

    /**
     *设置目标翻译语言 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void setTranslatedLanguage(IRTMEmptyCallback callback, String targetLanguage, int timeout)

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
                             translateType type, ProfanityType profanity)

    /**
     *敏感词过滤 async(调用此接口需在管理系统启用文本检测系统)
     * @param callback  ProfanityCallback回调(NoNull)
     * @param text      需要过滤的文本
     * @param classify  是否进行文本分类检测
     * @param timeout   超时时间(秒)
     */
    public void profanity(final IRTMCallback<ProfanityStruct> callback, String text, boolean classify, int timeout)


    /**
     *语音识别 async(调用此接口需在管理系统启用语音识别系统)
     * @param callback  IRTMCallback<TranscribeStruct>回调(NoNull)
     * @param file     语音文件(NoNull)
     * @param profanityFilter     是否开启敏感词过滤(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void transcribe(final IRTMCallback<TranscribeStruct> callback, File file, boolean profanityFilter, int timeout)
    
 
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
~~~