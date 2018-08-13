package com.limefriends.molde.ui.menu_map.favorite;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.ui.menu_map.callback_method.MyFavoriteAdapterCallBack;
import com.limefriends.molde.ui.menu_map.entity.MoldeMyFavoriteEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMyFavoriteActivity extends AppCompatActivity implements MyFavoriteAdapterCallBack {
    private static final int FAVORITE_SELECT_RESULT_CODE = 12;
    private static final int FAVORITE_NOSELECT_RESULT_CODE = 13;

    @BindView(R.id.my_favorite_list_view)
    RecyclerView my_favorite_list_view;

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

        ArrayList<MoldeMyFavoriteEntity> moldeMyFavoriteList = new ArrayList<MoldeMyFavoriteEntity>();
        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("37.55267706", "126.92438746", "홍익대학교", "서울시 마포구 와우산로 94", true));
        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("37.54781897", "127.06120846", "아인빌딩", "서울특별시 성동구 광나루로 286", true));
        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("37.55431537", "126.90266717", "망원파출소", "서울특별시 마포구 망원로2길 63", true));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        my_favorite_list_view.setLayoutManager(layoutManager);
        MoldeMyFavoriteAdapter myFavoriteAdapter = new MoldeMyFavoriteAdapter(getApplicationContext(), moldeMyFavoriteList);
        my_favorite_list_view.setAdapter(myFavoriteAdapter);
        myFavoriteAdapter.setMoldeMyFavoriteAdapterCallBack(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void applyMyFavoriteMapInfo(MoldeMyFavoriteEntity entity) {
        Intent intent = new Intent(getApplicationContext(), MoldeMainActivity.class);
        intent.putExtra("mapFavoriteInfo", entity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, FAVORITE_SELECT_RESULT_CODE);
    }
}
