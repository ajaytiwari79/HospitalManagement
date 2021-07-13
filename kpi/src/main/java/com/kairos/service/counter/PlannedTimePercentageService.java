package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.KPISetResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.ApplicableKPI;
import com.kairos.persistence.model.FibonacciKPICalculation;
import com.kairos.persistence.model.KPI;
import com.kairos.persistence.repository.counter.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.counter.ShiftMongoRepository;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.enums.kpi.KPIRepresentation.REPRESENT_PER_STAFF;

@Service
public class PlannedTimePercentageService implements CounterService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private CounterHelperService counterHelperService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;

    private Map<BigInteger,String> plannedTimeIdAndNameMap=new HashMap<>();
    private List<CommonKpiDataUnit> getPlannedTimePercentageOfShift(Long organizationId, Map<FilterType, List> filterBasedCriteria, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>) filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = (List<Long>) filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>) filterCriteria[3];
        List<BigInteger> plannedTimeIds=(List<BigInteger>) filterCriteria[6];
        Object[] kpiData = counterHelperService.getKPIdata(new HashMap(),applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) kpiData[0];
        staffIds = (List<Long>) kpiData[2];
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findShiftsByShiftAndActvityKpiFilters(staffIds, ObjectUtils.isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), new ArrayList<>(), dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate(),null);
        List<PresenceTypeDTO> plannedTimes= planningPeriodMongoRepository.getAllPresenceTypeByCountry(UserContext.getUserDetails().getCountryId());
        if(ObjectUtils.isCollectionNotEmpty(plannedTimeIds)){
         plannedTimeIdAndNameMap=plannedTimes.stream().filter(presenceTypeDTO -> plannedTimeIds.contains(presenceTypeDTO.getId())).collect(Collectors.toMap(PresenceTypeDTO::getId, PresenceTypeDTO::getName));
        }else {
            plannedTimeIdAndNameMap=plannedTimes.stream().collect(Collectors.toMap(PresenceTypeDTO::getId, PresenceTypeDTO::getName));
        }
        Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, shifts.stream().filter(shift -> dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList()));
        }
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectDoubleMap = calculateDataByKpiRepresentation(staffIds, dateTimeIntervalListMap, dateTimeIntervals, applicableKPI, shifts);
        KPIUtils.getKpiDataUnits(objectDoubleMap, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        KPIUtils.sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }




    private Double getPlannedTimePercentage(double shiftDuration, double plannedtimeDiff) {
        return KPIUtils.getValueWithDecimalFormat(plannedtimeDiff/shiftDuration*100);
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> calculateDataByKpiRepresentation(List<Long> staffIds, Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalAndShiftListMap, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<ShiftWithActivityDTO> shifts) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndShiftAndPlannedTimePercentageMap;
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffIdAndShiftAndPlannedTimePercentageMap = getShiftAndPlannedTimePercentagePerStaff(staffIds, shifts);
                break;
            case REPRESENT_TOTAL_DATA:
                staffIdAndShiftAndPlannedTimePercentageMap = getShiftAndPlannedTimePercentageByRepresentTotalData(dateTimeIntervals, shifts);
                break;
            default:
                staffIdAndShiftAndPlannedTimePercentageMap = getShiftAndPlannedTimePercentageByRepresentPerInterval(dateTimeIntervalAndShiftListMap, dateTimeIntervals, applicableKPI.getFrequencyType());
                break;
        }
        return KPIUtils.verifyKPIResponseListData(staffIdAndShiftAndPlannedTimePercentageMap) ? staffIdAndShiftAndPlannedTimePercentageMap : new HashMap<>();
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getShiftAndPlannedTimePercentageByRepresentPerInterval(Map<DateTimeInterval, List<ShiftWithActivityDTO>> dateTimeIntervalAndShiftListMap, List<DateTimeInterval> dateTimeIntervals, DurationType frequencyType) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndShiftAndActivityDurationMap = new HashedMap();
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue;
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            subClusteredBarValue = new ArrayList<>();
            List<ShiftWithActivityDTO> shiftWithActivityDTOs = dateTimeIntervalAndShiftListMap.get(dateTimeInterval);
            if (CollectionUtils.isNotEmpty(shiftWithActivityDTOs)) {
                subClusteredBarValue = getShiftAndPlannedTimePercentageMap(shiftWithActivityDTOs);
            }
            staffIdAndShiftAndActivityDurationMap.put(DurationType.DAYS.equals(frequencyType) ? DateUtils.getStartDateTimeintervalString(dateTimeInterval) : DateUtils.getDateTimeintervalString(dateTimeInterval), subClusteredBarValue);
        }
        return staffIdAndShiftAndActivityDurationMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getShiftAndPlannedTimePercentageByRepresentTotalData(List<DateTimeInterval> dateTimeIntervals, List<ShiftWithActivityDTO> shifts) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndShiftAndActivityDurationMap = new HashedMap();
        staffIdAndShiftAndActivityDurationMap.put(DateUtils.getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), getShiftAndPlannedTimePercentageMap(shifts));
        return staffIdAndShiftAndActivityDurationMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getShiftAndPlannedTimePercentagePerStaff(List<Long> staffIds, List<ShiftWithActivityDTO> shifts) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndPlannedTimePercentageMap = new HashedMap();
        Map<Long, List<ShiftWithActivityDTO>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(ShiftDTO::getStaffId, Collectors.toList()));
        for (Long staffId : staffIds) {
            staffIdAndPlannedTimePercentageMap.put(staffId, getShiftAndPlannedTimePercentageMap( staffShiftMapping.getOrDefault(staffId,new ArrayList<>())));
       }
        return staffIdAndPlannedTimePercentageMap;
    }

    private List<ClusteredBarChartKpiDataUnit> getShiftAndPlannedTimePercentageMap(List<ShiftWithActivityDTO> shifts) {
        double shiftDuration=0.0;
        Map<String, Double> plannedTimeAndPercentageMap = new HashMap<>();
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        for (ShiftWithActivityDTO shift : shifts) {
            for (ShiftActivityDTO activity : shift.getActivities()) {
                for (PlannedTime plannedTime : activity.getPlannedTimes()) {
                    if(plannedTimeIdAndNameMap.containsKey(plannedTime.getPlannedTimeId())) {
                        double plannedtimeDuration = plannedTimeAndPercentageMap.getOrDefault(plannedTimeIdAndNameMap.get(plannedTime.getPlannedTimeId()), 0.0);
                        plannedTimeAndPercentageMap.put(plannedTimeIdAndNameMap.get(plannedTime.getPlannedTimeId()), plannedtimeDuration +Double.valueOf(DateUtils.getTimeDuration(plannedTime.getStartDate(), plannedTime.getEndDate())));
                    }
                }
            }
            shiftDuration+= DateUtils.getTimeDuration(shift.getStartDate(),shift.getEndDate());
        }
        if(ObjectUtils.isCollectionNotEmpty(shifts)) subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(AppConstants.SHIFT, 100));
        for (Map.Entry<String, Double> stringDoubleEntry : plannedTimeAndPercentageMap.entrySet()) {
            subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(stringDoubleEntry.getKey(), getPlannedTimePercentage(shiftDuration,stringDoubleEntry.getValue())));
        }

        return subClusteredBarValue;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getPlannedTimePercentageOfShift(organizationId, filterBasedCriteria, null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.PERCENT, dataList, new KPIAxisData(AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getPlannedTimePercentageOfShift(organizationId, filterBasedCriteria, applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.PERCENT, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(REPRESENT_PER_STAFF) ? AppConstants.STAFF : AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        return new KPISetResponseDTO();
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS,KPI kpi, ApplicableKPI applicableKPI) {
        return new TreeSet<>();
    }
}
