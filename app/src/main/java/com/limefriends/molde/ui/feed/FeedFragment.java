package com.limefriends.molde.ui.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.comm.custom.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.comm.utils.DateUtil;
import com.limefriends.molde.comm.utils.NetworkUtil;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntityList;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.limefriends.molde.comm.Constant.Feed.*;

public class FeedFragment extends Fragment implements FeedAdapter.OnClickFeedItemListener {

    @BindView(R.id.feed_sort_toggle)
    ToggleButton feed_sort_toggle;
    @BindView(R.id.feed_update_date)
    TextView feed_update_date;
    @BindView(R.id.feed_update_text)
    TextView feed_update_text;
    @BindView(R.id.feed_list)
    AddOnScrollRecyclerView feed_list;

    private MoldeRestfulService.Feed feedService;
    private FeedAdapter feedAdapter;

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
        feed_list.setOnLoadMoreListener(new OnLoadMoreListener() {
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

        if (!NetworkUtil.isConnected(getContext())) {
            Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        LatLng latLng = ((MoldeApplication) getActivity().getApplication()).getCurrLocation();

        Call<FeedResponseInfoEntityList> call = getFeedService()
                .getPagedFeedByDistance(latLng.latitude, latLng.longitude, perPage, page);

        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call, Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {

                    List<FeedResponseInfoEntity> schemas = response.body().getData();

                    feed_list.setIsLoading(false);

                    isFirstCall = false;

                    if (schemas == null || schemas.size() == 0) {
                        hasMoreToLoad(false);
                        return;
                    }

                    List<FeedEntity> entities = FromSchemaToEntitiy.feed2(schemas);
                    feedAdapter.addAll(entities);
                    currentPage++;

                    feed_update_text.setText(getText(R.string.feed_date_location));
                    feed_update_date.setText(DateUtil.fromLongToDate(entities.get(0).getRepDate()));

                    if (schemas.size() < PER_PAGE) {
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

        if (!NetworkUtil.isConnected(getContext())) {
            Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<FeedResponseInfoEntityList> call = getFeedService()
                .getPagedFeedByDate(perPage, page);

        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call, Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {

                    feed_list.setIsLoading(false);

                    List<FeedResponseInfoEntity> schemas = response.body().getData();

                    isFirstCall = false;

                    if (schemas == null || schemas.size() == 0) {
                        hasMoreToLoad(false);
                        return;
                    }

                    List<FeedEntity> entities = FromSchemaToEntitiy.feed2(schemas);
                    feedAdapter.addAll(entities);
                    currentPage++;

                    feed_update_text.setText(getText(R.string.feed_date_recent));
                    feed_update_date.setText(DateUtil.fromLongToDate(entities.get(0).getRepDate()));

                    if (schemas.size() < PER_PAGE) {
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
    }
}