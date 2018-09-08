package com.kairos.user.country.localAreaTag;

/**
 * @author pradeep
 * @date - 11/6/18
 */

public class LatLngDTO {

    private float lat;
    private float lng;
    private int coordOrder;

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

    public int getCoordOrder() {
        return coordOrder;
    }

    public void setCoordOrder(int coordOrder) {
        this.coordOrder = coordOrder;
    }
}
