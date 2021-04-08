package com.rtmsdk.test;

import android.util.Log;

import com.fpnn.sdk.ErrorRecorder;

public class mylog extends ErrorRecorder{
    public static void log(String msg) {
        Log.i("sdktest", msg);
    }
    public static void log1(String msg) {
        Log.i("sdktest", "sdktest " + msg);
    }
}
