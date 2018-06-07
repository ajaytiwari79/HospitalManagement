package com.kairos.response.dto.web.open_shift;

import com.kairos.response.dto.web.cta.EmploymentTypeDTO;
import com.kairos.response.dto.web.experties.ExpertiseResponseDTO;

import java.util.List;

public class PriorityGroupDefaultData {
    private List<EmploymentTypeDTO> employmentTypes;
    private List<ExpertiseResponseDTO> expertise;

    public PriorityGroupDefaultData() {
        //Default Constructor
    }

    public PriorityGroupDefaultData(List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertise) {
        this.employmentTypes = employmentTypes;
        this.expertise = expertise;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<ExpertiseResponseDTO> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<ExpertiseResponseDTO> expertise) {
        this.expertise = expertise;
    }
}
