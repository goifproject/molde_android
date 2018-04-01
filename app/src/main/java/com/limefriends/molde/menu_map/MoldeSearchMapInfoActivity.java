package com.limefriends.molde.menu_map;

import com.limefriends.molde.R;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeSearchMapInfoActivity extends AppCompatActivity implements MoldeMapInfoRecyclerViewAdapterCallback {
    @BindView(R.id.loc_map_info_search_bar)
    LinearLayout loc_map_info_search_bar;
    @BindView(R.id.loc_map_info_search_input)
    EditText loc_map_info_search_input;
    @BindView(R.id.loc_map_info_search_button)
    ImageButton loc_map_info_search_button;
    @BindView(R.id.loc_map_info_list)
    RecyclerView loc_map_info_list;

    private MoldeMapInfoRecyclerViewAdapter loc_map_info_list_adapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable workRunnable;
    private final long DELAY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molde_search_map_info);
        ButterKnife.bind(this);
        loc_map_info_search_bar.setElevation(12);
        searchFieldInit();
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void applyMapInfo(int position) {

    }

    private void searchFieldInit() {
        loc_map_info_search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        loc_map_info_list_adapter = new MoldeMapInfoRecyclerViewAdapter();
        loc_map_info_list.setLayoutManager(layoutManager);
        loc_map_info_list.setAdapter(loc_map_info_list_adapter);
        loc_map_info_list_adapter.setCallback(this);
    }

    private void searchMapInfoList() {
        final String keyword = loc_map_info_search_input.getText().toString();
        handler.removeCallbacks(workRunnable);
        workRunnable = new Runnable() {
            @Override
            public void run() {
                loc_map_info_list_adapter.filter(keyword);
            }
        };
        handler.postDelayed(workRunnable, DELAY);
    }
}
