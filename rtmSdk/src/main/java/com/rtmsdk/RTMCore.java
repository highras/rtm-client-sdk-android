package com.rtmsdk;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.fpnn.sdk.ConnectionWillCloseCallback;
import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.ErrorRecorder;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.TCPClient;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;
import com.rtmsdk.UserInterface.IReloginCompleted;
import com.rtmsdk.UserInterface.IReloginStart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

class RTMCore  implements INetEvent{
    public enum ClientStatus {
        Closed,
        Connecting,
        Connected
    }

    public enum CloseType {
        ByUser,
        NetWoekSwitch,
        ByServer,
        Timeout,
        None
    }

    //-------------[ Fields ]--------------------------//
    public static INetEvent mINetEvent;
    private NetStateReceiver stateReceiver;
    private final Object interLocker =  new Object();
    private long pid;
    private long uid;
    private String token;
    private String lang;
    private String loginAddresType;
    private Map<String, String>  loginAttrs;

    private String curve;
    private byte[] encrptyData;
    private boolean autoConnect;
    private Context context;
    private static CloseType closedCase = CloseType.None;
    private int lastNetType = NetUtils.NETWORK_NOTINIT;
    private AtomicBoolean isRelogin = new AtomicBoolean(false);

    private ClientStatus status = ClientStatus.Closed;
    private AtomicBoolean running = new AtomicBoolean(true);
    private AtomicBoolean initCheckThread = new AtomicBoolean(false);
    private Thread checkThread;

    private RTMQuestProcessor processor;
    protected com.fpnn.sdk.ErrorRecorder errorRecorder = (ErrorRecorder)ErrorRecorder.getInstance();

    private TCPClient dispatch;
    private TCPClient rtmGate;
    private Map<String, Map<TCPClient, Long>> fileGates;
    private int connectionId = 0;
    private boolean authFinish = false;
    private RTMStruct.RTMAnswer lastReloginAnswer = new RTMStruct.RTMAnswer();
    private boolean noNetWorkNotify = false;

    private IReloginStart reloginStartCallback;
    private IReloginCompleted reloginCompletedCallback;
    private ArrayList<Integer> finishCodeList = new ArrayList<Integer>(){
        {
            add(RTMErrorCode.RTM_EC_INVALID_AUTH_TOEKN.value());
            add(RTMErrorCode.RTM_EC_PROJECT_BLACKUSER.value());
        }
    };

    void setAutoConnect(Context applicationContext, IReloginStart startCallback, IReloginCompleted completedCallback){
        if (applicationContext == null || startCallback == null || completedCallback == null) {
            if (errorRecorder != null)
                errorRecorder.recordError("invalid setAutoConnect parma ");
            return;
        }
        mINetEvent = this;
        context = applicationContext;
        autoConnect = true;
        reloginStartCallback = startCallback;
        reloginCompletedCallback = completedCallback;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        stateReceiver = new NetStateReceiver();
        context.registerReceiver(stateReceiver,intentFilter);
    }

    void reloginEvent(int count){
//        isRelogin.set(true);
        int num = count;
        Map<String, String> kk = loginAttrs;
        if (reloginStartCallback.reloginWillStart(uid, lastReloginAnswer, num)) {
            lastReloginAnswer = login(token, lang, kk, loginAddresType);
            if(lastReloginAnswer.errorCode == ErrorCode.FPNN_EC_OK.value()) {
                isRelogin.set(false);
                reloginCompletedCallback.reloginCompleted(uid, true, lastReloginAnswer, num);
                return;
            }
            else {
                if (finishCodeList.contains(lastReloginAnswer.errorCode)){
                    isRelogin.set(false);
                    reloginCompletedCallback.reloginCompleted(uid, false, lastReloginAnswer, num);
                    return;
                }
                else {
                    if (!isRelogin.get()) {
                        reloginCompletedCallback.reloginCompleted(uid, false, lastReloginAnswer, num);
                        return;
                    }
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        isRelogin.set(false);
                        reloginCompletedCallback.reloginCompleted(uid, false, lastReloginAnswer, num);
                        return;
                    }
                    reloginEvent(++num);
                }
            }
        }
        else {
            isRelogin.set(false);
            reloginCompletedCallback.reloginCompleted(uid, false, lastReloginAnswer, --num);
        }
    }

    public void onNetChange(int netWorkState){
        if (lastNetType != NetUtils.NETWORK_NOTINIT) {
            switch (netWorkState) {
                case NetUtils.NETWORK_NONE:
//                    close();
                    //                sayBye(true);
                    noNetWorkNotify = true;
//                    if (isRelogin.get()){
//                        isRelogin.set(false);
//                    }
                    break;
                case NetUtils.NETWORK_MOBILE:
                case NetUtils.NETWORK_WIFI:
                    noNetWorkNotify = false;
                    if (lastNetType != netWorkState && autoConnect) {
                        if (isRelogin.get())
                            return;

                        isRelogin.set(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (getClientStatus() == ClientStatus.Connected){
                                    sayBye(new IRTMEmptyCallback() {
                                        @Override
                                        public void onResult(RTMStruct.RTMAnswer answer) {
                                            reloginEvent(1);
                                        }
                                    });
                                }
                                else
                                    reloginEvent(1);
                            }
                        }).start();
                    }
                    break;
            }
        }
        lastNetType = netWorkState;
    }

    void RTMInit(String endpoint, long pid, long uid, RTMPushProcessor serverPushProcessor) {
        try {
            this.pid = pid;
            this.uid = uid;
            autoConnect = false;

            fileGates = new HashMap<>();
            processor = new RTMQuestProcessor();
            processor.SetProcessor(serverPushProcessor);

            dispatch = TCPClient.create(endpoint, true);
            dispatch.connectTimeout = RTMConfig.globalConnectTimeoutSeconds;
            dispatch.setQuestTimeout(RTMConfig.globalConnectTimeoutSeconds);
            isRelogin.set(false);
        }
        catch (Exception ex){
            Log.e("rtmsdk","RTMInit error " + ex.getMessage());
        }
    }

    public void setErrorRecoder(com.fpnn.sdk.ErrorRecorder value){
        if (value == null)
            return;
        synchronized (interLocker) {
            errorRecorder = value;
            processor.SetErrorRecorder(value);
            if (dispatch != null)
                dispatch.SetErrorRecorder(value);
        }
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

    protected long getPid() {
        return pid;
    }

    protected long getUid() {
        return uid;
    }

    protected String getToken() {
        return token;
    }

    synchronized protected ClientStatus getClientStatus() {
        synchronized (interLocker) {
            return status;
        }
    }

    private boolean connectionIsAlive() {
        return processor.ConnectionIsAlive();
    }

    RTMStruct.RTMAnswer genRTMAnswer(int errCode){
        return genRTMAnswer(errCode,"");
    }

    RTMStruct.RTMAnswer genRTMAnswer(int errCode,String msg)
    {
        RTMStruct.RTMAnswer tt = new RTMStruct.RTMAnswer();
        tt.errorCode = errCode;
        if (msg.isEmpty())
            tt.errorMsg = RTMErrorCode.getMsg(errCode);
        else
            tt.errorMsg = msg;
        return tt;
    }


    RTMStruct.RTMAnswer genRTMAnswer(Answer answer) {
        if (answer == null)
            return genRTMAnswer(answer, ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value());
        return new RTMStruct.RTMAnswer(answer.getErrorCode(),answer.getErrorMessage());
    }


    RTMStruct.RTMAnswer genRTMAnswer(Answer answer,int errcode) {
        if (answer == null && errcode !=0) {
            if (errcode == ErrorCode.FPNN_EC_CORE_TIMEOUT.value())
                return new RTMStruct.RTMAnswer(errcode, "FPNN_EC_CORE_TIMEOUT");
            else
                return new RTMStruct.RTMAnswer(errcode,"fpnn  error");
        }
        else
            return new RTMStruct.RTMAnswer(answer.getErrorCode(),answer.getErrorMessage());
    }

    private TCPClient getCoreClient() {
        synchronized (interLocker) {
            if (status == ClientStatus.Connected)
                return rtmGate;
            else
                return null;
        }
    }

    Answer sendQuest(Quest quest) {
        return sendQuest(quest,RTMConfig.globalQuestTimeoutSeconds);
    }

    Answer sendQuest(Quest quest, int timeout) {
        TCPClient client = getCoreClient();
        if (client == null)
            return null;

        Answer answer = null;
        try {
            answer = client.sendQuest(quest, timeout);
        } catch (Exception e) {
            if (errorRecorder != null)
                errorRecorder.recordError(e);
            answer = new Answer(quest);
            answer.fillErrorInfo(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(),e.getMessage());
            Thread.currentThread().interrupt();
        }
        return answer;
    }

    void setCloseType(CloseType type)
    {
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
            if (stateReceiver != null)
                context.unregisterReceiver(stateReceiver);
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

    void sendQuest(Quest quest, final FunctionalAnswerCallback callback) {
        sendQuest(quest, callback, RTMConfig.globalQuestTimeoutSeconds);
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
            timeout = RTMConfig.globalQuestTimeoutSeconds;
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
        }, RTMConfig.globalQuestTimeoutSeconds);
    }

    RTMStruct.RTMAnswer sendQuestEmptyResult(Quest quest){
        Answer ret =  sendQuest(quest);
        if (ret == null)
            return genRTMAnswer(ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value(),"invalid connection");
        return genRTMAnswer(ret);
    }

    void activeFileGateClient(String endpoint, final TCPClient client) {
        synchronized (interLocker) {
            if (fileGates.containsKey(endpoint)) {
                if (fileGates.get(endpoint) != null)
                    fileGates.get(endpoint).put(client, RTMUtils.getCurrentSeconds());
            }
            else
                fileGates.put(endpoint, new HashMap<TCPClient, Long>() {{
                    put(client, RTMUtils.getCurrentSeconds());
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
        client.connectTimeout = RTMConfig.globalConnectTimeoutSeconds;
        client.setQuestTimeout(RTMConfig.globalQuestTimeoutSeconds);

        if (encrptyData != null && curve!=null && !curve.equals(""))
            client.enableEncryptorByDerData(curve, encrptyData);

        if (errorRecorder != null)
            client.SetErrorRecorder(errorRecorder);

        client.setQuestProcessor(processor, "com.rtmsdk.RTMQuestProcessor");
        processor.setAnswerTCPclient(client);
//        client.setConnectedCallback(new ConnectionConnectedCallback() {
//            @Override
//            public void connectResult(InetSocketAddress peerAddress, int _connectionId,boolean connected) {
//                connectionId = _connectionId;
//            }
//        });
        client.setWillCloseCallback(new ConnectionWillCloseCallback() {
            @Override
            public void connectionWillClose(InetSocketAddress peerAddress, int _connectionId,boolean causedByError) {
                if (connectionId != 0 && connectionId == _connectionId && closedCase != CloseType.ByUser && getClientStatus() != ClientStatus.Connecting) {
                    close();
                    processor.rtmConnectClose(causedByError ? ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value() : ErrorCode.FPNN_EC_OK.value());
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
                                if (noNetWorkNotify)
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

    private RTMStruct.RTMAnswer auth(String token, Map<String, String> attr) {
        return auth(token, attr, false);
    }

    private RTMStruct.RTMAnswer auth(String token, Map<String, String> attr, boolean retry) {
        Quest qt = new Quest("auth");
        qt.param("pid", pid);
        qt.param("uid", uid);
        qt.param("token", token);
        qt.param("lang", lang);
        qt.param("version", "Android-" + RTMConfig.SDKVersion);

        if (attr != null)
            qt.param("attrs", attr);
        try {
            Answer answer = rtmGate.sendQuest(qt,RTMConfig.globalConnectTimeoutSeconds);

            if (answer.getErrorCode() != ErrorCode.FPNN_EC_OK.value()) {
                closeStatus();
                return genRTMAnswer(answer);
            }
            else if (!answer.wantBoolean("ok")) {
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
                    return auth(token, attr, true);
                }
            }
            synchronized (interLocker) {
                status = ClientStatus.Connected;
            }
            processor.setLastPingTime(RTMUtils.getCurrentSeconds());
            checkRoutineInit();
            authFinish = true;
            connectionId = rtmGate.getConnectionId();
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
        qt.param("version", "Android-" + RTMConfig.SDKVersion);
        if (attr != null)
            qt.param("attrs", attr);

        rtmGate.sendQuest(qt, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                try {
                    if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        closeStatus();
                        callback.onResult(genRTMAnswer(answer, errorCode));
                    } else if (!answer.wantBoolean("ok")) {
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
                                auth(callback, token, attr, true);
                            }
                        }
                    } else {
                        synchronized (interLocker) {
                            status = ClientStatus.Connected;
                        }
                        processor.setLastPingTime(RTMUtils.getCurrentSeconds());
                        checkRoutineInit();
                        authFinish = true;
                        connectionId = rtmGate.getConnectionId();
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
            this.loginAddresType = addressType;
            this.loginAttrs = attr;
            closedCase = CloseType.None;

            if (rtmGate != null) {
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
                                ConfigRtmGateClient(rtmGate);
                                auth(callback, token, attr);
                            }
                        }
                    }
                }, RTMConfig.globalConnectTimeoutSeconds);
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

    RTMStruct.RTMAnswer login(String token, String lang, Map<String, String> attr, String addressType) {
        try {
            if (lang == null)
                this.lang = "";
            else
                this.lang = lang;
            this.token =  token;
            this.loginAddresType = addressType;
            this.loginAttrs = attr;
            closedCase = CloseType.None;

            synchronized (interLocker) {
                if (status == ClientStatus.Connected || status == ClientStatus.Connecting)
                    return genRTMAnswer(ErrorCode.FPNN_EC_OK.value());

                status = ClientStatus.Connecting;
            }

            if (rtmGate != null){
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
            Answer answer = dispatch.sendQuest(quest,RTMConfig.globalConnectTimeoutSeconds);
            if (answer.getErrorCode() != ErrorCode.FPNN_EC_OK.value()) {
                closeStatus();
                return genRTMAnswer(answer);
            }

            String endpoint = answer.getString("endpoint");
            if (endpoint.equals("")) {
                closeStatus();
                return genRTMAnswer(RTMErrorCode.RTM_EC_UNKNOWN_ERROR.value(),"rtmgate not get");
                //返回错误码
            }
            rtmGate = TCPClient.create(endpoint);
            dispatch.close();
            ConfigRtmGateClient(rtmGate);
            return auth(token, attr);
        } catch (Exception e) {
            closeStatus();
            return  genRTMAnswer(ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value(),e.getMessage());
        }
    }

    public void close() {
        synchronized (interLocker) {
            authFinish = false;
            initCheckThread.set(false);
            running.set(false);
            if (status == ClientStatus.Closed)
                return;
            status = ClientStatus.Closed;
        }
        if (rtmGate !=null)
            rtmGate.close();
    }
}