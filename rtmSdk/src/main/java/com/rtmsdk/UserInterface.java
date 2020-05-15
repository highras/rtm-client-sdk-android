package com.rtmsdk;

import com.rtmsdk.RTMStruct.HistoryMessage;
import com.rtmsdk.RTMStruct.RetrievedMessage;
import com.rtmsdk.RTMStruct.TranslatedMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UserInterface {
    //接口状态码的回调
    public interface ErrorCodeCallback {
        void call(int errorCode);
    }

    //返回
    public interface LongFunctionCallback {
        void call(long mtime, int errorCode);
    }

    //
    public interface UserAttrsCallback {
        void call(Map<String, String> attrs, int errorCode);
    }

    //属性值回调
    public interface AttrsCallback {
        void call(List<Map<String, String>> attrs, int errorCode);
    }

    public interface  HistoryMessageCallback{
        void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode);
    }

    public interface  UnreadCallback{
        void call(List<Long> p2p_uids, List<Long> groupIds, int errorCode);
    }

    public interface RetrievedMessageCallback{
        void call(RetrievedMessage message, int errorCode);
    }

    public interface TranslateCallback{
        void call(TranslatedMessage message, int errorCode);
    }

    public interface MessageCallback{
        void call(String message, int errorCode);
    }

    public interface  ProfanityCallback{
        void call(String resultText, List<String> classification, int errorCode);
    }

    public interface DoubleStringCallback{
        void call(String str1, String str2, int errorCode);
    }

    public interface MembersCallback{
        void call(HashSet<Long> uids, int errorCode);
    }
}
