package com.limefriends.molde.ui.map.main;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.PermissionUtil;
import com.limefriends.molde.entity.favorite.FavoriteEntity;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.ui.map.favorite.MapFavoriteActivity;
import com.limefriends.molde.ui.map.favorite.MapFavoriteDialog;
import com.limefriends.molde.ui.map.main.reportCard.MapReportCardListDialog;
import com.limefriends.molde.ui.map.main.reportCard.MapReportCardPagerAdapter;
import com.limefriends.molde.ui.map.main.reportCard.MapReportCardItem;
import com.limefriends.molde.ui.map.main.reportCard.ShadowTransformer;
import com.limefriends.molde.ui.map.report.ReportActivity;
import com.limefriends.molde.ui.map.search.SearchMapInfoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.limefriends.molde.comm.Constant.Map.*;
import static com.limefriends.molde.comm.Constant.ReportState.ACCEPTED;
import static com.limefriends.molde.comm.Constant.ReportState.CLEAN;
import static com.limefriends.molde.comm.Constant.ReportState.FOUND;
import static com.limefriends.molde.comm.Constant.ReportState.RECEIVING;
import static com.limefriends.molde.comm.utils.PermissionUtil.REQ_CODE;

public class MapFragment extends Fragment implements
        OnMapReadyCallback, MoldeMainActivity.OnKeyBackPressedListener,
        ShadowTransformer.OnPageSelectedCallback,
        MapFavoriteDialog.MoldeApplyMyFavoriteInfoCallback,
        PermissionUtil.PermissionCallback, View.OnClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowCloseListener, GoogleMap.OnMarkerClickListener {

    // 전체 컨테이
    @BindView(R.id.map_container)
    FrameLayout map_container;

    /**
     * 지도
     */
    // 지도 위에 올라가는 검색창, 하단 옵션, 로딩중 레이아웃을 담는 레이아웃
    @BindView(R.id.map_ui)
    RelativeLayout map_ui;

    /**
     * 검색창
     */
    @BindView(R.id.loc_search_bar)
    LinearLayout search_bar;
    @BindView(R.id.loc_search_input)
    TextView loc_search_input;

    /**
     * 하단에 구성한 옵션
     */
    @BindView(R.id.map_option_layout)
    RelativeLayout map_option_layout;
    // 현재 위치
    @BindView(R.id.my_loc_button)
    ImageView my_loc_button;
    // 즐겨찾기
    @BindView(R.id.favorite_button)
    ImageView favorite_button;
    // 즐겨찾기 추가된 거 있는지
    @BindView(R.id.favorite_new)
    ImageView favorite_new;
    // 신고하기
    @BindView(R.id.report_button)
    ImageView report_button;

    /**
     * 만약 로딩 중이거나 권한이 없을 때
     */
    @BindView(R.id.map_view_progress)
    FrameLayout map_view_progress;

    /**
     * 하단 카드뷰
     */
//    @BindView(R.id.report_card_view)
//    FrameLayout report_card_view_layout;
    @BindView(R.id.report_card_view_pager)
    ViewPager report_card_view_pager;

    public static final int ZOOM_CUR_LOCATION = 17;
    public static final int ZOOM_FEED_MARKER = 15;
    public static final int REQ_SEARCH_MAP = 999;
    public static final int REQ_FAVORITE = 996;

    public static final int MARKER_MY_LOCATION = -1;
    public static final int MARKER_MY_LOCATION_HISTORY = -2;
    public static final int MARKER_MY_LOCATION_SEARCH = -3;
    public static final int MARKER_MY_LOCATION_FAVORITE = -4;

    long a;
    private int currentMarkerPosition = -1;
    private double lat = 0.0;
    private double lng = 0.0;
    private boolean isFirst = true;
    private boolean isBackBtnClicked = false;
    private boolean isInit = false;
    private boolean isMyFavoriteActive = false;
    private boolean fromFeed = false;
    private boolean hasMoreToLoad = true;
    private List<Marker> reportInfoMarkers;
    private List<MapReportCardItem> mapReportCardItemList;

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private LocationManager manager;
    private MyLocationListener myLocationListener;
    private MapReportCardPagerAdapter reportCardAdapter;
    private PermissionUtil mPermission;
    private MoldeRestfulService.Feed feedService;

    /**
     * 초기화 작업 - 생명주기
     */
    @Override
    public void onAttach(Context context) {
        /*
            * onAttach 에서는 context 를 넘겨받기 때문에 인터페이스 연결할 사항을 처리한다
            * 다만 onCreateView 에서 View 를 binding 하기 이전까지는 모든 뷰가 null 이기 때문에 해당 사항을 처리하려면
            * onViewCrated 에서 해야 함
            * add 할 때 호출됨
         */
        super.onAttach(context);
        ((MoldeMainActivity) context).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // 처음 생성시 호출되고 호출되지 않음 - 초기화 작업에 필요
        super.onCreate(savedInstanceState);
        reportInfoMarkers = new ArrayList<>();
        mapReportCardItemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // replace 할 때마다 호출됨. 매니저에 add, replace 되는 순간 반드시 호출되는 듯
        // TODO 캐싱 문제를 좀 더 살펴보자. 일단은 안드로이드에서 처리해 주고 있다고 하니 굳이 추가로 캐싱을 하지 않아도 될 듯
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // 뷰
        setupViews(view);

        setupListener();

        // 카드뷰 페이저 & 캐싱
        setCardViewPagerAdapter();

        // 데이터 호출
        setupData();

        return view;
    }


    /**
     * 뷰 세팅
     */
    private void setupViews(View view) {

        ButterKnife.bind(this, view);
        SupportMapFragment mapView = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);

        map_ui.bringToFront();
        report_card_view_pager.setVisibility(View.GONE);
        favorite_new.setElevation(12);
        my_loc_button.setElevation(8);
        report_button.setElevation(8);
    }

    private void setupListener() {

        // 검색창으로 이동
        search_bar.setOnClickListener(this);

        // 즐겨찾기 창으로 이동
        favorite_button.setOnClickListener(this);

        // 신고하기
        report_button.setOnClickListener(this);

        // 현재 위치 찾기
        my_loc_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.loc_search_bar:
                intent = new Intent();
                intent.setClass(v.getContext(), SearchMapInfoActivity.class);
                startActivityForResult(intent, REQ_SEARCH_MAP,
                        ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
            case R.id.favorite_button:
                if (getFirebaseAuth() != null && getFirebaseAuth().getUid() != null) {
                    intent = new Intent();
                    intent.setClass(v.getContext(), MapFavoriteActivity.class);
                    startActivityForResult(intent, REQ_FAVORITE);
                } else {
                    snackBar(getText(R.string.toast_require_signin).toString());
                }
                break;
            case R.id.report_button:
                if (getFirebaseAuth() != null && getFirebaseAuth().getUid() != null) {
                    intent = new Intent();
                    intent.setClass(v.getContext(), ReportActivity.class);
                    startActivity(intent);
                } else {
                    snackBar(getText(R.string.toast_require_signin).toString());
                }
                break;
            case R.id.my_loc_button:
                getMyLocation();
                break;
        }
    }

    // 하단 뷰페이저 세팅
    private void setCardViewPagerAdapter() {
        if (reportCardAdapter == null) {
            reportCardAdapter = new MapReportCardPagerAdapter(this);
        }
        ShadowTransformer shadowTransformer
                = new ShadowTransformer(report_card_view_pager, reportCardAdapter, this);
        shadowTransformer.enableScaling(true);
        report_card_view_pager.setAdapter(reportCardAdapter);
        report_card_view_pager.setOnPageChangeListener(shadowTransformer);
    }

    // 하단 뷰페이저 숨기기
    private void hideCardView() {
        if (report_card_view_pager.getVisibility() == View.VISIBLE) {
            Animation trans_to_down
                    = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
            report_card_view_pager.startAnimation(trans_to_down);
            report_card_view_pager.setVisibility(View.GONE);
            report_card_view_pager.setClickable(false);
            map_option_layout.setVisibility(View.VISIBLE);
            isBackBtnClicked = true;
        }
    }

    // 하단 뷰페이저 보이기
    private void showCardView() {
        Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
        report_card_view_pager.setVisibility(View.VISIBLE);
        report_card_view_pager.startAnimation(trans_to_up);
        report_card_view_pager.bringToFront();
        map_option_layout.setVisibility(View.INVISIBLE);
        isBackBtnClicked = false;
    }

    // 프로그래스바 숨기기
    private void hideProgress() {
        map_view_progress.setVisibility(View.INVISIBLE);
    }

    // 프로그래스바 보이기
    private void showProgress() {
        map_view_progress.setVisibility(View.VISIBLE);
    }

    // 스낵바
    public void snackBar(String message) {
        Snackbar.make(map_container, message, 300).show();
    }


    /**
     * 맵 세팅1 - 초기 세팅
     */
    // 맵이 초기화 될 때 호출되는 콜백 함수
    @Override
    public void onMapReady(GoogleMap googleMap) {

        setupMapUi(googleMap);

        mMap.setOnMapClickListener(this);

        mMap.setOnMapLongClickListener(this);

        mMap.setOnMarkerClickListener(this);

        mMap.setOnInfoWindowCloseListener(this);
    }

    // 구글맵 Ui 세팅
    private void setupMapUi(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
    }

    // 맵 화면 클릭
    @Override
    public void onMapClick(LatLng latLng) {
        hideCardView();
    }

    // 맵 화면 롱클릭
    @Override
    public void onMapLongClick(LatLng latLng) {
        addMarker(
                latLng,
                getText(R.string.marker_title_favorite_location).toString(),
                "",
                R.drawable.ic_map_pick,
                MARKER_MY_LOCATION_FAVORITE);
    }

    // 즐겨찾기 마커 클릭시 보여줄 대화상자
    private void showFavoriteDialog(Marker marker) {
        MapFavoriteDialog mapFavoriteDialog = new MapFavoriteDialog();
        mapFavoriteDialog.setCallback(this, marker);
        Bundle bundle = new Bundle();
        bundle.putString("markerTitle", marker.getTitle().replace("★", ""));
        bundle.putString("markerInfo", marker.getSnippet());
        bundle.putDouble("markerLat", marker.getPosition().latitude);
        bundle.putDouble("markerLng", marker.getPosition().longitude);
        bundle.putBoolean("myFavoriteActive", isMyFavoriteActive);
        mapFavoriteDialog.setArguments(bundle);
        mapFavoriteDialog.show(
                ((MoldeMainActivity) getContext()).getSupportFragmentManager(), "bottomSheet");
    }

    // 마커 클릭
    @Override
    public boolean onMarkerClick(Marker marker) {
        int tagNumber = (int) marker.getTag();
        if (tagNumber >= 0) {
            if (report_card_view_pager.getVisibility() == View.GONE) {
                showCardView();
            }
            applyReportCardInfo(tagNumber);
            enlargeMarkerIcon(marker, mapReportCardItemList.get(tagNumber).getStatus());
        } else {
            switch (tagNumber) {
                case MARKER_MY_LOCATION:
                case MARKER_MY_LOCATION_HISTORY:
                case MARKER_MY_LOCATION_SEARCH:
                    marker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_marker_cur_location)));
                    break;
                case MARKER_MY_LOCATION_FAVORITE:
                    marker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));
                    showFavoriteDialog(marker);
                    break;
            }
            hideCardView();
        }
        return false;
    }

    // 마커 클릭 포커스 해제
    @Override
    public void onInfoWindowClose(Marker marker) {
        int tagNumber = (int) marker.getTag();
        if (tagNumber >= 0) {
            shrinkMarkerIcon(marker,
                    mapReportCardItemList.get((int) marker.getTag()).getStatus());
        } else {
            switch (tagNumber) {
                case MARKER_MY_LOCATION:
                case MARKER_MY_LOCATION_HISTORY:
                case MARKER_MY_LOCATION_SEARCH:
                    marker.setIcon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_map_marker_cur_location));
                    break;
                case MARKER_MY_LOCATION_FAVORITE:
                    marker.setIcon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_map_pick));
                    showFavoriteDialog(marker);
                    break;
            }
        }
    }


    /**
     * 맵 세팅2 - 동적 변화
     */
    // 지도 이동
    private void moveCamera(LatLng location, int zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
    }

    // 마커 추가
    private Marker addMarker(LatLng latLng, String title, String snippet, int icon, int tag) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        if (!title.equals("")) {
            options.title(title);
        }
        if (!snippet.equals("")) {
            options.snippet(snippet);
        }
        if (icon != 0) {
            options.icon(BitmapDescriptorFactory.fromResource(icon));
        }
        Marker marker = mMap.addMarker(options);
        marker.setTag(tag);
        return marker;
    }

    // 피드에서 넘어올 때 마커 추가
    private void addFeedMarkers(FeedEntity feedData) {
        clearReportInfoDataAndMarkers();
        LatLng feedLocation = new LatLng(feedData.getRepLat(), feedData.getRepLon());
        Marker feedMarker = addMarker(
                feedLocation,
                getText(R.string.marker_title_feed_location).toString(),
                feedData.getRepDetailAddr(),
                R.drawable.ic_map_marker_cur_location,
                MARKER_MY_LOCATION);
        feedMarker.showInfoWindow();
        loadData(feedLocation.latitude, feedLocation.longitude);
    }



    // 마커 아이콘 사이즈업
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

    // 사이즈업 할 아이콘 선별
    private void enlargeMarkerIcon(Marker marker, int status) {
        switch (status) {
            case RECEIVING:
            case ACCEPTED:
                marker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_marker_red)));
                break;
            case FOUND:
                marker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_marker_white)));
                break;
            case CLEAN:
                marker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_marker_green)));
                break;
        }
    }

    // 마커 아이콘 사이즈 복귀
    private void shrinkMarkerIcon(Marker marker, int status) {
        switch (status) {
            case RECEIVING:
            case ACCEPTED:
                marker.setIcon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_map_marker_red));
                break;
            case FOUND:
                marker.setIcon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_map_marker_white));
                break;
            case CLEAN:
                marker.setIcon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_map_marker_green));
                break;
        }
    }

    /**
     * 데이터 세팅
     */
    // 화면에 세팅될 때마다 어떤 경로로 데이터를 세팅할지 결정함
    private void setupData() {
        if (isFirst) {
            getMyLocation();
        }
        // 피드에서 데이터를 받아올 경우
        else if (fromFeed) {
            isLoading(true);
            FeedEntity feedEntity = ((MoldeMainActivity) getActivity()).getFeedEntity();
            moveCamera(new LatLng(feedEntity.getRepLat(),
                    feedEntity.getRepLon()), ZOOM_FEED_MARKER);
            addFeedMarkers(feedEntity);
            fromFeed = false;
        }
        // 한 번 데이터를 불러온 후 외부에서 접근할 경우
        else {
            if (currentMarkerPosition != -1 && mapReportCardItemList.size() != 0) {
                applyReportCardInfo(currentMarkerPosition);
                showCardView();
            }
            isBackBtnClicked = true;
        }
    }

    private MoldeRestfulService.Feed getFeedService() {
        if (feedService == null) {
            feedService
                    = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);
        }
        return feedService;
    }

    // 네트워크에서 피드 데이터 받아옴
    private void loadData(final double lat, final double lng) {

        Call<FeedResponseInfoEntityList> call
                = getFeedService().getFeedByDistance(lat, lng);

        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call,
                                   Response<FeedResponseInfoEntityList> response) {

                List<FeedResponseInfoEntity> entityList = response.body().getData();

                if (entityList.size() != 0) {

                    for (int i = 0; i < entityList.size(); i++) {
                        FeedResponseInfoEntity entity = entityList.get(i);
                        LatLng location = new LatLng(entity.getRepLat(), entity.getRepLon());
                        Marker marker = null;
                        switch (entityList.get(i).getRepState()) {
                            // TODO 데이터 제대로 들어가면 변경할 것
                            case RECEIVING:
                                Log.e("호출 확인", "RECEIVING");
                                continue;
                            case ACCEPTED:
                                marker = addMarker(location, entity.getRepDetailAddr(),
                                        location.latitude + ", " + location.longitude,
                                        R.drawable.ic_map_marker_red, i);
                                break;
                            case FOUND:
                                marker = addMarker(location, entity.getRepContents(),
                                        location.latitude + ", " + location.longitude,
                                        R.drawable.ic_map_marker_white, i);
                                break;
                            case CLEAN:
                                marker = addMarker(location, entity.getRepContents(),
                                        location.latitude + ", " + location.longitude,
                                        R.drawable.ic_map_marker_green, i);
                                break;
                        }
                        reportInfoMarkers.add(marker);
                        marker.setTag(reportInfoMarkers.size()-1);
                        mapReportCardItemList.add(
                                new MapReportCardItem(
                                        entity.getRepContents(),
                                        entity.getRepDetailAddr(),
                                        entity.getRepState(),
                                        entity.getRepId(),
                                        entity.getRepDate(),
                                        entity.getRepImg().get(0).getFilepath()));
                    }

                    if (reportInfoMarkers.size() != 0) {

                        updateData(mapReportCardItemList);

                        applyReportCardInfo(0);

                        showCardView();

                    } else {

                        snackBar(getText(R.string.toast_no_feed_place).toString());

                        moveCamera(new LatLng(lat, lng), ZOOM_CUR_LOCATION);

                    }

                } else {

                    snackBar(getText(R.string.toast_no_feed_place).toString());

                    moveCamera(new LatLng(lat, lng), ZOOM_CUR_LOCATION);
                }

                isFirst(false);

                isLoading(false);

            }

            @Override
            public void onFailure(Call<FeedResponseInfoEntityList> call, Throwable t) {
                snackBar(getText(R.string.toast_network_error).toString());

                isLoading(false);
            }
        });
    }

    // 받아온 데이터로 하단 뷰페이저 갱신
    private void updateData(List<MapReportCardItem> data) {
        reportCardAdapter.setData(data);
    }

    // 새로 받아올 때 기존 데이터 캐시 제거
    public void clearReportInfoDataAndMarkers() {
        mMap.clear();
        if (reportInfoMarkers.size() > 0 || mapReportCardItemList.size() > 0) {
            for (Marker marker : reportInfoMarkers) {
                marker.remove();
            }
            reportInfoMarkers.clear();
            mapReportCardItemList.clear();

            reportCardAdapter.removeAllCardItem(mapReportCardItemList);
            /**
             * makeRandomMarker 와 바꾸기만 하면 안 되길래 뭐가 문제인지 파악하지 못했는데
             * 결국 어떤 차이가 있었던 것이 아니라 데이터가 유동적으로 변경된 것이 문제였음
             * 즉, 네트워크에서 받아오는 데이터는 10 -> 0개로 변경이 일어났는데 그것을 계속 어댑터에서만
             * 변경하고 뷰에 반영을 해주지 않아 이전 뷰페이저에서는 2-3개 남아있던 아이템이 계속 살아있었던 것이다
             * 다만 어댑터는 변경되었기 떄문에 어디에서도 호출이 발생하지 않았던 것
             */
            report_card_view_pager.setAdapter(reportCardAdapter);
        }
    }

    // 즐겨찾기, 검색에서 받아온 위치로 데이터 갱신
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_SEARCH_MAP) {
            LatLng defaultLoc = ((MoldeApplication)
                    getActivity().getApplication()).getCurrLocation();
            lat = data.getDoubleExtra("reportLat", defaultLoc.latitude);
            lng = data.getDoubleExtra("reportLng", defaultLoc.longitude);
            String reportName = data.getStringExtra("reportName");
            loc_search_input.setText(reportName);
            clearReportInfoDataAndMarkers();
            addMarker(
                    new LatLng(lat, lng),
                    getText(R.string.marker_title_search_location).toString(),
                    "",
                    R.drawable.ic_map_marker_cur_location,
                    MARKER_MY_LOCATION_HISTORY);

            loadData(lat, lng);
        } else if (resultCode == RESULT_OK && requestCode == REQ_FAVORITE) {
            FavoriteEntity myFavoriteEntity
                    = (FavoriteEntity) data.getSerializableExtra(EXTRA_KEY_FAVORITE_INFO);
            if (myFavoriteEntity != null) {
                lat = myFavoriteEntity.getFavLat();
                lng = myFavoriteEntity.getFavLon();
                loc_search_input.setText("★" + myFavoriteEntity.getFavName());
                isMyFavoriteActive = myFavoriteEntity.isActive();
                clearReportInfoDataAndMarkers();
                addMarker(
                        new LatLng(lat, lng),
                        getText(R.string.marker_title_favorite_location).toString(),
                        "",
                        R.drawable.ic_map_pick,
                        MARKER_MY_LOCATION_FAVORITE);
                loadData(lat, lng);
            }
        }
    }

    // 최종 데이터 세팅, 뷰페이저/화면 이동
    @Override
    public void applyReportCardInfo(int position) {
        currentMarkerPosition = position;
        MapReportCardItem item = mapReportCardItemList.get(position);
        Marker marker = reportInfoMarkers.get(position);
        moveCamera(marker.getPosition(), ZOOM_FEED_MARKER);
        marker.showInfoWindow();
        report_card_view_pager.setCurrentItem(currentMarkerPosition, false);
        enlargeMarkerIcon(marker, item.getStatus());
    }

    // 즐겨찾기 콜백 함수
    @Override
    public void applyMyFavoriteInfo(String title, String info, Marker marker) {
        marker.setTitle("★" + title);
        marker.setSnippet(info);
    }

    // 즐겨찾기 콜백 함수
    @Override
    public void setMyFavoriteActive(boolean active) {
        this.isMyFavoriteActive = active;
    }

    // 피드에서 데이터를 받아왔음을 명시
    public void setFromFeed(boolean fromFeed) {
        this.fromFeed = fromFeed;
    }


    /**
     * 현재 위치 찾기
     */
    // gps 사용 여부, 권한확인 후 현재 위치 리스너 세팅
    public void getMyLocation() {

        isLoading(true);

        a = System.currentTimeMillis();

        if (manager == null) {
            manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        boolean isGpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnable) {
            /**
             * 이틀에 걸쳐 해결하지 못했던 문제의 원인은 static 으로 계속 메모리에 올라가 있던 PermissionUtil 에게
             * activity 와 this 를 통째로 넘겨준 것이었다. back 키를 통해 앱을 종료하고 다시 실행시키면 activity, fragment
             * 는 죽는데 문제는 static 에 넘겨준 객체는 죽지 않고 그대로 살아 있었던 것이다. getInstance 를 했을 때
             * 당연히 기존에 있던 객체를 사용했던 것이고 인자를 넘겨줘도 기존에 넘겨받았던 액티비티와 콜백함수를 사용했으니
             * 당연히 getContext 를 했을 때 이전에는 죽어버렸던 객체를 호출하게 된 것이다. static 이 아주 흉물스러운 것이라는
             * 것을 다시 느낌. 생각해보면 이전에도 이런 일이 있었는데 강사님이 해결해 줬던 기억이 난다.
             */
            getPermission().checkPermission(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION});
        } else {
            Toast.makeText(getContext(), "GPS를 사용할 수 있도록 켜주셔야 사용이 가능합니다.",
                    Toast.LENGTH_LONG).show();
            hideProgress();
        }
    }

    // 현재 위치 찾기 리스너 구현 클래스
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            Log.e("호출확인", "onLocationChanged : " + (System.currentTimeMillis() - a));

            // 현재위치 위경도 좌표 가져오기
            lat = location.getLatitude();
            lng = location.getLongitude();
            LatLng currLocation = new LatLng(lat, lng);
            // 다른 곳에서 사용할 수 있도록 위경도 좌표 app 단위 세팅
            setCurrentLocation(currLocation);
            // 현재 좌표로 지도 이동
            moveCamera(currLocation, ZOOM_CUR_LOCATION);
            clearReportInfoDataAndMarkers();
            addMarker(
                    currLocation,
                    getText(R.string.marker_title_my_location).toString(),
                    "",
                    R.drawable.ic_map_marker_cur_location,
                    MARKER_MY_LOCATION);
            loadData(lat, lng);
            removeLocationListener();
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

    // 리스너 세팅하기
    @SuppressLint("MissingPermission")
    private void setLocationListener() {
        long minTime = 1500;
        float minDistance = 100;
        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener();
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener);
    }

    // 리스너 제거하기
    private void removeLocationListener() {
        if (manager != null && myLocationListener != null) {
            manager.removeUpdates(myLocationListener);
            hideProgress();
        }
    }

    // App 단위로 현재 위치 설정하기
    private void setCurrentLocation(LatLng currLocation) {
        ((MoldeApplication) getActivity().getApplication()).setCurrLocation(currLocation);
    }


    /**
     * 권한 설정
     */
    private PermissionUtil getPermission() {
        if (mPermission == null) {
            mPermission = new PermissionUtil(getActivity(), MapFragment.this);
        }
        return mPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE) {
            getPermission().onResult(grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionGranted() {
        setLocationListener();
    }

    @Override
    public void onPermissionDenied() {
        showAskAgainDialog();
    }

    private void showAskAgainDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getText(R.string.dialog_request_permission))
                .setMessage(getText(R.string.dialog_go_settings))
                .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goSettings();
                    }
                })
                .setNegativeButton(getText(R.string.no), null)
                .create();
        dialog.show();
    }

    private void goSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS,
                Uri.fromParts("package", getContext().getPackageName(), null));
        startActivity(intent);
    }


    /**
     * 기타
     */
    private FirebaseAuth getFirebaseAuth() {
        if (mAuth == null) {
            mAuth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();
        }
        return mAuth;
    }

    private void isFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    private void isLoading(boolean isLoading) {
        if (isLoading) {
            showProgress();
        }  else {
            hideProgress();
        }
        isInit = isLoading;
    }

    @Override
    public void onBackKey() {
        if (!isBackBtnClicked && !isInit) {
            hideCardView();
        }
    }

    public void showReportCardListDialog(int reportId) {
        MapReportCardListDialog mapReportCardListDialog = new MapReportCardListDialog();
        mapReportCardListDialog.show(getActivity().getSupportFragmentManager(), "bottomSheet");
        mapReportCardListDialog.setData(reportId);
    }
}
