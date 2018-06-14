package com.kairos.response.dto.web.employment_dto;

import java.util.ArrayList;
import java.util.List;

public class MainEmploymentResultDTO {

    private List<EmploymentOverlapDTO> employmentOverlapDTOList=new ArrayList<>();
    private Boolean OverLapping;

    public List<EmploymentOverlapDTO> getEmploymentOverlapDTOList() {
        return employmentOverlapDTOList;
    }

    public void setEmploymentOverlapDTOList(List<EmploymentOverlapDTO> employmentOverlapDTOList) {
        this.employmentOverlapDTOList = employmentOverlapDTOList;
    }

    public Boolean getOverLapping() {
        return OverLapping;
    }

    public void setOverLapping(Boolean overLapping) {
        OverLapping = overLapping;
    }
}
