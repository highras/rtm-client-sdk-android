package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class RTMSystem extends RTMUser {
    public void bye() {
        bye(true);
    }

    public void bye(boolean async) {
        sayBye(async);
    }

    //===========================[ Kickout ]=========================//
    public boolean kickout(UserInterface.ErrorCodeCallback callback, String endpoint) {
        return kickout(callback, endpoint, 0);
    }

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean kickout(final UserInterface.ErrorCodeCallback callback, String endpoint, int timeout) {
        Quest quest = new Quest("kickout");
        quest.param("ce", endpoint);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                callback.call(errorCode);
            }
        }, timeout);
    }

    public int kickout(String endpoint) {
        return kickout(endpoint, 0);
    }

    /**
     *踢掉一个链接（只对多用户登录有效，不能踢掉自己，可以用来实现同类设备，只容许一个登录） sync
     * @param endpoint  另一个用户的地址(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int kickout(String endpoint, int timeout) {
        Quest quest = new Quest("kickout");
        quest.param("ce", endpoint);

        return sendQuestCode(quest, timeout);
    }


    //===========================[ Add Attributes ]=========================//
    public boolean addAttributes(final UserInterface.ErrorCodeCallback callback, Map<String, String> attrs) {
        return addAttributes(callback, attrs, 0);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean addAttributes(final UserInterface.ErrorCodeCallback callback, Map<String, String> attrs, int timeout) {
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                callback.call(errorCode);
            }
        }, timeout);
    }

    public int addAttributes(Map<String, String> attrs) {
        return addAttributes(attrs, 0);
    }

    /**
     *添加key_value形式的变量（例如设置客户端信息，会保存在当前链接中） async
     * @param attrs     客户端自定义属性值(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int addAttributes(Map<String, String> attrs, int timeout) {
        Quest quest = new Quest("addattrs");
        quest.param("attrs", attrs);
        return sendQuestCode(quest, timeout);
    }

    //===========================[ Get Attributes ]=========================//
    //-- Action<attributes, errorCode>
    public boolean getAttributes(final UserInterface.AttrsCallback callback) {
        return getAttributes(callback, 0);
    }

    /**
     * 获取用户属性 async
     * @param callback  用户属性回调(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getAttributes(final UserInterface.AttrsCallback callback, int timeout) {
        Quest quest = new Quest("getattrs");

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                List<Map<String, String>> attributes = new ArrayList<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    RTMUtils.wantListHashMap(answer,"attrs", attributes);

                callback.call(attributes, errorCode);
            }
        }, timeout);
    }

    public int getAttributes(List<Map<String, String>> attributes) {
        return getAttributes(attributes, 0);
    }

    /**
     *获取用户属性 async
     * @param attributes     客户端自定义属性对象(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getAttributes(List<Map<String, String>> attributes, int timeout) {
        Quest quest = new Quest("getattrs");
        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantListHashMap(answer,"attrs", attributes);
        return code;
    }

    //===========================[ Add Debug Log ]=========================//
    public boolean addDebugLog(final UserInterface.ErrorCodeCallback callback, String message, String attrs, int timeout) {
        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);

        return sendQuest(callback, quest, timeout);
    }

    public boolean addDebugLog(final UserInterface.ErrorCodeCallback callback, String message, String attrs) {
        return addDebugLog(callback, message, attrs, 0);
    }

    public int addDebugLog(String message, String attrs, int timeout) {
        Quest quest = new Quest("adddebuglog");
        quest.param("msg", message);
        quest.param("attrs", attrs);
        return sendQuestCode(quest, timeout);
    }

    public int addDebugLog(String message, String attrs) {
        return addDebugLog(message, attrs, 0);
    }

    //===========================[ Add Device ]=========================//

    /**
     * 添加设备，应用信息 async
     * @param  callback  ErrorCodeCallback回调
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean addDevice(final UserInterface.ErrorCodeCallback callback, String appType, String deviceToken, int timeout) {
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        return sendQuest(callback, quest, timeout);
    }
    public boolean addDevice(final UserInterface.ErrorCodeCallback callback, String appType, String deviceToken) {
        return addDevice(callback, appType, deviceToken, 0);
    }

    public int addDevice(String appType, String deviceToken) {
        return addDevice(appType, deviceToken, 0);
    }

    /**
     * 添加设备，应用信息 async
     * @param appType     应用类型(NoNull)
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int addDevice(String appType, String deviceToken, int timeout) {
        Quest quest = new Quest("adddevice");
        quest.param("apptype", appType);
        quest.param("devicetoken", deviceToken);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Remove Device ]=========================//
    public boolean RemoveDevice(final UserInterface.ErrorCodeCallback callback, String deviceToken) {
        return RemoveDevice(callback, deviceToken, 0);
    }

    /**
     * 删除设备，应用信息 async
     * @param  callback  ErrorCodeCallback回调
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean RemoveDevice(final UserInterface.ErrorCodeCallback callback, String deviceToken, int timeout) {
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);

        return sendQuest(callback, quest, timeout);
    }

    public int RemoveDevice(String deviceToken) {
        return RemoveDevice(deviceToken, 0);
    }

    /**
     * 删除设备，应用信息 async
     * @param deviceToken   设备token(NoNull)
     * @param timeout   超时时间(秒)
     * @return          errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int RemoveDevice(String deviceToken, int timeout) {
        Quest quest = new Quest("removedevice");
        quest.param("devicetoken", deviceToken);
        return sendQuestCode(quest, timeout);
    }
}
