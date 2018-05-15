package com.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fpnn.event.FPEvent;
import com.fpnn.nio.ThreadPool;
import com.rtm.R;
import com.rtm.RTMClient;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Thread.sleep;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        System.out.println(new String("Test with activity!"));

        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                // case 1
//                baseTest();

                // case 2
                asyncStressTest();

                // case 3
//                singleClientConcurrentTest();
            }
        });
    }

    public void baseTest() {

        byte[] file = this.loadKey("key/test-secp256k1-public.der");
        byte[] bytes = this.loadKey("key/test-secp256k1-public.der-false");

        new TestCase(bytes, file);
    }

    public static void asyncStressTest() {

        String endpoint = "35.167.185.139:13013";

        int clientCount = 10;
        int totalQPS = 1800;

        /*
        System.out.println("Usage:");
        System.out.println("\tasyncStressClient [endpoint] [clientCount] [totalQPS]");
        System.out.println("\tdefault: endpoint: <please change code>, clientCount = 100, totalQPS = 10000.");
        */

        AsyncStressTester tester = new AsyncStressTester(endpoint, clientCount, totalQPS);
        tester.launch();

        try {

            tester.showStatistics();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public static void singleClientConcurrentTest() {

        String endpoint = "35.167.185.139:13013";

        RTMClient client = new RTMClient(
                "highras-rtm-rtmgated.ifunplus.cn:13325",
                1017,
                654321,
                "69D963B0978348179BC1B90422AF10DB",
                null,
                false,
                true,
                20 * 1000,
                true
        );

        FPEvent.IListener listener = new FPEvent.IListener() {

            @Override
            public void fpEvent(FPEvent event) {

                switch (event.getType()) {
                    case "connect":
                        System.out.println("\nconnect");
                        incConnectSuccess();
                        break;
                    case "close":
                        System.out.println("\nclose");
                        incConnectionClosed();
//                        System.out.print('~');
                        break;
                    case "error":
                        event.getException().printStackTrace();
                        break;
                }
            }
        };

        client.getEvent().addListener("connect", listener);
        client.getEvent().addListener("close", listener);
        client.getEvent().addListener("error", listener);

        client.connect(endpoint, 20 * 1000);
        TestHolder.showSignDesc();

        try {

            final int questCount = 30000;
            for (int i = 10; i <= 60; i += 10) {

                System.out.println("\n\n-- Test case begin: " + i + " threads, " + questCount + " quest per thread.");
                TestHolder holder = new TestHolder(client, i, questCount);
                holder.launch();
                holder.stop();
                System.out.println("\n\n-- Test case end: " + i + " threads, " + questCount + " quest per thread.");
                showStatic();
            }

        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        System.out.println("=============== down ====================");

        int i = 1;
        while (connectSuccess != connectionClosed) {

            try {

                sleep(5 * 1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            System.out.println("=============== wait " + (i * 5) + " seconds ====================");
            showStatic();
        }

        //ClientEngine.stop();

        //System.out.println("\n\n-----------------[ Error Info ]------------------------");
        //ErrorRecorder recorder = (ErrorRecorder)ErrorRecorder.getInstance();
        //recorder.println();

        showStatic();
    }

    private static long connectSuccess = 0;
    private static long connectFailed = 0;
    private static long connectionClosed = 0;

    private static void incConnectSuccess() {

        synchronized (TestActivity.class) {

            connectSuccess += 1;
        }
    }

    private static void incConnectFailed() {

        synchronized (TestActivity.class) {

            connectFailed += 1;
        }
    }

    private static void incConnectionClosed() {

        synchronized (TestActivity.class) {

            connectionClosed += 1;
        }
    }

    private static void showStatic() {

        System.out.println("connectSuccess: " + connectSuccess);
        System.out.println("connectFailed: " + connectFailed);
        System.out.println("connectionClosed: " + connectionClosed);
    }

    private byte[] loadKey(String derPath) {

        byte[] bytes = new byte[0];
        InputStream inputStream = null;

        try {

            inputStream = getAssets().open(derPath);

            int size = inputStream.available();
            bytes = new byte[size];

            inputStream.read(bytes);
            inputStream.close();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return bytes;
    }
}
