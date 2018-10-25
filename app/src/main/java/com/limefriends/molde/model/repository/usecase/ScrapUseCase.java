package com.limefriends.molde.model.repository.usecase;

import android.util.Log;

import com.limefriends.molde.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.model.entity.scrap.ScrapEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScrapUseCase implements Repository.Scrap {

    private final MoldeRestfulService.Scrap mScrapService;
    private final MoldeRestfulService.CardNews mCardNewsService;
    private final FromSchemaToEntity mFromSchemaToEntity;

    public ScrapUseCase(MoldeRestfulService.Scrap mScrapService,
                        MoldeRestfulService.CardNews mCardNewsService,
                        FromSchemaToEntity mFromSchemaToEntity) {
        this.mScrapService = mScrapService;
        this.mCardNewsService = mCardNewsService;
        this.mFromSchemaToEntity = mFromSchemaToEntity;
    }

    @Override
    public Observable<ScrapEntity> getScrap(String userId, int cardNewsId) {
        return mScrapService
                .getMyScrapObservable(userId, cardNewsId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(scrapResponseSchema -> {
                    List<ScrapEntity> entity
                            = mFromSchemaToEntity.scrapNS(scrapResponseSchema.getData());
                    return entity == null ? Observable.empty() : Observable.just(entity.get(0));
                });
    }

    @Override
    public Observable<CardNewsEntity> getScrapList(String userId, int perPage, int currentPage) {
        return mScrapService
                .getMyScrapListObservable(userId, perPage, currentPage)
                .subscribeOn(Schedulers.io())

                // fetch my scrap
                .flatMap(scrapResponseSchema -> {
                    Log.e("Thread1", Thread.currentThread().getName());
                    List<ScrapEntity> entities = mFromSchemaToEntity.scrapNS(scrapResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                })
                // fetch card news
                .flatMap(scrapEntity
                        -> {
                    Log.e("Thread2", Thread.currentThread().getName());
                    return mCardNewsService
                            .getCardNewsByIdObservable(scrapEntity.getNewsId())
                            .subscribeOn(Schedulers.io());
                })

                // cardNewsSchema -> cardNewsEntity
                .flatMap(cardNewsSchema -> {
                    Log.e("Thread3", Thread.currentThread().getName());
                    List<CardNewsEntity> entities = mFromSchemaToEntity.cardNewsNS(cardNewsSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just((entities.get(0)));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Observable<Result> addToMyScrap(String userId, int newsId) {
        return mScrapService
                .addToMyScrapObservable(userId, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteMyScrap(String userId, int newsId) {
        return mScrapService
                .deleteMyScrapObservable(userId, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
