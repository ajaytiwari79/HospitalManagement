package com.kairos.activity.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.MinMaxSetting;
import com.kairos.enums.PartOfDay;
import com.kairos.enums.WTATemplateType;
import com.kairos.activity.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.dto.ShiftWithActivityDTO;
import com.kairos.util.DateTimeInterval;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.util.WTARuleTemplateValidatorUtility.*;
import static com.kairos.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AverageScheduledTimeWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<PartOfDay> partOfDays = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;

    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public List<PartOfDay> getPartOfDays() {
        return partOfDays;
    }

    public void setPartOfDays(List<PartOfDay> partOfDays) {
        this.partOfDays = partOfDays;
    }

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }



    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }




    public AverageScheduledTimeWTATemplate(String name, boolean disabled,
                                           String description, long intervalLength, LocalDate validationStartDate
            , boolean balanceAdjustment, boolean useShiftTimes, long maximumAvgTime, String intervalUnit) {
        this.intervalLength = intervalLength;
        //this.validationStartDateMillis = validationStartDateMillis;
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalUnit=intervalUnit;
        wtaTemplateType = WTATemplateType.AVERAGE_SHEDULED_TIME;

    }



    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public AverageScheduledTimeWTATemplate() {
        wtaTemplateType = WTATemplateType.AVERAGE_SHEDULED_TIME;
    }

    @Override
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled()  && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues) && (plannedTimeIds.contains(infoWrapper.getShift().getPlannedTypeId()) && timeTypeIds.contains(infoWrapper.getShift().getActivity().getBalanceSettingsActivityTab().getTimeTypeId()))){
            DateTimeInterval interval = getIntervalByRuleTemplate(infoWrapper.getShift(),intervalUnit,intervalLength);
            List<ShiftWithActivityDTO> shifts = filterShifts(infoWrapper.getShifts(),timeTypeIds,plannedTimeIds,null);
            shifts = getShiftsByInterval(interval,infoWrapper.getShifts(),null);
            shifts.add(infoWrapper.getShift());
            List<DateTimeInterval> intervals = getIntervals(interval);
            Integer[] limitAndCounter = getValueByPhase(infoWrapper,phaseTemplateValues,getId());
            for (DateTimeInterval dateTimeInterval : intervals) {
                int totalMin = 0;
                for (ShiftWithActivityDTO shift : shifts) {
                    if(dateTimeInterval.overlaps(shift.getDateTimeInterval())){
                        totalMin +=dateTimeInterval.overlap(shift.getDateTimeInterval()).getMinutes();
                    }
                }
                boolean isValid = isValid(minMaxSetting, limitAndCounter[0], totalMin/(60*dateTimeInterval.getDays()));
                if (!isValid) {
                    if(limitAndCounter[1]!=null) {
                        int counterValue =  limitAndCounter[1] - 1;
                        if(counterValue<0){
                            throw new InvalidRequestException(getName() + " is Broken");
                        }else {
                            infoWrapper.getCounterMap().put(getId(), infoWrapper.getCounterMap().getOrDefault(getId(), 0) + 1);
                            infoWrapper.getShift().getBrokenRuleTemplateIds().add(getId());
                        }
                    }else {
                        throw new InvalidRequestException(getName() + " is Broken");
                    }
                }
            }
        }
        return "";
    }

    public ZonedDateTime getNextDateOfInterval(ZonedDateTime dateTime){
        ZonedDateTime zonedDateTime = null;
        switch (intervalUnit){
            case DAYS:dateTime.plusDays(intervalLength);
                break;
            case WEEKS:dateTime.plusWeeks(intervalLength);
                break;
            case MONTHS:dateTime.plusMonths(intervalLength);
                break;
            case YEARS:dateTime.plusYears(intervalLength);
                break;
        }
        return zonedDateTime;
    }

    private List<DateTimeInterval> getIntervals(DateTimeInterval interval){
        List<DateTimeInterval> intervals = new ArrayList<>();
        ZonedDateTime nextEnd = getNextDateOfInterval(interval.getStart());
        intervals.add(new DateTimeInterval(interval.getStart(),nextEnd));
        intervals.add(new DateTimeInterval(nextEnd,getNextDateOfInterval(nextEnd)));
        return intervals;
    }


}
