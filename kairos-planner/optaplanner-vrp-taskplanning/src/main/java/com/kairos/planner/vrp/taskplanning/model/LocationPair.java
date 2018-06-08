package com.kairos.planner.vrp.taskplanning.model;

public class LocationPair {

    private double fromLatitute;
    private double fromLongitude;
    private double toLatitute;
    private double toLongitude;

    public LocationPair(double fromLatitute, double fromLongitude, double toLatitute, double toLongitude) {
        this.fromLatitute = fromLatitute;
        this.fromLongitude = fromLongitude;
        this.toLatitute = toLatitute;
        this.toLongitude = toLongitude;
    }
}
