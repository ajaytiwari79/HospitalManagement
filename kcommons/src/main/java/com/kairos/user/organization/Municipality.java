package com.kairos.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by oodles on 22/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Municipality {

    Long id;
    private String name;
    private String geoFence;
    private String code;

    private float latitude;
    private float longitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private boolean isEnable = true;


    public String getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(String geoFence) {
        this.geoFence = geoFence;
    }


    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
