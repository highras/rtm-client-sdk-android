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

import static java.lang.Thread.sleep;

class AsyncStressTester {

    private static String gate;

    private static long sendCount = 0;
    private static long recvCount = 0;
    private static long recvError = 0;
    private static long timecost = 0;       //-- in usec

    private static LinkedList<Tester> threads;

    private static void incSend() {

        synchronized (AsyncStressTester.class) {

            sendCount += 1;
        }
    }
    private static void incRecv() {
        synchronized (AsyncStressTester.class) {
            recvCount += 1;
        }
    }
    private static void incRecvError() {
        synchronized (AsyncStressTester.class) {
            recvError += 1;
        }
    }
    private static void addTimecost(long cost) {    //-- in usec
        synchronized (AsyncStressTester.class) {
            timecost += cost;
        }
    }

    private class Tester extends Thread {

        private RTMClient client;
        private int sleepMills;
        private int batchCount;
        private FPData buffer;

        Tester(String endpoint, int qps) {

            client = new RTMClient(
                    "52.83.245.22:13325",
                    1000012,
                    654321,
                    "E397902F2C03C4216D115A488E2F687C",
                    null,
                    new HashMap<String, String>(),
                    true,
                    20 * 1000,
                    true
            );

            sleepMills = 1000 / qps;
            batchCount = 1;

            buffer = buildStandardTestQuest();

            while (sleepMills == 0) {
                batchCount += 1;
                sleepMills = 1000 * batchCount / qps;
            }

            System.out.println("-- single client qps: " + qps + ", sleep millisecond interval: " + sleepMills + ", batch count: " + batchCount);
        }

        private FPData buildStandardTestQuest() {
            LinkedList<Object> array = new LinkedList<>();
            array.add("first_vec");
            array.add(4);

            TreeMap<String, Object> map = new TreeMap<>();
            map.put("map1","first_map");
            map.put("map2",true);
            map.put("map3",5);
            map.put("map4",5.7);
            map.put("map5","中文");

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
            try {
                client.connect(gate, 20 * 1000);

                while (true) {
                    long startTime = System.nanoTime();

                    for (int i = 0; i < batchCount; i++) {

                        final long send_time = System.nanoTime();
                        buffer.setSeq(buffer.getSeq() + 1);

                        client.sendQuest(buffer, new FPCallback.ICallback() {

                                    @Override
                                    public void callback(CallbackData cbd) {

                                        if (cbd.getException() != null) {
                                            incRecvError();
                                        }else{
                                            incRecv();
                                            long recv_time = System.nanoTime();
                                            long diff = recv_time - send_time;
                                            addTimecost(diff/1000);
                                        }
                                    }
                                }, 5 * 1000);

                                incSend();
                    }

                    long finishTime = System.nanoTime();
                    long sleepTime = sleepMills - (finishTime - startTime) / 1000000;
                    if (sleepTime > 0)
                        sleep(sleepTime);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    AsyncStressTester(String endpoint, int clientCount, int totalQPS) {
        threads = new LinkedList<>();

        gate = endpoint;
        int qps = totalQPS / clientCount;
        if (qps == 0)
            qps = 1;
        int remain = totalQPS - qps * clientCount;

        for (int i = 0; i < clientCount; i++) {
            Tester test = new Tester(endpoint, qps);
            threads.add(test);
        }

        if (remain > 0) {
            Tester test = new Tester(endpoint, remain);
            threads.add(test);
        }
    }

    void launch() {
        for (Tester test : threads) {
            test.start();
        }
    }

    void showStatistics() throws InterruptedException {

        int sleepSeconds = 3;

        long sendSt;
        long recvSt;
        long recvErrorSt;
        long timecostSt;

        synchronized (AsyncStressTester.class) {
            sendSt = sendCount;
            recvSt = recvCount;
            recvErrorSt = recvError;
            timecostSt = timecost;
        }

        while (true)
        {
            long start = System.nanoTime();

            sleep(sleepSeconds * 1000);

            long s;
            long r;
            long re;
            long tc;

            synchronized (AsyncStressTester.class) {
                s = sendCount;
                r = recvCount;
                re = recvError;
                tc = timecost;
            }

            long ent = System.nanoTime();

            long ds = s - sendSt;
            long dr = r - recvSt;
            long dre = re - recvErrorSt;
            long dtc = tc - timecostSt;

            sendSt = s;
            recvSt = r;
            recvErrorSt = re;
            timecostSt = tc;

            long real_time = ent - start;

            if (dr > 0)
                dtc = dtc / dr;

            ds = ds * 1000 * 1000 * 1000 / real_time;
            dr = dr * 1000 * 1000 * 1000 / real_time;
            //dse = dse * 1000 * 1000 * 1000 / real_time;
            //dre = dre * 1000 * 1000 * 1000 / real_time;

            System.out.println("time interval: " + (real_time / 1000000.0) + " ms, recv error: " + dre);
            System.out.println("[QPS] send: " + ds + ", recv: " + dr + ", per quest time cost: " + dtc + " usec");
        }
    }
}
