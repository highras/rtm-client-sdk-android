#### RTMAudio
~~~c++
       void startRecord();
        void stopRecord();
        void broadAudio();
        void broadFinish();
~~~

#### API
public void broadAudio(byte[] amrData) {
public void startRecord();
public RTMAudioStruct stopRecord()


####
    RTMAudio audioManage = RTMAudio.getinstance();
    public void init(File file, String lang, IAudioAction audioAction) { //lang, action may null
    audioManage.startRecord();
    audioManage.stopRecord();
    rtmclient.sendfile/sendgroupfile/sendroomfile

    audioManage.broadAudio(data)


