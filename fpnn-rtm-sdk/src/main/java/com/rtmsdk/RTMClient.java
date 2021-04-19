package com.rtmsdk;

import android.content.Context;

import java.util.Map;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

public class RTMClient extends RTMChat {
    public RTMClient(String endpoint, long pid, long uid, RTMPushProcessor serverPushProcessor,Context applicationContext) {
        RTMInit(endpoint, pid, uid, serverPushProcessor,applicationContext,null);
    }

    /**
     *
     * @param endpoint RTM网关地址
     * @param pid      项目id
     * @param uid       用户id
     * @param serverPushProcessor serverpush类
     * @applicationContext
     * @RTMConfig
     */
    public RTMClient(String endpoint, long pid, long uid, RTMPushProcessor serverPushProcessor,Context applicationContext,RTMConfig config){
        RTMInit(endpoint, pid, uid, serverPushProcessor,applicationContext,config);
    }

    /** 用户下线(单纯的用户下线)
     */
    public void bye() {
        bye(true);
    }

    /**
     * 关闭rtm(下线并释放资源,网络广播监听会持有RTMClient对象 如果不调用RTMClient对象会一直持有不释放)
     */
    public void closeRTM(){
        realClose();
    }

    public long getPid() {
        return super.getPid();
    }

    public long getUid() {
        return super.getUid();
    }

    /**获取用户登录状态
     *
     */
    public boolean isOnline() {
        return getClientStatus() == ClientStatus.Connected?true:false;
    }

    public RTMStruct.RTMAnswer login(String token) {
        return login(token, "", null);
    }

    public void login(IRTMEmptyCallback callback, String token) {
        super.login(callback, token, "",null);
    }

    /**
     *rtm登陆  sync
     * @param token     用户token
     * @param lang      用户语言(详见TranslateLang.java枚举列表)(当项目启用了自动翻译  如果客户端设置了语言会收到翻译后的结果 不设置语言或者为空则不会自动翻译,后续可以通过setTranslatedLanguage设置)
     * @param attr      登陆附加信息
     */
    public RTMStruct.RTMAnswer login(String token, String lang, Map<String, String> attr) {
        return super.login(token, lang, attr);
    }

    /**
     *rtm登陆  async
     * @param callback  登陆结果回调
     * @param token     用户token
     * @param lang      用户语言(详见TranslateLang.java枚举列表)(当项目启用了自动翻译  如果客户端设置了语言会收到翻译后的结果 不设置语言或者为空则不会自动翻译,后续可以通过setTranslatedLanguage设置)
     * @param attr      登陆附加信息
     */
    public void login(IRTMEmptyCallback callback,String token, String lang, Map<String, String> attr) {
        super.login(callback, token, lang, attr);
    }
}
