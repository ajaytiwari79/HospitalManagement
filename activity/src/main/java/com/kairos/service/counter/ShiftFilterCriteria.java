package com.kairos.service.counter;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class ShiftFilterCriteria {
    private Criteria matchCriteria;
    private List<AggregationOperation> operations;

    private ShiftFilterCriteria(){
        operations = new ArrayList<>();
        matchCriteria = Criteria.where("deleted").is(false);
        operations.add(Aggregation.match(matchCriteria));
    }

    public static ShiftFilterCriteria getInstance(){
        return new ShiftFilterCriteria();
    }

    public ShiftFilterCriteria setTimeInterval(List timeInterval){
        matchCriteria = matchCriteria.and("startDate").gt(timeInterval.get(0)).and("startDate").lt(timeInterval.get(1));
        return this;
    }

    public ShiftFilterCriteria setActivityIds(List activityIds){
        if(activityIds !=null && !activityIds.isEmpty())
            matchCriteria = matchCriteria.and("activityId").in(activityIds);
        return this;
    }

    public ShiftFilterCriteria setUnitId(List unitIds) {
        if(unitIds != null && !unitIds.isEmpty())
            matchCriteria = matchCriteria.and("unitId").in(unitIds);
        return this;
    }

    public ShiftFilterCriteria setStaffIds(List staffIds) {
        if(staffIds != null && !staffIds.isEmpty())
            matchCriteria = matchCriteria.and("staffId").in(staffIds);
        return this;
    }

    public List<AggregationOperation> getMatchOperations(){
        return operations;
    }
}
