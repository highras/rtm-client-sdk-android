package com.rtmsdk.test;

import android.content.Context;
import android.util.Log;

import com.fpnn.sdk.ErrorCode;
import com.fpnn.sdk.TCPClient;
import com.fpnn.sdk.proto.Answer;
import com.fpnn.sdk.proto.Quest;
import com.rtmsdk.RTMAudio;
import com.rtmsdk.RTMClient;
import com.rtmsdk.RTMStruct;
import com.rtmsdk.RTMStruct.AttrsStruct;
import com.rtmsdk.RTMStruct.AudioTextStruct;
import com.rtmsdk.RTMStruct.CheckResult;
import com.rtmsdk.RTMStruct.FileMessageType;
import com.rtmsdk.RTMStruct.GroupInfoStruct;
import com.rtmsdk.RTMStruct.HistoryMessage;
import com.rtmsdk.RTMStruct.HistoryMessageResult;
import com.rtmsdk.RTMStruct.MembersStruct;
import com.rtmsdk.RTMStruct.MessageType;
import com.rtmsdk.RTMStruct.ModifyTimeStruct;
import com.rtmsdk.RTMStruct.ProfanityType;
import com.rtmsdk.RTMStruct.PublicInfo;
import com.rtmsdk.RTMStruct.RTMAnswer;
import com.rtmsdk.RTMStruct.RTMAudioStruct;
import com.rtmsdk.RTMStruct.TranslateType;
import com.rtmsdk.RTMStruct.TranslatedInfo;
import com.rtmsdk.TranscribeLang;
import com.rtmsdk.TranslateLang;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMDoubleValueCallback;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TestClass {
    public long peerUid = 101;
    public long roomId = 100;
    public long groupId = 100;
    public byte sendMessgeType = 90;
    public String setLang = "no";
    public Map<Long, RTMClient> pushClients = new HashMap<>();
    public Map<Long, String> pushUserTokens;
    public File audioSave;
    TestErrorRecorder mylogRecoder = new TestErrorRecorder();
    TestErrorRecorder1 mylogRecoder1 = new TestErrorRecorder1();
    public long loginUid = 999;
    public String loginToken = "1C68107913115A986993D9BB70768FFC";
    public Context appContext;
//    public String dispatchEndpoint = "161.189.171.91:13325";
    public String dispatchEndpoint = "rtm-intl-frontgate.ilivedata.com:13321";
//    public String dispatchEndpoint = "";
    public long pid = 11000001;
//    public long pid = 90000033;

    public RTMClient client = null;
    Random rand = new Random();
    public Map<String, CaseInterface> testMap;
    public String roomBeizhu = " to room " + roomId;
    public String groupBeizhu = " to group " + groupId;
    public String userBeizhu = " to user " + peerUid;
//    public byte[] audioData = null;
    public File audioFile = null;
    public byte[] rtmAudioData = null;
    public byte[] audioData = null;
    public byte[] videoData = null;
    public byte[] piccontent = null;
    public byte[] fileData = null;
    public RTMAudioStruct audioStruct = null;
    public long lastCloseTime = 0;

    class ChatCase implements CaseInterface {
        //    RTMClient client = pushClients.get(101L);
//    public String textMessage = "{\"user\":{\"name\":\"alex\",\"age\":\"18\",\"isMan\":true}}";
        public String textMessage = "chat test";
        public String translateMessage = "fuck you";
        byte[] binaryData = {4,2,4};

        public JSONObject audioAtrrs = new JSONObject(){{
            try {

                put("userkey", "hahahah");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }};
//    audioAtrrs.

        String changeLang = TranslateLang.AR.getName();
        String lang = "en";

        JSONObject fileattrs = new JSONObject(){{
            try {
                put("mykey", "1111");
                put("mykey1", "2222");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }};

        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }
            mylog.log("start chat case\n");

            enterRoomSync();
//        mySleep(1);
//        ClientEngine.stop();

//        enterRoomSync(300);
//        enterRoomSync(400);
        newInterface();
//        syncChatTest();
//        asyncChatTest();

            //黑名单发送测试
//        blackListSendTest();

            mylog.log("end chat case");
        }

        //------------------------[ Chat Demo ]-------------------------//
        void syncChatTest() {
            ModifyTimeStruct answer;

            answer = client.sendChat(peerUid, textMessage,null);
            outPutMsg(answer, "sendChat", userBeizhu, answer.modifyTime, answer.messageId);

//        answer = client.sendCmd(peerUid, textMessage);
//        outPutMsg(answer, "sendCmd", userBeizhu, answer.modifyTime,answer.messageId);
//
//        answer = client.sendMessage(peerUid, sendMessgeType, textMessage);
//        outPutMsg(answer, "sendMessage", userBeizhu, answer.modifyTime,answer.messageId);
//
//        answer = client.sendGroupChat(groupId, textMessage);
//        outPutMsg(answer, "sendGroupChat", groupBeizhu, answer.modifyTime,answer.messageId);

//        answer = client.sendGroupCmd(groupId, textMessage);
//        outPutMsg(answer, "sendGroupCmd", groupBeizhu, answer.modifyTime,answer.messageId);
//
//        answer = client.sendGroupMessage(groupId, sendMessgeType, textMessage);
//        outPutMsg(answer, "sendGroupMessage", groupBeizhu, answer.modifyTime,answer.messageId);
            if (true)
                return;


            AudioTextStruct iikk = client.audioToText(fileToByteArray(RTMAudio.getInstance().getRecordFile()), TranscribeLang.EN_US.getName(),"AMR_WB",16000);
            mylog.log("audioToText sync checkResult result " + iikk.text);

            CheckResult ooohh = client.audioCheck(fileToByteArray(RTMAudio.getInstance().getRecordFile()), TranscribeLang.EN_US.getName());
            mylog.log("audioCheck sync checkResult result " + ooohh.result);

//        answer = client.sendChat(peerUid, translateMessage);
//        outPutMsg(answer, "sendGroupChat", groupBeizhu, answer.modifyTime,answer.messageId);

            if (true)
                return;
            answer = client.sendChat(peerUid, textMessage,null);
            outPutMsg(answer, "sendChat", userBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendGroupChat(groupId, textMessage);
            outPutMsg(answer, "sendGroupChat", groupBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendRoomChat(roomId, textMessage);
            outPutMsg(answer, "sendRoomChat", roomBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendCmd(peerUid, textMessage);
            outPutMsg(answer, "sendCmd", userBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendGroupCmd(groupId, textMessage);
            outPutMsg(answer, "sendGroupCmd", groupBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendRoomCmd(roomId, textMessage);
            outPutMsg(answer, "sendRoomCmd", roomBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendMessage(peerUid, sendMessgeType, textMessage);
            outPutMsg(answer, "sendMessage", userBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendRoomMessage(roomId, sendMessgeType, textMessage);
            outPutMsg(answer, "sendMessage", roomBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendGroupMessage(groupId, sendMessgeType, textMessage);
            outPutMsg(answer, "sendGroupMessage", groupBeizhu, answer.modifyTime,answer.messageId);

//binary
            answer = client.sendMessage(peerUid, sendMessgeType, piccontent);
            outPutMsg(answer, "sendMessage binary", userBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendRoomMessage(roomId, sendMessgeType, piccontent);
            outPutMsg(answer, "sendMessage binary", roomBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendGroupMessage(groupId, sendMessgeType, piccontent);
            outPutMsg(answer, "sendMessage binary", groupBeizhu, answer.modifyTime,answer.messageId);


            //增值服务测试
//            TranslatedInfo transInfo = client.translate(translateMessage, lang,TranslateType.Chat,ProfanityType.Censor);
            TranslatedInfo transInfo = client.translate(translateMessage, lang,null,null);
            String beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;
            mylog.log("translate result is:" + beizhu);

            client.setTranslatedLanguage(setLang);
            mylog.log("setTranslatedLanguage :" + lang + "ok");

            transInfo = client.translate(translateMessage, lang);
            beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;
            ;
            mylog.log("translate result is:" + beizhu);
        }

        void asyncChatTest() {
//            casyncChatlient.sendRoomChat(new IRTMDoubleValueCallback<Long,Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendRoomChat", roomBeizhu, mtime, messageId,false);
//                }
//            }, roomId, textMessage);

//        client.sendMessage(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                outPutMsg(answer, "sendMessage binary", userBeizhu, mtime, messageId,false);
//            }
//        }, peerUid, sendMessgeType, binaryData);
//            if (true)
//                return;
//        client.sendRoomChat(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                outPutMsg(answer, "sendRoomChat", roomBeizhu, mtime, messageId,false);
//            }
//        }, roomId, textMessage);
//        if (true)
//            return;
//        for (int i =0;i<1;i++) {
//            client.sendRoomChat(new IRTMDoubleValueCallback<Long, Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendRoomChat", roomBeizhu, mtime, messageId, false);
//                }
//            }, roomId, textMessage);
//
//            client.sendRoomCmd(new IRTMDoubleValueCallback<Long, Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendRoomCmd", roomBeizhu, mtime, messageId, false);
//                }
//            }, roomId, textMessage);
//
//            client.sendRoomMessage(new IRTMDoubleValueCallback<Long, Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendRoomMessage", roomBeizhu, mtime, messageId, false);
//                }
//            }, roomId, sendMessgeType, textMessage);
//
//            client.sendRoomFile(new IRTMDoubleValueCallback<Long, Long>() {
//                @Override
//                public void onResult(Long time, Long messageId, RTMAnswer answer) {
//                    asyncOutPutMsg(answer, "sendRoomFile normal file", roomBeizhu, time, messageId);
//                }
//            }, roomId, FileMessageType.NORMALFILE, fileData, "nihao.txt", fileattrs);
//
//
//            client.sendGroupChat(new IRTMDoubleValueCallback<Long,Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendGroupChat", groupBeizhu, mtime, messageId,false);
//                }
//            }, groupId, textMessage);
//
//
//            client.sendGroupCmd(new IRTMDoubleValueCallback<Long,Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendGroupCmd", groupBeizhu, mtime, messageId,false);
//                }
//            }, groupId, textMessage);
//
//            client.sendGroupMessage(new IRTMDoubleValueCallback<Long,Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendGroupMessage", groupBeizhu, mtime, messageId,false);
//                }
//            }, groupId, sendMessgeType, textMessage);
//
//            client.sendGroupFile(new IRTMDoubleValueCallback<Long,Long>() {
//                @Override
//                public void onResult(Long time,Long messageId, RTMAnswer answer) {
//                    asyncOutPutMsg(answer,"sendGroupFile image", groupBeizhu, time,messageId);
//                }
//            },groupId, FileMessageType.IMAGEFILE, fileData, "nihao.txt",null);
//            mySleep1(500);
//        }


//        String klk = null;
//        mylog.log(klk.toString());
            long startTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();
            long sendcount =0;
            final AtomicLong p2pcount = new AtomicLong(0);
            final AtomicLong groupcount = new AtomicLong(0);
            while ( sendcount< 30000){
                client.sendChat(new IRTMDoubleValueCallback<Long,Long>() {
                    @Override
                    public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                        if (answer.errorCode != 0)
                            mylog.log("send chat failed " + answer.getErrInfo());
                        else {
                            mylog.log("send chat ok ");
                            p2pcount.incrementAndGet();
                        }
//                        outPutMsg(answer, "sendchat", userBeizhu, mtime, messageId,false);
                    }
                }, peerUid, textMessage );


                client.sendGroupChat(new IRTMDoubleValueCallback<Long,Long>() {
                    @Override
                    public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                        if (answer.errorCode != 0)
                            mylog.log("send groupchat failed " + answer.getErrInfo());
                        else {
                            mylog.log("send groupchat ok ");
                            groupcount.incrementAndGet();
                        }
                    }
                }, groupId, textMessage);
//                mySleep1(5);
                sendcount++;
            }
            mySleep(2);
            mylog.log("send p2pchat  " + p2pcount.get());
            mylog.log("send groupchat " + groupcount.get());



//            client.sendCmd(new IRTMDoubleValueCallback<Long,Long>() {
//                @Override
//                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                    outPutMsg(answer, "sendCmd", userBeizhu, mtime, messageId,false);
//                }
//            }, peerUid, textMessage);
        if (true)
            return;


//        client.sendAudio(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                outPutMsg(answer, "sendAudio", userBeizhu, mtime, messageId,false);
//            }
//        }, peerUid, audioFile);

            client.sendMessage(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendMessage", userBeizhu, mtime, messageId,false);
                }
            }, peerUid, sendMessgeType, textMessage);

            client.sendMessage(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendMessage binary", userBeizhu, mtime, messageId,false);
                }
            }, peerUid, sendMessgeType, binaryData);

//        if (true)
//            return;
            //group
            client.sendGroupChat(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendGroupChat", groupBeizhu, mtime, messageId,false);
                }
            }, groupId, textMessage);


            client.sendGroupCmd(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendGroupCmd", groupBeizhu, mtime, messageId,false);
                }
            }, groupId, textMessage);

//        client.sendGroupAudio(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                outPutMsg(answer, "sendGroupAudio", groupBeizhu, mtime, messageId,false);
//            }
//        }, groupId, audioFile);


            client.sendGroupMessage(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendGroupMessage", groupBeizhu, mtime, messageId,false);
                }
            }, groupId, sendMessgeType, textMessage);

            client.sendGroupMessage(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendGroupMessage  binary", groupBeizhu, mtime, messageId,false);
                }
            }, groupId, sendMessgeType, binaryData);

            //room
            client.sendRoomChat(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendRoomChat", roomBeizhu, mtime, messageId,false);
                }
            }, roomId, textMessage);

            client.sendRoomCmd(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendRoomCmd", roomBeizhu, mtime, messageId,false);
                }
            }, roomId, textMessage);


//        client.sendRoomAudio(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                outPutMsg(answer, "sendRoomAudio", roomBeizhu, mtime, messageId,false);
//            }
//        }, roomId, audioFile);

            client.sendRoomMessage(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendRoomMessage", roomBeizhu, mtime, messageId,false);
                }
            }, roomId, sendMessgeType, textMessage);

            client.sendRoomMessage(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendRoomMessage binary", roomBeizhu, mtime, messageId,false);
                }
            }, roomId, sendMessgeType, binaryData);

            if (true)
                return;


            TranslatedInfo ll = client.translate(null,lang);
            //增值服务测试
            client.translate(new IRTMCallback<TranslatedInfo>() {
                @Override
                public void onResult(TranslatedInfo transInfo, RTMAnswer answer) {
                    String beizhu = "";
                    if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                        beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;;
                    outPutMsg(answer, "translate", beizhu, 0, 0,false);
                }
            }, translateMessage, lang,TranslateType.Chat,ProfanityType.Censor);

            client.setTranslatedLanguage(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    outPutMsg(answer, "setTranslatedLanguage", setLang, 0, 0,false);
                }
            }, changeLang);
            mySleep(2);

            client.translate(new IRTMCallback<TranslatedInfo>() {
                @Override
                public void onResult(TranslatedInfo transInfo, RTMAnswer answer) {
                    String beizhu = "";
                    if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                        beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;;
                    outPutMsg(answer, "translate", beizhu, 0, 0,false);
                }
            }, translateMessage, lang);
            mySleep(2);
        }

        void newInterface(){
            HashSet<Long> p2puids = new HashSet<>();
            p2puids.add(100L);
            RTMStruct.UnreadNum hehe9;
//            hehe9 = client.getP2PUnread(p2puids);
//            mylog.log(hehe9 + " ");
//
//
//            client.getP2PUnread(new IRTMCallback<RTMStruct.UnreadNum>() {
//                @Override
//                public void onResult(RTMStruct.UnreadNum unreadNum, RTMAnswer answer) {
//                    mylog.log("lala");
//                }
//            },p2puids);
//
            hehe9 = client.getGroupUnread(p2puids);
            mylog.log(hehe9 + " ");

            client.getGroupUnread(new IRTMCallback<RTMStruct.UnreadNum>() {
                @Override
                public void onResult(RTMStruct.UnreadNum unreadNum, RTMAnswer answer) {
                    mylog.log("lala");
                }
            },p2puids);


//        CheckResult pp1 = client.textCheck("System Notification");
//        mylog.log("textCheck sync result is " + pp1.result);
//
//        client.textCheck(new IRTMCallback<CheckResult>() {
//            @Override
//            public void onResult(CheckResult checkResult, RTMAnswer answer) {
//                mylog.log(answer.getErrInfo());
//                if (answer.errorCode == 0){
//                    mylog.log(("textCheck async checkResult text " + checkResult.text));
//                }
//            }
//        },"bitch you");

            if(true)
                return;

            for (int i = 0; i<200;i++) {
                for (final long uid : pushClients.keySet()) {
                    pushClients.get(uid).bye();
                }
                mySleep1(500);
                for (final long uid : pushClients.keySet()) {
                    pushClients.get(uid).login(new IRTMEmptyCallback() {
                        @Override
                        public void onResult(RTMAnswer answer) {
                            if (answer.errorCode == ErrorCode.FPNN_EC_OK.value())
                                mylog.log("user " + uid + " login success ");
                        }
                    }, pushUserTokens.get(uid));
                }
                mySleep1(500);
                enterRoomSync();
            }
            if (true)
                return;

            HashSet messageTypes = new HashSet<Integer>(){{add(30);}};
            HashSet messageTypes1 = new HashSet<Integer>(){{add(108);}};
////        HashSet messageTypes1 = new HashSet<Integer>(){{add(98);add(108);}};
//        HashSet removeMessageTypes = new HashSet<Integer>(){{add(30);}};
//
//        DevicePushOption deviceOptions;
//        RTMAnswer kkk;
//        client.addDevicePushOption(new IRTMEmptyCallback() {
//            @Override
//            public void onResult(RTMAnswer answer) {
//                mylog.log("addDevicePushOption result " + answer.getErrInfo());
//            }
//        },0, 100, messageTypes);
////        client.removeDevicePushOption(0,100, removeMessageTypes);
//
//        deviceOptions = client.getDevicePushOption();
//        mylog.log(deviceOptions.toString());
//
//       kkk = client.addDevicePushOption(0,100,messageTypes);
//        mylog.log("addDevicePushOption result " + kkk.getErrInfo());
//        if (true)
//            return;
//
////        //group
//       kkk = client.addDevicePushOption(1,100, messageTypes);
//        mylog.log("addDevicePushOption result " + kkk.getErrInfo());
//
//        deviceOptions = client.getDevicePushOption();
//        mylog.log(deviceOptions.toString());
//
//        kkk = client.addDevicePushOption(0,100, messageTypes1);
//        mylog.log("addDevicePushOption result " + kkk.getErrInfo());
//
//        deviceOptions = client.getDevicePushOption();
//        mylog.log(deviceOptions.toString());
//
////
////        DevicePushOption deviceOptions = client.getDevicePushOption();
////        mylog.log(deviceOptions.toString());
////
////        kkk = client.addDevicePushOption(1,111,null);
////        mylog.log("addDevicePushOption result " + kkk.getErrInfo());
////
////       deviceOptions = client.getDevicePushOption();
////        mylog.log(deviceOptions.toString());
////
//        client.removeDevicePushOption(1,100, removeMessageTypes);
//
//        deviceOptions = client.getDevicePushOption();
//        mylog.log(deviceOptions.toString());
////


//        //p2p
//        client.addDevicePushOption(new IRTMEmptyCallback() {
//            @Override
//            public void onResult(RTMAnswer answer) {
//                mylog.log("addDevicePushOption result " + answer.getErrInfo());
//            }
//        },0, 101, messageTypes);
//
//        client.addDevicePushOption(new IRTMEmptyCallback() {
//            @Override
//            public void onResult(RTMAnswer answer) {
//                mylog.log("addDevicePushOption result " + answer.getErrInfo());
//            }
//        },1, 100, messageTypes1);
//
//        mySleep(1);
//
//       client.getDevicePushOption(new IRTMCallback<DevicePushOption>() {
//           @Override
//           public void onResult(DevicePushOption devicePushOption, RTMAnswer answer) {
//               mylog.log("getDevicePushOption result " + devicePushOption.toString());
//           }
//       });
////
//        mySleep(5);
//
//
//        client.removeDevicePushOption(new IRTMEmptyCallback() {
//            @Override
//            public void onResult(RTMAnswer answer) {
//                mylog.log("removeDevicePushOption result " + answer.getErrInfo());
//
//            }
//        },0, 101, removeMessageTypes);
//
//        mySleep(1);
//
//
//        client.getDevicePushOption(new IRTMCallback<DevicePushOption>() {
//            @Override
//            public void onResult(DevicePushOption devicePushOption, RTMAnswer answer) {
//                mylog.log("getDevicePushOption result " + devicePushOption.toString());
//            }
//        });
//        if (true)
//            return;
//
//
//        CheckResult lloo = client.imageCheck(piccontent);
//        mylog.log("imageCheck sync checkResult result " + lloo.result);
//
//        client.imageCheck(new IRTMCallback<CheckResult>() {
//            @Override
//            public void onResult(CheckResult checkResult, RTMAnswer answer) {
//                mylog.log(" imageCheck " + answer.getErrInfo());
//                if (answer.errorCode == 0){
//                    mylog.log("imageCheck async checkResult result " + checkResult.result);
//                }
//            }
//        },piccontent);

//        HashSet rids = new HashSet<Long>(){{add(roomId);add(100L);add(200L);}};
//        MemberCount hehehaha = client.getRoomCount(rids);
//        for (Map.Entry<Long,Integer> rid: hehehaha.memberCounts.entrySet()){
//            mylog.log("room " + rid.getKey() + " membercount is " + rid.getValue());
//        }
//
//        client.getRoomCount(new IRTMCallback<Map<Long,Integer>>() {
//            @Override
//            public void onResult(Map<Long,Integer> mems, RTMAnswer answer) {
//                for (Map.Entry<Long,Integer> rid: mems.entrySet()){
//                    mylog.log("room " + rid.getKey() + " membercount is " + rid.getValue());
//                }            }
//        },rids);

//        client.getRoomMembers(new IRTMCallback<HashSet<Long>>() {
//            @Override
//            public void onResult(HashSet<Long> longs, RTMAnswer answer) {
//                mylog.log("room getRoomMembers is " + longs.toString());
//            }
//        },roomId);

            if (true)
                return;


            AudioTextStruct iikk = client.audioToText(rtmAudioData, TranscribeLang.ZH_CN.getName(),"AMR_WB",16000);
            mylog.log("audioToText sync checkResult result " + iikk.text);


            CheckResult ooohh = client.audioCheck(rtmAudioData, TranscribeLang.EN_US.getName());
            mylog.log("audioCheck sync checkResult result " + ooohh.result);

            AudioTextStruct iikk2 = client.audioToTextURL("https://s3.cn-northwest-1.amazonaws.com.cn/rtm-filegated-test-cn-northwest-1/90000033/20201022/100/a92adc98ec315c2a52b51f248c53c233.amr",TranscribeLang.ZH_CN.getName());
            mylog.log("audioToTextURL sync checkResult result " + iikk2.text + " " + iikk2.lang);

            client.audioToTextURL(new IRTMCallback<AudioTextStruct>() {
                @Override
                public void onResult(AudioTextStruct audioTextStruct, RTMAnswer answer) {
                    mylog.log("audioToTextURL async checkResult result " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("audioToTextURL async text " + audioTextStruct.text + " " + audioTextStruct.lang);
                    }

                }
            },"https://s3.cn-northwest-1.amazonaws.com.cn/rtm-filegated-test-cn-northwest-1/90000033/20201022/100/a92adc98ec315c2a52b51f248c53c233.amr", TranscribeLang.ZH_CN.getName());

            AudioTextStruct iikk1 = client.audioToText(rtmAudioData, TranscribeLang.ZH_CN.getName());
            mylog.log("audioToText sync checkResult result " + iikk1.text + " " + iikk1.lang);

            client.audioToText(new IRTMCallback<AudioTextStruct>() {
                @Override
                public void onResult(AudioTextStruct audioTextStruct, RTMAnswer answer) {
                    mylog.log("audioToText async checkResult result " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("audioToText async text " + audioTextStruct.text + " " + audioTextStruct.lang);
                    }

                }
            }, rtmAudioData, TranscribeLang.ZH_CN.getName());

            mySleep(5);

            CheckResult pp = client.textCheck("fuck you");
            mylog.log("textCheck sync result is " + pp.result);

            client.textCheck(new IRTMCallback<CheckResult>() {
                @Override
                public void onResult(CheckResult checkResult, RTMAnswer answer) {
                    mylog.log(answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log(("textCheck async checkResult text " + checkResult.text));
                    }
                }
            },"bitch you");


            CheckResult opl = client.imageCheckURL("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E8%94%A1%E8%8B%B1%E6%96%87&step_word=&hs=0&pn=31&spn=0&di=100870&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=3211985769%2C881571615&os=2143750784%2C4271208400&simid=4224450280%2C753660985&adpicid=0&lpn=0&ln=794&fr=&fmq=1603262060531_R&fm=result&ic=&s=undefined&hd=&latest=&copyright=&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg1.cache.netease.com%2Fcatchpic%2FC%2FCB%2FCBFA4A040907B972B6604449EC875E10.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3F45gjy_z%26e3B8mn_z%26e3Bv54AzdH3F8mAzdH3Fac80AzdH3F88AzdH3FBNbVGcBmaadcnBaH_z%26e3Bip4s%23u654%3D6jsjewgp&gsm=20&rpstart=0&rpnum=0&islist=&querylist=&force=undefined");
            mylog.log("imageCheckURL sync result is " + opl.result);

            client.imageCheckURL(new IRTMCallback<CheckResult>() {
                @Override
                public void onResult(CheckResult checkResult, RTMAnswer answer) {
                    mylog.log(" imageCheckURL " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("imageCheckURL async checkResult result " + checkResult.result);
                    }
                }
            },"https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E8%94%A1%E8%8B%B1%E6%96%87&step_word=&hs=0&pn=31&spn=0&di=100870&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=3211985769%2C881571615&os=2143750784%2C4271208400&simid=4224450280%2C753660985&adpicid=0&lpn=0&ln=794&fr=&fmq=1603262060531_R&fm=result&ic=&s=undefined&hd=&latest=&copyright=&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg1.cache.netease.com%2Fcatchpic%2FC%2FCB%2FCBFA4A040907B972B6604449EC875E10.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3F45gjy_z%26e3B8mn_z%26e3Bv54AzdH3F8mAzdH3Fac80AzdH3F88AzdH3FBNbVGcBmaadcnBaH_z%26e3Bip4s%23u654%3D6jsjewgp&gsm=20&rpstart=0&rpnum=0&islist=&querylist=&force=undefined");

            CheckResult oooll = client.imageCheck(piccontent);
            mylog.log("imageCheck sync checkResult result " + oooll.result);

            client.imageCheck(new IRTMCallback<CheckResult>() {
                @Override
                public void onResult(CheckResult checkResult, RTMAnswer answer) {
                    mylog.log(" imageCheck " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("imageCheck async checkResult result " + checkResult.result);
                    }
                }
            }, piccontent);


            CheckResult hehe = client.audioCheckURL("https://emumo.xiami.com/play?ids=/song/playlist/id/1795782490/object_name/default/object_id/0#loaded",TranscribeLang.EN_US.getName());
            mylog.log("sync checkResult result " + hehe.getErrInfo());

            client.audioCheckURL(new IRTMCallback<CheckResult>() {
                @Override
                public void onResult(CheckResult checkResult, RTMAnswer answer) {
                    mylog.log(" audioCheckURL " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("audioCheckURL async checkResult result " + checkResult.result);
                    }
                }
            },"https://emumo.xiami.com/play?ids=/song/playlist/id/1795782490/object_name/default/object_id/0#loaded",TranscribeLang.EN_US.getName());


            ooohh = client.audioCheck(rtmAudioData, TranscribeLang.EN_US.getName());
            mylog.log("audioCheck sync checkResult result " + ooohh.result);

            client.audioCheck(new IRTMCallback<CheckResult>() {
                @Override
                public void onResult(CheckResult checkResult, RTMAnswer answer) {
                    mylog.log(" audioCheck " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("audioCheck async checkResult result " + checkResult.result);
                    }
                }
            }, rtmAudioData,TranscribeLang.EN_US.getName());


            CheckResult hehe1 = client.videoCheckURL("https://www.bilibili.com/video/BV1ry4y1r7zV?spm_id_from=333.851.b_7265706f7274466972737431.7");
            mylog.log("videoCheckURL sync checkResult result " + hehe1.result);

            client.videoCheckURL(new IRTMCallback<CheckResult>() {
                @Override
                public void onResult(CheckResult checkResult, RTMAnswer answer) {
                    mylog.log(" audioCheck " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("videoCheckURL async checkResult result " + checkResult.result);
                    }
                }
            },"https://www.bilibili.com/video/BV1ry4y1r7zV?spm_id_from=333.851.b_7265706f7274466972737431.7");
            mySleep(2);


            CheckResult ooohhtt = client.videoCheck(videoData,"videoDemo.mp4");
            mylog.log("audioCheck sync checkResult result " + ooohhtt.result);

            client.videoCheck(new IRTMCallback<CheckResult>() {
                @Override
                public void onResult(CheckResult checkResult, RTMAnswer answer) {
                    mylog.log(" videoCheck " + answer.getErrInfo());
                    if (answer.errorCode == 0){
                        mylog.log("videoCheck async checkResult result " + checkResult.result);
                    }
                }
            }, videoData,"videoDemo.mp4");


            iikk = client.audioToText(audioData, TranscribeLang.ZH_CN.getName(),"rtm",16000);
            mylog.log("audioToText sync checkResult result " + iikk.text);

            client.audioToText(new IRTMCallback<AudioTextStruct>() {
                @Override
                public void onResult(AudioTextStruct audioTextStruct, RTMAnswer answer) {
                    mylog.log("audioToText sync checkResult result " + answer.getErrInfo());

                }
            }, audioData, TranscribeLang.ZH_CN.getName(),"rtm",16000);

            mySleep(5);

            HashSet<Long> gids = new HashSet<Long>(){{
                add(200L);
                add(300L);
                add(400L);
            }};

            HashSet<Long> uids = new HashSet<Long>(){{
                add(100L);
                add(101L);
            }};

            client.setUserInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set user100 info " + answer.getErrInfo());
                }
            },"100 publicinfo","100 privateinfo");
            mySleep(1);

            pushClients.get(101L).setUserInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set user101 info " + answer.getErrInfo());
                }
            },"101 publicinfo","101 privateinfo");
            mySleep(1);


            client.getUserPublicInfo(new IRTMCallback<Map<String, String>>() {
                @Override
                public void onResult(Map<String, String> infoMap, RTMAnswer answer) {
                    mylog.log("getUserPublicInfo " + answer.getErrInfo());
                    if (answer.errorCode  ==0){
                        JSONObject kk = new JSONObject(infoMap);
                        mylog.log("users info is " + kk.toString());
                    }
                }
            },uids);
            mySleep(1);

            client.setGroupInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set group 200 info " + answer.getErrInfo());
                }
            },200,"group 200 publicinfo","group 200 privateinfo");
            mySleep(1);

            client.setGroupInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set group 300 info " + answer.getErrInfo());
                }
            },300,"group 300 publicinfo","group 300 privateinfo");
            mySleep(1);

            client.setGroupInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set group 400 info " + answer.getErrInfo());
                }
            },400,"group 400 publicinfo"," group 400 privateinfo");
            mySleep(1);


//        client.getGroupsOpeninfo(new IRTMCallback<Map<String, String>>() {
//            @Override
//            public void onResult(Map<String, String> infoMap, RTMAnswer answer) {
//                mylog.log("getGroupsOpeninfo " + answer.getErrInfo());
//                if (answer.errorCode  ==0){
//                    JSONObject kk = new JSONObject(infoMap);
//                    mylog.log("groups info is " + kk.toString());
//                }
//            }
//        },gids);

            mySleep(1);

            client.setRoomInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set room 200 info " + answer.getErrInfo());
                }
            },200,"room 200 publicinfo","room 200 privateinfo");
            mySleep(1);

            client.setRoomInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set room 300 info " + answer.getErrInfo());
                }
            },300,"room 300 publicinfo","room 300 privateinfo");
            mySleep(1);

            client.setRoomInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("set room 400 info " + answer.getErrInfo());
                }
            },400,"room 400 publicinfo","room 400 privateinfo");
            mySleep(1);


            client.getRoomsOpeninfo(new IRTMCallback<Map<String, String>>() {
                @Override
                public void onResult(Map<String, String> infoMap, RTMAnswer answer) {
                    mylog.log("getRoomsOpeninfo " + answer.getErrInfo());
                    if (answer.errorCode  ==0){
                        JSONObject kk = new JSONObject(infoMap);
                        mylog.log("rooms info is " + kk.toString());
                    }
                }
            },gids);

            mySleep(5);
        }

        void blackListSendTest() {
            final String beizhu = "to user " + loginUid;
            long toUid = loginUid;
            RTMClient client = pushClients.get(peerUid);
            client.sendChat(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendchat", beizhu, mtime, messageId,false);
                }
            }, toUid, textMessage);


            client.sendCmd(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendCmd", beizhu, mtime, messageId,false);
                }
            }, toUid, textMessage);

            client.sendMessage(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                    outPutMsg(answer, "sendMessage", beizhu, mtime, messageId,false);
                }
            }, toUid, sendMessgeType, textMessage);


//        client.sendAudio(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                outPutMsg(answer, "sendAudio", beizhu, mtime, messageId,false);
//            }
//        }, toUid, audioFile);
        }
    }

    class DataCase implements CaseInterface {
        

        public void start() {
            IRTMEmptyCallback codeback = new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    mylog.log("answer");
                }
            };
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }

            mylog.log("=========== Begin set user data ===========");

            Setdata("key 1", "value 1");
            mylog.log("=========== Begin get user data ===========");
            getData("key 1");
            mySleep(1);
            getData("key 2");

            mylog.log("=========== Begin delete one of user data ===========");

            deleteData("key 2");

            mylog.log("=========== Begin get user data after delete action ===========");

            getData("key 1");
            mySleep(1);
            getData("key 2");

            mylog.log("=========== User logout ===========");

            client.bye();

            mylog.log("=========== User relogin ===========");
            mySleep(1);

            client.login(loginToken);

            mylog.log("=========== Begin get user data after relogin ===========");

            getData("key 1");
            mySleep(1);
            getData("key 2");
        }


        void Setdata(String key, String value){
            RTMAnswer answer = client.dataSet(key, value);
            outPutMsg(answer, "dataSet", userBeizhu);

            client.dataSet(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                        mylog.log("dataSet async success ");
                    else
                        mylog.log("dataSet async failed answer:" + answer.getErrInfo());
                }
            },key, value);
            mySleep(1);
        }

        void getData(String key){

            RTMAnswer answer = client.dataGet(key);
            outPutMsg(answer, "dataGet", userBeizhu);

            client.dataGet(new IRTMCallback<String>() {
                @Override
                public void onResult(String value, RTMAnswer answer) {
                    if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                        mylog.log("dataGet async success value is:" + value);
                    else
                        mylog.log("dataGet async failed answer:" + answer.getErrInfo());
                }
            },key);
        }

        void deleteData(String key){
            RTMAnswer answer = client.dataDelete(key);
            outPutMsg(answer, "dataDelete", userBeizhu);
            client.dataDelete(key, new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                        mylog.log("dataDelete async success ");
                    else
                        mylog.log("dataDelete async failed answer:" + answer.getErrInfo());
                }
            });
        }
    }

    class FriendCase implements CaseInterface {
        
        HashSet<Long> uids = new HashSet<Long>() {{
            add(123456L);
            add(234567L);
        }};

        final HashSet<Long> blacks = new HashSet<Long>() {{
            add(101L);
        }};

        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }

            mylog.log("start friend case\n");
            syncFriendTest();
            asyncFriendTest();
            mylog.log("end friend case\n");

        }

        void syncFriendTest(){
            RTMAnswer answer = client.addFriends(uids);
            outPutMsg(answer, "addFriends", uids.toString());

            MembersStruct answer1 = client.getFriends();
            outPutMsg(answer, "getFriends", answer1.toString());

            answer = client.deleteFriends(uids);
            outPutMsg(answer, "deleteFriends", uids.toString());

            answer1 = client.getFriends();
            outPutMsg(answer, "getFriends", answer1.toString());

            //黑名单
            answer = client.addBlacklist(blacks);
            outPutMsg(answer, "addBlacklist", blacks.toString());

            answer1 = client.getBlacklist();
            outPutMsg(answer, "getBlacklist", answer1.toString());

            answer = client.delBlacklist(blacks);
            outPutMsg(answer, "delBlacklist", blacks.toString());

            answer1 = client.getBlacklist();
            outPutMsg(answer, "getBlacklist", answer1.toString());
        }

        void asyncFriendTest(){
            client.addFriends(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer, "addFriends", uids.toString());
                }
            },uids);

            client.getFriends(new IRTMCallback<HashSet<Long>>() {
                @Override
                public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                    asyncOutPutMsg(answer, "getFriends", longs!=null?longs.toString():"");
                }
            });

            client.deleteFriends(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer, "deleteFriends", uids.toString());
                }
            },uids);

            client.getFriends(new IRTMCallback<HashSet<Long>>() {
                @Override
                public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                    asyncOutPutMsg(answer, "getFriends", longs!=null?longs.toString():"");
                }
            });

            //黑名单
            client.addBlacklist(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer, "addBlacklist", blacks.toString());
                }
            },blacks);

            client.getBlacklist(new IRTMCallback<HashSet<Long>>() {
                @Override
                public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                    asyncOutPutMsg(answer, "getBlacklist", longs!=null?longs.toString():"");
                }
            });

            client.delBlacklist(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer, "delBlacklist", blacks.toString());
                }
            },blacks);
            mySleep(3);

            client.getBlacklist(new IRTMCallback<HashSet<Long>>() {
                @Override
                public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                    asyncOutPutMsg(answer, "getBlacklist", longs!=null?longs.toString():"");
                }
            });
        }
    }

    class GroupCase implements CaseInterface {
        
        
        final HashSet<Long> uids = new HashSet<Long>() {{
            add(9988678L);
            add(9988789L);
        }};

        final HashSet<Long> gids = new HashSet<Long>() {{
            add(100L);
            add(200L);
        }};

        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }
            mylog.log("Begin group test case\n");
            syncGroupTest();
            asyncGroupTest();
            mySleep(5);
            mylog.log("End group test case\n");
        }

        void syncGroupTest(){
            MembersStruct answer = client.getGroupMembers(groupId);
            outPutMsg(answer, "getGroupMembers", answer.onlineUids.toString() + " " + answer.uids.toString());

            RTMStruct.GroupCount hehe = client.getGroupCount(groupId);
            outPutMsg(hehe, "getGroupCount", hehe.totalCount + " " + hehe.onlineCount);

            RTMAnswer ret = client.addGroupMembers(groupId, uids);
            outPutMsg(ret, "addGroupMembers");

            ret = client.deleteGroupMembers(groupId, uids);
            outPutMsg(ret, "deleteGroupMembers");

            GroupInfoStruct info = client.getGroupPublicInfo(groupId);
            outPutMsg(info, "getGroupPublicInfo", info.toString());

            PublicInfo ll= client.getGroupsOpeninfo(gids);

            client.getGroupPublicInfo(groupId);

            ret = client.setGroupInfo(groupId, "hehe", "haha");
            outPutMsg(ret, "setGroupInfo");

            GroupInfoStruct groupInfo = client.getGroupInfo(groupId);
            outPutMsg(groupInfo, "getGroupInfo", groupInfo.toString());

            answer = client.getUserGroups();
            outPutMsg(answer, "getUserGroups", answer.toString());
        }

        void asyncGroupTest(){
            client.getGroupMembers(new IRTMCallback<MembersStruct>() {
                @Override
                public void onResult(MembersStruct uidInfos, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getGroupMembers",uidInfos.uids.toString() + " " + uidInfos.onlineUids.toString());
                }
            },groupId);

            client.getGroupCount(new IRTMCallback<RTMStruct.GroupCount>() {
                @Override
                public void onResult(RTMStruct.GroupCount groupCount, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getGroupCount",groupCount.totalCount + " " + groupCount.onlineCount);

                }
            },groupId);
            client.addGroupMembers(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer,"addGroupMembers");
                }
            },groupId,uids);


            client.deleteGroupMembers(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer,"deleteGroupMembers");
                }
            },groupId,uids);


            client.getGroupPublicInfo(new IRTMCallback<String>() {
                @Override
                public void onResult(String s, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getGroupPublicInfo",s);
                }
            },groupId);


            client.setGroupInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer,"setGroupInfo");
                }
            },groupId,"hehe","haa");

            client.getGroupInfo(new IRTMCallback<GroupInfoStruct>() {
                @Override
                public void onResult(GroupInfoStruct groupInfoStruct, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getGroupInfo", groupInfoStruct!=null?groupInfoStruct.toString():"");
                }
            },groupId);

            client.getUserGroups(new IRTMCallback<HashSet<Long>>() {
                @Override
                public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getUserGroups");
                }
            });
        }
    }

    class RoomCase implements CaseInterface {

        final HashSet<Long> rids = new HashSet<Long>() {{
            add(900L);
        }};


        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }
            enterRoomAsync();
//        enterRoomSync(300);
            mylog.log("======== Begin room test case =========\n");
            syncRoomTest();
//        asyncRoomTest();
            mylog.log("======== End room test case =========\n");

        }

        void displayHistoryMessages(List<HistoryMessage> messages) {
            for (HistoryMessage hm : messages) {
                String str = "";
                if (hm.binaryMessage != null) {
                    str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  binary message length :%d, attrs:%s, mtime:%d",
                            hm.cursorId, hm.fromUid, hm.messageType, hm.messageId, hm.binaryMessage.length, hm.attrs, hm.modifiedTime);
                } else {
                    if (hm.messageType >= MessageType.IMAGEFILE && hm.messageType <= MessageType.NORMALFILE)
                        str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d, fileinfo :%s, attrs:%s, mtime:%d",
                                hm.cursorId, hm.fromUid, hm.messageType, hm.messageId, hm.fileInfo.fileSize +" " + hm.fileInfo.url , hm.attrs, hm.modifiedTime);
                    else
                        str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  message :%s, attrs:%s, mtime:%d",
                                hm.cursorId, hm.fromUid, hm.messageType, hm.messageId, hm.stringMessage, hm.attrs, hm.modifiedTime);
                }
                mylog.log(str);
            }
        }

        void syncRoomTest(){
            String textMessage = "lala nihao";
            List<Byte> types = new ArrayList<Byte>() {{
                add((byte) 66);
            }};


            client.leaveRoom(roomId);

            MembersStruct answer1 = client.getUserRooms();
            outPutMsg(answer1, "getUserRooms", answer1.toString());


            GroupInfoStruct info = client.getRoomPublicInfo(roomId);
            outPutMsg(info, "getRoomPublicInfo", info.toString());

            RTMAnswer hehe = client.setRoomInfo(roomId, "hehe", "haha");
            outPutMsg(hehe, "setRoomInfo");

            PublicInfo kkkl = client.getRoomsOpeninfo(rids);

            GroupInfoStruct groupInfo = client.getRoomInfo(roomId);
            outPutMsg(groupInfo, "getRoomInfo", groupInfo.toString());

            //test roommessage
            ModifyTimeStruct answer = client.sendRoomChat(roomId, textMessage);
            outPutMsg(answer, "sendRoomChat", roomBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendRoomCmd(roomId, textMessage);
            outPutMsg(answer, "sendRoomCmd", roomBeizhu, answer.modifyTime,answer.messageId);

            answer = client.sendRoomMessage(roomId, (byte)80, "bugaoxing");
            outPutMsg(answer, "sendMessage", roomBeizhu, answer.modifyTime,answer.messageId);

            int fetchTotalCount = 10;
            int count = fetchTotalCount;
            long beginMsec = 0;
            long endMsec = 0;
            long lastId = 0;
            HistoryMessageResult hisresult;
            mylog.log("\n================[ get Room History Chat " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;

//        while (count >= 0) {
//            hisresult = client.getRoomHistoryChat(roomId, true, fetchTotalCount, beginMsec, endMsec, lastId, 0);
//            count = hisresult.count;
//
//            count -= fetchTotalCount;
//
//            displayHistoryMessages(hisresult.messages);
//            beginMsec = hisresult.beginMsec;
//            endMsec = hisresult.endMsec;
//            lastId = hisresult.lastId;
//        }



            mylog.log("\n================[ get Room History Message " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;

            while (count >= 0) {
                hisresult = client.getRoomHistoryMessage(roomId, true, fetchTotalCount, beginMsec, endMsec, lastId, types);
                if (hisresult.errorCode !=0)
                    break;
                count = hisresult.count;

                count -= fetchTotalCount;

                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }
            //test roommessage*/

//            answer = client.getUserGroups();
//            outPutMsg(answer, "getUserGroups", answer.uids.toString());
        }

        void asyncRoomTest(){
//        client.leaveRoom(new IRTMEmptyCallback() {
//            @Override
//            public void onResult(RTMAnswer answer) {
//                asyncOutPutMsg(answer,"leaveRoom");
//            }
//        },roomId);
//

            client.enterRoom(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    if (answer.errorCode == 0){
                        client.getUserRooms(new IRTMCallback<HashSet<Long>>() {
                            @Override
                            public void onResult(HashSet<Long> rooms, RTMAnswer answer) {
                                String desc = "";
                                if (rooms != null)
                                {
                                    for (long tt: rooms)
                                    {
                                        desc = desc + tt + " ";
                                    }
                                }
                                asyncOutPutMsg(answer,"getUserRooms", desc);
                            }
                        });
                    }

                }
            },roomId);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            client.setRoomInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer,"setRoomInfo");
                }
            },roomId,"hello","world");

            client.getRoomPublicInfo(new IRTMCallback<String>() {
                @Override
                public void onResult(String s, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getRoomPublicInfo",s);
                }
            },roomId);


            client.getRoomInfo(new IRTMCallback<GroupInfoStruct>() {
                @Override
                public void onResult(GroupInfoStruct groupInfoStruct, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getRoomInfo", groupInfoStruct!=null?groupInfoStruct.toString():"");
                }
            },roomId);
        }
    }

    class FileCase implements CaseInterface {
        
        JSONObject fileattrs = new JSONObject(){{
            try {
                put("mykey", "1111");
                put("mykey1", "2222");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }};

        //    private FileMessageType fileMType = FileMessageType.NORMALFILE;
        private String filename = "demo.bin";
        private byte[] fileContent = piccontent;
        private String picName = "testpicc.jpeg";


        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }
            enterRoomSync();
            mySleep(1);

            mylog.log("======== Begin file test case =========\n");
//        syncFileTest();
            asyncFileTest();
            mylog.log("======== End file test case =========\n");
        }

        //--------------[ send files Demo ]---------------------//
        void syncFileTest(){
            ModifyTimeStruct answer;
            answer = client.sendFile(peerUid, FileMessageType.IMAGEFILE, piccontent, picName,fileattrs);
            outPutMsg(answer, "sendFile image", userBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendGroupFile(groupId, FileMessageType.IMAGEFILE, piccontent, picName,fileattrs);
            outPutMsg(answer, "sendGroupFile image", groupBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendRoomFile(roomId, FileMessageType.IMAGEFILE, piccontent, picName,fileattrs);
            outPutMsg(answer, "sendRoomFile image", roomBeizhu, answer.modifyTime, answer.messageId);
            if (true)
                return;


            answer = client.sendFile(peerUid, FileMessageType.AUDIOFILE, audioData, "taishan.wav",fileattrs);
            outPutMsg(answer, "sendFile audio", userBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendGroupFile(groupId, FileMessageType.AUDIOFILE, audioData, "taishan.wav",fileattrs);
            outPutMsg(answer, "sendGroupFile audio", groupBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendRoomFile(roomId, FileMessageType.AUDIOFILE, audioData, "taishan.wav",fileattrs);
            outPutMsg(answer, "sendRoomFile audio", roomBeizhu, answer.modifyTime, answer.messageId);


            answer = client.sendFile(peerUid, FileMessageType.VIDEOFILE, videoData, "videoDemo.mp4",fileattrs);
            outPutMsg(answer, "sendFile video", userBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendGroupFile(groupId, FileMessageType.VIDEOFILE, videoData, "videoDemo.mp4",fileattrs);
            outPutMsg(answer, "sendGroupFile video", groupBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendRoomFile(roomId, FileMessageType.VIDEOFILE, videoData, "videoDemo.mp4",fileattrs);
            outPutMsg(answer, "sendRoomFile video", roomBeizhu, answer.modifyTime, answer.messageId);


            answer = client.sendFile(peerUid, FileMessageType.NORMALFILE, fileData, "normalFile.unity",fileattrs);
            outPutMsg(answer, "sendFile normal file", userBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendGroupFile(groupId,  FileMessageType.NORMALFILE, fileData, "normalFile.unity",fileattrs);
            outPutMsg(answer, "sendGroupFile normal file", groupBeizhu, answer.modifyTime, answer.messageId);

            answer = client.sendRoomFile(roomId,  FileMessageType.NORMALFILE, fileData, "normalFile.unity",fileattrs);
            outPutMsg(answer, "sendRoomFile normal file", roomBeizhu, answer.modifyTime, answer.messageId);

        }

        void asyncFileTest(){
//        client.sendGroupFile(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long time,Long messageId, RTMAnswer answer) {
//                asyncOutPutMsg(answer,"sendGroupFile image", groupBeizhu, time,messageId);
//            }
//        },groupId, FileMessageType.IMAGEFILE, fileContent, picName,null);
//
//        client.sendRoomFile(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long time,Long messageId, RTMAnswer answer) {
//                asyncOutPutMsg(answer,"sendRoomFile image", roomBeizhu,time,messageId);
//            }
//        },roomId, FileMessageType.IMAGEFILE, fileContent, picName,null);
//        if (true)
//            return;
//        client.sendFile(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long time,Long messageId, RTMAnswer answer) {
//                asyncOutPutMsg(answer,"sendFile audio", userBeizhu,time, messageId);
//            }
//        },peerUid, FileMessageType.AUDIOFILE, audioData, "taishan.wav",fileattrs);
//
//        if (true)
//            return;

            client.sendFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendFile image", userBeizhu,time, messageId);
                }
            }, peerUid, FileMessageType.IMAGEFILE, fileContent, picName,fileattrs);

            client.sendGroupFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendGroupFile image", groupBeizhu, time,messageId);
                }
            }, groupId, FileMessageType.IMAGEFILE, fileContent, picName,fileattrs);

            client.sendRoomFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendRoomFile image", roomBeizhu,time,messageId);
                }
            }, roomId, FileMessageType.IMAGEFILE, fileContent, picName,fileattrs);

            mySleep(1);
            client.sendFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendFile audio", userBeizhu,time, messageId);
                }
            }, peerUid, FileMessageType.AUDIOFILE, audioData, "taishan.wav",fileattrs);

            client.sendGroupFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendGroupFile audio", groupBeizhu, time,messageId);
                }
            }, groupId, FileMessageType.AUDIOFILE, audioData, "taishan.wav",fileattrs);

            client.sendRoomFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendRoomFile audio", roomBeizhu,time,messageId);
                }
            }, roomId, FileMessageType.AUDIOFILE, audioData, "taishan.wav",fileattrs);

            mySleep(1);

            client.sendFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendFile video", userBeizhu,time, messageId);
                }
            }, peerUid, FileMessageType.VIDEOFILE, videoData, "videoDemo.mp4",fileattrs);

            client.sendGroupFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendGroupFile video", groupBeizhu, time,messageId);
                }
            }, groupId, FileMessageType.VIDEOFILE, videoData, "videoDemo.mp4",fileattrs);

            client.sendRoomFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendRoomFile video", roomBeizhu,time,messageId);
                }
            }, roomId, FileMessageType.VIDEOFILE, videoData, "videoDemo.mp4",fileattrs);


            mySleep(1);

            client.sendFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendFile normal file", userBeizhu,time, messageId);
                }
            }, peerUid,FileMessageType.NORMALFILE, fileData, "normalFile.unity",fileattrs);

            client.sendGroupFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendGroupFile normal file", groupBeizhu, time,messageId);
                }
            }, groupId, FileMessageType.NORMALFILE, fileData, "normalFile.unity",fileattrs);

            client.sendRoomFile(new IRTMDoubleValueCallback<Long,Long>() {
                @Override
                public void onResult(Long time,Long messageId, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"sendRoomFile normal file", roomBeizhu,time,messageId);
                }
            }, roomId, FileMessageType.NORMALFILE, fileData, "normalFile.unity",fileattrs);

        }
    }

    class SystemCase implements CaseInterface {
        
        Map<String, String> attrs = new HashMap<String, String>(){{
            put("name","tome");
            put("age","18");
        }};

        void systemTest(){

            RTMAnswer answer = client.addAttributes(attrs);
            outPutMsg(answer, "addAttributes", attrs.toString());

            AttrsStruct ret = client.getAttributes();
            outPutMsg(ret, "getAttributes", ret.toString());

            client.addAttributes(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer,"addAttributes", attrs.toString());
                }
            },attrs);

            client.getAttributes(new IRTMCallback<List<Map<String, String>>>() {
                @Override
                public void onResult(List<Map<String, String>> maps, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getAttributes", maps!=null?maps.toString():"");
                }
            });
        }


        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }
            mylog.log("======== Begin system test case =========\n");
            systemTest();
            mylog.log("======== End system test case =========\n");
        }
    }

    class UserCase implements CaseInterface {
        
        HashSet<Long> onlineUsers = new HashSet<Long>(){
            {
                add(100L);
                add(101L);
                add(102L);
            }
        };
        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }
            mylog.log("======== Begin user test case =========\n");
            userTest();
            mylog.log("======== End user test case =========\n");

        }

        void userTest(){
            PublicInfo ll = client.getUserPublicInfo(onlineUsers);
            MembersStruct answer = client.getOnlineUsers(onlineUsers);
            outPutMsg(answer, "getOnlineUsers", answer.toString());

            RTMAnswer ret = client.setUserInfo("hehe", "haha");
            outPutMsg(ret, "setUserInfo");

            GroupInfoStruct userInfo = client.getUserInfo();
            outPutMsg(userInfo, "getUserInfo", userInfo.privateInfo + " " + userInfo.publicInfo);


            client.getOnlineUsers(new IRTMCallback<HashSet<Long>>() {
                @Override
                public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getOnlineUsers", longs!=null?longs.toString():"");
                }
            },onlineUsers);

            client.setUserInfo(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    asyncOutPutMsg(answer,"setUserInfo");
                }
            },"hehe","haa");

            client.getUserInfo(new IRTMCallback<GroupInfoStruct>() {
                @Override
                public void onResult(GroupInfoStruct groupInfoStruct, RTMAnswer answer) {
                    asyncOutPutMsg(answer,"getUserInfo", groupInfoStruct.privateInfo + " " + groupInfoStruct.publicInfo);
                }
            });
        }
    }


    class HistoryCase implements CaseInterface {
        List<Byte> types = new ArrayList<Byte>() {{
            add((byte) 66);
        }};
        private int fetchTotalCount = 1;

        

        public void start() {
            if (client == null) {
                mylog.log("not available rtmclient");
                return;
            }
            mylog.log("Begin History test case\n");
            enterRoomSync();
            syncHistoryTest();
//        asyncHistoryTest();

            mylog.log("End History test case\n");
        }

        //------------------------[ Desplay Histories Message ]-------------------------//
        void syncHistoryTest() {
            mylog.log("\n================[ get P2P History Chat " + fetchTotalCount + " items ]==================");
            int count = fetchTotalCount;
            long beginMsec = 0;
            long endMsec = 0;
            long lastId = 0;

            HistoryMessageResult hisresult;

            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getP2PHistoryChat(peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId);
                count = hisresult.count;

                count -= fetchTotalCount;

                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }

  /*      while (count >= 0) {
            hisresult = client.getBroadcastHistoryChat(true, fetchTotalCount, beginMsec, endMsec, lastId, 0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }

        while (count >= 0) {
            hisresult = client.getP2PHistoryChat(peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, 0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }*/
            mylog.log("\n================[ get Group History Chat " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getGroupHistoryChat(groupId, true, fetchTotalCount, beginMsec, endMsec, lastId);
                count = hisresult.count;

                count -= fetchTotalCount;

                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }

            mylog.log("\n================[ get Room History Chat " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getRoomHistoryChat(roomId, true, fetchTotalCount, beginMsec, endMsec, lastId);
                count = hisresult.count;

                count -= fetchTotalCount;
                if (hisresult.errorCode != 0)
                    break;
                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }

            mylog.log("\n================[ get Broadcast History Chat " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getBroadcastHistoryChat(true, fetchTotalCount, beginMsec, endMsec, lastId);
                count = hisresult.count;

                count -= fetchTotalCount;

                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }


            mylog.log("\n================[ get P2P History Message " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getP2PHistoryMessage(peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, types);
                count = hisresult.count;

                count -= fetchTotalCount;

                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }


            mylog.log("\n================[ get Group History Message " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getGroupHistoryMessage(groupId, true, fetchTotalCount, beginMsec, endMsec, lastId, types);
                count = hisresult.count;

                count -= fetchTotalCount;

                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }


            mylog.log("\n================[ get Room History Message " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getRoomHistoryMessage(roomId, true, fetchTotalCount, beginMsec, endMsec, lastId, types);
                count = hisresult.count;

                count -= fetchTotalCount;
                if (hisresult.errorCode != 0 )
                {
                    mylog.log("chucuol " + hisresult.getErrInfo());
                    break;
                }
                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }

            mylog.log("\n================[ get Broadcast History Message " + fetchTotalCount + " items ]==================");
            beginMsec = 0;endMsec = 0;lastId = 0;count = fetchTotalCount;

            while (count >= 0) {
                hisresult = client.getBroadcastHistoryMessage(true, fetchTotalCount, beginMsec, endMsec, lastId, types);
                count = hisresult.count;

                count -= fetchTotalCount;

                displayHistoryMessages(hisresult.messages);
                beginMsec = hisresult.beginMsec;
                endMsec = hisresult.endMsec;
                lastId = hisresult.lastId;
            }
        }

        void asyncHistoryTest(){
            mylog.log("\n================[ get P2P History Chat " + fetchTotalCount + " items ]==================");
            client.getP2PHistoryChat(new IRTMCallback<HistoryMessageResult>() {
                @Override
                public void onResult(HistoryMessageResult ret, RTMAnswer answer) {
                    if (answer.errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        mylog.log("getP2PHistoryChat in async return error:" + answer.getErrInfo());
                        return;
                    }
                    displayHistoryMessages(ret.messages);
                }
            }, peerUid, true, fetchTotalCount, 0, 0, 0);
            mySleep(1);

            mylog.log("\n================[ get GROUP History Chat " + fetchTotalCount + " items ]==================");
            client.getGroupHistoryChat(new IRTMCallback<HistoryMessageResult>() {
                @Override
                public void onResult(HistoryMessageResult ret, RTMAnswer answer) {
                    if (answer.errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        mylog.log("getGroupHistoryChat in async return error:" + answer.getErrInfo());
                        return;
                    }
                    displayHistoryMessages(ret.messages);
                }
            }, groupId, true, fetchTotalCount, 0, 0, 0);
            mySleep(1);

            mylog.log("\n================[ get ROOM History Chat " + fetchTotalCount + " items ]==================");
            client.getRoomHistoryChat(new IRTMCallback<HistoryMessageResult>() {
                @Override
                public void onResult(HistoryMessageResult ret, RTMAnswer answer) {
                    if (answer.errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        mylog.log("getRoomHistoryChat in async return error:" + answer.getErrInfo());
                        return;
                    }
                    displayHistoryMessages(ret.messages);
                }
            }, roomId, true, fetchTotalCount, 0, 0, 0);
            mySleep(1);

            mylog.log("\n================[ get P2P History Message " + fetchTotalCount + " items ]==================");
            client.getP2PHistoryMessage(new IRTMCallback<HistoryMessageResult>() {
                @Override
                public void onResult(HistoryMessageResult ret, RTMAnswer answer) {
                    if (answer.errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        mylog.log("getP2PHistoryMessage in async return error:" + answer.getErrInfo());
                        return;
                    }
                    displayHistoryMessages(ret.messages);
                }
            }, peerUid, true, fetchTotalCount, 0, 0, 0, types);
            mySleep(1);

            mylog.log("\n================[ get GROUP History Message " + fetchTotalCount + " items ]==================");
            client.getGroupHistoryMessage(new IRTMCallback<HistoryMessageResult>() {
                @Override
                public void onResult(HistoryMessageResult ret, RTMAnswer answer) {
                    if (answer.errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        mylog.log("getGroupHistoryMessage in async return error:" + answer.getErrInfo());
                        return;
                    }
                    displayHistoryMessages(ret.messages);
                }
            }, groupId, true, fetchTotalCount, 0, 0, 0, types);
            mySleep(1);


            mylog.log("\n================[ get ROOM History Message " + fetchTotalCount + " items ]==================");
            client.getRoomHistoryMessage(new IRTMCallback<HistoryMessageResult>() {
                @Override
                public void onResult(HistoryMessageResult ret, RTMAnswer answer) {
                    if (answer.errorCode != ErrorCode.FPNN_EC_OK.value()) {
                        mylog.log("getRoomHistoryMessage in async return error:" + answer.getErrInfo());
                        return;
                    }
                    displayHistoryMessages(ret.messages);
                }
            }, roomId, true, fetchTotalCount, 0, 0, 0, types);
            mySleep(1);
        }

        void displayHistoryMessages(List<HistoryMessage> messages) {
            if (messages == null)
                return;
            for (HistoryMessage hm : messages) {
                mylog.log(hm.getInfo());
            }
        }
    }


    int getuid(){
        return rand.nextInt(20000 - 1 + 1) + 1;
    }

    public static byte[] httpGetFile(String fileUrl) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true); // 同意输入流，即同意下载
            httpURLConnection.setUseCaches(false); // 不使用缓冲
            httpURLConnection.setRequestMethod("GET"); // 使用get请求
            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setReadTimeout(20 * 1000);
            httpURLConnection.connect();

            int code = httpURLConnection.getResponseCode();

            if (code == 200) { // 正常响应
                InputStream inputStream = httpURLConnection.getInputStream();

                byte[] buffer = new byte[4096];
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }

                inputStream.close();
            }
            else {
                mylog.log("http return error " + code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    public void startCase(String type) throws InterruptedException {
        if (testMap == null){
            mylog.log("rtmclient init error");
        return;
    }
        if (!testMap.containsKey(type))
            mylog.log("bad case type:" + type);
        else
            testMap.get(type).start();
    }

    public void displayHistoryMessages(List<HistoryMessage> messages) {
        for (HistoryMessage hm : messages) {
            String str = "";
            if (hm.binaryMessage != null) {
                str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  binary message length :%d, attrs:%s, mtime:%d",
                        hm.cursorId, hm.fromUid, hm.messageType, hm.messageId, hm.binaryMessage.length, hm.attrs, hm.modifiedTime);
            } else {
                if (hm.messageType >= MessageType.IMAGEFILE && hm.messageType <= MessageType.NORMALFILE)
                    str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  fileinfo :%s, attrs:%s, mtime:%d",
                            hm.cursorId, hm.fromUid, hm.messageType, hm.messageId, hm.fileInfo.fileSize +" " + hm.fileInfo.url , hm.attrs, hm.modifiedTime);
                else
                    str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  message :%s, attrs:%s, mtime:%d",
                            hm.cursorId, hm.fromUid, hm.messageType, hm.messageId, hm.stringMessage, hm.attrs, hm.modifiedTime);
            }
            mylog.log(str);
        }
    }

    public void startAudioTest() {
//        client.sendAudio(peerUid,audioFile);
//        client.sendGroupAudio(groupId,audioFile);
//        enterRoomSync();
        JSONObject lll = new JSONObject(){{
            try {
                put("userkey audio", "1111");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }};
//        client.sendRoomFile(new IRTMDoubleValueCallback<Long,Long>() {
//            @Override
//            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
//                outPutMsg(answer, "sendFile audio", userBeizhu, mtime, messageId,false);
//            }
//        }, roomId, FileMessageType.AUDIOFILE,audioStruct.audioData,"",lll,audioStruct);


        if (audioStruct == null)
            return;
        client.sendGroupFile(new IRTMDoubleValueCallback<Long,Long>() {
            @Override
            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                outPutMsg(answer, "sendGroupFile audio", groupBeizhu, mtime, messageId,false);
            }
        }, groupId, FileMessageType.AUDIOFILE, audioStruct.audioData,"",lll, audioStruct);
        if(true)
            return;

        client.sendRoomFile(new IRTMDoubleValueCallback<Long,Long>() {
            @Override
            public void onResult(Long mtime, Long messageId, RTMAnswer answer) {
                outPutMsg(answer, "sendFile audio", userBeizhu, mtime, messageId,false);
            }
        }, roomId, FileMessageType.AUDIOFILE, audioStruct.audioData,"",lll, audioStruct);

//        ModifyTimeStruct dd = client.sendAudio(peerUid,audioFile);
//        client.sendGroupAudio(groupId,audioFile);
//        if (testMap == null) {
//            mylog.log("rtmclient init error");
//            return;
//        }
//        AudioCase hh = (AudioCase) testMap.get("audio");
//        hh.sendAudio(data);
    }

    public void startCase() throws InterruptedException {
        for (CaseInterface key : testMap.values())
            key.start();
    }

    public void writeFile(byte[] data, File file)
    {
        try {
            FileOutputStream fos = new FileOutputStream(audioSave);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


    public void repeatLogin(Context context){
        loginUid = 100;
        loginToken = getToken(loginUid);
        client  = new RTMClient(dispatchEndpoint, pid, loginUid, new RTMExampleQuestProcessor(loginUid),context);
        client.setErrorRecoder(mylogRecoder);
    }


    public void exist(){
        if (client == null)
            return;;
        client.bye();
        client.closeRTM();
        client= null;

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (client == null)
//                    return;;
//                client.bye();
//                client.closeRTM();
//            }
//        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (client == null)
//                    return;;
////                client.closeRTM();
//                client= null;
//            }
//        }).start();

    }

    public void loginRTM(Context context) {
        appContext = context;
//        loginUid = getuid();
//        loginUid = 100;
//        loginToken = getToken(loginUid);
//        loginToken = "80A905645083C5A5864AC0B51FAA604A";
//        loginUid = 101;
//        loginToken = "648606B9B32804704791AE1FEF69783A";
        client  = new RTMClient(dispatchEndpoint, pid, loginUid, new RTMExampleQuestProcessor(loginUid),context);
        client.setErrorRecoder(mylogRecoder);

//        for(int i= 0;i<1;i++) {
//            peerUid = getuid();
//            userBeizhu = " to user " + peerUid;
//
//            pushUserTokens.put(peerUid,getToken(peerUid));
//            RTMClient rtmUser = new RTMClient(dispatchEndpoint, pid, peerUid, new RTMExampleQuestProcessor(peerUid),context);
//            rtmUser.setErrorRecoder(mylogRecoder1);
//
//            rtmUser.setErrorRecoder(mylogRecoder1);
//            pushClients.put(peerUid, rtmUser);
//        }


        client.login(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                    mylog.log("user " + loginUid + " login RTM success");
                else
                    mylog.log("user " + loginUid + " login RTM error:" + answer.getErrInfo());
            }
        },loginToken);
        if(true)
            return;
        mylog.log("nihao send  login");
        RTMAnswer answer = client.login(loginToken);

        if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
            mylog.log("user " + loginUid + " login RTM success");
        else
            mylog.log("user " + loginUid + " login RTM error:" + answer.getErrInfo());

        for (final long uid : pushClients.keySet()) {
            final RTMClient loginClient = pushClients.get(uid);

            loginClient.login(new IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    if (answer.errorCode == ErrorCode.FPNN_EC_OK.value()) {
                        mylog.log("user " + uid + " login success ");
//                            final List<Byte> unreadMessageTypes = new ArrayList<Byte>() {{
//                                add((byte)30);
//                                add((byte)32);
//                                add((byte)41);
//                                add((byte)66);
//                            }};
//
//                            Unread opi = loginClient.getSession();
//                            if (opi.errorCode == 0)
//                            {
//                                if (opi.p2pList.size() != 0){
//                                    UnreadNum groupunread = loginClient.getP2PUnread(new HashSet<>(opi.p2pList), unreadMessageTypes);
//                                    if (groupunread.errorCode == 0) {
//                                        for (String uid : groupunread.unreadInfo.keySet()) {
//                                            HistoryMessageResult hehe = loginClient.getP2PHistoryMessage(Long.valueOf(uid), true, groupunread.unreadInfo.get(uid), 0, 0,0,unreadMessageTypes);
//                                            for (RTMMessage kk: hehe.messages){
//                                                mylog.log(kk.getInfo());
//                                            }
//                                        }
//                                    }
//
//                                    loginClient.getP2PUnread(new IRTMCallback<Map<String, Integer>>() {
//                                        @Override
//                                        public void onResult(Map<String, Integer> unreadP2ps, RTMAnswer answer) {
//                                            if (answer.errorCode == 0) {
//                                                for (String gid : unreadP2ps.keySet()) {
//                                                    HistoryMessageResult hehe = loginClient.getP2PHistoryMessage(Long.valueOf(gid), true, unreadP2ps.get(gid), 0, 0, 0,unreadMessageTypes);
//                                                    for (RTMMessage kk: hehe.messages){
//                                                        mylog.log(kk.getInfo());
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }, new HashSet<>(opi.p2pList),unreadMessageTypes);
//                                }
//
//                                if (opi.groupList.size() != 0){
//                                    UnreadNum groupunread = loginClient.getGroupUnread(new HashSet<>(opi.groupList), unreadMessageTypes);
//                                    if (groupunread.errorCode == 0) {
//                                        for (String uid : groupunread.unreadInfo.keySet()) {
//                                            HistoryMessageResult hehe = loginClient.getGroupHistoryMessage(Long.valueOf(uid), true, groupunread.unreadInfo.get(uid), 0, 0,0,unreadMessageTypes);
//                                            for (RTMMessage kk: hehe.messages){
//                                                mylog.log(kk.getInfo());
//                                            }
//                                        }
//                                    }
//
//                                    loginClient.getGroupUnread(new IRTMCallback<Map<String, Integer>>() {
//                                        @Override
//                                        public void onResult(Map<String, Integer> unreadP2ps, RTMAnswer answer) {
//                                            if (answer.errorCode == 0) {
//                                                for (String gid : unreadP2ps.keySet()) {
//                                                    HistoryMessageResult hehe = loginClient.getGroupHistoryMessage(Long.valueOf(gid), true, unreadP2ps.get(gid), 0, 0, 0,unreadMessageTypes);
//                                                    for (RTMMessage kk: hehe.messages){
//                                                        mylog.log(kk.getInfo());
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }, new HashSet<>(opi.groupList),unreadMessageTypes);
//                                }
//                            }
                    } else {
                        mylog.log("user " + uid + " login failed " + answer.getErrInfo());
                    }
                }
            }, pushUserTokens.get(uid));
        }
    }

    public void loginRTM1() {
        client  = new RTMClient(dispatchEndpoint, pid, loginUid, new RTMExampleQuestProcessor1(),appContext);
        client.setErrorRecoder(mylogRecoder1);


        RTMAnswer answer = client.login(loginToken);

        if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
            mylog.log("user " + loginUid + " login RTM success");
        else
            mylog.log("user " + loginUid + " login RTM error:" + answer.getErrInfo());
    }

    public byte[] fileToByteArray(File file) {
        byte[] data;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (Exception e) {
            Log.e("rtmaudio","fileToByteArray error " + e.getMessage());
            return null;
        }
        return data;
    }

    public static void mySleep(int second) {
        try {
            Thread.sleep(second*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void mySleep1(int millsecond) {
        try {
            Thread.sleep(millsecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  String getToken(long uid) {
        TCPClient kk = TCPClient.create("161.189.171.91:13777",true);
        kk.setErrorRecorder(mylogRecoder);
        Quest ll = new Quest("getUserToken");
        String gettoken = "";
        ll.param("pid",pid);
        ll.param("uid",uid);
        try {
            Answer ret = kk.sendQuest(ll,10);
            if (ret.getErrorCode() == 0) {
                gettoken = (String)ret.want("token");
                if (gettoken.isEmpty()) {
                    mylog.log("getUserToken is empty");
                }
            }
            else
                mylog.log("getUserToken " + ret.getErrorMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        kk.close();
        return gettoken;
    }


    public TestClass() {
        pushUserTokens = new HashMap<>();
        testMap = new HashMap<String, CaseInterface>() {
            {
                put("chat", new ChatCase());
                put("data", new DataCase());
                put("group", new GroupCase());
                put("friend", new FriendCase());
                put("room", new RoomCase());
                put("file", new FileCase());
                put("system", new SystemCase());
                put("user", new UserCase());
                put("history", new HistoryCase());
            }
        };
    }

    public void leaveRoomSync() {
        enterRoomSync(roomId);
    }

    public void leaveRoomSync(final long roomId) {
        client.leaveRoom(new IRTMEmptyCallback(){
            @Override
            public void onResult(RTMAnswer answer) {
                outPutMsg(answer,"leaveroom" + roomId,"uid is " + loginUid);
            }
        },roomId);
        for (final long uid:pushClients.keySet()) {
            pushClients.get(uid).leaveRoom(new IRTMEmptyCallback(){
                @Override
                public void onResult(RTMAnswer answer) {
                    outPutMsg(answer,"leaveroom" + roomId,"uid is " + uid);
                }
            },roomId);
        }
    }


    public void sayAllbye() {
        client.bye();
        for (final long uid:pushClients.keySet()) {
            pushClients.get(uid).bye();
        }
    }


    public void enterRoomSync() {
        enterRoomSync(roomId);
    }

    public void enterRoomSync(long roomid) {

            RTMAnswer answer = client.enterRoom(roomid);
            outPutMsg(answer,"enterroom" + roomid,"uid is " + loginUid);
            for (long uid:pushClients.keySet()) {
                answer = pushClients.get(uid).enterRoom(roomid);
                outPutMsg(answer,"enterroom" + roomid,"uid is " + uid);
            }
    }


    public void enterRoomAsync() {
        enterRoomAsync(roomId);
    }

    public void enterRoomAsync(final long roomid) {
        client.enterRoom(new IRTMEmptyCallback(){
            @Override
            public void onResult(RTMAnswer answer) {
                outPutMsg(answer,"enterroom" + roomid,"uid is " + loginUid);
            }
        },roomid);
        for (final long uid:pushClients.keySet()) {
            pushClients.get(uid).enterRoom(new IRTMEmptyCallback(){
                @Override
                public void onResult(RTMAnswer answer) {
                    outPutMsg(answer,"enterroom" + roomid,"uid is " + uid);
                }
            },roomid);
        }
    }

    public void asyncOutPutMsg(RTMAnswer answer, String method) {
        outPutMsg(answer, method, "", 0, 0L,false);
    }

    public void asyncOutPutMsg(RTMAnswer answer, String method, String beizhu) {
        outPutMsg(answer, method, beizhu, 0, 0L,false);
    }

    public void asyncOutPutMsg(RTMAnswer answer, String method, String beizhu,long mtime, long messageId) {
        outPutMsg(answer, method, beizhu, mtime, messageId,false);
    }


    public void outPutMsg(RTMAnswer answer, String method) {
        outPutMsg(answer, method, "", 0, 0L,true);
    }

    public void outPutMsg(RTMAnswer answer ,String method, String beizhu) {
        outPutMsg(answer, method, beizhu, 0, 0L,true);
    }

    public void outPutMsg(RTMAnswer answer, String method, String beizhu, long mtime, long messageId) {
        outPutMsg(answer, method, beizhu, mtime, messageId,true);
    }

    public void outPutMsg(RTMAnswer answer, String method, String beizhu, long mtime, long messageId, boolean sync) {
        String syncType = "sync", msg = "";
        long xid = 0;
        if (!sync)
            syncType = "async";

        if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value()) {
            if (mtime > 0)
                msg = String.format("%s %s in %s successed, mtime is:%d, messageId is :%d", method, beizhu, syncType, mtime, messageId);
            else
                msg = String.format("%s %s in %s successed", method, beizhu, syncType);
        } else
            msg = String.format("%s %s in %s failed, errordes:%s", method, beizhu, syncType, answer.getErrInfo());
        mylog.log(msg);
    }
}

interface CaseInterface {
    void start() throws InterruptedException;
}
