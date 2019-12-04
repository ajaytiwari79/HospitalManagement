package com.kairos.dto.user.country.experties;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SeniorDaysDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private Long expertiseId;
    private List<AgeRangeDTO> seniorDaysDetails;

}
