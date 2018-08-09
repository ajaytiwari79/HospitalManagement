package com.kairos.service.phase;

import com.kairos.enums.shift.ShiftStatus;
import com.kairos.rest_client.CountryRestClient;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.user.organization.OrganizationDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.DateUtils;
import com.kairos.enums.DurationType;
import com.kairos.enums.phase.PhaseType;
import com.kairos.activity.phase.PhaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.AppConstants.*;

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
            exceptionService.dataNotFoundByIdException("message.unit.id", unitId);
        }
        return phaseMongoRepository.getPlanningPhasesByUnit(unitId, Sort.Direction.DESC);
    }


    public List<PhaseDTO> getPhasesByUnit(Long unitId) {
        OrganizationDTO unitOrganization = organizationRestClient.getOrganizationWithoutAuth(unitId);
        if (unitOrganization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id", unitId);
        }
        return phaseMongoRepository.getPhasesByUnit(unitId, Sort.Direction.DESC);
    }

    public Map<String, List<PhaseDTO>> getCategorisedPhasesByUnit(Long unitId) {
        OrganizationDTO unitOrganization = organizationRestClient.getOrganizationWithoutAuth(unitId);
        if (unitOrganization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id", unitId);
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

    private Phase buildPhaseForCountry(PhaseDTO phaseDTO) {
        return new Phase(phaseDTO.getName(), phaseDTO.getDescription(), phaseDTO.getDuration(), phaseDTO.getDurationType(), phaseDTO.getSequence(),
                phaseDTO.getCountryId(), phaseDTO.getOrganizationId(), phaseDTO.getParentCountryPhaseId(), phaseDTO.getPhaseType(), phaseDTO.getStatus());
    }

    public List<PhaseDTO> getPhasesByCountryId(Long countryId) {
        return phaseMongoRepository.findByCountryIdAndDeletedFalse(countryId);
    }

    public Map<String, List<PhaseDTO>> getPhasesWithCategoryByCountryId(Long countryId) {
        List<PhaseDTO> phases = getPhasesByCountryId(countryId);
        Map<String, List<PhaseDTO>> phasesData = new HashMap<>(2);
        phasesData.put("planningPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.PLANNING)).collect(Collectors.toList()));
        phasesData.put("actualPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.ACTUAL)).collect(Collectors.toList()));
        return phasesData;
    }

    public List<PhaseDTO> getApplicablePlanningPhasesByOrganizationId(Long orgId, Sort.Direction direction) {
           return  phaseMongoRepository.getApplicablePlanningPhasesByUnit(orgId, direction);
        }

    public List<PhaseDTO> getActualPhasesByOrganizationId(Long orgId) {
        return phaseMongoRepository.getActualPhasesByUnit(orgId);
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
        LocalDate upcomingMondayDate = DateUtils.getDateForUpcomingDay(LocalDate.now(), DayOfWeek.MONDAY);
        LocalDate proposedDate = DateUtils.getLocalDateFromDate(date);

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        int startWeekNumber = upcomingMondayDate.get(weekFields.weekOfWeekBasedYear());
        int proposedWeekNumber = proposedDate.get(weekFields.weekOfWeekBasedYear());

        int weekDifference = proposedWeekNumber-startWeekNumber;

        weekDifference++; // 34-30  its 4 but actually we need 5 including currently
        Collections.sort(phases, (Phase p1, Phase p2) -> {
            if (p1.getSequence() < p2.getSequence())
                return 1;
            else
                return -1;
        });
        if (weekDifference <= 0) {
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
        }
        logger.info(phase.getName());
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

        if (phase.getPhaseType().equals(PhaseType.PLANNING)) {
            phase.setDescription(phaseDTO.getDescription());
            phase.setDurationType(phaseDTO.getDurationType());
            phase.setDuration(phaseDTO.getDuration());
        }
        phase.setStatus(ShiftStatus.getListByValue(phaseDTO.getStatus()));
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
        if (PhaseType.PLANNING.equals(oldPhase.getPhaseType())) {
            preparePhase(oldPhase, phaseDTO);
            save(oldPhase);
        }
        return phaseDTO;
    }

    public List<String> getAllApplicablePhaseStatus() {
        return Stream.of(ShiftStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public List<Phase> getAllPhasesOfUnit(Long unitId) {
        return phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalseAndDurationGreaterThan(unitId, PhaseType.PLANNING.toString(), 0L);
        }


    public Map<LocalDate,List<ShiftStatus>> getStatusByDates(Long unitId, Set<LocalDate> dates) {
        Map<LocalDate,List<ShiftStatus>> localDatePhaseStatusMap=new HashMap<>();
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<String,List<ShiftStatus>> phaseMap=phases.stream().collect(Collectors.toMap(Phase::getName, Phase::getStatus));
        LocalDate currentDate=LocalDate.now();
        List<Phase> planningPhases=phases.stream().filter(phase -> phase.getPhaseType().equals(PhaseType.PLANNING) && phase.getDuration()>0).collect(Collectors.toList());
        Collections.sort(planningPhases, (Phase p1, Phase p2) -> {
            if (p1.getSequence() < p2.getSequence())
                return 1;
            else
                return -1;
        });
        LocalDate upcomingMondayDate = DateUtils.getDateForUpcomingDay(LocalDate.now(), DayOfWeek.MONDAY);
        LocalDate previousMonday=DateUtils.getDateForPreviousDay(LocalDate.now(),DayOfWeek.MONDAY);
        for(LocalDate date:dates){
            if(date.isBefore(previousMonday)){
                localDatePhaseStatusMap.put(date,phaseMap.get(PAYROLL));
            } else if(date.isBefore(currentDate) && date.isAfter(previousMonday.minusDays(1))){
                localDatePhaseStatusMap.put(date,phaseMap.get(TIME_AND_ATTENDANCE));
            } else if((date).isEqual(currentDate)){
                localDatePhaseStatusMap.put(date,phaseMap.get(REALTIME));
            } else if((date).isBefore(upcomingMondayDate.plusDays(1)) && date.isAfter(currentDate)){
                localDatePhaseStatusMap.put(date,phaseMap.get(TENTATIVE));
            } else {
                //No Any Actual phase found so going to add Planning Phase
                addPlanningPhase(planningPhases,date,localDatePhaseStatusMap,upcomingMondayDate);
            }
        }
        return localDatePhaseStatusMap;
    }

    /**
     *
     * @param phases
     * @param proposedDate
     * @param localDatePhaseStatusMap
     * @param upcomingMondayDate
     */
    private void addPlanningPhase(List<Phase> phases, LocalDate proposedDate, Map<LocalDate,List<ShiftStatus>> localDatePhaseStatusMap, LocalDate upcomingMondayDate) {
        if(!Optional.ofNullable(phases).isPresent()){
            exceptionService.actionNotPermittedException("phases.absent");
        }
        Phase phase = null;
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        int startWeekNumber = upcomingMondayDate.get(weekFields.weekOfWeekBasedYear());
        int proposedWeekNumber = proposedDate.get(weekFields.weekOfWeekBasedYear());

        int weekDifference = proposedWeekNumber-startWeekNumber;

        weekDifference++; // 34-30  its 4 but actually we need 5 including currently
        if (weekDifference <= 0) {
            Optional<Phase> phaseOptional = phases.stream().findFirst();
            if(phaseOptional.isPresent()){
                phase = phaseOptional.get();

            }

        }
        int weekCount = 1;
        outerLoop:
        for (Phase phaseObject : phases) {
            if (DurationType.WEEKS .equals(phaseObject.getDurationType()) && phaseObject.getDuration() > 0) {    // Only considering Week based phases
                for (int i = 0; i < phaseObject.getDuration(); i++) {
                    if (weekDifference == weekCount) {
                        phase=phaseObject;
                        break outerLoop;
                    }
                    weekCount++;
                }
            }
        }
        if (!Optional.ofNullable(phase).isPresent()) {
            phase = phases.get(phases.size() - 1);
            }
        localDatePhaseStatusMap.put(proposedDate,phase.getStatus());
    }

}