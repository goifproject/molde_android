package com.limefriends.molde.screen.common.recyclerview.itemView.favorite;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class FavoriteItemViewImpl
        extends BaseObservableView<FavoriteItemView.Listener> implements FavoriteItemView {

    public static final String DELETE_FAVORITE_DIALOG = "DELETE_FAVORITE_DIALOG";

    private RelativeLayout my_favorite_layout;
    private TextView my_favorite_view_title;
    private TextView my_favorite_view_info;
    private ImageView my_favorite_star;

    private DialogFactory mDialogFactory;
    private DialogManager mDialogManager;

    private FavoriteEntity mFavoriteEntity;

    public FavoriteItemViewImpl(LayoutInflater inflater,
                                ViewGroup parent,
                                DialogFactory dialogFactory,
                                DialogManager dialogManager) {
        setRootView(inflater.inflate(R.layout.item_favorite, parent, false));

        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;

        setupViews();

        setupListener();
    }

    private void setupViews() {
        my_favorite_layout = findViewById(R.id.my_favorite_layout);
        my_favorite_view_title = findViewById(R.id.my_favorite_view_title);
        my_favorite_view_info = findViewById(R.id.my_favorite_view_info);
        my_favorite_star = findViewById(R.id.my_favorite_star);
    }

    private void setupListener() {

        my_favorite_layout.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(mFavoriteEntity.getFavId());
            }
        });

        my_favorite_star.setOnClickListener(v -> showDeleteDialog());
    }

    private void showDeleteDialog() {

        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.dialog_delete_favorite_message).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onItem2Clicked(mFavoriteEntity.getFavId());
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, DELETE_FAVORITE_DIALOG);
    }

    @Override
    public void bindFavorite(FavoriteEntity entity) {
        this.mFavoriteEntity = entity;
        my_favorite_view_title.setText(entity.getFavName());
        my_favorite_view_info.setText(entity.getFavAddr());
    }
}
