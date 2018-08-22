package com.limefriends.molde.ui.magazine.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.ui.magazine.comment.CardNewsCommentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO "lkj" getUid()로 변경할 것
public class CardNewsDetailActivity extends AppCompatActivity {

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

    private CardNewsImagePagerAdapter cardNewsImagePagerAdapter;
    private FirebaseAuth mFirebaseAuth;

    private boolean isLoading;
    private boolean isScrap;
    private int cardNewsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_cardnews_detail);

        setupViews();

        setupListeners();

        setupCardNewsImagePager();

        setupData();
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
    }

    private void setupListeners() {
        cardnews_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), CardNewsCommentActivity.class);
                intent.putExtra("cardNewsId", cardNewsId);
                intent.putExtra("description", cardnews_description.getText());
                startActivity(intent);
            }
        });
        cardnews_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirebaseAuth != null && mFirebaseAuth.getUid() != null) {
                    if (!isLoading && isScrap) {
                        deleteFromScrap(cardNewsId, "lkj");
                    } else {
                        addToMyScrap(cardNewsId, "lkj");
                    }
                } else {
                    Snackbar.make(cardnews_detail_layout,
                            "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        cardnews_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "공유하기", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCardNewsImagePager() {
        cardNewsImagePagerAdapter = new CardNewsImagePagerAdapter(getLayoutInflater());
        cardnews_pager.setAdapter(cardNewsImagePagerAdapter);
        cardnews_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                current_page_no.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //-----
    // Network
    //-----

    private void setupData() {
        cardNewsId = getIntent().getIntExtra("cardNewsId", 0);
        mFirebaseAuth = FirebaseAuth.getInstance();
        loadCardNews(cardNewsId);
        loadMyScrap(cardNewsId, "lkj");
    }

    private void loadCardNews(int cardNewsId) {
        MoldeRestfulService.CardNews newsService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.CardNews.class);
        Call<CardNewsResponseInfoEntityList> call = newsService.getCardNewsListById(cardNewsId);
        call.enqueue(new Callback<CardNewsResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<CardNewsResponseInfoEntityList> call,
                                   Response<CardNewsResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    CardNewsEntity entity
                            = FromSchemaToEntitiy.cardNews(response.body().getData().get(0));
                    // 카드뉴스 이미지 개수 세팅
                    if (entity.getNewsImg() != null) {
                        setImageCount(entity.getNewsImg().size());
                    }
                    cardnews_description.setText(entity.getDescription());
                    cardNewsImagePagerAdapter.setData(entity.getNewsImg());
                }
            }

            @Override
            public void onFailure(Call<CardNewsResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void loadMyScrap(int cardNewsId, String userId) {
        isLoading = true;
        MoldeRestfulService.Scrap scrapService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Scrap.class);
        Call<Result> call = scrapService.getMyScrap(userId, cardNewsId);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult() == 0) {
                        Log.e("호출 확인", "가져오기 완료");
                        cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_false);
                        isScrap = false;
                    } else {
                        cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_true);
                        isScrap = true;
                    }
                    isLoading = false;
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
        Call<Result> call = scrapService.addToMyScrap(userId, cardNewsId);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getResult() == 1) {
                    Log.e("호출 확인", "댓글 추가 완료");
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
        Call<Result> call = scrapService.deleteMyScrap(userId, cardNewsId);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getResult() == 1) {
                    Log.e("호출 확인", "삭제 완료");
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

    private void setImageCount(int size) {
        total_page_no.setText(String.valueOf(size));
    }

}
