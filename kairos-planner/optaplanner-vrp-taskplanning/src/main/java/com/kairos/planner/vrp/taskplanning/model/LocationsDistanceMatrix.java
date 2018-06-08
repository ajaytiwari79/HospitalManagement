package com.kairos.planner.vrp.taskplanning.model;

import java.util.HashMap;
import java.util.Map;

public class LocationsDistanceMatrix {
    private Map<LocationPair,LocationPairDifference> table;

    public LocationsDistanceMatrix() {
        this.table = new HashMap<>();
    }
    public void addLocationDistance(LocationPair locationPair,LocationPairDifference locationPairDifference){
        table.put(locationPair, locationPairDifference);
    }
    public LocationPairDifference getLocationsDifference(LocationPair locationPair){
        return table.get(locationPair);
    }

    public LocationPairDifference getLocationsDifference(double fromLatitute, double fromLongitude, double toLatitute, double toLongitude){
        return table.get(new LocationPair(fromLatitute, fromLongitude, toLatitute, toLongitude));
    }

}
