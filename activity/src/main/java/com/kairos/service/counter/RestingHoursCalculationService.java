package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.chart.KpiDataUnit;
import com.kairos.dto.activity.counter.data.RawRepresentationData;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.TimeTypeService;
import org.joda.time.DateTimeConstants;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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

    public double getTotalRestingHours(List<Shift> shifts, long initTs, long endTs, boolean dayOffAllowed) {
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

    public Map<Long, Double> calculateRestingHours(List<Long> staffIds, Long organizationId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<Long, Double> staffRestingHours = new HashMap<>();
        //currently not use
//        Long countryId = genericIntegrationService.getCountryIdOfOrganization(organizationId);
//        List<BigInteger> activityIds = getPresenceTimeTypeActivitiesIds(countryId);
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByStaffIdsAndDate(staffIds,startDate, endDate);
        Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()));
        staffIds.forEach(staffId -> {
            if(staffId != null) {
                if(staffShiftMapping.get(staffId)!=null) {
                    Double restingHours = getTotalRestingHours(staffShiftMapping.get(staffId), DateUtils.getLongFromLocalDateimeTime(startDate),DateUtils.getLongFromLocalDateimeTime(endDate.plusDays(1)), false);
                    staffRestingHours.put(staffId, restingHours);
                }
            }
        });
        return staffRestingHours;
    }

//    private List<BigInteger> getPresenceTimeTypeActivitiesIds(Long countryId) {
//        List supportedTimeTypeIdList = timeTypeService.getTimeTypesByTimeTypesAndByCountryId(countryId, TimeTypes.WORKING_TYPE);
//        return activityService.getActivitiesIdByTimeTypes(supportedTimeTypeIdList);
//    }

    private List<KpiDataUnit> getDataList(Map<FilterType, List> filterBasedCriteria,Long organizationId, boolean averageDay, boolean kpi) {
        // TO BE USED FOR AVERAGE CALCULATION.
        double multiplicationFactor = 1;
        //FIXME: fixed time interval TO BE REMOVED ONCE FILTERS IMPLEMENTED PROPERLY
        List staffIds= (filterBasedCriteria.get(FilterType.STAFF_IDS) != null)?getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS)):new ArrayList<>();
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null) ? filterBasedCriteria.get(FilterType.TIME_INTERVAL): Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS)!=null) ? getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)):new ArrayList();
        List<Long> employmentTypes = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)!=null) ?getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)): new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO=new StaffEmploymentTypeDTO(staffIds,unitIds,employmentTypes,organizationId,filterDates.get(0).toString(),filterDates.get(1).toString());
        List<StaffDTO> staffDTOS=genericIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Long, Double> staffRestingHours = calculateRestingHours(staffDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), organizationId, filterDates.get(0).atStartOfDay(), filterDates.get(1).atStartOfDay());
        Map<Long, String> staffIdAndNameMap = staffDTOS.stream().collect(Collectors.toMap(StaffDTO::getId, StaffDTO::getFullName));
        List<KpiDataUnit> dataList = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : staffRestingHours.entrySet()) {
            dataList.add(new KpiDataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getKey(), entry.getValue()*multiplicationFactor));
        }
        return dataList;
    }

    @Override
    public RawRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<KpiDataUnit> dataList = getDataList(filterBasedCriteria, organizationId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), false);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<KpiDataUnit> dataList = getDataList(filterBasedCriteria, organizationId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), true);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }

    private List<Long> getLongValue(List<Object> objects){
        return objects.stream().map(o -> ((Integer)o).longValue()).collect(Collectors.toList());
    }
}


