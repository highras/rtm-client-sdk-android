package com.test;

import com.fpnn.callback.CallbackData;
import com.fpnn.callback.FPCallback;
import com.fpnn.event.EventData;
import com.fpnn.event.FPEvent;
import com.rtm.RTMClient;
import com.rtm.RTMConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCase {

    private RTMClient _client;
    private int _sleepCount;
    private byte[] _fileBytes;

    public TestCase(byte[] derKey, byte[] fileBytes) {

        this._client = new RTMClient(
                "52.83.245.22:13325",
                1000012,
                654321,
                "E94BDE822C5FAA1C5912970668D9E52C",
                null,
                new HashMap<String, String>(),
                true,
                20 * 1000,
                true
        );

        final TestCase self = this;
        FPEvent.IListener listener = new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData event) {

                switch (event.getType()) {
                    case "login":
                        if (event.getException() != null) {
                            self.onError(event.getException());
                            break;
                        }
                        self.onLogin(event.getPayload());
                        break;
                    case "close":
                        self.onClose(event.hasRetry());
                        break;
                    case "error":
                        self.onError(event.getException());
                        break;
                }
            }
        };

        this._client.getEvent().addListener("login", listener);
        this._client.getEvent().addListener("close", listener);
        this._client.getEvent().addListener("error", listener);

        this._client.getProcessor().getEvent().addListener(RTMConfig.SERVER_PUSH.recvPing, new FPEvent.IListener() {

            @Override
            public void fpEvent(EventData event) {

                System.out.println("\n[PUSH] ".concat(event.getType()).concat(":"));
                System.out.println(event.getPayload().toString());
            }
        });

        if (derKey != null && derKey.length > 0) {

            this._client.login("secp256k1", derKey, null, false);
        } else {

            this._client.login(null, false);
        }

        this._fileBytes = fileBytes;
    }

    private void onLogin(Object obj) {

        System.out.println("Login on ".concat(obj.toString()));

        long to = 778899;
        long fuid = 778898;

        List<Long> tos = new ArrayList();
        tos.add((long) 654321);
        tos.add(fuid);
        tos.add(to);

        long gid = 999;
        long rid = 666;

        List<Long> friends = new ArrayList();
        friends.add(fuid);
        friends.add(to);

        double lat = 39239.1123;
        double lng = 69394.4850;

        List<Long> gids = new ArrayList();
        gids.add(gid);

        List<Long> rids = new ArrayList();
        rids.add(rid);

        List<String> evets = new ArrayList();
        evets.add(RTMConfig.SERVER_EVENT.login);
        evets.add(RTMConfig.SERVER_EVENT.logout);

        Map dict = new HashMap();
        dict.put("key", "test-value");

        Map<String, String>  attrs = new HashMap<String, String>();
        attrs.put("user1", "test user1 attrs");


        int timeout = 20 * 1000;
        int sleep = 1000;

        System.out.println("\ntest start!");
        this.threadSleep(sleep);

        //rtmGate (2)
        //---------------------------------sendMessage--------------------------------------
        this._client.sendMessage(to, (byte) 8, "hello !", "", 0, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] sendMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] sendMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (3)
        //---------------------------------sendGroupMessage--------------------------------------
        this._client.sendGroupMessage(gid, (byte) 8, "hello !", "", 0, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] sendGroupMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] sendGroupMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (4)
        //---------------------------------sendRoomMessage--------------------------------------
        this._client.sendRoomMessage(rid, (byte) 8, "hello !", "", 0, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] sendRoomMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] sendRoomMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (5)
        //---------------------------------getUnreadMessage--------------------------------------
        this._client.getUnreadMessage(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getUnreadMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getUnreadMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (6)
        //---------------------------------cleanUnreadMessage--------------------------------------
        this._client.cleanUnreadMessage(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] cleanUnreadMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] cleanUnreadMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (7)
        //---------------------------------getSession--------------------------------------
        this._client.getSession(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getSession:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getSession:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (8)
        //---------------------------------getGroupMessage--------------------------------------
        this._client.getGroupMessage(gid, true, 10, 0, 0, 0,  timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getGroupMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getGroupMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (9)
        //---------------------------------getRoomMessage--------------------------------------
        this._client.getRoomMessage(rid, true, 10, 0, 0, 0, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getRoomMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getRoomMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (10)
        //---------------------------------getBroadcastMessage--------------------------------------
        this._client.getBroadcastMessage(true, 10, 0, 0, 0, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getBroadcastMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getBroadcastMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (11)
        //---------------------------------getP2PMessage--------------------------------------
        this._client.getP2PMessage(to,true,10,0,0,0, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getP2PMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getP2PMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (12)
        //---------------------------------fileToken--------------------------------------
        this._client.fileToken("sendfile", null, to, 0, 0, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] fileToken:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] fileToken:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (14)
        //---------------------------------addAttrs--------------------------------------
        this._client.addAttrs(attrs, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] addAttrs:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] addAttrs:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (15)
        //---------------------------------getAttrs--------------------------------------
        this._client.getAttrs(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getAttrs:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getAttrs:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (16)
        //---------------------------------addDebugLog--------------------------------------
        this._client.addDebugLog("msg", "attrs", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] addDebugLog:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] addDebugLog:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (17)
        //---------------------------------addDevice--------------------------------------
        this._client.addDevice("app-info", "device-token", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] addDevice:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] addDevice:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (18)
        //---------------------------------removeDevice--------------------------------------
        this._client.removeDevice("device-token", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] removeDevice:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] removeDevice:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (19)
        //---------------------------------setTranslationLanguage--------------------------------------
        this._client.setTranslationLanguage("en", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] setTranslationLanguage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] setTranslationLanguage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (20)
        //---------------------------------translate--------------------------------------
        this._client.translate("你好!", null, "en", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] translate:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] translate:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (21)
        //---------------------------------addFriends--------------------------------------
        this._client.addFriends(friends, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] addFriends:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] addFriends:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (22)
        //---------------------------------deleteFriends--------------------------------------
        this._client.deleteFriends(friends, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] deleteFriends:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] deleteFriends:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (23)
        //---------------------------------getFriends--------------------------------------
        this._client.getFriends(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getFriends:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getFriends:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (24)
        //---------------------------------addGroupMembers--------------------------------------
        this._client.addGroupMembers(gid, tos, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] addGroupMembers:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] addGroupMembers:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (25)
        //---------------------------------deleteGroupMembers--------------------------------------
        this._client.deleteGroupMembers(rid, tos, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] deleteGroupMembers:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] deleteGroupMembers:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (26)
        //---------------------------------getGroupMembers--------------------------------------
        this._client.getGroupMembers(gid, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<Long> payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getGroupMembers:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getGroupMembers:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (27)
        //---------------------------------getUserGroups--------------------------------------
        this._client.getUserGroups(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<Long> payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getUserGroups:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getUserGroups:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (28)
        //---------------------------------enterRoom--------------------------------------
        this._client.enterRoom(rid, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] enterRoom:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] enterRoom:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (29)
        //---------------------------------leaveRoom--------------------------------------
        this._client.leaveRoom(rid, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] leaveRoom:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] leaveRoom:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (30)
        //---------------------------------getUserRooms--------------------------------------
        this._client.getUserRooms(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<Long> payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getUserRooms:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getUserRooms:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (31)
        //---------------------------------getOnlineUsers--------------------------------------
        this._client.getOnlineUsers(tos, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<Long> payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getOnlineUsers:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] getOnlineUsers:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (32)
        //---------------------------------deleteMessage--------------------------------------
        this._client.deleteMessage(0, to, (byte)1, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] deleteMessage:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] deleteMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (33)
        //---------------------------------kickout--------------------------------------
        this._client.kickout("", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] kickout:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] kickout:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (35)
        //---------------------------------dbSet--------------------------------------
        this._client.dbSet("db-test-key", "db-test-value", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] dbSet:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] dbSet:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (34)
        //---------------------------------dbGet--------------------------------------
        this._client.dbGet("db-test-key", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] dbGet:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] dbGet:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //fileGate (1)
        //---------------------------------sendFile--------------------------------------
        this._client.sendFile((byte) 50, to, this._fileBytes, 0, 20 * 1000, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] sendFile:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] sendFile:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //fileGate (3)
        //---------------------------------sendGroupFile--------------------------------------
        this._client.sendGroupFile((byte) 50, gid, this._fileBytes, 0, 20 * 1000, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] sendGroupFile:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] sendGroupFile:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //fileGate (4)
        //---------------------------------sendRoomFile--------------------------------------
        this._client.sendRoomFile((byte) 50, rid, this._fileBytes, 0, 20 * 1000, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] sendRoomFile:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] sendRoomFile:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        this.threadSleep(sleep);

        //rtmGate (13)
        //---------------------------------close--------------------------------------
//        this._client.close();

        System.out.println("\ntest end! ".concat(String.valueOf(this._sleepCount - 1)));
    }

    private void onClose(boolean retry) {

        System.out.println(new String("Closed! retry: " + retry));
    }

    private void onError(Exception ex) {

        ex.printStackTrace();
    }

    private void threadSleep(int ms) {

        try {

            Thread.sleep(ms);
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        this._sleepCount++;
    }
}
