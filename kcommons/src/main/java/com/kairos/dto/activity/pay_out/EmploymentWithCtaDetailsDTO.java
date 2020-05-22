package com.kairos.dto.activity.pay_out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EmploymentWithCtaDetailsDTO {

    private Long employmentId;
    private List<CTARuleTemplateCalulatedPayOutDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;
    private Long countryId;
    private int minutesFromCta;
    private ZoneId unitTimeZone;
}
