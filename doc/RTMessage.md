###  同步接口
~~~c++
以下接口统一统参数说明
    /**
     *发送p2p消息(sync)
     * @param uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @param mtype     消息类型
     * @param message   消息内容(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return          服务器返回时间
     */
发送p2p消息   
    public ModifyTimeStruct sendMessage(long uid, byte mtype, String message, String attrs, int timeout)

发送群组消息
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, String message, String attrs, int timeout)

发送房间消息
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, String message, String attrs, int timeout)

----二进制消息-----
发送p2p二进制消息
    public ModifyTimeStruct sendMessage(long uid, byte mtype, byte[] message, String attrs, int timeout)

发送群组二进制消息
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, byte[] message, String attrs, int timeout)

发送房间二进制消息
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, byte[] message, String attrs, int timeout)


以下接口统一参数说明
    /**
     *获取p2p记录(sync)
     * @param peerUid/groupId/roomId  用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return          HistoryMessageResult
     */
获得p2p历史记录
    public HistoryMessageResult getP2PHistoryMessage( long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)

获得群组历史记录
    public HistoryMessageResult getGroupHistoryMessage(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)

获得房间历史记录
    public HistoryMessageResult   getRoomHistoryMessage(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)

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
    public HistoryMessageResult getBroadcastHistoryMessage( boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)

以下接口统一参数说明
    /*获取单条聊天消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @param timeout   超时时间(秒)
     */

获取p2p单条消息
    public SingleMessage getP2PMessage(long fromUid, long toUid, long messageId, int timeout)

获取群组单条消息
    public SingleMessage getGroupMessage(long fromUid, long groupId, long messageId, int timeout)

获取房间单条消息
    public SingleMessage getRoomMessage(long fromUid, long roomId, long messageId, int timeout)

删除p2p单条消息
    public RTMAnswer deleteP2PMessage(long fromUid, long toUid, long messageId, int timeout)

删除群组单条消息
    public RTMAnswer deleteGroupMessage(long fromUid, long groupId, long messageId, int timeout)

删除房间单条消息
    public RTMAnswer deleteRoomMessage(long fromUid, long roomId, long messageId, int timeout)

     /*获取广播单条聊天消息 sync
     * @param messageId   消息id(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public SingleMessage getBroadcastMessage(long messageId, int timeout)
~~~



### 异步接口
~~~c++
以下接口统一统参数说明(mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE)
    /**
     *发送p2p消息(async)
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @param mtype     消息类型
     * @param message   p2p消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     */
     
发送p2p消息
    public void sendMessage(IRTMCallback<Long> callback, long uid, byte mtype, String message, String attrs, int timeout) 
     
发送群组消息
    public void sendGroupMessage(IRTMCallback<Long> callback, long groupId, byte mtype, String message, String attrs, int timeout) 

发送房间消息
    public void sendRoomMessage(IRTMCallback<Long> callback, long roomId, byte mtype, String message, String attrs, int timeout) 
     
----二进制消息----
    public void sendMessage(IRTMCallback<Long> callback, long uid, byte mtype, byte[] message, String attrs, int timeout) 

发送群组二进制消息
    public void sendGroupMessage(IRTMCallback<Long> callback, long groupId, byte mtype, byte[] message, String attrs, int timeout) 

发送房间二进制消息
    public void sendRoomMessage(IRTMCallback<Long> callback, long roomId, byte mtype, byte[] message, String attrs, int timeout) 

以下接口统一参数说明
    /**
     * @param callback  IRTMCallback<HistoryMessageResult> 回调(NoNull)
     * @param uid/groupId/roomId  目标用户id/群组id/房间id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     */

获得p2p历史消息
    public void getP2PHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 

获得群组历史消息
    public void getGroupHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 

获得房间历史消息
    public void getRoomHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 

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
    public void getBroadcastHistoryMessage(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 

以下接口统一参数说明
    /**
     *获取单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调(NoNull)
     * @param fromUid   发送者id(NoNull)
     * @param toUid     接收者id(NoNull)
     * @param messageId   消息id(NoNull)
     * @param timeout   超时时间(秒)
     */

获取p2p单条聊天消息
    public void getP2PMessage(IRTMCallback<SingleMessage> callback, long fromUid, long toUid, long messageId, int timeout) 

获取群组单条聊天消息
    public void getGroupMessage(IRTMCallback<SingleMessage> callback, long fromUid, long groupId, long messageId, int timeout) 

获取房间单条聊天消息
    public void getRoomMessage(IRTMCallback<SingleMessage> callback, long fromUid, long roomId, long messageId, int timeout) 

删除p2p单条消息
    public void deleteP2PMessage(IRTMEmptyCallback callback, long fromUid, long toUid, long messageId,  int timeout) 

获取群组单条聊天消息
    public void deleteGroupMessage(IRTMEmptyCallback callback, long fromUid, long groupId, long messageId,  int timeout) 

获取房间单条聊天消息
    public void deleteRoomMessage(IRTMEmptyCallback callback, long fromUid, long RoomId, long messageId,  int timeout) 


    /**
     *获取广播单条聊天消息 async
     * @param callback IRTMCallback<SingleMessage>回调(NoNull)
     * @param messageId   消息id(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getBroadcastMessage(IRTMCallback<SingleMessage> callback, long messageId, int timeout) 
~~~
