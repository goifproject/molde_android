package com.limefriends.molde.menu_mypage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.MoldeMapFragment;

public class MoldeMyPageFragment extends Fragment {

    public static MoldeMyPageFragment newInstance() {
        return new MoldeMyPageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_molde_mypage, container, false);
    }


}
