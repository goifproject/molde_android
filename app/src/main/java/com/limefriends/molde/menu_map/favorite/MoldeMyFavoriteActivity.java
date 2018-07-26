package com.limefriends.molde.menu_map.favorite;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.entity.MoldeMyFavoriteEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMyFavoriteActivity extends AppCompatActivity {
    @BindView(R.id.my_favorite_list_view)
    RecyclerView my_favorite_list_view;

    private MoldeMyFavoriteRecyclerAdapter myFavoriteAdapter;
    private ArrayList<MoldeMyFavoriteEntity> moldeMyFavoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_molde_my_favorite);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("즐겨찾기");

        moldeMyFavoriteList = new ArrayList<MoldeMyFavoriteEntity>();
        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("홍익대학교", "서울시 마포구 와우산로 94", true));
        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("아인빌딩", "서울특별시 성동구 광나루로 286", true));
        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("망원파출소", "서울특별시 마포구 망원로2길 63", true));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        my_favorite_list_view.setLayoutManager(layoutManager);
        myFavoriteAdapter = new MoldeMyFavoriteRecyclerAdapter(getApplicationContext(), moldeMyFavoriteList);
        my_favorite_list_view.setAdapter(myFavoriteAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
