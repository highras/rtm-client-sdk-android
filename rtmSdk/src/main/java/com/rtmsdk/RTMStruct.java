package com.rtmsdk;

import java.util.List;

public class RTMStruct {
    public static class LongMtime {
        public long mtime = 0;
    }

    public static class TimeOutStruct {
        public int timeout;
        public long lastActionTimestamp;

        TimeOutStruct(int timeout, long lastActionTimestamp) {
            this.timeout = timeout;
            this.lastActionTimestamp = lastActionTimestamp;
        }
    }

    //message消息结构
    public static class RetrievedMessage {
        public long id;
        public byte mtype;  //消息类型
        public String stringMessage; //二进制数据
        public byte[] binaryMessage;//文本数据
        public String attrs;    //属性值
        public long mtime;      //服务器应答时间
    }

    //翻译结构
    public static class TranslatedMessage {
        public String source;   //源语言
        public String target;   //目标语言
        public String sourceText;   //原文本
        public String targetText;   //翻译后文本
    }

    //单条历史消息详情结构
    public static class HistoryMessage {
        public long id;
        public long fromUid; //发起用户id(可以是自己的uid)
        public byte mtype;   //消息类型
        public long mid;    //消息序号
        public String message;//文本数据
        public byte[] binaryMessage; //二进制数据
        public String attrs;//属性值
        public long mtime;//服务器应答时间
    }

    //历史消息结果
    public static class HistoryMessageResult {
        public int count;   //历史消息总数量
        public long lastId; //最后一条消息id
        public long beginMsec; //开始时间戳(毫秒)
        public long endMsec;    //结束时间戳(毫秒)
        public List<HistoryMessage> messages; //历史消息结构集合
    }
}
