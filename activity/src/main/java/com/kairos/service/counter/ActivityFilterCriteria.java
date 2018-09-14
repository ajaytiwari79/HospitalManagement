package com.kairos.service.counter;

import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class ActivityFilterCriteria {
    private Criteria criteria;


    private  ActivityFilterCriteria(){
        this.criteria = Criteria.where("deleted").is(false);
    }

    public static ActivityFilterCriteria getInstance(){
        return new ActivityFilterCriteria();
    }

    public ActivityFilterCriteria setActivityIds(List activityIds){
        if(activityIds !=null && !activityIds.isEmpty()) {
            criteria = criteria.and("_id").in(activityIds);
        }
        return this;
    }

    public ActivityFilterCriteria setTimeTypeList(List timeTypeIds) {
        if(timeTypeIds != null && !timeTypeIds.isEmpty())
            criteria = criteria.and("");
        return this;
    }

    public ActivityFilterCriteria setPlanneTimeType(List plannedTimeType) {
        if(plannedTimeType !=null && !plannedTimeType.isEmpty())
            criteria = criteria.and("");
        return this;
    }

    public ActivityFilterCriteria setOrganizationTypes(List organizationTypes) {
        if(organizationTypes != null && organizationTypes.isEmpty())
            criteria = criteria.and("organizationTypes").in(organizationTypes);
        return this;
    }

    public ActivityFilterCriteria setEmploymentTypes(List employmentTypes) {
        if(employmentTypes != null && !employmentTypes.isEmpty())
            criteria = criteria.and("employmentTypes").in(employmentTypes);
        return this;
    }

    public ActivityFilterCriteria setUnitId(List unitIds) {
        if(unitIds != null && !unitIds.isEmpty())
            criteria = criteria.and("unitIds").in(unitIds);
        return this;
    }

    public ActivityFilterCriteria setCategoryId(List categoryIds) {
        if(categoryIds != null && !categoryIds.isEmpty())
            criteria = criteria.and("generalActivityTab.categoryIds").in(categoryIds);
        return this;
    }

    public ActivityFilterCriteria setExpertiseCriteria(List expertises){
        if(expertises != null && !expertises.isEmpty())
            criteria = criteria.and("expertises").in(expertises);
        return this;
    }

    public Criteria getFilterCriteria(){
        return criteria;
    }
}
