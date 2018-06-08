package com.kairos.response.dto.web.open_shift;

import com.kairos.response.dto.web.cta.EmploymentTypeDTO;
import com.kairos.response.dto.web.experties.ExpertiseResponseDTO;

import java.util.List;

public class PriorityGroupDefaultData {
    private List<EmploymentTypeDTO> employmentTypes;
    private List<ExpertiseResponseDTO> expertises;

    public PriorityGroupDefaultData() {
        //Default Constructor
    }

    public PriorityGroupDefaultData(List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises) {
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<ExpertiseResponseDTO> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<ExpertiseResponseDTO> expertises) {
        this.expertises = expertises;
    }
}
