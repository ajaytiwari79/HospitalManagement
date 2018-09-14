package com.kairos.dto.activity.wta.templates;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.AgeRange;
import com.kairos.enums.wta.WTATemplateType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class ChildCareDaysCheckWTATemplateDTO extends WTABaseRuleTemplateDTO {

    private List<AgeRange> ageRange;
    private List<BigInteger> activityIds;
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private LocalDate validationStartDate;
    private int numberOfWeeks;
    private boolean borrowLeave;
    private boolean carryForwardLeave;

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    protected float recommendedValue;

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    public ChildCareDaysCheckWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }

    public boolean isBorrowLeave() {
        return borrowLeave;
    }

    public void setBorrowLeave(boolean borrowLeave) {
        this.borrowLeave = borrowLeave;
    }

    public boolean isCarryForwardLeave() {
        return carryForwardLeave;
    }

    public void setCarryForwardLeave(boolean carryForwardLeave) {
        this.carryForwardLeave = carryForwardLeave;
    }

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public List<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }


    public List<AgeRange> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRange> ageRange) {
        this.ageRange = ageRange;
    }


    public LocalDate getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(LocalDate validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public int getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(int numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }



}
