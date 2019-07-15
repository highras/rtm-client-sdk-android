# fpnn rtm sdk android #

底层基于NIO实现, 支持FPNN加密.

#### 关于依赖 ####

* [fpnn.jar](https://github.com/highras/fpnn-sdk-java)
* [msgpack-core.jar](https://github.com/msgpack/msgpack-java)

#### 关于线程 ####

* 一个计时器线程`ThreadPool.getInstance().startTimerThread()`, 负责超时检测/安全检查
    * 默认实现`Executors.newScheduledThreadPool(1)`, 构造`RTMClient`时可以选择是否启用该线程
    * 如果已有计时器, `NIOCore.getInstance().checkSecond()` 周期性调用该方法，以进行超时检查（建议频率1s）

* 一个线程池, 接口`ThreadPool.IThreadPool`
    * 默认实现`Executors.newFixedThreadPool(FPConfig.MAX_THREAD_COUNT)`
    * 如需自己管理线程，实现该接口并注册线程池`ThreadPool.getInstance().setPool(IThreadPool value)`

* 不要阻塞事件触发和回调, 否则线程池将被耗尽

#### 关于IPV6 ####

* `SOCKET`链接支持`IPV6`接口
* 兼容`DNS64/NAT64`网络环境

#### 关于连接 ####

* 默认连接会自动保持, 如实现按需连接则需要通过`login()`和`close()`进行连接或关闭处理
* 或可通过`login`和`close`事件以及注册`ping`服务来对连接进行管理

#### 关于编码格式 ####

* 消息发送接口仅支持`UTF-8`格式编码的`String`类型数据, `Binary`数据需进行`Base64`编解码

#### 一个例子 ####

```java
import com.fpnn.callback.CallbackData;
import com.fpnn.callback.FPCallback;
import com.fpnn.event.EventData;
import com.fpnn.event.FPEvent;
import com.rtm.RTMClient;
import com.rtm.RTMConfig;
...

// 创建Client
RTMClient client = new RTMClient(
    "rtm-nx-front.ifunplus.cn:13325",
    1000012,
    654321,
    "5C65CD872903AAB37211EC468B4A1364",
    null,
    new HashMap<String, String>(),
    true,
    20 * 1000,
    true
);

// 添加监听
client.getEvent().addListener("login", new FPEvent.IListener() {

    @Override
    public void fpEvent(EventData evd) {

        if (evd.getException() != null) {

            System.out.println("Auth Fail!");
            return;
        }

        System.out.println("Authed!");

        // 发送消息
        client.sendMessage(778899, (byte) 8, "hello !", "", 0, 5 * 1000, new FPCallback.ICallback() {

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
    }
});

client.getEvent().addListener("close", new FPEvent.IListener() {

    @Override
    public void fpEvent(EventData evd) {

        System.out.println("Closed! retry:" + evd.hasRetry());
    }
});

client.getEvent().addListener("error", new FPEvent.IListener() {

    @Override
    public void fpEvent(EventData evd) {

        evd.getException().printStackTrace();
    }
});

// push service
RTMProcessor processor = this._client.getProcessor();

processor.addPushService(RTMConfig.SERVER_PUSH.recvMessage, new RTMProcessor.IService() {

    @Override
    public void Service(Map<String, Object> data) {
        
        System.out.println("[recvMessage] " + JsonHelper.getInstance().getJson().toJSON(data));
    }
});

// 开启连接
client.login(null);

// destory
// client.destory();
// client = null;
```

#### 测试 ####

参考`app/src/main/java/com/test/MainTest.java`:

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
        * `retry`: **(boolean)** 是否执行自动重连(未启用或者被踢掉则不会执行自动重连)

#### PushService ####

* `RTMProcessor::addPushService(String name, IService is)`: 添加推送回调
    * `name`: **(String)** 推送服务类型, 参考`RTMConfig.SERVER_PUSH`成员
    * `is`: **(IService)** 回调方法

* `RTMProcessor::removePushService(String name)`: 删除推送回调
    * `name`: **(String)** 推送服务类型, 参考`RTMConfig.SERVER_PUSH`成员

* `RTMProcessor::hasPushService(String name)`: 是否存在推送回调
    * `name`: **(String)** 推送服务类型, 参考`RTMConfig.SERVER_PUSH`成员

* `RTMConfig.SERVER_PUSH`:
    * `kickoutroom`: RTMGate主动从Room移除
        * `data`: **(Map(String, Object))**
            * `data.rid`: **(long)** Room id

    * `ping`: RTMGate主动ping
        * `data`: **(Map(String, Object))**
            * `data`: **(Map)**

    * `pushmsg`: RTMGate主动推送P2P消息
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.mtype`: **(byte)** 消息类型
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 消息内容
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

    * `pushgroupmsg`: RTMGate主动推送Group消息
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.gid`: **(long)** Group id
            * `data.mtype`: **(byte)** 消息类型
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 消息内容
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

    * `pushroommsg`: RTMGate主动推送Room消息
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.rid`: **(long)** Room id
            * `data.mtype`: **(byte)** 消息类型
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 消息内容
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

    * `pushbroadcastmsg`: RTMGate主动推送广播消息
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.mtype`: **(byte)** 消息类型
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 消息内容
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

    * `pushfile`: RTMGate主动推送P2P文件
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.mtype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 文件获取地址(url)
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

    * `pushgroupfile`: RTMGate主动推送Group文件
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.gid`: **(long)** Group id
            * `data.mtype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 文件获取地址(url)
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

    * `pushroomfile`: RTMGate主动推送Room文件
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.rid`: **(long)** Room id
            * `data.mtype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 文件获取地址(url)
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

    * `pushbroadcastfile`: RTMGate主动推送广播文件
        * `data`: **(Map(String, Object))**
            * `data.from`: **(long)** 发送者 id
            * `data.mtype`: **(byte)** 文件类型, 请参考 `RTMConfig.FILE_TYPE` 成员
            * `data.mid`: **(long)** 消息 id, 当前链接会话内唯一
            * `data.msg`: **(String)** 文件获取地址(url)
            * `data.attrs`: **(String)** 发送时附加的自定义内容
            * `data.mtime`: **(long)**

#### API ####

* `constructor(String dispatch, int pid, long uid, String token, String version, Map<String, String> attrs, boolean reconnect, int timeout, boolean startTimerThread)`: 构造RTMClient
    * `dispatch`: **(String)** Dispatch服务地址, RTM提供
    * `pid`: **(int)** 应用编号, RTM提供
    * `uid`: **(long)** 用户ID
    * `token`: **(String)** 用户登录Token, RTM提供
    * `version`: **(String)** 服务器版本号, RTM提供
    * `attrs`: **(Map(String,String))** 设置用户端信息, 保存在当前链接中, 客户端可以获取到
    * `reconnect`: **(boolean)** 是否自动重连
    * `timeout`: **(int)** 超时时间(ms), 默认: `30 * 1000`
    * `startTimerThread`: **(boolean)** 是否开启计时器线程 (负责超时检测/安全检查)

* `getProcessor`: **(RTMProcessor)** 监听PushService的句柄

* `destroy()`: 断开连接并销毁

* `login(String endpoint)`: 连接并登陆
    * `endpoint`: **(String)** RTMGate服务地址, 由Dispatch服务获取, 或由RTM提供

* `login(String curve, byte[] derKey, String endpoint)`: 连接并登陆(加密)
    * `curve`: **(String)** 加密协议
    * `derKey`: **(byte[])** 加密公钥, 秘钥文件RTM提供
    * `endpoint`: **(String)** RTMGate服务地址, 由Dispatch服务获取, 或由RTM提供

* `sendMessage(long to, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback)`: 发送消息
    * `to`: **(long)** 接收方uid
    * `mtype`: **(byte)** 消息类型
    * `msg`: **(String)** 消息内容
    * `attrs`: **(String)** 消息附加信息, 没有可传`""`
    * `mid`: **(long)** 消息 id, 用于过滤重复消息, 非重发时为`0`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map(mtime:long))**
            * `exception`: **(Exception)**
            * `mid`: **(long)**

* `sendGroupMessage(long gid, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback)`: 发送group消息
    * `gid`: **(long)** group id
    * `mtype`: **(byte)** 消息类型
    * `msg`: **(String)** 消息内容
    * `attrs`: **(String)** 消息附加信息, 可传`""`
    * `mid`: **(long)** 消息 id, 用于过滤重复消息, 非重发时为`0`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map(mtime:long))**
            * `exception`: **(Exception)**
            * `mid`: **(long)**

* `sendRoomMessage(long rid, byte mtype, String msg, String attrs, long mid, int timeout, FPCallback.ICallback callback)`: 发送room消息
    * `rid`: **(long)** room id
    * `mtype`: **(byte)** 消息类型
    * `msg`: **(String)** 消息内容
    * `attrs`: **(String)** 消息附加信息, 可传`""`
    * `mid`: **(long)** 消息 id, 用于过滤重复消息, 非重发时为`0`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map(mtime:long))**
            * `exception`: **(Exception)**
            * `mid`: **(long)**

* `getUnreadMessage(int timeout, FPCallback.ICallback callback)`: 检测未读消息数目
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `mid`: **(long)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(p2p:Map(String,int),group:Map(String,int)))**

* `cleanUnreadMessage(int timeout, FPCallback.ICallback callback)`: 清除未读消息
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**
            * `mid`: **(long)**

* `getSession(int timeout, FPCallback.ICallback callback)`: 获取所有会话
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `mid`: **(long)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(p2p:Map(String,long),group:Map(String,long)))**

* `getGroupMessage(long gid, boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback)`: 获取Group历史消息
    * `gid`: **(long)** Group id
    * `desc`: **(boolean)** `true`: 则从`end`的时间戳开始倒序翻页, `false`: 则从`begin`的时间戳顺序翻页
    * `num`: **(int)** 获取数量, **一次最多获取20条, 建议10条**
    * `begin`: **(long)** 开始时间戳, 毫秒, 默认`0`, 条件：`>=`
    * `end`: **(long)** 结束时间戳, 毫秒, 默认`0`, 条件：`<=`
    * `lastid`: **(long)** 最后一条消息的id, 第一次默认传`0`, 条件：`> or <`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(num:int,lastid:long,begin:long,end:long,msgs:List(GroupMsg)))**
                * `GroupMsg.id` **(long)**
                * `GroupMsg.from` **(long)**
                * `GroupMsg.mtype` **(byte)**
                * `GroupMsg.mid` **(long)**
                * `GroupMsg.deleted` **(boolean)**
                * `GroupMsg.msg` **(String)**
                * `GroupMsg.attrs` **(String)**
                * `GroupMsg.mtime` **(long)**

* `getRoomMessage(long rid, boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback)`: 获取Room历史消息
    * `rid`: **(long)** Room id
    * `desc`: **(boolean)** `true`: 则从`end`的时间戳开始倒序翻页, `false`: 则从`begin`的时间戳顺序翻页
    * `num`: **(int)** 获取数量, **一次最多获取20条, 建议10条**
    * `begin`: **(long)** 开始时间戳, 毫秒, 默认`0`, 条件：`>=`
    * `end`: **(long)** 结束时间戳, 毫秒, 默认`0`, 条件：`<=`
    * `lastid`: **(long)** 最后一条消息的id, 第一次默认传`0`, 条件：`> or <`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(num:int,lastid:long,begin:long,end:long,msgs:List(RoomMsg)))**
                * `RoomMsg.id` **(long)**
                * `RoomMsg.from` **(long)**
                * `RoomMsg.mtype` **(byte)**
                * `RoomMsg.mid` **(long)**
                * `RoomMsg.deleted` **(boolean)**
                * `RoomMsg.msg` **(String)**
                * `RoomMsg.attrs` **(String)**
                * `RoomMsg.mtime` **(long)**

* `getBroadcastMessage(boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback)`: 获取广播历史消息
    * `desc`: **(boolean)** `true`: 则从`end`的时间戳开始倒序翻页, `false`: 则从`begin`的时间戳顺序翻页
    * `num`: **(int)** 获取数量, **一次最多获取20条, 建议10条**
    * `begin`: **(long)** 开始时间戳, 毫秒, 默认`0`, 条件：`>=`
    * `end`: **(long)** 结束时间戳, 毫秒, 默认`0`, 条件：`<=`
    * `lastid`: **(long)** 最后一条消息的id, 第一次默认传`0`, 条件：`> or <`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(num:int,lastid:long,begin:long,end:long,msgs:List(BroadcastMsg)))**
                * `BroadcastMsg.id` **(long)**
                * `BroadcastMsg.from` **(long)**
                * `BroadcastMsg.mtype` **(byte)**
                * `BroadcastMsg.mid` **(long)**
                * `BroadcastMsg.deleted` **(boolean)**
                * `BroadcastMsg.msg` **(String)**
                * `BroadcastMsg.attrs` **(String)**
                * `BroadcastMsg.mtime` **(long)**

* `getP2PMessage(long ouid, boolean desc, int num, long begin, long end, long lastid, int timeout, FPCallback.ICallback callback)`: 获取P2P历史消息
    * `ouid`: **(long)** 获取和两个用户之间的历史消息
    * `desc`: **(boolean)** `true`: 则从`end`的时间戳开始倒序翻页, `false`: 则从`begin`的时间戳顺序翻页
    * `num`: **(int)** 获取数量, **一次最多获取20条, 建议10条**
    * `begin`: **(long)** 开始时间戳, 毫秒, 默认`0`, 条件：`>=`
    * `end`: **(long)** 结束时间戳, 毫秒, 默认`0`, 条件：`<=`
    * `lastid`: **(long)** 最后一条消息的id, 第一次默认传`0`, 条件：`> or <`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(num:int,lastid:long,begin:long,end:long,msgs:List(P2PMsg)))**
                * `P2PMsg.id` **(long)**
                * `P2PMsg.direction` **(byte)**
                * `P2PMsg.mtype` **(byte)**
                * `P2PMsg.mid` **(long)**
                * `P2PMsg.deleted` **(boolean)**
                * `P2PMsg.msg` **(String)**
                * `P2PMsg.attrs` **(String)**
                * `P2PMsg.mtime` **(long)**

* `fileToken(String cmd, List<Long> tos, long to, long rid, long gid, int timeout, FPCallback.ICallback callback)`: 获取发送文件的token
    * `cmd`: **(String)** 文件发送方式`sendfile | sendfiles | sendroomfile | sendgroupfile | broadcastfile`
    * `tos`: **(List(Long))** 接收方 uids
    * `to`: **(long)** 接收方 uid
    * `rid`: **(long)** Room id
    * `gid`: **(long)** Group id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(token:String, endpoint:String))**

* `close()`: 断开连接

* `addAttrs(Map<String, String> attrs, int timeout, FPCallback.ICallback callback)`: 设置客户端信息, 保存在当前链接中, 客户端可以获取到
    * `attrs`: **(Map(String,String))** key-value形式的变量
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `getAttrs(int timeout, FPCallback.ICallback callback)`: 获取客户端信息
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(attrs:List(Map)))**
                * `Map.ce` **(String)**
                * `Map.login` **(String)**
                * `Map.my` **(String)**

 * `addDebugLog(String msg, String attrs, int timeout, FPCallback.ICallback callback)`: 添加debug日志
    * `msg`: **(String)** 调试信息msg
    * `attrs`: **(String)** 调试信息attrs
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `addDevice(String apptype, String devicetoken, int timeout, FPCallback.ICallback callback)`: 添加设备, 应用信息
    * `apptype`: **(String)** 应用信息
    * `devicetoken`: **(String)** 设备信息
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `removeDevice(String devicetoken, int timeout, FPCallback.ICallback callback)`: 删除设备, 应用信息
    * `devicetoken`: **(String)** 设备信息
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `setTranslationLanguage(String targetLanguage, int timeout, FPCallback.ICallback callback)`: 设置自动翻译的默认目标语言类型, 如果 targetLanguage 为空字符串, 则取消自动翻译
    * `targetLanguage`: **(String)** 翻译的目标语言类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `translate(String originalMessage, String originalLanguage, String targetLanguage, int timeout, FPCallback.ICallback callback)`: 翻译消息
    * `originalMessage`: **(String)** 待翻译的原始消息
    * `originalLanguage`: **(String)** 待翻译的消息的语言类型, 可为`null`
    * `targetLanguage`: **(String)** 本次翻译的目标语言类型
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(stext:String,src:String,dtext:String,dst:String))**

* `addFriends(List<Long> friends, int timeout, FPCallback.ICallback callback)`: 添加好友, 每次最多添加100人
    * `friends`: **(List(Long))** 多个好友 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `deleteFriends(List<Long> friends, int timeout, FPCallback.ICallback callback)`: 删除好友, 每次最多删除100人
    * `friends`: **(List(Long))** 多个好友 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `getFriends(int timeout, FPCallback.ICallback callback)`: 获取好友
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(List(Long))**
            * `exception`: **(Exception)**

* `addGroupMembers(long gid, List<Long> uids, int timeout, FPCallback.ICallback callback)`: 添加group成员, 每次最多添加100人
    * `gid`: **(long)** group id
    * `uids`: **(List(Long))** 多个用户 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(List(Long))**
            * `exception`: **(Exception)**

* `deleteGroupMembers(long gid, List<Long> uids, int timeout, FPCallback.ICallback callback)`:  删除group成员, 每次最多删除100人
    * `gid`: **(long)** group id
    * `uids`: **(List(Long))** 多个用户 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(List(Long))**
            * `exception`: **(Exception)**

* `getGroupMembers(long gid, int timeout, FPCallback.ICallback callback)`: 获取group成员
    * `gid`: **(long)** group id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(List(Long))**
            * `exception`: **(Exception)**

* `getUserGroups(int timeout, FPCallback.ICallback callback)`: 获取用户所在的Group
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(List(Long))**
            * `exception`: **(Exception)**

* `enterRoom(long rid, int timeout, FPCallback.ICallback callback)`: 进入房间
    * `rid`: **(long)** 房间 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `leaveRoom(long rid, int timeout, FPCallback.ICallback callback)`: 离开房间
    * `rid`: **(long)** 房间 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `getUserRooms(int timeout, FPCallback.ICallback callback)`: 获取用户所在的Room
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(List(Long))**
            * `exception`: **(Exception)**

* `getOnlineUsers(List<Long> uids, int timeout, FPCallback.ICallback callback)`: 获取在线用户, 限制每次最多获取200个
    * `uids`: **(List(Long))** 多个用户 id
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(List(Long))**
            * `exception`: **(Exception)**

* `deleteMessage(long mid, long xid, byte type, int timeout, FPCallback.ICallback callback)`: 删除消息
    * `mid`: **(long)** 消息 id
    * `xid`: **(long)** 消息接收方 id (userId/RoomId/GroupId)
    * `type`: **(byte)** 接收方类型 (1:p2p, 2:group, 3:room)
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `kickout(String ce, int timeout, FPCallback.ICallback callback)`: 踢掉一个链接 (只对多用户登录有效, 不能踢掉自己, 可以用来实现同类设备唯一登录)
    * `ce`: **(String)** 当前链接的`endpoint`, 可以通过调用`getAttrs`获取
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `dbGet(String key, int timeout, FPCallback.ICallback callback)`: 获取存储的数据信息, 返回值不包含`val`表示`key`不存在
    * `key`: **(String)** 存储数据对应键值, 最长`128 字节`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `exception`: **(Exception)**
            * `payload`: **(Map(val:String))**

* `dbSet(String key, String value, int timeout, FPCallback.ICallback callback)`: 设置存储的数据信息, `value`为空则删除对应`key`
    * `key`: **(String)** 存储数据对应键值, 最长`128 字节`
    * `value`: **(String)** 存储数据实际内容, 最长`1024 * 1024 * 2 字节`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map)**
            * `exception`: **(Exception)**

* `sendFile(byte mtype, long to, byte[] fileBytes, long mid, int timeout, FPCallback.ICallback callback)`: 发送文件
    * `mtype`: **(byte)** 消息类型
    * `to`: **(long)** 接收者 id
    * `fileBytes`: **(byte[])** 要发送的文件
    * `mid`: **(long)** 消息 id, 用于过滤重复消息, 非重发时为`0`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map(mtime:long))**
            * `exception`: **(Exception)**
            * `mid`: **(long)**

* `sendGroupFile(byte mtype, long gid, byte[] fileBytes, long mid, int timeout, FPCallback.ICallback callback)`: 发送文件
    * `mtype`: **(byte)** 消息类型
    * `gid`: **(long)** Group id
    * `fileBytes`: **(byte[])** 要发送的文件
    * `mid`: **(long)** 消息 id, 用于过滤重复消息, 非重发时为`0`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map(mtime:long))**
            * `exception`: **(Exception)**
            * `mid`: **(long)**

* `sendRoomFile(byte mtype, long rid, byte[] fileBytes, long mid, int timeout, FPCallback.ICallback callback)`: 发送文件
    * `mtype`: **(byte)** 消息类型
    * `rid`: **(long)** Room id
    * `fileBytes`: **(byte[])** 要发送的文件
    * `mid`: **(long)** 消息 id, 用于过滤重复消息, 非重发时为`0`
    * `timeout`: **(int)** 超时时间(ms)
    * `callback`: **(FPCallback.ICallback)** 回调方法
        * `cbdata`: **(CallbackData)**
            * `payload`: **(Map(mtime:long))**
            * `exception`: **(Exception)**
            * `mid`: **(long)**
