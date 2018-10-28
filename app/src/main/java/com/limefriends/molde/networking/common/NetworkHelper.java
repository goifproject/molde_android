package com.limefriends.molde.networking.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 웬만하면 static 으로 context 를 넘기지 않는 것이 좋을 것 같다. 따라서 의존성으로 주입해주기로 함
 * ctrl+c,v 할 수 있는 코드만 util 로 사용할 것
 */
public class NetworkHelper {

    private Context mContext;

    public NetworkHelper(Context context) {
        this.mContext = context;
    }

    public boolean isNetworkConnected() {

        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
