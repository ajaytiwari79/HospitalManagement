package com.kairos.persistence.model.country.experties;

import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.UnionQueryResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by vipul on 27/3/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class UnionServiceWrapper {
    private List<UnionQueryResult> unions;
    private Iterable<OrganizationService> services;
    private List<Level> organizationLevels;
    private List<SectorDTO> sectors;

}
