package com.limefriends.molde.ui.map.report;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.limefriends.molde.comm.Constant;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.comm.utils.pattern.RegexUtil;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.comm.manager.camera_manager.MoldeReportCameraActivity;
import com.limefriends.molde.entity.map.SearchMapHistoryEntity;
import com.limefriends.molde.entity.map.SearchMapInfoEntity;
import com.limefriends.molde.ui.map.search.SearchMapInfoActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.limefriends.molde.comm.Constant.Authority.*;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_AUTHORITY;

public class MoldeReportActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.first_iamge)
    ImageButton first_iamge;
    @BindView(R.id.second_iamge)
    ImageButton second_iamge;
    @BindView(R.id.third_iamge)
    ImageButton third_iamge;
    @BindView(R.id.forth_image)
    ImageButton forth_iamge;
    @BindView(R.id.fifth_image)
    ImageButton fifth_iamge;

    @BindView(R.id.first_iamge_delete_button)
    ImageButton first_iamge_delete_button;
    @BindView(R.id.second_iamge_delete_button)
    ImageButton second_iamge_delete_button;
    @BindView(R.id.third_iamge_delete_button)
    ImageButton third_iamge_delete_button;
    @BindView(R.id.forth_iamge_delete_button)
    ImageButton forth_iamge_delete_button;
    @BindView(R.id.fifth_iamge_delete_button)
    ImageButton fifth_iamge_delete_button;

    @BindView(R.id.search_loc_input)
    TextView search_loc_input;
    @BindView(R.id.detail_address)
    EditText detail_address;
    @BindView(R.id.report_email_input)
    EditText report_email_input;
    @BindView(R.id.report_email_select)
    Spinner report_email_select;
    @BindView(R.id.report_email_self_input)
    EditText report_email_self_input;
    @BindView(R.id.report_self_close_button)
    ImageButton report_self_close_button;
    @BindView(R.id.find_map_loc_button)
    ImageButton find_map_loc_button;
    @BindView(R.id.report_progress)
    ProgressBar progressBar;
    @BindView(R.id.send_report_button)
    Button send_report_button;

    @BindView(R.id.report_content)
    EditText report_content;
    @BindView(R.id.report_content_title)
    TextView report_content_title;
    @BindView(R.id.green_zone_admin)
    LinearLayout green_zone_admin;
    @BindView(R.id.report_email_title)
    TextView report_email_title;
    @BindView(R.id.report_email_spinner)
    RelativeLayout report_email_spinner;
    @BindView(R.id.switch_green_zone)
    Switch switch_green_zone;

    @BindView(R.id.report_toolbar)
    Toolbar report_toolbar;
    @BindView(R.id.report_title)
    TextView report_title;

    private static final int TAKE_PICTURE_FOR_ADD_IMAGE = 100;
    private static final int REQ_REPORT_LOCATION = 997;

    private ArrayAdapter emailArrayAdapter;
    private SparseArrayCompat<Uri> imageSparseArray;

    private double reportLat;
    private double reportLng;

    private int reportState = Constant.ReportState.RECEIVING;
    private int authority;
    private boolean isGreenFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_molde_report);

        // 권한 설정
        authority = (int) PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY);

        setupViews();

        setupListener();

        setupEmailSpinner();

        imageSparseArray = new SparseArrayCompat<>();
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        setSupportActionBar(report_toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // 권한에 따라 그린 신고 가능 여부 바뀜
        switch (authority) {
            case MEMBER:
                switch_green_zone.setVisibility(View.GONE);
                break;
            case GUARDIAN:
            case ADMIN:
                switch_green_zone.setVisibility(View.VISIBLE);
                break;
        }

        report_title.setText(getText(R.string.report));
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

        switch_green_zone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isGreenFeed = isChecked;
                switchGreenZone();
            }
        });
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
                first_iamge.setImageResource(R.drawable.ic_image_add);
                break;
            case R.id.second_iamge_delete_button:
                imageSparseArray.delete(2);
                second_iamge_delete_button.setVisibility(View.INVISIBLE);
                second_iamge.setImageResource(R.drawable.ic_image_add);
                break;
            case R.id.third_iamge_delete_button:
                imageSparseArray.delete(3);
                third_iamge_delete_button.setVisibility(View.INVISIBLE);
                third_iamge.setImageResource(R.drawable.ic_image_add);
                break;
            case R.id.forth_iamge_delete_button:
                imageSparseArray.delete(4);
                forth_iamge_delete_button.setVisibility(View.INVISIBLE);
                forth_iamge.setImageResource(R.drawable.ic_image_add);
                break;
            case R.id.fifth_iamge_delete_button:
                imageSparseArray.delete(5);
                fifth_iamge_delete_button.setVisibility(View.INVISIBLE);
                fifth_iamge.setImageResource(R.drawable.ic_image_add);
                break;
            case R.id.report_self_close_button:
                InputMethodManager inputMethodManager
                        = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager
                        .hideSoftInputFromWindow(report_email_select.getWindowToken(), 0);
                report_email_self_input.setVisibility(View.GONE);
                report_self_close_button.setVisibility(View.GONE);
                report_email_self_input.setText("");
                report_email_select.setVisibility(View.VISIBLE);
                report_email_select.bringToFront();
                report_email_select.setSelection(0);
                break;
            case R.id.find_map_loc_button:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SearchMapInfoActivity.class);
                intent.putExtra("activity", "Report");
                startActivityForResult(intent, REQ_REPORT_LOCATION);
                break;
            case R.id.send_report_button:
                sendReport();
                break;


        }
    }

    private void switchGreenZone() {
        if (switch_green_zone.isChecked()) {
            report_content.setVisibility(View.GONE);
            report_content_title.setVisibility(View.GONE);
            green_zone_admin.setVisibility(View.VISIBLE);
            report_email_title.setVisibility(View.GONE);
            report_email_spinner.setVisibility(View.GONE);
        } else {
            report_content.setVisibility(View.VISIBLE);
            report_content_title.setVisibility(View.VISIBLE);
            green_zone_admin.setVisibility(View.GONE);
            report_email_title.setVisibility(View.VISIBLE);
            report_email_spinner.setVisibility(View.VISIBLE);
        }
    }

    private void setupEmailSpinner() {
        emailArrayAdapter = ArrayAdapter.createFromResource(this, R.array.email_select, android.R.layout.simple_spinner_item);
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

    private void snack(String message) {
        Snackbar.make(findViewById(R.id.report_layout), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    //-----
    // Network
    //-----

    private void sendReport() {

        // 1. 이미지 등록 확인
        List<MultipartBody.Part> imageMultiParts = null;
        if (imageSparseArray != null && imageSparseArray.size() > 0) {
            imageMultiParts = new ArrayList<>();
            for (int i = 1; i <= imageSparseArray.size(); i++) {
                imageMultiParts.add(
                        prepareFilePart("reportImageList", imageSparseArray.get(i)));
            }
        } else {
            snack(getText(R.string.snackbar_no_image).toString());
            return;
        }

        // 바디에 넣어줄 피드 데이터
        FirebaseAuth firebaseAuth = ((MoldeApplication) getApplication()).getFireBaseAuth();
        String reportUserId = firebaseAuth.getUid();
        String reportUserName = "";
        if (firebaseAuth.getCurrentUser().getDisplayName() != null) {
            reportUserName = firebaseAuth.getCurrentUser().getDisplayName();
        }

        // 신고 내용
        String reportContent = report_content.getText().toString();
        // 신고 주소
        String reportAddress = search_loc_input.getText().toString();
        // 상세 위치
        String reportDetailAddress = detail_address.getText().toString();

        if (reportContent.equals("") && authority == MEMBER) {
            snack(getText(R.string.snackbar_no_content).toString());
            return;
        }
        if (reportAddress.equals("")) {
            snack(getText(R.string.snackbar_no_address).toString());
            return;
        }
        if (reportDetailAddress.equals("")) {
            snack(getText(R.string.snackbar_no_detail_address).toString());
            return;
        }

        String reportEmail = "";

        if (switch_green_zone.isChecked()) {
            reportEmail = firebaseAuth.getCurrentUser().getEmail();
        } else {
            if (report_email_input.getText().toString().equals("")) {
                snack(getText(R.string.snackbar_no_email).toString());
            } else {
                if (report_email_select.getVisibility() == View.GONE && report_email_self_input.getVisibility() == View.VISIBLE) {
                    reportEmail = report_email_input.getText().toString() + "@" + report_email_self_input.getText().toString();
                } else if (report_email_select.getVisibility() == View.VISIBLE && report_email_self_input.getVisibility() == View.GONE) {
                    reportEmail = report_email_input.getText().toString() + "@" + report_email_select.getSelectedItem().toString();
                }
            }
        }

        if (switch_green_zone.isChecked()) {
            reportState = Constant.ReportState.CLEAN;
        }

        if (RegexUtil.validateEmail(reportEmail)) {

            MoldeRestfulService.Feed feedService
                    = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

            Call<Result> moldeReportEntityCall = feedService.reportNewFeed3(
                    reportUserId,
                    reportUserName,
                    reportEmail,
                    reportContent,
                    reportLat,
                    reportLng,
                    reportAddress,
                    reportDetailAddress,
                    reportState,
                    System.currentTimeMillis(),
                    imageMultiParts
            );

            progressBar.setVisibility(View.VISIBLE);

            moldeReportEntityCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful()) {
                        // TODO 1 여기서 finish() 잠시 두고 3-5대 열어놓고 연속으로 누르면서 부하를 얼만큼 버티는지 알아보자
                        // TODO 2 해당 지역이면 바로 반영될 수 있도록 할 것
                        progressBar.setVisibility(View.INVISIBLE);
                        snack(getText(R.string.snackbar_report_success).toString());
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    snack(getText(R.string.snackbar_network_error).toString());
                }
            });
        } else {
            snack(getText(R.string.snackbar_wrong_email_form).toString());
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
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                snack("권한 거부\n" + deniedPermissions.toString());
            }
        };

        TedPermission.with(MoldeReportActivity.this)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_REPORT_LOCATION) {
            LatLng defaultLoc = ((MoldeApplication) getApplication()).getCurrLocation();
            reportLat = data.getDoubleExtra("reportLat", defaultLoc.latitude);
            reportLng = data.getDoubleExtra("reportLng", defaultLoc.longitude);
            String reportAddress = data.getStringExtra("reportAddress");
            search_loc_input.setText(reportAddress);
        } else if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_FOR_ADD_IMAGE) {
            if (data != null) {
                Uri uri = data.getParcelableExtra("imagePath");
                int imageSeq = data.getIntExtra("imageSeq", 1);
                ArrayList<String> imagePathList = data.getStringArrayListExtra("imagePathList");

                if (data.getStringExtra("reportEmailDomainPosition") != null) {
                    report_email_select.setSelection(Integer.parseInt(data.getStringExtra("reportEmailDomainPosition")));
                }

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

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        Log.e("file uri", fileUri.toString());
        File file = new File(fileUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    //-----
    // lifecycle
    //-----

    @Override
    protected void onResume() {
        super.onResume();
        if (isGreenFeed) {
            switch_green_zone.setChecked(true);
        }
        applyImageArray();
    }


}
