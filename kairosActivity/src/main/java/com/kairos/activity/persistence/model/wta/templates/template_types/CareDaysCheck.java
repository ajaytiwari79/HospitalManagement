package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.AgeRange;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;

import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class CareDaysCheck extends WTABaseRuleTemplate{
    private List<AgeRange> ageRange;
    private List<Long> activities;
    private long validationStartDateMillis;
    private Long numberOfWeeks;
    private WTATemplateType wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;

    public CareDaysCheck() {
        //Default Constructor
    }

    public CareDaysCheck(String name, boolean minimum, boolean disabled, String description, List<AgeRange> ageRange, List<Long> activities, long validationStartDateMillis, Long numberOfWeeks) {
        super(name, description);
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
