package com.limefriends.molde.menu_map.reportCard;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.MoldeMapFragment;
import com.limefriends.molde.menu_map.MoldeMapReportPagerAdapterCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportCardPagerAdapter extends PagerAdapter implements ReportCardAdapter {
    private List<CardView> reportCardViewList;
    private List<ReportCardItem> reportCardDataList;
    private MoldeMapReportPagerAdapterCallback callback;
    private float mBaseElevation;
    private Context context;

    public ReportCardPagerAdapter(Context context) {
        reportCardViewList = new ArrayList<CardView>();
        reportCardDataList = new ArrayList<ReportCardItem>();
        this.context = context;
    }

    public void addCardItem(ReportCardItem item) {
        reportCardViewList.add(null);
        reportCardDataList.add(item);
    }

    public void removeAllCardItem(){
        reportCardDataList.clear();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        if(callback != null){
            callback.applyReportCardInfo(position);
        }
        return reportCardViewList.get(position);
    }

    @Override
    public int getCount() {
        return reportCardDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.map_report_card, container, false);
        notifyDataSetChanged();
        container.addView(view);
        bind(reportCardDataList.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, position + 1 + "번째 컨테이너 눌림", Toast.LENGTH_SHORT).show();
            }
        });
        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        reportCardViewList.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        reportCardViewList.set(position, null);
    }

    private void bind(ReportCardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }

    public void setCallback(MoldeMapReportPagerAdapterCallback callback) {
        this.callback = callback;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
