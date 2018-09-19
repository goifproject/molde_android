package com.limefriends.molde.ui.magazine.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.MoldeApplication;
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

import static com.limefriends.molde.comm.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.comm.Constant.Scrap.INTENT_VALUE_SCRAP;

// TODO "lkj" getUid()로 변경할 것 -> uId
// TODO auth Application 에서 가져다 쓸 것
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
    @BindView(R.id.cardnews_progress)
    ProgressBar cardnews_progress;

    private CardNewsImagePagerAdapter cardNewsImagePagerAdapter;
    private FirebaseAuth mFirebaseAuth;
    private CardNewsEntity mCardNewsEntity;

    private String activityName;
    private boolean isLoading;
    private boolean isScrap;
    private int cardNewsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardnews_detail);

        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);

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
                    final String uId = mFirebaseAuth.getCurrentUser().getUid();
                    if (!isLoading && isScrap) {
                        AlertDialog dialog = new AlertDialog.Builder(CardNewsDetailActivity.this)
                                .setMessage(getText(R.string.scrap_delete_message))
                                .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteFromScrap(cardNewsId, uId);
                                    }
                                })
                                .setNegativeButton(getText(R.string.no), null)
                                .create();
                        dialog.show();
                    } else {
                        addToMyScrap(cardNewsId, uId);
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

                cardnews_progress.setVisibility(View.VISIBLE);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_sub_tutorial_1);

                // 액션 타입이랑 Object 타입이 맞아야 올라가고
                // 맞더라도 페이스북 오류 때문에 실시간으로 반영되지 않음
                // 앱 설정은 그대로 따라가면 됨
                SharePhoto sharePhoto = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .setUserGenerated(true)
                        .build();
//                TODO 실제 데이터가 들어갈 때
//                SharePhoto sharePhoto = new SharePhoto.Builder()
//                        .setImageUrl(Uri.parse(mCardNewsEntity.getNewsImg().get(0).getUrl()))
//                        .build();

                ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                        .putString("og:type", "article")
                        .putString("og:title", mCardNewsEntity.getDescription())
                        //.putString("og:description", "테스트 내용")
                        .build();
                ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                        .setActionType("news.reads")
                        .putObject("article", object)
                        .putPhoto("image", sharePhoto)
                        .build();
                ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                        .setPreviewPropertyName("article")
                        .setAction(action)
                        .build();

                ShareDialog.show(CardNewsDetailActivity.this, content);

                cardnews_progress.setVisibility(View.GONE);

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
        activityName = getIntent().getStringExtra(EXTRA_KEY_ACTIVITY_NAME);
        cardNewsId = getIntent().getIntExtra("cardNewsId", 0);
        mFirebaseAuth = ((MoldeApplication) getApplication()).getFireBaseAuth();
        loadCardNews(cardNewsId);
        if (mFirebaseAuth.getUid() != null) {
            loadMyScrap(cardNewsId, mFirebaseAuth.getCurrentUser().getUid());
        } else {
            loadMyScrap(cardNewsId, "");
        }

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
                    mCardNewsEntity
                            = FromSchemaToEntitiy.cardNews(response.body().getData().get(0));
                    // 카드뉴스 이미지 개수 세팅
                    if (mCardNewsEntity.getNewsImg() != null) {
                        setImageCount(mCardNewsEntity.getNewsImg().size());
                    }
                    cardnews_description.setText(mCardNewsEntity.getDescription());
                    cardNewsImagePagerAdapter.setData(mCardNewsEntity.getNewsImg());
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
                        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_off);
                        isScrap = false;
                    } else {
                        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_on);
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
                    snack(getText(R.string.snack_scrap_added).toString());
                    cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_on);
                    isScrap = true;
                } else {
                    cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_off);
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
                    snack(getText(R.string.snack_scrap_deleted).toString());
                    cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_off);
                    isScrap = false;
                    if (activityName != null && activityName.equals(INTENT_VALUE_SCRAP)) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } else {
                    cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_on);
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

    private void snack(String msg) {
        Snackbar.make(cardnews_detail_layout, msg, Snackbar.LENGTH_LONG).show();
    }

}
