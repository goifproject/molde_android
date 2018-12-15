package com.limefriends.molde.screen.controller.magazine.detail;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.entity.scrap.ScrapEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.view.magazine.detail.CardNewsDetailView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_CARDNEWS_ID;
import static com.limefriends.molde.common.Constant.Scrap.INTENT_VALUE_SCRAP;

public class CardNewsDetailActivity extends BaseActivity implements CardNewsDetailView.Listener {

    public static void start(Context context, int newsId) {
        Intent intent = new Intent();
        intent.setClass(context, CardNewsDetailActivity.class);
        intent.putExtra(EXTRA_KEY_CARDNEWS_ID, newsId);
        context.startActivity(intent);
    }

    @Service
    private Repository.Scrap mScrapRepository;
    @Service
    private Repository.CardNews mCardNewsRepository;
    @Service
    private ToastHelper mToastHelper;
    @Service
    private ActivityScreenNavigator mActivityScreenNavigator;
    @Service
    private ViewFactory mViewFactory;
    @Service
    private ImageLoader mImageLoader;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private CardNewsDetailView mCardNewsDetailView;
    private CardNewsEntity mCardNewsEntity;
    private FirebaseAuth mFirebaseAuth;

    private String mUid;
    private String activityName;
    private boolean isLoading;
    private boolean isScrap;
    private int cardNewsScrapId;
    private int cardNewsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mCardNewsDetailView = mViewFactory.newInstance(CardNewsDetailView.class, null);

        setContentView(mCardNewsDetailView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCardNewsDetailView.registerListener(this);

        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);

        setupData();
    }


    private void setupData() {

        activityName = getIntent().getStringExtra(EXTRA_KEY_ACTIVITY_NAME);
        cardNewsId = getIntent().getIntExtra("cardNewsId", 0);
        mFirebaseAuth = ((MoldeApplication) getApplication()).getFireBaseAuth();
        mUid = mFirebaseAuth.getUid();

        loadCardNews(cardNewsId);
        loadMyScrap(cardNewsId, mUid);
    }

    private void loadCardNews(int cardNewsId) {

        mCompositeDisposable.add(
                mCardNewsRepository.getCardNewsListById(cardNewsId)
                        .subscribeWith(new DisposableObserver<List<CardNewsEntity>>() {
                            @Override
                            public void onNext(List<CardNewsEntity> newsEntityList) {
                                mCardNewsEntity = newsEntityList.get(0);
                                mCardNewsDetailView.bindPageCount(mCardNewsEntity.getNewsImg().size());
                                mCardNewsDetailView.bindCardNewsDescription(mCardNewsEntity.getDescription());
                                mCardNewsDetailView.bindCardNewsImages(mCardNewsEntity.getNewsImg());
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
                                        mCardNewsDetailView.unsetScrap();
                                        isScrap = false;
                                    } else {
                                        mCardNewsDetailView.setIsScrap();
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
                                        mCardNewsDetailView.showSnackBar(getText(R.string.snack_scrap_added).toString());
                                        mCardNewsDetailView.setIsScrap();
                                        isScrap = true;
                                    } else {
                                        mCardNewsDetailView.unsetScrap();
                                        isScrap = false;
                                    }
                                },
                                err -> {
                                },
                                () -> {
                                }
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
                                        mCardNewsDetailView.showSnackBar(getText(R.string.snack_scrap_deleted).toString());
                                        mCardNewsDetailView.unsetScrap();
                                        isScrap = false;
                                        if (activityName != null && activityName.equals(INTENT_VALUE_SCRAP)) {
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    } else {
                                        mCardNewsDetailView.setIsScrap();
                                        isScrap = true;
                                    }
                                },
                                err -> {
                                },
                                () -> {
                                }
                        )
        );
    }

    /**
     * Callback
     */

    @Override
    public void onCommentIconClicked(String description) {
        mActivityScreenNavigator
                .toCardNewsCommentActivity(cardNewsId, description);
    }

    @Override
    public void onScrapClicked() {
        if (mFirebaseAuth != null && mUid != null) {
            if (!isLoading && isScrap) {
                mCardNewsDetailView.showPromptDialog();
            } else {
                addToMyScrap(cardNewsId, mUid);
            }
        } else {
            mCardNewsDetailView.showSnackBar("몰디 로그인이 필요합니다!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCardNewsDetailView.hideProgressIndication();
    }

    @Override
    public void onShareClicked() {

        mCardNewsDetailView.showProgressIndication();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook")) {
                facebookAppFound = true;

                mToastHelper.showShortToast("페이스북 앱으로 이동합니다");

                mImageLoader.loadBitmap(mCardNewsEntity.getNewsImg().get(0).getImageUrl(), new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // 액션 타입이랑 Object 타입이 맞아야 올라가고
                        // 맞더라도 페이스북 오류 때문에 실시간으로 반영되지 않음
                        // 앱 설정은 그대로 따라가면 됨

                        // 페이스북 오류가 아니라 제대로 된 데이터를 넣지 않으면 포스트가 되지 않음
                        SharePhoto sharePhoto = new SharePhoto.Builder()
                                .setBitmap(resource)
                                .setUserGenerated(true)
                                .build();

                        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                                .putString("og:type", "article")
                                .putString("og:title", "테스트중입니다...")
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
                    }
                });
                break;
            }
        }
        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + mCardNewsEntity.getNewsImg().get(0).getImageUrl();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);

        }
    }

    @Override
    public void onDeleteScrapClicked() {
        deleteFromScrap(cardNewsScrapId, mUid);
    }
}
