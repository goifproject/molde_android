package com.limefriends.molde.menu_magazine;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cardnews_wrapper)
    RelativeLayout cardnews_wrapper;

    @BindView(R.id.img_cardnews_thumbnail)
    ImageView img_thumbnail;

    @BindView(R.id.cardnews_title)
    TextView title;


    public ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


}


public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener  {

    private Context context;
    private int resourceId;
    private List<CardnewsListElement> dataList;


    // resourceId == R.layout.item_cardnews_recycler
    // 즉 하나의 단위가 되는 layout 의미
    public RecyclerAdapter(Context context, int resourceId, List<CardnewsListElement> dataList) {
        this.context = context;
        this.resourceId = resourceId;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(resourceId, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardnewsListElement cardnewsListElement = dataList.get(position);

        Glide.with(context).load(cardnewsListElement.getThumbnail()).into(holder.img_thumbnail);
        holder.title.setText(cardnewsListElement.getTitle());
        holder.cardnews_wrapper.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "recyclerview clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(v.getContext(), MagazineCardnewsDetailActivity.class);
        v.getContext().startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
