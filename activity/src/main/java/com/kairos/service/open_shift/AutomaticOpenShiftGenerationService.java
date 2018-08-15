package com.kairos.service.open_shift;

import com.kairos.activity.staffing_level.StaffingLevelActivity;
import com.kairos.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.activity.staffing_level.StaffingLevelInterval;
import com.kairos.persistence.model.activity.Shift;
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

    public void findUnderStaffingByActivityId(Long unitId) {

        List<OpenShiftRuleTemplateDTO> openShiftRuleTemplates = openShiftRuleTemplateRepository.findOpenShiftRuleTemplatesWithInterval(unitId);
        Map<LocalDate,Set<Long>> dateActivityIdsMap = new HashMap<>();

        Map<LocalDate,Set<Shift>> shiftsLocalDateMap = new HashMap<LocalDate, Set<Shift>>();
        for(OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO: openShiftRuleTemplates) {
            List<LocalDate> localDates = getDate(openShiftRuleTemplateDTO.getOpenShiftInterval(),"Asia/Kolkata");
            Set<Long> activityIds = openShiftRuleTemplateDTO.getActivitiesPerTimeTypes().stream().map(activitiesPerTimeType -> activitiesPerTimeType.getSelectedActivities()).flatMap(selectedActivities->selectedActivities.stream().map(BigInteger::longValue)).collect(Collectors.toSet());

            for(LocalDate localDate: localDates) {
                if(!dateActivityIdsMap.containsKey(localDate)) {
                    dateActivityIdsMap.put(localDate,activityIds);
                }
                else {
                    dateActivityIdsMap.get(localDate).addAll(activityIds);
                   // dateActivityIdsMap.put(localDate,dateActivityIdsMap.get(localDate));
                }
            }
        }
        //TODO compile list of localdates in the previous loop itself
        List<LocalDate> localDates =  openShiftRuleTemplates.stream().map(openShiftRuleTemplateDTO -> getDate(openShiftRuleTemplateDTO.getOpenShiftInterval(),
                "")).flatMap(localDatesStream -> localDatesStream.stream()).collect(Collectors.toList());
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndCurrentDate(unitId,localDates);
        LocalDateTime minLocalDateTime = DateUtils.getStartOfDayFromLocalDate(localDates.stream().min(LocalDate::compareTo).get());
        LocalDateTime maxLocalDateTime = DateUtils.getEndOfDayFromLocalDate(localDates.stream().min(LocalDate::compareTo).get());
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDuration(minLocalDateTime,maxLocalDateTime,unitId);

        for(Shift shift: shifts) {
            LocalDate localDate1 = DateUtils.asLocalDate(shift.getStartDate());
            LocalDate localDate2 = DateUtils.asLocalDate(shift.getEndDate());
            insertInShiftsLocalDateMap(shiftsLocalDateMap,localDate1,shift);
            if(!localDate1.equals(localDate2)) {
                insertInShiftsLocalDateMap(shiftsLocalDateMap, localDate2, shift);
            }
        }
        Map<Long,List<StaffingLevelActivityWithDuration>> staffingLevelIntervalActivityIdMap = new HashMap<Long,List<StaffingLevelActivityWithDuration>>();
        Map<LocalDate,Map<Long,List<StaffingLevelActivityWithDuration>>> dateFilteredActivityWithDurationsMap = new HashMap<>();




        for(StaffingLevel staffingLevel:staffingLevels) {

            staffingLevelIntervalActivityIdMap = new HashMap<Long,List<StaffingLevelActivityWithDuration>>();
            Set<Long> activityIds = dateActivityIdsMap.get(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()));
            List<StaffingLevelActivityWithDuration> activityWithDurationsList = staffingLevel.getPresenceStaffingLevelInterval().stream().flatMap((StaffingLevelInterval currentInterval) -> currentInterval.getStaffingLevelActivities().stream()
                    .map((StaffingLevelActivity currentActivity) -> new StaffingLevelActivityWithDuration(currentActivity.getActivityId(), currentActivity.getMinNoOfStaff(), currentActivity.getMaxNoOfStaff(), currentInterval.getStaffingLevelDuration()))
            ).collect(Collectors.toList());

            Map<Long, List<StaffingLevelActivityWithDuration>> activityWithDuration =
                    staffingLevel.getPresenceStaffingLevelInterval().stream().
                            flatMap((StaffingLevelInterval currentInterval) -> currentInterval.getStaffingLevelActivities().stream()
                                    .filter((staffingLevelActivity -> activityIds.contains(staffingLevelActivity.getActivityId())))
                                    .map((StaffingLevelActivity currentActivity) -> new StaffingLevelActivityWithDuration(currentActivity.getActivityId(), currentActivity.getMinNoOfStaff(), currentActivity.getMaxNoOfStaff(), currentInterval.getStaffingLevelDuration()))
                            ).collect(Collectors.groupingBy(ele -> ele.getActivityId()));

            for(Map.Entry<Long,List<StaffingLevelActivityWithDuration>>entry:activityWithDuration.entrySet()) {
               Set<Shift> shiftsLocal =  shiftsLocalDateMap.get(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()));
               if(Optional.ofNullable(shiftsLocal).isPresent()){
                   shiftsLocal.stream().filter(shift -> shift.getActivityId().equals(BigInteger.valueOf(entry.getKey())));
               }
               else {
                   shiftsLocal = Collections.emptySet();
               }

               List<StaffingLevelActivityWithDuration> filteredActivityWithDurations = new ArrayList<>();
               int currentCount = 0;
                StaffingLevelActivityWithDuration filteredActivityWithDuration =  null;
                StaffingLevelActivityWithDuration lastStaffingLevelActivityWithDuration = null;

               for(StaffingLevelActivityWithDuration staffingLevelActivityWithDuration : entry.getValue()) {
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

                        }
                        else if(count>min) {
                            staffingLevelActivityWithDuration.setUnderStaffingOverStaffingCount(0);
                        }
                        else {
                            staffingLevelActivityWithDuration.setUnderStaffingOverStaffingCount(min - count);
                        }

                        if(filteredActivityWithDurations.isEmpty()&&!Optional.ofNullable(filteredActivityWithDuration).isPresent()) {
                            filteredActivityWithDuration = new StaffingLevelActivityWithDuration(staffingLevelActivityWithDuration);
                            currentCount = staffingLevelActivityWithDuration.getUnderStaffingOverStaffingCount();

                        }
                        else {
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
                staffingLevelIntervalActivityIdMap.put(entry.getKey(),filteredActivityWithDurations);
                dateFilteredActivityWithDurationsMap.put(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()), staffingLevelIntervalActivityIdMap);

            }

        }





    }
    public void insertInShiftsLocalDateMap(Map<LocalDate,Set<Shift>> shiftsLocalDateMap,LocalDate localDate,Shift shift) {
        if(!shiftsLocalDateMap.containsKey(localDate)) {
            shiftsLocalDateMap.put(localDate, Stream.of(shift).collect(Collectors.toSet()));
        }
        else {
            shiftsLocalDateMap.get(localDate).add(shift);

        }
    }
    public List<LocalDate> getDate(OpenShiftInterval openShiftInterval,String timezone) {

        List<LocalDate> localDates = new ArrayList<LocalDate>();
        if (openShiftInterval.getType().toString().equals("DAYS")) {
            for (int i = openShiftInterval.getFrom(); i <= openShiftInterval.getTo(); i++) {
                localDates.add(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(i).toLocalDate());
            }
        } else {
            LocalDate localDate1 = LocalDateTime.now().plusHours(openShiftInterval.getFrom()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDate2 = LocalDateTime.now().plusHours(openShiftInterval.getTo()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (localDate1.equals(localDate2)) {
                localDates.add(localDate1);
            } else {
                localDates.add(localDate1);
                localDates.add(localDate2);
            }
        }
        return localDates;
    }

}


