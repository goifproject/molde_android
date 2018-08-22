package com.limefriends.molde.comm.manager.cache_manager;


import android.util.Log;

public class FileCacheNotFoundException extends Throwable {
    public FileCacheNotFoundException(String format) {
        Log.e("CacheNotFoundE", format);
    }
}
