package com.limefriends.molde.menu_map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMyFavoriteInfoMapDialog extends BottomSheetDialogFragment {
    @BindView(R.id.my_favorite_marker_title)
    TextView my_favorite_marker_title;
    @BindView(R.id.my_favorite_marker_info)
    TextView my_favorite_marker_info;
    @BindView(R.id.my_favorite_toggle)
    ToggleButton my_favorite_toggle;
    @BindView(R.id.my_favorite_marker_modi_button)
    ImageButton my_favorite_marker_modi_button;

    private Context context;

    public static MoldeMyFavoriteInfoMapDialog getInstance() {
        return new MoldeMyFavoriteInfoMapDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_my_favorite_info_dialog, container,false);
        ButterKnife.bind(this, view);
        this.context = getContext();
        Bundle bundle = getArguments();
        if(bundle != null){
            my_favorite_marker_title.setText(bundle.getString("markerTitle"));
            my_favorite_marker_info.setText(bundle.getString("markerInfo"));
        }

        my_favorite_toggle.setChecked(false);
        my_favorite_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(my_favorite_toggle.isChecked() == false){
                    my_favorite_toggle.setBackgroundResource(R.drawable.ic_star_off);
                }else if(my_favorite_toggle.isChecked() == true){
                    my_favorite_toggle.setBackgroundResource(R.drawable.ic_star_on);
                }
            }
        });

        my_favorite_marker_modi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "수정버튼 눌림", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }



}
