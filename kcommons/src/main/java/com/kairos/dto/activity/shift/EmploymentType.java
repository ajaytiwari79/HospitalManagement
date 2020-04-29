package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by vipul on 6/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
 @JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EmploymentType {
    private String name;
    private String description;
    private boolean allowedForContactPerson;
    private boolean allowedForShiftPlan;
    private boolean allowedForFlexPool;
    private Long id;
}
