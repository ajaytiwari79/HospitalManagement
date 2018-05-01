package com.kairos.response.dto.web.wta;

import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.response.dto.web.AgeRangeDTO;

import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class SeniorDaysPerYearWTATemplateDTO extends WTABaseRuleTemplateDTO{
    private List<AgeRangeDTO> ageRange;
    private List<Long> activitieIds;
    private long validationStartDateMillis;
    private Long numberOfWeeks;
    private WTATemplateType wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;

    public SeniorDaysPerYearWTATemplateDTO() {
        //Default Constructor
    }

    public SeniorDaysPerYearWTATemplateDTO(String name, boolean minimum, boolean disabled, String description, List<AgeRangeDTO> ageRange, List<Long> activitieIds,
                                           long validationStartDateMillis, Long numberOfWeeks) {
        super(name , description);
        this.disabled=disabled;
        this.ageRange = ageRange;
        this.activitieIds = activitieIds;
        this.validationStartDateMillis = validationStartDateMillis;
        this.numberOfWeeks = numberOfWeeks;
    }

    public List<AgeRangeDTO> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRangeDTO> ageRange) {
        this.ageRange = ageRange;
    }

    public List<Long> getActivitieIds() {
        return activitieIds;
    }

    public void setActivitieIds(List<Long> activitieIds) {
        this.activitieIds = activitieIds;
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
