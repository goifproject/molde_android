package com.limefriends.molde.menu_reportlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.R;

public class MoldeReportListFragment extends Fragment {

    public static MoldeReportListFragment newInstance() {
        return new MoldeReportListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_molde_reportlist, container, false);
    }


}
