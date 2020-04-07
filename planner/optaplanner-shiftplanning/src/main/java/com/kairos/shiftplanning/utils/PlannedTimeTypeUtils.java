package com.kairos.shiftplanning.utils;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.PlannedTime;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

public class PlannedTimeTypeUtils {

    public void addPlannedTimeInShift(ShiftImp shift, Unit unit, boolean shiftTypeChanged) {
        List<PlannedTime> plannedTimeList = shift.getShiftActivities().stream().flatMap(k -> k.getPlannedTimes().stream()).collect(Collectors.toList());
        Map<DateTimeInterval, PlannedTime> plannedTimeMap = plannedTimeList.stream().filter(distinctByKey(plannedTime -> new DateTimeInterval(plannedTime.getStartDate(), plannedTime.getEndDate()))).collect(toMap(k -> new DateTimeInterval(k.getStartDate(), k.getEndDate()), Function.identity()));
        for (ShiftActivity shiftActivity : shift.getShiftActivities()) {
            List<BigInteger> plannedTimeIds = addPlannedTimeInShift(unit, shiftActivity.getActivity());
            BigInteger plannedTimeId = plannedTimeIds.get(0);
            List<PlannedTime> plannedTimes = isNull(shift.getId()) || shiftTypeChanged ? newArrayList(new PlannedTime(plannedTimeId, shiftActivity.getStartDate(), shiftActivity.getEndDate())) : filterPlannedTimes(shiftActivity.getStartDate(), shiftActivity.getEndDate(), plannedTimeMap, plannedTimeId);
            shiftActivity.setPlannedTimes(plannedTimes);
        }
    }

    public List<BigInteger> addPlannedTimeInShift(Unit unit, Activity activity) {
        List<BigInteger> plannedTimes;
        switch (activity.getTimeType().getTimeTypeEnum()){
            case ABSENCE :
                plannedTimes = getAbsencePlannedTime(unit, activity);
                break;
            case PRESENCE:
                plannedTimes = unit.getPresencePlannedTime().getManagementPlannedTimeIds();
                break;
            default:
                plannedTimes = unit.getNonWorkingPlannedTime().getPlannedTimeIds();

        }
        return plannedTimes;
    }

    private List<BigInteger> getAbsencePlannedTime(Unit unit, Activity activity) {
        List<BigInteger> plannedTimeIds = unit.getAbsencePlannedTime().getPlannedTimeIds();
        if (unit.getAbsencePlannedTime().isException() && activity.getTimeType().getId().equals(unit.getAbsencePlannedTime().getTimeTypeId())) {
            plannedTimeIds = unit.getAbsencePlannedTime().getPlannedTimeIds();
        }
        return plannedTimeIds;
    }

    private List<com.kairos.dto.activity.shift.PlannedTime> filterPlannedTimes(Date startDate, Date endDate, Map<DateTimeInterval, com.kairos.dto.activity.shift.PlannedTime> plannedTimeMap, BigInteger plannedTimeId) {
        DateTimeInterval activityInterval = new DateTimeInterval(startDate, endDate);
        plannedTimeMap = plannedTimeMap.entrySet().stream().filter(map -> map.getKey().overlaps(activityInterval)).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        plannedTimeMap = plannedTimeMap.entrySet().stream().sorted(comparing(k -> k.getKey().getStartDate())).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        List<com.kairos.dto.activity.shift.PlannedTime> plannedTimes = new ArrayList<>();
        final boolean endDateInside = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().containsStartOrEnd(endDate));
        final boolean activityIntervalOverLapped = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().overlaps(activityInterval));

        if (!activityIntervalOverLapped) {
            plannedTimes.add(new com.kairos.dto.activity.shift.PlannedTime(plannedTimeId, startDate, endDate));
        } else {

            if (plannedTimeMap.size() != 0) {
                DateTimeInterval lastInterval = plannedTimeMap.keySet().stream().skip(plannedTimeMap.keySet().size() - 1).findFirst().get();
                boolean addedAtLeading = false;
                for (Map.Entry<DateTimeInterval, com.kairos.dto.activity.shift.PlannedTime> plannedTimeInterval : plannedTimeMap.entrySet()) {
                    DateTimeInterval shiftActivityInterVal = new DateTimeInterval(startDate, endDate);
                    if (plannedTimeInterval.getKey().containsInterval(shiftActivityInterVal)) {
                        plannedTimes.add(new com.kairos.dto.activity.shift.PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, endDate));
                        break;
                    } else if (startDate.before(plannedTimeInterval.getKey().getStartDate())) {
                        if (!addedAtLeading) {
                            plannedTimes.add(new com.kairos.dto.activity.shift.PlannedTime(plannedTimeId, startDate, plannedTimeInterval.getKey().getStartDate()));
                            addedAtLeading = true;
                        }
                        plannedTimes.add(new com.kairos.dto.activity.shift.PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), plannedTimeInterval.getKey().getStartDate(), plannedTimeInterval.getKey().getEndDate()));
                        startDate = plannedTimeInterval.getKey().getEndDate();
                    } else if (startDate.equals(plannedTimeInterval.getKey().getStartDate()) || startDate.after(plannedTimeInterval.getKey().getStartDate())) {
                        plannedTimes.add(new com.kairos.dto.activity.shift.PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, plannedTimeInterval.getKey().getEndDate()));
                        startDate = plannedTimeInterval.getKey().getEndDate();
                    }  else if (!plannedTimeInterval.getKey().overlaps(shiftActivityInterVal)) {
                        plannedTimes.add(new com.kairos.dto.activity.shift.PlannedTime(plannedTimeId, startDate, endDate));
                    }
                }
                if (!endDateInside) {
                    plannedTimes.add(new com.kairos.dto.activity.shift.PlannedTime(plannedTimeId, lastInterval.getEndDate(), endDate));
                }

            }
        }
        return plannedTimes;
    }

}
