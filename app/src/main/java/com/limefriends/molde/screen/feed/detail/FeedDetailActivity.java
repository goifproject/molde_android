package com.limefriends.molde.screen.feed.detail;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.limefriends.molde.R;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.screen.common.camera.MoldeReportCameraActivity;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.feed.detail.view.FeedDetailView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.limefriends.molde.common.Constant.Authority.*;
import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_POSITION;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_AUTHORITY;
import static com.limefriends.molde.common.Constant.Feed.*;
import static com.limefriends.molde.common.Constant.ReportState.*;
import static com.limefriends.molde.screen.common.camera.MoldeReportCameraActivity.TAKE_PICTURE_FOR_ADD_IMAGE;

public class FeedDetailActivity extends BaseActivity implements FeedDetailView.Listener {

    public static void start(Context context, int feedId) {
        Intent intent = new Intent(context, FeedDetailActivity.class);
        intent.putExtra(EXTRA_KEY_FEED_ID, feedId);
        context.startActivity(intent);
    }

    private FeedEntity feedEntity;

    @Service private Repository.Feed mFeedRepository;
    @Service private Repository.FeedResult mFeedResultRepository;
    @Service private ToastHelper mToastHelper;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;

    @Service ViewFactory mViewFactory;
    private FeedDetailView mFeedDetailView;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private String activityName;
    private int feedId;
    private int position;
    private long authority;
    private boolean isloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mFeedDetailView = mViewFactory.newInstance(FeedDetailView.class, null);

        setContentView(mFeedDetailView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFeedDetailView.registerListener(this);

        prepare();

        // 먼저 뷰를 세팅하고
        if (authority == ADMIN && activityName != null && activityName.equals(INTENT_VALUE_MY_FEED)) {
            mFeedDetailView.switchToAdminPage();
        }

        // 데이터를 불러와야 함
        loadReport(feedId);
    }

    private void prepare() {
        activityName = getIntent().getStringExtra(EXTRA_KEY_ACTIVITY_NAME);

        feedId = getIntent().getIntExtra(EXTRA_KEY_FEED_ID, 0);

        position = getIntent().getIntExtra(EXTRA_KEY_POSITION, 0);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_FOR_ADD_IMAGE) {
            if (data != null) {
                Uri uri = data.getParcelableExtra("imagePath");
                int imageSeq = data.getIntExtra("imageSeq", 1);
                List<String> imagePathList = data.getStringArrayListExtra("imagePathList");

                if (uri != null && imageSeq != 0) {
                    mFeedDetailView.setPictureForReport(uri, imageSeq);
                } else if (imagePathList != null) {
                    mFeedDetailView.setPictureForReport(imagePathList, imageSeq);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isloading) {
            mFeedDetailView.showSnackBar("데이터를 로딩중입니다.");
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onNavigateUpClicked() {
        if (isloading) {
            mFeedDetailView.showSnackBar("데이터를 로딩중입니다.");
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDeleteClicked(int feedId) {
        deleteReport(feedId);
    }

    @Override
    public void onRefuseClicked(int feedId) {
        refuseReport(feedId, DENIED);
    }

    @Override
    public void onUpdateClicked(int feedId, int state) {
        if (isloading) {
            mFeedDetailView.showSnackBar("상태를 변경중입니다.");
            return;
        }
        updateReport(feedId, state);
    }

    @Override
    public void onReportInspectionClicked(SparseArrayCompat<Uri> images) {
        if (feedEntity.getRepState() != FOUND) {
            List<MultipartBody.Part> imageMultiParts = new ArrayList<>();
            for (int i = 1; i <= images.size(); i++) {
                imageMultiParts.add(
                        prepareFilePart("resultImageList", images.get(i)));
            }

            mCompositeDisposable.add(
                    mFeedResultRepository
                            .reportFeedResult(feedEntity.getRepId(), imageMultiParts)
                            .subscribeWith(getFeedResultObserver())
            );
        }
    }

    @Override
    public void onSelectPictureClicked(int sequence, int size) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MoldeReportCameraActivity.class);
                intent.putExtra("imageSeq", sequence);
                intent.putExtra("imageArraySize", size);
                startActivityForResult(intent, TAKE_PICTURE_FOR_ADD_IMAGE);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                mFeedDetailView.showSnackBar("권한 거부\n" + deniedPermissions.toString());
            }
        };

        TedPermission.with(FeedDetailActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(getText(R.string.snackbar_permission_denied))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    private void loadReport(int reportId) {

        mCompositeDisposable.add(
                mFeedRepository
                        .getFeedById(reportId)
                        .subscribeWith(getFetchFeedObserver())
        );
    }

    private DisposableObserver<List<FeedEntity>> getFetchFeedObserver() {
        return new DisposableObserver<List<FeedEntity>>() {
            @Override
            public void onNext(List<FeedEntity> feedEntities) {
                feedEntity = feedEntities.get(0);

                mFeedDetailView.bindFeedDetail(feedEntity);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    public void deleteReport(int reportId) {

        FirebaseAuth auth = ((MoldeApplication) getApplication()).getFireBaseAuth();

        String uId = auth.getCurrentUser().getUid();

        mCompositeDisposable.add(
                mFeedRepository
                        .deleteFeed(uId, reportId)
                        .subscribeWith(getDeleteFeedObserver())
        );
    }

    private DisposableObserver<Result> getDeleteFeedObserver() {
        return new DisposableObserver<Result>() {
            @Override
            public void onNext(Result result) {
                isloading = false;

                mToastHelper.showShortToast("신고를 취소했습니다.");

                Intent intent = new Intent();
                intent.putExtra(EXTRA_KEY_POSITION, position);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void updateReport(int reportId, final int state) {

        isloading = true;

        mCompositeDisposable.add(
                mFeedRepository
                        .updateFeed(reportId, state)
                        .subscribeWith(getUpdateFeedObserver(state))
        );
    }

    private DisposableObserver<Result> getUpdateFeedObserver(int state) {
        return new DisposableObserver<Result>() {
            @Override
            public void onNext(Result result) {
                mFeedDetailView.changeAdminProgress(state);
                isloading = false;

                Intent intent = new Intent();
                intent.putExtra(EXTRA_KEY_POSITION, position);
                intent.putExtra(EXTRA_KEY_STATE, state);
                setResult(RESULT_OK, intent);

                if (state == DENIED) {
                    mToastHelper.showShortToast("신고를 거절했습니다.");
                } else {
                    mToastHelper.showShortToast("신고 상태가 변경되었습니다.");
                }
            }

            @Override
            public void onError(Throwable e) {
                mToastHelper.showNetworkError();
            }

            @Override
            public void onComplete() {
                isloading = false;
            }
        };
    }

    public void refuseReport(int reportId, final int state) {
        // 체크된 거로 데이터 바꿔주자
        updateReport(reportId, state);
    }

    private DisposableObserver<Result> getFeedResultObserver() {
        return new DisposableObserver<Result>() {
            @Override
            public void onNext(Result result) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                mFeedDetailView.hideImageList();
                isloading = false;
            }
        };
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
