~~~ java
    //重连开始接口 每次重连都会判断reloginWillStart 返回值 若返回false则中断重连
    //reloginWillStart 参数说明 uid-用户id  answer本次重连的结果  reloginCount重连次数
    //备注 需要用户设定一些条件 比如重连间隔 最大重连次数
    public interface  IReloginStart{
        boolean reloginWillStart(long uid, RTMAnswer answer, int reloginCount);
    }

    //重连完成(如果 successful 为false表示最终重连失败,answer会有详细的错误码和错和错误信息 为true表示重连成功)
    //备注 当用户的token过期 重连会直接返回 不会继续判断reloginWillStart
    public interface  IReloginCompleted{
        void   reloginCompleted(long uid, boolean successful, RTMAnswer answer, int reloginCount);
    }

    //返回空结果的回调接口
    public interface IRTMEmptyCallback {
        void onResult(RTMAnswer answer);
    }

    //泛型接口 带有一个返回值的回调函数
    public interface IRTMCallback<T> {
        void onResult(T t, RTMAnswer answer);
    }

    //泛型接口 带有两个返回值的回调函数
    public interface IRTMDoubleValueCallback<T,V> {
        void onResult(T t, V v, RTMAnswer answer);
    }

    

public class RTMStruct {

    public enum TranslateType {
        Chat,
        Mail
    }

    //敏感词过滤类型
    public enum ProfanityType {
        Off, //不进行敏感词过滤
        Censor //如果有敏感词 结果用*号代替
    }
    
    //未读消息条目数结构
    public static class UnreadNum extends RTMAnswer
    {
        public HashMap<String, Integer> unreadInfo; //uid/groupid集合
    }

    
    public enum FileMessageType
    {
        IMAGEFILE(40),  //图片
        AUDIOFILE(41),  //音频文件
        VIDEOFILE(42),  //视频文件
        NORMALFILE(50); //一般文件
        private int value;

        FileMessageType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    //errorCode==0为成功 非0错误 错误信息详见errorMsg字段
    public static class RTMAnswer
    {
        public int errorCode = -1;
        public String errorMsg = "";
        RTMAnswer(){};
        RTMAnswer(int _code, String msg){
            errorCode = _code;
            errorMsg = msg;
        }
        public String getErrInfo(){
            return  " " + errorCode + "-" + errorMsg;
        }
    }

    //未读消息结构
    public static class Unread extends RTMAnswer
    {
        public List<Long> p2pList; //uid集合
        public List<Long> groupList;//群组id集合
    }

    public static class MessageType
    {
        public static final byte WITHDRAW = 1;
        public static final byte GEO = 2;
        public static final byte MULTILOGIN = 7; //多点登陆
        public static final byte CHAT = 30; //聊天
        public static final byte CMD = 32;//命令
        public static final byte REALAUDIO = 35;//实时语音
        public static final byte REALVIDEO = 36;//实时视频
        public static final byte IMAGEFILE = 40;//图片
        public static final byte AUDIOFILE = 41;//音频文件
        public static final byte VIDEOFILE = 42;//视频文件
        public static final byte NORMALFILE = 50;//一般文件
    }

    //serverpush 消息结构
    public static class RTMMessage
    {
        public long fromUid;    //发送者id 若等于自己uid 说明发送者是自己
        public long toId;       //目标id 根据messagetype 有可能是uid/groupid/roomid
        public byte messageType;  //消息类型 常规聊天类型见 RTMcore enum MessageType 用户可以自定义messagetype 51-127
        public long messageId;  //消息id
        public String stringMessage; //字符串消息
        public byte[] binaryMessage; //二进制消息
        public String attrs;        //客户端发送消息自定义的附加信息
        public long modifiedTime;   //服务器处理返回时间
        public FileStruct fileInfo; //文件结构信息(语音的信息也存在这里)
        public TranslatedInfo translatedInfo = null; //聊天信息结构(push)
    }

    public static class HistoryMessage extends RTMMessage //历史消息结构
    {
        public long cursorId;       //历史消息的索引id
    }

    //getmsg单条消息结构
    public static class SingleMessage extends RTMAnswer{
        public long cusorId; //消息的索引id
        public byte messageType;  ////消息类型 常规聊天类型见 RTMcore enum MessageType 用户可以自定义messagetype 51-127
        public String stringMessage; //二进制数据
        public byte[] binaryMessage;//文本数据
        public String attrs;    //消息的附加属性信息(客户端自定义)
        public long modifiedTime;   //服务器应答时间
        public FileStruct fileInfo; //文件类型结构 messageType为FileMessageType的都存在这里
    }

    public static class TranslatedInfo extends RTMAnswer//聊天消息结构
    {
        public String source; //原语言
        public String target; //翻译的目标语言
        public String sourceText; //原文本
        public String targetText; //设置自动翻译后的目标文本
    }

    public static class CheckResult extends RTMAnswer{ //文本/语音/图片/视频检测结构
        public int result = -1;      //检测结果 0-通过 2-不通过
        public String text;     //(只对文本检测接口)返回text，文本内容,含有的敏感词会被替换为*，如果检测通过,则无此字段
        public List<String> wlist; //(只对文本检测接口)敏感词列表
        public List<Integer> tags; //触发的分类，比如涉黄涉政等等，(只有审核结果不通过才有此值)
    }

    public static class GroupInfoStruct extends RTMAnswer{
        public String  publicInfo; //群组/房间公开信息
        public String  privateInfo; //群组/房间私有信息
    }


    public static class AudioTextStruct extends RTMAnswer{
        public String  text; //语音转文字的结果
        public String  lang; //语音转文字的语言
    }


    public static class ModifyTimeStruct extends RTMAnswer{
        public long  modifyTime; //服务器返回时间
        public long  messageId = 0; //消息id
    }

    //历史消息结果 需要循环调用
    public static class HistoryMessageResult extends RTMAnswer{
        public int count;   //实际返回消息条数
        public long lastId; //最后一条消息的索引id
        public long beginMsec; //开始时间戳(毫秒)
        public long endMsec;    //结束时间戳(毫秒)
        public List<HistoryMessage> messages; //历史消息详细信息结构集合
    }

    public static class MembersStruct extends RTMAnswer{
        public  HashSet<Long> uids;
    }

    public static class DataInfo extends RTMAnswer{
        public String info;
    }

    public static class AttrsStruct extends RTMAnswer{
        public List<Map<String, String>> attrs;
        //  map中自动添加如下几个参数：
        //  ce：链接的endpoint，需要让其下线可以调用kickout
        //  login：登录时间，utc时间戳
        //  my：当前链接的attrs
    }

    public static class PublicInfo extends RTMAnswer{
        public Map<String, String> publicInfos; //群组/房间/个人的公开信息 key -用户id/群组id/房间id
    }

    public static class FileStruct{ //serverpush的文件结构
        public String url;     //文件的url地址 图片/语音/视频/普通文件
        public long fileSize;  //文件大小(字节)
        public boolean isRTMaudio = false; //是否是rtm语音消息
        public String lang;    //语言 如果是rtm语音消息 会有此值
        public int duration;   //语音长度(毫秒) 如果是rtm语音消息 会有此值
        public String codec;   //语音编码 如果是rtm语音消息 会有此值
        public int srate;       //语音采样率 如果是rtm语音消息 会有此值
    }
}

~~~
