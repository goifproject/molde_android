package com.limefriends.molde.ui.menu_feed;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.feed.MoldeFeedEntity;
import com.limefriends.molde.entity.feed.MoldeFeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.MoldeFeedResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.menu_map.MapFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoldeFeedFragment extends Fragment implements
        MoldeMainActivity.onKeyBackPressedListener,
        MoldeFeedRecyclerAdapter.OnClickFeedItemListener {

    @BindView(R.id.feed_sort_toggle)
    ToggleButton feed_sort_toggle;
    @BindView(R.id.feed_update_date)
    TextView feed_update_date;
    @BindView(R.id.feed_list)
    AddOnScrollRecyclerView feed_list;

    private MoldeRestfulService.Feed feedService;
    private MoldeFeedRecyclerAdapter feedAdapter;
    // private List<MoldeFeedEntity> reportFeedList;
    private List<MoldeFeedResponseInfoEntity> feedResponseInfoList;
    private static SparseArrayCompat feedFragmentSparseArrayCompat;

    private static final String FEED_BY_DISTANCE = "거리순";
    private static final String FEED_BY_LAST = "최신순";
    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private String feedStatus;
    private boolean hasMoreToLoad = true;

    public static MoldeFeedFragment newInstance() {
        feedFragmentSparseArrayCompat = new SparseArrayCompat();
        return new MoldeFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        // reportFeedList = new ArrayList<>();

        feedAdapter = new MoldeFeedRecyclerAdapter(getContext(), this);

        // 1. 어댑터
        feed_list.setAdapter(feedAdapter);

        // 2. 레이아웃 매니저
        feed_list.setLayoutManager(new LinearLayoutManager(getContext()), false);

        // 3. loadMore
        feed_list.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadData(feedStatus, PER_PAGE, currentPage);
            }
        });

//        feed_list.setOnItemClickedListener(new AddOnScrollRecyclerView.OnItemClickedListener() {
//            @Override
//            public void itemClicked(Object item) {
//
//            }
//        });



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (feedFragmentSparseArrayCompat.get(R.string.feedStatus) != null) {
            // reportFeedList.clear();
            feedStatus = (String) feedFragmentSparseArrayCompat.get(R.string.feedStatus);
            if (feedStatus.equals(FEED_BY_DISTANCE)) {
                feed_sort_toggle.setChecked(false);
                feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_off));
                loadData(FEED_BY_DISTANCE, PER_PAGE, FIRST_PAGE);
            } else if (feedStatus.equals(FEED_BY_LAST)) {
                feed_sort_toggle.setChecked(true);
                feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_on));
                loadData(FEED_BY_LAST, PER_PAGE, FIRST_PAGE);
            }
            return;
        }
        feedStatus = FEED_BY_DISTANCE;
        loadData(FEED_BY_DISTANCE, PER_PAGE, FIRST_PAGE);
    }

    private void loadData(String cmd, int perPage, int page) {

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // reportFeedList.clear();

        // 2. 불러온다면 프로그래스바를 띄움
        feedAdapter.setProgressMore(true);

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        feed_list.setIsLoading(true);

        Log.e("현재 페이지", currentPage+"");

        if (cmd.equals(FEED_BY_DISTANCE)) {
            if (MoldeApplication.myLocation != null) {
                fetchByLocation(perPage, page);
            } else {
                fetchByDefaultLocation(perPage, page);
            }
        } else if (cmd.equals(FEED_BY_LAST)) {
            fetchByLast(perPage, page);
        }
    }

    private void fetchByPageTemp(final int perPage, int page) {

        Log.e("모아 로딩", "1");

        Call<MoldeFeedResponseInfoEntityList> call = getFeedService().getPagedFeed(perPage, page);
        call.enqueue(new Callback<MoldeFeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeFeedResponseInfoEntityList> call, Response<MoldeFeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {


//                    feedResponseInfoList = response.body().getData();
//                    int size = feedResponseInfoList.size();
//
//
//                    for (int i = 0; i < size; i++) {
//                        addFeedList(i);
//                    }
                    // 4. 호출 후 데이터 정리
                    List<MoldeFeedEntity> entities = fromSchemaToLocalEntity(response.body().getData());
                    // 5. 데이터가 세팅되기 이전에 프로그래스 바 세팅
                    feedAdapter.setProgressMore(false);
                    // 6. 데이터 추가
                    feedAdapter.addAll(entities);
                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                    currentPage++;
                    // Log.e("사이즈", size+"");
                    // Log.e("페이지", perPage+"");
                    // feedAdapter.setMoreLoading(false);
                    // 8. 더 이상 데이터를 세팅중이 아님을 명시
                    feed_list.setIsLoading(false);
                    // Log.e("모아 로딩", "2");
                    // 9. 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다.
                    if (entities.size() < PER_PAGE) {
                        setHasMoreToLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoldeFeedResponseInfoEntityList> call, Throwable t) {
                Log.e("피드 에러", t.getMessage());
            }
        });
    }

    private void fetchByLocation(int perPage, int page) {
        fetchByPageTemp(perPage, page);
    }

    private void fetchByDefaultLocation(int perPage, int page) {
        fetchByPageTemp(perPage, page);
    }

    private void fetchByLast(int perPage, int page) {
        fetchByPageTemp(perPage, page);
    }



    private List<MoldeFeedEntity> fromSchemaToLocalEntity(List<MoldeFeedResponseInfoEntity> entities) {
        List<MoldeFeedEntity> data = new ArrayList<>();
        for (MoldeFeedResponseInfoEntity entity : entities) {
            data.add(new MoldeFeedEntity(
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

//    public void addFeedList(int i) {
//        MoldeFeedResponseInfoEntity entity = feedResponseInfoList.get(i);
//        reportFeedList.add(new MoldeFeedEntity(
//                        entity.getRepId(),
//                        entity.getUserName(),
//                        entity.getUserEmail(),
//                        entity.getUserId(),
//                        entity.getRepContents(),
//                        entity.getRepLat(),
//                        entity.getRepLon(),
//                        entity.getRepAddr(),
//                        entity.getRepDetailAddr(),
//                        entity.getRepDate(),
//                        entity.getRepImg(),
//                        entity.getRepState()));
//    }

    private MoldeRestfulService.Feed getFeedService() {
        if (feedService == null) {
            feedService = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);
        }
        return feedService;
    }

//    @Override
//    public void onLoadMore() {
//
//        Log.e("더 가져오기", "1");
//
//        if (!hasMoreToLoad) return;
//
//        Log.e("더 가져오기", "2");
//
//        // ?
//        // feedAdapter.setProgressMore(true);
//
//        // reportFeedList.clear();
//        // feedAdapter.setProgressMore(false);
//
//        // 시간순
////        if (!feed_sort_toggle.isChecked()) {
////            fetchByLast(PER_PAGE, currentPage);
////            // 거리순
////        } else if (feed_sort_toggle.isChecked()) {
////            fetchByLocation(PER_PAGE, currentPage);
////        }
//
//        loadData(feedStatus, PER_PAGE, currentPage);
//
//        // feedAdapter.setMoreLoading(false);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MoldeMainActivity) context).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        feed_sort_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!feed_sort_toggle.isChecked()) {
                    feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_off));
                    loadData(FEED_BY_DISTANCE, PER_PAGE, currentPage);
                } else if (feed_sort_toggle.isChecked()) {
                    feed_sort_toggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_feed_toggle_on));
                    loadData(FEED_BY_LAST, PER_PAGE, currentPage);
                }
            }
        });
    }

    // 4. onItemClick
    @Override
    public void callFeedData(MoldeFeedEntity feedEntitiy) {
        if (((MoldeMainActivity) getActivity()).fragmentSparseArray.get(R.string.main_menu_map) != null) {
            // feedData = feedEntitiy;
            Bundle bundle = new Bundle();
            bundle.putInt("reportFeedId", feedEntitiy.getRepId());
            bundle.putString("reportFeedUserName", feedEntitiy.getUserName());
            bundle.putString("reportFeedUserEmail", feedEntitiy.getUserEmail());
            bundle.putString("reportFeedUserId", feedEntitiy.getUserId());
            bundle.putString("reportFeedContent", feedEntitiy.getRepContents());

            bundle.putDouble("reportFeedLocationLat", feedEntitiy.getRepLat());
            bundle.putDouble("reportFeedLocationLng", feedEntitiy.getRepLon());
            bundle.putString("reportFeedAddress", feedEntitiy.getRepAddr());
            bundle.putString("reportFeedDetailAddress", feedEntitiy.getRepDetailAddr());
            bundle.putString("reportFeedDate", feedEntitiy.getRepDate());
            bundle.putString("reportFeedThumbnail", feedEntitiy.getRepImg().get(0).getFilepath());
            bundle.putInt("reportFeedState", feedEntitiy.getRepState());

            Fragment fragment = (Fragment) ((MoldeMainActivity) getActivity())
                    .fragmentSparseArray.get(R.string.main_menu_map);
            fragment.setArguments(bundle);
            ((MoldeMainActivity) getActivity()).replaceFragment(fragment);
            BottomNavigationView navigation = ((MoldeMainActivity) getActivity()).findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.main_menu_map);

        } else {
            ((MoldeMainActivity) getActivity()).replaceFragment(MapFragment.newInstance());
            BottomNavigationView navigation = ((MoldeMainActivity) getActivity()).findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.main_menu_map);
        }
    }

    // 5. setHasMoreToLoad
    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!feed_sort_toggle.isChecked()) {
            feedFragmentSparseArrayCompat.append(R.string.feedStatus, FEED_BY_DISTANCE);
        } else if (feed_sort_toggle.isChecked()) {
            feedFragmentSparseArrayCompat.append(R.string.feedStatus, FEED_BY_LAST);
        }
    }

    @Override
    public void onBackKey() {

    }

    /**
     * TODO 생명주기 관리가 전혀 안 됨. 왜 이게 어쩔 때는 저장되었다가 어쩔 때는 원상태인지 파악이 안 됨
     */
    // 6. 생명주기
    @Override
    public void onPause() {
        super.onPause();
        setHasMoreToLoad(true);
        currentPage = 0;
    }

}
