package com.kairos.activity.persistence.repository.phase;


import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.response.dto.web.cta.PhaseResponseDTO;
import com.kairos.response.dto.web.phase.PhaseDTO;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 26/9/17.
 */
public interface CustomPhaseMongoRepository {
     List<PhaseDTO> getPlanningPhasesByUnit(Long unitId, Sort.Direction direction);

     List<PhaseDTO> getPhasesByUnit(Long unitId, Sort.Direction direction);
     List<PhaseResponseDTO> getApplicablePlanningPhasesByUnit(Long unitId);
     List<OrganizationPhaseDTO> getPhasesGroupByOrganization();
     Boolean checkPhaseByName(BigInteger phaseId, String name);
     Boolean checkPhaseBySequence(BigInteger phaseId, int sequence);
     List<PhaseDTO> getNextApplicablePhasesOfUnitBySequence(Long unitId, int sequence);
     List<Phase> getPlanningPhasesByUnit(Long unitId);
}
