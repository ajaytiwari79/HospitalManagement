package com.kairos.dto.activity.open_shift;

import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriorityGroupDefaultData {
    private List<EmploymentTypeDTO> employmentTypes;
    private List<ExpertiseResponseDTO> expertises;
    private List<CounterDTO> counters;

    public PriorityGroupDefaultData(List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises) {
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
    }


}
