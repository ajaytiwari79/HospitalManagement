
package com.kairos.planner.vrp.taskplanning.routes;


import java.util.List;

public class Instruction {

    private Integer routeOffsetInMeters;
    private Integer travelTimeInSeconds;
    private Point point;
    private String instructionType;
    private String street;
    private String countryCode;
    private List<String> roadNumbers;
    private Boolean possibleCombineWithNext;
    private String drivingSide;
    private String maneuver;
    private String junctionType;
    private Integer turnAngleInDecimalDegrees;

    public Integer getRouteOffsetInMeters() {
        return routeOffsetInMeters;
    }

    public void setRouteOffsetInMeters(Integer routeOffsetInMeters) {
        this.routeOffsetInMeters = routeOffsetInMeters;
    }

    public Integer getTravelTimeInSeconds() {
        return travelTimeInSeconds;
    }

    public void setTravelTimeInSeconds(Integer travelTimeInSeconds) {
        this.travelTimeInSeconds = travelTimeInSeconds;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getInstructionType() {
        return instructionType;
    }

    public void setInstructionType(String instructionType) {
        this.instructionType = instructionType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Boolean getPossibleCombineWithNext() {
        return possibleCombineWithNext;
    }

    public void setPossibleCombineWithNext(Boolean possibleCombineWithNext) {
        this.possibleCombineWithNext = possibleCombineWithNext;
    }

    public String getDrivingSide() {
        return drivingSide;
    }

    public void setDrivingSide(String drivingSide) {
        this.drivingSide = drivingSide;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    public String getJunctionType() {
        return junctionType;
    }

    public void setJunctionType(String junctionType) {
        this.junctionType = junctionType;
    }

    public Integer getTurnAngleInDecimalDegrees() {
        return turnAngleInDecimalDegrees;
    }

    public void setTurnAngleInDecimalDegrees(Integer turnAngleInDecimalDegrees) {
        this.turnAngleInDecimalDegrees = turnAngleInDecimalDegrees;
    }

    public List<String> getRoadNumbers() {
        return roadNumbers;
    }

    public void setRoadNumbers(List<String> roadNumbers) {
        this.roadNumbers = roadNumbers;
    }
}
