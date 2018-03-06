package com.fpnn.rtm;

import com.fpnn.sdk.proto.Answer;

public interface RTMAuthCallback {
    void authResult(boolean success);
    void onException(int errorCode, String message);
}
