package com.limefriends.molde.comm.manager.cache_manager;

import android.util.Log;

class FileCacheAleadyExistException extends Throwable {

    FileCacheAleadyExistException(String format) {
        Log.e("CacheAleadyExistE", format);
    }
}
