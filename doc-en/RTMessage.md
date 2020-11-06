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
     * @return          ModifyTimeStruct结构
     */
send p2p message   
    public ModifyTimeStruct sendMessage(long uid, byte mtype, String message, String attrs)

send message in group
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, String message, String attrs)

send messaget in room
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, String message, String attrs)


send p2p binary message  
    public ModifyTimeStruct sendMessage(long uid, byte mtype, byte[] message, String attrs)

send binary message in group
    public ModifyTimeStruct sendGroupMessage(long groupId, byte mtype, byte[] message, String attrs)

send binary message in room
    public ModifyTimeStruct sendRoomMessage(long roomId, byte mtype, byte[] message, String attrs)



    /**
     *get history messget(sync)
     * @param peerUid/groupId/roomId  (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     
     * return   HistoryMessageResult
     */
     

    public HistoryMessageResult getP2PHistoryMessage( long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes)


    public HistoryMessageResult getGroupHistoryMessage(long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes)


    public HistoryMessageResult   getRoomHistoryMessage(long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes)

    /**
     *(async)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     
     * return   HistoryMessageResult
     */
    public HistoryMessageResult getBroadcastHistoryMessage( boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes)



    /*get single history message sync
     * @param messageId   message id(NoNull)
     * @param fromUid   send user id(NoNull)
     * @param toUid/groupId/roomId  (NoNull)
     
     */


    public SingleMessage getP2PMessage(long fromUid, long toUid, long messageId)


    public SingleMessage getGroupMessage(long fromUid, long groupId, long messageId)


    public SingleMessage getRoomMessage(long fromUid, long roomId, long messageId)


    public RTMAnswer deleteP2PMessage(long fromUid, long toUid, long messageId)


    public RTMAnswer deleteGroupMessage(long fromUid, long groupId, long messageId)


    public RTMAnswer deleteRoomMessage(long fromUid, long roomId, long messageId)

     /* sync
     * @param messageId   message id(NoNull)
     * @param fromUid   send user id(NoNull)
     
     */
    public SingleMessage getBroadcastMessage(long messageId)
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
     
     */
     
    public void sendMessage(IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, String message, String attrs) 
     

    public void sendGroupMessage(IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, String message, String attrs) 


    public void sendRoomMessage(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, String message, String attrs) 
     
    //binary message
    public void sendMessage(IRTMDoubleValueCallback<Long,Long> callback, long uid, byte mtype, byte[] message, String attrs) 

    public void sendGroupMessage(IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, byte[] message, String attrs) 

    public void sendRoomMessage(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, byte[] message, String attrs) 



    /**
     * @param callback  IRTMCallback<HistoryMessageResult> (NoNull)
     * @param uid/groupId/roomId  (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param mtypes    query messaget types
     
     */


    public void getP2PHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) 


    public void getGroupHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) 


    public void getRoomHistoryMessage(IRTMCallback<HistoryMessageResult> callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) 

    /**
     *(async)
     * @param callback  IRTMCallback (NoNull)
     * @param desc      if reverse order  
     * @param count     show count 
     * @param beginMsec query begin time(millisecond)
     * @param endMsec   query end time(millisecond)
     * @param lastId    last message indexId
     * @param mtypes    query messaget types
     
     */
    public void getBroadcastHistoryMessage(IRTMCallback<HistoryMessageResult> callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) 


    /**
     * get single messaget(async)
     * @param callback IRTMCallback<SingleMessage>(NoNull)
     * @param fromUid   send user id(NoNull)
     * @param toUid     to user id(NoNull)
     * @param messageId   messaget id(NoNull)
     
     */


    public void getP2PMessage(IRTMCallback<SingleMessage> callback, long fromUid, long toUid, long messageId) 


    public void getGroupMessage(IRTMCallback<SingleMessage> callback, long fromUid, long groupId, long messageId) 


    public void getRoomMessage(IRTMCallback<SingleMessage> callback, long fromUid, long roomId, long messageId) 


    public void deleteP2PMessage(IRTMEmptyCallback callback, long fromUid, long toUid, long messageId,  ) 


    public void deleteGroupMessage(IRTMEmptyCallback callback, long fromUid, long groupId, long messageId,  ) 


    public void deleteRoomMessage(IRTMEmptyCallback callback, long fromUid, long RoomId, long messageId,  ) 


    /**
     * async
     * @param callback IRTMCallback<SingleMessage> (NoNull)
     * @param messageId   messaget id(NoNull)
     
     */
    public void getBroadcastMessage(IRTMCallback<SingleMessage> callback, long messageId) 
~~~
