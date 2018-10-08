package com.limefriends.molde.ui.map.favorite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFavoriteDialog extends BottomSheetDialogFragment {

    @BindView(R.id.my_favorite_content)
    RelativeLayout my_favorite_content;
    @BindView(R.id.my_favorite_marker_title)
    TextView my_favorite_marker_title;
    @BindView(R.id.my_favorite_marker_info)
    TextView my_favorite_marker_info;
    @BindView(R.id.my_favorite_marker_modify_button)
    ImageView my_favorite_marker_modify_button;
    @BindView(R.id.my_favorite_toggle)
    ImageView my_favorite_toggle;

    @BindView(R.id.my_favorite_content_modify)
    RelativeLayout my_favorite_content_modify;
    @BindView(R.id.my_favorite_modify_title)
    EditText my_favorite_modify_title;
    @BindView(R.id.my_favorite_modify_info)
    EditText my_favorite_modify_info;
    @BindView(R.id.my_favorite_modify_button)
    Button my_favorite_modify_button;

    private double markerLat;
    private double markerLng;

    public MoldeApplyMyFavoriteInfoCallback moldeApplyMyFavoriteInfoCallback;
    public Marker myFavoriteMarker;
    private String uId;
    private boolean isMyFavoriteActive;

    public interface MoldeApplyMyFavoriteInfoCallback {
        void applyMyFavoriteInfo(String title, String info, Marker marker);
        void setMyFavoriteActive(boolean active);
    }

    public void setCallback(MoldeApplyMyFavoriteInfoCallback moldeApplyMyFavoriteInfoCallback,
                            Marker marker,
                            String uId) {
        this.moldeApplyMyFavoriteInfoCallback = moldeApplyMyFavoriteInfoCallback;
        this.myFavoriteMarker = marker;
        this.uId = uId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_map_favorite, container, false);

        setupViews(view);

        setupListener();

        return view;
    }

    private void setupViews(View view) {

        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {

            markerLat = bundle.getDouble("markerLat");
            markerLng = bundle.getDouble("markerLng");
            isMyFavoriteActive = bundle.getBoolean("myFavoriteActive");

            if (isMyFavoriteActive) {
                my_favorite_toggle.setImageResource(R.drawable.ic_favorite_star_on_t);
            }

            if (bundle.getString("markerTitle").equals(getText(R.string.marker_title_favorite).toString()) ||
                    bundle.getString("markerTitle").equals(getText(R.string.marker_title_search_location).toString())) {
                my_favorite_marker_title.setText(getText(R.string.marker_require_info));
                return;
            }

            my_favorite_marker_title.setText(bundle.getString("markerTitle"));
            my_favorite_marker_info.setText(bundle.getString("markerInfo"));

        }
    }

    private void setupListener() {

        if (my_favorite_marker_title.getText().toString().equals(getText(R.string.marker_require_info))) {
            my_favorite_marker_modify_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    my_favorite_content.setVisibility(View.GONE);
                    my_favorite_content_modify.setVisibility(View.VISIBLE);
//                    my_favorite_modify_title.setText(my_favorite_marker_title.getText());
//                    my_favorite_modify_info.setText(my_favorite_marker_info.getText());
                }
            });
        }

        my_favorite_modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (my_favorite_marker_title.getText().toString().equals("")
                        || my_favorite_marker_info.getText().toString().equals("")
                        || my_favorite_marker_info.getText().toString().equals(getText(R.string.marker_title_favorite).toString())
                        || my_favorite_marker_info.getText().toString().equals(getText(R.string.marker_title_search_location).toString())) {
                    Toast.makeText(getContext(), "정보가 부족합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

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
    }

    private void addToMyFavorite() {

        MoldeRestfulService.Favorite favoriteService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Favorite.class);

        Call<Result> call = favoriteService.addToMyFavorite(
                uId,
                my_favorite_marker_title.getText().toString(),
                my_favorite_marker_info.getText().toString(),
                markerLat,
                markerLng);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "즐겨찾기 추가 성공", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), "데이터 전송 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
