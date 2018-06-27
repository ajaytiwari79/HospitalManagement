package com.kairos.user.employment.employment_dto;

import com.kairos.user.employment.EmploymentDTO;

import java.util.ArrayList;
import java.util.List;

public class MainEmploymentResultDTO {

    private List<EmploymentOverlapDTO> employmentOverlapList =new ArrayList<>();
    private EmploymentDTO updatedMainEmployment;

    public List<EmploymentOverlapDTO> getEmploymentOverlapList() {
        return employmentOverlapList;
    }

    public void setEmploymentOverlapList(List<EmploymentOverlapDTO> employmentOverlapList) {
        this.employmentOverlapList = employmentOverlapList;
    }

    public EmploymentDTO getUpdatedMainEmployment() {
        return updatedMainEmployment;
    }

    public void setUpdatedMainEmployment(EmploymentDTO updatedMainEmployment) {
        this.updatedMainEmployment = updatedMainEmployment;
    }
}
