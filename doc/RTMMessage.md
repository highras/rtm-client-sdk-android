###  同步接口
~~~c++
以下接口统一统参数说明
    /*
     * @param mtime  LongMtime对象(NoNull)
     * @param uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @mtype 消息类型
     * @param message   消息/二进制消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    
发送p2p消息   
public int sendMessage(LongMtime mtime, long uid, byte mtype, String message, String attrs, int timeout);

发送群组消息
public int sendGroupMessage(LongMtime mtime, long groupId, byte mtype, String message, String attrs, int timeout);

发送房间消息
public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, String message, String attrs, int timeout);

----二进制消息-----
发送p2p二进制消息
public int sendMessage(LongMtime mtime, long uid, byte mtype, byte[] message, String attrs, int timeout);

发送群组二进制消息
public int sendGroupMessage(LongMtime mtime, long groupId, byte mtype, byte[] message, String attrs, int timeout);

发送房间二进制消息
public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, byte[] message, String attrs, int timeout);


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
获得p2p历史记录
public int getP2PMessage(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout);

获得群组历史记录
public int getGroupMessage(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout);

获得房间历史记录
public int getRoomMessage(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout);

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
public int getBroadcastMessage(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout);

    /*获取单条消息 sync
     * @param retrievedMessage RetrievedMessage对象(**NoNull**)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getMessage(RetrievedMessage retrievedMessage, long xid, long mid, int type, int timeout)
~~~



### 异步接口
~~~c++
以下接口统一统参数说明(mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE)
    /*
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @mtype 消息类型
     * @param message   消息/指令消息/语音消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
     
发送p2p消息
public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, String message, String attrs, int timeout);
     
发送群组消息
public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, String message, String attrs, int timeout);

发送房间消息
public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, String message, String attrs, int timeout);
     
----二进制消息----
public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, byte[] message, String attrs, int timeout);

发送群组二进制消息
public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, byte[] message, String attrs, int timeout);

发送房间二进制消息
public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, byte[] message, String attrs, int timeout);

以下接口统一参数说明
    /*
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param uid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
获得p2p历史消息
public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout);

获得群组历史消息
public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout);

获得房间历史消息
public boolean getRoomChat(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

    /**
    * 获得广播历史消息
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
public boolean getBroadcastChat(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, int timeout);

    /**
     *获取单条消息 async
     * @param callback RetrievedMessageCallback回调(NoNull)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getMessage(final RetrievedMessageCallback callback, long xid, long mid, int type, int timeout)
~~~
