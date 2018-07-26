package com.limefriends.molde.menu_map.cache_manager;

import android.util.Log;

public class FileCacheAleadyExistException extends Throwable {
    public FileCacheAleadyExistException(String format) {
        Log.e("CacheAleadyExistE", format);
    }
}
