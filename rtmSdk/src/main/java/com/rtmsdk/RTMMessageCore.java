package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.DuplicatedMessageFilter.MessageCategories;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMDoubleValueCallback;
import com.rtmsdk.RTMStruct.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class RTMMessageCore extends RTMCore {
    //======================[ String message version ]================================//
    private ModifyTimeStruct sendMsgSync(long id, byte mtype, Object message, Object attrs, int timeout, MessageCategories type) {
        String method = "", toWhere = "";
        switch (type) {
            case GroupMessage:
                method = "sendgroupmsg";
                toWhere = "gid";
                break;
            case RoomMessage:
                method = "sendroommsg";
                toWhere = "rid";
                break;
            case P2PMessage:
                method = "sendmsg";
                toWhere = "to";
                break;
        }
        long messageId = RTMUtils.genMid();

        Quest quest = new Quest(method);
        quest.param(toWhere, id);
        quest.param("mid", messageId);
        quest.param("mtype", mtype);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        Answer answer = sendQuest(quest, timeout);
        if (answer == null)
            return genModifyAnswer(ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value());
        else if (answer.getErrorCode() != ErrorCode.FPNN_EC_OK.value())
            return genModifyAnswer(answer);
        return genModifyAnswer(answer,answer.wantLong("mtime"), messageId);
    }

    private ModifyTimeStruct genModifyAnswer(int code){
        ModifyTimeStruct tmp = new ModifyTimeStruct();
        tmp.errorCode = code;
        tmp.errorMsg = RTMErrorCode.getMsg(code);
        tmp.modifyTime = 0;
        tmp.messageId = 0;
        if (code == ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value())
            tmp.errorMsg = "invalid connection";
        return tmp;
    }

    private ModifyTimeStruct genModifyAnswer(Answer anser, long time, long messageId){
        ModifyTimeStruct tmp = new ModifyTimeStruct();
        tmp.errorCode = anser.getErrorCode();
        tmp.errorMsg = anser.getErrorMessage();
        tmp.modifyTime = time;
        tmp.messageId = messageId;
        return tmp;
    }

    private ModifyTimeStruct genModifyAnswer(Answer anser){
        return genModifyAnswer(anser,0,0);
    }

    private void sendMsgAsync(final IRTMDoubleValueCallback<Long,Long> callback, long id, byte mtype, Object message, String attrs, int timeout, MessageCategories type) {
        String method = "", toWhere = "";
        switch (type) {
            case GroupMessage:
                method = "sendgroupmsg";
                toWhere = "gid";
                break;
            case RoomMessage:
                method = "sendroommsg";
                toWhere = "rid";
                break;
            case P2PMessage:
                method = "sendmsg";
                toWhere = "to";
                break;
        }
        long mid = RTMUtils.genMid();
        final Quest quest = new Quest(method);
        quest.param(toWhere, id);
        quest.param("mid", mid);
        quest.param("mtype", mtype);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                long mtime = 0;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    mtime = answer.wantLong("mtime");
                callback.onResult(mtime, quest.wantLong("mid"),genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }


    private HistoryMessageResult buildHistoryMessageResult(Answer answer) {
        HistoryMessageResult result = new HistoryMessageResult();
        if(answer == null)
            return result;
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

            RTMStruct.HistoryMessage tmp = new RTMStruct.HistoryMessage();
            tmp.cursorId = RTMUtils.wantLong(value.get(0));
            tmp.fromUid = RTMUtils.wantLong(value.get(1));
            tmp.messageType = (byte)RTMUtils.wantInt(value.get(2));
            tmp.messageId = RTMUtils.wantLong(value.get(3));
            Object obj = value.get(5);
            if (tmp.messageType == RTMStruct.MessageType.AUDIO) {
                RTMStruct.AudioInfo audioTmp = new RTMStruct.AudioInfo();
                try {
                    JSONObject kk = new JSONObject(String.valueOf(obj));
                    audioTmp.duration = kk.getInt("du");
                    audioTmp.sourceLanguage = kk.getString("sl");
                    audioTmp.recognizedLanguage = kk.getString("rl");
                    audioTmp.recognizedText = kk.getString("rt");
                    tmp.audioInfo = audioTmp;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                if (obj instanceof byte[])
                    tmp.binaryMessage = (byte [])obj;
                else
                    tmp.stringMessage = String.valueOf(obj);
            }

            tmp.attrs = String.valueOf(value.get(6));
            tmp.modifiedTime = RTMUtils.wantLong(value.get(7));
            result.messages.add(tmp);
        }
        result.count = result.messages.size();
        return result;
    }

    private Quest genGetMessageQuest(long id, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, MessageCategories type)
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

    private void adjustHistoryMessageResultForP2PMessage(long fromUid, HistoryMessageResult result) {
        for (RTMStruct.HistoryMessage hm : result.messages) {
            if (hm.fromUid == 1)
                hm.fromUid = getUid();
            else
                hm.fromUid = fromUid;
        }
    }

    void getHistoryMessage(final IRTMCallback<HistoryMessageResult> callback, final long id, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout, final MessageCategories type) {
        Quest quest = genGetMessageQuest(id, desc, count, beginMsec, endMsec, lastId, mtypes, type);
        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                HistoryMessageResult result = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    result = buildHistoryMessageResult(answer);
                    if (type == DuplicatedMessageFilter.MessageCategories.P2PMessage)
                        adjustHistoryMessageResultForP2PMessage(id, result);
                }
                callback.onResult(result, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    HistoryMessageResult getHistoryMessage(final long id, boolean desc, int count, long beginMsec, long endMsec, long lastId, List<Byte> mtypes, int timeout, MessageCategories type){
        Quest quest = genGetMessageQuest(id, desc, count, beginMsec, endMsec, lastId, mtypes, type);
        Answer answer = sendQuest(quest, timeout);
        HistoryMessageResult ret = new HistoryMessageResult();
        if (answer == null){
            ret.errorCode = ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value();
            ret.errorMsg = "invalid connection";
        }
        else if (answer.getErrorCode() == ErrorCode.FPNN_EC_OK.value()){
            ret = buildHistoryMessageResult(answer);
        }
        ret.errorCode = answer.getErrorCode();
        ret.errorMsg = answer.getErrorMessage();
        return ret;
    }

    private SingleMessage buildSingleMessage(Answer answer) {
        SingleMessage message = new SingleMessage();
        if (answer ==null || answer.getErrorCode() != RTMErrorCode.RTM_EC_OK.value())
            return message;
        message.messageId = answer.wantLong("id");
        message.messageType = (byte) answer.wantInt("mtype");
        message.attrs = answer.wantString("attrs");
        message.modifiedTime = answer.wantLong("mtime");
        Object obj = answer.want("msg");

        if (obj instanceof byte[])
            message.binaryMessage = (byte[]) obj;
        else
            message.stringMessage = String.valueOf(obj);
        return message;
    }

    void getMessage(final IRTMCallback<SingleMessage> callback, long fromUid, long xid, long messageId, int type, int timeout) {
        Quest quest = new Quest("getmsg");
        quest.param("mid", messageId);
        quest.param("xid", xid);
        quest.param("from", fromUid);
        quest.param("type", type);

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                SingleMessage SingleMessage = null;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    SingleMessage = buildSingleMessage(answer);
                callback.onResult(SingleMessage, genRTMAnswer(answer,errorCode));
            }
        }, timeout);
    }

    SingleMessage getMessage(long fromUid, long xid, long messageId, int type, int timeout){
        Quest quest = new Quest("getmsg");
        quest.param("mid", messageId);
        quest.param("xid", xid);
        quest.param("from", fromUid);
        quest.param("type", type);

        Answer answer = sendQuest(quest, timeout);
        RTMAnswer result = genRTMAnswer(answer);

        return buildSingleMessage(answer);
    }

    void delMessage(UserInterface.IRTMEmptyCallback callback, long fromUid, long xid, long messageId, int type, int timeout) {
        Quest quest = new Quest("delmsg");
        quest.param("mid", messageId);
        quest.param("xid", xid);
        quest.param("from", fromUid);
        quest.param("type", type);

        sendQuestEmptyCallback(callback,quest,timeout);
    }

    RTMAnswer delMessage(long fromUid, long xid, long messageId, int type, int timeout){
        Quest quest = new Quest("delmsg");
        quest.param("mid", messageId);
        quest.param("xid", xid);
        quest.param("from", fromUid);
        quest.param("type", type);

        return sendQuestEmptyResult(quest,timeout);
    }

    //======================[ String message version ]================================//
    void internalSendMessage(IRTMDoubleValueCallback<Long,Long> callback, long toid, byte mtype, Object message, String attrs, int timeout, MessageCategories msgType) {
        if (mtype <= MessageType.NORMALFILE){
            callback.onResult(0L,0L,genRTMAnswer(RTMErrorCode.RTM_EC_INVALID_FILE_MTYPE.value()));
            return;
        }
        sendMsgAsync(callback, toid, mtype, message, attrs, timeout, msgType);
    }

    ModifyTimeStruct internalSendMessage(long toid, byte mtype, Object message, String attrs, int timeout,MessageCategories msgType) {
        if (mtype <= MessageType.NORMALFILE)
            return genModifyAnswer(RTMErrorCode.RTM_EC_INVALID_FILE_MTYPE.value());
        return sendMsgSync(toid, mtype, message, attrs, timeout, msgType);
    }

    void internalSendChat(IRTMDoubleValueCallback<Long,Long> callback, long toid, byte mtype, Object message, String attrs, int timeout, MessageCategories msgType) {
        sendMsgAsync(callback, toid, mtype, message, attrs, timeout, msgType);
    }

    ModifyTimeStruct internalSendChat(long toid, byte mtype, Object message, String attrs, int timeout,MessageCategories msgType) {
        return sendMsgSync(toid, mtype, message, attrs, timeout, msgType);
    }
}

