package com.kairos.activity.service.phase;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.DateUtils;
import com.kairos.persistence.model.enums.DurationType;
import com.kairos.enums.phase.PhaseType;
import com.kairos.response.dto.web.phase.PhaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vipul on 25/9/17.
 */
@Service
@Transactional
public class PhaseService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(PhaseService.class);
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private ExceptionService exceptionService;

    public List<Phase> createDefaultPhase(Long unitId, Long countryId) {
        List<PhaseDTO> countryPhases = phaseMongoRepository.findByCountryIdAndDeletedFalse(countryId);
        List<Phase> phases = new ArrayList<>();
        for (PhaseDTO phaseDTO : countryPhases) {
            Phase phase = new Phase(phaseDTO.getName(), phaseDTO.getDescription(), phaseDTO.getDuration(), phaseDTO.getDurationType(), phaseDTO.getSequence(), null,
                    unitId, phaseDTO.getId(), phaseDTO.getPhaseType(), phaseDTO.getStatus());

            phases.add(phase);
        }
        if (!phases.isEmpty()) {
            save(phases);
        }
        return phases;
    }

    /*
     *@Author vipul
     */
    public List<PhaseDTO> getPlanningPhasesByUnit(Long unitId) {
        OrganizationDTO unitOrganization = organizationRestClient.getOrganizationWithoutAuth(unitId);
        if (unitOrganization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id",unitId);
        }
        List<PhaseDTO> phases = phaseMongoRepository.getPlanningPhasesByUnit(unitId, Sort.Direction.DESC);
        return phases;
    }


    public List<PhaseDTO> getPhasesByUnit(Long unitId) {
        OrganizationDTO unitOrganization = organizationRestClient.getOrganizationWithoutAuth(unitId);
        if (unitOrganization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id", unitId);
        }
        List<PhaseDTO> phases = phaseMongoRepository.getPhasesByUnit(unitId, Sort.Direction.DESC);
        return phases;
    }

    public Map<String, List<PhaseDTO>> getCategorisedPhasesByUnit(Long unitId) {
        OrganizationDTO unitOrganization = organizationRestClient.getOrganizationWithoutAuth(unitId);
        if (unitOrganization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id",unitId);
        }
        List<PhaseDTO> phases = getPhasesByUnit(unitId);
        Map<String, List<PhaseDTO>> phasesData = new HashMap<>(2);
        phasesData.put("planningPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.PLANNING)).collect(Collectors.toList()));
        phasesData.put("actualPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.ACTUAL)).collect(Collectors.toList()));
        return phasesData;
    }

    public boolean removePhase(BigInteger phaseId) {
        Phase phase = phaseMongoRepository.findOne(phaseId);
        if (phase == null) {
            return false;
        }
        phase.setDeleted(true);
        save(phase);

        return true;
    }

    //TODO
    public List<OrganizationPhaseDTO> getPhasesGroupByOrganization() {
        return phaseMongoRepository.getPhasesGroupByOrganization();
    }

    public PhaseDTO getUnitPhaseByDate(Long unitId, Date date) {
        PhaseDTO phaseDTO = new PhaseDTO();
        LocalDate currentDate = LocalDate.now();
        LocalDate proposedDate = DateUtils.getLocalDateFromDate(date);
        long weekDifference = currentDate.until(proposedDate, ChronoUnit.WEEKS);
        OrganizationDTO unitOrganization = organizationRestClient.getOrganization(unitId);
        if (!Optional.ofNullable(unitOrganization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id", unitId);
        }
        List<PhaseDTO> phaseDTOS = phaseMongoRepository.getPlanningPhasesByUnit(unitId, Sort.Direction.ASC);
        int weekCount = 0;
        if (weekDifference < 0) {    // Week has passed so FINAL will be the object returned
            phaseDTO = phaseDTOS.get(0);
        } else {
            for (PhaseDTO phase : phaseDTOS) {
                for (int i = 0; i < phase.getDuration(); i++) {
                    if (weekDifference == weekCount) {
                        phaseDTO = phase;
                    }
                    weekCount++;
                }

            }
            if (weekDifference > weekCount) {    // Week has still greater  so It will be request and Request object will be  returned
                phaseDTO = phaseDTOS.get(phaseDTOS.size() - 1);
            }
        }

        return phaseDTO;
    }

    public Phase createPhaseInCountry(Long countryId, PhaseDTO phaseDTO) {
        long phaseExists = phaseMongoRepository.findBySequenceAndCountryIdAndDeletedFalse(phaseDTO.getSequence(), countryId);
        if (phaseExists > 0) {
            logger.info("Phase already exist by sequence in country" + phaseDTO.getCountryId());
            exceptionService.dataNotFoundByIdException("message.country.phase.sequence", phaseDTO.getCountryId());
        }
        Phase phase = buildPhaseForCountry(phaseDTO);
        phase.setCountryId(countryId);
        save(phase);
        return phase;
    }

    public Phase buildPhaseForCountry(PhaseDTO phaseDTO) {
        Phase phase = new Phase(phaseDTO.getName(), phaseDTO.getDescription(), phaseDTO.getDuration(), phaseDTO.getDurationType(), phaseDTO.getSequence(),
                phaseDTO.getCountryId(), phaseDTO.getOrganizationId(), phaseDTO.getParentCountryPhaseId(), phaseDTO.getPhaseType(), phaseDTO.getStatus());
        return phase;
    }

    public List<PhaseDTO> getPhasesByCountryId(Long countryId) {
        List<PhaseDTO> phases = phaseMongoRepository.findByCountryIdAndDeletedFalse(countryId);
        return phases;
    }

    public Map<String, List<PhaseDTO>> getPhasesWithCategoryByCountryId(Long countryId) {
        List<PhaseDTO> phases = getPhasesByCountryId(countryId);
        Map<String, List<PhaseDTO>> phasesData = new HashMap<>(2);
        phasesData.put("planningPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.PLANNING)).collect(Collectors.toList()));
        phasesData.put("actualPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.ACTUAL)).collect(Collectors.toList()));
        return phasesData;
    }

    public List<PhaseDTO> getApplicablePlanningPhasesByOrganizationId(Long orgId) {
        List<PhaseDTO> phases = phaseMongoRepository.getApplicablePlanningPhasesByUnit(orgId);
        return phases;
    }

    public List<PhaseDTO> getActualPhasesByOrganizationId(Long orgId) {
        List<PhaseDTO> phases = phaseMongoRepository.getActualPhasesByUnit(orgId);
        return phases;
    }

    public boolean deletePhase(Long countryId, BigInteger phaseId) {
        Phase phase = phaseMongoRepository.findOne(phaseId);
        if (!Optional.ofNullable(phase).isPresent()) {
            logger.info("Phase not found in country " + phaseId);
            exceptionService.dataNotFoundByIdException("message.country.phase.notfound", phaseId);
        }
        phase.setDeleted(true);
        save(phase);
        return true;
    }

    public Phase getPhaseCurrentByUnit(Long unitId, Date date) {

        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalseAndDurationGreaterThan(unitId, PhaseType.PLANNING.toString(), 0L);
        if (phases.isEmpty()) {
            logger.info("Phase not found in unit " + unitId);
            exceptionService.dataNotFoundByIdException("message.organization.phase.notfound", unitId);
        }
        return getCurrentPhaseInUnitByDate(phases, date);
    }

    public Phase getCurrentPhaseInUnitByDate(List<Phase> phases, Date date) {
        Phase phase = null;
        LocalDate currentDate = LocalDate.now();
        LocalDate proposedDate = DateUtils.getLocalDateFromDate(date);
        long weekDifference = currentDate.until(proposedDate, ChronoUnit.WEEKS);

        Collections.sort(phases, (Phase p1, Phase p2) -> {
            if (p1.getSequence() < p2.getSequence())
                return 1;
            else
                return -1;
        });
        if (weekDifference < 0) {
            Optional<Phase> phaseOptional = phases.stream().findFirst();
            phase = phaseOptional.get();
            return phase;
        }
        int weekCount = 1;
        outerLoop:
        for (Phase phaseObject : phases) {
            if (phaseObject.getDurationType().equals(DurationType.WEEKS) && phaseObject.getDuration() > 0) {    // Only considering Week based phases
                for (int i = 0; i < phaseObject.getDuration(); i++) {
                    //logger.info(phaseObject.getName());
                    if (weekDifference == weekCount) {
                        phase = phaseObject;
                        break outerLoop;
                    }
                    weekCount++;
                }
            }
        }

        if (!Optional.ofNullable(phase).isPresent()) {
            phase = phases.get(phases.size() - 1);
            return phase;
        }
        //logger.info(phase.getName());
        return phase;
    }

    public Phase updatePhases(Long countryId, BigInteger phaseId, PhaseDTO phaseDTO) {
        Phase phase = phaseMongoRepository.findOne(phaseId);
        if (!Optional.ofNullable(phase).isPresent()) {
            logger.info("Phase not found in country " + phaseId);
            exceptionService.dataNotFoundByIdException("message.country.phase.notfound", phaseId);

        }
        if (phase.getSequence() != phaseDTO.getSequence()) {
            long phaseInUse = phaseMongoRepository.findBySequenceAndCountryIdAndDeletedFalse(phaseDTO.getSequence(), countryId);
            if (phaseInUse > 0) {
                logger.info("Phase already exist by sequence in country" + phaseDTO.getCountryId());
                exceptionService.duplicateDataException("message.country.phase.sequence", phaseDTO.getCountryId());
            }
        }
        // Disable update of name
        /*phase.setName(phaseDTO.getName());
        phase.setSequence(phaseDTO.getSequence());*/

        if(phase.getPhaseType().equals(PhaseType.PLANNING)){
            phase.setDescription(phaseDTO.getDescription());
            phase.setDurationType(phaseDTO.getDurationType());
            phase.setDuration(phaseDTO.getDuration());
        }
        phase.setStatus(Phase.PhaseStatus.getListByValue(phaseDTO.getStatus()));
        save(phase);
        return phase;
    }

    private void preparePhase(Phase phase, PhaseDTO phaseDTO) {

        phase.setDuration(phaseDTO.getDuration());
        phase.setDurationType(phaseDTO.getDurationType());
        phase.setName(phase.getName());
        phase.setSequence(phase.getSequence());
        phase.setDescription(phaseDTO.getDescription());

    }

    public PhaseDTO updatePhase(BigInteger phaseId, Long unitId, PhaseDTO phaseDTO) {
        phaseDTO.setOrganizationId(unitId);
        OrganizationDTO organization = organizationRestClient.getOrganization(unitId);

        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id", unitId);
        }
        Phase oldPhase = phaseMongoRepository.findOne(phaseId);
        if (oldPhase == null) {
            exceptionService.dataNotFoundByIdException("message.phase.id.notfound", phaseDTO.getId());
        }
        Phase phase = phaseMongoRepository.findByNameAndDisabled(unitId, phaseDTO.getName(), false);
        if (phase != null && !oldPhase.getName().equals(phaseDTO.getName())) {
            exceptionService.actionNotPermittedException("message.phase.name.alreadyexists", phaseDTO.getName());
        }
        if(oldPhase.getPhaseType().equals(PhaseType.PLANNING)){
            preparePhase(oldPhase, phaseDTO);
            save(oldPhase);
        }
        return phaseDTO;
    }

    public List<String> getAllApplicablePhaseStatus(){
        return Stream.of(Phase.PhaseStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
    /*private ArrayList getDefaultPhases(long unitId) {
        ArrayList<Phase> phases = new ArrayList();
        return phases;
    }*/

    /*public void createDefaultPhases() {

        logger.info("<<<<<< createDefaultPhases executes >>>>>>>");
        List<Long> organizationIdList = organizationRestClient.getAllOrganizationIds();
        logger.info("Organization whose phases need to create " + organizationIdList.size());
        if (!organizationIdList.isEmpty()) {
            for (Long organizationId : organizationIdList) {
                createDefaultPhase(organizationId);
            }
            logger.info("<<<<<< createDefaultPhases completed >>>>>>>");
            organizationRestClient.updateOrganizationWithoutPhases(organizationIdList);
        } else {
            logger.info("<<<<<< All organization's have already Phases created >>>>>>>");
        }

    }*/

    /*public Phase createPhasesByUnitId(Long unitId, PhaseDTO phaseDTO) {

        if (phaseDTO.getDuration() <= 0) {
            throw new ActionNotPermittedException("Invalid Phase Duration : " + phaseDTO.getDuration());
        }

        OrganizationDTO unitOrganization = organizationRestClient.getOrganization(unitId);
        if (unitOrganization == null) {
            throw new DataNotFoundByIdException("Invalid unitId : " + unitId);
        }
        Phase phase = phaseMongoRepository.findByNameAndDisabled(unitId, phaseDTO.getName(), false);

        if (phase != null) {
            throw new ActionNotPermittedException("Phase with name : " + phaseDTO.getName() + " already exists.");
        }

        phase = preparePhase(phaseDTO, unitOrganization);
        save(phase);

        return phase;
    }*/

    // called once when  new country is registered
    /*public void createDefaultPhasesInCountry(Long countryId) {
//        boolean exists = countryRestClient.isCountryExists(countryId);
//        if (!exists) {
//            throw new DataNotFoundByIdException("Invalid unitId : " + countryId);
//        }
        ArrayList<Phase> phases = getDefaultPhasesForCountry(countryId);
        save(phases);
    }*/

    /*private ArrayList getDefaultPhasesForCountry(Long countryId) {
        Phase realTimePhase = new Phase(REALTIME_PHASE_NAME, REALTIME_PHASE_DESCRIPTION, 24, DurationType.HOURS, 1, countryId, false, null, null);
        Phase tentativePhase = new Phase(TENTATIVE_PHASE_NAME, TENTATIVE_PHASE_DESCRIPTION, 7, DurationType.DAYS, 2, countryId, false, null, null);
        Phase draftPhase = new Phase(DRAFT_PHASE_NAME, DRAFT_PHASE_DESCRIPTION, 4, DurationType.WEEKS, 3, countryId, false, null, null);
        Phase constructionPhase = new Phase(CONSTRUCTION_PHASE_NAME, CONSTRUCTION_PHASE_DESCRIPTION, 1, DurationType.WEEKS, 4, countryId, false, null, null);
        Phase puzzlePhase = new Phase(PUZZLE_PHASE_NAME, PUZZLE_PHASE_DESCRIPTION, 1, DurationType.WEEKS, 5, countryId, false, null, null);
        Phase requestPhase = new Phase(REQUEST_PHASE_NAME, REQUEST_PHASE_DESCRIPTION, 1, DurationType.WEEKS, 6, countryId, false, null, null);
        ArrayList<Phase> phases = new ArrayList();
        phases.add(realTimePhase);
        phases.add(tentativePhase);
        phases.add(draftPhase);
        phases.add(constructionPhase);
        phases.add(puzzlePhase);
        phases.add(requestPhase);
        return phases;
    }*/

    /*private Phase preparePhase(PhaseDTO phaseDTO, OrganizationDTO unitOrganization) {
        Phase phase = new Phase();
        phase.setName(phaseDTO.getName());
        phase.setDuration(phaseDTO.getDuration());
        phase.setSequence(phaseDTO.getSequence());
        phase.setDescription(phaseDTO.getDescription());
//        phase.setConstructionPhaseStartsAtDay(phaseDTO.getConstructionPhaseStartsAtDay());
//        phase.setActivityAccess(phaseDTO.getActivityAccess());
        phase.setOrganizationId(unitOrganization.getId());
        return phase;
    }*/

    public List<Phase> getAllPhasesOfUnit(Long unitId) {
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalseAndDurationGreaterThan(unitId, PhaseType.PLANNING.toString(), 0L);
        return phases;
    }

}