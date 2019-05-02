package com.kairos.persistence.model.country.employment_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.employment_type.EmploymentCategory;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;
import java.util.Set;


/**
 * Created by prerna on 2/11/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class EmploymentType extends UserBaseEntity {

    @NotBlank(message = "error.EmploymentType.name.notEmptyOrNotNull")
    private String name;
    private String description;
    private boolean allowedForContactPerson;
    private boolean allowedForShiftPlan;
    private boolean allowedForFlexPool;
    private Set<EmploymentCategory> employmentCategories;
    private PaidOutFrequencyEnum paymentFrequency;
    //Added By Pavan
    private boolean editableAtEmployment;
    private Short weeklyMinutes;

    public EmploymentType() {
        //Default Constructor
    }

    public EmploymentType(Long id,@NotBlank(message = "error.EmploymentType.name.notEmptyOrNotNull") String name, String description, boolean allowedForContactPerson, boolean allowedForShiftPlan, boolean allowedForFlexPool, Set<EmploymentCategory> employmentCategories, PaidOutFrequencyEnum paymentFrequency, boolean editableAtEmployment) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.allowedForContactPerson = allowedForContactPerson;
        this.allowedForShiftPlan = allowedForShiftPlan;
        this.allowedForFlexPool = allowedForFlexPool;
        this.employmentCategories = employmentCategories;
        this.paymentFrequency = paymentFrequency;
        this.editableAtEmployment = editableAtEmployment;
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

    public boolean isEditableAtEmployment() {
        return editableAtEmployment;
    }

    public void setEditableAtEmployment(boolean editableAtEmployment) {
        this.editableAtEmployment = editableAtEmployment;
    }

    public Short getWeeklyMinutes() {
        return weeklyMinutes;
    }

    public void setWeeklyMinutes(Short weeklyMinutes) {
        this.weeklyMinutes = weeklyMinutes;
    }
}
