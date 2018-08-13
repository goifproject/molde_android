package com.limefriends.molde.ui.menu_map.cache_manager;


import android.util.Log;

public class FileCacheNotFoundException extends Throwable {
    public FileCacheNotFoundException(String format) {
        Log.e("CacheNotFoundE", format);
    }
}
