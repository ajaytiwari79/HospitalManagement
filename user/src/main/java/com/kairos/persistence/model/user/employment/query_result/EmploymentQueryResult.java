package com.kairos.persistence.model.user.employment.query_result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
@NoArgsConstructor
public class EmploymentQueryResult {
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
    private List<EmploymentLinesQueryResult> employmentLines;
    private Boolean history;
    private Boolean editable=true;
    private Boolean published;
    private EmploymentSubType employmentSubType;
    private List<FunctionDTO> appliedFunctions;
    private String unitName;
    private float taxDeductionPercentage;
    private long accumulatedTimebankMinutes;
    private LocalDate accumulatedTimebankDate;
    private long totalShifts;
    private SeniorAndChildCareDaysDTO seniorAndChildCareDays;
    private Long expertiseId;


    /**
     *  Please do not use in backend its just only for FE compactibility
     */
    private WTAResponseDTO workingTimeAgreement;

    public EmploymentQueryResult(Expertise expertise, LocalDate startDate, LocalDate endDate, long id, Organization union, LocalDate lastWorkingDate, WTAResponseDTO wta, Long unitId, Boolean published, Long parentUnitId) {
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

    public EmploymentQueryResult(Expertise expertise, LocalDate startDate, LocalDate endDate, long id, Organization union, LocalDate lastWorkingDate, WTAResponseDTO wta, Long unitId, Long parentUnitId, Boolean published,
                                 Map<String, Object> reasonCode, Map<String, Object> unitInfo, EmploymentSubType employmentSubType, List<EmploymentLinesQueryResult> employmentLines, float taxDeductionPercentage, long accumulatedTimebankMinutes, LocalDate accumulatedTimebankDate) {
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
        this.employmentSubType =employmentSubType;
        this.employmentLines=employmentLines;
        this.taxDeductionPercentage=taxDeductionPercentage;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
        this.accumulatedTimebankDate = accumulatedTimebankDate;

    }

    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    public List<EmploymentLinesQueryResult> getEmploymentLines() {
        return Optional.ofNullable(employmentLines).orElse(new ArrayList<>());
    }


}
