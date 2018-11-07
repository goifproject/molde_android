package com.limefriends.molde.screen.feed.detail.view;

import android.net.Uri;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.common.util.StringUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.entity.feed.FeedImageEntity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.imagePager.ImagePagerAdapter;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import java.util.List;

import static com.limefriends.molde.common.Constant.ReportState.ACCEPTED;
import static com.limefriends.molde.common.Constant.ReportState.CLEAN;
import static com.limefriends.molde.common.Constant.ReportState.DENIED;
import static com.limefriends.molde.common.Constant.ReportState.FOUND;
import static com.limefriends.molde.common.Constant.ReportState.RECEIVING;

public class FeedDetailViewImpl
        extends BaseObservableView<FeedDetailView.Listener> implements FeedDetailView, View.OnClickListener {

    public static final String REFUSE_REPORTED_COMMENT_DIALOG = "REFUSE_REPORTED_COMMENT_DIALOG";
    public static final String CANCEL_REPORT_FEED_DIALOG = "CANCEL_REPORT_FEED_DIALOG";
    public static final String CHANGE_FEED_STATE_DIALOG = "CHANGE_FEED_STATE_DIALOG";

    private LinearLayout feed_detail_container;
    // 이미지 페이저
    private ViewPager mypage_detail_report_image_pager;
    // 위치, 신고 내용
    private TextView mypage_detail_report_location_content;
    private TextView mypage_detail_report_content;
    // 신고 취소
    private Button mypage_detail_report_cancel_button;

    // 신고 상태 이미지
    private RelativeLayout report_detail_normal;
    private ImageView siren_bad_status;
    private ImageView siren_receiving_status;
    private ImageView siren_found_status;
    private ImageView siren_clean_status;
    // 신고 상태 메시지
    private TextView report_detail_result_text;

    // 관리자용 신고 상태 체크박스
    private RelativeLayout report_detail_admin;
    private CheckBox progress_checkbox_admin_accepted;
    private CheckBox progress_checkbox_admin_found;
    private CheckBox progress_checkbox_admin_clean;
    private Button report_confirm_admin;

    // 진행 경과 프로그래스
    private View myfeed_progress_line_first;
    private View myfeed_progress_line_second;
    private ImageView myfeed_progress_dot_second_yellow;
    private ImageView myfeed_progress_dot_third_yellow;

    private TextView myfeed_progress_text_receiving;
    private TextView myfeed_progress_text_accepted;
    private TextView myfeed_progress_text_completed;

    private TextView myfeed_progress_receiving_date;
    private TextView myfeed_progress_accepted_date;
    private TextView myfeed_progress_completed_date;

    private ImageView report_detail_response_msg_background;

    // 진행 경과 프로그래스 어드민
    private View myfeed_progress_line_first_admin;
    private View myfeed_progress_line_second_admin;

    private ImageView myfeed_progress_dot_first_yellow_admin;
    private ImageView myfeed_progress_dot_second_yellow_admin;
    private ImageView myfeed_progress_dot_third_yellow_admin;

    private LinearLayout image_list;
    private ImageView first_iamge;
    private ImageView second_iamge;
    private ImageView third_iamge;
    private ImageView forth_iamge;
    private ImageView fifth_iamge;

    private ImageView first_iamge_delete_button;
    private ImageView second_iamge_delete_button;
    private ImageView third_iamge_delete_button;
    private ImageView forth_iamge_delete_button;
    private ImageView fifth_iamge_delete_button;

    private Toolbar mToolbar;

    private DialogFactory mDialogFactory;
    private DialogManager mDialogManager;
    private NestedToolbar mNestedToolbar;
    private ViewFactory mViewFactory;
    private ToastHelper mToastHelper;
    private ImagePagerAdapter<FeedImageEntity> mFeedImageAdapter;

    private SparseArrayCompat<Uri> imageSparseArray = new SparseArrayCompat<>();
    private boolean isMyFeed;
    private FeedEntity mFeedEntity;
    private int reportState;

    public FeedDetailViewImpl(LayoutInflater inflater,
                              ViewGroup parent,
                              ViewFactory viewFactory,
                              DialogFactory dialogFactory,
                              DialogManager dialogManager,
                              ToastHelper toastHelper) {
        setRootView(inflater.inflate(R.layout.activity_feed_detail, parent, false));

        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;
        this.mViewFactory = viewFactory;
        this.mToastHelper = toastHelper;

        setupViews();

        setupToolbar();

        setupListener();

        setImagePager();
    }

    private void setupViews() {

        feed_detail_container = findViewById(R.id.feed_detail_container);
        mypage_detail_report_image_pager = findViewById(R.id.mypage_detail_report_image_pager);
        mypage_detail_report_location_content = findViewById(R.id.mypage_detail_report_location_content);
        mypage_detail_report_content = findViewById(R.id.mypage_detail_report_content);
        mypage_detail_report_cancel_button = findViewById(R.id.mypage_detail_report_cancel_button);

        report_detail_normal = findViewById(R.id.report_detail_normal);
        siren_bad_status = findViewById(R.id.siren_bad_status);
        siren_receiving_status = findViewById(R.id.siren_receiving_status);
        siren_found_status = findViewById(R.id.siren_found_status);
        siren_clean_status = findViewById(R.id.siren_clean_status);
        report_detail_result_text = findViewById(R.id.report_detail_result_text);

        report_detail_admin = findViewById(R.id.report_detail_admin);
        progress_checkbox_admin_accepted = findViewById(R.id.progress_checkbox_admin_accepted);
        progress_checkbox_admin_found = findViewById(R.id.progress_checkbox_admin_found);
        progress_checkbox_admin_clean = findViewById(R.id.progress_checkbox_admin_clean);
        report_confirm_admin = findViewById(R.id.report_confirm_admin);

        myfeed_progress_line_first = findViewById(R.id.myfeed_progress_line_first);
        myfeed_progress_line_second = findViewById(R.id.myfeed_progress_line_second);
        myfeed_progress_dot_second_yellow = findViewById(R.id.myfeed_progress_dot_second_yellow);
        myfeed_progress_dot_third_yellow = findViewById(R.id.myfeed_progress_dot_third_yellow);

        myfeed_progress_text_receiving = findViewById(R.id.myfeed_progress_text_receiving);
        myfeed_progress_text_accepted = findViewById(R.id.myfeed_progress_text_accepted);
        myfeed_progress_text_completed = findViewById(R.id.myfeed_progress_text_completed);

        myfeed_progress_receiving_date = findViewById(R.id.myfeed_progress_receiving_date);
        myfeed_progress_accepted_date = findViewById(R.id.myfeed_progress_accepted_date);
        myfeed_progress_completed_date = findViewById(R.id.myfeed_progress_completed_date);

        report_detail_response_msg_background = findViewById(R.id.report_detail_response_msg_background);

        myfeed_progress_line_first_admin = findViewById(R.id.myfeed_progress_line_first_admin);
        myfeed_progress_line_second_admin = findViewById(R.id.myfeed_progress_line_second_admin);

        myfeed_progress_dot_first_yellow_admin = findViewById(R.id.myfeed_progress_dot_first_yellow_admin);
        myfeed_progress_dot_second_yellow_admin = findViewById(R.id.myfeed_progress_dot_second_yellow_admin);
        myfeed_progress_dot_third_yellow_admin = findViewById(R.id.myfeed_progress_dot_third_yellow_admin);

        image_list = findViewById(R.id.image_list);
        first_iamge = findViewById(R.id.first_iamge);
        second_iamge = findViewById(R.id.second_iamge);
        third_iamge = findViewById(R.id.third_iamge);
        forth_iamge = findViewById(R.id.forth_image);
        fifth_iamge = findViewById(R.id.fifth_image);

        first_iamge_delete_button = findViewById(R.id.first_iamge_delete_button);
        second_iamge_delete_button = findViewById(R.id.second_iamge_delete_button);
        third_iamge_delete_button = findViewById(R.id.third_iamge_delete_button);
        forth_iamge_delete_button = findViewById(R.id.forth_iamge_delete_button);
        fifth_iamge_delete_button = findViewById(R.id.fifth_iamge_delete_button);

        mToolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mNestedToolbar.setTitle(getContext().getText(R.string.feed_detail).toString());
        mToolbar.addView(mNestedToolbar.getRootView());
    }

    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        mypage_detail_report_cancel_button.setOnClickListener(v -> {

            if (mypage_detail_report_cancel_button.getText()
                    .equals(getContext().getText(R.string.cancel_report))) {
                showDeleteFeedDialog(mFeedEntity.getRepId());
            } else if (mypage_detail_report_cancel_button.getText()
                    .equals(getContext().getText(R.string.deny_report))) {
                showDenyFeedDialog(mFeedEntity.getRepId());
            }
        });

        report_confirm_admin.setOnClickListener(v -> {

            boolean accepted = progress_checkbox_admin_accepted.isChecked();
            boolean found = progress_checkbox_admin_found.isChecked();
            boolean clean = progress_checkbox_admin_clean.isChecked();

            if (reportState == CLEAN) {
                mToastHelper.showSnackBar(feed_detail_container, "확인이 완료된 신고입니다.");
                return;
            } else if (reportState == DENIED) {
                mToastHelper.showSnackBar(feed_detail_container, "거절된 신고입니다.");
                return;
            } else if (reportState == ACCEPTED && accepted) {
                mToastHelper.showSnackBar(feed_detail_container, "신고 상태를 변경해주세요");
                return;
            } else if (reportState == FOUND && found) {
                mToastHelper.showSnackBar(feed_detail_container, "신고 상태를 변경해주세요");
                return;
            } else if (!accepted && !found && !clean) {
                mToastHelper.showSnackBar(feed_detail_container, "신고 상태를 변경해주세요");
                return;
            }
            showChangeFeedDialog(accepted, found, clean);
        });

        progress_checkbox_admin_accepted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                progress_checkbox_admin_found.setChecked(false);
                progress_checkbox_admin_clean.setChecked(false);
                image_list.setVisibility(View.GONE);
            }
        });

        progress_checkbox_admin_found.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progress_checkbox_admin_accepted.setChecked(false);
                    progress_checkbox_admin_clean.setChecked(false);
                    if (reportState != FOUND) {
                        image_list.setVisibility(View.VISIBLE);
                    }
                } else {
                    image_list.setVisibility(View.GONE);
                }
            }
        });

        progress_checkbox_admin_clean.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                progress_checkbox_admin_accepted.setChecked(false);
                progress_checkbox_admin_found.setChecked(false);
                image_list.setVisibility(View.GONE);
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
                getContext().getText(R.string.deny_message).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onRefuseClicked(feedId);
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, REFUSE_REPORTED_COMMENT_DIALOG);
    }

    private void showChangeFeedDialog(boolean accepted, boolean found, boolean clean) {

        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.dialog_change_feed_state).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {


                if (accepted) {
                    for (Listener listener : getListeners()) {
                        listener.onUpdateClicked(mFeedEntity.getRepId(), ACCEPTED);
                    }
                } else if (found) {
                    if (imageSparseArray != null && imageSparseArray.size() > 0) {

                        for (Listener listener : getListeners()) {
                            listener.onUpdateClicked(mFeedEntity.getRepId(), FOUND);
                            listener.onReportInspectionClicked(imageSparseArray);
                        }
                    } else {
                        mToastHelper.showSnackBar(feed_detail_container,
                                getContext().getText(R.string.snackbar_no_image).toString());
                    }
                } else if (clean) {
                    for (Listener listener : getListeners()) {
                        listener.onUpdateClicked(mFeedEntity.getRepId(), CLEAN);
                    }
                } else {
                    mToastHelper.showShortToast("진행중인 신고입니다");
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, CHANGE_FEED_STATE_DIALOG);
    }

    private void showDeleteFeedDialog(int feedId) {

        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.cancel_message).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onDeleteClicked(feedId);
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, CANCEL_REPORT_FEED_DIALOG);
    }

    private void setImagePager() {
        mFeedImageAdapter = new ImagePagerAdapter<>(mViewFactory);
        mypage_detail_report_image_pager.setAdapter(mFeedImageAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_iamge:
                for (Listener listener : getListeners()) {
                    listener.onSelectPictureClicked(1, imageSparseArray.size());
                }
                break;
            case R.id.second_iamge:
                for (Listener listener : getListeners()) {
                    listener.onSelectPictureClicked(2, imageSparseArray.size());
                }
                break;
            case R.id.third_iamge:
                for (Listener listener : getListeners()) {
                    listener.onSelectPictureClicked(3, imageSparseArray.size());
                }
                break;
            case R.id.forth_image:
                for (Listener listener : getListeners()) {
                    listener.onSelectPictureClicked(4, imageSparseArray.size());
                }
                break;
            case R.id.fifth_image:
                for (Listener listener : getListeners()) {
                    listener.onSelectPictureClicked(5, imageSparseArray.size());
                }
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
                resultText = getContext().getText(R.string.report_message_receiving).toString();
                siren_receiving_status.setVisibility(View.VISIBLE);
                // 접수중일 때만 삭제 가능
                if (isMyFeed) mypage_detail_report_cancel_button.setVisibility(View.VISIBLE);
                break;
            case ACCEPTED:
                siren_receiving_status.setVisibility(View.VISIBLE);
                resultText = getContext().getText(R.string.report_message_accepted).toString();
                break;
            case FOUND:
                siren_found_status.setVisibility(View.VISIBLE);
                resultText = getContext().getText(R.string.report_message_found).toString();
                break;
            case CLEAN:
                siren_clean_status.setVisibility(View.VISIBLE);
                resultText = getContext().getText(R.string.report_message_clean).toString();
                break;
            case DENIED:
                siren_bad_status.setVisibility(View.VISIBLE);
                resultText = getContext().getText(R.string.report_message_denied).toString();
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

    private void changeProgress(int state) {
        switch (state) {
            case RECEIVING:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
                myfeed_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
                break;
            case ACCEPTED:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
                myfeed_progress_text_accepted
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_accepted
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getContext().getResources().getDimension(R.dimen.feed_detail_progress));
                break;
            case FOUND:
            case CLEAN:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_dot_third_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_text_accepted
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_completed
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_accepted
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getContext().getResources().getDimension(R.dimen.feed_detail_progress));
                myfeed_progress_text_completed
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getContext().getResources().getDimension(R.dimen.feed_detail_progress));
                report_detail_response_msg_background
                        .setImageResource(R.drawable.ic_response_msg_completed);
                break;
            case DENIED:
                myfeed_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                myfeed_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_text_accepted
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_completed
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                myfeed_progress_text_accepted
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getContext().getResources().getDimension(R.dimen.feed_detail_progress));
                myfeed_progress_text_completed
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                getContext().getResources().getDimension(R.dimen.feed_detail_progress));
                report_detail_response_msg_background
                        .setImageResource(R.drawable.ic_response_msg_receiving);
                break;
        }
    }

    @Override
    public void changeAdminProgress(int state) {
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
                        getContext().getResources().getColor(R.color.colorDivision));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
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
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
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
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
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
                        getContext().getResources().getColor(R.color.colorAccent));
                myfeed_progress_line_second_admin.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                progress_checkbox_admin_accepted.setEnabled(false);
                progress_checkbox_admin_found.setEnabled(false);
                progress_checkbox_admin_clean.setEnabled(false);
                progress_checkbox_admin_clean.setChecked(false);
                break;
        }
    }

    @Override
    public void hideImageList() {
        image_list.setVisibility(View.GONE);
    }

    private void applyImage(Uri uri, int seq) {
        switch (seq) {
            case 1:
                first_iamge.setImageURI(uri);
                first_iamge_delete_button.setVisibility(View.VISIBLE);
                break;
            case 2:
                second_iamge.setImageURI(uri);
                second_iamge_delete_button.setVisibility(View.VISIBLE);
                break;
            case 3:
                third_iamge.setImageURI(uri);
                third_iamge_delete_button.setVisibility(View.VISIBLE);
                break;
            case 4:
                forth_iamge.setImageURI(uri);
                forth_iamge_delete_button.setVisibility(View.VISIBLE);
                break;
            case 5:
                fifth_iamge.setImageURI(uri);
                fifth_iamge_delete_button.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(feed_detail_container, message);
    }

    @Override
    public void setPictureForReport(Uri uri, int seq) {
        imageSparseArray.append(seq, uri);
        applyImage(uri, seq);
    }

    @Override
    public void setPictureForReport(List<String> imagePathList, int seq) {
        int count = 0;
        int bringImgListSize = imagePathList.size();
        for (int i = 1; i <= 5; i++) {
            if (bringImgListSize == 0) {
                break;
            }
            if (imageSparseArray.get(i) == null) {
                imageSparseArray.append(i, Uri.parse(imagePathList.get(count)));
                applyImage(Uri.parse(imagePathList.get(count)), i);
                count++;
                bringImgListSize--;
            }
        }
    }

    @Override
    public void bindFeedDetail(FeedEntity feedEntity) {
        mFeedEntity = feedEntity;
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

        changeAdminProgress(feedEntity.getRepState());

        changeProgress(feedEntity.getRepState());
        // 신고 상태
        setSirenState(feedEntity.getRepState());
        // 신고 날짜
        setDate(feedEntity.getRepState(), feedEntity.getRepDate());
        // 신고 이미지
        mFeedImageAdapter.setData(feedEntity.getRepImg());
    }

    @Override
    public void switchToAdminPage() {
        report_detail_normal.setVisibility(View.GONE);
        report_detail_admin.setVisibility(View.VISIBLE);
        mypage_detail_report_cancel_button.setText(getContext().getText(R.string.deny_report));
        isMyFeed = true;
    }
}
