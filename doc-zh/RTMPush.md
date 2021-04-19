~~~ c++
     //如果有耗时操作 需要用户单开线程处理业务逻辑 以免阻塞后续的请求
    public class RTMPushProcessor
    {
         /**
         * RTM链接断开 (默认会自动连接 kickout除外)(备注:链接断开会自动退出之前进入的房间,需要在重连成功再次加入房间)
         */
        public void rtmConnectClose(long uid){}
    
        /**
         * RTM重连开始接口 每次重连都会判断reloginWillStart返回值 若返回false则中断重连
         * 参数说明 uid-用户id  answer-上次重连的结果  reloginCount-将要重连的次数
         * 备注:需要用户设定一些条件 比如重连间隔 最大重连次数
         */
        public boolean reloginWillStart(long uid, RTMStruct.RTMAnswer answer, int reloginCount){return true;};
    
        /**
         * RTM重连完成(如果 successful 为false表示最终重连失败,answer会有详细的错误码和错和错误信息 为true表示重连成功)
         * 备注:当用户的token过期或被加入黑名单 重连会直接返回 不会继续判断reloginWillStart
         */
        public void reloginCompleted(long uid, boolean successful, RTMStruct.RTMAnswer answer, int reloginCount){};
    
    
        /**
         * 被服务器踢下线(不会自动重连)
         */
        public void kickout(){}
    
        /**
         * 被踢出房间
         */
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
~~~