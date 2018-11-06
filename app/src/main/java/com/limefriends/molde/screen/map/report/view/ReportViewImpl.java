package com.limefriends.molde.screen.map.report.view;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.utils.pattern.RegexUtil;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.views.BaseObservableView;
import com.limefriends.molde.screen.common.views.ViewFactory;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

import static com.limefriends.molde.common.Constant.Authority.ADMIN;
import static com.limefriends.molde.common.Constant.Authority.GUARDIAN;
import static com.limefriends.molde.common.Constant.Authority.MEMBER;

public class ReportViewImpl
        extends BaseObservableView<ReportView.Listener> implements ReportView, View.OnClickListener, NestedToolbar.SwitchGreenZoneListener, NestedToolbar.NavigateUpClickListener {

    public static final String CANCEL_REPORT_DIALOG = "CANCEL_REPORT_DIALOG";

    private RelativeLayout mReportLayout;
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

    private TextView search_loc_input;
    private EditText detail_address;
    private EditText report_email_input;
    private Spinner report_email_select;
    private EditText report_email_self_input;
    private ImageView report_self_close_button;
    private ImageView find_map_loc_button;
    private ProgressBar progressBar;
    private Button send_report_button;

    private EditText report_content;
    private TextView report_content_title;
    private LinearLayout green_zone_admin;

    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;

    private DialogFactory mDialogFactory;
    private DialogManager mDialogManager;

    private SparseArrayCompat<Uri> imageSparseArray = new SparseArrayCompat<>();
    private ArrayAdapter emailArrayAdapter;
    private ToastHelper mToastHelper;
    private ViewFactory mViewFactory;

    private int authority;
    private boolean isGreenZone;

    public ReportViewImpl(LayoutInflater inflater,
                          ViewGroup parent,
                          DialogFactory dialogFactory,
                          DialogManager dialogManager,
                          ToastHelper toastHelper,
                          ViewFactory viewFactory) {

        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;
        this.mToastHelper = toastHelper;
        this.mViewFactory = viewFactory;

        setRootView(inflater.inflate(R.layout.activity_report, parent, false));

        setupViews();

        setupToolbar();

        setupListener();

        setupEmailSpinner();
    }


    private void setupViews() {

        mReportLayout = findViewById(R.id.report_layout);

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

        search_loc_input = findViewById(R.id.search_loc_input);
        detail_address = findViewById(R.id.detail_address);
        report_email_input = findViewById(R.id.report_email_input);
        report_email_select = findViewById(R.id.report_email_select);
        report_email_self_input = findViewById(R.id.report_email_self_input);
        report_self_close_button = findViewById(R.id.report_self_close_button);
        find_map_loc_button = findViewById(R.id.find_map_loc_button);
        progressBar = findViewById(R.id.report_progress);
        send_report_button = findViewById(R.id.send_report_button);

        report_content = findViewById(R.id.report_content);
        report_content_title = findViewById(R.id.report_content_title);
        green_zone_admin = findViewById(R.id.green_zone_admin);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.report_toolbar);
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
        mNestedToolbar.setTitle(getContext().getText(R.string.report).toString());
    }

    private void setupListener() {

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

        report_self_close_button.setOnClickListener(this);
        find_map_loc_button.setOnClickListener(this);
        send_report_button.setOnClickListener(this);

        mNestedToolbar.enableUpButtonAndListen(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeAsUp:
                onBackPressed();
                break;
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
            case R.id.report_self_close_button:
                hideSoftKeyboard();
                closeSelfInput();
                break;
            case R.id.find_map_loc_button:
                for (Listener listener : getListeners()) {
                    listener.onFindLocationClicked();
                }
                break;
            case R.id.send_report_button:
                sendReport();
                break;
        }
    }

    private void setSwitchVisibility(int authority) {
        switch (authority) {
            case MEMBER:
                break;
            case GUARDIAN:
            case ADMIN:
                mNestedToolbar.enableSwitchButtonAndListen(this);
                break;
        }
    }

    private void closeSelfInput() {
        report_email_self_input.setVisibility(View.GONE);
        report_self_close_button.setVisibility(View.GONE);
        report_email_self_input.setText("");
        report_email_select.setVisibility(View.VISIBLE);
        report_email_select.bringToFront();
        report_email_select.setSelection(0);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mReportLayout.getWindowToken(), 0);
    }

    private void setupEmailSpinner() {
        emailArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.email_select, android.R.layout.simple_spinner_item);
        report_email_select.setAdapter(emailArrayAdapter);
        report_email_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    report_email_select.setSelected(false);
                    return;
                }
                if (position == emailArrayAdapter.getCount() - 1) {
                    report_email_select.setVisibility(View.GONE);
                    report_email_self_input.setVisibility(View.VISIBLE);
                    report_self_close_button.setVisibility(View.VISIBLE);
                    report_self_close_button.bringToFront();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sendReport() {

        if (imageSparseArray == null || imageSparseArray.size() == 0) {
            showSnackBar(getContext().getText(R.string.snackbar_no_image).toString());
            return;
        }

        String reportContent = report_content.getText().toString();
        String reportAddress = search_loc_input.getText().toString();
        String reportDetailAddress = detail_address.getText().toString();
        String email = report_email_input.getText().toString();

        if (reportContent.equals("") && authority == MEMBER) {
            showSnackBar(getContext().getText(R.string.snackbar_no_content).toString());
            return;
        }
        if (reportAddress.equals("")) {
            showSnackBar(getContext().getText(R.string.snackbar_no_address).toString());
            return;
        }
        if (reportDetailAddress.equals("")) {
            showSnackBar(getContext().getText(R.string.snackbar_no_detail_address).toString());
            return;
        }

        String reportEmail = "";
        if (report_email_input.getText().toString().equals("")) {
            showSnackBar(getContext().getText(R.string.snackbar_no_email).toString());
            return;
        } else {
            if (report_email_select.getVisibility() == View.GONE && report_email_self_input.getVisibility() == View.VISIBLE) {
                reportEmail = report_email_input.getText().toString() + "@" + report_email_self_input.getText().toString();
            } else if (report_email_select.getVisibility() == View.VISIBLE && report_email_self_input.getVisibility() == View.GONE) {
                reportEmail = report_email_input.getText().toString() + "@" + report_email_select.getSelectedItem().toString();
            }
        }

        if (RegexUtil.validateEmail(reportEmail)) {
            for (Listener listener : getListeners()) {
                listener.onSendReportClicked(imageSparseArray, reportContent, reportAddress, reportDetailAddress, email, isGreenZone);
            }
        } else {
            showSnackBar(getContext().getText(R.string.snackbar_wrong_email_form).toString());
        }
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
    public void switchGreenZone() {
        if (isGreenZone) {
            report_content.setVisibility(View.GONE);
            report_content_title.setVisibility(View.GONE);
            green_zone_admin.setVisibility(View.VISIBLE);
        } else {
            report_content.setVisibility(View.VISIBLE);
            report_content_title.setVisibility(View.VISIBLE);
            green_zone_admin.setVisibility(View.GONE);
        }
    }

    @Override
    public void showProgressIndication() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressIndication() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void bindAddress(String address) {
        search_loc_input.setText(address);
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
    public void onBackPressed() {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.dialog_cancel_report).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onCancelReportClicked();
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, CANCEL_REPORT_DIALOG);
    }

    @Override
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(mReportLayout, message);
    }

    @Override
    public void bindAuthority(int authority) {
        this.authority = authority;
        setSwitchVisibility(authority);
    }

    @Override
    public void onSwitchGreenZoneClicked(boolean isChecked) {
        isGreenZone = isChecked;
        for (Listener listener : getListeners()) {
            listener.onGreenZoneCheckChanged(isChecked);
        }
        switchGreenZone();
    }

    @Override
    public void onNavigateUpClicked() {
        for (Listener listener : getListeners()) {
            listener.onNavigateUpClicked();
        }
    }
}
