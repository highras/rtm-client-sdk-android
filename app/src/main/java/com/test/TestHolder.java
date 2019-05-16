package com.test;

import com.fpnn.FPData;
import com.fpnn.callback.CallbackData;
import com.fpnn.callback.FPCallback;
import com.rtm.RTMClient;
import com.rtm.msgpack.PayloadPacker;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class TestHolder {

    static void showSignDesc() {

//        System.out.println("Sign:");
//        System.out.println("    +: establish connection");
//        System.out.println("    ~: close connection");
//        System.out.println("    #: connection error");
//
//        System.out.println("    *: send sync quest");
//        System.out.println("    &: send async quest");
//
//        System.out.println("    ^: sync answer Ok");
//        System.out.println("    ?: sync answer exception");
//        System.out.println("    |: sync answer exception by connection closed");
//        System.out.println("    (: sync operation fpnn exception");
//        System.out.println("    ): sync operation unknown exception");
//
//        System.out.println("    $: async answer Ok");
//        System.out.println("    @: async answer exception");
//        System.out.println("    ;: async answer exception by connection closed");
//        System.out.println("    {: async operation fpnn exception");
//        System.out.println("    }: async operation unknown exception");
//
//        System.out.println("    !: close operation");
//        System.out.println("    [: close operation fpnn exception");
//        System.out.println("    ]: close operation unknown exception");
    }

    private LinkedList<Tester> threads;

    TestHolder(RTMClient client, int threadCount, int perThreadQuestCount) {

        threads = new LinkedList<>();

        for (int i = 0; i < threadCount; i++) {

            Tester test = new Tester(client, perThreadQuestCount);
            threads.add(test);
        }
    }

    void launch() {

        for (Tester test : threads) {

            test.start();
        }
    }

    void stop() throws InterruptedException {

        for (Tester test : threads) {

            test.join();
        }
    }

    private class Tester extends Thread {

        private RTMClient client;
        private int questCount;

        Tester(RTMClient client, int questCount) {

            this.client = client;
            this.questCount = questCount;
        }

        private FPData buildStandardTestQuest() {

            LinkedList<Object> array = new LinkedList<>();
            array.add("first_vec");
            array.add(4);

            TreeMap<String, Object> map = new TreeMap<>();
            map.put("map1", "first_map");
            map.put("map2", true);
            map.put("map3", 5);
            map.put("map4", 5.7);
            map.put("map5", "中文");

            Map payload = new HashMap();
            payload.put("quest", "one");
            payload.put("int", 2);
            payload.put("double", 3.3);
            payload.put("boolean", true);
            payload.put("ARRAY", array);
            payload.put("MAP", map);

            FPData data = new FPData();

            data.setFlag(0x1);
            data.setMtype(0x1);
            data.setMethod("two way demo");

            byte[] bytes = new byte[0];
            PayloadPacker packer = new PayloadPacker();

            try {

                packer.pack(payload);
                bytes = packer.toByteArray();
            } catch (IOException ex) {

                ex.printStackTrace();
            }

            data.setPayload(bytes);
            return data;
        }

        @Override
        public void run() {

            int act = 0;
            int k = 0;

            for (int i = 0; i < questCount; i++, k++) {

                if (k == 20) {

                    System.out.println();
                    k = 0;
                }

                long index = (System.currentTimeMillis() + i) % 64;

                if (i >= 10) {

                    if (index < 6)
                        act = 2;    //-- close operation
                    else if (index < 32)
                        act = 1;    //-- async quest
                    else
                        act = 0;    //-- sync quest
                } else {

                    act = (int) index & 0x1;
                }

                try {

                    switch (act) {

                        case 0: {

                            System.out.print('*');
                            CallbackData answer = client.sendQuest(buildStandardTestQuest(), 5 * 1000);
                            if (answer != null) {
                                if (answer.getException() == null)
                                    System.out.print('^');
                                else {
                                    System.out.print('|');
                                }
                            } else
                                System.out.print('?');

                            break;
                        }
                        case 1: {

                            System.out.print('&');
                            client.sendQuest(buildStandardTestQuest(), new FPCallback.ICallback() {
                                @Override
                                public void callback(CallbackData cbd) {
                                    if (cbd.getException() == null) {
                                        System.out.print('@');
                                    } else {
                                        System.out.print(';');
                                    }
                                }
                            }, 5 * 1000);

                            break;
                        }
                        case 2: {

                            System.out.print('!');
                            client.close();
                            break;
                        }
                    }
                } catch (Exception e) {

                    switch (act) {

                        case 0:
                            System.out.print(')');
                            break;
                        case 1:
                            System.out.print('}');
                            break;
                        case 2:
                            System.out.print(']');
                            break;
                    }
                }
            }
        }
    }
}
