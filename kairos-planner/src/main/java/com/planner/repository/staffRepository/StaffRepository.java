package com.planner.repository.staffRepository;


import com.planner.domain.staff.PlanningStaff;
import com.planner.domain.staff.UnitStaffRelationShip;
import com.planner.repository.customRepository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StaffRepository extends BaseRepository {

    private static Logger logger = LoggerFactory.getLogger(StaffRepository.class);

   /* public PlanningStaff findById(long id){
        Select select = QueryBuilder.select().from("PlanningStaff");
        select.where(QueryBuilder.eq("id",id));
        return (PlanningStaff) findByField(select,PlanningStaff.class);
    }

    public List<PlanningStaff> findAllByIds(List ids) {
        Select select = QueryBuilder.select().from("PlanningStaff");
        select.where(QueryBuilder.in("id",ids));
        return (List<PlanningStaff>)findAllByQuery(select,PlanningStaff.class);
    }*/



    public UnitStaffRelationShip getOneUnitStaffRelationship(long unitId,String staffId){
        /*Select select = QueryBuilder.select().from(UnitStaffRelationShip.class.getSimpleName()).allowFiltering();
        select.where(QueryBuilder.eq("unitid", unitId));
        select.where(QueryBuilder.eq("staffId", staffId));
        return (UnitStaffRelationShip)findOne(select,UnitStaffRelationShip.class);*/
        return null;
    }



    public PlanningStaff getOneStaffByExternalId(long externalId){
        /*Select select = QueryBuilder.select().from("planningstaff").allowFiltering();
        select.where(QueryBuilder.eq("externalid",externalId));
        return (PlanningStaff) findByField(select,PlanningStaff.class);*/
        return null;
    }

   public List<PlanningStaff> getAllByIds(List<String> staffIds){
       /*Select select = QueryBuilder.select().from("PlanningStaff").allowFiltering();
       select.where(QueryBuilder.in("id",staffIds));
       return (List<PlanningStaff>)findAllByQuery(select,PlanningStaff.class);*/
       return null;
   }
}
