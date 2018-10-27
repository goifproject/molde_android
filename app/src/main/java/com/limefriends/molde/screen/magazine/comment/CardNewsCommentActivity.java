package com.limefriends.molde.screen.magazine.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.screen.common.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class CardNewsCommentActivity extends BaseActivity
        implements CardNewsCommentRecyclerAdapter.OnReportCommentClickListener {

    public static final String REPORT_COMMENT_DIALOG = "REPORT_COMMENT_DIALOG";

    public static void start(Context context, int cardNewsId, String description) {
        Intent intent = new Intent();
        intent.setClass(context, CardNewsCommentActivity.class);
        intent.putExtra("cardNewsId", cardNewsId);
        intent.putExtra("description", description);
        context.startActivity(intent);
    }

    public static final int REPORT_COMMENT_EXIST = 2;
    public static final int REPORT_COMMENT_DONE = 1;

    @BindView(R.id.comment_layout)
    RelativeLayout comment_layout;
    @BindView(R.id.comment_list_view)
    AddOnScrollRecyclerView comment_list_view;
    @BindView(R.id.comment_input)
    EditText comment_input;
    @BindView(R.id.comment_send_button)
    Button comment_send_button;

    private CardNewsCommentRecyclerAdapter cardNewsCommentRecyclerAdapter;
    private MoldeRestfulService.Comment commentService;

    @Service private Repository.Comment mCommentRepository;
    @Service private ToastHelper mToastHelper;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

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

        setContentView(R.layout.activity_cardnews_comment);

        setupViews();

        setupListeners();

        setupCommentList();

        setupData();
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        String description = getIntent().getStringExtra("description");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title
                = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setSingleLine();
        toolbar_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        toolbar_title.setText(description);
    }

    private void setupListeners() {

        comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(comment_input.getWindowToken(), 0);
            }
        });

        comment_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 실제 로그인 상태 확인 구현
                // 로그인 된 상태라면 -> 댓글 등록
                FirebaseAuth auth = ((MoldeApplication) getApplication()).getFireBaseAuth();
                if (auth != null && auth.getUid() != null) {
                    String uId = auth.getCurrentUser().getUid();
                    String name = auth.getCurrentUser().getDisplayName();
                    String content = comment_input.getText().toString();
                    if (content.equals("")) {
                        showSnack("댓글 내용을 입력해주세요");
                        return;
                    }
                    if (name == null) name = auth.getCurrentUser().getEmail();
                    addToComment(uId, name,
                            cardNewsId, content,
                            String.valueOf(System.currentTimeMillis()));
                    comment_input.setText("");
                } else {
                    showSnack("몰디 로그인이 필요합니다!");
                }
            }
        });
    }

    private void setupCommentList() {
        cardNewsCommentRecyclerAdapter
                = new CardNewsCommentRecyclerAdapter(this, this);
        comment_list_view.setAdapter(cardNewsCommentRecyclerAdapter);
        comment_list_view.setLayoutManager(new LinearLayoutManager(this), false);
        comment_list_view.setOnLoadMoreListener(() -> {
            if (isLoading) return;
            loadComment(cardNewsId, PER_PAGE, currentPage);
        });
    }

    public void showSnack(String message) {
        Snackbar.make(comment_layout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    //--------
    // Network
    //--------

    private void setupData() {
        cardNewsId = getIntent().getIntExtra("cardNewsId", 0);
        loadComment(cardNewsId, PER_PAGE, FIRST_PAGE);
    }

    private void loadComment(int cardNewsId, int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        mCompositeDisposable.add(
                mCommentRepository
                        .getNewsComment(cardNewsId, perPage, page)
                        .subscribeWith(getFetchCommentObserver())
        );
    }

    private DisposableObserver<List<CommentEntity>> getFetchCommentObserver() {
        List<CommentEntity> data = new ArrayList<>();
        return new DisposableObserver<List<CommentEntity>>() {
            @Override
            public void onNext(List<CommentEntity> commentEntities) {
                data.addAll(commentEntities);
            }

            @Override
            public void onError(Throwable e) {
                isLoading = false;
            }

            @Override
            public void onComplete() {

                isLoading = false;

                if (data.size() == 0) {
                    setHasMoreToLoad(false);
                    return;
                }

                cardNewsCommentRecyclerAdapter.addData(data);
                // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                currentPage++;

                // 9. 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다.
                if (data.size() < PER_PAGE) {
                    setHasMoreToLoad(false);
                }
            }
        };
    }

    private void addToComment(final String userId, final String userName,
                              final int newsId, final String content, final String regiDate) {

        mCompositeDisposable.add(
                mCommentRepository
                        .createNewComment(userId, userName, newsId, content, regiDate)
                        .subscribe(
                                e -> cardNewsCommentRecyclerAdapter.addData(
                                        new CommentEntity(userId, userName, newsId, content, regiDate)),
                                err -> {},
                                () -> {}
                        )
        );
    }

    public void reportComment(int commentId) {

        FirebaseAuth auth = ((MoldeApplication) getApplication()).getFireBaseAuth();

        if (auth != null) {

            isReporting = true;

            mCompositeDisposable.add(
                    mCommentRepository
                            .reportComment(auth.getUid(), commentId)
                            .subscribe(
                                    emit -> {
                                        switch (emit.getResult()) {
                                            case REPORT_COMMENT_DONE:
                                                showSnack("댓글이 신고되었습니다.");
                                                break;
                                            case REPORT_COMMENT_EXIST:
                                                showSnack("이미 신고한 댓글입니다.");
                                                break;
                                        }
                                    },
                                    err -> {},
                                    () -> isReporting = false

                            )
            );
        } else {
            showSnack("몰디 로그인이 필요합니다!");
        }
    }

    public boolean isReporting() {

        if (isReporting) {
            showSnack("신고중입니다");
            return true;
        }
        return false;
    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    //----------
    // lifecycle
    //----------

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
        setHasMoreToLoad(true);
        currentPage = 0;
    }

    @Override
    public void onReportCommentClicked(int commentId) {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getText(R.string.dialog_report_comment_msg).toString(),
                "",
                getText(R.string.yes).toString(),
                getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                reportComment(commentId);
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, REPORT_COMMENT_DIALOG);
    }
}
