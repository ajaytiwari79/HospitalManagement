package com.kairos.persistence.repository.phase;


import com.kairos.activity.phase.PhaseDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.user.organization.OrganizationPhaseDTO;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 26/9/17.
 */
public interface CustomPhaseMongoRepository {
     List<PhaseDTO> getPlanningPhasesByUnit(Long unitId, Sort.Direction direction);

     List<PhaseDTO> getPhasesByUnit(Long unitId, Sort.Direction direction);
     List<PhaseDTO> getApplicablePlanningPhasesByUnit(Long unitId);
     List<PhaseResponseDTO> getAllPlanningPhasesByUnit(Long unitId);
     List<OrganizationPhaseDTO> getPhasesGroupByOrganization();
     Boolean checkPhaseByName(BigInteger phaseId, String name);
     Boolean checkPhaseBySequence(BigInteger phaseId, int sequence);
     List<PhaseDTO> getNextApplicablePhasesOfUnitBySequence(Long unitId, int sequence);
     List<Phase> getPlanningPhasesByUnit(Long unitId);
}
