package com.rtmsdk;

import com.fpnn.sdk.ErrorRecorder;

public class RTMConfig {
    public static final String SDKVersion = "2.3.5";
    public static final String InterfaceVersion = "2.6.1";

    static int lostConnectionAfterLastPingInSeconds = 60;
    static int globalConnectTimeoutSeconds = 30;
    static int globalQuestTimeoutSeconds = 30;
    static int fileGateClientHoldingSeconds = 150;
    static int globalFileQuestTimeoutSeconds = 120;
    static int globalTranslateQuestTimeoutSeconds = 120;
    static int globalMaxThread = 8;
    static ErrorRecorder defaultErrorRecorder = (ErrorRecorder)ErrorRecorder.getInstance();

    public int maxPingInterval; //最大ping间隔没有收到服务器的发包 客户端主动断开连接
    public int globalQuestTimeout;//请求超时时间
    public int globalConnectTimeout;//链接超时时间
//    public int fileClientHoldingSeconds;
    public int globalFileQuestTimeout;//传输文件/音频 最大超时时间
    public int globalTranslateQuestTimeout;//翻译/识别最大超时时间
    public int globalMaxTaskThread;//翻译/识别最大超时时间
    public ErrorRecorder errorRecorder = (ErrorRecorder)ErrorRecorder.getInstance();//错误日志收集类

    public RTMConfig() {
        maxPingInterval = 60;
        globalQuestTimeout = 30;
        globalConnectTimeout = 30;
//        fileClientHoldingSeconds = 150;
        globalFileQuestTimeout = 120;
        globalTranslateQuestTimeout = 120;
        globalMaxTaskThread = 8;
        defaultErrorRecorder = errorRecorder;
    }

    public static void Config(RTMConfig config) {
        defaultErrorRecorder = config.errorRecorder;
        globalQuestTimeoutSeconds = config.globalQuestTimeout;
        globalConnectTimeoutSeconds = config.globalConnectTimeout;
//        fileGateClientHoldingSeconds = config.fileClientHoldingSeconds;
        lostConnectionAfterLastPingInSeconds = config.maxPingInterval;
        globalFileQuestTimeoutSeconds = config.globalFileQuestTimeout;
        globalMaxThread = config.globalMaxTaskThread;
        globalTranslateQuestTimeoutSeconds = config.globalTranslateQuestTimeout;
    }
}
