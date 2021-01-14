### android-rtm-sdk 使用文档
- [版本支持](#版本支持)
- [依赖集成](#依赖集成)
- [使用说明](#使用说明)
- [使用示例](#使用示例)
- [接口说明](#接口说明)
- [测试案例](#测试案例)

### 版本支持
- 最低支持Android版本为4.1(api16)
- 支持fpnn ecc加密(secp192r1,secp224r1,secp256r1,secp256r1)

### 依赖集成
1.  dependency in Gradle
    - Add jcenter as your repository in project's build.gradle:
    ~~~
    allprojects {
            repositories {
                jcenter()
            }
        }
    ~~~
    - Add dependency in your module's build.gradle:
    ~~~
    dependencies {
        api 'com.github.highras:rtm-android:2.3.4'
    }
    ~~~
2. dependency in Maven
    ~~~
    <dependency>
        <groupId>com.github.highras</groupId>
        <artifactId>rtm-android</artifactId>
        <version>2.3.4</version>
        <type>pom</type>
    </dependency>
    ~~~



### 使用说明
- RTM通信需要网络权限，使用语音相关功能需要存储和录音权限
- 请在子线程初始化RTMClient以及登录和任何发送操作
- RTM支持自动重连 初始化RTMclient成功后可调用setAutoconnect方法设置自动重连
  - 自动重连需要设置重连开始回调和重连完成回调函数并传入applicationContext
- 服务器push消息:请继承RTMPushProcessor类,重写自己需要的push系列函数(RTM的push回调函数和收发线程在一起 如果用户在push的回调函数中有耗时操作 请单独开启线程不然会有可能阻塞后续的处理请求)
- RTM的各项服务配置和增值服务可以在后台配置，请登陆管理后台预览详细的配置参数
- 所有同步和异步接口都会返回 RTMAnswer结构，请优先判断answer中的errorCode 如果为0正常
- RTM的room和group的区别 group在服务端会持久化 room是非持久化(用户下线或者RTM链接断开会自动离开room)
  - room默认不支持多房间（当用户进入第二个房间会自动退出第一个房间） 用户可以在控制台开启支持多房间配置
- RTMConfig是RTM的全局配置参数，所有配置均已有默认值，使用者如需要重新设置默认值，请在初始化RTMclient之前调用RTMConfig.Config(RTMConfig newConfig)接口。
- 用户可以重写RTM的日志类 收集和获取sdk内部的错误信息(强烈建议重载日志类) 例如
    ~~~
     public class TestErrorRecorder extends ErrorRecorder {
        public TestErrorRecorder(){
            super.setErrorRecorder(this);
        }
    
        public void recordError(Exception e) {
            Log.i("log","Exception:" + e);
        }
    
        public void recordError(String message) {
            Log.i("log","Error:" + message);
        }
    
        public void recordError(String message, Exception e) {
            Log.i("log",String.format("Error: %s, exception: %s", message, e));
        }
    }
    RTMClient rtmclient  = new RTMClient((String endpoint, long pid, long uid, RTMPushProcessor serverPushProcessor)
    rtmclient.setErrorRecoder(new TestErrorRecorder())
    或者
    RTMConfig newconfig = new RTMConfig();
    newconfig.errorRecorder = new TestErrorRecorder();
    RTMConfig.Config( newconfig); （需要在RTMClient初始化之前）
    ~~~

### 使用示例
import com.rtmsdk.RTMClient;<br>
import com.rtmsdk.RTMErrorCode;
import com.rtmsdk.RTMAudio; //语音相关功能

 ~~~
    public class RTMExampleQuestProcessor extends RTMPushProcessor {
        ....//重写自己需要处理的业务接口
    }
    
    RTMClient client = new RTMClient(String endpoint, long pid, long uid,new RTMExampleQuestProcessor());
    
    //若服务端启用fpnn加密功能 客户端需要传入公钥和曲线算法
    client.enableEncryptorByDerData(String curve, byte[] peerPublicKey);
    
    //-- sync
    client.login(String token, String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    //-- Async
    client.login(loginCallback callback, String token = "", String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    
    login成功后可以正常调用rtm相关接口
    client.sendChat/ client.sendMessage.....
~~~

##  接口说明
- [用户回调接口和数据结构](doc-zh/RTMUserInterface.md)
- [服务端push接口](doc-zh/RTMPush.md)
- [聊天接口](doc-zh/RTMChat.md)
- [消息接口](doc-zh/RTMessage.md)
- [文件接口](doc-zh/RTMFile.md)
- [房间/群组/好友接口](doc-zh/RTMRelationship.md)
- [用户系统命令接口](doc-zh/RTMUserSystem.md)
- [语音接口](doc-zh/RTMAudio.md)
- [RTM错误码](doc-zh/ErrorCode.md)


#### 测试案例
- [详见测试案例](app/src/main/java/com/rtm)
