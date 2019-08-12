package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.Day;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.utils.counter.KPIUtils.sortKpiDataByDateTimeInterval;
import static com.kairos.utils.counter.KPIUtils.verifyKPIResponseData;

@Service
public class AbsencePlanningKPIService {
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private CounterHelperService counterHelperService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;

    private Map<Long, Set<DateTimeInterval>> getPlanningPeriodIntervals(List<Long> unitIds, Date startDate, Date endDate) {
        Map<Long, Set<DateTimeInterval>> unitAndDateTimeIntervalMap = new HashMap<>();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdsAndBetweenDates(unitIds, startDate, endDate);
        Map<Long, List<PlanningPeriod>> unitAndPlanningPeriodMap = planningPeriods.stream().collect(Collectors.groupingBy(PlanningPeriod::getUnitId, Collectors.toList()));
        unitIds.forEach(unitId -> {
            Set<DateTimeInterval> dateTimeIntervals = unitAndPlanningPeriodMap.getOrDefault(unitId, new ArrayList<>()).stream().map(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
            unitAndDateTimeIntervalMap.put(unitId, dateTimeIntervals);
        });
        return unitAndDateTimeIntervalMap;
    }

    public List<ShiftStatus> getActivityStatus(List<Shift> shifts) {
        List<ShiftStatus> shiftStatuses = new ArrayList<>();
        shifts.forEach(shift -> {
            shiftStatuses.addAll(shift.getActivities().stream().map(x -> x.getStatus()).flatMap(x -> x.stream()).collect(Collectors.toList()));
        });
        return shiftStatuses;
    }

    private List<CommonKpiDataUnit> getAbsencePlanningKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits;
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>) filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = (List<Long>) filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>) filterCriteria[3];
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        if (CollectionUtils.isEmpty(unitIds)) {
            unitIds.add(organizationId);
        }
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) kpiData[0];
        staffIds = (List<Long>) kpiData[2];
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFiltersWithActivityStatus(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), shiftActivityStatus, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<ShiftStatus, Long> absencePlanningActivityStatus = getAbsencePlanningActivityStatus(shifts);
        kpiDataUnits = null;
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    public Map<ShiftStatus, Long> getAbsencePlanningActivityStatus(List<Shift> shifts) {
        List<ShiftStatus> shiftStatuses = getActivityStatus(shifts);
        return shiftStatuses.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    }

    public Map<Object, Map<ShiftStatus, Long>> getAbsencePlanningActivityStatusForStaff(List<Shift> shifts, List<Long> staffIds) {

        Map<ShiftStatus, Long> shiftActivityStatus;
        Map<Object, Map<ShiftStatus, Long>> staffActivityStatusMap = new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            shiftActivityStatus = getAbsencePlanningActivityStatus(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
            staffActivityStatusMap.put(staffId, shiftActivityStatus);

        }

        return staffActivityStatusMap;
    }

    public Map<Object, Map<ShiftStatus, Long>> getAbsencePlanningActivityStatusForPerInterval(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, DurationType frequencyType) {
        Map<ShiftStatus, Long> shiftActivityStatus;
        Map<Object, Map<ShiftStatus, Long>> staffActivityStatusMap = new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping;
        Map<DateTimeInterval, Map<Long, List<Shift>>> dateTimeIntervalAndShiftMap = new HashedMap();
        dateTimeIntervalListMap.keySet().stream().forEach(dateTimeInterval -> dateTimeIntervalAndShiftMap.put(dateTimeInterval, dateTimeIntervalListMap.get(dateTimeInterval).stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()))));
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            shiftActivityStatus = new HashMap<>();
            staffShiftMapping = dateTimeIntervalAndShiftMap.get(dateTimeInterval);
            for (Long staffId : staffIds) {
                shiftActivityStatus = getAbsencePlanningActivityStatus(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
            }
            staffActivityStatusMap.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), shiftActivityStatus);
        }

        return staffActivityStatusMap;
    }

    public Map<Object, Map<ShiftStatus, Long>> getAbsencePlanningActivityStatusForTotalData(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<Shift> shifts, Map<ShiftStatus, Long> shiftActivityStatus) {
        Map<Object, Map<ShiftStatus, Long>> staffWithActivityStatus = new HashMap<>();
        Map<Long, List<Shift>> staffShiftMapping;
        staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            shiftActivityStatus = getAbsencePlanningActivityStatus(staffShiftMapping.getOrDefault(staffId, new ArrayList<>()));
        }
        staffWithActivityStatus.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getStartDate())), shiftActivityStatus);
        return staffWithActivityStatus;

    }

    public Map<Object,Map<ShiftStatus,Long>> getActivityStatusForTimeSlot(Long unitId)
    {
        List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlot(unitId);

     
        return null;
    }

    private Map<Object,Map<ShiftStatus,Long>> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Shift> shifts) {
        Map<Object, Map<ShiftStatus,Long>> staffWithActivityStatus;
        Map<ShiftStatus,Long>  shiftActivityStatus = new HashMap<>() ;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffWithActivityStatus = getAbsencePlanningActivityStatusForStaff(shifts,staffIds);
                break;
            case REPRESENT_TOTAL_DATA:
                staffWithActivityStatus = getAbsencePlanningActivityStatusForTotalData(staffIds, dateTimeIntervals, shifts, shiftActivityStatus);
                break;
            case REPRESENT_PER_INTERVAL:
                staffWithActivityStatus = getAbsencePlanningActivityStatusForPerInterval(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI.getFrequencyType());
                break;
            default:
                staffWithActivityStatus = getAbsencePlanningActivityStatusForPerInterval(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI.getFrequencyType());
                break;
        }
        return staffWithActivityStatus;
    }


}
