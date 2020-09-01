package com.rtm.test;

import com.fpnn.sdk.ErrorCode;
import com.rtmsdk.RTMClient;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.TranscribeLang;
import com.rtmsdk.TranslateLang;
import com.rtmsdk.UserInterface;
import com.rtmsdk.UserInterface.IRTMCallback;
import com.rtmsdk.UserInterface.IRTMEmptyCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TestClass {
    public static long peerUid = 101;
    public static long roomId = 200;
    public static long groupId = 200;
    public static byte sendMessgeType = 66;
    public static String setLang = "no";
    public static Map<Long, RTMClient> pushClients = new HashMap<>();
    public Map<Long, String> pushUserTokens;

//    public static long peerUid = 99;
//    public static long roomId = 7788521;
//    public static long groupId = 50;

    public static long pid = 90000017;
    public static long loginUid = 9527;
    public static String token = "4065261c-f349-4209-bc72-cc6c253791a0";
    public static String dispatchEndpoint = "52.82.27.68:13325";
    public static RTMClient client = null;
    public static int lgonStatus = -1;

    public static Map<String, CaseInterface> testMap;
    public static String roomBeizhu = "to room " + roomId;
    public static String groupBeizhu = "to group " + roomId;
    public static String userBeizhu = "to user " + peerUid;
//    public static byte[] audioData = null;
    public static File audioFile = null;

    public enum MsgType {
        P2P,
        GROUP,
        ROOM,
        BROADCAST
    }

/*    public enum CaseType {
        CHAT,
        DATA,
        ROOM,
        GROUP,
        FRIEND,
        USERS,
        SYSTEM,
        MESSAGE,
        FILE,
        HISTORY,
        AUDIO
    }*/

    public void addClients(long uid, RTMClient client){
        pushClients.put(uid, client);
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

    public void startAudioTest(byte[] data) {
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

    public void loginRTM() {
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
                put("audio", new AudioCase());
            }
        };

        TestErrorRecorder mylogRecoder = new TestErrorRecorder();
        client.setErrorRecoder(mylogRecoder);
        RTMAnswer answer = client.login(token);
        lgonStatus = answer.errorCode;
        if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
            mylog.log(" " + loginUid + " login RTM success");
        else
            mylog.log(" " + loginUid + " login RTM error:" + answer.getErrInfo());

        for (final long uid : pushClients.keySet()){
            RTMClient loginClient = pushClients.get(uid);
            loginClient.setErrorRecoder(mylogRecoder);

            loginClient.login(new UserInterface.IRTMEmptyCallback() {
                @Override
                public void onResult(RTMAnswer answer) {
                    if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                        mylog.log("user " + uid + " login result " + answer.getErrInfo());
                    else
                        mylog.log("user " + uid + " login result " + answer.getErrInfo());
                }
            },pushUserTokens.get(uid));
        }
    }

    public static void mySleep(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkClient() {
        return client != null;
    }


    public TestClass(long pid, long uid, String token, String dispatchEndpoint) {
        this.pid = pid;
        this.loginUid = uid;
        this.token = token;
        this.dispatchEndpoint = dispatchEndpoint;
    }


    public static void enterRoom() {
            RTMAnswer answer = client.enterRoom(roomId);
            TestClass.outPutMsg(answer,"enterroom","uid is " + loginUid);
            for (long uid:pushClients.keySet()) {
                answer = pushClients.get(uid).enterRoom(roomId);
                TestClass.outPutMsg(answer,"enterroom","uid is " + uid);
            }
    }

    public static boolean checkStatus() {
        if (lgonStatus != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("not available rtmclient");
            return false;
        }
        return true;
    }

    public static void asyncOutPutMsg(RTMAnswer answer, String method) {
        TestClass.outPutMsg(answer, method, "", 0, false);
    }

    public static void asyncOutPutMsg(RTMAnswer answer, String method, String beizhu) {
        TestClass.outPutMsg(answer, method, beizhu, 0, false);
    }

    public static void outPutMsg(RTMAnswer answer, String method) {
        TestClass.outPutMsg(answer, method, "", 0, true);
    }

    public static void outPutMsg(RTMAnswer answer ,String method, String beizhu) {
        TestClass.outPutMsg(answer, method, beizhu, 0, true);
    }

    public static void outPutMsg(RTMAnswer answer, String method, String beizhu, long mtime) {
        TestClass.outPutMsg(answer, method, beizhu, mtime, true);
    }

    public static void outPutMsg(RTMAnswer answer, String method, String beizhu, long mtime, boolean sync) {
        String syncType = "sync", msg = "";
        long xid = 0;
        if (!sync)
            syncType = "async";

        if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value()) {
            if (mtime > 0)
                msg = String.format("%s %s in %s successed, mtime is:%d", method, beizhu, syncType, mtime);
            else
                msg = String.format("%s %s in %s successed", method, beizhu, syncType);
        } else
            msg = String.format("%s %s in %s failed, errordes:%s", method, beizhu, syncType, answer.getErrInfo());
        mylog.log(msg);
    }
}

class AudioCase implements CaseInterface
{
    public void start(){

    }
}

class ChatCase implements CaseInterface {
    RTMClient client = TestClass.client;
    public static String textMessage = "{\"user\":{\"name\":\"alex\",\"age\":\"18\",\"isMan\":true}}";
    TranslateLang changeLang = TranslateLang.AR;
    String lang = "en";

    private String transMessage = "我今天很高兴";

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        mylog.log("start chat case\n");
        TestClass.enterRoom();
        syncChatTest();
//        asyncChatTest();

        //黑名单发送测试
//        blackListSendTest();

        mylog.log("end chat case");
    }

    //------------------------[ Chat Demo ]-------------------------//
    void syncChatTest(){


        TranslatedInfo transInfo = client.translate(transMessage, lang);
        String beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;;
        mylog.log("translate result is:" + beizhu);

        TranscribeStruct transcribeinfo = client.transcribe(TestClass.audioFile);
        mylog.log("transcribe resultText:" + transcribeinfo.resultText + " resultLang:" + transcribeinfo.resultLang);


        /*    TranscribeStruct transcribeinfo1 = client.transcribe(TestClass.audioFile);
            mylog.log("transcribe resultText:" + transcribeinfo1.resultText + " resultLang:" + transcribeinfo1.resultLang);

            ModifyTimeStruct answer = client.sendChat(TestClass.peerUid, textMessage);
            TestClass.outPutMsg(answer, "sendChat", TestClass.userBeizhu, answer.modifyTime);

            answer = client.sendGroupChat(TestClass.groupId, textMessage);
            TestClass.outPutMsg(answer, "sendGroupChat", TestClass.groupBeizhu, answer.modifyTime);

            answer = client.sendRoomChat(TestClass.roomId, textMessage);
            TestClass.outPutMsg(answer, "sendRoomChat", TestClass.roomBeizhu, answer.modifyTime);

            answer = client.sendCmd(TestClass.peerUid, textMessage);
            TestClass.outPutMsg(answer, "sendCmd", TestClass.userBeizhu, answer.modifyTime);

            answer = client.sendGroupCmd(TestClass.groupId, textMessage);
            TestClass.outPutMsg(answer, "sendGroupCmd", TestClass.groupBeizhu, answer.modifyTime);

            answer = client.sendRoomCmd(TestClass.roomId, textMessage);
            TestClass.outPutMsg(answer, "sendRoomCmd", TestClass.roomBeizhu, answer.modifyTime);

            answer = client.sendAudio(TestClass.peerUid, TestClass.audioFile);
            TestClass.outPutMsg(answer, "sendAudio", TestClass.userBeizhu, answer.modifyTime);

            answer = client.sendGroupAudio(TestClass.groupId, TestClass.audioFile);
            TestClass.outPutMsg(answer, "sendGroupAudio", TestClass.groupBeizhu, answer.modifyTime);

            answer = client.sendRoomAudio(TestClass.roomId, TestClass.audioFile);
            TestClass.outPutMsg(answer, "sendRoomAudio", TestClass.roomBeizhu, answer.modifyTime);

            //增值服务测试
            StringBuilder jk = new StringBuilder();
            List<String> classification = new ArrayList<>();

            ProfanityStruct ret = client.profanity("i am fuck happy",true,0);
            if (ret.errorCode == 0)
                mylog.log("profanity result is:" +  "text: " + ret.text!=null?ret.text:" " + " classification :" + ret.classification!=null?ret.classification.toString():"");

            TranslatedInfo transInfo = client.translate(transMessage, lang);
            String beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;;
            mylog.log("translate result is:" + beizhu);

            client.setTranslatedLanguage(TestClass.setLang);
            mylog.log("setTranslatedLanguage :" + lang + "ok");

            transInfo = client.translate(transMessage, lang);
            beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;;
            mylog.log("translate result is:" + beizhu);

            TranscribeStruct transcribeinfo = client.transcribe(TestClass.audioFile);
            mylog.log("transcribe resultText:" + transcribeinfo.resultText + " resultLang:" + transcribeinfo.resultLang);*/
    }

    void asyncChatTest(){
        client.sendChat(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendchat", TestClass.userBeizhu, mtime, false);
            }
        }, TestClass.peerUid, textMessage);

        client.sendGroupChat(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendGroupChat", TestClass.groupBeizhu, mtime, false);
            }
        }, TestClass.groupId, textMessage);

        client.sendRoomChat(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendRoomChat", TestClass.groupBeizhu, mtime, false);
            }
        }, TestClass.roomId, textMessage);

        client.sendCmd(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendCmd", TestClass.userBeizhu, mtime, false);
            }
        }, TestClass.peerUid, textMessage);

        client.sendGroupCmd(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendGroupCmd", TestClass.groupBeizhu, mtime, false);
            }
        }, TestClass.groupId, textMessage);

        client.sendRoomCmd(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendRoomCmd", TestClass.roomBeizhu, mtime, false);
            }
        }, TestClass.roomId, textMessage);


        client.sendAudio(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendAudio", TestClass.userBeizhu, mtime, false);
            }
        }, TestClass.peerUid, TestClass.audioFile);

        client.sendGroupAudio(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendGroupAudio", TestClass.groupBeizhu, mtime, false);
            }
        }, TestClass.groupId, TestClass.audioFile);

        client.sendRoomAudio(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendRoomAudio", TestClass.roomBeizhu, mtime, false);
            }
        }, TestClass.roomId, TestClass.audioFile);


        client.sendMessage(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendMessage", TestClass.userBeizhu, mtime, false);
            }
        }, TestClass.peerUid, TestClass.sendMessgeType, textMessage);

        client.sendGroupMessage(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendGroupMessage", TestClass.groupBeizhu, mtime, false);
            }
        }, TestClass.groupId, TestClass.sendMessgeType, textMessage);

        client.sendRoomMessage(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendRoomMessage", TestClass.roomBeizhu, mtime, false);
            }
        }, TestClass.roomId, TestClass.sendMessgeType, textMessage);

        TestClass.mySleep(3);

        //增值服务测试
        client.profanity(new IRTMCallback<ProfanityStruct>() {
            @Override
            public void onResult(ProfanityStruct ret, RTMAnswer answer) {
                String beizhu = "";
                if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                    beizhu = ret.text + " " + ret.classification!=null?ret.classification.toString():"";
                TestClass.outPutMsg(answer, "profanity", beizhu, 0, false);

            }
        }, "i am fuck happy today");


        client.translate(new IRTMCallback<TranslatedInfo>() {
            @Override
            public void onResult(TranslatedInfo transInfo, RTMAnswer answer) {
                String beizhu = "";
                if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                    beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;;
                TestClass.outPutMsg(answer, "translate", beizhu, 0, false);
            }
        }, transMessage, lang);

        client.setTranslatedLanguage(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.outPutMsg(answer, "setTranslatedLanguage", TestClass.setLang, 0, false);
            }
        }, changeLang);
        TestClass.mySleep(2);

        client.translate(new IRTMCallback<TranslatedInfo>() {
            @Override
            public void onResult(TranslatedInfo transInfo, RTMAnswer answer) {
                String beizhu = "";
                if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                    beizhu = "sourceText:" + transInfo.sourceText + " sourceLang:" + transInfo.source + " target:" + transInfo.targetText + " targetLang:" + transInfo.target;;
                TestClass.outPutMsg(answer, "translate", beizhu, 0, false);
            }
        }, transMessage, lang);
        TestClass.mySleep(2);
    }

    void blackListSendTest() {
        final String beizhu = "to user " + TestClass.loginUid;
        long toUid = TestClass.loginUid;
        RTMClient client = TestClass.pushClients.get(TestClass.peerUid);
        client.sendChat(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendchat", beizhu, mtime, false);
            }
        }, toUid, textMessage);


        client.sendCmd(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendCmd", beizhu, mtime, false);
            }
        }, toUid, textMessage);

        client.sendMessage(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendMessage", beizhu, mtime, false);
            }
        }, toUid, TestClass.sendMessgeType, textMessage);


        client.sendAudio(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long mtime, RTMAnswer answer) {
                TestClass.outPutMsg(answer, "sendAudio", beizhu, mtime, false);
            }
        }, toUid, TestClass.audioFile);
    }
}

interface CaseInterface {
    void start() throws InterruptedException;
}

class DataCase implements CaseInterface {
    RTMClient client = TestClass.client;

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
            TestClass.mySleep(1);
            getData("key 2");

            mylog.log("=========== Begin delete one of user data ===========");

            deleteData("key 2");

            mylog.log("=========== Begin get user data after delete action ===========");

            getData("key 1");
            TestClass.mySleep(1);
            getData("key 2");

            mylog.log("=========== User logout ===========");

            client.bye();

            mylog.log("=========== User relogin ===========");
            TestClass.mySleep(1);

            client.login(TestClass.token);

            mylog.log("=========== Begin get user data after relogin ===========");

            getData("key 1");
            TestClass.mySleep(1);
            getData("key 2");
    }


    void Setdata(String key, String value){
        RTMAnswer answer = client.dataSet(key, value);
        TestClass.outPutMsg(answer, "dataSet", TestClass.userBeizhu);

        client.dataSet(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                if (answer.errorCode ==ErrorCode.FPNN_EC_OK.value())
                    mylog.log("dataSet async success ");
                else
                    mylog.log("dataSet async failed answer:" + answer.getErrInfo());
            }
        },key, value);
        TestClass.mySleep(1);
    }

    void getData(String key){

        RTMAnswer answer = client.dataGet(key);
        TestClass.outPutMsg(answer, "dataGet", TestClass.userBeizhu);

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
        TestClass.outPutMsg(answer, "dataDelete", TestClass.userBeizhu);
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
    RTMClient client = TestClass.client;
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
            TestClass.outPutMsg(answer, "addFriends", uids.toString());

            MembersStruct answer1 = client.getFriends();
            TestClass.outPutMsg(answer, "getFriends", answer1.toString());

            answer = client.deleteFriends(uids);
            TestClass.outPutMsg(answer, "deleteFriends", uids.toString());

            answer1 = client.getFriends();
            TestClass.outPutMsg(answer, "getFriends", answer1.toString());

            //黑名单
            answer = client.addBlacklist(blacks);
            TestClass.outPutMsg(answer, "addBlacklist", blacks.toString());

            answer1 = client.getBlacklist();
            TestClass.outPutMsg(answer, "getBlacklist", answer1.toString());

            answer = client.delBlacklist(blacks);
            TestClass.outPutMsg(answer, "delBlacklist", blacks.toString());

            answer1 = client.getBlacklist();
            TestClass.outPutMsg(answer, "getBlacklist", answer1.toString());
    }

    void asyncFriendTest(){
        client.addFriends(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "addFriends", uids.toString());
            }
        },uids);

        client.getFriends(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "getFriends", longs!=null?longs.toString():"");
            }
        });

        client.deleteFriends(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "deleteFriends", uids.toString());
            }
        },uids);

        client.getFriends(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "getFriends", longs!=null?longs.toString():"");
            }
        });

        //黑名单
        client.addBlacklist(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "addBlacklist", blacks.toString());
            }
        },blacks);

        client.getBlacklist(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "getBlacklist", longs!=null?longs.toString():"");
            }
        });

        client.delBlacklist(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "delBlacklist", blacks.toString());
            }
        },blacks);
        TestClass.mySleep(3);

        client.getBlacklist(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer, "getBlacklist", longs!=null?longs.toString():"");
            }
        });
    }
}

class GroupCase implements CaseInterface {
    RTMClient client = TestClass.client;
    long groupId = TestClass.groupId;
    final HashSet<Long> uids = new HashSet<Long>() {{
        add(9988678L);
        add(9988789L);
    }};

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        mylog.log("Begin group test case\n");
        syncGroupTest();
        asyncGroupTest();
        TestClass.mySleep(5);
        mylog.log("End group test case\n");
    }

    void syncGroupTest(){
            MembersStruct answer = client.getGroupMembers(groupId);
            TestClass.outPutMsg(answer, "getGroupMembers", answer.toString());

            RTMAnswer ret = client.addGroupMembers(groupId, uids);
            TestClass.outPutMsg(ret, "addGroupMembers");

            ret = client.deleteGroupMembers(groupId, uids);
            TestClass.outPutMsg(ret, "deleteGroupMembers");

            DataInfo info = client.getGroupPublicInfo(groupId);
            TestClass.outPutMsg(info, "getGroupPublicInfo", info.toString());

            ret = client.setGroupInfo(groupId, "hehe", "haha");
            TestClass.outPutMsg(ret, "setGroupInfo");

            GroupInfoStruct groupInfo = client.getGroupInfo(groupId);
            TestClass.outPutMsg(groupInfo, "getGroupInfo", groupInfo.toString());

            answer = client.getUserGroups();
            TestClass.outPutMsg(answer, "getUserGroups", answer.toString());
    }

    void asyncGroupTest(){
        client.getGroupMembers(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> uids, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getGroupMembers",uids!=null?uids.toString():"");
            }
        },groupId);

        client.addGroupMembers(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"addGroupMembers");
            }
        },groupId,uids);


        client.deleteGroupMembers(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"deleteGroupMembers");
            }
        },groupId,uids);


        client.getGroupPublicInfo(new IRTMCallback<String>() {
            @Override
            public void onResult(String s, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getGroupPublicInfo",s);
            }
        },groupId);


        client.setGroupInfo(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"setGroupInfo");
            }
        },groupId,"hehe","haa");

        client.getGroupInfo(new IRTMCallback<GroupInfoStruct>() {
            @Override
            public void onResult(GroupInfoStruct groupInfoStruct, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getGroupInfo", groupInfoStruct!=null?groupInfoStruct.toString():"");
            }
        },groupId);

        client.getUserGroups(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getUserGroups");
            }
        });
    }
}

class RoomCase implements CaseInterface {
    RTMClient client = TestClass.client;
    long roomId = TestClass.roomId;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        TestClass.enterRoom();
        mylog.log("======== Begin room test case =========\n");
        syncRoomTest();
        asyncRoomTest();
        mylog.log("======== End room test case =========\n");

    }

    void syncRoomTest(){
            client.leaveRoom(roomId);

            MembersStruct answer = client.getUserRooms();
            TestClass.outPutMsg(answer, "getUserRooms", answer.toString());

            TestClass.enterRoom();

            DataInfo info = client.getRoomPublicInfo(roomId);
            TestClass.outPutMsg(info, "getRoomPublicInfo", info.toString());

            RTMAnswer hehe = client.setRoomInfo(roomId, "hehe", "haha");
            TestClass.outPutMsg(hehe, "setRoomInfo");

            GroupInfoStruct groupInfo = client.getRoomInfo(roomId);
            TestClass.outPutMsg(groupInfo, "getRoomInfo", groupInfo.toString());

            answer = client.getUserGroups();
            TestClass.outPutMsg(answer, "getUserGroups", answer.uids.toString());
    }

    void asyncRoomTest(){
        client.leaveRoom(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"leaveRoom");
            }
        },roomId);

        TestClass.enterRoom();

        client.getUserRooms(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> rooms, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getUserRooms", rooms.toString());
            }
        });

        client.enterRoom(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"enterRoom");
            }
        },roomId);

        client.setRoomInfo(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"setRoomInfo");
            }
        },roomId,"hello","world");

        client.getRoomPublicInfo(new IRTMCallback<String>() {
            @Override
            public void onResult(String s, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getRoomPublicInfo",s);
            }
        },roomId);


        client.getRoomInfo(new IRTMCallback<GroupInfoStruct>() {
            @Override
            public void onResult(GroupInfoStruct groupInfoStruct, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getRoomInfo", groupInfoStruct!=null?groupInfoStruct.toString():"");
            }
        },roomId);
    }
}

class FileCase implements CaseInterface {
    RTMClient client = TestClass.client;

    private byte fileMType = 50;
    private String filename = "demo.bin";
    private byte[] fileContent = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        TestClass.enterRoom();
        mylog.log("======== Begin file test case =========\n");
        syncFileTest();
        asyncFileTest();
        mylog.log("======== End file test case =========\n");
    }

    //--------------[ send files Demo ]---------------------//
    void syncFileTest(){
            ModifyTimeStruct answer = client.sendFile(TestClass.peerUid, fileMType, fileContent, filename);
            TestClass.outPutMsg(answer, "sendFile", TestClass.userBeizhu);

            answer = client.sendGroupFile(TestClass.groupId, fileMType, fileContent, filename);
            TestClass.outPutMsg(answer, "sendGroupFile", TestClass.groupBeizhu);

            answer = client.sendRoomFile(TestClass.roomId, fileMType, fileContent, filename);
            TestClass.outPutMsg(answer, "sendRoomFile", TestClass.roomBeizhu);
    }

    void asyncFileTest(){
        client.sendFile(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long aLong, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"sendFile", TestClass.roomBeizhu);
            }
        },TestClass.roomId, fileMType, fileContent, filename);

        client.sendGroupFile(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long aLong, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"sendGroupFile", TestClass.groupBeizhu);
            }
        },TestClass.roomId, fileMType, fileContent, filename);


        client.sendRoomFile(new IRTMCallback<Long>() {
            @Override
            public void onResult(Long aLong, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"sendRoomFile", TestClass.roomBeizhu);
            }
        },TestClass.roomId, fileMType, fileContent, filename);
    }
}

class SystemCase implements CaseInterface {
    RTMClient client = TestClass.client;
    Map<String, String> attrs = new HashMap<String, String>(){{
        put("name","tome");
        put("age","18");
    }};

    void systemTest(){

            RTMAnswer answer = client.addAttributes(attrs);
            TestClass.outPutMsg(answer, "addAttributes", attrs.toString());

            AttrsStruct ret = client.getAttributes();
            TestClass.outPutMsg(ret, "getAttributes", ret.toString());

        client.addAttributes(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"addAttributes", attrs.toString());
            }
        },attrs);

        client.getAttributes(new IRTMCallback<List<Map<String, String>>>() {
            @Override
            public void onResult(List<Map<String, String>> maps, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getAttributes", maps!=null?maps.toString():"");
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
    RTMClient client = TestClass.client;
    HashSet<Long> onlineUsers = new HashSet<Long>(){
        {
            add(101L);
            add(100L);
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
            MembersStruct answer = client.getOnlineUsers(onlineUsers);
            TestClass.outPutMsg(answer, "getOnlineUsers", answer.toString());

            RTMAnswer ret = client.setUserInfo("hehe", "haha");
            TestClass.outPutMsg(ret, "setUserInfo");

            GroupInfoStruct userInfo = client.getUserInfo();
            TestClass.outPutMsg(userInfo, "getUserInfo", userInfo.privateInfo + " " + userInfo.publicInfo);


        client.getOnlineUsers(new IRTMCallback<HashSet<Long>>() {
            @Override
            public void onResult(HashSet<Long> longs, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getOnlineUsers", longs!=null?longs.toString():"");
            }
        },onlineUsers);

        client.setUserInfo(new IRTMEmptyCallback() {
            @Override
            public void onResult(RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"setUserInfo");
            }
        },"hehe","haa");

        client.getUserInfo(new IRTMCallback<GroupInfoStruct>() {
            @Override
            public void onResult(GroupInfoStruct groupInfoStruct, RTMAnswer answer) {
                TestClass.asyncOutPutMsg(answer,"getUserInfo", groupInfoStruct.privateInfo + " " + groupInfoStruct.publicInfo);
            }
        });
    }
}

class HistoryCase implements CaseInterface {
    List<Byte> types = new ArrayList<Byte>() {{
        add((byte) 66);
    }};
    private static int fetchTotalCount = 10;

    RTMClient client = TestClass.client;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        mylog.log("Begin History test case\n");
        TestClass.enterRoom();
        syncHistoryTest();
        asyncHistoryTest();

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
        while (count >= 0) {
            hisresult = client.getP2PHistoryChat(TestClass.peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, 0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }

        mylog.log("\n================[ get Group History Chat " + fetchTotalCount + " items ]==================");
        beginMsec = 0;endMsec = 0;lastId = 0;

        while (count >= 0) {
            hisresult = client.getGroupHistoryChat(TestClass.peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, 0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }

        mylog.log("\n================[ get Room History Chat " + fetchTotalCount + " items ]==================");
        beginMsec = 0;endMsec = 0;lastId = 0;

        while (count >= 0) {
            hisresult = client.getRoomHistoryChat(TestClass.peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, 0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }

        mylog.log("\n================[ get Broadcast History Chat " + fetchTotalCount + " items ]==================");
        beginMsec = 0;endMsec = 0;lastId = 0;

        while (count >= 0) {
            hisresult = client.getBroadcastHistoryChat(true, fetchTotalCount, beginMsec, endMsec, lastId, 0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }


        mylog.log("\n================[ get P2P History Message " + fetchTotalCount + " items ]==================");
        beginMsec = 0;endMsec = 0;lastId = 0;

        while (count >= 0) {
            hisresult = client.getP2PHistoryMessage(TestClass.peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, types,0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }


        mylog.log("\n================[ get Group History Message " + fetchTotalCount + " items ]==================");
        beginMsec = 0;endMsec = 0;lastId = 0;

        while (count >= 0) {
            hisresult = client.getGroupHistoryMessage(TestClass.peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, types,0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }


        mylog.log("\n================[ get Room History Message " + fetchTotalCount + " items ]==================");
        beginMsec = 0;endMsec = 0;lastId = 0;

        while (count >= 0) {
            hisresult = client.getRoomHistoryMessage(TestClass.peerUid, true, fetchTotalCount, beginMsec, endMsec, lastId, types,0);
            count = hisresult.count;

            count -= fetchTotalCount;

            displayHistoryMessages(hisresult.messages);
            beginMsec = hisresult.beginMsec;
            endMsec = hisresult.endMsec;
            lastId = hisresult.lastId;
        }

        mylog.log("\n================[ get Broadcast History Message " + fetchTotalCount + " items ]==================");
        beginMsec = 0;endMsec = 0;lastId = 0;

        while (count >= 0) {
            hisresult = client.getBroadcastHistoryMessage(true, fetchTotalCount, beginMsec, endMsec, lastId, types,0);
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
                   mylog.log("getP2PMessage in async return error:" + answer.getErrInfo());
                   return;
               }
               displayHistoryMessages(ret.messages);
           }
       },TestClass.peerUid, true, fetchTotalCount, 0, 0, 0,0);
       TestClass.mySleep(1);

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
        },TestClass.groupId, true, fetchTotalCount, 0, 0, 0,0);
        TestClass.mySleep(1);

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
        },TestClass.roomId, true, fetchTotalCount, 0, 0, 0,0);
        TestClass.mySleep(1);

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
        },TestClass.peerUid, true, fetchTotalCount, 0, 0, 0, types,0);
        TestClass.mySleep(1);

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
        },TestClass.groupId, true, fetchTotalCount, 0, 0, 0, types,0);
        TestClass.mySleep(1);


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
        },TestClass.roomId, true, fetchTotalCount, 0, 0, 0, types,0);
        TestClass.mySleep(1);
    }

    void displayHistoryMessages(List<HistoryMessage> messages) {
        for (HistoryMessage hm : messages) {
            String str = "";
            if (hm.binaryMessage != null) {
                str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  binary message length :%s, attrs:%s, mtime:%d",
                        hm.cursorId, hm.messageId, hm.fromUid, hm.messageType, hm.binaryMessage.length, hm.attrs, hm.modifiedTime);
            } else {
                if (hm.messageType == MessageType.AUDIO)
                    str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  audioinfo :%s, attrs:%s, mtime:%d",
                            hm.cursorId, hm.messageId, hm.fromUid, hm.messageType, hm.audioInfo.duration +" " + hm.audioInfo.sourceLanguage + " " + hm.audioInfo.recognizedLanguage + " " + hm.audioInfo.recognizedText, hm.attrs, hm.modifiedTime);
                else
                    str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  message :%s, attrs:%s, mtime:%d",
                            hm.cursorId, hm.messageId, hm.fromUid, hm.messageType, hm.stringMessage, hm.attrs, hm.modifiedTime);
            }
            mylog.log(str);
        }
    }
}

