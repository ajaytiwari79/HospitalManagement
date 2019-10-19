package com.kairos.dto.user.country.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayTypeEmploymentTypeWrapper {
    private List<DayType> dayTypes;
    private List<EmploymentTypeDTO> employmentTypes;
}
