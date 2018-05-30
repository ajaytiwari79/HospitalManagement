package com.kairos.activity.service.priority_group;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.activity.persistence.model.priority_group.PriorityGroup;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import com.kairos.response.dto.web.StaffDTO;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;
import org.apache.commons.collections.map.HashedMap;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static java.util.stream.Collectors.groupingBy;


public class PriorityGroupFilter {

    private PriorityGroupDTO priorityGroupDTO;
    private Map<Long,List<DailyTimeBankEntry>> unitPositionDailyTimeBankEntryMap;
    private List<StaffUnitPositionQueryResult> staffsUnitPositions;
    private TimeBankCalculationService timeBankCalculationService;
    private Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap;
    private Map<BigInteger, OpenShift> openShiftMap;
    private Map<Long,Integer> assignedOpenShiftMap;
    private Map<Long,List<Shift>> shiftUnitPositionsMap;
    private List<OpenShiftNotification> openShiftNotifications;
    private List<Shift> shifts;
    private Set<BigInteger> unavailableActivitySet;
    private Set<BigInteger> vetoActivitySet;
    private Set<BigInteger> stopBricksActivitySet;

    public PriorityGroupFilter(PriorityGroupDTO priorityGroupDTO, Map<Long,List<DailyTimeBankEntry>> unitPositionDailyTimeBankEntryMap, List<StaffUnitPositionQueryResult> staffsUnitPositions,
                               TimeBankCalculationService timeBankCalculationService, List<OpenShift> openShifts, Map<Long,Integer> assignedOpenShiftMap,
                               List<OpenShiftNotification> openShiftNotifications,List<Shift> shifts,Set<BigInteger> unavailableActivitySet,
                               Set<BigInteger> vetoActivitySet, Set<BigInteger> stopBricksActivitySet)
     {
        this.priorityGroupDTO = priorityGroupDTO;
        this.unitPositionDailyTimeBankEntryMap = unitPositionDailyTimeBankEntryMap;
        this.staffsUnitPositions = staffsUnitPositions;
        if(Optional.ofNullable(priorityGroupDTO.getStaffExcludeFilter().getUnitExperienceInWeek()).isPresent()) {
            filterStaffByUnitExperienceRule();
        }
        this.timeBankCalculationService = timeBankCalculationService;
        openShiftStaffMap = new HashMap<BigInteger, List<StaffUnitPositionQueryResult>>();
       // new ArrayList<>(openShiftStaffMap.values().stream().flatMap(v->v.stream().map(va->va.getUnitPositionId())).collect(Collectors.toSet()));
        openShiftMap = new HashMap<BigInteger,OpenShift>();
        for(OpenShift openShift:openShifts) {
            openShiftStaffMap.put(openShift.getId(),new ArrayList<>(staffsUnitPositions));
            openShiftMap.put(openShift.getId(),openShift);
        }
        this.assignedOpenShiftMap = assignedOpenShiftMap;
        this.openShiftNotifications = openShiftNotifications;
        this.shifts = shifts;
        this.unavailableActivitySet = unavailableActivitySet;
        this.vetoActivitySet = vetoActivitySet;
        this.stopBricksActivitySet = stopBricksActivitySet;
    }

    public List<StaffDTO> getStaffByPriorityGroupIncludeFilter(List<StaffDTO> staffDTOS) {

        //staffDTOS.str
        return staffDTOS;
    }

    public void filterStaffByUnitExperienceRule() {

        List<StaffUnitPositionQueryResult> staffUnitPositionList = staffsUnitPositions;
        int experienceInDays = priorityGroupDTO.getStaffExcludeFilter().getUnitExperienceInWeek()*7;
        Long startDate = DateUtils.getLongFromLocalDate(LocalDate.now().minusDays(experienceInDays));
        staffUnitPositionList.stream().filter(staffUnitPosition->staffUnitPosition.getStartDate()<startDate);
    }
    public void filterStaffByAssignedOpenShiftRule() {

        int thresholdShiftCount = priorityGroupDTO.getStaffExcludeFilter().getNumberOfShiftAssigned();
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            entry.getValue().removeIf(staffUnitPosition-> assignedOpenShiftMap.containsKey(staffUnitPosition.getUnitPositionId())
                    &&(assignedOpenShiftMap.get(staffUnitPosition.getUnitPositionId())>thresholdShiftCount));
        }

    }

    public void filterStaffByTimeBankCriteria() {

        int timeBank;
        //Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = staffsUnitPositions.iterator();

        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();

            while(staffUnitPositionIterator.hasNext()) {

                StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
                Long endDate = DateUtils.getLongFromLocalDate(openShiftMap.get(entry.getKey()).getStartDate());
                UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(staffUnitPositionQueryResult.getUnitPositionId(),
                        staffUnitPositionQueryResult.getContractedMinByWeek(),staffUnitPositionQueryResult.getWorkingDaysPerWeek(),
                        DateUtils.getDateFromEpoch(staffUnitPositionQueryResult.getStartDate()), DateUtils.getDateFromEpoch(staffUnitPositionQueryResult.getEndDate()));
                timeBank = timeBankCalculationService.calculateTimeBankForInterval(new Interval(staffUnitPositionQueryResult.getStartDate(),endDate),
                        unitPositionWithCtaDetailsDTO,false,unitPositionDailyTimeBankEntryMap.get(staffUnitPositionQueryResult.getUnitPositionId()),false);
                if(timeBank<priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()) {
                    staffUnitPositionIterator.remove();
                }
            }
        }
    }

    public void filterStaffByDeltaTimeBankCriteria() {

        int timeBank;
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();

            while(staffUnitPositionIterator.hasNext()) {

                StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
                Long endDate = DateUtils.getISOEndOfWeekDate(openShiftMap.get(entry.getKey()).getStartDate()).getTime();
                Long startDate = DateUtils.getISOStartOfWeek(openShiftMap.get(entry.getKey()).getStartDate());
                UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(staffUnitPositionQueryResult.getUnitPositionId(),
                        staffUnitPositionQueryResult.getContractedMinByWeek(),staffUnitPositionQueryResult.getWorkingDaysPerWeek(),
                        DateUtils.getDateFromEpoch(staffUnitPositionQueryResult.getStartDate()), DateUtils.getDateFromEpoch(staffUnitPositionQueryResult.getEndDate()));
                timeBank = timeBankCalculationService.calculateTimeBankForInterval(new Interval(startDate,endDate),
                        unitPositionWithCtaDetailsDTO,false,unitPositionDailyTimeBankEntryMap.get(staffUnitPositionQueryResult.getUnitPositionId()), false);
                if(timeBank<priorityGroupDTO.getStaffExcludeFilter().getMinTimeBank()) {
                    staffUnitPositionIterator.remove();
                }
            }
        }
    }

    public void filterStaffByPlannedHoursPerWeekCriteria() {

        int plannedHoursWeekly;
        for (Map.Entry<BigInteger, List<StaffUnitPositionQueryResult>> entry : openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            while(staffUnitPositionIterator.hasNext()) {

                StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
                List<DailyTimeBankEntry> dailyTimeBankEntries = unitPositionDailyTimeBankEntryMap.get(staffUnitPositionQueryResult.getUnitPositionId());
                LocalDate startDate = DateUtils.getDateFromEpoch(DateUtils.getISOStartOfWeek(openShiftMap.get(entry.getKey()).getStartDate()));
                LocalDate endDate = DateUtils.asLocalDate(DateUtils.getISOEndOfWeekDate(openShiftMap.get(entry.getKey()).getStartDate()));
                plannedHoursWeekly = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> dailyTimeBankEntry.getDate().isAfter(startDate)||
                        dailyTimeBankEntry.getDate().isEqual(startDate)&&dailyTimeBankEntry.getDate().isBefore(endDate)||
                        dailyTimeBankEntry.getDate().isEqual(endDate)).mapToInt(d->d.getScheduledMin() + d.getTimeBankMinWithCta()).sum();
                if(plannedHoursWeekly>priorityGroupDTO.getStaffExcludeFilter().getMaxPlannedTime()) {
                    staffUnitPositionIterator.remove();
                }

            }
        }

    }

    public void filterStaffByLastWorkInUnit() {

        int lastWorkingDaysWithActivity = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity();
        int lastWorkingDaysWithUnit = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit();
        int shiftCount = 0;
        for (Map.Entry<BigInteger, List<StaffUnitPositionQueryResult>> entry : openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate().minusDays(lastWorkingDaysWithUnit));
            Date filterEndDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate());
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            removeStaffFromList(staffUnitPositionIterator,dateTimeInterval,null);
        }
    }

    public void filterStaffByLastWorkWithActivity() {

        int lastWorkingDaysWithActivity = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity();
        int shiftCount = 0;
        for (Map.Entry<BigInteger, List<StaffUnitPositionQueryResult>> entry : openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate().minusDays(lastWorkingDaysWithActivity));
            Date filterEndDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate());
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            removeStaffFromList(staffUnitPositionIterator,dateTimeInterval,openShiftMap.get(entry.getKey()).getActivityId());
        }
    }

    private void removeStaffFromList(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator, DateTimeInterval dateTimeInterval,BigInteger activityId) {
        int shiftCount = 0;
        while(staffUnitPositionIterator.hasNext()) {
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            List<Shift> shifts = shiftUnitPositionsMap.get(staffUnitPositionQueryResult.getUnitPositionId());
            for(Shift shift:shifts) {
                if(dateTimeInterval.overlaps(shift.getInterval())&&shift.getActivityId().equals(activityId)) {
                    shiftCount++;
                    break;
                }
            }
            if(shiftCount==0){
                staffUnitPositionIterator.remove();
            }
        }
    }

    public void filterStaffByRestingHoursBeforeStart() {

        int restingHoursBeforeStart = priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeBeforeShiftStart();
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            Date filterEndDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getFromTime());
            Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).
                    getFromTime().minusHours(restingHoursBeforeStart));
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            removeStaffFromListByRestingHours(staffUnitPositionIterator,dateTimeInterval);
        }
    }

    private void removeStaffFromListByRestingHours(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator,DateTimeInterval dateTimeInterval) {

        Set<Long> unitPositionIds = new HashSet<Long>();
        for(Shift shift:shifts) {
            if(dateTimeInterval.overlaps(shift.getInterval())) {
                if(!unitPositionIds.contains(shift.getUnitPositionId())) {
                    unitPositionIds.add(shift.getUnitPositionId());
                }
            }
        }
        while(staffUnitPositionIterator.hasNext()) {
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            if(unitPositionIds.contains(staffUnitPositionQueryResult.getUnitPositionId())) {
                staffUnitPositionIterator.remove();
            }
        }
    }

    public void filterStaffByRestingHoursAfterEnd() {

        int restingHoursAfterEnd = priorityGroupDTO.getStaffExcludeFilter().getMinRestingTimeAfterShiftEnd();
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            Date filterEndDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getToTime().plusHours(restingHoursAfterEnd));
            Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getToTime());
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
            Map<Long,List<Shift>> shiftsUnitPositionMapForOpenShift = shiftUnitPositionsMap.values().stream().flatMap(shifts -> shifts.stream().
                    filter(shift ->dateTimeInterval.overlaps(shift.getInterval()))).collect(groupingBy(Shift::getUnitPositionId));
            removeStaffFromListByRestingHours(staffUnitPositionIterator,dateTimeInterval);

        }
    }

    public void filterStaffByNegativeAvailabilityVeto() {

        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            Date filterEndDate = DateUtils.asDateEndOfDay(openShiftMap.get(entry.getKey()).getStartDate());
            Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate());
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());
           /* Map<Long,List<Shift>> shiftsUnitPositionMapForOpenShift = shiftUnitPositionsMap.values().stream().flatMap(shifts -> shifts.stream().
                    filter(shift ->dateTimeInterval.overlaps(shift.getInterval()))).collect(groupingBy(Shift::getUnitPositionId));*/

            Set<Long> unitPositionIds = new HashSet<Long>();
            for(Shift shift:shifts) {
                if(dateTimeInterval.overlaps(shift.getInterval())&&unavailableActivitySet.contains(shift.getActivityId())) {
                    if(priorityGroupDTO.getStaffExcludeFilter().isNegativeAvailabilityInCalender()&&unavailableActivitySet.contains(shift.getActivityId())&&
                            !unitPositionIds.contains(shift.getUnitPositionId())) {
                        unitPositionIds.add(shift.getUnitPositionId());
                    }
                    if(priorityGroupDTO.getStaffExcludeFilter().isVeto()&&vetoActivitySet.contains(shift.getActivityId())&&
                            !unitPositionIds.contains(shift.getUnitPositionId())) {
                        unitPositionIds.add(shift.getUnitPositionId());
                    }

                }
            }
            removeStaffFromList(staffUnitPositionIterator,unitPositionIds);

        }
    }

    private void removeStaffFromList(Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator,Set<Long> unitPositionIds) {

        while(staffUnitPositionIterator.hasNext()) {
            StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
            if(unitPositionIds.contains(staffUnitPositionQueryResult.getUnitPositionId())) {
                staffUnitPositionIterator.remove();
            }
        }
    }

    public void filterStaffByPercentAvailabilityAndStopBricks() {

        if(Optional.ofNullable(priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability()).isPresent()) {
            float availabilityPercentage = priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability();
        }
        float availabilityPercentage = priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability();
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();
            Date filterEndDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getToTime());
            Date filterStartDate = DateUtils.asDate(openShiftMap.get(entry.getKey()).getStartDate(),openShiftMap.get(entry.getKey()).getFromTime());
            DateTimeInterval dateTimeInterval = new DateTimeInterval(filterStartDate.getTime(),filterEndDate.getTime());

            /*Map<Long,List<Shift>> shiftsUnitPositionMapForOpenShift = shiftUnitPositionsMap.values().stream().flatMap(shifts -> shifts.stream().
                    filter(shift ->dateTimeInterval.overlaps(shift.getInterval()))).collect(groupingBy(Shift::getUnitPositionId));*/
            Set<Long> unitPositionIds = new HashSet<Long>();
            for(Shift shift:shifts) {
                if(dateTimeInterval.overlaps(shift.getInterval())) {
                    if(Optional.ofNullable(priorityGroupDTO.getStaffIncludeFilter().getStaffAvailability()).isPresent()&&
                            (((dateTimeInterval.overlap(shift.getInterval()).getMinutes())/(dateTimeInterval.getMinutes()))*100)<availabilityPercentage&&
                            !unitPositionIds.contains(shift.getUnitPositionId())) {
                            unitPositionIds.add(shift.getUnitPositionId());
                    }
                    if(priorityGroupDTO.getStaffExcludeFilter().isStopBricks()&&stopBricksActivitySet.contains(shift.getActivityId())&&
                            !unitPositionIds.contains(shift.getUnitPositionId())) {
                        unitPositionIds.add(shift.getUnitPositionId());

                    }
                }
            }
            removeStaffFromList(staffUnitPositionIterator,unitPositionIds) ;
        }
    }

    public void filterByPendingRequestRule() {
        int maxPendingRequests = priorityGroupDTO.getStaffExcludeFilter().getNumberOfPendingRequest();
        Map<Long,List<OpenShiftNotification>> staffOpenShiftNotificationsMap = openShiftNotifications.stream().collect(groupingBy(OpenShiftNotification::getStaffId));
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()){

            Iterator<StaffUnitPositionQueryResult> staffsUnitPositions = entry.getValue().iterator();
            while(staffsUnitPositions.hasNext()) {
                StaffUnitPositionQueryResult staffUnitPosition = staffsUnitPositions.next();
                if(staffOpenShiftNotificationsMap.get(staffUnitPosition.getStaffId()).size()>=maxPendingRequests){
                    staffsUnitPositions.remove();
                }
            }
        }
    }
}
