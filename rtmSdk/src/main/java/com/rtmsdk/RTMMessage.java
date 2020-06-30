package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.HistoryMessage;
import com.rtmsdk.RTMStruct.HistoryMessageResult;
import com.rtmsdk.RTMStruct.LongMtime;
import com.rtmsdk.RTMStruct.RetrievedMessage;
import com.rtmsdk.UserInterface.ErrorCodeCallback;
import com.rtmsdk.UserInterface.HistoryMessageCallback;
import com.rtmsdk.UserInterface.LongFunctionCallback;
import com.rtmsdk.UserInterface.RetrievedMessageCallback;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

class RTMMessage extends RTMMessageCore {
    private boolean checkMessageType(byte mtype) {
        if (mtype <= MessageMType_FileEnd) {
            if (errorRecorder != null)
                errorRecorder.recordError("require mtype large than 50, current mtype is " + mtype);

            return false;
        }
        return true;
    }

    private void buildHistoryMessageResult(Answer answer, HistoryMessageResult result) {
        result.count = answer.wantInt("num");
        result.lastId = answer.wantLong("lastid");
        result.beginMsec = answer.wantLong("begin");
        result.endMsec = answer.wantLong("end");
        result.messages = new ArrayList<>();

        ArrayList<List<Object>> messages = (ArrayList<List<Object>>) answer.want("msgs");
        for (List<Object> value : messages) {
            boolean delete = (boolean)(value.get(4));
            if (delete)
                continue;

            HistoryMessage tmp = new HistoryMessage();
            tmp.id = RTMUtils.wantLong(value.get(0));
            tmp.fromUid = RTMUtils.wantLong(value.get(1));
            tmp.mtype = (byte)RTMUtils.wantInt(value.get(2));
            tmp.mid = RTMUtils.wantLong(value.get(3));
            Object obj = value.get(5);
            if (tmp.mtype == MessageMType_Audio)
            {
                byte data[] = null;
                if (obj instanceof byte[]) {
                    data = (byte[]) obj;
                }
                else {
                    try {
                        data = String.valueOf(obj).getBytes("iso8859-1");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                tmp.binaryMessage = RTMAudio.unpackAudioData(data);
            }
            else {
                if (obj instanceof byte[])
                    tmp.binaryMessage = (byte [])obj;
                else
                    tmp.message = String.valueOf(obj);
            }

            tmp.attrs = String.valueOf(value.get(6));
            tmp.mtime = RTMUtils.wantLong(value.get(7));
            result.messages.add(tmp);
        }
        result.count = result.messages.size();
    }

    private void adjustHistoryMessageResultForP2PMessage(long fromUid, HistoryMessageResult result) {
        for (HistoryMessage hm : result.messages) {
            if (hm.fromUid == 1)
                hm.fromUid = getUid();
            else
                hm.fromUid = fromUid;
        }
    }

    private Quest genGetMessageQuest(long id, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, DuplicatedMessageFilter.MessageCategories type)
    {
        String method = "", toWhere = "";
        switch (type) {
            case GroupMessage:
                method = "getgroupmsg";
                toWhere = "gid";
                break;
            case RoomMessage:
                method = "getroommsg";
                toWhere = "rid";
                break;
            case P2PMessage:
                method = "getp2pmsg";
                toWhere = "ouid";
                break;
            case BroadcastMessage:
                method = "getbroadcastmsg";
                toWhere = "";
                break;
        }

        Quest quest = new Quest(method);
        if (!toWhere.equals(""))
            quest.param(toWhere, id);
        quest.param("desc", desc);
        quest.param("num", count);

        quest.param("begin", beginMsec);
        quest.param("end", endMsec);
        quest.param("lastid", lastId);

        if (mtypes != null && mtypes.size() > 0)
            quest.param("mtypes", mtypes);
        return quest;
    }

    private boolean getHistoryMessage(final HistoryMessageCallback callback, final long id, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout, final DuplicatedMessageFilter.MessageCategories type) {
        Quest quest = genGetMessageQuest(id, desc, count, beginMsec, endMsec, lastId, mtypes, type);
        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    HistoryMessageResult result = new HistoryMessageResult();
                    buildHistoryMessageResult(answer, result);
                    if (type == DuplicatedMessageFilter.MessageCategories.P2PMessage)
                        adjustHistoryMessageResultForP2PMessage(id, result);
                    callback.call(result.count, result.lastId, result.beginMsec, result.endMsec, result.messages, errorCode);
                } else
                    callback.call(0, 0, 0, 0, null, errorCode);
            }
        }, timeout);
    }

    private int getHistoryMessage(HistoryMessageResult result, final long id, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout, DuplicatedMessageFilter.MessageCategories type) {
        Quest quest = genGetMessageQuest(id, desc, count, beginMsec, endMsec, lastId, mtypes, type);
        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            buildHistoryMessageResult(answer, result);
            if (type == DuplicatedMessageFilter.MessageCategories.P2PMessage)
                adjustHistoryMessageResultForP2PMessage(id, result);
        }
        return code;
    }

    //===========================[ Sending String Messages ]=========================//

    /**
     * mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE.
     */
    public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, String message) {
        return sendMessage(callback, uid, mtype, message, "", 0);
    }

    public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, String message, String attrs) {
        return sendMessage(callback, uid, mtype, message, attrs, 0);
    }

    /**
     *发送p2p消息(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param uid       目标用户id(NoNull)
     * @param mtype     消息类型
     * @param message   p2p消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, String message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return false;
        return internalSendMessage(callback, uid, mtype, message, attrs, timeout);
    }

    public int sendMessage(LongMtime mtime, long uid, byte mtype, String message) {
        return sendMessage(mtime, uid, mtype, message, "", 0);
    }

    public int sendMessage(LongMtime mtime, long uid, byte mtype, String message, String attrs) {
        return sendMessage(mtime, uid, mtype, message, attrs, 0);
    }

    /**
     *发送p2p消息(sync)
     * @param mtime     LongMtimed对象(服务器接收时间)
     * @param uid       目标用户id(NoNull)
     * @param mtype     消息类型
     * @param message   消息内容(NoNull)
     * @param attrs     客户端自定义信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendMessage(LongMtime mtime, long uid, byte mtype, String message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return RTMErrorCode.RTM_EC_INVALID_MTYPE.value();
        return internalSendMessage(mtime, uid, mtype, message, attrs, timeout);
    }


    //*****sendGroupMessage******//
    public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, String message) {
        return sendGroupMessage(callback, groupId, mtype, message, "", 0);
    }

    public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, String message, String attrs) {
        return sendGroupMessage(callback, groupId, mtype, message, attrs, 0);
    }

    /**
     *发送群组消息(async)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型
     * @param message   群组消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, String message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return false;
        return internalSendGroupMessage(callback, groupId, mtype, message, attrs, timeout);
    }

    public int sendGroupMessage(LongMtime mtime, long groupId, byte mtype, String message) {
        return sendGroupMessage(mtime, groupId, mtype, message, "", 0);
    }

    public int sendGroupMessage(LongMtime mtime, long groupId, byte mtype, String message, String attrs) {
        return sendGroupMessage(mtime, groupId, mtype, message, attrs, 0);
    }

    /**
     *发送群组消息(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型
     * @param message   群组消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendGroupMessage(LongMtime mtime, long groupId, byte mtype, String message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return RTMErrorCode.RTM_EC_INVALID_MTYPE.value();
        return internalSendGroupMessage(mtime, groupId, mtype, message, attrs, timeout);
    }


    //*****sendRoomMessage******//
    public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, String message) {
        return sendRoomMessage(callback, roomId, mtype, message, "", 0);
    }

    public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, String message, String attrs) {
        return sendRoomMessage(callback, roomId, mtype, message, attrs, 0);
    }

    /**
     *发送房间消息(sync)
     * @param callback  LongFunctionCallback接口回调(NoNull)
     * @param roomId    房间id(NoNull)
     * @param mtype     消息类型
     * @param message   房间消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, String message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return false;
        return internalSendRoomMessage(callback, roomId, mtype, message, attrs, timeout);
    }

    public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, String message) {
        return sendRoomMessage(mtime, roomId, mtype, message, "", 0);
    }

    public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, String message, String attrs) {
        return sendRoomMessage(mtime, roomId, mtype, message, attrs, 0);
    }

    /**
     *发送房间消息(sync)
     * @param mtime  LongMtime对象(NoNull)
     * @param roomId    房间id(NoNull)
     * @param mtype     消息类型
     * @param message   房间消息(NoNull)
     * @param attrs     客户端自定义属性信息
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, String message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return RTMErrorCode.RTM_EC_INVALID_MTYPE.value();

        return internalSendRoomMessage(mtime, roomId, mtype, message, attrs, timeout);
    }


    //===========================[ Sending Binary Messages ]=========================//
    /**参数说明同上
     * mtype MUST large than 50, else this interface will return false or erroeCode-RTM_EC_INVALID_MTYPE.
     */

    public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, byte[] message) {
        return sendMessage(callback, uid, mtype, message, "", 0);
    }

    public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, byte[] message, String attrs) {
        return sendMessage(callback, uid, mtype, message, attrs, 0);
    }

    public boolean sendMessage(LongFunctionCallback callback, long uid, byte mtype, byte[] message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return false;
        return internalSendMessage(callback, uid, mtype, message, attrs, timeout);
    }

    public int sendMessage(LongMtime mtime, long uid, byte mtype, byte[] message) {
        return sendMessage(mtime, uid, mtype, message, "", 0);
    }

    public int sendMessage(LongMtime mtime, long uid, byte mtype, byte[] message, String attrs) {
        return sendMessage(mtime, uid, mtype, message, attrs, 0);
    }

    public int sendMessage(LongMtime mtime, long uid, byte mtype, byte[] message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return RTMErrorCode.RTM_EC_INVALID_MTYPE.value();

        return internalSendMessage(mtime, uid, mtype, message, attrs, timeout);
    }


    //*****sendGroupMessage******//
    public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, byte[] message) {
        return sendMessage(callback, groupId, mtype, message, "", 0);
    }

    public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, byte[] message, String attrs) {
        return sendMessage(callback, groupId, mtype, message, attrs, 0);
    }

    public boolean sendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, byte[] message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return false;
        return internalSendGroupMessage(callback, groupId, mtype, message, attrs, timeout);
    }

    public int sendGroupMessage(LongMtime mtime, long uid, byte mtype, byte[] message) {
        return sendMessage(mtime, uid, mtype, message, "", 0);
    }

    public int sendGroupMessage(LongMtime mtime, long uid, byte mtype, byte[] message, String attrs) {
        return sendMessage(mtime, uid, mtype, message, attrs, 0);
    }

    public int SendGroupMessage(LongMtime mtime, long uid, byte mtype, byte[] message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return RTMErrorCode.RTM_EC_INVALID_MTYPE.value();

        return internalSendGroupMessage(mtime, uid, mtype, message, attrs, timeout);
    }


    //*****sendRoomMessage******//
    public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, byte[] message) {
        return sendRoomMessage(callback, roomId, mtype, message, "", 0);
    }

    public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, byte[] message, String attrs) {
        return sendRoomMessage(callback, roomId, mtype, message, attrs, 0);
    }

    public boolean sendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, byte[] message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return false;
        return internalSendRoomMessage(callback, roomId, mtype, message, attrs, timeout);
    }

    public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, byte[] message) {
        return sendRoomMessage(mtime, roomId, mtype, message, "", 0);
    }

    public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, byte[] message, String attrs) {
        return sendRoomMessage(mtime, roomId, mtype, message, attrs, 0);
    }

    public int sendRoomMessage(LongMtime mtime, long roomId, byte mtype, byte[] message, String attrs, int timeout) {
        if (!checkMessageType(mtype))
            return RTMErrorCode.RTM_EC_INVALID_MTYPE.value();

        return internalSendRoomMessage(mtime, roomId, mtype, message, attrs, timeout);
    }


    //===========================[ History Messages ]=========================//
    //-------------[ Group History Messages ]---------------------//

    public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count) {
        return getGroupMessage(callback, groupId, desc, count, 0, 0, 0, null, 0);
    }

    public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec) {
        return getGroupMessage(callback, groupId, desc, count, beginMsec, 0, 0, null, 0);
    }

    public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec) {
        return getGroupMessage(callback, groupId, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getGroupMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getGroupMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    /**
     *
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param groupId  群组id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getGroupMessage(HistoryMessageCallback callback, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(callback, groupId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.GroupMessage);
    }

    public int getGroupMessage(HistoryMessageResult result, long groupId, boolean desc, int count) {
        return getGroupMessage(result, groupId, desc, count, 0, 0, 0, null, 0);
    }

    public int getGroupMessage(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec) {
        return getGroupMessage(result, groupId, desc, count, beginMsec, 0, 0, null, 0);
    }

    public int getGroupMessage(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec) {
        return getGroupMessage(result, groupId, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public int getGroupMessage(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getGroupMessage(result, groupId, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public int getGroupMessage(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getGroupMessage(result, groupId, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    public int getGroupMessage(HistoryMessageResult result, long groupId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(result, groupId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.GroupMessage);
    }

    //-------------[ Room History Messages ]---------------------//
    public boolean getRoomMessage(HistoryMessageCallback callback, long roomId, boolean desc, int count) {
        return getRoomMessage(callback, roomId, desc, count, 0, 0, 0, null, 0);
    }

    public boolean getRoomMessage(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec) {
        return getRoomMessage(callback, roomId, desc, count, beginMsec, 0, 0, null, 0);
    }

    public boolean getRoomMessage(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec) {
        return getRoomMessage(callback, roomId, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public boolean getRoomMessage(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getRoomMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public boolean getRoomMessage(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getRoomMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    public boolean getRoomMessage(HistoryMessageCallback callback, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(callback, roomId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.RoomMessage);
    }

    public int getRoomMessage(HistoryMessageResult result, long roomId, boolean desc, int count) {
        return getRoomMessage(result, roomId, desc, count, 0, 0, 0, null, 0);
    }

    public int getRoomMessage(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec) {
        return getRoomMessage(result, roomId, desc, count, beginMsec, 0, 0, null, 0);
    }

    public int getRoomMessage(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec) {
        return getRoomMessage(result, roomId, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public int getRoomMessage(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getRoomMessage(result, roomId, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public int getRoomMessage(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getRoomMessage(result, roomId, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    public int getRoomMessage(HistoryMessageResult result, long roomId, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(result, roomId, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.RoomMessage);
    }

    //-------------[ Broadcast History Messages ]---------------------//
    public boolean getBroadcastMessage(HistoryMessageCallback callback, boolean desc, int count) {
        return getBroadcastMessage(callback, desc, count, 0, 0, 0, null, 0);
    }

    public boolean getBroadcastMessage(HistoryMessageCallback callback, boolean desc, int count, long beginMsec) {
        return getBroadcastMessage(callback, desc, count, beginMsec, 0, 0, null, 0);
    }

    public boolean getBroadcastMessage(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec) {
        return getBroadcastMessage(callback, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public boolean getBroadcastMessage(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getBroadcastMessage(callback, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public boolean getBroadcastMessage(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getBroadcastMessage(callback, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    public boolean getBroadcastMessage(HistoryMessageCallback callback, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(callback, -1, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.BroadcastMessage);
    }

    public int getBroadcastMessage(HistoryMessageResult result, boolean desc, int count) {
        return getBroadcastMessage(result, desc, count, 0, 0, 0, null, 0);
    }

    public int getBroadcastMessage(HistoryMessageResult result, boolean desc, int count, long beginMsec) {
        return getBroadcastMessage(result, desc, count, beginMsec, 0, 0, null, 0);
    }

    public int getBroadcastMessage(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec) {
        return getBroadcastMessage(result, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public int getBroadcastMessage(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getBroadcastMessage(result, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public int getBroadcastMessage(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getBroadcastMessage(result, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    public int getBroadcastMessage(HistoryMessageResult result, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(result, -1, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.BroadcastMessage);
    }

    //-------------[ P2P History Messages ]---------------------//
    public boolean getP2PMessage(HistoryMessageCallback callback, long peerUid, boolean desc, int count) {
        return getP2PMessage(callback, peerUid, desc, count, 0, 0, 0, null, 0);
    }

    public boolean getP2PMessage(HistoryMessageCallback callback, long peerUid, boolean desc, int count, long beginMsec) {
        return getP2PMessage(callback, peerUid, desc, count, beginMsec, 0, 0, null, 0);
    }

    public boolean getP2PMessage(HistoryMessageCallback callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec) {
        return getP2PMessage(callback, peerUid, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public boolean getP2PMessage(HistoryMessageCallback callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getP2PMessage(callback, peerUid, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public boolean getP2PMessage(HistoryMessageCallback callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getP2PMessage(callback, peerUid, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    /**
     *获取p2p记录(async)
     * @param callback  HistoryMessageCallback回调(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getP2PMessage(HistoryMessageCallback callback, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(callback, peerUid, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }

    public int getP2PMessage(HistoryMessageResult result, long peerUid, boolean desc, int count) {
        return getP2PMessage(result, peerUid, desc, count, 0, 0, 0, null, 0);
    }

    public int getP2PMessage(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec) {
        return getP2PMessage(result, peerUid, desc, count, beginMsec, 0, 0, null, 0);
    }

    public int getP2PMessage(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec) {
        return getP2PMessage(result, peerUid, desc, count, beginMsec, endMsec, 0, null, 0);
    }

    public int getP2PMessage(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId) {
        return getP2PMessage(result, peerUid, desc, count, beginMsec, endMsec, lastId, null, 0);
    }

    public int getP2PMessage(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes) {
        return getP2PMessage(result, peerUid, desc, count, beginMsec, endMsec, lastId, mtypes, 0);
    }

    /**
     *获取p2p记录(sync)
     * @param result  HistoryMessageResult对象(NoNull)
     * @param peerUid   用户id(NoNull)
     * @param desc      是否按时间倒叙排列
     * @param count     显示条目数 最多一次20
     * @param beginMsec 开始时间戳(毫秒)
     * @param endMsec   结束时间戳(毫秒)
     * @param lastId    最后一条消息id
     * @param mtypes    查询历史消息类型
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getP2PMessage(HistoryMessageResult result, long peerUid, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout) {
        return getHistoryMessage(result, peerUid, desc, count, beginMsec, endMsec, lastId, mtypes, timeout, DuplicatedMessageFilter.MessageCategories.P2PMessage);
    }
    //-------------[ delete Messages ]---------------------//
    //-- xid: peer uid, or groupId, or roomId
    //-- type: 1: p2p, 2: group; 3: room

    public boolean deleteMessage(ErrorCodeCallback callback, long xid, long mid, int type) {
        return deleteMessage(callback, xid, mid, type, 0);
    }

    /**
     *删除单条信息 async
     * @param callback ErrorCodeCallback回调(NoNull)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean deleteMessage(ErrorCodeCallback callback, long xid, long mid, int type, int timeout) {
        Quest quest = new Quest("delmsg");
        quest.param("mid", mid);
        quest.param("xid", xid);
        quest.param("type", type);

        return sendQuest(callback, quest, timeout);
    }

    public int deleteMessage(long xid, long mid, int type) {
        return deleteMessage(xid, mid, type, 0);
    }

    /**
     * 删除消息 sync
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int deleteMessage(long xid, long mid, int type, int timeout) {
        Quest quest = new Quest("delmsg");
        quest.param("mid", mid);
        quest.param("xid", xid);
        quest.param("type", type);

        return sendQuestCode(quest, timeout);
    }

    //-------------[ Get Messages ]---------------------//
    private void buildRetrievedMessage(Answer answer, RetrievedMessage message) {
        message.id = answer.wantLong("id");
        message.mtype = (byte) answer.want("mtype");
        message.attrs = answer.wantString("attrs");
        message.mtime = answer.wantLong("mtime");

        String originalMessage = answer.wantString("msg");

        if (message.mtype == MessageMType_Audio)
            message.binaryMessage =  originalMessage.getBytes();
         else
             message.stringMessage = originalMessage;
    }

    //-- xid: peer uid, or groupId, or roomId
    //-- type: 1: p2p, 2: group; 3: room
    public boolean getMessage(RetrievedMessageCallback callback, long xid, long mid, int type) {
        return getMessage(callback, xid, mid, type, 0);
    }

    /**
     *获取单条消息 async
     * @param callback RetrievedMessageCallback回调(NoNull)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  true(发送成功)  false(发送失败)
     */
    public boolean getMessage(final RetrievedMessageCallback callback, long xid, long mid, int type, int timeout) {
        Quest quest = new Quest("getmsg");
        quest.param("mid", mid);
        quest.param("xid", xid);
        quest.param("type", type);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                RetrievedMessage retrievedMessage = new RetrievedMessage();
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    buildRetrievedMessage(answer, retrievedMessage);

                callback.call(retrievedMessage, errorCode);
            }
        }, timeout);
    }

    public int getMessage(RetrievedMessage retrievedMessage, long xid, long mid, int type) {
        return getMessage(retrievedMessage, xid, mid, type, 0);
    }

    /*获取单条消息 sync
     * @param retrievedMessage RetrievedMessage对象(**NoNull**)
     * @param xid   roomid/groupid/touid(NoNull)
     * @param mid   消息mid(NoNull)
     * @param type  1-p2p; 2-group; 3-room(NoNull)
     * @param timeout   超时时间(秒)
     * @return  errcode错误码(如果为RTMErrorCode.FPNN_EC_OK为成功)
     */
    public int getMessage(RetrievedMessage retrievedMessage, long xid, long mid, int type, int timeout) {
        Quest quest = new Quest("getmsg");
        quest.param("mid", mid);
        quest.param("xid", xid);
        quest.param("type", type);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            buildRetrievedMessage(answer, retrievedMessage);
        return code;
    }
}
