package com.rtm.test;

import com.rtmsdk.IRTMQuestProcessor;
import com.rtmsdk.RTMAudio;
import com.rtmsdk.RTMStruct.*;

public class RTMExampleQuestProcessor implements IRTMQuestProcessor {
    private Object interlock;

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
    public void pushMessage(RTMMessage message) {
        synchronized (interlock) {
            String msg = "";
            if (message.binaryMessage != null)
                msg = String.format("Receive pushMessage: from %d, type: %d, mid: %d, attrs: %s, messageLength: %s", message.fromUid, message.messageType, message.messageId, message.attrs, message.binaryMessage.length);
            else
                msg = String.format("Receive pushMessage: from %d, type: %d, mid: %d, attrs: %s, message: %s", message.fromUid, message.messageType, message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushGroupMessage(RTMMessage message) {
        synchronized (interlock) {
            String msg = "";
            if (message.binaryMessage != null)
                msg = String.format("Receive pushGroupMessage: from %d, in group: %d, type: %d, mid: %d, attrs: %s, messageLength: %s", message.fromUid, message.toId, message.messageType, message.messageId, message.attrs, message.binaryMessage.length);
            else
                msg = String.format("Receive pushGroupMessage: from %d, in group: %d, type: %d, mid: %d, attrs: %s, message: %s", message.fromUid, message.toId, message.messageType, message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushRoomMessage(RTMMessage message) {
        synchronized (interlock) {
            String msg = "";
            if (message.binaryMessage != null)
                msg = String.format("Receive pushRoomMessage: from %d, in room: %d, type: %d, mid: %d, attrs: %s, messageLength: %s", message.fromUid, message.toId, message.messageType, message.messageId, message.attrs, message.binaryMessage.length);
            else
                msg = String.format("Receive pushRoomMessage: from %d, in room: %d, type: %d, mid: %d, attrs: %s, message: %s", message.fromUid, message.toId, message.messageType, message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushBroadcastMessage(RTMMessage message) {
        synchronized (interlock) {
            String msg = "";
            if (message.binaryMessage != null)
                msg = String.format("Receive pushBroadcastMessage: from %d, type: %d, mid: %d, attrs: %s, messageLength: %s", message.fromUid, message.messageType, message.messageId, message.attrs, message.binaryMessage.length);
            else
                msg = String.format("Receive pushBroadcastMessage: from %d, type: %d, mid: %d, attrs: %s, message: %s", message.fromUid, message.messageType, message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushChat(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive  pushChat: from %d, mid: %d, attrs: %s, translateinfo:%s", message.fromUid, message.messageId, message.attrs, message.translatedInfo.toString());
            mylog.log(msg);
        }
    }

    public void pushGroupChat(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive  pushGroupChat: from %d, inGroupId:%d, mid: %d, attrs: %s, translateinfo:%s", message.fromUid, message.toId, message.messageId, message.attrs, message.translatedInfo.toString());
            mylog.log(msg);
        }
    }

    public void pushRoomChat(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive  pushRoomChat: from %d, inRoomId:%d, mid: %d, attrs: %s, translateinfo:%s", message.fromUid, message.toId, message.messageId, message.attrs, message.translatedInfo.toString());
            mylog.log(msg);
        }
    }

    public void pushBroadcastChat(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive  pushBroadcastChat: from %d, mid: %d, attrs: %s, translateinfo:%s", message.fromUid, message.toId, message.messageId, message.attrs, message.translatedInfo.toString());
            mylog.log(msg);
        }
    }

    public void pushAudio(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushAudio: from %d, mid: %d, attrs: %s, audioInfo: %s", message.fromUid, message.messageId, message.attrs, message.audioInfo.duration + message.audioInfo.sourceLanguage + message.audioInfo.recognizedLanguage + message.audioInfo.recognizedText);
            mylog.log(msg);
//            audioTest.broadAduio(message);
        }
    }

    public void pushGroupAudio(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushGroupAudio: from %d,groupId:%d,  mid: %d, attrs: %s, audioInfo: %s", message.fromUid, message.toId, message.messageId, message.attrs, message.audioInfo.duration + message.audioInfo.sourceLanguage + message.audioInfo.recognizedLanguage + message.audioInfo.recognizedText);
            mylog.log(msg);
        }
    }

    public void pushRoomAudio(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushRoomAudio: from %d,groupId:%d,  mid: %d, attrs: %s, audioInfo: %s", message.fromUid, message.toId, message.messageId, message.attrs, message.audioInfo.duration + message.audioInfo.sourceLanguage + message.audioInfo.recognizedLanguage + message.audioInfo.recognizedText);
            mylog.log(msg);
        }
    }

    public void pushBroadcastAudio(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushBroadcastAudio: from %d,mid: %d, attrs: %s, audioInfo: %s", message.fromUid, message.messageId, message.attrs, message.audioInfo.duration + message.audioInfo.sourceLanguage + message.audioInfo.recognizedLanguage + message.audioInfo.recognizedText);
            mylog.log(msg);
        }
    }

    public void pushCmd(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushCmd: from %d, mid: %d, attrs: %s, message: %s", message.fromUid, message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushGroupCmd(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushGroupCmd: from %d, groupId:%d，mid: %d, attrs: %s, message: %s", message.fromUid, message.toId,message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushRoomCmd(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushRoomCmd: from %d, groupId:%d，mid: %d, attrs: %s, message: %s", message.fromUid, message.toId,message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushBroadcastCmd(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushBroadcastCmd: from %d, mid: %d, attrs: %s, message: %s", message.fromUid, message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushFile(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushFile: from %d, mtype：%d, mid: %d, attrs: %s, message: %s", message.fromUid, message.messageType,message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushGroupFile(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushGroupFile: from %d, groupId:%d，mtype：%d, mid:%d, attrs: %s, message: %s", message.fromUid, message.toId,message.messageType,message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushRoomFile(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushRoomFile: from %d, groupId:%d，mtype：%d,mid: %d, attrs: %s, message: %s", message.fromUid, message.toId,message.messageType,message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }

    public void pushBroadcastFile(RTMMessage message) {
        synchronized (interlock) {
            String msg = String.format("Receive pushBroadcastFile: from %d, mtype：%d, mid: %d, attrs: %s, message: %s", message.fromUid, message.messageType , message.messageId, message.attrs, message.stringMessage);
            mylog.log(msg);
        }
    }
}
