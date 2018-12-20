package com.limefriends.molde.screen.controller.mypage.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.common.util.comparator.CardNewsComparator;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.view.mypage.comment.MyCommentView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static com.limefriends.molde.common.Constant.Common.PREF_KEY_AUTHORITY;

public class MyCommentActivity extends BaseActivity
        implements MyCommentExpandableAdapter.OnItemClickCallback, MyCommentView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent(context, MyCommentActivity.class);
        context.startActivity(intent);
    }

    public static final String DELETE_COMMENT_DIALOG = "DELETE_COMMENT_DIALOG";
    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean isLoading;

    // 카드뉴스 목록
    private List<CardNewsEntity> newsEntities = new ArrayList<>();
    private boolean hasMoreToLoad = true;
    private long authority;

    @Service private Repository.Comment mCommentRepository;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    @Service private ViewFactory mViewFactory;
    private MyCommentView mMyCommentView;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private List<CommentEntity> currentlyShownReportedComment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mMyCommentView = mViewFactory.newInstance(MyCommentView.class, null);

        setContentView(mMyCommentView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMyCommentView.registerListener(this);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);

        loadData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hasMoreToLoad(true);
        currentPage = 0;
    }

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

        if (!hasMoreToLoad) return;

        mMyCommentView.showProgressIndication();

        isLoading = true;

        String uId
                = ((MoldeApplication) getApplication()).getFireBaseAuth().getCurrentUser().getUid();

        extraFetchedNews = new ArrayList<>();

        mCompositeDisposable.add(
                mCommentRepository
                        .getMyComment(newsEntities, uId, perPage, page)
                        .subscribe(
                                e -> {},
                                err -> {
                                    mMyCommentView.showSnackBar("작성한 댓글을 불러오는 중 오류가 발생했습니다.");
                                    throw new RuntimeException("[MyCommentActivity][loadComment] " + err.getMessage());
                                },
                                () -> {
                                    Collections.sort(newsEntities, new CardNewsComparator());
                                    mMyCommentView.bindComments(newsEntities);
                                    mMyCommentView.expandMyCommentList(newsEntities.size());

                                    int commentSize = 0;

                                    for (CardNewsEntity entity : newsEntities) {
                                        commentSize += entity.getComments().size();
                                    }

                                    if (commentSize < (currentPage + 1) * PER_PAGE) {
                                        hasMoreToLoad(false);
                                    }

                                    currentPage++;

                                    isLoading = false;

                                    mMyCommentView.hideProgressIndication();
                                }
                        )
        );

    }

    // 2. 관리자용 신고된 댓글
    private void loadReportedComment(int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        mMyCommentView.showProgressIndication();

        List<CommentEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mCommentRepository
                        .getReportedComment(perPage, page)
                        .subscribe(
                                data::add,
                                err -> {
                                    mMyCommentView.showSnackBar("신고댓글을 불러오는 중 오류가 발생했습니다.");

                                },
                                () -> {
                                    if (data.size() == 0) {
                                        isLoading = false;
                                        mMyCommentView.hideProgressIndication();
                                        hasMoreToLoad(false);
                                        return;
                                    }

                                    mMyCommentView.hideProgressIndication();

                                    mMyCommentView.bindReportedComments(data);

                                    currentPage++;

                                    isLoading = false;

                                    if (data.size() < PER_PAGE) {
                                        hasMoreToLoad(false);
                                    }
                                }
                        )
        );
    }

    // 3. 댓글 삭제
    public void deleteComment(final int position, final int commentId) {

        mCompositeDisposable.add(
                mCommentRepository
                        .deleteComment(commentId)
                        .subscribe(
                                emit -> {
                                    mMyCommentView.showSnackBar("신고된 댓글을 삭제했습니다");
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
                                    if (refuseReport) mMyCommentView.showSnackBar("신고를 취소했습니다");
                                    mMyCommentView.deleteComment(position);
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
    public void onNavigateUpClicked() {
        onBackPressed();
    }

    @Override
    public void onMyCommentLoadMore() {
        if (isLoading) return;
        loadComment(PER_PAGE, currentPage);
    }

    @Override
    public void onReportedCommentLoadMore() {
        if (isLoading) return;
        loadReportedComment(PER_PAGE, currentPage);
    }

    @Override
    public void onDeleteCommentClicked(int position) {
        deleteComment(position, currentlyShownReportedComment.get(position).getCommId());
    }

    @Override
    public void onRefuseReportClicked(int position) {
        deleteReportedComment(position, currentlyShownReportedComment.get(position).getCommId(), true);
    }

    @Override
    public void onReportedCommentClicked(int position) {
        // none
    }

    @Override
    public void onParentItemClick(int groupPosition, int newsId) {
        mMyCommentView.expandGroup(groupPosition);
        mActivityScreenNavigator.toCardNewsDetailActivity(newsId);
    }

    @Override
    public void onChildItemClick(int childPosition, int newsId, String description) {
        mActivityScreenNavigator.toCardNewsCommentActivity(newsId, description);
    }
}