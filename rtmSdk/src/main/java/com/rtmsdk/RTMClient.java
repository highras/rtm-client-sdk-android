package com.rtmsdk;

import android.content.Context;

import java.util.Map;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

public class RTMClient extends RTMChat {
    private long pid;
    private long uid;
    private String token;
    private String endpoint;

    /**
     *
     * @param endpoint 客户端网关地址
     * @param pid      项目id
     * @param uid       用户id
     * @param serverPushProcessor serverpush类
     */
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
            errorRecorder.recordError("rtmclient init error " + errDesc);
        this.pid = pid;
        this.uid = uid;
        this.endpoint = endpoint;
        RTMInit(endpoint, pid, uid, serverPushProcessor);
    }

    /**
     * 配置rtm全局参数
     * @param config RTMConfig结构
     */
    void config(RTMConfig config){
        RTMConfig.Config(config);
    }

    /**
     * 关闭rtm
     */
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

    public void login(IRTMEmptyCallback callback, String token) {
        super.login(callback, token, "", "ipv4",null);
    }

    /**
     *rtm登陆  sync
     * @param token     用户token
     * @param lang      用户语言(详见TranslateLang.java枚举列表)(当项目启用了自动翻译  如果客户端设置了语言会收到翻译后的结果 不设置语言或者为空则不会自动翻译,后续可以通过setTranslatedLanguage设置)
     * @param attr      登陆附加信息
     * @param addressType   连接网关的类型 "ipv4"-ip地址 "domain"-域名
     */
    public RTMStruct.RTMAnswer login(String token, String lang, Map<String, String> attr, String addressType) {
        return super.login(token, lang, attr, addressType);
    }

    /**
     *rtm登陆  async
     * @param callback  登陆结果回调
     * @param token     用户token
     * @param lang      用户语言(详见TranslateLang.java枚举列表)(当项目启用了自动翻译  如果客户端设置了语言会收到翻译后的结果 不设置语言或者为空则不会自动翻译,后续可以通过setTranslatedLanguage设置)
     * @param attr      登陆附加信息
     * @param addressType   连接网关的类型 "ipv4"-ip地址 "domain"-域名
     */
    public void login(IRTMEmptyCallback callback,String token, String lang, Map<String, String> attr, String addressType) {
        super.login(callback, token, lang, addressType, attr);
    }

    /**
     *
     * @param applicationContext
     * @param startCallback        //重连开始回调
     * @param completedCallback    //重连结束回调
     */
    public void setAutoConnect(Context applicationContext, UserInterface.IReloginStart startCallback, UserInterface.IReloginCompleted completedCallback) {
        super.setAutoConnect(applicationContext,startCallback, completedCallback);
    }
}
