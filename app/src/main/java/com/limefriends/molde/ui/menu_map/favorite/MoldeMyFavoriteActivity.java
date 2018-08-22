package com.limefriends.molde.ui.menu_map.favorite;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.entity.favorite.MoldeFavoriteEntity;
import com.limefriends.molde.entity.favorite.MoldeFavoriteResponseInfoEntity;
import com.limefriends.molde.entity.favorite.MoldeFavoriteResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoldeMyFavoriteActivity extends AppCompatActivity implements
        MoldeMyFavoriteAdapter.MyFavoriteAdapterCallBack {

    private static final int FAVORITE_SELECT_RESULT_CODE = 12;
    private static final int FAVORITE_NOSELECT_RESULT_CODE = 13;

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;

    @BindView(R.id.my_favorite_list_view)
    AddOnScrollRecyclerView my_favorite_list_view;

    private MoldeMyFavoriteAdapter myFavoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_molde_my_favorite);
        ButterKnife.bind(this);

        setupViews();

//        ArrayList<MoldeMyFavoriteEntity> moldeMyFavoriteList = new ArrayList<MoldeMyFavoriteEntity>();
//        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("37.55267706", "126.92438746", "홍익대학교", "서울시 마포구 와우산로 94", true));
//        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("37.54781897", "127.06120846", "아인빌딩", "서울특별시 성동구 광나루로 286", true));
//        moldeMyFavoriteList.add(new MoldeMyFavoriteEntity("37.55431537", "126.90266717", "망원파출소", "서울특별시 마포구 망원로2길 63", true));

//        ArrayList<MoldeFavoriteEntity> moldeFavoriteEntities = new ArrayList<>();
//        moldeFavoriteEntities.add(new MoldeFavoriteEntity("lkj", "홍익대학교", 37.55267706, 126.92438746));
//        moldeFavoriteEntities.add(new MoldeFavoriteEntity("lkj", "홍익대학교", 37.55267706, 126.92438746));
//        moldeFavoriteEntities.add(new MoldeFavoriteEntity("lkj", "홍익대학교", 37.55267706, 126.92438746));

        myFavoriteAdapter = new MoldeMyFavoriteAdapter(getApplicationContext());

        myFavoriteAdapter.setMoldeMyFavoriteAdapterCallBack(this);

        my_favorite_list_view.setAdapter(myFavoriteAdapter);

        my_favorite_list_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()), false);

        my_favorite_list_view.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadFavorite(PER_PAGE, currentPage);
            }
        });

        loadFavorite(PER_PAGE, FIRST_PAGE);
    }

    private void setupViews() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("즐겨찾기");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void loadFavorite(int perPage, int page) {

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // 2. 불러온다면 프로그래스바를 띄움
        // reportAdapter.setProgressMore(true);

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        my_favorite_list_view.setIsLoading(true);

        MoldeRestfulService.Favorite favoriteService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Favorite.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<MoldeFavoriteResponseInfoEntityList> call = favoriteService.getMyFavorite("lkj", perPage, page);

        call.enqueue(new Callback<MoldeFavoriteResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeFavoriteResponseInfoEntityList> call, Response<MoldeFavoriteResponseInfoEntityList> response) {
                if (response.isSuccessful()) {

                    // 4. 호출 후 데이터 정리
                    List<MoldeFavoriteEntity> entities = fromSchemaToLocalEntity(response.body().getData());
                    // 6. 데이터 추가
                    myFavoriteAdapter.setData(entities);
                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                    currentPage++;
                    // 8. 더 이상 데이터를 세팅중이 아님을 명시
                    my_favorite_list_view.setIsLoading(false);
                    if (entities.size() < PER_PAGE) {
                        // Log.e("호출확인5", "magazine fragment");
                        setHasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoldeFavoriteResponseInfoEntityList> call, Throwable t) {
                Log.e("즐겨찾기 오류", t.getMessage());
            }
        });

    }

    private List<MoldeFavoriteEntity> fromSchemaToLocalEntity(List<MoldeFavoriteResponseInfoEntity> schemas) {
        List<MoldeFavoriteEntity> entities = new ArrayList<>();
        for (MoldeFavoriteResponseInfoEntity schema : schemas) {
            entities.add(new MoldeFavoriteEntity(
                    schema.getFavId(),
                    schema.getUserId(),
                    schema.getFavName(),
                    schema.getFavAddr(),
                    schema.getFavLat(),
                    schema.getFavLon(),
                    true
            ));
        }
        return entities;
    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    private void deleteFavorite(final int favId) {

        MoldeRestfulService.Favorite favoriteService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Favorite.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<Result> call = favoriteService.deleteFavorite("lkj", favId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MoldeMyFavoriteActivity.this, "즐겨찾기 삭제 성공", Toast.LENGTH_SHORT).show();
                        Log.e("로그 자 보자", response.body().getResult() + "");
                        // TODO 여기서 finish() 잠시 두고 3-5대 열어놓고 연속으로 누르면서 부하를 얼만큼 버티는지 알아보자
                        myFavoriteAdapter.notifyFavoriteRemoved();
                    } else {
                        try {
                            Log.e("로그 자 보자", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });

    }

        /**
         * TODO 생명주기 관리가 전혀 안 됨. 왜 이게 어쩔 때는 저장되었다가 어쩔 때는 원상태인지 파악이 안 됨
         */
    // 5. 생명주기
//    @Override
//    public void onPause() {
//        super.onPause();
//        setHasMoreToLoad(true);
//        currentPage = 0;
//    }
        @Override
        public void onDestroy() {
            super.onDestroy();
            setHasMoreToLoad(true);
            currentPage = 0;
        }


    @Override
    public void applyMyFavoriteMapInfo(MoldeFavoriteEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("mapFavoriteInfo", entity);
        setResult(RESULT_OK, intent);
        finish();

//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivityForResult(intent, FAVORITE_SELECT_RESULT_CODE);
    }

    @Override
    public void onUnSelected(int favId) {
        deleteFavorite(favId);
    }

}
