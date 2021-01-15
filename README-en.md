### android-rtm-sdk 使用文档
- [AndroidVersionSupport](#androidversionsupport)
- [Depends](#depends)
- [Instructions](#instructions)
- [Demonstration](#demonstration)
- [API](#api-interface)
- [TestCase](#testcase)

## AndroidVersionSupport
- lowest Android version is 4.1(api16)
- support fpnn ecc encryption(secp192r1,secp224r1,secp256r1,secp256r1)

### Depends
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
        api 'com.github.highras:rtm-android:2.3.6'
    }
    ~~~
2. dependency in Maven
    ~~~
    <dependency>
        <groupId>com.github.highras</groupId>
        <artifactId>rtm-android</artifactId>
        <version>2.3.6</version>
        <type>pom</type>
    </dependency>
    ~~~



### Instructions
- RTM need network permission，if you use RTMaudio  module，it's need storage and record permission
- please use RTMClient in child thread
- RTM suipport autoconnect,after init RTMclient,you can use <setAutoconnect> function
  - autoconnect need set reconnect-start function and reconnect-complete funciton, as well applicationContext
- server push:please extends RTMPushProcessor,overload the function what you need
- the different of room and group,group is persistent ，room is not persistent；when you disconnect rtm or logout you will be leave room
- all async and sync function will contain RTMAnswer，please judge errorCode in RTMAnswer first, if errorCode equal 0 means successful
- RTMConfig class is RTM gloable config, all param has default value，if user need change default value, please call RTMConfig.Config(RTMConfig newConfig) before init RTMClient。
- user can extends ErrorRecorder record rtmsdk internal error(strongly recommend). for example:
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
    or
    RTMConfig newconfig = new RTMConfig();
    newconfig.errorRecorder = new TestErrorRecorder();
    RTMConfig.Config( newconfig); （before RTMClient init）
    ~~~

### Demonstration
import com.rtmsdk.RTMClient;<br>
import com.rtmsdk.RTMErrorCode;
import com.rtmsdk.RTMAudio; //audio

 ~~~
    RTMClient client = new RTMClient(String endpoint, long pid, long uid,new RTMExampleQuestProcessor());
    
    //if server use fpnn encry  rtmclient need peerPublicKey and algorithm type;
    client.enableEncryptorByDerData(String curve, byte[] peerPublicKey);
    
    //-- sync
    client.login(String token, String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")
    //-- Async
    client.login(loginCallback callback, String token = "", String lang = "", Map<String, String> attr = "", string addrestype = "ipv4")

    if login successed
    client.sendChat/ client.sendMessage.....
~~~

##  API-Interface
- [CallBackInterface](doc-en/RTMUserInterface.md)
- [ServerPush](doc-en/RTMPush.md)
- [Chat](doc-en/RTMChat.md)
- [Message](doc-en/RTMessage.md)
- [File](doc-en/RTMFile.md)
- [Room/Group/Friend](doc-en/RTMRelationship.md)
- [User-System](doc-en/RTMUserSystem.md)
- [Audio](doc-en/RTMAudio.md)

#### TestCase
- [Case](app/src/main/java/com/rtm)
