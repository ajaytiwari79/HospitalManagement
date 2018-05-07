package com.kairos.response.dto.web.wta;

import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.AgeRange;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by pavan on 24/4/18.
 */
public class SeniorDaysPerYearWTATemplateDTO extends WTABaseRuleTemplateDTO{
    private List<AgeRange> ageRange;
    private List<Long> activitieIds;
    private LocalDate validationStartDate;
    private Long numberOfWeeks;
    private WTATemplateType wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;
    private List<BigInteger> timeTypeIds;
    private List<Long> plannedTimeIds;

    public SeniorDaysPerYearWTATemplateDTO() {
        //Default Constructor
    }


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

    public LocalDate getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(LocalDate validationStartDate) {
        this.validationStartDate = validationStartDate;
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
