package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.AttrsStruct;
import com.rtmsdk.RTMStruct.RTMAnswer;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

import java.util.List;
import java.util.Map;

class RTMSystem extends RTMUser {

    //重载
    public void bye() {
        bye(true);
    }

    public void kickout(IRTMEmptyCallback callback, String endpoint) {
        kickout(callback, endpoint, 0);
    }

    public RTMAnswer kickout(String endpoint) {
        return kickout(endpoint, 0);
    }

    public void addAttributes(final IRTMEmptyCallback callback, Map<String, String> attrs) {
        addAttributes(callback, attrs, 0);
    }

    public RTMAnswer addAttributes(Map<String, String> attrs){
        return addAttributes(attrs, 0);
    }

    public void getAttributes(IRTMCallback<List<Map<String, String>>> callback) {
        getAttributes(callback, 0);
    }

    public AttrsStruct  getAttributes(){
        return getAttributes(0);
    }

    public void addDebugLog(IRTMEmptyCallback callback, String message, String attrs) {
        addDebugLog(callback, message,attrs,0);
    }

    public RTMAnswer addDebugLog(String message, String attrs){
        return addDebugLog(message, attrs, 0);
    }

    public void addDevice(final IRTMEmptyCallback callback, String appType, String deviceToken) {
        addDevice(callback, appType, deviceToken, 0);
    }

    public RTMAnswer addDevice(String appType, String deviceToken){
        return addDevice(appType, deviceToken, 0);
    }

    public void RemoveDevice(final IRTMEmptyCallback callback, String deviceToken) {
        RemoveDevice(callback, deviceToken, 0);
    }

    public RTMAnswer RemoveDevice(String deviceToken){
        return RemoveDevice(deviceToken, 0);
    }
    //重载end

    /** 断开rtm
     * @param async //是否同步等待
     */
    public void bye(boolean async) {
        sayBye(async);
    }


    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void kickout(final IRTMEmptyCallback callback, String endpoint, int timeout) {
        Quest quest = new Quest("kickout");
        quest.param("ce", endpoint);
        sendQuestEmptyCallback(callback,quest,timeout);
    }

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） sync
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer kickout(String endpoint, int timeout){
        Quest quest = new Quest("kickout");
        quest.param("ce", endpoint);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void addAttributes(final IRTMEmptyCallback callback, Map<String, String> attrs, int timeout) {
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);
        sendQuestEmptyCallback(callback,quest,timeout);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer addAttributes(Map<String, String> attrs, int timeout){
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);
        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 获取用户属性 async
     * @param callback  用户属性回调(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void getAttributes(final IRTMCallback<List<Map<String, String>>> callback, int timeout) {
        Quest quest = new Quest("getattrs");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                List<Map<String, String>> attributes = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    attributes = RTMUtils.wantListHashMap(answer, "attrs");
                callback.onResult(attributes, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     *获取用户属性 async
     * @param timeout   超时时间(秒)
     * @return          List<Map<String, String>>
     */
    public AttrsStruct getAttributes(int timeout){
        Quest quest = new Quest("getattrs");
        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        AttrsStruct ret = new AttrsStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.attrs = RTMUtils.wantListHashMap(answer,"attrs");

        return ret;
    }

    /**
     * 添加debug日志
     * @param callback  IRTMEmptyCallback回调(notnull)
     * @param message   消息内容
     * @param attrs     消息属性信息
     * @param timeout   超时时间
     */
    public void addDebugLog(IRTMEmptyCallback callback, String message, String attrs, int timeout) {
        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 添加debug日志
     * @param message   消息内容
     * @param attrs     消息属性信息
     * @param timeout   超时时间
     * @return          RTMAnswer
     */
    public RTMAnswer addDebugLog(String message, String attrs, int timeout){
        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);
        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 添加设备，应用信息 async
     * @param  callback  IRTMEmptyCallback回调
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void addDevice(IRTMEmptyCallback callback, String appType, String deviceToken, int timeout) {
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 添加设备，应用信息 async
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer addDevice(String appType, String deviceToken, int timeout){
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 删除设备， async
     * @param  callback  IRTMEmptyCallback回调
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public void RemoveDevice(final IRTMEmptyCallback callback, String deviceToken, int timeout) {
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 删除设备， async
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     */
    public RTMAnswer RemoveDevice(String deviceToken, int timeout){
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);
        return sendQuestEmptyResult(quest, timeout);
    }
}
