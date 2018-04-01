package com.limefriends.molde.menu_map;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String lat;
    private String lng;
    @BindView(R.id.map_ui) RelativeLayout map_ui;
    @BindView(R.id.loc_search_bar) LinearLayout search_bar;

    public static MoldeMapFragment newInstance() {
        return new MoldeMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_molde_map, container, false);
        ButterKnife.bind(this, view);
        SupportMapFragment mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);

        search_bar.setElevation(12);
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "검색 창으로 이동", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), MoldeSearchMapInfoActivity.class);
                startActivity(intent);
            }
        });

        map_ui.bringToFront();
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        lat = "37.3928948";
        lng = "126.72539759999995";

        //초기 좌표 설정
        LatLng initialLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        /*
        지도를 마커위치로 이동해야함
         */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 17));
        /*
        지도 UI 설정 ZOOM 레벨 설정
         */

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng myLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        markerOptions.position(myLocation);
        markerOptions.title("제목");
        markerOptions.snippet("내용");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon));
        mMap.addMarker(markerOptions).showInfoWindow();

        /*********************** Map Click ***********************/
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        /*********************** Map long Click ***********************/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });

    }
}
