package com.limefriends.molde.common.manager.cacheHelper;

import android.util.Log;

class FileCacheAleadyExistException extends Throwable {

    FileCacheAleadyExistException(String format) {
        Log.e("CacheAleadyExistE", format);
    }
}
