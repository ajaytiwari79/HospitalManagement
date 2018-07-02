
package com.kairos.planner.vrp.taskplanning.routes;


public class Summary {

    private Integer lengthInMeters;
    private Integer travelTimeInSeconds;
    private Integer trafficDelayInSeconds;
    private String departureTime;
    private String arrivalTime;

    public Integer getLengthInMeters() {
        return lengthInMeters;
    }

    public void setLengthInMeters(Integer lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    public Integer getTravelTimeInSeconds() {
        return travelTimeInSeconds;
    }

    public void setTravelTimeInSeconds(Integer travelTimeInSeconds) {
        this.travelTimeInSeconds = travelTimeInSeconds;
    }

    public Integer getTrafficDelayInSeconds() {
        return trafficDelayInSeconds;
    }

    public void setTrafficDelayInSeconds(Integer trafficDelayInSeconds) {
        this.trafficDelayInSeconds = trafficDelayInSeconds;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

}
