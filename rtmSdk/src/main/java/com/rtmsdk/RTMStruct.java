package com.rtmsdk;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class RTMStruct {
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
        public static final byte AUDIO = 31;//语音
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
        public long toId;       //目标id 根据messagetype 有可能是uid/gid/rid
        public byte messageType;  //消息类型 常规聊天类型见 RTMcore enum MessageType 用户可以自定义messagetype 51-127
        public long messageId;  //消息id
        public String stringMessage; //字符串消息
        public byte[] binaryMessage; //二进制消息 messageType为语音
        public String attrs;        //
        public long modifiedTime;   //服务器处理返回时间
        public AudioInfo audioInfo = null; //语音消息结构 实际语音消息需要再次调用getchat获得
        public TranslatedInfo translatedInfo = null; //聊天信息结构(push)
    }

    public static class HistoryMessage extends RTMMessage //历史消息结构
    {
        public long cursorId;       //历史消息的索引id
    }

    //getmsg单条消息结构
    public static class SingleMessage{
        public long messageId; //消息id
        public byte messageType;  ////消息类型 常规聊天类型见 RTMcore enum MessageType 用户可以自定义messagetype 51-127
        public String stringMessage; //二进制数据
        public byte[] binaryMessage;//文本数据
        public String attrs;    //属性值
        public long modifiedTime;   //服务器应答时间
    }

    public static class AudioInfo
    {
        public String sourceLanguage;   //语音发送者的语种
        public String recognizedLanguage; //设置了自动语音识别的情况下，返回识别后的语种
        public String recognizedText; //设置了自动语音识别的情况下，返回识别后的内容
        public int duration; //音长度，毫秒
    }

    public static class TranslatedInfo extends RTMAnswer//聊天消息结构
    {
        public String source; //原语言
        public String target; //翻译的目标语言
        public String sourceText; //原文本
        public String targetText; //设置自动翻译后的目标文本
    }

    public static class ProfanityStruct extends RTMAnswer{ //敏感词过滤结构
        public  String  text; //敏感词过滤后的文本内容，含有的敏感词会被替换为*
        public List<String>    classification;//文本分类结果
    }

    public static class TranscribeStruct extends RTMAnswer{ //敏感词过滤结构
        public String  resultText; //语音识别后的文本
        public String  resultLang; //语音识别后的语言
    }

    public static class GroupInfoStruct extends RTMAnswer{
        public String  publicInfo; //群组/房间公开信息
        public String  privateInfo; //群组/房间私有信息
    }


    public static class ModifyTimeStruct extends RTMAnswer{
        public long  modifyTime; //服务器返回时间
        public long  messageId = 0; //消息id
    }


    public static class TimeOutStruct {
        public int timeout;
        public long lastActionTimestamp;

        TimeOutStruct(int timeout, long lastActionTimestamp) {
            this.timeout = timeout;
            this.lastActionTimestamp = lastActionTimestamp;
        }
    }

    //历史消息结果 需要循环调用
    public static class HistoryMessageResult extends RTMAnswer{
        public int count;   //实际返回消息条数
        public long lastId; //最后一条消息id(下一轮查询请使用此值)
        public long beginMsec; //开始时间戳(毫秒)(下一轮查询的请使用此值)
        public long endMsec;    //结束时间戳(毫秒)(下一轮查询请使用此值)
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
    }

    public static class UserPublicInfo extends RTMAnswer{
        public Map<String, String> userInfo;
    }
}
