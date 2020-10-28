~~~ c++
    /** 断开rtm
     * @param async //是否同步等待
     */
    public void bye(boolean async)


    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void kickout(final IRTMEmptyCallback callback, String endpoint, int timeout)

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） sync
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer kickout(String endpoint, int timeout)


    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void addAttributes(final IRTMEmptyCallback callback, Map<String, String> attrs, int timeout)

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer addAttributes(Map<String, String> attrs, int timeout)

    /**
     * 获取用户属性 async
     * @param callback  用户属性回调(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getAttributes(final IRTMCallback<List<Map<String, String>>> callback, int timeout) 

    /**
     *获取用户属性 async
     * @param timeout   超时时间(秒)
     * @return          List<Map<String, String>>
     */
    public AttrsStruct getAttributes(int timeout)

    /**
     * 添加debug日志
     * @param callback  IRTMEmptyCallback回调(notnull)
     * @param message   消息内容
     * @param attrs     消息属性信息
     * @param timeout   超时时间
     */
    public void addDebugLog(IRTMEmptyCallback callback, String message, String attrs, int timeout) 

    /**
     * 添加debug日志
     * @param message   消息内容
     * @param attrs     消息属性信息
     * @param timeout   超时时间
     * @return          RTMAnswer
     */
    public RTMAnswer addDebugLog(String message, String attrs, int timeout)

    /**
     * 添加设备，应用信息 async
     * @param  callback  IRTMEmptyCallback回调
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void addDevice(IRTMEmptyCallback callback, String appType, String deviceToken, int timeout) 

    /**
     * 添加设备，应用信息 async
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer addDevice(String appType, String deviceToken, int timeout)

    /**
     * 删除设备， async
     * @param  callback  IRTMEmptyCallback回调
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void RemoveDevice(final IRTMEmptyCallback callback, String deviceToken, int timeout)

    /**
     * 删除设备， async
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer RemoveDevice(String deviceToken, int timeout)


    /**
     * 查询用户是否在线   async
     * @param callback IRTMCallback回调(NoNull)
     * @param uids     待查询的用户id集合(NoNull)
     * @param timeout  超时时间（秒）
     */
    public void getOnlineUsers(final IRTMCallback<HashSet<Long>> callback, HashSet<Long> uids, int timeout)

    /**
     * 查询用户是否在线   async
     * @param timeout    超时时间（秒）
     *return 用户id列表
     */
    public MembersStruct getOnlineUsers(HashSet<Long> checkUids, int timeout)

    /**
     * 设置用户自己的公开信息或者私有信息(publicInfo,privateInfo 最长 65535) async
     * @param callback    IRTMEmptyCallback回调(NoNull)
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     */
    public void setUserInfo(IRTMEmptyCallback callback, String publicInfo, String privateInfo, int timeout)

    /**
     * 设置用户自己的公开信息或者私有信息 sync
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     */
    public RTMAnswer setUserInfo(String publicInfo, String privateInfo, int timeout)

    /**
     * 获取的用户公开信息或者私有信息 async
     * @param callback DoubleStringCallback回调(NoNull)
     * @param timeout  超时时间（秒）

     */
    public void getUserInfo(final IRTMCallback<GroupInfoStruct> callback, int timeout)

    /**
     * 获取公开信息或者私有信息 sync
     * @param timeout     超时时间（秒）
     * @return  GroupInfoStruct用户信息结构
     */
    public GroupInfoStruct getUserInfo(int timeout)

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback UserAttrsCallback回调(NoNull)
     * @param uids     用户uid集合
     * @param timeout  超时时间(秒)
     */
    public void getUserPublicInfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> uids, int timeout)

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param uids        用户uid集合
     * @param timeout     超时时间(秒)
     *return 返回用户id 公开信息map(NoNull) 用户id会被转变成string返回
     */
    public UserPublicInfo getUserPublicInfo(HashSet<Long> uids, int timeout)
~~~