package com.rtmsdk;

import com.rtmsdk.RTMStruct.RTMMessage;

public class RTMPushProcessor
{
    //链接断开 (如果设置重连 会自动连接)
    void sessionClosed(int ClosedByErrorCode){};        //-- com.fpnn.ErrorCode & com.fpnn.rtm.ErrorCode

    //被踢
    void kickout(){};

    //踢出房间
    void kickoutRoom(long roomId){};

    //push聊天消息(具体消息内容为 RTMMessage 中的translatedInfo)
    void pushChat(RTMStruct.RTMMessage pushMsg){};
    void pushGroupChat(RTMStruct.RTMMessage pushMsg){};
    void pushRoomChat(RTMStruct.RTMMessage pushMsg){};
    void pushBroadcastChat(RTMStruct.RTMMessage pushMsg){};


    //push命令消息(具体消息内容为 RTMMessage 中的stringMessage)
    void pushCmd(RTMStruct.RTMMessage pushMsg){};
    void pushGroupCmd(RTMStruct.RTMMessage pushMsg){};
    void pushRoomCmd(RTMStruct.RTMMessage pushMsg){};
    void pushBroadcastCmd(RTMStruct.RTMMessage pushMsg){};

    //push语音消息(具体消息内容为 RTMMessage 中的audioInfo 具体语音的内容需要通过getp2pmsg/group/room系列函数获取)
    void pushAudio(RTMStruct.RTMMessage pushMsg){};
    void pushGroupAudio(RTMStruct.RTMMessage pushMsg){};
    void pushRoomAudio(RTMStruct.RTMMessage pushMsg){};
    void pushBroadcastAudio(RTMStruct.RTMMessage pushMsg){};

    //pushmsg消息 (具体消息内容 根据业务自己的messagetype判断 如果为string类型消息RTMMessage中的stringMessage 不为空 反之 binaryMessage不为空)
    void pushMessage(RTMStruct.RTMMessage pushMsg){};
    void pushGroupMessage(RTMStruct.RTMMessage pushMsg){};
    void pushRoomMessage(RTMStruct.RTMMessage pushMsg){};
    void pushBroadcastMessage(RTMStruct.RTMMessage pushMsg){};

    //pushmsg消息 (RTMMessage 中的stringMessage 不为空 内容为aws s3存放文件的url地址)
    void pushFile(RTMStruct.RTMMessage pushMsg){};
    void pushGroupFile(RTMStruct.RTMMessage pushMsg){};
    void pushRoomFile(RTMStruct.RTMMessage pushMsg){};
    void pushBroadcastFile(RTMStruct.RTMMessage pushMsg){};
}
