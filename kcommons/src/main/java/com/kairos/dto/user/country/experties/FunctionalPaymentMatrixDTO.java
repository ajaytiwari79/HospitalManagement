package com.kairos.dto.user.country.experties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Getter
@Setter
public class FunctionalPaymentMatrixDTO {
    private Set<Long> payGroupAreasIds;
    private List<SeniorityLevelFunctionDTO> seniorityLevelFunction;
    private Long id;
}
