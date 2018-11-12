package com.limefriends.molde.screen.mypage.comment.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler.AddOnScrollExpandableListView;
import com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.mypage.comment.MyCommentExpandableAdapter;

import java.util.List;


import static com.limefriends.molde.screen.mypage.comment.MyCommentActivity.DELETE_COMMENT_DIALOG;

public class MyCommentViewImpl
        extends BaseObservableView<MyCommentView.Listener>
        implements MyCommentView, RecyclerViewAdapter.OnItemClickListener,
        RecyclerViewAdapter.OnItem2ClickListener, MyCommentExpandableAdapter.OnItemClickCallback {

    private RelativeLayout myComment_container;
    private AddOnScrollExpandableListView myComment_listView;
    private AddOnScrollRecyclerView myComment_reported_comment_listview;
    private ProgressBar progressBar;

    private DialogManager mDialogManager;
    private DialogFactory mDialogFactory;
    private ViewFactory mViewFactory;

    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;
    private RecyclerViewAdapter<CommentEntity> mCommentAdapter;
    private MyCommentExpandableAdapter commentExpandableAdapter;
    private ToastHelper mToastHelper;

    public MyCommentViewImpl(LayoutInflater inflater,
                             ViewGroup parent,
                             ViewFactory viewFactory,
                             DialogFactory dialogFactory,
                             DialogManager dialogManager,
                             ToastHelper toastHelper) {

        this.mDialogManager = dialogManager;
        this.mDialogFactory = dialogFactory;
        this.mViewFactory = viewFactory;
        this.mToastHelper = toastHelper;

        setRootView(inflater.inflate(R.layout.activity_my_comment, parent,false));

        setupViews();

        setupToolbar();

        setupListener();

        setupCommentRecyclerView();

        setupReportedCommentRecyclerView();
    }

    private void setupViews() {
        myComment_container = findViewById(R.id.myComment_container);
        myComment_listView = findViewById(R.id.myComment_listView);
        myComment_reported_comment_listview = findViewById(R.id.myComment_reported_comment_listview);
        progressBar = findViewById(R.id.progressBar2);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
        mNestedToolbar.setTitle(getContext().getText(R.string.mycomment).toString());
    }

    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        myComment_reported_comment_listview.setOnLoadMoreListener(() -> {
            for (Listener listener : getListeners()) {
                listener.onReportedCommentLoadMore();
            }
        });

        myComment_listView.setOnLoadMoreListener(() -> {
            for (Listener listener : getListeners()) {
                listener.onMyCommentLoadMore();
            }
        });
    }

    private void setupCommentRecyclerView() {
        commentExpandableAdapter = new MyCommentExpandableAdapter(this);
        myComment_listView.setAdapter(commentExpandableAdapter);
        myComment_listView.setGroupIndicator(null);
        myComment_listView.setChildIndicator(null);
        myComment_listView.setChildDivider(getContext().getResources().getDrawable(R.color.white));
        myComment_listView.setDivider(getContext().getResources().getDrawable(R.color.white));
        myComment_listView.setDividerHeight(0);
    }

    private void setupReportedCommentRecyclerView() {
        mCommentAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.CARDNEWS_COMMENT);
        myComment_reported_comment_listview.setAdapter(mCommentAdapter);
        myComment_reported_comment_listview.setLayoutManager(
                new LinearLayoutManager(getContext()), false);
        mCommentAdapter.setOnItemClickListener(this);
        mCommentAdapter.setOnItem2ClickListener(this);
    }

    public void showDeleteCommentDialog(int position) {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.dialog_msg_delete_comment).toString(),
                "",
                getContext().getText(R.string.refuse_report).toString(),
                getContext().getText(R.string.delete_comment).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onDeleteCommentClicked(position);
                }
            }

            @Override
            public void onNegativeButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onRefuseReportClicked(position);
                }
            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, DELETE_COMMENT_DIALOG);
    }

    @Override
    public void bindComments(List<CardNewsEntity> myComments) {
        commentExpandableAdapter.addAll(myComments);
    }

    @Override
    public void bindReportedComments(List<CommentEntity> reportedComments) {
        myComment_reported_comment_listview.setVisibility(View.VISIBLE);
        myComment_listView.setVisibility(View.INVISIBLE);
        mCommentAdapter.addData(reportedComments);
    }

    @Override
    public void onItemClicked(int position) {
        for (Listener listener : getListeners()) {
            listener.onReportedCommentClicked(position);
        }
    }

    @Override
    public void onItem2Clicked(int position) {
        showDeleteCommentDialog(position);
    }

    @Override
    public void showProgressIndication() {
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressIndication() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void expandMyCommentList(int count) {
        for (int i = 0; i < count; i++) {
            myComment_listView.expandGroup(i);
        }
    }

    @Override
    public void expandGroup(int groupPosition) {
        myComment_listView.expandGroup(groupPosition);
    }

    @Override
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(myComment_container, message);
    }

    @Override
    public void deleteComment(int position) {
        mCommentAdapter.removeData(position);
    }

    @Override
    public void onParentItemClick(int groupPosition, int newsId) {
        for (Listener listener : getListeners()) {
            listener.onParentItemClick(groupPosition, newsId);
        }
    }

    @Override
    public void onChildItemClick(int childPosition, int newsId, String description) {
        for (Listener listener : getListeners()) {
            listener.onChildItemClick(childPosition, newsId, description);
        }
    }
}
