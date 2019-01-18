package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.ShiftValidatorService.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AverageScheduledTimeWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.interval.notNull")
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
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
                                           String description, long intervalLength,String intervalUnit) {
        this.intervalLength = intervalLength;
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
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled()) {
            if (intervalLength == 0l || StringUtils.isEmpty(intervalUnit)) {
                throwException("message.ruleTemplate.interval.notNull");
            }
            if (isValidForPhase(infoWrapper.getPhase(), this.phaseTemplateValues) && CollectionUtils.containsAny(timeTypeIds, infoWrapper.getShift().getActivitiesTimeTypeIds())) {
                DateTimeInterval interval = getIntervalByRuleTemplate(infoWrapper.getShift(), intervalUnit, intervalLength);
                List<ShiftWithActivityDTO> shifts = filterShiftsByPlannedTypeAndTimeTypeIds(infoWrapper.getShifts(), timeTypeIds, plannedTimeIds);
                shifts = getShiftsByInterval(interval, infoWrapper.getShifts(), null);
                shifts.add(infoWrapper.getShift());
                List<DateTimeInterval> intervals = getIntervals(interval);
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, this);
                for (DateTimeInterval dateTimeInterval : intervals) {
                    int totalMin = 0;
                    for (ShiftWithActivityDTO shift : shifts) {
                        if (dateTimeInterval.overlaps(shift.getDateTimeInterval())) {
                            totalMin += (int) dateTimeInterval.overlap(shift.getDateTimeInterval()).getMinutes();
                        }
                    }
                    boolean isValid = isValid(minMaxSetting, limitAndCounter[0], totalMin / (60 * (int) dateTimeInterval.getDays()));
                    brokeRuleTemplate(infoWrapper,limitAndCounter[1],isValid, this);
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

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate)wtaBaseRuleTemplate;
        return (this != averageScheduledTimeWTATemplate) && !(intervalLength == averageScheduledTimeWTATemplate.intervalLength &&
                Float.compare(averageScheduledTimeWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(intervalUnit, averageScheduledTimeWTATemplate.intervalUnit) &&
                Objects.equals(plannedTimeIds, averageScheduledTimeWTATemplate.plannedTimeIds) &&
                Objects.equals(timeTypeIds, averageScheduledTimeWTATemplate.timeTypeIds) &&
                Objects.equals(partOfDays, averageScheduledTimeWTATemplate.partOfDays) &&
                minMaxSetting == averageScheduledTimeWTATemplate.minMaxSetting && Objects.equals(this.phaseTemplateValues,averageScheduledTimeWTATemplate.phaseTemplateValues));
    }

}
