package com.limefriends.molde;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.limefriends.molde.menu_magazine.MoldeMagazineFragment;
import com.limefriends.molde.menu_map.MoldeMapFragment;
import com.limefriends.molde.menu_mypage.MoldeMyPageFragment;
import com.limefriends.molde.menu_reportlist.MoldeReportListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMainActivity extends AppCompatActivity {
    @BindView(R.id.navigation) BottomNavigationView navigation;
    FragmentTransaction ft;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_menu_map:
                    replaceFragment(MoldeMapFragment.newInstance());
                    return true;
                case R.id.main_menu_magazine:
                    replaceFragment(MoldeMagazineFragment.newInstance());
                    return true;
                case R.id.main_menu_reportlist:
                    replaceFragment(MoldeReportListFragment.newInstance());
                    return true;
                case R.id.main_menu_mypage:
                    replaceFragment(MoldeMyPageFragment.newInstance());
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molde_main);
        ButterKnife.bind(this);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.menu_fragment, MoldeMapFragment.newInstance()).commit();
    }

    private void replaceFragment(Fragment fm) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.menu_fragment, fm).commit();
    }

}
