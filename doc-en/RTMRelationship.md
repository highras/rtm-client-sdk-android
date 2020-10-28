~~~ java
--Group Interface--
    /**
     * add members to group async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * @param timeout   timeout（seconds）
     * */
    public void addGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids, int timeout) 

    /**
     * add members to group  sync
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * @param timeout   timeout（seconds）
     * @return
     * */
    public RTMAnswer addGroupMembers(long groupId, HashSet<Long> uids, int timeout)


    /**
     * get group public info，max 100
     * @param callback IRTMCallback<Map<String, String>> (NoNull)
     * @param gids     room ids
     * @param timeout  timeoout(seconds)
     */
    public void getGroupsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> gids, int timeout) 
    
        /**
     * get group public info，max 100
     * @param gids         group ids
     * @param timeout     timeoout(seconds)
     *return              PublicInfo
     */
    public PublicInfo getGroupsOpeninfo(HashSet<Long> gids, int timeout) 
    
    
    /**
     * delete members from group   async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * @param timeout   timeout（seconds）
     * */
    public void deleteGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids, int timeout) 
  
    /**
     * delete members from group   sync
     * @param groupId    group id(NoNull)
     * @param uids      user ids(NoNull)
     * @param timeout   timeout（seconds）
     * */
    public RTMAnswer deleteGroupMembers(long groupId, HashSet<Long> uids, int timeout){

    /**
     * get all members in group  async
     * @param callback  IRTMCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param timeout   timeout（seconds）
     * */
    public void getGroupMembers(final IRTMCallback<HashSet<Long>> callback, long groupId, int timeout)

    /**
     * get all members in group   sync
     * @param groupId    group id(NoNull)
     * @param timeout   timeout（seconds）
     * reutn user ids
     * */
    public MembersStruct getGroupMembers(long groupId, int timeout){


    /**
     * get groups where user add   async
     * @param callback  IRTMCallback (NoNull)
     * @param timeout   timeout（seconds）
     * */
    public void getUserGroups(final IRTMCallback<HashSet<Long>> callback, int timeout)


    /**
     * get groups where user add    sync
     * @param timeout   timeout（seconds）
     * @return  MembersStruct
     * */
    public MembersStruct getUserGroups(int timeout){

    /**
     * set group public and private info async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param publicInfo    group public info
     * @param privateInfo   group private info
     * @param timeout   timeout（seconds）
     */
    public void setGroupInfo(IRTMEmptyCallback callback, long groupId, String publicInfo, String privateInfo, int timeout)
    
    
    /**
     * 设置group public info or private info sync
     * @param groupId    group id(NoNull)
     * @param publicInfo    group public info
     * @param privateInfo   group private info
     * @param timeout   timeout（seconds）
     */
    public RTMAnswer setGroupInfo(long groupId, String publicInfo, String privateInfo, int timeout){

    /**
     * get group public info or private info async
     * @param callback  IRTMCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param timeout   timeout（seconds）
     */
    public void getGroupInfo(final IRTMCallback<GroupInfoStruct> callback, final long groupId, int timeout)

    /**
     * get group public info and private info sync
     * @param groupId    group id(NoNull)
     * @param timeout   timeout（seconds）
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getGroupInfo(long groupId, int timeout)

    /**
     * get group public info async
     * @param callback  MessageCallback (NoNull)
     * @param groupId    group id(NoNull)
     * @param timeout   timeout（seconds）
     */
    public void getGroupPublicInfo(final IRTMCallback<String>  callback, long groupId, int timeout)
    
    
    /**
     * get group public info sync
     * @param groupId    group id(NoNull)
     * @param timeout   timeout（seconds）
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getGroupPublicInfo(long groupId, int timeout){
        
        //ROOM interface
    /**
     * enter room  async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param roomId   room id(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void enterRoom(final IRTMEmptyCallback callback, long roomId, int timeout)

    /**
     * enter room  sync
     * @param roomId  room id(NoNull)
     * @param timeout timeoout(seconds)
     */
    public RTMAnswer enterRoom(long roomId, int timeout){

    /**
     * leave room  async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param roomId   room id(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void leaveRoom(IRTMEmptyCallback callback, long roomId, int timeout)

    /**
     * leave room  sync
     * @param roomId  room id(NoNull)
     * @param timeout timeoout(seconds)
     */
    public RTMAnswer leaveRoom(long roomId, int timeout)

    /**
     * Get user's rooms    async
     * @param callback IRTMCallback (NoNull)
     * @param timeout  timeout（seconds）
     */
    public void getUserRooms(final IRTMCallback<HashSet<Long>> callback, int timeout)

    /**
     * Get user's rooms    sync
     * @param timeout   timeout（seconds）
     * @return  MembersStruct
     * */
    public MembersStruct getUserRooms(int timeout){

    /**
     * set room  public info or private info async
     * @param callback  IRTMEmptyCallback (NoNull)
     * @param roomId   room id(NoNull)
     * @param publicInfo    room public info
     * @param privateInfo   room  private info
     * @param timeout   timeout（seconds）
     */
    public void setRoomInfo(IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo, int timeout)

    /**
     * 设置room  public info or private info sync
     * @param roomId   room id(NoNull)
     * @param publicInfo    room public info
     * @param privateInfo   room  private info
     * @param timeout   timeout（seconds）
     */
    public RTMAnswer setRoomInfo(long roomId, String publicInfo, String privateInfo, int timeout){

    /**
     * get room  public info or private info async
     * @param callback  IRTMCallback (NoNull)
     * @param roomId   room id(NoNull)
     * @param timeout   timeout（seconds）
     */
    public void getRoomInfo(final IRTMCallback<GroupInfoStruct> callback, final long roomId, int timeout)

    /**
     * get room  public info or private info sync
     * @param roomId   room id(NoNull)
     * @param timeout   timeout（seconds）
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getRoomInfo(long roomId, int timeout){

      /**
     * get rooms public info，max 100
     * @param callback IRTMCallback<Map<String, String>> (NoNull)
     * @param rids     room ids
     * @param timeout  timeoout(seconds)
     */
    public void getRoomsOpeninfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> rids, int timeout) 
    
      /**
     * get group public info，max 100
     * @param rids        room ids
     * @param timeout     timeoout(seconds)
     *return              PublicInfo   struct
     */
    public PublicInfo getRoomsOpeninfo(HashSet<Long> rids, int timeout) 
    
    /**
     * get room  public info async
     * @param callback  IRTMCallback (NoNull)
     * @param roomId   room id(NoNull)
     * @param timeout   timeout（seconds）
     */
    public void getRoomPublicInfo(final IRTMCallback<String>  callback, long roomId, int timeout)
    
    /**
     * get room  public info sync
     * @param roomId   room id(NoNull)
     * @param timeout   timeout（seconds）
     * @return  GroupInfoStruct  struct
     */
    public GroupInfoStruct getRoomPublicInfo(long roomId, int timeout){
    
    
    --Friend Interface---
    /**
     * async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void addFriends(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * add friends sync
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public RTMAnswer addFriends(HashSet<Long> uids, int timeout){   

    /**
     * delete friends async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void deleteFriends(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * delete friends sync
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public RTMAnswer deleteFriends(HashSet<Long> uids, int timeout){

    /**
     * query my friends async
     * @param callback MembersCallback (NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void getFriends(final IRTMCallback<HashSet<Long>> callback, int timeout)

    /**
     * query my friends sync
     * @param timeout  timeoout(seconds)
     * @return MembersStruct
     */
    public MembersStruct getFriends(int timeout){

    /**
     * add blacklist async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void addBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * add blacklist sync
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public RTMAnswer addBlacklist(HashSet<Long> uids, int timeout){

    /**
     * delete user from blacklist async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void delBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout)

    /**
     * delete user from blacklist sync
     * @param uids   user ids(NoNull)
     * @param timeout  timeoout(seconds)
     */
    public RTMAnswer delBlacklist(HashSet<Long> uids, int timeout){

    /**
     * query my blacklist async
     * @param callback MembersCallback (NoNull)
     * @param timeout  timeoout(seconds)
     */
    public void getBlacklist(final IRTMCallback<HashSet<Long>> callback, int timeout)

    /**
     * query my blacklist sync
     * @param timeout  timeoout(seconds)
     * @return MembersStruct
     */
    public MembersStruct getBlacklist(int timeout){
~~~