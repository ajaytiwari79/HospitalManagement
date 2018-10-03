package com.kairos.persistence.model.user.unit_position.query_result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.position_code.PositionCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by vipul on 10/8/17.
 */

@QueryResult

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitPositionQueryResult {
    private Expertise expertise;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long id;
    private PositionCode positionCode;
    private Organization union;
    private LocalDate lastWorkingDate;
    private Long parentUnitId;
    private Long unitId;

    private Map<String, Object> reasonCode;
    private Map<String, Object> unitInfo;
    private List<PositionLinesQueryResult> positionLines;
    private Boolean history;
    private Boolean editable;
    private Boolean published;

    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    public void setUnitInfo(Map<String, Object> unitInfo) {
        this.unitInfo = unitInfo;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

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

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public Long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }

    public LocalDate getLastWorkingDate() {
        return lastWorkingDate;
    }

    public void setLastWorkingDate(LocalDate lastWorkingDate) {
        this.lastWorkingDate = lastWorkingDate;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Boolean getHistory() {
        return history;
    }

    public void setHistory(Boolean history) {
        this.history = history;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public List<PositionLinesQueryResult> getPositionLines() {
        return Optional.ofNullable(positionLines).orElse(new ArrayList<>());
    }

    public void setPositionLines(List<PositionLinesQueryResult> positionLines) {
        this.positionLines = positionLines;
    }

    public Map<String, Object> getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Map<String, Object> reasonCode) {
        this.reasonCode = reasonCode;
    }

    public UnitPositionQueryResult() {
        //default cons
    }

    public UnitPositionQueryResult(Expertise expertise, LocalDate startDate, LocalDate endDate, long id, PositionCode positionCode, Organization union, LocalDate lastWorkingDate, CTAResponseDTO cta, WTAResponseDTO wta) {
        this.expertise = expertise;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastWorkingDate = lastWorkingDate;
        this.id = id;
        this.positionCode = positionCode;
        this.union = union;
    }

}
