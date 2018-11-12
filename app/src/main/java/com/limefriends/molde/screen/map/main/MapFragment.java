package com.limefriends.molde.screen.map.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.common.helper.PermissionUtil;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BackPressDispatcher;
import com.limefriends.molde.screen.common.viewController.BackPressedListener;
import com.limefriends.molde.screen.common.viewController.BaseFragment;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.main.MoldeMainActivity;
import com.limefriends.molde.screen.map.main.view.MapView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static android.app.Activity.RESULT_OK;
import static com.limefriends.molde.common.Constant.Map.*;
import static com.limefriends.molde.common.Constant.ReportState.*;
import static com.limefriends.molde.common.helper.PermissionUtil.REQ_CODE;
import static com.limefriends.molde.screen.common.mapView.GoogleMapView.ZOOM_CUR_LOCATION;
import static com.limefriends.molde.screen.common.mapView.GoogleMapView.ZOOM_FEED_MARKER;
import static com.limefriends.molde.screen.map.main.view.MapViewImpl.MARKER_MY_LOCATION;
import static com.limefriends.molde.screen.map.main.view.MapViewImpl.MARKER_MY_LOCATION_FAVORITE;
import static com.limefriends.molde.screen.map.main.view.MapViewImpl.MARKER_MY_LOCATION_PRE_FAVORITE;
import static com.limefriends.molde.screen.map.main.view.MapViewImpl.MARKER_SAFEHOUSE;

public class MapFragment extends BaseFragment implements
        PermissionUtil.PermissionCallback, MapView.Listener, BackPressedListener {

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public static final int REQ_SEARCH_MAP = 999;
    public static final int REQ_FAVORITE = 996;
    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;

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
    private boolean isLoading = false;
    private boolean isMyFavoriteActive = false;
    private boolean fromFeed = false;
    private boolean hasMoreToLoad = true;
    private Marker curLocationMarker;
    private List<Marker> reportInfoMarkers;
    private List<FeedEntity> mapReportCardItemList;
    private FirebaseAuth mAuth;
    private LocationManager manager;
    private MyLocationListener myLocationListener;
    private PermissionUtil mPermission;
    private MapView mMapView;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Service private Repository.Favorite mFavoriteRepository;
    @Service private Repository.Safehouse mSafehouseRepository;
    @Service private Repository.Feed mFeedRepository;
    @Service private ToastHelper mToastHelper;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    @Service private ViewFactory mViewFactory;
    @Service private BackPressDispatcher mBackPressDispatcher;

    long start = System.currentTimeMillis();

    /**
     * life cycle
     */



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

        Log.e("호출확인1", System.currentTimeMillis() - start +"");
        getInjector().inject(this);
        Log.e("호출확인2", System.currentTimeMillis() - start +"");
        // TODO 여기서 재활용하는지 계속 new 해주는지 확인
        if (mMapView == null) {
            mMapView = mViewFactory.newInstance(MapView.class, null);
        }
        Log.e("호출확인3", System.currentTimeMillis() - start +"");

        return mMapView.getRootView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("호출확인4", System.currentTimeMillis() - start +"");
        mMapView.registerListener(this);
        Log.e("호출확인5", System.currentTimeMillis() - start +"");
        setupData();
    }

    /**
     * fetch data
     */

    private void setupData() {

        if (isFirst) {
            getMyLocation();
        }
        else if (fromFeed) {
            FeedEntity feedEntity = ((MoldeMainActivity) getActivity()).getFeedEntity();
            mMapView.moveCamera(new LatLng(feedEntity.getRepLat(), feedEntity.getRepLon()), ZOOM_FEED_MARKER);
            clearReportInfoDataAndMarkers();
            loadDataFromScratch(feedEntity.getRepLat(), feedEntity.getRepLon());
        }
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
                                    mMapView.showSnackBar(getText(R.string.toast_network_error).toString());

                                    isLoading(false);

                                    fromFeed = false;
                                },
                                () -> {

                                    if (entityList.size() != 0) {

                                        // 피드를 추가로 불러왔을 때 위치
                                        reportCardPosition = mapReportCardItemList.size();
                                        boolean isFirstMarker = true;
                                        for (int i = 0; i < entityList.size(); i++) {
                                            FeedEntity entity = entityList.get(i);
                                            LatLng location = new LatLng(entity.getRepLat(), entity.getRepLon());
                                            Marker marker = null;
                                            switch (entity.getRepState()) {
                                                case RECEIVING:
                                                    continue;
                                                case ACCEPTED:
                                                    Log.e("호출확인", "ACCEPTED");
                                                    marker = mMapView.addMarker(location, entity.getRepDetailAddr(),
                                                        "",
                                                        R.drawable.ic_map_marker_red, i);
                                                    if (isFirstMarker) {
                                                        Log.e("호출확인", "isFirstMarker");
                                                        isFirstMarker = false;
                                                        mMapView.enlargeMarkerIcon(marker, entity.getRepState());
                                                        marker.showInfoWindow();
                                                    }
                                                    break;
                                                case FOUND:
                                                    Log.e("호출확인", "FOUND");
                                                    marker = mMapView.addMarker(location, entity.getRepContents(),
                                                            "",
                                                            R.drawable.ic_map_marker_white, i);
                                                    if (isFirstMarker) {
                                                        Log.e("호출확인", "isFirstMarker");
                                                        isFirstMarker = false;
                                                        mMapView.enlargeMarkerIcon(marker, entity.getRepState());
                                                        marker.showInfoWindow();
                                                    }
                                                    break;
                                                case CLEAN:
                                                    Log.e("호출확인", "CLEAN");
                                                    marker = mMapView.addMarker(location, entity.getRepContents(),
                                                            "",
                                                            R.drawable.ic_map_marker_green, i);
                                                    if (isFirstMarker) {
                                                        Log.e("호출확인", "isFirstMarker");
                                                        isFirstMarker = false;
                                                        mMapView.enlargeMarkerIcon(marker, entity.getRepState());
                                                        marker.showInfoWindow();
                                                    }
                                                    break;
                                                case DENIED:
                                                    continue;
                                            }
                                            reportInfoMarkers.add(marker);
                                            Pair<Integer, Integer> pair = new Pair<>(reportInfoMarkers.size() - 1, entity.getRepState());
                                            marker.setTag(pair);
                                            mapReportCardItemList.add(
                                                    new FeedEntity(
                                                            entity.getRepContents(),
                                                            entity.getRepDetailAddr(),
                                                            entity.getRepState(),
                                                            entity.getRepId(),
                                                            entity.getRepDate(),
                                                            entity.getRepImg())
                                            );
                                        }
                                        // 지도에 추가할 피드가 있는 경우
                                        if (reportInfoMarkers.size() != 0) {

                                            mMapView.bindFeed(mapReportCardItemList);

                                            if (reportCardPosition == 0 && fromFeed) {
                                                mMapView.showCardView();
                                                fromFeed = false;
                                            }

                                            currentPage++;

                                            if (entityList.size() < PER_PAGE) {
                                                hasMoreToLoad(false);
                                            }
                                        }
                                        // 지도에 추가할 피드가 없는 경우
                                        else {

                                            mMapView.showSnackBar(getText(R.string.toast_no_feed_place).toString());

                                            mMapView.moveCamera(new LatLng(lat, lng), ZOOM_CUR_LOCATION);
                                        }
                                    }
                                    // 피드가 아예 없는 경우
                                    else {

                                        hasMoreToLoad(false);

                                        mMapView.showSnackBar(getText(R.string.toast_no_feed_place).toString());

                                        mMapView.moveCamera(new LatLng(lat, lng), ZOOM_CUR_LOCATION);
                                    }

                                    isFirst(false);

                                    isLoading(false);
                                }
                        )
        );
    }

    private void loadFavoriteFeed(final double lat, final double lng) {

        if (getUid() == null || getUid().equals("")) return;

        mCompositeDisposable.add(
                mFavoriteRepository
                        .getMyFavoriteByDistance(getUid(), lat, lng)
                        .subscribe(
                                entity -> {
                                    Marker newFavoriteMarker = mMapView.addMarker(
                                            new LatLng(entity.getFavLat(), entity.getFavLon()),
                                            entity.getFavName(),
                                            entity.getFavAddr(),
                                            R.drawable.ic_map_pick,
                                            MARKER_MY_LOCATION_FAVORITE);

                                    if (lat == entity.getFavLat() && lng == entity.getFavLon()) {
                                        mMapView.enlargeMarkerIcon(newFavoriteMarker, R.drawable.ic_map_pick);
                                        newFavoriteMarker.showInfoWindow();
                                    }
                                },
                                err -> { },
                                () -> { }
                        )
        );
    }

    private void loadSafeHouse(double lat, double lng) {

        mCompositeDisposable.add(
                mSafehouseRepository
                        .getSafehouse(lat, lng, PER_PAGE_30, FIRST_PAGE)
                        .subscribe(
                                entity -> mMapView.addMarker(
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

    public void loadDataFromScratch(double lat,  double lng) {
        loadData(lat, lng);
        loadFavoriteFeed(lat, lng);
        loadSafeHouse(lat, lng);
    }

    // 새로 받아올 때 기존 데이터 캐시 제거
    public void clearReportInfoDataAndMarkers() {

        currentPage = FIRST_PAGE;
        mMapView.clearMap();
        hasMoreToLoad(true);

        if (reportInfoMarkers.size() > 0 || mapReportCardItemList.size() > 0) {
            reportInfoMarkers.clear();
            mapReportCardItemList.clear();
            mMapView.clearReportCardList();
        }
    }

    /**
     * activity result callback
     */

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
            mMapView.bindSearchLocation(reportName);
            clearReportInfoDataAndMarkers();
            if (curLocationMarker != null) curLocationMarker.remove();
            LatLng newLatlng = new LatLng(lat, lng);
            curLocationMarker = mMapView.addMarker(
                    newLatlng,
                    getText(R.string.marker_title_search_location).toString(),
                    "",
                    R.drawable.ic_map_pick,
                    MARKER_MY_LOCATION_PRE_FAVORITE);

            mMapView.enlargeMarkerIcon(curLocationMarker, R.drawable.ic_map_pick);
            curLocationMarker.showInfoWindow();
            mMapView.moveCamera(newLatlng, ZOOM_CUR_LOCATION);
            loadDataFromScratch(lat, lng);
        } else if (resultCode == RESULT_OK && requestCode == REQ_FAVORITE) {
            FavoriteEntity myFavoriteEntity
                    = (FavoriteEntity) data.getSerializableExtra(EXTRA_KEY_FAVORITE_INFO);

            if (myFavoriteEntity != null) {
                lat = myFavoriteEntity.getFavLat();
                lng = myFavoriteEntity.getFavLon();
                mMapView.bindSearchLocation("★" + myFavoriteEntity.getFavName());
                isMyFavoriteActive = myFavoriteEntity.isActive();
                clearReportInfoDataAndMarkers();
                if (curLocationMarker != null) curLocationMarker.remove();
                LatLng newLatlng = new LatLng(lat, lng);
                mMapView.addMarker(
                        newLatlng,
                        myFavoriteEntity.getFavName(),
                        "",
                        R.drawable.ic_map_pick,
                        MARKER_MY_LOCATION_FAVORITE);
                mMapView.moveCamera(newLatlng, ZOOM_CUR_LOCATION);
                loadDataFromScratch(lat, lng);
            }
        }
    }

    /**
     * view callback
     */

    @Override
    public boolean onMapLongClicked() {
        if (getUid() == null || getUid().equals("")) {
            mMapView.showSnackBar("몰디 로그인이 필요합니다.");
            return false;
        }
        return true;
    }

    @Override
    public void onSearchBarClicked() {
        mActivityScreenNavigator.toSearchLocationActivity(getActivity());
    }

    @Override
    public void onFavoriteButtonClicked() {
        if (getFirebaseAuth() != null && getFirebaseAuth().getUid() != null) {
            mActivityScreenNavigator.toFavoriteActivity(getActivity());
        } else {
            mMapView.showSnackBar(getText(R.string.toast_require_signin).toString());
        }
    }

    @Override
    public void onReportButtonClicked() {
        if (getFirebaseAuth() != null && getFirebaseAuth().getUid() != null) {
            mActivityScreenNavigator.toReportActivity();
        } else {
            mMapView.showSnackBar(getText(R.string.toast_require_signin).toString());
        }
    }

    @Override
    public void onFindCurrentLocationClicked() {
        clearReportInfoDataAndMarkers();
        getMyLocation();
    }

    @Override
    public void onReAskPermissionAllowed() {
        mActivityScreenNavigator.toSystemSettings();
    }

    @Override
    public Marker onPageSelected(int position) {
        return reportInfoMarkers.get(position);
    }

    @Override
    public void onLastPageSelected() {
        loadData(lat, lng);
        if (hasMoreToLoad) mMapView.showSnackBar("피드 데이터가 추가되었습니다.");
        else mMapView.showSnackBar("추가할 피드가 없습니다.");
    }

    /**
     * MapFragment View Controller
     * 1. 현재 위치 찾기
     * 2. 권한 설정
     */


    /**
     * 1. 현재 위치 찾기
     */
    public void getMyLocation() {

        mMapView.showProgressIndication();

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
            mMapView.hideProgressIndication();
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
            mMapView.moveCamera(currLocation, ZOOM_CUR_LOCATION);
            // clearReportInfoDataAndMarkers();
            mMapView.addMarker(currLocation, getText(R.string.marker_title_my_location).toString(),
                    "", R.drawable.ic_map_marker_cur_location, MARKER_MY_LOCATION)
                    .showInfoWindow();

            loadDataFromScratch(lat, lng);

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
        }
    }

    // App 단위로 현재 위치 설정하기
    private void setCurrentLocation(LatLng currLocation) {
        ((MoldeApplication) getActivity().getApplication()).setCurrLocation(currLocation);
    }


    /**
     * 2. 권한 설정
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
        mMapView.showAskAgainDialog();
    }


    /**
     * 기타
     */

    // 로그인 여부 파악
    private FirebaseAuth getFirebaseAuth() {
        if (mAuth == null) {
            mAuth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();
        }
        return mAuth;
    }

    private String getUid() {
        return getFirebaseAuth().getUid();
    }

    // 처음 로그인했는지 설정
    private void isFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    // 데이터 로딩중인지 확인
    private void isLoading(boolean isLoading) {
        if (isLoading) {
            mMapView.showProgressIndication();
        } else {
            mMapView.hideProgressIndication();
        }
        this.isLoading = isLoading;
    }

    // 데이터를 더 가져올 게 있는지 설정
    private void hasMoreToLoad(boolean hasMoreToLoad) {
        this.hasMoreToLoad = hasMoreToLoad;
    }

    // 피드에서 데이터를 받아왔음을 명시
    public void setFromFeed(boolean fromFeed) {
        this.fromFeed = fromFeed;
    }

    // fragment의 onBackPressed 상태를 확인해서 MainActivity에 넘겨줌
    @Override
    public boolean onBackPressed() {
        return !mMapView.isReportCardShown();
    }




}
