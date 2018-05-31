package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.enums.MinMaxSetting;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private long minimumRest;//hh:mm
    private long daysWorked;
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);

    private List<Long> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MINIMUM;


    public MinMaxSetting getMinMaxSetting() {
        return minMaxSetting;
    }

    public void setMinMaxSetting(MinMaxSetting minMaxSetting) {
        this.minMaxSetting = minMaxSetting;
    }

    public List<Long> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<Long> plannedTimeIds) {
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
    public long getMinimumRest() {
        return minimumRest;
    }


    public void setMinimumRest(long minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(long daysWorked) {
        this.daysWorked = daysWorked;
    }


    public ConsecutiveRestPartOfDayWTATemplate(String name, boolean disabled, String description, long minimumRest, long daysWorked) {
        this.name=name;
        this.disabled=disabled;
        this.description=description;
        this.minimumRest = minimumRest;
        this.daysWorked = daysWorked;
        wtaTemplateType = WTATemplateType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;

    }
    public ConsecutiveRestPartOfDayWTATemplate() {
        wtaTemplateType = WTATemplateType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;
    }

    @Override
    public String isSatisfied(RuleTemplateSpecificInfo infoWrapper) {
       /* if(shifts.size()<2) return true;
        WTARuleTemplateValidatorUtility.sortShifts(shifts);
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
                ZonedDateTime start= DateUtils.getZoneDateTime(shifts.get(l-1).getEndDate());
                ZonedDateTime end=DateUtils.getZoneDateTime(shifts.get(l).getStartDate());
                int diff=new DateTimeInterval(start,end).getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }*/
        return "";
    }

}
