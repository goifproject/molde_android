package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FavoriteUseCase implements Repository.Favorite {

    private final MoldeRestfulService.Favorite mFavoriteService;
    private final FromSchemaToEntity mFromSchemaToEntity;

    public FavoriteUseCase(MoldeRestfulService.Favorite favoriteService,
                           FromSchemaToEntity fromSchemaToEntity) {
        this.mFavoriteService = favoriteService;
        this.mFromSchemaToEntity = fromSchemaToEntity;
    }

    @Override
    public Observable<List<FavoriteEntity>> getMyFavorite (
            String userId, int perPage, int page) {
        return mFavoriteService
                .getMyFavoriteObservable(userId, perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(favoriteResponseSchema -> {
                    List<FavoriteEntity> entities
                            = mFromSchemaToEntity.favoriteNS(favoriteResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    @Override
    public Observable<FavoriteEntity> getMyFavoriteByDistance (
            String userId, double lat, double lng) {
        return mFavoriteService
                .getMyFavoriteByDistanceObservable(userId, lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(favoriteResponseSchema -> {
                    List<FavoriteEntity> entities
                            = mFromSchemaToEntity.favoriteNS(favoriteResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                });
    }

    @Override
    public Observable<Result> addToMyFavorite (
            String userId, String name, String address, double lat, double lng) {
        return mFavoriteService
                .addToMyFavoriteObservable(userId, name, address, lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteFavorite (String userId, int favoriteId) {
        return mFavoriteService
                .deleteFavoriteObservable(userId, favoriteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
