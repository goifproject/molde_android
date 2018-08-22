package com.limefriends.molde.ui.menu_map.main.card;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.ui.menu_map.main.card.IReportCardPagerAdapter.MAX_ELEVATION_FACTOR;

public class ReportCardPagerAdapter extends PagerAdapter implements IReportCardPagerAdapter {

    @BindView(R.id.report_card_address)
    TextView report_card_address;
    @BindView(R.id.report_card_detail_address)
    TextView report_card_detail_address;
    @BindView(R.id.report_card_view)
    CardView report_card_view;

//    public interface ReportCardPagerAdapterCallback {
//        void applyReportCardInfo(int position);
//    }

    //private List<CardView> reportCardViewList;
    private List<ReportCardItem> reportCardDataList;
    // private ReportCardPagerAdapterCallback callback;
    private float mBaseElevation;
    private Context context;

    private CardView[] reportCardViewArray;

    public ReportCardPagerAdapter(Context context) {
        //reportCardViewList = new ArrayList<>();
        reportCardViewArray = new CardView[0];
        reportCardDataList = new ArrayList<>();
        this.context = context;
    }

    public void addCardItem(ReportCardItem item) {
        //reportCardViewList.add(null);
        reportCardDataList.add(item);
    }

    public void setData(List<ReportCardItem> data) {
        reportCardDataList = data;
        reportCardViewArray = new CardView[data.size()];
        notifyDataSetChanged();
    }

    public void removeAllCardItem(List<ReportCardItem> data) {
        reportCardDataList = data;
        reportCardViewArray = new CardView[data.size()];
         notifyDataSetChanged();
        Log.e("호출확인", "reportCardDataList : "+reportCardDataList.size());
        Log.e("호출확인", "removeAllCardItem");
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        if (reportCardViewArray.length == 0) return null;

        // if (reportCardViewList.size() == 0) return null;

//        if (callback != null) {
//            callback.applyReportCardInfo(position);
//        }
        // return reportCardViewList.get(position);

         return reportCardViewArray[position];
    }

    @Override
    public int getCount() {
        Log.e("호출확인", "getCount : "+reportCardDataList.size());
        return reportCardDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        Log.e("호출확인 카드뷰", "instantiateItem : "+position);

        // TODO 살짝 손
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.map_report_card_item, container, false);
         ButterKnife.bind(this, view);
        // report_card_view = view.findViewById(R.id.report_card_view);
         //notifyDataSetChanged();
        container.addView(view);
        bind(reportCardDataList.get(position));
        if (mBaseElevation == 0) {
            mBaseElevation = report_card_view.getCardElevation();
        }
        report_card_view.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);

        report_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoldeReportCardMapDialog moldeReportCardMapDialog = MoldeReportCardMapDialog.getInstance();
                moldeReportCardMapDialog.show(((MoldeMainActivity) context).getSupportFragmentManager(), "bottomSheet");
                moldeReportCardMapDialog.setData(reportCardDataList.get(position));
            }
        });
        reportCardViewArray[position] = report_card_view;

        //reportCardViewList.add(report_card_view);

        // reportCardViewList.set(position, report_card_view);
//        reportCardViewList.get(position).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MoldeReportCardMapDialog moldeReportCardMapDialog = MoldeReportCardMapDialog.getInstance();
//                moldeReportCardMapDialog.show(((MoldeMainActivity) context).getSupportFragmentManager(), "bottomSheet");
//                moldeReportCardMapDialog.setData(reportCardDataList.get(position));
//
//
//            }
//        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        //reportCardViewList.set(position, null);
    }

    private void bind(ReportCardItem item) {
        report_card_address.setText(item.getTitle());
        report_card_detail_address.setText(item.getText());
    }

//    public void setCallback(ReportCardPagerAdapterCallback callback) {
//        this.callback = callback;
//    }
}
