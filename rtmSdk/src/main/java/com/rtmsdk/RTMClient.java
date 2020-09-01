package com.rtmsdk;

import android.content.Context;

import java.util.Map;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

public class RTMClient extends RTMChat {
    private long pid;
    private long uid;
    private String token;
    private String endpoint;

    public RTMClient(String endpoint, long pid, long uid, RTMPushProcessor serverPushProcessor){
        String errDesc = "";
        if (endpoint == null || endpoint.equals("") || endpoint.lastIndexOf(':') == -1)
            errDesc = "invalid endpoint:" + endpoint;
        if (pid <= 0)
            errDesc += " pid is invalid:" + pid;
        if (uid <= 0)
            errDesc += " uid is invalid:" + uid;
        if (serverPushProcessor == null)
            errDesc += " IRTMQuestProcessor is null";

        if (!errDesc.equals(""))
            throw new IllegalArgumentException(errDesc);
        this.pid = pid;
        this.uid = uid;
        this.endpoint = endpoint;
        RTMInit(endpoint, pid, uid, serverPushProcessor);
    }

    void init(RTMConfig config){
        RTMConfig.Config(config);
    }

    public void closeRTM(){
        realClose();
    }

    public long getPid() {
        return pid;
    }

    public long getUid() {
        return uid;
    }

    public RTMStruct.RTMAnswer  login(String token) {
        return login(token, "", null,"ipv4");
    }

    public RTMStruct.RTMAnswer login(String token, String lang, Map<String, String> attr, String addressType) {
        return super.login(token, lang, attr, addressType);
    }

    public void login(IRTMEmptyCallback callback, String token) {
        super.login(callback, token, "", "ipv4",null);
    }

    public void login(IRTMEmptyCallback callback,String token, String lang, Map<String, String> attr, String addressType) {
        super.login(callback, token, lang, addressType, attr);
    }

    public void setAutoConnect(Context applicationContext, UserInterface.IReloginStart startCallback, UserInterface.IReloginCompleted completedCallback) {
        super.setAutoConnect(applicationContext,startCallback, completedCallback);
    }
}
