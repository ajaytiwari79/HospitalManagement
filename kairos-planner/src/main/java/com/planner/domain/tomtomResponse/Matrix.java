package com.planner.domain.tomtomResponse;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class Matrix {
    private Double firstLatitude;
    private Double firstLongitude;
    private Double secondLattitude;
    private Double secondLongitude;
    private int statusCode;
    private Response response;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Double getFirstLatitude() {
        return firstLatitude;
    }

    public void setFirstLatitude(Double firstLatitude) {
        this.firstLatitude = firstLatitude;
    }

    public Double getFirstLongitude() {
        return firstLongitude;
    }

    public void setFirstLongitude(Double firstLongitude) {
        this.firstLongitude = firstLongitude;
    }

    public Double getSecondLattitude() {
        return secondLattitude;
    }

    public void setSecondLattitude(Double secondLattitude) {
        this.secondLattitude = secondLattitude;
    }

    public Double getSecondLongitude() {
        return secondLongitude;
    }

    public void setSecondLongitude(Double secondLongitude) {
        this.secondLongitude = secondLongitude;
    }
}
