package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.employment_type.EmploymentCategory;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by prerna on 7/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true,value={ "valid" })
@QueryResult
public class EmploymentTypeDTO {
    private Long id;
    @NotNull(message = "error.EmploymentType.name.notEmptyOrNotNull")
    private String name;
    private String description;
    private boolean allowedForContactPerson;
    private boolean allowedForShiftPlan;
    private boolean allowedForFlexPool;
    private Set<EmploymentCategory> employmentCategories;
    private PaidOutFrequencyEnum paymentFrequency;
    //Added By Pavan
    private boolean editableAtUnitPosition;
    private Short weeklyMinutes;
    private boolean markMainEmployment;

    public EmploymentTypeDTO() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isEditableAtUnitPosition() {
        return editableAtUnitPosition;
    }

    public void setEditableAtUnitPosition(boolean editableAtUnitPosition) {
        this.editableAtUnitPosition = editableAtUnitPosition;
    }

    public Short getWeeklyMinutes() {
        return weeklyMinutes;
    }

    public void setWeeklyMinutes(Short weeklyMinutes) {
        this.weeklyMinutes = weeklyMinutes;
    }

    public boolean isMarkMainEmployment() {
        return markMainEmployment;
    }

    public void setMarkMainEmployment(boolean markMainEmployment) {
        this.markMainEmployment = markMainEmployment;
    }

    @AssertTrue(message = "At least one role should be selected")
    public boolean isValid() {
        return (!employmentCategories.isEmpty());
    }

}
