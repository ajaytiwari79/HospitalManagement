package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.TimeSlot;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.getValueByPhaseAndCounter;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.isValid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
public class NoOfSequenceShiftWTATemplate extends WTABaseRuleTemplate{

    //private int sequence;
    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private boolean restingTimeAllowed;
    private int restingTime;
    private PartOfDay sequenceShiftFrom;
    private PartOfDay sequenceShiftTo;

    private List<BigInteger> timeTypeIds = new ArrayList<>();

    public NoOfSequenceShiftWTATemplate() {
        wtaTemplateType=WTATemplateType.NO_OF_SEQUENCE_SHIFT;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if(!isDisabled() && CollectionUtils.containsAny(timeTypeIds,shiftImp.getActivitiesTimeTypeIds())){
            TimeSlot timeSlotWrapper = getTimeSlotWrapper(unit, shiftImp);
            if(isNotNull(timeSlotWrapper)) {
                int totalOccurrencesSequenceShift = getOccurrencesSequenceShift(shiftImps,shiftImp,unit);
                int limit = getValueByPhaseAndCounter(unit, getPhaseTemplateValues());
                penality = isValid(MAXIMUM, limit, totalOccurrencesSequenceShift);
            }
        }
        return penality;
    }

    private int getOccurrencesSequenceShift(List<ShiftImp> shiftImps,ShiftImp shiftImp,Unit unit){
        int totalOccurrencesSequenceShift = 0;
        shiftImps.add(shiftImp);
        shiftImps = shiftImps.stream().sorted(Comparator.comparing(k->k.getStart())).collect(Collectors.toList());
        for(int i=0; i<shiftImps.size()-1; i++){
            TimeSlot timeSlot = getTimeSlotWrapper(unit, shiftImps.get(i));
            TimeSlot nextTimeSlot = getTimeSlotWrapper(unit, shiftImps.get(i+1));
            List<PartOfDay> partOfDays = newArrayList(sequenceShiftFrom,sequenceShiftTo);
            if(partOfDays.contains(PartOfDay.valueOf(timeSlot.getName().toUpperCase())) && partOfDays.contains(PartOfDay.valueOf(nextTimeSlot.getName().toUpperCase())) && !timeSlot.getName().equals(nextTimeSlot.getName())){
                Period period = Period.between(shiftImps.get(i).getStartDate(), shiftImps.get(i+1).getStartDate());
                if(period.getDays() < 2) {
                    totalOccurrencesSequenceShift++;
                }
            }
        }
        return totalOccurrencesSequenceShift;
    }

    private TimeSlot getTimeSlotWrapper(Unit unit, ShiftImp shift){
        TimeSlot timeSlot = null;
        for (Map.Entry<String, TimeSlot> stringTimeSlotWrapperEntry : unit.getTimeSlotMap().entrySet()) {
            timeSlot = stringTimeSlotWrapperEntry.getValue();
            int endMinutesOfInterval = (timeSlot.getEndHour() * 60) + timeSlot.getEndMinute();
            int startMinutesOfInterval = (timeSlot.getStartHour() * 60) + timeSlot.getStartMinute();
            TimeInterval interval = new TimeInterval(startMinutesOfInterval, endMinutesOfInterval);
            int minuteOfTheDay = shift.getStart().get(ChronoField.MINUTE_OF_DAY);
            if (minuteOfTheDay == (int) interval.getStartFrom() || interval.contains(minuteOfTheDay)) {
                break;
            }else{
                timeSlot = null;
            }
        }
        return timeSlot;
    }


}
