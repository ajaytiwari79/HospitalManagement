package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeniorDaysInYearWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private long daysLimit;
    private String activityCode;

    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<BigInteger> activityIds = new ArrayList<>();
    private List<Long> plannedTimeIds = new ArrayList<>();

    private List<PartOfDay> partOfDays = new ArrayList<>();
    private float recommendedValue;
    private boolean minimum;

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

    public boolean isMinimum() {
        return minimum;
    }

    public void setMinimum(boolean minimum) {
        this.minimum = minimum;
    }

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    public List<Long> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<Long> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
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

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public SeniorDaysInYearWTATemplate(String name,  boolean disabled,
                                       String description, long intervalLength, String intervalUnit, long validationStartDateMillis,
                                       long daysLimit, String activityCode) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalLength =intervalLength;
        this.intervalUnit=intervalUnit;
        this.validationStartDateMillis =validationStartDateMillis;
        this.daysLimit =daysLimit;
        this.activityCode=activityCode;

    }
    public SeniorDaysInYearWTATemplate() {
        wtaTemplateType = WTATemplateType.MAXIMUM_SENIOR_DAYS_IN_YEAR;
    }


}
