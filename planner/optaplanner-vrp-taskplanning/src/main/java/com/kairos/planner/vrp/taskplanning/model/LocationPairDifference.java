package com.kairos.planner.vrp.taskplanning.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Dist")
public class LocationPairDifference {
    private int distance;
    private int time;
    private int trafficDelay;

    public LocationPairDifference() {
    }

    public LocationPairDifference(int distance, int time, int trafficDelay) {
        this.distance = distance;
        this.time = time;
        this.trafficDelay = trafficDelay;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTrafficDelay() {
        return trafficDelay;
    }

    public void setTrafficDelay(int trafficDelay) {
        this.trafficDelay = trafficDelay;
    }
}
