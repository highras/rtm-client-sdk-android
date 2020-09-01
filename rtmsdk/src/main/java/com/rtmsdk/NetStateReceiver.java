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
        if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
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