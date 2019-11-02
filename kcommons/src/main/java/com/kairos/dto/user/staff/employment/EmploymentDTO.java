package com.kairos.dto.user.staff.employment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.employment_type.EmploymentCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class EmploymentDTO {

    @NotNull(message = "expertise is required for position")
    @Range(min = 0, message = "expertise is required for position")
    private Long expertiseId;
    private Long id;
    private Long employmentLineId;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastWorkingDate;

    @Range(min = 0, max = 60, message = "Incorrect Weekly minute")
    private int totalWeeklyMinutes;
    @Range(min = 0, message = "Incorrect Weekly Hours")
    private int totalWeeklyHours;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private BigDecimal hourlyCost;
    private Double salary;
    private Long employmentTypeId;
    @NotNull(message = "employmentTypeCategory can't be null")
    private EmploymentCategory employmentTypeCategory;
    @NotNull(message = "wta can't be null")
    private BigInteger wtaId;
    @NotNull(message = "cta can't be null")
    private BigInteger ctaId;
    @NotNull(message = "staffId is missing")
    @Range(min = 0, message = "staffId is missing")
    private Long staffId;
    // private Long expiryDate;


    private Long unionId;
    private Long parentUnitId;

    @NotNull(message = "unitId  is required for position")
    @Range(min = 0, message = "unit Id  is required for position")
    private Long unitId;

    private Long reasonCodeId;

    @NotNull(message = "seniorityLevel  is required for position")
    @Range(min = 0, message = "seniorityLevel  is required for position")
    private Long seniorityLevelId;
    private Set<FunctionsDTO> functions = new HashSet<>();
    private Long timeCareExternalId;
    private boolean published;
    private Long accessGroupId;
    @NotNull(message = "employmentSubType Not selected")
    private EmploymentSubType employmentSubType;
    private float taxDeductionPercentage;
    private ExpertiseResponseDTO expertise;
    //This is the Intial value of accumulatedTimebank
    private long accumulatedTimebankMinutes;
    private LocalDate accumulatedTimebankDate;


    public EmploymentDTO(Long expertiseId, LocalDate startDate, LocalDate endDate, int totalWeeklyMinutes,
                         float avgDailyWorkingHours, BigDecimal hourlyCost, Double salary, Long employmentTypeId) {
        this.salary = salary;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.hourlyCost = hourlyCost;
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentTypeId = employmentTypeId;

    }


    public EmploymentDTO(Long expertiseId, LocalDate startDate, LocalDate endDate, int totalWeeklyHours, Long employmentTypeId,
                         Long staffId, BigInteger wtaId, BigInteger ctaId, Long unitId, Long timeCareExternalId) {
        this.expertiseId = expertiseId;
        this.employmentTypeId = employmentTypeId;
        this.staffId = staffId;
        this.wtaId = wtaId;
        this.ctaId = ctaId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalWeeklyHours = totalWeeklyHours;
        this.timeCareExternalId = timeCareExternalId;
        this.unitId = unitId;

    }
}
