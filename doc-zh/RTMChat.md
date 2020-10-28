###  同步接口
~~~c++
以下接口统一统参数说明
    /**
     * @param uid/groupId/roomId     用户id/群组Id/房间Id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return          ModifyTimeStruct结构
     */
    
发送p2p聊天消息
    public ModifyTimeStruct sendChat(long uid, String message, String attrs, int timeout)     
     
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


以下接口统一参数说明
    /**
     *获取历史聊天记录(sync)
     * @param toUid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
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
     * @param count     显示条目数 
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * @param timeout   超时时间(秒)
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryChat(boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout){

    /*获取服务器未读消息(sync)
     * @param clear     是否清除离线提醒(默认false)
     * @param timeout   超时时间(秒)
     * return           Unread 结构
     */
    public Unread getUnread( boolean clear, int timeout){

    /*清除离线提醒
     * @param timeout   超时时间(秒)
     * @return  RTMAnswer
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
    public TranslatedInfo translate(String text, String destinationLanguage, String sourceLanguage, 
                         translateType type, ProfanityType profanity,int timeout){

     /**
     * 设置翻译的目标语言 sync
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer setTranslatedLanguage(String targetLanguage, int timeout){

    /**
     *文本检测 sync(调用此接口需在管理系统启用文本审核系统）
     * @param text          需要检测的文本(NoNull)
     * @param timeout       超时时间(秒)
     * @return              CheckResult结构
     */
    public CheckResult textCheck(String text, int timeout)
~~~


### 异步接口
~~~c++
以下接口统一统参数说明
     /**
     *发送p2p聊天消息(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(服务器返回时间,消息id)
     * @param uid       uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     附加信息
     * @param timeout   超时时间(秒)
     */
发送p2p聊天消息
    public void sendChat(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs, int timeout)
     
发送群组聊天消息
    public void sendGroupChat(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs, int timeout)

发送房间聊天消息
    public void sendRoomChat(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs, int timeout){
     
发送p2p指令
    public void sendCmd(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs, int timeout)

发送群组指令
    public void sendGroupCmd(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs, int timeout)

发送房间指令
    public void sendRoomCmd(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs, int timeout){

以下接口统一参数说明
    /*
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param uid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息的索引id
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
     * @param count     显示条目数 
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息的索引id
     * @param timeout   超时时间(秒)
     */
    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout)

    /**
     *获取服务器未读消息(async)
     * @param callback  IRTMCallback<Unread> 回调
     * @param clear     是否清除离线提醒(默认false)
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
     *文本检测 async(调用此接口需在管理系统启用文本审核系统）
     * @param callback      IRTMCallback<CheckResult>回调(NoNull)
     * @param text          需要检测的文本(NoNull)
     * @param timeout       超时时间(秒)
     */
    public void textCheck(final IRTMCallback<CheckResult> callback, String text, int timeout)


    /**语音转文字 sync
     * @param content   语音内容(NoNull)
     * @param lang      语言(NoNull)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     */
    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, TranscribeLang lang, String codec, int srate) 
~~~