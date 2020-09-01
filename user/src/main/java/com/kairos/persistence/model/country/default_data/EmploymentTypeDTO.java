package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.TranslationInfo;
import com.kairos.enums.employment_type.EmploymentCategory;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

/**
 * Created by prerna on 7/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true,value={ "valid" })
@QueryResult
@Getter
@Setter
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
    private boolean editableAtEmployment;
    private Short weeklyMinutes;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;

    public EmploymentTypeDTO() {
        //Default Constructor
    }

    @AssertTrue(message = "At least one role should be selected")
    public boolean isValid() {
        return (!employmentCategories.isEmpty());
    }

}
