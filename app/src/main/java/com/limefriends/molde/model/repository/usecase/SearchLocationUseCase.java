package com.limefriends.molde.model.repository.usecase;

import com.limefriends.molde.common.helper.SecretRetriever;
import com.limefriends.molde.model.database.dao.SearchHistoryDao;
import com.limefriends.molde.model.common.FromSchemaToEntity;
import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.common.NetworkHelper;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SearchLocationUseCase extends BaseNetworkUseCase implements Repository.SearchLocation {

    private static final int SEARCH_COUNT = 20;

    private SecretRetriever mSecretRetriever;
    private SearchHistoryDao mSearchHistoryDao;
    private MoldeRestfulService.SearchLocation mSearchLocationService;

    public SearchLocationUseCase(FromSchemaToEntity fromSchemaToEntity,
                                 ToastHelper toastHelper,
                                 NetworkHelper networkHelper,
                                 SecretRetriever secretRetriever,
                                 SearchHistoryDao searchHistoryDao,
                                 MoldeRestfulService.SearchLocation searchLocation) {
        super(fromSchemaToEntity, toastHelper, networkHelper);

        this.mSecretRetriever = secretRetriever;
        this.mSearchHistoryDao = searchHistoryDao;
        this.mSearchLocationService = searchLocation;
    }

    // get history info
    @Override
    public Maybe<List<SearchInfoEntity>> getSearchHistory() {
        return mSearchHistoryDao
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(searchHistorySchemas -> {
                    List<SearchInfoEntity> entities =getFromSchemaToEntity().searchHistory(searchHistorySchemas);
                    return entities == null ? Maybe.empty() : Maybe.just(entities);
                });
    }

    // add history info
    @Override
    public Maybe<Long> addToSearchHistory(SearchInfoEntity schema) {
        return Maybe.just(mSearchHistoryDao.insert(getFromSchemaToEntity().searchHistoryReverse(schema)));
    }

    // delete history info
    @Override
    public Maybe<Integer> deleteFromSearchHistory(SearchInfoEntity schema) {
        return Maybe.just(mSearchHistoryDao.deleteSingle(getFromSchemaToEntity().searchHistoryReverse(schema)));
    }

    // delete all history info
    @Override
    public Maybe<Integer> deleteSearchHistory(List<SearchInfoEntity> schemas) {
        return Maybe.just(mSearchHistoryDao.deleteAll(getFromSchemaToEntity().searchHistoryReverse(schemas)));
    }

    // get tmap search info
    @Override
    public Observable<List<SearchInfoEntity>> getTMapSearchAddressInfo(String keyword) {

        if (!isNetworkConnected()) return Observable.empty();

        return mSearchLocationService
                .getSearchLocationObservable(
                        mSecretRetriever.getTMapApiKey(), 1,"", SEARCH_COUNT, keyword,
                        "", "", "", "", "",
                        "", "", "", "", "")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(tMapSearchResponseSchema -> {
                    List<SearchInfoEntity> entities = getFromSchemaToEntity()
                            .searchInfo(tMapSearchResponseSchema.getSearchResponseSchema().getSearchSchemaList().getSearchSchema());
                    return entities == null ? Observable.empty() : Observable.just(entities);
                });
    }
}
