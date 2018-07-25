package com.planner.repository.taskRepository;


import com.planner.domain.task.PlanningTaskType;
import com.planner.repository.customRepository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TaskTypeRepository extends BaseRepository {

    public boolean exist(Long externalId,long unitId){
        /*Select select = QueryBuilder.select().from("planningtasktype").allowFiltering();
        select.where(QueryBuilder.eq("externalid",externalId));
        select.where(QueryBuilder.in("unitid",unitId));
        return ((PlanningTaskType) findByField(select,PlanningTaskType.class))!=null;*/
        return false;
    }

    /*public List<PlanningTaskType> findAllByIds(List ids) {
        Select select = QueryBuilder.select().from("PlanningTaskType");
        select.where(QueryBuilder.in("id",ids));
        return (List<PlanningTaskType>)findAllByQuery(select,PlanningTaskType.class);
    }*/




/*
    public List<PlanningTaskType> getAllByUnitId(long unitId){
        Select select = QueryBuilder.select().from("planningtasktype");
        select.where(QueryBuilder.in("unitid",unitId));
        return (List<PlanningTaskType>)findAllByQuery(select,PlanningTaskType.class);
    }
*/

}
