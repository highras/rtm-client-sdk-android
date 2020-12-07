~~~ c++
    /** 用户下线
     * @param async //是否同步等待
     */
    public void bye(boolean async)


    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param endpoint  另一个用户的地址(NoNull)(用户的地址可以通过 getAttributes拿到)
     */
    public void kickout(final IRTMEmptyCallback callback, String endpoint)

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） sync
     * @param endpoint  另一个用户的地址(NoNull)(用户的地址可以通过 getAttributes拿到)
     */
    public RTMAnswer kickout(String endpoint)


    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param attrs     客户端自定义属性值(NoNull)
     */
    public void addAttributes(final IRTMEmptyCallback callback, Map<String, String> attrs)

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param attrs     客户端自定义属性值(NoNull)
     */
    public RTMAnswer addAttributes(Map<String, String> attrs)

    /**
     * 获取用户属性 async
     * @param callback  用户属性回调 其中map的key
     *                  map中自动添加如下几个参数：
     *                  ce：链接的endpoint，需要让其下线可以调用kickout
     *                  login：登录时间，utc时间戳
     *                  my：当前链接的attrs
     */
    public void getAttributes(final IRTMCallback<List<Map<String, String>>> callback) 

    /**
     *获取用户属性 async
     * @return         AttrsStruct结构
     */
    public AttrsStruct getAttributes()

    /**
     * 添加debug日志
     * @param callback  IRTMEmptyCallback回调(notnull)
     * @param message   消息内容
     * @param attrs     消息属性信息
     */
    public void addDebugLog(IRTMEmptyCallback callback, String message, String attrs) 

    /**
     * 添加debug日志
     * @param message   消息内容
     * @param attrs     消息属性信息
     * @return          RTMAnswer
     */
    public RTMAnswer addDebugLog(String message, String attrs)

    /**
     * 添加设备，应用信息 async
     * @param  callback  IRTMEmptyCallback回调
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     */
    public void addDevice(IRTMEmptyCallback callback, String appType, String deviceToken) 

    /**
     * 添加设备，应用信息 async
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     */
    public RTMAnswer addDevice(String appType, String deviceToken)

    /**
     * 删除设备， async
     * @param  callback  IRTMEmptyCallback回调
     * @param deviceToken   设备token(NoNull)
     */
    public void RemoveDevice(final IRTMEmptyCallback callback, String deviceToken)

    /**
     * 删除设备， async
     * @param deviceToken   设备token(NoNull)
     */
    public RTMAnswer RemoveDevice(String deviceToken)


    /**
     * 查询用户是否在线   async
     * @param callback IRTMCallback回调(NoNull)
     * @param uids     待查询的用户id集合(NoNull)
     ）
     */
    public void getOnlineUsers(final IRTMCallback<HashSet<Long>> callback, HashSet<Long> uids)

    /**
     * 查询用户是否在线   async
     *return 用户id列表
     */
    public MembersStruct getOnlineUsers(HashSet<Long> checkUids)

    /**
     * 设置用户自己的公开信息或者私有信息(publicInfo,privateInfo 最长 65535) async
     * @param callback    IRTMEmptyCallback回调(NoNull)
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     */
    public void setUserInfo(IRTMEmptyCallback callback, String publicInfo, String privateInfo)

    /**
     * 设置用户自己的公开信息或者私有信息 sync
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     */
    public RTMAnswer setUserInfo(String publicInfo, String privateInfo)

    /**
     * 获取的用户公开信息或者私有信息 async
     * @param callback DoubleStringCallback回调(NoNull)
     ）
     */
    public void getUserInfo(final IRTMCallback<GroupInfoStruct> callback)

    /**
     * 获取公开信息或者私有信息 sync
     * @return  GroupInfoStruct用户信息结构
     */
    public GroupInfoStruct getUserInfo()

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback UserAttrsCallback回调(NoNull)
     * @param uids     用户uid集合
     */
    public void getUserPublicInfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> uids)

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param uids        用户uid集合
     *return 返回用户id 公开信息map(NoNull) 用户id会被转变成string返回
     */
    public UserPublicInfo getUserPublicInfo(HashSet<Long> uids)
~~~