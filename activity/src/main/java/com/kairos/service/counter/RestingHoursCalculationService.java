package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.TimeTypeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestingHoursCalculationService implements CounterService {
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private RepresentationService representationService;
    @Inject
    private ActivityService activityService;
    @Inject
    private GenericIntegrationService genericIntegrationService;

    public double getTotalRestingHours(List<Shift> shifts, long initTs, long endTs) {
        //all shifts should be sorted on startDate
        DateTimeInterval dateTimeInterval = new DateTimeInterval(initTs, endTs);
        long totalrestingMinutes = dateTimeInterval.getMilliSeconds();
        for (Shift shift : shifts) {
            DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
            if (dateTimeInterval.overlaps(shiftInterval)) {
                totalrestingMinutes -= dateTimeInterval.overlap(shiftInterval).getMilliSeconds();
            }
        }
        return DateUtils.getHoursFromTotalMilliSeconds(totalrestingMinutes);
    }

    public Map<Long, Double> calculateRestingHours(List<Long> staffIds, LocalDateTime startDate, LocalDateTime endDate) {
        Map<Long, Double> staffRestingHours = new HashMap<>();
        //currently not use
//        Long countryId = genericIntegrationService.getCountryIdOfOrganization(organizationId);
//        List<BigInteger> activityIds = getPresenceTimeTypeActivitiesIds(countryId);
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByStaffIdsAndDate(staffIds,startDate, endDate);
        Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()));
        staffIds.forEach(staffId -> {
            if(staffId != null) {
                    Double restingHours = getTotalRestingHours(staffShiftMapping.getOrDefault(staffId,new ArrayList<>()), DateUtils.getLongFromLocalDateimeTime(startDate),DateUtils.getLongFromLocalDateimeTime(endDate));
                    staffRestingHours.put(staffId, restingHours);
            }
        });
        return staffRestingHours;
    }

//    private List<BigInteger> getPresenceTimeTypeActivitiesIds(Long countryId) {
//        List supportedTimeTypeIdList = timeTypeService.getTimeTypesByTimeTypesAndByCountryId(countryId, TimeTypes.WORKING_TYPE);
//        return activityService.getActivitiesIdByTimeTypes(supportedTimeTypeIdList);
//    }

    private List<CommonKpiDataUnit> getRestingHoursKpiData(Map<FilterType, List> filterBasedCriteria, Long organizationId, boolean averageDay, boolean kpi) {
        // TO BE USED FOR AVERAGE CALCULATION.
        double multiplicationFactor = 1;
        //FIXME: fixed time interval TO BE REMOVED ONCE FILTERS IMPLEMENTED PROPERLY
        List staffIds= (filterBasedCriteria.get(FilterType.STAFF_IDS) != null)? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)):new ArrayList<>();
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null) ? KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)):Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS)!=null) ? KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)):new ArrayList();
        List<Long> employmentTypes = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)!=null) ?KPIUtils.getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)): new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO=new StaffEmploymentTypeDTO(staffIds,unitIds,employmentTypes,organizationId,filterDates.get(0).toString(),filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS=genericIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Long, Double> staffRestingHours = calculateRestingHours(staffKpiFilterDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), DateUtils.getLocalDateTimeFromLocalDate(filterDates.get(0)), DateUtils.getLocalDateTimeFromLocalDate(filterDates.get(1)).plusDays(1));
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        List<CommonKpiDataUnit> kpiDataUnits = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : staffRestingHours.entrySet()) {
            kpiDataUnits.add(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()),  Arrays.asList(new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(entry.getKey()),entry.getValue()*multiplicationFactor))));
        }
        return kpiDataUnits;
    }


    @Override
    public KPIRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getRestingHoursKpiData(filterBasedCriteria, organizationId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), false);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF,AppConstants.LABEL),new KPIAxisData(AppConstants.HOURS,AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<CommonKpiDataUnit> dataList = getRestingHoursKpiData(filterBasedCriteria, organizationId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), true);
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.STAFF,AppConstants.LABEL),new KPIAxisData(AppConstants.HOURS,AppConstants.VALUE_FIELD));
    }


}


