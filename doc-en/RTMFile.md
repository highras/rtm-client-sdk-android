 ~~~c++
     * send p2p file async
     * @param callback  IRTMDoubleValueCallback<Long,Long> (NoNull)
     * @param peerUid   dest user id(NoNull)
     * @param mtype     messaget type(NoNull)
     * @param fileContent   file content(NoNull)
     * @param filename      file name(NoNull)
     * @param attrs     file additional message 
     * @param audioInfo rtm audio struct(if you not send rtmaduio default null)
     */
    public void sendFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long peerUid, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs, RTMAudioStruct audioInfo)
    
    
    /**
     * send p2p file sync
     * @param peerUid   dest user id(NoNull)
     * @param mtype     messaget type(NoNull)
     * @param fileContent   file content(NoNull)
     * @param filename      file name(NoNull)
     * @param attrs     file additional message 
     * @param audioInfo rtm audio struct(if you not send rtmaduio default null)
     * return ModifyTimeStruct
     */
    public ModifyTimeStruct sendFile(long peerUid, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)
    
    /**
     * send file in group async
     * @param callback  IRTMDoubleValueCallback<Long,Long> (NoNull)
     * @param groupId   group id(NoNull)
     * @param mtype     messaget type(NoNull)
     * @param fileContent   file content(NoNull)
     * @param filename      file name(NoNull)
     * @param attrs     file additional message 
     * @param audioInfo rtm audio struct(if you not send rtmaduio default null)
     */
    public void  sendGroupFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long groupId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)
    
    
    /**
     * send file in group sync
     * @param groupId   group id(NoNull)
     * @param mtype     messaget type(NoNull)
     * @param fileContent   file content(NoNull)
     * @param filename      file name(NoNull)
     * @param attrs     file additional message 
     * @param audioInfo rtm audio struct(if you not send rtmaduio default null)
     * return ModifyTimeStruct
     */
    public ModifyTimeStruct sendGroupFile(long groupId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)

    /**
     * send file in room async
     * @param callback  IRTMDoubleValueCallback<Long,Long> (NoNull)
     * @param roomId   room id(NoNull)
     * @param mtype     messaget type(NoNull)
     * @param fileContent   file content(NoNull)
     * @param filename      file name(NoNull)
     * @param attrs     file additional message 
     * @param audioInfo rtm audio struct(if you not send rtmaduio default null)
     */
    public void  sendRoomFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long roomId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)

    /**
     * send file in room sync
     * @param roomId   room id(NoNull)
     * @param mtype     messaget type(NoNull)
     * @param fileContent   file content(NoNull)
     * @param filename      file name(NoNull)
     * @param attrs     file additional message 
     * @param audioInfo rtm audio struct(if you not send rtmaduio default null)
     * return ModifyTimeStruct
     */
    public ModifyTimeStruct sendRoomFile(long roomId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)
    ~~~