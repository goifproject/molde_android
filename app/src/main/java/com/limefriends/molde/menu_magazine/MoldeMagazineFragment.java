package com.limefriends.molde.menu_magazine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;

public class MoldeMagazineFragment extends Fragment implements MoldeMainActivity.onKeyBackPressedListener{

    public static MoldeMagazineFragment newInstance() {
        return new MoldeMagazineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.magazine_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MoldeMainActivity)context).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onBackKey() {
    }
}
