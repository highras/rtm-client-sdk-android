~~~ java
--Group Interface--
    /**
     * 添加群组用户 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * */
    public void addGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids) 

    /**
     * 添加群组用户  sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @return
     * */
    public RTMAnswer addGroupMembers(long groupId, HashSet<Long> uids)


    /**
     * 获取其他用户的公开信息，每次最多获取100个群组
     * @param callback IRTMCallback<Map<String, String>>回调(NoNull)
     * @param gids     房间id集合
     */
    public void getGroupsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> gids) 
    
        /**
     * 获取群组的公开信息，每次最多获取100个群组
     * @param gids        群组id集合
     *return              PublicInfo 结构
     */
    public PublicInfo getGroupsOpeninfo(HashSet<Long> gids) 
    
    
    /**
     * 删除群组用户   async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     
     * */
    public void deleteGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids) 
  
    /**
     * 删除群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     
     * */
    public RTMAnswer deleteGroupMembers(long groupId, HashSet<Long> uids){

    /**
     * 获取群组用户   async
     * @param callback  IRTMCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * */
    public void getGroupMembers(final IRTMCallback<HashSet<Long>> callback, long groupId)

    /**
     * 获取群组用户   sync
     * @param groupId   群组id(NoNull)
     * reutn 用户id集合
     * */
    public MembersStruct getGroupMembers(long groupId){


    /**
     * 获取用户所在的群组   async
     * @param callback  IRTMCallback回调(NoNull)
     * */
    public void getUserGroups(final IRTMCallback<HashSet<Long>> callback)


    /**
     * 获取用户所在的群组   sync
     * @return  用户所在群组集合
     * */
    public MembersStruct getUserGroups(){

    /**
     * 设置群组的公开信息或者私有信息 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     
     */
    public void setGroupInfo(IRTMEmptyCallback callback, long groupId, String publicInfo, String privateInfo)
    
    
    /**
     * 设置群组的公开信息或者私有信息 sync
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     */
    public RTMAnswer setGroupInfo(long groupId, String publicInfo, String privateInfo){

    /**
     * 获取群组的公开信息或者私有信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     */
    public void getGroupInfo(final IRTMCallback<GroupInfoStruct> callback, final long groupId)

    /**
     * 获取群组的公开信息和私有信息 sync
     * @param groupId   群组id(NoNull)
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getGroupInfo(long groupId)

    /**
     * 获取群组的公开信息 async
     * @param callback  MessageCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     */
    public void getGroupPublicInfo(final IRTMCallback<String>  callback, long groupId)
    
    
    /**
     * 获取群组的公开信息 sync
     * @param groupId   群组id(NoNull)
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getGroupPublicInfo(long groupId){
        
        //ROOM interface
    /**
     * 进入房间 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     */
    public void enterRoom(final IRTMEmptyCallback callback, long roomId)

    /**
     * 进入房间 sync
     * @param roomId  房间id(NoNull)
     
     */
    public RTMAnswer enterRoom(long roomId){

    /**
     * 离开房间 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     */
    public void leaveRoom(IRTMEmptyCallback callback, long roomId)

    /**
     * 离开房间 sync
     * @param roomId  房间id(NoNull)
     */
    public RTMAnswer leaveRoom(long roomId)


    /**
     * 获取房间中的所有member sync(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param roomId  房间id
     */
    public MembersStruct getRoomMembers(long roomId) 
    
    
    /**
     * 获取房间中的所有人数 async(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param callback IRTMEmptyCallback回调
     * @param roomId   房间id
     */
    public void getRoomCount(@NonNull final IRTMCallback<Integer> callback, long roomId)


    /**
     * 获取房间中的所有人数 sync(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param roomId  房间id
     */
    public MemberCount getRoomCount(long roomId) 

    /**
     * 获取房间中的所有member async(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param callback IRTMEmptyCallback回调
     * @param roomId   房间id
     */
    public void getRoomMembers(@NonNull final IRTMCallback<HashSet<Long>> callback, long roomId)
    
    
    /**
     * 获取用户所在的房间   async
     * @param callback IRTMCallback回调(NoNull)
     
     */
    public void getUserRooms(final IRTMCallback<HashSet<Long>> callback)

    /**
     * 获取用户所在的房间   sync
     
     * @return  用户所在房间集合
     * */
    public MembersStruct getUserRooms(){

    /**
     * 设置房间的公开信息或者私有信息 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     
     */
    public void setRoomInfo(IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo)

    /**
     * 设置房间的公开信息或者私有信息 sync
     * @param roomId   房间id(NoNull)
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     */
    public RTMAnswer setRoomInfo(long roomId, String publicInfo, String privateInfo){

    /**
     * 获取房间的公开信息或者私有信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     */
    public void getRoomInfo(final IRTMCallback<GroupInfoStruct> callback, final long roomId)

    /**
     * 获取房间的公开信息或者私有信息 sync
     * @param roomId   房间id(NoNull)
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getRoomInfo(long roomId){
        
        /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback IRTMCallback<Map<String, String>>回调(NoNull)
     * @param rids     房间id集合
     
     */
    public void getRoomsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> rids) 
    
      /**
     * 获取群组的公开信息，每次最多获取100人
     * @param rids        房间id集合
     *return              PublicInfo 结构
     */
    public PublicInfo getRoomsOpeninfo(HashSet<Long> rids) 
    
    /**
     * 获取房间的公开信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     */
    public void getRoomPublicInfo(final IRTMCallback<String>  callback, long roomId)
    
    /**
     * 获取房间的公开信息 sync
     * @param roomId   房间id(NoNull)
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getRoomPublicInfo(long roomId){
    
    
    --Friend Interface---
    /**
     * 添加好友 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     */
    public void addFriends(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * 添加好友 sync
     * @param uids   用户id集合(NoNull)
     */
    public RTMAnswer addFriends(HashSet<Long> uids){   

    /**
     * 删除好友 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     */
    public void deleteFriends(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * 删除好友 sync
     * @param uids   用户id集合(NoNull)
     */
    public RTMAnswer deleteFriends(HashSet<Long> uids){

    /**
     * 查询自己好友 async
     * @param callback MembersCallback回调(NoNull)
     */
    public void getFriends(final IRTMCallback<HashSet<Long>> callback)

    /**
     * 查询自己好友 sync
     * @return 好友id集合
     */
    public MembersStruct getFriends(){

    /**
     * 添加黑名单 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     */
    public void addBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * 添加黑名单 sync
     * @param uids   用户id集合(NoNull)
     */
    public RTMAnswer addBlacklist(HashSet<Long> uids){

    /**
     * 删除黑名单用户 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     
     */
    public void delBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * 删除黑名单用户 sync
     * @param uids   用户id集合(NoNull)
     */
    public RTMAnswer delBlacklist(HashSet<Long> uids){

    /**
     * 查询黑名单 async
     * @param callback MembersCallback回调(NoNull)
     */
    public void getBlacklist(final IRTMCallback<HashSet<Long>> callback)

    /**
     * 查询黑名单 sync
     * @return 黑名单id集合
     */
    public MembersStruct getBlacklist(){
~~~