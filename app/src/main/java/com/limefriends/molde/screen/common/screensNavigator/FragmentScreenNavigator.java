package com.limefriends.molde.screen.common.screensNavigator;

import android.support.v4.app.Fragment;

import com.limefriends.molde.screen.common.fragmentFrameHelper.FragmentFrameHelper;
import com.limefriends.molde.screen.controller.feed.main.FeedFragment;
import com.limefriends.molde.screen.controller.magazine.main.CardNewsFragment;
import com.limefriends.molde.screen.controller.map.main.MapFragment;
import com.limefriends.molde.screen.controller.mypage.main.MyPageFragment;

public class FragmentScreenNavigator {

    private FragmentFrameHelper mFragmentFrameHelper;

    public FragmentScreenNavigator(FragmentFrameHelper mFragmentFrameHelper) {
        this.mFragmentFrameHelper = mFragmentFrameHelper;
    }

    public void replaceFragment(Fragment fragment) {
        mFragmentFrameHelper.replaceFragment(fragment);
    }

    public void toMagazineFragment() {
        mFragmentFrameHelper.replaceFragment(CardNewsFragment.newInstance());
    }

    public void toMapFragment() {
        mFragmentFrameHelper.replaceFragment(MapFragment.newInstance());
    }

    public void toFeedFragment() {
        mFragmentFrameHelper.replaceFragment(FeedFragment.newInstance());
    }

    public void toMyPageFragment() {
        mFragmentFrameHelper.replaceFragment(MyPageFragment.newInstance());
    }

}
