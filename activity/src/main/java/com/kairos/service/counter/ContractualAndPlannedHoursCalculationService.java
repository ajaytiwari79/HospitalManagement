package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.BarLineChartKPiDateUnit;
import com.kairos.dto.activity.counter.chart.BasicChartKpiDateUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.BarLineChartKPIRepresentationData;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.distinctByKey;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@Service
public class ContractualAndPlannedHoursCalculationService implements CounterService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ContractualAndPlannedHoursCalculationService.class);

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

    private Set<DateTimeInterval> getPlanningPeriodIntervals(List<Long> unitIds, Date startDate, Date endDate) {
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdsAndBetweenDates(unitIds, startDate, endDate);
        Set<DateTimeInterval> dateTimeIntervals = planningPeriods.stream().map(planningPeriod -> new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
        return dateTimeIntervals;
    }

    private List<CommonKpiDataUnit> getContractualAndPlannedHoursKpiDate(Long organizationId, Map<FilterType, List> filterBasedCriteria) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        List<Long> staffIds = filterBasedCriteria.containsKey(FilterType.STAFF_IDS) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = (filterBasedCriteria.containsKey(FilterType.TIME_INTERVAL)) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) ? KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
        List<Long> unitIds = filterBasedCriteria.containsKey(FilterType.UNIT_IDS) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentType = filterBasedCriteria.containsKey(FilterType.EMPLOYMENT_TYPE) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentType, organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().filter(distinctByKey(staff -> staff.getId())).collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        Set<DateTimeInterval> planningPeriodIntervel = getPlanningPeriodIntervals((CollectionUtils.isNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId)), DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(filterDates.get(1)));
        Map<Long, Double> calculateContractualForInterval = calculateContractualHoursForInterval(planningPeriodIntervel, new Interval(DateUtils.getLongFromLocalDate(filterDates.get(0)), DateUtils.getLongFromLocalDate(filterDates.get(1))), staffKpiFilterDTOS);

       // List<CommonKpiDataUnit> commonKpiDataUnits = shiftMongoRepository.findShiftsByKpiFilters(staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), new ArrayList<>(), new HashSet<>(), DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(DateUtils.getEndOfDayFromLocalDate(filterDates.get(1))));
//        Map<Long, Double> staffAndPlannedHoursMap = commonKpiDataUnits.stream().collect(Collectors.toMap(k -> k.getRefId().longValue(), v -> DateUtils.getHoursByMinutes(((BasicChartKpiDateUnit) v).getValue())));
//        List<CommonKpiDataUnit> kpiDataUnits = calculateContractualForInterval.entrySet().stream().map(entry -> new BarLineChartKPiDateUnit(staffIdAndNameMap.get(entry.getKey()), entry.getKey(), entry.getValue(), staffAndPlannedHoursMap.get(entry.getKey()))).collect(Collectors.toList());
        return kpiDataUnits;
    }

    /**
     * @param planningPeriodIntervals
     * @param interval
     * @param staffKpiFilterDTOS
     * @return
     */
    public Map<Long, Double> calculateContractualHoursForInterval(Set<DateTimeInterval> planningPeriodIntervals, Interval interval, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        Map<Long, Double> staffAndContractualHourMap = new HashMap<>();
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            Long contractualMinutes = 0l;
            for (EmploymentWithCtaDetailsDTO positionWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                int totalWeeklyMinutes = 0;
                interval = timeBankCalculationService.getIntervalByDateForAdvanceView(positionWithCtaDetailsDTO, interval);
                if (interval != null) {
                    DateTime startDate = interval.getStart();
                    while (startDate.isBefore(interval.getEnd())) {
                        contractualMinutes += timeBankCalculationService.getContractualMinutesByDate(planningPeriodIntervals, DateUtils.asLocalDate(startDate), positionWithCtaDetailsDTO.getEmploymentLines());
                        startDate = startDate.plusDays(1);
                    }
                }
            }
            staffAndContractualHourMap.put(staffKpiFilterDTO.getId(), DateUtils.getHoursByMinutes(contractualMinutes.doubleValue()));

        }
        return staffAndContractualHourMap;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getContractualAndPlannedHoursKpiDate(organizationId, filterBasedCriteria);
        return new BarLineChartKPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.CONTRACTUAL_HOURS, AppConstants.BAR_YAXIS), new KPIAxisData(AppConstants.PLANNED_HOURS, AppConstants.LINE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi,ApplicableKPI applicableKPI) {
        List<CommonKpiDataUnit> dataList = getContractualAndPlannedHoursKpiDate(organizationId, filterBasedCriteria);
        return new BarLineChartKPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(applicableKPI.getKpiRepresentation().equals(KPIRepresentation.REPRESENT_PER_STAFF) ? AppConstants.STAFF :AppConstants.DATE, AppConstants.LABEL), new KPIAxisData(AppConstants.CONTRACTUAL_HOURS, AppConstants.BAR_YAXIS), new KPIAxisData(AppConstants.PLANNED_HOURS, AppConstants.LINE_FIELD));

    }

    @Override
    public Map<Long,Number>  getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
        return new HashMap<>();//getContractualAndPlannedHoursKpiDate(organizationId, filterBasedCriteria).stream().;
    }


}
