package com.limefriends.molde.screen.common.pagerHelper.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.common.util.StringUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.pagerHelper.common.ShadowTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportCardPagerAdapter
        extends PagerAdapter implements ShadowTransformer.IMapReportCardPagerAdapter {

    public interface OnReportCardClickListener {

        void onReportCardClicked(int reportId);
    }

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

    private OnReportCardClickListener mOnReportCardClickListener;
    private List<FeedEntity> reportCardDataList;
    private float mBaseElevation;
    private ImageLoader mImageLoader;

    private CardView[] reportCardViewArrayOrigin;
    private CardView[] reportCardViewArrayTemp;

    public ReportCardPagerAdapter(ImageLoader imageLoader) {
        reportCardViewArrayOrigin = new CardView[0];
        reportCardViewArrayTemp = new CardView[0];
        reportCardDataList = new ArrayList<>();
        this.mImageLoader = imageLoader;
    }

    public void addAll(List<FeedEntity> data) {
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

    public void removeAllCardItem() {
        reportCardDataList = new ArrayList<>();
        reportCardViewArrayOrigin = new CardView[0];
        notifyDataSetChanged();
    }

    public void setOnReportCardClickListener(OnReportCardClickListener mOnReportCardClickListener) {
        this.mOnReportCardClickListener = mOnReportCardClickListener;
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
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_feed_pager, container, false);
        ButterKnife.bind(this, view);
        container.addView(view);
        bind(reportCardDataList.get(position));
        if (mBaseElevation == 0) {
            mBaseElevation = report_card_view.getCardElevation();
        }
        report_card_view.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        report_card_view.setOnClickListener(v
                -> {
            mOnReportCardClickListener.onReportCardClicked(reportCardDataList.get(position).getRepId());
            Log.e("호출확인", reportCardDataList.get(position).getRepId()+"");
        });
        reportCardViewArrayOrigin[position] = report_card_view;
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void bind(FeedEntity entity) {
        report_info_date.setText(DateUtil.fromLongToDate(entity.getRepDate()));
        report_card_address.setText(StringUtil.moveLine(entity.getRepContents()));
        report_card_detail_address.setText(entity.getRepDetailAddr());
        if (entity.getRepImg() != null && entity.getRepImg().size() != 0) {
            mImageLoader.loadCenterCrop(entity.getRepImg().get(0).getImageUrl(), report_card_image);
        }
    }

}
