package com.limefriends.molde.menu_map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.entity.MoldeMyFavoriteEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMyFavoriteRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<MoldeMyFavoriteEntity> moldeMyFavoriteList;


    public MoldeMyFavoriteRecyclerAdapter(Context context, ArrayList<MoldeMyFavoriteEntity> moldeMyFavoriteList) {
        this.context = context;
        this.moldeMyFavoriteList = moldeMyFavoriteList;
    }

    public static class MyFavoriteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.my_favorite_view_content)
        RelativeLayout my_favorite_view_content;
        @BindView(R.id.my_favorite_view_new)
        ImageView my_favorite_view_new;
        @BindView(R.id.my_favorite_view_title)
        TextView my_favorite_view_title;
        @BindView(R.id.my_favorite_view_info)
        TextView my_favorite_view_info;
        @BindView(R.id.my_favorite_view_toggle)
        ToggleButton my_favorite_view_toggle;

        public MyFavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_list_item_my_favorite_info, parent, false);
        return new MyFavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MyFavoriteViewHolder){
            final MyFavoriteViewHolder viewHolder = (MyFavoriteViewHolder) holder;
            viewHolder.my_favorite_view_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("TAG", position + 1 + "번 째 눌림");
                }
            });
            viewHolder.my_favorite_view_title.setText(moldeMyFavoriteList.get(position).getMyFavoriteTitle());
            viewHolder.my_favorite_view_info.setText(moldeMyFavoriteList.get(position).getMyFavoriteInfo());
            viewHolder.my_favorite_view_toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolder.my_favorite_view_toggle.isChecked() == false){
                        viewHolder.my_favorite_view_toggle.setBackgroundResource(R.drawable.ic_star_off);
                        moldeMyFavoriteList.get(position).setMyFavoriteToggle(false);
                    }else if(viewHolder.my_favorite_view_toggle.isChecked() == true){
                        viewHolder.my_favorite_view_toggle.setBackgroundResource(R.drawable.ic_star_on);
                        moldeMyFavoriteList.get(position).setMyFavoriteToggle(true);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return moldeMyFavoriteList.size();
    }
}
