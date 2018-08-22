package com.limefriends.molde.ui.menu_map.main;

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
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.PermissionUtil;
import com.limefriends.molde.entity.favorite.MoldeFavoriteEntity;
import com.limefriends.molde.entity.feed.MoldeFeedEntity;
import com.limefriends.molde.entity.feed.MoldeFeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.MoldeFeedResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.entity.map.MoldeSearchMapHistoryEntity;
import com.limefriends.molde.entity.map.MoldeSearchMapInfoEntity;
import com.limefriends.molde.ui.menu_map.favorite.MoldeMyFavoriteActivity;
import com.limefriends.molde.ui.menu_map.favorite.MoldeMyFavoriteInfoMapDialog;
import com.limefriends.molde.ui.menu_map.report.MoldeReportActivity;
import com.limefriends.molde.ui.menu_map.main.card.ReportCardItem;
import com.limefriends.molde.ui.menu_map.main.card.ReportCardPagerAdapter;
import com.limefriends.molde.ui.menu_map.main.card.ShadowTransformer;
import com.limefriends.molde.ui.menu_map.search.SearchMapInfoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.limefriends.molde.comm.Constant.ReportState.ACCEPTED;
import static com.limefriends.molde.comm.Constant.ReportState.CLEAN;
import static com.limefriends.molde.comm.Constant.ReportState.FOUND;
import static com.limefriends.molde.comm.Constant.ReportState.RECEIVING;
import static com.limefriends.molde.comm.utils.PermissionUtil.REQ_CODE;


public class MapFragment extends Fragment implements
        OnMapReadyCallback, MoldeMainActivity.OnKeyBackPressedListener, ShadowTransformer.OnPageSelectedCallback,
        MoldeMyFavoriteInfoMapDialog.MoldeApplyMyFavoriteInfoCallback, PermissionUtil.PermissionCallback {

    public static final int ZOOM_CUR_LOCATION = 17;
    public static final int ZOOM_FEED_MARKER = 15;
    public static final int RED = 0;
    public static final int GREEN = 2;
    public static final int WHITE = 3;
    public static final int NULL = 9;
    public static final int REQ_SEARCH_MAP = 999;
    public static final int REQ_FAVORITE = 996;

    /**
     * 지도
     */
    // 지도 fragment 담고 있는 레이아웃
    @BindView(R.id.map_view_layout)
    LinearLayout map_view_layout;

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
    ImageButton my_loc_button;
    // 즐겨찾기
    @BindView(R.id.favorite_button)
    ImageButton favorite_button;
    // 즐겨찾기 추가된 거 있는지
    @BindView(R.id.favorite_new)
    ImageView favorite_new;
    // 신고하기
    @BindView(R.id.report_button)
    ImageButton report_button;

    /**
     * 만약 로딩 중이거나 권한이 없을 때
     */
    @BindView(R.id.map_view_progress)
    RelativeLayout map_view_progress;
    @BindView(R.id.progress_loading)
    ProgressBar progress_loading;
    @BindView(R.id.request_gps_button)
    Button request_gps_button;

    /**
     * 하단 카드뷰
     */
    @BindView(R.id.report_card_view)
    FrameLayout report_card_view_layout;
    @BindView(R.id.report_card_view_pager)
    ViewPager report_card_view_pager;

    private View view;
    private SupportMapFragment mapView;

    private double lat = 0.0;
    private double lng = 0.0;
    private String searchTitle = "";
    private String searchInfo = "";
    // private int moveCnt = 0;
    private int REQUEST_LOCATION = 1;
    private int currentMarkerPosition = -1;
    long a;
    // private long gpsRequestTime = 0;

    //    private static final String FEED_BY_DISTANCE = "거리순";
//    private static final String FEED_BY_LAST = "최신순";
    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
//    private String feedStatus;
//    private boolean hasMoreToLoad = true;

    private boolean isFirst = true;
    private boolean isBackBtnClicked = false;
    private boolean isInit = false;
    private boolean isMyLocChange = false;
    private boolean isAfterSearch = false;
    private boolean isFirstPresentReportCard = false;
    private boolean isMyFavoriteActive = false;
    // private boolean isGpsEnable = false;

    private GoogleMap mMap;
    private LocationManager manager;
    private MyLocationListener myLocationListener;
    private ReportCardPagerAdapter reportCardAdapter;
    private ShadowTransformer reportCardShadowTransformer;
    private MoldeFeedEntity feedData;
    private PermissionUtil permission;

    MoldeSearchMapInfoEntity searchEntity;
    MoldeSearchMapHistoryEntity historyEntity;
    MoldeFavoriteEntity myFavoriteEntity;

    // private Marker myMarker;
    private Marker feedMarker;

    /**
     * 현재 지도에서 표현하는 리스트는 총 다섯개
     * 1. 빨간 마커 리스트
     * 2. 초록 마커 리스트
     * 3. 흰색 마커 리스트
     * 4. 내가 추가한 마커 리스트
     * 5. [1,2,3]을 전부 포함한 피드 카드 리스트
     */
//    private List<Marker> reportRedMarkerList;
//    private List<Marker> reportGreenMarkerList;
//    private List<Marker> reportWhiteMarkerList;
    private List<Marker> reportInfoMarkers;
    private List<ReportCardItem> reportCardItemList;
    private boolean fromFeed = false;

    // private List<Integer> reportCardPositionList;
    // private List<Long> beforeCallApplyMethodTimeList;
    // private SparseArrayCompat mapFragmentSparseArrayCompat;

//    public static MapFragment newInstance() {
//        return new MapFragment();
//    }

    public boolean isFromFeed() {
        return fromFeed;
    }

    public void setFromFeed(boolean fromFeed) {
        this.fromFeed = fromFeed;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("호출확인", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("호출확인", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("호출확인", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("호출확인", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("호출확인", "onDetach");
    }

    /**
     * onAttach 에서는 context 를 넘겨받기 때문에 인터페이스 연결할 사항을 처리한다
     * 다만 onCreateView 에서 View 를 binding 하기 이전까지는 모든 뷰가 null 이기 때문에 해당 사항을 처리하려면
     * onViewCrated 에서 해야 함
     * add 할 때 호출됨
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("호출확인", "onAttach1");
        ((MoldeMainActivity) context).setOnKeyBackPressedListener(this);
//        if (report_card_view_layout != null) {
//            Log.e("호출확인", "onAttach2");
//            Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
//            report_card_view_layout.startAnimation(trans_to_down);
//            report_card_view_layout.setVisibility(View.INVISIBLE);
//        }
    }

    /**
     * 처음 생성시 호출되고 호출되지 않음 - 초기화 작업에 필요
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("호출확인", "onCreate");
//        if (mapFragmentSparseArrayCompat == null) {
//            mapFragmentSparseArrayCompat = new SparseArrayCompat();
//        }
        //색상 별 마커 객체 생성
//        reportRedMarkerList = new ArrayList<>();
//        reportGreenMarkerList = new ArrayList<>();
//        reportWhiteMarkerList = new ArrayList<>();

        //뷰페이저어댑터 생성 및 콜백 지정
        reportInfoMarkers = new ArrayList<>();
        reportCardItemList = new ArrayList<>();

    }

    /**
     * replace 할 때마다 호출됨. 매니저에 add, replace 되는 순간 반드시 호출되는 듯
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO 캐싱

        if (report_card_view_layout != null) {
            Log.e("호출확인", "onCreateView0");
        }

        Log.e("호출확인", "onCreateView");

        // TODO 캐싱 문제를 좀 더 살펴보자. 일단은 안드로이드에서 처리해 주고 있다고 하니 굳이 추가로 캐싱을 하지 않아도 될 듯

//        if (view == null) {

        view = inflater.inflate(R.layout.map_fragment, container, false);

        ButterKnife.bind(this, view);

//        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
//
//        mapView.getMapAsync(this);

//        }

//        if (mapView == null) {

        mapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);

        mapView.getMapAsync(this);

//        }

        setCardViewPagerAdapter();


        // 현재 프래그먼트가 처음 생성되었을 때 호출, 외부에서 호출되거나 다른 탭에서 옮겨온 경우 호출되지 않음
        //if (moveCnt == 0) {

        // location 마지막에 데이터 받아온 후 isFirst false 로 세팅해준다
        if (isFirst) {

            Log.e("호출확인", "isFirst " + isFirst);

            getMyLocation();


//            //색상 별 마커 객체 생성
//            reportRedMarkerList = new ArrayList<>();
//            reportGreenMarkerList = new ArrayList<>();
//            reportWhiteMarkerList = new ArrayList<>();
//
//            //뷰페이저어댑터 생성 및 콜백 지정
//            reportInfoMarkers = new ArrayList<>();
//            reportCardItemList = new ArrayList<>();

//            reportCardAdapter = new ReportCardPagerAdapter(getContext());
//            reportCardAdapter.setCallback(this);

//            setCardViewPagerAdapter();
        } else if (fromFeed) {
            map_view_progress.setVisibility(View.INVISIBLE);

            MoldeFeedEntity entityFromFeed = ((MoldeMainActivity) getActivity()).getFeedEntity();

//            if (((MoldeMainActivity) getActivity()).getFeedEntity() != null) {
            feedData = entityFromFeed;
            moveCamera(new LatLng(feedData.getRepLat(), feedData.getRepLon()), ZOOM_FEED_MARKER);
            addFeedMarkers(feedData);
            showCardView();
//            }
            fromFeed = false;

        }
        // 외부에서 옮겨온 경우
        else {
            // 카드뷰를 보여주지 않음
            Log.e("호출확인", "onCreateView - moveCnt == 0 else ");
            map_view_progress.setVisibility(View.INVISIBLE);

//            if (reportInfoMarkers.size() > 0) {

//                Log.e("호출확인", ""+reportRedMarkerList.size());
//                Log.e("호출확인", ""+reportGreenMarkerList.size());
//                Log.e("호출확인", ""+reportWhiteMarkerList.size());
//                Log.e("호출확인", ""+reportInfoMarkers.size());

//                search_bar.setVisibility(View.VISIBLE);
//                map_view_layout.setVisibility(View.VISIBLE);
//                map_ui.setVisibility(View.VISIBLE);

//                TODO 수정하기 전에 기정군에게 물어보자
//                if (mapFragmentSparseArrayCompat.get(R.string.reportRedhMarkerList) != null) {
//                    reportRedMarkerList = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportRedhMarkerList);
//                }
//                if (mapFragmentSparseArrayCompat.get(R.string.reportGreenhMarkerList) != null) {
//                    reportGreenMarkerList = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportGreenhMarkerList);
//                }
//                if (mapFragmentSparseArrayCompat.get(R.string.reportWhitehMarkerList) != null) {
//                    reportWhiteMarkerList = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportWhitehMarkerList);
//                }
//                if (mapFragmentSparseArrayCompat.get(R.string.reportInfohMarkers) != null) {
//                    reportInfoMarkers = (ArrayList<Marker>) mapFragmentSparseArrayCompat.get(R.string.reportInfohMarkers);
//                }
//                if (mapFragmentSparseArrayCompat.get(R.string.reportCardItemList) != null) {
//                    reportCardItemList = (ArrayList<ReportCardItem>) mapFragmentSparseArrayCompat.get(R.string.reportCardItemList);
//                }
//                if (reportCardAdapter == null) {
//                    Log.e("호출확인", "reportCardAdapter == null");
//                    reportCardAdapter = new ReportCardPagerAdapter(getContext());
//                    reportCardAdapter.setCallback(this);
////                    for (int i = 0; i < reportInfoMarkers.size(); i++) {
////                        reportCardAdapter.addCardItem(new ReportCardItem(reportInfoMarkers.get(i).getTitle(), reportInfoMarkers.get(i).getSnippet()));
////                    }
//                }
//                if (reportCardShadowTransformer == null) {
//                    reportCardShadowTransformer = new ShadowTransformer(report_card_view_pager, reportCardAdapter);
//                    Log.e("호출확인", "reportCardShadowTransformer == null");
//                }
//                report_card_view_pager.setAdapter(reportCardAdapter);
//                // isFirstPresentReportCard = true;
//                report_card_view_pager.setPageTransformer(false, reportCardShadowTransformer);
//                report_card_view_pager.setOffscreenPageLimit(reportCardItemList.size());
//                reportCardShadowTransformer.enableScaling(true);

            // setCardViewPagerAdapter();

            // isFirstPresentReportCard = true;
            if (currentMarkerPosition != -1) {
                // report_card_view_pager.setCurrentItem(currentMarkerPosition);
                applyReportCardInfo(currentMarkerPosition);
            }
//                if (mapFragmentSparseArrayCompat.get(R.string.currMarkerPosition) != null) {
//                    report_card_view_pager.setCurrentItem((int) mapFragmentSparseArrayCompat.get(R.string.currMarkerPosition));
//                }

            report_card_view_layout.setVisibility(View.VISIBLE);
            isBackBtnClicked = true;
//            }
        }

        setupViews();

        setListener();

        isInit = true;

        return view;
    }

    private void setupViews() {
        map_ui.bringToFront();

        report_card_view_layout.setVisibility(View.INVISIBLE);

        favorite_new.setElevation(12);

        my_loc_button.setElevation(8);

        report_button.setElevation(8);
    }

    /**
     * 뷰 리스너
     */
    private void setListener() {

        /**
         * 검색창으로 이동
         */
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "검색 창으로 이동", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), SearchMapInfoActivity.class);
                intent.putExtra("searchTitle", searchTitle);
                startActivityForResult(intent, REQ_SEARCH_MAP, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                // startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        /**
         * 즐겨찾기 창으로 이동
         */
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();
                if (auth != null && auth.getUid() != null) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), MoldeMyFavoriteActivity.class);
                    startActivityForResult(intent, REQ_FAVORITE);
                } else {
                    Snackbar.make(getView().findViewById(R.id.map_view_layout), "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        /**
         * 신고하기
         */
        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 로그인 처리
                FirebaseAuth auth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();
                if (auth != null && auth.getUid() != null) {
                    String uId = auth.getUid();
                    Toast.makeText(getContext(), "Auth : " + uId, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(getContext(), MoldeReportActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(getView().findViewById(R.id.map_view_layout), "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 현재 위치 찾기
         */
        my_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "위치 가져오기 기능", Toast.LENGTH_LONG).show();
                loc_search_input.setText(R.string.search);
                searchTitle = "검색하기";
//                if (lat == 0.0 && lng == 0.0) {
//                    isMyLocChange = true;
                    getMyLocation();
//                } else {
//                    clearReportInfoMarkers();
                    // makeRandomMarkers(new LatLng(lat, lng));
//                    showCardView();
//                    isInit = false;
//                    isFirst = false;

//                    loadData(lat, lng);
//                }
            }
        });

        /**
         * gps 권한 설정하기
         */
        request_gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (permission == null) {
                    permission = new PermissionUtil(getActivity(), MapFragment.this);
                }

                permission.checkPermission(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION});

//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
//                            REQUEST_LOCATION);
//                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("호출확인", "onActivityResult1");

        if (resultCode == RESULT_OK && requestCode == REQ_SEARCH_MAP) {
            Log.e("호출확인", "onActivityResult2");
            MoldeSearchMapInfoEntity searchEntity
                    = (MoldeSearchMapInfoEntity) data.getSerializableExtra("mapSearchInfo");
            MoldeSearchMapHistoryEntity historyEntity
                    = (MoldeSearchMapHistoryEntity) data.getSerializableExtra("mapHistoryInfo");

            if (historyEntity != null) {
                Log.e("호출확인", "historyEntity != null");

                lat = historyEntity.getMapLat();
                lng = historyEntity.getMapLng();
                searchTitle = historyEntity.getName();
                searchInfo = historyEntity.getMainAddress();
                loc_search_input.setText(searchTitle);

                // isInit = false;
                // TODO back 키 누르는 상황 모두 처리해줘야 함
//                if (!SearchMapInfoActivity.isCheckBackPressed && !isBackBtnClicked) {
//                    // 이곳에 좌표를 기준으로 검색 메서드를 추가해야함
//                    // moveCnt++;
//                    isFirst = false;
//                }
                clearReportInfoMarkers();

                addMarker(new LatLng(lat, lng), "내 위치", "", R.drawable.my_location_icon);

                loadData(lat, lng);

            } else if (searchEntity != null) {
                Log.e("호출확인", "historyEntity != null");

                lat = searchEntity.getMapLat();
                lng = searchEntity.getMapLng();
                searchTitle = searchEntity.getName();
                searchInfo = searchEntity.getMainAddress();
                loc_search_input.setText(searchTitle);

                //isInit = false;
                // TODO back 키 누르는 상황 모두 처리해줘야 함
                if (!SearchMapInfoActivity.isCheckBackPressed && !isBackBtnClicked) {
                    //이곳에 좌표를 기준으로 검색 메서드를 추가해야함
                    // moveCnt++;
                    isFirst = false;
                }
                clearReportInfoMarkers();

                addMarker(new LatLng(lat, lng), "내 위치", "", R.drawable.my_location_icon);

                loadData(lat, lng);
            }
        } else if (resultCode == RESULT_OK && requestCode == REQ_FAVORITE) {
            MoldeFavoriteEntity myFavoriteEntity = (MoldeFavoriteEntity) data.getSerializableExtra("mapFavoriteInfo");

            if (myFavoriteEntity != null) {
                Log.e("호출확인", "myFavoriteEntity != null");

//                Log.e("즐겨찾기 정보", myFavoriteEntity.toString());

                lat = myFavoriteEntity.getFavLat();
                lng = myFavoriteEntity.getFavLon();
                searchTitle = "★" + myFavoriteEntity.getFavName();
                searchInfo = myFavoriteEntity.getFavAddr();
                loc_search_input.setText(searchTitle);

                isMyFavoriteActive = myFavoriteEntity.isActive();
                // moveCnt++;
                clearReportInfoMarkers();

                addMarker(new LatLng(lat, lng), "내 위치", "", R.drawable.ic_map_pick);

                loadData(lat, lng);
            }
        }
    }

    private void setCardViewPagerAdapter() {
        Log.e("호출확인", "setCardViewPagerAdapter");
        if (reportCardAdapter == null) {
            Log.e("호출확인", "reportCardAdapter == null");
            reportCardAdapter = new ReportCardPagerAdapter(getContext());
            // reportCardAdapter.setCallback(this);
//            for (int i = 0; i < reportInfoMarkers.size(); i++) {
//                reportCardAdapter.addCardItem(new ReportCardItem(reportInfoMarkers.get(i).getTitle(), reportInfoMarkers.get(i).getSnippet()));
//            }
        }
        //if (reportCardShadowTransformer == null) {
        Log.e("호출확인", "reportCardShadowTransformer == null");
        reportCardShadowTransformer = new ShadowTransformer(report_card_view_pager, reportCardAdapter, this);
        reportCardShadowTransformer.enableScaling(true);
        //}
        //reportCardShadowTransformer.setViewPager(report_card_view_pager);
        report_card_view_pager.setAdapter(reportCardAdapter);
        report_card_view_pager.setPageTransformer(false, reportCardShadowTransformer);
        // report_card_view_pager.setOffscreenPageLimit(reportCardItemList.size());


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("호출확인", "onResume");

        Log.e("호출확인", "searchTitle : " + searchTitle);

//        MoldeFeedEntity entityFromFeed = ((MoldeMainActivity) getActivity()).getFeedEntity();
//
//        if (((MoldeMainActivity) getActivity()).getFeedEntity() != null) {
//            feedData = entityFromFeed;
//            moveCamera(new LatLng(feedData.getRepLat(), feedData.getRepLon()), ZOOM_FEED_MARKER);
//            addFeedMarkers(feedData);
//            showCardView();
//        }
//
//        /**
//         * 피드에서 넘어올 때
//         */
//        if (getArguments() != null) {
//
//            Log.e("호출확인", "getArguments() != null");
//
//            //피드 프래그먼트에서 받아온 피드객체 데이터
//             = new MoldeFeedEntity(
//                    getArguments().getInt("reportFeedId", 0),
//                    getArguments().getString("reportFeedUserName"),
//                    getArguments().getString("reportFeedUserEmail"),
//                    getArguments().getString("reportFeedUserId"),
//                    getArguments().getString("reportFeedContent"),
//                    getArguments().getInt("reportFeedLocationLat", 0),
//                    getArguments().getInt("reportFeedLocationLng", 0),
//                    getArguments().getString("reportFeedAddress"),
//                    getArguments().getString("reportFeedDetailAddress"),
//                    getArguments().getString("reportFeedDate"),
//                    null,
//                    getArguments().getInt("reportFeedState", 0));
//
//        }

        /**
         * 검색하고 넘어올 때
         */
        if (searchTitle.equals("검색하기")) {
            Log.e("호출확인", "searchTitle.equals(검색하기)");
//             map_view_progress.setVisibility(View.INVISIBLE);
//            report_card_view_layout.bringToFront();
//            report_card_view_layout.setVisibility(View.VISIBLE);
//            map_option_layout.setVisibility(View.INVISIBLE);

            showCardView();

            if (!isAfterSearch) {
                isInit = false;
                //moveCnt++;
                isFirst = false;
                isAfterSearch = true;
                return;
            }
        }

//        /**
//         * 처음에는 호출 안 됨. 당연히 searchEntity, historyEntity, myFavoriteEntity 전부 null 로 넘어옴.
//         * 검색하고 SearchMapInfoActivity 에서 넘어올 때 값이 함께 넘어오는 듯 하다.
//         */
        //if (getActivity() != null && getActivity() instanceof MoldeMainActivity) {
//            Log.e("호출확인", "getActivity() != null");
//            MoldeSearchMapInfoEntity searchEntity = ((MoldeMainActivity) getActivity()).getMapInfoResultData();
//            MoldeSearchMapHistoryEntity historyEntity = ((MoldeMainActivity) getActivity()).getMapHistoryResultData();
//            MoldeFavoriteEntity myFavoriteEntity = ((MoldeMainActivity) getActivity()).getMyFavoriteEntity();

//            if (searchEntity != null) {
//                Log.e("호출확인", "searchEntity != null");
//                map_view_progress.setVisibility(View.INVISIBLE);
//                lat = searchEntity.getMapLat();
//                lng = searchEntity.getMapLng();
//                searchTitle = searchEntity.getName();
//                searchInfo = searchEntity.getMainAddress();
//                loc_search_input.setText(searchTitle);
//                report_card_view_layout.bringToFront();
//                report_card_view_layout.setVisibility(View.VISIBLE);
//                map_option_layout.setVisibility(View.INVISIBLE);
//                isInit = false;
//                if (!SearchMapInfoActivity.isCheckBackPressed && !isBackBtnClicked) {
//                    //이곳에 좌표를 기준으로 검색 메서드를 추가해야함
//                    // moveCnt++;
//                    isFirst = false;
//                }
//
//            } else if (historyEntity != null) {
//                Log.e("호출확인", "historyEntity != null");
//                map_view_progress.setVisibility(View.INVISIBLE);
//                lat = historyEntity.getMapLat();
//                lng = historyEntity.getMapLng();
//                searchTitle = historyEntity.getName();
//                searchInfo = historyEntity.getMainAddress();
//                loc_search_input.setText(searchTitle);
//                report_card_view_layout.bringToFront();
//                report_card_view_layout.setVisibility(View.VISIBLE);
//                map_option_layout.setVisibility(View.INVISIBLE);
//                //isInit = false;
//                // TODO back 키 누르는 상황 모두 처리해줘야 함
//                if (!SearchMapInfoActivity.isCheckBackPressed && !isBackBtnClicked) {
//                    //이곳에 좌표를 기준으로 검색 메서드를 추가해야함
//                    // moveCnt++;
//                    isFirst = false;
//                }
//                clearReportInfoMarkers();
//                makeRandomMarkers(new LatLng(lat, lng));
//                showCardView();
//                isInit = false;
//                isFirst = false;
//
//            } else if (myFavoriteEntity != null) {
//                Log.e("호출확인", "myFavoriteEntity != null");
//                map_view_progress.setVisibility(View.INVISIBLE);
//                Log.e("즐겨찾기 정보", myFavoriteEntity.toString());
//                lat = myFavoriteEntity.getFavLat();
//                lng = myFavoriteEntity.getFavLon();
//                searchTitle = "★" + myFavoriteEntity.getContent();
//                searchInfo = myFavoriteEntity.getContent();
//                loc_search_input.setText(searchTitle);
//                report_card_view_layout.bringToFront();
//                report_card_view_layout.setVisibility(View.VISIBLE);
//                map_option_layout.setVisibility(View.INVISIBLE);
//                isInit = false;
//                isMyFavoriteActive = myFavoriteEntity.isActive();
//                // moveCnt++;
//                isFirst = false;
//            }
        //}
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        if (report_card_view_layout != null) {
//            Log.e("호출확인", "onViewCreated");
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("호출확인", "onMapReady");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

//        if (feedData != null) {
//
//            Log.e("호출확인", "feedData != null");
//
//            moveCamera(new LatLng(feedData.getRepLat(), feedData.getRepLon()), ZOOM_FEED_MARKER);
//            addFeedMarkers(feedData);
//            return;
//        }

//        LatLng moveLoc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        LatLng moveLoc = new LatLng(lat, lng);
        // TODO searchTitle.equals("") 에 해당하는 값을 startActivityForResult 로 통신하도록 하자
        if (!searchTitle.equals("")) {

            Log.e("호출확인", "!searchTitle.equals()");

            if (searchTitle.charAt(searchTitle.length() - 1) == '동') {

                Log.e("호출확인", "searchTitle.charAt(searchTitle.length() - 1) == '동'");

                StringTokenizer placeInfo = new StringTokenizer(searchTitle, " ");
                String si = placeInfo.nextToken();
                String gu = placeInfo.nextToken();
                String dong = placeInfo.nextToken();
                if (gu.charAt(gu.length() - 1) == '구' && dong.charAt(dong.length() - 1) == '동') {
                    // 주변 위치에 따라 검색해오기
                    if (!isAfterSearch) {

                        // makeSearchMarkers(moveLoc);
                        addSearchMarkers(moveLoc);
                        isAfterSearch = true;
                    }
                }
            } else {

                Log.e("호출확인", "searchTitle.charAt(searchTitle.length() - 1) == '동' else");

                if (!isAfterSearch) {

//                    makeSearchMarkers(moveLoc);
//                    makeSearchMarker(moveLoc);
                    addSearchMarkers(moveLoc);
                    isAfterSearch = true;
                }
            }
        }

        /*********************** Map Click ***********************/
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("호출확인", "onMapClick");
                if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                    hideCardView();
                }
            }
        });
        /*********************** Map long Click ***********************/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.e("호출확인", "onMapLongClick");
                addMarker(latLng, "★", "", R.drawable.ic_map_pick);
            }
        });


        /*********************** Marker Click ***********************/
        //mMap.setOnMarkerClickListener(clusterManager);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                if (marker.getTitle() != null && marker.getTitle().equals("내 위치")) {
                    Log.e("호출확인", "onMarkerClick - marker.getTitle().equals(\"내 위치\")");
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.my_location_icon)));
                    // 내 위치일 경우에 카드뷰 숨김
                    if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                        hideCardView();
                    }
                    return false;
                }

                if (marker.getTitle() != null && marker.getTitle().startsWith("★")) {
                    Log.e("호출확인", "onMarkerClick - marker.getTitle().startsWith(\"★\")");
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_map_pick)));
                    // 즐겨찾기일 경우 카드뷰 숨김
                    if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                        hideCardView();
                    }

                    MoldeMyFavoriteInfoMapDialog moldeMyFavoriteInfoMapDialog = MoldeMyFavoriteInfoMapDialog.getInstance();
                    moldeMyFavoriteInfoMapDialog.setCallback(MapFragment.this, marker);
                    Bundle bundle = new Bundle();
                    bundle.putString("markerTitle", marker.getTitle().replace("★", ""));
                    bundle.putString("markerInfo", marker.getSnippet());
                    bundle.putDouble("markerLat", marker.getPosition().latitude);
                    bundle.putDouble("markerLng", marker.getPosition().longitude);
                    Log.e("호출확인",  marker.getPosition().latitude+":"+marker.getPosition().longitude);
                    bundle.putBoolean("myFavoriteActive", isMyFavoriteActive);
                    moldeMyFavoriteInfoMapDialog.setArguments(bundle);
                    moldeMyFavoriteInfoMapDialog.show(((MoldeMainActivity) getContext()).getSupportFragmentManager(), "bottomSheet");
//                    } else {
//                        MoldeMyFavoriteInfoMapDialog moldeMyFavoriteInfoMapDialog = MoldeMyFavoriteInfoMapDialog.getInstance();
//                        moldeMyFavoriteInfoMapDialog.setCallback(MapFragment.this, marker);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("markerTitle", marker.getTitle().replace("★", ""));
//                        bundle.putString("markerInfo", marker.getSnippet());
//                        bundle.putDouble("markerLat", marker.getPosition().latitude);
//                        bundle.putDouble("markerLng", marker.getPosition().longitude);
//                        bundle.putBoolean("myFavoriteActive", isMyFavoriteActive);
//                        moldeMyFavoriteInfoMapDialog.setArguments(bundle);
//                        moldeMyFavoriteInfoMapDialog.show(((MoldeMainActivity) getContext()).getSupportFragmentManager(), "bottomSheet");
//                    }
                    return false;
                }

//                if (feedData != null) {
//                    Log.e("호출확인", "onMarkerClick - feedData != null");
//                    Log.e("호출확인", marker.getTitle());
//                    Log.e("호출확인", feedData.getRepAddr());
//                    if (marker.getTitle().equals(feedData.getRepAddr())) {
//                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.my_location_icon)));
//                        if (report_card_view_layout.getVisibility() == View.VISIBLE) {
//                            hideCardView();
//                        }
//                        return false;
//                    }
//                }

                if (marker.getTitle() != null && marker.getTitle().equals(searchTitle)) {
                    Log.e("호출확인", "onMarkerClick - marker.getTitle().equals(searchTitle)");
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.my_location_icon)));
                    if (report_card_view_layout.getVisibility() == View.VISIBLE) {
                        hideCardView();
                    }
                    return false;
                }

                //if (moveCnt > 0 && isBackBtnClicked) {
//                if (!isFirst && isBackBtnClicked) {
//                    Log.e("호출확인", "onMarkerClick - moveCnt > 0 && isBackBtnClicked");
//                    showCardView();
//                }

                if (report_card_view_layout.getVisibility() == View.INVISIBLE) {
                    showCardView();
                }

//                if (reportInfoMarkers != null) {
                int position = (int) marker.getTag();

                applyReportCardInfo(position);
//                Log.e("호출확인", "onMarkerClick - reportInfoMarkers != null");
//                Log.e("호출확인", "position : " + position);
                enlargeMarkerIcon(marker, reportCardItemList.get(position).getStatus());

//                    for (int i = 0; i < reportInfoMarkers.size(); i++) {
//                        if (marker.getTitle().equals(reportInfoMarkers.get(i).getTitle())) {
//                            for (Marker redMarker : reportRedMarkerList) {
//                                if (marker.getTitle().equals(redMarker.getTitle())) {
//                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_red)));
//                                }
//                            }
//                            for (Marker greenMarker : reportGreenMarkerList) {
//                                if (marker.getTitle().equals(greenMarker.getTitle())) {
//                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_green)));
//                                }
//                            }
//                            for (Marker whiteMarker : reportWhiteMarkerList) {
//                                if (marker.getTitle().equals(whiteMarker.getTitle())) {
//                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_white)));
//                                }
//                            }
//                            report_card_view_pager.setCurrentItem(i, false);
//                            return false;
//                        }
//                    }

//                }

                return false;
            }
        });

        /*********************** Map detect Deselect Marker ***********************/
        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                Log.e("호출확인", "onInfoWindowClose");
                if (marker.getTitle().equals("내 위치")) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon));
                } else if (marker.getTitle().startsWith("★")) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pick));
                } else if (marker.getTitle().equals(searchTitle)) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon));
                } else if (marker.getTag() != null) {
                    shrinkMarkerIcon(marker, reportCardItemList.get((int) marker.getTag()).getStatus());
                }

//                else if (feedData != null) {
//                    if (marker.getTitle().equals(feedData.getRepAddr())) {
//                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon));
//                    }
//                    for (Marker redMarker : reportRedMarkerList) {
//                        if (marker.getTitle().equals(redMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
//                        }
//                    }
//                    for (Marker greenMarker : reportGreenMarkerList) {
//                        if (marker.getTitle().equals(greenMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
//                        }
//                    }
//                    for (Marker whiteMarker : reportWhiteMarkerList) {
//                        if (marker.getTitle().equals(whiteMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_white));
//                        }
//                    }
//                } else {
//                    for (Marker redMarker : reportRedMarkerList) {
//                        if (marker.getTitle().equals(redMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
//                        }
//                    }
//                    for (Marker greenMarker : reportGreenMarkerList) {
//                        if (marker.getTitle().equals(greenMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
//                        }
//                    }
//                    for (Marker whiteMarker : reportWhiteMarkerList) {
//                        if (marker.getTitle().equals(whiteMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_white));
//                        }
//                    }
//                }
            }
        });

    }

    @Override
    public void applyMyFavoriteInfo(String title, String info, Marker marker) {
        marker.setTitle("★" + title);
        marker.setSnippet(info);
    }

    @Override
    public void setMyFavoriteActive(boolean active) {
        this.isMyFavoriteActive = active;
    }

//    /**
//     * //     * GPS, Network 를 이용해 현재 위치 찾기
//     * //
//     */
//    public void getMyLocation() {
//        Log.e("호출확인", "getMyLocation");
//        manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
//        boolean isGpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (isGpsEnable) {
////            final long minTime = 1500;
////            final float minDistance = 100;
////            if (myLocationListener == null) {
//
//
//            permission = PermissionUtil.getInstance(getActivity(), this);
//
//            permission.checkPermission(new String[]{
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION});
//
////                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
////                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                    ActivityCompat.requestPermissions(getActivity(),
////                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
////                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
////                            REQUEST_LOCATION);
////                    return;
////                }
////                myLocationListener = new MyLocationListener();
////            }
////            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
////            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
//            //  gpsRequestTime = System.currentTimeMillis();
////            }
//        } else {
//            // TODO GPS 사용페이지로 넘어감
//            Toast.makeText(getContext(), "GPS를 사용할 수 있도록 켜주셔야 사용이 가능합니다.", Toast.LENGTH_LONG).show();
//            map_view_progress.setVisibility(View.INVISIBLE);
//        }
//    }

    /**
     * 현재 위치 찾기
     */
    public void getMyLocation() {

        map_view_progress.setVisibility(View.VISIBLE);

        Log.e("호출확인", "getMyLocation");
        a = System.currentTimeMillis();

        if (manager == null) {
            manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        boolean isGpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnable) {

            Log.e("호출확인", "getMyLocation : " + (System.currentTimeMillis() - a));

//            final long minTime = 1500;
//            final float minDistance = 100;
//            if (myLocationListener == null) {
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
//                            REQUEST_LOCATION);

            /**
             * 이틀에 걸쳐 해결하지 못했던 문제의 원인은 static 으로 계속 메모리에 올라가 있던 PermissionUtil 에게
             * activity 와 this 를 통째로 넘겨준 것이었다. back 키를 통해 앱을 종료하고 다시 실행시키면 activity, fragment
             * 는 죽는데 문제는 static 에 넘겨준 객체는 죽지 않고 그대로 살아 있었던 것이다. getInstance 를 했을 때
             * 당연히 기존에 있던 객체를 사용했던 것이고 인자를 넘겨줘도 기존에 넘겨받았던 액티비티와 콜백함수를 사용했으니
             * 당연히 getContext 를 했을 때 이전에는 죽어버렸던 객체를 호출하게 된 것이다. static 이 아주 흉물스러운 것이라는
             * 것을 다시 느낌. 생각해보면 이전에도 이런 일이 있었는데 강사님이 해결해 줬던 기억이 난다.
             */
            if (permission == null) {
                permission = new PermissionUtil(getActivity(), this);
            }

            // permission = PermissionUtil.getInstance(getActivity(), this);
//
            permission.checkPermission(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION});
            // return;
//                }
//                myLocationListener = new MyLocationListener();
//            }
//            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
//            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
            //gpsRequestTime = System.currentTimeMillis();
        } else {
            Toast.makeText(getContext(), "GPS를 사용할 수 있도록 켜주셔야 사용이 가능합니다.", Toast.LENGTH_LONG).show();
            map_view_progress.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 현재 위치 업데이트 요청 & 삭제
     */
    @SuppressLint("MissingPermission")
    private void setLocationListener() {
        // TODO 6-9초 걸리는 거 해결해야 함
        Log.e("호출확인", "setLocationListener : " + (System.currentTimeMillis() - a));
        long minTime = 1500;
        float minDistance = 100;
        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener();
        }
        Log.e("호출확인", "setLocationListener : " + (System.currentTimeMillis() - a));
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
    }

    private void removeLocationListener() {
        if (manager != null && myLocationListener != null) {
            // 데이터를 받아오고 난 후 업데이트를 취소한다.
            // TODO 콜백으로 바꿔야 함
            Log.e("호출확인", "onLocationChanged4");
            manager.removeUpdates(myLocationListener);
            //manager = null;
            // myLocationListener = null;
            map_view_progress.setVisibility(View.INVISIBLE);
        }
    }

//    /**
//     * GPS 권한체크 및 얻기
//     */
//    public void onPermissionCheck(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        // permission.onResult(grantResults);
//        if (requestCode == REQ_CODE) {
////            for (int grantResult : grantResults) {
////                if (grantResult == PackageManager.PERMISSION_DENIED) {
////                    progress_loading.setVisibility(View.INVISIBLE);
////                    request_gps_button.setVisibility(View.VISIBLE);
////                    return;
////                }
////            }
////            getMyLocation();
//            permission.onResult(grantResults);
//        }
//    }

    /**
     * GPS 권한체크
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE) {
//            for (int grantResult : grantResults) {
//                if (grantResult == PackageManager.PERMISSION_DENIED) {
//                    progress_loading.setVisibility(View.INVISIBLE);
//                    request_gps_button.setVisibility(View.VISIBLE);
//                    return;
//                }
//            }
//            getMyLocation();
            permission.onResult(grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionGranted() {
        long minTime = 1500;
        float minDistance = 100;
//        if (manager == null)
//         manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // boolean isGpsEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // if (isGpsEnable) {
//            if (myLocationListener == null) {

        // if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        // return;
        // }
//                myLocationListener = new MyLocationListener();
//            }
//            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
//            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
//        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
//        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
        setLocationListener();
        // }
//        getMyLocation();
    }

    @Override
    public void onPermissionDenied() {
        progress_loading.setVisibility(View.INVISIBLE);
        request_gps_button.setVisibility(View.VISIBLE);
        showAskAgainDialog();
    }

    private void showAskAgainDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("권한 설정 필요")
                .setMessage("현재 기능을 사용하기 위해서는 해당 권한 설정이 필요합니다. 설정 페이지로 넘어가시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goSettings();
                    }
                })
                .setNegativeButton("아니오", null)
                .create();
        dialog.show();
    }

    private void goSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS,
                Uri.fromParts("package", getContext().getPackageName(), null));
        startActivity(intent);
    }

    /**
     * 위치가 변경될 때 피드를 삭제해준다
     */
    public void clearReportInfoMarkers() {
        Log.e("호출확인", "clearReportInfoMarkers");
        mMap.clear();
        if (reportInfoMarkers.size() > 0 || reportCardItemList.size() > 0) {
            for (Marker marker : reportInfoMarkers) {
                marker.remove();
            }
            reportInfoMarkers.clear();
            reportCardItemList.clear();

            reportCardAdapter.removeAllCardItem(reportCardItemList);
            /**
             * makeRandomMarker 와 바꾸기만 하면 안 되길래 뭐가 문제인지 파악하지 못했는데
             * 결국 어떤 차이가 있었던 것이 아니라 데이터가 유동적으로 변경된 것이 문제였음
             * 즉, 네트워크에서 받아오는 데이터는 10 -> 0개로 변경이 일어났는데 그것을 계속 어댑터에서만
             * 변경하고 뷰에 반영을 해주지 않아 이전 뷰페이저에서는 2-3개 남아있던 아이템이 계속 살아있었던 것이다
             * 다만 어댑터는 변경되었기 떄문에 어디에서도 호출이 발생하지 않았던 것
             */
            report_card_view_pager.setAdapter(reportCardAdapter);
            // reportCardAdapter.notifyDataSetChanged();
        }
//        reportCardAdapter.removeAllCardItem();
//        report_card_view_pager.setAdapter(reportCardAdapter);
    }

//    private LocationListener locationListener = new LocationListener() {
//
//        int locChangeCount = 0;
//
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.e("호출확인", "onLocationChanged1");
//            if (searchTitle.equals("") || isMyLocChange) {
//                Log.e("호출확인", "onLocationChanged2");
//                /*if(System.currentTimeMillis() - gpsRequestTime > 3000){
//                    Toast.makeText(getContext(), "건물 안에서는 더 오랜 시간이 걸립니다", Toast.LENGTH_SHORT).show();
//                }*/
//                if (locChangeCount == 0) {
//                    Log.e("호출확인", "onLocationChanged3");
//                    // TODO 여기처럼 하나 지운 곳 하나 있었는데 어딘지 모르겠음. 확인요망
////                    lat = Double.toString(location.getLatitude());
////                    lng = Double.toString(location.getLongitude());
////                    LatLng myLocation = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
//                    lat = location.getLatitude();
//                    lng = location.getLongitude();
//                    LatLng myLocation = new LatLng(lat, lng);
//                    MoldeApplication.myLocation = myLocation;
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
////                    if (myMarker != null) {
////                        myMarker.remove();
////                    }
////                    myMarker = mMap.addMarker(
////                            new MarkerOptions()
////                                    .position(myLocation)
////                                    .title("내 위치")
////                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon))
////                    );
//                    makeNewMarker(myLocation, "내 위치", "", R.drawable.my_location_icon);
//                    //locChangeCount++;
//                    isMyLocChange = false;
//                    //if (moveCnt > 0) {
//                    if (reportInfoMarkers.size() > 0 || reportCardItemList.size() > 0) {
//                        clearReportInfoMarkers();
//                    }
//                    beforeCallApplyMethodTimeList = new ArrayList<>();
//                    reportCardPositionList = new ArrayList<>();
//                    makeRandomMarkers(myLocation);
//                    report_card_view_layout.setVisibility(View.VISIBLE);
//                    map_option_layout.setVisibility(View.INVISIBLE);
//                    showCardView();
//                    isInit = false;
//                    //}
//                    moveCnt++;
//                    Log.e("호출확인", "moveCnt " + moveCnt);
//                }
//            }
//
//            if (manager == null) {
//                Log.e("호출확인", "manager == null");
//            }
//
//            if (locationListener == null) {
//                Log.e("호출확인", "myLocationListener == null");
//            }
//
//
//            if (manager != null && locationListener != null) {
//                Log.e("호출확인", "onLocationChanged4");
//                manager.removeUpdates(locationListener);
//                // manager = null;
//                // myLocationListener = null;
//                map_view_progress.setVisibility(View.INVISIBLE);
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };

    /**
     * 위치 변경 리스너
     */
    public class MyLocationListener implements LocationListener {
        //위치정보 보여주기
        //구글 맵 이동
        // int locChangeCount = 0;

        @Override
        public void onLocationChanged(Location location) {

            Log.e("호출확인", "onLocationChanged : " + (System.currentTimeMillis() - a));

            Log.e("호출확인", "onLocationChanged1");

            // TODO if문 왜 있는거지 - searchTitle 은 살려야 할 듯
            // if (searchTitle.equals("") || isMyLocChange) {
            Log.e("호출확인", "onLocationChanged2");
                /*if(System.currentTimeMillis() - gpsRequestTime > 3000){
                    Toast.makeText(getContext(), "건물 안에서는 더 오랜 시간이 걸립니다", Toast.LENGTH_SHORT).show();
                }*/
            // if (locChangeCount == 0) {
            Log.e("호출확인", "onLocationChanged3");
            // TODO 여기처럼 하나 지운 곳 하나 있었는데 어딘지 모르겠음. 확인요망
//                    lat = Double.toString(location.getLatitude());
//                    lng = Double.toString(location.getLongitude());
//                    LatLng myLocation = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
            // 현재위치 위경도 좌표 가져오기
            lat = location.getLatitude();
            lng = location.getLongitude();
            LatLng currLocation = new LatLng(lat, lng);
            // 다른 곳에서 사용할 수 있도록 위경도 좌표 app 단위 세팅
            setCurrentLocation(currLocation);
            // 현재 좌표로 지도 이동
            moveCamera(currLocation, ZOOM_CUR_LOCATION);


//                    if (myMarker != null) {
//                        myMarker.remove();
//                    }
//                    myMarker = mMap.addMarker(
//                            new MarkerOptions()
//                                    .position(myLocation)
//                                    .title("내 위치")
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_icon))
//                    );
            // 현재위치 마커 추가
            clearReportInfoMarkers();

            addMarker(currLocation, "내 위치", "", R.drawable.my_location_icon);
            // locChangeCount++;
            // isMyLocChange = false;
            //if (moveCnt > 0) {

//                if (reportInfoMarkers.size() > 0 || reportCardItemList.size() > 0) {
//            clearReportInfoMarkers();
////                }
//
////                beforeCallApplyMethodTimeList = new ArrayList<>();
////                reportCardPositionList = new ArrayList<>();
//            Log.e("호출확인", "isInit " + isInit);
//            // 데이터 받아오기
//            makeRandomMarkers(currLocation);
//            // report_card_view_layout.setVisibility(View.VISIBLE);
//            // map_option_layout.setVisibility(View.INVISIBLE);
//            showCardView();
//            isInit = false;
//            //}
//            // moveCnt++;
//            isFirst = false;
//            clearReportInfoMarkers();

            loadData(lat, lng);

            Log.e("호출확인", "isFirst " + isFirst);
            // }
            //}

//            if (manager == null) {
//                Log.e("호출확인", "manager == null");
//            }
//
//            if (myLocationListener == null) {
//                Log.e("호출확인", "myLocationListener == null");
//            }

            removeLocationListener();

//            if (manager != null && myLocationListener != null) {
//                // 데이터를 받아오고 난 후 업데이트를 취소한다.
//                // TODO 콜백으로 바꿔야 함
//                Log.e("호출확인", "onLocationChanged4");
//                manager.removeUpdates(myLocationListener);
//                //manager = null;
//                myLocationListener = null;
//                map_view_progress.setVisibility(View.INVISIBLE);
//            }
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

//    private void simpleRoutineWithLocation(LatLng latLng) {
//        clearReportInfoMarkers();
//
//        loadData(latLng.latitude, latLng.longitude);
//        // makeRandomMarkers(latLng);
//
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        if (manager != null && myLocationListener != null) {
//            Log.e("호출확인", "onLocationChanged4");
//            manager.removeUpdates(myLocationListener);
//            //manager = null;
//            myLocationListener = null;
//            map_view_progress.setVisibility(View.INVISIBLE);
//        }
//    }

    /**
     * 현재 위치 좌표 설정
     */
    private void setCurrentLocation(LatLng currLocation) {
        ((MoldeApplication) getActivity().getApplication()).setCurrLocation(currLocation);
    }

    /**
     * 카메라 이동
     */
    private void moveCamera(LatLng location, int zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
    }

    /**
     * 빈 정보의 마커 생성 & 정보 추가 & 즐겨찾기
     */
    public Marker addMarker(LatLng latLng, String title, String snippet, int icon) {
        Log.e("호출확인", "addMarker");

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
        return mMap.addMarker(options);
    }

    /**
     * 피드에서 선택한 데이터로 마커 생성
     */
    public void addFeedMarkers(MoldeFeedEntity feedData) {
        Log.e("호출확인", "makeFeedMarker");
//        if (feedMarker != null) {
//            feedMarker.remove();
//        }
        clearReportInfoMarkers();
//        mMap.clear();
        LatLng feedLocation = new LatLng(feedData.getRepLat(), feedData.getRepLon());
        Marker feedMarker = addMarker(
                feedLocation,
                feedData.getRepAddr(),
                feedData.getRepDetailAddr(),
                R.drawable.my_location_icon);
        feedMarker.showInfoWindow();

        loadData(feedLocation.latitude, feedLocation.longitude);
        // makeRandomMarkers(feedLocation);
//        report_card_view_layout.setVisibility(View.VISIBLE);
//        map_option_layout.setVisibility(View.INVISIBLE);
//        showCardView();
        // TODO isInit 무슨 용도인지 모르겠음
        isInit = false;
    }

    /**
     * 검색한 위치로 마커 생성
     */
    public void addSearchMarkers(LatLng moveLoc) {
        Log.e("호출확인", "makeSearchMarker");
//        mMap.clear();
        clearReportInfoMarkers();
        Marker newMarker = addMarker(
                moveLoc,
                searchTitle,
                searchInfo,
                R.drawable.my_location_icon);
        newMarker.showInfoWindow();
//        clearReportInfoMarkers();
        // makeRandomMarkers(moveLoc);
        loadData(moveLoc.latitude, moveLoc.longitude);
    }

    /**
     * 마커 크기 키우기
     */
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

//    private void loadData(int perPage, int page) {
//        fetchByLocation(perPage, page);
//    }

    private void loadData(double lat, double lng, int perPage, int page) {

        // LatLng latLng = ((MoldeApplication) getActivity().getApplication()).getCurrLocation();

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<MoldeFeedResponseInfoEntityList> call
                = feedService.getPagedFeedByDistance(lat, lng, perPage, page);

        call.enqueue(new Callback<MoldeFeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeFeedResponseInfoEntityList> call,
                                   Response<MoldeFeedResponseInfoEntityList> response) {
                // List<MoldeFeedEntity> entities = fromSchemaToLocalEntity(response.body().getData());

                List<MoldeFeedResponseInfoEntity> entityList = response.body().getData();

                for (int i = 0; i < entityList.size(); i++) {
                    MoldeFeedResponseInfoEntity entity = entityList.get(i);
                    LatLng location = new LatLng(entity.getRepLat(), entity.getRepLon());
                    Marker marker = null;
                    switch (entityList.get(i).getRepState()) {
                        case RECEIVING:
                        case ACCEPTED:
                            marker = addMarker(location, entity.getRepContents(),
                                    location.latitude + ", " + location.longitude,
                                    R.drawable.ic_marker_red);
                            break;
                        case FOUND:
                            marker = addMarker(location, entity.getRepContents(),
                                    location.latitude + ", " + location.longitude,
                                    R.drawable.ic_marker_white);
                            break;
                        case CLEAN:
                            marker = addMarker(location, entity.getRepContents(),
                                    location.latitude + ", " + location.longitude,
                                    R.drawable.ic_marker_green);
                            break;
                    }
                    marker.setTag(i);
                    reportInfoMarkers.add(marker);
                    reportCardItemList.add(new ReportCardItem(marker.getTitle(), marker.getSnippet(), entity.getRepState(), entity.getRepId()));
                }

                updateData(reportCardItemList);

                applyReportCardInfo(0);

                showCardView();

                isInit = false;

                isFirst = false;
            }

            @Override
            public void onFailure(Call<MoldeFeedResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void loadData(final double lat, final double lng) {

        // LatLng latLng = ((MoldeApplication) getActivity().getApplication()).getCurrLocation();

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<MoldeFeedResponseInfoEntityList> call
                = feedService.getFeedByDistance(lat, lng);

        call.enqueue(new Callback<MoldeFeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeFeedResponseInfoEntityList> call,
                                   Response<MoldeFeedResponseInfoEntityList> response) {
                // List<MoldeFeedEntity> entities = fromSchemaToLocalEntity(response.body().getData());

                List<MoldeFeedResponseInfoEntity> entityList = response.body().getData();

                if (entityList.size() != 0) {

                    for (int i = 0; i < entityList.size(); i++) {
                        MoldeFeedResponseInfoEntity entity = entityList.get(i);
                        LatLng location = new LatLng(entity.getRepLat(), entity.getRepLon());
                        Marker marker = null;
                        switch (entityList.get(i).getRepState()) {
                            // TODO 데이터 제대로 들어가면 변경할 것
                            case RECEIVING:
                            case ACCEPTED:
                                marker = addMarker(location, entity.getRepContents(),
                                        location.latitude + ", " + location.longitude,
                                        R.drawable.ic_marker_red);
                                break;
                            case FOUND:
                                marker = addMarker(location, entity.getRepContents(),
                                        location.latitude + ", " + location.longitude,
                                        R.drawable.ic_marker_white);
                                break;
                            case CLEAN:
                                marker = addMarker(location, entity.getRepContents(),
                                        location.latitude + ", " + location.longitude,
                                        R.drawable.ic_marker_green);
                                break;
                        }
                        marker.setTag(i);
                        reportInfoMarkers.add(marker);
                        reportCardItemList.add(new ReportCardItem(marker.getTitle(), marker.getSnippet(), entity.getRepState(), entity.getRepId()));
                    }

                    updateData(reportCardItemList);

                    applyReportCardInfo(0);

                    showCardView();

                    isInit = false;

                    isFirst = false;

                } else {

                    Toast.makeText(getContext(), "신고된 몰래카메라가 없는 지역입니다", Toast.LENGTH_LONG).show();


                    moveCamera(new LatLng(lat, lng), ZOOM_CUR_LOCATION);


                    // clearReportInfoMarkers();
                }


            }

            @Override
            public void onFailure(Call<MoldeFeedResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private List<MoldeFeedEntity> fromSchemaToLocalEntity(List<MoldeFeedResponseInfoEntity> entities) {
        List<MoldeFeedEntity> data = new ArrayList<>();
        for (MoldeFeedResponseInfoEntity entity : entities) {
            data.add(new MoldeFeedEntity(
                    entity.getRepId(),
                    entity.getUserName(),
                    entity.getUserEmail(),
                    entity.getUserId(),
                    entity.getRepContents(),
                    entity.getRepLat(),
                    entity.getRepLon(),
                    entity.getRepAddr(),
                    entity.getRepDetailAddr(),
                    entity.getRepDate(),
                    entity.getRepImg(),
                    entity.getRepState()
            ));
        }
        return data;
    }

    /**
     * 랜덤으로 마커 정보 10개 추가 및 카드뷰 추가 - 테스트용
     * TODO 네트워크 통신으로 변경
     */
    private void makeRandomMarkers(LatLng moveLoc) {
        Log.e("호출확인", "makeRandomMarkers");
        double start = -0.000000001;
        double end = 0.000000001;
        double rng = (end - start) + 0.01;
        Random randomGenerator = new Random();
        for (int i = 0; i < 10; i++) {
            double rndValLat = (randomGenerator.nextDouble() * rng) + start;
            double rndValLng = (randomGenerator.nextDouble() * rng) + start;
            LatLng latLng = new LatLng(moveLoc.latitude + rndValLat, moveLoc.longitude + rndValLng);

            if (i % 3 == 0) {
                Marker currMarker = addMarker(latLng,
                        i + 1 + "번째",
                        latLng.latitude + ", " + latLng.longitude,
                        R.drawable.ic_marker_red);
                currMarker.setTag(i);
                reportInfoMarkers.add(currMarker);
                // reportRedMarkerList.add(currMarker);
                reportCardItemList.add(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet(), i % 3));
                //reportCardAdapter.addCardItem(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
            } else if (i % 3 == 1) {
                Marker currMarker = addMarker(latLng,
                        i + 1 + "번째",
                        latLng.latitude + ", " + latLng.longitude,
                        R.drawable.ic_marker_green);
                currMarker.setTag(i);
                reportInfoMarkers.add(currMarker);
                // reportGreenMarkerList.add(currMarker);
                reportCardItemList.add(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet(), i % 3));
                //reportCardAdapter.addCardItem(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
            } else {
                Marker currMarker = addMarker(latLng,
                        i + 1 + "번째",
                        latLng.latitude + ", " + latLng.longitude,
                        R.drawable.ic_marker_white);
                currMarker.setTag(i);
                reportInfoMarkers.add(currMarker);
                // reportWhiteMarkerList.add(currMarker);
                reportCardItemList.add(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet(), i % 3));
                //reportCardAdapter.addCardItem(new ReportCardItem(currMarker.getTitle(), currMarker.getSnippet()));
            }
        }

        Log.e("호출확인", "makeRandomMarkers setData 이전");

        // reportCardAdapter.notifyDataSetChanged();

//        reportCardShadowTransformer = new ShadowTransformer(report_card_view_pager, reportCardAdapter);
//        report_card_view_pager.setAdapter(reportCardAdapter);
        // TODO Background sticky concurrent mark sweep GC freed 이거 뭐냐
        // report_card_view_pager.setOffscreenPageLimit(reportCardItemList.size());


//        isFirstPresentReportCard = true;
//        reportCardAdapter.setData(reportCardItemList);
        updateData(reportCardItemList);

        // onPageSelected 에서 첫 번쨰 값이 호출되지 않기 때문에 수동으로 첫번째 값으로 옮겨가도록 호출해준다
        applyReportCardInfo(0);
//        report_card_view_pager.setPageTransformer(false, reportCardShadowTransformer);

        showCardView();

        isInit = false;

        isFirst = false;
        //reportCardShadowTransformer.enableScaling(true);

//        mapFragmentSparseArrayCompat.append(R.string.reportInfohMarkers, reportInfoMarkers);
//        mapFragmentSparseArrayCompat.append(R.string.reportRedhMarkerList, reportRedMarkerList);
//        mapFragmentSparseArrayCompat.append(R.string.reportGreenhMarkerList, reportGreenMarkerList);
//        mapFragmentSparseArrayCompat.append(R.string.reportWhitehMarkerList, reportWhiteMarkerList);
//        mapFragmentSparseArrayCompat.append(R.string.reportCardItemList, reportCardItemList);
    }

    /**
     * 하단 카드뷰 뷰페이저 데이터 갱신
     */
    private void updateData(List<ReportCardItem> data) {
//        isFirstPresentReportCard = true;
        reportCardAdapter.setData(data);
    }

    /**
     * 뷰페이저 아이템 변경시 하단 카드 데이터 변경 콜백 함수
     */
    @Override
    public void applyReportCardInfo(int position) {

        Log.e("호출확인", "applyReportCardInfo : " + position);
        // if (reportInfoMarkers != null) {
        Log.e("호출확인", "reportInfoMarkers != null");

//            if (!isInit && !isBackBtnClicked) {
//                Log.e("호출확인", "!isInit && !isBackBtnClicked");
//                if (isFirstPresentReportCard) {
//                    Log.e("호출확인", "isFirstPresentReportCard");
//                    if (position == 1) {
//                        Log.e("호출확인", "position == 1");
//                        Marker marker = reportInfoMarkers.get(0);
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
//                        marker.showInfoWindow();
//                        isFirstPresentReportCard = false;
//                        return;
//                    }
//                }
//            }

        currentMarkerPosition = position;
        ReportCardItem item = reportCardItemList.get(position);
        Marker marker = reportInfoMarkers.get(position);
        // TODO zoom Constant 로 전부 설정할 것
        moveCamera(marker.getPosition(), ZOOM_FEED_MARKER);
        marker.showInfoWindow();
        report_card_view_pager.setCurrentItem(currentMarkerPosition, false);
        enlargeMarkerIcon(marker, item.getStatus());

//        if (isFirstPresentReportCard) {
//            Log.e("호출확인", "isFirstPresentReportCard");
//
//            Marker marker = reportInfoMarkers.get(0);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
//            marker.showInfoWindow();
//            isFirstPresentReportCard = false;
//            return;
//        }
//
//        if (beforeCallApplyMethodTimeList != null) {
//            Log.e("호출확인", "beforeCallApplyMethodTimeList != null");
//            beforeCallApplyMethodTimeList.add(System.currentTimeMillis());
//            reportCardPositionList.add(position);
//            if (beforeCallApplyMethodTimeList.size() == 2) {
//                Log.e("호출확인", "beforeCallApplyMethodTimeList.size() == 2");
//                if (beforeCallApplyMethodTimeList.get(1) - beforeCallApplyMethodTimeList.get(0) < 0.01) {
//                    beforeCallApplyMethodTimeList.remove(1);
//                } else {
//                    Log.e("호출확인", "beforeCallApplyMethodTimeList.size() == 2 else ");
//                    // 현재 위치 설정
//                    int currPosition = reportCardPositionList.get(0);
//                    Marker marker = reportInfoMarkers.get(currPosition);
//                    currentMarkerPosition = currPosition;
//                    // mapFragmentSparseArrayCompat.append(R.string.currMarkerPosition, currPosition);
//                    // 선택된 아이콘 크기 키우기
//                    for (Marker redMarker : reportRedMarkerList) {
//                        if (marker.getTitle().equals(redMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_red)));
//                        }
//                    }
//                    for (Marker greenMarker : reportGreenMarkerList) {
//                        if (marker.getTitle().equals(greenMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_green)));
//                        }
//                    }
//                    for (Marker whiteMarker : reportWhiteMarkerList) {
//                        if (marker.getTitle().equals(whiteMarker.getTitle())) {
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_white)));
//                        }
//                    }
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
//                    marker.showInfoWindow();
//                    beforeCallApplyMethodTimeList.clear();
//                    reportCardPositionList.clear();
//                }
//            }
//        }
        // }
    }

    private void enlargeMarkerIcon(Marker marker, int status) {
        switch (status) {
            case RECEIVING:
            case ACCEPTED:
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_red)));
                break;
            case FOUND:
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_white)));
                break;
            case CLEAN:
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(sizeUpMapIcon(R.drawable.ic_marker_green)));
                break;
        }
    }

    private void shrinkMarkerIcon() {
        for (Marker marker : reportInfoMarkers) {
            switch (reportCardItemList.get((int) marker.getTag()).getStatus()) {
                case RECEIVING:
                case ACCEPTED:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
                    break;
                case FOUND:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_white));
                    break;
                case CLEAN:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
                    break;
            }
        }
    }

    private void shrinkMarkerIcon(Marker marker, int status) {
        switch (status) {
            case RECEIVING:
            case ACCEPTED:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
                break;
            case FOUND:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_white));
                break;
            case CLEAN:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
                break;
        }
    }

    /**
     * 뒤로가기
     */
    @Override
    public void onBackKey() {
        if (!isBackBtnClicked && !isInit) {
            hideCardView();
        }
    }

    /**
     * 카드뷰 숨기기
     */
    private void hideCardView() {
        Animation trans_to_down = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
        report_card_view_layout.startAnimation(trans_to_down);
        report_card_view_layout.setVisibility(View.INVISIBLE);
        report_card_view_layout.setClickable(false);
        map_option_layout.setVisibility(View.VISIBLE);
        isBackBtnClicked = true;
    }

    /**
     * 카드뷰 보이기
     */
    private void showCardView() {
        Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
        report_card_view_layout.setVisibility(View.VISIBLE);
        report_card_view_layout.startAnimation(trans_to_up);
        report_card_view_layout.bringToFront();
        map_option_layout.setVisibility(View.INVISIBLE);
        isBackBtnClicked = false;
    }

    //----------------------------------------------------------------------------------------------
    // 삭제 영역
    //----------------------------------------------------------------------------------------------



}
