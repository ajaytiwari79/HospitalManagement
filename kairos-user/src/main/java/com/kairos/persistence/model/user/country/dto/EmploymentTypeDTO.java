package com.kairos.persistence.model.user.country.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.EmploymentType;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 7/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class EmploymentTypeDTO extends UserBaseEntity {
    @NotEmpty(message = "error.EmploymentType.name.notEmptyOrNotNull")    @NotNull(message = "error.EmploymentType.name.notEmptyOrNotNull")
    private String name;
    private String description;
    private boolean allowedForContactPerson;
    private boolean allowedForShiftPlan;
    private boolean allowedForFlexPool;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAllowedForContactPerson() {
        return allowedForContactPerson;
    }

    public void setAllowedForContactPerson(boolean allowedForContactPerson) {
        this.allowedForContactPerson = allowedForContactPerson;
    }

    public boolean isAllowedForShiftPlan() {
        return allowedForShiftPlan;
    }

    public void setAllowedForShiftPlan(boolean allowedForShiftPlan) {
        this.allowedForShiftPlan = allowedForShiftPlan;
    }

    public boolean isAllowedForFlexPool() {
        return allowedForFlexPool;
    }

    public void setAllowedForFlexPool(boolean allowedForFlexPool) {
        this.allowedForFlexPool = allowedForFlexPool;
    }

    public EmploymentType generateEmploymentTypeFromEmploymentTypeDTO() {
        EmploymentType employmentType = new EmploymentType();
        employmentType.setName(this.getName());
        employmentType.setDescription(this.getDescription());
        employmentType.setAllowedForContactPerson(this.isAllowedForContactPerson());
        employmentType.setAllowedForShiftPlan(this.isAllowedForShiftPlan());
        employmentType.setAllowedForFlexPool(this.isAllowedForFlexPool());
        return employmentType;
    }
}
