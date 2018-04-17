package com.planning.repository.citizenRepository;


import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.planning.domain.citizen.PlanningCitizen;
import com.planning.repository.customRepository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CitizenRepository extends BaseRepository{

    /*public PlanningCitizen findById(long id){
        Select select = QueryBuilder.select().from("PlanningCitizen");
        select.where(QueryBuilder.eq("id",id));
        return (PlanningCitizen) findByField(select,PlanningCitizen.class);
    }*/

   /* public List<PlanningCitizen> findAllByIds(List ids) {
        Select select = QueryBuilder.select().from("PlanningCitizen");
        select.where(QueryBuilder.in("id",ids));
        return (List<PlanningCitizen>)findAllByQuery(select,PlanningCitizen.class);
    }
*/
   public boolean exist(Long externalId,long unitId){
       Select select = QueryBuilder.select().from("planningcitizen");
       select.where(QueryBuilder.eq("externalid",externalId));
       select.where(QueryBuilder.in("unitid",unitId));
       return ((PlanningCitizen) findByField(select,PlanningCitizen.class))!=null;
   }


   public List<PlanningCitizen> getAllByUnitId(long unitId){
       Select select = QueryBuilder.select().from("Planningcitizen");
       select.where(QueryBuilder.eq("unitid",unitId));
       return (List<PlanningCitizen>)findAllByQuery(select,PlanningCitizen.class);
   }
}
