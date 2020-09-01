package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.RTMStruct.*;

import java.util.HashSet;

class RTMGroup extends RTMFile {
    //重载
    public void addGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids) {
        addGroupMembers(callback, groupId, uids, 0);
    }

    public RTMAnswer addGroupMembers(long groupId, HashSet<Long> uids){
        return addGroupMembers(groupId, uids, 0);
    }
    
    public void deleteGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids) {
        deleteGroupMembers(callback, groupId, uids, 0);
    }

    public RTMAnswer deleteGroupMembers(long groupId, HashSet<Long> uids){
        return deleteGroupMembers(groupId, uids, 0);
    }

    public void getGroupMembers(IRTMCallback<HashSet<Long>> callback, long groupId) {
        getGroupMembers(callback, groupId, 0);
    }

    public MembersStruct getGroupMembers(long groupId){
        return getGroupMembers(groupId, 0);
    }

    public void getUserGroups(IRTMCallback<HashSet<Long>> callback) {
        getUserGroups(callback, 0);
    }

    public MembersStruct getUserGroups(){
        return getUserGroups(0);
    }

    public void setGroupInfo(IRTMEmptyCallback callback, long groupId, String publicInfo, String privateInfo) {
        setGroupInfo(callback, groupId, publicInfo, privateInfo, 0);
    }

    public RTMAnswer setGroupInfo(long groupId, String publicInfo, String privateInfo){
        return setGroupInfo(groupId, publicInfo, privateInfo, 0);
    }

    public void getGroupInfo(IRTMCallback<GroupInfoStruct> callback, long groupId) {
        getGroupInfo(callback, groupId, 0);
    }

    public GroupInfoStruct getGroupInfo(long groupId){
        return getGroupInfo(groupId,0);
    }

    public void getGroupPublicInfo(IRTMCallback<String> callback, long groupId) {
        getGroupPublicInfo(callback, groupId, 0);
    }

    public DataInfo getGroupPublicInfo(long groupId){
        return getGroupPublicInfo(groupId, 0);
    }
    //重载end
    /**
     * 添加群组用户 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void addGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 添加群组用户  sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return
     * */
    public RTMAnswer addGroupMembers(long groupId, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);
        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 删除群组用户   async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void deleteGroupMembers(final IRTMEmptyCallback callback, long groupId, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);
        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 删除群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public RTMAnswer deleteGroupMembers(long groupId, HashSet<Long> uids, int timeout){
        Quest quest = new Quest("delgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 获取群组用户   async
     * @param callback  IRTMCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void getGroupMembers(final IRTMCallback<HashSet<Long>> callback, long groupId, int timeout) {
        Quest quest = new Quest("getgroupmembers");
        quest.param("gid", groupId);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> uids = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    uids  = RTMUtils.wantLongHashSet(answer,"uids");
                callback.onResult(uids, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * reutn 用户id集合
     * */
    public MembersStruct getGroupMembers(long groupId, int timeout){
        Quest quest = new Quest("getgroupmembers");
        quest.param("gid", groupId);

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
     * 获取用户所在的群组   async
     * @param callback  IRTMCallback回调(NoNull)
     * @param timeout   超时时间（秒）
     * */
    public void getUserGroups(final IRTMCallback<HashSet<Long>> callback, int timeout) {
        Quest quest = new Quest("getusergroups");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> groupIds = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    groupIds = RTMUtils.wantLongHashSet(answer,"gids");
                callback.onResult(groupIds, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取用户所在的群组   sync
     * @param timeout   超时时间（秒）
     * @return  用户所在群组集合
     * */
    public MembersStruct getUserGroups(int timeout){
        Quest quest = new Quest("getusergroups");

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        MembersStruct ret = new MembersStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.uids = RTMUtils.wantLongHashSet(answer,"gids");

        return ret;
    }

    /**
     * 设置群组的公开信息或者私有信息 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）

     */
    public void setGroupInfo(IRTMEmptyCallback callback, long groupId, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setgroupinfo");
        quest.param("gid", groupId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 设置群组的公开信息或者私有信息 sync
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）
     */
    public RTMAnswer setGroupInfo(long groupId, String publicInfo, String privateInfo, int timeout){
        Quest quest = new Quest("setgroupinfo");
        quest.param("gid", groupId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 获取群组的公开信息或者私有信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getGroupInfo(final IRTMCallback<GroupInfoStruct> callback, final long groupId, int timeout) {
        Quest quest = new Quest("getgroupinfo");
        quest.param("gid", groupId);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                GroupInfoStruct groupInfo = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    groupInfo = new GroupInfoStruct();
                    groupInfo.publicInfo = answer.wantString("oinfo");
                    groupInfo.privateInfo = answer.wantString("pinfo");
                }
                callback.onResult(groupInfo, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取群组的公开信息或者私有信息 sync
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getGroupInfo(long groupId, int timeout){
        Quest quest = new Quest("getgroupinfo");
        quest.param("gid", groupId);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        GroupInfoStruct ret = new GroupInfoStruct();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            ret.publicInfo = answer.wantString("oinfo");
            ret.privateInfo = answer.wantString("pinfo");
        }
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        return ret;
    }


    /**
     * 获取群组的公开信息 async
     * @param callback  MessageCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getGroupPublicInfo(final IRTMCallback<String>  callback, long groupId, int timeout) {
        Quest quest = new Quest("getgroupopeninfo");
        quest.param("gid", groupId);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String publicInfo = "";
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    publicInfo = answer.wantString("oinfo");

                callback.onResult(publicInfo, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取群组的公开信息 sync
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  群组公开信息
     */
    public DataInfo getGroupPublicInfo(long groupId, int timeout){
        Quest quest = new Quest("getgroupopeninfo");
        quest.param("gid", groupId);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        DataInfo ret = new DataInfo();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.info = answer.wantString("oinfo");
        return ret;
    }
}
