package com.kairos.activity.persistence.repository.phase;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.activity.persistence.model.phase.Phase;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 26/9/17.
 */
public interface CustomPhaseMongoRepository {
     List<PhaseDTO> getPhasesByUnit(Long unitId);
     List<PhaseDTO> getApplicablePhasesByUnit(Long unitId);
     List<OrganizationPhaseDTO> getPhasesGroupByOrganization();
     Boolean checkPhaseByName(BigInteger phaseId, String name);
     Phase getNextApplicablePhasesOfUnitBySequence(Long unitId, int sequence);
}
