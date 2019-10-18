package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.BarLineChartKPiDateUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.BarLineChartKPIRepresentationData;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.*;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.utils.counter.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.enums.kpi.KPIRepresentation.REPRESENT_PER_STAFF;
import static com.kairos.utils.counter.KPIUtils.getDateTimeIntervals;
import static com.kairos.utils.counter.KPIUtils.sortKpiDataByDateTimeInterval;

@Service
public class PlannedHoursVsTimeBankService implements CounterService {
    @Inject
    private  TimeBankCalculationService timeBankCalculationService;
    @Inject
    private PlannedHoursCalculationService plannedHoursCalculationService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private CounterHelperService  counterHelperService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject private PlanningPeriodService planningPeriodService;

    private Map<Long, Set<DateTimeInterval>> getPlanningPeriodIntervals(List<Long> unitIds, Date startDate, Date endDate) {
        Map<Long, Set<DateTimeInterval>> unitAndDateTimeIntervalMap = new HashMap<>();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdsAndBetweenDates(unitIds, startDate, endDate);
        Map<Long, List<PlanningPeriod>> unitAndPlanningPeriodMap = planningPeriods.stream().collect(Collectors.groupingBy(PlanningPeriod::getUnitId, Collectors.toList()));
        unitIds.forEach(unitId -> {
            Set<DateTimeInterval>  dateTimeIntervals = unitAndPlanningPeriodMap.getOrDefault(unitId , new ArrayList<>()).stream().map(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
            unitAndDateTimeIntervalMap.put(unitId, dateTimeIntervals);
        });
        return unitAndDateTimeIntervalMap;
    }

    private List<DailyTimeBankEntry> getDailyTimeBankEntryByDate(List<Long> staffIds, LocalDate startDate, LocalDate endDate, Set<DayOfWeek> daysOfWeeks) {
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByStaffIdsAndBetweenDates(staffIds, startDate,endDate);
        List<LocalDate> localDates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(daysOfWeeks)) {
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
                if (daysOfWeeks.contains(date.getDayOfWeek())) {
                    localDates.add(date);
                }
            }
            dailyTimeBankEntries = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> localDates.contains(dailyTimeBankEntry.getDate())).collect(Collectors.toList());
        }
        return dailyTimeBankEntries;
    }

    private List<CommonKpiDataUnit> getPlannedHoursVsTimeBankKpiStaffs(Long organizationId, Map<FilterType, List> filterBasedCriteria , ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> kpiDataUnits ;
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>)filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>)filterCriteria[1];
       List<Long> unitIds =(List<Long>)filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>)filterCriteria[3];
        List<DateTimeInterval> dateTimeIntervals =getDateTimeIntervals(applicableKPI.getInterval(), applicableKPI.getValue(), applicableKPI.getFrequencyType(), filterDates,null);
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentTypeIds, organizationId, dateTimeIntervals.get(0).getStartLocalDate().toString(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate().toString());
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiDefaultData(staffEmploymentTypeDTO);
        List<Long> dayTypeIds = filterBasedCriteria.containsKey(FilterType.DAY_TYPE) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.DAY_TYPE)) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.DAY_TYPE)) : defaultKpiDataDTO.getDayTypeDTOS().stream().map(DayTypeDTO::getId).collect(Collectors.toList());
        Map<Long, DayTypeDTO> daysTypeIdAndDayTypeMap = defaultKpiDataDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        Set<DayOfWeek> daysOfWeek = counterHelperService.getDayOfWeek(dayTypeIds,daysTypeIdAndDayTypeMap);
        List<Integer> dayOfWeeksNo = new ArrayList<>();
        daysOfWeek.forEach(dayOfWeek -> dayOfWeeksNo.add((dayOfWeek.getValue() < 7) ? dayOfWeek.getValue() + 1 : 1));
        if (CollectionUtils.isEmpty(unitIds)) {
            unitIds.add(organizationId);
        }
        Object[] kpiData = counterHelperService.getKPIdata(applicableKPI,filterDates, staffIds,employmentTypeIds,unitIds,organizationId);
         dateTimeIntervals = (List<DateTimeInterval>)kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>)kpiData[0];
        staffIds=(List<Long>)kpiData[2];
        List<Shift> shifts = shiftMongoRepository.findShiftsByKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), new HashSet<>(), dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate());
        Map<Object, Double> staffPlannedHours = plannedHoursCalculationService.calculatePlannedHours(staffIds, applicableKPI, dateTimeIntervals, shifts);
        Map<Object, Double> staffPlannedHoursAndTimeBankHours =calculateDataByKpiRepresentation(staffIds,dateTimeIntervals,applicableKPI,unitIds,staffKpiFilterDTOS,daysOfWeek);
       kpiDataUnits= getKpiDataUnits(staffPlannedHours,staffPlannedHoursAndTimeBankHours,applicableKPI,staffKpiFilterDTOS);
        sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    private Map<Long,List<DailyTimeBankEntry>> getDailyTimeBankEntryByStaffId(List<Long> staffIds,List<DailyTimeBankEntry> dailyTimeBankEntries){
        Map<Long, List<DailyTimeBankEntry>> dateTimeIntervalListMap = new HashMap<>();
        for (Long staffId : staffIds) {
            dateTimeIntervalListMap.put(staffId, dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> dailyTimeBankEntry.getStaffId().equals(staffId)).collect(Collectors.toList()));
        }
        return  dateTimeIntervalListMap;
    }
    private Map<Object, Double> calculateDataByKpiRepresentation(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Long> unitIds,List<StaffKpiFilterDTO> staffKpiFilterDTOS,Set<DayOfWeek> daysOfWeek){
        Map<Object, Double> staffDeltaHours;
        Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.groupingBy(StaffKpiFilterDTO::getUnitId, Collectors.toList()));
        Map<Long, Set<DateTimeInterval>> planningPeriodIntervel = getPlanningPeriodIntervals(unitIds, dateTimeIntervals.get(0).getStartDate(),dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndDate());
        List<DailyTimeBankEntry> employmentAndDailyTimeBank = getDailyTimeBankEntryByDate(staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getEmployment().stream().map(EmploymentWithCtaDetailsDTO::getId)).collect(Collectors.toList()), dateTimeIntervals.get(0).getStartLocalDate(),dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate(), daysOfWeek);
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffDeltaHours = getstaffPlannedAndTimeBankHoursPerStaff(staffIds, dateTimeIntervals, staffKpiFilterDTOS, employmentAndDailyTimeBank);
                break;
            case REPRESENT_TOTAL_DATA:
                staffDeltaHours = getstaffPlannedAndTimeBankHoursOfUnits(dateTimeIntervals, unitIds,  unitAndStaffKpiFilterMap, employmentAndDailyTimeBank);
                break;
            default:
                staffDeltaHours = getstaffPlannedAndTimeBankHoursByInterval(dateTimeIntervals, unitIds, unitAndStaffKpiFilterMap, employmentAndDailyTimeBank ,applicableKPI.getFrequencyType());
                break;
        }
        return staffDeltaHours;

    }

    private Map<Object, Double> getstaffPlannedAndTimeBankHoursPerStaff(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<StaffKpiFilterDTO> staffKpiFilterDTOS, List<DailyTimeBankEntry> dailyTimeBankEntries) {
        Map<Object, Double> staffIdAndDeltaTimeBankMap = new HashedMap();

         dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByStaffIdsAndBetweenDates(staffIds, dateTimeIntervals.get(0).getStartLocalDate(), dateTimeIntervals.get(0).getEndLocalDate().minusDays(1));

        Map<Long, List<DailyTimeBankEntry>> staffAndDailyTimeBankMap;
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        Map<Long, StaffKpiFilterDTO> staffAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, v -> v));
        Map<Long, List<DailyTimeBankEntry>> staffIdAndDailyTimeBankMap = getDailyTimeBankEntryByStaffId(staffIds, dailyTimeBankEntries);

            for (Long staffId : staffIds) {
                staffAndDailyTimeBankMap = staffIdAndDailyTimeBankMap.getOrDefault(staffId, new ArrayList<>()).stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
                Long totalDeltaTimeBankOfUnit = 0l;
                StaffKpiFilterDTO staffKpiFilterDTO = staffAndStaffKpiFilterMap.get(staffId);
                DateTimeInterval dateTimeInterval = new DateTimeInterval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate()));
                totalDeltaTimeBankOfUnit = getTotalTimeBank(staffAndDailyTimeBankMap, dateTimeInterval, totalDeltaTimeBankOfUnit, staffKpiFilterDTO);
                staffIdAndDeltaTimeBankMap.put(staffIdAndNameMap.get(staffId), DateUtils.getHoursByMinutes(totalDeltaTimeBankOfUnit.doubleValue()));
            }


        return staffIdAndDeltaTimeBankMap;
    }

    private Map<Object, Double> getstaffPlannedAndTimeBankHoursOfUnits(List<DateTimeInterval> dateTimeIntervals, List<Long> unitIds,  Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap, List<DailyTimeBankEntry> employmentAndDailyTimeBank) {
        Map<Object, Double> staffIdAndDeltaTimeBankMap = new HashedMap();
        Map<Long, List<DailyTimeBankEntry>> longListMap;
        longListMap=employmentAndDailyTimeBank.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
        Long totalTimeBankOfUnit = 0l;
        for (Long unitId : unitIds) {
            for (StaffKpiFilterDTO staffKpiFilterDTO : unitAndStaffKpiFilterMap.get(unitId)) {
                DateTimeInterval dateTimeInterval=new DateTimeInterval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate()));
                totalTimeBankOfUnit = getTotalTimeBank(longListMap, dateTimeInterval, totalTimeBankOfUnit, staffKpiFilterDTO);
            }
        }
        staffIdAndDeltaTimeBankMap.put(getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())), DateUtils.getHoursByMinutes(totalTimeBankOfUnit.doubleValue()));
       return  staffIdAndDeltaTimeBankMap;
    }

    private Map<Object, Double> getstaffPlannedAndTimeBankHoursByInterval(List<DateTimeInterval> dateTimeIntervals, List<Long> unitIds, Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap, List<DailyTimeBankEntry> employmentAndDailyTimeBank, DurationType frequencyType) {
        Map<Object, Double> staffIdAndDeltaTimeBankMap = new HashedMap();
        Map<Long, List<DailyTimeBankEntry>> longListMap;
        Long totalTimeBankOfUnit = 0l;
        Map<DateTimeInterval,List<DailyTimeBankEntry>> dateTimeIntervalListMap1 = getDailyTimeBankEntryByInterval(employmentAndDailyTimeBank,dateTimeIntervals);
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            longListMap=dateTimeIntervalListMap1.getOrDefault(dateTimeInterval,new ArrayList<>()).stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
            for (Long unitId : unitIds) {

                if(isCollectionNotEmpty(unitAndStaffKpiFilterMap.get(unitId))){
                    for (StaffKpiFilterDTO staffKpiFilterDTO : unitAndStaffKpiFilterMap.get(unitId)) {
                        totalTimeBankOfUnit = getTotalTimeBank(longListMap, dateTimeInterval, totalTimeBankOfUnit, staffKpiFilterDTO);
                    }
                }

            }
            staffIdAndDeltaTimeBankMap.put(DurationType.DAYS.equals(frequencyType) ? getStartDateTimeintervalString(dateTimeInterval) : getDateTimeintervalString(dateTimeInterval), DateUtils.getHoursByMinutes(totalTimeBankOfUnit.doubleValue()));
        }
        return staffIdAndDeltaTimeBankMap;
    }


    private Map<DateTimeInterval,List<DailyTimeBankEntry>> getDailyTimeBankEntryByInterval(List<DailyTimeBankEntry> dailyTimeBankEntries, List<DateTimeInterval> dateTimeIntervals){
        Map<DateTimeInterval, List<DailyTimeBankEntry>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, dailyTimeBankEntries.stream().filter(dailyTimeBankEntry ->   dateTimeInterval.contains(asDate(dailyTimeBankEntry.getDate()))).collect(Collectors.toList()));
        }
        return  dateTimeIntervalListMap;
    }


    private Long getTotalTimeBank(Map<Long, List<DailyTimeBankEntry>> longListMap, DateTimeInterval dateTimeInterval, Long totalDeltaTimeBankOfUnit, StaffKpiFilterDTO staffKpiFilterDTO) {
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(staffKpiFilterDTO.getUnitId());
        for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = longListMap.getOrDefault(employmentWithCtaDetailsDTO.getId(), new ArrayList<>());
            int timeBankOfInterval = timeBankCalculationService.calculateDeltaTimeBankForInterval(planningPeriodInterval, new Interval(DateUtils.getLongFromLocalDate(dateTimeInterval.getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeInterval.getEndLocalDate())), employmentWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
            int calculatedTimeBank = dailyTimeBankEntries.stream().mapToInt(DailyTimeBankEntry::getDeltaTimeBankMinutes).sum();
            int totalTimeBank = calculatedTimeBank - timeBankOfInterval;
            totalDeltaTimeBankOfUnit += totalTimeBank;
        }
        return totalDeltaTimeBankOfUnit;
    }

    private List<CommonKpiDataUnit> getKpiDataUnits(Map<Object, Double> staffPlannedHours,Map<Object, Double> staffPlannedHoursAndTimeBankHours, ApplicableKPI applicableKPI, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        List<CommonKpiDataUnit> kpiDataUnits = newArrayList();
        if(REPRESENT_PER_STAFF.equals(applicableKPI.getKpiRepresentation())) {
            Map<String, Long> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getFullName, StaffKpiFilterDTO::getId));
            List<BarLineChartKPiDateUnit> barLineChartKPiDateUnits = new ArrayList<>();
            staffPlannedHoursAndTimeBankHours.entrySet().forEach(entry -> barLineChartKPiDateUnits.add(new BarLineChartKPiDateUnit(entry.getKey().toString(), (Long) staffIdAndNameMap.get(entry.getKey()), entry.getValue(), staffPlannedHours.get(staffIdAndNameMap.get(entry.getKey())))));
            kpiDataUnits.addAll(barLineChartKPiDateUnits);
        }else {
            kpiDataUnits = staffPlannedHoursAndTimeBankHours.entrySet().stream().map(entry -> new BarLineChartKPiDateUnit(entry.getKey().toString(), entry.getValue(), staffPlannedHours.get(entry.getKey()))).collect(Collectors.toList());
        }
        return kpiDataUnits;
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursVsTimeBankKpiStaffs(organizationId, filterBasedCriteria,null);
        return new BarLineChartKPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.BAR_YAXIS), new KPIAxisData(AppConstants.PLANNED_HOURS, AppConstants.LINE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursVsTimeBankKpiStaffs(organizationId, filterBasedCriteria ,applicableKPI);
        return new BarLineChartKPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF :AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.BAR_YAXIS), new KPIAxisData(AppConstants.PLANNED_HOURS, AppConstants.LINE_FIELD));
    }

    @Override
    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        return new KPISetResponseDTO();
    }


    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, ApplicableKPI applicableKPI) {
       return new TreeSet<>();
    }
}
