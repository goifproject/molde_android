package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.repository.FromSchemaToEntity;
import com.limefriends.molde.model.entity.safehouse.SafehouseEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.NetworkHelper;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SafehouseUseCase extends BaseNetworkUseCase implements Repository.Safehouse {

    private final MoldeRestfulService.Safehouse mSafehouseService;

    public SafehouseUseCase(MoldeRestfulService.Safehouse mSafehouseService,
                            FromSchemaToEntity fromSchemaToEntity,
                            ToastHelper toastHelper,
                            NetworkHelper networkHelper) {
        super(fromSchemaToEntity, toastHelper, networkHelper);

        this.mSafehouseService = mSafehouseService;
    }

    @Override
    public Observable<SafehouseEntity> getSafehouse(
            double safeLat, double safeLng, int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mSafehouseService
                .getSafehouseObservable(safeLat, safeLng, perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(safehouseResponseSchema -> {
                    List<SafehouseEntity> entities
                            = getFromSchemaToEntity().safehouseNS(safehouseResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.fromIterable(entities);
                });
    }


}
