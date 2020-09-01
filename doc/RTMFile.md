    ~~~c++
    /**
     * 发送p2p文件 async
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public void sendFile(IRTMCallback<Long> callback, long peerUid, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout) 
    
    /**
     * 发送p2p文件 sync
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public ModifyTimeStruct sendFile(long peerUid, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout)
    
    /**
     * 发送群组文件 async
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public void  sendGroupFile(IRTMCallback<Long> callback, long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout) 
    
    
    /**
     * 发送群组文件 sync
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public ModifyTimeStruct sendGroupFile(long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout)

    /**
     * 发送房间文件 async
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public void  sendRoomFile(IRTMCallback<Long> callback, long roomId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout)  

    /**
     * 发送房间文件 sync
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public ModifyTimeStruct sendRoomFile(long roomId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout)
    ~~~