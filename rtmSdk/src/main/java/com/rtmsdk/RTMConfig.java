package com.rtmsdk;

import com.fpnn.sdk.ErrorRecorder;

public class RTMConfig {
    public static final String SDKVersion = "2.3.2";
    public static final String InterfaceVersion = "2.4.0";

    static int lostConnectionAfterLastPingInSeconds = 60;
    static int globalConnectTimeoutSeconds = 30;
    static int globalQuestTimeoutSeconds = 30;
    static int fileGateClientHoldingSeconds = 150;
    static int globalFileQuestTimeoutSeconds = 120;
    static int globalTranslateQuestTimeoutSeconds = 120;
    static ErrorRecorder errorRecorder = new ErrorRecorder();

    public int maxPingInterval;
    public int globalQuestTimeout;
    public int globalConnectTimeout;
    public int fileClientHoldingSeconds;
    public int globalFileQuestTimeout;
    public int globalTranslateQuestTimeout;
    public ErrorRecorder defaultErrorRecorder;

    public RTMConfig() {
        maxPingInterval = 60;
        globalQuestTimeout = 30;
        globalConnectTimeout = 30;
        fileClientHoldingSeconds = 150;
        globalFileQuestTimeout = 120;
        globalTranslateQuestTimeout = 120;
    }

    public static void Config(RTMConfig config) {
        errorRecorder = config.defaultErrorRecorder;
        globalQuestTimeoutSeconds = config.globalQuestTimeout;
        globalConnectTimeoutSeconds = config.globalConnectTimeout;
        fileGateClientHoldingSeconds = config.fileClientHoldingSeconds;
        lostConnectionAfterLastPingInSeconds = config.maxPingInterval;
        globalFileQuestTimeoutSeconds = config.globalFileQuestTimeout;
        globalTranslateQuestTimeoutSeconds = config.globalTranslateQuestTimeout;
    }
}
