package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.RiskSeverity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class RiskBasicResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String riskRecommendation;
    private LocalDate dueDate;
    private boolean isReminderActive;
    private int daysToReminderBefore;
    private RiskSeverity riskLevel;

}
