package com.limefriends.molde.ui.magazine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.ui.magazine.detail.CardNewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardNewsAdapter extends RecyclerView.Adapter<CardNewsAdapter.MagazineCardNewsHolder> {

    private Context context;

    private List<CardNewsEntity> entities = new ArrayList<>();

    CardNewsAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<CardNewsEntity> newEntities) {
        entities.addAll(newEntities);
        notifyDataSetChanged();
    }

    @Override
    public MagazineCardNewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MagazineCardNewsHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_cardnews, parent, false));
    }

    @Override
    public void onBindViewHolder(MagazineCardNewsHolder holder, int position) {
        CardNewsEntity cardNewsEntity = entities.get(position);
        if (cardNewsEntity.getNewsImg().size() != 0) {
            Glide.with(context).load(cardNewsEntity.getNewsImg().get(0).getUrl())
                    .placeholder(R.drawable.img_placeholder_magazine).into(holder.cardnews_thumbnail);
        } else {
            Glide.with(context).load(R.drawable.img_placeholder_magazine).into(holder.cardnews_thumbnail);
        }
        holder.cardnews_title.setText(cardNewsEntity.getDescription());
        holder.cardNewsId = cardNewsEntity.getNewsId();
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    class MagazineCardNewsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardnews_layout)
        CardView cardnews_layout;
        @BindView(R.id.cardnews_thumbnail)
        ImageView cardnews_thumbnail;
        @BindView(R.id.cardnews_title)
        TextView cardnews_title;

        int cardNewsId;

        MagazineCardNewsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {
            cardnews_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CardNewsDetailActivity.class);
                    intent.putExtra("cardNewsId", cardNewsId);
                    context.startActivity(intent);
                }
            });
        }

    }

}
