## android-rtm-sdk
    最低支持android版本为4.4 支持fpnn ecc加密

### 第三方依赖
## fpnn-android-sdk
~~~
https://github.com/highras/fpnn-sdk-android
~~~

### for Maven
~~~
<dependency>
    <groupId>com.github.highras</groupId>
    <artifactId>rtm-android</artifactId>
    <version>2.0.0</version>
    <type>pom</type>
</dependency>
~~~

### for Gradle
~~~
    implementation 'com.github.highras:rtm-android:2.0.0'
~~~

### 使用
import com.rtmsdk.RTMClient;<br>
import com.rtmsdk.RTMErrorCode;

说明:
1. rtm通信需要网络权限，使用语音相关功能需要存储和录音权限
2. 请在子线程中调用RTMClient的登录和任何发送操作

如果使用语音相关功能 (需要录音权限和读取存储权限)
import com.rtmsdk.RTMAudio;
### 初始化RTMClient
    RTMClient client = new RTMClient(String endpoint, long pid, long uid,new RTMExampleQuestProcessor());
    
    //若服务端启用fpnn加密功能 客户端需要传入公钥和曲线算法
    client.enableEncryptorByDerData(String curve, byte[] peerPublicKey);
    
    //-- sync
    client.login(String token, String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    //-- Async
    client.login(loginCallback callback, String token = "", String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    
    login成功后可以正常调用rtm相关接口

### 链接关闭
- 用户可以自己定义RTMClient关闭事件，继承IRTMQuestProcessor接口 重写sessionClosed方法，当链接关闭或被服务器kickout时 用户的auth(验证)状态清除，之后调用任何rtm相关接口都会返回失败200022(unauthed)
用户需要再次调用login


## 回调和api
- [回调接口](doc/RTMUserInterface.md)
- [聊天接口](doc/RTMChat.md)
- [消息接口](doc/RTMMessage.md)
- [文件接口](doc/RTMFile.md)
- [房间/群组/好友接口](doc/RTMRelationship.md)
- [用户系统命令接口](doc/RTMUserSystem.md)
- [语音接口](doc/RTMAudio.md)

服务器push消息
- 请继承IRTMQuestProcessor接口,重写push系列函数

#### 使用
- [详见测试案例](app/src/main/java/com/rtm)
