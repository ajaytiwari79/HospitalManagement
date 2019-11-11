package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.user.staff.staff.Staff;
import com.kairos.enums.EmploymentSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by vipul on 29/1/18.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffEmploymentDetails {
    private Expertise expertise;
    private Staff staff;
    private EmploymentType employmentType;
    private WTAResponseDTO workingTimeAgreement;
    private int workingDaysInWeek;
    private LocalDate endDate;
    private LocalDate StartDate;
    private Long lastModificationDate;
    private Integer totalWeeklyHours;
    private Integer fullTimeWeeklyMinutes;
    private Float avgDailyWorkingHours;
    private Long id;
    private Long staffId;
    private Long userId;
    private Float salary;
    private int totalWeeklyMinutes;
    private List<CTARuleTemplateDTO> ctaRuleTemplates=new ArrayList<>();
    private ZoneId unitTimeZone;
    private Long countryId;
    private List<FunctionDTO> appliedFunctions= new ArrayList<>();
    private BigInteger excludedPlannedTime;
    private BigInteger includedPlannedTime;
    private Long unitId;
    private BigDecimal hourlyCost;
    private Long functionId;
    private List<EmploymentLinesDTO> employmentLines;
    private Boolean history;
    private Boolean editable;
    private boolean published;
    //This is the Intial value of accumulatedTimebank
    private long accumulatedTimebankMinutes;
    private LocalDate accumulatedTimebankDate;
    private CTAResponseDTO costTimeAgreement;
    private EmploymentSubType employmentSubType;
    private List<ProtectedDaysOffSetting> protectedDaysOffSettings;

    public StaffEmploymentDetails(Long id, Long staffId, List<EmploymentLinesDTO> employmentLines, CTAResponseDTO costTimeAgreement) {
        this.id = id;
        this.staffId = staffId;
        this.employmentLines = employmentLines;
        this.costTimeAgreement = costTimeAgreement;
    }

    public StaffEmploymentDetails(Long id,Expertise expertise, LocalDate endDate, LocalDate startDate, Long unitId, EmploymentSubType employmentSubType) {
        this.id=id;
        this.expertise = expertise;
        this.endDate = endDate;
        this.StartDate = startDate;
        this.unitId = unitId;
        this.employmentSubType = employmentSubType;
    }

    public StaffEmploymentDetails(Long unitId) {
        this.unitId = unitId;
    }

    public StaffEmploymentDetails(int workingDaysInWeek, int totalWeeklyMinutes) {
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public StaffEmploymentDetails( Long staffId, List<CTARuleTemplateDTO> ctaRuleTemplates, BigDecimal hourlyCost, List<EmploymentLinesDTO> employmentLines) {
        this.staffId = staffId;
        this.ctaRuleTemplates = ctaRuleTemplates;
        this.hourlyCost = hourlyCost;
        this.employmentLines = employmentLines;
    }

    public StaffEmploymentDetails(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
}