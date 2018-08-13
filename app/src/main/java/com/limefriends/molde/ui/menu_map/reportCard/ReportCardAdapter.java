package com.limefriends.molde.ui.menu_map.reportCard;


import android.support.v7.widget.CardView;

public interface ReportCardAdapter {

    int MAX_ELEVATION_FACTOR = 6;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
