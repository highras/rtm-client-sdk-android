~~~ c++
    //连接关闭 ClosedByErrorCode被关闭的错误码
    void sessionClosed(int ClosedByErrorCode);        //-- com.fpnn.ErrorCode & com.fpnn.rtm.ErrorCode

    //踢出用户
    void kickout();
    
    //踢出房间
    void kickoutRoom(long roomId);

    //-- message for String format
    /**
     *
     * @param fromUid   发送用户id
     * @param toUid     目标用户id/房间id/群组id
     * @param mtype     消息类型
     * @param mid       消息id
     * @param message   消息内容
     * @param attrs     属性值
     * @param mtime     发生时间
     */
    void pushMessage(long fromUid, long toUid, byte mtype, long mid, String message, String attrs, long mtime);
    void pushGroupMessage(long fromUid, long groupId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushRoomMessage(long fromUid, long roomId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushBroadcastMessage(long fromUid, byte mtype, long mid, String message, String attrs, long mtime);

    /**
     *
     * @param fromUid   发送用户id
     * @param toUid     目标用户id/房间id/群组id
     * @param mtype     消息类型(51-127)
     * @param mid       消息id
     * @param message   消息内容
     * @param attrs     属性值
     * @param mtime     发生时间
     */
    //-- message for binary format
    void pushMessage(long fromUid, long toUid, byte mtype, long mid, byte[] message, String attrs, long mtime);
    void pushGroupMessage(long fromUid, long groupId, byte mtype, long mid, byte[] message, String attrs, long mtime);
    void pushRoomMessage(long fromUid, long roomId, byte mtype, long mid, byte[] message, String attrs, long mtime);
    void pushBroadcastMessage(long fromUid, byte mtype, long mid, byte[] message, String attrs, long mtime);

    /**
     *
     * @param fromUid   发送用户id
     * @param toUid     目标用户id/房间id/群组id
     * @param mtype     消息类型(30)
     * @param mid       消息id
     * @param message   消息内容
     * @param attrs     属性值
     * @param mtime     发生时间
     */
    void pushChat(long fromUid, long toUid, long mid, TranslatedMessage message, String attrs, long mtime);
    void pushGroupChat(long fromUid, long groupId, long mid, TranslatedMessage message, String attrs, long mtime);
    void pushRoomChat(long fromUid, long roomId, long mid, TranslatedMessage message, String attrs, long mtime);
    void pushBroadcastChat(long fromUid, long mid, TranslatedMessage message, String attrs, long mtime);

    /**
     *
     * @param fromUid   发送用户id
     * @param toUid     目标用户id/房间id/群组id
     * @param mtype     消息类型(31)
     * @param mid       消息id
     * @param message   语音内容
     * @param attrs     属性值
     * @param mtime     发生时间
     */
    void pushAudio(long fromUid, long toUid, long mid, byte[] message, String attrs, long mtime);
    void pushGroupAudio(long fromUid, long groupId, long mid, byte[] message, String attrs, long mtime);
    void pushRoomAudio(long fromUid, long roomId, long mid, byte[] message, String attrs, long mtime);
    void pushBroadcastAudio(long fromUid, long mid, byte[] message, String attrs, long mtime);

    /**
     *
     * @param fromUid   发送用户id
     * @param toUid     目标用户id/房间id/群组id
     * @param mtype     消息类型(32)
     * @param mid       消息id
     * @param message   指令内容
     * @param attrs     属性值
     * @param mtime     发生时间
     */
    void pushCmd(long fromUid, long toUid, long mid, String message, String attrs, long mtime);
    void pushGroupCmd(long fromUid, long groupId, long mid, String message, String attrs, long mtime);
    void pushRoomCmd(long fromUid, long roomId, long mid, String message, String attrs, long mtime);
    void pushBroadcastCmd(long fromUid, long mid, String message, String attrs, long mtime);

    /**
     *
     * @param fromUid   发送用户id
     * @param toUid     目标用户id/房间id/群组id
     * @param mtype     消息类型(40-50)
     * @param mid       消息id
     * @param message   文件url地址
     * @param attrs     属性值
     * @param mtime     发生时间
     */
    void pushFile(long fromUid, long toUid, byte mtype, long mid, String message, String attrs, long mtime);
    void pushGroupFile(long fromUid, long groupId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushRoomFile(long fromUid, long roomId, byte mtype, long mid, String message, String attrs, long mtime);
    void pushBroadcastFile(long fromUid, byte mtype, long mid, String message, String attrs, long mtime);
~~~