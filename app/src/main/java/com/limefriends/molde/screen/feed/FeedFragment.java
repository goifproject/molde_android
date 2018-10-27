package com.limefriends.molde.screen.feed;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.MoldeApplication;
import com.limefriends.molde.screen.common.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.common.utils.DateUtil;
import com.limefriends.molde.common.utils.NetworkUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.screen.common.controller.BaseFragment;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.main.MoldeMainActivity;
import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.limefriends.molde.common.Constant.Feed.*;

public class FeedFragment extends BaseFragment implements FeedAdapter.OnClickFeedItemListener {

    @BindView(R.id.feed_sort_toggle)
    ToggleButton feed_sort_toggle;
    @BindView(R.id.feed_update_date)
    TextView feed_update_date;
    @BindView(R.id.feed_update_text)
    TextView feed_update_text;
    @BindView(R.id.feed_list)
    AddOnScrollRecyclerView feed_list;

    private FeedAdapter feedAdapter;
    private boolean isLoading;

    @Service private Repository.Feed mFeedRepository;
    @Service private ToastHelper mToastHelper;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final int PER_PAGE = 10;
    private final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isFirstCall = true;
    private boolean isSecondCall = false;
    private String feedStandard = FEED_BY_DISTANCE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getInjector().inject(this);

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        if (!isFirstCall) isSecondCall = true;

        setupViews(view);

        setupListener();

        setupFeedList();

        if (isFirstCall) loadFeedData(feedStandard, PER_PAGE, currentPage);

        return view;
    }

    //-----
    // View
    //-----

    private void setupViews(View view) {
        ButterKnife.bind(this, view);
        if (feedStandard.equals(FEED_BY_DISTANCE)) {
            feed_sort_toggle.setChecked(false);
            feed_sort_toggle.setBackgroundDrawable(
                    getResources().getDrawable(R.drawable.ic_feed_toggle_off));
        } else {
            feed_sort_toggle.setChecked(true);
            feed_sort_toggle.setBackgroundDrawable(
                    getResources().getDrawable(R.drawable.ic_feed_toggle_on));
        }
    }

    private void setupListener() {
        feed_sort_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    feed_sort_toggle.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.ic_feed_toggle_on));

                    if (isSecondCall) return;

                    hasMoreToLoad(true);

                    currentPage = FIRST_PAGE;

                    feedAdapter.clear();

                    loadFeedData(FEED_BY_LAST, PER_PAGE, currentPage);
                } else {

                    feed_sort_toggle.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.ic_feed_toggle_off));

                    isSecondCall = false;

                    hasMoreToLoad(true);

                    currentPage = FIRST_PAGE;

                    feedAdapter.clear();

                    loadFeedData(FEED_BY_DISTANCE, PER_PAGE, currentPage);
                }
            }
        });
    }

    private void setupFeedList() {
        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter(getContext(), this,
                    getFragmentManager());
        }
        feed_list.setAdapter(feedAdapter);
        feed_list.setLayoutManager(new LinearLayoutManager(getContext()), false);
        feed_list.setOnLoadMoreListener(() -> {
            if (isLoading) return;
            loadFeedData(feedStandard, PER_PAGE, currentPage);
        });
    }

    //-----
    // Network
    //-----

    private void loadFeedData(String feedStandard, int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        if (feedStandard.equals(FEED_BY_DISTANCE)) {
            fetchByLocation(perPage, page);
        } else if (feedStandard.equals(FEED_BY_LAST)) {
            fetchByDate(perPage, page);
        }
    }

    private void fetchByLocation(int perPage, int page) {

        LatLng latLng = ((MoldeApplication) getActivity().getApplication()).getCurrLocation();

        mCompositeDisposable.add(
                mFeedRepository
                        .getPagedFeedByDistance(latLng.latitude, latLng.longitude, perPage, page)
                        .subscribeWith(getObserver())
        );
    }

    private void fetchByDate(int perPage, int page) {

        mCompositeDisposable.add(
                mFeedRepository
                        .getPagedFeedByDate(perPage, page)
                        .subscribeWith(getObserver())
        );
    }

    @Override
    public void callFeedData(FeedEntity feedEntity) {
        ((MoldeMainActivity) getActivity()).setFeedEntity(feedEntity);
        ((MoldeMainActivity) getActivity()).setSelectedMenu(R.id.main_menu_map);
    }

    private DisposableObserver<List<FeedEntity>> getObserver() {
        List<FeedEntity> data = new ArrayList<>();
        return new DisposableObserver<List<FeedEntity>>() {
            @Override
            public void onNext(List<FeedEntity> feedEntities) {
                data.addAll(feedEntities);
            }

            @Override
            public void onError(Throwable e) {
                isLoading = false;
            }

            @Override
            public void onComplete() {
                isLoading = false;

                isFirstCall = false;

                if (data.size() == 0) {
                    hasMoreToLoad(false);
                    return;
                }

                feedAdapter.addAll(data);
                currentPage++;

                feed_update_text.setText(getText(R.string.feed_date_recent));
                feed_update_date.setText(DateUtil.fromLongToDate(data.get(0).getRepDate()));

                if (data.size() < PER_PAGE) {
                    hasMoreToLoad(false);
                }
            }
        };
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeDisposable.clear();
    }
}
