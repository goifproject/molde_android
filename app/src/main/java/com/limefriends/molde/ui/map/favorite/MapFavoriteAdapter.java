package com.limefriends.molde.ui.map.favorite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.entity.favorite.FavoriteEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFavoriteAdapter extends RecyclerView.Adapter<MapFavoriteAdapter.MyFavoriteViewHolder> {

    private Context context;
    private List<FavoriteEntity> moldeMyFavoriteList = new ArrayList<>();
    private MyFavoriteAdapterCallBack myFavoriteAdapterCallBack;
    private int currentPosition;

    public interface MyFavoriteAdapterCallBack {
        void applyMyFavoriteMapInfo(FavoriteEntity entity);

        void onUnSelected(int favId);
    }

    MapFavoriteAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<FavoriteEntity> data) {
        this.moldeMyFavoriteList = data;
        notifyDataSetChanged();
    }

    public void notifyFavoriteRemoved() {
        moldeMyFavoriteList.remove(currentPosition);
        notifyDataSetChanged();
    }

    @Override
    public MyFavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new MyFavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyFavoriteViewHolder viewHolder, int position) {
        viewHolder.my_favorite_view_title.setText(moldeMyFavoriteList.get(position).getFavName());
        viewHolder.my_favorite_view_info.setText(moldeMyFavoriteList.get(position).getFavAddr());
        viewHolder.position = position;
    }


    @Override
    public int getItemCount() {
        return moldeMyFavoriteList.size();
    }

    public void setMoldeMyFavoriteAdapterCallBack(MyFavoriteAdapterCallBack myFavoriteAdapterCallBack) {
        this.myFavoriteAdapterCallBack = myFavoriteAdapterCallBack;
    }

    class MyFavoriteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.my_favorite_layout)
        RelativeLayout my_favorite_layout;
        @BindView(R.id.my_favorite_view_new)
        ImageView my_favorite_view_new;
        @BindView(R.id.my_favorite_view_title)
        TextView my_favorite_view_title;
        @BindView(R.id.my_favorite_view_info)
        TextView my_favorite_view_info;
        @BindView(R.id.my_favorite_star)
        ImageView my_favorite_star;

        int position;

        MyFavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {

            my_favorite_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFavoriteAdapterCallBack
                            .applyMyFavoriteMapInfo(moldeMyFavoriteList.get(position));
                }
            });

            my_favorite_star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int favId = moldeMyFavoriteList.get(position).getFavId();
                    currentPosition = position;
                    myFavoriteAdapterCallBack.onUnSelected(favId);
                }
            });
        }
    }

}
