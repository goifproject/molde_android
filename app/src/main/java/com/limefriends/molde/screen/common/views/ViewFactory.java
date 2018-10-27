package com.limefriends.molde.screen.common.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.magazine.main.view.CardNewsItemView;
import com.limefriends.molde.screen.magazine.main.view.CardNewsItemViewImpl;
import com.limefriends.molde.screen.magazine.main.view.CardNewsView;
import com.limefriends.molde.screen.magazine.main.view.CardNewsViewImpl;

import javax.annotation.Nullable;

public class ViewFactory {

    private final LayoutInflater mLayoutInflater;
    private final ImageLoader mImageLoader;

    public ViewFactory(LayoutInflater mLayoutInflater, ImageLoader imageLoader) {
        this.mLayoutInflater = mLayoutInflater;
        this.mImageLoader = imageLoader;
    }

    public <T extends ViewMvc> T newInstance(Class<T> viewClass, @Nullable ViewGroup container) {

        ViewMvc viewMvc;

        if (viewClass == CardNewsView.class) {
            viewMvc = new CardNewsViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == CardNewsItemView.class) {
            viewMvc = new CardNewsItemViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else {
            throw new IllegalArgumentException("unsupported MVC view class " + viewClass);
        }

        // noinspection unchecked
        return (T) viewMvc;
    }

}
