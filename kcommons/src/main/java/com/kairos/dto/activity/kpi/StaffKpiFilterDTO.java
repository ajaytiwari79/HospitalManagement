package com.kairos.dto.activity.kpi;

import java.time.LocalDate;
import java.util.List;

public class StaffKpiFilterDTO {
    private Long id;
    private String firstName;
    private String lastName;
    //UnitPosition startDate endate
    private LocalDate startDate;
    private LocalDate endDate;
    private List<UnitPositionLinesDTO> positionLines;


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<UnitPositionLinesDTO> getPositionLines() {
        return positionLines;
    }

    public void setPositionLines(List<UnitPositionLinesDTO> positionLines) {
        this.positionLines = positionLines;
    }

    public String getFullName(){
        return this.firstName+" "+this.getLastName();
    }
}
