package com.kairos.dto.planner.constarints.country;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryConstraintDTO extends ConstraintDTO {
    //~
    //@NotBlank
    private Long countryId;
   // @NotBlank
    private Long organizationServiceId;
    //@NotBlank
    private Long organizationSubServiceId;
}
