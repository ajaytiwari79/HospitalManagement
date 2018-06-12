package com.kairos.planner.vrp.taskplanning.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationPair that = (LocationPair) o;
        return Double.compare(that.fromLatitute, fromLatitute) == 0 &&
                Double.compare(that.fromLongitude, fromLongitude) == 0 &&
                Double.compare(that.toLatitute, toLatitute) == 0 &&
                Double.compare(that.toLongitude, toLongitude) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(fromLatitute, fromLongitude, toLatitute, toLongitude);
    }
}
