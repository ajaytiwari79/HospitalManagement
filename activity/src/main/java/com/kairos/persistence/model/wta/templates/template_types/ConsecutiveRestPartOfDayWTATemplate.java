package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE4
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsecutiveRestPartOfDayWTATemplate extends WTABaseRuleTemplate {

    private Long consecutiveDays;
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);

    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

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


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public Long getConsecutiveDays() {
        return consecutiveDays;
    }

    public void setConsecutiveDays(Long consecutiveDays) {
        this.consecutiveDays = consecutiveDays;
    }

    public ConsecutiveRestPartOfDayWTATemplate(String name, boolean disabled, String description) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        wtaTemplateType = WTATemplateType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;

    }
    public ConsecutiveRestPartOfDayWTATemplate() {
        wtaTemplateType = WTATemplateType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
       /* if(shifts.size()<2) return true;
        ShiftValidatorService.sortShifts(shifts);
        List<LocalDate> dates=getSortedDates(shifts);

        int l=1;
        int consDays=0;
        int totalRestUnder=0;
        while (l<dates.size()){
            if(dates.get(l-1).equals(dates.get(l).minusDays(1)) && isNightShift(shifts.get(l),timeSlotWrapper)&& isNightShift(shifts.get(l-1),timeSlotWrapper)){
                consDays++;
            }else{
                consDays=0;
            }
            if(consDays>=nightsWorked){
                ZonedDateTime start= DateUtils.asZoneDateTime(shifts.get(l-1).getEndDate());
                ZonedDateTime end=DateUtils.asZoneDateTime(shifts.get(l).getStartDate());
                int diff=new DateTimeInterval(start,end).getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }*/
    }

}
