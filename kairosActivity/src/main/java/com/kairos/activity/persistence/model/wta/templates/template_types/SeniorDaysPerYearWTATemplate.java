package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.AgeRange;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class SeniorDaysPerYearWTATemplate extends WTABaseRuleTemplate{
    private List<AgeRange> ageRange;
    private List<Long> activitieIds;
    private LocalDate validationStartDate;
    private Long numberOfWeeks;
    private boolean borrowLeave;
    private boolean carryForwardLeave;

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

    public SeniorDaysPerYearWTATemplate(String name, boolean minimum, boolean disabled, String description, List<AgeRange> ageRange, List<Long> activitieIds,
                                        LocalDate validationStartDate, Long numberOfWeeks) {
        super(name , description);
        this.disabled=disabled;
        this.ageRange = ageRange;
        this.activitieIds = activitieIds;
        this.validationStartDate = validationStartDate;
        this.numberOfWeeks = numberOfWeeks;
    }

    public List<AgeRange> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRange> ageRange) {
        this.ageRange = ageRange;
    }

    public List<Long> getActivitieIds() {
        return activitieIds;
    }

    public void setActivitieIds(List<Long> activitieIds) {
        this.activitieIds = activitieIds;
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
