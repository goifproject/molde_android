package com.limefriends.molde.ui.mypage.scrap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.news.CardNewsEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-05-19.
 */

public class ScrapAdapter extends RecyclerView.Adapter<ScrapAdapter.MyPageMyScrapViewHolder> {

    private ScrapActivity view;
    private List<CardNewsEntity> cardNewsEntities = new ArrayList<>();

    public ScrapAdapter(ScrapActivity view) {
        this.view = view;
    }

    public void setData(List<CardNewsEntity> entities) {
        this.cardNewsEntities = entities;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        cardNewsEntities.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public MyPageMyScrapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardnews, parent, false);
        return new ScrapAdapter.MyPageMyScrapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPageMyScrapViewHolder viewHolder, int position) {

        CardNewsEntity cardNews = cardNewsEntities.get(position);

        if (cardNews.getNewsImg() != null && cardNews.getNewsImg().size() != 0) {
            Glide.with(view)
                    .load(cardNews.getNewsImg().get(0).getUrl())
                    .placeholder(R.drawable.img_placeholder_magazine)
                    .into(viewHolder.cardnews_thumbnail);
        } else {
            Glide.with(view)
                    .load(R.drawable.img_placeholder_magazine)
                    .into(viewHolder.cardnews_thumbnail);
        }
        viewHolder.cardnews_title.setText(cardNews.getDescription());
        viewHolder.newsId = cardNews.getNewsId();
        viewHolder.position = position;
    }

    @Override
    public int getItemCount() {
        return cardNewsEntities.size();
    }

    class MyPageMyScrapViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardnews_thumbnail)
        ImageView cardnews_thumbnail;
        @BindView(R.id.cardnews_title)
        TextView cardnews_title;

        int newsId;
        int position;

        MyPageMyScrapViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.onCardNewsSelected(newsId, position);
                }
            });
        }

    }
}