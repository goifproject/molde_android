package com.limefriends.molde.screen.magazine.main.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.views.BaseObservableView;

public class CardNewsItemViewImpl
        extends BaseObservableView<CardNewsItemView.Listener> implements CardNewsItemView {

    public CardNewsItemViewImpl(LayoutInflater inflater, ViewGroup parent) {

        setRootView(inflater.inflate(R.layout.item_cardnews, parent, false));
    }
}
