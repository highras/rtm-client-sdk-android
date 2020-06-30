package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.ErrorCodeCallback;
import com.rtmsdk.UserInterface.MembersCallback;

import java.util.HashSet;

public class RTMFriend extends RTMGroup {
    //===========================[ Add Friends ]=========================//
    public boolean addFriends(ErrorCodeCallback callback, HashSet<Long> uids) {
        return addFriends(callback, uids, 0);
    }

    /**
     * 添加好友 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean addFriends(ErrorCodeCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addfriends");
        quest.param("friends", uids);

        return sendQuest(callback, quest, timeout);
    }

    public int addFriends(HashSet<Long> uids) {
        return addFriends(uids, 0);
    }

    /**
     * 添加好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int addFriends(HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addfriends");
        quest.param("friends", uids);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Delete Friends ]=========================//
    public boolean deleteFriends(ErrorCodeCallback callback, HashSet<Long> uids) {
        return deleteFriends(callback, uids, 0);
    }

    /**
     * 删除好友 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean deleteFriends(ErrorCodeCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delfriends");
        quest.param("friends", uids);

        return sendQuest(callback, quest, timeout);
    }

    public int deleteFriends(HashSet<Long> uids) {
        return deleteFriends(uids, 0);
    }

    /**
     * 删除好友 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int deleteFriends(HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delfriends");
        quest.param("friends", uids);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Get Friends ]=========================//
    public boolean getFriends(final MembersCallback callback) {
        return getFriends(callback, 0);
    }

    /**
     * 查询自己好友 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getFriends(final MembersCallback callback, int timeout) {
        Quest quest = new Quest("getfriends");

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> uids = new HashSet<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    RTMUtils.wantLongHashSet(answer,"uids", uids);
                callback.call(uids, errorCode);
            }
        }, timeout);
    }

    public int getFriends(HashSet<Long> friends) {
        return getFriends(friends, 0);
    }

    /**
     * 查询自己好友 sync
     * @param friends   好友id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int getFriends(HashSet<Long> friends, int timeout) {
        Quest quest = new Quest("getfriends");

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantLongHashSet(answer,"uids", friends);
        return code;
    }


    //===========================[ Add BlackList ]=========================//
    public boolean addBlacklist(ErrorCodeCallback callback, HashSet<Long> uids) {
        return addBlacklist(callback, uids, 0);
    }

    /**
     * 添加黑名单 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean addBlacklist(ErrorCodeCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addblacks");
        quest.param("blacks", uids);

        return sendQuest(callback, quest, timeout);
    }

    public int addBlacklist(HashSet<Long> uids) {
        return addBlacklist(uids, 0);
    }

    /**
     * 添加黑名单 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int addBlacklist(HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addblacks");
        quest.param("blacks", uids);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Delete Blacklist ]=========================//
    public boolean delBlacklist(ErrorCodeCallback callback, HashSet<Long> uids) {
        return delBlacklist(callback, uids, 0);
    }

    /**
     * 删除黑名单用户 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean delBlacklist(ErrorCodeCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delblacks");
        quest.param("blacks", uids);

        return sendQuest(callback, quest, timeout);
    }

    public int delBlacklist(HashSet<Long> uids) {
        return delBlacklist(uids, 0);
    }

    /**
     * 删除黑名单用户 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int delBlacklist(HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delblacks");
        quest.param("blacks", uids);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Get Blacklist ]=========================//
    public boolean getBlacklist(final MembersCallback callback) {
        return getBlacklist(callback, 0);
    }

    /**
     * 查询黑名单 async
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getBlacklist(final MembersCallback callback, int timeout) {
        Quest quest = new Quest("getblacks");

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> uids = new HashSet<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    RTMUtils.wantLongHashSet(answer,"uids", uids);
                callback.call(uids, errorCode);
            }
        }, timeout);
    }

    public int getBlacklist(HashSet<Long> uids) {
        return getBlacklist(uids, 0);
    }

    /**
     * 查询黑名单 sync
     * @param uids   用户id集合(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int getBlacklist(HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("getblacks");

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantLongHashSet(answer,"uids", uids);
        return code;
    }
}
