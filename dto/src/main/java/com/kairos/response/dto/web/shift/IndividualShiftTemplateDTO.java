package com.kairos.response.dto.web.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class IndividualShiftTemplateDTO {
    private BigInteger id;
    private String name;
    private String remarks;
    @Range(min = 0)
    @NotNull(message = "error.shiftTemplate.activityId.notnull")
    private BigInteger activityId;
    private Long unitId;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private boolean isMainShift = true;
    private List<IndividualShiftTemplateDTO> subShifts;

    public IndividualShiftTemplateDTO() {
        //Default Constructor
    }

    public IndividualShiftTemplateDTO(BigInteger id, String name, String remarks, @Range(min = 0) @NotNull(message = "error.shiftTemplate.activityId.notnull") BigInteger activityId, Long unitId
                               , LocalTime startTime, LocalTime endTime, boolean isMainShift,
                                      List<IndividualShiftTemplateDTO> subShifts) {
        this.id = id;
        this.name = name;
        this.remarks = remarks;
        this.activityId = activityId;
        this.unitId = unitId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMainShift = isMainShift;
        this.subShifts = subShifts;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isMainShift() {
        return isMainShift;
    }

    public void setMainShift(boolean mainShift) {
        isMainShift = mainShift;
    }

    public List<IndividualShiftTemplateDTO> getSubShifts() {
        return subShifts=Optional.ofNullable(subShifts).orElse(new ArrayList<>());
    }

    public void setSubShifts(List<IndividualShiftTemplateDTO> subShifts) {
        this.subShifts = subShifts;
    }
}
