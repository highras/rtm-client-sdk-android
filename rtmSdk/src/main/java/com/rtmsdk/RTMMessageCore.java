package com.rtmsdk;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.DuplicatedMessageFilter.MessageCategories;
import com.rtmsdk.RTMStruct.LongMtime;
import com.rtmsdk.UserInterface.LongFunctionCallback;

class RTMMessageCore extends RTMCore {
    static final byte MessageMType_Chat = 30;
    static final byte MessageMType_Audio = 31;
    static final byte MessageMType_Cmd = 32;
    static final byte MessageMType_FileStart = 40;
    static final byte MessageMType_FileEnd = 50;

    //======================[ String message version ]================================//
    private int sendMsgSync(long id, byte mtype, Object message, Object attrs, int timeout, MessageCategories type, LongMtime mtime) {
        String method = "", toWhere = "";
        mtime.mtime = 0;
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

        Quest quest = new Quest(method);
        quest.param(toWhere, id);
        quest.param("mid", RTMUtils.genMid());
        quest.param("mtype", mtype);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        Answer answer = sendQuest(quest, timeout);
        int code = checkAnswer(answer);
        if (code == ErrorCode.FPNN_EC_OK.value())
            mtime.mtime = answer.wantLong("mtime");
        return code;
    }

    private boolean sendMsgAsync(final LongFunctionCallback callback, long id, byte mtype, Object message, String attrs, int timeout, MessageCategories type) {
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
        Quest quest = new Quest(method);
        quest.param(toWhere, id);
        quest.param("mid", mid);
        quest.param("mtype", mtype);
        quest.param("msg", message);
        quest.param("attrs", attrs);

        return sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                long mtime = 0;
                if (errorCode == ErrorCode.FPNN_EC_OK.value())
                    mtime = answer.wantLong("mtime");
                callback.call(mtime, errorCode);
            }
        }, timeout);
    }

    //======================[ String message version ]================================//
    protected boolean internalSendMessage(final LongFunctionCallback callback, long uid, byte mtype, String message, String attrs, int timeout) {
        return sendMsgAsync(callback, uid, mtype, message, attrs, timeout, MessageCategories.P2PMessage);
    }

    protected int internalSendMessage(LongMtime mtime, long uid, byte mtype, String message, String attrs, int timeout) {
        return sendMsgSync(uid, mtype, message, attrs, timeout, MessageCategories.P2PMessage, mtime);
    }

    protected boolean internalSendGroupMessage(final LongFunctionCallback callback, long groupId, byte mtype, String message, String attrs, int timeout) {
        return sendMsgAsync(callback, groupId, mtype, message, attrs, timeout, MessageCategories.GroupMessage);
    }

    protected int internalSendGroupMessage(LongMtime mtime, long groupId, byte mtype, String message, String attrs, int timeout) {
        return sendMsgSync(groupId, mtype, message, attrs, timeout, MessageCategories.GroupMessage, mtime);
    }

    protected boolean internalSendRoomMessage(final LongFunctionCallback callback, long roomId, byte mtype, String message, String attrs, int timeout) {
        return sendMsgAsync(callback, roomId, mtype, message, attrs, timeout, MessageCategories.RoomMessage);
    }

    protected int internalSendRoomMessage(LongMtime mtime, long roomId, byte mtype, String message, String attrs, int timeout) {
        return sendMsgSync(roomId, mtype, message, attrs, timeout, MessageCategories.RoomMessage, mtime);
    }

    //======================[ binary message version ]================================//
    protected boolean internalSendMessage(final LongFunctionCallback callback, long uid, byte mtype, byte[] message, String attrs, int timeout) {
        return sendMsgAsync(callback, uid, mtype, message, attrs, timeout, MessageCategories.P2PMessage);
    }

    protected int internalSendMessage(LongMtime mtime, long uid, byte mtype, byte[] message, String attrs, int timeout) {
        return sendMsgSync(uid, mtype, message, attrs, timeout, MessageCategories.P2PMessage, mtime);
    }

    protected boolean internalSendGroupMessage(LongFunctionCallback callback, long groupId, byte mtype, byte[] message, String attrs, int timeout) {
        return sendMsgAsync(callback, groupId, mtype, message, attrs, timeout, MessageCategories.GroupMessage);
    }

    protected int internalSendGroupMessage(LongMtime mtime, long groupId, byte mtype, byte[] message, String attrs, int timeout) {
        return sendMsgSync(groupId, mtype, message, attrs, timeout, MessageCategories.GroupMessage, mtime);
    }

    protected boolean internalSendRoomMessage(LongFunctionCallback callback, long roomId, byte mtype, byte[] message, String attrs, int timeout) {
        return sendMsgAsync(callback, roomId, mtype, message, attrs, timeout, MessageCategories.RoomMessage);
    }

    protected int internalSendRoomMessage(LongMtime mtime, long roomId, byte mtype, byte[] message, String attrs, int timeout) {
        return sendMsgSync(roomId, mtype, message, attrs, timeout, MessageCategories.RoomMessage, mtime);
    }
}

