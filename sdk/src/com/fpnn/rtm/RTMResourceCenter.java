package com.fpnn.rtm;

import com.fpnn.sdk.ErrorRecorder;
import com.fpnn.sdk.TCPClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

class RTMResourceCenter extends Thread {

    //------------------[ Static Fields & Functions ]---------------------//

    private static volatile boolean created = false;
    private static volatile boolean stoppedCalled = true;
    private static volatile int pingIntervalSeconds = 20;
    private static RTMResourceCenter instance = null;

    public static RTMResourceCenter instance() {

        if (!created) {
            synchronized (RTMResourceCenter.class) {
                if (created)
                    return instance;

                instance = new RTMResourceCenter();
                instance.start();
                created = true;
                stoppedCalled = false;
            }
        }

        return instance;
    }

    public static void close() {
        if (stoppedCalled)
            return;

        synchronized (RTMResourceCenter.class) {
            if (stoppedCalled)
                return;

            if (!created)
                return;

            stoppedCalled = true;
        }

        instance.running = false;
        try {
            instance.join();
        }
        catch (InterruptedException e)
        {
            ErrorRecorder.record("Join Resource Center thread exception.", e);
        }
    }

    public static void setPingInterval(int intervalInSeconds) {
        pingIntervalSeconds = intervalInSeconds;
    }

    //-------------------[ Instance Fields & Functions ]--------------------//

    private class FileGateInfo {
        TCPClient fileGate;
        long lastTaskExpireMilliseconds;

        FileGateInfo(String endpoint, int taskTimeoutInSeconds) {
            fileGate = TCPClient.create(endpoint);
            lastTaskExpireMilliseconds = System.currentTimeMillis() + taskTimeoutInSeconds * 1000;
        }
    }

    private class RTMClientInfo {
        long lastPingTime;
        int refCount;

        RTMClientInfo() {
            lastPingTime = 0;
            refCount = 1;
        }
    }

    private static final int fileGateKeptMilliseconds = 10 * 60 * 1000;     //-- 10 minutes;
    private HashMap<String, FileGateInfo> fileGateCache;
    private HashMap<RTMClient, RTMClientInfo> rtmClients;
    private DuplicatedMseeageFilter midFilter;
    private volatile boolean running;

    private RTMResourceCenter() {
        fileGateCache = new HashMap<>();
        rtmClients = new HashMap<>();
        midFilter = new DuplicatedMseeageFilter();
        running = true;
        setDaemon(true);
    }

    public DuplicatedMseeageFilter getMessageIdFilter() {
        return midFilter;
    }

    public TCPClient getFileClient(String endpoint, int questTimeout) {
        synchronized (this) {
            FileGateInfo gateInfo = fileGateCache.get(endpoint);
            if (gateInfo == null) {
                gateInfo = new FileGateInfo(endpoint, questTimeout);
                fileGateCache.put(endpoint, gateInfo);
            }
            else {
                long newLastExpire = System.currentTimeMillis() + questTimeout * 1000;
                if (gateInfo.lastTaskExpireMilliseconds < newLastExpire)
                gateInfo.lastTaskExpireMilliseconds = newLastExpire;
            }

            return gateInfo.fileGate;
        }
    }

    public void registerRTMClient(RTMClient client) {
        synchronized (this) {
            RTMClientInfo info = rtmClients.get(client);
            if (info == null) {
                info = new RTMClientInfo();
                info.refCount = 1;
            }
            else {
                info.refCount += 1;
            }
            rtmClients.put(client, info);
        }
    }

    public void unregisterRTMClient(RTMClient client) {
        synchronized (this) {
            RTMClientInfo info = rtmClients.get(client);
            if (info != null) {
                if (info.refCount == 1)
                    rtmClients.remove(client);
                else {
                    info.refCount -= 1;
                    rtmClients.put(client, info);
                }
            }
        }
    }

    @Override
    public void run() {

        while (running) {
            cleanFileGate();
            RTMClientPing();
            midFilter.cleanExpiredCache();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                //-- Do nothing.
            }
        }
    }

    private void cleanFileGate() {
        long curr = System.currentTimeMillis();
        long threshold = curr - fileGateKeptMilliseconds;

        HashSet<String> expiredFileGate = new HashSet<>();

        synchronized (this) {
            Iterator<Map.Entry<String, FileGateInfo>> entries = fileGateCache.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, FileGateInfo> entry = entries.next();
                FileGateInfo gateInfo = entry.getValue();

                if (gateInfo.lastTaskExpireMilliseconds <= threshold) {
                    String endpoint = entry.getKey();
                    expiredFileGate.add(endpoint);
                }
            }

            for (String endpoint : expiredFileGate) {
                fileGateCache.remove(endpoint);
            }
        }
    }

    private void RTMClientPing() {
        long curr = System.currentTimeMillis();
        long threshold = curr - pingIntervalSeconds * 1000;

        synchronized (this) {

            Iterator<Map.Entry<RTMClient, RTMClientInfo>> entries = rtmClients.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<RTMClient, RTMClientInfo> entry = entries.next();
                RTMClientInfo info = entry.getValue();
                if (info.lastPingTime <= threshold) {

                    RTMClient client = entry.getKey();
                    client.ping(null);
                    info.lastPingTime = curr;
                }
            }
        }
    }
}
