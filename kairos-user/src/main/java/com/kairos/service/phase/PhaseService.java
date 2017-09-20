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
        Phase phase=phaseGraphRepository.findByNameAndDisabled(unitId,phaseDTO.getName(),false);
        if(phase==null){
            throw new ActionNotPermittedException("Phase with name : "+phaseDTO.getName()+" already exists.");
        }
        if(phaseDTO.getDuration()<=0){
            throw new ActionNotPermittedException("Invalid Phase Duration : "+phaseDTO.getDuration());
        }
       // phase=phaseGraphRepository.findBySequenceAndDisabled(unitId,phaseDTO.getSequence(),false);
        phase=preparePhase(phaseDTO,unitOrganization);
        save(phase);
        return phase;
    }

    private Phase preparePhase(PhaseDTO phaseDTO,Organization unitOrganization) {
        Phase phase = new Phase();
        phase.setName(phaseDTO.getName());
        phase.setDuration(phaseDTO.getDuration());
        phase.setSequence(phaseDTO.getSequence());
        phase.setDescription(phaseDTO.getDescription());
        phase.setConstructionPhaseStartsAtDay(phaseDTO.getConstructionPhaseStartsAtDay());
        phase.setActivityAccess(phaseDTO.getActivityAccess());
        phase.setOrganization(unitOrganization);
        return  phase;
    }


    public PhaseDTO updatePhase(Long unitId, PhaseDTO phaseDTO) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Invalid unitId " + unitId);
        }
        Phase phase = phaseGraphRepository.findOne(phaseDTO.getId());
        if (phase == null) {
            throw new DataNotFoundByIdException("Phase does not Exists Id " + phaseDTO.getId());
        }
        phase=phaseGraphRepository.findByNameAndDisabled(unitId,phaseDTO.getName(),false);
        if(phase==null){
            throw new ActionNotPermittedException("Phase with name : "+phaseDTO.getName()+" already exists.");
        }
        preparePhase(phase,phaseDTO);
        save(phase);
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
        List<PhaseDTO> phases=phaseGraphRepository.getPhasesByUnit(unitId);
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
