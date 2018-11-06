package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.common.NetworkHelper;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FaqUseCase extends BaseNetworkUseCase implements Repository.Faq {

    private final MoldeRestfulService.Faq mFaqService;

    public FaqUseCase(MoldeRestfulService.Faq mFaqService,
                      FromSchemaToEntity fromSchemaToEntity,
                      ToastHelper toastHelper,
                      NetworkHelper networkHelper) {
        super(fromSchemaToEntity, toastHelper, networkHelper);

        this.mFaqService = mFaqService;
    }

    @Override
    public Observable<List<FaqEntity>> getFaqList() {

        if (!isNetworkConnected()) return Observable.empty();

        return mFaqService
                .getMyFaqListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(faqResponseSchema -> {
                    List<FaqEntity> entities
                            = getFromSchemaToEntity().faqNS(faqResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    @Override
    public Observable<Result> createNewFaq(String userId, String userName, String content, String email) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFaqService
                .createNewFaqObservable(userId, userName, content, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
