package com.kairos.activity.service.counter;

import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;

public class ActivityFilterCriteria {
    private Criteria criteria;

    private  ActivityFilterCriteria(){
        this.criteria = Criteria.where("deleted").is(false);
    }

    public static ActivityFilterCriteria getInstance(){
        return new ActivityFilterCriteria();
    }

    public ActivityFilterCriteria setActivityIds(List<BigInteger> activityIds){
        if(activityIds !=null && !activityIds.isEmpty())
            criteria = criteria.and("_id").in(activityIds);
        return this;
    }

    public ActivityFilterCriteria setTimeTypeList(List<BigInteger> timeTypeList) {
        if(timeTypeList != null && !timeTypeList.isEmpty())
            criteria = criteria.and("");
        return this;
    }

    public ActivityFilterCriteria setPlanneTimeType(List<BigInteger> plannedTimeType) {
        if(plannedTimeType !=null && !plannedTimeType.isEmpty())
            criteria = criteria.and("");
        return this;
    }

    public ActivityFilterCriteria setOrganizationTypes(List<Long> organizationTypes) {
        if(organizationTypes != null && organizationTypes.isEmpty())
            criteria = criteria.and("");
        return this;
    }

    public ActivityFilterCriteria setEmploymentTypes(List<Long> employmentTypes) {
        if(employmentTypes != null && !employmentTypes.isEmpty())
            criteria = criteria.and("");
        return this;
    }

    public ActivityFilterCriteria setUnitId(List<Long> unitIds) {
        if(unitIds != null && !unitIds.isEmpty())
            criteria = criteria.and("unitIds").in(unitIds);
        return this;
    }

    public ActivityFilterCriteria setCategoryId(List<BigInteger> categoryIds) {
        if(categoryIds != null && !categoryIds.isEmpty())
            criteria = criteria.and("generalActivityTab.categoryIds").in(categoryIds);
        return this;
    }

    public ActivityFilterCriteria setExpertiseCriteria(List<Long> expertises){
        if(expertises != null && !expertises.isEmpty())
            criteria = criteria.and("expertises").in(expertises);
        return this;
    }

    public Criteria getFilterCriteria(){
        return criteria;
    }
}
