package com.limefriends.molde.common.di;

import com.limefriends.molde.BuildConfig;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.limefriends.molde.BuildConfig.SERVER_URL;
import static com.limefriends.molde.BuildConfig.TMAP_URL;

public class CompositionRoot {

    private static final int CONNECT_TIMEOUT = 3;

    private Retrofit mRetrofit, mTMapRetrofit;

    public MoldeRestfulService.Comment getCommentRestfulService() {
        return generateServerService(MoldeRestfulService.Comment.class);
    }

    public MoldeRestfulService.Faq getFaqRestfulService() {
        return generateServerService(MoldeRestfulService.Faq.class);
    }

    public MoldeRestfulService.Favorite getFavoriteRestfulService() {
        return generateServerService(MoldeRestfulService.Favorite.class);
    }

    public MoldeRestfulService.Feed getFeedRestfulService() {
        return generateServerService(MoldeRestfulService.Feed.class);
    }

    public MoldeRestfulService.FeedResult getFeedResultRestfulService() {
        return generateServerService(MoldeRestfulService.FeedResult.class);
    }

    public MoldeRestfulService.CardNews getCardNewsRestfulService() {
        return generateServerService(MoldeRestfulService.CardNews.class);
    }

    public MoldeRestfulService.Safehouse getSafehouseRestfulService() {
        return generateServerService(MoldeRestfulService.Safehouse.class);
    }

    public MoldeRestfulService.Scrap getScrapRestfulService() {
        return generateServerService(MoldeRestfulService.Scrap.class);
    }

    public MoldeRestfulService.SearchLocation getSearchLocationRestfulService() {
        return generateTMapService(MoldeRestfulService.SearchLocation.class);
    }

    private <T> T generateServerService(Class<T> clazz) {
        return buildRetrofit().create(clazz);
    }

    private <T> T generateTMapService(Class<T> clazz) {
        return buildTMapRetrofit().create(clazz);
    }

    private Retrofit buildRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(buildOkHttpClient())
                    .build();
        }
        return mRetrofit;
    }

    private Retrofit buildTMapRetrofit() {
        if (mTMapRetrofit == null) {
            mTMapRetrofit = new Retrofit.Builder()
                    .baseUrl(TMAP_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(buildOkHttpClient())
                    .build();
        }
        return mTMapRetrofit;
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
