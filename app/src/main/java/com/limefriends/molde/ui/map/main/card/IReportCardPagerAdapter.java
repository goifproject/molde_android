package com.limefriends.molde.ui.map.main.card;

import android.support.v7.widget.CardView;

public interface IReportCardPagerAdapter {

    int MAX_ELEVATION_FACTOR = 6;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();

}
