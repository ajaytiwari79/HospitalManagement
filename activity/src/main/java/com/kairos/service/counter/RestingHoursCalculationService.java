package com.kairos.service.counter;

import com.kairos.dto.activity.counter.data.BasicRequirementDTO;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.data.RepresentationDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepositoryImpl;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.activity.TimeTypeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RestingHoursCalculationService implements CounterService{
    @Inject
    TimeTypeService timeTypeService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    ActivityMongoRepositoryImpl activityMongoRepository;
    @Inject private CounterDataService counterDataService;

    public Map<Long,Long> calculateRestingHours(List<Long> staffIds, Date startDate, Date endDate){
        Map<Long,Long> staffRestingHours=new HashMap<>();
        staffIds.stream().forEach(staffId -> {
            List<Shift> shifts= shiftMongoRepository.findAllShiftsByStaffIdsAndDate(Arrays.asList(staffId),startDate,endDate);
            Long restingHours =counterDataService.getTotalRestingHours(shifts,startDate.getTime(),endDate.getTime(),0,false);
            staffRestingHours.put(staffId,restingHours);

        });
        return staffRestingHours;
    }


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
    public RepresentationDTO getCalculatedCounter(FilterCriteriaDTO filterCriteria, BasicRequirementDTO representationRequirement) {
        filterCriteria.getFilters().add(getTimeTypeCriteriaForRestingHours(filterCriteria.getCurrentCountryId()));
        List<BigInteger> activityIds = activityMongoRepository.getActivityIdsByFilter(filterCriteria.getFilters());
        return null;
    }
}
