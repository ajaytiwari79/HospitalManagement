package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.FilterType.ACTIVITY_IDS;

@Service
public class TimeBankOffKPI {
    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    public double getCountAndHoursAndPercentageOfTODOS(Long staffId, DateTimeInterval dateTimeInterval,KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){

        List<StaffKpiFilterDTO> staffKpiFilterDTOS = isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        for(StaffKpiFilterDTO staffKpiFilterDTO :staffKpiFilterDTOS){
           for()
        }
        return 0.0d;
    }

    public List<TodoDTO> getFilterTodoDTO(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        Map<BigInteger,List<TodoDTO>> activityIdTodoListMap = kpiCalculationRelatedInfo.getActivityIdAndTodoListMap();
        Set<BigInteger> activityIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS)) : new HashSet<>();
        for(BigInteger activityId :activityIdTodoListMap.keySet()){
            if(activityIds.contains(activityId)){
                lis
            }
        }

    }
}
