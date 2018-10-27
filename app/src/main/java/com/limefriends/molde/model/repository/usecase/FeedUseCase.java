package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.model.repository.FromSchemaToEntity;
import com.limefriends.molde.model.entity.feed.FeedEntity;
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

public class FeedUseCase extends BaseNetworkUseCase implements Repository.Feed {

    private final MoldeRestfulService.Feed mFeedService;

    public FeedUseCase(MoldeRestfulService.Feed feedService,
                       FromSchemaToEntity fromSchemaToEntity,
                       ToastHelper toastHelper,
                       NetworkHelper networkHelper) {
        super(fromSchemaToEntity, toastHelper, networkHelper);

        this.mFeedService = feedService;
    }

    @Override
    public Observable<List<FeedEntity>> getMyFeed(String userId, int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedService
                .getMyFeedObservable(userId, perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(feedResponseSchema -> {
                    List<FeedEntity> entities
                            = getFromSchemaToEntity().feedNS(feedResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    @Override
    public Observable<List<FeedEntity>> getPagedFeedByDate(int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedService
                .getPagedFeedByDateObservable(perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(feedResponseSchema -> {
                    List<FeedEntity> entities
                            = getFromSchemaToEntity().feedNS(feedResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    @Override
    public Observable<List<FeedEntity>> getPagedFeedByDistance(double lat, double lng, int perPage, int page) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedService
                .getPagedFeedByDistanceObservable(lat, lng, perPage, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(feedResponseSchema -> {
                    List<FeedEntity> entities
                            = getFromSchemaToEntity().feedNS(feedResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    @Override
    public Observable<List<FeedEntity>> getFeedById(int reportId) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedService
                .getFeedByIdObservable(reportId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(feedResponseSchema -> {
                    List<FeedEntity> entities
                            = getFromSchemaToEntity().feedNS(feedResponseSchema.getData());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }

    @Override
    public Observable<Result> reportNewFeed(String userId, String userName,
                                            String userEmail, String reportContent,
                                            double reportLat, double reportLng,
                                            String reportAddress, String reportDetailAddress,
                                            int reportState, long reportDate,
                                            List<MultipartBody.Part> reportImageList) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedService
                .reportNewFeedObservable(
                        userId, userName, userEmail, reportContent, reportLat,
                        reportLng, reportAddress, reportDetailAddress, reportState,
                        System.currentTimeMillis(), reportImageList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> updateFeed(int reportId, int state) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedService
                .updateFeedObservable(reportId, state)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Result> deleteFeed(String userId, int state) {

        if (!isNetworkConnected()) return Observable.empty();

        return mFeedService
                .deleteFeedObservable(userId, state)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
