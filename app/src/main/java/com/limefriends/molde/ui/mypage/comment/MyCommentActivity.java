package com.limefriends.molde.ui.mypage.comment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.Constant;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollExpandableListView;
import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.comm.custom.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.comment.CommentEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntityList;
import com.limefriends.molde.entity.comment.ReportedCommentEntity;
import com.limefriends.molde.entity.comment.ReportedCommentResponseInfoEntityList;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.comm.utils.comparator.CardNewsComparator;
import com.limefriends.molde.comm.utils.comparator.CommentComparator;
import com.limefriends.molde.ui.magazine.comment.CardNewsCommentActivity;
import com.limefriends.molde.ui.magazine.detail.CardNewsDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.limefriends.molde.comm.Constant.Comment.EXTRA_KEY_COMMENT_DESCRIPTION;
import static com.limefriends.molde.comm.Constant.Common.EXTRA_KEY_CARDNEWS_ID;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_AUTHORITY;


/**
 * 정리
 * <p>
 * 서버에서 데이터를 묶어서 보내주는 게 가장 편하긴 하지만 클라이언트 사정에 따라 일일이 서버를 구성하면
 * <p>
 * 확장성이 떨어지고 1:1 코드가 늘어나기 때문에 서버에서는 데이터 호출을 모듈화 해 놓는 듯 하다.
 * <p>
 * 따라서 클라이언트에서 2개 api 호출이 필요한 경우, 또한 2개의 api 를 묶어서 보여줘야 하는 경우 보다 복잡한 구성이 이루어진다
 * <p>
 * 1. 첫번째 api 호출 이후 반복문을 돌면서 다시 api 를 호출하는 경우
 * <p>
 * retrofit 은 비동기 호출이기 때문에 10개 반복문을 돌면 10개 응답 중 어떤 것이 마지막에 올지 예측할 수 없다
 * <p>
 * 따라서 해당 호출에 count++ 를 사용한다거나 size() 를 통해 비교한다거나 멤버변수 값을 변화시키려고 하는 경우
 * <p>
 * 혹은 일부는 반복문 내부에서 돌리고 나머지는 네트워크의 응답을 통해 값을 변경하려고 하는 경우
 * <p>
 * 눈에는 속도가 비슷해 보여도 네트워크 속도가 훨씬 느리기 때문에 반복문이 전부 먼저 실행되고 나머지 네트워크가 한참 뒤에
 * <p>
 * 그것도 어떤 것이 먼저인지 모른 채로 응답된다.
 * <p>
 * 참고로 첫번째 케이스는 카드뉴스와 댓글을 하나의 List 에 담아 넘겨주는 형식으로 함
 * <p>
 * 2. 첫번째 api 호출 이후 반복문을 돌면서 호출하되 값을 변수로 넘겨줘서 콜백에서 값을 묶어주는 경우
 * <p>
 * 이렇게 할 경우 카드뉴스 객체에 댓글 리스트를 담아 ExpandableListView 에서 보여주는 형태가 된다
 * <p>
 * 3. 첫번째 api 호출 이후 반복문을 돌면서 호출하되 count 값에 락을 걸어서 마지막에 값을 갱신하도록 하는 경우
 * <p>
 * 이렇게 할 경우 count 값에 반드시 락을 걸어서 비교하려고 값을 가져오고 값을 연산하는 사이에 값이 또 변경되는 경우가 없도록 해야 한다
 * <p>
 * 4. 동기로 데이터를 불러오는 경우
 * <p>
 * enqueue 가 아닌 execute 혹은 따로 스레드를 통해서 하나씩 값을 불러올 수 있다.
 * <p>
 * 혹은 synchronized 를 통해 하나씩 데어터를 호출하도록 할 수 있다.
 * <p>
 * 결론)
 * <p>
 * 어떤 경우에 10개 이상의 비동기 처리를 생각해 줘야 하는지
 * <p>
 * 비동기로 호출한 2개의 값을 어떻게 하나로 합칠 수 있는지 생각해 보면
 * <p>
 * 왜 synchronized 가 필요한지 Observable, 스레드 처리, Retrofit, OkHttp, Rx 가
 * <p>
 * 왜 사용되는지 왜 등장했는지 조금은 이해할 수 있을 것 같다.
 * <p>
 * 다양한 케이스를 생각해 보면 결국 어디에서는 특정 시점을 캐치해 낼 수 있어야 하고
 * <p>
 * 어디에선가는 동기화가 필요하다.
 * <p>
 * 마지막으로 Rx 를 사용하면 정말 편하게 개발할 수 있을 것 같다는 생각이 듬
 */
// TODO responsecount로 처리하는 애들 중에 만약 중간에 네트워크가 끊기거나 하나 못 받아오면 어떻게 되는거임
// TODO addOnScroll 추가 구현할 것
// TODO 어드민 페이지랑 delete api 추가할 것
public class MyCommentActivity extends AppCompatActivity implements MyCommentExpandableAdapter.OnItemClickCallback {

    @BindView(R.id.myComment_container)
    RelativeLayout myComment_container;
    @BindView(R.id.myComment_listView)
    AddOnScrollExpandableListView myComment_listView;
    @BindView(R.id.myComment_reported_comment_listview)
    AddOnScrollRecyclerView myComment_reported_comment_listview;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar;

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;

    private MyCommentExpandableAdapter commentExpandableAdapter;
    private MoldeRestfulService.Comment commentService;
    private MoldeRestfulService.CardNews newsService;
    private ReportedCommentAdapter reportedCommentAdapter;

    // 댓글 목록
    private List<CommentResponseInfoEntity> commentSchemas = new ArrayList<>();
    private List<CommentEntity> commentEntities = new ArrayList<>();
    // 카드뉴스 목록
    private List<CardNewsEntity> newsEntities = new ArrayList<>();
    private boolean hasMoreToLoad = true;
    private int fetchCount = 0;
    private int responseCount = 0;

    private long authority;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);

        setupViews();

        setupList();

        loadData();
    }

    //-----
    // View
    //-----

    private void setupViews() {
        setContentView(R.layout.activity_my_comment);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title
                = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        if (authority == Constant.Authority.ADMIN) {
            toolbar_title.setText(getText(R.string.reported_comment));
        } else {
            toolbar_title.setText(getText(R.string.my_comment));
        }
    }

    private void setupList() {
//        TODO 풀어줘
        if (authority == Constant.Authority.ADMIN) {
            myComment_reported_comment_listview.setVisibility(View.VISIBLE);
            myComment_listView.setVisibility(View.INVISIBLE);
            reportedCommentAdapter = new ReportedCommentAdapter(this, this);
            myComment_reported_comment_listview.setAdapter(reportedCommentAdapter);
            myComment_reported_comment_listview
                    .setLayoutManager(new LinearLayoutManager(this), false);
            myComment_reported_comment_listview.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void loadMore() {
                    Log.e("호출확인 ","loadMore3");
                    loadReportedComment(PER_PAGE, currentPage);
                }
            });
        } else {
            commentExpandableAdapter = new MyCommentExpandableAdapter(this);
            myComment_listView.setAdapter(commentExpandableAdapter);
            myComment_listView.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void loadMore() {
                    loadComment(PER_PAGE, currentPage);
                }
            });
            myComment_listView.setGroupIndicator(null);
            myComment_listView.setChildIndicator(null);
            myComment_listView.setChildDivider(getResources().getDrawable(R.color.white));
            myComment_listView.setDivider(getResources().getDrawable(R.color.white));
            myComment_listView.setDividerHeight(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private void snack(String message){
        Snackbar.make(myComment_container, message, Snackbar.LENGTH_LONG).show();
    }

    //-----
    // Network
    //-----

    private MoldeRestfulService.CardNews getNewsService() {
        if (newsService == null) {
            newsService
                    = MoldeNetwork.getInstance().generateService(MoldeRestfulService.CardNews.class);
        }
        return newsService;
    }

    private MoldeRestfulService.Comment getCommentService() {
        if (commentService == null) {
            commentService
                    = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Comment.class);
        }
        return commentService;
    }

    private void loadData() {
        if (authority == Constant.Authority.ADMIN) {
            loadReportedComment(PER_PAGE, currentPage);
        } else {
            loadComment(PER_PAGE, currentPage);
        }
    }

    // 1. 내가 작성한 댓글
    private void loadComment(int perPage, int page) {

        if (!hasMoreToLoad) return;

        progressBar.setVisibility(View.VISIBLE);

        myComment_listView.setIsLoading(true);

        String uId
                = ((MoldeApplication) getApplication()).getFireBaseAuth().getCurrentUser().getUid();

        Call<CommentResponseInfoEntityList> call
                = getCommentService().getMyComment(uId, perPage, page);

        call.enqueue(new Callback<CommentResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<CommentResponseInfoEntityList> call,
                                   Response<CommentResponseInfoEntityList> response) {
                // 같은 뉴스의 댓글들을 모으기 위해 오름차순 정렬
                commentSchemas = response.body().getData();
                Collections.sort(commentSchemas, new CommentComparator());
                CommentEntity lastAddedComment = null;
                outer:
                for (int i = 0; i < commentSchemas.size(); i++) {
                    // 꺼내고
                    CommentEntity commentEntity
                            = FromSchemaToEntitiy.comment(commentSchemas.get(i));

                    // 이전 뉴스 소속이 아닌지 확인해본다
                    if (newsEntities.size() != 0) {

                        int addCount = 0;

                        for (CardNewsEntity newsEntity : newsEntities) {
                            if (newsEntity.getNewsId() == commentEntity.getNewsId()) {
                                newsEntity.addComments(commentEntity);

                                addCount++;

                                if (commentSchemas.size() == addCount) {

                                    Collections.sort(newsEntities, new CardNewsComparator());
                                    commentExpandableAdapter.addAll(newsEntities);

                                    for (int j = 0; j < newsEntities.size(); j++) {
                                        myComment_listView.expandGroup(j);
                                    }
                                    fetchCount = 0;
                                    responseCount = 0;
                                    currentPage++;
                                    myComment_listView.setIsLoading(false);
                                    if (commentSchemas.size() < PER_PAGE) {
                                        hasMoreToLoad(false);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }

                                continue outer;
                            }
                        }
                    }

                    // TODO 동기화 해야 할 듯.
                    // 1. 댓글 리스트를 먼저 다시 리스트에 담고
                    // 2. 반복문으로 돌리면서 호출
                    // 3. 리스트 개수 == responseCount 일 때 마지막 처리

                    // 처음이거나 바로 이전 댓글과 소속 뉴스가 같으면 더해준다
                    if (lastAddedComment == null ||
                            lastAddedComment.getNewsId() == commentEntity.getNewsId()) {
                        commentEntities.add(commentEntity);
                        if (i == commentSchemas.size() - 1) {
                            loadCardNews(commentEntities);
                            commentEntities = new ArrayList<>();
                            return;
                        }
                    } else if (lastAddedComment.getNewsId() != commentEntity.getNewsId()) {
                        // 만약 다를 경우 바로 이전 댓글까지의 모음 해당하는 뉴스를 호출한다
                        loadCardNews(commentEntities);
                        commentEntities = new ArrayList<>();
                        commentEntities.add(commentEntity);
                        if (i == commentSchemas.size() - 1) {
                            loadCardNews(commentEntities);
                            commentEntities = new ArrayList<>();
                            return;
                        }
                    }
                    lastAddedComment = commentEntity;
                }

                if (commentEntities.size() == 0) progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<CommentResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    // 내가 댓글을 작성한 카드뉴스
    private void loadCardNews(final List<CommentEntity> entityList) {

        addFetchCount();

        Call<CardNewsResponseInfoEntityList> newsCall
                = getNewsService().getCardNewsListById(entityList.get(0).getNewsId());

        newsCall.enqueue(new Callback<CardNewsResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<CardNewsResponseInfoEntityList> call,
                                   Response<CardNewsResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<CardNewsResponseInfoEntity> newsSchemas = response.body().getData();

                    if (newsSchemas.size() == 0) return;
                    CardNewsResponseInfoEntity schema = response.body().getData().get(0);
                    CardNewsEntity cardNewsEntity = new CardNewsEntity(
                            schema.getNewsId(),
                            schema.getPostId(),
                            schema.getDescription(),
                            schema.getDate(),
                            schema.getNewsImg(),
                            entityList
                    );
                    newsEntities.add(cardNewsEntity);
                    addResponseCount();

                    if (fetchCount == responseCount) {
                        Collections.sort(newsEntities, new CardNewsComparator());
                        commentExpandableAdapter.addAll(newsEntities);
                        for (int i = 0; i < newsEntities.size(); i++) {
                            myComment_listView.expandGroup(i);
                        }
                        fetchCount = 0;
                        responseCount = 0;
                        currentPage++;
                        myComment_listView.setIsLoading(false);
                        if (commentSchemas.size() < PER_PAGE) {
                            hasMoreToLoad(false);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CardNewsResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    // 2. 관리자용 신고된 댓글
    private void loadReportedComment(int perPage, int page) {

        if (!hasMoreToLoad) return;

        myComment_reported_comment_listview.setIsLoading(true);

        progressBar.setVisibility(View.VISIBLE);

        Call<ReportedCommentResponseInfoEntityList> call
                = getCommentService().getReportedComment(perPage, page);

        call.enqueue(new Callback<ReportedCommentResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<ReportedCommentResponseInfoEntityList> call,
                                   Response<ReportedCommentResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<ReportedCommentEntity> entities
                            = FromSchemaToEntitiy.reportedComment(response.body().getData());
                    fetchCount = entities.size();
                    for (ReportedCommentEntity commentEntity : entities) {
                        loadCardNewsComment(commentEntity.getCommId());
                    }
                    if (fetchCount == 0) {
                        progressBar.setVisibility(View.GONE);
                        myComment_reported_comment_listview.setIsLoading(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReportedCommentResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    // 신고된 댓글 내용
    private void loadCardNewsComment(int commentId) {

        // TODO commentId 를 통해 하나씩 가져올 수 있어야 함
        Call<CommentResponseInfoEntityList> call
                = getCommentService().getReportedCommentDetail(commentId);

        call.enqueue(new Callback<CommentResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<CommentResponseInfoEntityList> call,
                                   Response<CommentResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    addResponseCount();
                    List<CommentResponseInfoEntity> commentSchema = response.body().getData();
                    if (commentSchema.size() != 0) {
                        CommentEntity newsEntity = FromSchemaToEntitiy.comment(commentSchema.get(0));
                        commentEntities.add(newsEntity);
                    }
                    if (fetchCount == responseCount) {
                        progressBar.setVisibility(View.GONE);
                        reportedCommentAdapter.addData(commentEntities);
                        currentPage++;
                        fetchCount = 0;
                        responseCount = 0;
                        myComment_reported_comment_listview.setIsLoading(false);
                        if (commentEntities.size() < PER_PAGE) {
                            hasMoreToLoad(false);
                        }
                        // 순서 지키세요 위에서 제거하면 size가 0이 되어 hasMoreToLoad(false); 됨
                        commentEntities.clear();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    // 3. 댓글 삭제
    public void deleteComment(final int position, final int commentId) {

        String uId
                = ((MoldeApplication) getApplication()).getFireBaseAuth().getCurrentUser().getUid();

        Call<Result> call = getCommentService().deleteComment(uId, commentId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    // deleteReportedComment(position, commentId);
                    snack("신고된 댓글을 삭제했습니다");
                    reportedCommentAdapter.deleteComment(position);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    // 댓글 삭제시 신고된 댓글도 삭제
    // TODO 아직 삭제가 안
    private void deleteReportedComment(int position, int commentId) {

        Call<Result> call = getCommentService().deleteReportedComment(commentId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    // snack("신고를 취소했습니다");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    public void showDeleteCommentDialog(final int position, final int commentId) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getText(R.string.dialog_title_change_status))
                .setMessage(getText(R.string.dialog_msg_delete_comment))
                .setPositiveButton(getText(R.string.refuse_report), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReportedComment(position, commentId);
                    }
                })
                .setNegativeButton(getText(R.string.delete_comment), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteComment(position, commentId);
                    }
                })
                .setNeutralButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private synchronized void addFetchCount() {
        fetchCount++;
    }

    private synchronized void addResponseCount() {
        responseCount++;
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void onParentItemClick(int groupPosition, int newsId) {
        myComment_listView.expandGroup(groupPosition);
        Intent intent = new Intent();
        intent.setClass(this, CardNewsDetailActivity.class);
        intent.putExtra(EXTRA_KEY_CARDNEWS_ID, newsId);
        startActivity(intent);
    }

    @Override
    public void onChildItemClick(int childPosition, int newsId, String description) {
        Intent intent = new Intent();
        intent.setClass(this, CardNewsCommentActivity.class);
        intent.putExtra(EXTRA_KEY_CARDNEWS_ID, newsId);
        intent.putExtra(EXTRA_KEY_COMMENT_DESCRIPTION, description);
        startActivity(intent);
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroy() {
        super.onDestroy();
        hasMoreToLoad(true);
        currentPage = 0;
    }


}