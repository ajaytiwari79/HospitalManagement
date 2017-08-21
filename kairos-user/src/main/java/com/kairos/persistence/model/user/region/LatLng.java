package com.kairos.persistence.model.user.region;

import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by neuron on 12/6/17.
 */
@NodeEntity
public class LatLng extends UserBaseEntity{

    private float lat;
    private float lng;

    public LatLng(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng(){

    }


    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
