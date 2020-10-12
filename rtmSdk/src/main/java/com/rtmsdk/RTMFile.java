package com.rtmsdk;

import com.fpnn.sdk.ConnectionConnectedCallback;
import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.ErrorRecorder;
import com.fpnn.sdk.FunctionalAnswerCallback;
import com.fpnn.sdk.TCPClient;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.IRTMDoubleValueCallback;

import java.net.InetSocketAddress;

class RTMFile extends RTMSystem {
    interface DoubleStringCallback{
        void onResult(String str1, String str2, int errorCode);
    }

    private enum fileTokenType {
        P2P,
        Group,
        Room
    }

    private static class SendFileInfo {
        public fileTokenType actionType;

        public long xid;
        public byte mtype;
        public byte[] fileContent;
        public String filename;
        public String fileExtension;

        public String token;
        public String endpoint;
        public int remainTimeout;
        public long lastActionTimestamp;
        public UserInterface.IRTMDoubleValueCallback<Long,Long> callback;
    }

    //重载
    public void sendFile(IRTMDoubleValueCallback<Long,Long> callback, long peerUid, byte mtype, byte[] fileContent, String filename) {
        sendFile(callback, peerUid, mtype, fileContent, filename, "", RTMConfig.globalFileQuestTimeoutSeconds);
    }

    public ModifyTimeStruct sendFile(long peerUid, byte mtype, byte[] fileContent, String filename){
        return sendFile(peerUid, mtype, fileContent, filename, "", RTMConfig.globalFileQuestTimeoutSeconds);
    }

    public void sendGroupFile(IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, byte[] fileContent, String filename) {
        sendGroupFile(callback, groupId, mtype, fileContent, filename, "", RTMConfig.globalFileQuestTimeoutSeconds);
    }

    public ModifyTimeStruct sendGroupFile(long groupId, byte mtype, byte[] fileContent, String filename){
        return sendGroupFile(groupId, mtype, fileContent, filename, "", RTMConfig.globalFileQuestTimeoutSeconds);
    }

    public void  sendRoomFile(IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, byte[] fileContent, String filename) {
        sendRoomFile(callback, roomId, mtype, fileContent, filename, "", RTMConfig.globalFileQuestTimeoutSeconds);
    }

    public ModifyTimeStruct sendRoomFile(long roomId, byte mtype, byte[] fileContent, String filename){
        return sendRoomFile(roomId, mtype, fileContent, filename, "", RTMConfig.globalFileQuestTimeoutSeconds);
    }
    //重载end

    //===========================[ File Token ]=========================//
    private void fileToken(final DoubleStringCallback callback, fileTokenType tokenType, long xid) {
        fileToken(callback, tokenType, xid, 0);
    }

    private void fileToken(final DoubleStringCallback callback, fileTokenType tokenType, long xid, int timeout) {
        Quest quest = new Quest("filetoken");
        switch (tokenType) {
            case P2P:
                quest.param("cmd", "sendfile");
                quest.param("to", xid);
                break;
            case Group:
                quest.param("cmd", "sendgroupfile");
                quest.param("gid", xid);
                break;
            case Room:
                quest.param("cmd", "sendroomfile");
                quest.param("rid", xid);
                break;
        }

        sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                String token = "" ,endpoint = "";
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    token = answer.wantString("token");
                    endpoint = answer.wantString("endpoint");
                }
                callback.onResult(token, endpoint, errorCode);
            }
        }, timeout);
    }

    private int fileToken(StringBuilder token, StringBuilder endpoint, fileTokenType tokenType, long xid) {
        return fileToken(token, endpoint, tokenType, xid, 0);
    }

    private int fileToken(StringBuilder token, StringBuilder endpoint, fileTokenType tokenType, long xid, int timeout) {
        Quest quest = new Quest("filetoken");
        switch (tokenType) {
            case P2P:
                quest.param("cmd", "sendfile");
                quest.param("to", xid);
                break;

            case Group:
                quest.param("cmd", "sendgroupfile");
                quest.param("gid", xid);
                break;

            case Room:
                quest.param("cmd", "sendroomfile");
                quest.param("rid", xid);
                break;
        }

        Answer answer = sendQuest(quest, timeout);
        int code = 0;
        if (answer == null)
            code = ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value();
        if (code == ErrorCode.FPNN_EC_OK.value()) {
            token.append(answer.wantString("token"));
            endpoint.append(answer.wantString("endpoint"));
        }
        return code;
    }

    //===========================[ File Utilies ]=========================//
    private void updateTimeout(TimeOutStruct refTime) {
        long currMsec = RTMUtils.getCurrentMilliseconds();

        refTime.timeout -= (int) ((currMsec - refTime.lastActionTimestamp) / 1000);

        refTime.lastActionTimestamp = currMsec;
    }

    private String extraFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx == -1)
            return null;

        return filename.substring(idx + 1);
    }

    private String buildFileAttrs(SendFileInfo info) {
        String fileMD5 = MD5Utils.getMd5(info.fileContent, false);
        String sign = MD5Utils.getMd5(fileMD5 + ":" + info.token, false);

        StringBuilder sb = new StringBuilder();
        sb.append("{\"sign\":\"");
        sb.append(sign);
        sb.append("\"");

        if (info.filename != null && info.filename.length() > 0) {
            sb.append(",\"filename\":\"");
            sb.append(info.filename);
            sb.append("\"");

            if (info.fileExtension == null || info.fileExtension.length() == 0) {
                info.fileExtension = extraFileExtension(info.filename);
            }
        }

        if (info.fileExtension != null && info.fileExtension.length() > 0) {
            sb.append(",\"ext\":\"");
            sb.append(info.fileExtension);
            sb.append("\"");
        }

        sb.append("}");

        return sb.toString();
    }

    private Quest buildSendFileQuest(SendFileInfo info) {
        Quest quest = null;
        switch (info.actionType) {
            case P2P:
                quest = new Quest("sendfile");
                quest.param("to", info.xid);
                break;

            case Group:
                quest = new Quest("sendgroupfile");
                quest.param("gid", info.xid);
                break;

            case Room:
                quest = new Quest("sendroomfile");
                quest.param("rid", info.xid);
                break;
        }

        quest.param("pid", getPid());
        quest.param("from", getUid());
        quest.param("token", info.token);
        quest.param("mtype", info.mtype);
        quest.param("mid", RTMUtils.genMid());

        quest.param("file", info.fileContent);
        quest.param("attrs", buildFileAttrs(info));

        return quest;
    }

    private int sendFileWithClient(final SendFileInfo info, final TCPClient client) {
        TimeOutStruct mytime = new TimeOutStruct(info.remainTimeout, info.lastActionTimestamp);
        updateTimeout(mytime);

        info.remainTimeout = mytime.timeout;
        info.lastActionTimestamp = mytime.lastActionTimestamp;

        if (info.remainTimeout <= 0)
            return ErrorCode.FPNN_EC_CORE_TIMEOUT.value();

        final Quest quest = buildSendFileQuest(info);

        client.sendQuest(quest, new FunctionalAnswerCallback() {
            @Override
            public void onAnswer(Answer answer, int errorCode) {
                if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
                    try {
                        long mtime = answer.wantLong("mtime");
                        info.callback.onResult(mtime, quest.wantLong("mid"),genRTMAnswer(ErrorCode.FPNN_EC_OK.value()));

                        activeFileGateClient(info.endpoint, client);
                        return;
                    } catch (Exception e) {
                        errorCode = ErrorCode.FPNN_EC_CORE_INVALID_PACKAGE.value();
                    }
                }
                info.callback.onResult(0L, 0L,genRTMAnswer(answer,errorCode));
            }
        }, info.remainTimeout);

        return ErrorCode.FPNN_EC_OK.value();
    }

    private int sendFileWithoutClient(final SendFileInfo info){
        String fileGateEndpoint;
        fileGateEndpoint = info.endpoint;

        final TCPClient client = TCPClient.create(fileGateEndpoint, true);
        client.setQuestTimeout(RTMConfig.globalQuestTimeoutSeconds);
        client.connectTimeout = RTMConfig.globalConnectTimeoutSeconds;
        if (errorRecorder != null)
            client.SetErrorRecorder(errorRecorder);

        client.setConnectedCallback(new ConnectionConnectedCallback() {
            @Override
            public void connectResult(InetSocketAddress peerAddress, int connectionId, boolean connected) {
                int errorCode;
                if (connected) {
                    activeFileGateClient(info.endpoint, client);
                    errorCode = sendFileWithClient(info, client);
                }
                else
                    errorCode = ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value();

                if (errorCode != ErrorCode.FPNN_EC_OK.value())
                    info.callback.onResult(0L, 0L,genRTMAnswer(errorCode));
            }
        });
        try {
            client.connect(false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ErrorCode.FPNN_EC_CORE_UNKNOWN_ERROR.value();
        }

        return ErrorCode.FPNN_EC_OK.value();
    }

    private void getFileTokenCallback(SendFileInfo info, String token, String endpoint, int errorCode) throws InterruptedException {
        if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
            info.token = token;
            info.endpoint = endpoint;
            int  err;

            TCPClient fileClient = fecthFileGateClient(info.endpoint);
            if (fileClient != null)
                err = sendFileWithClient(info, fileClient);
            else
                err = sendFileWithoutClient(info);

            if (err == ErrorCode.FPNN_EC_OK.value())
                return;
            else
                ErrorRecorder.getInstance().recordError("send file error");
        } else
            info.callback.onResult(0L,0L,genRTMAnswer(errorCode));
    }

    //===========================[ Real Send File ]=========================//
    private void realSendFile(final UserInterface.IRTMDoubleValueCallback<Long,Long> callback, fileTokenType tokenType, long targetId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout) {
        if (mtype < MessageType.IMAGEFILE || mtype > MessageType.NORMALFILE) {
            callback.onResult(0L,0L,genRTMAnswer(RTMErrorCode.RTM_EC_INVALID_MTYPE.value()));
            return ;
        }

        SendFileInfo info = new SendFileInfo();
        info.actionType = tokenType;
        info.xid = targetId;
        info.mtype = mtype;
        info.fileContent = fileContent;
        info.filename = filename;
        info.fileExtension = fileExtension;
        info.remainTimeout = timeout;
        info.lastActionTimestamp = RTMUtils.getCurrentMilliseconds();
        info.callback = callback;
        final SendFileInfo inFile = info;
        fileToken(new DoubleStringCallback() {
            @Override
            public void onResult(String token, String endpoint, int errorCode) {
                try {
                    getFileTokenCallback(inFile, token, endpoint, errorCode);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }, tokenType, info.xid, timeout);
    }

    private int realSendFile(fileTokenType tokenType, long targetId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout, StringBuilder mtime){
        //----------[ 1. check mtype ]---------------//
        if (mtype < MessageType.IMAGEFILE || mtype > MessageType.NORMALFILE) {
            return RTMErrorCode.RTM_EC_INVALID_MTYPE.value();
        }

        //----------[ 2. Get File Token ]---------------//
        long lastActionTimestamp = RTMUtils.getCurrentMilliseconds();

        StringBuilder token = new StringBuilder();
        StringBuilder endpoint = new StringBuilder();
        int errorCode = fileToken(token, endpoint, tokenType, targetId, timeout);
        if (errorCode != ErrorCode.FPNN_EC_OK.value())
            return errorCode;
        String realEndpoint = endpoint.toString();

        //----------[ 2.1 check timeout ]---------------//

        TimeOutStruct mytime = new TimeOutStruct(timeout, lastActionTimestamp);
        updateTimeout(mytime);
        if (mytime.timeout <= 0)
            return ErrorCode.FPNN_EC_CORE_TIMEOUT.value();

        //----------[ 3. fetch file gate client ]---------------//
        try {
            TCPClient fileClient = fecthFileGateClient(realEndpoint);
            if (fileClient == null) {
                fileClient = TCPClient.create(realEndpoint);
                fileClient.setQuestTimeout(RTMConfig.globalQuestTimeoutSeconds);
                fileClient.connectTimeout = RTMConfig.globalConnectTimeoutSeconds;

                if (fileClient.connect(true)) {
                    activeFileGateClient(realEndpoint, fileClient);
                } else {
                    //----------[ 3.1 check timeout ]---------------//
                    return ErrorCode.FPNN_EC_CORE_INVALID_CONNECTION.value();
                }
            }

            //----------[ 3.2 check timeout ]---------------//

            updateTimeout(mytime);
            if (mytime.timeout <= 0)
                return ErrorCode.FPNN_EC_CORE_TIMEOUT.value();

            //----------[ 4. build quest ]---------------//
            SendFileInfo info = new SendFileInfo();
            info.actionType = tokenType;
            info.xid = targetId;
            info.mtype = mtype;
            info.fileContent = fileContent;
            info.filename = filename;
            info.fileExtension = fileExtension;
            info.token = token.toString();


            Quest quest = buildSendFileQuest(info);
            Answer answer = fileClient.sendQuest(quest, timeout);

            if (answer.isErrorAnswer())
                return answer.getErrorCode();

            activeFileGateClient(realEndpoint, fileClient);

            mtime.append(answer.wantLong("mtime"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ErrorCode.FPNN_EC_PROTO_UNKNOWN_ERROR.value();
        }
        return ErrorCode.FPNN_EC_OK.value();
    }

    /**
     * 发送p2p文件 async
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public void sendFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long peerUid, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout) {
        realSendFile(callback, fileTokenType.P2P, peerUid, mtype, fileContent, filename, fileExtension, timeout);
    }

    /**
     * 发送p2p文件 sync
     * @param peerUid   目标uid(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public ModifyTimeStruct sendFile(long peerUid, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout){
        StringBuilder mtim = new StringBuilder();
        int errcode = realSendFile(fileTokenType.P2P, peerUid, mtype, fileContent, filename, fileExtension, timeout, mtim);
        ModifyTimeStruct ret = new ModifyTimeStruct();
        ret.errorCode = errcode;
        ret.errorMsg = "";
        ret.modifyTime = Long.parseLong(mtim.toString());
        return ret;
    }

    /**
     * 发送群组文件 async
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public void  sendGroupFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout) {
        realSendFile(callback, fileTokenType.Group, groupId, mtype, fileContent, filename, fileExtension, timeout);
    }

    /**
     * 发送群组文件 sync
     * @param groupId   群组id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public ModifyTimeStruct sendGroupFile(long groupId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout){
        StringBuilder mtim = new StringBuilder();
        int errcode = realSendFile(fileTokenType.Group, groupId, mtype, fileContent, filename, fileExtension, timeout, mtim);
        ModifyTimeStruct ret = new ModifyTimeStruct();
        ret.errorCode = errcode;
        ret.errorMsg = "";
        ret.modifyTime = Long.parseLong(mtim.toString());
        return ret;
    }

    /**
     * 发送房间文件 async
     * @param callback  IRTMCallback<Long>接口回调(NoNull)
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public void  sendRoomFile(UserInterface.IRTMDoubleValueCallback<Long,Long> callback, long roomId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout) {
        realSendFile(callback, fileTokenType.Room, roomId, mtype, fileContent, filename, fileExtension, timeout);
    }

    /**
     * 发送房间文件 sync
     * @param roomId   房间id(NoNull)
     * @param mtype     消息类型(NoNull)
     * @param fileContent   文件内容(NoNull)
     * @param filename      文件名字(NoNull)
     * @param fileExtension 文件属性信息
     * @param timeout       超时时间(秒)
     */
    public ModifyTimeStruct sendRoomFile(long roomId, byte mtype, byte[] fileContent, String filename, String fileExtension, int timeout){
        StringBuilder mtim = new StringBuilder();
        int errcode = realSendFile(fileTokenType.Group, roomId, mtype, fileContent, filename, fileExtension, timeout, mtim);
        ModifyTimeStruct ret = new ModifyTimeStruct();
        ret.errorCode = errcode;
        ret.errorMsg = "";
        ret.modifyTime = Long.parseLong(mtim.toString());
        return ret;
    }
}

