~~~ java
--群组相关接口--
    /**
     * 添加群组用户 async
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean addGroupMembers(final ErrorCodeCallback callback, long groupId, HashSet<Long> uids, int timeout);

    /**
     * 添加群组用户  sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int addGroupMembers(long groupId, HashSet<Long> uids, int timeout);

    /**
     * 删除群组用户   async
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean deleteGroupMembers(final ErrorCodeCallback callback, long groupId, HashSet<Long> uids, int timeout);
  
    /**
     * 删除群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int deleteGroupMembers(long groupId, HashSet<Long> uids, int timeout);

    /**
     * 获取群组用户   async
     * @param callback  MembersCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean getGroupMembers(final MembersCallback callback, long groupId, int timeout);

    /**
     * 删除群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int getGroupMembers(HashSet<Long> uids, long groupId, int timeout);


    /**
     * 获取用户所在的群组   async
     * @param callback  MembersCallback回调(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean getUserGroups(final MembersCallback callback, int timeout);


    /**
     * 获取用户所在的群组   sync
     * @param groupIds  群组集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int getUserGroups(HashSet<Long> groupIds, int timeout);

    /**
     * 设置群组的公开信息或者私有信息 async
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean setGroupInfo(ErrorCodeCallback callback, long groupId, String publicInfo, String privateInfo, int timeout);
    
    
    /**
     * 设置群组的公开信息或者私有信息 sync
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int setGroupInfo(long groupId, String publicInfo, String privateInfo, int timeout);

    /**
     * 获取群组的公开信息或者私有信息 async
     * @param callback  DoubleStringCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getGroupInfo(final DoubleStringCallback callback, long groupId, int timeout);

    /**
     * 获取群组的公开信息或者私有信息 sync
     * @param publicInfo    群组公开信息
     * @param privateInfo    群组私有信息
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getGroupInfo(StringBuilder publicInfo, StringBuilder privateInfo, long groupId, int timeout);

    /**
     * 获取群组的公开信息 async
     * @param callback  MessageCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getGroupPublicInfo(final MessageCallback callback, long groupId, int timeout);

    /**
     * 获取群组的公开信息 sync
     * @param publicInfo    群组公开信息
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getGroupPublicInfo(StringBuilder publicInfo, long groupId, int timeout);
    
    
    
    ----房间相关接口----
    /**
     * 进入房间 async
     *
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean enterRoom(final ErrorCodeCallback callback, long roomId, int timeout);

    /**
     * 进入房间 async
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int enterRoom(long roomId, int timeout);

    /**
     * 离开房间 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean leaveRoom(ErrorCodeCallback callback, long roomId, int timeout);

    /**
     * 离开房间 async
     *
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int leaveRoom(long roomId, int timeout);

    /**
     * 获取用户所在的房间   async
     *
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getUserRooms(final MembersCallback callback, int timeout);

    /**
     * 获取用户所在的房间   sync
     *
     * @param roomIds 群组集合(NoNull)
     * @param timeout 超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getUserRooms(HashSet<Long> roomIds, int timeout);

    /**
     * 设置房间的公开信息或者私有信息 async
     *
     * @param callback    ErrorCodeCallback回调(NoNull)
     * @param roomId      房间id(NoNull)
     * @param publicInfo  群组公开信息
     * @param privateInfo 群组 私有信息
     * @param timeout     超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean setRoomInfo(ErrorCodeCallback callback, long roomId, String publicInfo, String privateInfo, int timeout);

    /**
     * 设置房间的公开信息或者私有信息 sync
     *
     * @param roomId      房间id(NoNull)
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int setRoomInfo(long roomId, String publicInfo, String privateInfo, int timeout);

    /**
     * 获取房间的公开信息或者私有信息 async
     *
     * @param callback DoubleStringCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getRoomInfo(final DoubleStringCallback callback, long roomId, int timeout);

    /**
     * 获取房间的公开信息或者私有信息 sync
     *
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param roomId      房间id(NoNull)
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getRoomInfo(StringBuilder publicInfo, StringBuilder privateInfo, long roomId, int timeout);

    /**
     * 获取房间的公开信息 async
     * @param callback MessageCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getRoomPublicInfo(final MessageCallback callback, long roomId, int timeout);
    
    /**
     * 获取房间的公开信息 sync
     * @param publicInfo    公开信息
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getRoomPublicInfo(StringBuilder publicInfo, long roomId, int timeout);
    
    
    --好友相关接口---
    /**
     * 添加好友 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean addFriends(ErrorCodeCallback callback, HashSet<Long> uids, int timeout);

    /**
     * 添加好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int addFriends(HashSet<Long> uids, int timeout);

    /**
     * 删除好友 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean deleteFriends(ErrorCodeCallback callback, HashSet<Long> uids, int timeout);
    
    /**
     * 删除好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int deleteFriends(HashSet<Long> uids, int timeout);

    /**
     * 查询自己好友 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getFriends(final MembersCallback callback, int timeout);

    /**
     * 查询自己好友 sync
     * @param friends   好友id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int getFriends(HashSet<Long> friends, int timeout);
    
    
    /**
     * 添加黑名单 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean addBlacklist(ErrorCodeCallback callback, HashSet<Long> uids, int timeout) 
    /**
     * 添加黑名单 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int addBlacklist(HashSet<Long> uids, int timeout)

    /**
     * 删除黑名单用户 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean delBlacklist(ErrorCodeCallback callback, HashSet<Long> uids, int timeout)

    /**
     * 删除黑名单用户 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int delBlacklist(HashSet<Long> uids, int timeout) 

    /**
     * 查询黑名单 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getBlacklist(final MembersCallback callback, int timeout) 

    /**
     * 查询黑名单 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int getBlacklist(HashSet<Long> uids, int timeout)
~~~