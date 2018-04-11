package com.limefriends.molde.menu_map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.reportCard.ReportCardItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    @BindView(R.id.testB)
    Button testB;

    public ReportCardItem reportCardData;

    public static BottomSheetDialog getInstance() { return new BottomSheetDialog(); }

    public void setData(ReportCardItem data){
        reportCardData = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container,false);
        ButterKnife.bind(this, view);
        testB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), reportCardData.getTitle() + " 눌림", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


}
