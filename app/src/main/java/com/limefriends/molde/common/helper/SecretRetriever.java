package com.limefriends.molde.common.helper;

import android.content.Context;

import com.limefriends.molde.R;

public class SecretRetriever {

    private Context context;

    public SecretRetriever(Context context) {
        this.context = context;
    }

    public String getTMapApiKey() {
        return context.getResources().getString(R.string.tmap_key);
    }
}
