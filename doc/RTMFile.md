    /**
     * 发送p2p文件 async
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendFile(LongFunctionCallback callback, long peerUid, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout);
    
    /**
     * 发送p2p文件 sync
     * @param mytime  LongMtime对象(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public boolean sendFile(LongFunctionCallback callback, long peerUid, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout);
    
    /**
     * 发送群组文件 async
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendGroupFile(LongFunctionCallback callback, long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout);

    /**
     * 发送群组文件 sync
     * @param mtime  LongMtime对象(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendGroupFile(LongMtime mtime, long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout);
    
    
    /**
     * 发送房间文件 async
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendRoomFile(LongFunctionCallback callback, long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout);

    /**
     * 发送房间文件 sync
     * @param mtime  LongMtime对象(NoNull)
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendRoomFile(LongMtime mtime, long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout);    