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
                "35.167.185.139:13325",
                1000012,
                654321,
                "5C65CD872903AAB37211EC468B4A1364",
                null,
                false,
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

        int timeout = 20 * 1000;
        int sleep = 1000;

        System.out.println("\ntest start!");

        //---------------------------------sendMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.sendMessage(to, (byte) 8, "hello !", "", timeout, new FPCallback.ICallback() {

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

        //---------------------------------sendMessages--------------------------------------
        this.threadSleep(sleep);
        this._client.sendMessages(tos, (byte) 8, "hello !", "", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] sendMessages:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] sendMessages:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------sendGroupMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.sendGroupMessage(gid, (byte) 8, "hello !", "", timeout, new FPCallback.ICallback() {

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

        //---------------------------------sendRoomMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.sendRoomMessage(rid, (byte) 8, "hello !", "", timeout, new FPCallback.ICallback() {

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

        //---------------------------------addVariables--------------------------------------
        this.threadSleep(sleep);
        this._client.addVariables(dict, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] addVariables:");
                    System.out.println(payload.toString());
                } else {

                    System.err.println("\n[ERR] addVariables:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------addFriends--------------------------------------
        this.threadSleep(sleep);
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

        //---------------------------------deleteFriends--------------------------------------
        this.threadSleep(sleep);
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

        //---------------------------------getFriends--------------------------------------
        this.threadSleep(sleep);
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

        //---------------------------------addGroupMembers--------------------------------------
        this.threadSleep(sleep);
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

        //---------------------------------deleteGroupMembers--------------------------------------
        this.threadSleep(sleep);
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

        //---------------------------------getGroupMembers--------------------------------------
        this.threadSleep(sleep);
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

        //---------------------------------getUserGroups--------------------------------------
        this.threadSleep(sleep);
        this._client.getUserGroups(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<Long> payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getUserGroups:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getUserGroups:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------enterRoom--------------------------------------
        this.threadSleep(sleep);
        this._client.enterRoom(rid, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] enterRoom:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] enterRoom:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------leaveRoom--------------------------------------
        this.threadSleep(sleep);
        this._client.leaveRoom(rid, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] leaveRoom:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] leaveRoom:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getUserRooms--------------------------------------
        this.threadSleep(sleep);
        this._client.getUserRooms(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<Long> payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getUserRooms:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getUserRooms:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getOnlineUsers--------------------------------------
        this.threadSleep(sleep);
        this._client.getOnlineUsers(tos, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<Long> payload = (List<Long>) obj;
                    System.out.println("\n[DATA] getOnlineUsers:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getOnlineUsers:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------checkUnreadMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.checkUnreadMessage(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] checkUnreadMessage:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] checkUnreadMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getGroupMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.getGroupMessage(gid, 10, false, 0, 0, 0, null, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getGroupMessage:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getGroupMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getRoomMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.getRoomMessage(rid, 10, false, 0, 0, 0, null, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getRoomMessage:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getRoomMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getBroadcastMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.getBroadcastMessage(10, false, 0, 0, 0, null, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getBroadcastMessage:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getBroadcastMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getP2PMessage--------------------------------------
        this.threadSleep(sleep);
        this._client.getP2PMessage(to, 10, 0, false, 0, 0, 0, null, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getP2PMessage:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getP2PMessage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------addDevice--------------------------------------
        this.threadSleep(sleep);
        this._client.addDevice("app-info", "device-token", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] addDevice:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] addDevice:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------removeDevice--------------------------------------
        this.threadSleep(sleep);
        this._client.removeDevice("device-token", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] removeDevice:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] removeDevice:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------setTranslationLanguage--------------------------------------
        this.threadSleep(sleep);
        this._client.setTranslationLanguage("en", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] setTranslationLanguage:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] setTranslationLanguage:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------translate--------------------------------------
        this.threadSleep(sleep);
        this._client.translate("你好!", null, "en", timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] translate:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] translate:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------setGeo--------------------------------------
        this.threadSleep(sleep);
        this._client.setGeo(lat, lng, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] setGeo:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] setGeo:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getGeo--------------------------------------
        this.threadSleep(sleep);
        this._client.getGeo(timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    Map payload = (Map) obj;
                    System.out.println("\n[DATA] getGeo:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getGeo:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------getGeos--------------------------------------
        this.threadSleep(sleep);
        this._client.getGeos(tos, timeout, new FPCallback.ICallback() {

            @Override
            public void callback(CallbackData cbd) {

                Object obj = cbd.getPayload();

                if (obj != null) {

                    List<ArrayList> payload = (List<ArrayList>) obj;
                    System.out.println("\n[DATA] getGeos:");
                    System.out.println(payload);
                } else {

                    System.err.println("\n[ERR] getGeos:");
                    System.err.println(cbd.getException().getMessage());
                }
            }
        });

        //---------------------------------sendFile--------------------------------------
        this.threadSleep(sleep);
        this._client.sendFile((byte) 8, to, this._fileBytes, 20 * 1000, new FPCallback.ICallback() {

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
