package com.limefriends.molde.molde_backend;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limefriends.molde.menu_mypage.faq.FaqProxy;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by haams on 2017-12-04.
 */

public class Network {
    public static Network network;
    private Retrofit retrofit;

    private FaqProxy faqProxy;

    public static Network getNetworkInstance() {
        if (network == null) {
            network = new Network();
        }
        return network;
    }

    public Network() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setLenient().create();
        // 서버 연결 (Java -> Json (GsonConverterFactory 연결)
        retrofit = new Retrofit.Builder().baseUrl("http://13.209.64.183:7019/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build()).build();
        faqProxy = new FaqProxy(retrofit);
    }

    public FaqProxy getFaqProxy() {
        return faqProxy;
    }
}
