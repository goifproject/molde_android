package com.limefriends.molde.menu_map.cacheManager;

import android.util.Log;

public class FileCacheAleadyExistException extends Throwable {
    public FileCacheAleadyExistException(String format) {
        Log.e("CacheAleadyExistE", format);
    }
}
