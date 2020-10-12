package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.GroupInfoStruct;
import com.rtmsdk.RTMStruct.MembersStruct;
import com.rtmsdk.RTMStruct.RTMAnswer;
import com.rtmsdk.RTMStruct.UserPublicInfo;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

import java.util.HashSet;
import java.util.Map;

public class RTMUser extends RTMData {

    //重载
    public void getOnlineUsers(IRTMCallback<HashSet<Long>> callback, HashSet<Long> uids) {
        getOnlineUsers(callback, uids, 0);
    }

    public MembersStruct getOnlineUsers(HashSet<Long> checkUids) {
        return getOnlineUsers(checkUids,0);
    }

    public void setUserInfo(IRTMEmptyCallback callback, String publicInfo, String privateInfo) {
        setUserInfo(callback, publicInfo, privateInfo, 0);
    }

    public RTMAnswer setUserInfo(String publicInfo, String privateInfo) {
        return setUserInfo(publicInfo, privateInfo, 0);
    }

    public void getUserInfo(IRTMCallback<GroupInfoStruct> callback) {
        getUserInfo(callback, 0);
    }

    public GroupInfoStruct getUserInfo() {
        return getUserInfo(0);
    }

    public void getUserPublicInfo(IRTMCallback<Map<String, String>>  callback, HashSet<Long> uids) {
        getUserPublicInfo(callback, uids, 0);
    }

    public UserPublicInfo getUserPublicInfo(HashSet<Long> uids) {
        return getUserPublicInfo(uids, 0);
    }

    //重载end

    /**
     * 查询用户是否在线   async
     * @param callback IRTMCallback回调(NoNull)
     * @param uids     待查询的用户id集合(NoNull)
     * @param timeout  超时时间（秒）
     */
    public void getOnlineUsers(final IRTMCallback<HashSet<Long>> callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("getonlineusers");
        quest.param("uids", uids);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> onlineUids = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    onlineUids = RTMUtils.wantLongHashSet(answer, "uids");
                }
                callback.onResult(onlineUids, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 查询用户是否在线   async
     * @param timeout    超时时间（秒）
     *return 用户id列表
     */
    public MembersStruct getOnlineUsers(HashSet<Long> checkUids, int timeout) {
        Quest quest = new Quest("getonlineusers");
        quest.param("uids", checkUids);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        MembersStruct ret = new MembersStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.uids = RTMUtils.wantLongHashSet(answer,"uids");

        return ret;
    }

    /**
     * 设置用户自己的公开信息或者私有信息(publicInfo,privateInfo 最长 65535) async
     * @param callback    IRTMEmptyCallback回调(NoNull)
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     */
    public void setUserInfo(IRTMEmptyCallback callback, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setuserinfo");
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        sendQuestEmptyCallback(callback,quest,timeout);
    }

    /**
     * 设置用户自己的公开信息或者私有信息 sync
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     */
    public RTMAnswer setUserInfo(String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setuserinfo");
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestEmptyResult(quest,timeout);
    }

    /**
     * 获取的用户公开信息或者私有信息 async
     * @param callback IRTMCallback<GroupInfoStruct>回调(NoNull)
     * @param timeout  超时时间（秒）

     */
    public void getUserInfo(final IRTMCallback<GroupInfoStruct> callback, int timeout) {
        Quest quest = new Quest("getuserinfo");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                GroupInfoStruct userInfo = new GroupInfoStruct();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    userInfo.publicInfo = answer.wantString("oinfo");
                    userInfo.privateInfo = answer.wantString("pinfo");
                }
                callback.onResult(userInfo, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取公开信息或者私有信息 sync
     * @param timeout     超时时间（秒）
     * @return  GroupInfoStruct用户信息结构
     */
    public GroupInfoStruct getUserInfo(int timeout) {
        Quest quest = new Quest("getuserinfo");

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        GroupInfoStruct userInfo = new GroupInfoStruct();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            userInfo.publicInfo = answer.wantString("oinfo");
            userInfo.privateInfo = answer.wantString("pinfo");
        }
        userInfo.errorCode = result.errorCode;
        userInfo.errorMsg = result.errorMsg;
        return userInfo;
    }

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback UserAttrsCallback回调(NoNull)
     * @param uids     用户uid集合
     * @param timeout  超时时间(秒)
     */
    public void getUserPublicInfo(final IRTMCallback<Map<String, String>> callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("getuseropeninfo");
        quest.param("uids",uids);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                Map<String, String> attributes = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    attributes = RTMUtils.wantStringMap(answer, "info");
                }
                callback.onResult(attributes, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param uids        用户uid集合
     * @param timeout     超时时间(秒)
     *return 返回用户id 公开信息map(NoNull) 用户id会被转变成string返回
     */
    public UserPublicInfo getUserPublicInfo(HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("getuseropeninfo");
        quest.param("uids", uids);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        UserPublicInfo ret = new UserPublicInfo();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.userInfo = RTMUtils.wantStringMap(answer, "uids");

        return ret;
    }
}
