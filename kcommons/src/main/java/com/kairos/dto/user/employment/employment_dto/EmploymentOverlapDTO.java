package com.kairos.dto.user.employment.employment_dto;

import java.time.LocalDate;

public class EmploymentOverlapDTO {
    private String organizationName;
    private LocalDate mainEmploymentStartDate;
    private LocalDate mainEmploymentEndDate;
    private LocalDate afterChangeStartDate;
    private LocalDate afterChangeEndDate;


    public LocalDate getMainEmploymentStartDate() {
        return mainEmploymentStartDate;
    }

    public void setMainEmploymentStartDate(LocalDate mainEmploymentStartDate) {
        this.mainEmploymentStartDate = mainEmploymentStartDate;
    }

    public LocalDate getMainEmploymentEndDate() {
        return mainEmploymentEndDate;
    }

    public void setMainEmploymentEndDate(LocalDate mainEmploymentEndDate) {
        this.mainEmploymentEndDate = mainEmploymentEndDate;
    }

    public LocalDate getAfterChangeStartDate() {
        return afterChangeStartDate;
    }

    public void setAfterChangeStartDate(LocalDate afterChangeStartDate) {
        this.afterChangeStartDate = afterChangeStartDate;
    }

    public LocalDate getAfterChangeEndDate() {
        return afterChangeEndDate;
    }

    public void setAfterChangeEndDate(LocalDate afterChangeEndDate) {
        this.afterChangeEndDate = afterChangeEndDate;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


}
