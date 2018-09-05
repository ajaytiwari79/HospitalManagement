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

    }

    private ShiftFilterCriteria(boolean lookupActivity){
        operations = new ArrayList<>();
        matchCriteria = Criteria.where("deleted").is(false);
//        if(lookupActivity){
//            operations.add(Aggregation.lookup("activities", "activityId", "_id", "activity"));
//            operations.add(Aggregation.project().and("activity").arrayElementAt(0).as("activity"));
//        }
        operations.add(Aggregation.match(matchCriteria));
    }

    public static ShiftFilterCriteria getInstance(boolean useActivity){
        return new ShiftFilterCriteria(useActivity);
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

//    public ShiftFilterCriteria setTimeTypeList(List<BigInteger> timeTypeIdList) {
//        if(timeTypeIdList != null && !timeTypeIdList.isEmpty())
//            matchCriteria = matchCriteria.and("activity.balanceSettingsActivityTab.timeTypeId").in(timeTypeIdList);
//        return this;
//    }
//
//    public ShiftFilterCriteria setPlanneTimeType(List<BigInteger> plannedTimeTypeIdList) {
//        if(plannedTimeTypeIdList !=null && !plannedTimeTypeIdList.isEmpty())
//            matchCriteria = matchCriteria.and("activity.balanceSettingsActivityTab.presenceTypeId").in(plannedTimeTypeIdList);
//        return this;
//    }
//
//    public ShiftFilterCriteria setOrganizationTypes(List<Long> organizationTypeIdList) {
//        if(organizationTypeIdList != null && organizationTypeIdList.isEmpty())
//            matchCriteria = matchCriteria.and("activity.organizationTypes").in(organizationTypeIdList);
//        return this;
//    }
//
//    public ShiftFilterCriteria setEmploymentTypes(List<Long> employmentTypeIdList) {
//        if(employmentTypeIdList != null && !employmentTypeIdList.isEmpty())
//            matchCriteria = matchCriteria.and("activity.employmentTypeIdList").in(employmentTypeIdList);
//        return this;
//    }
//
    public ShiftFilterCriteria setUnitId(List unitIds) {
        if(unitIds != null && !unitIds.isEmpty())
            matchCriteria = matchCriteria.and("unitId").in(unitIds);
        return this;
    }
//
//    public ShiftFilterCriteria setCategoryId(List<BigInteger> categoryIds) {
//        if(categoryIds != null && !categoryIds.isEmpty())
//            matchCriteria = matchCriteria.and("activity.generalActivityTab.categoryIds").in(categoryIds);
//        return this;
//    }
//
//    public ShiftFilterCriteria setExpertiseCriteria(List<Long> expertises){
//        if(expertises != null && !expertises.isEmpty())
//            matchCriteria = matchCriteria.and("activity.expertises").in(expertises);
//        return this;
//    }

    public ShiftFilterCriteria setStaffIds(List staffIds) {
        if(staffIds != null && !staffIds.isEmpty())
            matchCriteria = matchCriteria.and("staffId").in(staffIds);
        return this;
    }

    public List<AggregationOperation> getMatchOperations(){
        return operations;
    }
}
