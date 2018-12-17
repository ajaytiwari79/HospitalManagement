package com.planner.service.locationService;

import java.text.DecimalFormat;
import java.util.*;

import com.graphhopper.directions.api.client.api.RoutingApi;
import com.graphhopper.directions.api.client.model.*;
import com.planner.appConfig.appConfig.AppConfig;
import com.planner.commonUtil.StaticField;
import com.planner.domain.location.PlanningLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.graphhopper.directions.api.client.ApiException;
import com.graphhopper.directions.api.client.api.GeocodingApi;
import com.graphhopper.directions.api.client.api.MatrixApi;
import com.kairos.planning.domain.Location;
import com.planner.domain.location.LocationDistance;

@Service
public class GraphHopperService {

 /*   private static Logger log = LoggerFactory.getLogger(GraphHopperService.class);

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private LocationService locationService;


    public MatrixResponse getMatrixData(List<PlanningLocation> locations, String vehicle) {
        MatrixApi api = new MatrixApi();
        List<String> point = getLatsLongsFromLocations(locations);
        String fromPoint = null;
        String toPoint = null;
        List<String> requiredFields = Arrays.asList("weights", "distances", "times");
        MatrixResponse response = null;
        try {
            response = api.matrixGet(appConfig.getGraphhoperkey(), point, fromPoint, toPoint, requiredFields, vehicle);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return response;
    }

    private List<LocationDistance> getMatrixOfLocationDistance(List<PlanningLocation> planningLocations) {
        MatrixResponse response = getMatrixData(planningLocations, "car");
        List<LocationDistance> locationDistances = new ArrayList<>();
        for (int i = 0; i < response.getDistances().size(); i++) {
            for (int k = 0; k < response.getDistances().get(i).size(); k++) {
                if (i == k) continue;
                LocationDistance locationDistance = new LocationDistance();
                locationDistance.setDistanceByCar(Math.ceil((response.getDistances().get(i).get(k).doubleValue()) / 1000.0d));
                locationDistance.setTimeByCar(response.getTimes().get(i).get(k).doubleValue());
                locationDistance.setFirstLocationId(planningLocations.get(i).getId());
                locationDistance.setSecondLocationId(planningLocations.get(k).getId());
                locationDistances.add(locationDistance);
            }
        }
        return locationDistances;
    }

    *//***
     * this function gives distance By car
     *//*

    public void getRoute() {
        RoutingApi routing = new RoutingApi();
        try {
            RouteResponse rsp = routing.routeGet(Arrays.asList("28.4595,77.0266", "28.5355,77.3910"), false, appConfig.getGraphhoperkey(),
                    "en", true, "car", true, true, Arrays.<String>asList(), false,
                    "fastest", null, null, null, null, null,
                    null, null, null, null, null);
            RouteResponsePath path = rsp.getPaths().get(0);
            ResponseInstruction instr = path.getInstructions().get(0);
            log.info(instr.getText());
        } catch (ApiException ex) {
            log.info(ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    private Map<String, LocationDistance> getLocationDistanceMap(List<LocationDistance> locationDistances) {
        Map<String, LocationDistance> locationDistanceMap = new HashMap<>();
        if (locationDistances.size() > 0) {
            for (LocationDistance locationDistance : locationDistances) {
                locationDistanceMap.put(locationDistance.getFirstLocationId() + "-" + locationDistance.getSecondLocationId(), locationDistance);
            }
        }
        return locationDistanceMap;
    }


    public List<LocationDistance> getLocationDistances(List<PlanningLocation> planningLocations, List<LocationDistance> locationDistances) {
        List<LocationDistance> updatedLocationDistances = new ArrayList<>();
        *//*if(locationDistances.size()==0){
            updatedLocationDistances = getInitialLocationDistance(planningLocations);
		}*//*
        Map<String, LocationDistance> locationDistancesMap = getLocationDistanceMap(locationDistances);
        for (PlanningLocation planningLocation1 : planningLocations) {
            for (PlanningLocation planningLocation2 : planningLocations) {
                if (!planningLocation1.equals(planningLocation2) && !isLocationDistanceExists(locationDistancesMap, planningLocation1.getId(), planningLocation2.getId())) {
                    LocationDistance locationDistance = getDistanceTimeFromGraphHopper(planningLocation1, planningLocation2);
                    updatedLocationDistances.add(locationDistance);
                }
            }
        }
        return updatedLocationDistances;
    }

    private LocationDistance getDistanceTimeFromGraphHopper(PlanningLocation planningLocation1, PlanningLocation planningLocation2) {
        MatrixResponse response = getdistance(planningLocation1, planningLocation2, StaticField.CAR);
        LocationDistance locationDistance = new LocationDistance();
        locationDistance.setFirstLocationId(planningLocation1.getId());
        locationDistance.setSecondLocationId(planningLocation2.getId());
        if (response != null) {
            locationDistance.setDistanceByCar(response.getDistances().get(0).get(0).doubleValue() / 1000);
            locationDistance.setTimeByCar(response.getTimes().get(0).get(0).doubleValue());
        }
        response = getdistance(planningLocation1, planningLocation2, StaticField.RACINGBIKE);
        if (response != null) {
            locationDistance.setDistanceByBike(response.getDistances().get(0).get(0).doubleValue() / 1000);
            locationDistance.setTimeByBike(response.getTimes().get(0).get(0).doubleValue());
        }
        return locationDistance;
    }

    private List<LocationDistance> getInitialLocationDistance(List<PlanningLocation> planningLocations) {
        List<LocationDistance> locationDistances = new ArrayList<>();
        int start = 0, end = 79;
        while (planningLocations.size() >= end) {
            List<LocationDistance> updatedLocationDistances = getMatrixOfLocationDistance(planningLocations.subList(start, end));
            locationDistances.addAll(updatedLocationDistances);
            start = end + 1;
            end = end + 80;
            if (planningLocations.size() >= end) {
                end = planningLocations.size();
            }
        }
        return locationDistances;
    }

    private boolean isLocationDistanceExists(Map<String, LocationDistance> locationDistancesMap, String planningLocationId1, String planningLocationId2) {
        return locationDistancesMap.containsKey(planningLocationId1 + "-" + planningLocationId2);
    }


    *//***
     * this function gives distance By car
     *//*

    public List<LocationDistance> getLocationDistancesByPlanningLocation(PlanningLocation planningLocation, List<LocationDistance> locationDistances) {
        Map<String, LocationDistance> locationDistancesMap = getLocationDistanceMap(locationDistances);
        List<PlanningLocation> planningLocations = locationService.getAllPlanningLocations();
        List<LocationDistance> updatedLocationDistances = new ArrayList<>();
        for (PlanningLocation planningLocation2 : planningLocations) {
            if (!planningLocation.equals(planningLocation2) && !isLocationDistanceExists(locationDistancesMap, planningLocation.getId(), planningLocation2.getId())) {
                MatrixResponse response = getdistance(planningLocation, planningLocation2, StaticField.CAR);
                LocationDistance locationDistance = new LocationDistance();
                locationDistance.setFirstLocationId(planningLocation.getId());
                locationDistance.setSecondLocationId(planningLocation2.getId());
                if (response != null) {
                    locationDistance.setDistanceByCar(response.getDistances().get(0).get(0).doubleValue() / 1000);
                    locationDistance.setTimeByCar(response.getTimes().get(0).get(0).doubleValue());
                }
                response = getdistance(planningLocation, planningLocation2, StaticField.RACINGBIKE);
                if (response != null) {
                    locationDistance.setDistanceByBike(response.getDistances().get(0).get(0).doubleValue() / 1000);
                    locationDistance.setTimeByBike(response.getTimes().get(0).get(0).doubleValue());
                }
                updatedLocationDistances.add(locationDistance);
            }
        }
        return updatedLocationDistances;
    }


    public MatrixResponse getdistance(PlanningLocation planningLocation1, PlanningLocation planningLocation2, String vehicle) {
        MatrixApi api = new MatrixApi();
        List<String> point = null;
        String fromPoint = new DecimalFormat("##.####").format(planningLocation1.getLatitude()) + "," + new DecimalFormat("##.####").format(planningLocation1.getLongitude());
        String toPoint = planningLocation2.getLatitude() + "," + planningLocation2.getLongitude();
        List<String> requiredFields = Arrays.asList("weights", "distances", "times");
        MatrixResponse response = null;
        try {
            response = api.matrixGet(appConfig.getKeyAfterExpires(), point, fromPoint, toPoint, requiredFields, vehicle);
        } catch (ApiException e) {
            appConfig.setGraphhoperKeyExpireCount(appConfig.graphhoperKeyExpireCount+1);
            e.printStackTrace();
            return getdistance(planningLocation1,planningLocation2,vehicle);
        }
        return response;
    }

    private List<String> getPoints(Location location1, Location location2) {
        List<String> points = new ArrayList<>();
        points.add(location1.getLatitude() + "," + location1.getLongitude());
        points.add(location2.getLatitude() + "," + location2.getLongitude());
        return points;
    }


    public OptaLocationDTO getLatLongByAddress(OptaLocationDTO optaLocationDTO) {
        GeocodingApi geoApi = new GeocodingApi();
        String address = optaLocationDTO.getHouseNumber() + "," + optaLocationDTO.getStreet() + "," + optaLocationDTO.getCity() + "," + optaLocationDTO.getCountry();//"DK,Odense,Østergade,45 D";
        //address = "spaze i-tech park sector 49";//"i-Tech Park, 122018, Gurugram, India";//optaLocationDTO.getAddress();
        boolean isAddressVerfied = false;
        GeocodingResponse response;
        try {
            log.info(appConfig.getGraphhoperkey());
            response = geoApi.geocodeGet(appConfig.getGraphhoperkey(), address, "en", 1, false, "", "default");
            if (response != null && response.getHits().size() > 0) {
                log.info("latitude " + response.getHits().get(0).getPoint().getLat() + " longitude "
                        + response.getHits().get(0).getPoint().getLng());
                optaLocationDTO.setLatitude(response.getHits().get(0).getPoint().getLat());
                optaLocationDTO.setLongitude(response.getHits().get(0).getPoint().getLng());
                isAddressVerfied = true;
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        optaLocationDTO.setAddressVerified(isAddressVerfied);
        return optaLocationDTO;
    }

    public List<String> getLatsLongsFromLocations(List<PlanningLocation> locations) {
        List<String> allPoints = new ArrayList<>();
        for (PlanningLocation planningLocation : locations) {
            allPoints.add(planningLocation.getLongitude() + "," + planningLocation.getLatitude());
        }
        return allPoints;
    }
*/
}
