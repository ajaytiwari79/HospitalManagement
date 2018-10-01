package com.kairos.persistence.model.user.unit_position.query_result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.position_code.PositionCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by vipul on 10/8/17.
 */

@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitPositionQueryResult {
    private Expertise expertise;
    private Long startDateMillis;
    private Long endDateMillis;
    private Long id;
    private Map<String, Object> employmentType;
    private Map<String, Object> seniorityLevel;
    private PositionCode positionCode;
    private CTAResponseDTO costTimeAgreement;
    private Organization union;
    private Long lastWorkingDateMillis;
    private Long parentUnitId;
    private Long unitId;
    private Long reasonCodeId;
    private Map<String, Object> unitInfo;
    private WTAResponseDTO workingTimeAgreement;

    private List<PositionLinesQueryResult> positionLines;
    private Long positionLineId;
    private Boolean history;
    private Boolean editable;
    private Boolean published;

    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    public void setUnitInfo(Map<String, Object> unitInfo) {
        this.unitInfo = unitInfo;
    }

    public WTAResponseDTO getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WTAResponseDTO workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
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

    public CTAResponseDTO getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(CTAResponseDTO costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }

    public Long getLastWorkingDateMillis() {
        return lastWorkingDateMillis;
    }

    public void setLastWorkingDateMillis(Long lastWorkingDateMillis) {
        this.lastWorkingDateMillis = lastWorkingDateMillis;
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

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }


    public Map<String, Object> getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(Map<String, Object> employmentType) {
        this.employmentType = employmentType;
    }

    public Map<String, Object> getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(Map<String, Object> seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
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

    public Long getPositionLineId() {
        return positionLineId;
    }

    public void setPositionLineId(Long positionLineId) {
        this.positionLineId = positionLineId;
    }

    public UnitPositionQueryResult() {
        //default cons
    }

    public UnitPositionQueryResult(Expertise expertise, Long startDateMillis, Long endDateMillis,  long id,  PositionCode positionCode, Organization union, Long lastWorkingDateMillis, CTAResponseDTO cta, WTAResponseDTO wta) {
        this.expertise = expertise;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.lastWorkingDateMillis = lastWorkingDateMillis;
        this.id = id;
        this.positionCode = positionCode;
        this.union = union;
        this.costTimeAgreement = cta;
        this.workingTimeAgreement = wta;
    }

}
