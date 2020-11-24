### android-rtm-sdk 使用文档
- [版本支持](#版本支持)
- [依赖集成](#依赖集成)
- [使用说明](#使用说明)
- [使用示例](#使用示例)
- [接口说明](#接口说明)
- [测试案例](#测试案例)

### 版本支持
- 最低支持Android版本为4.1
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
        api 'com.github.highras:rtm-android:2.3.0'
    }
    ~~~
2. dependency in Maven
    ~~~
    <dependency>
        <groupId>com.github.highras</groupId>
        <artifactId>rtm-android</artifactId>
        <version>2.3.0</version>
        <type>pom</type>
    </dependency>
    ~~~



### 使用说明
- rtm通信需要网络权限，使用语音相关功能需要存储和录音权限
- 请在子线程初始化RTMClient以及登录和任何发送操作
- rtm支持自动重连 初始化rtmclient成功后可调用setAutoconnect方法设置自动重连
  - 自动重连需要设置重连开始回调和重连完成回调函数并传入applicationContext
- 服务器push消息:请继承RTMPushProcessor类,重写自己需要的push系列函数
- 所有同步和异步接口都会返回 RTMAnswer结构，请先判断answer中的errorCode 如果为0正常
- RTMConfig创建后，所有配置均已有默认值，使用者如需要重新设置默认值，请调用config接口即可。
- 用户可以重写rtm的日志类 收集和获取sdk内部的错误信息(强烈建议重载日志类) 例如
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
    rtmclient.setErrorRecoder(TestErrorRecorder)
    ~~~

### 使用示例
import com.rtmsdk.RTMClient;<br>
import com.rtmsdk.RTMErrorCode;
import com.rtmsdk.RTMAudio; //语音相关功能

 ~~~
    RTMClient client = new RTMClient(String endpoint, long pid, long uid,new RTMExampleQuestProcessor());
    
    //若服务端启用fpnn加密功能 客户端需要传入公钥和曲线算法
    client.enableEncryptorByDerData(String curve, byte[] peerPublicKey);
    
    //-- sync
    client.login(String token, String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    //-- Async
    client.login(loginCallback callback, String token = "", TranslateLang lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    
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
- [翻译语言/语言识别语言](https://wiki.ifunplus.cn/display/livedata/Speech+Recognition+API+V1)


#### 测试案例
- [详见测试案例](app/src/main/java/com/rtm)
