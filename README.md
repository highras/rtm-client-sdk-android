# FPNN RTM Android SDK #

底层基于NIO实现, 支持FPNN加密.

#### 关于依赖 ####

* [msgpack-core-0.8.16.jar](https://github.com/msgpack/msgpack-java)

#### 关于线程 ####

* 一个计时器线程`ThreadPool.getInstance().startTimerThread()`, 负责超时检测/安全检查
    * 默认实现`Executors.newScheduledThreadPool(1)`, 构造`RTMClient`时可以选择是否启用该线程
    * 如果已有计时器, `NIOCore.getInstance().checkSecond()` 周期性调用该方法，以进行超时检查（建议频率1s）

* 一个线程池, 接口`ThreadPool.IThreadPool`
    * 默认实现`Executors.newFixedThreadPool(FPConfig.MAX_THREAD_COUNT)`
    * 如需自己管理线程，实现该接口并注册线程池`ThreadPool.getInstance().setPool(IThreadPool value)`

* 不要阻塞事件触发和回调, 否则线程池将被耗尽

#### 一个例子 ####

```java

// 创建Client
RTMClient client = new RTMClient(
    "rtm-nx-front.ifunplus.cn:13325",
    1000012,
    654321,
    "5C65CD872903AAB37211EC468B4A1364",
    null,
    false,
    true,
    20 * 1000,
    true
);

// 添加监听
client.getEvent().addListener("login", listener);
client.getEvent().addListener("close", listener);
client.getEvent().addListener("error", listener);

// push service
client.getProcessor().getEvent().addListener(RTMConfig.SERVER_PUSH.recvPing, new FPEvent.IListener() {
    @Override
    public void fpEvent(FPEvent event) {
        System.out.println("\n[PUSH] ".concat(event.getType()).concat(":"));
        System.out.println(event.getPayload().toString());
    }
});

FPEvent.IListener listener = new FPEvent.IListener() {

    @Override
    public void fpEvent(FPEvent event) {

        switch (event.getType()) {
            case "login":
                if (event.getException() != null) {
                    System.out.println("Auth Fail!");
                    break;
                }

                System.out.println("Authed!");

                // 发送消息
                client.sendMessage(778899, (byte) 8, "hello !", "", 5 * 1000, new FPCallback.ICallback() {
                    @Override
                    public void callback(CallbackData cbd) {
                        Object obj = cbd.getPayload();
                        if (obj != null) {
                            Map payload = (Map) obj;
                            System.out.println("\n[DATA] sendMessage:");
                            System.out.println(payload.toString());
                        } else {
                            System.err.println("\n[ERR] sendMessage:");
                            System.err.println(cbd.getException().getMessage());
                        }
                    }
                });
                break;
            case "close":
                System.out.println("Closed! retry:" + event.hasRetry());
                break;
            case "error":
                event.getException().printStackTrace();
                break;
        }
    }
};

// 开启连接
client.login(null, false);

```

#### 测试 ####

参考`TestActivity`:

```java

// case 1
baseTest();

// case 2
// asyncStressTest();

// case 3
// singleClientConcurrentTest();
```

#### Events ####

* `event`:
    * `login`: 登陆
        * `exception`: **(Exception)** auth失败, token失效需重新获取
        * `payload`: **(Map)** 当前连接的RTMGate地址, 可在本地缓存, 下次登陆可使用该地址以加速登陆过程, **每次登陆成功需更新本地缓存**
    * `error`: 发生异常
        * `exception`: **(Exception)**
    * `close`: 连接关闭
        * `retry`: **(boolean)** 是否自动重连

#### PushService ####

请参考 `RTMConfig.SERVER_PUSH` 成员

* `kickout`: RTMGate主动断开
    * `data`: **(Map)**

* `kickoutroom`: RTMGate主动从Room移除
    * `data.rid`: **(long)** Room id

* `ping`: RTMGate主动ping
    * `data`: **(Map)**

* `pushmsg`: RTMGate主动推送P2P消息
    * `data.from`: **(long)** 发送者 id
    * `data.mtype`: **(byte)** 消息类型
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 发送时附加的自定义内容

* `pushgroupmsg`: RTMGate主动推送Group消息
    * `data.from`: **(long)** 发送者 id
    * `data.gid`: **(long)** Group id
    * `data.mtype`: **(byte)** 消息类型
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 发送时附加的自定义内容

* `pushroommsg`: RTMGate主动推送Room消息
    * `data.from`: **(long)** 发送者 id
    * `data.rid`: **(long)** Room id
    * `data.mtype`: **(byte)** 消息类型
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 发送时附加的自定义内容

* `pushbroadcastmsg`: RTMGate主动推送广播消息
    * `data.from`: **(long)** 发送者 id
    * `data.mtype`: **(byte)** 消息类型
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 发送时附加的自定义内容

* `transmsg`: RTMGate主动推送翻译消息(P2P)
    * `data.from`: **(long)** 发送者 id
    * `data.mid`: **(long)** 翻译后的消息 id, 当前链接会话内唯一
    * `data.omid`: **(long)** 原始消息的消息 id
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 翻译后的消息内容

* `transgroupmsg`: RTMGate主动推送翻译消息(Group)
    * `data.from`: **(long)** 发送者 id
    * `data.gid`: **(long)** Group id
    * `data.mid`: **(long)** 翻译后的消息 id, 当前链接会话内唯一
    * `data.omid`: **(long)** 原始消息的消息 id
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 翻译后的消息内容
    
* `transroommsg`: RTMGate主动推送翻译消息(Room)
    * `data.from`: **(long)** 发送者 id
    * `data.rid`: **(long)** Room id
    * `data.mid`: **(long)** 翻译后的消息 id, 当前链接会话内唯一
    * `data.omid`: **(long)** 原始消息的消息 id
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 翻译后的消息内容
    
* `transbroadcastmsg`: RTMGate主动推送翻译消息(广播)
    * `data.from`: **(long)** 发送者 id
    * `data.mid`: **(long)** 翻译后的消息 id, 当前链接会话内唯一
    * `data.omid`: **(long)** 原始消息的消息 id
    * `data.msg`: **(String)** 消息内容
    * `data.attrs`: **(String)** 翻译后的消息内容

* `pushunread`: RTMGate主动推送消息(未读)
    * `data.p2p`: **(List<Long>)** 有未读消息的发送者 id 列表
    * `data.group`: **(List<Long>)** 有未读消息的Group id 列表
    * `data.bc`: **(boolean)** `true`代表有未读广播消息

* `pushfile`: RTMGate主动推送P2P文件
    * `data.from`: **(long)** 发送者 id
    * `data.mtype`: **(byte)** 消息类型
    * `data.ftype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 文件获取地址(url)
    * `data.attrs`: **(String)** 发送时附加的自定义内容

* `pushgroupfile`: RTMGate主动推送Group文件
    * `data.from`: **(long)** 发送者 id
    * `data.gid`: **(long)** Group id
    * `data.mtype`: **(byte)** 消息类型
    * `data.ftype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 文件获取地址(url)
    * `data.attrs`: **(String)** 发送时附加的自定义内容

* `pushroomfile`: RTMGate主动推送Room文件
    * `data.from`: **(long)** 发送者 id
    * `data.rid`: **(long)** Room id
    * `data.mtype`: **(byte)** 消息类型
    * `data.ftype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 文件获取地址(url)
    * `data.attrs`: **(String)** 发送时附加的自定义内容

* `pushbroadcastfile`: RTMGate主动推送广播文件
    * `data.from`: **(long)** 发送者 id
    * `data.mtype`: **(byte)** 消息类型
    * `data.ftype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
    * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
    * `data.msg`: **(String)** 文件获取地址(url)
    * `data.attrs`: **(String)** 发送时附加的自定义内容

#### API ####

* `constructor(String dispatch, int pid, long uid, String token, String version, boolean recvUnreadMsg, boolean reconnect, int timeout, boolean startTimerThread)`: 构造RTMClient
    * `dispatch`: **(String)** Dispatch服务地址, RTM提供
    * `pid`: **(int)** 应用编号, RTM提供
    * `uid`: **(long)** 用户ID
    * `token`: **(String)** 用户登录Token, RTM提供
    * `version`: **(String)** 服务器版本号, RTM提供
    * `recvUnreadMsg`: **(boolean)** 是否接收未读消息状态信息
    * `reconnect`: **(boolean)** 是否自动重连
    * `timeout`: **(int)** 超时时间(ms), 默认: `30 * 1000`
    * `startTimerThread`: **(boolean)** 是否开启计时器线程 (负责超时检测/安全检查)

* `getProcessor`: **(FPProcessor)** 监听PushService的句柄

* `login(String endpoint, boolean ipv6)`: 连接并登陆
    * `endpoint`: **(String)** RTMGate服务地址, 由Dispatch服务获取, 或由RTM提供
    * `ipv6`: **(boolean)** 是否为IPV6地址格式

* `login(String curve, byte[] derKey, String endpoint, boolean ipv6)`: 连接并登陆(加密)
    * `curve`: **(String)** 加密协议
    * `derKey`: **(byte[])** 加密公钥, 秘钥文件RTM提供
    * `endpoint`: **(String)** RTMGate服务地址, 由Dispatch服务获取, 或由RTM提供
    * `ipv6`: **(boolean)** 是否为IPV6地址格式

* `sendMessage(long to, byte mtype, String msg, String attrs, int timeout, FPCallback.ICallback callback)`: 发送消息
    * `to`: **(long)** 接收方uid
    * `mtype`: **(byte)** 消息类型
    * `msg`: **(String)** 消息内容
    * `attrs`: **(String)** 消息附加信息, 没有可传`""`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `sendMessages(List<Long> tos, byte mtype, String msg, String attrs, int timeout, FPCallback.ICallback callback)`: 发送多人消息
    * `tos`: **(List<Long>)** 接收方uids
    * `mtype`: **(byte)** 消息类型
    * `msg`: **(String)** 消息内容
    * `attrs`: **(String)** 消息附加信息, 没有可传`""`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `sendGroupMessage(long gid, byte mtype, String msg, String attrs, int timeout, FPCallback.ICallback callback)`: 发送group消息
    * `gid`: **(long)** group id
    * `mtype`: **(byte)** 消息类型
    * `msg`: **(String)** 消息内容
    * `attrs`: **(String)** 消息附加信息, 可传`""`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `sendRoomMessage(long rid, byte mtype, String msg, String attrs, int timeout, FPCallback.ICallback callback)`: 发送room消息
    * `rid`: **(long)** room id
    * `mtype`: **(byte)** 消息类型
    * `msg`: **(String)** 消息内容
    * `attrs`: **(String)** 消息附加信息, 可传`""`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `close()`: 断开连接

* `addVariables(Map dict, int timeout, FPCallback.ICallback callback)`: 添加属性
    * `dict`: **(Map)** 参数字典, 值必须是`string`类型, 具体参数参见 RTM 文档。
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `addFriends(List<Long> friends, int timeout, FPCallback.ICallback callback)`: 添加好友
    * `friends`: **(List<Long>)** 多个好友 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `deleteFriends(List<Long> friends, int timeout, FPCallback.ICallback callback)`: 删除好友
    * `friends`: **(List<Long>)** 多个好友 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `getFriends(int timeout, FPCallback.ICallback callback)`: 获取好友
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(List<Long>)**

* `addGroupMembers(long gid, List<Long> uids, int timeout, FPCallback.ICallback callback)`: 添加group成员
    * `gid`: **(long)** group id
    * `uids`: **(List<Long>)** 多个用户 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `deleteGroupMembers(long gid, List<Long> uids, int timeout, FPCallback.ICallback callback)`:  删除group成员
    * `gid`: **(long)** group id
    * `uids`: **(List<Long>)** 多个用户 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `getGroupMembers(long gid, int timeout, FPCallback.ICallback callback)`: 获取group成员
    * `gid`: **(long)** group id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(List<Long>)**

* `getUserGroups(int timeout, FPCallback.ICallback callback)`: 获取用户所在的Group
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(List<Long>)**

* `enterRoom(long rid, int timeout, FPCallback.ICallback callback)`: 进入房间
    * `rid`: **(long)** 房间 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**
    
* `leaveRoom(long rid, int timeout, FPCallback.ICallback callback)`: 离开房间
    * `rid`: **(long)** 房间 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `getUserRooms(int timeout, FPCallback.ICallback callback)`: 获取用户所在的Room
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(List<Long>)**

* `getOnlineUsers(List<Long> uids, int timeout, FPCallback.ICallback callback)`: 获取在线用户
    * `uids`: **(List<Long>)** 多个用户 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(List<Long>)**

* `checkUnreadMessage(int timeout, FPCallback.ICallback callback)`: 获取离线消息／未读消息数目
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `getGroupMessage(long gid, int num, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback)`: 获取Group历史消息
    * `gid`: **(long)** Group id
    * `num`: **(int)** 获取数量, **一次最多获取10条**
    * `desc`: **(boolean)** `true`: 降序排列, `false`: 升序排列
    * `page`: **(int)** 翻页索引, 基数为 0
    * `localmid`: **(long)** 本地保存消息的 mid, 没有传递 -1, 服务器将返回此 mid 之后的新消息
    * `localid`: **(long)** 本地保存的上一轮获取到的消息的最大消息 id, 没有传递 -1, 服务器将返回大于这个id的所有消息, 翻页时, 本参数传一样的值
    * `mtypes`: **(List<Byte>)** 关心的消息类型列表, 空代表所有类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map<num:int16, maxid:long, msgs:List<GroupMsg>>)**
            * `GroupMsg.id` **(long)**
            * `GroupMsg.from` **(long)**
            * `GroupMsg.mtype` **(byte)**
            * `GroupMsg.ftype` **(byte)**
            * `GroupMsg.mid` **(long)**
            * `GroupMsg.msg` **(String)**
            * `GroupMsg.attrs` **(String)**
            * `GroupMsg.mtime` **(int)**

* `getRoomMessage(long rid, int num, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback)`: 获取Room历史消息
    * `rid`: **(long)** Room id
    * `num`: **(int)** 获取数量, **一次最多获取10条**
    * `desc`: **(boolean)** `true`: 降序排列, `false`: 升序排列
    * `page`: **(int)** 翻页索引, 基数为 0
    * `localmid`: **(long)** 本地保存消息的 mid, 没有传递 -1, 服务器将返回此 mid 之后的新消息
    * `localid`: **(long)** 本地保存的上一轮获取到的消息的最大消息 id, 没有传递 -1, 服务器将返回大于这个id的所有消息, 翻页时, 本参数传一样的值
    * `mtypes`: **(List<Byte>)** 关心的消息类型列表, 空代表所有类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map<num:int16, maxid:long, msgs:List<RoomMsg>>)**
            * `RoomMsg.id` **(long)**
            * `RoomMsg.from` **(long)**
            * `RoomMsg.mtype` **(byte)**
            * `RoomMsg.ftype` **(byte)**
            * `RoomMsg.mid` **(long)**
            * `RoomMsg.msg` **(String)**
            * `RoomMsg.attrs` **(String)**
            * `RoomMsg.mtime` **(int)**

* `getBroadcastMessage(int num, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback)`: 获取广播历史消息
    * `num`: **(int)** 获取数量, **一次最多获取10条**
    * `desc`: **(boolean)** `true`: 降序排列, `false`: 升序排列
    * `page`: **(int)** 翻页索引, 基数为 0
    * `localmid`: **(long)** 本地保存消息的 mid, 没有传递 -1, 服务器将返回此 mid 之后的新消息
    * `localid`: **(long)** 本地保存的上一轮获取到的消息的最大消息 id, 没有传递 -1, 服务器将返回大于这个id的所有消息, 翻页时, 本参数传一样的值
    * `mtypes`: **(List<Byte>)** 关心的消息类型列表, 空代表所有类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map<num:int16, maxid:long, msgs:List<BroadcastMsg>>)**
            * `BroadcastMsg.id` **(long)**
            * `BroadcastMsg.from` **(long)**
            * `BroadcastMsg.mtype` **(byte)**
            * `BroadcastMsg.ftype` **(byte)**
            * `BroadcastMsg.mid` **(long)**
            * `BroadcastMsg.msg` **(String)**
            * `BroadcastMsg.attrs` **(String)**
            * `BroadcastMsg.mtime` **(int)**

* `getP2PMessage(long peeruid, int num, int direction, boolean desc, int page, long localmid, long localid, List<Byte> mtypes, int timeout, FPCallback.ICallback callback)`: 获取P2P历史消息
    * `peeruid`: **(long)** 发送者 id
    * `num`: **(int)** 获取数量, **一次最多获取10条**
    * `direction`: **(int)** `0`: sent + recv, `1`: sent, `2`: recv
    * `desc`: **(boolean)** `true`: 降序排列, `false`: 升序排列
    * `page`: **(int)** 翻页索引, 基数为 0
    * `localmid`: **(long)** 本地保存消息的 mid, 没有传递 -1, 服务器将返回此 mid 之后的新消息
    * `localid`: **(long)** 本地保存的上一轮获取到的消息的最大消息 id, 没有传递 -1, 服务器将返回大于这个id的所有消息, 翻页时, 本参数传一样的值
    * `mtypes`: **(List<Byte>)** 关心的消息类型列表, 空代表所有类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map<num:int16, maxid:long, msgs:List<P2PMsg>>)**
            * `P2PMsg.id` **(long)**
            * `P2PMsg.direction` **(byte)**
            * `P2PMsg.mtype` **(byte)**
            * `P2PMsg.ftype` **(byte)**
            * `P2PMsg.mid` **(long)**
            * `P2PMsg.msg` **(String)**
            * `P2PMsg.attrs` **(String)**
            * `P2PMsg.mtime` **(int)**

* `addDevice(String apptype, String devicetoken, int timeout, FPCallback.ICallback callback)`: 添加设备, 应用信息
    * `apptype`: **(String)** 应用信息
    * `devicetoken`: **(String)** 设备信息
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `removeDevice(String devicetoken, int timeout, FPCallback.ICallback callback)`: 删除设备, 应用信息
    * `devicetoken`: **(String)** 设备信息
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `setTranslationLanguage(String targetLanguage, int timeout, FPCallback.ICallback callback)`: 设置自动翻译的默认目标语言类型, 如果 targetLanguage 为空字符串, 则取消自动翻译
    * `targetLanguage`: **(String)** 翻译的目标语言类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `translate(String originalMessage, String originalLanguage, String targetLanguage, int timeout, FPCallback.ICallback callback)`: 翻译消息
    * `originalMessage`: **(String)** 待翻译的原始消息
    * `originalLanguage`: **(String)** 待翻译的消息的语言类型, 可为`null` 
    * `targetLanguage`: **(String)** 本次翻译的目标语言类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `setGeo(double lat, double lng, int timeout, FPCallback.ICallback callback)`: 设置位置
    * `lat`: **(double)** 纬度
    * `lng`: **(double)** 经度
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `getGeo(int timeout, FPCallback.ICallback callback)`: 获取位置
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `getGeos(List<Long> uids, int timeout, FPCallback.ICallback callback)`: 获取位置
    * `uids`: **(List<Long>)** 多个用户 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(List<ArrayList>)**

* `sendFile(byte mtype, long to, byte[] fileBytes, int timeout, FPCallback.ICallback callback)`: 发送文件
    * `mtype`: **(byte)** 消息类型
    * `to`: **(long)** 接收者 id
    * `fileBytes`: **(byte[])** 要发送的文件
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `sendFiles(byte mtype, List<Long> tos, byte[] fileBytes, int timeout, FPCallback.ICallback callback)`: 发送多人文件
    * `mtype`: **(byte)** 消息类型
    * `tos`: **(List<Long>)** 接收者 id
    * `fileBytes`: **(byte[])** 要发送的文件
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `sendGroupFile(byte mtype, long gid, byte[] fileBytes, int timeout, FPCallback.ICallback callback)`: 发送文件
    * `mtype`: **(byte)** 消息类型
    * `gid`: **(long)** Group id
    * `fileBytes`: **(byte[])** 要发送的文件
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**

* `sendRoomFile(byte mtype, long rid, byte[] fileBytes, int timeout, FPCallback.ICallback callback)`: 发送文件
    * `mtype`: **(byte)** 消息类型
    * `rid`: **(long)** Room id
    * `fileBytes`: **(byte[])** 要发送的文件
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `exception`: **(Exception)**
        * `payload`: **(Map)**
