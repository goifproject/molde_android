package com.limefriends.molde.ui.feed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
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

import static com.limefriends.molde.comm.Constant.Authority.*;
import static com.limefriends.molde.comm.Constant.Feed.*;
import static com.limefriends.molde.comm.Constant.ReportState.*;

// TODO "lkj" 변경할 것
public class FeedDetailActivity extends AppCompatActivity {

    // 이미지 페이저
    @BindView(R.id.mypage_detail_report_image_pager)
    ViewPager mypage_detail_report_image_pager;
    // 위치, 신고 내용
    @BindView(R.id.mypage_detail_report_location_content)
    TextView mypage_detail_report_location_content;
    @BindView(R.id.mypage_detail_report_content)
    TextView mypage_detail_report_content;
    // 페이지 인디케이더
    @BindView(R.id.mypage_detail_report_image_indicator_container)
    RelativeLayout mypage_detail_report_image_indicator_container;
    @BindView(R.id.mypage_detail_report_image_indicator)
    CircleIndicator mypage_detail_report_image_indicator;
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

    private FeedImageAdapter feedImageAdapter;
    private MoldeRestfulService.Feed feedService;

    private String activityName;
    private int reportId;
    private int position;
    private long authority;
    private boolean isMyFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_my_report_detail);

        prepare();

        setupViews();

        setupListener();

        setImagePager();

        loadReport(reportId);
    }

    //-----
    // View
    //-----

    private void prepare() {
        activityName = getIntent().getStringExtra(INTENT_KEY_ACTIVITY_NAME);

        reportId = getIntent().getIntExtra(INTENT_KEY_FEED_ID, 0);

        position = getIntent().getIntExtra(INTENT_KEY_POSITION, 0);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY);
    }

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
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
        mypage_detail_report_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mypage_detail_report_cancel_button.getText().equals(getText(R.string.cancel_report))) {
                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setTitle(getText(R.string.cancel_report))
                            .setMessage(getText(R.string.cancel_message))
                            .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReport(reportId);
                                }
                            })
                            .setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                } else if (mypage_detail_report_cancel_button.getText().equals(getText(R.string.deny_report))) {
                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setTitle(getText(R.string.cancel_report))
                            .setMessage(getText(R.string.cancel_message))
                            .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    refuseReport(reportId, DENIED);
                                }
                            })
                            .setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
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
        feedImageAdapter = new FeedImageAdapter(getApplicationContext());
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
                resultText = getText(R.string.report_status_receiving).toString();
                siren_receiving_status.setVisibility(View.VISIBLE);
                // 접수중일 때만 삭제 가능
                if (isMyFeed) mypage_detail_report_cancel_button.setVisibility(View.VISIBLE);
                break;
            case ACCEPTED:
                siren_receiving_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.report_status_accepted).toString();
                progress_checkbox_admin_accepted.setChecked(true);
                break;
            case FOUND:
                siren_found_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.report_status_found).toString();
                progress_checkbox_admin_accepted.setChecked(true);
                break;
            case CLEAN:
                siren_clean_status.setVisibility(View.VISIBLE);
                resultText = getText(R.string.report_status_clean).toString();
                progress_checkbox_admin_accepted.setChecked(true);
                break;
            case DENIED:
                resultText = getText(R.string.report_status_denied).toString();
                break;
        }
        report_detail_result_text.setText(resultText);
    }

    //-----
    // Network
    //-----

    private MoldeRestfulService.Feed getFeedService() {
        if (feedService == null) {
            feedService = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);
        }
        return feedService;
    }

    private void loadReport(int reportId) {
        Call<FeedResponseInfoEntityList> call = getFeedService().getFeedById(reportId);
        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call,
                                   Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<FeedResponseInfoEntity> entityList = response.body().getData();
                    if (entityList != null && entityList.size() != 0) {
                        FeedEntity entity = FromSchemaToEntitiy.feed(response.body().getData()).get(0);
                        // 위치
                        mypage_detail_report_location_content.setText(
                                String.format("%s %s", entity.getRepAddr(), entity.getRepDetailAddr()));
                        // 내용
                        mypage_detail_report_content.setText(entity.getRepContents());
                        // 신고 상태
                        setSirenState(entity.getRepState());
                        // 신고 이미지
                        List<FeedImageResponseInfoEntity> imageList = entity.getRepImg();
                        List<String> imageUrls = new ArrayList<>();
                        for (int i = 0; i < imageList.size(); i++) {
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
        Call<Result> call = getFeedService().deleteFeed("lkj", reportId);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FeedDetailActivity.this, "신고를 취소했습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_KEY_POSITION, position);
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
        Call<Result> call = getFeedService().updateFeed(reportId, state);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FeedDetailActivity.this, "신고 상태가 변경되었습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    public void refuseReport(int reportId, final int state) {
        // 체크된 거로 데이터 바꿔주자
        updateReport(reportId, state);
    }
}