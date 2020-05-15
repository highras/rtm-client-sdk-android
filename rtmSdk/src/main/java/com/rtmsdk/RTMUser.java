package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.DoubleStringCallback;
import com.rtmsdk.UserInterface.ErrorCodeCallback;
import com.rtmsdk.UserInterface.MembersCallback;
import com.rtmsdk.UserInterface.UserAttrsCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RTMUser extends RTMData {
    //===========================[ Get Online Users ]=========================//
    public boolean getOnlineUsers(final MembersCallback callback, HashSet<Long> uids) {
        return getOnlineUsers(callback, uids, 0);
    }

    /**
     * 查询用户是否在线   async
     *
     * @param callback MembersCallback回调(NoNull)
     * @param uids     待查询的用户id集合(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getOnlineUsers(final MembersCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("getonlineusers");
        quest.param("uids", uids);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> onlineUids = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    onlineUids = new HashSet<>();
                    RTMUtils.wantLongHashSet(answer, "uids", onlineUids);
                }
                callback.call(onlineUids, errorCode);
            }
        }, timeout);
    }

    public int getOnlineUsers(HashSet<Long> onlineUids, HashSet<Long> checkUids) {
        return getOnlineUsers(onlineUids, checkUids, 0);
    }

    /**
     * 查询用户是否在线   async
     *
     * @param onlineUids 返回在线的用户id集合(NoNull)
     * @param checkUids  待查询的用户id集合(NoNull)
     * @param timeout    超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public int getOnlineUsers(HashSet<Long> onlineUids, HashSet<Long> checkUids, int timeout) {
        Quest quest = new Quest("getonlineusers");
        quest.param("uids", checkUids);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantLongHashSet(answer, "uids", onlineUids);
        return code;
    }

    //===========================[ Set User Info ]=========================//
    public boolean setUserInfo(ErrorCodeCallback callback) {
        return setUserInfo(callback, null, null, 0);
    }

    public boolean setUserInfo(ErrorCodeCallback callback, String publicInfo) {
        return setUserInfo(callback, publicInfo, null, 0);
    }

    public boolean setUserInfo(ErrorCodeCallback callback, String publicInfo, String privateInfo) {
        return setUserInfo(callback, publicInfo, privateInfo, 0);
    }

    /**
     * 设置用户自己的公开信息或者私有信息(publicInfo,privateInfo 最长 65535) async
     *
     * @param callback    ErrorCodeCallback回调(NoNull)
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean setUserInfo(ErrorCodeCallback callback, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setuserinfo");
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuest(callback, quest, timeout);
    }

    public int setUserInfo() {
        return setUserInfo(null, null, 0);
    }

    public int setUserInfo(String publicInfo) {
        return setUserInfo(publicInfo, null, 0);
    }

    public int setUserInfo(String publicInfo, String privateInfo) {
        return setUserInfo(publicInfo, privateInfo, 0);
    }

    /**
     * 设置群组的公开信息或者私有信息 sync
     *
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int setUserInfo(String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setuserinfo");
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Get User Info ]=========================//
    //-- DoubleStringCallback<publicInfo, privateInfo, errorCode>
    public boolean getUserInfo(final DoubleStringCallback callback) {
        return getUserInfo(callback, 0);
    }

    /**
     * 获取的公开信息或者私有信息 async
     *
     * @param callback DoubleStringCallback回调(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getUserInfo(final DoubleStringCallback callback, int timeout) {
        Quest quest = new Quest("getuserinfo");

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

    public int getUserInfo(StringBuilder publicInfo, StringBuilder privateInfo) {
        return getUserInfo(publicInfo, privateInfo, 0);
    }

    /**
     * 获取公开信息或者私有信息 sync
     *
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getUserInfo(StringBuilder publicInfo, StringBuilder privateInfo, int timeout) {
        Quest quest = new Quest("getuserinfo");

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            publicInfo.append(answer.want("oinfo"));
            privateInfo.append(answer.want("pinfo"));
        }
        return code;
    }

    //===========================[ Get User Open Info ]=========================//
    public boolean getUserPublicInfo(UserAttrsCallback callback, HashSet<Long> uids) {
        return getUserPublicInfo(callback, uids, 0);
    }

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     *
     * @param callback UserAttrsCallback回调(NoNull)
     * @param uids     用户uid集合
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getUserPublicInfo(final UserAttrsCallback callback, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("getuseropeninfo");

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                Map<String, String> attributes = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    attributes = new HashMap<>();
                    RTMUtils.wantStringMap(answer, "info", attributes);
                }
                callback.call(attributes, errorCode);
            }
        }, timeout);
    }

    public int getUserPublicInfo(Map<String, String> publicInfos, HashSet<Long> uids) {
        return getUserPublicInfo(publicInfos, uids, 0);
    }

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     *
     * @param publicInfos 返回用户id 公开信息map(NoNull) 用户id会被转变成string返回
     * @param uids        用户uid集合
     * @param timeout     超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public int getUserPublicInfo(Map<String, String> publicInfos, HashSet<Long> uids, int timeout) {
        Quest quest = new Quest("getuseropeninfo");
        quest.param("uids", uids);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantStringMap(answer, "uids", publicInfos);
        return code;
    }
}
