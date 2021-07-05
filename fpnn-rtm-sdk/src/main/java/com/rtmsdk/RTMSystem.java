package com.rtmsdk;

import androidx.annotation.NonNull;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class RTMSystem extends RTMUser {
    /**
     *
     * @param async 用户下线
     */
    public void bye(boolean async) {
        sayBye(async);
    }

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） sync
     * @param endpoint  另一个用户的地址(用户的地址可以通过 getAttributes拿到)
     */
    public RTMStruct.RTMAnswer kickout(String endpoint){
        Quest quest = new Quest("kickout");
        quest.param("ce", endpoint);

        return sendQuestEmptyResult(quest);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param callback IRTMEmptyCallback回调
     * @param attrs     客户端自定义属性值
     */
    public void addAttributes(@NonNull UserInterface.IRTMEmptyCallback callback, Map<String, String> attrs) {
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);
        sendQuestEmptyCallback(callback,quest);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param attrs     客户端自定义属性值
     */
    public RTMStruct.RTMAnswer addAttributes(Map<String, String> attrs){
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);
        return sendQuestEmptyResult(quest);
    }

    /**
     * 获取用户属性 async
     * @param callback  用户属性回调 其中map的key
     *                  map中自动添加如下几个参数：
     *                  login：登录时间，utc时间戳
     *                  my：当前链接的attrs
     */
    public void getAttributes(@NonNull final UserInterface.IRTMCallback<List<Map<String, String>>> callback) {
        Quest quest = new Quest("getattrs");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                List<Map<String, String>> attributes = new ArrayList<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    attributes = rtmUtils.wantListHashMap(answer, "attrs");
                callback.onResult(attributes, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     *获取用户属性 async
     * @return      AttrsStruct
     */
    public RTMStruct.AttrsStruct getAttributes(){
        Quest quest = new Quest("getattrs");
        Answer answer = sendQuest(quest);
        RTMStruct.RTMAnswer result = genRTMAnswer(answer);
        RTMStruct.AttrsStruct ret = new RTMStruct.AttrsStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.attrs = rtmUtils.wantListHashMap(answer,"attrs");

        return ret;
    }

    /**
     * 添加debug日志
     * @param callback  IRTMEmptyCallback回调(notnull)
     * @param message   消息内容
     * @param attrs     消息属性信息
     */
    public void addDebugLog(@NonNull UserInterface.IRTMEmptyCallback callback, String message, String attrs) {
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
    public RTMStruct.RTMAnswer addDebugLog(@NonNull String message, @NonNull String attrs){
        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);
        return sendQuestEmptyResult(quest);
    }

    /**
     * 添加设备，应用信息 async
     * @param  callback  IRTMEmptyCallback回调
     * @param appType     应用类型 fcm(android) 或者 apns(ios)
     * @param deviceToken   设备推送token
     */
    public void addDevice(@NonNull UserInterface.IRTMEmptyCallback callback, @NonNull String appType, @NonNull String deviceToken) {
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 添加设备，应用信息 async
     * @param appType     应用类型 fcm(android) 或者 apns(ios)
     * @param deviceToken   设备推送token
     */
    public RTMStruct.RTMAnswer addDevice(String appType, String deviceToken){
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 删除设备， async
     * @param  callback  IRTMEmptyCallback回调
     * @param deviceToken   设备推送token
     */
    public void removeDevice(@NonNull UserInterface.IRTMEmptyCallback callback, @NonNull String deviceToken) {
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 删除设备， async
     * @param deviceToken   设备推送token
     */
    public RTMStruct.RTMAnswer removeDevice(String deviceToken){
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);
        return sendQuestEmptyResult(quest);
    }

    /**设置设备推送属性(注意此接口是设置个人或群组某个类型的type不推送的设置) sync
     * @param type  type=0, 设置某个p2p 不推送；type=1, 设置某个group不推送
     * @param xid   当type =0 时 表示userid；当type =1时 表示groupId
     * @param messageTypes (为空，则所有mtype均不推送;否则表示指定mtype不推送)
     * @return  RTMAnswer
     */
    public RTMAnswer addDevicePushOption(@NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes){
        Quest quest = new Quest("addoption");
        quest.param("type", type);
        quest.param("xid", xid);
        if (messageTypes != null)
            quest.param("mtypes", messageTypes);
        return sendQuestEmptyResult(quest);
    }

    /**取消设备推送属性(和addDevicePushOption对应) sync
     * @param type  type=0, 取消p2p推送属性；type=1, 取消group推送属性
     * @param xid   当type =0 时 表示userid；当type =1时 表示groupId
     * @param messageTypes  需要取消设置的messagetype集合(如果为空表示什么都不做)
     * @return  RTMAnswer
     */
    public RTMAnswer removeDevicePushOption(@NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes){
        Quest quest = new Quest("removeoption");
        quest.param("type", type);
        quest.param("xid", xid);
        if (messageTypes != null)
            quest.param("mtypes", messageTypes);
        return sendQuestEmptyResult(quest);
    }

    /**获取设备推送属性(addDevicePushOption的结果) sync
     * @return DevicePushOption
     */
    public DevicePushOption getDevicePushOption() {
        Quest quest = new Quest("getoption");
        DevicePushOption ret = new DevicePushOption();

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            ret.p2pPushOptions = rtmUtils.wantDeviceOption(answer,"p2p");
            ret.groupPushOptions = rtmUtils.wantDeviceOption(answer,"group");
        }
        return ret;
    }



    /**设置设备推送属性(注意此接口是设置个人或群组某个类型的type不推送的设置) async
     * @param callback      IRTMEmptyCallback回调
     * @param type  type=0, 设置某个p2p 不推送；type=1, 设置某个group不推送
     * @param xid   当type =0 时 表示userid；当type =1时 表示groupId
     * @param messageTypes (为空，则所有mtype均不推送;否则表示指定mtype不推送)
     */
    public void addDevicePushOption(@NonNull UserInterface.IRTMEmptyCallback callback, @NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes){
        Quest quest = new Quest("addoption");
        quest.param("type", type);
        quest.param("xid", xid);
        if (messageTypes != null)
            quest.param("mtypes", messageTypes);
        sendQuestEmptyCallback(callback, quest);
    }

    /**取消设备推送属性(和addDevicePushOption对应) async
     * @param callback      IRTMEmptyCallback回调
     * @param type  type=0, 取消p2p推送属性；type=1, 取消group推送属性
     * @param xid   当type =0 时 表示userid；当type =1时 表示groupId
     * @param messageTypes  需要取消设置的messagetype集合(如果为空表示什么都不做)
     */
    public void removeDevicePushOption(UserInterface.IRTMEmptyCallback callback, @NonNull int type, @NonNull long xid, HashSet<Integer> messageTypes){
        Quest quest = new Quest("removeoption");
        quest.param("type", type);
        quest.param("xid", xid);
        if (messageTypes != null)
            quest.param("mtypes", messageTypes);
        sendQuestEmptyCallback(callback, quest);
    }

    /**获取设备推送属性(addDevicePushOption的结果) async
     * @param callback 回调
     */
    public void getDevicePushOption(final UserInterface.IRTMCallback<DevicePushOption> callback) {
        Quest quest = new Quest("getoption");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                DevicePushOption ret = new DevicePushOption();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    ret.p2pPushOptions = rtmUtils.wantDeviceOption(answer,"p2p");
                    ret.groupPushOptions = rtmUtils.wantDeviceOption(answer,"group");
                }
                callback.onResult(ret, genRTMAnswer(answer,errorCode));
            }
        });
    }
}
