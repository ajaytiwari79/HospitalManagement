package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.time_bank.CTARuleTemplateCalulatedTimeBankDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class EmploymentWithCtaDetailsDTO {

    private Long employmentId;
    private List<CTARuleTemplateCalulatedTimeBankDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;
    private int totalWeeklyMinutes;
    private int fullTimeWeeklyMinutes;
    private Long countryId;
    private int minutesFromCta;
    private ZoneId unitTimeZone;




    public EmploymentWithCtaDetailsDTO(Long employmentId) {
        this.employmentId = employmentId;
    }

    public EmploymentWithCtaDetailsDTO(Long employmentId, int contractedMinByWeek, int workingDaysPerWeek, LocalDate employmentStartDate, LocalDate employmentEndDate) {
        this.employmentId = employmentId;
        this.contractedMinByWeek = contractedMinByWeek;
        this.workingDaysPerWeek = workingDaysPerWeek;
        this.employmentStartDate = employmentStartDate;
        this.employmentEndDate = employmentEndDate;
    }


}
