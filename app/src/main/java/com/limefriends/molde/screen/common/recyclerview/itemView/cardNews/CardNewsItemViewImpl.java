package com.limefriends.molde.screen.common.recyclerview.itemView.cardNews;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class CardNewsItemViewImpl
        extends BaseObservableView<CardNewsItemView.Listener> implements CardNewsItemView {

    private CardView cardnews_layout;
    private ImageView cardnews_thumbnail;
    private TextView cardnews_title;

    private ImageLoader mImageLoader;
    private int cardNewsId;

    public CardNewsItemViewImpl(LayoutInflater inflater,
                                ViewGroup parent,
                                ImageLoader imageLoader) {
        setRootView(inflater.inflate(R.layout.item_cardnews, parent, false));

        this.mImageLoader = imageLoader;

        setupViews();
    }

    private void setupViews() {
        cardnews_layout = findViewById(R.id.cardnews_layout);
        cardnews_thumbnail = findViewById(R.id.cardnews_thumbnail);
        cardnews_title = findViewById(R.id.cardnews_title);

        setupListener();
    }

    private void setupListener() {
        cardnews_layout.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(cardNewsId);
            }
        });
    }


    @Override
    public void bindCardNews(CardNewsEntity cardNewsEntity) {

        if (cardNewsEntity.getNewsImg() != null) {
            mImageLoader.load(
                    cardNewsEntity.getNewsImg().get(0).getUrl(),
                    R.drawable.img_placeholder_magazine,
                    cardnews_thumbnail);
        } else {
            mImageLoader.load(
                    R.drawable.img_placeholder_magazine,
                    cardnews_thumbnail
            );
        }
        cardnews_title.setText(cardNewsEntity.getDescription());
        cardNewsId = cardNewsEntity.getNewsId();
    }
}

