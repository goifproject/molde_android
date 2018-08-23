package com.limefriends.molde.ui.feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FeedImageAdapter extends PagerAdapter{

    private Context context;
    private List<String> reportImageLinkList = new ArrayList<>();

    public FeedImageAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> imgUrl) {
        this.reportImageLinkList = imgUrl;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reportImageLinkList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mypage_my_report_detail_image_item, null);
        ImageView mypage_detail_report_image = view.findViewById(R.id.mypage_detail_report_image);
        Glide.with(context).load(reportImageLinkList.get(position)).into(mypage_detail_report_image);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

}
