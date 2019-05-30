package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.KPIUtils.getDateTimeIntervals;
import static com.kairos.commons.utils.KPIUtils.sortKpiDataByDateTimeInterval;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@Service
public class TimeBankKpiCalculationService implements CounterService {
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private TimeBankRepository timeBankRepository;

    private Map<Long, Set<DateTimeInterval>> getPlanningPeriodIntervals(List<Long> unitIds, Date startDate, Date endDate, List<BigInteger> phaseIds) {
        Map<Long, Set<DateTimeInterval>> unitAndDateTimeIntervalMap = new HashMap<>();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdsAndBetweenDates(unitIds, startDate, endDate);
        Map<Long, List<PlanningPeriod>> unitAndPlanningPeriodMap = planningPeriods.stream().collect(Collectors.groupingBy(PlanningPeriod::getUnitId, Collectors.toList()));
        unitIds.forEach(unitId -> {
            Set<DateTimeInterval> dateTimeIntervals;
            if (CollectionUtils.isNotEmpty(phaseIds)) {
                dateTimeIntervals = unitAndPlanningPeriodMap.get(unitId).stream().filter(planningPeriod -> phaseIds.contains(planningPeriod.getCurrentPhaseId())).map(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
            } else {
                dateTimeIntervals = unitAndPlanningPeriodMap.get(unitId).stream().map(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
            }
            unitAndDateTimeIntervalMap.put(unitId, dateTimeIntervals);
        });
        return unitAndDateTimeIntervalMap;
    }

    private List<DailyTimeBankEntry> getDailyTimeBankEntryByDate(List<Long> employmentIds, LocalDate startDate, LocalDate endDate, List<String> daysOfWeek) {
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByIdsAndBetweenDates(employmentIds, asDate(startDate), asDate(endDate));
        List<LocalDate> localDates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(daysOfWeek)) {
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
                if (daysOfWeek.contains(date.getDayOfWeek().toString())) {
                    localDates.add(date);
                }
            }
            dailyTimeBankEntries = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> localDates.contains(dailyTimeBankEntry.getDate())).collect(Collectors.toList());
        }
        return dailyTimeBankEntries;
    }

    private Map<DateTimeInterval,List<DailyTimeBankEntry>> getDailyTimeBankEntryByInterval(List<DailyTimeBankEntry> dailyTimeBankEntries, List<DateTimeInterval> dateTimeIntervals){
        Map<DateTimeInterval, List<DailyTimeBankEntry>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, dailyTimeBankEntries.stream().filter(dailyTimeBankEntry ->   dateTimeInterval.contains(asDate(dailyTimeBankEntry.getDate()))).collect(Collectors.toList()));
        }
        return  dateTimeIntervalListMap;
    }

    private Map<Long,List<DailyTimeBankEntry>> getDailyTimeBankEntryByStaffId(List<Long> staffIds,List<DailyTimeBankEntry> dailyTimeBankEntries){
        Map<Long, List<DailyTimeBankEntry>> dateTimeIntervalListMap = new HashMap<>();
        for (Long staffId : staffIds) {
            dateTimeIntervalListMap.put(staffId, dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> dailyTimeBankEntry.getStaffId().equals(staffId)).collect(Collectors.toList()));
        }
        return  dateTimeIntervalListMap;
    }

    private List<CommonKpiDataUnit> getTimeBankForUnitKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria,ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        List<BigInteger> phaseIds = filterBasedCriteria.containsKey(FilterType.PHASE) ? KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.PHASE)) : new ArrayList<>();
        List<String> daysOfWeek = filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAYS_OF_WEEK)) ? filterBasedCriteria.get(FilterType.DAYS_OF_WEEK) : Stream.of(DayOfWeek.values()).map(Enum::name).collect(Collectors.toList());
        List<Long> staffIds = filterBasedCriteria.containsKey(FilterType.STAFF_IDS) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = new ArrayList<>();
        if (isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))) {
            filterDates = KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL));
        }
        List<Long> unitIds = filterBasedCriteria.containsKey(FilterType.UNIT_IDS) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<DateTimeInterval> dateTimeIntervals = getDateTimeIntervals(applicableKPI.getInterval(), applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, new ArrayList<>(), organizationId,dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        if (CollectionUtils.isEmpty(unitIds)) {
            unitIds.add(organizationId);
        }
        staffIds = staffKpiFilterDTOS.stream().map(StaffKpiFilterDTO::getId).collect(Collectors.toList());
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectDoubleMap = calculateDataByKpiRepresentation(staffIds, dateTimeIntervals, applicableKPI,unitIds,staffKpiFilterDTOS,daysOfWeek,phaseIds);
        getKpiDataUnits(objectDoubleMap, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getTimeBankForUnitKpiData(organizationId, filterBasedCriteria,null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi,ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTimeBankForUnitKpiData(organizationId, filterBasedCriteria,applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF :AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public Map<Long,Number> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
        return new HashMap<>();
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> calculateDataByKpiRepresentation(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Long> unitIds,List<StaffKpiFilterDTO> staffKpiFilterDTOS,List<String> daysOfWeek,List<BigInteger> phaseIds){
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap = new HashedMap();
        Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.groupingBy(StaffKpiFilterDTO::getUnitId, Collectors.toList()));
        Map<Long, Set<DateTimeInterval>> planningPeriodIntervel = getPlanningPeriodIntervals(unitIds, dateTimeIntervals.get(0).getStartDate(),dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndDate(), phaseIds);
        List<DailyTimeBankEntry> employmentAndDailyTimeBank = getDailyTimeBankEntryByDate(staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getEmployment().stream().map(EmploymentWithCtaDetailsDTO::getId)).collect(Collectors.toList()), dateTimeIntervals.get(0).getStartLocalDate(),dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate(), daysOfWeek);
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffIdAndTimeBankMap = getTimebankPerStaff(staffIds, dateTimeIntervals, staffKpiFilterDTOS, planningPeriodIntervel, employmentAndDailyTimeBank);
                break;
            case REPRESENT_TOTAL_DATA:
                staffIdAndTimeBankMap = getTotalTimeBankOfUnits(dateTimeIntervals, unitIds, subClusteredBarValue, unitAndStaffKpiFilterMap, planningPeriodIntervel, employmentAndDailyTimeBank);
                break;
            case REPRESENT_PER_INTERVAL:
                staffIdAndTimeBankMap = getTimeBankByInterval(dateTimeIntervals, unitIds, subClusteredBarValue, unitAndStaffKpiFilterMap, planningPeriodIntervel, employmentAndDailyTimeBank , applicableKPI.getFrequencyType());
                break;
            default:
                staffIdAndTimeBankMap = getTimeBankByInterval(dateTimeIntervals, unitIds, subClusteredBarValue, unitAndStaffKpiFilterMap, planningPeriodIntervel, employmentAndDailyTimeBank ,applicableKPI.getFrequencyType());
                break;
        }
        return staffIdAndTimeBankMap;

    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTimebankPerStaff(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<StaffKpiFilterDTO> staffKpiFilterDTOS, Map<Long, Set<DateTimeInterval>> planningPeriodIntervel, List<DailyTimeBankEntry> employmentAndDailyTimeBank) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap = new HashedMap();
        Map<Long, List<DailyTimeBankEntry>> staffAndDailyTimeBankMap;
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        Map<Long, StaffKpiFilterDTO> staffAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, v->v));
        Map<Long,List<DailyTimeBankEntry>> staffIdAndDailyTimeBankMap = getDailyTimeBankEntryByStaffId(staffIds,employmentAndDailyTimeBank);
        for (Long staffId :staffIds) {
            staffAndDailyTimeBankMap =staffIdAndDailyTimeBankMap.getOrDefault(staffId,new ArrayList<>()).stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
                Long totalTimeBankOfUnit = 0l;
                    StaffKpiFilterDTO staffKpiFilterDTO = staffAndStaffKpiFilterMap.get(staffId);
                    DateTimeInterval dateTimeInterval=new DateTimeInterval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate()));
            totalTimeBankOfUnit = getTotalTimeBank(planningPeriodIntervel, staffAndDailyTimeBankMap, dateTimeInterval, staffKpiFilterDTO.getUnitId(), totalTimeBankOfUnit, staffKpiFilterDTO);
            staffIdAndTimeBankMap.put(staffId, Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(staffId), null, DateUtils.getHoursByMinutes(totalTimeBankOfUnit))));
        }
        return staffIdAndTimeBankMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalTimeBankOfUnits(List<DateTimeInterval> dateTimeIntervals, List<Long> unitIds, List<ClusteredBarChartKpiDataUnit> subClusteredBarValue, Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap, Map<Long, Set<DateTimeInterval>> planningPeriodIntervel, List<DailyTimeBankEntry> employmentAndDailyTimeBank) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap = new HashedMap();
        Map<Long, List<DailyTimeBankEntry>> longListMap;
        longListMap=employmentAndDailyTimeBank.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
        for (Long unitId : unitIds) {
            Long totalTimeBankOfUnit = 0l;
            String unitName = (!unitAndStaffKpiFilterMap.get(unitId).isEmpty()) ? unitAndStaffKpiFilterMap.get(unitId).get(0).getUnitName() : "";
            for (StaffKpiFilterDTO staffKpiFilterDTO : unitAndStaffKpiFilterMap.get(unitId)) {
                DateTimeInterval dateTimeInterval=new DateTimeInterval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate()));
                totalTimeBankOfUnit = getTotalTimeBank(planningPeriodIntervel, longListMap, dateTimeInterval, unitId, totalTimeBankOfUnit, staffKpiFilterDTO);
            }
            subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(unitName, null, DateUtils.getHoursByMinutes(totalTimeBankOfUnit)));
        }
        staffIdAndTimeBankMap.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getStartDate())),subClusteredBarValue );
        return staffIdAndTimeBankMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTimeBankByInterval(List<DateTimeInterval> dateTimeIntervals, List<Long> unitIds, List<ClusteredBarChartKpiDataUnit> subClusteredBarValue, Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap, Map<Long, Set<DateTimeInterval>> planningPeriodIntervel, List<DailyTimeBankEntry> employmentAndDailyTimeBank, DurationType frequencyType) {
        Map<Long, List<DailyTimeBankEntry>> longListMap;
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap = new HashedMap();
        Map<DateTimeInterval,List<DailyTimeBankEntry>> dateTimeIntervalListMap1 = getDailyTimeBankEntryByInterval(employmentAndDailyTimeBank,dateTimeIntervals);
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
          longListMap=dateTimeIntervalListMap1.getOrDefault(dateTimeInterval,new ArrayList<>()).stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
            for (Long unitId : unitIds) {
                Long totalTimeBankOfUnit = 0l;
                String unitName = (isCollectionNotEmpty(unitAndStaffKpiFilterMap.get(unitId))) ? unitAndStaffKpiFilterMap.get(unitId).get(0).getUnitName() : "";
                if(isCollectionNotEmpty(unitAndStaffKpiFilterMap.get(unitId))){
                for (StaffKpiFilterDTO staffKpiFilterDTO : unitAndStaffKpiFilterMap.get(unitId)) {
                    totalTimeBankOfUnit = getTotalTimeBank(planningPeriodIntervel, longListMap, dateTimeInterval, unitId, totalTimeBankOfUnit, staffKpiFilterDTO);
                }
                }
                subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(unitName, null, DateUtils.getHoursByMinutes(totalTimeBankOfUnit)));
            }
            staffIdAndTimeBankMap.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), subClusteredBarValue);
        }
        return staffIdAndTimeBankMap;
    }

    private Long getTotalTimeBank(Map<Long, Set<DateTimeInterval>> planningPeriodIntervel, Map<Long, List<DailyTimeBankEntry>> longListMap, DateTimeInterval dateTimeInterval, Long unitId, Long totalTimeBankOfUnit, StaffKpiFilterDTO staffKpiFilterDTO) {
        for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = longListMap.getOrDefault(employmentWithCtaDetailsDTO.getId(), new ArrayList<>());
            int timeBankOfInterval = timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervel.get(unitId), new Interval(DateUtils.getLongFromLocalDate(dateTimeInterval.getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeInterval.getEndLocalDate())), employmentWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
            int calculatedTimeBank = dailyTimeBankEntries.stream().mapToInt(DailyTimeBankEntry::getDeltaTimeBankMinutes).sum();
            int totalTimeBank = calculatedTimeBank - timeBankOfInterval;
            totalTimeBankOfUnit += totalTimeBank;
        }
        return totalTimeBankOfUnit;
    }

    private void getKpiDataUnits(Map<Object, List<ClusteredBarChartKpiDataUnit>> staffRestingHours, List<CommonKpiDataUnit> kpiDataUnits, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        for (Map.Entry<Object, List<ClusteredBarChartKpiDataUnit>> entry : staffRestingHours.entrySet()) {
            switch (applicableKPI.getKpiRepresentation()) {
                case REPRESENT_PER_STAFF:
                    Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
                    kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getValue()));
                    break;
                default:
                    kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(entry.getKey().toString(), entry.getValue()));
                    break;

            }
        }
    }

}
