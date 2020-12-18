package com.rtmsdk;

import androidx.annotation.NonNull;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.UserInterface.*;
import com.rtmsdk.RTMStruct.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RTMRoom extends RTMFriend {
    /**
     * 进入房间 async
     * @param callback IRTMEmptyCallback回调
     * @param roomId   房间id
     */
    public void enterRoom(@NonNull IRTMEmptyCallback callback, long roomId) {
        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 进入房间 sync
     * @param roomId  房间id
     */
    public RTMAnswer enterRoom(long roomId){
        Quest quest = new Quest("enterroom");
        quest.param("rid", roomId);
        return sendQuestEmptyResult(quest);
    }

    /**
     * 离开房间 async
     * @param callback IRTMEmptyCallback回调
     * @param roomId   房间id
     */
    public void leaveRoom(@NonNull IRTMEmptyCallback callback, long roomId) {
        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);
        sendQuestEmptyCallback(callback, quest);
    }


    /**
     * 获取房间中的所有member sync(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param roomId  房间id
     */
    public MembersStruct getRoomMembers(long roomId) {
        Quest quest = new Quest("getroommembers");
        quest.param("rid",roomId);
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
     * 获取房间中的所有人数 async(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param callback IRTMEmptyCallback回调
     * @param roomId   房间id
     */
    public void getRoomCount(@NonNull final IRTMCallback<Integer> callback, long roomId) {
        Quest quest = new Quest("getroomcount");
        quest.param("rid",roomId);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                int count = 0;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    count = answer.wantInt("cn");
                callback.onResult(count, genRTMAnswer(answer,errorCode));
            }
        });
    }


    /**
     * 获取房间中的所有人数 sync(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param roomId  房间id
     */
    public MemberCount getRoomCount(long roomId) {
        Quest quest = new Quest("getroomcount");
        quest.param("rid",roomId);
        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        MemberCount ret = new MemberCount();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.count = answer.wantInt("cn");
        return ret;
    }

    /**
     * 获取房间中的所有member async(由于分布式系统，房间的人数会有几秒同步间隔)
     * @param callback IRTMEmptyCallback回调
     * @param roomId   房间id
     */
    public void getRoomMembers(@NonNull final IRTMCallback<HashSet<Long>> callback, long roomId) {
        Quest quest = new Quest("getroommembers");
        quest.param("rid",roomId);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> uIds = new HashSet<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    uIds = RTMUtils.wantLongHashSet(answer,"uids");
                callback.onResult(uIds, genRTMAnswer(answer,errorCode));
            }
        });
    }


    /**
     * 离开房间 sync
     * @param roomId  房间id
     */
    public RTMAnswer leaveRoom(long roomId) {
        Quest quest = new Quest("leaveroom");
        quest.param("rid", roomId);
        return sendQuestEmptyResult(quest);
    }

    /**
     * 获取用户所在的房间   async
     * @param callback IRTMCallback回调
     */
    public void getUserRooms(@NonNull final IRTMCallback<HashSet<Long>> callback) {
        Quest quest = new Quest("getuserrooms");

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HashSet<Long> groupIds = new HashSet<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    groupIds = RTMUtils.wantLongHashSet(answer,"rooms");
                callback.onResult(groupIds, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     * 获取用户所在的房间   sync
     * @return  用户所在房间集合
     * */
    public MembersStruct getUserRooms(){
        Quest quest = new Quest("getuserrooms");

        Answer answer = sendQuest(quest);
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
     * @param callback  IRTMEmptyCallback回调
     * @param roomId   房间id
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     */
    public void setRoomInfo(@NonNull IRTMEmptyCallback callback, long roomId, String publicInfo, String privateInfo) {
        Quest quest = new Quest("setroominfo");
        quest.param("rid", roomId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        sendQuestEmptyCallback(callback, quest);
    }

    /**
     * 设置房间的公开信息或者私有信息 sync
     * @param roomId   房间id
     * @param publicInfo    房间公开信息
     * @param privateInfo   房间 私有信息
     */
    public RTMAnswer setRoomInfo(long roomId, String publicInfo, String privateInfo){
        Quest quest = new Quest("setroominfo");
        quest.param("rid", roomId);
        if (publicInfo != null)
            quest.param("oinfo", publicInfo);
        if (privateInfo != null)
            quest.param("pinfo", privateInfo);

        return sendQuestEmptyResult(quest);
    }

    /**
     * 获取房间的公开信息或者私有信息 async
     * @param callback  IRTMCallback回调
     * @param roomId   房间id
     */
    public void getRoomInfo(@NonNull final IRTMCallback<GroupInfoStruct> callback, final long roomId) {
        Quest quest = new Quest("getroominfo");
        quest.param("rid", roomId);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                GroupInfoStruct RoomInfo = new GroupInfoStruct();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    RoomInfo.publicInfo = answer.wantString("oinfo");
                    RoomInfo.privateInfo = answer.wantString("pinfo");
                }
                callback.onResult(RoomInfo, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     * 获取房间的公开信息或者私有信息 sync
     * @param roomId   房间id
     * @return  GroupInfoStruct结构
     */
    public GroupInfoStruct getRoomInfo(long roomId){
        Quest quest = new Quest("getroominfo");
        quest.param("rid", roomId);

        Answer answer = sendQuest(quest);
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
     * @param callback  IRTMCallback回调
     * @param roomId   房间id
     */
    public void getRoomPublicInfo(@NonNull final IRTMCallback<String>  callback, long roomId) {
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
        });
    }

    /**
     * 获取房间的公开信息 sync
     * @param roomId   房间id
     * @return  GroupInfoStruct
     */
    public GroupInfoStruct getRoomPublicInfo(long roomId){
        Quest quest = new Quest("getroomopeninfo");
        quest.param("rid", roomId);

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        GroupInfoStruct ret = new GroupInfoStruct();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.publicInfo = answer.wantString("oinfo");
        return  ret;
    }

    /**
     * 获取其他用户的公开信息，每次最多获取100人
     * @param callback IRTMCallback<Map<String, String>>回调
     * @param rids     房间id集合
     */
    public void getRoomsOpeninfo(@NonNull final IRTMCallback<Map<String, String>> callback, HashSet<Long> rids) {
        Quest quest = new Quest("getroomsopeninfo");
        quest.param("rids",rids);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                Map<String, String> attributes = new HashMap<>();
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    attributes = RTMUtils.wantStringMap(answer, "info");
                }
                callback.onResult(attributes, genRTMAnswer(answer,errorCode));
            }
        });
    }

    /**
     * 获取群组的公开信息，每次最多获取100人
     * @param rids        房间id集合
     *return              PublicInfo 结构
     */
    public PublicInfo getRoomsOpeninfo(@NonNull HashSet<Long> rids) {
        Quest quest = new Quest("getroomsopeninfo");
        quest.param("rids", rids);

        Answer answer = sendQuest(quest);
        RTMAnswer result = genRTMAnswer(answer);
        PublicInfo ret = new PublicInfo();
        ret.errorCode = result.errorCode;
        ret.errorMsg = result.errorMsg;
        if (ret.errorCode == RTMErrorCode.RTM_EC_OK.value())
            ret.publicInfos = RTMUtils.wantStringMap(answer, "info");

        return ret;
    }
}
