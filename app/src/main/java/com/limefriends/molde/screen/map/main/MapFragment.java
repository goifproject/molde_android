package com.limefriends.molde.screen.map.main;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.common.utils.NetworkUtil;
import com.limefriends.molde.common.utils.PermissionUtil;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.controller.BaseFragment;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.main.MoldeMainActivity;
import com.limefriends.molde.screen.map.favorite.MapFavoriteActivity;
import com.limefriends.molde.screen.map.favorite.MapFavoriteDialog;
import com.limefriends.molde.screen.map.main.reportCard.MapReportCardListDialog;
import com.limefriends.molde.screen.map.main.reportCard.MapReportCardPagerAdapter;
import com.limefriends.molde.screen.map.main.reportCard.MapReportCardItem;
import com.limefriends.molde.screen.map.main.reportCard.ShadowTransformer;
import com.limefriends.molde.screen.map.report.ReportActivity;
import com.limefriends.molde.screen.map.search.SearchMapInfoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

import static android.app.Activity.RESULT_OK;
import static com.limefriends.molde.common.Constant.Map.*;
import static com.limefriends.molde.common.Constant.ReportState.ACCEPTED;
import static com.limefriends.molde.common.Constant.ReportState.CLEAN;
import static com.limefriends.molde.common.Constant.ReportState.DENIED;
import static com.limefriends.molde.common.Constant.ReportState.FOUND;
import static com.limefriends.molde.common.Constant.ReportState.RECEIVING;
import static com.limefriends.molde.common.utils.PermissionUtil.REQ_CODE;

public class MapFragment extends BaseFragment implements
        OnMapReadyCallback, MoldeMainActivity.OnKeyBackPressedListener,
        ShadowTransformer.OnPageSelectedCallback,
        MapFavoriteDialog.MoldeApplyMyFavoriteInfoCallback,
        PermissionUtil.PermissionCallback, View.OnClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowCloseListener, GoogleMap.OnMarkerClickListener {

    public static final String RE_ASK_PERMISSION_DIALOG = "RE_ASK_PERMISSION_DIALOG";
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

    // 신고하기
    @BindView(R.id.report_button)
    ImageView report_button;

    /**
     * 만약 로딩 중이거나 권한이 없을 때
     */
    @BindView(R.id.map_view_progress)
    FrameLayout map_view_progress;
    @BindView(R.id.progress_loading)
    ProgressBar progress_loading;

    /**
     * 하단 카드뷰
     */
    @BindView(R.id.report_card_view_pager)
    ViewPager report_card_view_pager;

    public static final int ZOOM_CUR_LOCATION = 17;
    public static final int ZOOM_FEED_MARKER = 15;
    public static final int REQ_SEARCH_MAP = 999;
    public static final int REQ_FAVORITE = 996;
    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;

    public static final int MARKER_MY_LOCATION = -1;
    public static final int MARKER_MY_LOCATION_HISTORY = -2;
    public static final int MARKER_MY_LOCATION_SEARCH = -3;
    public static final int MARKER_MY_LOCATION_FAVORITE = -4;
    public static final int MARKER_MY_LOCATION_PRE_FAVORITE = -5;
    public static final int MARKER_SAFEHOUSE = -6;

    private int currentMarkerPosition = -1;
    private int currentPage = FIRST_PAGE;
    private int reportCardPosition = 0;
    private int currentSafehousePage = FIRST_PAGE;
    private static final int PER_PAGE_10 = 10;
    private static final int PER_PAGE_20 = 20;
    private static final int PER_PAGE_30 = 20;
    private double lat = 0.0;
    private double lng = 0.0;
    private boolean isFirst = true;
    private boolean isBackBtnClicked = false;
    private boolean isLoading = false;
    private boolean isMyFavoriteActive = false;
    private boolean fromFeed = false;
    private boolean hasMoreToLoad = true;
    private Marker curLocationMarker;
    private List<Marker> reportInfoMarkers;
    private List<MapReportCardItem> mapReportCardItemList;

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private LocationManager manager;
    private MyLocationListener myLocationListener;
    private MapReportCardPagerAdapter reportCardAdapter;
    private PermissionUtil mPermission;

    @Service
    private Repository.Favorite mFavoriteRepository;
    @Service
    private Repository.Safehouse mSafehouseRepository;
    @Service
    private Repository.Feed mFeedRepository;
    @Service
    private ToastHelper mToastHelper;
    @Service
    private DialogFactory mDialogFactory;
    @Service
    private DialogManager mDialogManager;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private MapFavoriteDialog mapFavoriteDialog;
    private MapReportCardListDialog mapReportCardListDialog;

    /**
     * 초기화 작업 - 생명주기
     */
    @Override
    public void onAttach(Context context) {
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

        getInjector().inject(this);

        // replace 할 때마다 호출됨. 매니저에 add, replace 되는 순간 반드시 호출되는 듯
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

                if (!NetworkUtil.isConnected(getContext())) {
                    mToastHelper.showNetworkError();
                    return;
                }

                intent = new Intent();
                intent.setClass(v.getContext(), SearchMapInfoActivity.class);
                startActivityForResult(intent, REQ_SEARCH_MAP);
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

                if (!NetworkUtil.isConnected(getContext())) {
                    mToastHelper.showNetworkError();
                    return;
                }

                clearReportInfoDataAndMarkers();
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
        report_card_view_pager.addOnPageChangeListener(shadowTransformer);

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

        if (getUid() == null || getUid().equals("")) {
            snack("몰디 로그인이 필요합니다.");
            return;
        }

        Marker newFavoriteMarker = addMarker(
                latLng,
                getText(R.string.marker_title_favorite).toString(),
                "",
                R.drawable.ic_map_pick,
                MARKER_MY_LOCATION_PRE_FAVORITE);

        newFavoriteMarker.setIcon(BitmapDescriptorFactory
                .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));

        newFavoriteMarker.showInfoWindow();

        showFavoriteDialog(newFavoriteMarker, MARKER_MY_LOCATION_PRE_FAVORITE);
    }

    // 즐겨찾기 마커 클릭시 보여줄 대화상자
    private void showFavoriteDialog(Marker marker, int type) {
        mapFavoriteDialog = new MapFavoriteDialog();
        mapFavoriteDialog.setCallback(this, marker, getUid());
        Bundle bundle = new Bundle();
        bundle.putString("markerTitle", marker.getTitle());
        bundle.putString("markerInfo", marker.getSnippet());
        bundle.putDouble("markerLat", marker.getPosition().latitude);
        bundle.putDouble("markerLng", marker.getPosition().longitude);
        if (type == MARKER_MY_LOCATION_FAVORITE) {
            isMyFavoriteActive = true;
        } else if (type == MARKER_MY_LOCATION_PRE_FAVORITE) {
            isMyFavoriteActive = false;
        }
        bundle.putBoolean("myFavoriteActive", isMyFavoriteActive);
        mapFavoriteDialog.setArguments(bundle);
        mapFavoriteDialog.show(
                ((MoldeMainActivity) getContext()).getSupportFragmentManager(),
                "bottomSheet");
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
                    marker.showInfoWindow();
                    break;
                case MARKER_MY_LOCATION_HISTORY:
                case MARKER_MY_LOCATION_SEARCH:
                case MARKER_MY_LOCATION_FAVORITE:
                    marker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));
                    marker.showInfoWindow();
                    showFavoriteDialog(marker, MARKER_MY_LOCATION_FAVORITE);
                    break;
                case MARKER_MY_LOCATION_PRE_FAVORITE:
                    marker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));
                    marker.showInfoWindow();
                    showFavoriteDialog(marker, MARKER_MY_LOCATION_PRE_FAVORITE);
                    break;
                case MARKER_SAFEHOUSE:
                    marker.setIcon(BitmapDescriptorFactory.
                            fromBitmap(sizeUpMapIcon(R.drawable.ic_safehouse)));
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
                    break;
                case MARKER_MY_LOCATION_HISTORY:
                case MARKER_MY_LOCATION_SEARCH:
                case MARKER_MY_LOCATION_FAVORITE:
                case MARKER_MY_LOCATION_PRE_FAVORITE:
                    marker.setIcon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_map_pick));
                    break;
                case MARKER_SAFEHOUSE:
                    marker.setIcon(BitmapDescriptorFactory.
                            fromResource(R.drawable.ic_safehouse));
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
        loadData(feedLocation.latitude, feedLocation.longitude);
        loadFavoriteFeed(feedLocation.latitude, feedLocation.longitude);
        loadSafeHouse(feedLocation.latitude, feedLocation.longitude);
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

    @Override
    public void snack(String message) {
        if (hasMoreToLoad)
            Snackbar.make(map_container, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 데이터 세팅
     */
    // 화면에 세팅될 때마다 어떤 경로로 데이터를 세팅할지 결정함
    private void setupData() {
        if (isFirst) {


            if (!NetworkUtil.isConnected(getContext())) {
                mToastHelper.showNetworkError();
                return;
            }

            getMyLocation();
        }
        // 피드에서 데이터를 받아올 경우
        else if (fromFeed) {

            if (!NetworkUtil.isConnected(getContext())) {
                mToastHelper.showNetworkError();
                return;
            }

            FeedEntity feedEntity = ((MoldeMainActivity) getActivity()).getFeedEntity();
            moveCamera(new LatLng(feedEntity.getRepLat(),
                    feedEntity.getRepLon()), ZOOM_FEED_MARKER);
            addFeedMarkers(feedEntity);

        }
        // 한 번 데이터를 불러온 후 외부에서 접근할 경우
        else {

        }
    }

    @Override
    public void loadData() {

        if (!NetworkUtil.isConnected(getContext())) {
            mToastHelper.showNetworkError();
            return;
        }

        loadData(lat, lng);
    }

    // 네트워크에서 피드 데이터 받아옴
    private void loadData(final double lat, final double lng) {

        if (!hasMoreToLoad) return;

        if (isLoading) {
            return;
        } else {
            isLoading(true);
        }

        List<FeedEntity> entityList = new ArrayList<>();
        mCompositeDisposable.add(
                mFeedRepository
                        .getPagedFeedByDistance(lat, lng, PER_PAGE, currentPage)
                        .subscribe(
                                entityList::addAll,
                                err -> {
                                    snackBar(getText(R.string.toast_network_error).toString());

                                    isLoading(false);

                                    fromFeed = false;
                                },
                                () -> {

                                    if (entityList.size() != 0) {

                                        // 피드를 추가로 불러왔을 때 위치
                                        reportCardPosition = mapReportCardItemList.size();
                                        for (int i = 0; i < entityList.size(); i++) {
                                            FeedEntity entity = entityList.get(i);
                                            LatLng location = new LatLng(entity.getRepLat(), entity.getRepLon());
                                            Marker marker = null;
                                            switch (entityList.get(i).getRepState()) {
                                                // TODO 데이터 제대로 들어가면 변경할 것
                                                case RECEIVING:
                                                    continue;
                                                case ACCEPTED:
                                                    marker = addMarker(location, entity.getRepDetailAddr(),
                                                            "",
                                                            R.drawable.ic_map_marker_red, i);
                                                    if (i == 0 && fromFeed) {
                                                        marker.setIcon(BitmapDescriptorFactory
                                                                .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_marker_red)));
                                                        marker.showInfoWindow();
                                                    }
                                                    break;
                                                case FOUND:
                                                    marker = addMarker(location, entity.getRepContents(),
                                                            "",
                                                            R.drawable.ic_map_marker_white, i);
                                                    if (i == 0 && fromFeed) {
                                                        marker.setIcon(BitmapDescriptorFactory
                                                                .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_marker_white)));
                                                        marker.showInfoWindow();
                                                    }
                                                    break;
                                                case CLEAN:
                                                    marker = addMarker(location, entity.getRepContents(),
                                                            "",
                                                            R.drawable.ic_map_marker_green, i);
                                                    if (i == 0 && fromFeed) {
                                                        marker.setIcon(BitmapDescriptorFactory
                                                                .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_marker_green)));
                                                        marker.showInfoWindow();
                                                    }
                                                    break;
                                                case DENIED:
                                                    continue;
                                            }
                                            reportInfoMarkers.add(marker);
                                            marker.setTag(reportInfoMarkers.size() - 1);
                                            mapReportCardItemList.add(
                                                    new MapReportCardItem(
                                                            entity.getRepContents(),
                                                            entity.getRepDetailAddr(),
                                                            entity.getRepState(),
                                                            entity.getRepId(),
                                                            entity.getRepDate(),
                                                            entity.getRepImg().get(0).getFilepath()));
                                        }
                                        // 지도에 추가할 피드가 있는 경우
                                        if (reportInfoMarkers.size() != 0) {

                                            updateData(mapReportCardItemList);

                                            // applyReportCardInfo(reportCardPosition);

                                            if (reportCardPosition == 0 && fromFeed) {
                                                showCardView();
                                                fromFeed = false;
                                            }

                                            currentPage++;

                                            if (entityList.size() < PER_PAGE) {
                                                hasMoreToLoad(false);
                                            }

                                        }
                                        // 지도에 추가할 피드가 없는 경우
                                        else {

                                            snackBar(getText(R.string.toast_no_feed_place).toString());

                                            moveCamera(new LatLng(lat, lng), ZOOM_CUR_LOCATION);

                                        }

                                        // 피드가 아예 없는 경우
                                    } else {

                                        hasMoreToLoad(false);

                                        snackBar(getText(R.string.toast_no_feed_place).toString());

                                        moveCamera(new LatLng(lat, lng), ZOOM_CUR_LOCATION);
                                    }

                                    isFirst(false);

                                    isLoading(false);
                                }
                        )
        );
    }

    // 받아온 데이터로 하단 뷰페이저 갱신
    private void updateData(List<MapReportCardItem> data) {
        reportCardAdapter.addAll(data);
    }

    private void loadFavoriteFeed(final double lat, final double lng) {

        if (getUid() == null || getUid().equals("")) return;

        mCompositeDisposable.add(
                mFavoriteRepository
                        .getMyFavoriteByDistance(getUid(), lat, lng)
                        .subscribe(
                                entity -> {
                                    Marker newFavoriteMarker = addMarker(
                                            new LatLng(entity.getFavLat(), entity.getFavLon()),
                                            entity.getFavName(),
                                            entity.getFavAddr(),
                                            R.drawable.ic_map_pick,
                                            MARKER_MY_LOCATION_FAVORITE);

                                    if (lat == entity.getFavLat() && lng == entity.getFavLon()) {
                                        newFavoriteMarker.setIcon(BitmapDescriptorFactory
                                                .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));
                                        newFavoriteMarker.showInfoWindow();
                                    }
                                },
                                err -> {
                                },
                                () -> {
                                }
                        )
        );
    }

    private void loadSafeHouse(double lat, double lng) {

        mCompositeDisposable.add(
                mSafehouseRepository
                        .getSafehouse(lat, lng, PER_PAGE_30, FIRST_PAGE)
                        .subscribe(
                                entity -> addMarker(
                                        new LatLng(entity.getSafeLat(), entity.getSafeLon()),
                                        entity.getSafeName(),
                                        "",
                                        R.drawable.ic_safehouse,
                                        MARKER_SAFEHOUSE),
                                e -> { },
                                () -> { }
                        )
        );
    }

    // 새로 받아올 때 기존 데이터 캐시 제거
    public void clearReportInfoDataAndMarkers() {

        currentPage = FIRST_PAGE;
        mMap.clear();
        hasMoreToLoad(true);

        if (reportInfoMarkers.size() > 0 || mapReportCardItemList.size() > 0) {
            reportInfoMarkers.clear();
            mapReportCardItemList.clear();
            reportCardAdapter.removeAllCardItem(mapReportCardItemList);
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
            if (curLocationMarker != null) curLocationMarker.remove();
            LatLng newLatlng = new LatLng(lat, lng);
            curLocationMarker = addMarker(
                    newLatlng,
                    getText(R.string.marker_title_search_location).toString(),
                    "",
                    R.drawable.ic_map_pick,
                    MARKER_MY_LOCATION_PRE_FAVORITE);
            curLocationMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));
            curLocationMarker.showInfoWindow();
            moveCamera(newLatlng, ZOOM_CUR_LOCATION);
            loadData(lat, lng);
            loadFavoriteFeed(lat, lng);
            loadSafeHouse(lat, lng);
        } else if (resultCode == RESULT_OK && requestCode == REQ_FAVORITE) {
            FavoriteEntity myFavoriteEntity
                    = (FavoriteEntity) data.getSerializableExtra(EXTRA_KEY_FAVORITE_INFO);

            if (myFavoriteEntity != null) {
                lat = myFavoriteEntity.getFavLat();
                lng = myFavoriteEntity.getFavLon();
                loc_search_input.setText("★" + myFavoriteEntity.getFavName());
                isMyFavoriteActive = myFavoriteEntity.isActive();
                clearReportInfoDataAndMarkers();
                if (curLocationMarker != null) curLocationMarker.remove();
                LatLng newLatlng = new LatLng(lat, lng);
                addMarker(
                        newLatlng,
                        myFavoriteEntity.getFavName(),
                        "",
                        R.drawable.ic_map_pick,
                        MARKER_MY_LOCATION_FAVORITE);
                moveCamera(newLatlng, ZOOM_CUR_LOCATION);
                loadData(lat, lng);
                loadFavoriteFeed(lat, lng);
                loadSafeHouse(lat, lng);
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
        report_card_view_pager.setCurrentItem(currentMarkerPosition, true);
        enlargeMarkerIcon(marker, item.getStatus());
    }

    @Override
    public int getCardItemCount() {
        return reportInfoMarkers.size();
    }

    // 즐겨찾기 콜백 함수
    @Override
    public void applyMyFavoriteInfo(String title, String info, Marker marker) {
        marker.setTitle("★" + title);
        marker.setSnippet(info);
    }

    @Override
    public void addToMyFavorite(String userId, String name, String address, double lat, double lng) {

        mCompositeDisposable.add(
                mFavoriteRepository
                        .addToMyFavorite(userId, name, address, lat, lng)
                        .subscribe(
                                e -> {
                                    mToastHelper.showShortToast("즐겨찾기 추가 성공");
                                    if (mapFavoriteDialog != null) mapFavoriteDialog.dismiss();
                                },
                                err -> mToastHelper.showShortToast("데이터 전송 실패"),
                                () -> {
                                }
                        )
        );
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

        showProgress();

        if (manager == null) {
            manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        boolean isGpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnable) {

            getPermission().checkPermission(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION});
        } else {
            mToastHelper.showShortToast("GPS를 사용할 수 있도록 켜주셔야 사용이 가능합니다.");
            hideProgress();
        }
    }

    // 현재 위치 찾기 리스너 구현 클래스
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            // 현재위치 위경도 좌표 가져오기
            lat = location.getLatitude();
            lng = location.getLongitude();
            LatLng currLocation = new LatLng(lat, lng);
            // 다른 곳에서 사용할 수 있도록 위경도 좌표 app 단위 세팅
            setCurrentLocation(currLocation);
            // 현재 좌표로 지도 이동
            moveCamera(currLocation, ZOOM_CUR_LOCATION);
            // clearReportInfoDataAndMarkers();
            curLocationMarker = addMarker(
                    currLocation,
                    getText(R.string.marker_title_my_location).toString(),
                    "",
                    R.drawable.ic_map_marker_cur_location,
                    MARKER_MY_LOCATION);
            curLocationMarker.showInfoWindow();
            loadData(lat, lng);
            loadFavoriteFeed(lat, lng);
            loadSafeHouse(lat, lng);
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
            // hideProgress();
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

    // fragment 에서 Activity 의 자원을 끌어다 사용할 때 onActivityResult, onRequestPermission 같은 경우
    // 부모에서 자식으로 코드를 전달해 줘야 한
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getText(R.string.dialog_go_settings).toString(),
                "",
                getText(R.string.yes).toString(),
                getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                goSettings();
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, RE_ASK_PERMISSION_DIALOG);
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

    private String getUid() {
        return getFirebaseAuth().getUid();
    }

    private void isFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    private void isLoading(boolean isLoading) {
        if (isLoading) {
            showProgress();
        } else {
            hideProgress();
        }
        this.isLoading = isLoading;
    }

    private void hasMoreToLoad(boolean hasMoreToLoad) {
        this.hasMoreToLoad = hasMoreToLoad;
    }

    @Override
    public void onBackKey() {
        if (!isBackBtnClicked && !isLoading) {
            hideCardView();
        }
    }

    public void showReportCardListDialog(int reportId) {
        mapReportCardListDialog = MapReportCardListDialog.newInstance(reportId);
        mapReportCardListDialog.show(getActivity().getSupportFragmentManager(), "bottomSheet");

    }
}
