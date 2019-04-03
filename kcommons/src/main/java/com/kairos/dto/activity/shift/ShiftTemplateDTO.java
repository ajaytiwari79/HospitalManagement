package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;


public class ShiftTemplateDTO {
    private BigInteger id;
    private LocalDate startDate;
    @NotBlank
    private String name;
    private List<IndividualShiftTemplateDTO> shiftList;
    private Long createdBy;
    private Long unitId;
    private Set<BigInteger> individualShiftTemplateIds;

    public ShiftTemplateDTO() {
        //Default Constructor
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
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

    public List<IndividualShiftTemplateDTO> getShiftList() {
        return shiftList=Optional.ofNullable(shiftList).orElse(new ArrayList<>());
    }

    public void setShiftList(List<IndividualShiftTemplateDTO> shiftList) {
        this.shiftList = shiftList;
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

    public Set<BigInteger> getIndividualShiftTemplateIds() {
        return individualShiftTemplateIds=Optional.ofNullable(individualShiftTemplateIds).orElse(new HashSet<>());
    }

    public void setIndividualShiftTemplateIds(Set<BigInteger> individualShiftTemplateIds) {
        this.individualShiftTemplateIds = individualShiftTemplateIds;
    }
}
