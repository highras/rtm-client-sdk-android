package com.rtmsdk;

import java.util.Map;

public class RTMClient extends RTMChat {
    private long pid;
    private long uid;
    private String token;
    private String endpoint;

    public RTMClient(String endpoint, long pid, long uid, IRTMQuestProcessor serverPushProcessor){
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

    public long getPid() {
        return pid;
    }

    public long getUid() {
        return uid;
    }

    public int login(String token) {
        return login(token, "", null,"ipv4");
    }

    public int login(String token, String lang, Map<String, String> attr, String addressType) {
        return super.login(token, lang, attr, addressType);
    }

    public boolean login(UserInterface.ErrorCodeCallback callback, String token) {
        return super.login(callback, token, "", "ipv4",null);
    }

    public boolean login(UserInterface.ErrorCodeCallback callback,String token, String lang, Map<String, String> attr, String addressType) {
        return super.login(callback, token, lang, addressType, attr);
    }
}
