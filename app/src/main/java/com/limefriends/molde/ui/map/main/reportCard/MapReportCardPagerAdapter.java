package com.limefriends.molde.ui.map.main.reportCard;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.DateUitl;
import com.limefriends.molde.comm.utils.StringUtil;
import com.limefriends.molde.ui.map.main.MapFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapReportCardPagerAdapter extends PagerAdapter implements IMapReportCardPagerAdapter {

    @BindView(R.id.report_card_view)
    CardView report_card_view;
    @BindView(R.id.report_info_date)
    TextView report_info_date;
    @BindView(R.id.report_card_address)
    TextView report_card_address;
    @BindView(R.id.report_card_detail_address)
    TextView report_card_detail_address;
    @BindView(R.id.report_card_image)
    ImageView report_card_image;

    private List<MapReportCardItem> reportCardDataList;
    private float mBaseElevation;
    private MapFragment fragment;

    private CardView[] reportCardViewArrayOrigin;
    private CardView[] reportCardViewArrayTemp;

    public MapReportCardPagerAdapter(MapFragment fragment) {
        reportCardViewArrayOrigin = new CardView[0];
        reportCardViewArrayTemp = new CardView[0];
        reportCardDataList = new ArrayList<>();
        this.fragment = fragment;
    }

    public void setData(List<MapReportCardItem> data) {
        reportCardDataList = data;
        reportCardViewArrayOrigin = new CardView[data.size()];
        notifyDataSetChanged();
    }

    public void addAll(List<MapReportCardItem> data) {
        reportCardDataList.addAll(data.subList(reportCardDataList.size(), data.size()));
        reportCardViewArrayTemp = new CardView[reportCardViewArrayOrigin.length + data.size()];
        System.arraycopy(
                // 현재 저장된 카드뷰
                reportCardViewArrayOrigin,
                // 첫 항부터
                0,
                // 임시 저장소
                reportCardViewArrayTemp,
                // 첫 항에
                0,
                // 전부 복사해 넣는다
                reportCardViewArrayOrigin.length);
        reportCardViewArrayOrigin = reportCardViewArrayTemp;
        reportCardViewArrayTemp = null;
        notifyDataSetChanged();
    }

    public void removeAllCardItem(List<MapReportCardItem> data) {
        reportCardDataList = data;
        reportCardViewArrayOrigin = new CardView[data.size()];
        notifyDataSetChanged();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        if (reportCardViewArrayOrigin.length == 0) return null;
        return reportCardViewArrayOrigin[position];
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
                .inflate(R.layout.item_feed_pager, container, false);
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
        reportCardViewArrayOrigin[position] = report_card_view;
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void bind(MapReportCardItem item) {
        report_info_date.setText(DateUitl.fromLongToDate(item.getDate()));
        report_card_address.setText(StringUtil.moveLine(item.getRepContent()));
        report_card_detail_address.setText(item.getDetailAddress());
        if (item.getThumbnailUrl() != null) {
            Glide.with(fragment)
                    .load(item.getThumbnailUrl())
                    .centerCrop()
                    .into(report_card_image);
        }
    }

}
