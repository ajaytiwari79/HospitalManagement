package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.enums.MinMaxSetting;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AverageScheduledTimeWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
/*    private long validationStartDateMillis;*/
    private boolean balanceAdjustment;
    private boolean useShiftTimes;
    private long maximumAvgTime;
    private List<PartOfDay> partOfDays = new ArrayList<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;


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



    public boolean isBalanceAdjustment() {
        return balanceAdjustment;
    }

    public void setBalanceAdjustment(boolean balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }



    public boolean isUseShiftTimes() {
        return useShiftTimes;
    }

    public void setUseShiftTimes(boolean useShiftTimes) {
        this.useShiftTimes = useShiftTimes;
    }

    public AverageScheduledTimeWTATemplate(String name, boolean disabled,
                                           String description, long intervalLength, LocalDate validationStartDate
            , boolean balanceAdjustment, boolean useShiftTimes, long maximumAvgTime, String intervalUnit) {
        this.intervalLength = intervalLength;
        //this.validationStartDateMillis = validationStartDateMillis;
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.balanceAdjustment=balanceAdjustment;
        this.useShiftTimes =useShiftTimes;
        this.maximumAvgTime=maximumAvgTime;
        this.intervalUnit=intervalUnit;
        wtaTemplateType = WTATemplateType.AVERAGE_SHEDULED_TIME;

    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(long maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }

    public AverageScheduledTimeWTATemplate() {
        wtaTemplateType = WTATemplateType.AVERAGE_SHEDULED_TIME;
    }



}
