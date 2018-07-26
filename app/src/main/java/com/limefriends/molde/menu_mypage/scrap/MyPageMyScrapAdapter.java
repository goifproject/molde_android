package com.limefriends.molde.menu_mypage.scrap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_mypage.entity.MyPageMyScrapEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-05-19.
 */

public class MyPageMyScrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int resourceId;
    private List<MyPageMyScrapEntity> myPageMyScrapEntityList;

    public MyPageMyScrapAdapter(Context context, int resourceId, List<MyPageMyScrapEntity> myPageMyScrapEntityList) {
        this.context = context;
        this.resourceId = resourceId;
        this.myPageMyScrapEntityList = myPageMyScrapEntityList;
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

    @Override
    public MyPageMyScrapAdapter.MyPageMyScrapViewHorder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mypage_my_scrap_item, parent, false);
        return new MyPageMyScrapAdapter.MyPageMyScrapViewHorder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyPageMyScrapAdapter.MyPageMyScrapViewHorder viewHorder = (MyPageMyScrapAdapter.MyPageMyScrapViewHorder) holder;
        Glide.with(context).load(myPageMyScrapEntityList.get(position).getMyScrap_image()).into(viewHorder.myScrap_image);
        viewHorder.myScrap_text.setText(myPageMyScrapEntityList.get(position).getMyScrap_text());
    }

    @Override
    public int getItemCount() {
        return myPageMyScrapEntityList.size();
    }
}
