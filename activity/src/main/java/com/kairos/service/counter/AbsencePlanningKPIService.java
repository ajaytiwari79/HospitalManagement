package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.rest_client.UserIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

@Service
public class AbsencePlanningKPIService {
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private CounterHelperService counterHelperService;
    @Inject
    private TodoRepository todoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;


    private List<CommonKpiDataUnit> getAbsencePlanningKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits;
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>) filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = (List<Long>) filterCriteria[2];
        Collection<String> todoStatus =(List<String>)filterCriteria[5];
        if (CollectionUtils.isEmpty(unitIds)) {
            unitIds.add(organizationId);
        }
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI, filterDates, staffIds, new ArrayList<>(), unitIds, organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) kpiData[0];
        staffIds = (List<Long>) kpiData[2];
        List<TodoDTO> todos = todoRepository.findAllByKpiFilter(unitIds.get(0),dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(),staffIds, todoStatus);
        return new ArrayList<>();
    }






    private Map<Object,Map<ShiftStatus,Long>> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Shift> shifts) {
        Map<ShiftStatus,Long>  shiftActivityStatus = new HashMap<>() ;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:

                break;
            case REPRESENT_TOTAL_DATA:

                break;
            case REPRESENT_PER_INTERVAL:

                break;
            default:

                break;
        }
        return new HashMap<>();
    }

}
