###  sync interface
~~~c++
    /**
     * mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE.
     */
    /**
     *(sync)
     * @param userid/groupId/roomId (NoNull)
     * @param mtype     message type
     * @param message   (NoNull)
     * @param attrs     additional message
     * @param timeout   timeout(seconds)
     * @return          ModifyTimeStruct结构
     */
send p2p message   
    public ModifyTimeStruct sendMessage(long uid, byte mtype, String message, String attrs, int timeout)

send message in group
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, String message, String attrs, int timeout)

send messaget in room
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, String message, String attrs, int timeout)


send p2p binary message  
    public ModifyTimeStruct sendMessage(long uid, byte mtype, byte[] message, String attrs, int timeout)

send binary message in group
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, byte[] message, String attrs, int timeout)

send binary message in room
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, byte[] message, String attrs, int timeout)



    /**
     *get history messget(sync)
     * @param peerUid/groupId/roomId  (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param timeout   timeout(seconds)
     * return   HistoryMessageResult
     */
     

    public HistoryMessageResult getP2PHistoryMessage( long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)


    public HistoryMessageResult getGroupHistoryMessage(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)


    public HistoryMessageResult   getRoomHistoryMessage(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)

    /**
     *(async)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param timeout   timeout(seconds)
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryMessage( boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout)



    /*get single history message sync
     * @param messageId   message id(NoNull)
     * @param fromUid   send user id(NoNull)
     * @param toUid/groupId/roomId  (NoNull)
     * @param timeout   timeout(seconds)
     */


    public SingleMessage getP2PMessage(long fromUid, long toUid, long messageId, int timeout)


    public SingleMessage getGroupMessage(long fromUid, long groupId, long messageId, int timeout)


    public SingleMessage getRoomMessage(long fromUid, long roomId, long messageId, int timeout)


    public RTMAnswer deleteP2PMessage(long fromUid, long toUid, long messageId, int timeout)


    public RTMAnswer deleteGroupMessage(long fromUid, long groupId, long messageId, int timeout)


    public RTMAnswer deleteRoomMessage(long fromUid, long roomId, long messageId, int timeout)

     /* sync
     * @param messageId   message id(NoNull)
     * @param fromUid   send user id(NoNull)
     * @param timeout   timeout(seconds)
     */
    public SingleMessage getBroadcastMessage(long messageId, int timeout)
~~~



### async interface
~~~c++
    (mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE)
    /**
     *(async)
     * @param callback  IRTMDoubleValueCallback<Long,Long> (NoNull)
     * @param userid/groupId/roomId  (NoNull)
     * @param mtype     messaget type
     * @param message   need send message
     * @param attrs     additional message
     * @param timeout   timeout(seconds)
     */
     
    public void sendMessage(IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, String message, String attrs, int timeout) 
     

    public void sendGroupMessage(IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, String message, String attrs, int timeout) 


    public void sendRoomMessage(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, String message, String attrs, int timeout) 
     
    //binary message
    public void sendMessage(IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, byte[] message, String attrs, int timeout) 

    public void sendGroupMessage(IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, byte[] message, String attrs, int timeout) 

    public void sendRoomMessage(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, byte[] message, String attrs, int timeout) 



    /**
     * @param callback  IRTMCallback<HistoryMessageResult> (NoNull)
     * @param uid/groupId/roomId  (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param mtypes    query messaget types
     * @param timeout   timeout(seconds)
     */


    public void getP2PHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 


    public void getGroupHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 


    public void getRoomHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 

    /**
     *(async)
     * @param callback  IRTMCallback (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param mtypes    query messaget types
     * @param timeout   timeout(seconds)
     */
    public void getBroadcastHistoryMessage(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) 


    /**
     * get single messaget(async)
     * @param callback IRTMCallback<SingleMessage>(NoNull)
     * @param fromUid   send user id(NoNull)
     * @param toUid     to user id(NoNull)
     * @param messageId   messaget id(NoNull)
     * @param timeout   timeout(seconds)
     */


    public void getP2PMessage(IRTMCallback<SingleMessage> callback, long fromUid, long toUid, long messageId, int timeout) 


    public void getGroupMessage(IRTMCallback<SingleMessage> callback, long fromUid, long groupId, long messageId, int timeout) 


    public void getRoomMessage(IRTMCallback<SingleMessage> callback, long fromUid, long roomId, long messageId, int timeout) 


    public void deleteP2PMessage(IRTMEmptyCallback callback, long fromUid, long toUid, long messageId,  int timeout) 


    public void deleteGroupMessage(IRTMEmptyCallback callback, long fromUid, long groupId, long messageId,  int timeout) 


    public void deleteRoomMessage(IRTMEmptyCallback callback, long fromUid, long RoomId, long messageId,  int timeout) 


    /**
     * async
     * @param callback IRTMCallback<SingleMessage> (NoNull)
     * @param messageId   messaget id(NoNull)
     * @param timeout   timeout(seconds)
     */
    public void getBroadcastMessage(IRTMCallback<SingleMessage> callback, long messageId, int timeout) 
~~~
