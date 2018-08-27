package com.limefriends.molde.ui.map.main.reportCard;

import android.support.v7.widget.CardView;

public interface IMapReportCardPagerAdapter {

    int MAX_ELEVATION_FACTOR = 6;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();

}
