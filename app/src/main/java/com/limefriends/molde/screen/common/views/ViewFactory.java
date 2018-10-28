package com.limefriends.molde.screen.common.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.magazine.detail.view.CardNewsDetailPagerView;
import com.limefriends.molde.screen.magazine.detail.view.viewImpl.CardNewsDetailPagerViewImpl;
import com.limefriends.molde.screen.magazine.detail.view.CardNewsDetailView;
import com.limefriends.molde.screen.magazine.detail.view.viewImpl.CardNewsDetailViewImpl;
import com.limefriends.molde.screen.magazine.info.view.HowToDetectView;
import com.limefriends.molde.screen.magazine.info.view.viewImpl.HowToDetectViewImpl;
import com.limefriends.molde.screen.magazine.main.view.CardNewsItemView;
import com.limefriends.molde.screen.magazine.main.view.viewImpl.CardNewsItemViewImpl;
import com.limefriends.molde.screen.magazine.main.view.CardNewsView;
import com.limefriends.molde.screen.magazine.main.view.viewImpl.CardNewsViewImpl;

import javax.annotation.Nullable;

public class ViewFactory {

    private final LayoutInflater mLayoutInflater;
    private final ImageLoader mImageLoader;
    private final DialogFactory mDialogFactory;
    private final DialogManager mDialogManager;

    public ViewFactory(LayoutInflater mLayoutInflater,
                       ImageLoader imageLoader,
                       DialogManager dialogManager,
                       DialogFactory dialogFactory) {
        this.mLayoutInflater = mLayoutInflater;
        this.mImageLoader = imageLoader;
        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;
    }

    public <T extends ViewMvc> T newInstance(Class<T> viewClass, @Nullable ViewGroup container) {

        ViewMvc viewMvc;

        if (viewClass == NestedToolbar.class) {
            viewMvc = new NestedToolbar(mLayoutInflater, container);
        }
        else if (viewClass == CardNewsView.class) {
            viewMvc = new CardNewsViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == CardNewsItemView.class) {
            viewMvc = new CardNewsItemViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else if (viewClass == CardNewsDetailView.class) {
            viewMvc = new CardNewsDetailViewImpl(
                    mLayoutInflater, container,this, mDialogFactory, mDialogManager);
        }
        else if (viewClass == CardNewsDetailPagerView.class) {
            viewMvc = new CardNewsDetailPagerViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else if (viewClass == HowToDetectView.class) {
            viewMvc = new HowToDetectViewImpl(mLayoutInflater, container, mImageLoader, this);
        }
        else {
            throw new IllegalArgumentException("unsupported MVC view class " + viewClass);
        }

        // noinspection unchecked
        return (T) viewMvc;
    }

}
