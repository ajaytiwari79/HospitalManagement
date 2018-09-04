package com.kairos.service.counter;

import com.kairos.activity.counter.data.FilterCriteria;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeTypes;
import com.kairos.service.activity.TimeTypeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class RestingHoursCalculationService implements CounterService{
    @Inject
    TimeTypeService timeTypeService;

    private List<BigInteger> getFilterredActivities(List<FilterCriteria> filters){
        ActivityFilterCriteria activityCriteria = ActivityFilterCriteria.getInstance();
        for(FilterCriteria criteria: filters){
            switch(criteria.getType()){
                case ACTIVITY_IDS: activityCriteria.setActivityIds(criteria.getValues()); break;
                case UNIT_IDS: activityCriteria.setUnitId(criteria.getValues()); break;
                case ACTIVITY_CATEGORY_TYPE: activityCriteria.setCategoryId(criteria.getValues()); break;
                case EMPLOYMENT_TYPE: activityCriteria.setEmploymentTypes(criteria.getValues()); break;
                case EXPERTISE: activityCriteria.setExpertiseCriteria(criteria.getValues()); break;
                case TIME_TYPE: activityCriteria.setTimeTypeList(criteria.getValues()); break;
                case PLANNED_TIME_TYPE: activityCriteria.setPlanneTimeType(criteria.getValues()); break;
                case ORGANIZATION_TYPE: activityCriteria.setOrganizationTypes(criteria.getValues()); break;
                default: break;
            }
        }

        return null;
    }

    private FilterCriteria getTimeTypeCriteriaForRestingHours(Long countryId){
        List supportedTimeTypeIdList = timeTypeService.getTimeTypesByTimeTypesAndByCountryId(countryId, TimeTypes.WORKING_TYPE);
        return new FilterCriteria(FilterType.TIME_TYPE, supportedTimeTypeIdList);
    }

    @Override
    public Map getCalculatedCounter(List<FilterCriteria> providedFiltersMap) {

        return null;
    }

    @Override
    public Map getCalculatedKPI(List<FilterCriteria> providedFilterMap) {
        return null;
    }
}
