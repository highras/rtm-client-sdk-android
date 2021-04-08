package com.rtmsdk.test;

import com.fpnn.sdk.ErrorRecorder;

class TestErrorRecorder1 extends  ErrorRecorder{
    public TestErrorRecorder1(){
        super.setErrorRecorder(this);
    }

    public void recordError(Exception e) {
        mylog.log1("Exception:" + e);
    }

    public void recordError(String message) {
        mylog.log1("Error:" + message);
    }

    public void recordError(String message, Exception e) {
        mylog.log1(String.format("Error: %s, exception: %s", message, e));
    }
}