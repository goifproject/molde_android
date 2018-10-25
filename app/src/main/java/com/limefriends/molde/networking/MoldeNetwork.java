package com.limefriends.molde.networking;

import com.limefriends.molde.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
