package com.rtm.test;

import com.rtmsdk.IRTMQuestProcessor;
import com.rtmsdk.RTMAudio;
import com.rtmsdk.RTMStruct.*;

public class RTMExampleQuestProcessor implements IRTMQuestProcessor {
    private Object interlock;
    private RTMAudio audioTest = new RTMAudio();

    public RTMExampleQuestProcessor() {
        interlock =  new Object();
    }

    public void sessionClosed(int ClosedByErrorCode) {
        synchronized (interlock) {
            mylog.log("Session closed by error code: " + ClosedByErrorCode);
        }
    }

    public void kickout() {
        synchronized (interlock) {
            mylog.log("Received kickout.");
        }
    }

    public void kickoutRoom(long roomId) {
        synchronized (interlock) {
            mylog.log("Kickout from room " + roomId);
        }
    }

    //-- message for String format
    public void pushMessage(long fromUid, long toUid, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushMessage: from %d, type: %d, mid: %d, attrs: %s, message: %s", fromUid, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushGroupMessage(long fromUid, long groupId, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushGroupMessage: from %d, in group: %d, type: %d, mid: %d, attrs: %s, message: %s", fromUid, groupId, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushRoomMessage(long fromUid, long roomId, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushRoomMessage: from %d, in group: %d, type: %d, mid: %d, attrs: %s, message: %s", fromUid, roomId, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushBroadcastMessage(long fromUid, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushBroadcastMessage: from %d, type: %d, mid: %d, attrs: %s, message: %s", fromUid, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }

    //-- message for binary format
    public void pushMessage(long fromUid, long toUid, byte mtype, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive binary pushMessage: from %d, type: %d, mid: %d, attrs: %s, message length: %d", fromUid, mtype, mid, attrs, message.length);
            mylog.log(msg);
        }
    }

    public void pushGroupMessage(long fromUid, long groupId, byte mtype, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive binary pushGroupMessage: from %d, groupid:%d type: %d, mid: %d, attrs: %s, message length: %d", fromUid, groupId, mid, attrs, message.length);
            mylog.log(msg);
        }
    }

    public void pushRoomMessage(long fromUid, long roomId, byte mtype, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive binary pushRoomMessage: from %d, groupid:%d type: %d, mid: %d, attrs: %s, message length: %d", fromUid, roomId, mid, attrs, message.length);
            mylog.log(msg);
        }
    }

    public void pushBroadcastMessage(long fromUid, byte mtype, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive binary pushBroadcastMessage: from %d, type: %d, mid: %d, attrs: %s, message: %s", fromUid, mtype, mid, attrs, message.length);
            mylog.log(msg);
        }
    }

    public void pushChat(long fromUid, long toUid, long mid, TranslatedMessage message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive translated pushChat: from %d, mid: %d, attrs: %s, srcLang:%s, srcMsg:%s toLang:%s, toMsg:%s", fromUid, mid, attrs, message.source, message.sourceText, message.target, message.targetText);
            mylog.log(msg);
        }
    }

    public void pushGroupChat(long fromUid, long groupId, long mid, TranslatedMessage message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive  translated pushGroupChat: from %d, inGroupId:%d, mid: %d, attrs: %s, srcLang:%s, srcMsg:%s toLang:%s, toMsg:%s", fromUid, groupId, mid, attrs, message.source, message.sourceText, message.target, message.targetText);
            mylog.log(msg);
        }
    }

    public void pushRoomChat(long fromUid, long roomId, long mid, TranslatedMessage message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive translated pushRoomChat: from %d, inRoomId:%d, mid: %d, attrs: %s, srcLang:%s, srcMsg:%s toLang:%s, toMsg:%s", fromUid, roomId, mid, attrs, message.source, message.sourceText, message.target, message.targetText);
            mylog.log(msg);
        }
    }

    public void pushBroadcastChat(long fromUid, long mid, TranslatedMessage message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive translated pushBroadcastChat: from %d, mid: %d, attrs: %s, srcLang:%s, srcMsg:%s toLang:%s, toMsg:%s", fromUid, mid, attrs, message.source, message.sourceText, message.target, message.targetText);
            mylog.log(msg);
        }
    }

    public void pushAudio(long fromUid, long toUid, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushAudio: from %d, mid: %d, attrs: %s, message length: %d", fromUid, mid, attrs, message.length);
            mylog.log(msg);
            audioTest.broadAduio(message);
        }
    }

    public void pushGroupAudio(long fromUid, long groupId, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushGroupAudio: from %d, groupId:%d, mid: %d, attrs: %s, message length: %d", fromUid, groupId, mid, attrs, message.length);
            mylog.log(msg);
        }
    }

    public void pushRoomAudio(long fromUid, long roomId, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushRoomAudio: from %d, groupId:%d, mid: %d, attrs: %s, message length: %d", fromUid, roomId, mid, attrs, message.length);
            mylog.log(msg);
        }
    }

    public void pushBroadcastAudio(long fromUid, long mid, byte[] message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushBroadcastAudio: from %d, mid: %d, attrs: %s, message length: %d", fromUid, mid, attrs, message.length);
            mylog.log(msg);
        }
    }

    public void pushCmd(long fromUid, long toUid, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushCmd: from %d, mid: %d, attrs: %s, message: %s", fromUid, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushGroupCmd(long fromUid, long groupId, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushGroupCmd: from %d, groupId:%d， mid: %d, attrs: %s, message: %s", fromUid, groupId, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushRoomCmd(long fromUid, long roomId, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushRoomCmd: from %d, groupId:%d， mid: %d, attrs: %s, message: %s", fromUid, roomId, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushBroadcastCmd(long fromUid, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushBroadcastCmd: from %d, mid: %d, attrs: %s, message: %s", fromUid, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushFile(long fromUid, long toUid, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushFile: from %d, mtype：%d, mid: %d, attrs: %s, message: %s", fromUid, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushGroupFile(long fromUid, long groupId, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushGroupFile: from %d, groupId:%d, mtype：%d, mid: %d, attrs: %s, message: %s", fromUid, groupId, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushRoomFile(long fromUid, long roomId, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushRoomFile: from %d, groupId:%d, mtype：%d, mid: %d, attrs: %s, message: %s", fromUid, roomId, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }

    public void pushBroadcastFile(long fromUid, byte mtype, long mid, String message, String attrs, long mtime) {
        synchronized (interlock) {
            String msg = String.format("Receive pushBroadcastFile: from %d, mtype：%d, mid: %d, attrs: %s, message: %s", fromUid, mtype, mid, attrs, message);
            mylog.log(msg);
        }
    }
}
