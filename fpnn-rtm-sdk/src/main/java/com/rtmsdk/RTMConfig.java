package com.rtmsdk;

import com.fpnn.sdk.ErrorRecorder;

public class RTMConfig {
    final static int lostConnectionAfterLastPingInSeconds = 60;
    final static int globalMaxThread = 8;

    public final static String SDKVersion = "2.4.0";
    public final static String InterfaceVersion = "2.6.1";
    public ErrorRecorder defaultErrorRecorder = (ErrorRecorder)ErrorRecorder.getInstance();
    public int globalQuestTimeoutSeconds = 30;   //请求超时时间
    public int globalFileQuestTimeoutSeconds = 120;  //传输文件/音频/翻译/语音识别/文本检测 最大超时时间
    public boolean autoConnect = true;
}
