package com.limefriends.molde.screen.common.pagerHelper.common;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;

import static com.limefriends.molde.screen.common.pagerHelper.common.ShadowTransformer.IMapReportCardPagerAdapter.MAX_ELEVATION_FACTOR;


public class ShadowTransformer implements ViewPager.OnPageChangeListener {

    public interface IMapReportCardPagerAdapter {

        int MAX_ELEVATION_FACTOR = 6;

        float getBaseElevation();

        CardView getCardViewAt(int position);

        // int getCardItemCount(); 대체함
        int getCount();

    }

    public interface OnPageSelectedListener {
        // 데이터 세팅
        void onPageSelected(int position);
        // 데이터 추가로 부르기
        void onLastPageSelected();
    }

    private ViewPager mViewPager;
    private IMapReportCardPagerAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;

    private OnPageSelectedListener mOnPageSelectedListener;



    public ShadowTransformer(ViewPager viewPager,
                             IMapReportCardPagerAdapter adapter) {
        mViewPager = viewPager;
        mAdapter = adapter;
    }

    public void enableScaling(boolean enable) {
        if (mScalingEnabled && !enable) {
            // shrink main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        } else if (!mScalingEnabled && enable) {
            // grow main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);
            }
        }
        mScalingEnabled = enable;
    }

    public void setOnPageSelectedCallback(OnPageSelectedListener onPageSelectedCallback) {
        this.mOnPageSelectedListener = onPageSelectedCallback;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation = mAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft = mLastOffset > positionOffset;

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffset = positionOffset;
        }

        // Avoid crash on overscroll
        if (nextPosition > mAdapter.getCount() - 1
                || realCurrentPosition > mAdapter.getCount() - 1) {
            return;
        }

        CardView currentCard = mAdapter.getCardViewAt(realCurrentPosition);

        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentCard != null) {
            if (mScalingEnabled) {
                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffset)));
                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffset)));
            }
            currentCard.setCardElevation((baseElevation + baseElevation
                    * (MAX_ELEVATION_FACTOR - 1) * (1 - realOffset)));
        }

        CardView nextCard = mAdapter.getCardViewAt(nextPosition);

        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
            }
            nextCard.setCardElevation((baseElevation + baseElevation
                    * (MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }
        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageSelectedListener != null) {
            mOnPageSelectedListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if (state == ViewPager.SCROLL_STATE_IDLE
                && /* mOnPageSelectedListener.getCardItemCount()-1 */ mAdapter.getCount() -1 == mViewPager.getCurrentItem()) {
            mOnPageSelectedListener.onLastPageSelected();

        }
    }

}
