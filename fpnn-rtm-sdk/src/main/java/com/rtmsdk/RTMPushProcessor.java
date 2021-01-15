package com.rtmsdk;

import com.rtmsdk.RTMStruct.RTMMessage;
//如果有耗时操作 需要用户单开线程处理业务逻辑 以免阻塞后续的请求
public class RTMPushProcessor
{
    //rtm链接断开 (如果设置重连 会自动连接 kickout除外)(备注:链接断开会自动退出之前进入的房间,需要在重连成功根据业务需求再次加入房间)
    public void rtmConnectClose(int ClosedByErrorCode){}

    //被服务器踢下线(不会自动重连)
    public void kickout(){}

    //被踢出房间
    public void kickoutRoom(long roomId){}

    //push聊天消息(具体消息内容为 RTMMessage 中的translatedInfo)
    public void pushChat(RTMMessage msg){}
    public void pushGroupChat(RTMMessage msg){}
    public void pushRoomChat(RTMMessage msg){}
    public void pushBroadcastChat(RTMMessage msg){}


    //pushcmd命令消息(具体消息内容为 RTMMessage 中的stringMessage)
    public void pushCmd(RTMMessage msg){}
    public void pushGroupCmd(RTMMessage msg){}
    public void pushRoomCmd(RTMMessage msg){}
    public void pushBroadcastCmd(RTMMessage msg){}

    //pushmsg消息 (具体消息内容 根据业务自己的messagetype判断 如果为string类型消息RTMMessage中的stringMessage 不为空 反之 binaryMessage不为空)
    public void pushMessage(RTMMessage msg){}
    public void pushGroupMessage(RTMMessage msg){}
    public void pushRoomMessage(RTMMessage msg){}
    public void pushBroadcastMessage(RTMMessage msg){}

    //pushfile消息 (RTMMessage 中的fileInfo结构)
    public void pushFile(RTMMessage msg){}
    public void pushGroupFile(RTMMessage msg){}
    public void pushRoomFile(RTMMessage msg){}
    public void pushBroadcastFile(RTMMessage msg){}
}
