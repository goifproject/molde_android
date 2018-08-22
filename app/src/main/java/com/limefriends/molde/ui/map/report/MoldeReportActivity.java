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

public class MoldeReportActivity extends AppCompatActivity {

    public static final int REQ_REPORT_LOCATION = 997;
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

    private final int TAKE_PICTURE_FOR_ADD_IMAGE = 100;
    private static SparseArrayCompat<Uri> imageSparseArray;
    private SearchMapInfoEntity searchEntity;
    private SearchMapHistoryEntity historyEntity;
    private ArrayAdapter emailArrayAdapter;

    private String reportUserId;
    private String reportUserName;
    private String reportContent;
    private String reportAddress;
    private String reportDetailAddress;
    private String reportEmail;
    private double reportLat;
    private double reportLng;
    private Date reportDate;

    private int reportState = Constant.ReportState.RECEIVING;

    public ArrayList<String> imagePathList = new ArrayList<>();

    long authority;

    boolean isGreenFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_activity_molde_report);

        ButterKnife.bind(this);

        // TODO 권한 설정을 통해 switch 가능하게 할 것인지 여부 결정해 줘야 한다.

        setupViews();

        setupListener();

        setupEmailSpinner();

        authority = PreferenceUtil.getLong(this, "authority");

        if (imageSparseArray == null) {
            imageSparseArray = new SparseArrayCompat<>();
        }

//        Intent data = getIntent();
//        if (data != null) {
//            Uri uri = data.getParcelableExtra("imagePath");
//            int imageSeq = data.getIntExtra("imageSeq", 1);
//            imagePathList = data.getStringArrayListExtra("imagePathList");
//            searchEntity = (SearchMapInfoEntity) data.getSerializableExtra("mapSearchInfo");
//            historyEntity = (SearchMapHistoryEntity) data.getSerializableExtra("mapHistoryInfo");
//            report_content.setText(data.getStringExtra("reportContent"));
//            search_loc_input.setText(data.getStringExtra("reportAddress"));
//            detail_address.setText(data.getStringExtra("reportDetailAddress"));
//            report_email_input.setText(data.getStringExtra("reportEmailName"));
//            // TODO 디퐅르 장소로 바꿔줌
//            reportLat = data.getDoubleExtra("reportLat", 0);
//            reportLng = data.getDoubleExtra("reportLng", 0);
//
//            if (data.getStringExtra("reportEmailDomainPosition") != null) {
//                report_email_select.setSelection(Integer.parseInt(data.getStringExtra("reportEmailDomainPosition")));
//            }
//
//            if (uri != null && imageSeq != 0) {
//                Log.e("d", uri + "," + imageSeq);
//                if (imageSparseArray.get(imageSeq) == null) {
//                    imageSparseArray.append(imageSeq, uri);
//                } else {
//                    imageSparseArray.put(imageSeq, uri);
//                }
//            } else if (imagePathList != null) {
//                int count = 0;
//                int bringImgListSize = imagePathList.size();
//                for (int i = 1; i <= 5; i++) {
//                    if (bringImgListSize == 0) {
//                        break;
//                    }
//                    if (imageSparseArray.get(i) == null) {
//                        imageSparseArray.append(i, Uri.parse(imagePathList.get(count)));
//                        count++;
//                        bringImgListSize--;
//                    }
//                }
//            } else if (searchEntity != null || historyEntity != null) {
//                if (searchEntity != null) {
//                    search_loc_input.setText(searchEntity.getMainAddress() + "\n" + searchEntity.getName());
//                    reportAddress = searchEntity.getMainAddress() + " " + searchEntity.getName();
//
//                    reportLat = searchEntity.getMapLat();
//                    reportLng = searchEntity.getMapLng();
//                } else if (historyEntity != null) {
//                    search_loc_input.setText(historyEntity.getMainAddress() + "\n" + historyEntity.getName());
//                    reportAddress = historyEntity.getMainAddress() + " " + historyEntity.getName();
//
//                    reportLat = historyEntity.getMapLat();
//                    reportLng = historyEntity.getMapLng();
//                }
//            }
//        }

    }

    private void setupViews() {

        report_title.setText(getText(R.string.report));

        setSupportActionBar(report_toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
//        toolbar_title.setText("신고하기");

        long status = PreferenceUtil.getLong(this, "authority");

        switch ((int)status) {
            case Constant.Authority.MEMBER:
                switch_green_zone.setVisibility(View.GONE);
                break;
            case Constant.Authority.GUARDIAN:
            case Constant.Authority.ADMIN:
                switch_green_zone.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void setupListener() {

        first_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureForAdd(1);
            }
        });

        second_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureForAdd(2);
            }
        });

        third_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureForAdd(3);
            }
        });

        forth_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureForAdd(4);
            }
        });

        fifth_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureForAdd(5);
            }
        });

        first_iamge_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSparseArray.delete(1);
                first_iamge_delete_button.setVisibility(View.INVISIBLE);
                first_iamge.setImageResource(R.drawable.ic_image_add);
            }
        });

        second_iamge_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSparseArray.delete(2);
                second_iamge_delete_button.setVisibility(View.INVISIBLE);
                second_iamge.setImageResource(R.drawable.ic_image_add);
            }
        });

        third_iamge_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSparseArray.delete(3);
                third_iamge_delete_button.setVisibility(View.INVISIBLE);
                third_iamge.setImageResource(R.drawable.ic_image_add);
            }
        });

        forth_iamge_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSparseArray.delete(4);
                forth_iamge_delete_button.setVisibility(View.INVISIBLE);
                forth_iamge.setImageResource(R.drawable.ic_image_add);
            }
        });

        fifth_iamge_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSparseArray.delete(5);
                fifth_iamge_delete_button.setVisibility(View.INVISIBLE);
                fifth_iamge.setImageResource(R.drawable.ic_image_add);
            }
        });

        report_self_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(report_email_select.getWindowToken(), 0);
                report_email_self_input.setVisibility(View.GONE);
                report_self_close_button.setVisibility(View.GONE);
                report_email_self_input.setText("");
                report_email_select.setVisibility(View.VISIBLE);
                report_email_select.bringToFront();
                report_email_select.setSelection(0);
            }
        });

        find_map_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SearchMapInfoActivity.class);
                intent.putExtra("activity", "Report");
//                intent.putExtra("reportContent", report_content.getText().toString());
//                intent.putExtra("reportDetailAddress", detail_address.getText().toString());
//                intent.putExtra("reportEmailName", report_email_input.getText().toString());
//                intent.putExtra("reportEmailDomainPosition", report_email_select.getSelectedItemPosition());
//                Log.e("email domain position", "position : " + report_email_select.getSelectedItemPosition());
//                intent.putExtra("reportLat", reportLat);
//                intent.putExtra("reportLng", reportLng);
                startActivityForResult(intent, REQ_REPORT_LOCATION);
            }
        });

        send_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                // 이미지 등록 확인
                List<MultipartBody.Part> imageMultiParts = null;
                if (imageSparseArray != null && imageSparseArray.size() > 0) {
                    imageMultiParts = new ArrayList<>();
                    for (int i = 1; i <= imageSparseArray.size(); i++) {
                        //reportImageFileList.add(new File(imageSparseArray.get(i).getPath()));
                        imageMultiParts.add(prepareFilePart("reportImageList", imageSparseArray.get(i)));
                    }
                } else {
                    snack("이미지가 없잖아 임마");
                    return;
                }

                // 바디에 넣어줄 피드 데이터
                FirebaseAuth firebaseAuth = ((MoldeApplication) getApplication()).getFireBaseAuth();
                reportUserId = firebaseAuth.getUid();

                // TODO 원래는 아이디가 있어야 이 페이지에 접근할 수 있기 때문에 이후 뺴줘야 함
                if (firebaseAuth.getCurrentUser() != null)
                    reportUserName = firebaseAuth.getCurrentUser().getDisplayName();

                reportContent = report_content.getText().toString();
                reportAddress = search_loc_input.getText().toString();
                reportDetailAddress = detail_address.getText().toString();


                reportDate = new Date();

                if (reportContent.equals("") && authority == Constant.Authority.MEMBER) {
                    toast("내용이 없잖아 임마. 신고가 장난이냐");
                    return;
                }

                if (reportAddress.equals("")) {
                    toast("주소가 없잖아 임마. 신고가 장난이냐");
                    return;
                }

                if (reportDetailAddress.equals("")) {
                    toast("상세 주소가 없잖아 임마. 숨바꼭질하냐");
                    return;
                }

                if (switch_green_zone.isChecked()) {
                    reportEmail = firebaseAuth.getCurrentUser().getEmail();
                } else {
                    if (report_email_input.getText().toString().equals("")) {
                        toast("이메일 주소가 없잖아 임마. 신고가 장난이냐");
                    } else {
                        if (report_email_select.getVisibility() == View.GONE && report_email_self_input.getVisibility() == View.VISIBLE) {
                            reportEmail = report_email_input.getText().toString() + "@" + report_email_self_input.getText().toString();
                        } else if (report_email_select.getVisibility() == View.VISIBLE && report_email_self_input.getVisibility() == View.GONE) {
                            reportEmail = report_email_input.getText().toString() + "@" + report_email_select.getSelectedItem().toString();
                        }
                    }
                }

//                if (report_email_select.getVisibility() == View.GONE && report_email_self_input.getVisibility() == View.VISIBLE) {
//                    reportEmail = report_email_input.getText().toString() + "@" + report_email_self_input.getText().toString();
//                } else if (report_email_select.getVisibility() == View.VISIBLE && report_email_self_input.getVisibility() == View.GONE) {
//                    reportEmail = report_email_input.getText().toString() + "@" + report_email_select.getSelectedItem().toString();
//                }
//
//                // TODO 어차피 로그인 되어 있는 상태에서 들어오기 때문에 이메일 주소는 자동으로 세팅되도록 하자
//                if (reportEmail.equals("")) {
//                    reportEmail = firebaseAuth.getCurrentUser().getEmail();
//                    toast("이메일 주소가 없잖아 임마. 신고가 장난이냐");
//                    //return;
//                }

                if (switch_green_zone.isChecked()) {
                    reportState = Constant.ReportState.CLEAN;
                }

                if (RegexUtil.validateEmail(reportEmail)) {
                    //MoldeReportEntity moldeReportEntity = new MoldeReportEntity(reportUserId, reportUserName, 0, reportContent, reportAddress, reportDetailAddress, reportEmail, reportLat, reportLng, reportDate);

//                    FeedEntity moldeFeedEntity = new FeedEntity(
//                            reportUserName,
//                            reportEmail,
//                            reportUserId,
//                            reportContent,
//                            reportLat,
//                            reportLng,
//                            reportAddress,
//                            reportDetailAddress,
//                            reportDate.toString(),
//                            null,
//                            0);
//
//                    Log.e("entity json Data", new Gson().toJson(moldeFeedEntity));

                    MoldeRestfulService.Feed feedService
                            = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

                    //MultipartBody.Part body = prepareFilePart("reportImageList", imageSparseArray.get(1));

                    // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
                    Call<Result> moldeReportEntityCall = feedService.reportNewFeed3(
                            // null 값 때문에 문제가 생긴 듯 하다
                            reportUserId,
                            reportUserName,
                            reportEmail,
                            reportContent,
                            reportLat,
                            reportLng,
                            reportAddress,
                            reportDetailAddress,
                            // TODO 신고 상태 constant 에서 따로 관리할 것
                            reportState,
                            System.currentTimeMillis(),
                            imageMultiParts
                    );

                    progressBar.setVisibility(View.VISIBLE);

//                    샘플 - 정상 동작함
//                    Call<Result> moldeReportEntityCall = feedService.reportNewFeed3(
//                            "lkj",
//                            "이기정",
//                            // TODO 신고 상태 constant 에서 따로 관리할 것
//                            "lkj@gmail.com",
//                            "신고 내용내용",
//                            37.12341234,
//                            127.3452345,
//                            "서울 동대문구 전농동 90",
//                            "첫번쨰칸",
//                            0,
//                            23485092,
//                            imageMultiParts
//                    );

//                    Retrofit retrofit = MoldeNetwork.getNetworkInstance().getRetrofit();
//                    MoldeReportRestService moldeReportRestService = retrofit.create(MoldeReportRestService.class);
//                    Call<ResponseBody> moldeReportEntityCall = moldeReportRestService.sendReportData(reportUserId, reportUserName, 0, reportContent, reportAddress, reportDetailAddress, reportEmail, reportLat, reportLng, reportDate, imageMultiParts);
                    moldeReportEntityCall.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "데이터 전송 성공", Toast.LENGTH_SHORT).show();
                                Log.e("로그 자 보자", response.body().getResult() + "");
                                // TODO 1 여기서 finish() 잠시 두고 3-5대 열어놓고 연속으로 누르면서 부하를 얼만큼 버티는지 알아보자
                                // TODO 2 해당 지역이면 바로 반영될 수 있도록 할 것
                                progressBar.setVisibility(View.INVISIBLE);
                                toast("신고가 접수됬습니다. 감사합니다");
                                finish();
                            } else {
                                try {
                                    Log.e("로그 자 보자", response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "데이터 전송 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "이메일 양식에 맞지 않습니다. 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switch_green_zone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isGreenFeed = isChecked;
                switchGreenZone();
            }
        });
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
        String[] emailType = getResources().getStringArray(R.array.email_select);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    // TODO 이런 것들 왜 넘겨주는거야
    public void takePictureForAdd(final int imageSeq) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MoldeReportCameraActivity.class);
                intent.putExtra("imageSeq", imageSeq);
                intent.putExtra("imageArraySize", imageSparseArray.size());
                intent.putExtra("reportContent", report_content.getText().toString());
                if (searchEntity != null || historyEntity != null) {
                    if (searchEntity != null) {
                        intent.putExtra("reportAddress", searchEntity.getMainAddress() + "\n" + searchEntity.getName());
                    } else if (historyEntity != null) {
                        intent.putExtra("reportAddress", historyEntity.getMainAddress() + "\n" + historyEntity.getName());
                    }
                } else {
                    intent.putExtra("reportAddress", search_loc_input.getText().toString());
                }
                intent.putExtra("reportDetailAddress", detail_address.getText().toString());
                intent.putExtra("reportEmailName", report_email_input.getText().toString());
                intent.putExtra("reportEmailDomainPosition", report_email_select.getSelectedItemPosition());
                intent.putExtra("reportLat", reportLat);
                intent.putExtra("reportLng", reportLng);
                startActivityForResult(intent, TAKE_PICTURE_FOR_ADD_IMAGE);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(MoldeReportActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("권한얻는 데 실패했습니다.\n\n[설정] -> [어플리케이션] -> 해당앱에 들어가 권한을 켜주세요.")
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
    protected void onResume() {
        super.onResume();
        if (isGreenFeed) {
            switch_green_zone.setChecked(isGreenFeed);
        }
        applyImageArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("호출확인", "onActivityResult - Report1");
        if (resultCode == RESULT_OK && requestCode == REQ_REPORT_LOCATION) {
            Log.e("호출확인", "onActivityResult - Report2");
            LatLng defaultLoc = ((MoldeApplication) getApplication()).getCurrLocation();
            reportLat = data.getDoubleExtra("reportLat", defaultLoc.latitude);
            reportLng = data.getDoubleExtra("reportLng", defaultLoc.longitude);
            reportAddress = data.getStringExtra("reportAddress");
            // Log.e("호출확인", reportAddress);
            search_loc_input.setText(reportAddress);
        } else if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_FOR_ADD_IMAGE) {
            if (data != null) {
                Uri uri = data.getParcelableExtra("imagePath");
                int imageSeq = data.getIntExtra("imageSeq", 1);
                imagePathList = data.getStringArrayListExtra("imagePathList");
                searchEntity = (SearchMapInfoEntity) data.getSerializableExtra("mapSearchInfo");
                historyEntity = (SearchMapHistoryEntity) data.getSerializableExtra("mapHistoryInfo");
                report_content.setText(data.getStringExtra("reportContent"));
                search_loc_input.setText(data.getStringExtra("reportAddress"));
                detail_address.setText(data.getStringExtra("reportDetailAddress"));
                report_email_input.setText(data.getStringExtra("reportEmailName"));
                // TODO 디퐅르 장소로 바꿔줌
                reportLat = data.getDoubleExtra("reportLat", 0);
                reportLng = data.getDoubleExtra("reportLng", 0);

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
                } else if (searchEntity != null || historyEntity != null) {
                    if (searchEntity != null) {
                        search_loc_input.setText(searchEntity.getMainAddress() + "\n" + searchEntity.getName());
                        reportAddress = searchEntity.getMainAddress() + " " + searchEntity.getName();

                        reportLat = searchEntity.getMapLat();
                        reportLng = searchEntity.getMapLng();
                    } else if (historyEntity != null) {
                        search_loc_input.setText(historyEntity.getMainAddress() + "\n" + historyEntity.getName());
                        reportAddress = historyEntity.getMainAddress() + " " + historyEntity.getName();

                        reportLat = historyEntity.getMapLat();
                        reportLng = historyEntity.getMapLng();
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

    @Override
    public void onBackPressed() {
        SearchMapInfoActivity.isCheckBackPressed = true;
        imageSparseArray.clear();
        imageSparseArray = null;
        finish();
    }

    private void snack(String message) {
        Snackbar.make(findViewById(R.id.report_layout), message, Snackbar.LENGTH_SHORT).show();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




}
