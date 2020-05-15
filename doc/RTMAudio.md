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
//path 存储录音文件路径 可空
public void startRecord(String path);
public void stopRecord()


#### 使用
    RTMAudio audioManage = new RTMAudio();
    audioManage.init(String lang, IAudioAction action) //lang, action可空
    audioManage.startRecord(); //开始录音
    audioManage.stopRecord();  //结束录音
    byte[] data = audioManage.genAudioData()  //获取录音文件
    得到音频数据然后发送给用户/群组/房间
    RTMClient.sendAudio(....,data...);
    
    当用户实现自定义pushAudio函数后 收到语音流data 可以使用如下方法播放
    audioManage.broadAduio(data)


