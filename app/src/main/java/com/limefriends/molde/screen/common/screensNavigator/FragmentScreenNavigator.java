package com.limefriends.molde.screen.common.screensNavigator;

import com.limefriends.molde.screen.common.fragmentFrameHelper.FragmentFrameHelper;
import com.limefriends.molde.screen.feed.main.FeedFragment;
import com.limefriends.molde.screen.magazine.main.CardNewsFragment;
import com.limefriends.molde.screen.map.main.MapFragment;
import com.limefriends.molde.screen.mypage.main.MyPageFragment;

public class FragmentScreenNavigator {

    private FragmentFrameHelper mFragmentFrameHelper;

    public FragmentScreenNavigator(FragmentFrameHelper mFragmentFrameHelper) {
        this.mFragmentFrameHelper = mFragmentFrameHelper;
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
