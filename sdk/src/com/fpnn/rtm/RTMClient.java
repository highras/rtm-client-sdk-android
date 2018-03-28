package com.fpnn.rtm;

import com.fpnn.sdk.*;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Message;
import com.fpnn.sdk.proto.Quest;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.*;

public class RTMClient {

    private static final String RTMGatedName = "rtmGated";

    public enum Status {
        Closed,
        QueryRTMGatedAddress,
        ConnectingToRTMGate,
        Authing,
        AuthFailed,
        Connected,
    }

    private class RTMQuestCachedPackage {
        Quest quest;
        AnswerCallback callback;
        long putInCacheMillis;
        int timeout;

        RTMQuestCachedPackage(Quest quest, AnswerCallback callback, int timeout) {
            this.quest = quest;
            this.callback = callback;
            this.timeout = timeout;
            this.putInCacheMillis = System.currentTimeMillis();
        }
    }

    private TCPClient dispatch;
    private TCPClient rtmGated;
    private String rtmGatedName;
    private Status status;
    private boolean autoAuth;

    //-- auth info
    private int pid;
    private long uid;
    private String token;
    private boolean recvUnreadNotify;

    //-- Callbacks & quest processor
    private RTMEventHandlerInterface processor;
    private RTMAuthCallback authCallback;
    private RTMClosedCallback closedCallback;

    //-- For TCPClient
    private int questTimeout;
    private ConnectionConnectedCallback rtmGatedConnectedCallback;
    private ConnectionWillCloseCallback rtmGatedWillCloseCallback;
    private TreeSet<RTMQuestCachedPackage> questCache;

    //-- For encryption
    private String curveName;
    private byte[] rtmPublicKey;

    //-- Last error info
    private int lastErrorCode;
    private String lastErrorMessage;


    private RTMClient() {
        dispatch = null;
        rtmGated = null;
        rtmGatedName = RTMGatedName;
        status = Status.Closed;
        autoAuth = false;

        pid = 0;
        uid = 0;
        token = null;
        recvUnreadNotify = true;

        processor = null;
        authCallback = null;
        closedCallback = null;

        rtmGatedConnectedCallback = new ConnectionConnectedCallback() {
            @Override
            public void connectResult(InetSocketAddress peerAddress, boolean connected) {
                RTMGatedConnected(connected);
            }
        };

        rtmGatedWillCloseCallback = new ConnectionWillCloseCallback() {
            @Override
            public void connectionWillClose(InetSocketAddress peerAddress, boolean causedByError) {
                RTMGatedWillClose(causedByError);
            }
        };

        questTimeout = 0;
        questCache = new TreeSet<>();

        curveName = null;
        rtmPublicKey = null;

        lastErrorCode = ErrorCode.FPNN_EC_OK.value();
        lastErrorMessage = null;
    }

    //-------------[ Work with Standard Flow ]-------------//
    public RTMClient(String dispatchEndpoint, String cluster) {
        this();
        if (cluster != null && cluster.length() > 0)
            rtmGatedName = RTMGatedName + "@" + cluster;

        dispatch = TCPClient.create(dispatchEndpoint);
    }

    public RTMClient(String dispatchHost, int dispatchPort, String cluster) {
        this();
        if (cluster != null && cluster.length() > 0)
            rtmGatedName = RTMGatedName + "@" + cluster;

        dispatch = TCPClient.create(dispatchHost, dispatchPort);
    }

    //-------------[ Direct Connect to RtmGated ]-------------//
    public RTMClient(String rtmGatedEndpoint) {
        this();
        rtmGated = TCPClient.create(rtmGatedEndpoint, false);
        rtmGated.setConnectedCallback(rtmGatedConnectedCallback);
        rtmGated.setWillCloseCallback(rtmGatedWillCloseCallback);
    }

    public RTMClient(String rtmGatedHost, int rtmGatedPort) {
        this();
        rtmGated = TCPClient.create(rtmGatedHost, rtmGatedPort, false);
        rtmGated.setConnectedCallback(rtmGatedConnectedCallback);
        rtmGated.setWillCloseCallback(rtmGatedWillCloseCallback);
    }

    //-----------------------------------------------------//
    //--                  Configure APIs                 --//
    //-----------------------------------------------------//

    public Status getStatus() {
        synchronized (this) {
            return status;
        }
    }

    public int questTimeout() {
        return questTimeout;
    }

    public void setQuestTimeout(int timeout) {
        questTimeout = timeout;
    }

    public void enableAutoAuth(int pid, long uid, String token, boolean recvUnreadNotification, RTMAuthCallback cb) {
        this.pid = pid;
        this.uid = uid;
        this.token = token;
        this.recvUnreadNotify = recvUnreadNotification;
        autoAuth = true;
        authCallback = cb;
    }

    public void enableAutoAuth(int pid, long uid, String token, boolean recvUnreadNotification) {
        enableAutoAuth(pid, uid, token, recvUnreadNotification, null);
    }

    public void disableAutoAuth() {
        token = null;
        autoAuth = false;
        authCallback = null;
    }

    public void setClosedCallback(RTMClosedCallback cb) {
        closedCallback = cb;
    }

    public void setEventHandler(RTMEventHandlerInterface handler) {
        processor = handler;

        if (dispatch == null)
            rtmGated.setQuestProcessor(new RTMQuestProcessor(processor, rtmGated), "com.fpnn.rtm.RTMQuestProcessor");
    }

    public void enableEncryptorByDerFile(String curve, String keyDerFilePath) throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyDerFilePath));
        enableEncryptorByDerData(curve, keyBytes);
    }

    public void enableEncryptorByDerData(String curve, byte[] rtmPublicKey) {
        this.curveName = curve;
        this.rtmPublicKey = rtmPublicKey;

        if (dispatch != null)
            dispatch.enableEncryptorByDerData(curve, rtmPublicKey);
        else
            rtmGated.enableEncryptorByDerData(curve, rtmPublicKey);
    }

    public static void configureForMultipleSynchronousConcurrentAPIs(int taskThreadCount) {
        ClientEngine.setMaxThreadInTaskPool(taskThreadCount);
    }

    public static void configureForMultipleSynchronousConcurrentAPIs() {
        configureForMultipleSynchronousConcurrentAPIs(32);
    }

    public static boolean isAutoCleanup() {
        return ClientEngine.isAutoStop();
    }

    public static void setAtuoCleanup(boolean auto) {
        ClientEngine.setAutoStop(auto);
    }

    public static void SDKCleanup() {
        ClientEngine.stop();
        RTMResourceCenter.close();
    }

    //-----------------------------------------------------//
    //--                  Private APIs                   --//
    //-----------------------------------------------------//
    private static class MidGenerator {

        static private long count = 0;

        static public synchronized long gen() {
            if (count == 0)
                count = System.currentTimeMillis() % 1000;

            return ++count;
        }
    }

    //-----------------------------------------------------//
    //--          RTM Connect & close (Internal)         --//
    //-----------------------------------------------------//
    private void sendQuestInCache() {

        TreeSet<RTMQuestCachedPackage> swapCache;
        synchronized (this) {
            swapCache = questCache;
            questCache = new TreeSet<>();
            status = Status.Connected;

            notifyAll();
        }

        for (RTMQuestCachedPackage questPackage : swapCache) {
            int timeout =questPackage.timeout - (int)((System.currentTimeMillis() - questPackage.putInCacheMillis) / 1000);
            if (timeout == 0)
                timeout = -1;

            rtmGated.sendQuest(questPackage.quest, questPackage.callback, timeout);
        }
    }

    //-- Only called in synchronized block.
    private void clearQuestCache(int errorCode, String info) {

        for (RTMQuestCachedPackage questPackage : questCache) {

            Answer answer = new Answer(questPackage.quest);
            answer.fillErrorInfo(errorCode, info);

            ClientEngine.getThreadPool().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            questPackage.callback.fillResult(answer, errorCode);
                        }
                    });
        }

        questCache.clear();
    }

    private void connectToRTMGated(TCPClient gateClient) {

        synchronized (this) {
            status = Status.ConnectingToRTMGate;
        }

        if (gateClient != null) {
            rtmGated = gateClient;
            rtmGated.setQuestProcessor(new RTMQuestProcessor(processor, rtmGated), "com.fpnn.rtm.RTMQuestProcessor");

            if (curveName != null)
                rtmGated.enableEncryptorByDerData(curveName, rtmPublicKey);

            rtmGated.setConnectedCallback(rtmGatedConnectedCallback);
            rtmGated.setWillCloseCallback(rtmGatedWillCloseCallback);
        }

        try {
            if (!rtmGated.connect(false)) {
                String errorInfo = "Connect to RTM gated failed.";
                ErrorRecorder.record(errorInfo);
                RTMConnectFailedFinally(Status.Closed, ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value(), errorInfo, true);
            }
        } catch (InterruptedException e) {
            String errorInfo = "Connect to RTM gated interrupted.";
            ErrorRecorder.record(errorInfo, e);
            RTMConnectFailedFinally(Status.Closed, ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(), errorInfo, true);
        }
    }

    private void RTMGatedConnected(boolean connected) {

        if (!connected) {
            String errorInfo = "Connect RTM gated failed.";
            ErrorRecorder.record(errorInfo);
            RTMConnectFailedFinally(Status.Closed, ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value(), errorInfo, true);
            return;
        }

        synchronized (this) {
            status = Status.Authing;
        }

        Quest quest = new Quest("auth");
        quest.param("pid", pid);
        quest.param("uid", uid);
        quest.param("token", token);
        quest.param("unread", recvUnreadNotify);

        rtmGated.sendQuest(quest, new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                boolean success = (boolean) answer.get("ok", false);

                if (success) {
                    if (authCallback != null)
                        authCallback.authResult(true);

                    sendQuestInCache();
                }
                else {
                    RTMConnectFailedFinally(Status.AuthFailed, ErrorCode.FPNN_EC_OK.value(), "Auth failed.", false);

                    if (authCallback != null)
                        authCallback.authResult(false);
                }
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String errorInfo = "Auth exception.";
                ErrorRecorder.record(errorInfo);
                RTMConnectFailedFinally(Status.Closed, errorCode, errorInfo, false);

                if (authCallback != null) {
                    String exInfo = null;
                    if (answer != null)
                        exInfo = (String) answer.get("ex");

                    authCallback.onException(errorCode, exInfo);
                }
            }
        }, questTimeout);
    }

    private void RTMGatedWillClose(boolean causedByError) {

        synchronized (this) {
            while (status == Status.QueryRTMGatedAddress
                    || status == Status.ConnectingToRTMGate
                    || status == Status.Authing) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    ErrorRecorder.record("InterruptedException occurred when waiting RTM client connect & auth finish.", e);
                }
            }

            if (status == Status.Connected) {
                this.status = Status.Closed;
                clearQuestCache(ErrorCode.FPNN_EC_CORE_CONNECTION_CLOSED.value(), "Connection closed.");

                notifyAll();
            }
        }

        if (closedCallback != null) {
            closedCallback.RTMClosed(causedByError);
        }
    }

    private void RTMConnectFailedFinally(Status status, int errorCode, String message, boolean launchAuthCallback) {

        synchronized (this) {
            lastErrorCode = errorCode;
            lastErrorMessage = message;

            this.status = status;
            clearQuestCache(errorCode, message);

            if (launchAuthCallback && authCallback != null) {
                ClientEngine.getThreadPool().execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                authCallback.onException(errorCode, message);
                            }
                        });
            }

            notifyAll();
        }
    }

    private void fetchRTMGatedAddress() {

        Quest quest = new Quest("which");
        quest.param("what", rtmGatedName);

        dispatch.sendQuest(quest, new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {

                String endpoint = null;
                try {
                    endpoint = (String)answer.want("endpoint");
                    TCPClient client = TCPClient.create(endpoint, false);
                    connectToRTMGated(client);
                } catch (Exception e) {
                    String errorInfo = "Exception when fetched RTM gated address. Endpoint is " + endpoint;
                    ErrorRecorder.record(errorInfo, e);
                    RTMConnectFailedFinally(Status.Closed, ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(), errorInfo, true);
                }
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");

                String errorInfo = "Fetch RTM gated address failed. Error code: " + errorCode + ", info: " + info;
                ErrorRecorder.record(errorInfo);
                RTMConnectFailedFinally(Status.Closed, errorCode, errorInfo, true);
            }
        }, questTimeout);
    }

    private boolean realConnect(int pid, long uid, String token, boolean recvUnreadNotification, RTMAuthCallback cb) {
        synchronized (this) {
            if (status == Status.AuthFailed && token == null)
                return false;

            if (status != Status.Closed && status != Status.AuthFailed) {
                return false;
            }

            if (dispatch != null)
                status = Status.QueryRTMGatedAddress;
            else
                status = Status.ConnectingToRTMGate;
        }

        this.pid = pid;
        this.uid = uid;
        this.token = token;
        this.authCallback = cb;
        this.recvUnreadNotify = recvUnreadNotification;

        if (dispatch != null)
            fetchRTMGatedAddress();
        else {
            connectToRTMGated(null);
        }
        return true;
    }

    //-----------------------------------------------------//
    //--          RTM Connect & close (Public)           --//
    //-----------------------------------------------------//

    public void connect(int pid, long uid, String token, boolean recvUnreadNotification, RTMAuthCallback cb) {

        int code = ErrorCode.FPNN_EC_OK.value();
        String errorInfo = null;

        if (token != null) {
            if (!realConnect(pid, uid, token, recvUnreadNotification, cb)) {
                code = RTMErrorCode.RTM_EC_DUPLCATED_AUTH.value();
                errorInfo = "Client has been connecting.";
            }
        }
        else {
            code = RTMErrorCode.RTM_EC_EMPTY_PARAMETER.value();
            errorInfo = "Invalid token.";
        }

        if (code != ErrorCode.FPNN_EC_OK.value()) {

            final int errorCode = code;
            final String errorMessage = errorInfo;

            ClientEngine.getThreadPool().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            cb.onException(errorCode, errorMessage);
                        }
                    });
        }
    }

    public boolean connect(int pid, long uid, String token, boolean recvUnreadNotification) throws InterruptedException {
        realConnect(pid, uid, token, recvUnreadNotification, null);

        synchronized (this) {
            while (true) {
                if (status == Status.AuthFailed || status == Status.Closed)
                    return false;
                if (status == Status.Connected)
                    return true;

                wait();
            }
        }
    }

    public void colse() throws InterruptedException {
        bye();
    }

    //-----------------------------------------------------//
    //--                RTM Message APIs                 --//
    //-----------------------------------------------------//

    protected void sendQuest(Quest quest, AnswerCallback callback, int timeoutInSeconds) {

        boolean needReConnect = false;
        boolean cannotSend = false;

        synchronized (this) {
            if (status == Status.Closed || status == Status.AuthFailed) {
                if (autoAuth)
                    needReConnect = true;
                else
                    cannotSend = true;
            }
            else if (status == Status.Connected) {
                rtmGated.sendQuest(quest, callback, timeoutInSeconds);
                return;
            }
            else {
                RTMQuestCachedPackage pkg = new RTMQuestCachedPackage(quest, callback, timeoutInSeconds);
                questCache.add(pkg);
                return;
            }
        }

        if (needReConnect) {

            realConnect(pid, uid, token, recvUnreadNotify, authCallback);

            synchronized (this) {
                if (status == Status.AuthFailed || status == Status.Closed)
                    cannotSend = true;
                else if (status == Status.Connected) {
                    rtmGated.sendQuest(quest, callback, timeoutInSeconds);
                    return;
                }
                else {
                    RTMQuestCachedPackage pkg = new RTMQuestCachedPackage(quest, callback, timeoutInSeconds);
                    questCache.add(pkg);
                    return;
                }
            }
        }

        if (cannotSend) {
            final int errorCode = ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value();
            Answer answer = new Answer(quest);
            answer.fillErrorInfo(errorCode, "No available connection.");

            ClientEngine.getThreadPool().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            callback.fillResult(answer, errorCode);
                        }
                    });
        }
    }

    protected Answer sendQuest(Quest quest, int timeoutInSeconds) throws InterruptedException {
        SyncAnswerCallback callback = new SyncAnswerCallback();
        sendQuest(quest, callback, timeoutInSeconds);
        return callback.getAnswer();
    }

    //-----------------------------------------------------//
    //--                  RTM Gate APIs                  --//
    //-----------------------------------------------------//

    public interface DoneCallback {
        void done();
        void onException(int errorCode, String message);
    }

    private class FPNNDoneCallbackWrapper extends AnswerCallback {

        DoneCallback callback;

        FPNNDoneCallbackWrapper(DoneCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onAnswer(Answer answer) {
            if (callback != null)
                callback.done();
        }

        @Override
        public void onException(Answer answer, int errorCode) {
            if (callback != null) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");

                callback.onException(errorCode, info);
            }
        }
    }

    //=============================[ Special APIs ]==============================//
    //-----------------[ bye ]-----------------//

    public void bye(DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("bye");

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        synchronized (this) {
            if (status == Status.AuthFailed || status == Status.Closed) {
                ClientEngine.getThreadPool().execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                callback.done();
                            }
                        });
            }

            if (status == Status.Connected) {
                rtmGated.sendQuest(quest, internalCallback, timeoutInseconds);
                return;
            }

            RTMQuestCachedPackage cachePackage = new RTMQuestCachedPackage(quest, internalCallback, timeoutInseconds);
            questCache.add(cachePackage);
        }
    }

    public void bye(DoneCallback callback) {
        bye(callback, questTimeout);
    }

    public void bye() throws InterruptedException {

        Quest quest = new Quest("bye");
        TCPClient rtmGatedClient;

        synchronized (this) {
            while (true) {
                if (status == Status.AuthFailed || status == Status.Closed)
                    return;
                if (status == Status.Connected)
                    break;

                wait();
            }

            rtmGatedClient = rtmGated;
        }

        rtmGatedClient.sendQuest(quest);
    }

    //=============================[ Standard APIs ]==============================//
    //-----------------[ sendmsg ]-----------------//
    public void sendMessage(long uid, byte mType, String message, String attrs, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("sendmsg");
        quest.param("to", uid);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void sendMessage(long uid, byte mType, String message, String attrs, DoneCallback callback) {
        sendMessage(uid, mType, message, attrs, callback, questTimeout);
    }

    public void sendMessage(long uid, byte mType, String message, String attrs, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("sendmsg");
        quest.param("to", uid);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void sendMessage(long uid, byte mType, String message, String attrs) throws RTMException, InterruptedException {
        sendMessage(uid, mType, message, attrs, questTimeout);
    }

    //-----------------[sendmsgs]-----------------//

    public void sendMessages(Set<Long> uids, byte mType, String message, String attrs, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("sendmsgs");
        quest.param("tos", uids);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void sendMessages(Set<Long> uids, byte mType, String message, String attrs, DoneCallback callback) {
        sendMessages(uids, mType, message, attrs, callback, questTimeout);
    }

    public void sendMessages(Set<Long> uids, byte mType, String message, String attrs, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("sendmsgs");
        quest.param("tos", uids);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void sendMessages(Set<Long> uids, byte mType, String message, String attrs) throws RTMException, InterruptedException {
        sendMessages(uids, mType, message, attrs, questTimeout);
    }

    //-----------------[sendgroupmsg]-----------------//

    public void sendGroupMessage(long groupId, byte mType, String message, String attrs, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("sendgroupmsg");
        quest.param("gid", groupId);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void sendGroupMessage(long groupId, byte mType, String message, String attrs, DoneCallback callback) {
        sendGroupMessage(groupId, mType, message, attrs, callback, questTimeout);
    }

    public void sendGroupMessage(long groupId, byte mType, String message, String attrs, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("sendgroupmsg");
        quest.param("gid", groupId);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void sendGroupMessage(long groupId, byte mType, String message, String attrs) throws RTMException, InterruptedException {
        sendGroupMessage(groupId, mType, message, attrs, questTimeout);
    }

    //-----------------[sendroommsg]-----------------//

    public void sendRoomMessage(long roomId, byte mType, String message, String attrs, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("sendroommsg");
        quest.param("rid", roomId);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void sendRoomMessage(long roomId, byte mType, String message, String attrs, DoneCallback callback) {
        sendRoomMessage(roomId, mType, message, attrs, callback, questTimeout);
    }

    public void sendRoomMessage(long roomId, byte mType, String message, String attrs, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("sendroommsg");
        quest.param("rid", roomId);
        quest.param("mid", MidGenerator.gen());
        quest.param("mtype", mType);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void sendRoomMessage(long roomId, byte mType, String message, String attrs) throws RTMException, InterruptedException {
        sendRoomMessage(roomId, mType, message, attrs, questTimeout);
    }

    //-----------------[addvariables]-----------------//

    public void addVariables(Map<String, String> var, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("addvariables");
        quest.param("var", var);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void addVariables(Map<String, String> var, DoneCallback callback) {
        addVariables(var, callback, questTimeout);
    }

    public void addVariables(Map<String, String> var, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("addvariables");
        quest.param("var", var);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void addVariables(Map<String, String> var) throws RTMException, InterruptedException {
        addVariables(var, questTimeout);
    }

    //-----------------[adddebuglog]-----------------//

    public void addDebugLog(String message, String attrs, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void addDebugLog(String message, String attrs, DoneCallback callback) {
        addDebugLog(message, attrs, callback, questTimeout);
    }

    public void addDebugLog(String message, String attrs, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void addDebugLog(String message, String attrs) throws RTMException, InterruptedException {
        addDebugLog(message, attrs, questTimeout);
    }

    //-----------------[setpushname]-----------------//

    public void setPushName(String pushName, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("setpushname");
        quest.param("pushname", pushName);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void setPushName(String pushName, DoneCallback callback) {
        setPushName(pushName, callback, questTimeout);
    }

    public void setPushName(String pushName, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("setpushname");
        quest.param("pushname", pushName);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void setPushName(String pushName) throws RTMException, InterruptedException {
        setPushName(pushName, questTimeout);
    }

    //-----------------[getpushname]-----------------//

    public interface GetPushNameCallback {
        void done(String pushName);
        void onException(int errorCode, String message);
    }

    public void getPushName(GetPushNameCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getpushname");

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((String) answer.get("getpushname"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void getPushName(GetPushNameCallback callback) {
        getPushName(callback, questTimeout);
    }

    public String getPushName(int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getpushname");

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }

        return (String) answer.get("pushname");
    }

    public String getPushName() throws RTMException, InterruptedException {
        return getPushName(questTimeout);
    }

    //-----------------[setgeo]-----------------//

    public void setGeo(double latitude, double longitude, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("setgeo");
        quest.param("lat", latitude);
        quest.param("lng", longitude);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void setGeo(double latitude, double longitude, DoneCallback callback) {
        setGeo(latitude, longitude, callback, questTimeout);
    }

    public void setGeo(double latitude, double longitude, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("setgeo");
        quest.param("lat", latitude);
        quest.param("lng", longitude);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }
    }

    public void setGeo(double latitude, double longitude) throws RTMException, InterruptedException {
        setGeo(latitude, longitude, questTimeout);
    }

    //-----------------[getgeo]-----------------//

    public class GeoInfo {
        public long uid;
        double latitude;
        double longitude;
    }
    public interface GetGeoCallback {
        void done(double latitude, double longitude);
        void onException(int errorCode, String message);
    }

    public void getGeo(GetGeoCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getgeo");

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((double) answer.get("lat"), (double) answer.get("lng"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void getGeo(GetGeoCallback callback) {
        getGeo(callback, questTimeout);
    }

    public GeoInfo getGeo(int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getgeo");

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }

        GeoInfo gi = new GeoInfo();
        gi.uid = uid;
        gi.latitude = (double) answer.get("lat");
        gi.longitude = (double) answer.get("lng");
        return gi;
    }

    public GeoInfo getGeo() throws RTMException, InterruptedException {
        return getGeo(questTimeout);
    }

    //-----------------[getgeos]-----------------//

    //-- TODO: Wait the Interface confirmed.

    //-----------------[ addfriends ]-----------------//
    public void addFriends(Set<Long> friends, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("addfriends");
        quest.param("friends", friends);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void addFriends(Set<Long> friends, DoneCallback callback) {
        addFriends(friends, callback, questTimeout);
    }

    public void addFriends(Set<Long> friends, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("addfriends");
        quest.param("friends", friends);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void addFriends(Set<Long> friends) throws RTMException, InterruptedException {
        addFriends(friends, questTimeout);
    }

    //-----------------[delfriends]-----------------//

    public void deleteFriends(Set<Long> friends, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("delfriends");
        quest.param("friends", friends);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void deleteFriends(Set<Long> friends, DoneCallback callback) {
        deleteFriends(friends, callback, questTimeout);
    }

    public void deleteFriends(Set<Long> friends, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("delfriends");
        quest.param("friends", friends);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void deleteFriends(Set<Long> friends) throws RTMException, InterruptedException {
        deleteFriends(friends, questTimeout);
    }

    //-----------------[getfriends]-----------------//

    public interface GetFriendsCallback {
        void done(Set<Long> uids);
        void onException(int errorCode, String message);
    }

    @SuppressWarnings("unchecked")
    public void getFriends(GetFriendsCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getfriends");

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((Set<Long>) answer.get("uids"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void getFriends(GetFriendsCallback callback) {
        getFriends(callback, questTimeout);
    }

    @SuppressWarnings("unchecked")
    public Set<Long> getFriends(int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getfriends");

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        return (Set<Long>) answer.get("uids");
    }

    public Set<Long> getFriends() throws RTMException, InterruptedException {
        return getFriends(questTimeout);
    }

    //-----------------[addgroupmembers]-----------------//

    public void addGroupMembers(long groupId, Set<Long> uids, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("addgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void addGroupMembers(long groupId, Set<Long> uids, DoneCallback callback) {
        addGroupMembers(groupId, uids, callback, questTimeout);
    }

    public void addGroupMembers(long groupId, Set<Long> uids, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("addgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void addGroupMembers(long groupId, Set<Long> uids) throws RTMException, InterruptedException {
        addGroupMembers(groupId, uids, questTimeout);
    }

    //-----------------[delgroupmembers]-----------------//

    public void deleteGroupMembers(long groupId, Set<Long> uids, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("delgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void deleteGroupMembers(long groupId, Set<Long> uids, DoneCallback callback) {
        deleteGroupMembers(groupId, uids, callback, questTimeout);
    }

    public void deleteGroupMembers(long groupId, Set<Long> uids, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("delgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void deleteGroupMembers(long groupId, Set<Long> uids) throws RTMException, InterruptedException {
        deleteGroupMembers(groupId, uids, questTimeout);
    }

    //-----------------[getgroupmembers]-----------------//

    public interface GetGroupMembersCallback {
        void done(Set<Long> uids);
        void onException(int errorCode, String message);
    }
    @SuppressWarnings("unchecked")
    public void getGroupMembers(long groupId, GetGroupMembersCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getgroupmembers");
        quest.param("gid", groupId);

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((Set<Long>) answer.get("uids"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void getGroupMembers(long groupId, GetGroupMembersCallback callback) {
        getGroupMembers(groupId, callback, questTimeout);
    }

    @SuppressWarnings("unchecked")
    public Set<Long> getGroupMembers(long groupId, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getgroupmembers");
        quest.param("gid", groupId);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        return (Set<Long>) answer.get("uids");
    }

    public Set<Long> getGroupMembers(long groupId) throws RTMException, InterruptedException {
        return getGroupMembers(groupId, questTimeout);
    }

    //-----------------[getusergroups]-----------------//

    public interface GetUserGroupsCallback {
        void done(Set<Long> groupIds);
        void onException(int errorCode, String message);
    }
    @SuppressWarnings("unchecked")
    public void getUserGroups(GetGroupMembersCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getusergroups");

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((Set<Long>) answer.get("gids"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void getUserGroups(GetGroupMembersCallback callback) {
        getUserGroups(callback, questTimeout);
    }

    @SuppressWarnings("unchecked")
    public Set<Long> getUserGroups(int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getusergroups");

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        return (Set<Long>) answer.get("gids");
    }

    public Set<Long> getUserGroups() throws RTMException, InterruptedException {
        return getUserGroups(questTimeout);
    }

    //-----------------[enterroom]-----------------//

    public void enterRoom(long roomId, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void enterRoom(long roomId, DoneCallback callback) {
        enterRoom(roomId, callback, questTimeout);
    }

    public void enterRoom(long roomId, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void enterRoom(long roomId) throws RTMException, InterruptedException {
        enterRoom(roomId, questTimeout);
    }

    //-----------------[leaveroom]-----------------//

    public void leaveRoom(long roomId, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void leaveRoom(long roomId, DoneCallback callback) {
        leaveRoom(roomId, callback, questTimeout);
    }

    public void leaveRoom(long roomId, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void leaveRoom(long roomId) throws RTMException, InterruptedException {
        leaveRoom(roomId, questTimeout);
    }

    //-----------------[getrooms]-----------------//

    public interface GetUserRoomsCallback {
        void done(Set<Long> roomIds);
        void onException(int errorCode, String message);
    }
    @SuppressWarnings("unchecked")
    public void getUserRooms(GetUserRoomsCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getuserrooms");

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((Set<Long>) answer.get("rooms"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void getUserRooms(GetUserRoomsCallback callback) {
        getUserRooms(callback, questTimeout);
    }

    @SuppressWarnings("unchecked")
    public Set<Long> getUserRooms(int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getuserrooms");

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        return (Set<Long>) answer.get("rooms");
    }

    public Set<Long> getUserRooms() throws RTMException, InterruptedException {
        return getUserRooms(questTimeout);
    }

    //-----------------[getonlineusers]-----------------//

    public interface GetOnlineUsersCallback {
        void done(Set<Long> uids);
        void onException(int errorCode, String message);
    }
    @SuppressWarnings("unchecked")
    public void getOnlineUsers(Set<Long> uids, GetOnlineUsersCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getonlineusers");
        quest.param("uids", uids);

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((Set<Long>) answer.get("uids"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void getOnlineUsers(Set<Long> uids, GetOnlineUsersCallback callback) {
        getOnlineUsers(uids, callback, questTimeout);
    }

    @SuppressWarnings("unchecked")
    public Set<Long> getOnlineUsers(Set<Long> uids, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getonlineusers");
        quest.param("uids", uids);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        return (Set<Long>) answer.get("uids");
    }

    public Set<Long> getOnlineUsers(Set<Long> uids) throws RTMException, InterruptedException {
        return getOnlineUsers(uids, questTimeout);
    }

    //-----------------[getgroupmsg]-----------------//

    public class HistoryMessasge {
        public long id;
        public long fromUid;
        public byte mType;
        public byte fType;
        public long mid;
        public String message;
        public String attrs;
        public int mTime;
    }

    public class GetHistoryMessageResult {
        public int num;
        public long maxid;
        public List<HistoryMessasge> messages;
    }

    public interface GetHistoryMessageCallback {
        void done(int num, long maxid, List<HistoryMessasge> messages);
        void onException(int errorCode, String message);
    }

    @SuppressWarnings("unchecked")
    private List<HistoryMessasge> buildHistoryMessageList(Answer answer) {
        List<HistoryMessasge> messages = new LinkedList<>();

        List rawList = (List)answer.get("msgs");
        if (rawList == null)
            return messages;

        for( int i = 0 ; i < rawList.size() ; i++) {
            List messageUnit = (List)rawList.get(i);

            HistoryMessasge hm = new HistoryMessasge();
            hm.id = (long) messageUnit.get(0);
            hm.fromUid = (long) messageUnit.get(1);
            hm.mType = (byte) messageUnit.get(2);
            hm.fType = (byte) messageUnit.get(3);

            hm.mid = (long) messageUnit.get(4);
            hm.message = (String) messageUnit.get(5);
            hm.attrs = (String) messageUnit.get(6);
            hm.mTime = (int) messageUnit.get(7);

            messages.add(hm);
        }

        return messages;
    }

    public void GetGroupMessage(long groupId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetHistoryMessageCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getgroupmsg");
        quest.param("gid", groupId);
        quest.param("num", num);
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                int num = (int) answer.get("num", 0);
                int maxid = (int) answer.get("maxid", 0);
                List<HistoryMessasge> messages = buildHistoryMessageList(answer);
                callback.done(num, maxid, messages);
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void GetGroupMessage(long groupId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetHistoryMessageCallback callback) {
        GetGroupMessage(groupId, num, descOrder, page, localMid, localId, mTypes, callback, questTimeout);
    }

    public GetHistoryMessageResult GetGroupMessage(long groupId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getgroupmsg");
        quest.param("gid", groupId);
        quest.param("num", num);
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        GetHistoryMessageResult result = new GetHistoryMessageResult();
        result.num = (int) answer.get("num", 0);
        result.maxid = (int) answer.get("maxid", 0);
        result.messages = buildHistoryMessageList(answer);

        return result;
    }

    public GetHistoryMessageResult GetGroupMessage(long groupId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes) throws RTMException, InterruptedException {
        return GetGroupMessage(groupId, num, descOrder, page, localMid, localId, mTypes, questTimeout);
    }

    //-----------------[getroommsg]-----------------//

    public void GetRoomMessage(long roomId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetHistoryMessageCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getroommsg");
        quest.param("rid", roomId);
        quest.param("num", num);
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                int num = (int) answer.get("num", 0);
                int maxid = (int) answer.get("maxid", 0);
                List<HistoryMessasge> messages = buildHistoryMessageList(answer);
                callback.done(num, maxid, messages);
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void GetRoomMessage(long roomId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetHistoryMessageCallback callback) {
        GetRoomMessage(roomId, num, descOrder, page, localMid, localId, mTypes, callback, questTimeout);
    }

    public GetHistoryMessageResult GetRoomMessage(long roomId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getroommsg");
        quest.param("rid", roomId);
        quest.param("num", num);
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        GetHistoryMessageResult result = new GetHistoryMessageResult();
        result.num = (int) answer.get("num", 0);
        result.maxid = (int) answer.get("maxid", 0);
        result.messages = buildHistoryMessageList(answer);

        return result;
    }

    public GetHistoryMessageResult GetRoomMessage(long roomId, int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes) throws RTMException, InterruptedException {
        return GetRoomMessage(roomId, num, descOrder, page, localMid, localId, mTypes, questTimeout);
    }

    //-----------------[getbroadcastmsg]-----------------//

    public void GetBroadcastMessage(int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetHistoryMessageCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getbroadcastmsg");
        quest.param("num", num);
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                int num = (int) answer.get("num", 0);
                int maxid = (int) answer.get("maxid", 0);
                List<HistoryMessasge> messages = buildHistoryMessageList(answer);
                callback.done(num, maxid, messages);
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void GetBroadcastMessage(int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetHistoryMessageCallback callback) {
        GetBroadcastMessage(num, descOrder, page, localMid, localId, mTypes, callback, questTimeout);
    }

    public GetHistoryMessageResult GetBroadcastMessage(int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getbroadcastmsg");
        quest.param("num", num);
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        GetHistoryMessageResult result = new GetHistoryMessageResult();
        result.num = (int) answer.get("num", 0);
        result.maxid = (int) answer.get("maxid", 0);
        result.messages = buildHistoryMessageList(answer);

        return result;
    }

    public GetHistoryMessageResult GetBroadcastMessage(int num, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes) throws RTMException, InterruptedException {
        return GetBroadcastMessage(num, descOrder, page, localMid, localId, mTypes, questTimeout);
    }

    //-----------------[getp2pmsg]-----------------//

    public enum MessageDirection {

        SentAndReceived ((byte)0),
        Sent ((byte)1),
        Received ((byte)2);

        private byte value;

        MessageDirection (byte value) { this.value = value; }

        public byte value() { return value; }
    }

    public class P2PHistoryMessasge {
        public long id;
        public long peerUid;
        public MessageDirection direction;
        public byte mType;
        public byte fType;
        public long mid;
        public String message;
        public String attrs;
        public int mTime;
    }

    public class GetP2PHistoryMessageResult {
        public int num;
        public long maxid;
        public List<P2PHistoryMessasge> messages;
    }

    public interface GetP2PHistoryMessageCallback {
        void done(int num, long maxid, List<P2PHistoryMessasge> messages);
        void onException(int errorCode, String message);
    }

    @SuppressWarnings("unchecked")
    private List<P2PHistoryMessasge> buildP2PHistoryMessageList(Answer answer) {
        List<P2PHistoryMessasge> messages = new LinkedList<>();

        List rawList = (List)answer.get("msgs");
        if (rawList == null)
            return messages;

        for( int i = 0 ; i < rawList.size() ; i++) {
            List messageUnit = (List)rawList.get(i);

            P2PHistoryMessasge hm = new P2PHistoryMessasge();
            hm.id = (long) messageUnit.get(0);
            hm.peerUid = (long) messageUnit.get(1);

            byte direction = (byte) messageUnit.get(2);

            if (direction == MessageDirection.Sent.value())
                hm.direction = MessageDirection.Sent;
            else if (direction == MessageDirection.Received.value())
                hm.direction = MessageDirection.Received;
            else
                hm.direction = MessageDirection.SentAndReceived;

            hm.mType = (byte) messageUnit.get(3);
            hm.fType = (byte) messageUnit.get(4);

            hm.mid = (long) messageUnit.get(5);
            hm.message = (String) messageUnit.get(6);
            hm.attrs = (String) messageUnit.get(7);
            hm.mTime = (int) messageUnit.get(8);

            messages.add(hm);
        }

        return messages;
    }

    public void GetP2PMessage(long peerUid, int num, MessageDirection direction, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetP2PHistoryMessageCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("getp2pmsg");
        quest.param("fromuid", peerUid);
        quest.param("num", num);
        quest.param("direction", direction.value());
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                int num = (int) answer.get("num", 0);
                int maxid = (int) answer.get("maxid", 0);
                List<P2PHistoryMessasge> messages = buildP2PHistoryMessageList(answer);
                callback.done(num, maxid, messages);
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void GetP2PMessage(long peerUid, int num, MessageDirection direction, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, GetP2PHistoryMessageCallback callback) {
        GetP2PMessage(peerUid, num, direction, descOrder, page, localMid, localId, mTypes, callback, questTimeout);
    }

    public GetP2PHistoryMessageResult GetP2PMessage(long peerUid, int num, MessageDirection direction, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("getp2pmsg");
        quest.param("fromuid", peerUid);
        quest.param("num", num);
        quest.param("direction", direction.value());
        quest.param("desc", descOrder);
        quest.param("page", page);
        quest.param("localmid", localMid);
        quest.param("localid", localId);
        if (mTypes != null)
            quest.param("mtypes", mTypes);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }

        GetP2PHistoryMessageResult result = new GetP2PHistoryMessageResult();
        result.num = (int) answer.get("num", 0);
        result.maxid = (int) answer.get("maxid", 0);
        result.messages = buildP2PHistoryMessageList(answer);

        return result;
    }

    public GetP2PHistoryMessageResult GetP2PMessage(long peerUid, int num, MessageDirection direction, boolean descOrder, int page, long localMid, long localId, Set<Byte> mTypes) throws RTMException, InterruptedException {
        return GetP2PMessage(peerUid, num, direction, descOrder, page, localMid, localId, mTypes, questTimeout);
    }

    //-----------------[filetoken]-----------------//

    //-- TODO: Maybe hidden behind send files functions.

    //-----------------[adddevice]-----------------//

    public void addDevice(String pType, String dType, String token, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("adddevice");
        quest.param("ptype", pType);
        quest.param("dtype", dType);
        quest.param("token", token);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void addDevice(String pType, String dType, String token, DoneCallback callback) {
        addDevice(pType, dType, token, callback, questTimeout);
    }

    public void addDevice(String pType, String dType, String token, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("adddevice");
        quest.param("ptype", pType);
        quest.param("dtype", dType);
        quest.param("token", token);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void addDevice(String pType, String dType, String token) throws RTMException, InterruptedException {
        addDevice(pType, dType, token, questTimeout);
    }

    //-----------------[setlang]-----------------//

    public void setLanguage(String language, DoneCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("setlang");
        quest.param("lang", language);

        AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void setLanguage(String language, DoneCallback callback) {
        setLanguage(language, callback, questTimeout);
    }

    public void setLanguage(String language, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("setlang");
        quest.param("lang", language);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String message = answer.getErrorMessage();
            throw new RTMException(errorCode, message);
        }
    }

    public void setLanguage(String language) throws RTMException, InterruptedException {
        setLanguage(language, questTimeout);
    }

    //-----------------[translate]-----------------//

    public interface TranslateCallback {
        void done(String srcMessage, String srcLanguage, String destMessage, String destLanguage);
        void onException(int errorCode, String message);
    }

    public class TranslatedResult {
        public String srcMessage;
        public String srcLanguage;
        public String destMessage;
        public String destLanguage;
    }

    public void translate(String originalMessage, String srcLanguage, String destLanguage, TranslateCallback callback, int timeoutInseconds) {

        Quest quest = new Quest("translate");
        quest.param("text", originalMessage);
        quest.param("dst", destLanguage);
        if (srcLanguage != null && srcLanguage.length() != 0)
            quest.param("src", srcLanguage);

        AnswerCallback internalCallback = new AnswerCallback() {
            @Override
            public void onAnswer(Answer answer) {
                callback.done((String) answer.get("stext"),
                        (String) answer.get("src"),
                        (String) answer.get("dtext"),
                        (String) answer.get("dst"));
            }

            @Override
            public void onException(Answer answer, int errorCode) {
                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");
                callback.onException(errorCode, info);
            }
        };

        sendQuest(quest, internalCallback, timeoutInseconds);
    }

    public void translate(String originalMessage, String srcLanguage, String destLanguage, TranslateCallback callback) {
        translate(originalMessage, srcLanguage, destLanguage, callback, questTimeout);
    }

    public void translate(String originalMessage, String destLanguage, TranslateCallback callback, int timeoutInseconds) {
        translate(originalMessage, null, destLanguage, callback, timeoutInseconds);
    }

    public void translate(String originalMessage, String destLanguage, TranslateCallback callback) {
        translate(originalMessage, null, destLanguage, callback, questTimeout);
    }

    public TranslatedResult translate(String originalMessage, String srcLanguage, String destLanguage, int timeoutInseconds) throws RTMException, InterruptedException {

        Quest quest = new Quest("translate");
        quest.param("text", originalMessage);
        quest.param("dst", destLanguage);
        if (srcLanguage != null && srcLanguage.length() != 0)
            quest.param("src", srcLanguage);

        Answer answer = sendQuest(quest, timeoutInseconds);
        if (answer.isErrorAnswer()) {
            int errorCode = answer.getErrorCode();
            String errorMessage = answer.getErrorMessage();
            throw new RTMException(errorCode, errorMessage);
        }

        TranslatedResult result = new TranslatedResult();
        result.srcMessage = (String) answer.get("stext");
        result.srcLanguage = (String) answer.get("src");
        result.destMessage = (String) answer.get("dtext");
        result.destLanguage = (String) answer.get("dst");

        return result;
    }

    public TranslatedResult translate(String originalMessage, String srcLanguage, String destLanguage) throws RTMException, InterruptedException {
        return translate(originalMessage, srcLanguage, destLanguage, questTimeout);
    }

    public TranslatedResult translate(String originalMessage, String destLanguage, int timeoutInseconds) throws RTMException, InterruptedException {
        return translate(originalMessage, null, destLanguage, questTimeout);
    }

    public TranslatedResult translate(String originalMessage, String destLanguage) throws RTMException, InterruptedException {
        return translate(originalMessage, null, destLanguage, questTimeout);
    }

    //=============================[ FileGate APIs ]==============================//

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    private String buildFileAttrs(String token, byte[] fileContent, String filename, String ext) throws GeneralSecurityException, IOException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(fileContent);
        byte[] md5Binary = md5.digest();
        String md5Hex = bytesToHexString(md5Binary) + ":" + token;

        md5 = MessageDigest.getInstance("MD5");
        md5.update(md5Hex.getBytes("UTF-8"));
        md5Binary = md5.digest();
        md5Hex = bytesToHexString(md5Binary);

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"sign\":\"").append(md5Hex).append("\"");

        if (ext != null && ext.length() > 0)
            sb.append(", \"ext\":\"").append(ext).append("\"");

        if (filename != null && filename.length() > 0)
            sb.append(", \"filename\":\"").append(filename).append("\"");

        sb.append("}");

        return sb.toString();
    }

    private class FileInfo {
        byte[] fileContent;
        String filename;
        String filenameExtension;
    }
    private FileInfo readFileForSendAPI(String filePath) throws IOException {
        FileInfo info = new FileInfo();
        info.fileContent = Files.readAllBytes(Paths.get(filePath));

        File file = new File(filePath);
        info.filename = file.getName();
        int pos = info.filename.lastIndexOf(".");
        if (pos > 0)
            info.filenameExtension = info.filename.substring(pos + 1);
        else
            info.filenameExtension = null;

        return info;
    }

    //-----------------[sendfile]-----------------//

    public void sendFile(long peerUid, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback, int timeoutInseconds) {

        Quest fileTokenQuest = new Quest("filetoken");
        fileTokenQuest.param("cmd", "sendfile");
        fileTokenQuest.param("to", peerUid);

        long adjustedTimeout = System.currentTimeMillis();

        AnswerCallback fileTokenCallback = new AnswerCallback() {

            @Override
            public void onException(Answer answer, int errorCode) {

                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");

                String message = "Prepare sending P2P file failed. Cannot get file token. Message: " + info;

                callback.onException(errorCode, message);
            }

            @Override
            public void onAnswer(Answer answer) {

                String token = (String) answer.get("token");
                String endpoint = (String) answer.get("endpoint");

                String attrs;
                try {
                    attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);
                } catch (Exception e) {
                    ErrorRecorder.record("Build attrs for sending P2P file exception.", e);
                    callback.onException(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(), "Build attrs for sending P2P file exception.");
                    return;
                }

                int originalTimeout = timeoutInseconds;
                if (originalTimeout == 0)
                    originalTimeout = ClientEngine.getQuestTimeout();

                int timeout = (int)(System.currentTimeMillis() - adjustedTimeout);
                timeout = originalTimeout - (timeout / 1000);

                if (timeout <= 0) {
                    callback.onException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending P2P file is ready, but no data sent.");
                    return;
                }

                TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeout);

                Quest quest = new Quest("sendfile");
                quest.param("pid", pid);
                quest.param("token", token);
                quest.param("mtype", mType);
                quest.param("from", uid);

                quest.param("to", peerUid);
                quest.param("mid", MidGenerator.gen());
                quest.param("file", fileContent);
                quest.param("attrs", attrs);

                AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

                fileGate.sendQuest(quest, internalCallback, timeout);
            }
        };

        sendQuest(fileTokenQuest, fileTokenCallback, timeoutInseconds);
    }

    public void sendFile(long peerUid, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback) {
        sendFile(peerUid, mType, fileContent, filename, filenameExtension, callback, questTimeout);
    }

    public void sendFile(long peerUid, String mType, String filePath, DoneCallback callback, int timeoutInseconds) throws IOException {
        FileInfo info = readFileForSendAPI(filePath);
        sendFile(peerUid, mType, info.fileContent, info.filename, info.filenameExtension, callback, timeoutInseconds);
    }

    public void sendFile(long peerUid, String mType, String filePath, DoneCallback callback) throws IOException {
        sendFile(peerUid, mType, filePath, callback, questTimeout);
    }

    public void sendFile(long peerUid, String mType, byte[] fileContent, String filename, String filenameExtension, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {

        String token;
        String endpoint;
        long adjustedTimeout = System.currentTimeMillis();

        //-- get file token
        {
            Quest quest = new Quest("filetoken");
            quest.param("cmd", "sendfile");
            quest.param("to", peerUid);

            Answer answer = sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = "Prepare sending P2P file failed. Cannot get file token. Message: " + answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }

            token = (String) answer.get("token");
            endpoint = (String) answer.get("endpoint");
        }

        String attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);

        //-- Recalculate the timeout.
        {
            if (timeoutInseconds == 0)
                timeoutInseconds = ClientEngine.getQuestTimeout();

            adjustedTimeout = System.currentTimeMillis() - adjustedTimeout;
            timeoutInseconds -= adjustedTimeout / 1000;

            if (timeoutInseconds <= 0) {
                throw new RTMException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending P2P file is ready, but no data sent.");
            }
        }

        //-- send data
        {
            TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeoutInseconds);

            Quest quest = new Quest("sendfile");
            quest.param("pid", pid);
            quest.param("token", token);
            quest.param("mtype", mType);
            quest.param("from", uid);

            quest.param("to", peerUid);
            quest.param("mid", MidGenerator.gen());
            quest.param("file", fileContent);
            quest.param("attrs", attrs);

            Answer answer = fileGate.sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }
        }
    }

    public void sendFile(long peerUid, String mType, byte[] fileContent, String filename, String filenameExtension)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendFile(peerUid, mType, fileContent, filename, filenameExtension, questTimeout);
    }

    public void sendFile(long peerUid, String mType, String filePath, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        FileInfo info = readFileForSendAPI(filePath);
        sendFile(peerUid, mType, info.fileContent, info.filename, info.filenameExtension, timeoutInseconds);
    }

    public void sendFile(long peerUid, String mType, String filePath)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendFile(peerUid, mType, filePath, questTimeout);
    }

    //-----------------[sendfiles]-----------------//

    public void sendFiles(Set<Long> uids, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback, int timeoutInseconds) {

        Quest fileTokenQuest = new Quest("filetoken");
        fileTokenQuest.param("cmd", "sendfiles");
        fileTokenQuest.param("tos", uids);

        long adjustedTimeout = System.currentTimeMillis();

        AnswerCallback fileTokenCallback = new AnswerCallback() {

            @Override
            public void onException(Answer answer, int errorCode) {

                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");

                String message = "Prepare sending file to multi-peers failed. Cannot get file token. Message: " + info;

                callback.onException(errorCode, message);
            }

            @Override
            public void onAnswer(Answer answer) {

                String token = (String) answer.get("token");
                String endpoint = (String) answer.get("endpoint");

                String attrs;
                try {
                    attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);
                } catch (Exception e) {
                    ErrorRecorder.record("Build attrs for sending file to multi-peers exception.", e);
                    callback.onException(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(), "Build attrs for sending file to multi-peers exception.");
                    return;
                }

                int originalTimeout = timeoutInseconds;
                if (originalTimeout == 0)
                    originalTimeout = ClientEngine.getQuestTimeout();

                int timeout = (int)(System.currentTimeMillis() - adjustedTimeout);
                timeout = originalTimeout - (timeout / 1000);

                if (timeout <= 0) {
                    callback.onException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending file to multi-peers is ready, but no data sent.");
                    return;
                }

                TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeout);

                Quest quest = new Quest("sendfiles");
                quest.param("pid", pid);
                quest.param("token", token);
                quest.param("mtype", mType);
                quest.param("from", uid);

                quest.param("tos", uids);
                quest.param("mid", MidGenerator.gen());
                quest.param("file", fileContent);
                quest.param("attrs", attrs);

                AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

                fileGate.sendQuest(quest, internalCallback, timeout);
            }
        };

        sendQuest(fileTokenQuest, fileTokenCallback, timeoutInseconds);
    }

    public void sendFiles(Set<Long> uids, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback) {
        sendFiles(uids, mType, fileContent, filename, filenameExtension, callback, questTimeout);
    }

    public void sendFiles(Set<Long> uids, String mType, String filePath, DoneCallback callback, int timeoutInseconds) throws IOException {
        FileInfo info = readFileForSendAPI(filePath);
        sendFiles(uids, mType, info.fileContent, info.filename, info.filenameExtension, callback, timeoutInseconds);
    }

    public void sendFiles(Set<Long> uids, String mType, String filePath, DoneCallback callback) throws IOException {
        sendFiles(uids, mType, filePath, callback, questTimeout);
    }

    public void sendFiles(Set<Long> uids, String mType, byte[] fileContent, String filename, String filenameExtension, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {

        String token;
        String endpoint;
        long adjustedTimeout = System.currentTimeMillis();

        //-- get file token
        {
            Quest quest = new Quest("filetoken");
            quest.param("cmd", "sendfiles");
            quest.param("tos", uids);

            Answer answer = sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = "Prepare sending file to multi-peers failed. Cannot get file token. Message: " + answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }

            token = (String) answer.get("token");
            endpoint = (String) answer.get("endpoint");
        }

        String attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);

        //-- Recalculate the timeout.
        {
            if (timeoutInseconds == 0)
                timeoutInseconds = ClientEngine.getQuestTimeout();

            adjustedTimeout = System.currentTimeMillis() - adjustedTimeout;
            timeoutInseconds -= adjustedTimeout / 1000;

            if (timeoutInseconds <= 0) {
                throw new RTMException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending file to multi-peers is ready, but no data sent.");
            }
        }

        //-- send data
        {
            TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeoutInseconds);

            Quest quest = new Quest("sendfiles");
            quest.param("pid", pid);
            quest.param("token", token);
            quest.param("mtype", mType);
            quest.param("from", uid);

            quest.param("tos", uids);
            quest.param("mid", MidGenerator.gen());
            quest.param("file", fileContent);
            quest.param("attrs", attrs);

            Answer answer = fileGate.sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }
        }
    }

    public void sendFiles(Set<Long> uids, String mType, byte[] fileContent, String filename, String filenameExtension)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendFiles(uids, mType, fileContent, filename, filenameExtension, questTimeout);
    }

    public void sendFiles(Set<Long> uids, String mType, String filePath, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        FileInfo info = readFileForSendAPI(filePath);
        sendFiles(uids, mType, info.fileContent, info.filename, info.filenameExtension, timeoutInseconds);
    }

    public void sendFiles(Set<Long> uids, String mType, String filePath)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendFiles(uids, mType, filePath, questTimeout);
    }

    //-----------------[sendgroupfile]-----------------//

    public void sendGroupFile(long groupId, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback, int timeoutInseconds) {

        Quest fileTokenQuest = new Quest("filetoken");
        fileTokenQuest.param("cmd", "sendgroupfile");
        fileTokenQuest.param("gid", groupId);

        long adjustedTimeout = System.currentTimeMillis();

        AnswerCallback fileTokenCallback = new AnswerCallback() {

            @Override
            public void onException(Answer answer, int errorCode) {

                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");

                String message = "Prepare sending group file failed. Cannot get file token. Message: " + info;

                callback.onException(errorCode, message);
            }

            @Override
            public void onAnswer(Answer answer) {

                String token = (String) answer.get("token");
                String endpoint = (String) answer.get("endpoint");

                String attrs;
                try {
                    attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);
                } catch (Exception e) {
                    ErrorRecorder.record("Build attrs for sending group file exception.", e);
                    callback.onException(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(), "Build attrs for sending group file exception.");
                    return;
                }

                int originalTimeout = timeoutInseconds;
                if (originalTimeout == 0)
                    originalTimeout = ClientEngine.getQuestTimeout();

                int timeout = (int)(System.currentTimeMillis() - adjustedTimeout);
                timeout = originalTimeout - (timeout / 1000);

                if (timeout <= 0) {
                    callback.onException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending group file is ready, but no data sent.");
                    return;
                }

                TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeout);

                Quest quest = new Quest("sendgroupfile");
                quest.param("pid", pid);
                quest.param("token", token);
                quest.param("mtype", mType);
                quest.param("from", uid);

                quest.param("gid", groupId);
                quest.param("mid", MidGenerator.gen());
                quest.param("file", fileContent);
                quest.param("attrs", attrs);

                AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

                fileGate.sendQuest(quest, internalCallback, timeout);
            }
        };

        sendQuest(fileTokenQuest, fileTokenCallback, timeoutInseconds);
    }

    public void sendGroupFile(long groupId, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback) {
        sendGroupFile(groupId, mType, fileContent, filename, filenameExtension, callback, questTimeout);
    }

    public void sendGroupFile(long groupId, String mType, String filePath, DoneCallback callback, int timeoutInseconds) throws IOException {
        FileInfo info = readFileForSendAPI(filePath);
        sendGroupFile(groupId, mType, info.fileContent, info.filename, info.filenameExtension, callback, timeoutInseconds);
    }

    public void sendGroupFile(long groupId, String mType, String filePath, DoneCallback callback) throws IOException {
        sendGroupFile(groupId, mType, filePath, callback, questTimeout);
    }

    public void sendGroupFile(long groupId, String mType, byte[] fileContent, String filename, String filenameExtension, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {

        String token;
        String endpoint;
        long adjustedTimeout = System.currentTimeMillis();

        //-- get file token
        {
            Quest quest = new Quest("filetoken");
            quest.param("cmd", "sendgroupfile");
            quest.param("gid", groupId);

            Answer answer = sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = "Prepare sending group file failed. Cannot get file token. Message: " + answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }

            token = (String) answer.get("token");
            endpoint = (String) answer.get("endpoint");
        }

        String attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);

        //-- Recalculate the timeout.
        {
            if (timeoutInseconds == 0)
                timeoutInseconds = ClientEngine.getQuestTimeout();

            adjustedTimeout = System.currentTimeMillis() - adjustedTimeout;
            timeoutInseconds -= adjustedTimeout / 1000;

            if (timeoutInseconds <= 0) {
                throw new RTMException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending group file is ready, but no data sent.");
            }
        }

        //-- send data
        {
            TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeoutInseconds);

            Quest quest = new Quest("sendgroupfile");
            quest.param("pid", pid);
            quest.param("token", token);
            quest.param("mtype", mType);
            quest.param("from", uid);

            quest.param("gid", groupId);
            quest.param("mid", MidGenerator.gen());
            quest.param("file", fileContent);
            quest.param("attrs", attrs);

            Answer answer = fileGate.sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }
        }
    }

    public void sendGroupFile(long groupId, String mType, byte[] fileContent, String filename, String filenameExtension)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendGroupFile(groupId, mType, fileContent, filename, filenameExtension, questTimeout);
    }

    public void sendGroupFile(long groupId, String mType, String filePath, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        FileInfo info = readFileForSendAPI(filePath);
        sendGroupFile(groupId, mType, info.fileContent, info.filename, info.filenameExtension, timeoutInseconds);
    }

    public void sendGroupFile(long groupId, String mType, String filePath)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendGroupFile(groupId, mType, filePath, questTimeout);
    }

    //-----------------[sendroomfile]-----------------//

    public void sendRoomFile(long roomId, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback, int timeoutInseconds) {

        Quest fileTokenQuest = new Quest("filetoken");
        fileTokenQuest.param("cmd", "sendroomfile");
        fileTokenQuest.param("rid", roomId);

        long adjustedTimeout = System.currentTimeMillis();

        AnswerCallback fileTokenCallback = new AnswerCallback() {

            @Override
            public void onException(Answer answer, int errorCode) {

                String info = null;
                if (answer != null)
                    info = (String) answer.get("ex");

                String message = "Prepare sending room file failed. Cannot get file token. Message: " + info;

                callback.onException(errorCode, message);
            }

            @Override
            public void onAnswer(Answer answer) {

                String token = (String) answer.get("token");
                String endpoint = (String) answer.get("endpoint");

                String attrs;
                try {
                    attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);
                } catch (Exception e) {
                    ErrorRecorder.record("Build attrs for sending room file exception.", e);
                    callback.onException(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(), "Build attrs for sending room file exception.");
                    return;
                }

                int originalTimeout = timeoutInseconds;
                if (originalTimeout == 0)
                    originalTimeout = ClientEngine.getQuestTimeout();

                int timeout = (int)(System.currentTimeMillis() - adjustedTimeout);
                timeout = originalTimeout - (timeout / 1000);

                if (timeout <= 0) {
                    callback.onException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending room file is ready, but no data sent.");
                    return;
                }

                TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeout);

                Quest quest = new Quest("sendroomfile");
                quest.param("pid", pid);
                quest.param("token", token);
                quest.param("mtype", mType);
                quest.param("from", uid);

                quest.param("rid", roomId);
                quest.param("mid", MidGenerator.gen());
                quest.param("file", fileContent);
                quest.param("attrs", attrs);

                AnswerCallback internalCallback = new FPNNDoneCallbackWrapper(callback);

                fileGate.sendQuest(quest, internalCallback, timeout);
            }
        };

        sendQuest(fileTokenQuest, fileTokenCallback, timeoutInseconds);
    }

    public void sendRoomFile(long roomId, String mType, byte[] fileContent, String filename, String filenameExtension, DoneCallback callback) {
        sendRoomFile(roomId, mType, fileContent, filename, filenameExtension, callback, questTimeout);
    }


    public void sendRoomFile(long roomId, String mType, String filePath, DoneCallback callback, int timeoutInseconds) throws IOException {
        FileInfo info = readFileForSendAPI(filePath);
        sendRoomFile(roomId, mType, info.fileContent, info.filename, info.filenameExtension, callback, timeoutInseconds);
    }

    public void sendRoomFile(long roomId, String mType, String filePath, DoneCallback callback) throws IOException {
        sendRoomFile(roomId, mType, filePath, callback, questTimeout);
    }

    public void sendRoomFile(long roomId, String mType, byte[] fileContent, String filename, String filenameExtension, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {

        String token;
        String endpoint;
        long adjustedTimeout = System.currentTimeMillis();

        //-- get file token
        {
            Quest quest = new Quest("filetoken");
            quest.param("cmd", "sendroomfile");
            quest.param("rid", roomId);

            Answer answer = sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = "Prepare sending room file failed. Cannot get file token. Message: " + answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }

            token = (String) answer.get("token");
            endpoint = (String) answer.get("endpoint");
        }

        String attrs = buildFileAttrs(token, fileContent, filename, filenameExtension);

        //-- Recalculate the timeout.
        {
            if (timeoutInseconds == 0)
                timeoutInseconds = ClientEngine.getQuestTimeout();

            adjustedTimeout = System.currentTimeMillis() - adjustedTimeout;
            timeoutInseconds -= adjustedTimeout / 1000;

            if (timeoutInseconds <= 0) {
                throw new RTMException(ErrorCode.FPNN_EC_CORE_TIMEOUT.value(), "Timeout. Prepare sending room file is ready, but no data sent.");
            }
        }

        //-- send data
        {
            TCPClient fileGate = RTMResourceCenter.instance().getFileClient(endpoint, timeoutInseconds);

            Quest quest = new Quest("sendroomfile");
            quest.param("pid", pid);
            quest.param("token", token);
            quest.param("mtype", mType);
            quest.param("from", uid);

            quest.param("rid", roomId);
            quest.param("mid", MidGenerator.gen());
            quest.param("file", fileContent);
            quest.param("attrs", attrs);

            Answer answer = fileGate.sendQuest(quest, timeoutInseconds);
            if (answer.isErrorAnswer()) {
                int errorCode = answer.getErrorCode();
                String message = answer.getErrorMessage();
                throw new RTMException(errorCode, message);
            }
        }
    }

    public void sendRoomFile(long roomId, String mType, byte[] fileContent, String filename, String filenameExtension)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendRoomFile(roomId, mType, fileContent, filename, filenameExtension, questTimeout);
    }

    public void sendRoomFile(long roomId, String mType, String filePath, int timeoutInseconds)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        FileInfo info = readFileForSendAPI(filePath);
        sendRoomFile(roomId, mType, info.fileContent, info.filename, info.filenameExtension, timeoutInseconds);
    }

    public void sendRoomFile(long roomId, String mType, String filePath)
            throws RTMException, IOException, GeneralSecurityException, InterruptedException {
        sendRoomFile(roomId, mType, filePath, questTimeout);
    }
}
