package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.enums.employment_type.EmploymentCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class EmploymentTypeDTO {
    private Long id;
    private String name;
    private Set<EmploymentCategory> employmentCategories;

    public EmploymentTypeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }



}
