package com.kairos.shiftplanning.utils;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.PlannedTime;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isMapNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

public class PlannedTimeTypeUtils {

    public static void addPlannedTimeInShift(ShiftImp shift) {
        Set<BigInteger> plannedTimeTypeIds = new HashSet<>();
        for (ShiftActivity shiftActivity : shift.getShiftActivities()) {
            BigInteger plannedTimeId = getPlannedTimeIdByTimeType(shift.getEmployee().getUnit(), shiftActivity.getActivity());
            List<PlannedTime> plannedTimes;
            if(shift.isShiftTypeChanged()){
                plannedTimes = newArrayList(new PlannedTime(plannedTimeId, shiftActivity.getStartDate(), shiftActivity.getEndDate()));
            }else {
                plannedTimes = filterPlannedTimes(shiftActivity.getStartDate(), shiftActivity.getEndDate(), shift, plannedTimeId);
            }
            shiftActivity.setPlannedTimes(plannedTimes);
            plannedTimeTypeIds.addAll(plannedTimes.stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
        }
        shift.setActivitiesPlannedTimeIds(plannedTimeTypeIds);
    }

    private static BigInteger getPlannedTimeIdByTimeType(Unit unit, Activity activity) {
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
        return plannedTimes.get(0);
    }

    private static List<BigInteger> getAbsencePlannedTime(Unit unit, Activity activity) {
        List<BigInteger> plannedTimeIds = unit.getAbsencePlannedTime().getPlannedTimeIds();
        if (unit.getAbsencePlannedTime().isException() && activity.getTimeType().getId().equals(unit.getAbsencePlannedTime().getTimeTypeId())) {
            plannedTimeIds = unit.getAbsencePlannedTime().getPlannedTimeIds();
        }
        return plannedTimeIds;
    }

    private static List<PlannedTime> filterPlannedTimes(ZonedDateTime startDate, ZonedDateTime endDate, ShiftImp shiftImp, BigInteger plannedTimeId) {
        DateTimeInterval activityInterval = new DateTimeInterval(startDate, endDate);
        Map<DateTimeInterval,PlannedTime> plannedTimeMap = shiftImp.getActualShiftActivities().stream().flatMap(shiftActivity -> shiftActivity.getPlannedTimes().stream()).filter(plannedTime -> plannedTime.getInterval().overlaps(activityInterval)).sorted(comparing(plannedTime -> plannedTime.getStartDate())).collect(toMap(plannedTime->plannedTime.getInterval(), plannedTime->plannedTime, (e1, e2) -> e2, LinkedHashMap::new));
        List<PlannedTime> plannedTimes = new ArrayList<>();
        final boolean endDateInside = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().containsAndEqualsEndDate(endDate));
        final boolean activityIntervalOverLapped = plannedTimeMap.entrySet().stream().anyMatch(k -> k.getKey().overlaps(activityInterval));
        if (!activityIntervalOverLapped) {
            plannedTimes.add(new PlannedTime(plannedTimeId, startDate, endDate));
        } else {
            if (isMapNotEmpty(plannedTimeMap)) {
                DateTimeInterval lastInterval = plannedTimeMap.keySet().stream().skip(plannedTimeMap.keySet().size() - 1).findFirst().get();
                boolean addedAtLeading = false;
                for (Map.Entry<DateTimeInterval, PlannedTime> plannedTimeInterval : plannedTimeMap.entrySet()) {
                    DateTimeInterval shiftActivityInterVal = new DateTimeInterval(startDate, endDate);
                    if (plannedTimeInterval.getKey().containsInterval(shiftActivityInterVal)) {
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, endDate));
                        break;
                    } else if (startDate.isBefore(plannedTimeInterval.getKey().getStart())) {
                        if (!addedAtLeading) {
                            plannedTimes.add(new PlannedTime(plannedTimeId, startDate, plannedTimeInterval.getKey().getStart()));
                            addedAtLeading = true;
                        }
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), plannedTimeInterval.getKey().getStart(), plannedTimeInterval.getKey().getEnd()));
                        startDate = plannedTimeInterval.getKey().getEnd();
                    } else if (startDate.equals(plannedTimeInterval.getKey().getStartDate()) || startDate.isAfter(plannedTimeInterval.getKey().getStart())) {
                        plannedTimes.add(new PlannedTime(plannedTimeInterval.getValue().getPlannedTimeId(), startDate, plannedTimeInterval.getKey().getEnd()));
                        startDate = plannedTimeInterval.getKey().getEnd();
                    }  else if (!plannedTimeInterval.getKey().overlaps(shiftActivityInterVal)) {
                        plannedTimes.add(new PlannedTime(plannedTimeId, startDate, endDate));
                    }
                }
                if (!endDateInside) {
                    plannedTimes.add(new PlannedTime(plannedTimeId, lastInterval.getEnd(), endDate));
                }

            }
        }
        return plannedTimes;
    }

}
