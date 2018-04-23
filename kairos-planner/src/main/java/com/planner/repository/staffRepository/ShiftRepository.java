package com.planner.repository.staffRepository;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.planner.domain.staff.PlanningShift;
import com.planner.repository.customRepository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ShiftRepository extends BaseRepository{

    private static Logger logger = LoggerFactory.getLogger(ShiftRepository.class);

   /* public PlanningShift findById(long id){
        Select select = QueryBuilder.select().from("PlanningShift");
        select.where(QueryBuilder.eq("id",id));
        return (PlanningShift) findByField(select,PlanningTaskType.class);
    }

    public List<PlanningShift> findAllByIds(List ids) {
        Select select = QueryBuilder.select().from("PlanningShift");
        select.where(QueryBuilder.in("id",ids));
        return (List<PlanningShift>)findAllByQuery(select,PlanningShift.class);
    }*/

    public boolean exist(Long externalId,long unitId){
        Select select = QueryBuilder.select().from("planningshift");
        select.where(QueryBuilder.eq("externalid",externalId));
        select.where(QueryBuilder.in("unitid",unitId));
        return ((PlanningShift) findByField(select,PlanningShift.class))!=null;
    }


   public List<PlanningShift> getAllByUnitId(Date startDate,Date endDate,long unitId){
       Select select = QueryBuilder.select().from("PlanningShift");
       select.where(QueryBuilder.eq("unitid",unitId)).and(QueryBuilder.eq("unitId",unitId));
       return (List<PlanningShift>)findAllByQuery(select,PlanningShift.class);
   }
}
