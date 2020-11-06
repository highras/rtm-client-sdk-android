package com.rtmsdk;

import androidx.annotation.NonNull;

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
    /** 断开rtm
     */
    public void bye() {
        sayBye(true);
    }


    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） async
     * @param callback IRTMEmptyCallback回调
     * @param endpoint  另一个用户的地址
     */
    public void kickout(@NonNull IRTMEmptyCallback callback, String endpoint) {
        Quest quest = new Quest("kickout");
        quest.param("ce", endpoint);
        sendQuestEmptyCallback(callback,quest);
    }

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） sync
     * @param endpoint  另一个用户的地址
     */
    public RTMAnswer kickout(String endpoint){
        Quest quest = new Quest("kickout");
        quest.param("ce", endpoint);

        return sendQuestEmptyResult(quest);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param callback IRTMEmptyCallback回调
     * @param attrs     客户端自定义属性值
     */
    public void addAttributes(@NonNull IRTMEmptyCallback callback, Map<String, String> attrs) {
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);
        sendQuestEmptyCallback(callback,quest);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param attrs     客户端自定义属性值
     */
    public RTMAnswer addAttributes(Map<String, String> attrs){
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);
        return sendQuestEmptyResult(quest);
    }

    /**
     * 获取用户属性 async
     * @param callback  用户属性回调 其中map的key
     *                  map中自动添加如下几个参数：
     *                  ce：链接的endpoint，需要让其下线可以调用kickout
     *                  login：登录时间，utc时间戳
     *                  my：当前链接的attrs
     */
    public void getAttributes(@NonNull final IRTMCallback<List<Map<String, String>>> callback) {
        Quest quest = new Quest("getattrs");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                List<Map<String, String>> attributes = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    attributes = RTMUtils.wantListHashMap(answer, "attrs");
                callback.onResult(attributes, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     *获取用户属性 async
     * @return      AttrsStruct
     */
    public AttrsStruct getAttributes(){
        Quest quest = new Quest("getattrs");
        Answer answer = sendQuest(quest);
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
     */
    public void addDebugLog(@NonNull IRTMEmptyCallback callback, String message, String attrs) {
        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 添加debug日志
     * @param message   消息内容
     * @param attrs     消息属性信息
     * @return          RTMAnswer
     */
    public RTMAnswer addDebugLog(@NonNull String message, @NonNull String attrs){
        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);
        return sendQuestEmptyResult(quest);
    }

    /**
     * 添加设备，应用信息 async
     * @param  callback  IRTMEmptyCallback回调
     * @param appType     应用类型
     * @param deviceToken   设备token
     */
    public void addDevice(@NonNull IRTMEmptyCallback callback, @NonNull String appType, @NonNull String deviceToken) {
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 添加设备，应用信息 async
     * @param appType     应用类型
     * @param deviceToken   设备token
     */
    public RTMAnswer addDevice(String appType, String deviceToken){
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 删除设备， async
     * @param  callback  IRTMEmptyCallback回调
     * @param deviceToken   设备token
     */
    public void RemoveDevice(@NonNull IRTMEmptyCallback callback, @NonNull String deviceToken) {
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 删除设备， async
     * @param deviceToken   设备token
     */
    public RTMAnswer RemoveDevice(String deviceToken){
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);
        return sendQuestEmptyResult(quest);
    }
}
