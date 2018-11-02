package com.limefriends.molde.screen.magazine.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.magazine.comment.view.CardNewsCommentView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class CardNewsCommentActivity extends BaseActivity
        implements CardNewsCommentView.Listener {

    public static void start(Context context, int cardNewsId, String description) {
        Intent intent = new Intent();
        intent.setClass(context, CardNewsCommentActivity.class);
        intent.putExtra("cardNewsId", cardNewsId);
        intent.putExtra("description", description);
        context.startActivity(intent);
    }

    public static final int REPORT_COMMENT_EXIST = 2;
    public static final int REPORT_COMMENT_DONE = 1;

    @Service private Repository.Comment mCommentRepository;
    @Service private ToastHelper mToastHelper;
    @Service private ViewFactory mViewFactory;

    private CardNewsCommentView mCardNewsCommentView;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private FirebaseAuth mAuth;

    private static final int PER_PAGE = 20;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isReporting = false;
    private int cardNewsId;
    private boolean isLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mCardNewsCommentView = mViewFactory.newInstance(CardNewsCommentView.class, null);

        setContentView(mCardNewsCommentView.getRootView());

        setupData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = ((MoldeApplication) getApplication()).getFireBaseAuth();
        mCardNewsCommentView.registerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
        setHasMoreToLoad(true);
        currentPage = 0;
        mCardNewsCommentView.unregisterListener(this);
    }

    private void setupData() {
        cardNewsId = getIntent().getIntExtra("cardNewsId", 0);
        loadComment(cardNewsId, PER_PAGE, FIRST_PAGE);
    }

    private void loadComment(int cardNewsId, int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        List<CommentEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mCommentRepository
                        .getNewsComment(cardNewsId, perPage, page)
                        .subscribe(
                                data::addAll,
                                e -> isLoading = false,
                                () -> {
                                    isLoading = false;

                                    if (data.size() == 0) {
                                        setHasMoreToLoad(false);
                                        return;
                                    }
                                    mCardNewsCommentView.bindComments(data);
                                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                                    currentPage++;

                                    // 9. 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다.
                                    if (data.size() < PER_PAGE) {
                                        setHasMoreToLoad(false);
                                    }
                                }
                        )
        );
    }

    private void sendComment(String content) {

        if (mAuth != null) {
            String userId = mAuth.getUid();
            String userName = "";
            if (mAuth.getCurrentUser().getDisplayName() != null) {
                userName = mAuth.getCurrentUser().getDisplayName();
            } else if (mAuth.getCurrentUser().getEmail() != null) {
                userName = mAuth.getCurrentUser().getEmail();
            } else {
                userName = userId;
            }

            CommentEntity commentEntity = new CommentEntity(userId, userName, cardNewsId, content);

            mCompositeDisposable.add(
                    mCommentRepository
                            .createNewComment(userId, userName, cardNewsId, content)
                            .subscribe(
                                    e -> mCardNewsCommentView.bindComment(commentEntity),
                                    err -> { },
                                    () -> { }
                            )
            );
        } else {
            mCardNewsCommentView.showSnackBar("몰디 로그인이 필요합니다!");
        }
    }

    public void reportComment(int commentId) {

        if (mAuth != null) {

            if (isReporting) {
                mCardNewsCommentView.showSnackBar("신고중입니다");
                return;
            } else {
                isReporting = true;
            }

            mCompositeDisposable.add(
                    mCommentRepository
                            .reportComment(mAuth.getUid(), commentId)
                            .subscribe(
                                    emit -> {
                                        switch (emit.getResult()) {
                                            case REPORT_COMMENT_DONE:
                                                mCardNewsCommentView.showSnackBar("댓글이 신고되었습니다.");
                                                break;
                                            case REPORT_COMMENT_EXIST:
                                                mCardNewsCommentView.showSnackBar("이미 신고한 댓글입니다.");
                                                break;
                                        }
                                    },
                                    err -> isReporting = false,
                                    () -> isReporting = false

                            )
            );
        } else {
            mCardNewsCommentView.showSnackBar("몰디 로그인이 필요합니다!");
        }
    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void onNavigateUpClicked() {
        onBackPressed();
    }

    @Override
    public void onReportCommentClicked(int commentId) {
        reportComment(commentId);
    }

    @Override
    public void onSendCommentClicked(String comment) {
        sendComment(comment);
    }

    @Override
    public void onLoadMore() {
        loadComment(cardNewsId, PER_PAGE, currentPage);
    }

}
