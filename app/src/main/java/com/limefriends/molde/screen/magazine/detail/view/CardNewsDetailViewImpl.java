package com.limefriends.molde.screen.magazine.detail.view;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.cardNews.CardNewsImageEntity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.imagePager.ImagePagerAdapter;

import java.util.List;

public class CardNewsDetailViewImpl
        extends BaseObservableView<CardNewsDetailView.Listener> implements CardNewsDetailView {

    private RelativeLayout cardnews_detail_layout;
    private ViewPager cardnews_pager;
    private ImageView cardnews_comment;
    private ImageView cardnews_scrap;
    private ImageView cardnews_share;
    private TextView current_page_no;
    private TextView total_page_no;
    private TextView cardnews_description;
    private ProgressBar cardnews_progress;

    private ImagePagerAdapter<CardNewsImageEntity> imagePagerAdapter;

    private ViewFactory mViewFactory;
    private DialogFactory mDialogFactory;
    private DialogManager mDialogManager;
    private ToastHelper mToastHelper;

    private static final String DELETE_SCRAP_DIALOG_TAG = "DELETE_SCRAP_DIALOG_TAG";

    public CardNewsDetailViewImpl(LayoutInflater inflater,
                                  ViewGroup parent,
                                  ViewFactory viewFactory,
                                  ToastHelper toastHelper,
                                  DialogFactory dialogFactory,
                                  DialogManager dialogManager) {

        setRootView(inflater.inflate(R.layout.activity_cardnews_detail, parent, false));

        this.mViewFactory = viewFactory;
        this.mToastHelper = toastHelper;
        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;

        setupViews();
    }

    /**
     * setup listeners
     */

    private void setupViews() {
        cardnews_detail_layout = findViewById(R.id.cardnews_detail_layout);
        cardnews_pager = findViewById(R.id.cardnews_pager);
        cardnews_comment = findViewById(R.id.cardnews_comment);
        cardnews_scrap = findViewById(R.id.cardnews_scrap);
        cardnews_share = findViewById(R.id.cardnews_share);
        current_page_no = findViewById(R.id.current_page_no);
        total_page_no = findViewById(R.id.total_page_no);
        cardnews_description = findViewById(R.id.cardnews_description);
        cardnews_progress = findViewById(R.id.cardnews_progress);

        setupListeners();

        setupCardNewsImagePager();
    }

    private void setupListeners() {

        cardnews_comment.setOnClickListener(v -> {
            String description = cardnews_description.getText().toString();
            for (Listener listener : getListeners()) {
                listener.onCommentIconClicked(description);
            }
        });

        cardnews_scrap.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onScrapClicked();
            }
        });

        cardnews_share.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onShareClicked();
            }
        });

        cardnews_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_page_no.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupCardNewsImagePager() {
        imagePagerAdapter = new ImagePagerAdapter(mViewFactory);
        cardnews_pager.setAdapter(imagePagerAdapter);
    }

    /**
     * change view indication
     */

    @Override
    public void showPromptDialog() {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.scrap_delete_message).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onDeleteScrapClicked();
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, DELETE_SCRAP_DIALOG_TAG);
    }

    @Override
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(cardnews_detail_layout, message);
    }

    @Override
    public void showProgressIndication() {
        cardnews_progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressIndication() {
        cardnews_progress.setVisibility(View.GONE);
    }

    /**
     * bind data
     */

    @Override
    public void bindCardNewsDescription(String description) {
        cardnews_description.setText(description);
    }

    @Override
    public void bindCardNewsImages(List<CardNewsImageEntity> images) {
        imagePagerAdapter.setData(images);
    }

    @Override
    public void setIsScrap() {
        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_on);
    }

    @Override
    public void unsetScrap() {
        cardnews_scrap.setImageResource(R.drawable.ic_news_scrap_off);

    }

    @Override
    public void bindPageCount(int size) {
        total_page_no.setText(String.valueOf(size));
    }

}
