package com.rtm.test;

import com.fpnn.sdk.ErrorCode;
import com.rtmsdk.RTMClient;
import com.rtmsdk.RTMErrorCode;
import com.rtmsdk.RTMStruct.*;
import com.rtmsdk.UserInterface.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TestClass {
    public static long peerUid = 101;
    public static long roomId = 7788521;
    public static long groupId = 7788621;
    public static Map<Long, RTMClient> clients = new HashMap<Long, RTMClient>();

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
        clients.put(uid, client);
    }

    public void startStress(){
        for (int i = 0; i< 100; i ++)
            for (long uid: clients.keySet())
                sendP2PChat(uid,clients.get(uid));
    }

    private void sendP2PChat(long uid, RTMClient client) {
        final String beizhu = "to user " + uid;
        final String method = "sendChat";
        final String textMessage = "strss test";
        LongMtime ret = new LongMtime();

        boolean status = client.sendChat(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
//                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, loginUid, textMessage);
//        TestClass.asyncOutput(status, method);

        int errorCode = client.sendChat(ret, loginUid, textMessage);
//        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
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
        if (testMap == null) {
            mylog.log("rtmclient init error");
            return;
        }
        AudioCase hh = (AudioCase) testMap.get("audio");
        hh.sendAudio(data);
    }

    public void startCase() throws InterruptedException {
        for (CaseInterface key : testMap.values())
            key.start();
    }

    public static RTMClient loginRTM() {
        RTMClient testClient = new RTMClient(dispatchEndpoint, pid, loginUid, new RTMExampleQuestProcessor());
        testClient.setErrorRecoder(new TestErrorRecorder());
        lgonStatus = testClient.login(token);
        if (lgonStatus == ErrorCode.FPNN_EC_OK.value()) {
            mylog.log(" " + loginUid + " login RTM success");
            return testClient;
        } else {
            mylog.log(" " + loginUid + " login RTM error:" + lgonStatus);
            return null;
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


    public TestClass(RTMClient client)
    {

    }

    public TestClass(long pid, long uid, String token, String dispatchEndpoint) {
        this.pid = pid;
        this.loginUid = uid;
        this.token = token;
        this.dispatchEndpoint = dispatchEndpoint;

//        mylog.log("start login:" + RTMUtils.getCurrentSeconds());
        client = loginRTM();
        if (client == null) {
            mylog.log("init TestClass error");
            return;
        }
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
                put("message", new MessageCase());
                put("audio", new AudioCase());
            }
        };
//        mylog.log("end login:" + RTMUtils.getCurrentSeconds());
    }


    public static boolean enterRoom() {
        int errorCode = client.enterRoom(roomId);
        if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("Enter room " + TestClass.roomId + " in sync failed.");
            return false;
        }
        return true;
    }

    public static boolean checkStatus() {
        if (lgonStatus != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("not available rtmclient");
            return false;
        }
        return true;
    }

    public static void asyncOutput(boolean ret, String method) {
        asyncOutput(ret, method, 0);
    }

    public static void asyncOutput(boolean ret, String method, int second) {
        if (!ret)
            mylog.log(method + " in async failed.");
        else
            TestClass.mySleep(second);   //-- Waiting callback desipay result info
    }

    public static void outPutMsg(int errorCode, String method) {
        TestClass.outPutMsg(errorCode, method, "", 0, true);
    }

    public static void outPutMsg(int errorCode, String method, String beizhu) {
        TestClass.outPutMsg(errorCode, method, beizhu, 0, true);
    }

    public static void outPutMsg(int errorCode, String method, String beizhu, long mtime) {
        TestClass.outPutMsg(errorCode, method, beizhu, mtime, true);
    }

    public static void outPutMsg(int errorCode, String method, String beizhu, long mtime, boolean sync) {
        String syncType = "sync", msg = "";
        long xid = 0;
        if (!sync)
            syncType = "async";

        if (errorCode == ErrorCode.FPNN_EC_OK.value()) {
            if (mtime > 0)
                msg = String.format("%s %s in %s successed, mtime is:%d", method, beizhu, syncType, mtime);
            else
                msg = String.format("%s %s in %s successed", method, beizhu, syncType);
        } else
            msg = String.format("%s %s in %s failed, errordes:%s", method, beizhu, syncType, errorCode + "-" + RTMErrorCode.getMsg(errorCode));
        mylog.log(msg);
//        mySleep(2);
    }
}

class ChatCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;
    public static String textMessage = "{\"user\":{\"name\":\"alex\",\"age\":\"18\",\"isMan\":true}}";
    String changeLang = "fr";
    String lang = "no";
//    public static String textMessage = "      ";

    //    private String textMessage = "Hello, RTM!";
    JSONObject js = new JSONObject();


    private String transMessage = "我今天很高兴";

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        sendP2PChat();

        profanity();
        translateTest(lang);
        setTranslateLang(changeLang);
        TestClass.mySleep(2);
        translateTest(changeLang);
        sendGroupChat();
        sendP2PCmd();
        sendGroupCmd();

        if (TestClass.enterRoom()) {
            sendRoomChat();
            sendRoomCmd();
        }

        mylog.log("Wait 5 seonds for receiving server pushed Chat & Cmd if those are being demoed ...");
        TestClass.mySleep(5);
//        ClientEngine.stop();
    }

    //------------------------[ Chat Demo ]-------------------------//

    void profanity() {
        final String method = "profanity";
        boolean status = client.profanity(new ProfanityCallback() {
            @Override
            public void call(String resultText, List<String> classification, int errorCode) {
                String beizhu = "";
                if (classification != null)
                    beizhu = resultText + " " + classification.toString();
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);

            }
        }, "i am fuck happy");
        TestClass.asyncOutput(status, method);

        StringBuilder jk = new StringBuilder();
        List<String> classification = new ArrayList<>();

        int errorCode = client.profanity("i am fuck happy",jk,true, classification);
        String beizhu = jk.toString() + " " + classification.toString();
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void translateTest(String lang) {
        final String method = "translate";
        TranslatedMessage tr = new TranslatedMessage();
        boolean status = client.translate(new TranslateCallback() {
            @Override
            public void call(TranslatedMessage message, int errorCode) {
                String beizhu = "";
                if (message != null)
                    beizhu = "transResult source:" + message.sourceText + " target:" + message.targetText;
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        }, transMessage, lang);
        TestClass.asyncOutput(status, method);


        int errorCode = client.translate(tr, transMessage, lang);
        String beizhu = "transResult source:" + tr.sourceText + " target:" + tr.targetText;
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void setTranslateLang(final String lang) {
        final String method = "setTranslatedLanguage";

        boolean status = client.setTranslatedLanguage(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, lang, 0, false);
            }
        }, changeLang);
        TestClass.asyncOutput(status, method);

        int errorCode = client.setTranslatedLanguage(lang);
        String beizhu = " " + changeLang;
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void sendP2PChat() {
        final String beizhu = "to user " + TestClass.peerUid;
        final String method = "sendChat";

        boolean status = client.sendChat(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.peerUid, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendChat(ret, TestClass.peerUid, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }

    void sendGroupChat() {
        final String beizhu = "to group " + TestClass.groupId;
        final String method = "sendGroupChat";

        boolean status = client.sendGroupChat(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.groupId, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendGroupChat(ret, TestClass.groupId, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }

    void sendRoomChat() {
        final String beizhu = "to room " + TestClass.roomId;
        final String method = "sendRoomChat";

        boolean status = client.sendRoomChat(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.roomId, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendRoomChat(ret, TestClass.roomId, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }

    //------------------------[ Cmd Demo ]-------------------------//
    void sendP2PCmd() {
        final String beizhu = "to user " + TestClass.peerUid;
        final String method = "sendCmd";

        boolean status = client.sendCmd(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.peerUid, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendCmd(ret, TestClass.peerUid, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }

    void sendGroupCmd() {
        final String beizhu = "to group " + TestClass.groupId;
        final String method = "sendGroupCmd";

        boolean status = client.sendGroupCmd(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.groupId, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendGroupCmd(ret, TestClass.groupId, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }

    void sendRoomCmd() {
        final String beizhu = "to room " + TestClass.roomId;
        final String method = "sendRoomCmd";

        boolean status = client.sendRoomCmd(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.roomId, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendRoomCmd(ret, TestClass.roomId, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }
}

interface CaseInterface {
    void start() throws InterruptedException;
}

class DataCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    public void start() throws InterruptedException {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }

        mylog.log("=========== Begin set user data ===========");

        Setdata("key 1", "value 1");
        TestClass.mySleep(1);
        Setdata("key 2", "value 2");

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

        int errcode = client.login(TestClass.token);
        if (errcode != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("relogin error-" + errcode);
            return;
        }

        mylog.log("=========== Begin get user data after relogin ===========");

        getData("key 1");
        TestClass.mySleep(1);
        getData("key 2");
    }


    void Setdata(String key, String value) {
        int errorCode = client.dataSet(key, value);

        if (errorCode != ErrorCode.FPNN_EC_OK.value())
            mylog.log("dataSet with key:" + key + " in sync failed, error code is: " + errorCode);
        else
            mylog.log("dataSet with key: " + key + " in sync success.");
    }

    void getData(String key) {
        StringBuilder value = new StringBuilder();
        int errorCode = client.dataGet(value, key);

        if (errorCode != ErrorCode.FPNN_EC_OK.value())
            mylog.log("dataget with key:" + key + " in sync failed, error code is:" + errorCode);
        else
            mylog.log("dataget with key:" + key + " in sync success, value is:" + value.toString());
    }

    void deleteData(String key) {
        int errorCode = client.dataDelete(key);

        if (errorCode != ErrorCode.FPNN_EC_OK.value())
            mylog.log("dataDelete with key:" + key + " in sync failed.");
        else
            mylog.log("dataDelete with key:" + key + " in sync success.");
    }
}

class FriendCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        addFriends();
        getFriends();
        deleteFriends();

        TestClass.mySleep(2);   //-- Wait for server sync action.

        getFriends();
    }

    void addFriends() {
        final String method = "addFriends";
        HashSet<Long> uids = new HashSet<Long>() {{
            add(123456L);
            add(234567L);
        }};

        final HashSet<Long> uids1 = new HashSet<Long>() {{
            add(34567L);
            add(45678L);
        }};

        boolean status = client.addFriends(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, uids1.toString(), 0, false);
            }
        }, uids1);
        TestClass.asyncOutput(status, method);

        int errorCode = client.addFriends(uids);
        TestClass.outPutMsg(errorCode, method, uids.toString());

    }

    void deleteFriends() {
        final String method = "deleteFriends";
        HashSet<Long> deluids = new HashSet<Long>() {{
            add(123456L);
        }};

        final HashSet<Long> deluids1 = new HashSet<Long>() {{
            add(234567L);
        }};

        boolean status = client.deleteFriends(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, deluids1.toString(), 0, false);
            }
        }, deluids1);
        TestClass.asyncOutput(status, method);


        int errorCode = client.deleteFriends(deluids);
        TestClass.outPutMsg(errorCode, method, deluids.toString());
    }

    void getFriends() {
        final String method = "getFriends";
        HashSet<Long> uids = new HashSet<Long>();

        boolean status = client.getFriends(new MembersCallback() {
            @Override
            public void call(HashSet<Long> uids, int errorCode) {
                TestClass.outPutMsg(errorCode, method, uids.toString(), 0, false);

            }
        });
        TestClass.asyncOutput(status, method);
        uids.clear();

        int errorCode = client.getFriends(uids);
        TestClass.outPutMsg(errorCode, method, uids.toString());
    }
}

class AudioCase implements CaseInterface {
    RTMClient client = TestClass.client;
    LongMtime ret = new LongMtime();

    public void start() throws InterruptedException {

    }

    public void sendAudio(byte[] audioData) {
        final String beizhu = "to user " + TestClass.peerUid;
        final String method = "sendAudio";

        boolean status = client.sendAudio(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.peerUid, audioData);
        TestClass.asyncOutput(status, method);

//        int errorCode = client.sendAudio(ret, TestClass.peerUid, audioData);
//        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }
}

class GroupCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        mylog.log("======== get group members =========");
        getGroupMembers();

        mylog.log("======== add group members =========");
        addGroupMembers();
        TestClass.mySleep(2);

        mylog.log("======== get group members =========");
        getGroupMembers();

        mylog.log("======== delete group members =========");
        deleteGroupMembers();
        TestClass.mySleep(2);

        mylog.log("======== get group members =========");
        getGroupMembers();

        mylog.log("======== get self groups =========");
        getSelfGroups();

        mylog.log("======== set group infos =========");
        setGroupInfos("This is public info", "This is private info");

        TestClass.mySleep(2);
        getGroupInfos();

        mylog.log("======== change group infos =========");

        setGroupInfos("", "This is private info");
        TestClass.mySleep(2);
        getGroupInfos();

        mylog.log("======== change group infos =========");

        setGroupInfos("This is public info", "");
        TestClass.mySleep(2);
        getGroupInfos();

        mylog.log("======== only change the private infos =========");

        setGroupInfos(null, "balabala");
        TestClass.mySleep(2);
        getGroupInfos();

        setGroupInfos("This is public info", "This is private info");
        TestClass.mySleep(2);
        client.bye();
        TestClass.mySleep(1);

        mylog.log("======== user relogin =========");
        int errcode = client.login(TestClass.token);
        if (errcode != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("relogin error-" + errcode);
            return;
        }
        getGroupInfos();
    }

    void addGroupMembers() {
        final String method = "addGroupMembers";
        final HashSet<Long> uids = new HashSet<Long>() {{
            add(9988678L);
            add(9988789L);
        }};

        HashSet<Long> uids1 = new HashSet<Long>() {{
            add(9988900L);
            add(9988901L);
        }};

        boolean status = client.addGroupMembers(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, uids.toString(), 0, false);
            }
        }, TestClass.groupId, uids);
        TestClass.asyncOutput(status, method);

        int errorCode = client.addGroupMembers(TestClass.groupId, uids1);
        TestClass.outPutMsg(errorCode, method, uids1.toString());
    }

    void deleteGroupMembers() {
        final String method = "deleteGroupMembers";
        HashSet<Long> deluids = new HashSet<Long>() {{
            add(9988900L);
            add(9988123L);
        }};

        final HashSet<Long> deluids1 = new HashSet<Long>() {{
            add(9988901L);
            add(9988234L);
        }};

        boolean status = client.deleteGroupMembers(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, deluids1.toString(), 0, false);
            }
        }, TestClass.groupId, deluids1);
        TestClass.asyncOutput(status, method);

        int errorCode = client.deleteGroupMembers(TestClass.groupId, deluids);
        TestClass.outPutMsg(errorCode, method, deluids.toString());
    }

    void getGroupMembers() {
        final String method = "getGroupMembers";
        HashSet<Long> groupIds = new HashSet<Long>();

        boolean status = client.getGroupMembers(new MembersCallback() {
            @Override
            public void call(HashSet<Long> groupIds, int errorCode) {
                TestClass.outPutMsg(errorCode, method, groupIds.toString(), 0, false);

            }
        }, TestClass.groupId);
        TestClass.asyncOutput(status, method);
        groupIds.clear();

        int errorCode = client.getGroupMembers(groupIds, TestClass.groupId);
        TestClass.outPutMsg(errorCode, method, groupIds.toString());
    }

    void getSelfGroups() {
        final String method = "getUserGroups";
        HashSet<Long> groupIds = new HashSet<Long>();

        boolean status = client.getUserGroups(new MembersCallback() {
            @Override
            public void call(HashSet<Long> groupIds, int errorCode) {
                TestClass.outPutMsg(errorCode, method, groupIds.toString(), 0, false);

            }
        });
        TestClass.asyncOutput(status, method);
        groupIds.clear();

        int errorCode = client.getUserGroups(groupIds);
        TestClass.outPutMsg(errorCode, method, groupIds.toString());
    }

    void setGroupInfos(String publicInfos, String privateInfos) {
        final String method = "setGroupInfo";
        final String beizhu = "publicInfos:" + publicInfos + " privateInfos:" + privateInfos;

        boolean status = client.setGroupInfo(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        }, TestClass.groupId, publicInfos, privateInfos);
        TestClass.asyncOutput(status, method);


        int errorCode = client.setGroupInfo(TestClass.groupId, publicInfos, privateInfos);
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void getGroupInfos() {
        StringBuilder publicInfos = new StringBuilder();
        StringBuilder privateInfos = new StringBuilder();

        final String method = "getGroupInfo";

        boolean status = client.getGroupInfo(new DoubleStringCallback() {
            @Override
            public void call(String str1, String str2, int errorCode) {
                String beizhu = "publicInfos:" + str1 + " privateInfos:" + str2;
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        }, TestClass.groupId);
        TestClass.asyncOutput(status, method);


        int errorCode = client.getGroupInfo(publicInfos, privateInfos, TestClass.groupId);
        String beizhu = "publicInfos:" + publicInfos + " privateInfos:" + privateInfos;
        TestClass.outPutMsg(errorCode, method, beizhu);
    }
}

class RoomCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        mylog.log("======== enter room =========");
        TestClass.enterRoom();

        TestClass.mySleep(1);
        mylog.log("======== get self rooms =========");
        getSelfRooms();

        TestClass.mySleep(1);
        mylog.log("======== leave room =========");
        leaveRoom();
        TestClass.mySleep(1);

        mylog.log("======== get self rooms =========");
        getSelfRooms();

        TestClass.mySleep(1);
        mylog.log("======== enter room =========");
        TestClass.enterRoom();

        mylog.log("======== set room infos =========");

        setRoomInfos("This is public info", "This is private info");

        TestClass.mySleep(1);

        getRoomInfos();

        mylog.log("======== change room infos =========");

        setRoomInfos("", "This is private info");
        TestClass.mySleep(1);
        getRoomInfos();

        mylog.log("======== change room infos =========");

        setRoomInfos("This is public info", "");
        TestClass.mySleep(1);
        getRoomInfos();

        mylog.log("======== only change the private infos =========");

        setRoomInfos(null, "balabala");
        TestClass.mySleep(1);
        getRoomInfos();

        setRoomInfos("This is public info", "This is private info");
        client.bye();
        TestClass.mySleep(1);
        mylog.log("======== user relogin =========");

        int errcode = client.login(TestClass.token);
        if (errcode != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("relogin error-" + errcode);
            return;
        }
        mylog.log("======== enter room =========");
        TestClass.enterRoom();

        getRoomInfos();
    }

    void leaveRoom() {
        final String method = "leaveRoom";
        boolean status = client.leaveRoom(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, "", 0, false);
            }
        }, TestClass.roomId);
        TestClass.asyncOutput(status, method);

        int errorCode = client.leaveRoom(TestClass.roomId);
        TestClass.outPutMsg(errorCode, method);
    }

    void getSelfRooms() {
        final String method = "getUserGroups";
        HashSet<Long> roomIds = new HashSet<Long>();

        boolean status = client.getUserRooms(new MembersCallback() {
            @Override
            public void call(HashSet<Long> roomIds, int errorCode) {
                TestClass.outPutMsg(errorCode, method, roomIds.toString(), 0, false);

            }
        });
        TestClass.asyncOutput(status, method);
        roomIds.clear();

        int errorCode = client.getUserRooms(roomIds);
        TestClass.outPutMsg(errorCode, method, roomIds.toString());
    }

    void setRoomInfos(String publicInfos, String privateInfos) {
        final String method = "setRoomInfo";
        final String beizhu = "publicInfos:" + publicInfos + " privateInfos:" + privateInfos;

        boolean status = client.setRoomInfo(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        }, TestClass.roomId, publicInfos, privateInfos);
        TestClass.asyncOutput(status, method);

        int errorCode = client.setRoomInfo(TestClass.roomId, publicInfos, privateInfos);
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void getRoomInfos() {
        StringBuilder publicInfos = new StringBuilder();
        StringBuilder privateInfos = new StringBuilder();

        final String method = "getRoomInfo";

        boolean status = client.getRoomInfo(new DoubleStringCallback() {
            @Override
            public void call(String str1, String str2, int errorCode) {
                String beizhu = "publicInfos:" + str1 + " privateInfos:" + str2;
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        }, TestClass.roomId);
        TestClass.asyncOutput(status, method);

        int errorCode = client.getRoomInfo(publicInfos, privateInfos, TestClass.roomId);
        String beizhu = "publicInfos:" + publicInfos + " privateInfos:" + privateInfos;
        TestClass.outPutMsg(errorCode, method, beizhu);
    }
}

class FileCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    private byte fileMType = 50;
    private String filename = "demo.bin";
    private byte[] fileContent = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        sendP2PFile();
        sendGroupFile();
        TestClass.enterRoom();
        sendRoomFile();
    }

    //--------------[ send files Demo ]---------------------//
    void sendP2PFile() {
        final String method = "sendFile";
        final String beizhu = "to user:" + TestClass.peerUid;
        boolean status = client.sendFile(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.peerUid, fileMType, fileContent, filename);

        TestClass.asyncOutput(status, method, 3);

        int errorCode = client.sendFile(ret, TestClass.peerUid, fileMType, fileContent, filename);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }

    void sendGroupFile() {
        final String method = "sendGroupFile";
        final String beizhu = "to group:" + TestClass.groupId;
        boolean status = client.sendGroupFile(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.groupId, fileMType, fileContent, filename);
        TestClass.asyncOutput(status, method, 3);

        int errorCode = client.sendGroupFile(ret, TestClass.groupId, fileMType, fileContent, filename);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }

    void sendRoomFile() {
        final String method = "sendRoomFile";
        final String beizhu = "to room:" + TestClass.roomId;
        boolean status = client.sendRoomFile(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.roomId, fileMType, fileContent, filename);
        TestClass.asyncOutput(status, method, 3);

        int errorCode = client.sendRoomFile(ret, TestClass.roomId, fileMType, fileContent, filename);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);
    }
}

class SystemCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    void addAttributesDemo() {
        final String method = "addAttributes";
        final Map<String, String> arrs = new HashMap<String, String>() {
            {
                put("key1", "value1");
            }
        };

        Map<String, String> arrs1 = new HashMap<String, String>() {{
            put("key2", "value2");
        }};

        boolean status = client.addAttributes(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, arrs.toString(), 0, false);
            }
        }, arrs);
        TestClass.asyncOutput(status, method);

        int errorCode = client.addAttributes(arrs1);
        TestClass.outPutMsg(errorCode, method, arrs1.toString());
    }

    void getAttributesDemo() {
        final String method = "getAttributes";
        List<Map<String, String>> arrs = new ArrayList<>();

        boolean status = client.getAttributes(new AttrsCallback() {
            @Override
            public void call(List<Map<String, String>> attrs, int errorCode) {
                TestClass.outPutMsg(errorCode, method, attrs.toString(), 0, false);
            }
        });
        TestClass.asyncOutput(status, method);

        int errorCode = client.getAttributes(arrs);
        TestClass.outPutMsg(errorCode, method, arrs.toString());
    }

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        addAttributesDemo();
        getAttributesDemo();

        client.bye();
        TestClass.mySleep(1);

        int errcode = client.login(TestClass.token);
        if (errcode != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("relogin error-" + errcode);
            return;
        }
        else
            mylog.log("system login ok");
    }
}

class UserCase implements CaseInterface {
    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        getOnlineUsers();

        setUserInfos("This is public info", "This is private info");
        getUserInfos();

        mylog.log("======== =========");

        setUserInfos("", "This is private info");
        getUserInfos();

        mylog.log("======== =========");

        setUserInfos("This is public info", "");
        getUserInfos();

        mylog.log("======== only change the private infos =========");

        setUserInfos(null, "balabala");
        getUserInfos();

        setUserInfos("This is public info", "This is private info");
        client.bye();
        TestClass.mySleep(1);

        mylog.log("======== user relogin =========");
        int errcode = client.login(TestClass.token);
        if (errcode != ErrorCode.FPNN_EC_OK.value()) {
            mylog.log("relogin error-" + errcode);
            return;
        }

        getUserInfos();
    }

    void getOnlineUsers() {
        final String method = "getOnlineUsers";

        final HashSet<Long> uids = new HashSet<Long>() {{
            add(9527L);
            add(9528L);
        }};

        final HashSet<Long> onlineUids = new HashSet<Long>();

        boolean status = client.getOnlineUsers(new MembersCallback() {
            @Override
            public void call(HashSet<Long> retUids, int errorCode) {
                String beizhu = "getUsers online:" + uids.toString() + "  real onlines:" + retUids.toString();
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        }, uids);
        TestClass.asyncOutput(status, method);

        int errorCode = client.getOnlineUsers(onlineUids, uids);
        String beizhu = "getUsers online:" + uids.toString() + "  real onlines:" + onlineUids.toString();
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void setUserInfos(String publicInfos, String privateInfos) {
        final String method = "setUserInfos";
        final String beizhu = "publicInfos:" + publicInfos + " privateInfos:" + privateInfos;

        boolean status = client.setUserInfo(new ErrorCodeCallback() {
            @Override
            public void call(int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        }, publicInfos, privateInfos);
        TestClass.asyncOutput(status, method);


        int errorCode = client.setUserInfo(publicInfos, privateInfos);
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void getUserInfos() {
        StringBuilder publicInfos = new StringBuilder();
        StringBuilder privateInfos = new StringBuilder();

        final String method = "getUserInfo";

        boolean status = client.getUserInfo(new DoubleStringCallback() {
            @Override
            public void call(String str1, String str2, int errorCode) {
                String beizhu = "publicInfos:" + str1 + " privateInfos:" + str2;
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        });
        TestClass.asyncOutput(status, method);


        int errorCode = client.getUserInfo(publicInfos, privateInfos);
        String beizhu = "publicInfos:" + publicInfos + " privateInfos:" + privateInfos;
        TestClass.outPutMsg(errorCode, method, beizhu);
    }
}

class MessageCase implements CaseInterface {
    private static byte customMType = 66;

        public static String textMessage = "{\"user\":{\"name\":\"alex\",\"age\":\"18\",\"isMan\":true}}";
//    public static String textMessage = "            ";



    //    private static String textMessage = "Hello, RTM!";
    private static byte[] binaryMessage = new byte[]{-30, 0x00, 0x00, -112, 0x00, 0x00, 0x00, 1, 9, 10, 11, 12, 13, 14, 15, 16, 0x00};

    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        sendP2PMessage();
        sendGroupMessage();

        if (TestClass.enterRoom()) {
            sendRoomMessage();
        }
        getUnreadMessage();
        getSession();

        mylog.log("Wait 10 seonds for receiving server pushed messsage if those are being demoed ...");
        TestClass.mySleep(5);
    }

    void getSession() {
        final String method = "getSession";
        boolean status = client.getSession(new UnreadCallback() {
            @Override
            public void call(List<Long> p2p_uids, List<Long> groupIds, int errorCode) {
                String beizhu = "uids:" + p2p_uids.toString() + " gids:" + groupIds.toString();
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        });
        TestClass.asyncOutput(status, method);

        List<Long> uids = new ArrayList<Long>();
        List<Long> groupIds = new ArrayList<Long>();

        int errorCode = client.getSession(uids, groupIds);
        String beizhu = "uids:" + uids.toString() + " gids:" + groupIds.toString();
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void getUnreadMessage() {
        final String method = "getUnread";
        boolean status = client.getUnread(new UnreadCallback() {
            @Override
            public void call(List<Long> p2p_uids, List<Long> groupIds, int errorCode) {
                String beizhu = "uids:" + p2p_uids.toString() + " gids:" + groupIds.toString();
                TestClass.outPutMsg(errorCode, method, beizhu, 0, false);
            }
        });
        TestClass.asyncOutput(status, method);

        List<Long> uids = new ArrayList<Long>();
        List<Long> groupIds = new ArrayList<Long>();

        int errorCode = client.getUnread(uids, groupIds);
        String beizhu = "uids:" + uids.toString() + " gids:" + groupIds.toString();
        TestClass.outPutMsg(errorCode, method, beizhu);
    }

    void sendP2PMessage() {
        final String beizhu = "to user " + TestClass.peerUid;
        final String method = "sendMessage";

        boolean status = client.sendMessage(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.peerUid, customMType, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendMessage(ret, TestClass.peerUid, customMType, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);

        //for binary//
        final String method1 = "sendMessage in binary";
        boolean status1 = client.sendMessage(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method1, beizhu, mtime, false);
            }
        }, TestClass.peerUid, customMType, binaryMessage);
        TestClass.asyncOutput(status1, method1);

        int errorCode1 = client.sendMessage(ret, TestClass.peerUid, customMType, binaryMessage);
        TestClass.outPutMsg(errorCode1, method1, beizhu, ret.mtime);
    }

    void sendGroupMessage() {
        final String beizhu = "to group " + TestClass.groupId;
        final String method = "sendGroupMessage";

        boolean status = client.sendGroupMessage(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.groupId, customMType, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendGroupMessage(ret, TestClass.groupId, customMType, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);

        //for binary//
        final String method1 = "sendGroupMessage in binary";
        boolean status1 = client.sendMessage(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method1, beizhu, mtime, false);
            }
        }, TestClass.groupId, customMType, binaryMessage);
        TestClass.asyncOutput(status1, method1);

        int errorCode1 = client.sendMessage(ret, TestClass.groupId, customMType, binaryMessage);
        TestClass.outPutMsg(errorCode1, method1, beizhu, ret.mtime);
    }

    void sendRoomMessage() {
        final String beizhu = "to room " + TestClass.roomId;
        final String method = "sendRoomMessage";

        boolean status = client.sendRoomMessage(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method, beizhu, mtime, false);
            }
        }, TestClass.roomId, customMType, textMessage);
        TestClass.asyncOutput(status, method);

        int errorCode = client.sendRoomMessage(ret, TestClass.roomId, customMType, textMessage);
        TestClass.outPutMsg(errorCode, method, beizhu, ret.mtime);

        //for binary//
        final String method1 = "sendRoomMessage in binary";
        boolean status1 = client.sendMessage(new LongFunctionCallback() {
            @Override
            public void call(long mtime, int errorCode) {
                TestClass.outPutMsg(errorCode, method1, beizhu, mtime, false);
            }
        }, TestClass.groupId, customMType, binaryMessage);
        TestClass.asyncOutput(status1, method1);

        int errorCode1 = client.sendMessage(ret, TestClass.groupId, customMType, binaryMessage);
        TestClass.outPutMsg(errorCode1, method1, beizhu, ret.mtime);
    }
}

class HistoryCase implements CaseInterface {
    List<Byte> types = new ArrayList<Byte>() {{
        add((byte) 66);
    }};
    private static int fetchTotalCount = 10;

    LongMtime ret = new LongMtime();
    RTMClient client = TestClass.client;

    public void start() {
        if (client == null) {
            mylog.log("not available rtmclient");
            return;
        }
        getP2PMessage();
        if (true)
            return;

        TestClass.enterRoom();
        mylog.log("\n================[ get P2P Message " + fetchTotalCount + " items ]==================");
        getP2PMessage();

        mylog.log("\n================[ get Group Message " + fetchTotalCount + " items ]==================");
        getGroupMessage();

        mylog.log("\n================[ get Room Message " + fetchTotalCount + " items ]==================");
        getRoomMessage();

        mylog.log("\n================[ get Broadcast Message " + fetchTotalCount + " items ]==================");
        getBroadcastMessage();


        mylog.log("\n================[ get P2P Chat " + fetchTotalCount + " items ]==================");
        getP2PChat();

        mylog.log("\n================[ get Group Chat " + fetchTotalCount + " items ]==================");
        getGroupChat();

        mylog.log("\n================[ get Room Chat " + fetchTotalCount + " items ]==================");
        getRoomChat();

        mylog.log("\n================[ get Broadcast Chat " + fetchTotalCount + " items ]==================");
        getBroadcastChat();

        mylog.log("End History test case");
    }

    //------------------------[ Desplay Histories Message ]-------------------------//
    void displayHistoryMessages(List<HistoryMessage> messages) {
        for (HistoryMessage hm : messages) {
            String str = "";
            if (hm.binaryMessage != null) {
                str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  binary message length :%s, attrs:%s, mtime:%d",
                        hm.id, hm.fromUid, hm.mtype, hm.mid, hm.binaryMessage.length, hm.attrs, hm.mtime);
            } else {
                str = String.format("-- Fetched: ID:%d, from:%d, mtype:%d, mid:%d,  message :%s, attrs:%s, mtime:%d",
                        hm.id, hm.fromUid, hm.mtype, hm.mid, hm.message, hm.attrs, hm.mtime);
            }
            mylog.log(str);
        }
    }


    //------------------------[ Message Histories Demo ]-------------------------//
    void getP2PMessage() {
        final String beizhu = "in user " + TestClass.peerUid;
        final String method = "getP2PMessage";
        HistoryMessageResult result = new HistoryMessageResult();
        List<Byte> types = new ArrayList<Byte>() {{
            add((byte)66);
        }
        };


        boolean status = client.getP2PMessage(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getP2PMessage in async return error:" + errorCode);
                    return;
                }
                String desc = beizhu + " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + lastId;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, TestClass.peerUid, true, fetchTotalCount, 0, 0, 0, types);
        TestClass.asyncOutput(status, method);

        TestClass.mySleep(2);

        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;
            int errorCode = client.getP2PMessage(result, TestClass.peerUid, true, maxCount, beginMsec, endMsec, lastId, new ArrayList<Byte>() {{
                add((byte) 66);
            }});
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getP2PMessage return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;
            TestClass.outPutMsg(errorCode, method, beizhu);
            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }

    void getGroupMessage() {
        final String beizhu = "in group " + TestClass.groupId;
        final String method = "getGroupMessage";
        HistoryMessageResult result = new HistoryMessageResult();

        boolean status = client.getGroupMessage(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getGroupMessage in async return error:" + errorCode);
                    return;
                }
                String desc = beizhu + " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + lastId;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, TestClass.groupId, true, fetchTotalCount);
        TestClass.asyncOutput(status, method);


        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;

            int errorCode = client.getGroupMessage(result, TestClass.groupId, true, maxCount, beginMsec, endMsec, lastId);
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getGroupMessage return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;

            TestClass.outPutMsg(errorCode, method, beizhu);

            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }

    void getRoomMessage() {
        final String beizhu = "in room " + TestClass.roomId;
        final String method = "getRoomMessage";
        HistoryMessageResult result = new HistoryMessageResult();

        boolean status = client.getRoomMessage(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getRoomMessage in async return error:" + errorCode);
                    return;
                }
                String desc = beizhu + " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + lastId;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, TestClass.roomId, true, fetchTotalCount);
        TestClass.asyncOutput(status, method);


        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;

            int errorCode = client.getRoomMessage(result, TestClass.roomId, true, maxCount, beginMsec, endMsec, lastId);
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getRoomMessage return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;

            TestClass.outPutMsg(errorCode, method, beizhu);

            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }

    void getBroadcastMessage() {
        final String method = "getBroadcastMessage";
        HistoryMessageResult result = new HistoryMessageResult();

        boolean status = client.getBroadcastMessage(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getBroadcastMessage in async return error:" + errorCode);
                    return;
                }
                String desc = " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + count;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, true, fetchTotalCount);
        TestClass.asyncOutput(status, method);


        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;

            int errorCode = client.getBroadcastMessage(result, true, maxCount, beginMsec, endMsec, lastId);
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getBroadcastMessage return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;

            TestClass.outPutMsg(errorCode, method);

            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }

    //------------------------[ Chat Histories Demo ]-------------------------//
    void getP2PChat() {
        final String beizhu = "in user " + TestClass.peerUid;
        final String method = "getP2PChat";
        HistoryMessageResult result = new HistoryMessageResult();

        boolean status = client.getP2PChat(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getP2PChat in async return error:" + errorCode);
                    return;
                }
                String desc = beizhu + " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + count;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, TestClass.peerUid, true, fetchTotalCount);
        TestClass.asyncOutput(status, method);


        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;

            int errorCode = client.getP2PChat(result, TestClass.peerUid, true, maxCount, beginMsec, endMsec, lastId);
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getP2PChat in sync return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;


            TestClass.outPutMsg(errorCode, method, beizhu);

            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }

    void getGroupChat() {
        final String beizhu = "in group " + TestClass.groupId;
        final String method = "getGroupChat";
        HistoryMessageResult result = new HistoryMessageResult();

        boolean status = client.getGroupChat(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getGroupChat in async return error:" + errorCode);
                    return;
                }
                String desc = beizhu + " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + count;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, TestClass.groupId, true, fetchTotalCount);
        TestClass.asyncOutput(status, method);


        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;

            int errorCode = client.getGroupChat(result, TestClass.groupId, true, maxCount, beginMsec, endMsec, lastId);
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getGroupChat in sync return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;
            TestClass.outPutMsg(errorCode, method, beizhu);

            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }

    void getRoomChat() {
        final String beizhu = "in room " + TestClass.roomId;
        final String method = "getRoomChat";
        HistoryMessageResult result = new HistoryMessageResult();

        boolean status = client.getRoomChat(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getRoomChat in async return error:" + errorCode);
                    return;
                }
                String desc = beizhu + " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + count;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, TestClass.roomId, true, fetchTotalCount);
        TestClass.asyncOutput(status, method);


        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;

            int errorCode = client.getRoomChat(result, TestClass.roomId, true, maxCount, beginMsec, endMsec, lastId);
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getRoomChat in sync return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;
            TestClass.outPutMsg(errorCode, method, beizhu);

            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }

    void getBroadcastChat() {
        final String method = "getBroadcastChat";
        HistoryMessageResult result = new HistoryMessageResult();

        boolean status = client.getBroadcastChat(new HistoryMessageCallback() {
            @Override
            public void call(int count, long lastId, long beginMsec, long endMsec, List<HistoryMessage> messages, int errorCode) {
                if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                    mylog.log("getBroadcastChat in async return error:" + errorCode);
                    return;
                }
                String desc = " total num:" + count + " beginMsec:" + beginMsec + " endMsec" + endMsec + " lastId:" + count;
                TestClass.outPutMsg(errorCode, method, desc, 0, false);
                displayHistoryMessages(messages);
            }
        }, true, fetchTotalCount);
        TestClass.asyncOutput(status, method);


        int count = fetchTotalCount;
        long beginMsec = 0;
        long endMsec = 0;
        long lastId = 0;
        int fetchedCount = 0;

        while (count > 0) {
            int maxCount = (count > 20) ? 20 : count;
            count -= maxCount;

            int errorCode = client.getBroadcastChat(result, true, maxCount, beginMsec, endMsec, lastId);
            if (errorCode != ErrorCode.FPNN_EC_OK.value()) {
                mylog.log("getBroadcastChat in sync return error:" + errorCode);
                continue;
            }
            if (result.messages == null || result.messages.size() <= 0)
                break;
            TestClass.outPutMsg(errorCode, method);

            fetchedCount += result.count;
            displayHistoryMessages(result.messages);

            beginMsec = result.beginMsec;
            endMsec = result.endMsec;
            lastId = result.lastId;
        }
    }
}

