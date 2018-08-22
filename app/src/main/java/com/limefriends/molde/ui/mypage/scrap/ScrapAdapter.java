package com.limefriends.molde.ui.mypage.scrap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.scrap.ScrapEntity;
import com.limefriends.molde.ui.magazine.detail.CardNewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-05-19.
 */

public class ScrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int resourceId;
    // private List<ScrapEntity> myPageMyScrapEntityList;

    private List<CardNewsEntity> entities = new ArrayList<>();

    public ScrapAdapter(Context context, int resourceId, List<ScrapEntity> scrapEntityList) {
        this.context = context;
        this.resourceId = resourceId;
        // this.scrapEntityList = scrapEntityList;
    }

    public class MyPageMyScrapViewHorder extends RecyclerView.ViewHolder {

        @BindView(R.id.myScrap_image)
        ImageView myScrap_image;

        @BindView(R.id.myScrap_text)
        TextView myScrap_text;

        public MyPageMyScrapViewHorder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<CardNewsEntity> entities) {
        this.entities = entities;
        notifyDataSetChanged();
    }

    @Override
    public ScrapAdapter.MyPageMyScrapViewHorder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mypage_my_scrap_item, parent, false);
        return new ScrapAdapter.MyPageMyScrapViewHorder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ScrapAdapter.MyPageMyScrapViewHorder viewHorder = (ScrapAdapter.MyPageMyScrapViewHorder) holder;
        if (entities.get(position).getNewsImg() != null && entities.get(position).getNewsImg().size() != 0) {
            Glide.with(context).load(entities.get(position).getNewsImg().get(0).getUrl()).into(viewHorder.myScrap_image);
        }
        viewHorder.myScrap_text.setText(entities.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 분리
                Intent intent = new Intent(context, CardNewsDetailActivity.class);
                intent.putExtra("cardNewsId", entities.get(position).getNewsId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }
}
