package com.planner.repository.taskPlanningRepository;


//import com.datastax.driver.core.querybuilder.QueryBuilder;
//import com.datastax.driver.core.querybuilder.Select;
import com.planner.domain.citizen.PlanningCitizen;
import com.planner.repository.customRepository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TaskPlanningRepository extends BaseRepository {

    public PlanningCitizen findById(long id){
        //Select select = QueryBuilder.select().from("PlanningCitizen");
        //select.where(QueryBuilder.eq("id",id));
        return null;//(PlanningCitizen) findByField(select,PlanningCitizen.class);
    }
}
