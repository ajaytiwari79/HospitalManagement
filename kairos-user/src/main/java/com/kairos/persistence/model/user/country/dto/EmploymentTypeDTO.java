package com.kairos.persistence.model.user.country.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.EmploymentType;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Optional;

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
    private boolean permanent;
    private boolean temporary;
    private boolean guest;

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

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
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
    @AssertTrue(message = "At least one field should be selected")
    public boolean isValid() {

        if (allowedForContactPerson || allowedForShiftPlan || allowedForFlexPool || permanent || temporary ||guest) {
            return true;
        }

        return true;
    }

}
