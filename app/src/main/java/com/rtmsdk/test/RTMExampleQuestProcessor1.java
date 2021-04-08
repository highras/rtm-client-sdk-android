package com.rtmsdk.test;

import com.rtmsdk.RTMAudio;
import com.rtmsdk.RTMPushProcessor;
import com.rtmsdk.RTMStruct.RTMAnswer;
import com.rtmsdk.RTMStruct.RTMMessage;

import java.util.concurrent.atomic.AtomicInteger;


public class RTMExampleQuestProcessor1 extends RTMPushProcessor {
    private Object interlock;
    AtomicInteger allCount = new AtomicInteger();

    AtomicInteger roomChatCount = new AtomicInteger();
    AtomicInteger roomCmdCount = new AtomicInteger();
    AtomicInteger roomMessageCount = new AtomicInteger();
    AtomicInteger roomFileCount = new AtomicInteger();
    AtomicInteger groupChatCount = new AtomicInteger();
    AtomicInteger groupCmdCount = new AtomicInteger();
    AtomicInteger groupMessageCount = new AtomicInteger();
    AtomicInteger groupFileCount = new AtomicInteger();

    public RTMExampleQuestProcessor1() {
        interlock =  new Object();
    }


    public boolean reloginWillStart(long uid, RTMAnswer answer, int reloginCount) {
        if (reloginCount >= 6) {return false;}
        mylog.log1(uid + " 开始重连第 " + reloginCount + "次" + " answer " + answer.getErrInfo());
        return true;
//            TestClass.mySleep(1);
    }

    public void reloginCompleted(long uid, boolean successful, RTMAnswer answer, int reloginCount) {
        mylog.log1(uid + " 重连结束 结果 " + answer.getErrInfo() + " 重连次数 " + reloginCount);
//        if (successful)
//        {
//            TestClass.pushClients.get(101).enterRoom(TestClass.roomId);
//        }
    }

    public void rtmConnectClose(long uid) {
        synchronized (interlock) {
            mylog.log1(uid + " rtmconnect closed ");
        }
    }

    public void kickout() {
        synchronized (interlock) {
            mylog.log1("Received kickout.");
        }
    }

    public void kickoutRoom(long roomId) {
        synchronized (interlock) {
            mylog.log1("Kickout from room " + roomId);
        }
    }

    //-- message for String format
    public void pushMessage(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive pushMessage " + message.getInfo());
/*            getMessageSync("pushMessage", 0,message.fromUid,message.toId,message.messageId);
            getMessageAsync("pushMessage", 0,message.fromUid,message.toId,message.messageId);*/
        }
    }

    public void pushGroupMessage(RTMMessage message) {
        mylog.log1("receive  pushGroupMessage " + message.getInfo()+ " " + groupMessageCount.incrementAndGet());
/*            getMessageSync("pushGroupMessage", 1,message.fromUid,message.toId,message.messageId);
            getMessageAsync("pushGroupMessage", 1,message.fromUid,message.toId,message.messageId);*/

    }

    public void pushRoomMessage(RTMMessage message) {
        mylog.log1("receive  pushRoomMessage " + message.getInfo() + " " + roomMessageCount.incrementAndGet());
//            getMessageSync("pushRoomMessage", 2,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushRoomMessage", 2,message.fromUid,message.toId,message.messageId);

    }

    public void pushBroadcastMessage(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive  pushBroadcastMessage " + message.getInfo());
//            getMessageSync("pushBroadcastMessage", 3,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushBroadcastMessage", 3,message.fromUid,message.toId,message.messageId);
        }
    }

    public void pushChat(RTMMessage message) {
        mylog.log1("receive  pushChat time " + System.currentTimeMillis()+ " info "+ message.getInfo());
//            RTMAnswer ll = TestClass.client.deleteP2PMessage(message.fromUid,message.toId,message.messageId);
//            mylog.log1(ll.getErrInfo());
//            getMessageSync("pushChat", 0,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushChat", 0,message.fromUid,message.toId,message.messageId);
    }

    public void pushGroupChat(RTMMessage message) {
        mylog.log1("receive  pushGroupChat " + message.getInfo() + " " + groupChatCount.incrementAndGet());
//            getMessageSync("pushGroupChat", 1,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushGroupChat", 1,message.fromUid,message.toId,message.messageId);

    }

    public void pushRoomChat(RTMMessage message) {
        mylog.log1("receive  pushRoomChat " + message.getInfo() + " " + roomChatCount.incrementAndGet());
//            getMessageSync("pushRoomChat", 2,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushRoomChat", 2,message.fromUid,message.toId,message.messageId);

    }

    public void pushBroadcastChat(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive  pushBroadcastChat " + message.getInfo());
//            getMessageSync("pushBroadcastChat", 3,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushBroadcastChat", 3,message.fromUid,message.toId,message.messageId);
        }
    }

    public void pushBroadcastAudio(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive  pushBroadcastAudio " + message.getInfo());
//            getMessageSync("pushBroadcastAudio", 3,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushBroadcastAudio", 3,message.fromUid,message.toId,message.messageId);
        }
    }

    public void pushCmd(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive  pushCmd " + message.getInfo());
//            getMessageSync("pushCmd", 0,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushCmd", 0,message.fromUid,message.toId,message.messageId);
        }
    }

    public void pushGroupCmd(RTMMessage message) {
        mylog.log1("receive  pushGroupCmd " + message.getInfo()+ " " + groupCmdCount.incrementAndGet());
//            getMessageSync("pushGroupCmd", 1,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushGroupCmd", 1,message.fromUid,message.toId,message.messageId);

    }

    public void pushRoomCmd(RTMMessage message) {
        mylog.log1("receive  pushRoomCmd " + message.getInfo() + " " + roomCmdCount.incrementAndGet());
//            getMessageSync("pushRoomCmd", 2,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushRoomCmd", 2,message.fromUid,message.toId,message.messageId);

    }

    public void pushBroadcastCmd(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive  pushBroadcastCmd " + message.getInfo());
//            getMessageSync("pushBroadcastCmd", 3,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushBroadcastCmd", 3,message.fromUid,message.toId,message.messageId);
        }
    }

    public void pushFile(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive  pushFile " + message.getInfo());
            if(message.fileInfo.isRTMaudio)
            {
                byte[] jj = TestClass.httpGetFile(message.fileInfo.url);
                RTMAudio.getInstance().broadAudio(jj);
            }
//            getMessageSync("pushFile", 0,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushFile", 0,message.fromUid,message.toId,message.messageId);
        }
    }

    public void pushGroupFile(RTMMessage message) {
        mylog.log1("receive  pushGroupFile " + message.getInfo()+ " " + groupFileCount.incrementAndGet());
//            if(message.fileInfo.isRTMaudio)
//            {
//                byte[] jj = TestClass.httpGetFile(message.fileInfo.url);
//                RTMAudio.getInstance().broadAduio(jj);
//            }
//            getMessageSync("pushGroupFile", 1,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushGroupFile", 1,message.fromUid,message.toId,message.messageId);

    }

    public void pushRoomFile(RTMMessage message) {
        mylog.log1("receive  pushRoomFile " + message.getInfo() + " " + roomFileCount.incrementAndGet());
//            if(message.fileInfo.isRTMaudio)
//            {
//                byte[] jj = TestClass.httpGetFile(message.fileInfo.url);
//                RTMAudio.getInstance().broadAduio(jj);
//            }
//            getMessageSync("pushRoomFile", 2,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushRoomFile", 2,message.fromUid,message.toId,message.messageId);

    }

    public void pushBroadcastFile(RTMMessage message) {
        synchronized (interlock) {
            mylog.log1("receive  pushBroadcastFile " + message.getInfo());
//            getMessageSync("pushBroadcastFile", 3,message.fromUid,message.toId,message.messageId);
//            getMessageAsync("pushBroadcastFile", 3,message.fromUid,message.toId,message.messageId);
        }
    }
}
