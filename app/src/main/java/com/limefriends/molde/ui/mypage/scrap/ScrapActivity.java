package com.limefriends.molde.ui.mypage.scrap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntityList;
import com.limefriends.molde.entity.scrap.ScrapEntity;
import com.limefriends.molde.entity.scrap.ScrapResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.magazine.detail.CardNewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.limefriends.molde.comm.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.comm.Constant.Common.EXTRA_KEY_CARDNEWS_ID;
import static com.limefriends.molde.comm.Constant.Scrap.*;

// TODO "lkj" 바꿀 것 - > uId
public class ScrapActivity extends AppCompatActivity {

    @BindView(R.id.myScrap_recyclerView)
    AddOnScrollRecyclerView myScrap_recyclerView;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;

    private ScrapAdapter adapter;
    private List<CardNewsEntity> cardNewsEntities = new ArrayList<>();
    private MoldeRestfulService.CardNews newsService;
    private MoldeRestfulService.Scrap scrapService;

    private final int PER_PAGE = 10;
    private final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private int fetchCount = 0;
    private int responseCount = 0;
    private int selectedNewsPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_scrap);

        setupViews();

        setupScrapList();

        loadMyScrap(PER_PAGE, currentPage);
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
        toolbar_title.setText(getText(R.string.myscrap));
    }

    private void setupScrapList() {
        adapter = new ScrapAdapter(this);
        myScrap_recyclerView.setAdapter(adapter);
        myScrap_recyclerView.setLayoutManager(
                new GridLayoutManager(this, 2), true);
        myScrap_recyclerView.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadMyScrap(PER_PAGE, currentPage);
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

    private MoldeRestfulService.Scrap getScrapService() {
        if (scrapService == null) {
            scrapService = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Scrap.class);
        }
        return scrapService;
    }

    private MoldeRestfulService.CardNews getNewsService() {
        if (newsService == null) {
            newsService = MoldeNetwork.getInstance().generateService(MoldeRestfulService.CardNews.class);
        }
        return newsService;
    }

    private void loadMyScrap(int perPage, int page) {

        if (!hasMoreToLoad) return;

        myScrap_recyclerView.setIsLoading(true);

        progressBar.setVisibility(View.VISIBLE);

        String uId = ((MoldeApplication)getApplication()).getFireBaseAuth().getCurrentUser().getUid();

        Call<ScrapResponseInfoEntityList> call
                = getScrapService().getMyScrapList(uId, perPage, page);

        call.enqueue(new Callback<ScrapResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<ScrapResponseInfoEntityList> call,
                                   Response<ScrapResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<ScrapEntity> entities = FromSchemaToEntitiy.scrap(response.body().getData());
                    fetchCount = entities.size();
                    for (ScrapEntity scrapEntity : entities) {
                        loadCardNews(scrapEntity.getNewsId());
                    }
                    if (fetchCount == 0) {
                        progressBar.setVisibility(View.GONE);
                        myScrap_recyclerView.setIsLoading(false);
                    }

                }
            }

            @Override
            public void onFailure(Call<ScrapResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void loadCardNews(int newsId) {

        Call<CardNewsResponseInfoEntityList> call = getNewsService().getCardNewsListById(newsId);

        call.enqueue(new Callback<CardNewsResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<CardNewsResponseInfoEntityList> call,
                                   Response<CardNewsResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    addResponseCount();
                    List<CardNewsResponseInfoEntity> newsSchema = response.body().getData();
                    if (newsSchema.size() != 0) {
                        CardNewsEntity newsEntity = FromSchemaToEntitiy.cardNews(newsSchema.get(0));
                        cardNewsEntities.add(newsEntity);
                    }
                    if (fetchCount == responseCount) {
                        progressBar.setVisibility(View.GONE);
                        adapter.setData(cardNewsEntities);
                        currentPage++;
                        myScrap_recyclerView.setIsLoading(false);
                        if (cardNewsEntities.size() < PER_PAGE) {
                            hasMoreToLoad(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CardNewsResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private synchronized void addResponseCount() {
        responseCount++;
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    public void onCardNewsSelected(int newsId, int position) {
        selectedNewsPosition = position;
        Intent intent = new Intent(this, CardNewsDetailActivity.class);
        intent.putExtra(EXTRA_KEY_CARDNEWS_ID, newsId);
        intent.putExtra(EXTRA_KEY_ACTIVITY_NAME, INTENT_VALUE_SCRAP);
        startActivityForResult(intent, INTENT_KEY_CARDNEWS_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INTENT_KEY_CARDNEWS_DETAIL) {
            adapter.removeItem(selectedNewsPosition);
        }
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroy() {
        super.onDestroy();
        hasMoreToLoad(true);
        currentPage = 0;
    }

}