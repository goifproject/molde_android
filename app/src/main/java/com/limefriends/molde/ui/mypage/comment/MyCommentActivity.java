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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.Constant;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollExpandableListView;
import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.comm.custom.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.comm.utils.NetworkUtil;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.comment.CommentEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntityList;
import com.limefriends.molde.entity.comment.ReportedCommentEntity;
import com.limefriends.molde.entity.comment.ReportedCommentResponseInfoEntity;
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

    private void snack(String message) {
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

        if (!NetworkUtil.isConnected(this)) {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

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

                if (commentSchemas == null || commentSchemas.size() == 0) {
                    myComment_listView.setIsLoading(false);
                    progressBar.setVisibility(View.GONE);
                    hasMoreToLoad(false);
                    return;
                }

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

                    List<CardNewsResponseInfoEntity> newsSchema = response.body().getData();
                    if (newsSchema != null && newsSchema.size() != 0) {
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
                    }
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

        if (!NetworkUtil.isConnected(this)) {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

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

                    List<ReportedCommentResponseInfoEntity> schemas = response.body().getData();

                    if (schemas == null || schemas.size() == 0) {
                        myComment_reported_comment_listview.setIsLoading(false);
                        progressBar.setVisibility(View.GONE);
                        hasMoreToLoad(false);
                        return;
                    }

                    List<ReportedCommentEntity> entities
                            = FromSchemaToEntitiy.reportedComment(schemas);
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
                    if (commentSchema != null && commentSchema.size() != 0) {
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

        if (!NetworkUtil.isConnected(this)) {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<Result> call = getCommentService().deleteComment(commentId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    snack("신고된 댓글을 삭제했습니다");
                    deleteReportedComment(position, commentId, false);

                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    // 댓글 삭제시 신고된 댓글도 삭제
    // TODO 아직 삭제가 안
    private void deleteReportedComment(final int position, int commentId, final boolean refuseReport) {

        if (!NetworkUtil.isConnected(this)) {
            Toast.makeText(this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        Call<Result> call = getCommentService().deleteReportedComment(commentId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    if (refuseReport) snack("신고를 취소했습니다");
                    reportedCommentAdapter.deleteComment(position);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    public void showDeleteCommentDialog(final int position, final int commentId) {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle(getText(R.string.dialog_title_change_status))
                .setMessage(getText(R.string.dialog_msg_delete_comment))
                .setPositiveButton(getText(R.string.refuse_report), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReportedComment(position, commentId, true);
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