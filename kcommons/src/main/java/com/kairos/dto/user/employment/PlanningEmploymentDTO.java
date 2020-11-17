package com.kairos.dto.user.employment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.enums.EmploymentSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class PlanningEmploymentDTO {

    private Long id;
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private boolean nightWorker;
    private EmploymentSubType employmentSubType;
    private List<EmploymentLinesDTO> employmentLines;
    private List<EmploymentTypeDTO> employmentList;
    private EmploymentTypeDTO employmentType;
    private ExpertiseDTO expertise;

    @Setter
    @Getter
    @NoArgsConstructor
    class ExpertiseDTO {
        private Long id;
        private String name;
        private String startDate;
        private String endDate;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    class PlanningEmploymentLinesDTO {
        private Long id;
        private Long employmentId;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<EmploymentLineFunction> functions;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    class EmploymentLineFunction {
        private Long id;
        private String name;
        private String icon;
        private String code;
    }
}
