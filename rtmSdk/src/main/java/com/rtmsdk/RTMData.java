package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;
import com.rtmsdk.RTMStruct.*;

class RTMData extends RTMessage {

    //重载
    public void dataGet(IRTMCallback<String> callback, String key) {
        dataGet(callback, key,0);
    }

    public DataInfo dataGet(String key){
        return dataGet( key, 0);
    }

    public void dataSet(IRTMEmptyCallback callback, String key, String value) {
        dataSet(callback, key, value, 0);
    }

    public RTMAnswer dataSet(String key, String value) {
        return dataSet(key, value, 0);
    }

    public void dataDelete(String key, final IRTMEmptyCallback callback) {
        dataDelete(key, callback, 0);
    }

    public RTMAnswer dataDelete(String key){
        return dataDelete(key, 0);
    }
    //重载end


    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param callback  获取value回调
     * @param timeout   超时时间(秒)
     */
    public void dataGet(final IRTMCallback<String> callback, String key, int timeout) {
        Quest quest = new Quest("dataget");
        quest.param("key", key);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String value = "";
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    value = answer.getString("val");
                callback.onResult(value, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }


    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) sync
     * @param key      key值
     * @param timeout   超时时间(秒)
     * @return    存储的数据信息
     */
    public DataInfo dataGet(String key, int timeout) {
        Quest quest = new Quest("dataget");
        quest.param("key", key);

        Answer answer = sendQuest(quest, timeout);
        DataInfo result = new DataInfo();
        RTMAnswer ret = genRTMAnswer(answer);
        result.errorCode = ret.errorCode;
        result.errorMsg = ret.errorMsg;
        if (answer != null)
            result.info = answer.getString("val");
        return  result;
    }

    /**
     * 设置存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param callback  IRTMEmptyCallback接口回调
     * @param timeout   超时时间(秒)
     */
    public void dataSet(final IRTMEmptyCallback callback,String key, String value, int timeout) {
        Quest quest = new Quest("dataset");
        quest.param("key", key);
        quest.param("val", value);
        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 设置存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param value     设置的value值
     * @param timeout   超时时间(秒)
     *  return          RTMAnswer
     */
    public RTMStruct.RTMAnswer dataSet(String key, String value, int timeout) {
        Quest quest = new Quest("dataset");
        quest.param("key", key);
        quest.param("val", value);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @param callback  IRTMEmptyCallback接口回调
     * @param timeout   超时时间(秒)
     */
    public void dataDelete(String key, final IRTMEmptyCallback callback, int timeout) {
        Quest quest = new Quest("datadel");
        quest.param("key", key);
        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @param timeout   超时时间(秒)
     * @return    RTMAnswer
     */
    public RTMStruct.RTMAnswer dataDelete(String key, int timeout){
        Quest quest = new Quest("datadel");
        quest.param("key", key);
        return sendQuestEmptyResult(quest, timeout);
    }
}

