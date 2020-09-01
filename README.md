### android-rtm-sdk 使用文档
- [版本支持](#版本支持)
  - [依赖集成](#依赖集成)
  - [使用说明](#使用说明)
  - [使用示例](#使用示例)
- [接口说明](#接口说明)
    - [测试案例](#测试案例)

## 版本支持
- 最低支持Android版本为4.4
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
        implementation 'com.github.highras:rtm-android:2.0.1'
    }
    ~~~
2. dependency in Maven
    ~~~
    <dependency>
        <groupId>com.github.highras</groupId>
        <artifactId>rtm-android</artifactId>
        <version>2.0.1</version>
        <type>pom</type>
    </dependency>
    ~~~



### 使用说明
- rtm通信需要网络权限，使用语音相关功能需要存储和录音权限
- 请在子线程中调用RTMClient的登录和任何发送操作
- rtm支持自动重连 初始化rtmclient成功后调用setAutoconnect方法设置自动重连
  - 自动重连需要设置重连开始回调和重连完成回调函数并传入applicationContext
- 服务器push消息:请继承IRTMQuestProcessor类,重写自己需要的push系列函数
- 所有同步和异步接口都会返回 RTMAnswer，请先判断answer中的errorCode 如果为0正常
- RTMConfig 创建后，所有配置均已有默认值，使用者需要重新设置需要更改的值，然后传入Init接口即可。
- 用户可以重写rtm的日志类 收集和获取sdk内部的错误信息 例如
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
    client.login(loginCallback callback, String token = "", String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    
    login成功后可以正常调用rtm相关接口
    client.sendChat/ client.sendMessage.....
~~~

##  接口说明
- [用户回调接口](doc/RTMUserInterface.md)
- [服务端push接口](doc/RTMPush.md)
- [聊天接口](doc/RTMChat.md)
- [消息接口](doc/RTMessage.md)
- [文件接口](doc/RTMFile.md)
- [房间/群组/好友接口](doc/RTMRelationship.md)
- [用户系统命令接口](doc/RTMUserSystem.md)
- [语音接口](doc/RTMAudio.md)


#### 测试案例
- [详见测试案例](app/src/main/java/com/rtm)
