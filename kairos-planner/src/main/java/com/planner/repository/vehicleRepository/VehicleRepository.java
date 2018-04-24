package com.planner.repository.vehicleRepository;

import com.planner.domain.vehicle.PlanningVehicle;
import com.planner.repository.customRepository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VehicleRepository extends BaseRepository {


    public boolean exist(Long externalId,long unitId){
        /*Select select = QueryBuilder.select().from("planningvehicle");
        select.where(QueryBuilder.eq("externalid",externalId));
        select.where(QueryBuilder.in("unitid",unitId));
        return ((PlanningVehicle) findByField(select,PlanningVehicle.class))!=null;*/
        return false;
    }


    public PlanningVehicle findOne(String id){
        /*Select select = QueryBuilder.select().from("planningVehicle");
        select.where(QueryBuilder.eq("id",id));
        return (PlanningVehicle) findOne(select,PlanningVehicle.class);*/
        return null;
    }


    public List<PlanningVehicle> findAllByIds(List ids) {
        /*Select select = QueryBuilder.select().from("planningVehicle");
        select.where(QueryBuilder.in("id",ids));
        return (List<PlanningVehicle>)findAllByQuery(select,PlanningVehicle.class);*/
        return null;
    }
}
