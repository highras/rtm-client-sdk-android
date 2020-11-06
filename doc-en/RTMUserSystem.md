~~~ c++
    /** close rtm
     * @param async //
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
     */
    public void getAttributes(final IRTMCallback<List<Map<String, String>>> callback) 

    /**
     *get user attributes async
     * @return          List<Map<String, String>>
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
     * async
     * @param  callback  IRTMEmptyCallback 
     * @param appType     (NoNull)
     * @param deviceToken  (NoNull)
     */
    public void addDevice(IRTMEmptyCallback callback, String appType, String deviceToken) 

    /**
     * async
     * @param appType    (NoNull)
     * @param deviceToken (NoNull)
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
~~~