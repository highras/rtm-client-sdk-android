package com.rtmsdk.test;

import com.rtmsdk.RTMAudio;
import com.rtmsdk.RTMPushProcessor;
import com.rtmsdk.RTMStruct.RTMAnswer;
import com.rtmsdk.RTMStruct.RTMMessage;

import java.util.concurrent.atomic.AtomicInteger;


public class RTMExampleQuestProcessor extends RTMPushProcessor {
    private Object interlock;
    long uid;
    AtomicInteger allCount = new AtomicInteger();

    AtomicInteger roomChatCount = new AtomicInteger();
    AtomicInteger roomCmdCount = new AtomicInteger();
    AtomicInteger roomMessageCount = new AtomicInteger();
    AtomicInteger roomFileCount = new AtomicInteger();
    AtomicInteger groupChatCount = new AtomicInteger();
    AtomicInteger groupCmdCount = new AtomicInteger();
    AtomicInteger groupMessageCount = new AtomicInteger();
    AtomicInteger groupFileCount = new AtomicInteger();
    AtomicInteger p2pChatCount = new AtomicInteger();

    public RTMExampleQuestProcessor(long uid) {
        interlock =  new Object();
        this.uid = uid;
    }


    public boolean reloginWillStart(long uid, RTMAnswer answer, int reloginCount) {
//            if (reloginCount >= 6) {return false;}
        mylog.log(uid + " 开始重连第 " + reloginCount + "次");
//            TestClass.mySleep(1);
        return true;
    }

    public void reloginCompleted(long uid, boolean successful, RTMAnswer answer, int reloginCount) {
        mylog.log(uid + " 重连结束 结果 " + answer.getErrInfo() + " 重连次数 " + reloginCount);
//        if (successful)
//        {
//            TestClass.pushClients.get(101).enterRoom(TestClass.roomId);
//        }
    }

    public void rtmConnectClose(long uid) {
        synchronized (interlock) {
            mylog.log(uid + " rtmconnect closed ");
        }
    }

    public void kickout() {
        MainActivity.hehe.setText("nihao");
        synchronized (interlock) {
            mylog.log("Received kickout.");
        }
    }

    public void kickoutRoom(long roomId) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "Kickout from room " + roomId);
        }
    }

    //-- message for String format
    public void pushMessage(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "receive pushMessage " + message.getInfo());
        }
    }

    public void pushGroupMessage(RTMMessage message) {
            mylog.log("uid " + uid + " " + "receive  pushGroupMessage " + message.getInfo()+ " " + groupMessageCount.incrementAndGet());
    }

    public void pushRoomMessage(RTMMessage message) {
            mylog.log("uid " + uid + " " + "receive  pushRoomMessage " + message.getInfo() + " " + roomMessageCount.incrementAndGet());
    }

    public void pushBroadcastMessage(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "receive  pushBroadcastMessage " + message.getInfo());
        }
    }

    public void pushChat(RTMMessage message) {
//            mylog.log("uid " + uid + " " + "receive  pushChat " + message.getInfo() + " " + p2pChatCount.incrementAndGet());
            mylog.log("uid " + uid + " " + "receive  pushChat " + p2pChatCount.incrementAndGet());
    }

    public void pushGroupChat(RTMMessage message) {
//            mylog.log("uid " + uid + " " + "receive  pushGroupChat " + message.getInfo() + " " + groupChatCount.incrementAndGet());
            mylog.log("uid " + uid + " " + "receive  pushGroupChat " + groupChatCount.incrementAndGet());

    }

    public void pushRoomChat(RTMMessage message) {
        mylog.log("uid " + uid + " " + "receive  pushRoomChat " + message.getInfo() + " " + roomChatCount.incrementAndGet());
    }

    public void pushBroadcastChat(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "receive  pushBroadcastChat " + message.getInfo());
        }
    }

    public void pushBroadcastAudio(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "receive  pushBroadcastAudio " + message.getInfo());
        }
    }

    public void pushCmd(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "receive  pushCmd " + message.getInfo() + " " + p2pChatCount.incrementAndGet());
        }
    }

    public void pushGroupCmd(RTMMessage message) {
            mylog.log("uid " + uid + " " + "receive  pushGroupCmd " + message.getInfo()+ " " + groupCmdCount.incrementAndGet());

    }

    public void pushRoomCmd(RTMMessage message) {
            mylog.log("uid " + uid + " " + "receive  pushRoomCmd " + message.getInfo() + " " + roomCmdCount.incrementAndGet());

    }

    public void pushBroadcastCmd(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " +  "receive  pushBroadcastCmd " + message.getInfo());
        }
    }

    public void pushFile(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "receive  pushFile " + message.getInfo());
            if(message.fileInfo.isRTMaudio)
            {
                byte[] jj = TestClass.httpGetFile(message.fileInfo.url);
                RTMAudio.getInstance().broadAudio(jj);
            }
        }
    }

    public void pushGroupFile(RTMMessage message) {
            mylog.log("uid " + uid + " " + "receive  pushGroupFile " + message.getInfo()+ " " + groupFileCount.incrementAndGet());

    }

    public void pushRoomFile(RTMMessage message) {
            mylog.log("uid " + uid + " " + "receive  pushRoomFile " + message.getInfo() + " " + roomFileCount.incrementAndGet());
    }

    public void pushBroadcastFile(RTMMessage message) {
        synchronized (interlock) {
            mylog.log("uid " + uid + " " + "receive  pushBroadcastFile " + message.getInfo());
        }
    }
}
