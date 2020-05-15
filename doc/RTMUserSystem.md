~~~ c++
    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean kickout(final UserInterface.ErrorCodeCallback callback, String endpoint, int timeout);

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） sync
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int kickout(String endpoint, int timeout);

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean addAttributes(final UserInterface.ErrorCodeCallback callback, Map<String, String> attrs, int timeout);

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int addAttributes(Map<String, String> attrs, int timeout);

    /**
     * 获取用户属性 async
     * @param callback  用户属性回调(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getAttributes(final UserInterface.AttrsCallback callback, int timeout);

    /**
     *获取用户属性 async
     * @param attributes     客户端自定义属性对象(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getAttributes(List<Map<String, String>> attributes, int timeout);
    
    /**
     * 添加设备，应用信息 async
     * @param  callback  ErrorCodeCallback回调
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean addDevice(final UserInterface.ErrorCodeCallback callback, String appType, String deviceToken, int timeout);

    /**
     * 添加设备，应用信息 async
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int addDevice(String appType, String deviceToken, int timeout);

    /**
     * 删除设备，应用信息 async
     * @param  callback  ErrorCodeCallback回调
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean RemoveDevice(final UserInterface.ErrorCodeCallback callback, String deviceToken, int timeout);

    /**
     * 删除设备，应用信息 async
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int RemoveDevice(String deviceToken, int timeout);

    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param callback  获取value回调
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean dataGet(String key, final MessageCallback callback, int timeout);

    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) sync
     * @param key      key值
     * @param value  获取value值
     * @param timeout   超时时间(秒)
     * @return    errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int dataGet(StringBuilder value, String key, int timeout);
    
    /**
     * 设置存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param callback  ErrorCodeCallback接口回调
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean dataSet(String key, String value, final ErrorCodeCallback callback, int timeout);

    /**
     * 设置存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param value     设置的value值
     * @param timeout   超时时间(秒)
     * @return    errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int dataSet(String key, String value, int timeout);

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @param callback  ErrorCodeCallback接口回调
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean dataDelete(String key, final ErrorCodeCallback callback, int timeout);

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @param timeout   超时时间(秒)
     * @return    errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int dataDelete(String key, int timeout);

    /**
     * 查询用户是否在线   async
     *
     * @param callback MembersCallback回调(NoNull)
     * @param uids     待查询的用户id集合(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getOnlineUsers(final MembersCallback callback, HashSet<Long> uids, int timeout);

    /**
     * 查询用户是否在线   sync
     *
     * @param onlineUids 返回在线的用户id集合(NoNull)
     * @param checkUids  待查询的用户id集合(NoNull)
     * @param timeout    超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public int getOnlineUsers(HashSet<Long> onlineUids, HashSet<Long> checkUids, int timeout);

    /**
     * 设置用户自己的公开信息或者私有信息(publicInfo,privateInfo 最长 65535) async
     *
     * @param callback    ErrorCodeCallback回调(NoNull)
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean setUserInfo(ErrorCodeCallback callback, String publicInfo, String privateInfo, int timeout);
    
    
    /**
     * 设置群组的公开信息或者私有信息 sync
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int setUserInfo(String publicInfo, String privateInfo, int timeout);

    /**
     * 获取的公开信息或者私有信息 async
     *
     * @param callback DoubleStringCallback回调(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getUserInfo(final DoubleStringCallback callback, int timeout);

    /**
     * 获取公开信息或者私有信息 sync
     *
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getUserInfo(StringBuilder publicInfo, StringBuilder privateInfo, int timeout);

    /**
     * 获取其他用户的公开信息，每次最多获取100人 async
     *
     * @param callback UserAttrsCallback回调(NoNull)
     * @param uids     用户uid集合
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getUserPublicInfo(final UserAttrsCallback callback, HashSet<Long> uids, int timeout);

    /**
     * 获取其他用户的公开信息，每次最多获取100人 sync
     *
     * @param publicInfos 返回用户id 公开信息map(NoNull) 用户id会被转变成string返回
     * @param uids        用户uid集合
     * @param timeout     超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int getUserPublicInfo(Map<String, String> publicInfos, HashSet<Long> uids, int timeout);
~~~