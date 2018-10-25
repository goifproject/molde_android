package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FaqUseCase implements Repository.Faq {

    private final MoldeRestfulService.Faq mFaqService;
    private final FromSchemaToEntity mFromSchemaToEntity;

    public FaqUseCase(MoldeRestfulService.Faq mFaqService, FromSchemaToEntity fromSchemaToEntity) {
        this.mFaqService = mFaqService;
        mFromSchemaToEntity = fromSchemaToEntity;
    }

    @Override
    public Observable<List<FaqEntity>> getFaqList() {
        return mFaqService
                .getMyFaqListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(faqResponseSchema -> {
                    List<FaqEntity> entities
                            = mFromSchemaToEntity.faqNS(faqResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    @Override
    public Observable<Result> createNewFaq(String userId, String userName, String content, String email) {
        return mFaqService
                .createNewFaqObservable(userId, userName, content, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
