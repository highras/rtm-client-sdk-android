package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.RTMStruct.*;

import java.util.HashSet;

public class RTMRoom extends RTMFriend {
    //重载
    public void enterRoom(IRTMEmptyCallback callback, long roomId) {
        enterRoom(callback, roomId, 0);
    }

    public RTMAnswer enterRoom(long roomId) {
        return enterRoom(roomId, 0);
    }

    public void leaveRoom(IRTMEmptyCallback callback, long roomId) {
        leaveRoom(callback, roomId, 0);
    }

    public RTMAnswer leaveRoom(long roomId){
        return leaveRoom(roomId, 0);
    }

    public void getUserRooms(IRTMCallback<HashSet<Long>> callback) {
        getUserRooms(callback, 0);
    }

    public MembersStruct getUserRooms(){
        return getUserRooms(0);
    }

    public void setGroupInfo(IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo) {
        setGroupInfo(callback, roomId, publicInfo, privateInfo, 0);
    }

    public RTMAnswer setRoomInfo(long roomId, String publicInfo, String privateInfo){
        return setRoomInfo(roomId, publicInfo, privateInfo, 0);
    }

    public void setRoomInfo(IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo) {
        setRoomInfo(callback,roomId,publicInfo,privateInfo,0);
    }

    public GroupInfoStruct getRoomInfo(long roomId){
        return getRoomInfo(roomId,0);
    }

    public void getRoomInfo(final IRTMCallback<GroupInfoStruct> callback, long roomId) {
        getRoomInfo(callback,roomId, 0);
    }

    public DataInfo getRoomPublicInfo(long roomId){
        return getRoomPublicInfo(roomId, 0);
    }

    public void getRoomPublicInfo(IRTMCallback<String>  callback,long roomId){
        getRoomPublicInfo(callback,roomId, 0);
    }
    //重载end


    /**
     * 进入房间 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void enterRoom(final IRTMEmptyCallback callback, long roomId, int timeout) {
        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 进入房间 sync
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     */
    public RTMAnswer enterRoom(long roomId, int timeout){
        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);
        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 离开房间 async
     * @param callback IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout  超时时间(秒)
     */
    public void leaveRoom(IRTMEmptyCallback callback, long roomId, int timeout) {
        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);
        sendQuestEmptyCallback(callback, quest, timeout);
    }


    /**
     * 离开房间 sync
     * @param roomId  房间id(NoNull)
     * @param timeout 超时时间(秒)
     */
    public RTMAnswer leaveRoom(long roomId, int timeout) {
        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);
        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 获取用户所在的房间   async
     * @param callback IRTMCallback回调(NoNull)
     * @param timeout  超时时间（秒）
     */
    public void getUserRooms(final IRTMCallback<HashSet<Long>> callback, int timeout) {
        Quest quest = new Quest("getuserrooms");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> groupIds = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    groupIds = RTMUtils.wantLongHashSet(answer,"rooms");
                callback.onResult(groupIds, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取用户所在的房间   sync
     * @param timeout   超时时间（秒）
     * @return  用户所在房间集合
     * */
    public MembersStruct getUserRooms(int timeout){
        Quest quest = new Quest("getuserrooms");

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        MembersStruct ret = new MembersStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.uids = RTMUtils.wantLongHashSet(answer,"rooms");
        return ret;
    }

    /**
     * 设置房间的公开信息或者私有信息 async
     * @param callback  IRTMEmptyCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     * @param timeout   超时时间（秒）
     */
    public void setRoomInfo(IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo, int timeout) {
        Quest quest = new Quest("setroominfo");
        quest.param("rid", roomId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        sendQuestEmptyCallback(callback, quest, timeout);
    }

    /**
     * 设置房间的公开信息或者私有信息 sync
     * @param roomId   房间id(NoNull)
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     * @param timeout   超时时间（秒）
     */
    public RTMAnswer setRoomInfo(long roomId, String publicInfo, String privateInfo, int timeout){
        Quest quest = new Quest("setroominfo");
        quest.param("rid", roomId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestEmptyResult(quest, timeout);
    }

    /**
     * 获取房间的公开信息或者私有信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getRoomInfo(final IRTMCallback<GroupInfoStruct> callback, final long roomId, int timeout) {
        Quest quest = new Quest("getroominfo");
        quest.param("rid", roomId);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                GroupInfoStruct RoomInfo = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    RoomInfo = new GroupInfoStruct();
                    RoomInfo.publicInfo = answer.wantString("oinfo");
                    RoomInfo.privateInfo = answer.wantString("pinfo");
                }
                callback.onResult(RoomInfo, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    /**
     * 获取房间的公开信息或者私有信息 sync
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getRoomInfo(long roomId, int timeout){
        Quest quest = new Quest("getroominfo");
        quest.param("rid", roomId);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        GroupInfoStruct RoomInfo = new GroupInfoStruct();
        if (result.errorCode == RTMErrorCode.RTM_EC_OK.value()) {
            RoomInfo.publicInfo = answer.wantString("oinfo");
            RoomInfo.privateInfo = answer.wantString("pinfo");
        }
        RoomInfo.errorMsg = result.errorMsg;
        RoomInfo.errorCode = result.errorCode;
        return RoomInfo;
    }


    /**
     * 获取房间的公开信息 async
     * @param callback  IRTMCallback回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     */
    public void getRoomPublicInfo(final IRTMCallback<String>  callback, long roomId, int timeout) {
        Quest quest = new Quest("getroomopeninfo");
        quest.param("rid", roomId);

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
     * 获取房间的公开信息 sync
     * @param roomId   房间id(NoNull)
     * @param timeout   超时时间（秒）
     * @return  房间公开信息
     */
    public DataInfo getRoomPublicInfo(long roomId, int timeout){
        Quest quest = new Quest("getroomopeninfo");
        quest.param("rid", roomId);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);
        DataInfo ret = new DataInfo();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.info = answer.wantString("oinfo");
        return  ret;
    }
}
