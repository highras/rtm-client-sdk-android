package com.rtmsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.util.Objects;

class NetStateReceiver extends BroadcastReceiver {
    private INetEvent mINetEvent = RTMCore.mINetEvent;

    private static int LAST_TYPE = -3;

    @Override
    public void onReceive(Context context, Intent intent) {
        Object b= ConnectivityManager.CONNECTIVITY_ACTION;
        Object a= intent.getAction();
        if ((a == b) || (a != null && a.equals(b))) {
            int netWorkState = NetUtils.getNetWorkState(context);
            if (LAST_TYPE != netWorkState) {
                LAST_TYPE = netWorkState;
                if (mINetEvent != null) {
                    mINetEvent.onNetChange(netWorkState);
                }
            }
        }
    }
}