package com.limefriends.molde.screen.controller.map.report;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.common.helper.BitmapHelper;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.R;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.view.map.report.ReportView;
import com.limefriends.molde.screen.controller.map.search.SearchLocationActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_AUTHORITY;
import static com.limefriends.molde.common.Constant.ReportState.CLEAN;

public class ReportActivity extends BaseActivity implements ReportView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ReportActivity.class);
        context.startActivity(intent);
    }

    private static final int TAKE_PICTURE_FOR_ADD_IMAGE = 100;
    private static final int REQ_REPORT_LOCATION = 997;

    private double reportLat;
    private double reportLng;

    private int reportState = Constant.ReportState.RECEIVING;
    private boolean isGreenFeed;
    private boolean isReporting = false;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Service private Repository.Feed mFeedRepository;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    @Service private ToastHelper mToastHelper;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;
    @Service private ViewFactory mViewFactory;

    private ReportView mReportView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mReportView = mViewFactory.newInstance(ReportView.class, null);

        setContentView(mReportView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();


        mReportView.registerListener(this);

        int authority = (int) PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);

        mReportView.bindAuthority(authority);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGreenFeed) mReportView.switchGreenZone();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQ_REPORT_LOCATION) {
            LatLng defaultLoc = ((MoldeApplication) getApplication()).getCurrLocation();
            reportLat = data.getDoubleExtra("reportLat", defaultLoc.latitude);
            reportLng = data.getDoubleExtra("reportLng", defaultLoc.longitude);
            String reportAddress = data.getStringExtra("reportAddress");
            mReportView.bindAddress(reportAddress);
        }
        else if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_FOR_ADD_IMAGE) {

            if (data != null) {
                Uri uri = data.getParcelableExtra("imagePath");
                int imageSeq = data.getIntExtra("imageSeq", 1);
                List<String> imagePathList = data.getStringArrayListExtra("imagePathList");

                if (uri != null && imageSeq != 0) {
                    mToastHelper.showShortToast("이미지를 업로드중입니다.");
                    mReportView.setPictureForReport(uri, imageSeq);
                }
                else if (imagePathList != null) {
                    mToastHelper.showShortToast("이미지를 업로드중입니다.");
                    mReportView.setPictureForReport(imagePathList, imageSeq);
                }
            }
        }
    }


    @Override
    public void onNavigateUpClicked() {
        if (isReporting()) return;
        mReportView.onBackPressed();
    }

    @Override
    public void onSelectPictureClicked(int sequence, int size) {
        if (isReporting()) return;
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                mActivityScreenNavigator.toMoldeReportCameraActivity(
                        ReportActivity.this,
                        sequence,
                        size,
                        TAKE_PICTURE_FOR_ADD_IMAGE);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                mReportView.showSnackBar("권한 거부\n" + deniedPermissions.toString());
            }
        };

        TedPermission.with(ReportActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(getText(R.string.snackbar_permission_denied))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    @Override
    public void onFindLocationClicked() {
        if (isReporting()) return;
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), SearchLocationActivity.class);
        intent.putExtra(EXTRA_KEY_ACTIVITY_NAME, "Report");
        startActivityForResult(intent, REQ_REPORT_LOCATION);
    }

    @Override
    public void onSendReportClicked(SparseArrayCompat<File> images, String reportContent,
                                    String reportAddress, String detailAddress, String email, boolean isGreenZone) {
        if (isReporting()) return;

        // 1. 이미지 등록 확인
        List<MultipartBody.Part> imageMultiParts = new ArrayList<>();
        for (int i = 1; i <= images.size(); i++) {
            imageMultiParts.add(
                    prepareFilePart("reportImageList", images.get(i)));
        }

        // 바디에 넣어줄 피드 데이터
        FirebaseAuth firebaseAuth = ((MoldeApplication) getApplication()).getFireBaseAuth();
        String reportUserId = firebaseAuth.getUid();
        String reportUserName = "";
        if (firebaseAuth.getCurrentUser().getDisplayName() != null) {
            reportUserName = firebaseAuth.getCurrentUser().getDisplayName();
        }

        if (isGreenZone) reportState = CLEAN;

        mReportView.showProgressIndication();

        isReporting = true;

        mCompositeDisposable.add(
                mFeedRepository.reportNewFeed(
                        reportUserId,
                        reportUserName,
                        email,
                        reportContent,
                        reportLat,
                        reportLng,
                        reportAddress,
                        detailAddress,
                        reportState,
                        System.currentTimeMillis(),
                        imageMultiParts
                ).subscribe(
                        e -> {
                            isReporting = false;
                            mReportView.hideProgressIndication();
                            mToastHelper.showShortToast(getText(R.string.snackbar_report_success).toString());
                            finish();
                        },
                        err -> {

                            if (err.getMessage().startsWith("413")) {
                                mReportView.showSnackBar("이미지 크기가 한도를 초과했습니다.");
                            } else {
                                mReportView.showSnackBar(getText(R.string.snackbar_network_error).toString());
                            }
                            mReportView.hideProgressIndication();
                            isReporting = false;
                        }
                )
        );
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    @Override
    public void onCancelReportClicked() {
        finish();
    }

    @Override
    public void onGreenZoneCheckChanged(boolean isChecked) {
        isGreenFeed = isChecked;
    }

    @Override
    public void onBackPressed() {
        mReportView.onBackPressed();
    }

    private boolean isReporting() {
        if (isReporting) {
            mReportView.showSnackBar("이미 신고중입니다.");
            return true;
        }
        return false;
    }
}
