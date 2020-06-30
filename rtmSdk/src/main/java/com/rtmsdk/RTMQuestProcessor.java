package com.rtmsdk;
import com.fpnn.sdk.ErrorRecorder;
import com.fpnn.sdk.TCPClient;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.TranslatedMessage;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.rtmsdk.RTMMessageCore.MessageMType_Audio;
import static com.rtmsdk.RTMMessageCore.MessageMType_Chat;
import static com.rtmsdk.RTMMessageCore.MessageMType_Cmd;
import static com.rtmsdk.RTMMessageCore.MessageMType_FileEnd;
import static com.rtmsdk.RTMMessageCore.MessageMType_FileStart;

class RTMQuestProcessor {
    private IRTMQuestProcessor questProcessor;
    private DuplicatedMessageFilter duplicatedFilter;
    private ErrorRecorder errorRecorder;
    private AtomicLong lastPingTime;
    private TCPClient refRtmGate;

    public RTMQuestProcessor() {
        duplicatedFilter = new DuplicatedMessageFilter();
        lastPingTime = new AtomicLong();
    }

    public void setAnswerTCPclient(TCPClient client) {
        refRtmGate = client;
    }

    public void SetProcessor(IRTMQuestProcessor processor) {
        questProcessor = processor;
    }

    public void SetErrorRecorder(ErrorRecorder recorder) {
        errorRecorder = recorder;
    }

    public boolean ConnectionIsAlive() {
        long lastPingSec = lastPingTime.get();
        boolean ret = true;

        if (RTMUtils.getCurrentSeconds() - lastPingSec > RTMConfig.lostConnectionAfterLastPingInSeconds) {
            ret = false;
        }
        return ret;
    }

    public void sessionClosed(int ClosedByErrorCode) {
        if (questProcessor != null)
            questProcessor.sessionClosed(ClosedByErrorCode);
    }

    //----------------------[ RTM Operations ]-------------------//
    public Answer ping(Quest quest, InetSocketAddress peer) {
        long now = RTMUtils.getCurrentSeconds();
        lastPingTime.set(now);
        return new Answer(quest);
    }

    public Answer kickout(Quest quest, InetSocketAddress peer) {
        refRtmGate.close();
        if (questProcessor != null)
            questProcessor.kickout();
        return null;
    }

    public Answer kickoutRoom(Quest quest, InetSocketAddress peer) {
        if (questProcessor != null) {
            long roomId = (long) quest.get("rid");
            questProcessor.kickoutRoom(roomId);
        }

        return null;
    }

    private static class MessageInfo {
        public boolean isBinary;
        public byte[] binaryData;
        public String message;

        MessageInfo() {
            isBinary = false;
            message = "";
            binaryData = null;
        }
    }

    //----------------------[ RTM Messagess Utilities ]-------------------//
    private TranslatedMessage processChatMessage(Quest quest, StringBuilder message) {
        Object ret = quest.want("msg");
        Map<String, String> msg = new HashMap<>((Map<String, String>) ret);
        TranslatedMessage tm = new TranslatedMessage();
        tm.source = msg.get("source");
        tm.target = msg.get("target");
        tm.sourceText = msg.get("sourceText");
        tm.targetText = msg.get("targetText");
        return tm;
    }

    private MessageInfo BuildMessageInfo(Quest quest) {
        MessageInfo info = new MessageInfo();

        Object obj = quest.want("msg");
        if (obj instanceof byte[]) {
            info.isBinary = true;
            info.binaryData = (byte[]) obj;
        } else
            info.message = (String) obj;

        return info;
    }

    //----------------------[ RTM Messagess ]-------------------//
    public Answer pushmsg(Quest quest, InetSocketAddress peer) throws UnsupportedEncodingException {
        refRtmGate.sendAnswer(new Answer(quest));
        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long to = quest.wantLong("to");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.P2PMessage, from, mid))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");
        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        if (mtype == MessageMType_Chat) {
            StringBuilder orginialMessage = new StringBuilder();
            TranslatedMessage tm = processChatMessage(quest, orginialMessage);
            questProcessor.pushChat(from, to, mid, tm, attrs, mtime);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageMType_Audio) {
            byte[] audioData = messageInfo.binaryData;
            if (!messageInfo.isBinary)
                audioData = (messageInfo.message).getBytes("iso8859-1");
            byte[] data = RTMAudio.unpackAudioData(audioData);

            questProcessor.pushAudio(from, to, mid, data, attrs, mtime);
            return null;
        }

        if (mtype == MessageMType_Cmd)
            questProcessor.pushCmd(from, to, mid, messageInfo.message, attrs, mtime);
        else if (mtype >= MessageMType_FileStart && mtype <= MessageMType_FileStart)
            questProcessor.pushFile(from, to, mtype, mid, messageInfo.message, attrs, mtime);
        else {
            if (messageInfo.isBinary)
                questProcessor.pushMessage(from, to, mtype, mid, messageInfo.binaryData, attrs, mtime);
            else
                questProcessor.pushMessage(from, to, mtype, mid, messageInfo.message, attrs, mtime);
        }
        return null;
    }

    public Answer pushgroupmsg(Quest quest, InetSocketAddress peer) throws UnsupportedEncodingException {
        refRtmGate.sendAnswer(new Answer(quest));

        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long groupId = quest.wantLong("gid");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.GroupMessage, from, mid, groupId))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");
        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        if (mtype == MessageMType_Chat) {
            StringBuilder orginialMessage = new StringBuilder();
            TranslatedMessage tm = processChatMessage(quest, orginialMessage);
            questProcessor.pushGroupChat(from, groupId, mid, tm, attrs, mtime);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageMType_Audio) {
            byte[] audioData = messageInfo.binaryData;
            if (!messageInfo.isBinary)
                audioData = (messageInfo.message).getBytes("iso8859-1");
            byte[] data = RTMAudio.unpackAudioData(audioData);

            questProcessor.pushGroupAudio(from, groupId, mid, data, attrs, mtime);
            return null;
        }

        if (mtype == MessageMType_Cmd) {
            questProcessor.pushGroupCmd(from, groupId, mid, messageInfo.message, attrs, mtime);
        } else if (mtype >= MessageMType_FileStart && mtype <= MessageMType_FileEnd) {
            questProcessor.pushGroupFile(from, groupId, mtype, mid, messageInfo.message, attrs, mtime);
        } else {
            if (messageInfo.isBinary)
                questProcessor.pushGroupMessage(from, groupId, mtype, mid, messageInfo.binaryData, attrs, mtime);
            else
                questProcessor.pushGroupMessage(from, groupId, mtype, mid, messageInfo.message, attrs, mtime);
        }

        return null;
    }

    public Answer pushroommsg(Quest quest, InetSocketAddress peer) throws UnsupportedEncodingException {
        refRtmGate.sendAnswer(new Answer(quest));

        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long roomId = quest.wantLong("rid");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.RoomMessage, from, mid, roomId))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");
        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        if (mtype == MessageMType_Chat) {
            StringBuilder orginialMessage = new StringBuilder();
            TranslatedMessage tm = processChatMessage(quest, orginialMessage);
            questProcessor.pushRoomChat(from, roomId, mid, tm, attrs, mtime);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageMType_Audio) {
            byte[] audioData = messageInfo.binaryData;
            if (!messageInfo.isBinary)
                audioData = (messageInfo.message).getBytes("iso8859-1");
            byte[] data = RTMAudio.unpackAudioData(audioData);

            questProcessor.pushRoomAudio(from, roomId, mid, data, attrs, mtime);
            return null;
        }

        if (mtype == MessageMType_Cmd) {
            questProcessor.pushRoomCmd(from, roomId, mid, messageInfo.message, attrs, mtime);
        } else if (mtype >= MessageMType_FileStart && mtype <= MessageMType_FileEnd) {
            questProcessor.pushRoomFile(from, roomId, mtype, mid, messageInfo.message, attrs, mtime);
        } else {
            if (messageInfo.isBinary)
                questProcessor.pushRoomMessage(from, roomId, mtype, mid, messageInfo.binaryData, attrs, mtime);
            else
                questProcessor.pushRoomMessage(from, roomId, mtype, mid, messageInfo.message, attrs, mtime);
        }

        return null;
    }

    public Answer pushbroadcastmsg(Quest quest, InetSocketAddress peer) throws UnsupportedEncodingException {
        refRtmGate.sendAnswer(new Answer(quest));

        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.BroadcastMessage, from, mid))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");
        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        if (mtype == MessageMType_Chat) {
            StringBuilder orginialMessage = new StringBuilder();
            TranslatedMessage tm = processChatMessage(quest, orginialMessage);
            questProcessor.pushBroadcastChat(from, mid, tm, attrs, mtime);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageMType_Audio) {
            byte[] audioData = messageInfo.binaryData;
            if (!messageInfo.isBinary)
                audioData = (messageInfo.message).getBytes("iso8859-1");
            byte[] data = RTMAudio.unpackAudioData(audioData);

            questProcessor.pushBroadcastAudio(from, mid, data, attrs, mtime);

            return null;
        }

        if (mtype == MessageMType_Cmd) {
            questProcessor.pushBroadcastCmd(from, mid, messageInfo.message, attrs, mtime);
        } else if (mtype >= MessageMType_FileStart && mtype <= MessageMType_FileEnd) {
            questProcessor.pushBroadcastFile(from, mtype, mid, messageInfo.message, attrs, mtime);
        } else {
            if (messageInfo.isBinary)
                questProcessor.pushBroadcastMessage(from, mtype, mid, messageInfo.binaryData, attrs, mtime);
            else
                questProcessor.pushBroadcastMessage(from, mtype, mid, messageInfo.message, attrs, mtime);
        }
        return null;
    }
}

