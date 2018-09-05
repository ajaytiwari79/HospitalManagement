package com.kairos.service.open_shift;

import com.kairos.activity.staffing_level.StaffingLevelActivity;
import com.kairos.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.activity.staffing_level.StaffingLevelInterval;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.open_shift.OpenShiftInterval;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.open_shift.OpenShiftRuleTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import java.util.Collections;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigInteger;
import java.util.stream.Stream;

@Component
public class AutomaticOpenShiftGenerationService {

    @Inject
    private OpenShiftRuleTemplateService openShiftRuleTemplateService;
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private OpenShiftRuleTemplateRepository openShiftRuleTemplateRepository;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    /*
    This method returns map of localdate and (map of activity id and staffingLevelactivityobject containing
    understaffingoverstafing and interval.
    Suppose staffing level is defined as 00-00:15 - 5
    00:15 - 00:30 - 5
    00:30 - 00:45 - 5
    00:45 - 1:00  - 5
    1:00 - 1:15   - 5
    1:15 - 1:30   - 5
    1:30 - 1:45   - 5
    1:45 - 2;00   - 5
    2:00 - 2:15   - 5
    2:15 - 2:30   - 5
    2:30 - 2:45   - 5
    2:45 - 3:00   - 5
    3:00 - 3:15   - 5
    3:15 - 3:30   - 5
    3:30 - 3:45   - 5
    3:45 - 4:00   - 7
    4:00 - 4:15   - 7
    4:30 - 4:45   - 7
    4:45 - 5:00   - 7
    5:00 - 5:15   - 7
    5:15 - 5:30   - 7
    5:30 - 5:45   - 7
    5:45 - 6:00   - 7
    Then the function would return List of intervals with staffinglevel activityid and unserstaffingoverstaffing value(it is calcualted by reducing the shift count
    from min max value present in staffinglevelactivity) as below
    00:15 - 03:45 - 5
    03:45 - 06:00 - 7
     */
    public  Map<LocalDate,Map<BigInteger,List<StaffingLevelActivityWithDuration>>> findUnderStaffingOverStaffingByActivityIdAndDate(Long unitId) {

        List<OpenShiftRuleTemplateDTO> openShiftRuleTemplates = openShiftRuleTemplateRepository.findOpenShiftRuleTemplatesWithInterval(unitId);
        Map<LocalDate,Set<BigInteger>> dateActivityIdsMap = new HashMap<>();

        Set<LocalDate> allOpenShiftRuleTemplateLocalDates = new HashSet<>();
        for(OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO: openShiftRuleTemplates) {
            List<LocalDate> localDates = getOpenShiftRuleTemplateDates(openShiftRuleTemplateDTO.getOpenShiftInterval());
            Set<BigInteger> activityIds = openShiftRuleTemplateDTO.getActivitiesPerTimeTypes().stream().map(activitiesPerTimeType ->
                    activitiesPerTimeType.getSelectedActivities()).flatMap(selectedActivities->selectedActivities.stream()).
                    collect(Collectors.toSet());
            allOpenShiftRuleTemplateLocalDates.addAll(localDates);

            for(LocalDate localDate: localDates) {
                if(!dateActivityIdsMap.containsKey(localDate)) {
                    dateActivityIdsMap.put(localDate,activityIds);
                } else {
                    dateActivityIdsMap.get(localDate).addAll(activityIds);
                }
            }
        }

        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId,allOpenShiftRuleTemplateLocalDates);
        LocalDateTime minLocalDateTime = DateUtils.getStartOfDayFromLocalDate(allOpenShiftRuleTemplateLocalDates.stream().min(LocalDate::compareTo).get());
        LocalDateTime maxLocalDateTime = DateUtils.getEndOfDayFromLocalDate(allOpenShiftRuleTemplateLocalDates.stream().min(LocalDate::compareTo).get());
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDuration(minLocalDateTime,maxLocalDateTime,unitId);

        Map<LocalDate,Set<Shift>> shiftsLocalDateMap = getShiftsLocalDateMap(shifts);


        return getDateAndFilteredActivityWithDurationMap(staffingLevels,dateActivityIdsMap,shiftsLocalDateMap);
    }

    private List<StaffingLevelActivityWithDuration> getFilteredActivityWithDurations(StaffingLevel staffingLevel,List<StaffingLevelActivityWithDuration> staffingLevelActivityWithDurations,Set<Shift> shiftsLocal) {
        List<StaffingLevelActivityWithDuration> filteredActivityWithDurations = new ArrayList<>();
        int currentCount = 0;
        StaffingLevelActivityWithDuration filteredActivityWithDuration =  null;
        StaffingLevelActivityWithDuration lastStaffingLevelActivityWithDuration = null;

        for(StaffingLevelActivityWithDuration staffingLevelActivityWithDuration : staffingLevelActivityWithDurations) {
            ZonedDateTime startDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelActivityWithDuration.getStaffingLevelDuration().getFrom());
            ZonedDateTime endDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelActivityWithDuration.getStaffingLevelDuration().getTo());
            DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
            int count = 0;
            for(Shift shift:shiftsLocal) {
                if (interval.overlaps(shift.getInterval())) {
                    count++;
                }
            }
            int min = staffingLevelActivityWithDuration.getMinNoOfStaff();
            int max = staffingLevelActivityWithDuration.getMaxNoOfStaff();
            if(count>max) {
                staffingLevelActivityWithDuration.setUnderStaffingOverStaffingCount(max-count);
            } else if(count>min) {
                staffingLevelActivityWithDuration.setUnderStaffingOverStaffingCount(0);
            } else {
                staffingLevelActivityWithDuration.setUnderStaffingOverStaffingCount(min - count);
            }

            if(filteredActivityWithDurations.isEmpty()&&!Optional.ofNullable(filteredActivityWithDuration).isPresent()) {
                filteredActivityWithDuration = new StaffingLevelActivityWithDuration(staffingLevelActivityWithDuration);
                currentCount = staffingLevelActivityWithDuration.getUnderStaffingOverStaffingCount();
            } else {
                if(currentCount!= staffingLevelActivityWithDuration.getUnderStaffingOverStaffingCount()) {
                    filteredActivityWithDuration.getStaffingLevelDuration().setTo(staffingLevelActivityWithDuration.getStaffingLevelDuration().getFrom());
                    filteredActivityWithDurations.add(filteredActivityWithDuration);
                    filteredActivityWithDuration = new StaffingLevelActivityWithDuration(staffingLevelActivityWithDuration);
                    currentCount = staffingLevelActivityWithDuration.getUnderStaffingOverStaffingCount();
                }
            }
            lastStaffingLevelActivityWithDuration = staffingLevelActivityWithDuration;
        }
        filteredActivityWithDuration.getStaffingLevelDuration().setTo(lastStaffingLevelActivityWithDuration.getStaffingLevelDuration().getTo());
        filteredActivityWithDurations.add(filteredActivityWithDuration);
        return filteredActivityWithDurations;
    }

    private Map<LocalDate,Map<BigInteger,List<StaffingLevelActivityWithDuration>>> getDateAndFilteredActivityWithDurationMap(List<StaffingLevel>staffingLevels,Map<LocalDate,Set<BigInteger>> dateActivityIdsMap,Map<LocalDate,Set<Shift>> shiftsLocalDateMap) {
        Map<BigInteger,List<StaffingLevelActivityWithDuration>> staffingLevelIntervalActivityIdMap ;
        Map<LocalDate,Map<BigInteger,List<StaffingLevelActivityWithDuration>>> dateFilteredActivityWithDurationsMap = new HashMap<>();

        for(StaffingLevel staffingLevel:staffingLevels) {
            staffingLevelIntervalActivityIdMap = new HashMap<>();
            Set<BigInteger> activityIds = dateActivityIdsMap.get(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()));

            Map<BigInteger, List<StaffingLevelActivityWithDuration>> activityWithDuration =
                    staffingLevel.getPresenceStaffingLevelInterval().stream().
                            flatMap((StaffingLevelInterval currentInterval) -> currentInterval.getStaffingLevelActivities().stream()
                                    .filter((staffingLevelActivity -> activityIds.contains(staffingLevelActivity.getActivityId())))
                                    .map((StaffingLevelActivity currentActivity) -> new StaffingLevelActivityWithDuration(currentActivity.getActivityId(), currentActivity.getMinNoOfStaff(), currentActivity.getMaxNoOfStaff(), currentInterval.getStaffingLevelDuration()))
                            ).collect(Collectors.groupingBy(ele -> ele.getActivityId()));

            for(Map.Entry<BigInteger,List<StaffingLevelActivityWithDuration>>entry:activityWithDuration.entrySet()) {
                Set<Shift> shiftsLocal =  shiftsLocalDateMap.get(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()));
                if(Optional.ofNullable(shiftsLocal).isPresent()){
                    shiftsLocal.stream().filter(shift -> shift.getActivityId().equals(entry.getKey()));
                } else {
                    shiftsLocal = Collections.emptySet();
                }
                staffingLevelIntervalActivityIdMap.put(entry.getKey(),getFilteredActivityWithDurations(staffingLevel,entry.getValue(),shiftsLocal));
                dateFilteredActivityWithDurationsMap.put(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()), staffingLevelIntervalActivityIdMap);
            }
        }
        return dateFilteredActivityWithDurationsMap;
    }
    private void insertInShiftsLocalDateMap(Map<LocalDate,Set<Shift>> shiftsLocalDateMap,LocalDate localDate,Shift shift) {
        if(!shiftsLocalDateMap.containsKey(localDate)) {
            shiftsLocalDateMap.put(localDate, Stream.of(shift).collect(Collectors.toSet()));
        } else {
            shiftsLocalDateMap.get(localDate).add(shift);
        }
    }

    private Map<LocalDate,Set<Shift>> getShiftsLocalDateMap(List<Shift> shifts) {

        Map<LocalDate,Set<Shift>> shiftsLocalDateMap = new HashMap<>();

        for(Shift shift: shifts) {
            LocalDate startLocalDate = DateUtils.asLocalDate(shift.getStartDate());
            LocalDate endLocalDate = DateUtils.asLocalDate(shift.getEndDate());
            insertInShiftsLocalDateMap(shiftsLocalDateMap,startLocalDate,shift);
            if(!startLocalDate.equals(endLocalDate)) {
                insertInShiftsLocalDateMap(shiftsLocalDateMap, endLocalDate, shift);
            }
        }
        return shiftsLocalDateMap;
    }
    private List<LocalDate> getOpenShiftRuleTemplateDates(OpenShiftInterval openShiftInterval) {

        List<LocalDate> localDates = new ArrayList<>();
        if (openShiftInterval.getType().equals(DurationType.DAYS)) {
            for (int i = openShiftInterval.getFrom(); i <= openShiftInterval.getTo(); i++) {
                localDates.add(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(i).toLocalDate());
            }
        } else {
            LocalDate startLocalDate = LocalDateTime.now().plusHours(openShiftInterval.getFrom()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endLocalDate = LocalDateTime.now().plusHours(openShiftInterval.getTo()).atZone(ZoneId.systemDefault()).toLocalDate();
            localDates.add(startLocalDate);

            if (!startLocalDate.equals(endLocalDate)) {
                localDates.add(endLocalDate);
            }
        }
        return localDates;
    }

}


