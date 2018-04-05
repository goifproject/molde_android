package com.limefriends.molde.menu_map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapHistoryEntity;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapInfoEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMapFragment extends Fragment
        implements OnMapReadyCallback, MoldeMainActivity.onKeyBackPressedListener {
    @BindView(R.id.map_ui)
    RelativeLayout map_ui;
    @BindView(R.id.loc_search_bar)
    LinearLayout search_bar;
    @BindView(R.id.report_hitory)
    LinearLayout report_history;
    @BindView(R.id.report_history_header)
    RelativeLayout report_history_header;
    @BindView(R.id.loc_search_input)
    TextView loc_search_input;
    @BindView(R.id.my_loc_button)
    ImageButton my_loc_button;
    @BindView(R.id.favorite_button)
    ImageButton favorite_button;
    @BindView(R.id.report_button)
    ImageButton report_button;
    @BindView(R.id.map_view_layout)
    LinearLayout map_view_layout;

    SupportMapFragment mapView;
    private static GoogleMap mMap;
    private String lat = "37.499597";
    private String lng = "127.031372";
    private String name = "";
    private String telNo = "";
    private int moveCnt = 0;
    private boolean backChk = false;
    private boolean initChk = false;

    public static MoldeMapFragment newInstance() {
        return new MoldeMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, view);
        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);

        search_bar.setElevation(12);
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "검색 창으로 이동", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), MoldeSearchMapInfoActivity.class);
                intent.putExtra("searchName", name);
                startActivity(intent);
            }
        });

        map_ui.bringToFront();
        report_history.setVisibility(View.INVISIBLE);

        my_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "위치 가져오기 기능", Toast.LENGTH_LONG).show();
                loc_search_input.setText(R.string.search);
                name = "검색하기";
                getMyLocation();
            }
        });
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "즐겨찾기 페이지로 넘어가기", Toast.LENGTH_LONG).show();
            }
        });
        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "신고 페이지로 넘어가기", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), MoldeReportActivity.class);
                startActivity(intent);
            }
        });

        initChk = true;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        report_history.setVisibility(View.INVISIBLE);
        if (getActivity() != null && getActivity() instanceof MoldeMainActivity) {
            MoldeSearchMapInfoEntity entity = ((MoldeMainActivity) getActivity()).getMapInfoResultData();
            MoldeSearchMapHistoryEntity historyEntity = ((MoldeMainActivity) getActivity()).getMapHistoryResultData();
            if (entity != null) {
                Toast.makeText(getContext(), entity.toString(), Toast.LENGTH_LONG).show();
                lat = entity.getMapLat();
                lng = entity.getMapLng();
                name = entity.getName();
                telNo = entity.getTelNo();
                loc_search_input.setText(name);
                report_history_header.setElevation(12);
                report_history.bringToFront();
                report_history.setVisibility(View.VISIBLE);
                initChk = false;
                if(MoldeSearchMapInfoActivity.checkBackPressed == false){
                    Animation trans_to_little_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_little_up);
                    map_view_layout.startAnimation(trans_to_little_up);
                }
                report_history.setClickable(true);
            }else if(entity == null && historyEntity != null){
                Toast.makeText(getContext(), historyEntity.toString(), Toast.LENGTH_LONG).show();
                lat = historyEntity.getMapLat();
                lng = historyEntity.getMapLng();
                name = historyEntity.getName();
                telNo = historyEntity.getTelNo();
                loc_search_input.setText(name);
                report_history_header.setElevation(12);
                report_history.bringToFront();
                report_history.setVisibility(View.VISIBLE);
                initChk = false;
                if(MoldeSearchMapInfoActivity.checkBackPressed == false) {
                    Animation trans_to_little_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_little_up);
                    map_view_layout.startAnimation(trans_to_little_up);
                }
                report_history.setClickable(true);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MoldeMainActivity)context).setOnKeyBackPressedListener(this);
        if(report_history != null){
            Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
            report_history.startAnimation(trans_to_down);
            report_history.setVisibility(View.INVISIBLE);
        }
    }

    public void makeMarker(){
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng myLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        markerOptions.position(myLocation);
        markerOptions.title(name);
        markerOptions.snippet(telNo);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon));
        mMap.addMarker(markerOptions).showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng moveLoc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moveLoc, 17));


        if(initChk == false){
            makeMarker();
        }

        /*
        지도 UI 설정 ZOOM 레벨 설정
         */

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);



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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(Double.parseDouble(lat) == marker.getPosition().latitude && Double.parseDouble(lng) == marker.getPosition().longitude && !name.equals("")){
                    report_history.setVisibility(View.VISIBLE);
                    Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
                    Animation trans_to_little_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_little_up);
                    report_history.startAnimation(trans_to_up);
                    map_view_layout.startAnimation(trans_to_little_up);
                    backChk = false;
                    report_history.setClickable(true);
                }
                return false;
            }
        });
    }

    public void getMyLocation() {
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        final long minTime = 0;
        final float minDistance = 0;
        MyLocationListener myLocationListener = new MyLocationListener();
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
    }

    @Override
    public void onBackKey(){
        if(backChk == false && initChk == false){
            Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
            Animation trans_to_little_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_little_down);
            report_history.startAnimation(trans_to_down);
            report_history.setVisibility(View.INVISIBLE);
            map_view_layout.startAnimation(trans_to_little_down);
            backChk = true;
            report_history.setClickable(false);
        }else if(backChk == true && initChk == false){
            Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
            Animation trans_to_little_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_little_up);
            report_history.startAnimation(trans_to_up);
            report_history.setVisibility(View.VISIBLE);
            map_view_layout.startAnimation(trans_to_little_up);
            backChk = false;
            report_history.setClickable(false);
        }
    }

    /****************************************/
    public class MyLocationListener implements LocationListener {
        //위치정보 보여주기
        //구글 맵 이동
        int syncCount = 0;
        @Override
        public void onLocationChanged(Location location) {
            if(syncCount == 0) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLng position = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

                mMap.addMarker(
                        new MarkerOptions().position(position).title("내 위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon))
                ).showInfoWindow();
                syncCount++;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    }

    public void setMapHistoryBoundaryClick(){
        report_history.setClickable(false);
    }
    /****************************************/


}
