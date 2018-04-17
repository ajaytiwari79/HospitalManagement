package com.planning.repository.taskRepository;


import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.planning.domain.task.PlanningTaskType;
import com.planning.repository.customRepository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskTypeRepository extends BaseRepository {

    public boolean exist(Long externalId,long unitId){
        Select select = QueryBuilder.select().from("planningtasktype").allowFiltering();
        select.where(QueryBuilder.eq("externalid",externalId));
        select.where(QueryBuilder.in("unitid",unitId));
        return ((PlanningTaskType) findByField(select,PlanningTaskType.class))!=null;
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
