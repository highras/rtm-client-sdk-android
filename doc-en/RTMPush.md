~~~ c++
    //connection closed (if user set autoconnect then wiil be reconnect soon)
    public void sessionClosed(int ClosedByErrorCode){};

    //be kicked out from server 
    public void kickout(){};

    //be kicked out from room
    public void kickoutRoom(long roomId){};

    //push chat message (real message is in RTMMessage->translatedInfo)
    public void pushChat(RTMMessage msg){};
    public void pushGroupChat(RTMMessage msg){};
    public void pushRoomChat(RTMMessage msg){};
    public void pushBroadcastChat(RTMMessage msg){};


    //pushcmd(real message is in RTMMessage->stringMessage)
    public void pushCmd(RTMMessage msg){};
    public void pushGroupCmd(RTMMessage msg){};
    public void pushRoomCmd(RTMMessage msg){};
    public void pushBroadcastCmd(RTMMessage msg){};

    //pushmsg (r according to messagetype you need just if messaget type is string it's in RTMMessage->stringMessage if it's binary it's in RTMMessage->binaryMessage)
    public void pushMessage(RTMMessage msg){};
    public void pushGroupMessage(RTMMessage msg){};
    public void pushRoomMessage(RTMMessage msg){};
    public void pushBroadcastMessage(RTMMessage msg){};

    //pushfile (real content is in RTMMessage->fileInfo)
    public void pushFile(RTMMessage msg){};
    public void pushGroupFile(RTMMessage msg){};
    public void pushRoomFile(RTMMessage msg){};
    public void pushBroadcastFile(RTMMessage msg){};
}
~~~