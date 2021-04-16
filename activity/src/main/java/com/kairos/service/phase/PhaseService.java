package com.kairos.service.phase;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.phase.PhaseWeeklyDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.ShiftDataHelper;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.wrapper.phase.PhaseActivityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDateTime;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.phase.PhaseType.ACTUAL;

/**
 * Created by vipul on 25/9/17.
 */
@Service
@Transactional
public class PhaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhaseService.class);
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private ExceptionService exceptionService;
    @Inject private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject private ActivityConfigurationService activityConfigurationService;


    @CacheEvict(value = "getPhasesByUnit", key = "#unitId")
    public List<Phase> createDefaultPhase(Long unitId, Long countryId) {
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        if(isCollectionEmpty(phases)) {
            List<PhaseDTO> countryPhases = phaseMongoRepository.findByCountryIdAndDeletedFalseOrderByPhaseTypeDescSequenceAsc(countryId);
            phases = new ArrayList<>();
            for (PhaseDTO phaseDTO : countryPhases) {
                Phase phase = new Phase(phaseDTO.getName(), phaseDTO.getDescription(), phaseDTO.getPhaseEnum(), phaseDTO.getDuration(), phaseDTO.getDurationType(), phaseDTO.getSequence(), null,
                        unitId, phaseDTO.getId(), phaseDTO.getPhaseType(), phaseDTO.getStatus(), phaseDTO.getColor(), phaseDTO.getFlippingDefaultTime(), phaseDTO.getGracePeriodByStaff(), phaseDTO.getGracePeriodByManagement(), phaseDTO.getUntilNextDay(), phaseDTO.getRealtimeDuration());

                phases.add(phase);
            }
            if (!phases.isEmpty()) {
                phaseMongoRepository.saveEntities(phases);
            }
        }
        return phases;
    }

    /*
     *@Author vipul
     */
    public List<PhaseDTO> getPlanningPhasesByUnit(Long unitId) {
        return phaseMongoRepository.getPlanningPhasesByUnit(unitId, Sort.Direction.DESC);
    }


    @Cacheable(value = "getPhasesByUnit", key = "#unitId", cacheManager = "cacheManager")
    public List<PhaseDTO> getPhasesByUnit(Long unitId) {
        return phaseMongoRepository.getPhasesByUnit(unitId, Sort.Direction.DESC);
    }

    public Map<String, List<PhaseDTO>> getCategorisedPhasesByUnit(Long unitId) {
        List<PhaseDTO> phases = getPhasesByUnit(unitId);
        Map<String, List<PhaseDTO>> phasesData = new HashMap<>(2);
        phasesData.put("planningPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(PhaseType.PLANNING)).collect(Collectors.toList()));
        phasesData.put("actualPhases", phases.stream().filter(phaseDTO -> phaseDTO.getPhaseType().equals(ACTUAL)).collect(Collectors.toList()));
        return phasesData;
    }

    @CacheEvict(value = "getPhasesByUnit", allEntries = true)
    public boolean removePhase(BigInteger phaseId) {
        Phase phase = phaseMongoRepository.findOne(phaseId);
        if (phase == null) {
            return false;
        }
        phase.setDeleted(true);
        phaseMongoRepository.save(phase);

        return true;
    }


    public Phase createPhaseInCountry(Long countryId, PhaseDTO phaseDTO) {
        long phaseExists = phaseMongoRepository.findBySequenceAndCountryIdAndDeletedFalse(phaseDTO.getSequence(), countryId);
        if (phaseExists > 0) {
            LOGGER.info("Phase already exist by sequence in country {}" , phaseDTO.getCountryId());
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_PHASE_SEQUENCE, phaseDTO.getCountryId());
        }
        Phase phase = buildPhaseForCountry(phaseDTO);
        phase.setCountryId(countryId);
        phaseMongoRepository.save(phase);
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

    public List<PhaseDTO> getApplicablePlanningPhasesByUnitIds(List<Long> orgIds, Sort.Direction direction) {
        return  phaseMongoRepository.getApplicablePlanningPhasesByUnitIds(orgIds, direction);
    }

    public List<PhaseDTO> getActualPhasesByOrganizationId(Long orgId) {
        return phaseMongoRepository.getActualPhasesByUnit(orgId);
        }

    @CacheEvict(value = "getPhasesByUnit", allEntries = true)
    public boolean deletePhase(Long countryId, BigInteger phaseId) {
        Phase phase = phaseMongoRepository.findOne(phaseId);
        if (!Optional.ofNullable(phase).isPresent()) {
            LOGGER.info("Phase not found in country phase id is {}" , phaseId);
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_PHASE_NOTFOUND, phaseId);
        }
        phase.setDeleted(true);
        phaseMongoRepository.save(phase);
        return true;
    }


    public Phase updatePhases(Long countryId, BigInteger phaseId, PhaseDTO phaseDTO) {
        Phase phase = phaseMongoRepository.findOne(phaseId);
        if (!Optional.ofNullable(phase).isPresent()) {
            LOGGER.info("Phase not found in country phase id {}" , phaseId);
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_PHASE_NOTFOUND, phaseId);

        }
        if (phase.getSequence() != phaseDTO.getSequence()) {
            long phaseInUse = phaseMongoRepository.findBySequenceAndCountryIdAndDeletedFalse(phaseDTO.getSequence(), countryId);
            if (phaseInUse > 0) {
                LOGGER.info("Phase already exist by sequence in country {}" , phaseDTO.getCountryId());
                exceptionService.duplicateDataException(MESSAGE_COUNTRY_PHASE_SEQUENCE, phaseDTO.getCountryId());
            }
        }
        if (PhaseType.PLANNING.equals(phase.getPhaseType())) {
            preparePlanningPhase(phase, phaseDTO);
        }
        if(ACTUAL.equals(phase.getPhaseType())){
            prepareActualPhase(phase,phaseDTO);
        }
        phase.setStatus(ShiftStatus.getListByValue(phaseDTO.getStatus()));
        phaseMongoRepository.save(phase);
        return phase;
    }

    private void preparePlanningPhase(Phase phase, PhaseDTO phaseDTO) {
        phase.setDuration(phaseDTO.getDuration());
        phase.setDurationType(phaseDTO.getDurationType());
        phase.setDescription(phaseDTO.getDescription());
        phase.setColor(phaseDTO.getColor());
        phase.setFlippingDefaultTime(phaseDTO.getFlippingDefaultTime());
        phase.setShortName(phaseDTO.getShortName());
        phase.setAccessGroupIds(phaseDTO.getAccessGroupIds());

    }

   private void prepareActualPhase(Phase phase,PhaseDTO phaseDTO){
       phase.setColor(phaseDTO.getColor());
       phase.setShortName(phaseDTO.getShortName());
       phase.setAccessGroupIds(phaseDTO.getAccessGroupIds());
       if(PhaseDefaultName.REALTIME.equals(phaseDTO.getPhaseEnum())) {
           phase.setRealtimeDuration(phaseDTO.getRealtimeDuration());
       }else if(PhaseDefaultName.TENTATIVE.equals(phaseDTO.getPhaseEnum())) {
           phase.setUntilNextDay(phaseDTO.getUntilNextDay());
       }else {
           phase.setGracePeriodByManagement(phaseDTO.getGracePeriodByManagement());
           phase.setGracePeriodByStaff(phaseDTO.getGracePeriodByStaff());
       }
   }


    @CacheEvict(value = "getPhasesByUnit", key = "#unitId")
    public PhaseDTO updatePhase(BigInteger phaseId, Long unitId, PhaseDTO phaseDTO) {
        phaseDTO.setOrganizationId(unitId);
        Phase oldPhase = phaseMongoRepository.findOne(phaseId);
        if (isNull(oldPhase)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PHASE_ID_NOTFOUND, phaseDTO.getId());
        }
        Phase phase = phaseMongoRepository.findByUnitIdAndName(unitId, phaseDTO.getName());
        if (phase != null && !oldPhase.getName().equals(phaseDTO.getName())) {
            exceptionService.actionNotPermittedException(MESSAGE_PHASE_NAME_ALREADYEXISTS, phaseDTO.getName());
        }
        if (PhaseType.PLANNING.equals(oldPhase.getPhaseType())) {
            preparePlanningPhase(oldPhase, phaseDTO);
        }
        if(ACTUAL.equals(oldPhase.getPhaseType())){
            prepareActualPhase(oldPhase,phaseDTO);
        }
        phaseMongoRepository.save(oldPhase);
        return phaseDTO;
    }

    public Set<ShiftStatus> getAllApplicablePhaseStatus() {
        return ShiftStatus.getAllStatusExceptRequestAndPending();
    }

    /**
     * @Auther Pavan
     * @param unitId
     * @param startDate
     * @return phase
     */

    public Phase getCurrentPhaseByUnitIdAndDate(Long unitId, Date startDate,Date endDate){
        ZoneId timeZone= ZoneId.of(userIntegrationService.getTimeZoneByUnitId(unitId));
        Phase tentativePhase = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId,PhaseDefaultName.TENTATIVE.toString());
        LocalDateTime untilTentativeDate = DateUtils.getDateForUpcomingDay(DateUtils.getLocalDateFromTimezone(timeZone),tentativePhase.getUntilNextDay()==null?DayOfWeek.MONDAY:tentativePhase.getUntilNextDay()).atStartOfDay().minusSeconds(1);
        LocalDateTime startDateTime=DateUtils.asLocalDateTime(startDate);
        LocalDateTime endDateTime=Optional.ofNullable(endDate).isPresent()? DateUtils.asLocalDateTime(endDate):null;
        Phase phase;
        if(startDateTime.isAfter(untilTentativeDate)){
            phase= planningPeriodMongoRepository.getCurrentPhaseByDateUsingPlanningPeriod(unitId,DateUtils.asLocalDate(startDate));
        }
        else {
            List<Phase> actualPhases = phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalse(unitId, ACTUAL.toString());
            Map<String, Phase> phaseMap = actualPhases.stream().collect(Collectors.toMap(k->k.getPhaseEnum().toString(), Function.identity()));
            phase= getActualPhaseApplicableForDate(startDateTime,phaseMap,untilTentativeDate,timeZone);
        }
        if (isNull(phase)) {
            exceptionService.dataNotFoundException(MESSAGE_PHASESETTINGS_ABSENT);
        }
        return phase;
    }

    public Phase getCurrentPhaseByUnitIdAndDate(Date startDate, ShiftDataHelper shiftDataHelper){
        ZoneId timeZone= ZoneId.of(shiftDataHelper.getTimeZone());
        Phase tentativePhase = shiftDataHelper.getPhases().stream().filter(phase -> PhaseDefaultName.TENTATIVE.equals(phase.getPhaseEnum())).findFirst().get();
        LocalDateTime untilTentativeDate = DateUtils.getDateForUpcomingDay(DateUtils.getLocalDateFromTimezone(timeZone),tentativePhase.getUntilNextDay()==null?DayOfWeek.MONDAY:tentativePhase.getUntilNextDay()).atStartOfDay().minusSeconds(1);
        LocalDateTime startDateTime=DateUtils.asLocalDateTime(startDate);
        Phase phase;
        if(startDateTime.isAfter(untilTentativeDate)){
            phase= shiftDataHelper.getPhases().stream().filter(phase1 -> phase1.getId().equals(shiftDataHelper.getPlanningPeriod().getCurrentPhaseId())).findFirst().get();
        }
        else {
            List<Phase> actualPhases = shiftDataHelper.getPhases().stream().filter(phase1 -> phase1.getPhaseType().equals(ACTUAL)).collect(Collectors.toList());
            Map<String, Phase> phaseMap = actualPhases.stream().collect(Collectors.toMap(k->k.getPhaseEnum().toString(), Function.identity()));
            phase= getActualPhaseApplicableForDate(startDateTime,phaseMap,untilTentativeDate,timeZone);
        }
        if (isNull(phase)) {
            exceptionService.dataNotFoundException(MESSAGE_PHASESETTINGS_ABSENT);
        }
        return phase;
    }



    /**
     * @Auther Pavan
     * @param unitId
     * @param dates
     * @return
     */
    public Map<Date,Phase> getPhasesByDates(Long unitId, Set<LocalDateTime> dates) {
        ZoneId timeZone = ZoneId.of(userIntegrationService.getTimeZoneByUnitId(unitId));
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Set<LocalDate> localDates = dates.stream().map(localDateTime -> localDateTime.toLocalDate()).collect(Collectors.toSet());
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllPeriodsByUnitIdAndDates(unitId,localDates);
        Map<Date,Phase> localDatePhaseStatusMap=new HashMap<>();
        Map[] phaseDetailsMap=getPhaseMap(phases);
        Map<BigInteger,Phase> phaseAndIdMap=(Map<BigInteger,Phase>)phaseDetailsMap[0];
        Map<String,Phase> phaseMap = (Map<String,Phase>)phaseDetailsMap[1];
        DayOfWeek tentativeDayOfWeek = phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay() == null ? DayOfWeek.MONDAY : phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay();
        LocalDateTime untilTentative = DateUtils.getDateForUpcomingDay(DateUtils.getLocalDateFromTimezone(timeZone),tentativeDayOfWeek).atStartOfDay().minusSeconds(1);
        if (isCollectionNotEmpty(dates)) {
            for (LocalDateTime requestedDate : dates) {
                Phase phase = null;
                if (requestedDate.isAfter(untilTentative)) {
                    Optional<PlanningPeriod> planningPeriodOptional = planningPeriods.stream().filter(planningPeriod -> planningPeriod.contains(requestedDate.toLocalDate())).findAny();
                    if (planningPeriodOptional.isPresent()) {
                        phase = phaseAndIdMap.get(planningPeriodOptional.get().getCurrentPhaseId());
                    }
                } else {
                    phase = getActualPhaseApplicableForDate(requestedDate, phaseMap, untilTentative, timeZone);
                }
                if (isNull(phase)) {
                    exceptionService.dataNotFoundException(MESSAGE_ORGANIZATION_PHASES_ON_DATE, unitId, requestedDate);
                }
                localDatePhaseStatusMap.put(asDate(requestedDate), phase);
            }
        }
        return localDatePhaseStatusMap;
    }

    public Map[] getPhasesByDates(Long unitId, LocalDate startDate,LocalDate endDate,ZoneId timeZone,Long employementTypeId) {
        timeZone = isNull(timeZone) ? ZoneId.of(userIntegrationService.getTimeZoneByUnitId(unitId)) : timeZone;
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        List<PlanningPeriodDTO> planningPeriods = planningPeriodMongoRepository.findAllPlanningPeriodBetweenDatesAndUnitId(unitId,asDate(startDate),asDate(endDate));
        Map<Date,Phase> localDatePhaseStatusMap=new HashMap<>();
        Map[] phaseDetailsMap=getPhaseMap(phases);
        Map<LocalDate,Boolean> publishEmployementType = new HashMap<>();
        Map<BigInteger,Phase> phaseAndIdMap=(Map<BigInteger,Phase>)phaseDetailsMap[0];
        Map<String,Phase> phaseMap = (Map<String,Phase>)phaseDetailsMap[1];
        DayOfWeek tentativeDayOfWeek = phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay() == null ? DayOfWeek.MONDAY : phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay();
        LocalDateTime untilTentative = DateUtils.getDateForUpcomingDay(DateUtils.getLocalDateFromTimezone(timeZone),tentativeDayOfWeek).atStartOfDay().minusSeconds(1);
        while (!startDate.isAfter(endDate)){
            Phase phase = null;
            LocalDateTime requestedDate = asLocalDateTime(startDate);
            Optional<PlanningPeriodDTO> planningPeriodOptional = planningPeriods.stream().filter(planningPeriod -> planningPeriod.contains(requestedDate.toLocalDate())).findAny();
            if (requestedDate.isAfter(untilTentative)) {
                if (planningPeriodOptional.isPresent()) {
                    phase = phaseAndIdMap.get(planningPeriodOptional.get().getCurrentPhaseId());
                }
            } else {
                phase = getActualPhaseApplicableForDate(requestedDate, phaseMap, untilTentative, timeZone);
            }
            if (isNull(phase) || !planningPeriodOptional.isPresent()) {
                exceptionService.dataNotFoundException(MESSAGE_ORGANIZATION_PHASES_ON_DATE, unitId, requestedDate);
            }
            publishEmployementType.put(startDate,planningPeriodOptional.get().getPublishEmploymentIds().contains(employementTypeId));
            localDatePhaseStatusMap.put(asDate(requestedDate), phase);
            startDate = startDate.plusDays(1);
        }
        return new Map[]{localDatePhaseStatusMap,publishEmployementType};
    }

    //Please Use this method For Future dates
    public Map<LocalDate,Phase> getPhasesByDates(Set<LocalDate> dates,ShiftDataHelper shiftDataHelper) {
        ZoneId timeZone=ZoneId.of(shiftDataHelper.getTimeZone());
        List<Phase> phases = shiftDataHelper.getPhases();
        List<PlanningPeriod> planningPeriods = shiftDataHelper.getPlanningPeriods();
        Map<LocalDate,Phase> localDatePhaseStatusMap=new HashMap<>();
        Map[] phaseDetailsMap=getPhaseMap(phases);
        Map<BigInteger,Phase> phaseAndIdMap=(Map<BigInteger,Phase>)phaseDetailsMap[0];
        Map<String,Phase> phaseMap = (Map<String,Phase>)phaseDetailsMap[1];
        DayOfWeek tentativeDayOfWeek = phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay() == null ? DayOfWeek.MONDAY : phaseMap.get(PhaseDefaultName.TENTATIVE.toString()).getUntilNextDay();
        LocalDateTime untilTentative = DateUtils.getDateForUpcomingDay(DateUtils.getLocalDateFromTimezone(timeZone),tentativeDayOfWeek).atStartOfDay().minusSeconds(1);
        if (isCollectionNotEmpty(dates)) {
            for (LocalDate requestedDate : dates) {
                Phase phase = null;
                LocalDateTime localDateTime = asLocalDateTime(requestedDate);
                if (localDateTime.isAfter(untilTentative)) {
                    if(isNotNull(shiftDataHelper)){
                        phase = phaseAndIdMap.get(shiftDataHelper.getDatePhaseIdMap().get(requestedDate));
                    }else {
                        Optional<PlanningPeriod> planningPeriodOptional = planningPeriods.stream().filter(planningPeriod -> planningPeriod.contains(requestedDate)).findAny();
                        if (planningPeriodOptional.isPresent()) {
                            phase = phaseAndIdMap.get(planningPeriodOptional.get().getCurrentPhaseId());
                        }
                    }
                } else {
                    phase = getActualPhaseApplicableForDate(localDateTime, phaseMap, untilTentative, timeZone);
                }
                if (isNull(phase)) {
                    exceptionService.dataNotFoundException(MESSAGE_ORGANIZATION_PHASES_ON_DATE, UserContext.getUserDetails().getLastSelectedOrganizationId(), requestedDate);
                }
                localDatePhaseStatusMap.put(requestedDate, phase);
            }
        }
        return localDatePhaseStatusMap;
    }

    private Map[] getPhaseMap(List<Phase> phases){
        Map<BigInteger,Phase> phaseMap = new HashMap<>();
        Map<String,Phase> phaseEnumMap = new HashMap<>();
        for (Phase phase : phases) {
            phaseMap.put(phase.getId(),phase);
            phaseEnumMap.put(phase.getPhaseEnum().toString(),phase);
        }
        return new Map[]{phaseMap,phaseEnumMap};
    }


    private Phase getActualPhaseApplicableForDate(LocalDateTime startDateTime, Map<String,Phase> phaseMap, LocalDateTime untilTentativeDate,ZoneId timeZone){
        Phase phase=null;
        int minutesToCalculate=phaseMap.get(PhaseDefaultName.REALTIME.toString()).getRealtimeDuration();
        LocalDateTime realTimeStartDate=DateUtils.getLocalDateTimeFromZoneId(timeZone).minusMinutes(minutesToCalculate+1);
        if (startDateTime.isBefore(realTimeStartDate)) {
            return phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString());
        }
        LocalDateTime realTimeEndDate=DateUtils.getLocalDateTimeFromZoneId(timeZone).plusMinutes(minutesToCalculate+1);
         if(new DateTimeInterval(asDate(realTimeStartDate),asDate(realTimeEndDate)).contains(asDate(startDateTime))){
            return phaseMap.get(PhaseDefaultName.REALTIME.toString());
        }else if ((startDateTime).isBefore(untilTentativeDate) && startDateTime.isAfter(realTimeEndDate)) {
            return phaseMap.get(PhaseDefaultName.TENTATIVE.toString());
        }
        return phase;
    }

    public boolean shiftEditableInRealtime(String timeZone, Map<String,Phase> phaseMap, Date startDate, Date endDate){
        int realtimeDuration = phaseMap.get(PhaseDefaultName.REALTIME.toString()).getRealtimeDuration();
        LocalDateTime realtimePhaseStartDate = DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone)).minusMinutes(realtimeDuration + 1);
        LocalDateTime realtimePhaseEndDate = DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone)).plusMinutes(realtimeDuration + 1);
        DateTimeInterval shiftInterval = new DateTimeInterval(startDate,endDate);
        DateTimeInterval realtimeInterval = new DateTimeInterval(asDate(realtimePhaseStartDate), asDate(realtimePhaseEndDate));
        return shiftInterval.overlaps(realtimeInterval);
    }
    /**
     * @author mohit
     * @date 8-10-2018
     * @param unitId
     * @return
     */
    public List<PhaseDTO> getDefaultPhasesByUnit(Long unitId) {
        return phaseMongoRepository.findByOrganizationIdAndDeletedFalseOrderByPhaseTypeDescSequenceAsc(unitId);
    }

    public Phase getPhaseByName(final Long unitId,final String name){
        return phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId,name);
    }
    @CacheEvict(value = "getPhasesByUnit", allEntries = true)
    public Map<String, TranslationInfo>  updateTranslations(BigInteger phaseId,Map<String, TranslationInfo> translations){
        Phase phase = phaseMongoRepository.findOne(phaseId);
        phase.setTranslations(translations);
        phaseMongoRepository.save(phase);
        return phase.getTranslations();
    }

    public PhaseActivityDTO getApplicablePhases(Long unitId){
        List<PhaseDTO> phaseDTOs = getApplicablePlanningPhasesByOrganizationId(unitId, Sort.Direction.DESC);
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int currentWeek = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        List<PhaseWeeklyDTO> phaseWeeklyDTOS = getPhaseWeeklyDTO(phaseDTOs,currentWeek,year);
        // Creating dummy next remaining 2 years as PHASE with lowest sequence
        createDummyPhase(year, currentWeek, phaseDTOs, phaseWeeklyDTOS);
        List<PhaseDTO> actualPhase = getActualPhasesByOrganizationId(unitId);
        return PhaseActivityDTO.builder().actualPhases(actualPhase).phases(phaseWeeklyDTOS).applicablePhases(phaseDTOs).build();
    }

    private List<PhaseWeeklyDTO> getPhaseWeeklyDTO(List<PhaseDTO> phaseDTOs,int currentWeek,int year) {
        ArrayList<PhaseWeeklyDTO> phaseWeeklyDTOS = new ArrayList<>();
        for (PhaseDTO phaseObj : phaseDTOs) {
            if (phaseObj.getDurationType().equals(DurationType.WEEKS)) {
                for (int i = 0; i < phaseObj.getDuration(); i++) {
                    PhaseWeeklyDTO tempPhaseObj = phaseObj.buildWeekDTO();
                    tempPhaseObj.setWeekCount(++currentWeek);
                    tempPhaseObj.setYear(year);
                    if (currentWeek >= 52) {
                        year = year + 1;
                        currentWeek = 0;
                    }
                    phaseWeeklyDTOS.add(tempPhaseObj);
                }
            }
        }
        return phaseWeeklyDTOS;
    }

    private void createDummyPhase(int year, int currentWeek, List<PhaseDTO> phaseDTOs, List<PhaseWeeklyDTO> phaseWeeklyDTOS) {
        if (isCollectionNotEmpty(phaseDTOs)) {
            int indexOfPhaseWithLowestSeq = phaseDTOs.size() - 1;
            for (int start = phaseWeeklyDTOS.size(); start <= 104; start++) {
                PhaseWeeklyDTO tempPhaseObj = phaseDTOs.get(indexOfPhaseWithLowestSeq).buildWeekDTO();
                tempPhaseObj.setWeekCount(++currentWeek);
                tempPhaseObj.setYear(year);
                if (currentWeek >= 52) {
                    year = year + 1;
                    currentWeek = 0;
                }
                phaseWeeklyDTOS.add(tempPhaseObj);
            }
        }
    }


}