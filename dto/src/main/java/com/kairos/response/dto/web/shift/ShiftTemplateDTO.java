package com.kairos.response.dto.web.shift;

import java.math.BigInteger;
import java.util.List;

public class ShiftTemplateDTO {
    private BigInteger id;
    private String name;
    private List<IndividualShiftTemplateDTO> shifts;
    private Long createdBy;
    private Long unitId;

    public ShiftTemplateDTO() {
        //Default Constructor
    }

    public ShiftTemplateDTO(BigInteger id, String name, List<IndividualShiftTemplateDTO> shifts, Long createdBy, Long unitId) {
        this.id = id;
        this.name = name;
        this.shifts = shifts;
        this.createdBy = createdBy;
        this.unitId = unitId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IndividualShiftTemplateDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<IndividualShiftTemplateDTO> shifts) {
        this.shifts = shifts;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
