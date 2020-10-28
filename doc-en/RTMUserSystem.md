~~~ c++
    /** close rtm
     * @param async //
     */
    public void bye(boolean async)


    /**
     *kick another connection（you can kickout another connection when mutli login） async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param endpoint  another connection address(NoNull)
     * @param timeout    timeoout(seconds)
     */
    public void kickout(final IRTMEmptyCallback callback, String endpoint, int timeout)

    /**
     *kick another connection（you can kickout another connection when mutli login sync
     * @param endpoint  another connection address(NoNull)
     * @param timeout    timeoout(seconds)
     */
    public RTMAnswer kickout(String endpoint, int timeout)


    /**
     *add key_value （save current connection） async
     * @param callback IRTMEmptyCallback (NoNull)
     * @param attrs     additional message (NoNull)
     * @param timeout    timeoout(seconds)
     */
    public void addAttributes(final IRTMEmptyCallback callback, Map<String, String> attrs, int timeout)

    /**
     *add key_value （save current connection） async
     * @param attrs     additional message(NoNull)
     * @param timeout    timeoout(seconds)
     */
    public RTMAnswer addAttributes(Map<String, String> attrs, int timeout)

    /**
     * get user attributes async
     * @param callback  IRTMCallback<List<Map<String, String>>> (NoNull)
     * @param timeout    timeoout(seconds)
     */
    public void getAttributes(final IRTMCallback<List<Map<String, String>>> callback, int timeout) 

    /**
     *get user attributes async
     * @param timeout    timeoout(seconds)
     * @return          List<Map<String, String>>
     */
    public AttrsStruct getAttributes(int timeout)

    /**
     * add debug log
     * @param callback  IRTMEmptyCallback (notnull)
     * @param message   
     * @param attrs     additional message
     * @param timeout   timeoout(seconds)
     */
    public void addDebugLog(IRTMEmptyCallback callback, String message, String attrs, int timeout) 

    /**
     * add debug log sync
     * @param message   
     * @param attrs     additional message
     * @param timeout   timeoout(seconds)
     * @return          RTMAnswer
     */
    public RTMAnswer addDebugLog(String message, String attrs, int timeout)

    /**
     * async
     * @param  callback  IRTMEmptyCallback 
     * @param appType     (NoNull)
     * @param deviceToken  (NoNull)
     * @param timeout    timeoout(seconds)
     */
    public void addDevice(IRTMEmptyCallback callback, String appType, String deviceToken, int timeout) 

    /**
     * async
     * @param appType    (NoNull)
     * @param deviceToken (NoNull)
     * @param timeout    timeoout(seconds)
     */
    public RTMAnswer addDevice(String appType, String deviceToken, int timeout)

    /**
     * async
     * @param  callback  IRTMEmptyCallback 
     * @param deviceToken  (NoNull)
     * @param timeout    timeoout(seconds)
     */
    public void RemoveDevice(final IRTMEmptyCallback callback, String deviceToken, int timeout)

    /**
     * async
     * @param deviceToken (NoNull)
     * @param timeout    timeoout(seconds)
     */
    public RTMAnswer RemoveDevice(String deviceToken, int timeout)


    /**
     * query users if online   async
     * @param callback IRTMCallback (NoNull)
     * @param uids    
     * @param timeout  timeoout(seconds)
     */
    public void getOnlineUsers(final IRTMCallback<HashSet<Long>> callback, HashSet<Long> uids, int timeout)

    /**
     * query users if online   async
     * @param timeout    timeoout(seconds)
     *return MembersStruct
     */
    public MembersStruct getOnlineUsers(HashSet<Long> checkUids, int timeout)

    /**
     * set public info and private info async
     * @param callback    IRTMEmptyCallback (NoNull)
     * @param publicInfo  
     * @param privateInfo
     * @param timeout     timeoout(seconds)
     */
    public void setUserInfo(IRTMEmptyCallback callback, String publicInfo, String privateInfo, int timeout)

    /**
     * set public info and private info sync
     * @param publicInfo  
     * @param privateInfo 
     * @param timeout     timeoout(seconds)
     */
    public RTMAnswer setUserInfo(String publicInfo, String privateInfo, int timeout)

    /**
     * get public info and private info async
     * @param callback DoubleStringCallback (NoNull)
     * @param timeout  timeoout(seconds)

     */
    public void getUserInfo(final IRTMCallback<GroupInfoStruct> callback, int timeout)

    /**
     * get public info and private info sync
     * @param timeout     timeoout(seconds)
     * @return  GroupInfoStruct
     */
    public GroupInfoStruct getUserInfo(int timeout)

    /**
     * et other users public info，max 100
     * @param callback UserAttrsCallback (NoNull)
     * @param uids    
     * @param timeout   timeoout(seconds)
     */
    public void getUserPublicInfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> uids, int timeout)

    /**
     * get other users public info，max 100
     * @param uids       
     * @param timeout      timeoout(seconds)
     *return        UserPublicInfo
     */
    public UserPublicInfo getUserPublicInfo(HashSet<Long> uids, int timeout)
~~~