package com.limefriends.molde.screen.magazine.detail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
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
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.model.entity.scrap.ScrapEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.common.Constant.Scrap.INTENT_VALUE_SCRAP;

public class CardNewsDetailActivity extends BaseActivity {

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

    private static final String DELETE_SCRAP_DIALOG_TAG = "DELETE_SCRAP_DIALOG_TAG";

    @Service private Repository.Scrap mScrapRepository;
    @Service private Repository.CardNews mCardNewsRepository;
    @Service private ToastHelper mToastHelper;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private String activityName;
    private boolean isLoading;
    private boolean isScrap;
    private int cardNewsScrapId;
    private int cardNewsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardnews_detail);

        getInjector().inject(this);

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

                mActivityScreenNavigator
                        .toCardNewsCommentActivity(cardNewsId,
                                cardnews_description.getText().toString());
            }
        });

        cardnews_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirebaseAuth != null && mFirebaseAuth.getUid() != null) {
                    final String uId = mFirebaseAuth.getCurrentUser().getUid();
                    if (!isLoading && isScrap) {
                        showPromptDialog();
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

                ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                        .putString("og:type", "article")
                        .putString("og:title", mCardNewsEntity.getDescription())
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

    private void showPromptDialog() {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getText(R.string.scrap_delete_message).toString(),
                "",
                getText(R.string.yes).toString(),
                getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                deleteFromScrap(cardNewsScrapId, mFirebaseAuth.getUid());
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, DELETE_SCRAP_DIALOG_TAG);
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

        mCompositeDisposable.add(
                mCardNewsRepository.getCardNewsListById(cardNewsId)
                        .subscribeWith(new DisposableObserver<List<CardNewsEntity>>() {
                            @Override
                            public void onNext(List<CardNewsEntity> newsEntityList) {
                                mCardNewsEntity = newsEntityList.get(0);
                                if (mCardNewsEntity.getNewsImg() != null) {
                                    setImageCount(mCardNewsEntity.getNewsImg().size());
                                }
                                cardnews_description.setText(mCardNewsEntity.getDescription());
                                cardNewsImagePagerAdapter.setData(mCardNewsEntity.getNewsImg());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    private void loadMyScrap(int cardNewsId, String userId) {

        isLoading = true;

        List<ScrapEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mScrapRepository
                        .getScrap(userId, cardNewsId)
                        .subscribe(
                                data::add,
                                err -> {
                                },
                                () -> {
                                    if (data.size() == 0) {
                                        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_off);
                                        isScrap = false;
                                    } else {
                                        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_on);
                                        isScrap = true;
                                        cardNewsScrapId = data.get(0).getScrapId();
                                    }
                                    isLoading = false;
                                }
                        )
        );
    }

    private void addToMyScrap(int cardNewsId, String userId) {

        mCompositeDisposable.add(
                mScrapRepository
                        .addToMyScrap(userId, cardNewsId)
                        .subscribe(
                                e -> {
                                    if (e.getResult() == 1) {
                                        snack(getText(R.string.snack_scrap_added).toString());
                                        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_on);
                                        isScrap = true;
                                    } else {
                                        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_off);
                                        isScrap = false;
                                    }
                                },
                                err -> {},
                                () -> {}
                        )
        );
    }

    private void deleteFromScrap(int cardNewsScrapId, String userId) {

        mCompositeDisposable.add(
                mScrapRepository
                        .deleteMyScrap(userId, cardNewsScrapId)
                        .subscribe(
                                e -> {
                                    if (e.getResult() == 1) {
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
                                },
                                err -> {},
                                () -> {}
                        )
        );
    }

    private void setImageCount(int size) {
        total_page_no.setText(String.valueOf(size));
    }

    private void snack(String msg) {
        Snackbar.make(cardnews_detail_layout, msg, Snackbar.LENGTH_SHORT).show();
    }
}
