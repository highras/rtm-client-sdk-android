package com.rtmsdk;

import androidx.annotation.NonNull;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.RTMStruct.*;

import java.util.HashSet;

public class RTMFriend extends RTMGroup {
    /**
     * 添加好友 async
     * @param callback IRTMEmptyCallback回调
     * @param uids   用户id集合
     */
    public void addFriends(@NonNull IRTMEmptyCallback callback, @NonNull HashSet<Long> uids) {
        Quest quest = new Quest("addfriends");
        quest.param("friends", uids);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 添加好友 sync
     * @param uids   用户id集合
     */
    public RTMAnswer addFriends(@NonNull HashSet<Long> uids){
        Quest quest = new Quest("addfriends");
        quest.param("friends", uids);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 删除好友 async
     * @param callback IRTMEmptyCallback回调
     * @param uids   用户id集合
     */
    public void deleteFriends(@NonNull IRTMEmptyCallback callback, @NonNull HashSet<Long> uids) {
        Quest quest = new Quest("delfriends");
        quest.param("friends", uids);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 删除好友 sync
     * @param uids   用户id集合
     */
    public RTMAnswer deleteFriends(@NonNull HashSet<Long> uids){
        Quest quest = new Quest("delfriends");
        quest.param("friends", uids);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 查询自己好友 async
     * @param callback MembersCallback回调
     */
    public void getFriends(@NonNull final IRTMCallback<HashSet<Long>> callback) {
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
        });
    }

    /**
     * 查询自己好友 sync
     * @return 好友id集合
     */
    public MembersStruct getFriends(){
        Quest quest = new Quest("getfriends");

        Answer answer = sendQuest(quest);
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
     * @param callback IRTMEmptyCallback回调
     * @param uids   用户id集合
     */
    public void addBlacklist(@NonNull IRTMEmptyCallback callback, @NonNull HashSet<Long> uids) {
        Quest quest = new Quest("addblacks");
        quest.param("blacks", uids);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 添加黑名单 sync
     * @param uids   用户id集合
     */
    public RTMAnswer addBlacklist(@NonNull HashSet<Long> uids){
        Quest quest = new Quest("addblacks");
        quest.param("blacks", uids);

       return sendQuestEmptyResult(quest);
    }

    /**
     * 删除黑名单用户 async
     * @param callback IRTMEmptyCallback回调
     * @param uids   用户id集合
     */
    public void delBlacklist(@NonNull IRTMEmptyCallback callback, @NonNull HashSet<Long> uids) {
        Quest quest = new Quest("delblacks");
        quest.param("blacks", uids);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 删除黑名单用户 sync
     * @param uids   用户id集合
     */
    public RTMAnswer delBlacklist(@NonNull HashSet<Long> uids){
        Quest quest = new Quest("delblacks");
        quest.param("blacks", uids);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 查询黑名单 async
     * @param callback MembersCallback回调
     */
    public void getBlacklist(@NonNull final IRTMCallback<HashSet<Long>> callback) {
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
        });
    }

    /**
     * 查询黑名单 sync
     * @return 黑名单id集合
     */
    public MembersStruct getBlacklist(){
        Quest quest = new Quest("getblacks");

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        MembersStruct ret = new MembersStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.uids = RTMUtils.wantLongHashSet(answer,"uids");

        return ret;
    }
}
