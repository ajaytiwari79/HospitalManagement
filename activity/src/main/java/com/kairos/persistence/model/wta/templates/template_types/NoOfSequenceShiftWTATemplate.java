package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.enums.wta.MinMaxSetting.MAXIMUM;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.brakeRuleTemplateAndUpdateViolationDetails;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NoOfSequenceShiftWTATemplate extends WTABaseRuleTemplate{

    //private int sequence;
    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private int allowSequenceShift;
    private boolean restingTimeAllowed;
    private int restingTime;
    private PartOfDay sequenceShiftFrom;
    private PartOfDay sequenceShiftTo;

    private List<BigInteger> timeTypeIds = new ArrayList<>();

    public NoOfSequenceShiftWTATemplate() {
        wtaTemplateType=WTATemplateType.NO_OF_SEQUENCE_SHIFT;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && CollectionUtils.containsAny(timeTypeIds,infoWrapper.getShift().getActivitiesTimeTypeIds())){
            TimeSlotWrapper timeSlotWrapper = getTimeSlotWrapper(infoWrapper, infoWrapper.getShift());
            if(isNotNull(timeSlotWrapper)) {
                int totalOccurrencesSequenceShift = getOccurrencesSequenceShift(infoWrapper);
                Integer[] limitAndCounter = getValueByPhaseAndCounter(infoWrapper, getPhaseTemplateValues(), this);
                boolean isValid = isValid(MAXIMUM, allowSequenceShift, totalOccurrencesSequenceShift);
                brakeRuleTemplateAndUpdateViolationDetails(infoWrapper,limitAndCounter[1],isValid, this,
                        limitAndCounter[2], DurationType.DAYS,String.valueOf(allowSequenceShift));
            }
        }
    }

    private int getOccurrencesSequenceShift(RuleTemplateSpecificInfo infoWrapper){
        int totalOccurrencesSequenceShift = 0;
        List<ShiftWithActivityDTO> shifts = infoWrapper.getShifts();
        shifts.add(infoWrapper.getShift());
        shifts = infoWrapper.getShifts().stream().sorted(Comparator.comparing(k->k.getStartDate())).collect(Collectors.toList());
        for(int i=0; i<shifts.size()-1; i++){
            TimeSlotWrapper timeSlot = getTimeSlotWrapper(infoWrapper, shifts.get(i));
            TimeSlotWrapper nextTimeSlot = getTimeSlotWrapper(infoWrapper, shifts.get(i+1));
            List<PartOfDay> partOfDays = newArrayList(sequenceShiftFrom,sequenceShiftTo);
            if(partOfDays.contains(PartOfDay.valueOf(timeSlot.getName().toUpperCase())) && partOfDays.contains(PartOfDay.valueOf(nextTimeSlot.getName().toUpperCase())) && !timeSlot.getName().equals(nextTimeSlot.getName())){
                Period period = Period.between(asLocalDate(shifts.get(i).getStartDate()), asLocalDate(shifts.get(i+1).getStartDate()));
                if(period.getDays() < 2) {
                    totalOccurrencesSequenceShift++;
                }
            }
        }
        return totalOccurrencesSequenceShift;
    }

    private TimeSlotWrapper getTimeSlotWrapper(RuleTemplateSpecificInfo infoWrapper, ShiftWithActivityDTO shift){
        TimeSlotWrapper timeSlotWrapper = null;
        for (String key : infoWrapper.getTimeSlotWrapperMap().keySet()) {
            timeSlotWrapper = infoWrapper.getTimeSlotWrapperMap().get(key);
            int endMinutesOfInterval = (timeSlotWrapper.getEndHour() * 60) + timeSlotWrapper.getEndMinute();
            int startMinutesOfInterval = (timeSlotWrapper.getStartHour() * 60) + timeSlotWrapper.getStartMinute();
            TimeInterval interval = new TimeInterval(startMinutesOfInterval, endMinutesOfInterval);
            int minuteOfTheDay = DateUtils.asZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY);
            if (minuteOfTheDay == (int) interval.getStartFrom() || interval.contains(minuteOfTheDay)) {
                break;
            }else{
                timeSlotWrapper = null;
            }
        }
        return timeSlotWrapper;
    }

    public NoOfSequenceShiftWTATemplate(String name, boolean disabled, String description,  PartOfDay sequenceShiftFrom, PartOfDay sequenceShiftTo, long intervalLength, String intervalUnit) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.wtaTemplateType = WTATemplateType.NO_OF_SEQUENCE_SHIFT;
        //this.sequence=sequence;
        this.sequenceShiftTo = sequenceShiftTo;
        this.sequenceShiftFrom = sequenceShiftFrom;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        NoOfSequenceShiftWTATemplate noOfSequenceShiftWTATemplate = (NoOfSequenceShiftWTATemplate)wtaBaseRuleTemplate;
        return (this != noOfSequenceShiftWTATemplate) && !(restingTimeAllowed == noOfSequenceShiftWTATemplate.restingTimeAllowed &&
                restingTime == noOfSequenceShiftWTATemplate.restingTime &&
                sequenceShiftFrom == noOfSequenceShiftWTATemplate.sequenceShiftFrom &&
                sequenceShiftTo == noOfSequenceShiftWTATemplate.sequenceShiftTo &&
                Objects.equals(timeTypeIds, noOfSequenceShiftWTATemplate.timeTypeIds) && Objects.equals(this.phaseTemplateValues,noOfSequenceShiftWTATemplate.phaseTemplateValues));
    }

}
