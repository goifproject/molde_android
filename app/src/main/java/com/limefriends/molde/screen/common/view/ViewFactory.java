package com.limefriends.molde.screen.common.view;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.common.helper.BitmapHelper;
import com.limefriends.molde.screen.common.bottomNavigationViewHelper.BottomNavigationViewHelper;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.mapView.GoogleMapView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportCard.ReportCardItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportCard.ReportCardItemViewImpl;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNewsComment.CardNewsCommentItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.feed.FeedItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.feed.FeedItemViewImpl;
import com.limefriends.molde.screen.view.main.container.MoldeMainView;
import com.limefriends.molde.screen.view.main.container.MoldeMainViewImpl;
import com.limefriends.molde.screen.view.main.splash.MoldeSplashView;
import com.limefriends.molde.screen.view.main.splash.MoldeSplashViewImpl;
import com.limefriends.molde.screen.view.feed.detail.FeedDetailView;
import com.limefriends.molde.screen.view.feed.detail.FeedDetailViewImpl;
import com.limefriends.molde.screen.view.feed.main.FeedView;
import com.limefriends.molde.screen.view.feed.main.FeedViewImpl;
import com.limefriends.molde.screen.view.magazine.comment.CardNewsCommentView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNewsComment.CardNewsCommentItemViewImpl;
import com.limefriends.molde.screen.view.magazine.comment.CardNewsCommentViewImpl;
import com.limefriends.molde.screen.common.pagerHelper.itemView.ImagePagerView;
import com.limefriends.molde.screen.common.pagerHelper.itemView.ImagePagerViewImpl;
import com.limefriends.molde.screen.view.magazine.detail.CardNewsDetailView;
import com.limefriends.molde.screen.view.magazine.detail.CardNewsDetailViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.molcaInfo.MolcaInfoItemView;
import com.limefriends.molde.screen.view.magazine.info.MolcaInfoView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.molcaInfo.MolcaInfoItemViewImpl;
import com.limefriends.molde.screen.view.magazine.info.MolcaInfoViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNews.CardNewsItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNews.CardNewsItemViewImpl;
import com.limefriends.molde.screen.view.magazine.main.CardNewsView;
import com.limefriends.molde.screen.view.magazine.main.CardNewsViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.favorite.FavoriteItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.favorite.FavoriteItemViewImpl;
import com.limefriends.molde.screen.view.map.favorite.FavoriteView;
import com.limefriends.molde.screen.view.map.favorite.FavoriteViewImpl;
import com.limefriends.molde.screen.view.map.main.MapView;
import com.limefriends.molde.screen.view.map.main.MapViewImpl;
import com.limefriends.molde.screen.view.map.report.ReportView;
import com.limefriends.molde.screen.view.map.report.ReportViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.searchLocation.SearchLocationItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.searchLocation.SearchLocationItemViewImpl;
import com.limefriends.molde.screen.view.map.search.SearchLocationView;
import com.limefriends.molde.screen.view.map.search.SearchLocationViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.inquiry.InquiryItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.inquiry.InquiryItemViewImpl;
import com.limefriends.molde.screen.view.mypage.comment.MyCommentView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportedComment.ReportedCommentItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportedComment.ReportedCommentItemViewImpl;
import com.limefriends.molde.screen.view.mypage.comment.MyCommentViewImpl;
import com.limefriends.molde.screen.view.mypage.inquiry.InquiryView;
import com.limefriends.molde.screen.view.mypage.inquiry.InquiryViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.myFeed.MyFeedItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.myFeed.MyFeedItemViewImpl;
import com.limefriends.molde.screen.view.mypage.login.LoginView;
import com.limefriends.molde.screen.view.mypage.login.LoginViewImpl;
import com.limefriends.molde.screen.view.mypage.main.MyPageMainView;
import com.limefriends.molde.screen.view.mypage.main.MyPageMainViewImpl;
import com.limefriends.molde.screen.view.mypage.report.MyFeedView;
import com.limefriends.molde.screen.view.mypage.report.MyFeedViewImpl;
import com.limefriends.molde.screen.view.mypage.scrap.ScrapView;
import com.limefriends.molde.screen.view.mypage.scrap.ScrapViewImpl;
import com.limefriends.molde.screen.view.mypage.settings.SettingsView;
import com.limefriends.molde.screen.view.mypage.settings.SettingsViewImpl;

import javax.annotation.Nullable;

import io.reactivex.annotations.NonNull;

public class ViewFactory {

    private final LayoutInflater mLayoutInflater;
    private final ImageLoader mImageLoader;
    private final DialogFactory mDialogFactory;
    private final DialogManager mDialogManager;
    private final ToastHelper mToastHelper;
    private final FragmentManager mFragmentManager;
    private final BottomNavigationViewHelper mBottomNavigationViewHelper;
    private final BitmapHelper mBitmapHelper;

    public ViewFactory(LayoutInflater mLayoutInflater,
                       ImageLoader imageLoader,
                       DialogManager dialogManager,
                       DialogFactory dialogFactory,
                       ToastHelper toastHelper,
                       FragmentManager fragmentManager,
                       BottomNavigationViewHelper bottomNavigationViewHelper, BitmapHelper bitmapHelper) {
        this.mLayoutInflater = mLayoutInflater;
        this.mImageLoader = imageLoader;
        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;
        this.mToastHelper = toastHelper;
        this.mFragmentManager = fragmentManager;
        this.mBottomNavigationViewHelper = bottomNavigationViewHelper;
        this.mBitmapHelper = bitmapHelper;
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
                    mLayoutInflater, container,this, mToastHelper, mDialogFactory, mDialogManager);
        }
        else if (viewClass == ImagePagerView.class) {
            viewMvc = new ImagePagerViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else if (viewClass == MolcaInfoView.class) {
            viewMvc = new MolcaInfoViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == MolcaInfoItemView.class) {
            viewMvc = new MolcaInfoItemViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else if (viewClass == CardNewsCommentView.class) {
            viewMvc = new CardNewsCommentViewImpl(
                    mLayoutInflater, container, this, mToastHelper, mDialogFactory, mDialogManager);
        }
        else if (viewClass == CardNewsCommentItemView.class) {
            viewMvc = new CardNewsCommentItemViewImpl(mLayoutInflater, container);
        }
        else if (viewClass == FeedView.class) {
            viewMvc = new FeedViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == FeedItemView.class) {
            viewMvc = new FeedItemViewImpl(
                    mLayoutInflater, container, mImageLoader, mDialogFactory, mDialogManager);
        }
        else if (viewClass == FeedDetailView.class) {
            viewMvc = new FeedDetailViewImpl(
                    mLayoutInflater, container, this, mDialogFactory, mDialogManager, mToastHelper);
        }
        else if (viewClass == FavoriteView.class) {
            viewMvc = new FavoriteViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == FavoriteItemView.class) {
            viewMvc = new FavoriteItemViewImpl(mLayoutInflater, container, mDialogFactory, mDialogManager);
        }
        else if (viewClass == ReportView.class) {
            viewMvc = new ReportViewImpl(mLayoutInflater, container, mDialogFactory, mDialogManager,
                    mToastHelper, this, mImageLoader, mBitmapHelper);
        }
        else if (viewClass == SearchLocationView.class) {
            viewMvc = new SearchLocationViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == SearchLocationItemView.class) {
            viewMvc = new SearchLocationItemViewImpl(mLayoutInflater, container);
        }
        else if (viewClass == InquiryView.class) {
            viewMvc = new InquiryViewImpl(mLayoutInflater, container, this, mToastHelper);
        }
        else if (viewClass == InquiryItemView.class) {
            viewMvc = new InquiryItemViewImpl(mLayoutInflater, container);
        }
        else if (viewClass == ScrapView.class) {
            viewMvc = new ScrapViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == MyFeedView.class) {
            viewMvc = new MyFeedViewImpl(mLayoutInflater, container, this);
        }
        else if (viewClass == MyFeedItemView.class) {
            viewMvc = new MyFeedItemViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else if (viewClass == SettingsView.class) {
            viewMvc = new SettingsViewImpl(mLayoutInflater, container, this, mToastHelper);
        }
        else if (viewClass == MyPageMainView.class) {
            viewMvc = new MyPageMainViewImpl(
                    mLayoutInflater, container, mDialogFactory, mDialogManager, mImageLoader, mToastHelper);
        }
        else if (viewClass == LoginView.class) {
            viewMvc = new LoginViewImpl(mLayoutInflater, container);
        }
        else if (viewClass == MyCommentView.class) {
            viewMvc = new MyCommentViewImpl(mLayoutInflater, container, this, mDialogFactory, mDialogManager, mToastHelper);
        }
        else if (viewClass == ReportedCommentItemView.class) {
            viewMvc = new ReportedCommentItemViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else if (viewClass == MapView.class) {
            viewMvc = new MapViewImpl(mLayoutInflater, container, this, mToastHelper, mDialogManager, mDialogFactory, mImageLoader, mFragmentManager);
        }
        else if (viewClass == ReportCardItemView.class) {
            viewMvc = new ReportCardItemViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else if (viewClass == MoldeSplashView.class) {
            viewMvc = new MoldeSplashViewImpl(mLayoutInflater, container);
        }
        else if (viewClass == MoldeMainView.class) {
            viewMvc = new MoldeMainViewImpl(mLayoutInflater, container, mBottomNavigationViewHelper);
        }
        else {
            throw new IllegalArgumentException("unsupported MVC view class " + viewClass);
        }
        // noinspection unchecked
        return (T) viewMvc;
    }

    public <T extends ViewMvc> T newInstance(Class<T> viewClass, @NonNull int container) {

        ViewMvc viewMvc;

        if (viewClass == GoogleMapView.class) {
            viewMvc = new GoogleMapView(mFragmentManager, container);
        }
        else {
            throw new IllegalArgumentException("unsupported MVC view class " + viewClass);
        }
        // noinspection unchecked
        return (T) viewMvc;
    }

}
