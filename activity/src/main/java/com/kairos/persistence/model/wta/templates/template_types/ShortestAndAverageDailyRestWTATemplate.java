package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.ShiftValidatorService.*;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortestAndAverageDailyRestWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;//
    private String intervalUnit;
    private float recommendedValue;
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();


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



    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
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

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public ShortestAndAverageDailyRestWTATemplate(String name,  boolean disabled,
                                                  String description, long intervalLength, String intervalUnit) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalLength =intervalLength;
        this.intervalUnit=intervalUnit;
        wtaTemplateType = WTATemplateType.SHORTEST_AND_AVERAGE_DAILY_REST;
    }
    public ShortestAndAverageDailyRestWTATemplate() {
        wtaTemplateType = WTATemplateType.SHORTEST_AND_AVERAGE_DAILY_REST;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        String exception="";
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues) && (plannedTimeIds.contains(infoWrapper.getShift().getPlannedTypeId()) && timeTypeIds.contains(infoWrapper.getShift().getActivity().getBalanceSettingsActivityTab().getTimeTypeId()))){
            DateTimeInterval interval = getIntervalByRuleTemplate(infoWrapper.getShift(),intervalUnit,intervalLength);
            List<ShiftWithActivityDTO> shifts = filterShifts(infoWrapper.getShifts(),timeTypeIds,plannedTimeIds,null);
            shifts = getShiftsByInterval(interval,infoWrapper.getShifts(),null);
            shifts.add(infoWrapper.getShift());
            List<DateTimeInterval> intervals = getIntervals(interval);
            Integer[] limitAndCounter = getValueByPhase(infoWrapper,phaseTemplateValues,this);
            for (DateTimeInterval dateTimeInterval : intervals) {
                int totalMin = (int)dateTimeInterval.getMinutes();
                for (ShiftWithActivityDTO shift : shifts) {
                    if(dateTimeInterval.overlaps(shift.getDateTimeInterval())){
                        totalMin -= (int)dateTimeInterval.overlap(shift.getDateTimeInterval()).getMinutes();
                    }
                }
                boolean isValid = isValid(MinMaxSetting.MINIMUM, limitAndCounter[0], totalMin/(60*(int)dateTimeInterval.getDays()));
                if (!isValid) {
                    if(limitAndCounter[1]!=null) {
                        int counterValue =  limitAndCounter[1] - 1;
                        if(counterValue<0){
                            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                        }else {
                            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,limitAndCounter[1],true,true);
                            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                        }
                    }else {
                        WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                        infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);

                    }
                }
            }
        }
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
