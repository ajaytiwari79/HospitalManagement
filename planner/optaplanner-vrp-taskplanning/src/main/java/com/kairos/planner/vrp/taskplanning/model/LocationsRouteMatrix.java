package com.kairos.planner.vrp.taskplanning.model;

import java.util.HashMap;
import java.util.Map;

public class LocationsRouteMatrix {
    private Map<LocationPair,Boolean> table;

    public Map<LocationPair, Boolean> getTable() {
        return table;
    }

    public void setTable(Map<LocationPair, Boolean> table) {
        this.table = table;
    }

    public LocationsRouteMatrix(Map<LocationPair, Boolean> table) {
        this.table = table;
    }
    public boolean checkIfRightSideArrival(LocationPair locationPair){
        return table.get(locationPair);
    }
}
