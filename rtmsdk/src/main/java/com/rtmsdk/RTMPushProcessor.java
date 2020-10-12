package com.rtmsdk;

import com.rtmsdk.RTMStruct.RTMMessage;
public class RTMPushProcessor
{
    //链接断开 (如果设置重连 会自动连接)
    public void sessionClosed(int ClosedByErrorCode){};        //-- com.fpnn.ErrorCode & com.fpnn.rtm.ErrorCode

    //被踢
    public void kickout(){};

    //踢出房间
    public void kickoutRoom(long roomId){};

    //push聊天消息(具体消息内容为 RTMMessage 中的translatedInfo)
    public void pushChat(RTMMessage msg){};
    public void pushGroupChat(RTMMessage msg){};
    public void pushRoomChat(RTMMessage msg){};
    public void pushBroadcastChat(RTMMessage msg){};


    //push命令消息(具体消息内容为 RTMMessage 中的stringMessage)
    public void pushCmd(RTMMessage msg){};
    public void pushGroupCmd(RTMMessage msg){};
    public void pushRoomCmd(RTMMessage msg){};
    public void pushBroadcastCmd(RTMMessage msg){};

    //push语音消息(具体消息内容为 RTMMessage 中的audioInfo 具体语音的内容需要通过getp2pmsg/group/room系列函数获取)
    public void pushAudio(RTMMessage msg){};
    public void pushGroupAudio(RTMMessage msg){};
    public void pushRoomAudio(RTMMessage msg){};
    public void pushBroadcastAudio(RTMMessage msg){};

    //pushmsg消息 (具体消息内容 根据业务自己的messagetype判断 如果为string类型消息RTMMessage中的stringMessage 不为空 反之 binaryMessage不为空)
    public void pushMessage(RTMMessage msg){};
    public void pushGroupMessage(RTMMessage msg){};
    public void pushRoomMessage(RTMMessage msg){};
    public void pushBroadcastMessage(RTMMessage msg){};

    //pushmsg消息 (RTMMessage 中的stringMessage 不为空 内容为aws s3存放文件的url地址)
    public void pushFile(RTMMessage msg){};
    public void pushGroupFile(RTMMessage msg){};
    public void pushRoomFile(RTMMessage msg){};
    public void pushBroadcastFile(RTMMessage msg){};
}
