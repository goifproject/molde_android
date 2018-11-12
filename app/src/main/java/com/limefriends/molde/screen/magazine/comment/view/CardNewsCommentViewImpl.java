package com.limefriends.molde.screen.magazine.comment.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import java.util.List;

public class CardNewsCommentViewImpl
        extends BaseObservableView<CardNewsCommentView.Listener>
        implements CardNewsCommentView, RecyclerViewAdapter.OnItemClickListener {

    public static final String REPORT_COMMENT_DIALOG = "REPORT_COMMENT_DIALOG";

    private RelativeLayout comment_layout;
    private AddOnScrollRecyclerView comment_list_view;
    private EditText comment_input;
    private Button comment_send_button;
    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;

    private ViewFactory mViewFactory;
    private ToastHelper mToastHelper;
    private DialogFactory mDialogFactory;
    private DialogManager mDialogManager;
    private RecyclerViewAdapter<CommentEntity> mCommentAdapter;

    public CardNewsCommentViewImpl(LayoutInflater inflater,
                                   ViewGroup parent,
                                   ViewFactory viewFactory,
                                   ToastHelper toastHelper,
                                   DialogFactory dialogFactory,
                                   DialogManager dialogManager) {
        setRootView(inflater.inflate(R.layout.activity_cardnews_comment, parent, false));

        this.mViewFactory = viewFactory;
        this.mToastHelper = toastHelper;
        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;

        setupViews();
    }

    private void setupViews() {

        comment_layout = findViewById(R.id.comment_layout);
        comment_list_view = findViewById(R.id.comment_list_view);
        comment_input = findViewById(R.id.comment_input);
        comment_send_button = findViewById(R.id.comment_send_button);
        mToolbar = findViewById(R.id.toolbar);

        setupToolbar();

        setupListeners();

        setupCommentList();
    }

    private void setupToolbar() {
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
    }

    private void setupListeners() {

        comment_layout.setOnClickListener(v -> hideSoftKeyboard());

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        comment_list_view.setOnLoadMoreListener(() -> {
            for (Listener listener : getListeners()) {
                listener.onLoadMore();
            }
        });

        comment_send_button.setOnClickListener(v -> {

            if (comment_input.getText().toString().equals("")) {
                showSnackBar("댓글을 입력해주세요");
                return;
            }

            for (Listener listener : getListeners()) {
                listener.onSendCommentClicked(comment_input.getText().toString());
            }
        });
    }

    private void setupCommentList() {
        mCommentAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.CARDNEWS_COMMENT);
        mCommentAdapter.setOnItemClickListener(this);
        comment_list_view.setAdapter(mCommentAdapter);
        comment_list_view.setLayoutManager(new LinearLayoutManager(getContext()), false);
    }

    @Override
    public void onItemClicked(int commentId) {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.dialog_report_comment_msg).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onReportCommentClicked(commentId);
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, REPORT_COMMENT_DIALOG);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(comment_input.getWindowToken(), 0);
    }

    @Override
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(comment_layout, message);
    }

    @Override
    public void bindComment(CommentEntity entity) {
        mCommentAdapter.addData(entity);
    }

    @Override
    public void bindComments(List<CommentEntity> entityList) {
        mCommentAdapter.addData(entityList);
    }
}
