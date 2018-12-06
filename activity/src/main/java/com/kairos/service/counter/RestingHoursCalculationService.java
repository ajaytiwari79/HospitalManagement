package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.chart.DataUnit;
import com.kairos.dto.activity.counter.data.RawRepresentationData;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.TimeTypeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return totalrestingMinutes;
    }

    public Map<Long, Double> calculateRestingHours(List<Long> staffIds, Long countryId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<Long, Double> staffRestingHours = new HashMap<>();
        List<BigInteger> activityIds = getPresenceTimeTypeActivitiesIds(countryId);
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByStaffIdsAndDate(staffIds, activityIds, startDate, endDate);
        Map<Long, List<Shift>> staffShiftMapping = shifts.parallelStream().collect(Collectors.groupingBy(shift -> shift.getStaffId(), Collectors.toList()));
        staffIds.forEach(staffId -> {
            if(staffId != null) {
                Double restingHours = getTotalRestingHours(shifts, startDate.toEpochSecond(ZoneOffset.UTC), endDate.toEpochSecond(ZoneOffset.UTC), false);
                staffRestingHours.put(staffId, restingHours);
            }
        });
        return staffRestingHours;
    }

    private List<BigInteger> getPresenceTimeTypeActivitiesIds(Long countryId) {
        List supportedTimeTypeIdList = timeTypeService.getTimeTypesByTimeTypesAndByCountryId(countryId, TimeTypes.WORKING_TYPE);
        return activityService.getActivitiesIdByTimeTypes(supportedTimeTypeIdList);
    }

    private List<DataUnit> getDataList(Map<FilterType, List> filterBasedCriteria, Long countryId, boolean averageDay, boolean kpi) {
        List staffIds = new ArrayList<>();
        List dates = new ArrayList();

        // TO BE USED FOR AVERAGE CALCULATION.
        double multiplicationFactor = 1;

        //FIXME: fixed time interval TO BE REMOVED ONCE FILTERS IMPLEMENTED PROPERLY
        if(kpi && filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS)!= null){
            staffIds = filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS);
        }else if(filterBasedCriteria.get(FilterType.STAFF_IDS) != null){
            staffIds = filterBasedCriteria.get(FilterType.STAFF_IDS);
        }
        if(filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null){
            dates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }else{
            dates.add(DateUtils.substractDurationInLocalDateTime(LocalDateTime.now(), 24, DurationType.HOURS));
            dates.add(LocalDateTime.now());
        }
        Map<Long, Double> staffRestingHours = calculateRestingHours(staffIds, countryId, (LocalDateTime) dates.get(0), (LocalDateTime) dates.get(1));
        List<DataUnit> dataList = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : staffRestingHours.entrySet()) {
            dataList.add(new DataUnit(""+entry.getKey(), entry.getKey(), entry.getValue()*multiplicationFactor));
        }
        return dataList;
    }

    @Override
    public RawRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long countryId, KPI kpi) {
        List<DataUnit> dataList = getDataList(filterBasedCriteria, countryId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), false);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long countryId, KPI kpi) {
        List<DataUnit> dataList = getDataList(filterBasedCriteria, countryId, kpi.getType().equals(CounterType.AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY), true);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }
}
