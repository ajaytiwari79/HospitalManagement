package com.planner.domain.tomtomResponse;

import java.util.Date;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class RouteSummary {
    private int lengthInMeters;
    private int travelTimeInSeconds;
    private int trafficDelayInSeconds;
    private Date departureTime;
    private Date arrivalTime;

    public int getLengthInMeters() {
        return lengthInMeters;
    }

    public void setLengthInMeters(int lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    public int getTravelTimeInSeconds() {
        return travelTimeInSeconds;
    }

    public void setTravelTimeInSeconds(int travelTimeInSeconds) {
        this.travelTimeInSeconds = travelTimeInSeconds;
    }

    public int getTrafficDelayInSeconds() {
        return trafficDelayInSeconds;
    }

    public void setTrafficDelayInSeconds(int trafficDelayInSeconds) {
        this.trafficDelayInSeconds = trafficDelayInSeconds;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
