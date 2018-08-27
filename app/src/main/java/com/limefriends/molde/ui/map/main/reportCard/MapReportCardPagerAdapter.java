package com.limefriends.molde.ui.map.main.reportCard;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.ui.map.main.MapFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapReportCardPagerAdapter extends PagerAdapter implements IMapReportCardPagerAdapter {

    @BindView(R.id.report_card_view)
    CardView report_card_view;
    @BindView(R.id.report_card_address)
    TextView report_card_address;
    @BindView(R.id.report_card_detail_address)
    TextView report_card_detail_address;

    private List<MapReportCardItem> reportCardDataList;
    private float mBaseElevation;
    private MapFragment fragment;

    private CardView[] reportCardViewArray;

    public MapReportCardPagerAdapter(MapFragment fragment) {
        reportCardViewArray = new CardView[0];
        reportCardDataList = new ArrayList<>();
        this.fragment = fragment;
    }

    public void setData(List<MapReportCardItem> data) {
        reportCardDataList = data;
        reportCardViewArray = new CardView[data.size()];
        notifyDataSetChanged();
    }

    public void removeAllCardItem(List<MapReportCardItem> data) {
        reportCardDataList = data;
        reportCardViewArray = new CardView[data.size()];
        notifyDataSetChanged();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        if (reportCardViewArray.length == 0) return null;
        return reportCardViewArray[position];
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
        // TODO 살짝 손
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.map_report_card_item, container, false);
        ButterKnife.bind(this, view);
        container.addView(view);
        bind(reportCardDataList.get(position));
        if (mBaseElevation == 0) {
            mBaseElevation = report_card_view.getCardElevation();
        }
        report_card_view.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        report_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showReportCardListDialog(reportCardDataList.get(position).getRepId());
            }
        });
        reportCardViewArray[position] = report_card_view;
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void bind(MapReportCardItem item) {
        report_card_address.setText(item.getTitle());
        report_card_detail_address.setText(item.getText());
    }

}
