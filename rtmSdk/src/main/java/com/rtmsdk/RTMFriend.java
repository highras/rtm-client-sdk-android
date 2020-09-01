package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.RTMStruct.*;

import java.util.HashSet;

public class RTMFriend extends RTMGroup {

    //重载
    public void addFriends(IRTMEmptyCallback callback, HashSet<Long> uids) {
        addFriends(callback, uids, 0);
    }

    public RTMAnswer addFriends(HashSet<Long> uids) {
        return addFriends(uids, 0);
    }

    public void deleteFriends(IRTMEmptyCallback callback, HashSet<Long> uids) {
        deleteFriends(callback, uids, 0);
    }

    public RTMAnswer deleteFriends(HashSet<Long> uids){
        return deleteFriends(uids, 0);
    }

    public void getFriends(IRTMCallback<HashSet<Long>> callback) {
        getFriends(callback, 0);
    }

    public MembersStruct getFriends(){
        return getFriends(0);
    }

    public void addBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids) {
        addBlacklist(callback, uids, 0);
    }

    public RTMAnswer addBlacklist(HashSet<Long> uids){
        return addBlacklist(uids, 0);
    }

    public void delBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids) {
        delBlacklist(callback, uids, 0);
    }

    public RTMAnswer delBlacklist(HashSet<Long> uids){
        return delBlacklist(uids, 0);
    }

    public void getBlacklist(IRTMCallback<HashSet<Long>> callback) {
        getBlacklist(callback, 0);
    }

    public MembersStruct  getBlacklist(){
        return getBlacklist(0);
    }
    //重载end

    /**
     * 添加好友 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void addFriends(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addfriends");
        quest.param("friends", uids);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 添加好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer addFriends(HashSet<Long> uids, int timeout){
        Quest quest = new Quest("addfriends");
        quest.param("friends", uids);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 删除好友 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void deleteFriends(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delfriends");
        quest.param("friends", uids);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 删除好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer deleteFriends(HashSet<Long> uids, int timeout){
        Quest quest = new Quest("delfriends");
        quest.param("friends", uids);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 查询自己好友 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void getFriends(final IRTMCallback<HashSet<Long>> callback, int timeout) {
        Quest quest = new Quest("getfriends");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> uids = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    uids = RTMUtils.wantLongHashSet(answer, "uids");
                }
                callback.onResult(uids, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 查询自己好友 sync
     * @param timeout  超时时间(秒)
     * @return 好友id集合
     */
    public MembersStruct getFriends(int timeout){
        Quest quest = new Quest("getfriends");

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
     * 添加黑名单 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void addBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addblacks");
        quest.param("blacks", uids);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 添加黑名单 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer addBlacklist(HashSet<Long> uids, int timeout){
        Quest quest = new Quest("addblacks");
        quest.param("blacks", uids);

       return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 删除黑名单用户 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void delBlacklist(IRTMEmptyCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delblacks");
        quest.param("blacks", uids);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 删除黑名单用户 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     */
    public RTMAnswer delBlacklist(HashSet<Long> uids, int timeout){
        Quest quest = new Quest("delblacks");
        quest.param("blacks", uids);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 查询黑名单 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void getBlacklist(final IRTMCallback<HashSet<Long>> callback, int timeout) {
        Quest quest = new Quest("getblacks");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> uids = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    uids = RTMUtils.wantLongHashSet(answer, "uids");
                }
                callback.onResult(uids, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 查询黑名单 sync
     * @param timeout  超时时间(秒)
     * @return 黑名单id集合
     */
    public MembersStruct getBlacklist(int timeout){
        Quest quest = new Quest("getblacks");

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        MembersStruct ret = new MembersStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.uids = RTMUtils.wantLongHashSet(answer,"uids");

        return ret;
    }
}
