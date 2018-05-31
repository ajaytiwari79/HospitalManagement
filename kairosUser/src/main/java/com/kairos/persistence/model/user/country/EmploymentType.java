package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.EmploymentCategory;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;
import java.util.Set;

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
    private Set<EmploymentCategory> employmentCategories;
    private PaidOutFrequencyEnum paymentFrequency;

    public EmploymentType() {
        //Default Constructor
    }

    public EmploymentType(@NotEmpty(message = "error.EmploymentType.name.notEmptyOrNotNull") @NotNull(message = "error.EmploymentType.name.notEmptyOrNotNull") String name, String description, boolean allowedForContactPerson,
                          boolean allowedForShiftPlan, boolean allowedForFlexPool, Set<EmploymentCategory> employmentCategories, PaidOutFrequencyEnum paymentFrequency) {
        this.name = name;
        this.description = description;
        this.allowedForContactPerson = allowedForContactPerson;
        this.allowedForShiftPlan = allowedForShiftPlan;
        this.allowedForFlexPool = allowedForFlexPool;
        this.employmentCategories = employmentCategories;
        this.paymentFrequency = paymentFrequency;
    }

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

    public Set<EmploymentCategory> getEmploymentCategories() {
        return employmentCategories;
    }

    public void setEmploymentCategories(Set<EmploymentCategory> employmentCategories) {
        this.employmentCategories = employmentCategories;
    }

    public PaidOutFrequencyEnum getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(PaidOutFrequencyEnum paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

}
