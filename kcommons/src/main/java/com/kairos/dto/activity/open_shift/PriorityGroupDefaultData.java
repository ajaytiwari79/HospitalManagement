package com.kairos.dto.activity.open_shift;

import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;

import java.util.List;

public class PriorityGroupDefaultData {
    private List<EmploymentTypeDTO> employmentTypes;
    private List<ExpertiseResponseDTO> expertises;
    private List<CounterDTO> counters;

    public PriorityGroupDefaultData() {
        //Default Constructor
    }


    public PriorityGroupDefaultData(List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises) {
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
    }

    public PriorityGroupDefaultData(List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises, List<CounterDTO> counters) {
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
        this.counters = counters;
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

    public List<CounterDTO> getCounters() {
        return counters;
    }

    public void setCounters(List<CounterDTO> counters) {
        this.counters = counters;
    }
}
