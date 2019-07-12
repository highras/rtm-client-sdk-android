package com.rtm;

public class RTMConfig {

    public static final String VERSION = "1.0.0";

    public static final int MID_TTL = 5 * 1000;                         //MID缓存超时时间(ms)
    public static final int RECONN_COUNT_ONCE = 3;                      //一次重新连接流程中的尝试次数
    public static final int CONNCT_INTERVAL = 40 * 1000;                //客户端尝试重新连接的时间间隔(ms)
    public static final int RECV_PING_TIMEOUT = 40 * 1000;              //客户端收到Ping超时时间(ms)


    public class FILE_TYPE {

        public static final byte image = 40;        //图片
        public static final byte audio = 41;        //语音
        public static final byte video = 42;        //视频
        public static final byte file = 50;         //泛指文件，服务器会修改此值（如果服务器可以判断出具体类型的话，仅在mtype=50的情况下）
    }

    public final static String KICKOUT = "kickout";

    public class SERVER_PUSH {

        public static final String kickOutRoom = "kickoutroom";
        public static final String recvMessage = "pushmsg";
        public static final String recvGroupMessage = "pushgroupmsg";
        public static final String recvRoomMessage = "pushroommsg";
        public static final String recvBroadcastMessage = "pushbroadcastmsg";
        public static final String recvFile = "pushfile";
        public static final String recvGroupFile = "pushgroupfile";
        public static final String recvRoomFile = "pushroomfile";
        public static final String recvBroadcastFile = "pushbroadcastfile";
        public static final String recvPing = "ping";
    }

    public class SERVER_EVENT {

        public static final String login = "login";
        public static final String logout = "logout";
    }

    public class ERROR_CODE {

        public static final int RTM_EC_INVALID_PROJECT_ID_OR_USER_ID = 200001;
        public static final int RTM_EC_INVALID_PROJECT_ID_OR_SIGN = 200002;
        public static final int RTM_EC_INVALID_FILE_OR_SIGN_OR_TOKEN = 200003;
        public static final int RTM_EC_ATTRS_WITHOUT_SIGN_OR_EXT = 200004;

        public static final int RTM_EC_API_FREQUENCY_LIMITED = 200010;
        public static final int RTM_EC_MESSAGE_FREQUENCY_LIMITED = 200011;

        public static final int RTM_EC_FORBIDDEN_METHOD = 200020;
        public static final int RTM_EC_PERMISSION_DENIED = 200021;
        public static final int RTM_EC_UNAUTHORIZED = 200022;
        public static final int RTM_EC_DUPLCATED_AUTH = 200023;
        public static final int RTM_EC_AUTH_DENIED = 200024;
        public static final int RTM_EC_ADMIN_LOGIN = 200025;
        public static final int RTM_EC_ADMIN_ONLY = 200026;

        public static final int RTM_EC_LARGE_MESSAGE_OR_ATTRS = 200030;
        public static final int RTM_EC_LARGE_FILE_OR_ATTRS = 200031;
        public static final int RTM_EC_TOO_MANY_ITEMS_IN_PARAMETERS = 200032;
        public static final int RTM_EC_EMPTY_PARAMETER = 200033;

        public static final int RTM_EC_NOT_IN_ROOM = 200040;
        public static final int RTM_EC_NOT_GROUP_MEMBER = 200041;
        public static final int RTM_EC_MAX_GROUP_MEMBER_COUNT = 200042;
        public static final int RTM_EC_NOT_FRIEND = 200043;
        public static final int RTM_EC_BANNED_IN_GROUP = 200044;
        public static final int RTM_EC_BANNED_IN_ROOM = 200045;
        public static final int RTM_EC_EMPTY_GROUP = 200046;
        public static final int RTM_EC_ENTER_TOO_MANY_ROOMS = 200047;

        public static final int RTM_EC_UNSUPPORTED_LANGUAGE = 200050;
        public static final int RTM_EC_EMPTY_TRANSLATION = 200051;
        public static final int RTM_EC_SEND_TO_SELF = 200052;
        public static final int RTM_EC_DUPLCATED_MID = 200053;
        public static final int RTM_EC_SENSITIVE_WORDS = 200054;

        public static final int RTM_EC_UNKNOWN_ERROR = 200999;
    }
}
