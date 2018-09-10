package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.enums.wta.WTATemplateType;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class SeniorDaysPerYearWTATemplate extends WTABaseRuleTemplate{
    private List<AgeRange> ageRange;
    private List<BigInteger> activityIds;
    private LocalDate validationStartDate;
    private Long numberOfWeeks;
    private boolean borrowLeave;
    private boolean carryForwardLeave;

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    private float recommendedValue;

    public boolean isCarryForwardLeave() {
        return carryForwardLeave;
    }

    public void setCarryForwardLeave(boolean carryForwardLeave) {
        this.carryForwardLeave = carryForwardLeave;
    }

    public boolean isBorrowLeave() {
        return borrowLeave;
    }

    public void setBorrowLeave(boolean borrowLeave) {
        this.borrowLeave = borrowLeave;
    }

    public SeniorDaysPerYearWTATemplate() {
        this.wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;
        //Default Constructor
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {

    }

    public SeniorDaysPerYearWTATemplate(String name, boolean minimum, boolean disabled, String description, List<AgeRange> ageRange, List<BigInteger> activityIds,
                                        LocalDate validationStartDate, Long numberOfWeeks) {
        super(name , description);
        this.disabled=disabled;
        this.ageRange = ageRange;
        this.activityIds = activityIds;
        this.validationStartDate = validationStartDate;
        this.numberOfWeeks = numberOfWeeks;
        this.wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;
    }

    public List<AgeRange> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRange> ageRange) {
        this.ageRange = ageRange;
    }

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    public LocalDate getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(LocalDate validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public Long getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(Long numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }
}
