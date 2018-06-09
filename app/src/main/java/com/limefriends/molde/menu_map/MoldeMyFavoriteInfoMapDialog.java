package com.limefriends.molde.menu_map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.Marker;
import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMyFavoriteInfoMapDialog extends BottomSheetDialogFragment {
    @BindView(R.id.my_favorite_content)
    RelativeLayout my_favorite_content;
    @BindView(R.id.my_favorite_marker_title)
    TextView my_favorite_marker_title;
    @BindView(R.id.my_favorite_marker_info)
    TextView my_favorite_marker_info;
    @BindView(R.id.my_favorite_toggle)
    ToggleButton my_favorite_toggle;
    @BindView(R.id.my_favorite_marker_modify_button)
    ImageButton my_favorite_marker_modify_button;

    @BindView(R.id.my_favorite_content_modify)
    RelativeLayout my_favorite_content_modify;
    @BindView(R.id.my_favorite_modify_title)
    EditText my_favorite_modify_title;
    @BindView(R.id.my_favorite_modify_info)
    EditText my_favorite_modify_info;
    @BindView(R.id.my_favorite_modify_button)
    Button my_favorite_modify_button;

    private Context context;
    public MoldeApplyMyFavoriteInfoCallback moldeApplyMyFavoriteInfoCallback;
    public Marker myFavoriteMarker;

    public interface MoldeApplyMyFavoriteInfoCallback{
        void applyMyFavoriteInfo(String title, String info, Marker marker);
    }

    public static MoldeMyFavoriteInfoMapDialog getInstance() {
        return new MoldeMyFavoriteInfoMapDialog();
    }

    public void setCallback(MoldeApplyMyFavoriteInfoCallback moldeApplyMyFavoriteInfoCallback, Marker marker){
        this.moldeApplyMyFavoriteInfoCallback = moldeApplyMyFavoriteInfoCallback;
        myFavoriteMarker = marker;
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

        my_favorite_marker_modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_favorite_content.setVisibility(View.GONE);
                my_favorite_content_modify.setVisibility(View.VISIBLE);
                my_favorite_modify_title.setText(my_favorite_marker_title.getText());
                my_favorite_modify_info.setText(my_favorite_marker_info.getText());
            }
        });

        my_favorite_modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "수정버튼 눌림", Toast.LENGTH_SHORT).show();
                my_favorite_content.setVisibility(View.VISIBLE);
                my_favorite_content_modify.setVisibility(View.GONE);
                my_favorite_marker_title.setText(my_favorite_modify_title.getText());
                my_favorite_marker_info.setText(my_favorite_modify_info.getText());
                moldeApplyMyFavoriteInfoCallback.applyMyFavoriteInfo(
                        my_favorite_marker_title.getText().toString(),
                        my_favorite_marker_info.getText().toString(),
                        myFavoriteMarker
                );
            }
        });


        return view;
    }



}
