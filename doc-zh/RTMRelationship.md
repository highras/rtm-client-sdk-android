~~~ java
--Group Interface--
    /**
     * 添加群组用户 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void addGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids, int timeout) 

    /**
     * 添加群组用户  sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return
     * */
    public RTMAnswer addGroupMembers(long groupId, HashSet<Long> uids, int timeout)


    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback IRTMCallback<Map<String, String>>回调(NoNull)
     * @param gids     房间id集合
     * @param timeout  超时时间(秒)
     */
    public void getGroupsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> gids, int timeout) 
    
        /**
     * 获取群组的公开信息，每次最多获取100人
     * @param gids        群组id集合
     * @param timeout     超时时间(秒)
     *return              PublicInfo 结构
     */
    public PublicInfo getGroupsOpeninfo(HashSet<Long> gids, int timeout) 
    
    
    /**
     * 删除群组用户   async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void deleteGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids, int timeout) 
  
    /**
     * 删除群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public RTMAnswer deleteGroupMembers(long groupId, HashSet<Long> uids, int timeout){

    /**
     * 获取群组用户   async
     * @param callback  IRTMCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void getGroupMembers(final IRTMCallback<HashSet<Long>> callback, long groupId, int timeout)

    /**
     * 获取群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * reutn 用户id集合
     * */
    public MembersStruct getGroupMembers(long groupId, int timeout){


    /**
     * 获取用户所在的群组   async
     * @param callback  IRTMCallback回调(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void getUserGroups(final IRTMCallback<HashSet<Long>> callback, int timeout)


    /**
     * 获取用户所在的群组   sync
     * @param timeout   超时时间（秒）
     * @return  用户所在群组集合
     * */
    public MembersStruct getUserGroups(int timeout){

    /**
     * 设置群组的公开信息或者私有信息 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）
     */
    public void setGroupInfo(IRTMEmptyCallback callback, long groupId, String publicInfo, String privateInfo, int timeout)
    
    
    /**
     * 设置群组的公开信息或者私有信息 sync
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）
     */
    public RTMAnswer setGroupInfo(long groupId, String publicInfo, String privateInfo, int timeout){

    /**
     * 获取群组的公开信息或者私有信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getGroupInfo(final IRTMCallback<GroupInfoStruct> callback, final long groupId, int timeout)

    /**
     * 获取群组的公开信息和私有信息 sync
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getGroupInfo(long groupId, int timeout)

    /**
     * 获取群组的公开信息 async
     * @param callback  MessageCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getGroupPublicInfo(final IRTMCallback<String>  callback, long groupId, int timeout)
    
    
    /**
     * 获取群组的公开信息 sync
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getGroupPublicInfo(long groupId, int timeout){
        
        //ROOM interface
    /**
     * 进入房间 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void enterRoom(final IRTMEmptyCallback callback, long roomId, int timeout)

    /**
     * 进入房间 sync
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     */
    public RTMAnswer enterRoom(long roomId, int timeout){

    /**
     * 离开房间 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void leaveRoom(IRTMEmptyCallback callback, long roomId, int timeout)

    /**
     * 离开房间 sync
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     */
    public RTMAnswer leaveRoom(long roomId, int timeout)

    /**
     * 获取用户所在的房间   async
     * @param callback IRTMCallback回调(NoNull)
     * @param timeout  超时时间（秒）
     */
    public void getUserRooms(final IRTMCallback<HashSet<Long>> callback, int timeout)

    /**
     * 获取用户所在的房间   sync
     * @param timeout   超时时间（秒）
     * @return  用户所在房间集合
     * */
    public MembersStruct getUserRooms(int timeout){

    /**
     * 设置房间的公开信息或者私有信息 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     * @param timeout   超时时间（秒）
     */
    public void setRoomInfo(IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo, int timeout)

    /**
     * 设置房间的公开信息或者私有信息 sync
     * @param roomId   房间id(NoNull)
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     * @param timeout   超时时间（秒）
     */
    public RTMAnswer setRoomInfo(long roomId, String publicInfo, String privateInfo, int timeout){

    /**
     * 获取房间的公开信息或者私有信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getRoomInfo(final IRTMCallback<GroupInfoStruct> callback, final long roomId, int timeout)

    /**
     * 获取房间的公开信息或者私有信息 sync
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getRoomInfo(long roomId, int timeout){

            /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback IRTMCallback<Map<String, String>>回调(NoNull)
     * @param rids     房间id集合
     * @param timeout  超时时间(秒)
     */
    public void getRoomsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> rids, int timeout) 
    
      /**
     * 获取群组的公开信息，每次最多获取100人
     * @param rids        房间id集合
     * @param timeout     超时时间(秒)
     *return              PublicInfo 结构
     */
    public PublicInfo getRoomsOpeninfo(HashSet<Long> rids, int timeout) 
    
    /**
     * 获取房间的公开信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getRoomPublicInfo(final IRTMCallback<String>  callback, long roomId, int timeout)
    
    /**
     * 获取房间的公开信息 sync
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getRoomPublicInfo(long roomId, int timeout){
    
    
    --Friend Interface---
    /**
     * 添加好友 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void addFriends(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * 添加好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer addFriends(HashSet<Long> uids, int timeout){   

    /**
     * 删除好友 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void deleteFriends(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * 删除好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer deleteFriends(HashSet<Long> uids, int timeout){

    /**
     * 查询自己好友 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void getFriends(final IRTMCallback<HashSet<Long>> callback, int timeout)

    /**
     * 查询自己好友 sync
     * @param timeout  超时时间(秒)
     * @return 好友id集合
     */
    public MembersStruct getFriends(int timeout){

    /**
     * 添加黑名单 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void addBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * 添加黑名单 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer addBlacklist(HashSet<Long> uids, int timeout){

    /**
     * 删除黑名单用户 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void delBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * 删除黑名单用户 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer delBlacklist(HashSet<Long> uids, int timeout){

    /**
     * 查询黑名单 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void getBlacklist(final IRTMCallback<HashSet<Long>> callback, int timeout)

    /**
     * 查询黑名单 sync
     * @param timeout  超时时间(秒)
     * @return 黑名单id集合
     */
    public MembersStruct getBlacklist(int timeout){
~~~