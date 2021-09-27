package com.kairos.service;

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
import com.kairos.dto.activity.kpi.KPISetResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.ApplicableKPI;
import com.kairos.persistence.model.DailyTimeBankEntry;
import com.kairos.persistence.model.FibonacciKPICalculation;
import com.kairos.persistence.model.KPI;
import com.kairos.persistence.repository.counter.*;
import com.kairos.utils.FibonacciCalculationUtil;
import com.kairos.utils.KPIUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.enums.kpi.KPIRepresentation.*;

@Service
public class TimeBankKpiCalculationService implements CounterService {
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CounterHelperRepository counterHelperRepository;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    public CounterHelperService counterHelperService;

    private List<DailyTimeBankEntry> getDailyTimeBankEntryByDate(List<Long> employmentIds, LocalDate startDate, LocalDate endDate, Set<DayOfWeek> daysOfWeeks) {
        List<DailyTimeBankEntry> dailyTimeBankEntries = counterHelperRepository.findAllDailyTimeBankByIdsAndBetweenDates(employmentIds, DateUtils.asDate(startDate), DateUtils.asDate(endDate));
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

    private Map<DateTimeInterval,List<DailyTimeBankEntry>> getDailyTimeBankEntryByInterval(List<DailyTimeBankEntry> dailyTimeBankEntries, List<DateTimeInterval> dateTimeIntervals){
        Map<DateTimeInterval, List<DailyTimeBankEntry>> dateTimeIntervalListMap = new HashMap<>();
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
            dateTimeIntervalListMap.put(dateTimeInterval, dailyTimeBankEntries.stream().filter(dailyTimeBankEntry ->   dateTimeInterval.contains(DateUtils.asDate(dailyTimeBankEntry.getDate()))).collect(Collectors.toList()));
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
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = (List<Long>)filterCriteria[0];
        List<LocalDate> filterDates = (List<LocalDate>)filterCriteria[1];
        List<Long> unitIds = (List<Long>)filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>)filterCriteria[3];
        Set<DayOfWeek> daysOfWeeks = (Set<DayOfWeek>)filterCriteria[4];
        Object[] kpiData = counterHelperService.getKPIdata(new HashMap(),applicableKPI,filterDates,staffIds,employmentTypeIds,unitIds,organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>)kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>)kpiData[0];
        if (CollectionUtils.isEmpty(unitIds)) {
            unitIds.add(organizationId);
        }
        staffIds = (List<Long>) kpiData[2];
        Map<Object, List<ClusteredBarChartKpiDataUnit>> objectDoubleMap = calculateDataByKpiRepresentation(staffIds, dateTimeIntervals, applicableKPI,unitIds,staffKpiFilterDTOS,daysOfWeeks);
        KPIUtils.getKpiDataUnits(objectDoubleMap, kpiDataUnits, applicableKPI, staffKpiFilterDTOS);
        KPIUtils.sortKpiDataByDateTimeInterval(kpiDataUnits);
        return kpiDataUnits;
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getTimeBankForUnitKpiData(organizationId, filterBasedCriteria,null);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi,ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getTimeBankForUnitKpiData(organizationId, filterBasedCriteria,applicableKPI);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), XAxisConfig.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(REPRESENT_PER_STAFF) ? AppConstants.STAFF :AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> calculateDataByKpiRepresentation(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, ApplicableKPI applicableKPI, List<Long> unitIds,List<StaffKpiFilterDTO> staffKpiFilterDTOS,Set<DayOfWeek> daysOfWeek){
        List<ClusteredBarChartKpiDataUnit> subClusteredBarValue = new ArrayList<>();
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap ;
        Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.groupingBy(StaffKpiFilterDTO::getUnitId, Collectors.toList()));
        List<DailyTimeBankEntry> employmentAndDailyTimeBank = getDailyTimeBankEntryByDate(staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getEmployment().stream().map(EmploymentWithCtaDetailsDTO::getId)).collect(Collectors.toList()), dateTimeIntervals.get(0).getStartLocalDate(),dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate(), daysOfWeek);
        switch (applicableKPI.getKpiRepresentation()) {
            case REPRESENT_PER_STAFF:
                staffIdAndTimeBankMap = getTimebankPerStaff(staffIds, dateTimeIntervals, staffKpiFilterDTOS, employmentAndDailyTimeBank);
                break;
            case REPRESENT_TOTAL_DATA:
                staffIdAndTimeBankMap = getTotalTimeBankOfUnits(dateTimeIntervals, unitIds, subClusteredBarValue, unitAndStaffKpiFilterMap, employmentAndDailyTimeBank);
                break;
            default:
                staffIdAndTimeBankMap = getTimeBankByInterval(dateTimeIntervals, unitIds, subClusteredBarValue, unitAndStaffKpiFilterMap, employmentAndDailyTimeBank ,applicableKPI.getFrequencyType());
                break;
        }
        return staffIdAndTimeBankMap;

    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTimebankPerStaff(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, List<StaffKpiFilterDTO> staffKpiFilterDTOS, List<DailyTimeBankEntry> employmentAndDailyTimeBank) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap = new HashedMap();
        Map<Long, List<DailyTimeBankEntry>> staffAndDailyTimeBankMap;
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        Map<Long, StaffKpiFilterDTO> staffAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, v->v));
        Map<Long,List<DailyTimeBankEntry>> staffIdAndDailyTimeBankMap = getDailyTimeBankEntryByStaffId(staffIds,employmentAndDailyTimeBank);
        for (Long staffId : staffIds) {
            staffAndDailyTimeBankMap = staffIdAndDailyTimeBankMap.getOrDefault(staffId, new ArrayList<>()).stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
            Long totalTimeBankOfUnit = 0l;
            StaffKpiFilterDTO staffKpiFilterDTO = staffAndStaffKpiFilterMap.get(staffId);
            DateTimeInterval dateTimeInterval = new DateTimeInterval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate()));
            totalTimeBankOfUnit = getTotalTimeBank(staffAndDailyTimeBankMap, dateTimeInterval, totalTimeBankOfUnit, staffKpiFilterDTO);
            staffIdAndTimeBankMap.put(staffId, Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(staffId), null, DateUtils.getHoursByMinutes(totalTimeBankOfUnit))));
        }
        return staffIdAndTimeBankMap;
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTotalTimeBankOfUnits(List<DateTimeInterval> dateTimeIntervals, List<Long> unitIds, List<ClusteredBarChartKpiDataUnit> subClusteredBarValue, Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap,  List<DailyTimeBankEntry> employmentAndDailyTimeBank) {
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap = new HashedMap();
        Map<Long, List<DailyTimeBankEntry>> longListMap;
        longListMap=employmentAndDailyTimeBank.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
        for (Long unitId : unitIds) {
            Long totalTimeBankOfUnit = 0l;
            String unitName = (!unitAndStaffKpiFilterMap.get(unitId).isEmpty()) ? unitAndStaffKpiFilterMap.get(unitId).get(0).getUnitName() : "";
            for (StaffKpiFilterDTO staffKpiFilterDTO : unitAndStaffKpiFilterMap.get(unitId)) {
                DateTimeInterval dateTimeInterval=new DateTimeInterval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndLocalDate()));
                totalTimeBankOfUnit = getTotalTimeBank(longListMap, dateTimeInterval, totalTimeBankOfUnit, staffKpiFilterDTO);
            }
            subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(unitName, null, DateUtils.getHoursByMinutes(totalTimeBankOfUnit)));
        }
        staffIdAndTimeBankMap.put(DateUtils.getDateTimeintervalString(new DateTimeInterval(dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndDate())),subClusteredBarValue );
        return KPIUtils.verifyKPIResponseListData(staffIdAndTimeBankMap) ? staffIdAndTimeBankMap : new HashMap<>();
    }

    private Map<Object, List<ClusteredBarChartKpiDataUnit>> getTimeBankByInterval(List<DateTimeInterval> dateTimeIntervals, List<Long> unitIds, List<ClusteredBarChartKpiDataUnit> subClusteredBarValue, Map<Long, List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap,  List<DailyTimeBankEntry> employmentAndDailyTimeBank, DurationType frequencyType) {
        Map<Long, List<DailyTimeBankEntry>> longListMap;
        Map<Object, List<ClusteredBarChartKpiDataUnit>> staffIdAndTimeBankMap = new HashedMap();
        Map<DateTimeInterval,List<DailyTimeBankEntry>> dateTimeIntervalListMap1 = getDailyTimeBankEntryByInterval(employmentAndDailyTimeBank,dateTimeIntervals);
        for (DateTimeInterval dateTimeInterval : dateTimeIntervals) {
          longListMap=dateTimeIntervalListMap1.getOrDefault(dateTimeInterval,new ArrayList<>()).stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
            for (Long unitId : unitIds) {
                Long totalTimeBankOfUnit = 0l;
                String unitName = (ObjectUtils.isCollectionNotEmpty(unitAndStaffKpiFilterMap.get(unitId))) ? unitAndStaffKpiFilterMap.get(unitId).get(0).getUnitName() : "";
                if(ObjectUtils.isCollectionNotEmpty(unitAndStaffKpiFilterMap.get(unitId))){
                for (StaffKpiFilterDTO staffKpiFilterDTO : unitAndStaffKpiFilterMap.get(unitId)) {
                    totalTimeBankOfUnit = getTotalTimeBank(longListMap, dateTimeInterval, totalTimeBankOfUnit, staffKpiFilterDTO);
                }
                }
                subClusteredBarValue.add(new ClusteredBarChartKpiDataUnit(unitName, null, DateUtils.getHoursByMinutes(totalTimeBankOfUnit)));
            }
            staffIdAndTimeBankMap.put(DurationType.DAYS.equals(frequencyType) ? DateUtils.getStartDateTimeintervalString(dateTimeInterval) : DateUtils.getDateTimeintervalString(dateTimeInterval), subClusteredBarValue);
        }
        return staffIdAndTimeBankMap;
    }

    private Long getTotalTimeBank(Map<Long, List<DailyTimeBankEntry>> longListMap, DateTimeInterval dateTimeInterval, Long totalTimeBankOfUnit, StaffKpiFilterDTO staffKpiFilterDTO) {
        DateTimeInterval planningPeriodInterval = counterHelperRepository.getPlanningPeriodIntervalByUnitId(staffKpiFilterDTO.getUnitId());
        for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = longListMap.getOrDefault(employmentWithCtaDetailsDTO.getId(), new ArrayList<>());
            int timeBankOfInterval = (int) timeBankService.calculateDeltaTimeBankForInterval(planningPeriodInterval, new Interval(DateUtils.getLongFromLocalDate(dateTimeInterval.getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeInterval.getEndLocalDate())), employmentWithCtaDetailsDTO, new HashSet<>(), dailyTimeBankEntries, false)[0];
            totalTimeBankOfUnit += timeBankOfInterval;
        }
        return totalTimeBankOfUnit;
    }




    public KPISetResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        KPISetResponseDTO kpiSetResponseDTO = new KPISetResponseDTO();
        Object[] filterCriteria = counterHelperService.getDataByFilterCriteria(filterBasedCriteria);
        List<Long> staffIds = new ArrayList<>();
        List<LocalDate> filterDates = (List<LocalDate>) filterCriteria[1];
        List<Long> unitIds = CollectionUtils.isEmpty((List<Long>) filterCriteria[2]) ? ObjectUtils.newArrayList(organizationId) : (List<Long>) filterCriteria[2];
        List<Long> employmentTypeIds = (List<Long>) filterCriteria[3];
        Set<DayOfWeek> daysOfWeeks = (Set<DayOfWeek>) filterCriteria[4];
        Object[] kpiData = counterHelperService.getKPIdata(new HashMap(),applicableKPI, filterDates, staffIds, employmentTypeIds, unitIds, organizationId);
        List<DateTimeInterval> dateTimeIntervals = (List<DateTimeInterval>) kpiData[1];
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = (List<StaffKpiFilterDTO>) kpiData[0];
        staffIds = (List<Long>) kpiData[2];
        List<DailyTimeBankEntry> employmentAndDailyTimeBank = getDailyTimeBankEntryByDate(staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getEmployment().stream().map(EmploymentWithCtaDetailsDTO::getId)).collect(Collectors.toList()), dateTimeIntervals.get(0).getStartLocalDate(), dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate(), daysOfWeeks);
        Map<Long, List<DailyTimeBankEntry>> staffAndDailyTimeBankMap;
        Map<Long, StaffKpiFilterDTO> staffAndStaffKpiFilterMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, v -> v));
        Map<Long, List<DailyTimeBankEntry>> staffIdAndDailyTimeBankMap = getDailyTimeBankEntryByStaffId(staffIds, employmentAndDailyTimeBank);
        Map<Long, Double> kpiAndStaffIdMap = getTimebankDetailsByStaffs(staffIds, dateTimeIntervals, staffAndStaffKpiFilterMap, staffIdAndDailyTimeBankMap);
        kpiSetResponseDTO.setKpiName(kpi.getTitle());
        kpiSetResponseDTO.setKpiId(kpi.getId());
        kpiSetResponseDTO.setStaffKPIValue(kpiAndStaffIdMap);
        return kpiSetResponseDTO;
    }

    private Map<Long, Double> getTimebankDetailsByStaffs(List<Long> staffIds, List<DateTimeInterval> dateTimeIntervals, Map<Long, StaffKpiFilterDTO> staffAndStaffKpiFilterMap, Map<Long, List<DailyTimeBankEntry>> staffIdAndDailyTimeBankMap) {
        Map<Long, List<DailyTimeBankEntry>> staffAndDailyTimeBankMap;
        Map<Long, Double> kpiAndStaffIdMap = new HashMap<>();
        for (Long staffId : staffIds) {
            staffAndDailyTimeBankMap = staffIdAndDailyTimeBankMap.getOrDefault(staffId, new ArrayList<>()).stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId, Collectors.toList()));
            Long totalTimeBankOfUnit = 0l;
            StaffKpiFilterDTO staffKpiFilterDTO = staffAndStaffKpiFilterMap.get(staffId);
            DateTimeInterval dateTimeInterval = new DateTimeInterval(DateUtils.getLongFromLocalDate(dateTimeIntervals.get(0).getStartLocalDate()), DateUtils.getLongFromLocalDate(dateTimeIntervals.get(dateTimeIntervals.size() - 1).getEndLocalDate()));
            totalTimeBankOfUnit = getTotalTimeBank(staffAndDailyTimeBankMap, dateTimeInterval, totalTimeBankOfUnit, staffKpiFilterDTO);
            kpiAndStaffIdMap.put(staffId, DateUtils.getHoursByMinutes(totalTimeBankOfUnit.doubleValue()));
        }
        return kpiAndStaffIdMap;
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, KPI kpi,ApplicableKPI applicableKPI) {
        KPISetResponseDTO  kpiSetResponseDTO = getCalculatedDataOfKPI(filterBasedCriteria, organizationId,new KPI(),applicableKPI);
        Map<Long, Double> kpiAndStaffIdMap = kpiSetResponseDTO.getStaffKPIValue();
        return FibonacciCalculationUtil.getFibonacciCalculation(kpiAndStaffIdMap.entrySet().stream().collect(Collectors.toMap(k->k.getKey(), v->v.getValue().intValue())),sortingOrder);
    }

}
