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
 * TEMPLATE12
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VetoPerPeriodWTATemplate extends WTABaseRuleTemplate {

    private double maximumVetoPercentage;
    private List<Long> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<BigInteger> activityIds = new ArrayList<>();

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

    public double getMaximumVetoPercentage() {
        return maximumVetoPercentage;
    }

    public void setMaximumVetoPercentage(double maximumVetoPercentage) {
        this.maximumVetoPercentage = maximumVetoPercentage;
    }

    public VetoPerPeriodWTATemplate(String name, boolean disabled,
                                    String description, double maximumVetoPercentage) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;
        this.maximumVetoPercentage = maximumVetoPercentage;

    }

    public VetoPerPeriodWTATemplate() {
        wtaTemplateType = WTATemplateType.VETO_PER_PERIOD;
    }

}
