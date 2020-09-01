package com.rtm.test;

import com.fpnn.sdk.ErrorRecorder;

public class TestErrorRecorder extends ErrorRecorder {
    public TestErrorRecorder(){
        super.setErrorRecorder(this);
    }

    public void recordError(Exception e) {
        mylog.log("Exception:" + e);
    }

    public void recordError(String message) {
        mylog.log("Error:" + message);
    }

    public void recordError(String message, Exception e) {
        mylog.log(String.format("Error: %s, exception: %s", message, e));
    }
}