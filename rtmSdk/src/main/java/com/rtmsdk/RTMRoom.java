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

public class RTMRoom extends RTMFriend {
    //===========================[ Enter Room ]=========================//
    public boolean enterRoom(ErrorCodeCallback callback, long roomId) {
        return enterRoom(callback, roomId, 0);
    }

    /**
     * 进入房间 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean enterRoom(final ErrorCodeCallback callback, long roomId, int timeout) {
        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);

        return sendQuest(callback, quest, timeout);
    }

    public int enterRoom(long roomId) {
        return enterRoom(roomId, 0);
    }

    /**
     * 进入房间 async
     *
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int enterRoom(long roomId, int timeout) {
        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);
        return sendQuestCode(quest, timeout);
    }

    //===========================[ Leave Room ]=========================//
    public boolean leaveRoom(ErrorCodeCallback callback, long roomId) {
        return leaveRoom(callback, roomId, 0);
    }

    /**
     * 离开房间 async
     *
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     * @return true(发送成功)  false(发送失败)
     */
    public boolean leaveRoom(ErrorCodeCallback callback, long roomId, int timeout) {
        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);
        return sendQuest(callback, quest, timeout);
    }

    public int leaveRoom(long roomId) {
        return leaveRoom(roomId, 0);
    }

    /**
     * 离开房间 async
     *
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int leaveRoom(long roomId, int timeout) {
        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Get User Rooms ]=========================//
    //-- Action<roomIds, errorCode>
    public boolean getUserRooms(final MembersCallback callback) {
        return getUserRooms(callback, 0);
    }

    /**
     * 获取用户所在的房间   async
     *
     * @param callback MembersCallback回调(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getUserRooms(final MembersCallback callback, int timeout) {
        Quest quest = new Quest("getuserrooms");
        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> roomIds = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    roomIds = new HashSet<>();
                    RTMUtils.wantLongHashSet(answer,"rooms", roomIds);
                }
                callback.call(roomIds, errorCode);
            }
        }, timeout);
    }

    public int getUserRooms(HashSet<Long> roomIds) {
        return getUserRooms(roomIds, 0);
    }

    /**
     * 获取用户所在的房间   sync
     *
     * @param roomIds 群组集合(NoNull)
     * @param timeout 超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getUserRooms(HashSet<Long> roomIds, int timeout) {
        Quest quest = new Quest("getuserrooms");

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            RTMUtils.wantLongHashSet(answer,"rooms", roomIds);
        return code;
    }

    //===========================[ Set Room Info ]=========================//
    public boolean setRoomInfo(ErrorCodeCallback callback, long roomId) {
        return setRoomInfo(callback, roomId, null, null, 0);
    }

    public boolean setRoomInfo(ErrorCodeCallback callback, long roomId, String publicInfo) {
        return setRoomInfo(callback, roomId, publicInfo, null, 0);
    }

    public boolean setRoomInfo(ErrorCodeCallback callback, long roomId, String publicInfo, String privateInfo) {
        return setRoomInfo(callback, roomId, publicInfo, privateInfo, 0);
    }

    /**
     * 设置房间的公开信息或者私有信息 async
     *
     * @param callback    ErrorCodeCallback回调(NoNull)
     * @param roomId      房间id(NoNull)
     * @param publicInfo  群组公开信息
     * @param privateInfo 群组 私有信息
     * @param timeout     超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean setRoomInfo(ErrorCodeCallback callback, long roomId, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setroominfo");
        quest.param("rid", roomId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuest(callback, quest, timeout);
    }

    public int setRoomInfo(long roomId) {
        return setRoomInfo(roomId, null, null, 0);
    }

    public int setRoomInfo(long roomId, String publicInfo) {
        return setRoomInfo(roomId, publicInfo, null, 0);
    }

    public int setRoomInfo(long roomId, String publicInfo, String privateInfo) {
        return setRoomInfo(roomId, publicInfo, privateInfo, 0);
    }

    /**
     * 设置房间的公开信息或者私有信息 sync
     *
     * @param roomId      房间id(NoNull)
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int setRoomInfo(long roomId, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setroominfo");
        quest.param("rid", roomId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestCode(quest, timeout);
    }

    //===========================[ Get Room Info ]=========================//
    //-- Action<publicInfo, privateInfo, errorCode>
    public boolean getRoomInfo(final DoubleStringCallback callback, long roomId) {
        return getRoomInfo(callback, roomId, 0);
    }

    /**
     * 获取房间的公开信息或者私有信息 async
     *
     * @param callback DoubleStringCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getRoomInfo(final DoubleStringCallback callback, long roomId, int timeout) {
        Quest quest = new Quest("getroominfo");
        quest.param("rid", roomId);
        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String publicInfo = "";
                String privateInfo = "";

                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    publicInfo = answer.wantString("oinfo");
                    privateInfo = answer.wantString("pinfo");
                }
                callback.call(publicInfo, privateInfo, errorCode);
            }
        }, timeout);
    }

    public int getRoomInfo(StringBuilder publicInfo, StringBuilder privateInfo, long roomId) {
        return getRoomInfo(publicInfo, privateInfo, roomId, 0);
    }

    /**
     * 获取房间的公开信息或者私有信息 sync
     *
     * @param publicInfo  公开信息
     * @param privateInfo 私有信息
     * @param roomId      房间id(NoNull)
     * @param timeout     超时时间（秒）
     * @return errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getRoomInfo(StringBuilder publicInfo, StringBuilder privateInfo, long roomId, int timeout) {
        Quest quest = new Quest("getroominfo");
        quest.param("rid", roomId);
        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            publicInfo.append(answer.wantString("oinfo"));
            privateInfo.append(answer.wantString("pinfo"));
        }
        return code;
    }

    //===========================[ Get Room Open Info ]=========================//
    //-- Action<public_info, errorCode>
    public boolean getRoomPublicInfo(final MessageCallback callback, long roomId) {
        return getRoomPublicInfo(callback, roomId, 0);
    }

    /**
     * 获取房间的公开信息 async
     * @param callback MessageCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间（秒）
     * @return true(发送成功)  false(发送失败)
     */
    public boolean getRoomPublicInfo(final MessageCallback callback, long roomId, int timeout) {
        Quest quest = new Quest("getroomopeninfo");
        quest.param("rid", roomId);

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

    public int getRoomPublicInfo(StringBuilder publicInfo, long roomId) {
        return getRoomPublicInfo(publicInfo, roomId, 0);
    }

    /**
     * 获取房间的公开信息 sync
     * @param publicInfo    公开信息
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getRoomPublicInfo(StringBuilder publicInfo, long roomId, int timeout) {
        Quest quest = new Quest("getroomopeninfo");
        quest.param("rid", roomId);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            publicInfo.append(answer.wantString("oinfo"));

        return code;
    }
}
