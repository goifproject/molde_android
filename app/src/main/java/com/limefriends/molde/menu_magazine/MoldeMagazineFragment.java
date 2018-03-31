package com.limefriends.molde.menu_magazine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.R;

public class MoldeMagazineFragment extends Fragment {

    public static MoldeMagazineFragment newInstance() {
        return new MoldeMagazineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_molde_magazine, container, false);
    }


}
