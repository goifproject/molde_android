package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.repository.FromSchemaToEntity;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.model.entity.scrap.ScrapEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.NetworkHelper;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ScrapUseCase extends BaseNetworkUseCase implements Repository.Scrap {

    private final MoldeRestfulService.Scrap mScrapService;
    private final MoldeRestfulService.CardNews mCardNewsService;

    public ScrapUseCase(MoldeRestfulService.Scrap mScrapService,
                        MoldeRestfulService.CardNews mCardNewsService,
                        FromSchemaToEntity fromSchemaToEntity,
                        ToastHelper toastHelper,
                        NetworkHelper networkHelper) {
        super(fromSchemaToEntity, toastHelper, networkHelper);
        this.mScrapService = mScrapService;
        this.mCardNewsService = mCardNewsService;
    }

    @Override
    public Observable<ScrapEntity> getScrap(String userId, int cardNewsId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mScrapService
                .getMyScrapObservable(userId, cardNewsId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(scrapResponseSchema -> {
                    List<ScrapEntity> entity
                            = getFromSchemaToEntity().scrapNS(scrapResponseSchema.getData());
                    return entity == null ? Observable.empty() : Observable.just(entity.get(0));
                });
    }

    @Override
    public Observable<CardNewsEntity> getScrapList(String userId, int perPage, int currentPage) {

        if (!isNetworkConnected()) return Observable.empty();

        return mScrapService
                .getMyScrapListObservable(userId, perPage, currentPage)
                .subscribeOn(Schedulers.io())
                // fetch my scrap
                .flatMap(scrapResponseSchema -> {
                    List<ScrapEntity> entities
                            = getFromSchemaToEntity().scrapNS(scrapResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                })
                // fetch card news
                .flatMap(scrapEntity
                        -> mCardNewsService
                            .getCardNewsByIdObservable(scrapEntity.getNewsId())
                            .subscribeOn(Schedulers.io()))
                // cardNewsSchema -> cardNewsEntity
                .flatMap(cardNewsSchema -> {
                    List<CardNewsEntity> entities
                            = getFromSchemaToEntity().cardNewsNS(cardNewsSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just((entities.get(0)));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Observable<Result> addToMyScrap(String userId, int newsId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mScrapService
                .addToMyScrapObservable(userId, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteMyScrap(String userId, int newsId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mScrapService
                .deleteMyScrapObservable(userId, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
