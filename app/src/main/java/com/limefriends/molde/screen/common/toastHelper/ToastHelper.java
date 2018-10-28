package com.limefriends.molde.screen.common.toastHelper;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

public class ToastHelper {

    private final Context mContext;

    public ToastHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void showShortToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public void showNetworkError() {
        Toast.makeText(mContext, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
    }

    public void showNoNetworkError() {
        Toast.makeText(mContext, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar(View container, String msg) {
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }

}
