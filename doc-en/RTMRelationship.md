~~~ java
--Group Interface--
    /**
     * add members to group async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * */
    public void addGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids) 

    /**
     * add members to group  sync
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * @return
     * */
    public RTMAnswer addGroupMembers(long groupId, HashSet<Long> uids)


    /**
     * get group public info，max 100
     * @param callback IRTMCallback<Map<String, String>> (NoNull)
     * @param gids     room ids
     */
    public void getGroupsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> gids) 
    
        /**
     * get group public info，max 100
     * @param gids         group ids
     *return              PublicInfo
     */
    public PublicInfo getGroupsOpeninfo(HashSet<Long> gids) 
    
    
    /**
     * delete members from group   async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * */
    public void deleteGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids) 
  
    /**
     * delete members from group   sync
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * */
    public RTMAnswer deleteGroupMembers(long groupId, HashSet<Long> uids){

    /**
     * get all members in group  async
     * @param callback  IRTMCallback (NoNull)
     * @param groupId    group id(NoNull)
     * */
    public void getGroupMembers(final IRTMCallback<HashSet<Long>> callback, long groupId)

    /**
     * get all members in group   sync
     * @param groupId    group id(NoNull)
     * reutn user ids
     * */
    public MembersStruct getGroupMembers(long groupId){


    /**
     * get groups where user add   async
     * @param callback  IRTMCallback (NoNull)
     * */
    public void getUserGroups(final IRTMCallback<HashSet<Long>> callback)


    /**
     * get groups where user add    sync
     * @return  MembersStruct
     * */
    public MembersStruct getUserGroups(){

    /**
     * set group public and private info async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param publicInfo    group public info
     * @param privateInfo   group private info
     */
    public void setGroupInfo(IRTMEmptyCallback callback, long groupId, String publicInfo, String privateInfo)
    
    
    /**
     * 设置group public info or private info sync
     * @param groupId    group id(NoNull)
     * @param publicInfo    group public info
     * @param privateInfo   group private info
     */
    public RTMAnswer setGroupInfo(long groupId, String publicInfo, String privateInfo){

    /**
     * get group public info or private info async
     * @param callback  IRTMCallback (NoNull)
     * @param groupId    group id(NoNull)
     */
    public void getGroupInfo(final IRTMCallback<GroupInfoStruct> callback, final long groupId)

    /**
     * get group public info and private info sync
     * @param groupId    group id(NoNull)
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getGroupInfo(long groupId)

    /**
     * get group public info async
     * @param callback  MessageCallback (NoNull)
     * @param groupId    group id(NoNull)
     */
    public void getGroupPublicInfo(final IRTMCallback<String>  callback, long groupId)
    
    
    /**
     * get group public info sync
     * @param groupId    group id(NoNull)
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getGroupPublicInfo(long groupId){
        
        //ROOM interface
    /**
     * enter room  async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param roomId   room id(NoNull)
     */
    public void enterRoom(final IRTMEmptyCallback callback, long roomId)

    /**
     * enter room  sync
     * @param roomId  room id(NoNull)
     */
    public RTMAnswer enterRoom(long roomId){

    /**
     * leave room  async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param roomId   room id(NoNull)
     */
    public void leaveRoom(IRTMEmptyCallback callback, long roomId)

    /**
     * leave room  sync
     * @param roomId  room id(NoNull)
     */
    public RTMAnswer leaveRoom(long roomId)

    /**
     * Get user's rooms    async
     * @param callback IRTMCallback (NoNull)
     */
    public void getUserRooms(final IRTMCallback<HashSet<Long>> callback)

    /**
     * Get user's rooms    sync
     * @return  MembersStruct
     * */
    public MembersStruct getUserRooms(){

    /**
     * set room  public info or private info async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param roomId   room id(NoNull)
     * @param publicInfo    room public info
     * @param privateInfo   room  private info
     */
    public void setRoomInfo(IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo)

    /**
     * 设置room  public info or private info sync
     * @param roomId   room id(NoNull)
     * @param publicInfo    room public info
     * @param privateInfo   room  private info
     */
    public RTMAnswer setRoomInfo(long roomId, String publicInfo, String privateInfo){

    /**
     * get room  public info or private info async
     * @param callback  IRTMCallback (NoNull)
     * @param roomId   room id(NoNull)
     */
    public void getRoomInfo(final IRTMCallback<GroupInfoStruct> callback, final long roomId)

    /**
     * get room  public info or private info sync
     * @param roomId   room id(NoNull)
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getRoomInfo(long roomId){

      /**
     * get rooms public info，max 100
     * @param callback IRTMCallback<Map<String, String>> (NoNull)
     * @param rids     room ids
     */
    public void getRoomsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> rids) 
    
      /**
     * get group public info，max 100
     * @param rids        room ids
     *return              PublicInfo   struct
     */
    public PublicInfo getRoomsOpeninfo(HashSet<Long> rids) 
    
    /**
     * get room  public info async
     * @param callback  IRTMCallback (NoNull)
     * @param roomId   room id(NoNull)
     */
    public void getRoomPublicInfo(final IRTMCallback<String>  callback, long roomId)
    
    /**
     * get room  public info sync
     * @param roomId   room id(NoNull)
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getRoomPublicInfo(long roomId){
    
    
    --Friend Interface---
    /**
     * async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     */
    public void addFriends(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * add friends sync
     * @param uids   user ids(NoNull)
     */
    public RTMAnswer addFriends(HashSet<Long> uids){   

    /**
     * delete friends async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     */
    public void deleteFriends(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * delete friends sync
     * @param uids   user ids(NoNull)
     */
    public RTMAnswer deleteFriends(HashSet<Long> uids){

    /**
     * query my friends async
     * @param callback MembersCallback (NoNull)
     */
    public void getFriends(final IRTMCallback<HashSet<Long>> callback)

    /**
     * query my friends sync
     * @return MembersStruct
     */
    public MembersStruct getFriends(){

    /**
     * add blacklist async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     */
    public void addBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * add blacklist sync
     * @param uids   user ids(NoNull)
     */
    public RTMAnswer addBlacklist(HashSet<Long> uids){

    /**
     * delete user from blacklist async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     */
    public void delBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids)

    /**
     * delete user from blacklist sync
     * @param uids   user ids(NoNull)
     */
    public RTMAnswer delBlacklist(HashSet<Long> uids){

    /**
     * query my blacklist async
     * @param callback MembersCallback (NoNull)
     */
    public void getBlacklist(final IRTMCallback<HashSet<Long>> callback)

    /**
     * query my blacklist sync
     * @return MembersStruct
     */
    public MembersStruct getBlacklist(){
~~~