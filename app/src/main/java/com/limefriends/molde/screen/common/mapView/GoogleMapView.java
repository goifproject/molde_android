package com.limefriends.molde.screen.common.mapView;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.view.BaseView;

public class GoogleMapView extends BaseView
        implements
                OnMapReadyCallback,
                GoogleMap.OnMapClickListener,
                GoogleMap.OnMapLongClickListener,
                GoogleMap.OnMarkerClickListener,
                GoogleMap.OnInfoWindowCloseListener {

    public static final int ZOOM_CUR_LOCATION = 17;
    public static final int ZOOM_FEED_MARKER = 15;

    public interface OnMapClickListener {
        void onMapClicked(LatLng latLng);
    }

    public interface OnMapLongClickListener {
        void onMapLongClicked(LatLng latLng);
    }

    public interface OnMarkerClickListener {
        boolean onMarkerClicked(Marker marker);
    }

    public interface OnInfoWindowCloseListener {
        void onInfoWindowClosed(Marker marker);
    }

    private OnMapClickListener mOnMapClickListener;
    private OnMapLongClickListener mOnMapLongClickListener;
    private OnMarkerClickListener mOnMarkerClickListener;
    private OnInfoWindowCloseListener mOnInfoWindowCloseListener;

    private GoogleMap mGoogleMap;


    public GoogleMapView(FragmentManager fragmentManager, int container) {

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentByTag("mapFragment");

//        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(container, mapFragment, "mapFragment");
            ft.commit();
//        }
        mapFragment.getMapAsync(this);
    }


    public GoogleMapView(SupportMapFragment mapFragment) {
        mapFragment.getMapAsync(this);
    }

    public void setRootViewNull() {
        setRootView(null);
    }

    private void setMapUI() {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        mGoogleMap.setOnMapClickListener(this);

        mGoogleMap.setOnMapLongClickListener(this);

        mGoogleMap.setOnMarkerClickListener(this);

        mGoogleMap.setOnInfoWindowCloseListener(this);

        setMapUI();
    }

    public void setOnMapClickListener(OnMapClickListener mOnMapClickListener) {
        this.mOnMapClickListener = mOnMapClickListener;
    }

    public void setOnMapLongClickListener(OnMapLongClickListener mOnMapLongClickListener) {
        this.mOnMapLongClickListener = mOnMapLongClickListener;
    }

    public void setOnMarkerClickListener(OnMarkerClickListener mOnMarkerClickListener) {
        this.mOnMarkerClickListener = mOnMarkerClickListener;
    }

    public void setOnInfoWindowCloseListener(OnInfoWindowCloseListener mOnInfoWindowCloseListener) {
        this.mOnInfoWindowCloseListener = mOnInfoWindowCloseListener;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mOnMapClickListener.onMapClicked(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mOnMapLongClickListener.onMapLongClicked(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return mOnMarkerClickListener.onMarkerClicked(marker);
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        mOnInfoWindowCloseListener.onInfoWindowClosed(marker);
    }

    public void clear() {
        mGoogleMap.clear();
    }

    // 마커 추가
    public Marker addMarker(LatLng latLng, String title, int icon, int tag) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);

        if (!title.equals("")) options.title(title);
        if (icon != 0) options.icon(BitmapDescriptorFactory.fromResource(icon));

        Marker marker = mGoogleMap.addMarker(options);
        marker.setTag(tag);
        return marker;
    }

    public Marker addMarker(LatLng latLng, String title, String snippet, int icon, int tag) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);

        if (!title.equals("")) options.title(title);
        if (!snippet.equals("")) options.snippet(snippet);
        if (icon != 0) options.icon(BitmapDescriptorFactory.fromResource(icon));

        Marker marker = mGoogleMap.addMarker(options);
        Pair<Integer, Integer> pair = new Pair<>(tag, -1);
        marker.setTag(pair);
        return marker;
    }

    public void moveCamera(LatLng location, int zoom) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
    }

}
