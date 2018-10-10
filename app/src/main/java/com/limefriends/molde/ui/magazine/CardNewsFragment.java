package com.limefriends.molde.ui.magazine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.comm.custom.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.comm.utils.NetworkUtil;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntityList;
import com.limefriends.molde.R;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.magazine.info.HowToRespondActivity;
import com.limefriends.molde.ui.magazine.info.HowToDetectActivity;
import com.limefriends.molde.ui.magazine.info.RecentMolcaInfoActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardNewsFragment extends Fragment {

    @BindView(R.id.cardnews_recyclerView)
    AddOnScrollRecyclerView cardnews_recyclerView;
    @BindView(R.id.manual_new_molca)
    LinearLayout manual_new_molca;
    @BindView(R.id.manual_by_location)
    LinearLayout manual_by_location;
    @BindView(R.id.manual_for_spreading)
    LinearLayout manual_for_spreading;

    private CardNewsAdapter cardNewsAdapter;
    private MoldeRestfulService.CardNews newsService;

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isFirstOnCreateView = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_cardnews, container, false);

        setupViews(rootView);

        setupListeners();

        setupMagazineList();

        // 처음 한 번만 호출된다
        if (isFirstOnCreateView) loadMagazine(PER_PAGE, FIRST_PAGE);

        return rootView;
    }

    //-----
    // View
    //-----

    private void setupViews(View rootView) {
        ButterKnife.bind(this, rootView);
        manual_new_molca.setElevation(8);
        manual_for_spreading.setElevation(8);
        manual_by_location.setElevation(8);
    }

    private void setupListeners() {
        manual_new_molca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), RecentMolcaInfoActivity.class);
                intent.putExtra("title", "최신 몰카 정보");
                startActivity(intent);
            }
        });

        manual_by_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), HowToDetectActivity.class);
                intent.putExtra("title", "장소별 대처법");
                startActivity(intent);
            }
        });

        manual_for_spreading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), HowToRespondActivity.class);
                intent.putExtra("title", "몰카유포 대처");
                startActivity(intent);
            }
        });
    }

    private void setupMagazineList() {
        if (cardNewsAdapter == null) {
            cardNewsAdapter = new CardNewsAdapter(getContext());
        }
        // 1. 어댑터
        cardnews_recyclerView.setAdapter(cardNewsAdapter);
        // 2. 매니저
        cardnews_recyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2), true);
        // 3. loadMore
        cardnews_recyclerView.setOnLoadMoreListener(
                new OnLoadMoreListener() {
                    @Override
                    public void loadMore() {
                        loadMagazine(PER_PAGE, currentPage);
                    }
                });
    }

    //-----
    // Network
    //-----

    private MoldeRestfulService.CardNews getFeedService() {
        if (newsService == null) {
            newsService = MoldeNetwork.getInstance()
                    .generateService(MoldeRestfulService.CardNews.class);
        }
        return newsService;
    }

    private void loadMagazine(int perPage, int page) {

        if (!NetworkUtil.isConnected(getContext())) {
            Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        cardnews_recyclerView.setIsLoading(true);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<CardNewsResponseInfoEntityList> call = getFeedService()
                .getCardNewsList(perPage, page);

        call.enqueue(new Callback<CardNewsResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<CardNewsResponseInfoEntityList> call,
                                   Response<CardNewsResponseInfoEntityList> response) {
                if (response.isSuccessful()) {

                    List<CardNewsResponseInfoEntity> schemas = response.body().getData();

                    if (schemas == null || schemas.size() == 0) {
                        cardnews_recyclerView.setIsLoading(false);
                        hasMoreToLoad(false);
                        return;
                    }

                    // 4. 호출 후 데이터 정리
                    List<CardNewsEntity> entities = FromSchemaToEntitiy.cardNews(response.body().getData());
                    // 6. 데이터 추가
                    cardNewsAdapter.addData(entities);
                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                    currentPage++;
                    // 8. 더 이상 데이터를 세팅중이 아님을 명시
                    cardnews_recyclerView.setIsLoading(false);
                    // 9. 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다.
                    if (schemas.size() < PER_PAGE) {
                        hasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<CardNewsResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }


    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstOnCreateView = false;
    }


}
