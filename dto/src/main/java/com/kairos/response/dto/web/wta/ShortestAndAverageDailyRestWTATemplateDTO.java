package com.kairos.response.dto.web.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.enums.WTATemplateType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortestAndAverageDailyRestWTATemplateDTO extends WTABaseRuleTemplateDTO {

    private long intervalLength;//
    private String intervalUnit;
    private long validationStartDateMillis;
    private long continuousDayRestHours;
    private long averageRest;//(hours number)
    private String shiftAffiliation;//(List checkbox)
    private WTATemplateType wtaTemplateType = WTATemplateType.SHORTEST_AND_AVERAGE_DAILY_REST;
    protected List<PartOfDay> partOfDays = new ArrayList<>();
    protected float recommendedValue;
    protected boolean minimum;

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

    public long getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(long continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public long getAverageRest() {
        return averageRest;
    }

    public void setAverageRest(long averageRest) {
        this.averageRest = averageRest;
    }

    public String getShiftAffiliation() {
        return shiftAffiliation;
    }

    public void setShiftAffiliation(String shiftAffiliation) {
        this.shiftAffiliation = shiftAffiliation;
    }

    public ShortestAndAverageDailyRestWTATemplateDTO(String name, boolean disabled,
                                                     String description, long intervalLength, String intervalUnit, long validationStartDateMillis,
                                                     long continuousDayRestHours, long averageRest, String shiftAffiliation) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.intervalLength =intervalLength;
        this.intervalUnit=intervalUnit;
        this.validationStartDateMillis =validationStartDateMillis;
        this.continuousDayRestHours=continuousDayRestHours;
        this.averageRest=averageRest;
        this.shiftAffiliation=shiftAffiliation;
    }
    public ShortestAndAverageDailyRestWTATemplateDTO() {

    }

}
