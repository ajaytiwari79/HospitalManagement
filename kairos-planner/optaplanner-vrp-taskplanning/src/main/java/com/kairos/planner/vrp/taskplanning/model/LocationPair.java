package com.kairos.planner.vrp.taskplanning.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Locs")
public class LocationPair {
    @XStreamAlias("lat1")
    private double fromLatitute;
    @XStreamAlias("lon1")
    private double fromLongitude;
    @XStreamAlias("lat2")
    private double toLatitute;
    @XStreamAlias("lon2")
    private double toLongitude;

    public LocationPair() {
    }

    public LocationPair(double fromLatitute, double fromLongitude, double toLatitute, double toLongitude) {
        this.fromLatitute = fromLatitute;
        this.fromLongitude = fromLongitude;
        this.toLatitute = toLatitute;
        this.toLongitude = toLongitude;
    }
    public double getFromLatitute() {
        return fromLatitute;
    }

    public void setFromLatitute(double fromLatitute) {
        this.fromLatitute = fromLatitute;
    }

    public double getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public double getToLatitute() {
        return toLatitute;
    }

    public void setToLatitute(double toLatitute) {
        this.toLatitute = toLatitute;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }
}
