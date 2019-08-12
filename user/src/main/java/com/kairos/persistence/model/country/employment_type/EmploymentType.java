package com.kairos.persistence.model.country.employment_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.employment_type.EmploymentCategory;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;
import java.util.Set;


/**
 * Created by prerna on 2/11/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
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
}
