package com.planner.service.locationService;

import com.kairos.dto.user.organization.union.LocationDTO;
import com.kairos.planner.vrp.taskplanning.model.LocationInfo;
import com.planner.domain.location.LocationDistance;
import com.planner.domain.location.PlanningLocation;
import com.planner.repository.locationRepository.LocationRepository;
import com.planner.repository.locationRepository.PlanningLocationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private static Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private GraphHopperService graphHopperService;
    @Autowired private PlanningLocationRepository planningLocationRepository;


    public void saveList(List<PlanningLocation> planningLocations) {
        planningLocationRepository.saveAll(planningLocations);
    }

    public void saveEntity(PlanningLocation planningLocation) {
        planningLocationRepository.saveEntity(planningLocation);
    }

    public PlanningLocation findOne(String id) {
        return (PlanningLocation) planningLocationRepository.findById(id).get();
    }

    public List<PlanningLocation> findByIds(List<String> ids) {
        return (List<PlanningLocation>) planningLocationRepository.findAllById(ids);
    }

    public PlanningLocation getLocationByLatLong(double latitude, double logitude) {
        return planningLocationRepository.getLocationByLatLong(latitude, logitude);
    }

    public PlanningLocation saveLocation(LocationDTO locationDTO) {
        PlanningLocation planningLocation = new PlanningLocation();
        planningLocation.setCity(locationDTO.getAddress().getCity());
        planningLocation.setCountry(locationDTO.getAddress().getCountry());
        //planningLocation.setDistrict(locationDTO.getAddress().getDistrict());
        planningLocation.setHouseNumber(locationDTO.getAddress().getHouseNumber());
        planningLocation.setLatitude(locationDTO.getAddress().getLatitude());
        planningLocation.setLongitude(locationDTO.getAddress().getLongitude());
        planningLocation.setStreet(locationDTO.getAddress().getStreet());
        /*if(locationDTO.getAddress().getZip()!=null){
            planningLocation.setZip(locationDTO.getAddress().getZip());
        }*/
        planningLocation = (PlanningLocation) planningLocationRepository.saveEntity(planningLocation);
        return planningLocation;
    }

    public void saveLocations(List<LocationDTO> locationDTOS){
        List<PlanningLocation> planningLocations = new ArrayList<>();
        for (LocationDTO locationDTO:locationDTOS) {
            PlanningLocation planningLocation = new PlanningLocation();
            planningLocation.setCity(locationDTO.getAddress().getCity());
            planningLocation.setCountry(locationDTO.getAddress().getCountry());
           // planningLocation.setDistrict(locationDTO.getAddress().getDistrict());
            planningLocation.setHouseNumber(locationDTO.getAddress().getHouseNumber());
            planningLocation.setLatitude(locationDTO.getAddress().getLatitude());
            planningLocation.setLongitude(locationDTO.getAddress().getLongitude());
            planningLocation.setStreet(locationDTO.getAddress().getStreet());
            //planningLocation.setZip(locationDTO.getAddress().getZip());
            planningLocations.add(planningLocation);
        }
        saveList(planningLocations);
    }

   /* public boolean saveLocationDistances() {
        List<PlanningLocation> planningLocations = planningLocationRepository.findAll();
        List<LocationDistance> locationDistances = getAllLocationDistances();
        List<LocationDistance> updatedLocationDistances = graphHopperService.getLocationDistances(planningLocations, locationDistances);
        locationRepository.saveAll(updatedLocationDistances);
        return true;
    }


    public boolean saveLocationDistance(PlanningLocation planningLocation) {
        List<LocationDistance> locationDistances = getAllLocationDistances();
        List<LocationDistance> updatedLocationDistances = graphHopperService.getLocationDistancesByPlanningLocation(planningLocation, locationDistances);
        locationRepository.saveAll(updatedLocationDistances);
        return true;
    }*/


    public List<LocationDistance> getAllLocationDistances() {
        return locationRepository.findAll();
    }

    public void deleteList(List<PlanningLocation> planningLocations) {
        planningLocationRepository.deleteAll(planningLocations);
    }

    public List<PlanningLocation> getAllPlanningLocations() {
        return planningLocationRepository.findAll();
    }

    /*public List<PlanningLocation> getAllByUnitIdWithUnitLocation(long unitId) {
        List<PlanningLocation> planningLocations = new ArrayList<>();
        planningLocations.add(getUnitAddressBy(unitId));
        planningLocations.addAll(locationRepository.getAllByUnitId(unitId));
        return planningLocations;
    }*/

    /*public PlanningLocation getUnitAddressBy(long unitId) {
        return locationRepository.getUnitAddressBy(unitId);
    }*/

    public void update(PlanningLocation planningLocation){
        PlanningLocation planningLocation1 = findOne(planningLocation.getId().toString());
        planningLocation1.setCity(planningLocation.getCity());
        planningLocation1.setCountry(planningLocation.getCountry());
        planningLocation1.setDistrict(planningLocation.getDistrict());
        planningLocation1.setHouseNumber(planningLocation.getHouseNumber());
        planningLocation1.setLatitude(planningLocation.getLatitude());
        planningLocation1.setLongitude(planningLocation.getLongitude());
        planningLocation1.setStreet(planningLocation.getStreet());
        planningLocation1.setZip(planningLocation.getZip());
        planningLocationRepository.saveEntity(planningLocation1);
    }

    public void updateList(List<PlanningLocation> planningLocations){
        List<PlanningLocation> updatedPlanningLocation = new ArrayList<>();
        for (PlanningLocation planningLocation:planningLocations) {
            PlanningLocation planningLocation1 = findOne(planningLocation.getId().toString());
            planningLocation1.setCity(planningLocation.getCity());
            planningLocation1.setCountry(planningLocation.getCountry());
            planningLocation1.setDistrict(planningLocation.getDistrict());
            planningLocation1.setHouseNumber(planningLocation.getHouseNumber());
            planningLocation1.setLatitude(planningLocation.getLatitude());
            planningLocation1.setLongitude(planningLocation.getLongitude());
            planningLocation1.setStreet(planningLocation.getStreet());
            planningLocation1.setZip(planningLocation.getZip());
            updatedPlanningLocation.add(planningLocation1);
        }
        saveList(updatedPlanningLocation);
    }

    public void saveLocation(List<LocationDistance> locationDistances){
        locationRepository.saveAll(locationDistances);
    }

    public Map<Integer,Map<Integer,LocationInfo>> getLocationMap(){
        List<LocationDistance> locationDistances = locationRepository.findAll();
        Map<Long,List<LocationDistance>> locationMap = locationDistances.stream().collect(Collectors.groupingBy(l->l.getFirstInstallationNo(),Collectors.toList()));
        Map<Integer,Map<Integer,LocationInfo>> locationInfoMap = new HashMap<>();
        /*
        locationMap.entrySet().forEach(l->{
            locationInfoMap.put(l.getKey(),l.getValue().stream().collect(Collectors.toMap(ld->ld.getSecondInstallationNo(),ld->new LocationInfo(ld.getSecondInstallationNo(),ld.getDistanceByCar().intValue(),(int)ld.getTimeByCar().intValue()))));
        });*/
        return locationInfoMap;
    }

}
