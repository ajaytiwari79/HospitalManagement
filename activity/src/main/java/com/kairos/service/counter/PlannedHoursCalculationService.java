package com.kairos.service.counter;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.BasicChartKpiDateUnit;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.counter.FibonacciKPICalculation;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.KPIUtils.getLongValue;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.utils.Fibonacci.FibonacciCalculationUtil.getFibonacciCalculation;

@Service
public class PlannedHoursCalculationService implements CounterService {
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    private List<CommonKpiDataUnit> getPlannedHoursKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, boolean kpi) {
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        Set<BigInteger> timeTypeIds = new HashSet<>();
        List<Long> staffIds = (filterBasedCriteria.get(FilterType.STAFF_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)) : new ArrayList<>();
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) != null) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) ? KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)) : new ArrayList();
        List<String> shiftActivityStatus = (filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) != null) ? filterBasedCriteria.get(FilterType.ACTIVITY_STATUS) : new ArrayList<>();
        List<Long> employmentType = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE) != null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)) : new ArrayList();
        if (filterBasedCriteria.containsKey(FilterType.TIME_TYPE) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_TYPE))) {
            if (filterBasedCriteria.get(FilterType.TIME_TYPE).get(0) instanceof String) {
                timeTypeIds = timeTypeMongoRepository.findTimeTypeIdssByTimeTypeEnum(filterBasedCriteria.get(FilterType.TIME_TYPE));
            } else {
                timeTypeIds = timeTypeMongoRepository.findAllTimeTypeIdsByTimeTypeIds(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
                timeTypeIds.addAll(KPIUtils.getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
            }
        }
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, unitIds, employmentType, organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().filter(distinctByKey(staff -> staff.getId())).collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        List<CommonKpiDataUnit> basicChartKpiDateUnits = shiftMongoRepository.findShiftsByKpiFilters(staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), shiftActivityStatus, timeTypeIds, DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(DateUtils.getEndOfDayFromLocalDate(filterDates.get(1))));
        basicChartKpiDateUnits.forEach(kpiData -> {
            kpiData.setLabel(staffIdAndNameMap.get(kpiData.getRefId()));
            kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(kpiData.getLabel(),((BasicChartKpiDateUnit) kpiData).getValue(), Arrays.asList(new ClusteredBarChartKpiDataUnit(kpiData.getLabel(), DateUtils.getHoursByMinutes(((BasicChartKpiDateUnit) kpiData).getValue()))),kpiData.getRefId().longValue()));
        });
        return kpiDataUnits;
    }


    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, true);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getPlannedHoursKpiData(organizationId, filterBasedCriteria, false);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF, AppConstants.LABEL), new KPIAxisData(AppConstants.HOURS, AppConstants.VALUE_FIELD));
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder) {
        List<Long> staffIds = getLongValue(filterBasedCriteria.getOrDefault(FilterType.STAFF_IDS, new ArrayList<>()));
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null)&& isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))?KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)): Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
        List<Long> unitIds = newArrayList(organizationId);
        List<String> shiftActivityStatus = new ArrayList<>();
        Set<BigInteger> timeTypeIds = new HashSet<>();
        List<CommonKpiDataUnit> basicChartKpiDateUnits = shiftMongoRepository.findShiftsByKpiFilters(staffIds, isCollectionNotEmpty(unitIds) ? unitIds : Arrays.asList(organizationId), shiftActivityStatus, timeTypeIds, DateUtils.asDate(filterDates.get(0)), DateUtils.asDate(DateUtils.getEndOfDayFromLocalDate(filterDates.get(1))));
        Map<Long,Integer> staffAndPlannedMinutesMap = new HashMap<>(basicChartKpiDateUnits.size());
        basicChartKpiDateUnits.forEach(kpiData -> {
            staffAndPlannedMinutesMap.put((Long) kpiData.getRefId(),(int)((BasicChartKpiDateUnit)kpiData).getValue());
        });
        return getFibonacciCalculation(staffAndPlannedMinutesMap,sortingOrder);
    }
}
