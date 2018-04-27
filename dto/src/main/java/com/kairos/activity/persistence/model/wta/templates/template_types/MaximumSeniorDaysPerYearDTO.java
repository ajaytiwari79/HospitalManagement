package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.response.dto.web.AgeRangeDTO;
import com.kairos.response.dto.web.wta.WTABaseRuleTemplateDTO;

import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class MaximumSeniorDaysPerYearDTO extends WTABaseRuleTemplateDTO{
    private List<AgeRangeDTO> ageRange;
    private List<Long> activities;
    private long validationStartDateMillis;
    private Long numberOfWeeks;
    private WTATemplateType wtaTemplateType = WTATemplateType.MAXIMUM_SENIOR_DAYS_PER_YEAR;

    public MaximumSeniorDaysPerYearDTO() {
        //Default Constructor
    }

    public MaximumSeniorDaysPerYearDTO(String name, boolean minimum, boolean disabled, String description, List<AgeRangeDTO> ageRange, List<Long> activities,
                                       long validationStartDateMillis, Long numberOfWeeks) {
        super(name , description);
        this.disabled=disabled;
        this.ageRange = ageRange;
        this.activities = activities;
        this.validationStartDateMillis = validationStartDateMillis;
        this.numberOfWeeks = numberOfWeeks;
    }

    public List<AgeRangeDTO> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRangeDTO> ageRange) {
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
