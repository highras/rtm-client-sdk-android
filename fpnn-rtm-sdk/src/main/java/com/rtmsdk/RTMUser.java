package com.rtmsdk;

import androidx.annotation.NonNull;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.GroupInfoStruct;
import com.rtmsdk.RTMStruct.MembersStruct;
import com.rtmsdk.RTMStruct.RTMAnswer;
import com.rtmsdk.RTMStruct.PublicInfo;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RTMUser extends RTMData {
    /**
     * 查询用户是否在线   async
     * @param callback IRTMCallback回调
     * @param uids     待查询的用户id集合
     */
    public void getOnlineUsers(@NonNull final IRTMCallback<HashSet<Long>> callback, @NonNull HashSet<Long> uids) {
        Quest quest = new Quest("getonlineusers");
        quest.param("uids", uids);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> onlineUids = new HashSet<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    onlineUids = rtmUtils.wantLongHashSet(answer, "uids");
                }
                callback.onResult(onlineUids, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     * 查询用户是否在线   async
     *return 用户id列表
     */
    public MembersStruct getOnlineUsers(@NonNull HashSet<Long> checkUids) {
        Quest quest = new Quest("getonlineusers");
        quest.param("uids", checkUids);

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        MembersStruct ret = new MembersStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.uids = rtmUtils.wantLongHashSet(answer,"uids");

        return ret;
    }

    /**
     * 设置用户自己的公开信息或者私有信息(publicInfo,privateInfo 最长 65535) async
     * @param callback    IRTMEmptyCallback回调
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     */
    public void setUserInfo(@NonNull IRTMEmptyCallback callback, String publicInfo, String privateInfo) {
        Quest quest = new Quest("setuserinfo");
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        sendQuestEmptyCallback(callback,quest);
    }

    /**
     * 设置用户自己的公开信息或者私有信息 sync
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     */
    public RTMAnswer setUserInfo(String publicInfo, String privateInfo) {
        Quest quest = new Quest("setuserinfo");
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 获取的用户公开信息或者私有信息 async
     * @param callback IRTMCallback<GroupInfoStruct>回调
     */
    public void getUserInfo(@NonNull final IRTMCallback<GroupInfoStruct> callback) {
        Quest quest = new Quest("getuserinfo");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                GroupInfoStruct userInfo = new GroupInfoStruct();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    userInfo.publicInfo = rtmUtils.wantString(answer,"oinfo");
                    userInfo.privateInfo = rtmUtils.wantString(answer,"pinfo");
                }
                callback.onResult(userInfo, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     * 获取公开信息或者私有信息 sync
     * @return  GroupInfoStruct用户信息结构
     */
    public GroupInfoStruct getUserInfo() {
        Quest quest = new Quest("getuserinfo");

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        GroupInfoStruct userInfo = new GroupInfoStruct();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            userInfo.publicInfo = rtmUtils.wantString(answer,"oinfo");
            userInfo.privateInfo = rtmUtils.wantString(answer,"pinfo");
        }
        userInfo.errorCode = result.errorCode;
        userInfo.errorMsg = result.errorMsg;
        return userInfo;
    }

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback UserAttrsCallback回调
     * @param uids     用户uid集合
     */
    public void getUserPublicInfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> uids) {
        Quest quest = new Quest("getuseropeninfo");
        quest.param("uids",uids);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                Map<String, String> attributes = new HashMap<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    attributes = rtmUtils.wantStringMap(answer, "info");
                }
                callback.onResult(attributes, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param uids        用户uid集合
     *return 返回用户id 公开信息map 用户id会被转变成string返回
     */
    public PublicInfo getUserPublicInfo(HashSet<Long> uids) {
        Quest quest = new Quest("getuseropeninfo");
        quest.param("uids", uids);

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        PublicInfo ret = new PublicInfo();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.publicInfos = rtmUtils.wantStringMap(answer, "info");

        return ret;
    }
}
