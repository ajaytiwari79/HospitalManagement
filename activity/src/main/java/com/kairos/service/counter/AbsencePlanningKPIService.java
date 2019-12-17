package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.rest_client.UserIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.utils.counter.KPIUtils.*;

@Service
public class AbsencePlanningKPIService implements CounterService {
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private CounterHelperService counterHelperService;
    @Inject
    private TodoRepository todoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;


    private List<CommonKpiDataUnit> getAbsencePlanningKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>) filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = (List<Long>) filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>) filterCriteria[3];
        Collection<String> todoStatus = (List<String>) filterCriteria[5];
        if (CollectionUtils.isEmpty(unitIds)) {
            unitIds.add(organizationId);
        }

        List<DateTimeInterval> dateTimeIntervals = getDateTimeIntervals(applicableKPI.getInterval(), isNull(applicableKPI) ? 0 : applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates, null);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString());
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiDefaultData(staffEmploymentTypeDTO);
        staffIds = defaultKpiDataDTO.getStaffKpiFilterDTOs().stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        List<TodoDTO> todoDTOS = todoRepository.findAllByKpiFilter(unitIds.get(0), dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(), staffIds, todoStatus);
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectDoubleMap = calculateDataByKpiRepresentation(staffIds, dateTimeIntervals, applicableKPI, todoDTOS, defaultKpiDataDTO.getTimeSlotDTOS());
        getKpiDataUnits(objectDoubleMap, kpiDataUnits, applicableKPI, defaultKpiDataDTO.getStaffKpiFilterDTOs());
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }




    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTodoCountByTimeSlot(List<TimeSlotDTO> timeSlotDTOs, List<TodoDTO> todoDTOS) {
        List<TodoDTO> todoDtos = new CopyOnWriteArrayList<>(todoDTOS);
        Map<Object, List<ClusteredBarChartKpiDataUnit>> timeSlotAndActivityStatusAndCountMap = new HashedMap();
        for (TimeSlotDTO timeSlotDTO : timeSlotDTOs) {
            List<TodoDTO> todoDTOs = new ArrayList<>();
            LocalTime startTime = LocalTime.of(timeSlotDTO.getStartHour(), timeSlotDTO.getStartMinute());
            LocalTime endTime = LocalTime.of(timeSlotDTO.getEndHour(), timeSlotDTO.getEndMinute());
            for (TodoDTO todoDTO : todoDtos) {
                DateTimeInterval dateTimeInterval = new DateTimeInterval(getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(todoDTO.getShiftDateTime()), startTime)), getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(todoDTO.getShiftDateTime()), endTime)));
                if (AppConstants.NIGHT.equals(timeSlotDTO.getName())) {
                    dateTimeInterval = new DateTimeInterval(getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(todoDTO.getShiftDateTime()), startTime)), getLongFromLocalDateimeTime(LocalDateTime.of(asLocalDate(todoDTO.getShiftDateTime()).plusDays(1), endTime)));
                }
                if (dateTimeInterval.contains(todoDTO.getShiftDateTime())) {
                    todoDtos.remove(todoDTO);
                    todoDTOs.add(todoDTO);
                }
            }
            timeSlotAndActivityStatusAndCountMap.put(timeSlotDTO.getName(), getActivityStatusCount(todoDTOs));
        }
        return timeSlotAndActivityStatusAndCountMap;
    }


    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTodoCountByRepresentPerStaff(List<Long> staffIds, List<TodoDTO> todoDTOS) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndActivityStatusAndCountMap = new HashedMap();
        Map<Long, List<TodoDTO>> staffTodoShiftMapping = todoDTOS.parallelStream().collect(Collectors.groupingBy(TodoDTO::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            staffIdAndActivityStatusAndCountMap.put(staffId, getActivityStatusCount(staffTodoShiftMapping.getOrDefault(staffId, new ArrayList<>())));
        }
        return staffIdAndActivityStatusAndCountMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTodoCountByRepresentPerInterval(List<DateTimeInterval> dateTimeIntervals, List<TodoDTO> todoDTOS, DurationType frequencyType) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndActivityStatusAndCountMap = new HashedMap();
        Map<DateTimeInterval, List<TodoDTO>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, todoDTOS.stream().filter(todoDTO -> dateTimeInterval.contains(todoDTO.getShiftDateTime())).collect(Collectors.toList()));
        }
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            staffIdAndActivityStatusAndCountMap.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), getActivityStatusCount(dateTimeIntervalListMap.get(dateTimeInterval)));
        }
        return staffIdAndActivityStatusAndCountMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTodoCountByRepresentTotalData(List<DateTimeInterval> dateTimeIntervals, List<TodoDTO> todoDTOS) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndActivityStatusAndCountMap = new HashedMap();
        staffIdAndActivityStatusAndCountMap.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), getActivityStatusCount(todoDTOS));
        return staffIdAndActivityStatusAndCountMap;
    }

    public List<ClusteredBarChartKpiDataUnit> getActivityStatusCount(List<TodoDTO> todoDTOS) {
        List<ClusteredBarChartKpiDataUnit> clusteredBarChartKpiDataUnits = new ArrayList<>();
        int pending = 0;
        int disapprove = 0;
        int approve = 0;
        int requested = 0;
        for (TodoDTO todoDTO : todoDTOS) {
            switch (todoDTO.getStatus()) {
                case REQUESTED:
                    requested++;
                    break;
                case DISAPPROVE:
                    disapprove++;
                    break;
                case PENDING:
                    pending++;
                    break;
                case APPROVE:
                    approve++;
                    break;
                default:
                    break;
            }
        }
        clusteredBarChartKpiDataUnits.add(new ClusteredBarChartKpiDataUnit(TodoStatus.REQUESTED.toString(), AppConstants.REQUESTED_COLOR_CODE, Double.valueOf(requested)));
        clusteredBarChartKpiDataUnits.add(new ClusteredBarChartKpiDataUnit(TodoStatus.PENDING.toString(), AppConstants.PENDING_COLOR_CODE, Double.valueOf(pending)));
        clusteredBarChartKpiDataUnits.add(new ClusteredBarChartKpiDataUnit(TodoStatus.APPROVE.toString(), AppConstants.APPROVE_COLOR_CODE, Double.valueOf(approve)));
        clusteredBarChartKpiDataUnits.add(new ClusteredBarChartKpiDataUnit(TodoStatus.DISAPPROVE.toString(), AppConstants.DISAPPROVE_COLOR_CODE, Double.valueOf(disapprove)));
        return clusteredBarChartKpiDataUnits;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> calculateDataByKpiRepresentation(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<TodoDTO> todoDTOS, List<TimeSlotDTO> timeSlotDTOS) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectAndActivityStatusCountMap;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                objectAndActivityStatusCountMap = getTodoCountByRepresentPerStaff(staffIds, todoDTOS);
                break;
            case REPRESENT_PER_INTERVAL:
                objectAndActivityStatusCountMap = getTodoCountByRepresentPerInterval(dateTimeIntervals, todoDTOS, applicableKPI.getFrequencyType());
                break;
            case REPRESENT_TOTAL_DATA:
                objectAndActivityStatusCountMap = getTodoCountByRepresentTotalData(dateTimeIntervals, todoDTOS);
                break;
            default:
                objectAndActivityStatusCountMap = getTodoCountByTimeSlot(timeSlotDTOS, todoDTOS);
                break;
        }
        return objectAndActivityStatusCountMap;
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getAbsencePlanningKpiData(organizationId, filterBasedCriteria, null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.COUNT, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getAbsencePlanningKpiData(organizationId, filterBasedCriteria, applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), KPIRepresentation.INDIVIDUAL_STAFF.equals(applicableKPI.getKpiRepresentation()) ? ChartType.BAR : KPIRepresentation.COLUMN_TIMESLOT.equals(applicableKPI.getKpiRepresentation()) ? ChartType.BAR : ChartType.STACKED_CHART, XAxisConfig.COUNT, RepresentationUnit.NUMBER, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        return null;
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, KPI kpi,ApplicableKPI applicableKPI) {
        return new TreeSet<>();
    }
}
