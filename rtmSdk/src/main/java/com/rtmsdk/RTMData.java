package com.rtmsdk;

import androidx.annotation.NonNull;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;
import com.rtmsdk.RTMStruct.*;

class RTMData extends RTMessage {
    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param callback  获取value回调
     */
    public void dataGet(@NonNull final IRTMCallback<String> callback, @NonNull String key) {
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
        });
    }


    /**
     * 获取存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) sync
     * @param key      key值
     * @return    存储的数据信息
     */
    public DataInfo dataGet(@NonNull String key) {
        Quest quest = new Quest("dataget");
        quest.param("key", key);

        Answer answer = sendQuest(quest);
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
     */
    public void dataSet(@NonNull final IRTMEmptyCallback callback,@NonNull String key, @NonNull String value) {
        Quest quest = new Quest("dataset");
        quest.param("key", key);
        quest.param("val", value);
        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 设置存储的数据信息（仅能操作自己信息）(key:最长128字节，val：最长65535字节) async
     * @param key      key值
     * @param value     设置的value值
     *  return          RTMAnswer
     */
    public RTMStruct.RTMAnswer dataSet(@NonNull String key, @NonNull String value) {
        Quest quest = new Quest("dataset");
        quest.param("key", key);
        quest.param("val", value);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @param callback  IRTMEmptyCallback接口回调
     */
    public void dataDelete(@NonNull String key, @NonNull final IRTMEmptyCallback callback) {
        Quest quest = new Quest("datadel");
        quest.param("key", key);
        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 删除存储的数据信息 async
     * @param key      key值
     * @return    RTMAnswer
     */
    public RTMStruct.RTMAnswer dataDelete(@NonNull String key){
        Quest quest = new Quest("datadel");
        quest.param("key", key);
        return sendQuestEmptyResult(quest);
    }
}

