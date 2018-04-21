package com.planner.repository.locationRepository;


import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.planner.domain.location.LocationDistance;
import com.planner.domain.location.PlanningLocation;
import com.planner.repository.customRepository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationRepository extends BaseRepository {

    private static Logger logger = LoggerFactory.getLogger(LocationRepository.class);

    /*
    public PlanningLocation findById(long id){
        Select select = QueryBuilder.select().from("PlanningLocation");
        select.where(QueryBuilder.eq("id",id));
        return (PlanningLocation) findByField(select,PlanningLocation.class);
    }

    public List<PlanningLocation> findAllByIds(List ids) {
        Select select = QueryBuilder.select().from("PlanningLocation");
        select.where(QueryBuilder.in("id",ids));
        return (List<PlanningLocation>)findAllByQuery(select,PlanningLocation.class);
    }
*/



    public List<PlanningLocation> getAllByUnitId(long unitId){
        Select select = QueryBuilder.select().from("PlanningLocation").allowFiltering();
        select.where(QueryBuilder.eq("unitId",unitId));
        return (List<PlanningLocation>)findAllByQuery(select,PlanningLocation.class);
    }

    public PlanningLocation getUnitAddressBy(long unitId){
        Select select = QueryBuilder.select().from("PlanningLocation").allowFiltering();
        select.where(QueryBuilder.eq("unitId",unitId)).and(QueryBuilder.eq("isUnitAddress",true));
        return (PlanningLocation) findOne(select,PlanningLocation.class);
    }

    public PlanningLocation getLocationByLatLong(double latitude,double longitude){
        Select select = QueryBuilder.select().from("PlanningLocation").allowFiltering();
        select.where(QueryBuilder.eq("latitude",latitude)).and(QueryBuilder.eq("longitude",longitude));
        return (PlanningLocation) findOne(select,PlanningLocation.class);
    }

    public List<LocationDistance> getAllLocationDistances(){
        return (List<LocationDistance>)findAll(LocationDistance.class);
    }
}
