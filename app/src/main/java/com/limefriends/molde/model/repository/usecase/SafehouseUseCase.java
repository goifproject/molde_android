package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.safehouse.SafehouseEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SafehouseUseCase implements Repository.Safehouse {

    private final MoldeRestfulService.Safehouse mSafehouseService;
    private final FromSchemaToEntity mFromSchemaToEntity;

    public SafehouseUseCase(MoldeRestfulService.Safehouse mSafehouseService, FromSchemaToEntity mFromSchemaToEntity) {
        this.mSafehouseService = mSafehouseService;
        this.mFromSchemaToEntity = mFromSchemaToEntity;
    }

    @Override
    public Observable<SafehouseEntity> getSafehouse(
            double safeLat, double safeLng, int perPage, int page) {
        return mSafehouseService
                .getSafehouseObservable(safeLat, safeLng, perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(safehouseResponseSchema -> {
                    List<SafehouseEntity> entities
                            = mFromSchemaToEntity.safehouseNS(safehouseResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                });
    }


}
