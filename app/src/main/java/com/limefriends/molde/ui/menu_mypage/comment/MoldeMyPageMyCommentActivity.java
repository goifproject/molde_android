package com.limefriends.molde.ui.menu_mypage.comment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.entity.comment.MoldeCommentEntity;
import com.limefriends.molde.entity.comment.MoldeCommentResponseInfoEntity;
import com.limefriends.molde.entity.comment.MoldeCommentResponseInfoEntityList;
import com.limefriends.molde.entity.news.MoldeCardNewsEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.comm.utils.comparator.CardNewsComparator;
import com.limefriends.molde.comm.utils.comparator.CommentComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 정리
 *
 * 서버에서 데이터를 묶어서 보내주는 게 가장 편하긴 하지만 클라이언트 사정에 따라 일일이 서버를 구성하면
 *
 * 확장성이 떨어지고 1:1 코드가 늘어나기 때문에 서버에서는 데이터 호출을 모듈화 해 놓는 듯 하다.
 *
 * 따라서 클라이언트에서 2개 api 호출이 필요한 경우, 또한 2개의 api 를 묶어서 보여줘야 하는 경우 보다 복잡한 구성이 이루어진다
 *
 * 1. 첫번째 api 호출 이후 반복문을 돌면서 다시 api 를 호출하는 경우
 *
 * retrofit 은 비동기 호출이기 때문에 10개 반복문을 돌면 10개 응답 중 어떤 것이 마지막에 올지 예측할 수 없다
 *
 * 따라서 해당 호출에 count++ 를 사용한다거나 size() 를 통해 비교한다거나 멤버변수 값을 변화시키려고 하는 경우
 *
 * 혹은 일부는 반복문 내부에서 돌리고 나머지는 네트워크의 응답을 통해 값을 변경하려고 하는 경우
 *
 * 눈에는 속도가 비슷해 보여도 네트워크 속도가 훨씬 느리기 때문에 반복문이 전부 먼저 실행되고 나머지 네트워크가 한참 뒤에
 *
 * 그것도 어떤 것이 먼저인지 모른 채로 응답된다.
 *
 * 참고로 첫번째 케이스는 카드뉴스와 댓글을 하나의 List 에 담아 넘겨주는 형식으로 함
 *
 * 2. 첫번째 api 호출 이후 반복문을 돌면서 호출하되 값을 변수로 넘겨줘서 콜백에서 값을 묶어주는 경우
 *
 * 이렇게 할 경우 카드뉴스 객체에 댓글 리스트를 담아 ExpandableListView 에서 보여주는 형태가 된다
 *
 * 3. 첫번째 api 호출 이후 반복문을 돌면서 호출하되 count 값에 락을 걸어서 마지막에 값을 갱신하도록 하는 경우
 *
 * 이렇게 할 경우 count 값에 반드시 락을 걸어서 비교하려고 값을 가져오고 값을 연산하는 사이에 값이 또 변경되는 경우가 없도록 해야 한다
 *
 * 4. 동기로 데이터를 불러오는 경우
 *
 * enqueue 가 아닌 execute 혹은 따로 스레드를 통해서 하나씩 값을 불러올 수 있다.
 *
 * 혹은 synchronized 를 통해 하나씩 데어터를 호출하도록 할 수 있다.
 *
 * 결론)
 *
 * 어떤 경우에 10개 이상의 비동기 처리를 생각해 줘야 하는지
 *
 * 비동기로 호출한 2개의 값을 어떻게 하나로 합칠 수 있는지 생각해 보면
 *
 * 왜 synchronized 가 필요한지 Observable, 스레드 처리, Retrofit, OkHttp, Rx 가
 *
 * 왜 사용되는지 왜 등장했는지 조금은 이해할 수 있을 것 같다.
 *
 * 다양한 케이스를 생각해 보면 결국 어디에서는 특정 시점을 캐치해 낼 수 있어야 하고
 *
 * 어디에선가는 동기화가 필요하다.
 *
 * 마지막으로 Rx 를 사용하면 정말 편하게 개발할 수 있을 것 같다는 생각이 듬
 */
public class MoldeMyPageMyCommentActivity extends AppCompatActivity {

    @BindView(R.id.myComment_recyclerView)
    AddOnScrollRecyclerView myComment_recyclerView;

    MoldeMyPageMyCardNewsCommentAdapter commentAdapter;

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;

    // 댓글 목록
    List<MoldeCommentResponseInfoEntity> commentSchemas = new ArrayList<>();
    List<MoldeCommentEntity> commentEntities = new ArrayList<>();

    // 카드뉴스 목록
    List<MoldeCardNewsEntity> newsEntities = new ArrayList<>();

    // 마지막에 추가된 카드뉴스
    List myCommentList = new ArrayList();

    int count = 0;

    @BindView(R.id.progressBar2)
    ProgressBar progressBar;


    private int lastAddedCommentNewsId = -1;
    private boolean hasMoreToLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_my_comment);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("내가 쓴 댓글");

//        myPageMyCommentEntityList = new ArrayList<MoldeCommentEntity>();
//
//        myPageMyCommentEntityList.add(new MoldeCommentEntity("에어비앤*에서 다시 몰카 발각",
//                R.drawable.mypage_image, "2017.02.01",
//                "세상에… 이런일이 다 있네요 ㅠㅠ"));
//
//        myPageMyCommentEntityList.add(new MoldeCommentEntity("에어비앤*에서 다시 몰카 발각",
//                R.drawable.mypage_image,
//                "2017.02.02",
//                "세상에… 이런일이 다 있네요 ㅠㅠ"));
//
//        myPageMyCommentEntityList.add(new MoldeCommentEntity("에어비앤*에서 다시 몰카 발각",
//                R.drawable.mypage_image,
//                "2017.02.03",
//                "세상에… 이런일이 다 있네요 ㅠㅠ"));
//
//        myPageMyCommentEntityList.add(new MoldeCommentEntity("에어비앤*에서 다시 몰카 발각",
//                R.drawable.mypage_image,
//                "2017.02.04",
//                "세상에… 이런일이 다 있네요 ㅠㅠ"));
//
        commentAdapter = new MoldeMyPageMyCardNewsCommentAdapter(this);

        // 1. 어댑터
        myComment_recyclerView.setAdapter(commentAdapter);

        // 2. 레이아웃 매니저
        myComment_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()), false);

        // 3. loadMore
        myComment_recyclerView.setOnLoadMoreListener(new AddOnScrollRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadCommentData(PER_PAGE, currentPage);
            }
        });

        loadCommentData(PER_PAGE, FIRST_PAGE);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return false;
//    }



    private void loadCommentData(int perPage, int page) {

        progressBar.setVisibility(View.VISIBLE);

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // 2. 불러온다면 프로그래스바를 띄움 -> 이렇게 할 경우 GridLayout 에서 처리가 안 됨
        // commentAdapter.setProgressMore(true);

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        myComment_recyclerView.setIsLoading(true);

        MoldeRestfulService.Comment commentService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Comment.class);

        final MoldeRestfulService.CardNews newsService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.CardNews.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<MoldeCommentResponseInfoEntityList> call = commentService.getMyComment("lkj", PER_PAGE, currentPage);

        call.enqueue(new Callback<MoldeCommentResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeCommentResponseInfoEntityList> call, Response<MoldeCommentResponseInfoEntityList> response) {
                commentSchemas = response.body().getData();

                // 같은 뉴스의 댓글들을 모으기 위해 오름차순 정렬
                Collections.sort(commentSchemas, new CommentComparator());

                for (MoldeCommentResponseInfoEntity commentSchema : commentSchemas) {
                    Log.e("소팅 확인", commentSchema.getCommId()+"");
                }

                for (MoldeCommentResponseInfoEntity commentSchema : commentSchemas) {
                    MoldeCommentEntity commentEntity = fromSchemaToLocalEntity(commentSchema);
                    commentEntities.add(commentEntity);
                    if (lastAddedCommentNewsId == commentEntity.getNewsId()) {
                        addCount();
                        Log.e("카운트1", count+"");
                        continue;
                    }
                    lastAddedCommentNewsId = commentEntity.getNewsId();
                    loadCardNewsData(newsService, commentEntity);
                }
                // 비동기로 진행되기 때문에 마지막 데이터가 세팅되지 않고 호출될 수 있다.
                // adapter.setData(fromSchemaToLocalEntity(data));
            }

            @Override
            public void onFailure(Call<MoldeCommentResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void loadCardNewsData(MoldeRestfulService.CardNews newsService, final MoldeCommentEntity commentEntity) {

//        Log.e("뉴스 번호 확인-last", lastAddedCommentNewsId+"");
//        Log.e("뉴스 번호 확인-current", commentEntity.getNewsId()+"");

        // 같은 뉴스이면 뉴스만 추가하고 넘어감
        // 비동기로 처리하기 때문에 for문을 돌면서 무조건 호출이 되고, 나머지는 리턴될때까지 기다렸다가 lastAddedCommentNewsId
        // 를 세팅하기 때문에 당연히 여기서
//        if (lastAddedCommentNewsId == commentEntity.getNewsId()) {
//            Log.e("댓글만 추가", lastAddedCommentNewsId+"");
//            myCommentList.add(commentEntity);
//            return;
//        }

        final Call<MoldeCardNewsResponseInfoEntityList> newsCall = newsService.getCardNewsListById(commentEntity.getNewsId());

        newsCall.enqueue(new Callback<MoldeCardNewsResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeCardNewsResponseInfoEntityList> call, Response<MoldeCardNewsResponseInfoEntityList> response) {
                if (response.isSuccessful()) {

                    // TODO 네트워크 통신을 신뢰하면 안 됨

                    List<MoldeCardNewsResponseInfoEntity> newsSchemas = response.body().getData();

                    if (newsSchemas.size() != 0) {
                        MoldeCardNewsResponseInfoEntity schema = response.body().getData().get(0);

                        MoldeCardNewsEntity cardNewsEntity = new MoldeCardNewsEntity(
                                schema.getNewsId(),
                                schema.getPostId(),
                                schema.getDescription(),
                                schema.getDate(),
                                schema.getNewsImg()
                        );

                        newsEntities.add(cardNewsEntity);
                    }



                    // 카드 뉴스 저장
//                    if (lastAddedCommentNewsId != commentEntity.getNewsId()) {
//                        myCommentList.add(cardNewsEntity);
//                    }

                    // 뉴스 불러온 댓글 저장
                    // myCommentList.add(commentEntity);

                    // cardNewsEntity.addComments(commentEntity);

                    // lastAddedCommentNewsId = commentEntity.getNewsId();




                    addCount();


                    Log.e("카운트2", count+"");
//                    Log.e("둘다 추가", lastAddedCommentNewsId+"");
//                    Log.e("뉴스 번호 확인2-last", lastAddedCommentNewsId+"");
//                    Log.e("뉴스 번호 확인2-current", commentEntity.getNewsId()+"");

                    // newsEntities.add(cardNewsEntity);

                    if (commentSchemas.size() == count) {

                        Collections.sort(newsEntities, new CardNewsComparator());

                        for (MoldeCardNewsEntity cardNews : newsEntities) {
                            myCommentList.add(cardNews);
                            for (MoldeCommentEntity commentEntity : commentEntities) {
                                if (cardNews.getNewsId() == commentEntity.getNewsId()) {
                                    myCommentList.add(commentEntity);
                                }
                            }
                        }
                        // 데이터 추가
                        commentAdapter.addAll(myCommentList);
                        // 추가 완료 후 다음 페이지로 넘어가도록 세팅
                        currentPage++;
                        // 더 이상 데이터를 세팅중이 아님을 명시
                        myComment_recyclerView.setIsLoading(false);
                        // 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다
                        if (myCommentList.size() < PER_PAGE) {
                            setHasMoreToLoad(false);
                        }

                        myComment_recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

//                    if (myCommentList.size() == commentSchemas.size()) {
//                        adapter.setData(myCommentList);
//                        myComment_recyclerView.setVisibility(View.VISIBLE);
//                        progressBar.setVisibility(View.GONE);
//                    }

                }
            }

            @Override
            public void onFailure(Call<MoldeCardNewsResponseInfoEntityList> call, Throwable t) {

            }
        });
    }


    private synchronized void addCount() {
        count++;
    }

    private MoldeCommentEntity fromSchemaToLocalEntity(MoldeCommentResponseInfoEntity entity) {
        return new MoldeCommentEntity(
                entity.getCommId(),
                entity.getUserId(),
                entity.getUserName(),
                entity.getNewsId(),
                entity.getComment(),
                entity.getCommDate());
    }

    private List<MoldeCommentEntity> fromSchemaToLocalEntity(List<MoldeCommentResponseInfoEntity> data) {
        List<MoldeCommentEntity> comments = new ArrayList<>();
        for (MoldeCommentResponseInfoEntity entity : data) {
            comments.add(new MoldeCommentEntity(
                    entity.getCommId(),
                    entity.getUserId(),
                    entity.getUserName(),
                    entity.getNewsId(),
                    entity.getComment(),
                    entity.getCommDate()
            ));
        }
        return comments;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    // setHasMoreToLoad
    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    // 생명주기 관리
    @Override
    public void onPause() {
        super.onPause();
        setHasMoreToLoad(true);
        currentPage = 0;
    }

}