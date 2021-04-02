package com.rtmsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.fpnn.sdk.*;
import com.fpnn.sdk.proto.*;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;
import com.rtmsdk.RTMStruct.RTMAnswer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

class RTMCore  extends BroadcastReceiver implements INetEvent{
    public enum ClientStatus {
        Closed,
        Connecting,
        Connected
    }

    public enum CloseType {
        ByUser,
        ByServer,
        Timeout,
        None
    }

    /**for network change**/
    private int LAST_TYPE = -3;

    @Override
    public void onReceive(Context context, Intent intent) {
        Object b= ConnectivityManager.CONNECTIVITY_ACTION;
        Object a= intent.getAction();
        if ((a == b) || (a != null && a.equals(b))) {
            int netWorkState = NetUtils.getNetWorkState(context);
            if (LAST_TYPE != netWorkState) {
                LAST_TYPE = netWorkState;
                onNetChange(netWorkState);
            }
        }
    }
    /**for network change**/

    //-------------[ Fields ]--------------------------//
    private final Object interLocker =  new Object();
    private long pid;
    private long uid;
    private String lang;
    private String token;
    private String curve;
    private Context context;
    private byte[] encrptyData;
    private boolean autoConnect;

    private Map<String, String>  loginAttrs;
    private ClientStatus status = ClientStatus.Closed;
    private CloseType closedCase = CloseType.None;
    private int lastNetType = NetUtils.NETWORK_NOTINIT;
    private AtomicBoolean isRelogin = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(true);
    private AtomicBoolean initCheckThread = new AtomicBoolean(false);
    private Thread checkThread;
    private RTMQuestProcessor processor;
    ErrorRecorder errorRecorder = new ErrorRecorder();
    private TCPClient dispatch;
    private TCPClient rtmGate;
    private Map<String, Map<TCPClient, Long>> fileGates;
    private AtomicLong connectionId = new AtomicLong(0);
    private AtomicBoolean noNetWorkNotify = new AtomicBoolean(false);
    private RTMAnswer lastReloginAnswer = new RTMAnswer();
    private RTMPushProcessor serverPushProcessor;
    RTMConfig rtmConfig;
    RTMUtils rtmUtils = new RTMUtils();

    private ArrayList<Integer> finishCodeList = new ArrayList<Integer>(){{
            add(RTMErrorCode.RTM_EC_INVALID_AUTH_TOEKN.value());
            add(RTMErrorCode.RTM_EC_PROJECT_BLACKUSER.value()); }};

    class RTMQuestProcessor{
        private DuplicatedMessageFilter duplicatedFilter =  new DuplicatedMessageFilter();
        private AtomicLong lastPingTime  = new AtomicLong();;

        synchronized void setLastPingTime(long time){
            lastPingTime.set(time);
        }

        synchronized long getLastPingTime(){
            return lastPingTime.get();
        }

        boolean ConnectionIsAlive() {
            long lastPingSec = lastPingTime.get();
            boolean ret = true;

            if (Genid.getCurrentSeconds() - lastPingSec > rtmConfig.lostConnectionAfterLastPingInSeconds) {
                ret = false;
            }
            return ret;
        }

        void rtmConnectClose() {
            if (serverPushProcessor != null)
                serverPushProcessor.rtmConnectClose(getUid());
        }

        //----------------------[ RTM Operations ]-------------------//
        Answer ping(Quest quest, InetSocketAddress peer) {
            long now = Genid.getCurrentSeconds();
            lastPingTime.set(now);
            return new Answer(quest);
        }

        Answer kickout(Quest quest, InetSocketAddress peer) {
            setCloseType(CloseType.ByServer);
            close();
            serverPushProcessor.kickout();
            return null;
        }

        Answer kickoutRoom(Quest quest, InetSocketAddress peer) {
            long roomId = (long) quest.get("rid");
            serverPushProcessor.kickoutRoom(roomId);
            return null;
        }

        class MessageInfo {
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
        private RTMStruct.TranslatedInfo processChatMessage(Quest quest, StringBuilder message) {
            Object ret = quest.want("msg");
            Map<String, String> msg = new HashMap<>((Map<String, String>) ret);
            RTMStruct.TranslatedInfo tm = new RTMStruct.TranslatedInfo();
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
                info.message = String.valueOf(obj);
//                info.message = new Str(obj);

            return info;
        }

        //----------------------[ RTM Messagess ]-------------------//
        Answer pushmsg(Quest quest, InetSocketAddress peer){
            rtmGate.sendAnswer(new Answer(quest));

            long from = rtmUtils.wantLong(quest,"from");
            long to = rtmUtils.wantLong(quest,"to");
            long mid = rtmUtils.wantLong(quest,"mid");

            if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.P2PMessage, from, mid))
                return null;

            byte mtype = (byte) rtmUtils.wantInt(quest,"mtype");
            String attrs = rtmUtils.wantString(quest,"attrs");
            long mtime = rtmUtils.wantLong(quest,"mtime");

            RTMStruct.RTMMessage userMsg = new RTMStruct.RTMMessage();
            userMsg.attrs = attrs;
            userMsg.fromUid = from;
            userMsg.modifiedTime = mtime;
            userMsg.messageType = mtype;
            userMsg.messageId = mid;
            userMsg.toId = to;

            if (mtype == RTMStruct.MessageType.CHAT) {
                StringBuilder orginialMessage = new StringBuilder();
                userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
                serverPushProcessor.pushChat(userMsg);
                return null;
            }

            MessageInfo messageInfo = BuildMessageInfo(quest);
            if (mtype == RTMStruct.MessageType.CMD) {
                userMsg.stringMessage = messageInfo.message;
                serverPushProcessor.pushCmd(userMsg);
            } else if (mtype >= RTMStruct.MessageType.IMAGEFILE && mtype <= RTMStruct.MessageType.NORMALFILE) {
                RTMStruct.FileStruct fileInfo = new RTMStruct.FileStruct();
                userMsg.fileInfo = fileInfo;
                String fileRecieve = quest.getString("msg");
                try {
                    JSONObject kk = new JSONObject(fileRecieve);
                    fileInfo.url = kk.optString("url");
                    fileInfo.fileSize = kk.getLong("size");
                    if (kk.has("surl"))
                        fileInfo.surl = kk.optString("surl");
                    JSONObject tt = new JSONObject(attrs);
                    if (mtype == RTMStruct.MessageType.AUDIOFILE) {
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
                    errorRecorder.recordError("pushmsg parse json error " + e.getMessage());
                }
                serverPushProcessor.pushFile(userMsg);
            }
            else {
                if (messageInfo.isBinary) {
                    userMsg.binaryMessage = messageInfo.binaryData;
                    serverPushProcessor.pushMessage(userMsg);
                }
                else {
                    userMsg.stringMessage = messageInfo.message;
                    serverPushProcessor.pushMessage(userMsg);
                }
            }
            return null;
        }

        Answer pushgroupmsg(Quest quest, InetSocketAddress peer) {
            rtmGate.sendAnswer(new Answer(quest));

            long from = rtmUtils.wantLong(quest,"from");
            long groupId = rtmUtils.wantLong(quest,"gid");
            long mid = rtmUtils.wantLong(quest,"mid");

            if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.GroupMessage, from, mid, groupId))
                return null;

            byte mtype = (byte) rtmUtils.wantInt(quest,"mtype");
            String attrs = rtmUtils.wantString(quest,"attrs");
            long mtime = rtmUtils.wantLong(quest,"mtime");

            RTMStruct.RTMMessage userMsg = new RTMStruct.RTMMessage();
            userMsg.attrs = attrs;
            userMsg.fromUid = from;
            userMsg.modifiedTime = mtime;
            userMsg.messageType = mtype;
            userMsg.messageId = mid;
            userMsg.toId = groupId;

            if (mtype == RTMStruct.MessageType.CHAT) {
                StringBuilder orginialMessage = new StringBuilder();
                userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
                serverPushProcessor.pushGroupChat(userMsg);
                return null;
            }

            MessageInfo messageInfo = BuildMessageInfo(quest);
            if (mtype == RTMStruct.MessageType.CMD) {
                userMsg.stringMessage = messageInfo.message;
                serverPushProcessor.pushGroupCmd(userMsg);
            }else if (mtype >= RTMStruct.MessageType.IMAGEFILE && mtype <= RTMStruct.MessageType.NORMALFILE) {
                RTMStruct.FileStruct fileInfo = new RTMStruct.FileStruct();
                userMsg.fileInfo = fileInfo;
                String fileRecieve = rtmUtils.wantString(quest,"msg");
                String fileattrs = rtmUtils.wantString(quest,"attrs");
                try {
                    JSONObject tt = new JSONObject(fileattrs);
                    JSONObject kk = new JSONObject(fileRecieve);
                    fileInfo.url = kk.optString("url");
                    fileInfo.fileSize = kk.getLong("size");
                    if (kk.has("surl"))
                        fileInfo.surl = kk.optString("surl");

                    if (mtype == RTMStruct.MessageType.AUDIOFILE) {
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
                    errorRecorder.recordError("pushgroupmsg parse json error " + e.getMessage());
                }
                serverPushProcessor.pushGroupFile(userMsg);
            }else {
                if (messageInfo.isBinary) {
                    userMsg.binaryMessage = messageInfo.binaryData;
                    serverPushProcessor.pushGroupMessage(userMsg);
                }
                else {
                    userMsg.stringMessage = messageInfo.message;
                    serverPushProcessor.pushGroupMessage(userMsg);
                }
            }
            return null;
        }

        Answer pushroommsg(Quest quest, InetSocketAddress peer) {
            rtmGate.sendAnswer(new Answer(quest));

            long from = rtmUtils.wantLong(quest,"from");
            long roomId = rtmUtils.wantLong(quest,"rid");
            long mid = rtmUtils.wantLong(quest,"mid");

            if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.RoomMessage, from, mid, roomId))
                return null;

            byte mtype = (byte) rtmUtils.wantInt(quest,"mtype");
            String attrs = rtmUtils.wantString(quest,"attrs");
            long mtime = rtmUtils.wantLong(quest,"mtime");

            RTMStruct.RTMMessage userMsg = new RTMStruct.RTMMessage();
            userMsg.attrs = attrs;
            userMsg.fromUid = from;
            userMsg.modifiedTime = mtime;
            userMsg.messageType = mtype;
            userMsg.messageId = mid;
            userMsg.toId = roomId;

            if (mtype == RTMStruct.MessageType.CHAT) {
                StringBuilder orginialMessage = new StringBuilder();
                userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
                serverPushProcessor.pushRoomChat(userMsg);
                return null;
            }

            MessageInfo messageInfo = BuildMessageInfo(quest);
            if (mtype == RTMStruct.MessageType.CMD) {
                userMsg.stringMessage = messageInfo.message;
                serverPushProcessor.pushRoomCmd(userMsg);
            }else if (mtype >= RTMStruct.MessageType.IMAGEFILE && mtype <= RTMStruct.MessageType.NORMALFILE) {
                RTMStruct.FileStruct fileInfo = new RTMStruct.FileStruct();
                userMsg.fileInfo = fileInfo;

                String fileRecieve = rtmUtils.wantString(quest,"msg");
                String fileattrs = rtmUtils.wantString(quest,"attrs");
                try {
                    JSONObject tt = new JSONObject(fileattrs);
                    JSONObject kk = new JSONObject(fileRecieve);
                    fileInfo.url = kk.getString("url");
                    fileInfo.fileSize = kk.getLong("size");
                    if (kk.has("surl"))
                        fileInfo.surl = kk.getString("surl");

                    if (mtype == RTMStruct.MessageType.AUDIOFILE) {
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
                } catch (Exception e) {
                    errorRecorder.recordError("pushroommsg parse json error " + e.getMessage());
                }
                serverPushProcessor.pushRoomFile(userMsg);
            }else {
                if (messageInfo.isBinary) {
                    userMsg.binaryMessage = messageInfo.binaryData;
                    serverPushProcessor.pushRoomMessage(userMsg);
                }
                else {
                    userMsg.stringMessage = messageInfo.message;
                    serverPushProcessor.pushRoomMessage(userMsg);
                }
            }
            return null;
        }

        Answer pushbroadcastmsg(Quest quest, InetSocketAddress peer) {
            rtmGate.sendAnswer(new Answer(quest));

            long from = rtmUtils.wantLong(quest,"from");
            long mid = rtmUtils.wantLong(quest,"mid");

            if (!duplicatedFilter.CheckMessage(DuplicatedMessageFilter.MessageCategories.BroadcastMessage, from, mid))
                return null;

            byte mtype = (byte) rtmUtils.wantInt(quest,"mtype");
            String attrs = rtmUtils.wantString(quest,"attrs");
            long mtime = rtmUtils.wantLong(quest,"mtime");

            RTMStruct.RTMMessage userMsg = new RTMStruct.RTMMessage();
            userMsg.attrs = attrs;
            userMsg.fromUid = from;
            userMsg.modifiedTime = mtime;
            userMsg.messageType = mtype;
            userMsg.messageId = mid;

            if (mtype == RTMStruct.MessageType.CHAT) {
                StringBuilder orginialMessage = new StringBuilder();
                userMsg.translatedInfo = processChatMessage(quest, orginialMessage);
                serverPushProcessor.pushBroadcastChat(userMsg);
                return null;
            }

            MessageInfo messageInfo = BuildMessageInfo(quest);
            if (mtype == RTMStruct.MessageType.CMD) {
                userMsg.stringMessage = messageInfo.message;
                serverPushProcessor.pushBroadcastCmd(userMsg);
            } else if (mtype >= RTMStruct.MessageType.IMAGEFILE && mtype <= RTMStruct.MessageType.NORMALFILE) {
                RTMStruct.FileStruct fileInfo = new RTMStruct.FileStruct();
                userMsg.fileInfo = fileInfo;

                String fileRecieve = rtmUtils.wantString(quest,"msg");
                String fileattrs = rtmUtils.wantString(quest,"attrs");
                try {
                    JSONObject tt = new JSONObject(fileattrs);
                    JSONObject kk = new JSONObject(fileRecieve);
                    fileInfo.url = kk.optString("url");
                    fileInfo.fileSize = kk.getLong("size");
                    if (kk.has("surl"))
                        fileInfo.surl = kk.optString("surl");

                    if (mtype == RTMStruct.MessageType.AUDIOFILE) {
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
                    errorRecorder.recordError("pushbroadcastmsg parse json error " + e.getMessage());
                }
                serverPushProcessor.pushBroadcastFile(userMsg);
            }else {
                if (messageInfo.isBinary) {
                    userMsg.binaryMessage =  messageInfo.binaryData;
                    serverPushProcessor.pushBroadcastMessage(userMsg);
                }
                else {
                    userMsg.stringMessage = messageInfo.message;
                    serverPushProcessor.pushBroadcastMessage(userMsg);
                }
            }
            return null;
        }
    }


    void reloginEvent(int count){
        if (noNetWorkNotify.get()) {
            isRelogin.set(false);
            serverPushProcessor.reloginCompleted(uid, false, lastReloginAnswer, count);
            return;
        }
//        isRelogin.set(true);
        int num = count;
        Map<String, String> kk = loginAttrs;
        lastReloginAnswer = login(token, lang, kk,"ipv4");
        if (serverPushProcessor.reloginWillStart(uid, lastReloginAnswer, num)) {
            if(lastReloginAnswer.errorCode == ErrorCode.FPNN_EC_OK.value() || lastReloginAnswer.errorCode == RTMErrorCode.RTM_EC_DUPLCATED_AUTH.value()) {
                isRelogin.set(false);
                serverPushProcessor.reloginCompleted(uid, true, lastReloginAnswer, num);
                return;
            }
            else {
                if (finishCodeList.contains(lastReloginAnswer.errorCode)){
                    isRelogin.set(false);
                    serverPushProcessor.reloginCompleted(uid, false, lastReloginAnswer, num);
                    return;
                }
                else {
                    if (num >= serverPushProcessor.internalReloginMaxTimes){
                        isRelogin.set(false);
                        serverPushProcessor.reloginCompleted(uid, false, lastReloginAnswer, num);
                        return;
                    }
                    if (!isRelogin.get()) {
                        serverPushProcessor.reloginCompleted(uid, false, lastReloginAnswer, num);
                        return;
                    }
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        isRelogin.set(false);
                        serverPushProcessor.reloginCompleted(uid, false, lastReloginAnswer, num);
                        return;
                    }
                    reloginEvent(++num);
                }
            }
        }
        else {
            isRelogin.set(false);
            serverPushProcessor.reloginCompleted(uid, false, lastReloginAnswer, --num);
        }
    }

    public void onNetChange(int netWorkState){
        if (lastNetType != NetUtils.NETWORK_NOTINIT) {
            switch (netWorkState) {
                case NetUtils.NETWORK_NONE:
                    noNetWorkNotify.set(true);
//                    if (isRelogin.get()){
//                        isRelogin.set(false);
//                    }
                    break;
                case NetUtils.NETWORK_MOBILE:
                case NetUtils.NETWORK_WIFI:
                    noNetWorkNotify.set(false);
                    if (connectionId.get() == 0)
                        return;
                    if (lastNetType != netWorkState && autoConnect) {
                        if (isRelogin.get())
                            return;

                        isRelogin.set(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (getClientStatus() == ClientStatus.Connected){
                                    Quest quest = new Quest("bye");
                                    sendQuest(quest, new FunctionalAnswerCallback() {
                                        @Override
                                        public void onAnswer(Answer answer, int errorCode) {
                                            close();
                                            try {
                                                Thread.sleep(200);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            reloginEvent(1);
                                        }
                                    }, 5);
                                }
                                else {
                                    reloginEvent(1);
                                }
                            }
                        }).start();
                    }
                    break;
            }
        }
        lastNetType = netWorkState;
    }

    void RTMInit(String endpoint, long pid, long uid, RTMPushProcessor serverPushProcessor, Context applicationContext, RTMConfig config) {
        if (config == null)
            rtmConfig = new RTMConfig();
        else
            rtmConfig = config;

        if (rtmConfig.defaultErrorRecorder != null)
            errorRecorder = rtmConfig.defaultErrorRecorder;
        rtmUtils.errorRecorder = errorRecorder;

        String errDesc = "";
        if (endpoint == null || endpoint.equals("") || endpoint.lastIndexOf(':') == -1)
            errDesc = "invalid endpoint:" + endpoint;
        if (pid <= 0)
            errDesc += " pid is invalid:" + pid;
        if (uid <= 0)
            errDesc += " uid is invalid:" + uid;
        if (serverPushProcessor == null)
            errDesc += " RTMPushProcessor is null";

        if (!errDesc.equals("")) {
            errorRecorder.recordError("rtmclient init error." + errDesc);
            return;
        }

        this.pid = pid;
        this.uid = uid;
        isRelogin.set(false);
        fileGates = new HashMap<>();
        processor = new RTMQuestProcessor();
        this.serverPushProcessor = serverPushProcessor;
        autoConnect = rtmConfig.autoConnect;
        ClientEngine.setMaxThreadInTaskPool(rtmConfig.globalMaxThread);

        try {
            dispatch = TCPClient.create(endpoint, true);
            if (autoConnect) {
                if (applicationContext == null){
                    errorRecorder.recordError("applicationContext is null ");
                    return;
                }
                context = applicationContext;
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                context.registerReceiver(this, intentFilter);
            }
        }
        catch (Exception ex){
            errorRecorder.recordError("RTMInit error ",ex);
            return;
        }
        dispatch.setQuestTimeout(rtmConfig.globalQuestTimeoutSeconds);
        dispatch.setErrorRecorder(errorRecorder);
    }

    public void setErrorRecoder(com.fpnn.sdk.ErrorRecorder value){
        if (value == null)
            return;
        errorRecorder = value;
    }

    public void enableEncryptorByDerData(String curve, byte[] peerPublicKey) {
        this.curve = curve;
        encrptyData = peerPublicKey;
    }

    public void enableEncryptorByDerFile(String curve, String file) {
        this.curve = curve;
        try {
            FileInputStream fis = new FileInputStream(new File(file));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            encrptyData = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (Exception e) {
            errorRecorder.recordError("RTMInit error " + e.getMessage());
        }
    }

     long getPid() {
        return pid;
    }

     long getUid() {
        return uid;
    }

    synchronized ClientStatus getClientStatus() {
        synchronized (interLocker) {
            return status;
        }
    }

    private boolean connectionIsAlive() {
        return processor.ConnectionIsAlive();
    }

    RTMAnswer genRTMAnswer(int errCode){
        return genRTMAnswer(errCode,"");
    }

    RTMAnswer genRTMAnswer(int errCode,String msg)
    {
        RTMAnswer tt = new RTMAnswer();
        tt.errorCode = errCode;
        if (msg.isEmpty())
            tt.errorMsg = RTMErrorCode.getMsg(errCode);
        else
            tt.errorMsg = msg;
        return tt;
    }


    RTMAnswer genRTMAnswer(Answer answer, String msg) {
        if (answer == null)
            return new RTMAnswer(ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value(), "invalid connection");
        return new RTMAnswer(answer.getErrorCode(),answer.getErrorMessage() + " " + msg);
    }


    RTMAnswer genRTMAnswer(Answer answer) {
        if (answer == null)
            return new RTMAnswer(ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value(), "invalid connection");
        return new RTMAnswer(answer.getErrorCode(),answer.getErrorMessage());
    }


    RTMAnswer genRTMAnswer(Answer answer,int errcode) {
        if (answer == null && errcode !=0) {
            if (errcode == ErrorCode.FPNN_EC_CORE_TIMEOUT.value())
                return new RTMAnswer(errcode, "FPNN_EC_CORE_TIMEOUT");
            else
                return new RTMAnswer(errcode,"fpnn  error");
        }
        else
            return new RTMAnswer(answer.getErrorCode(),answer.getErrorMessage());
    }

    private TCPClient getCoreClient() {
        synchronized (interLocker) {
            if (status == ClientStatus.Connected)
                return rtmGate;
            else
                return null;
        }
    }

    Answer sendFileQuest(Quest quest) {
        return sendQuest(quest,rtmConfig.globalFileQuestTimeoutSeconds);
    }

    Answer sendQuest(Quest quest) {
        return sendQuest(quest,rtmConfig.globalQuestTimeoutSeconds);
    }

    Answer sendQuest(Quest quest, int timeout) {
        TCPClient client = getCoreClient();
        if (client == null)
            return null;

        Answer answer;
        try {
            answer = client.sendQuest(quest, timeout);
        } catch (Exception e) {
            errorRecorder.recordError("sendQuest error " + e);
            answer = new Answer(quest);
            answer.fillErrorInfo(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(),e.getMessage());
            Thread.currentThread().interrupt();
        }
        return answer;
    }

    void setCloseType(CloseType type) {
        closedCase = type;
    }

    void sayBye(final IRTMEmptyCallback callback) {
        closedCase = CloseType.ByUser;
        final TCPClient client = getCoreClient();
        if (client == null) {
            close();
            return;
        }
        Quest quest = new Quest("bye");
        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                close();
                callback.onResult(genRTMAnswer(answer,errorCode));
            }
        }, 5);
    }

    void realClose(){
        closedCase = CloseType.ByUser;
        try {
            if (autoConnect)
                context.unregisterReceiver(this);
        } catch ( IllegalArgumentException e){
        }
        close();
    }

     void sayBye(boolean async) {
        closedCase = CloseType.ByUser;
        final TCPClient client = getCoreClient();
        if (client == null) {
            close();
            return;
        }
        Quest quest = new Quest("bye");
        if (async) {
            sendQuest(quest, new FunctionalAnswerCallback() {
                @Override
                public void onAnswer(Answer answer, int errorCode) {
                    close();
                }
            }, 5);
        } else {
            try {
                client.sendQuest(quest,5);
                close();
            } catch (InterruptedException e) {
                close();
            }
        }
    }

    void sendFileQuest(Quest quest, final FunctionalAnswerCallback callback) {
        sendQuest(quest, callback,rtmConfig.globalFileQuestTimeoutSeconds);
    }

    void sendQuest(Quest quest, final FunctionalAnswerCallback callback) {
        sendQuest(quest, callback, rtmConfig.globalQuestTimeoutSeconds);
    }

    void sendQuest(Quest quest, final FunctionalAnswerCallback callback, int timeout) {
        TCPClient client = getCoreClient();
        final Answer answer = new Answer(quest);
        if (client == null) {
            answer.fillErrorInfo(ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value(),"invalid connection");
            callback.onAnswer(answer,answer.getErrorCode());//当前线程
            return;
//            ClientEngine.getThreadPool().execute(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            callback.onAnswer(answer, answer.getErrorCode());
//                        }
//                    });
        }
        if (timeout <= 0)
            timeout = rtmConfig.globalQuestTimeoutSeconds;
        try {
            client.sendQuest(quest, callback, timeout);
        }
        catch (Exception e){
            answer.fillErrorInfo(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(),e.getMessage());
            callback.onAnswer(answer, answer.getErrorCode());
        }
    }

    void sendQuestEmptyCallback(final IRTMEmptyCallback callback, Quest quest) {
        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                callback.onResult(genRTMAnswer(answer,errorCode));
            }
        }, rtmConfig.globalQuestTimeoutSeconds);
    }

    RTMAnswer sendQuestEmptyResult(Quest quest){
        Answer ret =  sendQuest(quest);
        if (ret == null)
            return genRTMAnswer(ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value(),"invalid connection");
        return genRTMAnswer(ret);
    }

    void activeFileGateClient(String endpoint, final TCPClient client) {
        synchronized (interLocker) {
            if (fileGates.containsKey(endpoint)) {
                if (fileGates.get(endpoint) != null)
                    fileGates.get(endpoint).put(client, Genid.getCurrentSeconds());
            }
            else
                fileGates.put(endpoint, new HashMap<TCPClient, Long>() {{
                    put(client, Genid.getCurrentSeconds());
                }});
        }
    }

    TCPClient fecthFileGateClient(String endpoint) {
        synchronized (interLocker) {
            if (fileGates.containsKey(endpoint)) {
                if(fileGates.get(endpoint) != null)
                    for (TCPClient client : fileGates.get(endpoint).keySet())
                        return client;
            }
        }
        return null;
    }

    private void checkRoutineInit() {
        if (initCheckThread.get() || !running.get())
            return;

        checkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running.get()) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        synchronized (interLocker) {
                            status = ClientStatus.Closed;
                        }
                        return;
                    }

                    synchronized (interLocker) {
                        if (status != ClientStatus.Closed && !connectionIsAlive()) {
                            closedCase = CloseType.Timeout;
                            close();
                        }
                    }
                }
            }
        });
        checkThread.setName("RTM.ThreadCheck");
        checkThread.setDaemon(true);
        checkThread.start();

        initCheckThread.set(true);
        running.set(true);
    }

    boolean isAirplaneModeOn() {
        return android.provider.Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON,0) != 0;
    }

    boolean isNetWorkConnected() {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeInfo = cm.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.isAvailable() && activeInfo.isConnected())
                isConnected = true;
        }
        return isConnected;
    }

    //-------------[ Auth(Login) utilies functions ]--------------------------//
    private void ConfigRtmGateClient(final TCPClient client) {
        client.setQuestTimeout(rtmConfig.globalQuestTimeoutSeconds);

        if (encrptyData != null && curve!=null && !curve.equals(""))
            client.enableEncryptorByDerData(curve, encrptyData);


        client.setQuestProcessor(processor, "com.rtmsdk.RTMCore$RTMQuestProcessor");

        client.setWillCloseCallback(new ConnectionWillCloseCallback() {
            @Override
            public void connectionWillClose(InetSocketAddress peerAddress, int _connectionId,boolean causedByError) {
                if (connectionId.get() != 0 && connectionId.get() == _connectionId && closedCase != CloseType.ByUser && getClientStatus() != ClientStatus.Connecting) {
//                if (connectionId.get() != 0 && connectionId.get() == _connectionId && closedCase != CloseType.ByUser) {
                    close();
                    processor.rtmConnectClose();
                    if (!autoConnect) {
                        return;
                    }
                    else
                    {
                        if (closedCase == CloseType.ByServer || isRelogin.get()) {
                            return;
                        }

                        if (isAirplaneModeOn()) {
                            return;
                        }

                        if(getClientStatus() == ClientStatus.Closed){
                            try {
                                Thread.sleep(2* 1000);//处理一些特殊情况
                                if (noNetWorkNotify.get())
                                    return;
                                if (isRelogin.get() || getClientStatus() == ClientStatus.Connected)
                                    return;
                                isRelogin.set(true);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        reloginEvent(1);
                                    }
                                }).start();
                            }
                            catch (Exception e){
                                errorRecorder.recordError(" relogin error " + e.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }


    //-------------[ Auth(Login) processing functions ]--------------------------//
    private void AsyncFetchRtmGateEndpoint(String addressType, FunctionalAnswerCallback callback, int timeout) {
        Quest quest = new Quest("which");
        quest.param("what", "rtmGated");
        quest.param("addrType", addressType);
        quest.param("proto", "tcp");

        dispatch.sendQuest(quest, callback, timeout);
    }

    private RTMAnswer auth(String token, Map<String, String> attr) {
        return auth(token, attr, false);
    }

    private RTMAnswer auth(String token, Map<String, String> attr, boolean retry) {
        Quest qt = new Quest("auth");
        qt.param("pid", pid);
        qt.param("uid", uid);
        qt.param("token", token);
        qt.param("lang", lang);
        qt.param("version", "Android-" + rtmConfig.SDKVersion);

        if (attr != null)
            qt.param("attrs", attr);
        try {
            Answer answer = rtmGate.sendQuest(qt,rtmConfig.globalQuestTimeoutSeconds);

            if (answer.getErrorCode() != ErrorCode.FPNN_EC_OK.value()) {
                closeStatus();
                return genRTMAnswer(answer,"when send auth");
            }
            else if (!rtmUtils.wantBoolean(answer,"ok")) {
                if (retry) {
                    closeStatus();
                    return genRTMAnswer(RTMErrorCode.RTM_EC_INVALID_AUTH_TOEKN.value(),"retry auth failed");
                }
                String endpoint = answer.getString("gate");
                if (endpoint.equals("")) {
                    closeStatus();
                    return genRTMAnswer(RTMErrorCode.RTM_EC_INVALID_AUTH_TOEKN.value(),"auth failed token maybe expired");
                } else {
                    rtmGate = TCPClient.create(endpoint);
                    rtmGate.setErrorRecorder(errorRecorder);
                    return auth(token, attr, true);
                }
            }
            synchronized (interLocker) {
                status = ClientStatus.Connected;
            }
            processor.setLastPingTime(Genid.getCurrentSeconds());
            checkRoutineInit();
            connectionId.set(rtmGate.getConnectionId());
            return genRTMAnswer(answer);
        }
        catch (Exception  ex){
            closeStatus();
            return genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(),ex.getMessage());
        }
    }

    private void auth(UserInterface.IRTMEmptyCallback callback, String token, Map<String, String> attr) {
        auth(callback, token, attr, false);
    }

    private void auth(final IRTMEmptyCallback callback, final String token, final Map<String, String> attr, final boolean retry) {
        Quest qt = new Quest("auth");
        qt.param("pid", pid);
        qt.param("uid", uid);
        qt.param("token", token);
        qt.param("lang", lang);
        qt.param("version", "Android-" + rtmConfig.SDKVersion);
        if (attr != null)
            qt.param("attrs", attr);

        rtmGate.sendQuest(qt, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                try {
                    if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        closeStatus();
                        callback.onResult(genRTMAnswer(answer, errorCode));
                    } else if (!rtmUtils.wantBoolean(answer,"ok")) {
                        if (retry) {
                            closeStatus();
                            callback.onResult(genRTMAnswer(RTMErrorCode.RTM_EC_INVALID_AUTH_TOEKN.value(), "retry failed auth failed token maybe expired"));
                        } else {
                            String endpoint = answer.getString("gate");
                            if (endpoint.equals("")) {
                                closeStatus();
                                callback.onResult(genRTMAnswer(RTMErrorCode.RTM_EC_INVALID_AUTH_TOEKN.value(), "auth failed token maybe expired"));
                            } else {
                                rtmGate = TCPClient.create(endpoint);
                                rtmGate.setErrorRecorder(errorRecorder);
                                auth(callback, token, attr, true);
                            }
                        }
                    } else {
                        synchronized (interLocker) {
                            status = ClientStatus.Connected;
                        }
                        processor.setLastPingTime(Genid.getCurrentSeconds());
                        checkRoutineInit();
                        connectionId.set(rtmGate.getConnectionId());
                        callback.onResult(genRTMAnswer(errorCode));
                    }
                }
                catch (Exception e){
                    callback.onResult(genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(),e.getMessage()));
                }
            }
        }, 0);
    }

    void login(final IRTMEmptyCallback callback, final String token, final String lang, final String addressType, final Map<String, String> attr) {
        if (token ==null){
            callback.onResult(genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value()," token  is null"));
            return;
        }

        try {
            synchronized (interLocker) {
                if (status == ClientStatus.Connected || status == ClientStatus.Connecting) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(genRTMAnswer(RTMErrorCode.RTM_EC_OK.value()));
                        }
                    }).start();
                    return;
                }
                status = ClientStatus.Connecting;
            }


            if (dispatch == null) {
                closeStatus();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(), "rtmclient not init success"));
                    }
                }).start();
                return;
            }


            this.token = token;
            if (lang == null)
                this.lang = "";
            else
                this.lang = lang;
            this.loginAttrs = attr;
            closedCase = CloseType.None;

            if (rtmGate != null) {
                rtmGate.close();
                auth(callback, token, attr);
            } else {
                AsyncFetchRtmGateEndpoint(addressType, new FunctionalAnswerCallback() {
                    @Override
                    public void onAnswer(Answer answer, int errorCode) {
                        if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                            closeStatus();
                            callback.onResult(genRTMAnswer(errorCode));
                        }
                        else {
                            String endpoint = answer.getString("endpoint");
                            if (endpoint.equals("")) {
                                closeStatus();
                                callback.onResult(genRTMAnswer(errorCode));
                            } else {
                                dispatch.close();
                                rtmGate = TCPClient.create(endpoint);
                                rtmGate.setErrorRecorder(errorRecorder);
                                ConfigRtmGateClient(rtmGate);
                                auth(callback, token, attr);
                            }
                        }
                    }
                }, rtmConfig.globalQuestTimeoutSeconds);
            }
        }
        catch (final Exception ex){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(), "rtm login error " + ex.getMessage()));
                }
            }).start();        }
    }

    private  void closeStatus()
    {
        synchronized (interLocker) {
            status = ClientStatus.Closed;
        }
    }

    RTMAnswer login(String token, String lang, Map<String, String> attr, String addressType) {
        if (token == null)
            return genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(), " token  is null");

        try {
            if (lang == null)
                this.lang = "";
            else
                this.lang = lang;
            this.token =  token;
            this.loginAttrs = attr;
            closedCase = CloseType.None;

            synchronized (interLocker) {
                if (status == ClientStatus.Connected || status == ClientStatus.Connecting)
                    return genRTMAnswer(ErrorCode.FPNN_EC_OK.value());

                status = ClientStatus.Connecting;
            }

            if (rtmGate != null){
                rtmGate.close();
                return auth(token, attr);
            }
            if (dispatch == null){
                closeStatus();
                return  genRTMAnswer(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(),"rtmclient not init success");
            }
            Quest quest = new Quest("which");
            quest.param("what", "rtmGated");
            quest.param("addrType", addressType);
            quest.param("proto", "tcp");
            Answer answer = dispatch.sendQuest(quest,rtmConfig.globalQuestTimeoutSeconds);
            if (answer.getErrorCode() != ErrorCode.FPNN_EC_OK.value()) {
                closeStatus();
                return genRTMAnswer(answer,"when get rtmgate send which to dispatch");
            }

            String endpoint = answer.getString("endpoint");
            if (endpoint.equals("")) {
                closeStatus();
                return genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(),"rtmgate not get");
                //返回错误码
            }
            rtmGate = TCPClient.create(endpoint);
            rtmGate.setErrorRecorder(errorRecorder);
            dispatch.close();
            ConfigRtmGateClient(rtmGate);
            return auth(token, attr);
        } catch (Exception e) {
            closeStatus();
            return  genRTMAnswer(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(),e.getMessage());
        }
    }

    void close() {
        synchronized (interLocker) {
            initCheckThread.set(false);
            running.set(false);
            fileGates.clear();
            if (status == ClientStatus.Closed)
                return;
            status = ClientStatus.Closed;
        }
        if (rtmGate !=null)
            rtmGate.close();
    }
}