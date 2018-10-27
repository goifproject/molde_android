package com.limefriends.molde.screen.feed;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.limefriends.molde.R;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.common.manager.cameraHelper.MoldeReportCameraActivity;
import com.limefriends.molde.common.utils.DateUtil;
import com.limefriends.molde.common.utils.PreferenceUtil;
import com.limefriends.molde.common.utils.StringUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.schema.feed.FeedImageSchema;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import static com.limefriends.molde.common.manager.cameraHelper.MoldeReportCameraActivity.TAKE_PICTURE_FOR_ADD_IMAGE;

public class FeedDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String REFUSE_REPORTED_COMMENT_DIALOG = "REFUSE_REPORTED_COMMENT_DIALOG";
    public static final String CANCEL_REPORT_FEED_DIALOG = "CANCEL_REPORT_FEED_DIALOG";
    public static final String CHANGE_FEED_STATE_DIALOG = "CHANGE_FEED_STATE_DIALOG";
    @BindView(R.id.feed_detail_container)
    LinearLayout feed_detail_container;
    // 이미지 페이저
    @BindView(R.id.mypage_detail_report_image_pager)
    ViewPager mypage_detail_report_image_pager;
    // 위치, 신고 내용
    @BindView(R.id.mypage_detail_report_location_content)
    TextView mypage_detail_report_location_content;
    @BindView(R.id.mypage_detail_report_content)
    TextView mypage_detail_report_content;
    // 신고 취소
    @BindView(R.id.mypage_detail_report_cancel_button)
    Button mypage_detail_report_cancel_button;

    // 신고 상태 이미지
    @BindView(R.id.report_detail_normal)
    RelativeLayout report_detail_normal;
    @BindView(R.id.siren_bad_status)
    ImageView siren_bad_status;
    @BindView(R.id.siren_receiving_status)
    ImageView siren_receiving_status;
    @BindView(R.id.siren_found_status)
    ImageView siren_found_status;
    @BindView(R.id.siren_clean_status)
    ImageView siren_clean_status;
    // 신고 상태 메시지
    @BindView(R.id.report_detail_result_text)
    TextView report_detail_result_text;

    // 관리자용 신고 상태 체크박스
    @BindView(R.id.report_detail_admin)
    RelativeLayout report_detail_admin;
    @BindView(R.id.progress_checkbox_admin_accepted)
    CheckBox progress_checkbox_admin_accepted;
    @BindView(R.id.progress_checkbox_admin_found)
    CheckBox progress_checkbox_admin_found;
    @BindView(R.id.progress_checkbox_admin_clean)
    CheckBox progress_checkbox_admin_clean;
    @BindView(R.id.report_confirm_admin)
    Button report_confirm_admin;

    // 진행 경과 프로그래스
    @BindView(R.id.myfeed_progress_line_first)
    View myfeed_progress_line_first;
    @BindView(R.id.myfeed_progress_line_second)
    View myfeed_progress_line_second;
    @BindView(R.id.myfeed_progress_dot_second_yellow)
    ImageView myfeed_progress_dot_second_yellow;
    @BindView(R.id.myfeed_progress_dot_third_yellow)
    ImageView myfeed_progress_dot_third_yellow;

    @BindView(R.id.myfeed_progress_text_receiving)
    TextView myfeed_progress_text_receiving;
    @BindView(R.id.myfeed_progress_text_accepted)
    TextView myfeed_progress_text_accepted;
    @BindView(R.id.myfeed_progress_text_completed)
    TextView myfeed_progress_text_completed;

    @BindView(R.id.myfeed_progress_receiving_date)
    TextView myfeed_progress_receiving_date;
    @BindView(R.id.myfeed_progress_accepted_date)
    TextView myfeed_progress_accepted_date;
    @BindView(R.id.myfeed_progress_completed_date)
    TextView myfeed_progress_completed_date;

    @BindView(R.id.report_detail_response_msg_background)
    ImageView report_detail_response_msg_background;

    // 진행 경과 프로그래스 어드민
    @BindView(R.id.myfeed_progress_line_first_admin)
    View myfeed_progress_line_first_admin;
    @BindView(R.id.myfeed_progress_line_second_admin)
    View myfeed_progress_line_second_admin;

    @BindView(R.id.myfeed_progress_dot_first_yellow_admin)
    ImageView myfeed_progress_dot_first_yellow_admin;
    @BindView(R.id.myfeed_progress_dot_second_yellow_admin)
    ImageView myfeed_progress_dot_second_yellow_admin;
    @BindView(R.id.myfeed_progress_dot_third_yellow_admin)
    ImageView myfeed_progress_dot_third_yellow_admin;

    @BindView(R.id.image_list)
    LinearLayout image_list;
    @BindView(R.id.first_iamge)
    ImageView first_iamge;
    @BindView(R.id.second_iamge)
    ImageView second_iamge;
    @BindView(R.id.third_iamge)
    ImageView third_iamge;
    @BindView(R.id.forth_image)
    ImageView forth_iamge;
    @BindView(R.id.fifth_image)
    ImageView fifth_iamge;

    @BindView(R.id.first_iamge_delete_button)
    ImageView first_iamge_delete_button;
    @BindView(R.id.second_iamge_delete_button)
    ImageView second_iamge_delete_button;
    @BindView(R.id.third_iamge_delete_button)
    ImageView third_iamge_delete_button;
    @BindView(R.id.forth_iamge_delete_button)
    ImageView forth_iamge_delete_button;
    @BindView(R.id.fifth_iamge_delete_button)
    ImageView fifth_iamge_delete_button;

    private FeedImageAdapter feedImageAdapter;
    private SparseArrayCompat<Uri> imageSparseArray = new SparseArrayCompat<>();
    private FeedEntity feedEntity;

    @Service private Repository.Feed mFeedRepository;
    @Service private Repository.FeedResult mFeedResultRepository;
    @Service private ToastHelper mToastHelper;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private String activityName;
    private int feedId;
    private int position;
    private long authority;
    private boolean isMyFeed;
    private boolean isloading;
    private int reportState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        setContentView(R.layout.activity_feed_detail);

        prepare();

        setupViews();

        setupListener();

        setImagePager();

        loadReport(feedId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyImageArray();
    }

    //-----
    // View
    //-----

    private void prepare() {
        activityName = getIntent().getStringExtra(EXTRA_KEY_ACTIVITY_NAME);

        feedId = getIntent().getIntExtra(EXTRA_KEY_FEED_ID, 0);

        position = getIntent().getIntExtra(EXTRA_KEY_POSITION, 0);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);
    }

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(getText(R.string.feed_detail));

        if (authority == ADMIN && activityName != null && activityName.equals(INTENT_VALUE_MY_FEED)) {
            report_detail_normal.setVisibility(View.GONE);
            report_detail_admin.setVisibility(View.VISIBLE);
            mypage_detail_report_cancel_button.setText(getText(R.string.deny_report));
            isMyFeed = true;
        }

    }

    private void setupListener() {
        mypage_detail_report_cancel_button.setOnClickListener(v -> {
            if (mypage_detail_report_cancel_button.getText().equals(getText(R.string.cancel_report))) {
                showDeleteFeedDialog(feedId);
            } else if (mypage_detail_report_cancel_button.getText().equals(getText(R.string.deny_report))) {
                showDenyFeedDialog(feedId);
            }
        });

        report_confirm_admin.setOnClickListener(v -> {

            if (isloading) {
                snack("상태를 변경중입니다.");
                return;
            }



            boolean accepted = progress_checkbox_admin_accepted.isChecked();
            boolean found = progress_checkbox_admin_found.isChecked();
            boolean clean = progress_checkbox_admin_clean.isChecked();


            if (reportState == CLEAN) {
                snack("확인이 완료된 신고입니다.");
                return;
            } else if (reportState == DENIED) {
                snack("거절된 신고입니다.");
                return;
            } else if (reportState == ACCEPTED && accepted) {
                snack("신고 상태를 변경해주세요");
                return;
            } else if (reportState == FOUND && found) {
                snack("신고 상태를 변경해주세요");
                return;
            } else if (!accepted && !found && !clean) {
                snack("신고 상태를 변경해주세요");
                return;
            }

            PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                    getText(R.string.dialog_change_feed_state).toString(),
                    "",
                    getText(R.string.yes).toString(),
                    getText(R.string.no).toString());
            promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
                @Override
                public void onPositiveButtonClicked() {



                    if (accepted) {
                        updateReport(feedId, ACCEPTED);
                    } else if (found) {
                        if (imageSparseArray != null && imageSparseArray.size() > 0) {
                            updateReport(feedId, FOUND);
                            reportInspection();
                        } else {
                            snack(getText(R.string.snackbar_no_image).toString());
                        }
                    } else if (clean) {
                        updateReport(feedId, CLEAN);
                    } else {
                        mToastHelper.showShortToast("진행중인 신고입니다");
                    }
                }

                @Override
                public void onNegativeButtonClicked() {

                }
            });
            mDialogManager.showRetainedDialogWithId(promptDialog, CHANGE_FEED_STATE_DIALOG);
        });

        progress_checkbox_admin_accepted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progress_checkbox_admin_found.setChecked(false);
                    progress_checkbox_admin_clean.setChecked(false);
                    image_list.setVisibility(View.GONE);
                }
            }
        });

        progress_checkbox_admin_found.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progress_checkbox_admin_accepted.setChecked(false);
                    progress_checkbox_admin_clean.setChecked(false);
                    if (feedEntity.getRepState() != FOUND) {
                        image_list.setVisibility(View.VISIBLE);
                    }
                } else {
                    image_list.setVisibility(View.GONE);
                }
            }
        });

        progress_checkbox_admin_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progress_checkbox_admin_accepted.setChecked(false);
                    progress_checkbox_admin_found.setChecked(false);
                    image_list.setVisibility(View.GONE);
                }
            }
        });

        first_iamge.setOnClickListener(this);
        second_iamge.setOnClickListener(this);
        third_iamge.setOnClickListener(this);
        forth_iamge.setOnClickListener(this);
        fifth_iamge.setOnClickListener(this);

        first_iamge_delete_button.setOnClickListener(this);
        second_iamge_delete_button.setOnClickListener(this);
        third_iamge_delete_button.setOnClickListener(this);
        forth_iamge_delete_button.setOnClickListener(this);
        fifth_iamge_delete_button.setOnClickListener(this);
    }

    private void showDenyFeedDialog(int feedId) {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getText(R.string.deny_message).toString(),
                "",
                getText(R.string.yes).toString(),
                getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                isloading = true;
                refuseReport(feedId, DENIED);
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, REFUSE_REPORTED_COMMENT_DIALOG);
    }

    private void showDeleteFeedDialog(int feedId) {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getText(R.string.cancel_message).toString(),
                "",
                getText(R.string.yes).toString(),
                getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                isloading = true;
                deleteReport(feedId);
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, CANCEL_REPORT_FEED_DIALOG);
    }

    private void setImagePager() {
        feedImageAdapter = new FeedImageAdapter(getApplicationContext());
        mypage_detail_report_image_pager.setAdapter(feedImageAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_iamge:
                takePictureForAdd(1);
                break;
            case R.id.second_iamge:
                takePictureForAdd(2);
                break;
            case R.id.third_iamge:
                takePictureForAdd(3);
                break;
            case R.id.forth_image:
                takePictureForAdd(4);
                break;
            case R.id.fifth_image:
                takePictureForAdd(5);
                break;
            case R.id.first_iamge_delete_button:
                imageSparseArray.delete(1);
                first_iamge_delete_button.setVisibility(View.INVISIBLE);
                first_iamge.setImageResource(R.drawable.ic_report_add);
                break;
            case R.id.second_iamge_delete_button:
                imageSparseArray.delete(2);
                second_iamge_delete_button.setVisibility(View.INVISIBLE);
                second_iamge.setImageResource(R.drawable.ic_report_add);
                break;
            case R.id.third_iamge_delete_button:
                imageSparseArray.delete(3);
                third_iamge_delete_button.setVisibility(View.INVISIBLE);
                third_iamge.setImageResource(R.drawable.ic_report_add);
                break;
            case R.id.forth_iamge_delete_button:
                imageSparseArray.delete(4);
                forth_iamge_delete_button.setVisibility(View.INVISIBLE);
                forth_iamge.setImageResource(R.drawable.ic_report_add);
                break;
            case R.id.fifth_iamge_delete_button:
                imageSparseArray.delete(5);
                fifth_iamge_delete_button.setVisibility(View.INVISIBLE);
                fifth_iamge.setImageResource(R.drawable.ic_report_add);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_FOR_ADD_IMAGE) {
            if (data != null) {
                Uri uri = data.getParcelableExtra("imagePath");
                int imageSeq = data.getIntExtra("imageSeq", 1);
                ArrayList<String> imagePathList = data.getStringArrayListExtra("imagePathList");

                if (uri != null && imageSeq != 0) {
                    Log.e("d", uri + "," + imageSeq);
                    if (imageSparseArray.get(imageSeq) == null) {
                        imageSparseArray.append(imageSeq, uri);
                    } else {
                        imageSparseArray.put(imageSeq, uri);
                    }
                } else if (imagePathList != null) {
                    int count = 0;
                    int bringImgListSize = imagePathList.size();
                    for (int i = 1; i <= 5; i++) {
                        if (bringImgListSize == 0) {
                            break;
                        }
                        if (imageSparseArray.get(i) == null) {
                            imageSparseArray.append(i, Uri.parse(imagePathList.get(count)));
                            count++;
                            bringImgListSize--;
                        }
                    }
                }
            }
        }
    }

    private void setSirenInvisible() {
        siren_bad_status.setVisibility(View.INVISIBLE);
        siren_receiving_status.setVisibility(View.INVISIBLE);
        siren_clean_status.setVisibility(View.INVISIBLE);
        siren_found_status.setVisibility(View.INVISIBLE);
    }


    private void setSirenState(int state) {
        setSirenInvisible();
        String resultText = "";
        switch (state) {
            case RECEIVING:
                resultText = getText(R.string.report_message_receiving).toString();
                siren_receiving_status.setVisibility(View.VISIBLE);
                // 접수중일 때만 삭제 가능
                if (isMyFeed) mypage_detail_report_cancel_button.setVisibility(View.VISIBLE);
                break;
            case ACCEPTED:
                siren_receiving_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.report_message_accepted).toString();
                break;
            case FOUND:
                siren_found_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.report_message_found).toString();
                break;
            case CLEAN:
                siren_clean_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.report_message_clean).toString();
                break;
            case DENIED:
                siren_bad_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.report_message_denied).toString();
                break;
        }
        report_detail_result_text.setText(resultText);
    }

    private void setDate(int state, String date) {
        switch (state) {
            case DENIED:
            case RECEIVING:
                myfeed_progress_receiving_date.setVisibility(View.VISIBLE);
                myfeed_progress_receiving_date.setText(DateUtil.fromLongToDate2(date));
                break;
            case ACCEPTED:
                myfeed_progress_accepted_date.setVisibility(View.VISIBLE);
                myfeed_progress_accepted_date.setText(DateUtil.fromLongToDate2(date));
                break;
            case FOUND:
            case CLEAN:
                myfeed_progress_completed_date.setVisibility(View.VISIBLE);
                myfeed_progress_completed_date.setText(DateUtil.fromLongToDate2(date));
                break;
        }
    }

    public void takePictureForAdd(final int imageSeq) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MoldeReportCameraActivity.class);
                intent.putExtra("imageSeq", imageSeq);
                intent.putExtra("imageArraySize", imageSparseArray.size());
                startActivityForResult(intent, TAKE_PICTURE_FOR_ADD_IMAGE);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                snack("권한 거부\n" + deniedPermissions.toString());
            }
        };

        TedPermission.with(FeedDetailActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(getText(R.string.snackbar_permission_denied))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    public void applyImageArray() {
        for (int i = 1; i <= 5; i++) {
            if (imageSparseArray.get(i) != null) {
                switch (i) {
                    case 1:
                        first_iamge.setImageURI(imageSparseArray.get(i));
                        first_iamge_delete_button.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        second_iamge.setImageURI(imageSparseArray.get(i));
                        second_iamge_delete_button.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        third_iamge.setImageURI(imageSparseArray.get(i));
                        third_iamge_delete_button.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        forth_iamge.setImageURI(imageSparseArray.get(i));
                        forth_iamge_delete_button.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        fifth_iamge.setImageURI(imageSparseArray.get(i));
                        fifth_iamge_delete_button.setVisibility(View.VISIBLE);
                        break;
                    default:
                        finish();
                }
            }
        }
    }

    private void changeProgress(int state) {
        switch (state) {
            case RECEIVING:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getResources().getColor(R.color.colorDivision));
                myfeed_progress_line_second.setBackgroundColor(
                        getResources().getColor(R.color.colorDivision));
                break;
            case ACCEPTED:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second.setBackgroundColor(
                        getResources().getColor(R.color.colorDivision));
                myfeed_progress_text_accepted
                        .setTextColor(getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_accepted
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimension(R.dimen.feed_detail_progress));
                break;
            case FOUND:
            case CLEAN:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_dot_third_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_text_accepted
                        .setTextColor(getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_completed
                        .setTextColor(getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_accepted
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimension(R.dimen.feed_detail_progress));
                myfeed_progress_text_completed
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimension(R.dimen.feed_detail_progress));
                report_detail_response_msg_background
                        .setImageResource(R.drawable.ic_response_msg_completed);
                break;
            case DENIED:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_text_accepted
                        .setTextColor(getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_completed
                        .setTextColor(getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_accepted
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimension(R.dimen.feed_detail_progress));
                myfeed_progress_text_completed
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimension(R.dimen.feed_detail_progress));
                report_detail_response_msg_background
                        .setImageResource(R.drawable.ic_response_msg_receiving);
                break;
        }
    }

    private void changeAdminProgress(int state) {
        switch (state) {
            case RECEIVING:
                break;
            case ACCEPTED:
                myfeed_progress_dot_first_yellow_admin.
                        setVisibility(View.VISIBLE);
                myfeed_progress_dot_second_yellow_admin
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_dot_third_yellow_admin
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_line_first_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorDivision));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorDivision));
                progress_checkbox_admin_accepted.setChecked(true);
                break;
            case FOUND:
                myfeed_progress_dot_first_yellow_admin.
                        setVisibility(View.VISIBLE);
                myfeed_progress_dot_second_yellow_admin
                        .setVisibility(View.VISIBLE);
                myfeed_progress_dot_third_yellow_admin
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_line_first_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorDivision));
                progress_checkbox_admin_accepted.setEnabled(false);
                progress_checkbox_admin_found.setEnabled(false);
                progress_checkbox_admin_found.setChecked(true);
                break;
            case CLEAN:
                myfeed_progress_dot_first_yellow_admin.
                        setVisibility(View.VISIBLE);
                myfeed_progress_dot_second_yellow_admin
                        .setVisibility(View.VISIBLE);
                myfeed_progress_dot_third_yellow_admin
                        .setVisibility(View.VISIBLE);
                myfeed_progress_line_first_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                progress_checkbox_admin_accepted.setEnabled(false);
                progress_checkbox_admin_found.setEnabled(false);
                progress_checkbox_admin_clean.setEnabled(false);
                progress_checkbox_admin_clean.setChecked(true);
                break;
            case DENIED:
                myfeed_progress_dot_first_yellow_admin.
                        setVisibility(View.VISIBLE);
                myfeed_progress_dot_second_yellow_admin
                        .setVisibility(View.VISIBLE);
                myfeed_progress_line_first_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getResources().getColor(R.color.colorAccent));
                progress_checkbox_admin_accepted.setEnabled(false);
                progress_checkbox_admin_found.setEnabled(false);
                progress_checkbox_admin_clean.setEnabled(false);
                progress_checkbox_admin_clean.setChecked(false);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (isloading) {
            snack("데이터를 로딩중입니다.");
            return;
        }
        super.onBackPressed();
    }

    //-----
    // Network
    //-----

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
                // 권한
                reportState = feedEntity.getRepState();
                // 위치
                mypage_detail_report_location_content.setText(
                        String.format("%s %s", feedEntity.getRepAddr(), feedEntity.getRepDetailAddr()));
                // 내용
                mypage_detail_report_content.setText(StringUtil.moveLine(feedEntity.getRepContents()));
                //
                if (feedEntity.getRepState() == RECEIVING) {
                    mypage_detail_report_cancel_button.setVisibility(View.VISIBLE);
                }
                if (authority == ADMIN) {
                    changeAdminProgress(feedEntity.getRepState());
                }
                changeProgress(feedEntity.getRepState());
                // 신고 상태
                setSirenState(feedEntity.getRepState());
                // 신고 날짜
                setDate(feedEntity.getRepState(), feedEntity.getRepDate());
                // 신고 이미지
                List<FeedImageSchema> imageList = feedEntity.getRepImg();
                List<String> imageUrls = new ArrayList<>();
                for (int i = 0; i < imageList.size(); i++) {
                    imageUrls.add(imageList.get(i).getFilepath());
                }
                feedImageAdapter.setData(imageUrls);
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
                changeAdminProgress(state);
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

    public void reportInspection() {

        if (feedEntity.getRepState() != FOUND && progress_checkbox_admin_found.isChecked()) {
            List<MultipartBody.Part> imageMultiParts = new ArrayList<>();
            for (int i = 1; i <= imageSparseArray.size(); i++) {
                imageMultiParts.add(
                        prepareFilePart("resultImageList", imageSparseArray.get(i)));
            }

            mCompositeDisposable.add(
                    mFeedResultRepository
                            .reportFeedResult(feedEntity.getRepId(), imageMultiParts)
                            .subscribeWith(getFeedResultObserver())
            );
        }
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
                image_list.setVisibility(View.GONE);
                isloading = false;
            }
        };
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void snack(String message) {
        Snackbar.make(feed_detail_container, message, Snackbar.LENGTH_LONG).show();
    }
}
