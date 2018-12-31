package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.BarLineChartKPiDateUnit;
import com.kairos.dto.activity.counter.chart.BasicChartKpiDateUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TimeBankKpiCalculationService implements  CounterService {
    private static final Logger logger = LoggerFactory.getLogger(TimeBankKpiCalculationService.class);
    @Inject
    private GenericIntegrationService genericIntegrationService;
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

    private Map<Long,Set<DateTimeInterval>> getPlanningPeriodIntervals(List<Long> unitIds, Date startDate, Date endDate,List<BigInteger> phaseIds) {
        Map<Long,Set<DateTimeInterval>> unitAndDateTimeIntervalMap=new HashMap<>();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdsAndBetweenDates(unitIds, startDate, endDate);
        Map<Long,List<PlanningPeriod>> unitAndPlanningPeriodMap=planningPeriods.stream().collect(Collectors.groupingBy(PlanningPeriod::getUnitId,Collectors.toList()));
        unitIds.forEach(unitId->{
            Set<DateTimeInterval> dateTimeIntervals;
            if(CollectionUtils.isNotEmpty(phaseIds)){
                dateTimeIntervals=unitAndPlanningPeriodMap.get(unitId).stream().filter(planningPeriod -> phaseIds.contains(planningPeriod.getCurrentPhaseId())).map(planningPeriod -> new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
            }else{
                dateTimeIntervals=unitAndPlanningPeriodMap.get(unitId).stream().map(planningPeriod -> new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
            }
            unitAndDateTimeIntervalMap.put(unitId,dateTimeIntervals);
        });
        return unitAndDateTimeIntervalMap;
    }

    private Map<Long,List<DailyTimeBankEntry>> getDailyTimeBankEntryByDate(List<Long> unitPositionIds, LocalDate startDate, LocalDate endDate, List<String> daysOfWeek) {
        List<DailyTimeBankEntry> dailyTimeBankEntries= timeBankRepository.findAllDailyTimeBankByIdsAndBetweenDates(unitPositionIds,DateUtils.asDate(startDate),DateUtils.asDate(endDate));
        List<LocalDate> localDates=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(daysOfWeek)){
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)){
                if(daysOfWeek.contains(date.getDayOfWeek().toString())){
                    localDates.add(date);
                }
            }
            dailyTimeBankEntries=dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> localDates.contains(dailyTimeBankEntry.getDate())).collect(Collectors.toList());
        }
        return dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getUnitPositionId,Collectors.toList()));
    }

    private List<CommonKpiDataUnit> getTimeBankForUnitKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, boolean kpi){
        List<CommonKpiDataUnit> basicChartKpiDateUnits=new ArrayList<>();
        List<BigInteger> phaseIds=filterBasedCriteria.containsKey(FilterType.PHASE)?KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.PHASE)):new ArrayList<>();
        List<String> daysOfWeek=filterBasedCriteria.containsKey(FilterType.DAYS_OF_WEEK)?filterBasedCriteria.get(FilterType.DAYS_OF_WEEK):Stream.of(DayOfWeek.values()).map(Enum::name).collect(Collectors.toList());
        List<Long> staffIds=(filterBasedCriteria.get(FilterType.STAFF_IDS) != null)? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)):new ArrayList<>();
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null) ? filterBasedCriteria.get(FilterType.TIME_INTERVAL): Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS)!=null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)):new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO=new StaffEmploymentTypeDTO(staffIds,unitIds,new ArrayList<>(),organizationId,filterDates.get(0).toString(),filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS=genericIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Long,List<StaffKpiFilterDTO>> unitAndStaffKpiFilterMap=staffKpiFilterDTOS.stream().collect(Collectors.groupingBy(k->k.getUnitId(),Collectors.toList()));
        Map<Long,Set<DateTimeInterval>> planningPeriodIntervel = getPlanningPeriodIntervals((CollectionUtils.isNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId)), DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(filterDates.get(1)),phaseIds);
        Map<Long,List<DailyTimeBankEntry>> unitPositionAndDailyTimeBank=getDailyTimeBankEntryByDate(staffKpiFilterDTOS.stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getUnitPosition().stream().map(unitPositionWithCtaDetailsDTO -> unitPositionWithCtaDetailsDTO.getId())).collect(Collectors.toList()),filterDates.get(0),filterDates.get(1),daysOfWeek);
        if(CollectionUtils.isEmpty(unitIds)){
            unitIds.add(organizationId);
        }
        unitIds.forEach(unitId->{
            Long totalTimeBankOfUnit=0l;
            String unitName=(unitAndStaffKpiFilterMap.get(unitId).isEmpty())?unitAndStaffKpiFilterMap.get(unitId).get(0).getUnitName():"";
            for (StaffKpiFilterDTO staffKpiFilterDTO : unitAndStaffKpiFilterMap.get(unitId)) {
                for (UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO : staffKpiFilterDTO.getUnitPosition()) {
                    List<DailyTimeBankEntry> dailyTimeBankEntries = unitPositionAndDailyTimeBank.getOrDefault(unitPositionWithCtaDetailsDTO.getId(),new ArrayList<>());
                    int timeBankOfInterval=timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervel.get(organizationId),new Interval(DateUtils.getLongFromLocalDate(filterDates.get(0)),DateUtils.getLongFromLocalDate(filterDates.get(1))),staffKpiFilterDTOS.get(0).getUnitPosition().get(0),false,dailyTimeBankEntries,false);
                    int calculatedTimeBank = dailyTimeBankEntries.stream().mapToInt(value -> value.getTotalTimeBankMin()).sum();
                    int totalTimeBank = calculatedTimeBank - timeBankOfInterval;
                    logger.info(staffKpiFilterDTO.getFirstName()+ "  "+ totalTimeBank);
                    totalTimeBankOfUnit+=totalTimeBank;
                }
            }
            basicChartKpiDateUnits.add(new BasicChartKpiDateUnit(unitName,unitId,DateUtils.getHoursByMinutes(totalTimeBankOfUnit)));
        });
        return basicChartKpiDateUnits;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList= getTimeBankForUnitKpiData(organizationId,filterBasedCriteria,true);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, AppConstants.XAXIS,AppConstants.YAXIS);
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList= getTimeBankForUnitKpiData(organizationId,filterBasedCriteria,false);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, AppConstants.XAXIS,AppConstants.YAXIS);
    }
}
