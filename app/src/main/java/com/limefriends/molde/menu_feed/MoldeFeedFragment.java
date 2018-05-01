package com.limefriends.molde.menu_feed;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_feed.entity.MoldeFeedEntitiy;
import com.limefriends.molde.menu_feed.entity.MoldeFeedResponseInfoEntity;
import com.limefriends.molde.menu_map.MoldeMapFragment;

import java.io.IOException;
import java.util.ArrayList;

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
    MoldeFeedRecyclerAdapter.OnClickFeedItemListener{
    @BindView(R.id.feed_sort_toggle)
    ToggleButton feed_sort_toggle;
    @BindView(R.id.feed_update_date)
    TextView feed_update_date;
    @BindView(R.id.feed_list)
    RecyclerView feed_list;

    private MoldeFeedRecyclerAdapter feedAdapter;
    private ArrayList<MoldeFeedEntitiy> reportFeedList;
    public static SparseArrayCompat feedFragmentSparseArrayCompat;

    public static MoldeFeedFragment newInstance() {
        feedFragmentSparseArrayCompat = new SparseArrayCompat();
        return new MoldeFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        reportFeedList = new ArrayList<MoldeFeedEntitiy>();
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
        if (feedFragmentSparseArrayCompat.get(R.string.feedList) != null && feedFragmentSparseArrayCompat.get(R.string.feedStatus) != null) {
            reportFeedList.clear();
            String feedStatus = (String) feedFragmentSparseArrayCompat.get(R.string.feedStatus);
            if (feedStatus.equals("거리순")) {
                feed_sort_toggle.setChecked(false);
                feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_off));
            } else if (feedStatus.equals("최신순")) {
                feed_sort_toggle.setChecked(true);
                feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_on));
            }
            reportFeedList = (ArrayList<MoldeFeedEntitiy>) feedFragmentSparseArrayCompat.get(R.string.feedList);
            feedAdapter.addAll(reportFeedList);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData("거리순");
            }
        }).start();
    }

    private void loadData(String cmd) {
        try {
            requestGet("http://13.209.64.183:7019/v1/report");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        reportFeedList.clear();
        if (cmd.equals("거리순")) {
            for (int i = 1; i <= 20; i++) {
                reportFeedList.add(new MoldeFeedEntitiy(
                        "거리순", "",
                        1, "","",
                        new LatLng(Double.valueOf("37.499597"), Double.valueOf("127.031372"))));
            }
        } else if (cmd.equals("최신순")) {
            for (int i = 1; i <= 20; i++) {
                reportFeedList.add(new MoldeFeedEntitiy(
                        "최신순", "",
                        2, "","",
                        new LatLng(Double.valueOf("37.499597"), Double.valueOf("127.031372"))));
            }
        }
        feedAdapter.addAll(reportFeedList);
    }

    public void requestGet(String url) throws IOException {
        final ArrayList<MoldeFeedResponseInfoEntity> feedResponseInfoList = new ArrayList<MoldeFeedResponseInfoEntity>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("에러", e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("응답", response.body().string());
                MoldeFeedResponseInfoEntity[] feedResponseInfoArray =
                        new Gson().fromJson(response.body().string(), MoldeFeedResponseInfoEntity[].class);
                for(int i = 0; i < feedResponseInfoArray.length; i++){
                    feedResponseInfoList.add(feedResponseInfoArray[i]);
                    Log.e("res", feedResponseInfoArray[i].toString());
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        feedAdapter.setProgressMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reportFeedList.clear();
                feedAdapter.setProgressMore(false);

                if (feed_sort_toggle.isChecked() == false) {
                    int start = feedAdapter.getItemCount();
                    int end = start + 10;
                    for (int i = start + 1; i <= end; i++) {
                        reportFeedList.add(new MoldeFeedEntitiy(
                                "거리순", "",
                                1, "","",
                                new LatLng(Double.valueOf("0.0"), Double.valueOf("0.0"))));
                    }
                } else if (feed_sort_toggle.isChecked() == true) {
                    int start = feedAdapter.getItemCount();
                    int end = start + 10;
                    for (int i = start + 1; i <= end; i++) {
                        reportFeedList.add(new MoldeFeedEntitiy(
                                "최신순", "",
                                2, "","",
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
                if (feed_sort_toggle.isChecked() == false) {
                    feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_off));
                    loadData("거리순");
                } else if (feed_sort_toggle.isChecked() == true) {
                    feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_on));
                    loadData("최신순");
                }
            }
        });
    }

    @Override
    public void callFeedData(MoldeFeedEntitiy feedEntitiy) {
        if(((MoldeMainActivity)getActivity()).fragmentSparseArray.get(R.string.main_menu_map) != null){
            Fragment fragment = (Fragment) ((MoldeMainActivity)getActivity())
                    .fragmentSparseArray.get(R.string.main_menu_map);
            Bundle feedDataBundle = new Bundle();
            feedDataBundle.putSerializable("feedData", feedEntitiy);
            fragment.setArguments(feedDataBundle);
            ((MoldeMainActivity)getActivity()).replaceFragment(fragment);
            BottomNavigationView navigation = ((MoldeMainActivity)getActivity()).findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.main_menu_map);

        }else{
            ((MoldeMainActivity)getActivity()).replaceFragment(MoldeMapFragment.newInstance());
            BottomNavigationView navigation = ((MoldeMainActivity)getActivity()).findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.main_menu_map);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        feedFragmentSparseArrayCompat.append(R.string.feedList, reportFeedList);
        if (feed_sort_toggle.isChecked() == false) {
            feedFragmentSparseArrayCompat.append(R.string.feedStatus, "거리순");
        } else if (feed_sort_toggle.isChecked() == true) {
            feedFragmentSparseArrayCompat.append(R.string.feedStatus, "최신순");
        }
    }

    @Override
    public void onBackKey() {
    }
}
