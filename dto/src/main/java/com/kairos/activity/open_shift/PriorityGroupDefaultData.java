package com.kairos.activity.open_shift;

import com.kairos.user.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.user.country.experties.ExpertiseResponseDTO;

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
