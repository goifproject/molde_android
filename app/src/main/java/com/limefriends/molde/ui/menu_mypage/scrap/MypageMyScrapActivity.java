package com.limefriends.molde.ui.menu_mypage.scrap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.entity.news.MoldeCardNewsEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntityList;
import com.limefriends.molde.entity.scrap.ScrapEntity;
import com.limefriends.molde.entity.scrap.ScrapResponseInfoEntity;
import com.limefriends.molde.entity.scrap.ScrapResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MypageMyScrapActivity extends AppCompatActivity {

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;

    @BindView(R.id.myScrap_recyclerView)
    AddOnScrollRecyclerView myScrap_recyclerView;

    @BindView(R.id.progressBar3)
    ProgressBar progressBar;

    MyPageMyScrapAdapter adapter;

    // 스크랩 목록
    List<ScrapEntity> scrapEntities = new ArrayList<>();

    // 뉴스 목록
    List<MoldeCardNewsEntity> cardNewsEntities = new ArrayList<>();
    private boolean hasMoreToLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_my_scrap);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("내 스크랩");


//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "에어비앤*에서 다시 몰카 발각"));
//
//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "몰카발각 법적 처벌 강화…"));
//
//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "최신 몰카트렌드"));
//
//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "몰카범 지하철역에서 잡혀…"));
//
//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "제목제목"));
//
//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "제-목"));
//
//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "제목!!"));
//
//        myPageMyScrapEntityList.add(new ScrapEntity(R.drawable.mypage_image,
//                "제목??"));


        adapter = new MyPageMyScrapAdapter(getApplicationContext(),
                R.layout.mypage_activity_my_scrap, scrapEntities);

        // 1. 어댑터
        myScrap_recyclerView.setAdapter(adapter);

        // 2. 레이아웃 매니저
        myScrap_recyclerView.setLayoutManager(new GridLayoutManager(this, 2), true);

        // 3. loadMore
        myScrap_recyclerView.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadMyScrap(PER_PAGE, currentPage);
            }
        });

        loadMyScrap(PER_PAGE, FIRST_PAGE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private void loadMyScrap(int perPage, int page) {

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // 2. 불러온다면 프로그래스바를 띄움
        // adapter.setProgressMore(true);

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        myScrap_recyclerView.setIsLoading(true);

        progressBar.setVisibility(View.VISIBLE);

        MoldeRestfulService.Scrap scrapService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Scrap.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<ScrapResponseInfoEntityList> call = scrapService.getMyScrapList("lkj", perPage, page);

        call.enqueue(new Callback<ScrapResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<ScrapResponseInfoEntityList> call, Response<ScrapResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    final List<ScrapEntity> entities = fromScrapSchemaToLocalEntity(response.body().getData());

                    new Thread() {
                        @Override
                        public void run() {
                            for (ScrapEntity scrapEntity : entities) {
                                Response<MoldeCardNewsResponseInfoEntityList> responseSync =
                                        loadNewsPreview(scrapEntity.getNewsId());

                                List<MoldeCardNewsResponseInfoEntity> newsSchema = responseSync.body().getData();
                                if (newsSchema.size() != 0) {
                                    MoldeCardNewsEntity newsEntity = fromNewsSchemaToLocalEntity(newsSchema.get(0));
                                    cardNewsEntities.add(newsEntity);
                                }
                            }
                            mHandler.sendEmptyMessage(0);
                        }
                    }.start();
                }
            }

            @Override
            public void onFailure(Call<ScrapResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            adapter.setData(cardNewsEntities);
            // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
            currentPage++;
            // 8. 더 이상 데이터를 세팅중이 아님을 명시
            myScrap_recyclerView.setIsLoading(false);
            if (cardNewsEntities.size() < PER_PAGE) {
                // Log.e("호출확인5", "magazine fragment");
                setHasMoreToLoad(false);
            }
        }
    };

    private Response<MoldeCardNewsResponseInfoEntityList> loadNewsPreview(int newsId) {

        MoldeRestfulService.CardNews newsService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.CardNews.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<MoldeCardNewsResponseInfoEntityList> call = newsService.getCardNewsListById(newsId);

        try {
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





    private List<ScrapEntity> fromScrapSchemaToLocalEntity(List<ScrapResponseInfoEntity> schemas) {
        List<ScrapEntity> entities = new ArrayList<>();
        for (ScrapResponseInfoEntity schema : schemas) {
            entities.add(new ScrapEntity(
                    schema.getScrapId(),
                    schema.getUserId(),
                    schema.getNewsId()
            ));
        }
        return entities;
    }

    private MoldeCardNewsEntity fromNewsSchemaToLocalEntity(MoldeCardNewsResponseInfoEntity schema) {


        return new MoldeCardNewsEntity(
                schema.getNewsId(),
                schema.getPostId(),
                schema.getDescription(),
                schema.getDate(),
                schema.getNewsImg());

    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

}