package com.limefriends.molde.menu_feed;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_feed.entity.MoldeFeedEntitiy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeFeedFragment extends Fragment
        implements MoldeMainActivity.onKeyBackPressedListener,
        MoldeFeedRecyclerAdapter.OnLoadMoreListener {
    @BindView(R.id.feed_sort_toggle)
    ToggleButton feed_sort_toggle;
    @BindView(R.id.feed_update_date)
    TextView feed_update_date;
    @BindView(R.id.feed_list)
    RecyclerView feed_list;

    private MoldeFeedRecyclerAdapter feedAdapter;
    private ArrayList<MoldeFeedEntitiy> reportFeedList;

    public static MoldeFeedFragment newInstance() {
        return new MoldeFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        feed_sort_toggle.setChecked(false);

        reportFeedList = new ArrayList<MoldeFeedEntitiy>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        feed_list.setLayoutManager(layoutManager);
        feedAdapter = new MoldeFeedRecyclerAdapter(getContext(), this);
        feedAdapter.setLinearLayoutManager(layoutManager);
        feedAdapter.setRecyclerView(feed_list);
        feed_list.setAdapter(feedAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        reportFeedList.clear();
        for (int i = 1; i <= 20; i++) {
            reportFeedList.add(new MoldeFeedEntitiy("", "", "", 1));
        }
        feedAdapter.addAll(reportFeedList);
    }

    @Override
    public void onLoadMore() {
        feedAdapter.setProgressMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reportFeedList.clear();
                feedAdapter.setProgressMore(false);

                int start = feedAdapter.getItemCount();
                int end = start + 10;
                for (int i = start + 1; i <= end; i++) {
                    reportFeedList.add(new MoldeFeedEntitiy("", "", "", 1));
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
                } else if (feed_sort_toggle.isChecked() == true) {
                    feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_on));
                }
            }
        });
    }

    @Override
    public void onBackKey() {
    }
}
