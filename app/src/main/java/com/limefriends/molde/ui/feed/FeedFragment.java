package com.limefriends.molde.ui.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntityList;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.map.main.MapFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.limefriends.molde.comm.Constant.Feed.*;

public class FeedFragment extends Fragment implements FeedRecyclerAdapter.OnClickFeedItemListener {

    @BindView(R.id.feed_sort_toggle)
    ToggleButton feed_sort_toggle;
    @BindView(R.id.feed_update_date)
    TextView feed_update_date;
    @BindView(R.id.feed_list)
    AddOnScrollRecyclerView feed_list;

    private MoldeRestfulService.Feed feedService;
    private FeedRecyclerAdapter feedAdapter;

    private final int PER_PAGE = 10;
    private final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isFirstOnCreateView = true;
    private String feedStandard = FEED_BY_DISTANCE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);

        setupViews(view);

        setupListener();

        setupFeedList();

        if (isFirstOnCreateView) loadFeedData(feedStandard, PER_PAGE, currentPage);

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
                // 체크가 최신순, 기본이 거리순
                hasMoreToLoad(true);
                currentPage = FIRST_PAGE;
                feedAdapter.clear();
                if (isChecked) {
                    feed_sort_toggle.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.ic_feed_toggle_on));
                    loadFeedData(FEED_BY_LAST, PER_PAGE, currentPage);
                } else {
                    feed_sort_toggle.setBackgroundDrawable(
                            getResources().getDrawable(R.drawable.ic_feed_toggle_off));
                    loadFeedData(FEED_BY_DISTANCE, PER_PAGE, currentPage);
                }
            }
        });
    }

    private void setupFeedList() {
        if (feedAdapter == null) {
            feedAdapter = new FeedRecyclerAdapter(getContext(), this);
        }
        feed_list.setAdapter(feedAdapter);
        feed_list.setLayoutManager(new LinearLayoutManager(getContext()), false);
        feed_list.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadFeedData(feedStandard, PER_PAGE, currentPage);
            }
        });
    }

    //-----
    // Network
    //-----

    private MoldeRestfulService.Feed getFeedService() {
        if (feedService == null) {
            feedService = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);
        }
        return feedService;
    }

    private void loadFeedData(String feedStandard, int perPage, int page) {

        if (!hasMoreToLoad) return;

        feed_list.setIsLoading(true);

        if (feedStandard.equals(FEED_BY_DISTANCE)) {
            fetchByLocation(perPage, page);
        } else if (feedStandard.equals(FEED_BY_LAST)) {
            fetchByDate(perPage, page);
        }
    }

    private void fetchByLocation(int perPage, int page) {
        LatLng latLng = ((MoldeApplication) getActivity().getApplication()).getCurrLocation();

        Call<FeedResponseInfoEntityList> call = getFeedService()
                .getPagedFeedByDistance(latLng.latitude, latLng.longitude, perPage, page);

        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call, Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<FeedEntity> entities = FromSchemaToEntitiy.feed(response.body().getData());
                    feedAdapter.addAll(entities);
                    currentPage++;
                    feed_list.setIsLoading(false);
                    if (entities.size() < PER_PAGE) {
                        hasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<FeedResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void fetchByDate(int perPage, int page) {
        Call<FeedResponseInfoEntityList> call = getFeedService()
                .getPagedFeedByDate(perPage, page);

        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call, Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<FeedEntity> entities = FromSchemaToEntitiy.feed(response.body().getData());
                    feedAdapter.addAll(entities);
                    currentPage++;
                    feed_list.setIsLoading(false);
                    if (entities.size() < PER_PAGE) {
                        hasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<FeedResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    @Override
    public void callFeedData(FeedEntity feedEntity) {
        ((MoldeMainActivity) getActivity()).setFeedEntity(feedEntity);
        ((MoldeMainActivity) getActivity()).setSelectedMenu(R.id.main_menu_map);
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
        isFirstOnCreateView = false;
    }
}
