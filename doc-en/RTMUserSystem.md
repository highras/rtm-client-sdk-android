~~~ c++
    /** loginout
     * @param async 
     */
    public void bye(boolean async)


    /**
     *kick another connection（you can kickout another connection when mutli login） async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param endpoint  another connection address(NoNull)
     */
    public void kickout(final IRTMEmptyCallback callback, String endpoint)

    /**
     *kick another connection（you can kickout another connection when mutli login sync
     * @param endpoint  another connection address(NoNull)
     */
    public RTMAnswer kickout(String endpoint)


    /**
     *add key_value （save current connection） async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param attrs     additional message (NoNull)
     
     */
    public void addAttributes(final IRTMEmptyCallback callback, Map<String, String> attrs)

    /**
     *add key_value （save current connection） async
     * @param attrs     additional message(NoNull)
     */
    public RTMAnswer addAttributes(Map<String, String> attrs)

    /**
     * get user attributes async
     * @param callback  IRTMCallback<List<Map<String, String>>> (NoNull)
             //  the map has some default key
            //  ce：connetction's endpoint，if kickout this endpoint you can use kickout function
            //  login：login time
            //  my：current connection's attrs
     */
    public void getAttributes(final IRTMCallback<List<Map<String, String>>> callback) 

    /**
     *get user attributes async
     * @return          AttrsStruct
     */
    public AttrsStruct getAttributes()

    /**
     * add debug log
     * @param callback  IRTMEmptyCallback (notnull)
     * @param message   
     * @param attrs     additional message
     */
    public void addDebugLog(IRTMEmptyCallback callback, String message, String attrs) 

    /**
     * add debug log sync
     * @param message   
     * @param attrs     additional message
     * @return          RTMAnswer
     */
    public RTMAnswer addDebugLog(String message, String attrs)

    /**
     * add device push info async
     * @param  callback  IRTMEmptyCallback
     * @param appType     fcm(android) or apns(ios)
     * @param deviceToken   token for push
     */
    public void addDevice(IRTMEmptyCallback callback, String appType, String deviceToken) 

    /**
     * async
     * @param appType    fcm(android) or apns(ios)
     * @param deviceToken token for push
     */
    public RTMAnswer addDevice(String appType, String deviceToken)

    /**
     * async
     * @param  callback  IRTMEmptyCallback 
     * @param deviceToken  (NoNull)
     */
    public void RemoveDevice(final IRTMEmptyCallback callback, String deviceToken)

    /**
     * async
     * @param deviceToken (NoNull)
     */
    public RTMAnswer RemoveDevice(String deviceToken)


    /**
     * query users if online   async
     * @param callback IRTMCallback (NoNull)
     * @param uids    
     */
    public void getOnlineUsers(final IRTMCallback<HashSet<Long>> callback, HashSet<Long> uids)

    /**
     * query users if online   async
     *return MembersStruct
     */
    public MembersStruct getOnlineUsers(HashSet<Long> checkUids)

    /**
     * set public info and private info async
     * @param callback    IRTMEmptyCallback (NoNull)
     * @param publicInfo  
     * @param privateInfo
     */
    public void setUserInfo(IRTMEmptyCallback callback, String publicInfo, String privateInfo)

    /**
     * set public info and private info sync
     * @param publicInfo  
     * @param privateInfo 
     */
    public RTMAnswer setUserInfo(String publicInfo, String privateInfo)

    /**
     * get public info and private info async
     * @param callback DoubleStringCallback (NoNull)
     */
    public void getUserInfo(final IRTMCallback<GroupInfoStruct> callback)

    /**
     * get public info and private info sync
     * @return  GroupInfoStruct
     */
    public GroupInfoStruct getUserInfo()

    /**
     * et other users public info，max 100
     * @param callback UserAttrsCallback (NoNull)
     * @param uids    
     */
    public void getUserPublicInfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> uids)

    /**
     * get other users public info，max 100
     * @param uids       
     *return        UserPublicInfo
     */
    public UserPublicInfo getUserPublicInfo(HashSet<Long> uids)
    
    
    /**set device push options(note:this interface is set some messagetypes not push) sync
     * @param type  when type=0, set p2p options；when type=1, set group options
     * @param xid   when type =0 is mean userid；when type =1  is mean groupId
     * @param messageTypes (when messageTypes is null，all message not push)
     * @return  RTMAnswer
     */
    public RTMAnswer addDevicePushOption(@NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes)
    
    

    /**cancel device push options(corresponding with addDevicePushOption) sync
     * @param type  when type=0, mean p2p；when type=1 mean group
     * @param xid   when type =0  mean userid；when type =1 mean groupId
     * @param messageTypes  message types
     * @return  RTMAnswer
     */
    public RTMAnswer removeDevicePushOption(@NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes)
    
    

    /** sync
     * @return DevicePushOption
     */
    public DevicePushOption getDevicePushOption() 



    /**set device push options(note:this interface is set some messagetypes not push) sync
     * @param callback
     * @param type  when type=0, set p2p options；when type=1, set group options
     * @param xid   when type =0 is mean userid；when type =1  is mean groupId
     * @param messageTypes (when messageTypes is null，all message not push)
     **/
    public void addDevicePushOption(@NonNull UserInterface.IRTMEmptyCallback callback, @NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes)
    
    
    
    /**cancel the device push options() async
     * @param callback
     * @param type  when type=0, set p2p options；when type=1, set group options
     * @param xid   when type =0 is mean userid；when type =1  is mean groupId
     * @param messageTypes 
     */
    public void removeDevicePushOption(UserInterface.IRTMEmptyCallback callback, @NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes)
    
    
    /**async
     *  @param callback
     */
    public void getDevicePushOption(final UserInterface.IRTMCallback<DevicePushOption> callback)
    
~~~