package com.fpnn.rtm;

public class RTMException extends Exception {

    private int errorCode;
    private String message;

    RTMException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (message == null)
            return "RTM exception. code: " + errorCode;
        else
            return "RTM exception. code: " + errorCode + ", message: " + message;
    }
}
