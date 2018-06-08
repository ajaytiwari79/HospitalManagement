package com.kairos.planner.vrp.taskplanning.model;

public class LocationPairDifference {
    private int distance;
    private int time;
    private int trafficDelay;

    public LocationPairDifference(int distance, int time, int trafficDelay) {
        this.distance = distance;
        this.time = time;
        this.trafficDelay = trafficDelay;
    }
}
