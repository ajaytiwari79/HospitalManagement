package com.planner.repository.taskRepository;


import com.planner.domain.task.PlanningTask;
import com.planner.repository.customRepository.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class PlanningTaskRepository extends BaseRepository {

    private static Logger logger = LoggerFactory.getLogger(PlanningTaskRepository.class);

    /*public PlanningTask findById(long id){
        Select select = QueryBuilder.select().from("PlanningTask");
        select.where(QueryBuilder.eq("id",id));
        return (PlanningTask) findByField(select,PlanningTask.class);
    }

    public List<PlanningTask> findAllByIds(List ids) {
        Select select = QueryBuilder.select().from("PlanningTask");
        select.where(QueryBuilder.in("id",ids));
        return (List<PlanningTask>)findAllByQuery(select,PlanningTask.class);
    }*/

    public boolean exist(String id,long unitId){
       /* Select select = QueryBuilder.select().from("planningtask");
        select.where(QueryBuilder.eq("id",id));
        select.where(QueryBuilder.in("unitId",unitId));
        return ((PlanningTask) findByField(select,PlanningTask.class))!=null;*/
        return false;
    }

    public List<PlanningTask> getAllTasksForPLanning(long unitId, Date startDate, Date endDate){
       /* Select select = QueryBuilder.select().from("planningtask");
        select.where(QueryBuilder.gte("firstStartDateTime",startDate)).and(QueryBuilder.lte("firstEndDateTime",endDate));
        return (List<PlanningTask>)findAllByQuery(select,PlanningTask.class);*/
        return null;
    }

}
