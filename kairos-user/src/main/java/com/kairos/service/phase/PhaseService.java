package com.kairos.service.phase;



import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.phase.Phase;
import com.kairos.persistence.model.user.phase.PhaseDTO;
import com.kairos.persistence.model.user.phase.PhaseOrganizationRelation;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.phase.PhaseGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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


    public void createPhasesByUnitId(Long unitId) {

        Organization unitOrganization = organizationGraphRepository.findOne(unitId);
        if (unitOrganization == null) {
            throw new DataNotFoundByIdException("Invalid Organization id : " + unitId);
        }

        List<Phase> phases = getPhases();
        if (phases == null) {
            throw new DataNotFoundByIdException("Phases not found in DB ");
        }

        for (Phase phase : phases) {
            PhaseOrganizationRelation phaseOrganizationRelation = new PhaseOrganizationRelation(phase, unitOrganization, 1);
            save(phaseOrganizationRelation);
        }


    }

    public  void createPhases(Organization unitOrganization) {


        List<Phase> phases = getPhases();
        if (phases == null) {
            throw new DataNotFoundByIdException("Phases not found in DB ");
        }

        for (Phase phase : phases) {
            PhaseOrganizationRelation phaseOrganizationRelation = new PhaseOrganizationRelation(phase, unitOrganization, 1);
            save(phaseOrganizationRelation);
        }
    }

    public void createPhases() {

        Phase requestPhase = phaseGraphRepository.findByNameAndDisabled(REQUEST_PHASE_NAME, false);
        if (requestPhase == null) {
            requestPhase = new Phase(REQUEST_PHASE_NAME, REQUEST_PHASE_DESCRIPTION, false);
            save(requestPhase);
        }

        Phase puzzlePhase = phaseGraphRepository.findByNameAndDisabled(PUZZLE_PHASE_NAME, false);
        if (puzzlePhase == null) {
            puzzlePhase = new Phase(PUZZLE_PHASE_NAME, PUZZLE_PHASE_DESCRIPTION, false);
            save(puzzlePhase);
        }

        Phase constructionPhase = phaseGraphRepository.findByNameAndDisabled(CONSTRUCTION_PHASE_NAME, false);
        if (constructionPhase == null) {
            constructionPhase = new Phase(CONSTRUCTION_PHASE_NAME, CONSTRUCTION_PHASE_DESCRIPTION, false);
            save(constructionPhase);
        }
        Phase finalPhase = phaseGraphRepository.findByNameAndDisabled(FINAL_PHASE_NAME, false);

        if (finalPhase == null) {
            finalPhase = new Phase(FINAL_PHASE_NAME, FINAL_PHASE_DESCRIPTION, false);
            save(finalPhase);
        }

    }


    public void linkPhasesWithAllOrganizations() {

        List<Organization> organizationIdList = organizationGraphRepository.getAllOrganizationIdsWithoutPhases();
        List<Phase> phases = getPhases();

        if (phases == null || phases.isEmpty()) {

            throw new DataNotFoundByIdException("Phases not found while creating relation with organization at BootService.");
        }
        for (Organization organization : organizationIdList) {
            for (Phase phase : phases) {
                PhaseOrganizationRelation phaseOrganizationRelation = new PhaseOrganizationRelation(phase, organization, DURATION_IN_WEEK);
                save(phaseOrganizationRelation);
            }

        }

    }

    private List<Phase> getPhases() {
        List<Phase> phases = phaseGraphRepository.findAll();
        return phases;
    }


    /*
    *@Author vipul
    *
     *
      *
      *
      * */
    public List<PhaseDTO> getPhasesByUnit(Long unitId) {

        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Can't find unit with provided Id " + unitId);
        }


        List<PhaseDTO> phases  = phaseGraphRepository.findAllPhaseWithDuration(unitId);
        if (phases == null || phases.isEmpty()) {

            throw new DataNotFoundByIdException("Phases not found.");
        }
        return phases;
    }

    public PhaseDTO updatePhase(Long unitId, Long phaseId, PhaseDTO phaseDTO) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Can't find unit with provided Id " + unitId);
        }
        Phase phase = phaseGraphRepository.findOne(phaseId);
        if (phase == null) {
            throw new DataNotFoundByIdException("Phase does not Exists Id " + phaseId);
        }
        phaseGraphRepository.findAndUpdateByPhaseAndDuration(unitId, phaseId, phaseDTO.getDuration());
        return phaseDTO;
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
