package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.counter.chart.BaseChart;
import com.kairos.dto.activity.counter.chart.DataUnit;
import com.kairos.dto.activity.counter.data.*;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepositoryImpl;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.activity.TimeTypeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RestingHoursCalculationService implements CounterService{
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private RepresentationService representationService;

    public Map<Long,Double> calculateRestingHours(List<Long> staffIds, Date startDate, Date endDate){
        Map<Long,Double> staffRestingHours=new HashMap<>();
        staffIds.stream().forEach(staffId -> {
            List<Shift> shifts= shiftMongoRepository.findAllShiftsByStaffIdsAndDate(Arrays.asList(staffId),startDate,endDate);
            Double restingHours =getTotalRestingHours(shifts,startDate.getTime(),endDate.getTime(),0,false);
            staffRestingHours.put(staffId,restingHours);

        });
        return staffRestingHours;
    }


    private List<BigInteger> getFilterredActivities(List<FilterCriteria> filters){
        ActivityFilterCriteria activityCriteria = ActivityFilterCriteria.getInstance();
        for(FilterCriteria criteria: filters){
            switch(criteria.getType()){
                case ACTIVITY_IDS: activityCriteria.setActivityIds(criteria.getValues()); break;
                case UNIT_IDS: activityCriteria.setUnitId(criteria.getValues()); break;
                case ACTIVITY_CATEGORY_TYPE: activityCriteria.setCategoryId(criteria.getValues()); break;
                case EMPLOYMENT_TYPE: activityCriteria.setEmploymentTypes(criteria.getValues()); break;
                case EXPERTISE: activityCriteria.setExpertiseCriteria(criteria.getValues()); break;
                case TIME_TYPE: activityCriteria.setTimeTypeList(criteria.getValues()); break;
                case PLANNED_TIME_TYPE: activityCriteria.setPlanneTimeType(criteria.getValues()); break;
                case ORGANIZATION_TYPE: activityCriteria.setOrganizationTypes(criteria.getValues()); break;
                default: break;
            }
        }

        return null;
    }

    private FilterCriteria getTimeTypeCriteriaForRestingHours(Long countryId){
        List supportedTimeTypeIdList = timeTypeService.getTimeTypesByTimeTypesAndByCountryId(countryId, TimeTypes.WORKING_TYPE);
        return new FilterCriteria(FilterType.TIME_TYPE, supportedTimeTypeIdList);
    }

    private void setShiftDayCollisionMap(long shiftCornerTs, long initTs, Map<Long, Integer> shiftDayCollisionMap) {
        if ((shiftCornerTs) > initTs) {
            long key = (shiftCornerTs - initTs) / (24 * 3600 * 1000);
            if (shiftDayCollisionMap.get(key) == null) {
                shiftDayCollisionMap.put(key, 0);
            }
            shiftDayCollisionMap.put(key, shiftDayCollisionMap.get(key) + 1);
        }
    }

    public double getTotalRestingHours(List<Shift> shifts, long initTs, long endTs, long restingHoursMillis, boolean dayOffAllowed) {
        //all shifts should be sorted on startDate
        Map<Long, Integer> shiftDayCollisionMap = new HashMap<>();
        long baseInitTs = initTs;
        long durationMillis = endTs - initTs;
        DateTimeInterval dateTimeInterval = new DateTimeInterval(initTs,endTs);
        long totalrestingMinutes = dateTimeInterval.getMilliSeconds();
        for (Shift shift : shifts) {
            DateTimeInterval shiftInterval= new DateTimeInterval(shift.getStartDate(),shift.getEndDate());
            if(dateTimeInterval.overlaps(shiftInterval)){
                totalrestingMinutes-=dateTimeInterval.overlap(shiftInterval).getMilliSeconds();
            }

            /*if (initTs >= shift.getStartDate().getTime() && initTs >= shift.getEndDate().getTime()) {
                durationMillis -= 0;
                //setShiftDayCollisionMap(shift.getEndDate().getTime() + restingHoursMillis, baseInitTs, shiftDayCollisionMap);
            } else if (initTs >= shift.getStartDate().getTime() && initTs < shift.getEndDate().getTime()) {
                durationMillis -= (shift.getEndDate().getTime() - initTs);
                initTs = shift.getEndDate().getTime();
                //setShiftDayCollisionMap(shift.getEndDate().getTime() + restingHoursMillis, baseInitTs, shiftDayCollisionMap);
            } else if (initTs < shift.getStartDate().getTime() && endTs > shift.getEndDate().getTime()) {
                durationMillis -= (shift.getEndDate().getTime() - shift.getStartDate().getTime());
                initTs = shift.getEndDate().getTime();
                //setShiftDayCollisionMap(shift.getEndDate().getTime() + restingHoursMillis, baseInitTs, shiftDayCollisionMap);
                //setShiftDayCollisionMap(shift.getStartDate().getTime(), baseInitTs, shiftDayCollisionMap);
            } else if (initTs < shift.getStartDate().getTime() && endTs < shift.getEndDate().getTime()) {
                durationMillis -= (endTs - shift.getStartDate().getTime());
                initTs = endTs;
                //setShiftDayCollisionMap(shift.getStartDate().getTime(), baseInitTs, shiftDayCollisionMap);
            }*/
        }
//        if (dayOffAllowed) {
//            durationMillis -= (shiftDayCollisionMap.entrySet().size() * (24 * 3600 * 1000));
//        }
        return totalrestingMinutes;
    }

    private List<DataUnit> getDataList(FilterCriteriaDTO filterCriteria){
        Map<FilterType, List> filterTypeListMap = new HashMap<>();
        for(FilterCriteria criteria:filterCriteria.getFilters()){
            filterTypeListMap.put(criteria.getType(), criteria.getValues());
        }
        List staffIds = filterTypeListMap.get(FilterType.STAFF_IDS);
        List dates = filterTypeListMap.get(FilterType.TIME_INTERVAL);
        Map<Long, Double> staffRestingHours = calculateRestingHours(staffIds, (Date)dates.get(0), (Date)dates.get(1));
        List<DataUnit> dataList = new ArrayList<>();
        for(Map.Entry<Long, Double> entry : staffRestingHours.entrySet()){
            dataList.add(new DataUnit(entry.getKey()+"", entry.getValue()));
        }
        return dataList;
    }

    @Override
    public RawRepresentationData getCalculatedCounter(FilterCriteriaDTO filterCriteria, KPI kpi) {
        //filterCriteria.getFilters().add(getTimeTypeCriteriaForRestingHours(filterCriteria.getCurrentCountryId()));
        //List<BigInteger> activityIds = activityMongoRepository.getActivityIdsByFilter(filterCriteria.getFilters());
        List<DataUnit> dataList = getDataList(filterCriteria);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart().getType(), "", RepresentationUnit.DECIMAL, dataList);
        //return representationService.getRepresentationData(basicData);
    }

    @Override
    public RawRepresentationData getCalculatedKPI(FilterCriteriaDTO filterCriteriaDTO, KPI kpi) {
        List<DataUnit> dataList = getDataList(filterCriteriaDTO);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart().getType(), "", RepresentationUnit.DECIMAL, dataList);
    }
}
