package com.limefriends.molde.screen.view.main.container;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.bottomNavigationViewHelper.BottomNavigationViewHelper;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class MoldeMainViewImpl
        extends BaseObservableView<MoldeMainView.Listener>
        implements MoldeMainView, BottomNavigationView.OnNavigationItemSelectedListener {

    private FrameLayout mFrameLayout;
    private BottomNavigationView navigation;
    private BottomNavigationViewHelper mBottomNavigationViewHelper;

    public MoldeMainViewImpl(LayoutInflater inflater,
                             ViewGroup parent,
                             BottomNavigationViewHelper bottomNavigationViewHelper) {
        setRootView(inflater.inflate(R.layout.activity_main, parent, false));

        mFrameLayout = findViewById(R.id.menu_fragment);
        navigation = findViewById(R.id.navigation);

        this.mBottomNavigationViewHelper = bottomNavigationViewHelper;

        mBottomNavigationViewHelper.disableShiftMode(navigation);

        navigation.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_magazine:
                for (Listener listener : getListeners()) {
                    listener.onMagazineTabClicked();
                }
                return true;
            case R.id.main_menu_map:
                for (Listener listener : getListeners()) {
                    listener.onMapTabClicked();
                }
                return true;
            case R.id.main_menu_feed:
                for (Listener listener : getListeners()) {
                    listener.onFeedTabClicked();
                }
                return true;
            case R.id.main_menu_mypage:
                for (Listener listener : getListeners()) {
                    listener.onMyPageTabClicked();
                }
                return true;
        }
        return false;
    }

    @Override
    public void changeTab(int tabId) {
        navigation.setSelectedItemId(tabId);
    }

    @Override
    public FrameLayout getFragmentFrame() {
        return mFrameLayout;
    }
}
