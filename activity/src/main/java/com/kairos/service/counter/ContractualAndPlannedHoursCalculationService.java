package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.KpiDataUnit;
import com.kairos.dto.activity.counter.data.RawRepresentationData;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractualAndPlannedHoursCalculationService implements CounterService {
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

    private Set<DateTimeInterval> getPlanningPeriodIntervals(List<Long> unitIds, Date startDate, Date endDate) {
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdsAndBetweenDates(unitIds, startDate, endDate);
        Set<DateTimeInterval> dateTimeIntervals = planningPeriods.stream().map(planningPeriod -> new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()))).collect(Collectors.toSet());
        return dateTimeIntervals;
    }

    private List<KpiDataUnit> getContractualAndPlannedHoursKpiDate(Long organizationId, Map<FilterType, List> filterBasedCriteria) {
        List<Long> staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null) ? getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) != null) ? filterBasedCriteria.get(FilterType.TIME_INTERVAL) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<Long> employmentType = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) ? getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentType, organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = genericIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        Set<DateTimeInterval> planningPeriodIntervel = getPlanningPeriodIntervals((CollectionUtils.isNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId)), DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(filterDates.get(1)));
        Map<Long,Long> calculateTimeBankForInterval=calculateTimeBankForInterval(planningPeriodIntervel,new Interval(DateUtils.getLongFromLocalDate(filterDates.get(0)),DateUtils.getLongFromLocalDate(filterDates.get(1))),staffKpiFilterDTOS);
        List<KpiDataUnit> kpiDataUnits = shiftMongoRepository.findShiftsByKpiFilters(staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), new ArrayList<>(), new HashSet<>(), DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(filterDates.get(1)));
        kpiDataUnits.forEach(kpiData -> {
            kpiData.setLabel(staffIdAndNameMap.get(kpiData.getRefId()));
            kpiData.setValue(DateUtils.getHoursByMinutes(kpiData.getValue()));
        });
        return kpiDataUnits;
    }

    /**
     *
     * @param planningPeriodIntervals
     * @param interval
     * @param staffKpiFilterDTOS
     * @return
     */
    public Map<Long,Long> calculateTimeBankForInterval(Set<DateTimeInterval> planningPeriodIntervals, Interval interval, List<StaffKpiFilterDTO> staffKpiFilterDTOS) {
        Map<Long,Long> staffAndContractualHourMap=new HashMap<>();
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            long contractualMinutes = 0;
            for (UnitPositionWithCtaDetailsDTO positionWithCtaDetailsDTO : staffKpiFilterDTO.getUnitPositionWithCtaDetailsDTOS()) {
                int totalWeeklyMinutes = 0;
                interval = timeBankCalculationService.getIntervalByDateForAdvanceView(positionWithCtaDetailsDTO, interval);
                contractualMinutes = 0;
                if (interval != null) {
                    DateTime startDate = interval.getStart();
                    while (startDate.isBefore(interval.getEnd())) {
                        contractualMinutes += timeBankCalculationService.getContractualAndTimeBankByPlanningPeriod(planningPeriodIntervals, DateUtils.asLocalDate(startDate), totalWeeklyMinutes, 0, true, positionWithCtaDetailsDTO.getPositionLines());
                        startDate = startDate.plusDays(1);
                    }
                }
            }
            staffAndContractualHourMap.put(staffKpiFilterDTO.getId(),contractualMinutes);

        }
        return staffAndContractualHourMap;
    }


    @Override
    public RawRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        return null;
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<KpiDataUnit> dataList = getContractualAndPlannedHoursKpiDate(organizationId,filterBasedCriteria);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, AppConstants.XAXIS,AppConstants.YAXIS);
    }

    private List<Long> getLongValue(List<Object> objects) {
        return objects.stream().map(o -> ((Integer) o).longValue()).collect(Collectors.toList());
    }

}
