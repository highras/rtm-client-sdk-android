### android-rtm-sdk 使用文档
- [AndroidVersionSupport](#androidversionsupport)
- [Depends](#depends)
- [Instructions](#instructions)
- [Demonstration](#demonstration)
- [API](#api-interface)
- [TestCase](#testcase)

## AndroidVersionSupport
- lowest Android version is 4.4
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
        api 'com.github.highras:rtm-android:2.0.8'
    }
    ~~~
2. dependency in Maven
    ~~~
    <dependency>
        <groupId>com.github.highras</groupId>
        <artifactId>rtm-android</artifactId>
        <version>2.0.8</version>
        <type>pom</type>
    </dependency>
    ~~~



### Instructions
- rtm need network permission，if you use RTMaudio  module，it's need storage and record permission
- please use RTMClient in child thread
- rtm suipport autoconnect,after init rtmclient,you can use <setAutoconnect> function
  - autoconnect need set reconnect-start function and reconnect-complete funciton, as well setapplicationContext
- server push:please extends RTMPushProcessor,overload the function what you need
- all async and sync function will contain RTMAnswer，please judge errorCode in RTMAnswer first, if errorCode equal 0 means successful
- RTMConfig has default value，if user need change default value, please call rtmclient.config function。
- user can extends ErrorRecorder record sdk's error for example
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
    RTMClient.setErrorRecoder(new TestErrorRecorder())
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
    client.login(loginCallback callback, String token = "", TranslateLang lang = "", Map<String, String> attr = "", string addrestype = "ipv4")

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
- [Translate language](https://wiki.ifunplus.cn/display/livedata/Speech+Recognition+API+V1)


#### TestCase
- [Case](app/src/main/java/com/rtm)
