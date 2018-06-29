package com.kairos.spec.night_shift;

import com.kairos.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.activity.night_worker.ShiftAndExpertiseNightWorkerSettingDTO;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.enums.DurationType;
import com.kairos.spec.AbstractActivitySpecification;
import com.kairos.user.country.time_slot.TimeSlot;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;

import javax.management.timer.Timer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class ValidNightShiftSpecification extends AbstractActivitySpecification<ShiftAndExpertiseNightWorkerSettingDTO> {

    private Map<Long, List<ShiftQueryResult>> expertiseWiseShifts = new HashMap<>();

    private Long addHoursAndMinutes(Long millis, int minutes, int hours){
        return millis + (hours * Timer.ONE_HOUR) + (minutes * Timer.ONE_MINUTE);
    }

    private boolean checkNightShiftIsForValidDuration(Date shiftStartDate, DurationType durationType, Integer intervalValue) {

        ZonedDateTime zonedDateTime =  ZonedDateTime.of(DateUtils.dateToLocalDateTime(shiftStartDate), ZoneId.systemDefault());
        switch (durationType){
            case HOURS: {
                zonedDateTime.plusHours(intervalValue);
                break;
            }
            case MONTHS: {
                zonedDateTime.plusMonths(intervalValue);
                break;
            }
            case WEEKS: {
                zonedDateTime.plusWeeks(intervalValue);
                break;
            }
            case DAYS: {
                zonedDateTime.plusDays(intervalValue);
                break;
            }
        }
        return zonedDateTime.isAfter(ZonedDateTime.now(ZoneId.systemDefault()));
    }

    private Long getNightWorkingMillis(TimeSlot timeSlot, ShiftQueryResult shiftQueryResult, ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettings){
        boolean isNightShift = false;
        Long nightWorkingMillis =0L;
        if( ! checkNightShiftIsForValidDuration(DateUtils.getDate(shiftQueryResult.getStartDate()), expertiseNightWorkerSettings.getIntervalUnitToCheckNightWorker(),
                expertiseNightWorkerSettings.getIntervalValueToCheckNightWorker())){
            return 0L;
        }

        DateTimeInterval shiftDateTimeInterval = new DateTimeInterval(shiftQueryResult.getStartDate(), shiftQueryResult.getEndDate());
        LocalDate startLocalDate = DateUtils.getLocalDate(shiftQueryResult.getStartDate());
        LocalDate endLocalDate = DateUtils.getLocalDate(shiftQueryResult.getEndDate());

        DateTimeInterval dateTimeInterval = new DateTimeInterval(
                addHoursAndMinutes(DateUtils.convertLocalDateToDate(startLocalDate).getTime(), timeSlot.getStartHour(), timeSlot.getStartMinute()),
                addHoursAndMinutes(DateUtils.convertLocalDateToDate(endLocalDate).getTime(), timeSlot.getEndHour(), timeSlot.getEndMinute()));

        // If shift time overlaps to defined time slot in rule
        if (dateTimeInterval.overlaps(shiftDateTimeInterval)) {
            nightWorkingMillis = Math.min(shiftDateTimeInterval.getEndMillis(), dateTimeInterval.getEndMillis()) -
                    Math.max(shiftDateTimeInterval.getStartMillis(), dateTimeInterval.getStartMillis());
            // If night shift satisfies the required min time for being night shift
            isNightShift = ( nightWorkingMillis >= (expertiseNightWorkerSettings.getMinMinutesToCheckNightShift() * Timer.ONE_MINUTE));
        }
        return (isNightShift ? nightWorkingMillis : 0L);
    }


    private boolean checkNightWorkerStatusByExpertise(int totalShifts, Long totalNightWorkingMillis, List<ShiftQueryResult> applicableShifts,
                                                      ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettings){

        switch (expertiseNightWorkerSettings.getMinShiftsUnitToCheckNightWorker()){
            case HOURS: {
                return (expertiseNightWorkerSettings.getMinShiftsValueToCheckNightWorker() * Timer.ONE_HOUR <= totalNightWorkingMillis);
            }
            case PERCENTAGE:{
                return ( ((applicableShifts.size() * totalShifts)/100) >= expertiseNightWorkerSettings.getMinShiftsValueToCheckNightWorker() );
            }
        }
        return true;
    }

    private boolean checkNightWorkerStatusByExpertiseId(List<ShiftQueryResult> shifts, ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettings){
        Long totalNightWorkingMillis = 0L;
        List<ShiftQueryResult> applicalNightShifts = new ArrayList<>();
        List<Long> listOfNightWorkingMillis = new ArrayList<>();

        shifts.forEach(shiftQueryResult -> {
            Long nightShiftMillis = getNightWorkingMillis(expertiseNightWorkerSettings.getTimeSlot(), shiftQueryResult, expertiseNightWorkerSettings);
            if( nightShiftMillis > 0){
                applicalNightShifts.add(shiftQueryResult);
                listOfNightWorkingMillis.add(nightShiftMillis);
            }
        });
        return checkNightWorkerStatusByExpertise(shifts.size(), totalNightWorkingMillis, shifts, expertiseNightWorkerSettings);
    }

    @Override
    public boolean isSatisfied(ShiftAndExpertiseNightWorkerSettingDTO shiftAndNightWorkerSettings) {

        boolean isNightWorker = false;
        shiftAndNightWorkerSettings.getShifts().stream().forEach(shiftQueryResult -> {
            Long expertiseId = shiftQueryResult.getExpertiseId();
            if(expertiseWiseShifts.containsKey(expertiseId)){
                expertiseWiseShifts.get(expertiseId).add(shiftQueryResult);
            } else {
                expertiseWiseShifts.put(expertiseId, new ArrayList<>(Arrays.asList(shiftQueryResult)));
            }
        });

        Iterator entries = expertiseWiseShifts.entrySet().iterator();

        for (Long key : expertiseWiseShifts.keySet()) {
            if(checkNightWorkerStatusByExpertiseId(expertiseWiseShifts.get(key), shiftAndNightWorkerSettings.getNightWorkerSettings().get(key))){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> isSatisfiedString(ShiftAndExpertiseNightWorkerSettingDTO shiftAndExpertiseNightWorkerSettingDTO) {
        return Collections.EMPTY_LIST;
    }

}
