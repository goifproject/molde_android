package com.limefriends.molde.screen.common.view;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
import com.limefriends.molde.screen.feed.detail.view.FeedDetailView;
import com.limefriends.molde.screen.feed.detail.view.FeedDetailViewImpl;
import com.limefriends.molde.screen.feed.main.view.FeedView;
import com.limefriends.molde.screen.feed.main.view.FeedViewImpl;
import com.limefriends.molde.screen.magazine.comment.view.CardNewsCommentView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNewsComment.CardNewsCommentItemViewImpl;
import com.limefriends.molde.screen.magazine.comment.view.CardNewsCommentViewImpl;
import com.limefriends.molde.screen.common.pagerHelper.itemView.ImagePagerView;
import com.limefriends.molde.screen.common.pagerHelper.itemView.ImagePagerViewImpl;
import com.limefriends.molde.screen.magazine.detail.view.CardNewsDetailView;
import com.limefriends.molde.screen.magazine.detail.view.CardNewsDetailViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.molcaInfo.MolcaInfoItemView;
import com.limefriends.molde.screen.magazine.info.view.MolcaInfoView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.molcaInfo.MolcaInfoItemViewImpl;
import com.limefriends.molde.screen.magazine.info.view.MolcaInfoViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNews.CardNewsItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNews.CardNewsItemViewImpl;
import com.limefriends.molde.screen.magazine.main.view.CardNewsView;
import com.limefriends.molde.screen.magazine.main.view.CardNewsViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.favorite.FavoriteItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.favorite.FavoriteItemViewImpl;
import com.limefriends.molde.screen.map.favorite.view.FavoriteView;
import com.limefriends.molde.screen.map.favorite.view.FavoriteViewImpl;
import com.limefriends.molde.screen.map.main.view.MapView;
import com.limefriends.molde.screen.map.main.view.MapViewImpl;
import com.limefriends.molde.screen.map.report.view.ReportView;
import com.limefriends.molde.screen.map.report.view.ReportViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.searchLocation.SearchLocationItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.searchLocation.SearchLocationItemViewImpl;
import com.limefriends.molde.screen.map.search.view.SearchLocationView;
import com.limefriends.molde.screen.map.search.view.SearchLocationViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.inquiry.InquiryItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.inquiry.InquiryItemViewImpl;
import com.limefriends.molde.screen.mypage.comment.view.MyCommentView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportedComment.ReportedCommentItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportedComment.ReportedCommentItemViewImpl;
import com.limefriends.molde.screen.mypage.comment.view.MyCommentViewImpl;
import com.limefriends.molde.screen.mypage.inquiry.view.InquiryView;
import com.limefriends.molde.screen.mypage.inquiry.view.InquiryViewImpl;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.myFeed.MyFeedItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.myFeed.MyFeedItemViewImpl;
import com.limefriends.molde.screen.mypage.login.view.LoginView;
import com.limefriends.molde.screen.mypage.login.view.LoginViewImpl;
import com.limefriends.molde.screen.mypage.main.view.MyPageMainView;
import com.limefriends.molde.screen.mypage.main.view.MyPageMainViewImpl;
import com.limefriends.molde.screen.mypage.report.view.MyFeedView;
import com.limefriends.molde.screen.mypage.report.view.MyFeedViewImpl;
import com.limefriends.molde.screen.mypage.scrap.view.ScrapView;
import com.limefriends.molde.screen.mypage.scrap.view.ScrapViewImpl;
import com.limefriends.molde.screen.mypage.settings.view.SettingsView;
import com.limefriends.molde.screen.mypage.settings.view.SettingsViewImpl;

import javax.annotation.Nullable;

public class ViewFactory {

    private final LayoutInflater mLayoutInflater;
    private final ImageLoader mImageLoader;
    private final DialogFactory mDialogFactory;
    private final DialogManager mDialogManager;
    private final ToastHelper mToastHelper;
    private final FragmentManager mFragmentManager;

    public ViewFactory(LayoutInflater mLayoutInflater,
                       ImageLoader imageLoader,
                       DialogManager dialogManager,
                       DialogFactory dialogFactory,
                       ToastHelper toastHelper,
                       FragmentManager fragmentManager) {
        this.mLayoutInflater = mLayoutInflater;
        this.mImageLoader = imageLoader;
        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;
        this.mToastHelper = toastHelper;
        this.mFragmentManager = fragmentManager;
    }

    public <T extends ViewMvc> T newInstance(Class<T> viewClass, @Nullable ViewGroup container) {

        ViewMvc viewMvc;

        if (viewClass == NestedToolbar.class) {
            viewMvc = new NestedToolbar(mLayoutInflater, container);
        }
        else if (viewClass == GoogleMapView.class) {
            viewMvc = new GoogleMapView(mLayoutInflater, container, mFragmentManager);
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
            viewMvc = new ReportViewImpl(mLayoutInflater, container, mDialogFactory, mDialogManager, mToastHelper, this);
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
            viewMvc = new MapViewImpl(mLayoutInflater, container, this, mToastHelper, mDialogManager, mDialogFactory, mImageLoader);
        }
        else if (viewClass == ReportCardItemView.class) {
            viewMvc = new ReportCardItemViewImpl(mLayoutInflater, container, mImageLoader);
        }
        else {
            throw new IllegalArgumentException("unsupported MVC view class " + viewClass);
        }
        // noinspection unchecked
        return (T) viewMvc;
    }
}
