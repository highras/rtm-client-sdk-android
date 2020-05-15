package com.rtm.test;

import com.fpnn.sdk.ErrorRecorder;

public class TestErrorRecorder extends ErrorRecorder {
    public void recordError(Exception e) {
        synchronized (this) {
            mylog.log("Exception:" + e);
        }
    }

    public void recordError(String message) {
        synchronized (this) {
            mylog.log("Error:" + message);
        }
    }

    public void recordError(String message, Exception e) {
        synchronized (this) {
            mylog.log(String.format("Error: %s, exception: %s", message, e));
        }
    }
}