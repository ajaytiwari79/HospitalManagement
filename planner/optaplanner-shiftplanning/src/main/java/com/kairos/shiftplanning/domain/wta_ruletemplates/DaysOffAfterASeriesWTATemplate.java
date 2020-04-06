package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.ExpertiseNightWorkerSetting;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DaysOffAfterASeriesWTATemplate extends WTABaseRuleTemplate {

    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private int nightShiftSequence;
    private boolean restingTimeAllowed;
    private int restingTime;

    public DaysOffAfterASeriesWTATemplate() {
        this.wtaTemplateType = WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if(!isDisabled() && shiftImp.getEmployee().isNightWorker() && isNotNull(shiftImp.getEmployee().getExpertiseNightWorkerSetting())){
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(shiftImp,this.intervalUnit,this.intervalLength);
            shiftImps.add(shiftImp);
            List<ShiftImp> nightShifts = getNightMinutesOrCount(shiftImp.getEmployee().getExpertiseNightWorkerSetting(),shiftImps,dateTimeInterval);
            List<LocalDate> shiftDates = getSortedAndUniqueLocalDates(nightShifts);
            LocalDate shiftDate = shiftImp.getStart().toLocalDate();
            boolean currentNightShift = shiftDates.removeIf(date -> date.equals(shiftDate));
            int consecutiveNightDays = getConsecutiveDaysInDate(new ArrayList<>(shiftDates));
            if(currentNightShift){
                shiftDates.add(shiftDate);
            }
            int daysOffCount = 0;
            int limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
            penality = validate(shiftDates, shiftDate, currentNightShift, consecutiveNightDays, daysOffCount);
        }
        return penality;
    }

    private int validate(List<LocalDate> shiftDates, LocalDate shiftDate, boolean currentNightShift, int consecutiveNightDays, int daysOffCount) {
        int penality = 0;
        if(currentNightShift && consecutiveNightDays>=nightShiftSequence){
            LocalDate daysOffDate = shiftDate.minusDays(restingTime);
            while (!daysOffDate.isAfter(shiftDate)){
                if(!shiftDates.contains(daysOffDate)){
                    daysOffCount++;
                }
                daysOffDate = daysOffDate.plusDays(1);
            }
            penality = isValid(MinMaxSetting.MINIMUM, restingTime, daysOffCount);
            if(penality==0){
                daysOffDate = shiftDate.plusDays(restingTime);
                while (!daysOffDate.isBefore(shiftDate)){
                    if(!shiftDates.contains(daysOffDate)){
                        daysOffCount++;
                    }
                    daysOffDate = daysOffDate.minusDays(1);
                }
                penality = isValid(MinMaxSetting.MINIMUM, restingTime, daysOffCount);
            }
        }
        return penality;
    }

    public DaysOffAfterASeriesWTATemplate(String name, boolean disabled, String description, long intervalLength, String intervalUnit, int nightShiftSequence) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.nightShiftSequence = nightShiftSequence;
        wtaTemplateType=WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }

    private List<ShiftImp> getNightMinutesOrCount(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, List<ShiftImp> shiftImps, DateTimeInterval dateTimeInterval) {
        List<ShiftImp> nightShifts = new ArrayList<>();
        for (ShiftImp shiftImp : shiftImps) {
            if (dateTimeInterval.contains(shiftImp.getStartDate())) {
                DateTimeInterval nightInterval = getNightInterval(shiftImp.getStart(), expertiseNightWorkerSetting.getTimeSlot());
                if (nightInterval.overlaps(shiftImp.getInterval())) {
                    int overlapMinutes = (int) nightInterval.overlap(shiftImp.getInterval()).getMinutes();
                    if (overlapMinutes >= expertiseNightWorkerSetting.getMinMinutesToCheckNightShift()) {
                        nightShifts.add(shiftImp);
                    }
                }
            }
        }
        return nightShifts;
    }

}
