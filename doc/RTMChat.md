###  同步接口
~~~c++
以下接口统一统参数说明
    /*
     * @param mtime  LongMtime对象(NoNull)
     * @param uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @param message   聊天消息/指令消息/语音消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    
发送p2p聊天消息   
public int sendChat(LongMtime mtime, long uid, String message, String attrs, int timeout);

发送群组聊天消息
public int sendGroupChat(LongMtime mtime, long groupId, String message, String attrs, int timeout);

发送房间聊天消息
public int sendRoomChat(LongMtime mtime, long roomId, String message, String attrs, int timeout);

发送p2p指令
public int sendCmd(LongMtime mtime, long uid, String message, String attrs, int timeout);

发送群组指令
public int sendGroupCmd(LongMtime mtime, long groupId, String message, String attrs, int timeout);

发送房间指令
public int sendRoomCmd(LongMtime mtime, long roomId, String message, String attrs, int timeout);

发送p2p语音
public int sendAudio(LongMtime mtime, long uid, byte[] message, String attrs, int timeout);

发送群组语音
public int sendGroupAudio(LongMtime mtime, long groupId, byte[] message, String attrs, int timeout);

发送房间语音
public int sendRoomAudio(LongMtime mtime, long roomId, byte[] message, String attrs, int timeout);


以下接口统一参数说明
    /*
     * @param result  HistoryMessageResult对象(NoNull)
     * @param peerUid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
获得p2p历史聊天记录
public int getP2PChat(HistoryMessageResult result,  long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

获得群组历史聊天记录
public int getGroupChat(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

获得房间历史聊天记录
public int getRoomChat(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

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
public int getBroadcastChat(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

    /*获取服务器未读消息 检测离线聊天数目
     * @param p2pList  未读消息的uid集合(NoNull)
     * @param groupList  未读消息的groupid集合(NoNull)
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int getUnread(List<Long> p2pList, List<Long> groupList, boolean clear, int timeout);

    /*清除离线提醒
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int clearUnread(int timeout);

    /*获取单条聊天消息
     * @param retrievedMessage RetrievedMessage对象(**NoNull**)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int getChat(RetrievedMessage retrievedMessage, long xid, long mid, int type);

    /**
     * 删除聊天消息
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int deleteChat(long xid, long mid, int type, int timeout);

    /**
     *文本翻译
     * @param translatedMessage     TranslatedMessage对象(NoNull)
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param timeout               超时时间(秒)
     * @param type                  翻译类型("mail","chat")
     * @param profanity             敏感语过滤
     * @param postProfanity         是否把翻译后的文本过滤
     * @return      errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int translate(TranslatedMessage translatedMessage, String text, String destinationLanguage, String sourceLanguage, int timeout,
                         translateType type, ProfanityType profanity, boolean postProfanity);

    /**
     * 设置翻译的目标语言
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     * @return      errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int setTranslatedLanguage(String targetLanguage, int timeout);

    /**
     *文本过滤
     * @param text      需要过滤的文本(NoNull)
     * @param resultText    过滤后的文本(NoNull)
     * @param classify  是否进行文本分类检测
     * @parm  classification  文本分类结果                   
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int profanity(StringBuilder resultText, List<String> classification, String text, boolean classify, int timeout);


    /**
     *语音识别
     * @param audio     语音消息(<font color=red>NoNull</font>)
     * @param resultText    识别后的文本(NoNull)
     * @param resultText    识别后的语言(NoNull)
     * @param timeout   超时时间(秒)
     * @return      errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
public int transcribe(byte[] audio, StringBuilder resultText, StringBuilder resultLanguage, int timeout);
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
public boolean sendChat(LongFunctionCallback callback, long uid, String message, String attrs, int timeout);
     
发送群组聊天消息
public boolean sendGroupChat(LongFunctionCallback callback, long groupId, String message, String attrs, int timeout);

发送房间聊天消息
public boolean sendRoomChat(LongFunctionCallback callback, long roomId, String message, String attrs, int timeout);
     
发送p2p指令
public boolean sendCmd(LongFunctionCallback callback, long uid, String message, String attrs, int timeout);

发送群组指令
public boolean sendGroupCmd(LongFunctionCallback callback, long groupId, String message, String attrs, int timeout);

发送房间指令
public boolean sendRoomCmd(LongFunctionCallback callback, long roomId, String message, String attrs, int timeout);
    
发送p2p语音
public boolean sendAudio(LongFunctionCallback callback, long uid, byte[] message, String attrs, int timeout)

发送群组语音
public boolean sendGroupAudio(LongFunctionCallback callback, long groupId, byte[] message, String attrs, int timeout);
     
发送房间语音
public boolean sendRoomAudio(LongFunctionCallback callback, long roomId, byte[] message, String attrs, int timeout);

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
     * @return  true(发送成功)  false(发送失败)
     */
获得p2p历史聊天消息
public boolean getP2PChat(HistoryMessageCallback callback,  long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

获得群组历史聊天消息
public boolean getGroupChat(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

获得房间历史聊天消息
public boolean getRoomChat(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

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
public boolean getBroadcastChat(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

    /*获取服务器未读消息 检测离线聊天数目
     * @param callback  UnreadCallback回调(NoNull)
     * @param clear     是否清除离线提醒
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
public boolean getUnread(final UnreadCallback callback, boolean clear, int timeout)

    /*清除离线提醒
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
public boolean clearUnread(ErrorCodeCallback callback, int timeout);
    
    /**
    * 获取和自己有过回话的用户uid和群组id集合
    * @param callback ErrorCodeCallback回调(NoNull)
    * @param timeout   超时时间(秒)
    * @return  true(发送成功)  false(发送失败)
    */
public boolean getSession(final UnreadCallback callback, int timeout);


    /*删除聊天消息
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
public boolean deleteChat(ErrorCodeCallback callback, long xid, long mid, int type, int timeout);

    /**
     * 设置翻译的目标语言
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param targetLanguage    目标语言(NoNull)
     * @param timeout   超时时间(秒)
     * @return      true(发送成功)  false(发送失败)
     */
public boolean setTranslatedLanguage(ErrorCodeCallback callback, String targetLanguage, int timeout);

    /**
     *文本翻译
     * @param callback      TranslateCallback回调(NoNull)
     * @param text          需要翻译的内容(NoNull)
     * @param destinationLanguage   目标语言(NoNull)
     * @param sourceLanguage        源文本语言
     * @param timeout               超时时间(秒)
     * @param type                  翻译类型("mail","chat")
     * @param profanity             敏感语过滤
     * @param postProfanity         是否把翻译后的文本过滤
     * @return
     */
public boolean translate(final TranslateCallback callback, String text, String destinationLanguage, String sourceLanguage, int timeout,
                             translateType type, ProfanityType profanity, boolean postProfanity);

    /**
     *文本检测
     * @param callback  ProfanityCallback回调(NoNull)
     * @param text      需要过滤的文本
     * @param classify  是否进行文本分类检测
     * @param timeout   超时时间(秒)
     * @return          true(发送成功)  false(发送失败)
     */
public boolean profanity(final ProfanityCallback callback, String text, boolean classify, int timeout);


    /**
     *语音识别
     * @param callback  DoubleStringCallback回调(NoNull)
     * @param audio     语音消息(NoNull)
     * @param timeout   超时时间(秒)
     * @return      true(发送成功)  false(发送失败)
     */
public boolean transcribe(final DoubleStringCallback callback, byte[] audio, int timeout)
~~~