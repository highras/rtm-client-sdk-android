package com.rtmsdk;
import com.rtmsdk.RTMStruct.RTMMessage;

public interface IRTMQuestProcessor
{
    //链接断开 (如果设置重连 会自动连接)
    void sessionClosed(int ClosedByErrorCode);        //-- com.fpnn.ErrorCode & com.fpnn.rtm.ErrorCode

    //被踢
    void kickout();

    //踢出房间
    void kickoutRoom(long roomId);

    //push聊天消息(具体消息内容为 RTMMessage 中的translatedInfo)
    void pushChat(RTMMessage pushMsg);
    void pushGroupChat(RTMMessage pushMsg);
    void pushRoomChat(RTMMessage pushMsg);
    void pushBroadcastChat(RTMMessage pushMsg);


    //push命令消息(具体消息内容为 RTMMessage 中的stringMessage)
    void pushCmd(RTMMessage pushMsg);
    void pushGroupCmd(RTMMessage pushMsg);
    void pushRoomCmd(RTMMessage pushMsg);
    void pushBroadcastCmd(RTMMessage pushMsg);

    //push语音消息(具体消息内容为 RTMMessage 中的audioInfo 具体语音的内容需要通过getp2pmsg/group/room系列函数获取)
    void pushAudio(RTMMessage pushMsg);
    void pushGroupAudio(RTMMessage pushMsg);
    void pushRoomAudio(RTMMessage pushMsg);
    void pushBroadcastAudio(RTMMessage pushMsg);

    //pushmsg消息 (具体消息内容 根据业务自己的messagetype判断 如果为string类型消息RTMMessage中的stringMessage 不为空 反之 binaryMessage不为空)
    void pushMessage(RTMMessage pushMsg);
    void pushGroupMessage(RTMMessage pushMsg);
    void pushRoomMessage(RTMMessage pushMsg);
    void pushBroadcastMessage(RTMMessage pushMsg);

    //pushmsg消息 (RTMMessage 中的stringMessage 不为空 内容为aws s3存放文件的url地址)
    void pushFile(RTMMessage pushMsg);
    void pushGroupFile(RTMMessage pushMsg);
    void pushRoomFile(RTMMessage pushMsg);
    void pushBroadcastFile(RTMMessage pushMsg);
}
