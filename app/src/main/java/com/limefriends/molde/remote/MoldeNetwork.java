package com.limefriends.molde.remote;

import com.limefriends.molde.BuildConfig;
import com.limefriends.molde.comm.MoldeApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoldeNetwork {

    private static final int CONNECT_TIMEOUT = 3;

    private static MoldeNetwork warmUp;

    private Retrofit mRetrofit;

    public static MoldeNetwork getInstance() {
        if (warmUp == null) {
            warmUp = new MoldeNetwork();
        }
        return warmUp;
    }

    public <T> T generateService(Class<T> clazz) {
        return buildRetrofit().create(clazz);
    }

    private Retrofit buildRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(buildOkHttpClient())
                    .build();
        }
        return mRetrofit;
    }

    private OkHttpClient buildOkHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

}
