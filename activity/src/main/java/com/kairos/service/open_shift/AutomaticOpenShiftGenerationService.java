package com.kairos.service.open_shift;

import com.kairos.activity.staffing_level.StaffingLevelActivity;
import com.kairos.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.activity.staffing_level.StaffingLevelInterval;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.open_shift.OpenShiftInterval;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.open_shift.OpenShiftRuleTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.util.DateUtils;
import org.bouncycastle.util.test.FixedSecureRandom;

import javax.inject.Inject;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.math.BigInteger;
import java.util.Map;

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

        List<OpenShiftRuleTemplateDTO> openShiftRuleTemplates = openShiftRuleTemplateRepository.findOpenShiftRuleTemplatesWithIntervalByUnitID(unitId);
        Map<LocalDate,List<BigInteger>> dateActivityIdsMap = new HashMap<LocalDate,List<BigInteger>>();

        for(OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO: openShiftRuleTemplates) {
            List<LocalDate> localDates = getDate(openShiftRuleTemplateDTO.getOpenShiftInterval(),"Asia/Kolkata");
            for(LocalDate localDate: localDates) {
                List<BigInteger> activityIds = openShiftRuleTemplateDTO.getActivitiesPerTimeTypes().stream().map(activitiesPerTimeType -> activitiesPerTimeType.getSelectedActivities()).flatMap(selectedActivities->selectedActivities.stream()).collect(Collectors.toList());
                if(!dateActivityIdsMap.containsKey(localDate)) {
                    dateActivityIdsMap.put(localDate,activityIds);
                }
                else {
                    dateActivityIdsMap.get(localDate).addAll(activityIds);
                    dateActivityIdsMap.put(localDate,dateActivityIdsMap.get(localDate));
                }
            }
        }
        List<LocalDate> localDates =  openShiftRuleTemplates.stream().map(openShiftRuleTemplateDTO -> getDate(openShiftRuleTemplateDTO.getOpenShiftInterval(),"")).flatMap(localDatesStream -> localDatesStream.stream()).collect(Collectors.toList());
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndCurrentDate(unitId,localDates);
        LocalDateTime minLocalDateTime = DateUtils.getStartOfDayFromLocalDate(localDates.stream().min(LocalDate::compareTo).get());
        LocalDateTime maxLocalDateTime = DateUtils.getEndOfDayFromLocalDate(localDates.stream().min(LocalDate::compareTo).get());
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDuration(minLocalDateTime,maxLocalDateTime,unitId);




        for(StaffingLevel staffingLevel:staffingLevels) {

            List<BigInteger> activityIds = dateActivityIdsMap.get(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()));
            Map<Long, List<StaffingLevelActivityWithDuration>> activityWithDuration =
                    staffingLevel.getPresenceStaffingLevelInterval().stream().filter(staffingLevelInterval ->
                            (staffingLevelInterval.getMaxNoOfStaff() > 0)).
                            flatMap((StaffingLevelInterval currentInterval) -> currentInterval.getStaffingLevelActivities().stream()
                                    .filter((staffingLevelActivity -> activityIds.contains(staffingLevelActivity.getActivityId())))
                                    .map((StaffingLevelActivity currentActivity) -> new StaffingLevelActivityWithDuration(currentActivity.getActivityId(), currentActivity.getMinNoOfStaff(), currentActivity.getMaxNoOfStaff(), currentInterval.getStaffingLevelDuration()))
                            ).collect(Collectors.groupingBy(ele -> ele.getActivityId()));

            for(Map.Entry<Long,List<StaffingLevelActivityWithDuration>>entry:activityWithDuration.entrySet()) {
               List<Shift> shiftsLcoal =  shifts.stream().filter(shift -> shift.getActivityId().equals(entry.getKey())&& DateUtils.asLocalDate(shift.getStartDate()).
                        equals(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()))|| DateUtils.asLocalDate(shift.getStartDate()).
                                equals(DateUtils.getLocalDateFromDate(staffingLevel.getCurrentDate()))).collect(Collectors.toList());
                for(StaffingLevelActivityWithDuration staffingLevelActivityWithDuration : entry.getValue()) {


                }
            }

        }





    }
    public List<LocalDate> getDate(OpenShiftInterval openShiftInterval,String timezone) {

        List<LocalDate> localDates = new ArrayList<LocalDate>();
        if (openShiftInterval.getType().equals("DAYS")) {
            for (int i = openShiftInterval.getFrom(); i <= openShiftInterval.getTo(); i++) {
                localDates.add(LocalDate.now().plusDays(i).atStartOfDay(ZoneId.of(timezone)).toLocalDate());
            }
        } else {
            LocalDate localDate1 = LocalDateTime.now().plusHours(openShiftInterval.getFrom()).atZone(ZoneId.of(timezone)).toLocalDate();
            LocalDate localDate2 = LocalDateTime.now().plusHours(openShiftInterval.getTo()).atZone(ZoneId.of(timezone)).toLocalDate();
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


