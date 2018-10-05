package com.kairos.service.phase;

import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.rest_client.CountryRestClient;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.enums.phase.PhaseType;
import com.kairos.dto.activity.phase.PhaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.phase.PhaseType.ACTUAL;

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
    @Inject private PlanningPeriodMongoRepository planningPeriodMongoRepository;


    public List<Phase> createDefaultPhase(Long unitId, Long countryId) {
        List<PhaseDTO> countryPhases = phaseMongoRepository.findByCountryIdAndDeletedFalseOrderByPhaseTypeDescSequenceAsc(countryId);
        List<Phase> phases = new ArrayList<>();
        for (PhaseDTO phaseDTO : countryPhases) {
            Phase phase = new Phase(phaseDTO.getName(), phaseDTO.getDescription(),phaseDTO.getPhaseEnum(), phaseDTO.getDuration(), phaseDTO.getDurationType(), phaseDTO.getSequence(), null,
                    unitId, phaseDTO.getId(), phaseDTO.getPhaseType(), phaseDTO.getStatus(),phaseDTO.getColor(),phaseDTO.getFlippingDefaultTime());

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
        phasesData.put("actualPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(ACTUAL)).collect(Collectors.toList()));
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
        return new Phase(phaseDTO.getName(), phaseDTO.getDescription(),phaseDTO.getPhaseEnum(),phaseDTO.getDuration(), phaseDTO.getDurationType(), phaseDTO.getSequence(),
                phaseDTO.getCountryId(), phaseDTO.getOrganizationId(), phaseDTO.getParentCountryPhaseId(), phaseDTO.getPhaseType(), phaseDTO.getStatus(),phaseDTO.getColor(),phaseDTO.getFlippingDefaultTime());
    }

    public List<PhaseDTO> getPhasesByCountryId(Long countryId) {
        return phaseMongoRepository.findByCountryIdAndDeletedFalseOrderByPhaseTypeDescSequenceAsc(countryId);
    }

    public Map<String, List<PhaseDTO>> getPhasesWithCategoryByCountryId(Long countryId) {
        List<PhaseDTO> phases = getPhasesByCountryId(countryId);
        Map<String, List<PhaseDTO>> phasesData = new HashMap<>(2);
        phasesData.put("planningPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.PLANNING)).collect(Collectors.toList()));
        phasesData.put("actualPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(ACTUAL)).collect(Collectors.toList()));
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

        if (PhaseType.PLANNING.equals(phase.getPhaseType())) {
            preparePlanningPhase(phase, phaseDTO);
        }
        if(ACTUAL.equals(phase.getPhaseType())){
            prepareActualPhase(phase,phaseDTO);
        }
        phase.setStatus(ShiftStatus.getListByValue(phaseDTO.getStatus()));
        save(phase);
        return phase;
    }

    private void preparePlanningPhase(Phase phase, PhaseDTO phaseDTO) {
        phase.setDuration(phaseDTO.getDuration());
        phase.setDurationType(phaseDTO.getDurationType());
        phase.setDescription(phaseDTO.getDescription());
        phase.setColor(phaseDTO.getColor());
        phase.setFlippingDefaultTime(phaseDTO.getFlippingDefaultTime());
        phase.setShortName(phaseDTO.getShortName());

    }

   private void prepareActualPhase(Phase phase,PhaseDTO phaseDTO){
       phase.setColor(phaseDTO.getColor());
       phase.setShortName(phaseDTO.getShortName());
       if(PhaseDefaultName.REALTIME.equals(phaseDTO.getPhaseEnum())) {
           phase.setRealtimeDuration(phaseDTO.getRealtimeDuration());
       }else if(PhaseDefaultName.TENTATIVE.equals(phaseDTO.getPhaseEnum())) {
           phase.setUntilNextDay(phaseDTO.getUntilNextDay());
       }else {
           phase.setGracePeriodByManagement(phaseDTO.getGracePeriodByManagement());
           phase.setGracePeriodByStaff(phaseDTO.getGracePeriodByStaff());
       }
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
        Phase phase = phaseMongoRepository.findByUnitIdAndName(unitId, phaseDTO.getName());
        if (phase != null && !oldPhase.getName().equals(phaseDTO.getName())) {
            exceptionService.actionNotPermittedException("message.phase.name.alreadyexists", phaseDTO.getName());
        }
        if (PhaseType.PLANNING.equals(oldPhase.getPhaseType())) {
            preparePlanningPhase(oldPhase, phaseDTO);
        }
        if(ACTUAL.equals(oldPhase.getPhaseType())){
            prepareActualPhase(oldPhase,phaseDTO);
        }
        save(oldPhase);
        return phaseDTO;
    }

    public List<String> getAllApplicablePhaseStatus() {
        return Stream.of(ShiftStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * @Auther Pavan
     * @param unitId
     * @param date
     * @return phase
     */
    public Phase getCurrentPhaseByUnitIdAndDate(Long unitId, Date date){
        Phase tentativePhase = phaseMongoRepository.findByUnitIdAndName(unitId,TENTATIVE);
        LocalDateTime untilTentativeDate = DateUtils.getDateForUpcomingDay(LocalDate.now(),tentativePhase.getUntilNextDay()).atStartOfDay().minusSeconds(1);
        LocalDateTime requestedDate=DateUtils.asLocalDateTime(date);
        Phase phase;
        if(requestedDate.isAfter(untilTentativeDate)){
            phase= planningPeriodMongoRepository.getCurrentPhaseByDateUsingPlanningPeriod(unitId,DateUtils.asLocalDate(date));
        }
        else {
            List<Phase> actualPhases = phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalse(unitId, ACTUAL.toString());
            LocalDateTime previousMonday=DateUtils.getDateForPreviousDay(LocalDate.now(),DayOfWeek.MONDAY).atStartOfDay();
            Map<String, Phase> phaseMap = actualPhases.stream().collect(Collectors.toMap(Phase::getName, Function.identity()));
            phase= getActualPhaseApplicableForDate(requestedDate,previousMonday,phaseMap,untilTentativeDate);
        }
        return phase;
    }

    /**
     * @Auther Pavan
     * @param unitId
     * @param dates
     * @return
     */
    public Map<LocalDate,Phase> getPhasesByDates(Long unitId, Set<LocalDateTime> dates) {
        Map<LocalDate,Phase> localDatePhaseStatusMap=new HashMap<>();
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<String,Phase> phaseMap=phases.stream().collect(Collectors.toMap(Phase::getName, v->v));
        Map<BigInteger,Phase> phaseAndIdMap=phases.stream().collect(Collectors.toMap(Phase::getId, v->v));
        LocalDateTime untilTentative = DateUtils.getDateForUpcomingDay(LocalDate.now(), phaseMap.get(TENTATIVE).getUntilNextDay()).atStartOfDay().minusSeconds(1);
        LocalDateTime previousMonday=DateUtils.getDateForPreviousDay(LocalDate.now(),DayOfWeek.MONDAY).atStartOfDay();
        Set<LocalDate> localDates=new HashSet<>();
        dates.forEach(d->{localDates.add(d.toLocalDate());});
        List<PlanningPeriod> planningPeriods=planningPeriodMongoRepository.findAllPeriodsByUnitIdAndDates(unitId,localDates);

        for(LocalDateTime requestedDate:dates){
            Phase phase;
            if(requestedDate.isAfter(untilTentative)){
               PlanningPeriod planningPeriod= planningPeriods.stream().filter(startDateFilter->startDateFilter.getStartDate().minusDays(1).atStartOfDay().isBefore(requestedDate)).
                       filter(endDateFilter->endDateFilter.getEndDate().plusDays(1).atStartOfDay().isAfter(requestedDate)).findAny().orElse(null);
               phase=phaseAndIdMap.get(planningPeriod.getCurrentPhaseId());
            }
            else {
               phase= getActualPhaseApplicableForDate(requestedDate,previousMonday,phaseMap,untilTentative);
            }
            localDatePhaseStatusMap.put(requestedDate.toLocalDate(),phase);
        }
        return localDatePhaseStatusMap;
    }

//    /**
//     *
//     * @param requestedDate
//     * @param previousMonday
//     * @param phaseMap
//     * @param currentDate
//     * @param upcomingMondayDate
//     * @return
//     */
//    private Phase getActualPhaseApplicableForDate(LocalDate requestedDate, LocalDate previousMonday, Map<String,Phase> phaseMap, LocalDate currentDate, LocalDate upcomingMondayDate){
//        Phase phase=null;
//        if (requestedDate.isBefore(previousMonday)) {
//            phase= phaseMap.get(PAYROLL);
//        } else if (requestedDate.isBefore(currentDate) && requestedDate.isAfter(previousMonday.minusDays(1))) {
//            phase= phaseMap.get(TIME_AND_ATTENDANCE);
//        } else if ((currentDate).isEqual(requestedDate)) {
//            phase= phaseMap.get(REALTIME);
//        } else if ((requestedDate).isBefore(upcomingMondayDate.plusDays(1)) && requestedDate.isAfter(currentDate)) {
//            phase=phaseMap.get(TENTATIVE);
//        }
//        return phase;
//    }

    private Phase getActualPhaseApplicableForDate(LocalDateTime requestedDateTime, LocalDateTime previousMondayLocalDateTime, Map<String,Phase> phaseMap, LocalDateTime untilTentativeDate){
        Phase phase=null;
        int minutesToCalculate=phaseMap.get(REALTIME).getRealtimeDuration();
        LocalDateTime localDateTimeAfterMinus=LocalDateTime.now().minusMinutes(minutesToCalculate+1);
        LocalDateTime localDateTimeAfterPlus=LocalDateTime.now().plusMinutes(minutesToCalculate+1);
        if (requestedDateTime.isBefore(previousMondayLocalDateTime)) {
            phase= phaseMap.get(PAYROLL);
        } else if (requestedDateTime.isBefore(localDateTimeAfterMinus) && requestedDateTime.isAfter(previousMondayLocalDateTime.plusDays(1))) {
            phase= phaseMap.get(TIME_AND_ATTENDANCE);
        } else if(requestedDateTime.isAfter(localDateTimeAfterMinus) && requestedDateTime.isBefore(localDateTimeAfterPlus)){
            phase= phaseMap.get(REALTIME);
        } else if ((requestedDateTime).isBefore(untilTentativeDate.plusDays(1)) && requestedDateTime.isAfter(localDateTimeAfterPlus)) {
            phase=phaseMap.get(TENTATIVE);
        }
        return phase;
    }

}