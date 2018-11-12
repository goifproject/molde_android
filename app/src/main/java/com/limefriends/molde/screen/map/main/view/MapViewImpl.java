package com.limefriends.molde.screen.map.main.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
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

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.FavoriteDialog;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.mapView.GoogleMapView;
import com.limefriends.molde.screen.common.pagerHelper.adapter.ReportCardPagerAdapter;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import com.limefriends.molde.screen.common.dialog.view.ReportCardListDialog;
import com.limefriends.molde.screen.common.pagerHelper.common.ShadowTransformer;

import java.util.List;

import static com.limefriends.molde.common.Constant.ReportState.ACCEPTED;
import static com.limefriends.molde.common.Constant.ReportState.CLEAN;
import static com.limefriends.molde.common.Constant.ReportState.FOUND;
import static com.limefriends.molde.common.Constant.ReportState.RECEIVING;
import static com.limefriends.molde.screen.common.mapView.GoogleMapView.ZOOM_FEED_MARKER;

public class MapViewImpl extends BaseObservableView<MapView.Listener> implements MapView {

    public static final int MARKER_MY_LOCATION = -1;
    private static final int MARKER_MY_LOCATION_HISTORY = -2;
    private static final int MARKER_MY_LOCATION_SEARCH = -3;
    public static final int MARKER_MY_LOCATION_FAVORITE = -4;
    public static final int MARKER_MY_LOCATION_PRE_FAVORITE = -5;
    public static final int MARKER_SAFEHOUSE = -6;
    private static final String ADD_FAVORITE_FEED_DIALOG = "ADD_FAVORITE_FEED_DIALOG";
    private static final String REPORT_CARD_LIST_DIALOG = "REPORT_CARD_LIST_DIALOG";
    private static final String RE_ASK_PERMISSION_DIALOG = "RE_ASK_PERMISSION_DIALOG";

    // 전체 컨테이
    private FrameLayout map_container;

    /**
     * 지도
     */
    // 지도 위에 올라가는 검색창, 하단 옵션, 로딩중 레이아웃을 담는 레이아웃
    private RelativeLayout map_ui;

    /**
     * 검색창
     */
    private LinearLayout search_bar;
    private TextView loc_search_input;

    /**
     * 하단에 구성한 옵션
     */
    private RelativeLayout map_option_layout;
    // 현재 위치
    private ImageView my_loc_button;
    // 즐겨찾기
    private ImageView favorite_button;
    // 신고하기
    private ImageView report_button;

    /**
     * 만약 로딩 중이거나 권한이 없을 때
     */
    private FrameLayout map_view_progress;

    /**
     * 하단 카드뷰
     */
    private ViewPager report_card_view_pager;

    private ViewFactory mViewFactory;
    private GoogleMapView mGoogleMapView;
    private ToastHelper mToastHelper;
    private DialogFactory mDialogFactory;
    private DialogManager mDialogManager;
    private ImageLoader mImageLoader;

    private ReportCardPagerAdapter reportCardAdapter;


    public MapViewImpl(LayoutInflater inflater,
                       ViewGroup parent,
                       ViewFactory viewFactory,
                       ToastHelper toastHelper,
                       DialogManager dialogManager,
                       DialogFactory dialogFactory,
                       ImageLoader imageLoader) {
        setRootView(inflater.inflate(R.layout.fragment_map, parent, false));

        this.mViewFactory = viewFactory;
        this.mToastHelper = toastHelper;
        this.mDialogManager = dialogManager;
        this.mDialogFactory = dialogFactory;
        this.mImageLoader = imageLoader;

        setupViews();

        setupListener();

        setCardViewPagerAdapter();
    }

    /**
     * setup view
     */

    private void setupViews() {

        map_container = findViewById(R.id.map_container);
        map_ui = findViewById(R.id.map_ui);
        search_bar = findViewById(R.id.loc_search_bar);
        loc_search_input = findViewById(R.id.loc_search_input);
        map_option_layout = findViewById(R.id.map_option_layout);
        my_loc_button = findViewById(R.id.my_loc_button);
        favorite_button = findViewById(R.id.favorite_button);
        report_button = findViewById(R.id.report_button);
        map_view_progress = findViewById(R.id.map_view_progress);
        report_card_view_pager = findViewById(R.id.report_card_view_pager);
        FrameLayout map_fragment_container = findViewById(R.id.map_fragment_container);
        mGoogleMapView = mViewFactory
                .newInstance(GoogleMapView.class, map_fragment_container);
        map_fragment_container.addView(mGoogleMapView.getRootView());

        map_ui.bringToFront();
        report_card_view_pager.setVisibility(View.GONE);
        my_loc_button.setElevation(8);
        report_button.setElevation(8);
    }


    private void setupListener() {

        // 맵 클릭
        mGoogleMapView.setOnMapClickListener(latLng -> hideCardView());

        // 맵 롱클릭
        mGoogleMapView.setOnMapLongClickListener(latLng -> {

            for (Listener listener : getListeners()) {
                if (!listener.onMapLongClicked()) return;
            }

            Marker newFavoriteMarker = mGoogleMapView.addMarker(
                    latLng,
                    getContext().getText(R.string.marker_title_favorite).toString(),
                    "",
                    R.drawable.ic_map_pick,
                    MARKER_MY_LOCATION_PRE_FAVORITE);

            newFavoriteMarker.setIcon(getBitmapDescriptor(sizeUpMapIcon(R.drawable.ic_map_pick)));

            newFavoriteMarker.showInfoWindow();

            showFavoriteDialog(newFavoriteMarker);
        });

        // 마커 클릭
        mGoogleMapView.setOnMarkerClickListener(marker -> {
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) marker.getTag();
            int tagNumber = pair.first;
            if (tagNumber >= 0) {
                if (report_card_view_pager.getVisibility() == View.GONE) {
                    showCardView();
                }
                applyFeedInfo(marker);
            } else {
                switch (tagNumber) {
                    case MARKER_MY_LOCATION:
                        marker.showInfoWindow();
                        break;
                    case MARKER_MY_LOCATION_HISTORY:
                    case MARKER_MY_LOCATION_SEARCH:
                    case MARKER_MY_LOCATION_FAVORITE:
                        marker.setIcon(getBitmapDescriptor(sizeUpMapIcon(R.drawable.ic_map_pick)));
                        marker.showInfoWindow();
                        showFavoriteDialog(marker);
                        break;
                    case MARKER_MY_LOCATION_PRE_FAVORITE:
                        marker.setIcon(getBitmapDescriptor(sizeUpMapIcon(R.drawable.ic_map_pick)));
                        marker.showInfoWindow();
                        showFavoriteDialog(marker);
                        break;
                    case MARKER_SAFEHOUSE:
                        marker.setIcon(getBitmapDescriptor(sizeUpMapIcon(R.drawable.ic_safehouse)));
                        break;
                }
                hideCardView();
            }
            return false;
        });

        // 마커 스니펫 클릭
        mGoogleMapView.setOnInfoWindowCloseListener(marker -> {
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) marker.getTag();
            int tagNumber = pair.first;
            int status = pair.second;
            if (tagNumber >= 0) {
                shrinkMarkerIcon(marker, status);
            } else {
                switch (tagNumber) {
                    case MARKER_MY_LOCATION:
                        break;
                    case MARKER_MY_LOCATION_HISTORY:
                    case MARKER_MY_LOCATION_SEARCH:
                    case MARKER_MY_LOCATION_FAVORITE:
                    case MARKER_MY_LOCATION_PRE_FAVORITE:
                        marker.setIcon(getBitmapDescriptor(R.drawable.ic_map_pick));
                        break;
                    case MARKER_SAFEHOUSE:
                        marker.setIcon(getBitmapDescriptor(R.drawable.ic_safehouse));
                        break;
                }
            }
        });

        // 검색창으로 이동
        search_bar.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onSearchBarClicked();
            }
        });

        // 즐겨찾기 창으로 이동
        favorite_button.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onFavoriteButtonClicked();
            }
        });

        // 신고하기
        report_button.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onReportButtonClicked();
            }
        });

        // 현재 위치 찾기
        my_loc_button.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onFindCurrentLocationClicked();
            }
        });
    }

    // 하단 뷰페이저 세팅
    private void setCardViewPagerAdapter() {
        if (reportCardAdapter == null) {
            reportCardAdapter = new ReportCardPagerAdapter(mImageLoader);
            reportCardAdapter.setOnReportCardClickListener(this::showReportCardListDialog);
        }
        ShadowTransformer shadowTransformer
                = new ShadowTransformer(report_card_view_pager, reportCardAdapter);
        shadowTransformer.setOnPageSelectedCallback(new ShadowTransformer.OnPageSelectedListener() {
            @Override
            public void onPageSelected(int position) {
                for (Listener listener : getListeners()) {
                    Marker marker = listener.onPageSelected(position);
                    applyFeedInfo(marker);
                }
            }

            @Override
            public void onLastPageSelected() {
                for (Listener listener : getListeners()) {
                    listener.onLastPageSelected();
                }
            }
        });
        shadowTransformer.enableScaling(true);
        report_card_view_pager.setAdapter(reportCardAdapter);
        report_card_view_pager.addOnPageChangeListener(shadowTransformer);
    }

    /**
     * show view
     */

    // 하단 뷰페이저 숨기기
    private void hideCardView() {
        if (report_card_view_pager.getVisibility() == View.VISIBLE) {
            Animation trans_to_down
                    = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_down);
            report_card_view_pager.startAnimation(trans_to_down);
            report_card_view_pager.setVisibility(View.GONE);
            report_card_view_pager.setClickable(false);
            map_option_layout.setVisibility(View.VISIBLE);
        }
    }

    // 하단 뷰페이저 보이기
    @Override
    public void showCardView() {
        Animation trans_to_up = AnimationUtils.loadAnimation(getContext(), R.anim.trans_to_up);
        report_card_view_pager.setVisibility(View.VISIBLE);
        report_card_view_pager.startAnimation(trans_to_up);
        report_card_view_pager.bringToFront();
        map_option_layout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showProgressIndication() {
        map_view_progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressIndication() {
        map_view_progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(map_container, message);
    }

    @Override
    public void showAskAgainDialog() {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.dialog_go_settings).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onReAskPermissionAllowed();
                }
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, RE_ASK_PERMISSION_DIALOG);
    }

    // 즐겨찾기 마커 클릭시 보여줄 대화상자
    private void showFavoriteDialog(Marker marker) {
        FavoriteDialog favoriteDialog = mDialogFactory.newFavoriteDialog(
                marker.getTitle(),
                marker.getSnippet(),
                marker.getPosition().latitude,
                marker.getPosition().longitude);
        favoriteDialog.registerListener((title, info) -> {
            marker.setTitle(title);
            marker.setSnippet(info);
        });
        mDialogManager.showRetainedDialogWithId(favoriteDialog, ADD_FAVORITE_FEED_DIALOG);
    }

    private void showReportCardListDialog(int reportId) {
        ReportCardListDialog reportCardListDialog
                = mDialogFactory.newMapReportCardListDialog(reportId);
        mDialogManager.showRetainedDialogWithId(reportCardListDialog, REPORT_CARD_LIST_DIALOG);
    }

    /**
     * view control
     */

    private Bitmap sizeUpMapIcon(int imageId) {
        Bitmap original = BitmapFactory.decodeResource(getContext().getResources(), imageId);
        int resizeWidth = (int) (original.getWidth() * 1.2);
        int targetHeight = (int) (original.getHeight() * 1.2);
        Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    // 사이즈업 할 아이콘 선별
    @Override
    public void enlargeMarkerIcon(Marker marker, int status) {
        Log.e("호출확인", "enlargeMarkerIcon");
        switch (status) {
            case RECEIVING:
            case ACCEPTED:
                Log.e("호출확인", "enlargeMarkerIcon");
                marker.setIcon(getBitmapDescriptor(sizeUpMapIcon(R.drawable.ic_map_marker_red)));
                break;
            case FOUND:
                marker.setIcon(getBitmapDescriptor(sizeUpMapIcon(R.drawable.ic_map_marker_white)));
                break;
            case CLEAN:
                marker.setIcon(getBitmapDescriptor(sizeUpMapIcon(R.drawable.ic_map_marker_green)));
                break;
        }
    }


    // 마커 아이콘 사이즈 복귀
    private void shrinkMarkerIcon(Marker marker, int status) {
        switch (status) {
            case RECEIVING:
            case ACCEPTED:
                marker.setIcon(getBitmapDescriptor(R.drawable.ic_map_marker_red));
                break;
            case FOUND:
                marker.setIcon(getBitmapDescriptor(R.drawable.ic_map_marker_white));
                break;
            case CLEAN:
                marker.setIcon(getBitmapDescriptor(R.drawable.ic_map_marker_green));
                break;
        }
    }


    private BitmapDescriptor getBitmapDescriptor(int id) {
        return BitmapDescriptorFactory.fromResource(id);
    }

    private BitmapDescriptor getBitmapDescriptor(Bitmap bitmap) {
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean isReportCardShown() {
        return report_card_view_pager.getVisibility() == View.VISIBLE;
    }

    @Override
    public void moveCamera(LatLng latLng, int zoom) {
        mGoogleMapView.moveCamera(latLng, zoom);
    }

    /**
     * set data
     */

    @Override
    public Marker addMarker(LatLng latLng, String title, String snippet, int icon, int tag) {
        return mGoogleMapView.addMarker(latLng, title, snippet, icon, tag);
    }

    @Override
    public void bindFeed(List<FeedEntity> feedEntities) {
        reportCardAdapter.addAll(feedEntities);
    }

    @Override
    public void clearMap() {
        mGoogleMapView.clear();
    }

    @Override
    public void clearReportCardList() {
        reportCardAdapter.removeAllCardItem();
    }

    @Override
    public void bindSearchLocation(String location) {
        loc_search_input.setText(location);
    }

    private void applyFeedInfo(Marker marker) {
        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) marker.getTag();
        int position = pair.first;
        int status = pair.second;
        mGoogleMapView.moveCamera(marker.getPosition(), ZOOM_FEED_MARKER);
        report_card_view_pager.setCurrentItem(position, true);
        marker.showInfoWindow();
        enlargeMarkerIcon(marker, status);
    }


}
