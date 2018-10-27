package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.NetworkHelper;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;

public class FeedResultUseCase extends BaseNetworkUseCase implements Repository.FeedResult {

    private final MoldeRestfulService.FeedResult mFeedResultService;

    public FeedResultUseCase(MoldeRestfulService.FeedResult mFeedResultService,
                             ToastHelper toastHelper,
                             NetworkHelper networkHelper) {
        super(toastHelper, networkHelper);

        this.mFeedResultService = mFeedResultService;
    }

    @Override
    public Observable<Result> reportFeedResult(int reportId, List<MultipartBody.Part> reportImageList) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedResultService
                .reportFeedResultObservable(reportId, reportImageList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
