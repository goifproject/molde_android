package com.limefriends.molde.ui.menu_magazine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.news.MoldeCardNewsEntity;
import com.limefriends.molde.ui.menu_magazine.detail.MagazineCardnewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MagazineCardNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    //    private List<CardNewsEntity> cardNewsList;
    private static final int VIEW_PROG = 0;
    private static final int VIEW_ITEM = 1;

    private List<MoldeCardNewsEntity> entities = new ArrayList<>();

    public MagazineCardNewsAdapter(Context context) {
        this.context = context;
        // this.cardNewsList = cardNewsList;
    }

    public void addData(List<MoldeCardNewsEntity> newEntities) {
        entities.addAll(newEntities);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return entities.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new MagazineCardNewsHolder(LayoutInflater.from(context).inflate(R.layout.magazine_cardnews_item, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(context).inflate(R.layout.feed_loading_progress, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MagazineCardNewsHolder) {
            MagazineCardNewsHolder holder = (MagazineCardNewsHolder) viewHolder;
            final MoldeCardNewsEntity cardNewsEntity = entities.get(position);
            if (cardNewsEntity.getNewsImg().size() != 0) {
                Glide.with(context).load(cardNewsEntity.getNewsImg().get(0).getUrl()).into(holder.cardnews_thumbnail);
            } else {
                Glide.with(context).load(R.drawable.img_cardnews_dummy).into(holder.cardnews_thumbnail);
            }
            holder.cardnews_title.setText(cardNewsEntity.getDescription());
            holder.cardnews_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 인터페이싱
                    Intent intent = new Intent(context, MagazineCardnewsDetailActivity.class);
                    intent.putExtra("cardNewsId", cardNewsEntity.getNewsId());
                    context.startActivity(intent);
                }
            });
        }
    }

//    @Override
//    public void onBindViewHolder(MagazineCardNewsHolder holder, int position) {
//        MoldeCardNewsEntity cardNewsEntity = entities.get(position);
//        if (cardNewsEntity.getNewsImg().size() != 0) {
//            Glide.with(context).load(cardNewsEntity.getNewsImg().get(0).getUrl()).into(holder.cardnews_thumbnail);
//        } else {
//            Glide.with(context).load(R.drawable.img_cardnews_dummy).into(holder.cardnews_thumbnail);
//        }
//        holder.cardnews_title.setText(cardNewsEntity.getDescription());
//        holder.cardnews_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, MagazineCardnewsDetailActivity.class);
//                context.startActivity(intent);
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

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

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressBar feedLoadingBar;

        public ProgressViewHolder(View v) {
            super(v);
            feedLoadingBar = v.findViewById(R.id.feed_loading_bar);
        }

    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
            entities.add(null);
            notifyItemInserted(entities.size() - 1);
//                }
//            });
        } else {
            entities.remove(entities.size() - 1);
            notifyItemRemoved(entities.size());
        }
    }

}
