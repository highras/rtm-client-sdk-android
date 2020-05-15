package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.ErrorCodeCallback;
import com.rtmsdk.UserInterface.MessageCallback;

class RTMData extends RTMMessage {
    //===========================[ Data Get ]=========================//
    //-- Action<value, errorCode>
    public boolean dataGet(String key, final MessageCallback callback) {
        return dataGet(key, callback, 0);
    }

    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param callback  获取value回调
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean dataGet(String key, final MessageCallback callback, int timeout) {
        Quest quest = new Quest("dataget");
        quest.param("key", key);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String value = "";
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    value = (String) answer.get("val", "");
                }
                callback.call(value, errorCode);
            }
        }, timeout);
    }

    public int dataGet(StringBuilder value, String key) {
        return dataGet(value, key, 0);
    }

    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) sync
     * @param key      key值
     * @param value  获取value值
     * @param timeout   超时时间(秒)
     * @return    errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int dataGet(StringBuilder value, String key, int timeout) {
        Quest quest = new Quest("dataget");
        quest.param("key", key);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            String ret = (String) answer.get("val", "");
            value.append(ret);
        }
        return code;
    }

    //===========================[ Data Set ]=========================//
    public boolean dataSet(String key, String value, final ErrorCodeCallback callbackt) {
        return dataSet(key, value, callbackt, 0);
    }

    /**
     * 设置存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param callback  ErrorCodeCallback接口回调
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean dataSet(String key, String value, final ErrorCodeCallback callback, int timeout) {
        Quest quest = new Quest("dataSet");
        quest.param("key", key);
        quest.param("val", value);
        return sendQuest(callback, quest, timeout);
    }

    public int dataSet(String key, String value) {
        return dataSet(key, value, 0);
    }

    /**
     * 设置存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param value     设置的value值
     * @param timeout   超时时间(秒)
     * @return    errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int dataSet(String key, String value, int timeout) {
        Quest quest = new Quest("dataset");
        quest.param("key", key);
        quest.param("val", value);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Data Delete ]=========================//
    public boolean dataDelete(String key, final ErrorCodeCallback callback) {
        return dataDelete(key, callback, 0);
    }

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @param callback  ErrorCodeCallback接口回调
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean dataDelete(String key, final ErrorCodeCallback callback, int timeout) {
        Quest quest = new Quest("datadel");
        quest.param("key", key);
        return sendQuest(callback, quest, timeout);
    }

    public int dataDelete(String key) {
        return dataDelete(key, 0);
    }

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @param timeout   超时时间(秒)
     * @return    errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int dataDelete(String key, int timeout) {
        Quest quest = new Quest("datadel");
        quest.param("key", key);
        return sendQuestCode(quest, timeout);
    }
}

