package com.kairos.service.counter;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class ShiftFilterCriteria {
    private Criteria matchCriteria;

    private ShiftFilterCriteria(){
        matchCriteria = Criteria.where("deleted").is(false);
    }

    public static ShiftFilterCriteria getInstance(){
        return new ShiftFilterCriteria();
    }

    private void setTimeInterval(List timeInterval){
        matchCriteria.and("startDate").gt(timeInterval.get(0)).and("startDate").lt(timeInterval.get(1));
    }

    private void setActivityIds(List activityIds){
        if(activityIds !=null && !activityIds.isEmpty())
            matchCriteria.and("activityId").in(activityIds);
    }

    private void setUnitIds(List unitIds){
        if(unitIds !=null && !unitIds.isEmpty())
            matchCriteria.and("unitPositionId").in(unitIds);
    }

    private void setPlanningPeriodIds(List planningPeriodIds){
        if(planningPeriodIds != null && !planningPeriodIds.isEmpty())
            matchCriteria.and("planningPeriodId").is(planningPeriodIds);
    }

    public Criteria getMatchCriteria(List<FilterCriteria> criterias){
        criterias.forEach(criteria -> {
            switch(criteria.getType()){
                case ACTIVITY_IDS: setActivityIds(criteria.getValues()); break;
                case TIME_INTERVAL: setTimeInterval(criteria.getValues()); break;
                case UNIT_IDS: setUnitIds(criteria.getValues()); break;
                // add planning period criteria
            }
        });
        return matchCriteria;
    }
}
