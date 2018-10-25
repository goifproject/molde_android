package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.schema.news.CardNewsResponseSchema;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CardNewsUseCase implements Repository.CardNews {

    private final MoldeRestfulService.CardNews mCardNewsService;
    private final FromSchemaToEntity mFromSchemaToEntity;

    public CardNewsUseCase(MoldeRestfulService.CardNews cardNewsService,
                           FromSchemaToEntity fromSchemaToEntity) {
        this.mCardNewsService = cardNewsService;
        this.mFromSchemaToEntity = fromSchemaToEntity;
    }

    @Override
    public Observable<List<CardNewsEntity>> getCardNewsList(int perPage, int page) {
        return mCardNewsService.getCardNewsListObservable(perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(this::getCardNewsList);
    }

    @Override
    public Observable<List<CardNewsEntity>> getCardNewsListById(int newsId) {
        return mCardNewsService.getCardNewsByIdObservable(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(this::getCardNewsList);
    }

    private Observable<List<CardNewsEntity>> getCardNewsList(CardNewsResponseSchema schema) {
        List<CardNewsEntity> entities = mFromSchemaToEntity.cardNewsNS(schema.getData());
        return entities == null ? Observable.empty() : Observable.just(entities);
    }

}
