package com.limefriends.molde.screen.mypage.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
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
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.common.utils.NetworkUtil;
import com.limefriends.molde.common.utils.PreferenceUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.common.utils.comparator.CardNewsComparator;
import com.limefriends.molde.screen.common.recyclerview.addOnRecycler.AddOnScrollExpandableListView;
import com.limefriends.molde.screen.common.recyclerview.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
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

public class MyCommentActivity
        extends BaseActivity
        implements MyCommentExpandableAdapter.OnItemClickCallback, ReportedCommentAdapter.OnCommentClickListener {

    public static final String DELETE_COMMENT_DIALOG = "DELETE_COMMENT_DIALOG";
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
    private boolean isLoading;

    private MyCommentExpandableAdapter commentExpandableAdapter;
    private ReportedCommentAdapter reportedCommentAdapter;

    // 카드뉴스 목록
    private List<CardNewsEntity> newsEntities = new ArrayList<>();
    private boolean hasMoreToLoad = true;
    private long authority;

    @Service private Repository.Comment mCommentRepository;
    @Service private ToastHelper mToastHelper;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;

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
        if (authority == Constant.Authority.ADMIN) {
            myComment_reported_comment_listview.setVisibility(View.VISIBLE);
            myComment_listView.setVisibility(View.INVISIBLE);
            reportedCommentAdapter = new ReportedCommentAdapter(this, this);
            myComment_reported_comment_listview.setAdapter(reportedCommentAdapter);
            myComment_reported_comment_listview.setLayoutManager(new LinearLayoutManager(this), false);
            myComment_reported_comment_listview.setOnLoadMoreListener(() -> {
                if (isLoading) return;
                loadReportedComment(PER_PAGE, currentPage);
            });
        } else {
            commentExpandableAdapter = new MyCommentExpandableAdapter(this);
            myComment_listView.setAdapter(commentExpandableAdapter);
            myComment_listView.setOnLoadMoreListener(() -> {
                if (isLoading) return;
                loadComment(PER_PAGE, currentPage);
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

    public void showDeleteCommentDialog(final int position, final int commentId) {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getText(R.string.dialog_msg_delete_comment).toString(),
                "",
                getText(R.string.refuse_report).toString(),
                getText(R.string.delete_comment).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                deleteReportedComment(position, commentId, true);
            }

            @Override
            public void onNegativeButtonClicked() {
                deleteComment(position, commentId);
            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, DELETE_COMMENT_DIALOG);
    }

    @Override
    public void onSirenClicked(int position, int commentId) {
        showDeleteCommentDialog(position, commentId);
    }

    @Override
    public void onCommentClicked(int cardNewsId) {
        mActivityScreenNavigator.toCardNewsDetailActivity(this, cardNewsId);
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

        isLoading = true;

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

                isLoading = false;

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

        isLoading = true;

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
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    hasMoreToLoad(false);
                    return;
                }

                progressBar.setVisibility(View.GONE);

                reportedCommentAdapter.addData(data);

                currentPage++;

                isLoading = false;

                if (data.size() < PER_PAGE) {
                    hasMoreToLoad(false);
                }
            }
        };
    }

    // 3. 댓글 삭제
    public void deleteComment(final int position, final int commentId) {

        mCompositeDisposable.add(
                mCommentRepository
                        .deleteComment(commentId)
                        .subscribe(
                                emit -> {
                                    snack("신고된 댓글을 삭제했습니다");
                                    deleteReportedComment(position, commentId, false);
                                },
                                err -> {},
                                () -> {}
                        )
        );
    }


    // 댓글 삭제시 신고된 댓글도 삭제
    private void deleteReportedComment(final int position, int commentId, final boolean refuseReport) {

        mCompositeDisposable.add(
                mCommentRepository
                        .deleteReportedComment(commentId)
                        .subscribe(
                                emit -> {
                                    if (refuseReport) snack("신고를 취소했습니다");
                                    reportedCommentAdapter.deleteComment(position);
                                },
                                err -> {},
                                () -> {}
                        )
        );
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