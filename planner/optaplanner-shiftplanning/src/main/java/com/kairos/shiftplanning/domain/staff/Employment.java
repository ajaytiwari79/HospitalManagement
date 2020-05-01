package com.kairos.shiftplanning.domain.staff;

import com.kairos.enums.EmploymentSubType;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employment {

    private Long id;
    private Expertise expertise;
    private LocalDate startDate;
    private LocalDate endDate;
    private EmploymentSubType employmentSubType;
    private List<EmploymentLine> employmentLines;
    private EmploymentType employmentType;
    @Builder.Default
    private Map<LocalDate,Function> dateWiseFunctionMap = new HashMap<>();

    public EmploymentLine getEmploymentLinesByDate(LocalDate localDate) {
        return employmentLines.stream().filter(employmentLine -> employmentLine.isValidByDate(localDate)).findFirst().orElse(null);
    }
}
