package com.limefriends.molde.menu_feed;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.limefriends.molde.MoldeApplication;
import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_feed.entity.MoldeFeedEntity;
import com.limefriends.molde.menu_feed.entity.MoldeFeedResponseInfoEntity;
import com.limefriends.molde.menu_feed.entity.MoldeFeedResponseInfoEntityList;
import com.limefriends.molde.menu_feed.feed.MoldeFeedRecyclerAdapter;
import com.limefriends.molde.menu_map.MapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoldeFeedFragment extends Fragment
        implements MoldeMainActivity.onKeyBackPressedListener,
        MoldeFeedRecyclerAdapter.OnLoadMoreListener,
        MoldeFeedRecyclerAdapter.OnClickFeedItemListener {
    @BindView(R.id.feed_sort_toggle)
    ToggleButton feed_sort_toggle;
    @BindView(R.id.feed_update_date)
    TextView feed_update_date;
    @BindView(R.id.feed_list)
    RecyclerView feed_list;

    private MoldeFeedRecyclerAdapter feedAdapter;
    private ArrayList<MoldeFeedEntity> reportFeedList;
    private Handler handler;

    private static final String feedDistance = "거리순";
    private static final String feedLast = "최신순";

    public String res;
    public static SparseArrayCompat feedFragmentSparseArrayCompat;
    public MoldeFeedResponseInfoEntityList feedResponseInfoEntityList;
    public List<MoldeFeedResponseInfoEntity> feedResponseInfoList;
    public MoldeFeedEntity feedData;

    public static MoldeFeedFragment newInstance() {
        feedFragmentSparseArrayCompat = new SparseArrayCompat();
        return new MoldeFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        reportFeedList = new ArrayList<MoldeFeedEntity>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        feed_list.setLayoutManager(layoutManager);
        feedAdapter = new MoldeFeedRecyclerAdapter(getContext(), this, this);
        feedAdapter.setLinearLayoutManager(layoutManager);
        feedAdapter.setRecyclerView(feed_list);
        feed_list.setAdapter(feedAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (feedFragmentSparseArrayCompat.get(R.string.feedStatus) != null) {
            reportFeedList.clear();
            String feedStatus = (String) feedFragmentSparseArrayCompat.get(R.string.feedStatus);
            if (feedStatus.equals(feedDistance)) {
                feed_sort_toggle.setChecked(false);
                feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_off));
                loadData(feedDistance);
            } else if (feedStatus.equals(feedLast)) {
                feed_sort_toggle.setChecked(true);
                feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_on));
                loadData(feedLast);
            }
            return;
        }
        loadData(feedDistance);
    }

    private void loadData(String cmd) {
        try {
            handler = new Handler(Looper.getMainLooper());
            reportFeedList.clear();
            if (cmd.equals(feedDistance)) {
                if (MoldeApplication.myLocation != null) {
                    feedRequestGet(MoldeApplication.BASE_URL + "/v1/pin?" + "reportLat=" + MoldeApplication.myLocation.latitude + "&reportLng=" + MoldeApplication.myLocation.longitude);
                } else {
                    feedRequestGet(MoldeApplication.BASE_URL + "/v1/pin");
                }
            } else if (cmd.equals(feedLast)) {
                feedRequestGet(MoldeApplication.BASE_URL + "/v1/pin");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void feedRequestGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("에러", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                res = response.body().string().replace("\\\"", "");
                feedResponseInfoEntityList = new Gson().fromJson(res, MoldeFeedResponseInfoEntityList.class);
                feedResponseInfoList = feedResponseInfoEntityList.getFeed();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (feedResponseInfoList.size() < 10) {
                            for (int i = 0; i < feedResponseInfoList.size(); i++) {
                                addFeedList(i);
                            }
                        } else {
                            for (int i = 0; i < 10; i++) {
                                addFeedList(i);
                            }
                        }
                        feedAdapter.addAll(reportFeedList);
                    }
                });
            }

        });
    }

    public void addFeedList(int i) {
        try {
            reportFeedList.add(new MoldeFeedEntity(
                            feedResponseInfoList.get(i).getRep_addr(),
                            feedResponseInfoList.get(i).getRep_detail_addr(),
                            feedResponseInfoList.get(i).getRep_state(),
                            feedResponseInfoList.get(i).getRep_img().get(0),
                            feedResponseInfoList.get(i).getRep_date(),
                            new LatLng(
                                    Double.valueOf(feedResponseInfoList.get(i).getRep_lat()),
                                    Double.valueOf(feedResponseInfoList.get(i).getRep_lon()))
                    )
            );
        } catch (Exception e) {
            reportFeedList.add(new MoldeFeedEntity(
                            "에러 나는 장소",
                            "에러 나는 세부 장소",
                            0,
                            "",
                            "에러 나는 날짜",
                            new LatLng(0, 0)
                    )
            );
        } finally {
            Log.e("s", feedResponseInfoList.get(i).toString());
        }
    }

    @Override
    public void onLoadMore() {
        feedAdapter.setProgressMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reportFeedList.clear();
                feedAdapter.setProgressMore(false);

                if (!feed_sort_toggle.isChecked()) {
                    int start = feedAdapter.getItemCount();
                    int end = start + 10;
                    for (int i = start + 1; i <= end; i++) {
                        reportFeedList.add(new MoldeFeedEntity(
                                "거리순 주소", "세부주소",
                                1, "", new Date().toString(),
                                new LatLng(Double.valueOf("0.0"), Double.valueOf("0.0"))));
                    }
                } else if (feed_sort_toggle.isChecked()) {
                    int start = feedAdapter.getItemCount();
                    int end = start + 10;
                    for (int i = start + 1; i <= end; i++) {
                        reportFeedList.add(new MoldeFeedEntity(
                                "시간순 주소", "세부주소",
                                2, "", new Date().toString(),
                                new LatLng(Double.valueOf("0.0"), Double.valueOf("0.0"))));
                    }
                }

                feedAdapter.addItemMore(reportFeedList);
                feedAdapter.setMoreLoading(false);
            }
        }, 2000);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MoldeMainActivity) context).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        feed_sort_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!feed_sort_toggle.isChecked()) {
                    feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_off));
                    loadData(feedDistance);
                } else if (feed_sort_toggle.isChecked()) {
                    feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_on));
                    loadData(feedLast);
                }
            }
        });
    }

    @Override
    public void callFeedData(MoldeFeedEntity feedEntitiy) {
        if (((MoldeMainActivity) getActivity()).fragmentSparseArray.get(R.string.main_menu_map) != null) {
            feedData = feedEntitiy;
            Bundle bundle = new Bundle();
            bundle.putString("reportFeedAddress", feedEntitiy.getReportFeedAddress());
            bundle.putString("reportFeedDetailAddress", feedEntitiy.getReportFeedDetailAddress());
            bundle.putInt("reportFeedMarkerId", feedEntitiy.getReportFeedMarkerId());
            bundle.putString("reportFeedThumbnail", feedEntitiy.getReportFeedThumbnail());
            bundle.putString("reportFeedDate", feedEntitiy.getReportFeedDate());
            bundle.putDouble("reportFeedLocationLat", feedEntitiy.getReportFeedLocation().latitude);
            bundle.putDouble("reportFeedLocationLng", feedEntitiy.getReportFeedLocation().longitude);

            Fragment fragment = (Fragment) ((MoldeMainActivity) getActivity())
                    .fragmentSparseArray.get(R.string.main_menu_map);
            fragment.setArguments(bundle);
            ((MoldeMainActivity) getActivity()).replaceFragment(fragment);
            BottomNavigationView navigation = ((MoldeMainActivity) getActivity()).findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.main_menu_map);

        } else {
            ((MoldeMainActivity) getActivity()).replaceFragment(MapFragment.newInstance());
            BottomNavigationView navigation = ((MoldeMainActivity) getActivity()).findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.main_menu_map);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!feed_sort_toggle.isChecked()) {
            feedFragmentSparseArrayCompat.append(R.string.feedStatus, feedDistance);
        } else if (feed_sort_toggle.isChecked()) {
            feedFragmentSparseArrayCompat.append(R.string.feedStatus, feedLast);
        }
    }

    @Override
    public void onBackKey() {
    }
}
