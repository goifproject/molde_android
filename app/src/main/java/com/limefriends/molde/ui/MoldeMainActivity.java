package com.limefriends.molde.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.helper.BottomNavigationViewHelper;
import com.limefriends.molde.entity.favorite.FavoriteEntity;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.map.SearchMapHistoryEntity;
import com.limefriends.molde.entity.map.SearchMapInfoEntity;
import com.limefriends.molde.ui.feed.FeedFragment;
import com.limefriends.molde.ui.magazine.CardNewsFragment;
import com.limefriends.molde.ui.map.main.MapFragment;
import com.limefriends.molde.ui.mypage.MyPageFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    public static Context allContext;
    public SparseArrayCompat fragmentSparseArray;

    private long lastTimeBackPressed;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private SearchMapInfoEntity searchEntity;
    private SearchMapHistoryEntity historyEntity;
    private FavoriteEntity myFavoriteEntity;
    private FeedEntity feedEntity;
    private Fragment fragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_menu_magazine:
                    if (fragmentSparseArray.get(R.string.main_menu_magazine) == null) {
                        fragment = new CardNewsFragment();
                        fragmentSparseArray.append(R.string.main_menu_magazine, fragment);
                        replaceFragment(fragment);
                    } else {
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_magazine);
                        replaceFragment(fragment);
                    }
                    return true;
                case R.id.main_menu_map:
                    if (fragmentSparseArray.get(R.string.main_menu_map) == null) {
//                        fragment = MapFragment.newInstance();
                        fragment = new MapFragment();
                        fragmentSparseArray.append(R.string.main_menu_map, fragment);
                        replaceFragment(fragment);
                    } else {
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_map);
                        replaceFragment(fragment);
                    }
                    return true;
                case R.id.main_menu_feed:
                    if (fragmentSparseArray.get(R.string.main_menu_report_list) == null) {
                        fragment = new FeedFragment();
                        fragmentSparseArray.append(R.string.main_menu_report_list, fragment);
                        replaceFragment(fragment);
                    } else {
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_report_list);
                        replaceFragment(fragment);
                    }
                    return true;
                case R.id.main_menu_mypage:
                    if (fragmentSparseArray.get(R.string.main_menu_mypage) == null) {
                        fragment = new MyPageFragment();
                        fragmentSparseArray.append(R.string.main_menu_mypage, fragment);
                        replaceFragment(fragment);
                    } else {
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_mypage);
                        replaceFragment(fragment);
                    }
                    return true;
            }
            return false;
        }

    };
    OnKeyBackPressedListener mOnKeyBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("호출확인", "Main : onCreate");

        setContentView(R.layout.activity_molde_main);
        setupWindowAnimations();
        ButterKnife.bind(this);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (fragment == null) {
            Log.e("호출확인", "Main : MagFragment == null");
        }

        if (fragment == null && fragmentSparseArray == null) {
            fragmentSparseArray = new SparseArrayCompat();
//            fragment = MapFragment.newInstance();
            fragment = new MapFragment();
            // fragmentSparseArray.append(R.string.main_menu_map, fragment);
        }
//        fm = getSupportFragmentManager();
//        ft = fm.beginTransaction();
//        ft.add(R.id.menu_fragment, fragment).commit();
        navigation.setSelectedItemId(R.id.main_menu_map);
        allContext = this;
    }

    public void setSelectedMenu(int fragmentId) {
        navigation.setSelectedItemId(fragmentId);
    }

    private void setupWindowAnimations() {
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (fragment != null && fragment instanceof MapFragment) {
//            ((MapFragment) fragment).onPermissionCheck(requestCode, permissions, grantResults);
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    public void replaceFragment(Fragment fm) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.menu_fragment, fm).addToBackStack(null).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Log.e("호출확인", "Main : onResume1");
        if (intent != null) {
            Log.e("호출확인", "Main : onResume2");
            searchEntity = (SearchMapInfoEntity) intent.getSerializableExtra("mapSearchInfo");
            historyEntity = (SearchMapHistoryEntity) intent.getSerializableExtra("mapHistoryInfo");
            myFavoriteEntity = (FavoriteEntity) intent.getSerializableExtra("mapFavoriteInfo");

            //Log.e("호출확인", "Main : searchEntity - "+searchEntity.toString());
            //Log.e("호출확인", "Main : historyEntity - "+historyEntity.toString());
            //Log.e("호출확인", "Main : myFavoriteEntity - "+myFavoriteEntity.toString());


        }
    }

    public SearchMapInfoEntity getMapInfoResultData() {
        return this.searchEntity;
    }

    public SearchMapHistoryEntity getMapHistoryResultData() {
        return this.historyEntity;
    }

    public FavoriteEntity getMyFavoriteEntity() {
        return this.myFavoriteEntity;
    }

    public interface OnKeyBackPressedListener {
        void onBackKey();
    }

    public void setOnKeyBackPressedListener(OnKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > lastTimeBackPressed + 1500) {
            lastTimeBackPressed = System.currentTimeMillis();
            if (mOnKeyBackPressedListener != null) {
                mOnKeyBackPressedListener.onBackKey();
            }
            FirebaseAuth firebaseAuth =((MoldeApplication)getApplication()).getFireBaseAuth();
            if (firebaseAuth.getUid() != null) {
                Log.e("Auth", firebaseAuth.getUid());
                Log.e("User name", firebaseAuth.getCurrentUser().getDisplayName());
            } else {
                Log.e("Auth", "계정 UID값 없음");
                Log.e("User name", "계정 이름 없음");
            }
        } else {
            finishAfterTransition();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public FeedEntity getFeedEntity() {
        return feedEntity;
    }

    public void setFeedEntity(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
        ((MapFragment) fragmentSparseArray.get(R.string.main_menu_map)).setFromFeed(true);
    }
}
