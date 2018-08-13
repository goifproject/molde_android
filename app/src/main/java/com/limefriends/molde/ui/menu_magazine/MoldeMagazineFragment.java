package com.limefriends.molde.ui.menu_magazine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntityList;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.news.MoldeCardNewsEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntity;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.menu_magazine.report.MagazineReportLocationDetailActivity;
import com.limefriends.molde.ui.menu_magazine.report.MagazineReportMolcaDetailActivity;
import com.limefriends.molde.ui.menu_magazine.report.MagazineReportSpreadDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoldeMagazineFragment extends Fragment implements MoldeMainActivity.onKeyBackPressedListener {

    @BindView(R.id.cardnews_recyclerView)
    AddOnScrollRecyclerView cardnews_recyclerView;
    @BindView(R.id.manual_new_molca)
    LinearLayout manual_new_molca;
    @BindView(R.id.manual_by_location)
    LinearLayout manual_by_location;
    @BindView(R.id.manual_for_spreading)
    LinearLayout manual_for_spreading;

    private MagazineCardNewsAdapter magazineCardNewsAdapter;
    private MoldeRestfulService.CardNews newsService;

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;

    public static MoldeMagazineFragment newInstance() {
        MoldeMagazineFragment fragment = new MoldeMagazineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("호출확인", "magazine fragment");

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.magazine_fragment, container, false);
        ButterKnife.bind(this, rootView);

        manual_new_molca.setElevation(8);
        manual_for_spreading.setElevation(8);
        manual_by_location.setElevation(8);


        manual_new_molca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportMolcaDetailActivity.class);
                intent.putExtra("title", "최신 몰카 정보");
                startActivity(intent);
            }
        });

        manual_by_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportLocationDetailActivity.class);
                intent.putExtra("title", "장소별 대처법");
                startActivity(intent);
            }
        });

        manual_for_spreading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportSpreadDetailActivity.class);
                intent.putExtra("title", "몰카유포 대처");
                startActivity(intent);
            }
        });


//        List<CardNewsEntity> cardnewsDataList = new ArrayList<CardNewsEntity>();
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스1"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스2"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스3"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스4"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스5"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스6"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스7"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스8"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스9"));
//        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스10"));

        magazineCardNewsAdapter = new MagazineCardNewsAdapter(getContext());
        // 1. 어댑터
        cardnews_recyclerView.setAdapter(magazineCardNewsAdapter);
        // 2. 매니저
        cardnews_recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2), true);
//        cardnews_recyclerView.setOnLoadMoreListener(new AddOnScrollRecyclerView2.OnLoadMoreListener() {
//            @Override
//            public void loadMore() {
//
//            }
//        });
//        cardnews_recyclerView.setOnItemClickedListener(new AddOnScrollRecyclerView2.OnItemClickedListener() {
//            @Override
//            public void itemClicked(Object item) {
//
//            }
//        });

        // 3. loadMore
        cardnews_recyclerView.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadMagazine(PER_PAGE, currentPage);
            }
        });



//        cardnews_recyclerView.setOnItemClickedListener(new AddOnScrollRecyclerView.OnItemClickedListener() {
//            @Override
//            public void itemClicked(Object item) {
//
//            }
//        });

        loadMagazine(PER_PAGE, FIRST_PAGE);

        return rootView;
    }

    @Override
    public void onBackKey() {

    }

    private MoldeRestfulService.CardNews getFeedService() {
        if (newsService == null) {
            newsService = MoldeNetwork.getInstance().generateService(MoldeRestfulService.CardNews.class);
        }
        return newsService;
    }

    private void loadMagazine(int perPage, int page) {

        // Log.e("호출확인2", "magazine fragment");

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // 2. 불러온다면 프로그래스바를 띄움
        magazineCardNewsAdapter.setProgressMore(true);

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        cardnews_recyclerView.setIsLoading(true);

        // Log.e("호출확인3", "magazine fragment");

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<MoldeCardNewsResponseInfoEntityList> call = getFeedService().getCardNewsList(perPage, page);

        call.enqueue(new Callback<MoldeCardNewsResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeCardNewsResponseInfoEntityList> call, Response<MoldeCardNewsResponseInfoEntityList> response) {
                if (response.isSuccessful()) {

                    // Log.e("호출확인4", "magazine fragment");
                    // 4. 호출 후 데이터 정리
                    List<MoldeCardNewsEntity> entities = fromSchemaToLocalEntity(response.body().getData());
                    // 5. 데이터가 세팅되기 이전에 프로그래스 바 세팅
                    magazineCardNewsAdapter.setProgressMore(false);
                    // 6. 데이터 추가
                    magazineCardNewsAdapter.addData(entities);
                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                    currentPage++;
                    // 8. 더 이상 데이터를 세팅중이 아님을 명시
                    cardnews_recyclerView.setIsLoading(false);
                    // 9. 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다.
                    if (entities.size() < PER_PAGE) {
                        // Log.e("호출확인5", "magazine fragment");
                        setHasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoldeCardNewsResponseInfoEntityList> call, Throwable t) {
                Log.e("매거진 실패", t.getMessage());
            }
        });
    }

    private List<MoldeCardNewsEntity> fromSchemaToLocalEntity(List<MoldeCardNewsResponseInfoEntity> schemas) {
        List<MoldeCardNewsEntity> entities = new ArrayList<>();
        for (MoldeCardNewsResponseInfoEntity schema : schemas) {
            entities.add(new MoldeCardNewsEntity(
                    schema.getNewsId(),
                    schema.getPostId(),
                    schema.getDescription(),
                    schema.getDate(),
                    schema.getNewsImg()
            ));
        }
        return entities;
    }

    // 4. setHasMoreToLoad
    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    /**
     * TODO 생명주기 관리가 전혀 안 됨. 왜 이게 어쩔 때는 저장되었다가 어쩔 때는 원상태인지 파악이 안 됨
     */
    // 5. 생명주기
    @Override
    public void onPause() {
        super.onPause();
        setHasMoreToLoad(true);
        currentPage = 0;
    }
}
