###  同步接口
~~~c++
以下接口统一统参数说明
    /**
     * @param uid/groupId/roomId     用户id/群组Id/房间Id(NoNull)
     * @param message   聊天消息(NoNull)
     * @param attrs     客户端自定义信息
     * @return          ModifyTimeStruct结构
     */
    
发送p2p聊天消息
    public ModifyTimeStruct sendChat(long userId, String message, String attrs)     
     
发送群组聊天消息
    public ModifyTimeStruct sendGroupChat(long groupId, String message, String attrs)

发送房间聊天消息
    public ModifyTimeStruct sendRoomChat(long roomId, String message, String attrs){
     
发送p2p指令
    public ModifyTimeStruct sendCmd(long uid, String message, String attrs){

发送群组指令
    public ModifyTimeStruct sendGroupCmd(long groupId, String message, String attrs){

发送房间指令
    public ModifyTimeStruct sendRoomCmd(long roomId, String message, String attrs){


以下接口统一参数说明
    /**
     *获取历史聊天记录(sync)
     * @param toUid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息索引id(第一次默认填0)
     * return       HistoryMessageResult结构
     */

获得p2p历史聊天记录
    public HistoryMessageResult getP2PHistoryChat(long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId){

获得群组历史聊天记录
    public HistoryMessageResult getGroupHistoryChat(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId){

获得房间历史聊天记录
    public HistoryMessageResult getRoomHistoryChat(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId){


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

    /*获取服务器未读消息(sync)
     * @param clear     是否清除离线提醒(默认true)
     * return           Unread 结构
     */
    public Unread getUnread( boolean clear){

    /*清除离线提醒
     * @return  RTMAnswer
     */
    public RTMAnswer clearUnread()

    /**
     *文本翻译 sync(调用此接口需在管理系统启用翻译系统）
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言(语言详见https://wiki.ifunplus.cn/display/livedata/Speech+Recognition+API+V1)
     * @param type                  可选值为chat或mail。如未指定，则默认使用'chat'
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off
     * @return                  TranslatedInfo结构
     */
    public TranslatedInfo translate(String text, String destinationLanguage, String sourceLanguage, 
                         translateType type, ProfanityType profanity,){

     /**
     * 设置翻译的目标语言 sync
     * @param targetLanguage    目标语言(详见TranslateLang.java语言列表)
     */
    public RTMAnswer setTranslatedLanguage(String targetLanguage){

    /**
     *文本检测 sync(调用此接口需在管理系统启用文本审核系统）
     * @param text          需要检测的文本(NoNull)
     * @return              CheckResult结构
     */
    public CheckResult textCheck(String text)
    
    
     /**语音转文字 sync
     * @param content   语音内容
     * @param lang      语言(详见TranscribeLang.java枚举值)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     * return           AudioTextStruct结构
     */
    public AudioTextStruct audioToText(@NonNull byte[] content, @NonNull String lang, @NonNull String codec, int srate) 
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
     */
发送p2p聊天消息
    public void sendChat(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs)
     
发送群组聊天消息
    public void sendGroupChat(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs)

发送房间聊天消息
    public void sendRoomChat(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs){
     
发送p2p指令
    public void sendCmd(IRTMDoubleValueCallback<Long,Long> callback, long uid, String message, String attrs)

发送群组指令
    public void sendGroupCmd(IRTMDoubleValueCallback<Long,Long> callback, long groupId, String message, String attrs)

发送房间指令
    public void sendRoomCmd(IRTMDoubleValueCallback<Long,Long> callback, long roomId, String message, String attrs){

以下接口统一参数说明
    /*
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param uid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息的索引id
     */
获得p2p历史聊天消息
    public void getP2PHistoryChat(IRTMCallback<HistoryMessageResult> callback,  long toUid, boolean desc, int count, long beginMsec, long endMsec, long lastId)

获得群组历史聊天消息
    public void getGroupHistoryChat(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId)

获得房间历史聊天消息
    public void getRoomHistoryChat(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId)

    /**
    * 获得广播历史聊天消息
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息的索引id
     */
    public void getBroadcastHistoryChat(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId)


    /**
     *获取p2p未读条目数(sync)
     * @param uids      用户id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天相关消息类型，不包含自定义的type)
     *return        UnreadNum结构
     */
    public UnreadNum getP2PUnread(HashSet<Long> uids, long lastMessageTime, HashSet<Integer> messageTypes)
    
    
      /**
     *获取群组未读条目数(sync)
     * @param gids      群组id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天相关消息类型，不包含自定义的type)
     *  return UnreadNum结构
     */
    public UnreadNum getGroupUnread(HashSet<Long> gids, long lastMessageTime, HashSet<Integer> messageTypes)
    
    
    /**
     *获取p2p未读条目数(async)
     * @param callback   IRTMCallback<Map<String, Integer>> 用户id，未读消息条目数
     * @param uids      用户id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天相关消息类型，不包含自定义的type)
     */
    public void getP2PUnread(@NonNull final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> uids,long lastMessageTime, HashSet<Integer> messageTypes) 
    
    
     /**
     *获取群组未读条目数(async)
     * @param callback   IRTMCallback<Map<String, Integer>> 群组id，未读消息条目数
     * @param gids      群组id集合(建议通过getSession接口获取)
     * @param lastMessageTime 最后一条消息的时间戳(毫秒)(如果不传默认用户最后一次下线时间)
     * @param messageTypes  消息类型集合(如果不传默认所有聊天相关消息类型，不包含自定义的type)
     */
    public void getGroupUnread(@NonNull final IRTMCallback<Map<String, Integer>> callback, HashSet<Long> gids,long lastMessageTime, HashSet<Integer> messageTypes) 
    
    
    /**
     *获取服务器未读消息(async)
     * @param callback  IRTMCallback<Unread> 回调
     * @param clear     是否清除离线提醒(默认true)
     */
    public void getUnread(final IRTMCallback<Unread> callback, boolean clear)

    /**
     *清除离线提醒 async
     * @param callback EmptyCallback回调(NoNull)
     */
    public void clearUnread(IRTMEmptyCallback callback)
    
    /**
     * 获取和自己有过会话的用户uid和群组id集合 async
     * @param callback UnreadCallback回调(NoNull)
     */
    public void getSession(final IRTMCallback<Unread> callback)

    /**
     *设置目标翻译语言 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param targetLanguage    目标语言(详见TranslateLang.java语言列表)
     */
    public void setTranslatedLanguage(IRTMEmptyCallback callback, String targetLanguage)

    /**
     *文本翻译 async(调用此接口需在管理系统启用翻译系统）
     * @param callback      TranslateCallback回调(NoNull)
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param type                  可选值为chat或mail。如未指定，则默认使用'chat'
     * @param profanity             对翻译结果进行敏感语过滤。设置为以下2项之一: off, censor，默认：off
     */
    public void translate(final IRTMCallback<TranslatedInfo> callback, String text, String destinationLanguage, String sourceLanguage,
                             translateType type, ProfanityType profanity)

   /**
     *文本检测 async(调用此接口需在管理系统启用文本审核系统）
     * @param callback      IRTMCallback<CheckResult>回调(NoNull)
     * @param text          需要检测的文本(NoNull)
     */
    public void textCheck(final IRTMCallback<CheckResult> callback, String text)


    /**语音转文字 async
     * @param content   语音内容(NoNull)
     * @param lang      语言(详见TranscribeLang.java枚举值)
     * @param codec     音频格式("AMR_WB")
     * @param srate     采样率(16000)
     */
    public void audioToText(IRTMCallback<AudioTextStruct> callback, byte[] content, String lang, String codec, int srate) 
~~~