package com.limefriends.molde.ui.menu_magazine.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.entity.news.MoldeCardNewsEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.entity.scrap.ScrapEntity;
import com.limefriends.molde.entity.scrap.ScrapResponseInfoEntity;
import com.limefriends.molde.entity.scrap.ScrapResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.ui.menu_magazine.comment.MagazineCommentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MagazineCardnewsDetailActivity extends AppCompatActivity {

    @BindView(R.id.cardnews_detail_layout)
    RelativeLayout cardnews_detail_layout;
    @BindView(R.id.cardnews_pager)
    ViewPager cardnews_pager;
    @BindView(R.id.cardnews_comment)
    ImageView cardnews_comment;
    @BindView(R.id.cardnews_scrap)
    ImageView cardnews_scrap;
    @BindView(R.id.cardnews_share)
    ImageView cardnews_share;
    @BindView(R.id.current_page_no)
    TextView current_page_no;
    @BindView(R.id.total_page_no)
    TextView total_page_no;
    @BindView(R.id.cardnews_description)
    TextView cardnews_description;

    private boolean isScrap = false;

    MagazineCardNewsDetailPagerAdapter magazineCardNewsDetailPagerAdapter;
    // List<MoldeCardNewsEntity> cardNewsDetailList;
    // private boolean hasMoreToLoad = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_cardnews_detail);
        ButterKnife.bind(this);

        final int cardNewsId = getIntent().getIntExtra("cardNewsId", 0);

//        cardNewsDetailList = new ArrayList<>();
//
//        for (int i = 0; i < 3; i++) {
//            String description = String.valueOf(i + 1) + "번째 페이지\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
//                    + "How Can I Add Many TextView or Other Views in ViewPager\n";
//            cardNewsDetailList.add(new MoldeCardNewsEntity(1, "1", description, "123", null));
//        }



        magazineCardNewsDetailPagerAdapter = new MagazineCardNewsDetailPagerAdapter(getLayoutInflater());
        cardnews_pager.setAdapter(magazineCardNewsDetailPagerAdapter);
        cardnews_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                current_page_no.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        cardnews_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "comment clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MagazineCommentActivity.class);
                intent.putExtra("cardNewsId", cardNewsId);
                intent.putExtra("description", cardnews_description.getText());
                startActivity(intent);
            }
        });

        cardnews_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_true);
                // Toast.makeText(getApplicationContext(), "스크랩 추가", Toast.LENGTH_SHORT).show();
                FirebaseAuth auth = ((MoldeApplication) getApplication()).getFireBaseAuth();
                if (auth != null && auth.getUid() != null) {
                    if (isScrap) {
                        deleteFromScrap(cardNewsId, "lkj");
                    } else {
                        addToMyScrap(cardNewsId, "lkj");
                    }
                } else {
                    Snackbar.make(cardnews_detail_layout, "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        cardnews_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "공유하기", Toast.LENGTH_SHORT).show();
            }
        });

        loadCardNews(cardNewsId);

        // TODO 실제 아이디를 리턴받아서 받아온다. 데이터 넣을 때에도 아이디에 맞도록 데이터 추가할 것
        FirebaseAuth auth = ((MoldeApplication) getApplication()).getFireBaseAuth();
        if (auth != null && auth.getUid() != null) {
            loadMyScrap(cardNewsId, "lkj");
        }

    }

    public void loadCardNews(int cardNewsId) {

        // Log.e("호출확인2", "magazine fragment");

        // 1. 더 이상 불러올 데이터가 없는지 확인
        // if (!hasMoreToLoad) return;

        // 2. 불러온다면 프로그래스바를 띄움
        // magazineCardNewsDetailPagerAdapter.setProgressMore(true);

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        // .setIsLoading(true);

        // Log.e("호출확인3", "magazine fragment");

        MoldeRestfulService.CardNews newsService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.CardNews.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<MoldeCardNewsResponseInfoEntityList> call = newsService.getCardNewsListById(cardNewsId);

        call.enqueue(new Callback<MoldeCardNewsResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeCardNewsResponseInfoEntityList> call, Response<MoldeCardNewsResponseInfoEntityList> response) {

                if (response.isSuccessful()) {
                    MoldeCardNewsEntity entity = fromSchemaToLocalEntity(response.body().getData().get(0));

                    // 카드뉴스 이미지 개수 세팅
                    if (entity.getNewsImg() != null) {
                        setImageCount(entity.getNewsImg().size());
                    }
                    cardnews_description.setText(entity.getDescription());
                    magazineCardNewsDetailPagerAdapter.setData(entity.getNewsImg());
                }
            }

            @Override
            public void onFailure(Call<MoldeCardNewsResponseInfoEntityList> call, Throwable t) {

            }
        });

    }

    private void loadMyScrap(int cardNewsId, String userId) {
        MoldeRestfulService.Scrap scrapService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Scrap.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<Result> call = scrapService.getMyScrap(userId, cardNewsId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getResult() == 0) {
                    Toast.makeText(MagazineCardnewsDetailActivity.this, "가져오기 완료", Toast.LENGTH_LONG).show();
                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_false);
                    isScrap = false;
                } else {
                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_true);
                    isScrap = true;
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    private void addToMyScrap(int cardNewsId, String userId) {
        MoldeRestfulService.Scrap scrapService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Scrap.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<Result> call = scrapService.addToMyScrap(userId, cardNewsId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getResult() == 1) {
                    Toast.makeText(MagazineCardnewsDetailActivity.this, "추가 완료", Toast.LENGTH_LONG).show();
                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_true);
                    isScrap = true;
                } else {
                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_false);
                    isScrap = false;
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    private void deleteFromScrap(int cardNewsId, String userId) {
        MoldeRestfulService.Scrap scrapService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Scrap.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<Result> call = scrapService.deleteMyScrap(userId, cardNewsId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getResult() == 1) {
                    Toast.makeText(MagazineCardnewsDetailActivity.this, "삭제완료", Toast.LENGTH_LONG).show();
                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_false);
                    isScrap = false;
                } else {
                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_true);
                    isScrap = true;
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }



    private MoldeCardNewsEntity fromSchemaToLocalEntity(MoldeCardNewsResponseInfoEntity schema) {
        return new MoldeCardNewsEntity(
                    schema.getNewsId(),
                    schema.getPostId(),
                    schema.getDescription(),
                    schema.getDate(),
                    schema.getNewsImg());
    }

    private void setImageCount(int size) {
        total_page_no.setText(String.valueOf(size));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
