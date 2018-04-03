package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 2/11/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class EmploymentType extends UserBaseEntity {

    @NotEmpty(message = "error.EmploymentType.name.notEmptyOrNotNull") @NotNull(message = "error.EmploymentType.name.notEmptyOrNotNull")
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

}
