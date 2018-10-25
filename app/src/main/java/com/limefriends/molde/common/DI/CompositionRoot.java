package com.limefriends.molde.common.DI;

import com.limefriends.molde.BuildConfig;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompositionRoot {

    private static final int CONNECT_TIMEOUT = 3;

    private Retrofit mRetrofit;

    public MoldeRestfulService.Comment getCommentRestfulService() {
        return generateService(MoldeRestfulService.Comment.class);
    }

    public MoldeRestfulService.Faq getFaqRestfulService() {
        return generateService(MoldeRestfulService.Faq.class);
    }

    public MoldeRestfulService.Favorite getFavoriteRestfulService() {
        return generateService(MoldeRestfulService.Favorite.class);
    }

    public MoldeRestfulService.Feed getFeedRestfulService() {
        return generateService(MoldeRestfulService.Feed.class);
    }

    public MoldeRestfulService.FeedResult getFeedResultRestfulService() {
        return generateService(MoldeRestfulService.FeedResult.class);
    }

    public MoldeRestfulService.CardNews getCardNewsRestfulService() {
        return generateService(MoldeRestfulService.CardNews.class);
    }

    public MoldeRestfulService.Safehouse getSafehouseRestfulService() {
        return generateService(MoldeRestfulService.Safehouse.class);
    }

    public MoldeRestfulService.Scrap getScrapRestfulService() {
        return generateService(MoldeRestfulService.Scrap.class);
    }

    private <T> T generateService(Class<T> clazz) {
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
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

}
