    ~~~c++
    /**
     * 发送p2p文件 async
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param attrs 文件属性信息
     * @param audioInfo rtm语音结构
     */
    public void sendFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long peerUid, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs, RTMAudioStruct audioInfo)
    
    /**
     * 发送p2p文件 sync
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param attrs 文件属性信息
     * @param audioInfo rtm语音结构
     */
    public ModifyTimeStruct sendFile(long peerUid, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)
    
    /**
     * 发送群组文件 async
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param attrs 文件属性信息
     * @param audioInfo rtm语音结构
     */
    public void  sendGroupFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long groupId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)
    
    
    /**
     * 发送群组文件 sync
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param attrs 文件属性信息
     * @param audioInfo rtm语音结构
     */
    public ModifyTimeStruct sendGroupFile(long groupId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)

    /**
     * 发送房间文件 async
     * @param callback  IRTMDoubleValueCallback<Long,Long>接口回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param attrs 文件属性信息
     * @param audioInfo rtm语音结构
     */
    public void  sendRoomFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long roomId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)

    /**
     * 发送房间文件 sync
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param attrs 文件属性信息
     * @param audioInfo rtm语音结构
     */
    public ModifyTimeStruct sendRoomFile(long roomId, FileMessageType mtype, byte[] fileContent, String filename, JSONObject attrs,RTMAudioStruct audioInfo)
    ~~~