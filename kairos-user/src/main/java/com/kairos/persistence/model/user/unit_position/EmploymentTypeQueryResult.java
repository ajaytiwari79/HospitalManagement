package com.kairos.persistence.model.user.unit_position;

import com.kairos.persistence.model.enums.EmploymentCategory;

/**
 * Created by vipul on 5/4/18.
 */
public class EmploymentTypeQueryResult {
    private String name;
    private Long id;
    private String description;
    private boolean allowedForContactPerson;
    private boolean allowedForShiftPlan;
    private boolean allowedForFlexPool;
    private EmploymentCategory employmentTypeCategory;

    public EmploymentTypeQueryResult() {
        //
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public EmploymentCategory getEmploymentTypeCategory() {
        return employmentTypeCategory;
    }

    public void setEmploymentTypeCategory(EmploymentCategory employmentTypeCategory) {
        this.employmentTypeCategory = employmentTypeCategory;
    }

    public EmploymentTypeQueryResult(String name, Long id, EmploymentCategory employmentTypeCategory) {
        this.name = name;
        this.id = id;
        this.employmentTypeCategory = employmentTypeCategory;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EmploymentTypeQueryResult{");
        sb.append("name='").append(name).append('\'');
        sb.append(", id=").append(id);
        sb.append(", description='").append(description).append('\'');
        sb.append(", allowedForContactPerson=").append(allowedForContactPerson);
        sb.append(", allowedForShiftPlan=").append(allowedForShiftPlan);
        sb.append(", allowedForFlexPool=").append(allowedForFlexPool);
        sb.append(", employmentTypeCategory=").append(employmentTypeCategory);
        sb.append('}');
        return sb.toString();
    }
}
