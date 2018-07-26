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
import com.limefriends.molde.menu_magazine.entity.CardnewsEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class CardNewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cardnews_wrapper)
    RelativeLayout cardnews_wrapper;

    @BindView(R.id.img_cardnews_thumbnail)
    ImageView img_thumbnail;

    @BindView(R.id.cardnews_title)
    TextView title;


    public CardNewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


}


public class CardNewsAdapter extends RecyclerView.Adapter<CardNewHolder> implements View.OnClickListener  {

    private Context context;
    private int resourceId;
    private List<CardnewsEntity> dataList;


    // resourceId == R.layout.item_cardnews_recycler
    // 즉 하나의 단위가 되는 layout 의미
    public CardNewsAdapter(Context context, int resourceId, List<CardnewsEntity> dataList) {
        this.context = context;
        this.resourceId = resourceId;
        this.dataList = dataList;
    }

    @Override
    public CardNewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardNewHolder(LayoutInflater.from(context).inflate(resourceId, parent, false));
    }

    @Override
    public void onBindViewHolder(CardNewHolder holder, int position) {
        CardnewsEntity cardnewsEntity = dataList.get(position);

        Glide.with(context).load(cardnewsEntity.getThumbnail()).into(holder.img_thumbnail);
//        Glide.with(context).load(cardnewsEntity.getThumbnail())
//                .bitmapTransform(new CropCircleTransformation(context))
//                .into(holder.img_thumbnail);
        holder.title.setText(cardnewsEntity.getTitle());
        holder.cardnews_wrapper.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(v.getContext(), CardnewsDetailActivity.class);
        v.getContext().startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
