package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.repository.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.common.NetworkHelper;
import com.limefriends.molde.networking.schema.cardNews.CardNewsResponseSchema;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CardNewsUseCase extends BaseNetworkUseCase implements Repository.CardNews {

    private final MoldeRestfulService.CardNews mCardNewsService;

    public CardNewsUseCase(MoldeRestfulService.CardNews cardNewsService,
                           FromSchemaToEntity fromSchemaToEntity,
                           ToastHelper toastHelper,
                           NetworkHelper networkHelper) {
        super(fromSchemaToEntity, toastHelper, networkHelper);

        this.mCardNewsService = cardNewsService;
    }

    @Override
    public Observable<List<CardNewsEntity>> getCardNewsList(int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCardNewsService.getCardNewsListObservable(perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(this::getCardNewsList);
    }

    @Override
    public Observable<List<CardNewsEntity>> getCardNewsListById(int newsId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mCardNewsService.getCardNewsByIdObservable(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(this::getCardNewsList);
    }

    private Observable<List<CardNewsEntity>> getCardNewsList(CardNewsResponseSchema schema) {
        List<CardNewsEntity> entities = getFromSchemaToEntity().cardNewsNS(schema.getData());
        return entities == null ? Observable.empty() : Observable.just(entities);
    }

}
