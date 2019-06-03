package com.kairos.dto.planner.solverconfig.unit;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

@Getter
@Setter
public class UnitSolverConfigDTO extends SolverConfigDTO {
    private Long unitId;
    private Long parentCountrySolverConfigId;
    private List<Long> organizationSubServiceIds;

    public List<Long> getOrganizationSubServiceIds() {

        return isNullOrElse(organizationSubServiceIds,new ArrayList<>());
    }
}
