package com.rtmsdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {
    /**
     * 没有连接网络
     */
    public static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    public static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    public static final int NETWORK_WIFI = 1;

    public static final int NETWORK_NOTINIT = -3;
    /**
     * 得到当前网络的状态
     *
     * @param context
     * @return
     */
    static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo gprsInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        // 判断是否是Connected事件
        boolean wifiConnected = false;
        boolean gprsConnected = false;
        if (wifiInfo != null && wifiInfo.isConnected()) {
            wifiConnected = true;
        }
        if (gprsInfo != null && gprsInfo.isConnected()) {
            gprsConnected = true;
        }
        if (wifiConnected || gprsConnected) {
            return true;
        }

        // 判断是否是Disconnected事件，注意：处于中间状态的事件不上报给应用！上报会影响体验
        boolean wifiDisconnected = false;
        boolean gprsDisconnected = false;
        if (wifiInfo == null || wifiInfo != null && wifiInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            wifiDisconnected = true;
        }
        if (gprsInfo == null || gprsInfo != null && gprsInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            gprsDisconnected = true;
        }
        if (wifiDisconnected && gprsDisconnected) {
            return false;
        }
        return true;
    }
}