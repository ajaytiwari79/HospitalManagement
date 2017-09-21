package com.kairos.service.phase;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.phase.Phase;
import com.kairos.persistence.model.user.phase.PhaseDTO;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.phase.PhaseGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by pawanmandhan on 29/8/17.
 */
@Service
public class PhaseService extends UserBaseService {

    @Inject
    private PhaseGraphRepository phaseGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;


    public Phase createPhasesByUnitId(Long unitId, PhaseDTO phaseDTO) {

        Organization unitOrganization = organizationGraphRepository.findOne(unitId);
        if (unitOrganization == null) {
            throw new DataNotFoundByIdException("Invalid unitId : " + unitId);
        }
        Phase phase = null;
        try {
            phase = phaseGraphRepository.findByNameAndDisabled(unitId, phaseDTO.getName(), false);
        } catch (Exception e) {
            System.out.println(e);
        }
        if (phase != null) {
            throw new ActionNotPermittedException("Phase with name : " + phaseDTO.getName() + " already exists.");
        }
        if (phaseDTO.getDuration() <= 0) {
            throw new ActionNotPermittedException("Invalid Phase Duration : " + phaseDTO.getDuration());
        }
        // phase=phaseGraphRepository.findBySequenceAndDisabled(unitId,phaseDTO.getSequence(),false);
        phase = preparePhase(phaseDTO, unitOrganization);
        save(phase);
        phase.setOrganization(null);
        return phase;
    }

    private ArrayList getDefaultPhases() {
        Phase requestPhase = new Phase(REQUEST_PHASE_NAME, REQUEST_PHASE_DESCRIPTION, false, 1, 5, 2, 4);
        Phase constructionPhase = new Phase(CONSTRUCTION_PHASE_NAME, CONSTRUCTION_PHASE_DESCRIPTION, false, 1, 5, 2, 2);
        Phase puzzlePhase = new Phase(PUZZLE_PHASE_NAME, PUZZLE_PHASE_DESCRIPTION, false, 1, 5, 2, 2);
        Phase finalPhase = new Phase(FINAL_PHASE_NAME, FINAL_PHASE_DESCRIPTION, false, 1, 5, 2, 2);

        ArrayList<Phase> phases = new ArrayList();
        phases.add(requestPhase);
        phases.add(constructionPhase);
        phases.add(puzzlePhase);
        phases.add(finalPhase);
        return phases;
    }

    public void createDefaultPhases() {
        List<Organization> organizationIdList = organizationGraphRepository.getAllOrganizationIdsWithoutPhases();
        for (Organization organization : organizationIdList) {
            ArrayList<Phase> phases = getDefaultPhases();
            for (Phase phase : phases) {
                phase.setOrganization(organization);
                save(phase);
            }

        }

    }

    private Phase preparePhase(PhaseDTO phaseDTO, Organization unitOrganization) {
        Phase phase = new Phase();
        phase.setName(phaseDTO.getName());
        phase.setDuration(phaseDTO.getDuration());
        phase.setSequence(phaseDTO.getSequence());
        phase.setDescription(phaseDTO.getDescription());
        phase.setConstructionPhaseStartsAtDay(phaseDTO.getConstructionPhaseStartsAtDay());
        phase.setActivityAccess(phaseDTO.getActivityAccess());
        phase.setOrganization(unitOrganization);
        return phase;
    }

    public void deleteOldPhase() {
        //  phaseGraphRepository.detachDeletePhases();
    }

    public PhaseDTO updatePhase(Long unitId, PhaseDTO phaseDTO) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Invalid unitId " + unitId);
        }
        Phase oldPhase = phaseGraphRepository.findOne(phaseDTO.getId());
        if (oldPhase == null) {
            throw new DataNotFoundByIdException("Phase does not Exists Id " + phaseDTO.getId());
        }
        Phase phase = phaseGraphRepository.findByNameAndDisabled(unitId, phaseDTO.getName(), false);
        if (phase != null && !oldPhase.getName().equals(phaseDTO.getName())) {
            throw new ActionNotPermittedException("Phase with name : " + phaseDTO.getName() + " already exists.");
        }
        preparePhase(oldPhase, phaseDTO);
        save(oldPhase);
        //phaseGraphRepository.findAndUpdateByPhaseAndDuration(unitId, phaseId, phaseDTO.getDuration());
        return phaseDTO;
    }


    private void preparePhase(Phase phase, PhaseDTO phaseDTO) {
        phase.setName(phaseDTO.getName());
        phase.setDuration(phaseDTO.getDuration());
        phase.setSequence(phaseDTO.getSequence());
        phase.setDescription(phaseDTO.getDescription());
        phase.setConstructionPhaseStartsAtDay(phaseDTO.getConstructionPhaseStartsAtDay());
        phase.setActivityAccess(phaseDTO.getActivityAccess());
    }


    /*
    *@Author vipul
    */
    public List<PhaseDTO> getPhasesByUnit(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Can't find unit with provided Id " + unitId);
        }
        List<PhaseDTO> phases = phaseGraphRepository.getPhasesByUnit(unitId);
        return phases;
    }


    public boolean removePhase(Long phaseId) {
        Phase phase = phaseGraphRepository.findOne(phaseId);
        if (phase == null) {
            return false;
        }
        phase.setDisabled(false);
        save(phase);
        if (phaseGraphRepository.findOne(phaseId).isDisabled()) {
            return false;
        }
        return true;
    }


}
