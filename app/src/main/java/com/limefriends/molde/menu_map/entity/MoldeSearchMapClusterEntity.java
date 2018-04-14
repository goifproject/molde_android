package com.limefriends.molde.menu_map.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

public class MoldeSearchMapClusterEntity implements ClusterItem{
    Marker infoMarker;
    LatLng latLng;

    public MoldeSearchMapClusterEntity(Marker infoMarker, LatLng latLng) {
        this.infoMarker = infoMarker;
        this.latLng = latLng;
    }

    public Marker getInfoMarker() {
        return infoMarker;
    }

    public void setInfoMarker(Marker infoMarker) {
        this.infoMarker = infoMarker;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }
}
