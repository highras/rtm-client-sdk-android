package com.rtmsdk;
import com.fpnn.sdk.ErrorRecorder;
import com.fpnn.sdk.TCPClient;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import  com.rtmsdk.DuplicatedMessageFilter.MessageCategories;

class RTMQuestProcessor extends RTMCore{
    private RTMPushProcessor questProcessor;
    private DuplicatedMessageFilter duplicatedFilter;
    private ErrorRecorder errorRecorder;
    private AtomicLong lastPingTime;
    private TCPClient refRtmGate;

    public RTMQuestProcessor() {
        duplicatedFilter = new DuplicatedMessageFilter();
        lastPingTime = new AtomicLong();
    }

    void setAnswerTCPclient(TCPClient client) {
        refRtmGate = client;
    }

    void SetProcessor(RTMPushProcessor processor) {
        questProcessor = processor;
    }

    void SetErrorRecorder(ErrorRecorder recorder) {
        errorRecorder = recorder;
    }

    synchronized void setLastPingTime(long time){
        lastPingTime.set(time);
    }

    synchronized long getLastPingTime(){
        return lastPingTime.get();
    }

    boolean ConnectionIsAlive() {
        long lastPingSec = lastPingTime.get();
        boolean ret = true;

        if (RTMUtils.getCurrentSeconds() - lastPingSec > RTMConfig.lostConnectionAfterLastPingInSeconds) {
            ret = false;
        }
        return ret;
    }

    void rtmConnectClose(int ClosedByErrorCode) {
        if (questProcessor != null)
            questProcessor.rtmConnectClose(ClosedByErrorCode);
    }

    //----------------------[ RTM Operations ]-------------------//
    Answer ping(Quest quest, InetSocketAddress peer) {
        long now = RTMUtils.getCurrentSeconds();
        lastPingTime.set(now);
        return new Answer(quest);
    }

    Answer kickout(Quest quest, InetSocketAddress peer) {
        setCloseType(CloseType.ByServer);
        refRtmGate.close();
        if (questProcessor != null)
            questProcessor.kickout();
        return null;
    }

    Answer kickoutRoom(Quest quest, InetSocketAddress peer) {
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
    private TranslatedInfo processChatMessage(Quest quest, StringBuilder message) {
        Object ret = quest.want("msg");
        Map<String, String> msg = new HashMap<>((Map<String, String>) ret);
        TranslatedInfo tm = new TranslatedInfo();
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
    Answer pushmsg(Quest quest, InetSocketAddress peer){
        refRtmGate.sendAnswer(new Answer(quest));
        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long to = quest.wantLong("to");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(MessageCategories.P2PMessage, from, mid))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");

        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        RTMMessage userMsg = new RTMMessage();
        userMsg.attrs = attrs;
        userMsg.fromUid = from;
        userMsg.modifiedTime = mtime;
        userMsg.messageType = mtype;
        userMsg.messageId = mid;
        userMsg.toId = to;

        if (mtype == MessageType.CHAT) {
            StringBuilder orginialMessage = new StringBuilder();
            userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
            questProcessor.pushChat(userMsg);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageType.CMD) {
            userMsg.stringMessage = messageInfo.message;
            questProcessor.pushCmd(userMsg);
        } else if (mtype >= MessageType.IMAGEFILE && mtype <= MessageType.NORMALFILE) {
            FileStruct fileInfo = new FileStruct();
            userMsg.fileInfo = fileInfo;
            String fileRecieve = quest.getString("msg");
            try {
                JSONObject kk = new JSONObject(fileRecieve);
                fileInfo.url = kk.optString("url");
                fileInfo.fileSize = kk.getLong("size");
                if (kk.has("surl"))
                    fileInfo.surl = kk.optString("surl");
                JSONObject tt = new JSONObject(attrs);
                if (mtype == MessageType.AUDIOFILE) {
                    if (tt.has("rtm")){
                        JSONObject rtmjson = tt.getJSONObject("rtm");
                        if (rtmjson.has("type") && rtmjson.getString("type").equals("audiomsg")) {//rtm语音消息
                            JSONObject fileAttrs = tt.getJSONObject("rtm");
                            userMsg.fileInfo.isRTMaudio = true;
                            userMsg.fileInfo.lang = fileAttrs.optString("lang");
                            userMsg.fileInfo.duration = fileAttrs.optInt("duration");
                            userMsg.fileInfo.codec = fileAttrs.optString("codec");
                            userMsg.fileInfo.srate = fileAttrs.optInt("srate");
                        }
                    }
                }
                String realAttrs = "";
                if (tt.has("custom")) {
                    try {
                        JSONObject custtomObject = tt.getJSONObject("custom");
                        realAttrs = custtomObject.toString();
                    } catch (Exception ex) {
                        realAttrs = "";
                    }
                    userMsg.attrs = realAttrs;
                }
            } catch (JSONException e) {
                ErrorRecorder.record("pushmsg parse json error " + e.getMessage());
            }
            questProcessor.pushFile(userMsg);
        }
        else {
            if (messageInfo.isBinary) {
                userMsg.binaryMessage = messageInfo.binaryData;
                questProcessor.pushMessage(userMsg);
            }
            else {
                userMsg.stringMessage = messageInfo.message;
                questProcessor.pushMessage(userMsg);
            }
        }
        return null;
    }

    Answer pushgroupmsg(Quest quest, InetSocketAddress peer) {
        refRtmGate.sendAnswer(new Answer(quest));

        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long groupId = quest.wantLong("gid");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(MessageCategories.GroupMessage, from, mid, groupId))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");
        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        RTMMessage userMsg = new RTMMessage();
        userMsg.attrs = attrs;
        userMsg.fromUid = from;
        userMsg.modifiedTime = mtime;
        userMsg.messageType = mtype;
        userMsg.messageId = mid;
        userMsg.toId = groupId;

        if (mtype == MessageType.CHAT) {
            StringBuilder orginialMessage = new StringBuilder();
            userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
            questProcessor.pushGroupChat(userMsg);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageType.CMD) {
            userMsg.stringMessage = messageInfo.message;
            questProcessor.pushGroupCmd(userMsg);
        }else if (mtype >= MessageType.IMAGEFILE && mtype <= MessageType.NORMALFILE) {
            FileStruct fileInfo = new FileStruct();
            userMsg.fileInfo = fileInfo;
            String fileRecieve = quest.wantString("msg");
            String fileattrs = quest.wantString("attrs");
            try {
                JSONObject tt = new JSONObject(fileattrs);
                JSONObject kk = new JSONObject(fileRecieve);
                fileInfo.url = kk.optString("url");
                fileInfo.fileSize = kk.getLong("size");
                if (kk.has("surl"))
                    fileInfo.surl = kk.optString("surl");

                if (mtype == MessageType.AUDIOFILE) {
                    if (tt.has("rtm")){
                        JSONObject rtmjson = tt.getJSONObject("rtm");
                        if (rtmjson.has("type") && rtmjson.getString("type").equals("audiomsg")) {//rtm语音消息
                            JSONObject fileAttrs = tt.getJSONObject("rtm");
                            userMsg.fileInfo.isRTMaudio = true;
                            userMsg.fileInfo.lang = fileAttrs.optString("lang");
                            userMsg.fileInfo.duration = fileAttrs.optInt("duration");
                            userMsg.fileInfo.codec = fileAttrs.optString("codec");
                            userMsg.fileInfo.srate = fileAttrs.optInt("srate");
                        }
                    }
                }
                String realAttrs = "";
                if (tt.has("custom")) {
                    try {
                        JSONObject custtomObject = tt.getJSONObject("custom");
                        realAttrs = custtomObject.toString();
                    } catch (JSONException ex) {
                        realAttrs = "";
                    }
                    userMsg.attrs = realAttrs;
                }
            } catch (JSONException e) {
                ErrorRecorder.record("pushgroupmsg parse json error " + e.getMessage());
            }
            questProcessor.pushGroupFile(userMsg);
        }else {
            if (messageInfo.isBinary) {
                userMsg.binaryMessage = messageInfo.binaryData;
                questProcessor.pushGroupMessage(userMsg);
            }
            else {
                userMsg.stringMessage = messageInfo.message;
                questProcessor.pushGroupMessage(userMsg);
            }
        }
        return null;
    }

    Answer pushroommsg(Quest quest, InetSocketAddress peer) {
        refRtmGate.sendAnswer(new Answer(quest));

        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long roomId = quest.wantLong("rid");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(MessageCategories.RoomMessage, from, mid, roomId))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");
        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        RTMMessage userMsg = new RTMMessage();
        userMsg.attrs = attrs;
        userMsg.fromUid = from;
        userMsg.modifiedTime = mtime;
        userMsg.messageType = mtype;
        userMsg.messageId = mid;
        userMsg.toId = roomId;

        if (mtype == MessageType.CHAT) {
            StringBuilder orginialMessage = new StringBuilder();
            userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
            questProcessor.pushRoomChat(userMsg);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageType.CMD) {
            userMsg.stringMessage = messageInfo.message;
            questProcessor.pushRoomCmd(userMsg);
        }else if (mtype >= MessageType.IMAGEFILE && mtype <= MessageType.NORMALFILE) {
            FileStruct fileInfo = new FileStruct();
            userMsg.fileInfo = fileInfo;

            String fileRecieve = quest.wantString("msg");
            String fileattrs = quest.wantString("attrs");
            try {
                JSONObject tt = new JSONObject(fileattrs);
                JSONObject kk = new JSONObject(fileRecieve);
                fileInfo.url = kk.getString("url");
                fileInfo.fileSize = kk.getLong("size");
                if (kk.has("surl"))
                    fileInfo.surl = kk.getString("surl");

                if (mtype == MessageType.AUDIOFILE) {
                    if (tt.has("rtm")){
                        JSONObject rtmjson = tt.getJSONObject("rtm");
                        if (rtmjson.has("type") && rtmjson.optString("type").equals("audiomsg")) {//rtm语音消息
                            JSONObject fileAttrs = tt.getJSONObject("rtm");
                            userMsg.fileInfo.isRTMaudio = true;
                            userMsg.fileInfo.lang = fileAttrs.optString("lang");
                            userMsg.fileInfo.duration = fileAttrs.optInt("duration");
                            userMsg.fileInfo.codec = fileAttrs.optString("codec");
                            userMsg.fileInfo.srate = fileAttrs.optInt("srate");
                        }
                    }
                }
                String realAttrs = "";
                if (tt.has("custom")) {
                    try {
                        JSONObject custtomObject = tt.getJSONObject("custom");
                        realAttrs = custtomObject.toString();
                    } catch (JSONException ex) {
                        realAttrs = "";
                    }
                    userMsg.attrs = realAttrs;
                }
            } catch (JSONException e) {
                ErrorRecorder.record("pushroommsg parse json error " + e.getMessage());
            }
            questProcessor.pushRoomFile(userMsg);
        }else {
            if (messageInfo.isBinary) {
                userMsg.binaryMessage = messageInfo.binaryData;
                questProcessor.pushRoomMessage(userMsg);
            }
            else {
                userMsg.stringMessage = messageInfo.message;
                questProcessor.pushRoomMessage(userMsg);
            }
        }
        return null;
    }

    Answer pushbroadcastmsg(Quest quest, InetSocketAddress peer) {
        refRtmGate.sendAnswer(new Answer(quest));

        if (questProcessor == null)
            return null;

        long from = quest.wantLong("from");
        long mid = quest.wantLong("mid");

        if (!duplicatedFilter.CheckMessage(MessageCategories.BroadcastMessage, from, mid))
            return null;

        byte mtype = (byte) quest.wantInt("mtype");
        String attrs = quest.wantString("attrs");
        long mtime = quest.wantLong("mtime");

        RTMMessage userMsg = new RTMMessage();
        userMsg.attrs = attrs;
        userMsg.fromUid = from;
        userMsg.modifiedTime = mtime;
        userMsg.messageType = mtype;
        userMsg.messageId = mid;

        if (mtype == MessageType.CHAT) {
            StringBuilder orginialMessage = new StringBuilder();
            userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
            questProcessor.pushBroadcastChat(userMsg);
            return null;
        }

        MessageInfo messageInfo = BuildMessageInfo(quest);
        if (mtype == MessageType.CMD) {
            userMsg.stringMessage = messageInfo.message;
            questProcessor.pushBroadcastCmd(userMsg);
        } else if (mtype >= MessageType.IMAGEFILE && mtype <= MessageType.NORMALFILE) {
            FileStruct fileInfo = new FileStruct();
            userMsg.fileInfo = fileInfo;

            String fileRecieve = quest.wantString("msg");
            String fileattrs = quest.wantString("attrs");
            try {
                JSONObject tt = new JSONObject(fileattrs);
                JSONObject kk = new JSONObject(fileRecieve);
                fileInfo.url = kk.optString("url");
                fileInfo.fileSize = kk.getLong("size");
                if (kk.has("surl"))
                    fileInfo.surl = kk.optString("surl");

                if (mtype == MessageType.AUDIOFILE) {
                    if (tt.has("rtm")){
                        JSONObject rtmjson = tt.getJSONObject("rtm");
                        if (rtmjson.has("type") && rtmjson.optString("type").equals("audiomsg")) {//rtm语音消息
                            JSONObject fileAttrs = tt.getJSONObject("rtm");
                            userMsg.fileInfo.isRTMaudio = true;
                            userMsg.fileInfo.lang = fileAttrs.optString("lang");
                            userMsg.fileInfo.duration = fileAttrs.optInt("duration");
                            userMsg.fileInfo.codec = fileAttrs.optString("codec");
                            userMsg.fileInfo.srate = fileAttrs.optInt("srate");
                        }
                    }
                }
                String realAttrs = "";
                if (tt.has("custom")) {
                    try {
                        JSONObject custtomObject = tt.getJSONObject("custom");
                        realAttrs = custtomObject.toString();
                    } catch (JSONException ex) {
                        realAttrs = "";
                    }
                    userMsg.attrs = realAttrs;
                }
            } catch (JSONException e) {
                ErrorRecorder.record("pushbroadcastmsg parse json error " + e.getMessage());
            }
            questProcessor.pushBroadcastFile(userMsg);
        }else {
            if (messageInfo.isBinary) {
                userMsg.binaryMessage =  messageInfo.binaryData;
                questProcessor.pushBroadcastMessage(userMsg);
            }
            else {
                userMsg.stringMessage = messageInfo.message;
                questProcessor.pushBroadcastMessage(userMsg);
            }
        }
        return null;
    }
}

