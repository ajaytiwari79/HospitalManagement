package com.kairos.shiftplanning.domain.staff;

import com.kairos.enums.employment_type.EmploymentCategory;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentType {
    private Long id;
    private String name;
    private Set<EmploymentCategory> employmentCategories;
}
