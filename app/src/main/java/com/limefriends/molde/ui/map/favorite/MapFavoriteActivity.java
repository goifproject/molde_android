package com.limefriends.molde.ui.map.favorite;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.comm.custom.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.favorite.FavoriteEntity;
import com.limefriends.molde.entity.favorite.FavoriteResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFavoriteActivity extends AppCompatActivity implements
        MapFavoriteAdapter.MyFavoriteAdapterCallBack {

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;

    @BindView(R.id.my_favorite_list_view)
    AddOnScrollRecyclerView my_favorite_list_view;

    private MapFavoriteAdapter myFavoriteAdapter;
    private MoldeRestfulService.Favorite favoriteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_favorite);

        setupViews();

        setupFavoriteList();

        loadFavorite(PER_PAGE, currentPage);
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(getText(R.string.my_favorite));
    }

    private void setupFavoriteList() {
        myFavoriteAdapter = new MapFavoriteAdapter(getApplicationContext());
        myFavoriteAdapter.setMoldeMyFavoriteAdapterCallBack(this);
        my_favorite_list_view.setAdapter(myFavoriteAdapter);
        my_favorite_list_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()), false);
        my_favorite_list_view.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadFavorite(PER_PAGE, currentPage);
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

    //-----
    // Network
    //-----

    private MoldeRestfulService.Favorite getFavoriteService() {
        if (favoriteService == null) {
            favoriteService
                    = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Favorite.class);
        }
        return favoriteService;
    }

    private void loadFavorite(int perPage, int page) {

        if (!hasMoreToLoad) return;

        my_favorite_list_view.setIsLoading(true);

        Call<FavoriteResponseInfoEntityList> call
                = getFavoriteService().getMyFavorite("lkj", perPage, page);

        call.enqueue(new Callback<FavoriteResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FavoriteResponseInfoEntityList> call,
                                   Response<FavoriteResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    // 4. 호출 후 데이터 정리
                    List<FavoriteEntity> entities = FromSchemaToEntitiy.favorite(response.body().getData());
                    // 6. 데이터 추가
                    myFavoriteAdapter.setData(entities);
                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                    currentPage++;
                    // 8. 더 이상 데이터를 세팅중이 아님을 명시
                    my_favorite_list_view.setIsLoading(false);
                    if (entities.size() < PER_PAGE) {
                        setHasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponseInfoEntityList> call, Throwable t) {
                Log.e("즐겨찾기 오류", t.getMessage());
            }
        });

    }

    private void deleteFavorite(final int favId) {

        Call<Result> call = getFavoriteService().deleteFavorite("lkj", favId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MapFavoriteActivity.this, "즐겨찾기 삭제 성공", Toast.LENGTH_SHORT).show();
                        myFavoriteAdapter.notifyFavoriteRemoved();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });

    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void applyMyFavoriteMapInfo(FavoriteEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("mapFavoriteInfo", entity);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onUnSelected(int favId) {
        deleteFavorite(favId);
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }

}
