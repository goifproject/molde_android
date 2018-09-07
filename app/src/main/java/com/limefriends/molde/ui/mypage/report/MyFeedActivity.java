package com.limefriends.molde.ui.mypage.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.feed.FeedDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.limefriends.molde.comm.Constant.Authority.*;
import static com.limefriends.molde.comm.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.comm.Constant.Common.EXTRA_KEY_POSITION;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_AUTHORITY;
import static com.limefriends.molde.comm.Constant.Feed.EXTRA_KEY_FEED_ID;
import static com.limefriends.molde.comm.Constant.Feed.INTENT_KEY_MY_FEED;
import static com.limefriends.molde.comm.Constant.Feed.INTENT_VALUE_MY_FEED;

public class MyFeedActivity extends AppCompatActivity implements MyFeedAdapter.OnItemClickListener {

    @BindView(R.id.myReport_recyclerView)
    AddOnScrollRecyclerView myReport_recyclerView;

    MyFeedAdapter reportAdapter;

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private long authority;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feed);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY);

        userId = FirebaseAuth.getInstance().getUid();

        setupViews();

        setFeedRecycler();

        loadMyReport(PER_PAGE, currentPage);
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(getText(R.string.myreport));
    }

    private void setFeedRecycler() {
        reportAdapter = new MyFeedAdapter(this, this);
        myReport_recyclerView.setAdapter(reportAdapter);
        myReport_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()), false);
        myReport_recyclerView.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadMyReport(PER_PAGE, currentPage);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    //-----
    // Network
    //-----

    private Call<FeedResponseInfoEntityList> generateCall(String userId, int perPage, int page) {
        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);
        if (authority == MEMBER || authority == GUARDIAN) {
            return feedService.getMyFeed(userId, perPage, page);
        } else if (authority == ADMIN) {
            return feedService.getPagedFeedByDate(perPage, page);
        }
        return null;
    }

    private void loadMyReport(int perPage, int page) {
        if (!hasMoreToLoad) return;
        myReport_recyclerView.setIsLoading(true);
        generateCall(userId, perPage, page).enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call, Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    // 4. 호출 후 데이터 정리
                    List<FeedEntity> entities = FromSchemaToEntitiy.feed(response.body().getData());
                    // 6. 데이터 추가
                    reportAdapter.addData(entities);
                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                    currentPage++;
                    // 8. 더 이상 데이터를 세팅중이 아님을 명시
                    myReport_recyclerView.setIsLoading(false);
                    if (entities.size() < PER_PAGE) {
                        // Log.e("호출확인5", "magazine fragment");
                        setHasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<FeedResponseInfoEntityList> call, Throwable t) {
                Log.e("문제확인", t.getMessage());
            }
        });

    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }


    @Override
    public void OnItemClick(int feedId, int position) {
        Intent intent = new Intent(this, FeedDetailActivity.class);
        intent.putExtra(EXTRA_KEY_FEED_ID, feedId);
        intent.putExtra(EXTRA_KEY_ACTIVITY_NAME, INTENT_VALUE_MY_FEED);
        intent.putExtra(EXTRA_KEY_POSITION, position);
        startActivityForResult(intent, INTENT_KEY_MY_FEED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INTENT_KEY_MY_FEED) {
            reportAdapter.removeItem(data.getIntExtra(EXTRA_KEY_POSITION, 0));
        }
    }

    //-----
    // lifecycle
    //-----

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }
}