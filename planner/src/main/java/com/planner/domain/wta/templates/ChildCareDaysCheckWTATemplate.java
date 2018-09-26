package com.planner.domain.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.dto.activity.wta.AgeRange;
import com.planner.domain.wta.WTABaseRuleTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 23/4/18.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildCareDaysCheckWTATemplate extends WTABaseRuleTemplate {
    private List<AgeRange> ageRange;
    private List<Long> activities;
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<Long> plannedTimeIds = new ArrayList<>();;
    private long validationStartDateMillis;
    private int numberOfWeeks;
    private boolean borrowLeave;
    private boolean carryForwardLeave;


    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public List<Long> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<Long> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

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

    public ChildCareDaysCheckWTATemplate() {
       this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }

    public ChildCareDaysCheckWTATemplate(String name, boolean disabled, String description, List<AgeRange> ageRange, List<Long> activities,
                                         int numberOfLeaves, long validationStartDateMillis, int numberOfWeeks) {
        super(name, description);
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
        this.disabled=disabled;
        this.ageRange = ageRange;
        this.activities = activities;
        this.validationStartDateMillis = validationStartDateMillis;
        this.numberOfWeeks = numberOfWeeks;
    }

    public List<AgeRange> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRange> ageRange) {
        this.ageRange = ageRange;
    }

    public List<Long> getActivities() {
        return activities;
    }

    public void setActivities(List<Long> activities) {
        this.activities = activities;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public int getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(int numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }
}
