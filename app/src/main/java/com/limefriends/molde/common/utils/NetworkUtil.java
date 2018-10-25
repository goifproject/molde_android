package com.limefriends.molde.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        manager.getActiveNetworkInfo();

        boolean bwimax = false;
        if (wimax != null) {
            bwimax = wimax.isConnected();
        }

        if (mobile != null) {
            if (mobile.isConnected() || wifi.isConnected() || bwimax) {
                return true;
            }
        } else {
            if (wifi.isConnected() || bwimax) {
                return true;
            }
        }
        return false;
    }

}
