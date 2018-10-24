package com.planner.domain.tomtomResponse;

import com.kairos.planner.vrp.taskplanning.routes.Route;
import com.planner.domain.common.MongoBaseEntity;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class AtoBRoute extends MongoBaseEntity{

    private Double firstLatitude;
    private Double firstLongitude;
    private Double secondLattitude;
    private Double secondLongitude;
    private Route route;

    public AtoBRoute(Double firstLatitude, Double firstLongitude, Double secondLattitude, Double secondLongitude, Route route) {
        this.firstLatitude = firstLatitude;
        this.firstLongitude = firstLongitude;
        this.secondLattitude = secondLattitude;
        this.secondLongitude = secondLongitude;
        this.route = route;
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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
