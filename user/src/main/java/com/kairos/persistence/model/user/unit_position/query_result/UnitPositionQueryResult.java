package com.kairos.persistence.model.user.unit_position.query_result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.experties.AppliedFunctionDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
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
    private Organization union;
    private LocalDate lastWorkingDate;
    private Long parentUnitId;
    private Long unitId;
    private Long staffId;
    private Map<String, Object> reasonCode;
    private Map<String, Object> unitInfo;
    private List<UnitPositionLinesQueryResult> positionLines;
    private Boolean history;
    private Boolean editable=true;
    private Boolean published;
    private List<AppliedFunctionDTO> appliedFunctions;
    private boolean mainUnitPosition;
    private String unitName;
    private float taxDeductionPercentage;
    private long accumulatedTimebankMinutes;

    public UnitPositionQueryResult() {
        //Default Constructor
    }

    public UnitPositionQueryResult(Expertise expertise, LocalDate startDate, LocalDate endDate, long id, Organization union, LocalDate lastWorkingDate,  WTAResponseDTO wta,Long unitId,Boolean published,Long parentUnitId) {
        this.expertise = expertise;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastWorkingDate = lastWorkingDate;
        this.id = id;
        this.union = union;
        this.workingTimeAgreement=wta;
        this.unitId=unitId;
        this.published=published;
        this.parentUnitId=parentUnitId;

    }

    public UnitPositionQueryResult(Expertise expertise, LocalDate startDate, LocalDate endDate, long id,Organization union, LocalDate lastWorkingDate,  WTAResponseDTO wta,Long unitId,Long parentUnitId,Boolean published,
                                   Map<String, Object> reasonCode,Map<String, Object> unitInfo,boolean mainUnitPosition,List<UnitPositionLinesQueryResult> positionLines,float taxDeductionPercentage,long accumulatedTimebankMinutes) {
        this.expertise = expertise;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastWorkingDate = lastWorkingDate;
        this.id = id;
        this.union = union;
        this.workingTimeAgreement=wta;
        this.unitId=unitId;
        this.parentUnitId=parentUnitId;
        this.published=published;
        this.reasonCode=reasonCode;
        this.unitInfo=unitInfo;
        this.mainUnitPosition=mainUnitPosition;
        this.positionLines=positionLines;
        this.taxDeductionPercentage=taxDeductionPercentage;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;

    }

    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    /**
     *  Please do not use in backend its just only for FE compactibility
     */
    private WTAResponseDTO workingTimeAgreement;



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



    public List<UnitPositionLinesQueryResult> getPositionLines() {
        return Optional.ofNullable(positionLines).orElse(new ArrayList<>());
    }

    public void setPositionLines(List<UnitPositionLinesQueryResult> positionLines) {
        this.positionLines = positionLines;
    }

    public Map<String, Object> getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Map<String, Object> reasonCode) {
        this.reasonCode = reasonCode;
    }

    public List<AppliedFunctionDTO> getAppliedFunctions() {
        return appliedFunctions;
    }

    public void setAppliedFunctions(List<AppliedFunctionDTO> appliedFunctions) {
        this.appliedFunctions = appliedFunctions;
    }

    public WTAResponseDTO getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WTAResponseDTO workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public boolean isMainUnitPosition() {
        return mainUnitPosition;
    }

    public void setMainUnitPosition(boolean mainUnitPosition) {
        this.mainUnitPosition = mainUnitPosition;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public float getTaxDeductionPercentage() {
        return taxDeductionPercentage;
    }

    public void setTaxDeductionPercentage(float taxDeductionPercentage) {
        this.taxDeductionPercentage = taxDeductionPercentage;
    }

    public long getAccumulatedTimebankMinutes() {
        return accumulatedTimebankMinutes;
    }

    public void setAccumulatedTimebankMinutes(long accumulatedTimebankMinutes) {
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }
}
