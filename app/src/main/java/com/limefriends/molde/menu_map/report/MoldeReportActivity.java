package com.limefriends.molde.menu_map.report;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.limefriends.molde.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.camera_manager.MoldeReportCameraActivity;
import com.limefriends.molde.menu_map.entity.MoldeReportEntity;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapHistoryEntity;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapInfoEntity;
import com.limefriends.molde.menu_map.search.SearchMapInfoActivity;
import com.limefriends.molde.molde_backend.MoldeNetwork;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MoldeReportActivity extends AppCompatActivity {
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

    @BindView(R.id.report_content)
    EditText report_content;

    @BindView(R.id.search_loc_input)
    TextView search_loc_input;

    @BindView(R.id.detail_address)
    EditText detail_address;

    @BindView(R.id.reply_email_input)
    EditText reply_email_input;

    @BindView(R.id.reply_email_select)
    Spinner reply_email_select;

    @BindView(R.id.reply_email_self)
    EditText reply_email_self;
    @BindView(R.id.self_close_button)
    ImageButton self_close_button;

    @BindView(R.id.find_map_loc_button)
    ImageButton find_map_loc_button;

    @BindView(R.id.send_report_button)
    Button send_report_button;

    private final int TAKE_PICTURE_FOR_ADD_IMAGE = 100;
    private static SparseArrayCompat<Uri> imageSparseArray;
    private MoldeSearchMapInfoEntity searchEntity;
    private MoldeSearchMapHistoryEntity historyEntity;

    private String reportUserId;
    private String reportUserName;
    private String reportContent;
    private String reportAddress;
    private String reportDetailAddress;
    private String reportEmail;
    private String reportLat;
    private String reportLng;
    private Date reportDate;

    public ArrayList<String> imagePathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_molde_report);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("신고하기");

        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //UI 컨트롤
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

        if (imageSparseArray == null) {
            imageSparseArray = new SparseArrayCompat<Uri>();
        }

        Intent data = getIntent();
        if (data != null) {
            Uri uri = data.getParcelableExtra("imagePath");
            int imageSeq = data.getIntExtra("imageSeq", 1);
            imagePathList = data.getStringArrayListExtra("imagePathList");
            searchEntity = (MoldeSearchMapInfoEntity) data.getSerializableExtra("mapSearchInfo");
            historyEntity = (MoldeSearchMapHistoryEntity) data.getSerializableExtra("mapHistoryInfo");
            report_content.setText(data.getStringExtra("reportContent"));
            search_loc_input.setText(data.getStringExtra("reportAddress"));
            detail_address.setText(data.getStringExtra("reportDetailAddress"));
            reply_email_input.setText(data.getStringExtra("reportEmailName"));
            reportLat = data.getStringExtra("reportLat");
            reportLng = data.getStringExtra("reportLng");

            if (data.getStringExtra("reportEmailDomainPosition") != null) {
                reply_email_select.setSelection(Integer.parseInt(data.getStringExtra("reportEmailDomainPosition")));
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

        final ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.email_select, android.R.layout.simple_spinner_item);
        reply_email_select.setAdapter(arrayAdapter);
        reply_email_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    reply_email_select.setSelected(false);
                    return;
                }
                if (position == arrayAdapter.getCount() - 1) {
                    Log.e("d", "직접선택");
                    reply_email_select.setVisibility(View.GONE);
                    reply_email_select.setClickable(false);

                    reply_email_self.setClickable(true);
                    reply_email_self.setVisibility(View.VISIBLE);
                    self_close_button.setVisibility(View.VISIBLE);
                    self_close_button.bringToFront();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        self_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply_email_self.setText("");
                inputMethodManager.hideSoftInputFromWindow(reply_email_select.getWindowToken(), 0);
                reply_email_select.setVisibility(View.VISIBLE);
                reply_email_select.bringToFront();
                reply_email_select.setSelection(0);
            }
        });

        find_map_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SearchMapInfoActivity.class);
                intent.putExtra("activity", "Report");
                intent.putExtra("reportContent", report_content.getText().toString());
                intent.putExtra("reportDetailAddress", detail_address.getText().toString());
                intent.putExtra("reportEmailName", reply_email_input.getText().toString());
                intent.putExtra("reportEmailDomainPosition", reply_email_select.getSelectedItemPosition());
                Log.e("email domain position", "position : " + reply_email_select.getSelectedItemPosition());
                intent.putExtra("reportLat", reportLat);
                intent.putExtra("reportLng", reportLng);
                startActivity(intent);
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
                intent.putExtra("reportEmailName", reply_email_input.getText().toString());
                intent.putExtra("reportEmailDomainPosition", reply_email_select.getSelectedItemPosition());
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
        applyImageArray();

        send_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = MoldeNetwork.getNetworkInstance().getRetrofit();
                MoldeReportRestService moldeReportRestService = retrofit.create(MoldeReportRestService.class);
                reportUserId = MoldeApplication.firebaseAuth.getUid();
                reportUserName = MoldeApplication.firebaseAuth.getCurrentUser().getDisplayName();
                List<MultipartBody.Part> imageMultiParts = new ArrayList<>();
                if (imageSparseArray != null) {
                    if (imageSparseArray.size() > 0) {
                        for (int i = 1; i <= imageSparseArray.size(); i++) {
                            //reportImageFileList.add(new File(imageSparseArray.get(i).getPath()));
                            imageMultiParts.add(prepareFilePart("reportImageList", imageSparseArray.get(i)));
                        }
                    }
                } else {
                    Snackbar.make(findViewById(R.id.report_layout), "이미지는 무조건 하나 이상 업로드 해야합니다!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                reportContent = report_content.getText().toString();
                reportDetailAddress = detail_address.getText().toString();
                reportEmail = reply_email_input.getText().toString() + "@" + reply_email_select.getSelectedItem().toString();
                reportDate = new Date();
                MoldeReportEntity moldeReportEntity = new MoldeReportEntity(reportUserId, reportUserName, 0, reportContent, reportAddress, reportDetailAddress, reportEmail, reportLat, reportLng, reportDate);
                Log.e("entity json Data", new Gson().toJson(moldeReportEntity));
                Call<ResponseBody> moldeReportEntityCall = moldeReportRestService.sendReportData(reportUserId, reportUserName, 0, reportContent, reportAddress, reportDetailAddress, reportEmail, reportLat, reportLng, reportDate, imageMultiParts);
                moldeReportEntityCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "데이터 전송 성공", Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("로그 자 보자", response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            try {
                                Log.e("로그 자 보자", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "데이터 전송 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
