package com.limefriends.molde.comm.manager.cache_manager;

import android.util.Log;

public class FileCacheAleadyExistException extends Throwable {
    public FileCacheAleadyExistException(String format) {
        Log.e("CacheAleadyExistE", format);
    }
}
