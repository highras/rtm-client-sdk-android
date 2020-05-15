package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.DoubleStringCallback;
import com.rtmsdk.UserInterface.ErrorCodeCallback;
import com.rtmsdk.UserInterface.MembersCallback;
import com.rtmsdk.UserInterface.MessageCallback;

import java.util.HashSet;

class RTMGroup extends RTMFile {
    //===========================[ Add Group Members ]=========================//
    public boolean addGroupMembers(final ErrorCodeCallback callback, long groupId, HashSet<Long> uids) {
        return addGroupMembers(callback, groupId, uids, 0);
    }

    /**
     * 添加群组用户 async
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean addGroupMembers(final ErrorCodeCallback callback, long groupId, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        return sendQuest(callback, quest, timeout);
    }

    public int addGroupMembers(long groupId, HashSet<Long> uids) {
        return addGroupMembers(groupId, uids, 0);
    }

    /**
     * 添加群组用户  sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int addGroupMembers(long groupId, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("addgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);
        return sendQuestCode(quest, timeout);
    }

    //===========================[ delete Group Members ]=========================//
    public boolean deleteGroupMembers(final ErrorCodeCallback callback, long groupId, HashSet<Long> uids) {
        return deleteGroupMembers(callback, groupId, uids, 0);
    }

    /**
     * 删除群组用户   async
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean deleteGroupMembers(final ErrorCodeCallback callback, long groupId, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);
        return sendQuest(callback, quest, timeout);
    }

    public int deleteGroupMembers(long groupId, HashSet<Long> uids) {
        return deleteGroupMembers(groupId, uids, 0);
    }

    /**
     * 删除群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int deleteGroupMembers(long groupId, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("delgroupmembers");
        quest.param("gid", groupId);
        quest.param("uids", uids);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ get Group Members ]=========================//
    //-- Action<uids, errorCode>
    public boolean getGroupMembers(final MembersCallback callback, long groupId) {
        return getGroupMembers(callback, groupId, 0);
    }

    /**
     * 获取群组用户   async
     * @param callback  MembersCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean getGroupMembers(final MembersCallback callback, long groupId, int timeout) {
        Quest quest = new Quest("getgroupmembers");
        quest.param("gid", groupId);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> uids = new HashSet<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    RTMUtils.wantLongHashSet(answer,"uids", uids);
                }
                callback.call(uids, errorCode);
            }
        }, timeout);
    }

    public int getGroupMembers(HashSet<Long> uids, long groupId) {
        return getGroupMembers(uids, groupId, 0);
    }

    /**
     * 删除群组用户   sync
     * @param groupId   群组id(NoNull)
     * @param uids      用户id集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int getGroupMembers(HashSet<Long> uids, long groupId, int timeout) {
        Quest quest = new Quest("getgroupmembers");
        quest.param("gid", groupId);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantLongHashSet(answer,"uids", uids);
        return code;
    }

    //===========================[ get User Groups ]=========================//
    //-- Action<groupIds, errorCode>
    public boolean getUserGroups(final MembersCallback callback) {
        return getUserGroups(callback, 0);
    }

    /**
     * 获取用户所在的群组   async
     * @param callback  MembersCallback回调(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     * */
    public boolean getUserGroups(final MembersCallback callback, int timeout) {
        Quest quest = new Quest("getusergroups");

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> groupIds = new HashSet<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    RTMUtils.wantLongHashSet(answer,"gids", groupIds);
                callback.call(groupIds, errorCode);
            }
        }, timeout);
    }

    public int getUserGroups(HashSet<Long> groupIds) {
        return getUserGroups(groupIds, 0);
    }

    /**
     * 获取用户所在的群组   sync
     * @param groupIds  群组集合(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     * */
    public int getUserGroups(HashSet<Long> groupIds, int timeout) {
        Quest quest = new Quest("getusergroups");

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantLongHashSet(answer,"gids", groupIds);
        return code;
    }

    //===========================[ Set Group Info ]=========================//
    public boolean setGroupInfo(ErrorCodeCallback callback, long groupId) {
        return setGroupInfo(callback, groupId, null, null, 0);
    }

    public boolean setGroupInfo(ErrorCodeCallback callback, long groupId, String publicInfo, String privateInfo) {
        return setGroupInfo(callback, groupId, publicInfo, privateInfo, 0);
    }

    public boolean setGroupInfo(ErrorCodeCallback callback, long groupId, String publicInfo) {
        return setGroupInfo(callback, groupId, publicInfo, null, 0);
    }

    /**
     * 设置群组的公开信息或者私有信息 async
     * @param callback  ErrorCodeCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean setGroupInfo(ErrorCodeCallback callback, long groupId, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setgroupinfo");
        quest.param("gid", groupId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuest(callback, quest, timeout);
    }

    public int setGroupInfo(long groupId) {
        return setGroupInfo(groupId, null, null, 0);
    }

    public int setGroupInfo(long groupId, String publicInfo) {
        return setGroupInfo(groupId, publicInfo, null, 0);
    }

    public int setGroupInfo(long groupId, String publicInfo, String privateInfo) {
        return setGroupInfo(groupId, publicInfo, privateInfo, 0);
    }

    /**
     * 设置群组的公开信息或者私有信息 sync
     * @param groupId   群组id(NoNull)
     * @param publicInfo    群组公开信息
     * @param privateInfo   群组 私有信息
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int setGroupInfo(long groupId, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setgroupinfo");
        quest.param("gid", groupId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ get Group Info ]=========================//
    //-- Action<publicInfo, privateInfo, errorCode>
    public boolean getGroupInfo(final DoubleStringCallback callback, long groupId) {
        return getGroupInfo(callback, groupId, 0);
    }

    /**
     * 获取群组的公开信息或者私有信息 async
     * @param callback  DoubleStringCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getGroupInfo(final DoubleStringCallback callback, long groupId, int timeout) {
        Quest quest = new Quest("getgroupinfo");
        quest.param("gid", groupId);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String publicInfo = "", privateInfo = "";
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    publicInfo = answer.wantString("oinfo");
                    privateInfo = answer.wantString("pinfo");
                }
                callback.call(publicInfo, privateInfo, errorCode);
            }
        }, timeout);
    }

    public int getGroupInfo(StringBuilder publicInfo, StringBuilder privateInfo, long groupId) {
        return getGroupInfo(publicInfo, privateInfo, groupId,0);
    }

    /**
     * 获取群组的公开信息或者私有信息 sync
     * @param publicInfo    群组公开信息
     * @param privateInfo    群组私有信息
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getGroupInfo(StringBuilder publicInfo, StringBuilder privateInfo, long groupId, int timeout) {
        Quest quest = new Quest("getgroupinfo");
        quest.param("gid", groupId);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            publicInfo.append(answer.wantString("oinfo"));
            privateInfo.append(answer.wantString("pinfo"));
        }
        return code;
    }

    //===========================[ get Group Open Info ]=========================//
    //-- Action<public_info, errorCode>
    public boolean getGroupPublicInfo(final MessageCallback callback, long groupId) {
        return getGroupPublicInfo(callback, groupId, 0);
    }

    /**
     * 获取群组的公开信息 async
     * @param callback  MessageCallback回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getGroupPublicInfo(final MessageCallback callback, long groupId, int timeout) {
        Quest quest = new Quest("getgroupopeninfo");
        quest.param("gid", groupId);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String publicInfo = "";
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    publicInfo = answer.wantString("oinfo");

                callback.call(publicInfo, errorCode);
            }
        }, timeout);
    }

    public int getGroupPublicInfo(StringBuilder publicInfo, long groupId) {
        return getGroupPublicInfo(publicInfo, groupId, 0);
    }

    /**
     * 获取群组的公开信息 sync
     * @param publicInfo    群组公开信息
     * @param groupId   群组id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getGroupPublicInfo(StringBuilder publicInfo, long groupId, int timeout) {
        Quest quest = new Quest("getgroupopeninfo");
        quest.param("gid", groupId);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            publicInfo.append(answer.wantString("oinfo"));
        return code;
    }
}
