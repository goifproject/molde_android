package com.limefriends.molde.screen.mypage.comment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.DI.Service;
import com.limefriends.molde.common.MoldeApplication;
import com.limefriends.molde.common.utils.NetworkUtil;
import com.limefriends.molde.common.utils.PreferenceUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.common.utils.comparator.CardNewsComparator;
import com.limefriends.molde.screen.common.addOnListview.AddOnScrollExpandableListView;
import com.limefriends.molde.screen.common.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.magazine.comment.CardNewsCommentActivity;
import com.limefriends.molde.screen.magazine.detail.CardNewsDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.limefriends.molde.common.Constant.Comment.EXTRA_KEY_COMMENT_DESCRIPTION;
import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_CARDNEWS_ID;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_AUTHORITY;

public class MyCommentActivity extends BaseActivity implements MyCommentExpandableAdapter.OnItemClickCallback {

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
    private List<CommentEntity> commentEntities = new ArrayList<>();
    // 카드뉴스 목록
    private List<CardNewsEntity> newsEntities = new ArrayList<>();
    private boolean hasMoreToLoad = true;
    private int fetchCount = 0;
    private int responseCount = 0;

    private long authority;

    @Service private Repository.Comment mCommentRepository;
    @Service private ToastHelper mToastHelper;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

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
            myComment_reported_comment_listview.setLayoutManager(new LinearLayoutManager(this), false);
            myComment_reported_comment_listview.setOnLoadMoreListener(() -> loadReportedComment(PER_PAGE, currentPage));
        } else {
            commentExpandableAdapter = new MyCommentExpandableAdapter(this);
            myComment_listView.setAdapter(commentExpandableAdapter);
            myComment_listView.setOnLoadMoreListener(() -> loadComment(PER_PAGE, currentPage));
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

    private void loadData() {
        if (authority == Constant.Authority.ADMIN) {
            loadReportedComment(PER_PAGE, currentPage);
        } else {
            loadComment(PER_PAGE, currentPage);
        }
    }

    List<CardNewsEntity> extraFetchedNews;

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

        extraFetchedNews = new ArrayList<>();

        mCompositeDisposable.add(
                mCommentRepository
                        .getMyComment(newsEntities, uId, perPage, page)
                        .subscribeWith(getNewsObserver())
        );

    }

    private DisposableObserver<CardNewsEntity> getNewsObserver() {
        return new DisposableObserver<CardNewsEntity>() {
            @Override
            public void onNext(CardNewsEntity cardNewsEntity) {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("에러", e.getMessage());
            }

            @Override
            public void onComplete() {

                Collections.sort(newsEntities, new CardNewsComparator());
                commentExpandableAdapter.addAll(newsEntities);

                for (int i = 0; i < newsEntities.size(); i++) {
                    myComment_listView.expandGroup(i);
                }

                int commentSize = 0;

                for (CardNewsEntity entity : newsEntities) {
                    commentSize += entity.getComments().size();
                }

                if (commentSize < (currentPage + 1) * PER_PAGE) {
                    hasMoreToLoad(false);
                }

                currentPage++;

                myComment_listView.setIsLoading(false);

                progressBar.setVisibility(View.GONE);
            }
        };
    }

    // 2. 관리자용 신고된 댓글
    private void loadReportedComment(int perPage, int page) {

        if (!NetworkUtil.isConnected(this)) {
            mToastHelper.showNetworkError();
            return;
        }

        if (!hasMoreToLoad) return;

        myComment_reported_comment_listview.setIsLoading(true);

        progressBar.setVisibility(View.VISIBLE);

        mCompositeDisposable.add(
                mCommentRepository
                        .getReportedComment(perPage, page)
                        .subscribeWith(getReportedCommentObserver())
        );
    }

    private DisposableObserver<CommentEntity> getReportedCommentObserver() {
        List<CommentEntity> data = new ArrayList<>();
        return new DisposableObserver<CommentEntity>() {
            @Override
            public void onNext(CommentEntity commentEntity) {
                data.add(commentEntity);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("에러", e.getMessage());
            }

            @Override
            public void onComplete() {

                if (data.size() == 0) {
                    myComment_reported_comment_listview.setIsLoading(false);
                    progressBar.setVisibility(View.GONE);
                    hasMoreToLoad(false);
                    return;
                }

                progressBar.setVisibility(View.GONE);

                reportedCommentAdapter.addData(data);

                currentPage++;

                myComment_reported_comment_listview.setIsLoading(false);

                if (data.size() < PER_PAGE) {
                    hasMoreToLoad(false);
                }
            }
        };
    }

    // 3. 댓글 삭제
    public void deleteComment(final int position, final int commentId) {

        if (!NetworkUtil.isConnected(this)) {
            mToastHelper.showNetworkError();
            return;
        }

        mCompositeDisposable.add(
                mCommentRepository
                        .deleteComment(commentId)
                        .subscribe(
                                emit -> {}, err -> {}, () -> {
                                    snack("신고된 댓글을 삭제했습니다");
                                    deleteReportedComment(position, commentId, false);
                                }
                        )
        );
    }


    // 댓글 삭제시 신고된 댓글도 삭제
    // TODO 아직 삭제가 안
    private void deleteReportedComment(final int position, int commentId, final boolean refuseReport) {

        if (!NetworkUtil.isConnected(this)) {
            mToastHelper.showNetworkError();
            return;
        }

        mCompositeDisposable.add(
                mCommentRepository
                        .deleteReportedComment(commentId)
                        .subscribe(
                                emit -> {},
                                err -> {},
                                () -> {
                                    if (refuseReport) snack("신고를 취소했습니다");
                                    reportedCommentAdapter.deleteComment(position);
                                }
                        )
        );
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