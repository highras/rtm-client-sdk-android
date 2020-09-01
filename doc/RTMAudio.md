#### RTM语音
录音数据需加上RTM语音头
用户可以继承IAudioAction接口 自定义开始录音,结束录音,开始播放,结束播放操作
- 接口
~~~c++
       void startRecord();
        void stopRecord();
        void broadAudio();
        void broadFinish();
~~~

#### API
public void broadAduio(byte[] amrData) {
public void startRecord(String path);
public File stopRecord()


#### 使用
    RTMAudio audioManage = RTMAudio.getinstance();
    public void init(File file, String lang, IAudioAction audioAction) { //lang, action可空
    audioManage.startRecord(); //开始录音
    audioManage.stopRecord();  //结束录音
    rtmclient.sendaudio/sendgroupaudio/sendroomaudio()
    
    当用户实现自定义pushAudio函数后 收到语音消息后 可以使用getmsg获取实际语音数据 然后调用播放
    audioManage.broadAduio(data)


