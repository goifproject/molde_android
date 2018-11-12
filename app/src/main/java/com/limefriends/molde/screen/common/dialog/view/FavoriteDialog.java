package com.limefriends.molde.screen.common.dialog.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.viewController.BaseObservableBottomSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class FavoriteDialog
        extends BaseObservableBottomSheetDialog<FavoriteDialog.FavoriteDialogDismissListener> {

    /* package */ public static final String ARG_TITLE = "ARG_TITLE";
    /* package */ public static final String ARG_INFO = "ARG_INFO";
    /* package */ public static final String ARG_LAT = "ARG_LAT";
    /* package */ public static final String ARG_LNG = "ARG_LNG";

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Service private Repository.Favorite mFavoriteRepository;
    @Service private ToastHelper mToastHelper;

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

    public interface FavoriteDialogDismissListener {

        void onNewFavoriteAdded(String title, String info);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getInjector().inject(this);
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

            markerLat = bundle.getDouble(ARG_LAT);
            markerLng = bundle.getDouble(ARG_LNG);

            if (bundle.getString(ARG_TITLE).equals(getText(R.string.marker_title_favorite).toString()) ||
                    bundle.getString(ARG_TITLE).equals(getText(R.string.marker_title_search_location).toString())) {
                my_favorite_marker_title.setText(getText(R.string.marker_require_info));
                return;
            }

            my_favorite_marker_title.setText(bundle.getString(ARG_TITLE));
            my_favorite_marker_info.setText(bundle.getString(ARG_INFO));

        }
    }

    private void setupListener() {

        if (my_favorite_marker_title.getText().toString().equals(getText(R.string.marker_require_info))) {
            my_favorite_marker_modify_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    my_favorite_content.setVisibility(View.GONE);
                    my_favorite_content_modify.setVisibility(View.VISIBLE);
                }
            });
        }

        my_favorite_modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (my_favorite_modify_title.getText().toString().equals("")
                        || my_favorite_modify_info.getText().toString().equals("")
                        || my_favorite_modify_title.getText().toString().equals(getText(R.string.marker_title_favorite).toString())
                        || my_favorite_modify_title.getText().toString().equals(getText(R.string.marker_title_search_location).toString())) {
                    Toast.makeText(getContext(), "정보가 부족합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                my_favorite_content.setVisibility(View.VISIBLE);
                my_favorite_content_modify.setVisibility(View.GONE);
                my_favorite_marker_title.setText(my_favorite_modify_title.getText());
                my_favorite_marker_info.setText(my_favorite_modify_info.getText());

                String uId = FirebaseAuth.getInstance().getUid();

                addToMyFavorite(uId,
                        my_favorite_marker_title.getText().toString(),
                        my_favorite_marker_info.getText().toString(),
                        markerLat, markerLng
                );
            }
        });
    }

    public void addToMyFavorite(String userId, String name, String address, double lat, double lng) {

        mCompositeDisposable.add(
                mFavoriteRepository
                        .addToMyFavorite(userId, name, address, lat, lng)
                        .subscribe(
                                e -> {
                                    for (FavoriteDialogDismissListener listener : getListeners()) {
                                        listener.onNewFavoriteAdded(
                                                my_favorite_marker_title.getText().toString(),
                                                my_favorite_marker_info.getText().toString());
                                    }
                                    mToastHelper.showShortToast("즐겨찾기 추가 성공");
                                    dismiss();
                                },
                                err -> mToastHelper.showShortToast("데이터 전송 실패"),
                                () -> { }
                        )
        );
    }

}
