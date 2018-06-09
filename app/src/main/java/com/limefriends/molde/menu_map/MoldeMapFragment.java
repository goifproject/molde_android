package com.limefriends.molde.menu_map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
//import com.google.maps.android.clustering.ClusterManager;
import com.limefriends.molde.R;
import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.menu_feed.entity.MoldeFeedEntitiy;
import com.limefriends.molde.menu_map.callbackMethod.MoldeMapReportPagerAdapterCallback;
//import com.limefriends.molde.menu_map.entity.MoldeSearchMapClusterEntity;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapHistoryEntity;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapInfoEntity;
import com.limefriends.molde.menu_map.reportCard.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMapFragment extends Fragment
        implements OnMapReadyCallback,
        MoldeMainActivity.onKeyBackPressedListener,
        MoldeMapReportPagerAdapterCallback,
        MoldeMyFavoriteInfoMapDialog.MoldeApplyMyFavoriteInfoCallback {
    //맵 구성
    @BindView(R.id.map_ui)
    RelativeLayout map_ui;
    @BindView(R.id.map_view_layout)
    LinearLayout map_view_layout;

    @BindView(R.id.loc_search_bar)
    LinearLayout search_bar;
    @BindView(R.id.loc_search_input)
    TextView loc_search_input;

    //하단에 구성한 옵션
    @BindView(R.id.map_option_layout)
    RelativeLayout map_option_layout;
    @BindView(R.id.my_loc_button)
    ImageButton my_loc_button;
    @BindView(R.id.favorite_button)
    ImageButton favorite_button;
    @BindView(R.id.favorite_new)
    ImageView favorite_new;
    @BindView(R.id.report_button)
    ImageButton report_button;

    //만약 로딩 중이거나 권한이 없을 때
    @BindView(R.id.map_view_progress)
    RelativeLayout map_view_progress;
    @BindView(R.id.progress_loading)
    ProgressBar progress_loading;
    @BindView(R.id.request_gps_button)
    Button request_gps_button;

    //신고 리스트 보여주는 리스트
    @BindView(R.id.report_card_view_layout)
    FrameLayout report_card_view_layout;
    @BindView(R.id.report_card_view_pager)
    ViewPager report_card_view_pager;

    SupportMapFragment mapView;
    private static GoogleMap mMap;
    private String lat = "37.499597";
    private String lng = "127.031372";
    private String searchName = "";
    private String telNo = "";
    private int moveCnt = 0;
    private boolean backChk = false;
    private boolean initChk = false;
    private boolean myLocChange = false;
    private boolean afterSearch = false;
    private LocationManager manager;
    private MyLocationListener myLocationListener;
    private Marker myMarker;
    private ArrayList<Marker> reportInfohMarkers;
    private ArrayList<Marker> reportRedMarkerList;
    private ArrayList<Marker> reportGreenMarkerList;
    private ArrayList<Marker> reportWhiteMarkerList;
    private ArrayList<ReportCardItem> reportCardItemList;
    private ArrayList<Integer> reportCardPositionList;
    private ReportCardPagerAdapter reportCardAdapter;
    private boolean firstPresentReportCard = false;
    private final int REQUEST_LOCATION = 1;
    private MoldeFeedEntitiy feedData;
    private Marker feedMarker;

    //public ClusterManager<MoldeSearchMapClusterEntity> clusterManager;
    public ShadowTransformer reportCardShadowTransformer;
    public long gpsRequestTime = 0;
    public boolean gpsEnable = false;
    public ArrayList<Long> beforeCallApplyMethodTimeList;
    public static SparseArrayCompat mapFragmentSparseArrayCompat;


    public static MoldeMapFragment newInstance() {
        mapFragmentSparseArrayCompat = new SparseArrayCompat();
        return new MoldeMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, view);
        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);

        if (moveCnt == 0) {
            getMyLocation();

            //색상 별 마커 객체 생성
            reportRedMarkerList = new ArrayList<Marker>();
            reportGreenMarkerList = new ArrayList<Marker>();
            reportWhiteMarkerList = new ArrayList<Marker>();

            //뷰페이저어댑터 생성 및 콜백 지정
            reportInfohMarkers = new ArrayList<Marker>();
            reportCardItemList = new ArrayList<ReportCardItem>();
            reportCardAdapter = new ReportCardPagerAdapter(getContext());
            reportCardAdapter.setCallback(this);
        } else {
            map_view_progress.setVisibility(View.INVISIBLE);

            if (reportInfohMarkers.size() > 0) {
                if (mapFragmentSparseArrayCompat.get(R.string.reportRedhMarkerList) != null) {
                    reportRedMarkerList = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportRedhMarkerList);
                }
                if (mapFragmentSparseArrayCompat.get(R.string.reportGreenhMarkerList) != null) {
                    reportGreenMarkerList = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportGreenhMarkerList);
                }
                if (mapFragmentSparseArrayCompat.get(R.string.reportWhitehMarkerList) != null) {
                    reportWhiteMarkerList = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportWhitehMarkerList);
                }
                if (mapFragmentSparseArrayCompat.get(R.string.reportInfohMarkers) != null) {
                    reportInfohMarkers = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportInfohMarkers);
                }
                if (mapFragmentSparseArrayCompat.get(R.string.reportCardItemList) != null) {
                    reportCardItemList = (ArrayList<ReportCardItem>) mapFragmentSparseArrayCompat.get(R.string.reportCardItemList);
                }
                reportCardAdapter = new ReportCardPagerAdapter(getContext());
                reportCardAdapter.setCallback(this);
                for (int i = 0; i < reportInfohMarkers.size(); i++) {
                    reportCardAdapter.addCardItem(new ReportCardItem(reportInfohMarkers.get(i).getTitle(), reportInfohMarkers.get(i).getSnippet()));
                }
                reportCardShadowTransformer = new ShadowTransformer(report_card_view_pager, reportCardAdapter);
                report_card_view_pager.setAdapter(reportCardAdapter);
                firstPresentReportCard = true;
                report_card_view_pager.setPageTransformer(false, reportCardShadowTransformer);
                report_card_view_pager.setOffscreenPageLimit(reportCardItemList.size());
                reportCardShadowTransformer.enableScaling(true);
                if (mapFragmentSparseArrayCompat.get(R.string.currMarkerPosition) != null) {
                    report_card_view_pager.setCurrentItem((int) mapFragmentSparseArrayCompat.get(R.string.currMarkerPosition));
                }
                report_card_view_layout.setVisibility(View.VISIBLE);
                backChk = true;
            }
        }

        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "검색 창으로 이동", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), MoldeSearchMapInfoActivity.class);
                intent.putExtra("searchName", searchName);
                startActivity(intent);
            }
        });

        map_ui.bringToFront();
        report_card_view_layout.setVisibility(View.INVISIBLE);

        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "즐겨찾기 페이지로 넘어가기", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), MoldeMyFavoriteActivity.class);
                startActivity(intent);
            }
        });

        favorite_new.setElevation(12);


        my_loc_button.setElevation(8);
        my_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "위치 가져오기 기능", Toast.LENGTH_LONG).show();
                loc_search_input.setText(R.string.search);
                searchName = "검색하기";
                myLocChange = true;
                getMyLocation();
            }
        });

        report_button.setElevation(8);
        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "신고 페이지로 넘어가기", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), MoldeReportActivity.class);
                startActivity(intent);
            }
        });

        request_gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                    return;
                }
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

        if (getArguments() != null) {
            //피드 프래그먼트에서 받아온 피드객체 데이터
            feedData = new MoldeFeedEntitiy(
                    getArguments().getString("reportFeedAddress"),
                    getArguments().getString("reportFeedDetailAddress"),
                    getArguments().getInt("reportFeedMarkerId"),
                    getArguments().getString("reportFeedThumbnail"),
                    getArguments().getString("reportFeedDate"),
                    new LatLng(
                            getArguments().getDouble("reportFeedLocationLat"),
                            getArguments().getDouble("reportFeedLocationLng")
                    )
            );
            Log.e("feedData", feedData.toString());
        }
        if (searchName.equals("검색하기")) {
            map_view_progress.setVisibility(View.INVISIBLE);
            report_card_view_layout.bringToFront();
            report_card_view_layout.setVisibility(View.VISIBLE);
            map_option_layout.setVisibility(View.INVISIBLE);
            if (afterSearch == false) {
                initChk = false;
                moveCnt++;
                afterSearch = true;
                return;
            }
        }
        if (getActivity() != null && getActivity() instanceof MoldeMainActivity) {
            MoldeSearchMapInfoEntity entity = ((MoldeMainActivity) getActivity()).getMapInfoResultData();
            MoldeSearchMapHistoryEntity historyEntity = ((MoldeMainActivity) getActivity()).getMapHistoryResultData();
            if (entity != null) {
                map_view_progress.setVisibility(View.INVISIBLE);
                //Toast.makeText(getContext(), entity.toString(), Toast.LENGTH_LONG).show();
                lat = entity.getMapLat();
                lng = entity.getMapLng();
                searchName = entity.getName();
                telNo = entity.getTelNo();
                loc_search_input.setText(searchName);
                report_card_view_layout.bringToFront();
                report_card_view_layout.setVisibility(View.VISIBLE);
                map_option_layout.setVisibility(View.INVISIBLE);
                initChk = false;
                if (MoldeSearchMapInfoActivity.checkBackPressed == false && backChk == false) {
                    //이곳에 좌표를 기준으로 검색 메서드를 추가해야함
                    moveCnt++;
                }
            } else if (entity == null && historyEntity != null) {
                map_view_progress.setVisibility(View.INVISIBLE);
                //Toast.makeText(getContext(), historyEntity.toString(), Toast.LENGTH_LONG).show();
                lat = historyEntity.getMapLat();
                lng = historyEntity.getMapLng();
                searchName = historyEntity.getName();
                telNo = historyEntity.getTelNo();
                loc_search_input.setText(searchName);
                report_card_view_layout.bringToFront();
                report_card_view_layout.setVisibility(View.VISIBLE);
                map_option_layout.setVisibility(View.INVISIBLE);
                initChk = false;
                if (MoldeSearchMapInfoActivity.checkBackPressed == false && backChk == false) {
                    //이곳에 좌표를 기준으로 검색 메서드를 추가해야함
                    moveCnt++;
                }

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MoldeMainActivity) context).setOnKeyBackPressedListener(this);
        if (report_card_view_layout != null) {
            Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
            report_card_view_layout.startAnimation(trans_to_down);
            report_card_view_layout.setVisibility(View.INVISIBLE);
        }
    }

    /*private void setupClusterer(LatLng moveLoc) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moveLoc, 15));
        clusterManager = new ClusterManager<MoldeSearchMapClusterEntity>(getContext(), mMap);
        mMap.setOnCameraChangeListener(clusterManager);
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        if (feedData != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(feedData.getReportFeedLocation(), 15));
            makeFeedMarker(feedData);
            return;
        }

        LatLng moveLoc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        if (searchName.equals("")) {
        } else if (!searchName.equals("")) {
            if (searchName.charAt(searchName.length() - 1) == '동') {
                StringTokenizer placeInfo = new StringTokenizer(searchName, " ");
                String si = placeInfo.nextToken();
                String gu = placeInfo.nextToken();
                String dong = placeInfo.nextToken();
                if (gu.charAt(gu.length() - 1) == '구' && dong.charAt(dong.length() - 1) == '동') {
                    //주변 위치에 따라 검색해오기
                    if (afterSearch == false) {
                        makeSearchMarkers(moveLoc);
                        afterSearch = true;
                    }
                }
            } else {
                if (afterSearch == false) {
                    makeSearchMarkers(moveLoc);
                    makeSearchMarker(moveLoc);
                    afterSearch = true;
                }
            }
        }

        /*********************** Map Click ***********************/
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                    Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
                    report_card_view_layout.startAnimation(trans_to_down);
                    report_card_view_layout.setVisibility(View.GONE);
                    report_card_view_layout.setClickable(false);
                    map_option_layout.setVisibility(View.VISIBLE);
                    backChk = true;
                }
            }
        });
        /*********************** Map long Click ***********************/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                makeNewMarker(latLng);
            }
        });


        /*********************** Marker Click ***********************/
        //mMap.setOnMarkerClickListener(clusterManager);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals("내 위치")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.my_location_icon)));
                    if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                        Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
                        report_card_view_layout.startAnimation(trans_to_down);
                        report_card_view_layout.setVisibility(View.GONE);
                        report_card_view_layout.setClickable(false);
                        map_option_layout.setVisibility(View.VISIBLE);
                        backChk = true;
                    }
                    return false;
                }

                //즐겨찾기 추가 기능
                if (marker.getTitle().contains("★")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));
                    if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                        Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
                        report_card_view_layout.startAnimation(trans_to_down);
                        report_card_view_layout.setVisibility(View.GONE);
                        report_card_view_layout.setClickable(false);
                        map_option_layout.setVisibility(View.VISIBLE);
                        backChk = true;
                        MoldeMyFavoriteInfoMapDialog moldeMyFavoriteInfoMapDialog = MoldeMyFavoriteInfoMapDialog.getInstance();
                        moldeMyFavoriteInfoMapDialog.setCallback(MoldeMapFragment.this, marker);
                        Bundle bundle = new Bundle();
                        bundle.putString("markerTitle", marker.getTitle().replace("★", ""));
                        bundle.putString("markerInfo", marker.getSnippet());
                        bundle.putDouble("markerLat", marker.getPosition().latitude);
                        bundle.putDouble("markerLng", marker.getPosition().longitude);
                        moldeMyFavoriteInfoMapDialog.setArguments(bundle);
                        moldeMyFavoriteInfoMapDialog.show(((MoldeMainActivity) getContext()).getSupportFragmentManager(), "bottomSheet");
                    } else {
                        MoldeMyFavoriteInfoMapDialog moldeMyFavoriteInfoMapDialog = MoldeMyFavoriteInfoMapDialog.getInstance();
                        moldeMyFavoriteInfoMapDialog.setCallback(MoldeMapFragment.this, marker);
                        Bundle bundle = new Bundle();
                        bundle.putString("markerTitle", marker.getTitle().replace("★", ""));
                        bundle.putString("markerInfo", marker.getSnippet());
                        bundle.putDouble("markerLat", marker.getPosition().latitude);
                        bundle.putDouble("markerLng", marker.getPosition().longitude);
                        moldeMyFavoriteInfoMapDialog.setArguments(bundle);
                        moldeMyFavoriteInfoMapDialog.show(((MoldeMainActivity) getContext()).getSupportFragmentManager(), "bottomSheet");
                    }
                    return false;
                }

                if (feedData != null) {
                    if (marker.getTitle().equals(feedData.getReportFeedAddress())) {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.my_location_icon)));
                        if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                            Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
                            report_card_view_layout.startAnimation(trans_to_down);
                            report_card_view_layout.setVisibility(View.GONE);
                            report_card_view_layout.setClickable(false);
                            map_option_layout.setVisibility(View.VISIBLE);
                            backChk = true;
                        }
                        return false;
                    }
                }

                if (marker.getTitle().equals(searchName)) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.my_location_icon)));
                    if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                        Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
                        report_card_view_layout.startAnimation(trans_to_down);
                        report_card_view_layout.setVisibility(View.GONE);
                        report_card_view_layout.setClickable(false);
                        map_option_layout.setVisibility(View.VISIBLE);
                        backChk = true;
                    }
                    return false;
                }

                if (moveCnt > 0 && backChk == true) {
                    report_card_view_layout.setVisibility(View.VISIBLE);
                    map_option_layout.setVisibility(View.INVISIBLE);
                    Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
                    report_card_view_layout.startAnimation(trans_to_up);
                    report_card_view_layout.bringToFront();
                    backChk = false;
                }

                if (reportInfohMarkers != null) {
                    for (int i = 0; i < reportInfohMarkers.size(); i++) {
                        if (marker.getTitle().equals(reportInfohMarkers.get(i).getTitle())) {
                            for (Marker redMarker : reportRedMarkerList) {
                                if (marker.getTitle().equals(redMarker.getTitle())) {
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_red)));
                                }
                            }
                            for (Marker greenMarker : reportGreenMarkerList) {
                                if (marker.getTitle().equals(greenMarker.getTitle())) {
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_green)));
                                }
                            }
                            for (Marker whiteMarker : reportWhiteMarkerList) {
                                if (marker.getTitle().equals(whiteMarker.getTitle())) {
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_white)));
                                }
                            }
                            report_card_view_pager.setCurrentItem(i, false);
                            return false;
                        }
                    }
                }

                return false;
            }
        });

        /*********************** Map detect Deselect Marker ***********************/
        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                if (marker.getTitle().equals("내 위치")) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon));
                } else if (marker.getTitle().contains("★")) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pick));
                } else if (marker.getTitle().equals(searchName)) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon));
                } else if (feedData != null) {
                    if (marker.getTitle().equals(feedData.getReportFeedAddress())) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon));
                    }
                    for (Marker redMarker : reportRedMarkerList) {
                        if (marker.getTitle().equals(redMarker.getTitle())) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
                        }
                    }
                    for (Marker greenMarker : reportGreenMarkerList) {
                        if (marker.getTitle().equals(greenMarker.getTitle())) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
                        }
                    }
                    for (Marker whiteMarker : reportWhiteMarkerList) {
                        if (marker.getTitle().equals(whiteMarker.getTitle())) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_white));
                        }
                    }
                } else {
                    for (Marker redMarker : reportRedMarkerList) {
                        if (marker.getTitle().equals(redMarker.getTitle())) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
                        }
                    }
                    for (Marker greenMarker : reportGreenMarkerList) {
                        if (marker.getTitle().equals(greenMarker.getTitle())) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
                        }
                    }
                    for (Marker whiteMarker : reportWhiteMarkerList) {
                        if (marker.getTitle().equals(whiteMarker.getTitle())) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_white));
                        }
                    }
                }
            }
        });

    }

    @Override
    public void applyMyFavoriteInfo(String title, String info, Marker marker) {
        marker.setTitle("★" + title);
        marker.setSnippet(info);
    }

    private Bitmap sizeUpMapIcon(int imageId) {
        Bitmap original = BitmapFactory.decodeResource(getResources(), imageId);
        int resizeWidth = (int) (original.getWidth() * 1.2);
        int targetHeight = (int) (original.getHeight() * 1.2);
        Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    //GPS 권한체크 및 얻기
    public void onPermissionCheck(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    progress_loading.setVisibility(View.INVISIBLE);
                    request_gps_button.setVisibility(View.VISIBLE);
                    return;
                }
            }
            getMyLocation();
        } else {
            return;
        }
    }

    public void getMyLocation() {
        manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        gpsEnable = false;
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnable = true;
        }
        if (gpsEnable) {
            final long minTime = 1500;
            final float minDistance = 100;
            if (myLocationListener == null) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                    return;
                }
                myLocationListener = new MyLocationListener();
            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
            gpsRequestTime = System.currentTimeMillis();
        } else {
            Toast.makeText(getContext(), "GPS를 사용할 수 있도록 켜주셔야 사용이 가능합니다.", Toast.LENGTH_LONG).show();
            map_view_progress.setVisibility(View.INVISIBLE);
        }
    }

    public void clearReportInfoMarkers() {
        for (Marker marker : reportInfohMarkers) {
            marker.remove();
        }
        reportInfohMarkers.clear();
        reportCardItemList.clear();
        reportCardAdapter.removeAllCardItem();
        report_card_view_pager.setAdapter(reportCardAdapter);
    }


    public class MyLocationListener implements LocationListener {
        //위치정보 보여주기
        //구글 맵 이동
        int locChangeCount = 0;

        @Override
        public void onLocationChanged(Location location) {
            if (searchName.equals("") || myLocChange == true) {
                /*if(System.currentTimeMillis() - gpsRequestTime > 3000){
                    Toast.makeText(getContext(), "건물 안에서는 더 오랜 시간이 걸립니다", Toast.LENGTH_SHORT).show();
                }*/
                if (locChangeCount == 0) {
                    /*double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng myLocation = new LatLng(latitude, longitude);*/
                    lat = Double.toString(location.getLatitude());
                    lng = Double.toString(location.getLongitude());
                    LatLng myLocation = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
                    if (myMarker != null) {
                        myMarker.remove();
                    }
                    myMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(myLocation)
                                    .title("내 위치")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon)
                                    )
                    );
                    locChangeCount++;
                    myLocChange = false;
                    if (moveCnt > 0) {
                        if (reportInfohMarkers.size() > 0 || reportCardItemList.size() > 0) {
                            clearReportInfoMarkers();
                        }
                        beforeCallApplyMethodTimeList = new ArrayList<Long>();
                        reportCardPositionList = new ArrayList<Integer>();
                        makeRandomMarkers(myLocation);

                        report_card_view_layout.setVisibility(View.VISIBLE);
                        map_option_layout.setVisibility(View.INVISIBLE);
                        Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
                        report_card_view_layout.startAnimation(trans_to_up);
                        report_card_view_layout.bringToFront();
                        report_card_view_layout.setClickable(true);
                        backChk = false;
                        initChk = false;
                    }
                    moveCnt++;
                }
            }
            if (manager != null && myLocationListener != null) {
                manager.removeUpdates(myLocationListener);
                myLocationListener = null;
                map_view_progress.setVisibility(View.INVISIBLE);
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

    //빈 정보의 마커 생성 및 후에 정보 추가 및 즐겨찾기
    public void makeNewMarker(LatLng latLng) {
        Marker newMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title("★")
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pick))
        );
    }

    //피드 프래그먼트에서 받아온 데이터를 기반으로 마커 정보를 생성
    public void makeFeedMarker(MoldeFeedEntitiy feedData) {
        if (feedMarker != null) {
            feedMarker.remove();
        }
        feedMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(feedData.getReportFeedLocation())
                        .title(feedData.getReportFeedAddress())
                        .snippet(feedData.getReportFeedDetailAddress())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon))
        );
        feedMarker.showInfoWindow();
        clearReportInfoMarkers();
        makeSearchMarkers(feedData.getReportFeedLocation());
        report_card_view_layout.setVisibility(View.VISIBLE);
        map_option_layout.setVisibility(View.INVISIBLE);
        Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
        report_card_view_layout.startAnimation(trans_to_up);
        report_card_view_layout.bringToFront();
        report_card_view_layout.setClickable(true);
        backChk = false;
        initChk = false;
    }

    //검색 후 하나의 마커 생성 및 검색정보 대입
    public void makeSearchMarker(LatLng moveLoc) {
        Marker newMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(moveLoc)
                        .title(searchName)
                        .snippet(telNo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon))
        );
        newMarker.showInfoWindow();
    }

    //검색 후 주변의 여러 신고장소 정보 신고 카드뷰에 추가 및 마커 추가
    public void makeSearchMarkers(LatLng moveLoc) {
        beforeCallApplyMethodTimeList = new ArrayList<Long>();
        reportCardPositionList = new ArrayList<Integer>();
        makeRandomMarkers(moveLoc);
    }

    //랜덤으로 마커 정보 10개 추가 및 카드뷰 추가 - 테스트용
    public void makeRandomMarkers(LatLng moveLoc) {
        //setupClusterer(moveLoc);
        double start = -0.000000001;
        double end = 0.000000001;
        double rng = (end - start) + 0.01;
        Random randomGenerator = new Random();
        for (int i = 0; i < 10; i++) {
            double rndValLat = (randomGenerator.nextDouble() * rng) + start;
            double rndValLng = (randomGenerator.nextDouble() * rng) + start;
            LatLng latLng = new LatLng(moveLoc.latitude + rndValLat, moveLoc.longitude + rndValLng);

            if (i % 3 == 0) {
                Marker currMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(i + 1 + "번째")
                        .snippet(latLng.latitude + ", " + latLng.longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red))
                );
                reportInfohMarkers.add(currMarker);
                reportRedMarkerList.add(currMarker);

                reportCardItemList.add(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
                reportCardAdapter.addCardItem(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
            } else if (i % 3 == 1) {
                Marker currMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(i + 1 + "번째")
                        .snippet(latLng.latitude + ", " + latLng.longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green))
                );
                reportInfohMarkers.add(currMarker);
                reportGreenMarkerList.add(currMarker);

                reportCardItemList.add(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
                reportCardAdapter.addCardItem(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
            } else {
                Marker currMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(i + 1 + "번째")
                        .snippet(latLng.latitude + ", " + latLng.longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_white))
                );
                reportInfohMarkers.add(currMarker);
                reportWhiteMarkerList.add(currMarker);

                reportCardItemList.add(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
                reportCardAdapter.addCardItem(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
            }
        }
        reportCardShadowTransformer = new ShadowTransformer(report_card_view_pager, reportCardAdapter);
        report_card_view_pager.setAdapter(reportCardAdapter);
        firstPresentReportCard = true;
        report_card_view_pager.setPageTransformer(false, reportCardShadowTransformer);
        report_card_view_pager.setOffscreenPageLimit(reportCardItemList.size());
        reportCardShadowTransformer.enableScaling(true);

        mapFragmentSparseArrayCompat.append(R.string.reportInfohMarkers, reportInfohMarkers);
        mapFragmentSparseArrayCompat.append(R.string.reportRedhMarkerList, reportRedMarkerList);
        mapFragmentSparseArrayCompat.append(R.string.reportGreenhMarkerList, reportGreenMarkerList);
        mapFragmentSparseArrayCompat.append(R.string.reportWhitehMarkerList, reportWhiteMarkerList);
        mapFragmentSparseArrayCompat.append(R.string.reportCardItemList, reportCardItemList);
    }

    //현재 신고 카드뷰 페이지 감지 및 데이터 바인딩
    @Override
    public void applyReportCardInfo(int position) {
        if (reportInfohMarkers != null) {
            if (initChk == false && backChk == false) {
                if (firstPresentReportCard == true) {
                    if (position == 1) {
                        Marker marker = reportInfohMarkers.get(0);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                        marker.showInfoWindow();
                        firstPresentReportCard = false;
                        return;
                    }
                }
            }

            if (beforeCallApplyMethodTimeList != null && beforeCallApplyMethodTimeList != null) {
                beforeCallApplyMethodTimeList.add(System.currentTimeMillis());
                reportCardPositionList.add(position);
                if (beforeCallApplyMethodTimeList.size() == 2) {
                    if (beforeCallApplyMethodTimeList.get(1) - beforeCallApplyMethodTimeList.get(0) < 0.01) {
                        beforeCallApplyMethodTimeList.remove(1);
                        return;
                    } else {
                        int currPosition = reportCardPositionList.get(0);
                        Marker marker = reportInfohMarkers.get(currPosition);
                        mapFragmentSparseArrayCompat.append(R.string.currMarkerPosition, currPosition);
                        for (Marker redMarker : reportRedMarkerList) {
                            if (marker.getTitle().equals(redMarker.getTitle())) {
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_red)));
                            }
                        }
                        for (Marker greenMarker : reportGreenMarkerList) {
                            if (marker.getTitle().equals(greenMarker.getTitle())) {
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_green)));
                            }
                        }
                        for (Marker whiteMarker : reportWhiteMarkerList) {
                            if (marker.getTitle().equals(whiteMarker.getTitle())) {
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_white)));
                            }
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                        marker.showInfoWindow();
                        beforeCallApplyMethodTimeList.clear();
                        reportCardPositionList.clear();
                    }
                }
            }
        }
    }

    @Override
    public void onBackKey() {
        if (backChk == false && initChk == false) {
            Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
            report_card_view_layout.startAnimation(trans_to_down);
            report_card_view_layout.setVisibility(View.GONE);
            report_card_view_layout.setClickable(false);
            map_option_layout.setVisibility(View.VISIBLE);
            backChk = true;
        }
    }

}
