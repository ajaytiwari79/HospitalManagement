package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pavan on 23/4/18.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildCareDaysCheck extends WTABaseRuleTemplate{
    private AgeRange ageRange;
    private List<Long> activities;
    private long validationStartDateMillis;
    private int numberOfWeeks;

    public ChildCareDaysCheck() {
        //Default Constructor
    }

    public ChildCareDaysCheck(String name, String templateType, boolean disabled,String description, AgeRange ageRange, List<Long> activities,
                              int numberOfLeaves, long validationStartDateMillis, int numberOfWeeks) {
        super(name, templateType, description);
        this.disabled=disabled;
        this.ageRange = ageRange;
        this.activities = activities;
        this.validationStartDateMillis = validationStartDateMillis;
        this.numberOfWeeks = numberOfWeeks;
    }

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeRange ageRange) {
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
