package com.limefriends.molde.menu_map.cacheManager;


import android.util.Log;

public class FileCacheNotFoundException extends Throwable {
    public FileCacheNotFoundException(String format) {
        Log.e("CacheNotFoundE", format);
    }
}
