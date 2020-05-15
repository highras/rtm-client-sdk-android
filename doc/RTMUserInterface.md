~~~ java

    //接口状态码的回调
    public interface ErrorCodeCallback {
        void call(int errorCode);
    }

    //返回
    public interface LongFunctionCallback {
        void call(long mtime, int errorCode);
    }

    //用户属性回调接口
    public interface UserAttrsCallback {
        void call(Map<String, String> attrs, int errorCode);
    }

    //属性值回调
    public interface AttrsCallback {
        void call(List<Map<String, String>> attrs, int errorCode);
    }

    //历史消息接口
    public interface  HistoryMessageCallback{
        void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode);
    };

    //未读消息接口
    public interface  UnreadCallback{
        void call(List<Long> p2p_uids, List<Long> groupIds, int errorCode);
    };

    //消息具体内容接口
    public interface RetrievedMessageCallback{
        void call(RetrievedMessage message, int errorCode);
    }

    //翻译回调接口
    public interface TranslateCallback{
        void call(TranslatedMessage message, int errorCode);
    }

    //消息回调接口
    public interface MessageCallback{
        void call(String message, int errorCode);
    }

    //文本过滤回调接口
    public interface  ProfanityCallback{
        void call(String resultText, List<String> classification, int errorCode);
    }

    //group/room 公开，私有信息 语音识别返回的翻译文本
    public interface DoubleStringCallback{
        void call(String str1, String str2, int errorCode);
    }

    //用户成员回调uids/groupids/roomids
    public interface MembersCallback{
        void call(HashSet<Long> uids, int errorCode);
    }
~~~
