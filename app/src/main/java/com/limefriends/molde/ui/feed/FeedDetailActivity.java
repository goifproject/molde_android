package com.limefriends.molde.ui.feed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.Constant;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedImageResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.pm10.library.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.limefriends.molde.comm.Constant.ReportState.*;

public class FeedDetailActivity extends AppCompatActivity {
    List<String> reportImageLinkList;

    @BindView(R.id.mypage_detail_report_image_indicator)
    CircleIndicator mypage_detail_report_image_indicator;
    @BindView(R.id.mypage_detail_report_image_pager)
    ViewPager mypage_detail_report_image_pager;
    @BindView(R.id.mypage_detail_report_location_content)
    TextView mypage_detail_report_location_content;
    @BindView(R.id.mypage_detail_report_content)
    TextView mypage_detail_report_content;
    @BindView(R.id.report_detail_result_text)
    TextView report_detail_result_text;

    @BindView(R.id.siren_bad_status)
    ImageView siren_bad_status;
    @BindView(R.id.siren_receiving_status)
    ImageView siren_receiving_status;
    @BindView(R.id.siren_found_status)
    ImageView siren_found_status;
    @BindView(R.id.siren_clean_status)
    ImageView siren_clean_status;
    @BindView(R.id.mypage_detail_report_cancel_button)
    Button mypage_detail_report_cancel_button;

    @BindView(R.id.report_detail_normal)
    RelativeLayout report_detail_normal;

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

    @BindView(R.id.mypage_detail_report_image_indicator_container)
    RelativeLayout mypage_detail_report_image_indicator_container;


    FeedImageAdapter feedImageAdapter;

    int reportId;
    int position;
    String activity;
    long authority;
    boolean isMyFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_my_report_detail);

        // TODO 권한 인증해서 어떤 화면을 보여줄지 결정한다

        activity = getIntent().getStringExtra("activity");

        reportId = getIntent().getIntExtra("feedId", 0);

        position = getIntent().getIntExtra("position", 0);

        authority = PreferenceUtil.getLong(this, "authority");

        setupViews();

        setupListener();

        setImagePager();

        loadReport(reportId);
    }

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("신고 상세 내역");

        if (authority == Constant.Authority.ADMIN && activity != null && activity.equals("MyFeed")) {
            report_detail_normal.setVisibility(View.GONE);
            report_detail_admin.setVisibility(View.VISIBLE);
            mypage_detail_report_cancel_button.setText(getText(R.string.report_deny));
            isMyFeed = true;
        }
    }

    private void setupListener() {

        mypage_detail_report_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mypage_detail_report_cancel_button.getText().equals(getText(R.string.report_cancel))) {
                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setTitle("신고 취소")
                            .setMessage("신고를 취소하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReport(reportId);
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                } else if (mypage_detail_report_cancel_button.getText().equals(getText(R.string.report_deny))) {
                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setTitle("신고 취소")
                            .setMessage("신고를 취소하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    refuseReport(reportId, DENIED);
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });

        report_confirm_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1개만 체크되어 있어야 함
                boolean accepted = progress_checkbox_admin_accepted.isChecked();
                boolean found = progress_checkbox_admin_found.isChecked();
                boolean clean = progress_checkbox_admin_clean.isChecked();

                if (accepted) {
                    updateReport(reportId, ACCEPTED);
                } else if (found) {
                    updateReport(reportId, FOUND);
                } else if (clean) {
                    updateReport(reportId, CLEAN);
                } else {
                    Toast.makeText(FeedDetailActivity.this, "진행중인 신고입니다", Toast.LENGTH_SHORT).show();
                }

                // 체크된 거로 이미지 바꿔주자

                // 체크된 거로 데이터 바꿔주자

            }
        });

        progress_checkbox_admin_accepted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progress_checkbox_admin_found.setChecked(false);
                    progress_checkbox_admin_clean.setChecked(false);
                }
            }
        });

        progress_checkbox_admin_found.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progress_checkbox_admin_accepted.setChecked(false);
                    progress_checkbox_admin_clean.setChecked(false);
                }
            }
        });

        progress_checkbox_admin_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progress_checkbox_admin_accepted.setChecked(false);
                    progress_checkbox_admin_found.setChecked(false);
                }
            }
        });

    }

    private void setImagePager() {
        reportImageLinkList = new ArrayList<>();
        feedImageAdapter = new FeedImageAdapter(getApplicationContext(), reportImageLinkList);
        mypage_detail_report_image_pager.setAdapter(feedImageAdapter);
        mypage_detail_report_image_indicator.setupWithViewPager(mypage_detail_report_image_pager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    public void loadReport(int reportId) {

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<FeedResponseInfoEntityList> call = feedService.getFeedById(reportId);

        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call, Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<FeedResponseInfoEntity> entityList = response.body().getData();
                    if (entityList != null && entityList.size() != 0) {
                        FeedEntity entity = fromSchemaToLocalEntity(response.body().getData()).get(0);
                        // 위치
                        mypage_detail_report_location_content.setText(entity.getRepAddr() + " " + entity.getRepDetailAddr());
                        // 내용
                        mypage_detail_report_content.setText(entity.getRepContents());
                        // 신고 상태
                        setSirenState(entity.getRepState());
                        // 신고 이미지
                        List<FeedImageResponseInfoEntity> imageList = entity.getRepImg();
                        List<String> imageUrls = new ArrayList<>();
                        for (int i = 0; i < imageList.size(); i++) {
                            Log.e("이미지", imageList.get(i).getFilepath());
                            imageUrls.add(imageList.get(i).getFilepath());
                        }
                        feedImageAdapter.setData(imageUrls);
                    }
                }
            }

            @Override
            public void onFailure(Call<FeedResponseInfoEntityList> call, Throwable t) {

            }
        });

    }

    public void deleteReport(int reportId) {

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<Result> call = feedService.deleteFeed("lkj", reportId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FeedDetailActivity.this, "신고를 취소했습니다.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);

                    finish();

                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    public void updateReport(int reportId, final int state) {

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<Result> call = feedService.updateFeed(reportId, state);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FeedDetailActivity.this, "신고 상태가 변경되었습니다.", Toast.LENGTH_LONG).show();
                    // 사실 현재 페이지에서는 할 필요 없음
                    // setSirenState(state);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    private void setSirenState(int state) {
        setSirenInvisible();
        String resultText = "";
        switch (state) {
            case RECEIVING:
                resultText = getText(R.string.reportReceivingStatus).toString();
                siren_receiving_status.setVisibility(View.VISIBLE);
                // 접수중일 때만 삭제 가능
                if (isMyFeed) mypage_detail_report_cancel_button.setVisibility(View.VISIBLE);
                break;
            case ACCEPTED:
                siren_receiving_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.reportAcceptedStatus).toString();
                progress_checkbox_admin_accepted.setChecked(true);
                break;
            case FOUND:
                siren_found_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.reportFoundStatus).toString();
                progress_checkbox_admin_accepted.setChecked(true);
                break;
            case CLEAN:
                siren_clean_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.reportCleanStatus).toString();
                progress_checkbox_admin_accepted.setChecked(true);
                break;
            case DENIED:
                resultText = getText(R.string.reportDeniedStatus).toString();
                break;
        }
        report_detail_result_text.setText(resultText);
    }

    public void refuseReport(int reportId, final int state) {
        // 체크된 거로 데이터 바꿔주자
        updateReport(reportId, state);
    }

    private void setSirenInvisible() {
        siren_bad_status.setVisibility(View.INVISIBLE);
        siren_receiving_status.setVisibility(View.INVISIBLE);
        siren_clean_status.setVisibility(View.INVISIBLE);
        siren_found_status.setVisibility(View.INVISIBLE);
    }

    private List<FeedEntity> fromSchemaToLocalEntity(List<FeedResponseInfoEntity> entities) {
        List<FeedEntity> data = new ArrayList<>();
        for (FeedResponseInfoEntity entity : entities) {
            data.add(new FeedEntity(
                    entity.getRepId(),
                    entity.getUserName(),
                    entity.getUserEmail(),
                    entity.getUserId(),
                    entity.getRepContents(),
                    entity.getRepLat(),
                    entity.getRepLon(),
                    entity.getRepAddr(),
                    entity.getRepDetailAddr(),
                    entity.getRepDate(),
                    entity.getRepImg(),
                    entity.getRepState()
            ));
        }
        return data;
    }

//    private void loadData(int cardNewsId, String userId) {
//        MoldeRestfulService.Scrap scrapService
//                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Scrap.class);
//
//        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
//        Call<Result> call = scrapService.deleteMyScrap(userId, cardNewsId);
//
//        call.enqueue(new Callback<Result>() {
//            @Override
//            public void onResponse(Call<Result> call, Response<Result> response) {
//                if (response.body().getResult() == 1) {
//                    Toast.makeText(MagazineCardnewsDetailActivity.this, "삭제완료", Toast.LENGTH_LONG).show();
//                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_false);
//                    isScrap = false;
//                } else {
//                    cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_true);
//                    isScrap = true;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Result> call, Throwable t) {
//
//            }
//        });
//    }

}
