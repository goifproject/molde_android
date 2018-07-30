package com.limefriends.molde.menu_magazine.cardnews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_magazine.entity.CardNewsEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class MagazineCardNewsHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cardnews_layout)
    RelativeLayout cardnews_layout;
    @BindView(R.id.cardnews_thumbnail)
    ImageView cardnews_thumbnail;
    @BindView(R.id.cardnews_title)
    TextView cardnews_title;

    public MagazineCardNewsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
public class MagazineCardNewsAdapter extends RecyclerView.Adapter<MagazineCardNewsHolder> {
    private Context context;
    private List<CardNewsEntity> cardNewsList;

    public MagazineCardNewsAdapter(Context context, List<CardNewsEntity> cardNewsList) {
        this.context = context;
        this.cardNewsList = cardNewsList;
    }

    @Override
    public MagazineCardNewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MagazineCardNewsHolder(LayoutInflater.from(context).inflate(R.layout.magazine_cardnews_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MagazineCardNewsHolder holder, int position) {
        CardNewsEntity cardNewsEntity = cardNewsList.get(position);
        Glide.with(context).load(cardNewsEntity.getThumbnail()).into(holder.cardnews_thumbnail);
        holder.cardnews_title.setText(cardNewsEntity.getTitle());
        holder.cardnews_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MagazineCardnewsDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardNewsList.size();
    }
}
