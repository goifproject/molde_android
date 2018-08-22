package com.limefriends.molde.ui.map.search;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.manager.cache_manager.Cache;
import com.limefriends.molde.entity.map.SearchMapHistoryEntity;
import com.limefriends.molde.entity.map.SearchMapInfoEntity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchMapInfoActivity extends AppCompatActivity implements
        MoldeMapInfoAdapter.MapInfoAdapterCallback,
        MoldeMapHistroyAdapter.MapHistoryAdapterCallback {

    @BindView(R.id.loc_map_info_search_bar)
    LinearLayout loc_map_info_search_bar;
    @BindView(R.id.loc_map_info_search_input)
    EditText loc_map_info_search_input;
    @BindView(R.id.loc_map_info_search_button)
    ImageButton loc_map_info_search_button;
    @BindView(R.id.history_map_info_list)
    RecyclerView history_map_info_list;
    @BindView(R.id.loc_map_info_list)
    RecyclerView loc_map_info_list;
    @BindView(R.id.delete_all_button)
    ImageButton delete_all_button;
    @BindView(R.id.delete_search_history_button)
    Button delete_search_history_button;

    // private FileCache fileCache;
    public static boolean isCheckBackPressed = false;
    private static final long DELAY = 100;
    private MoldeMapInfoAdapter loc_map_info_list_adapter;
    private MoldeMapHistroyAdapter history_map_info_list_adapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable workRunnable;
    private Cache cache;
    private String keywordHistoryStr = "";
    private String cmd = "";
    private final static String cmdReport = "Report";
//    private String reportContent;
//    private String reportDetailAddress;
//    private String reportEmailName;
//    private String reportEmailDomainPosition;
//    private String reportLat;
//    private String reportLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();

        initDataFromMapFragment();

        initDataFromReportActivity();

        setMapInfoRecyclerView();

        setListeners();

        historyFieldInit();
    }

    private void setupViews() {

        setContentView(R.layout.map_activity_molde_search_info);

        ButterKnife.bind(this);

        setupWindowAnimations();

        loc_map_info_search_bar.setElevation(12);
    }

    private void setupWindowAnimations() {
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
    }

    private void initDataFromReportActivity() {
        String activityName = getIntent().getStringExtra("activity");
        if (activityName != null && activityName.equals(cmdReport)) {
            cmd = cmdReport;
//            reportContent = getIntent().getStringExtra("reportContent");
//            reportDetailAddress = getIntent().getStringExtra("reportDetailAddress");
//            reportEmailName = getIntent().getStringExtra("reportEmailName");
//            reportEmailDomainPosition = getIntent().getStringExtra("reportEmailDomainPosition");
//            reportLat = getIntent().getStringExtra("reportLat");
//            reportLng = getIntent().getStringExtra("reportLng");
        }
    }

    private void initDataFromMapFragment() {
        String name = getIntent().getStringExtra("searchName");
        if (name != null && name.equals("검색하기")) {
            loc_map_info_search_input.setText("");
            delete_all_button.setVisibility(View.INVISIBLE);
        } else {
            loc_map_info_search_input.setText(name);
            delete_all_button.setVisibility(View.VISIBLE);
        }
    }

    private void setMapInfoRecyclerView() {
        //검색 정보 띄우기
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        loc_map_info_list_adapter = new MoldeMapInfoAdapter(getApplicationContext(), cmd);
        loc_map_info_list.setLayoutManager(layoutManager);
        loc_map_info_list.setAdapter(loc_map_info_list_adapter);
        loc_map_info_list_adapter.setMapInfoAdapterCallback(this);
    }

    private void setListeners() {

        loc_map_info_search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loc_map_info_search_button.performClick();
                    return true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loc_map_info_search_button.performClick();
                    return true;
                }
                return false;
            }
        });

        loc_map_info_search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    delete_all_button.setVisibility(View.VISIBLE);
                } else {
                    delete_all_button.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*final String keyword = s.toString();
                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        loc_map_info_list_adapter.filter(keyword);
                    }
                };
                handler.postDelayed(workRunnable, DELAY);*/
            }
        });

        loc_map_info_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMapInfoList();
                history_map_info_list.setVisibility(View.INVISIBLE);
            }
        });

        delete_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loc_map_info_search_input.setText("");
                delete_all_button.setVisibility(View.INVISIBLE);
            }
        });

        delete_search_history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cache cache = new Cache(getApplicationContext());
                try {
                    if (cache.delete()) {
                        showToast("성공적으로 삭제 완료");
                    } else {
                        showToast("삭제 실패");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    /**
     * 입력된 키워드로 T-Map 장소 검색
     */
    private void searchMapInfoList() {
        final String keyword = loc_map_info_search_input.getText().toString();
        handler.removeCallbacks(workRunnable);
        workRunnable = new Runnable() {
            @Override
            public void run() {
                loc_map_info_list_adapter.filter(keyword, keywordHistoryStr);
            }
        };
//        handler.postDelayed(workRunnable, DELAY);
        handler.post(workRunnable);
    }

    /**
     * 기존에 검색한 키워드를 파일에서 읽어옴
     */
    private void historyFieldInit() {
        cache = new Cache(getApplicationContext());
        try {
            if (cache.read().equals("")) {
                cache.write("");
            }
            keywordHistoryStr = cache.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (keywordHistoryStr.equals("")) {
            return;
        } else {
            keywordHistoryStr = keywordHistoryStr.trim();
            if (keywordHistoryStr.charAt(0) == ',') {
                keywordHistoryStr = keywordHistoryStr.substring(0, 1);
                try {
                    cache.write(keywordHistoryStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } else {
                makeHistoryList(keywordHistoryStr);
            }
        }
        Log.e("history", keywordHistoryStr + ".");
    }

    private void makeHistoryList(String keywordHistoryStr) {
        String[] historyArray = keywordHistoryStr.split(",");
        List<SearchMapHistoryEntity> historyEntityList = new ArrayList<>();
        for (String aHistoryArray : historyArray) {
            String[] historyElem = aHistoryArray.split("\\|");
            SearchMapHistoryEntity historyEntity;
            double mapLat = Double.parseDouble(historyElem[0]);
            double mapLng = Double.parseDouble(historyElem[1]);
            String name = historyElem[2];
            String mainAddr = historyElem[3];
            String bizName = historyElem[4];
            String telNo = historyElem[5];
            historyEntity = new SearchMapHistoryEntity(mapLat, mapLng, name, mainAddr, bizName, telNo);
            historyEntityList.add(historyEntity);
        }
        //검색 기록 띄우기
        setMapHistoryRecyclerView(historyEntityList);
    }

    private void setMapHistoryRecyclerView(List<SearchMapHistoryEntity> historyEntityList) {
        Collections.reverse(historyEntityList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        history_map_info_list_adapter = new MoldeMapHistroyAdapter(historyEntityList, getApplicationContext(), cmd);
        history_map_info_list.setLayoutManager(layoutManager);
        history_map_info_list.setAdapter(history_map_info_list_adapter);
        history_map_info_list_adapter.setMapHistoryAdapterCallback(this);
    }

    /**
     * 새로 입력된 키워드 캐시에 저장
     */
    @Override
    public void writeSearchMapHistory(SearchMapInfoEntity entity, String historyStr) throws IOException {
        String history = entity.getMapLat() + "|" + entity.getMapLng() + "|" + entity.getName() + "|" + entity.getMainAddress() + "|" + entity.getBizName() + "|" + entity.getTelNo();
        keywordHistoryStr = historyStr;
        keywordHistoryStr += history + ",";
        cache.write(keywordHistoryStr);
    }

    /**
     * T-Map 에서 검색한 리스트 중 하나를 선택했을 경우
     */
    @Override
    public void applySearchMapInfo(SearchMapInfoEntity searchEntity, String cmd) {
        Log.e("호출확인", "applySearchMapInfo1");
        Intent intent = new Intent();
        if (cmd.equals(cmdReport)) {
            Log.e("호출확인", "applySearchMapInfo2");
            // intent.setClass(getApplicationContext(), MoldeReportActivity.class);
            Log.e("호출확인", searchEntity.getMainAddress()+":"+searchEntity.getMapLat()+":"+searchEntity.getMapLng());

            intent.putExtra("reportAddress", searchEntity.getMainAddress());
            intent.putExtra("reportLat", searchEntity.getMapLat());
            intent.putExtra("reportLng", searchEntity.getMapLng());
        } else {
            //intent.setClass(getApplicationContext(), MoldeMainActivity.class);
            intent.putExtra("mapSearchInfo", searchEntity);
        }
        setResult(RESULT_OK, intent);
        //startActivity(intent);
        finish();
    }

    /**
     * 기존에 검색한 캐시 리스트 중 하나를 선택했을 경우
     */
    @Override
    public void applyHistoryMapInfo(SearchMapHistoryEntity historyEntity, String cmd) {
        Log.e("호출확인", "applyHistoryMapInfo");
        Intent intent = new Intent();
        if (cmd.equals(cmdReport)) {
            Log.e("호출확인", historyEntity.getMainAddress()+":"+historyEntity.getMapLat()+":"+historyEntity.getMapLng());
            // intent.setClass(getApplicationContext(), MoldeReportActivity.class);
            intent.putExtra("reportAddress", historyEntity.getMainAddress());
            intent.putExtra("reportLat", historyEntity.getMapLat());
            intent.putExtra("reportLng", historyEntity.getMapLng());
        } else {
            // intent.setClass(getApplicationContext(), MoldeMainActivity.class);
            intent.putExtra("mapHistoryInfo", historyEntity);
        }
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isCheckBackPressed = true;
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }



}
