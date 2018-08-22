package com.limefriends.molde.ui.menu_map.favorite;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
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
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;

    private double markerLat;
    private double markerLng;

    private Context context;
    public MoldeApplyMyFavoriteInfoCallback moldeApplyMyFavoriteInfoCallback;

    public Marker myFavoriteMarker;

    public interface MoldeApplyMyFavoriteInfoCallback{
        void applyMyFavoriteInfo(String title, String info, Marker marker);
        void setMyFavoriteActive(boolean active);
    }

    public static MoldeMyFavoriteInfoMapDialog getInstance() {
        return new MoldeMyFavoriteInfoMapDialog();
    }

    public void setCallback(MoldeApplyMyFavoriteInfoCallback moldeApplyMyFavoriteInfoCallback,
                            Marker marker){
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
            my_favorite_toggle.setChecked(bundle.getBoolean("myFavoriteActive"));

            markerLat = bundle.getDouble("markerLat");
            markerLng = bundle.getDouble("markerLng");

            Log.e("호출확인", markerLat+":"+markerLng);

            if (my_favorite_toggle.isChecked()) {
                my_favorite_toggle.setBackgroundResource(R.drawable.ic_star_on);
            } else {
                my_favorite_toggle.setBackgroundResource(R.drawable.ic_star_off);
            }
        }

        my_favorite_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(my_favorite_toggle.isChecked()){
                    my_favorite_toggle.setBackgroundResource(R.drawable.ic_star_on);

                }else {
                    my_favorite_toggle.setBackgroundResource(R.drawable.ic_star_off);
                    moldeApplyMyFavoriteInfoCallback.setMyFavoriteActive(false);
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
                // Toast.makeText(getContext(), "수정버튼 눌림", Toast.LENGTH_SHORT).show();
                my_favorite_content.setVisibility(View.VISIBLE);
                my_favorite_content_modify.setVisibility(View.GONE);
                my_favorite_marker_title.setText(my_favorite_modify_title.getText());
                my_favorite_marker_info.setText(my_favorite_modify_info.getText());
                moldeApplyMyFavoriteInfoCallback.applyMyFavoriteInfo(
                        my_favorite_marker_title.getText().toString(),
                        my_favorite_marker_info.getText().toString(),
                        myFavoriteMarker
                );
                addToMyFavorite();
            }
        });

        return view;
    }

    private void addToMyFavorite() {

        MoldeRestfulService.Favorite favoriteService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Favorite.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<Result> call = favoriteService.addToMyFavorite(
                "lkj",
                my_favorite_marker_title.getText().toString(),
                my_favorite_marker_info.getText().toString(),
                markerLat,
                markerLng);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "즐겨찾기 추가 성공", Toast.LENGTH_SHORT).show();
                    Log.e("로그 자 보자", response.body().getResult() + "");
                    // TODO 여기서 finish() 잠시 두고 3-5대 열어놓고 연속으로 누르면서 부하를 얼만큼 버티는지 알아보자
                    dismiss();
                } else {
                    try {
                        Log.e("로그 자 보자", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), "데이터 전송 실패", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
