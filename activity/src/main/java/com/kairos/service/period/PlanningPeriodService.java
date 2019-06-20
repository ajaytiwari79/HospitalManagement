package com.kairos.service.period;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.period.FlippingDateDTO;
import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.period.PeriodPhaseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.scheduler.scheduler_panel.LocalDateTimeScheduledPanelIdDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.scheduler.JobFrequencyType;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.period.PeriodPhaseFlippingDate;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevelState;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelStateMongoRepository;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftStateService;
import com.kairos.service.time_bank.TimeBankService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.*;

/**
 * Created by prerna on 6/4/18.
 */
@Service
@Transactional
public class PlanningPeriodService extends MongoBaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanningPeriodService.class);

    @Inject
    private PhaseService phaseService;

    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private SchedulerServiceRestClient schedulerRestClient;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private StaffingLevelStateMongoRepository staffingLevelStateMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftStateService shiftStateService;
    @Inject
    private TimeBankService timeBankService;

    // To get list of phases with duration in days
    public Map<Long, List<PhaseDTO>> getPhasesWithDurationInDays(List<Long> unitIds) {
        List<PhaseDTO> phases = phaseService.getApplicablePlanningPhasesByUnitIds(unitIds, Sort.Direction.DESC);
        phases.forEach(phase -> {
            if (DurationType.DAYS.equals(phase.getDurationType())) {
                phase.setDurationInDays(phase.getDuration());
                phase.setDurationType(DurationType.DAYS);
            } else if (DurationType.WEEKS.equals(phase.getDurationType())) {
                phase.setDurationInDays(phase.getDuration() * 7);
                phase.setDurationType(DurationType.DAYS);
            } else {
                phase.setDurationInDays(phase.getDuration());
                phase.setDurationType(DurationType.HOURS);
            }
        });
        return phases.stream().collect(Collectors.groupingBy(PhaseDTO::getOrganizationId));
    }

    // Prepare map for phases with id as key and sequence as value
    public Map<BigInteger, Integer> getMapOfPhasesIdAndSequence(List<PhaseDTO> phases) {
        Map<BigInteger, Integer> phaseIdAndSequenceMap = new HashMap<>();
        for (PhaseDTO phase : phases) {
            phaseIdAndSequenceMap.put(phase.getId(), phase.getSequence());
        }
        return phaseIdAndSequenceMap;
    }

    // To fetch list of planning periods
    public List<PlanningPeriodDTO> getPlanningPeriods(Long unitId, LocalDate startDate, LocalDate endDate) {
        List<PhaseDTO> phases = phaseService.getPlanningPhasesByUnit(unitId);

        // Prepare map for phases with id as key and sequence as value
        Map<BigInteger, Integer> phaseIdAndSequenceMap = getMapOfPhasesIdAndSequence(phases);

        // Fetch planning periods
        List<PlanningPeriodDTO> planningPeriods = null;
        if (Optional.ofNullable(startDate).isPresent() || Optional.ofNullable(endDate).isPresent()) {
            planningPeriods = planningPeriodMongoRepository.findPeriodsOfUnitByStartAndEndDate(unitId, startDate, endDate);
        } else {
            planningPeriods = planningPeriodMongoRepository.findAllPeriodsOfUnit(unitId);
        }

        for (PlanningPeriodDTO planningPeriod : planningPeriods) {

            // Set duration of period
            planningPeriod.setPeriodDuration(DateUtils.getDurationOfTwoLocalDates(planningPeriod.getStartDate(), planningPeriod.getEndDate().plusDays(1)));

            // Set flipping dates
            FlippingDateDTO flippingDateDTO;
            for (PeriodPhaseDTO flippingDateTime : planningPeriod.getPhaseFlippingDate()) {
                int phaseSequence = phaseIdAndSequenceMap.get(flippingDateTime.getPhaseId());
                switch (phaseSequence) {
                    case 4: {
                        flippingDateDTO = setFlippingDateAndTime(flippingDateTime);
                        planningPeriod.setConstructionToDraftDate(flippingDateDTO);
                        break;
                    }
                    case 3: {
                        flippingDateDTO = setFlippingDateAndTime(flippingDateTime);
                        planningPeriod.setPuzzleToConstructionDate(flippingDateDTO);
                        break;
                    }
                    case 2: {
                        flippingDateDTO = setFlippingDateAndTime(flippingDateTime);
                        planningPeriod.setRequestToPuzzleDate(flippingDateDTO);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        return planningPeriods;
    }

    public FlippingDateDTO setFlippingDateAndTime(PeriodPhaseDTO flippingDateTime) {
        return (Optional.ofNullable(flippingDateTime.getFlippingDate()).isPresent()) ? new FlippingDateDTO(flippingDateTime.getFlippingDate(), flippingDateTime.getFlippingTime().getHour(), flippingDateTime.getFlippingTime().getMinute(), flippingDateTime.getFlippingTime().getSecond()) : null;
    }


    /// API END Point
    public List<PlanningPeriodDTO> migratePlanningPeriods(Long unitId, PlanningPeriodDTO planningPeriodDTO) {
        List<PlanningPeriod> requestPlanningPeriods = planningPeriodMongoRepository.findAllPeriodsOfUnitByRequestPhaseId(unitId, AppConstants.REQUEST_PHASE_NAME);
        if (requestPlanningPeriods.isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIOD_REQUEST_PHASE_NOTFOUND);
        }
        Map<Long, List<PhaseDTO>> unitIdAndPhasesMap = getPhasesWithDurationInDays(Arrays.asList(unitId));
        if (!unitIdAndPhasesMap.containsKey(unitId)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_PHASES, unitId);
        }
        LocalDate startDate = requestPlanningPeriods.get(0).getStartDate();
        LocalDate endDate = requestPlanningPeriods.get(requestPlanningPeriods.size() - 1).getEndDate();
        if (requestPlanningPeriods.size() > 0) {
            createMigratedPlanningPeriodForTimeDuration(startDate, endDate, unitId, planningPeriodDTO, unitIdAndPhasesMap.get(unitId));
            for (PlanningPeriod planningPeriod : requestPlanningPeriods) {
                planningPeriod.setActive(false);
            }
            planningPeriodMongoRepository.saveEntities(requestPlanningPeriods);
        }
        return getPlanningPeriods(unitId, null, null);
    }

    public void createMigratedPlanningPeriodForTimeDuration(LocalDate oldStartDate, LocalDate oldEndDate, Long unitId, PlanningPeriodDTO planningPeriodDTO, List<PhaseDTO> phases) {
        List<PlanningPeriod> planningPeriods = new ArrayList<>();
        List<LocalDate> startDateList = getListOfStartDateInWeekOrMonths(oldStartDate, oldEndDate, planningPeriodDTO);
        Set<LocalDate> existingPlanningPeriods = new HashSet<>();
        for (LocalDate startDate : startDateList) {
            boolean alreadyExist = false;
            LocalDate endDate = getNextValidDateForPlanningPeriod(startDate, planningPeriodDTO);
            alreadyExist = existingPlanningPeriods.stream().filter(startDate::isBefore).findAny().isPresent();
            if (!alreadyExist) {
                if (endDate.isAfter(oldEndDate) || endDate.isEqual(oldEndDate)) {
                    endDate = oldEndDate;
                }
                existingPlanningPeriods.add(endDate);
                planningPeriods.add(createPlanningPeriodOnMigration(startDate, endDate, unitId, phases));
            }
        }
        if (planningPeriods.isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIOD_REQUEST_PHASE_NOTFOUND);
        }
        planningPeriodMongoRepository.saveEntities(planningPeriods);
    }

    public PlanningPeriod createPlanningPeriodOnMigration(LocalDate startDate, LocalDate endDate, Long unitId, List<PhaseDTO> applicablePhases) {
        String name = DateUtils.formatLocalDate(startDate, AppConstants.DATE_FORMET_STRING) + "  " + DateUtils.formatLocalDate(endDate, AppConstants.DATE_FORMET_STRING);
        PlanningPeriod planningPeriod = new PlanningPeriod(name, startDate, endDate, unitId);
        planningPeriod = setPhaseFlippingDatesForPlanningPeriod(startDate, applicablePhases, planningPeriod);
        return planningPeriod;
    }

    public PlanningPeriod setPhaseFlippingDatesForPlanningPeriod(LocalDate startDate, List<PhaseDTO> applicablePhases, PlanningPeriod planningPeriod) {

        BigInteger currentPhaseId = null;
        BigInteger nextPhaseId = null;
        List<PeriodPhaseFlippingDate> tempPhaseFlippingDate = new ArrayList<>();
        if (Optional.ofNullable(applicablePhases).isPresent()) {

            LocalDateTime tempFlippingDate = startDate.atStartOfDay();
            boolean scopeToFlipNextPhase = true;
            BigInteger previousPhaseId = null;
            int index = 0;


            for (PhaseDTO phase : applicablePhases) {
                // Check if duration of period is enough to assign next flipping
                if (DurationType.DAYS.equals(phase.getDurationType())) {
                    tempFlippingDate = DateUtils.addDurationInLocalDateTime(LocalDateTime.of(tempFlippingDate.toLocalDate(), phase.getFlippingDefaultTime()), -phase.getDurationInDays(), DurationType.DAYS, 1);
                } else {
                    tempFlippingDate = DateUtils.addDurationInLocalDateTime(tempFlippingDate, -phase.getDurationInDays(), DurationType.HOURS, 1);
                }
                // DateUtils.getDate().compareTo(tempFlippingDate) >= 0
                if (applicablePhases.size() == index + 1 || (scopeToFlipNextPhase && DateUtils.asLocalDateTime(DateUtils.getDate()).isAfter(tempFlippingDate))) {
                    if (scopeToFlipNextPhase) {
                        currentPhaseId = phase.getId();
                        nextPhaseId = previousPhaseId;
                    }
                    scopeToFlipNextPhase = false;
                }
                previousPhaseId = phase.getId();
                // Calculate flipping date by duration
                PeriodPhaseFlippingDate periodPhaseFlippingDate = new PeriodPhaseFlippingDate(phase.getId(), scopeToFlipNextPhase ? tempFlippingDate.toLocalDate() : null, scopeToFlipNextPhase ? tempFlippingDate.toLocalTime() : null);
                tempPhaseFlippingDate.add(periodPhaseFlippingDate);
                index += 1;
            }
        }
        planningPeriod.setCurrentPhaseId(currentPhaseId);
        planningPeriod.setNextPhaseId(nextPhaseId);
        planningPeriod.setPhaseFlippingDate(tempPhaseFlippingDate);
        return planningPeriod;
    }

    // To create Planning Period object and to save the list
    public void createPlanningPeriod(Long unitId, LocalDate startDate, List<PlanningPeriod> planningPeriods, List<PhaseDTO> applicablePhases, PlanningPeriodDTO planningPeriodDTO, int recurringNumber) {
        LocalDate endDate = getNextValidDateForPlanningPeriod(startDate, planningPeriodDTO);
        // Set name of period dynamically
        String name = DateUtils.formatLocalDate(startDate, AppConstants.DATE_FORMET_STRING) + "  " + DateUtils.formatLocalDate(endDate, AppConstants.DATE_FORMET_STRING);
        PlanningPeriod planningPeriod = new PlanningPeriod(name, startDate, endDate, unitId, planningPeriodDTO.getDurationType(), planningPeriodDTO.getDuration());
        planningPeriod = setPhaseFlippingDatesForPlanningPeriod(startDate, applicablePhases, planningPeriod);
        // Add planning period object in list

            planningPeriods.add(planningPeriod);

        if (recurringNumber > 1) {
            createPlanningPeriod(unitId, endDate.plusDays(1),
                    planningPeriods, applicablePhases, planningPeriodDTO, --recurringNumber);
        }
    }

    public List<LocalDate> getListOfStartDateInWeekOrMonths(LocalDate startDate, LocalDate endDate, PlanningPeriodDTO planningPeriodDTO) {
        List<LocalDate> startDateList = new ArrayList<>();
        if (planningPeriodDTO.getDurationType().equals(DurationType.WEEKS)) {
            if (!startDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                startDateList.add(startDate);
            }
            while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
                LocalDate startDateOfMonday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
                startDateList.add((startDateOfMonday.isBefore(endDate) ? startDateOfMonday : startDate));
                startDate = startDate.plusWeeks(planningPeriodDTO.getDuration());
            }
        } else {
            while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
                LocalDate startDateOfMonth = ((startDate.getDayOfMonth() != 1) ? startDate : startDate.withDayOfMonth(1));
                startDateList.add(startDateOfMonth);
                startDate = startDate.withDayOfMonth(1).plusMonths(1);
            }
        }
        return startDateList;
    }

    public LocalDate getNextValidDateForPlanningPeriod(LocalDate startDate, PlanningPeriodDTO planningPeriodDTO) {
        LocalDate endDate;
        if (validateStartDateForPeriodCreation(startDate, planningPeriodDTO.getDurationType())) {
            endDate = DateUtils.addDurationInLocalDateExcludingLastDate(startDate, planningPeriodDTO.getDuration(),
                    planningPeriodDTO.getDurationType(), 1);
        } else {
            if (planningPeriodDTO.getDurationType().equals(DurationType.MONTHS)) {
                endDate = startDate.with(TemporalAdjusters.firstDayOfNextMonth()).minusDays(1);
            } else {
                endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }
        }
        return endDate;
    }

    public boolean validateStartDateForPeriodCreation(LocalDate startDate, DurationType durationType) {
        if (durationType.equals(DurationType.WEEKS)) {
            return startDate.getDayOfWeek().equals(DayOfWeek.MONDAY);
        } else {
            return startDate.equals(startDate.withDayOfMonth(1));
        }
    }


    public List<PlanningPeriodDTO> addPlanningPeriods(Long unitId, PlanningPeriodDTO planningPeriodDTO) {
        Map<Long, List<PhaseDTO>> unitIdAndPhasesMap = getPhasesWithDurationInDays(Arrays.asList(unitId));

        if (!unitIdAndPhasesMap.containsKey(unitId)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_PHASES, unitId);
        }

        // period can't be created in past
        if (DateUtils.getLocalDateFromDate(DateUtils.getDate()).isAfter(planningPeriodDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(ERROR_PERIOD_PAST_DATE_CREATION);
        }


        List<PlanningPeriod> planningPeriods = new ArrayList<>(planningPeriodDTO.getRecurringNumber());
        PlanningPeriod lastEndDate = planningPeriodMongoRepository.findLastPlaningPeriodEndDate(unitId);
        if (Optional.ofNullable(lastEndDate).isPresent()) {
            planningPeriodDTO.setStartDate(lastEndDate.getEndDate().plusDays(1));
        } else {
            if (!validateStartDateForPeriodCreation(planningPeriodDTO.getStartDate(), planningPeriodDTO.getDurationType())) {
                exceptionService.actionNotPermittedException(ERROR_PERIOD_START_DATE_INVALID);
            }
        }

        createPlanningPeriod(unitId, planningPeriodDTO.getStartDate(), planningPeriods, unitIdAndPhasesMap.get(unitId), planningPeriodDTO, planningPeriodDTO.getRecurringNumber());
        planningPeriodMongoRepository.saveEntities(planningPeriods);
        createScheduleJobOfPanningPeriod(planningPeriods);
        return getPlanningPeriods(unitId, planningPeriodDTO.getStartDate(), (planningPeriodDTO.getEndDate() != null) ? planningPeriodDTO.getEndDate() : null);
    }

    private void createScheduleJobOfPanningPeriod(List<PlanningPeriod> planningPeriods) {
        Map<Long, String> unitAndTimeZoneMap = userIntegrationService.getTimeZoneByUnitIds(planningPeriods.stream().distinct().map(PlanningPeriod::getUnitId).collect(Collectors.toSet()));
        List<SchedulerPanelDTO> schedulerPanelDTOS = new ArrayList<>();
        planningPeriods.parallelStream().forEach(planningPeriod -> planningPeriod.getPhaseFlippingDate().parallelStream().forEach(periodPhaseFlippingDate -> {
            if (periodPhaseFlippingDate.getFlippingDate() != null && periodPhaseFlippingDate.getFlippingTime() != null)
                schedulerPanelDTOS.add(new SchedulerPanelDTO(planningPeriod.getUnitId(), JobType.FUNCTIONAL, JobSubType.FLIP_PHASE, true, LocalDateTime.of(periodPhaseFlippingDate.getFlippingDate(), periodPhaseFlippingDate.getFlippingTime()), planningPeriod.getId(), unitAndTimeZoneMap.get(planningPeriod.getUnitId())));
        }));
        if (!schedulerPanelDTOS.isEmpty()) {
            List<SchedulerPanelDTO> schedulerPanelRestDTOS = new ArrayList<>();
            try {
                LOGGER.info("send rest call for create job of planning period flippng date of unit");
                schedulerPanelRestDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, -1l, true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
                });
                LOGGER.info("successfully created job of planning period flippng date");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isCollectionNotEmpty(schedulerPanelRestDTOS)) {
                Map<String, SchedulerPanelDTO> schedulerPanelDTOMap = schedulerPanelRestDTOS.stream().collect(Collectors.toMap(schedulerPanelDTO -> schedulerPanelDTO.getEntityId() + "-" + schedulerPanelDTO.getOneTimeTriggerDate(), schedulerPanelDTO -> schedulerPanelDTO));
                planningPeriods.stream().forEach(planningPeriod -> {
                    try {
                        planningPeriod.getPhaseFlippingDate().stream().forEach(periodPhaseFlippingDate -> {
                            if (periodPhaseFlippingDate.getFlippingDate() != null && periodPhaseFlippingDate.getFlippingTime() != null) {
                                SchedulerPanelDTO schedulerPanelDTO = schedulerPanelDTOMap.get(planningPeriod.getId() + "-" + LocalDateTime.of(periodPhaseFlippingDate.getFlippingDate(), periodPhaseFlippingDate.getFlippingTime()));
                                periodPhaseFlippingDate.setSchedulerPanelId(schedulerPanelDTO.getId());
                            }
                        });
                    } catch (Exception e) {
                        LOGGER.info("error in set schedulerPanel job id in planning period via job in " + planningPeriod.getUnitId());
                        e.printStackTrace();
                    }
                });
                planningPeriodMongoRepository.saveEntities(planningPeriods);
            }
        }
    }


    public PlanningPeriod updatePhaseFlippingDateOfPeriod(PlanningPeriod planningPeriod, PlanningPeriodDTO planningPeriodDTO, Long unitId) {
        List<PeriodPhaseFlippingDate> phaseFlippingDateList = planningPeriod.getPhaseFlippingDate();
        List<PhaseDTO> phases = phaseService.getPlanningPhasesByUnit(unitId);
        Map<BigInteger, Integer> phasesMap = getMapOfPhasesIdAndSequence(phases);

        for (PeriodPhaseFlippingDate phaseFlippingDate : phaseFlippingDateList) {
            switch (phasesMap.get(phaseFlippingDate.getPhaseId())) {
                case 4: {
                    if (phaseFlippingDate.getFlippingDate() != null && phaseFlippingDate.getFlippingTime() != null && !isPastDate(LocalDateTime.of(phaseFlippingDate.getFlippingDate(), phaseFlippingDate.getFlippingTime()))) {
                        phaseFlippingDate.setFlippingDate(planningPeriodDTO.getConstructionToDraftDate().getDate());
                        phaseFlippingDate.setFlippingTime(LocalTime.of(planningPeriodDTO.getConstructionToDraftDate().getHours(), planningPeriodDTO.getConstructionToDraftDate().getMinutes()));
                        updateSchedularFlippingDateById(phaseFlippingDate.getSchedulerPanelId(), unitId, phaseFlippingDate.getFlippingDate(), phaseFlippingDate.getFlippingTime());
                    }
                    break;
                }
                case 3: {
                    if (phaseFlippingDate.getFlippingDate() != null && phaseFlippingDate.getFlippingTime() != null && !isPastDate(LocalDateTime.of(phaseFlippingDate.getFlippingDate(), phaseFlippingDate.getFlippingTime()))) {
                        phaseFlippingDate.setFlippingDate(planningPeriodDTO.getPuzzleToConstructionDate().getDate());
                        phaseFlippingDate.setFlippingTime(LocalTime.of(planningPeriodDTO.getPuzzleToConstructionDate().getHours(), planningPeriodDTO.getPuzzleToConstructionDate().getMinutes()));
                        updateSchedularFlippingDateById(phaseFlippingDate.getSchedulerPanelId(), unitId, phaseFlippingDate.getFlippingDate(), phaseFlippingDate.getFlippingTime());
                    }
                    break;
                }
                case 2: {
                    if (phaseFlippingDate.getFlippingDate() != null && phaseFlippingDate.getFlippingTime() != null && !isPastDate(LocalDateTime.of(phaseFlippingDate.getFlippingDate(), phaseFlippingDate.getFlippingTime()))) {
                        phaseFlippingDate.setFlippingDate(planningPeriodDTO.getRequestToPuzzleDate().getDate());
                        phaseFlippingDate.setFlippingTime(LocalTime.of(planningPeriodDTO.getRequestToPuzzleDate().getHours(), planningPeriodDTO.getRequestToPuzzleDate().getMinutes()));
                        updateSchedularFlippingDateById(phaseFlippingDate.getSchedulerPanelId(), unitId, phaseFlippingDate.getFlippingDate(), phaseFlippingDate.getFlippingTime());
                    }
                    break;
                }
                default:
                    break;
            }

        }
        return planningPeriod;
    }

    public void updateSchedularFlippingDateById(BigInteger schedulerPanelId, Long unitId, LocalDate localDate, LocalTime localTime) {
        LocalDateTimeScheduledPanelIdDTO localDateTimeScheduledPanelIdDTO = new LocalDateTimeScheduledPanelIdDTO(schedulerPanelId, LocalDateTime.of(localDate, localTime));
        if (Optional.ofNullable(schedulerPanelId).isPresent())
            schedulerRestClient.publishRequest(Arrays.asList(localDateTimeScheduledPanelIdDTO), unitId, true, IntegrationOperation.UPDATE, "/scheduler_panel/update_date_only", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<LocalDateTimeScheduledPanelIdDTO>>>() {
            }, null, null);
    }

    public boolean isPastDate(LocalDateTime localDateTime) {
        return (localDateTime.isBefore(LocalDateTime.now()) || localDateTime.isEqual(LocalDateTime.now()));
    }

    public List<PlanningPeriodDTO> updatePlanningPeriod(Long unitId, BigInteger periodId, PlanningPeriodDTO planningPeriodDTO) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(periodId);

        if (!Optional.ofNullable(planningPeriod).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PERIOD_ORGANIZATION_NOTFOUND, periodId);
        }
        if (!planningPeriodDTO.getStartDate().isEqual(planningPeriod.getStartDate()) &&
                !planningPeriodDTO.getEndDate().isEqual(planningPeriod.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIOD_STARTDATE_ENDDATE_NOTUPDATE);
        }
        LocalDateTime puzzleFlippingDateTime = (Optional.ofNullable(planningPeriodDTO.getRequestToPuzzleDate()).isPresent()) ? getLocalDateTime(planningPeriodDTO.getRequestToPuzzleDate().getDate(),
                planningPeriodDTO.getRequestToPuzzleDate().getHours(), planningPeriodDTO.getRequestToPuzzleDate().getMinutes(), planningPeriodDTO.getRequestToPuzzleDate().getSeconds()) : null;
        LocalDateTime constructionFlippingDate = (Optional.ofNullable(planningPeriodDTO.getPuzzleToConstructionDate()).isPresent()) ? getLocalDateTime(planningPeriodDTO.getPuzzleToConstructionDate().getDate(),
                planningPeriodDTO.getPuzzleToConstructionDate().getHours(), planningPeriodDTO.getPuzzleToConstructionDate().getMinutes(), planningPeriodDTO.getPuzzleToConstructionDate().getSeconds()) : null;
        LocalDateTime draftFlippingDate = (Optional.ofNullable(planningPeriodDTO.getConstructionToDraftDate()).isPresent()) ? getLocalDateTime(planningPeriodDTO.getConstructionToDraftDate().getDate(), planningPeriodDTO.getConstructionToDraftDate().getHours(),
                planningPeriodDTO.getConstructionToDraftDate().getMinutes(), planningPeriodDTO.getConstructionToDraftDate().getSeconds()) : null;
        boolean valid = !((puzzleFlippingDateTime == null || (puzzleFlippingDateTime != null && constructionFlippingDate != null && constructionFlippingDate.isAfter(puzzleFlippingDateTime))) && (constructionFlippingDate == null || (constructionFlippingDate != null && draftFlippingDate != null && draftFlippingDate.isAfter(constructionFlippingDate))));
        if (valid) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIOD_INVALID_FLIPPINGDATE);
        }
        planningPeriod = updatePhaseFlippingDateOfPeriod(planningPeriod, planningPeriodDTO, unitId);
        planningPeriod.setName(planningPeriodDTO.getName());
        save(planningPeriod);
        return getPlanningPeriods(unitId, planningPeriod.getStartDate(), planningPeriod.getEndDate());
    }

    // To delete planning period

    public boolean deletePlanningPeriod(Long unitId, BigInteger periodId) {

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if (!Optional.ofNullable(planningPeriod).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PERIOD_UNIT_ID, periodId);
        }

        // Check if it is last period
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);

        if (!lastPlanningPeriod.getId().equals(planningPeriod.getId())) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIOD_DELETE_LAST);
        }

        // Check if period is in request phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        // TO DO check phase by sequence
        if (!phaseMongoRepository.checkPhaseByPhaseIdAndPhaseEnum(planningPeriod.getCurrentPhaseId(), PhaseDefaultName.REQUEST)) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIOD_PHASE_REQUEST_NAME, planningPeriod.getName());
        }
        List<BigInteger> schedulerPanelIds = planningPeriod.getPhaseFlippingDate().stream().filter(periodPhaseFlippingDate -> periodPhaseFlippingDate.getSchedulerPanelId() != null).map(PeriodPhaseFlippingDate::getSchedulerPanelId).collect(Collectors.toList());
        schedulerRestClient.publishRequest(schedulerPanelIds, unitId, true, IntegrationOperation.DELETE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, null, null);
        planningPeriod.setDeleted(true);
        save(planningPeriod);
        return true;
    }

    public PlanningPeriodDTO setPlanningPeriodPhaseToNext(Long unitId, BigInteger periodId,List<Long> employmentTypeIds) {

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if (!Optional.ofNullable(planningPeriod).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PERIOD_UNIT_ID, periodId);
        }
        if (!Optional.ofNullable(planningPeriod.getNextPhaseId()).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_PERIOD_PHASE_LAST);
        }
        BigInteger oldPlanningPeriodPhaseId = planningPeriod.getCurrentPhaseId();
        Phase initialNextPhase = phaseMongoRepository.findOne(planningPeriod.getNextPhaseId());
        List<PhaseDTO> toBeNextPhase = phaseMongoRepository.getNextApplicablePhasesOfUnitBySequence(unitId, initialNextPhase.getSequence());
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByPlanningPeriod(periodId, unitId);
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()));
        planningPeriod.setCurrentPhaseId(initialNextPhase.getId());
        planningPeriod.setNextPhaseId(Optional.ofNullable(toBeNextPhase).isPresent() && toBeNextPhase.size() > 0 ? toBeNextPhase.get(0).getId() : null);
        PeriodPhaseFlippingDate periodPhaseFlippingDate = planningPeriod.getPhaseFlippingDate().stream().filter(periodPhaseFlippingDates -> periodPhaseFlippingDates.getPhaseId().equals(planningPeriod.getCurrentPhaseId())).findFirst().get();
        List<BigInteger> schedulerPanelIds = planningPeriod.getPhaseFlippingDate().stream().filter(periodPhaseFlippingDates -> periodPhaseFlippingDates.getPhaseId().equals(initialNextPhase.getId())).map(periodPhaseFlippingDates -> periodPhaseFlippingDate.getSchedulerPanelId()).collect(Collectors.toList());
        periodPhaseFlippingDate.setSchedulerPanelId(null);
        periodPhaseFlippingDate.setFlippingDate(DateUtils.getCurrentLocalDate());
        periodPhaseFlippingDate.setFlippingTime(DateUtils.getCurrentLocalTime());
        //TODO Work

        Map<Long, Map<Long, Set<LocalDate>>> employmentWithShiftDateFunctionIdMap = getEmploymentIdWithFunctionIdShiftDateMap(shifts);
        if (PhaseDefaultName.DRAFT.equals(initialNextPhase.getPhaseEnum())) {
            if(isCollectionEmpty(employmentTypeIds)){
                exceptionService.invalidRequestException(MESSAGE_EMPLOYMENTTYPE_NOTFOUND);
            }
            publishShiftsAfterFlippingPhaseConstructionToDraft(planningPeriod, unitId, employmentTypeIds);
        }
        createShiftState(shifts, oldPlanningPeriodPhaseId, employmentWithShiftDateFunctionIdMap);
        createStaffingLevelState(staffingLevels, oldPlanningPeriodPhaseId, planningPeriod.getId());
        save(planningPeriod);
//        schedulerRestClient.publishRequest(schedulerPanelIds, unitId, true, IntegrationOperation.DELETE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
//        }, null, null);
        return getPlanningPeriods(unitId, planningPeriod.getStartDate(), planningPeriod.getEndDate()).get(0);
    }

    //TODO test
    Map<Long, Map<Long, Set<LocalDate>>> getEmploymentIdWithFunctionIdShiftDateMap(List<Shift> shifts) {
        Map<Long, Map<Long, Set<LocalDate>>> employmentIdWithFunctionIdShiftDateMap = new HashMap<>();
        if (!shifts.isEmpty()) {
            Set<Long> employmentIds = new HashSet<>();
            for (Shift shift : shifts) {
                Long employmentId = shift.getEmploymentId();
                if (employmentId != null) {
                    employmentIds.add(employmentId);
                }
            }
        }
        return employmentIdWithFunctionIdShiftDateMap;
    }

    public List<PeriodDTO> getPeriodOfInterval(Long unitId, LocalDate startDate, LocalDate endDate) {
        return planningPeriodMongoRepository.findAllPeriodsByStartDateAndLastDate(unitId, startDate, endDate);
    }

    // flip phase of planning period via job
    public void updateFlippingDate(BigInteger periodId, Long unitId, BigInteger schedulerPanelId) {
        List<Shift> shifts = new ArrayList<>();
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);
        BigInteger oldPlanningPeriodPhaseId = planningPeriod.getCurrentPhaseId();
        boolean updateCurrentAndNextPhases = false;
        BigInteger nextPhaseId = null;
        for (PeriodPhaseFlippingDate phaseFlippingDate : planningPeriod.getPhaseFlippingDate()) {
            if (phaseFlippingDate.getSchedulerPanelId().equals(schedulerPanelId)) {
                shifts = shiftMongoRepository.findAllShiftsByPlanningPeriod(planningPeriod.getId(), unitId);
                planningPeriod.setCurrentPhaseId(phaseFlippingDate.getPhaseId());
                updateCurrentAndNextPhases = true;
                break;
            }
            nextPhaseId = phaseFlippingDate.getPhaseId();
        }
        if (updateCurrentAndNextPhases) {
            PeriodPhaseFlippingDate periodPhaseFlippingDate = planningPeriod.getPhaseFlippingDate().stream().filter(periodPhaseFlippingDates -> periodPhaseFlippingDates.getPhaseId().equals(planningPeriod.getCurrentPhaseId())).findFirst().get();
            periodPhaseFlippingDate.setSchedulerPanelId(null);
            planningPeriod.setNextPhaseId(nextPhaseId);
            planningPeriodMongoRepository.save(planningPeriod);
        }
        Map<Long, Map<Long, Set<LocalDate>>> employmentWithShiftDateFunctionIdMap = getEmploymentIdWithFunctionIdShiftDateMap(shifts);
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()));
        createShiftState(shifts, oldPlanningPeriodPhaseId, employmentWithShiftDateFunctionIdMap);
        createStaffingLevelState(staffingLevels, oldPlanningPeriodPhaseId, planningPeriod.getId());
    }


    public void createShiftState(List<Shift> shifts, BigInteger currentPhaseId, Map<Long, Map<Long, Set<LocalDate>>> employmentWithShiftDateFunctionIdMap) {
        if (!shifts.isEmpty()) {
            List<ShiftState> shiftStates = new ArrayList<>();
            shifts.stream().forEach(shift -> {
                ShiftState shiftState = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftState.class);
                shiftState.setShiftId(shift.getId());
                shiftState.setShiftStatePhaseId(currentPhaseId);
                shiftState.setId(null);
                Long employmentId = shift.getEmploymentId();
                if (!employmentWithShiftDateFunctionIdMap.isEmpty() && employmentWithShiftDateFunctionIdMap.containsKey(employmentId)) {
                    Map<Long, Set<LocalDate>> functionIdWithDates = employmentWithShiftDateFunctionIdMap.get(employmentId);
                    for (Long functionId : functionIdWithDates.keySet()) {
                        Set<LocalDate> datesByFunctionId = functionIdWithDates.get(functionId);
                        //TODO change date format
                        if (datesByFunctionId.contains(DateUtils.asLocalDate(shift.getStartDate()))) {
                            shiftState.setFunctionId(functionId);
                        }
                    }
                }
                shiftStates.add(shiftState);
            });
            save(shiftStates);
        }
    }

    public void createStaffingLevelState(List<StaffingLevel> staffingLevels, BigInteger currentPhaseId, BigInteger planningPeriodId) {
        if (!staffingLevels.isEmpty()) {
            List<StaffingLevelState> staffingLevelStates = new ArrayList<>();
            staffingLevels.stream().forEach(staffingLevel -> {
                StaffingLevelState staffingLevelState = ObjectMapperUtils.copyPropertiesByMapper(staffingLevel, StaffingLevelState.class);
                staffingLevelState.setStaffingLevelId(staffingLevel.getId());
                staffingLevelState.setStaffingLevelStatePhaseId(currentPhaseId);
                staffingLevelState.setPlanningPeriodId(planningPeriodId);
                staffingLevelState.setId(null);
                staffingLevelStates.add(staffingLevelState);
            });
            staffingLevelStateMongoRepository.saveEntities(staffingLevelStates);
        }
    }

    /**
     * for restore shift initial data
     */
    public boolean restoreShiftToPreviousPhase(BigInteger planningPeriodId, Long unitId) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(planningPeriodId, unitId);
        if (!Optional.ofNullable(planningPeriod).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_PERIODSETTING_NOTFOUND);
        }
        BigInteger planningPeriodPhaseId = getPlanningPeriodPreviousPhaseId(planningPeriod, unitId);
        if (isNotNull(planningPeriodPhaseId)) {
            List<ShiftState> shiftStates = shiftStateMongoRepository.getShiftsState(planningPeriodId, planningPeriodPhaseId, unitId);
            List<Shift> shiftList = shiftMongoRepository.findAllShiftsByPlanningPeriod(planningPeriod.getId(), unitId);
            List<StaffingLevelState> staffingLevelStates = staffingLevelStateMongoRepository.getStaffingLevelState(planningPeriodId, planningPeriodPhaseId, unitId);
            List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()));
            List<Shift> currentPhaseShifts = shiftMongoRepository.findAllShiftsByCurrentPhaseAndPlanningPeriod(planningPeriod.getId(), planningPeriod.getCurrentPhaseId());
            restoreFunctions(shiftStates, unitId, currentPhaseShifts);
            restoreAvailabilityCount(staffingLevels, staffingLevelStates);
            restoreShifts(shiftStates, shiftList, unitId, planningPeriod);
        }
        return true;
    }

    //get previous phase id of planning phases if request then return null
    private BigInteger getPlanningPeriodPreviousPhaseId(PlanningPeriod planningPeriod, Long unitId) {
        BigInteger planningPeriodPhaseId = null;
        List<Phase> phases = phaseMongoRepository.getPlanningPhasesByUnit(unitId);
        Phase planningPeriodphase = phases.stream().filter(phase -> phase.getId().equals(planningPeriod.getCurrentPhaseId())).findFirst().get();
        Map<PhaseDefaultName, BigInteger> phaseEnumAndIdMap = phases.stream().collect(Collectors.toMap(Phase::getPhaseEnum, MongoBaseEntity::getId));
        if (planningPeriodphase.getPhaseEnum().equals(PhaseDefaultName.DRAFT)) {
            planningPeriodPhaseId = phaseEnumAndIdMap.get(PhaseDefaultName.CONSTRUCTION);
        } else if (planningPeriodphase.getPhaseEnum().equals(PhaseDefaultName.CONSTRUCTION)) {
            planningPeriodPhaseId = phaseEnumAndIdMap.get(PhaseDefaultName.PUZZLE);
        } else if (planningPeriodphase.getPhaseEnum().equals(PhaseDefaultName.PUZZLE)) {
            planningPeriodPhaseId = phaseEnumAndIdMap.get(PhaseDefaultName.REQUEST);
        }
        return planningPeriodPhaseId;
    }

    public void restoreShifts(List<ShiftState> shiftStates, List<Shift> shiftList, Long unitId, PlanningPeriod planningPeriod) {
        if (!shiftStates.isEmpty()) {
            List<Shift> shifts = new ArrayList<>();
            shiftStates.forEach(shiftState -> {
                Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftState, Shift.class);
                shift.setId(shiftState.getShiftId());
                shifts.add(shift);
            });
            save(shifts);
            shiftMongoRepository.deleteShiftAfterRestorePhase(planningPeriod.getId(), planningPeriod.getCurrentPhaseId());
            shiftStateService.updateShiftDailyTimeBankAndPaidOut(shifts, shiftList, unitId);
        }
    }

    public void restoreAvailabilityCount(List<StaffingLevel> staffingLevels, List<StaffingLevelState> staffingLevelStates) {
        if (!staffingLevels.isEmpty() && !staffingLevelStates.isEmpty()) {
            Map<Date, StaffingLevelState> dateStaffingLevelStateMap = staffingLevelStates.stream().collect(Collectors.toMap(StaffingLevel::getCurrentDate, v -> v));
            staffingLevels.forEach(staffingLevel -> {
                if (dateStaffingLevelStateMap.get(staffingLevel.getCurrentDate()) != null) {
                    Map<Integer, StaffingLevelInterval> staffingLevelIntervalMap = dateStaffingLevelStateMap.get(staffingLevel.getCurrentDate()).getPresenceStaffingLevelInterval().stream().collect(Collectors.toMap(StaffingLevelInterval::getSequence, v -> v));
                    staffingLevel.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
                        if (staffingLevelIntervalMap.get(staffingLevelInterval.getSequence()) != null) {
                            staffingLevelInterval.setAvailableNoOfStaff(staffingLevelIntervalMap.get(staffingLevelInterval.getSequence()).getAvailableNoOfStaff());
                        }
                    });
                } else {
                    staffingLevel.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> staffingLevelInterval.setAvailableNoOfStaff(0));
                }
            });
            staffingLevelMongoRepository.saveEntities(staffingLevels);
        }
    }

    /**
     * @param shiftStates
     * @param unitId
     */
    public void restoreFunctions(List<ShiftState> shiftStates, Long unitId, List<Shift> currentPhaseShifts) {
        if (shiftStates.isEmpty() && currentPhaseShifts.isEmpty()) {
            return;
        }
        Map<Long, Map<LocalDate, Long>> employmentIdWithShiftDateFunctionIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(shiftStates)) {
            for (ShiftState shiftState : shiftStates) {
                Map<LocalDate, Long> dateFunctionIdMap = employmentIdWithShiftDateFunctionIdMap.getOrDefault(shiftState.getEmploymentId(), new HashMap<LocalDate, Long>());
                dateFunctionIdMap.put(DateUtils.asLocalDate(shiftState.getStartDate()), shiftState.getFunctionId());
                employmentIdWithShiftDateFunctionIdMap.putIfAbsent(shiftState.getEmploymentId(), dateFunctionIdMap);
            }
        }
        if (CollectionUtils.isNotEmpty(currentPhaseShifts)) {
            for (Shift shift : currentPhaseShifts) {
                Map<LocalDate, Long> dateFunctionIdMap = employmentIdWithShiftDateFunctionIdMap.getOrDefault(shift.getEmploymentId(), new HashMap<LocalDate, Long>());
                dateFunctionIdMap.put(DateUtils.asLocalDate(shift.getStartDate()), null);
                employmentIdWithShiftDateFunctionIdMap.putIfAbsent(shift.getEmploymentId(), dateFunctionIdMap);
            }
        }
        userIntegrationService.restoreFunctionsWithDatesByEmploymentIds(employmentIdWithShiftDateFunctionIdMap, unitId);
    }

    public PlanningPeriodDTO getStartDateAndEndDateOfPlanningPeriodByUnitId(Long unitId) {
        return planningPeriodMongoRepository.findStartDateAndEndDateOfPlanningPeriodByUnitId(unitId);
    }

    public boolean createJobOfPlanningPeriod() {
        List<SchedulerPanelDTO> schedulerPanelDTOS = Arrays.asList(new SchedulerPanelDTO(JobType.SYSTEM, JobSubType.ADD_PLANNING_PERIOD, JobFrequencyType.MONTHLY, getLocalDateTime(getFirstDayOfMonth(getLocalDate()), 02, 00, 00), false));
        LOGGER.info("create job for add planning period");
        schedulerPanelDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, null, true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
        LOGGER.info("job registered of add planning period");
        return isCollectionNotEmpty(schedulerPanelDTOS);
    }

    //add planning period in unit via job
    public boolean addPlanningPeriodViaJob() {
        List<PlanningPeriod> planningPeriodsViaJob = new ArrayList<>();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findLastPlanningPeriodOfAllUnits();
        if (isCollectionNotEmpty(planningPeriods)) {
            LOGGER.info("add planning period via job");
            Map<Long, List<PhaseDTO>> unitIdAndPhasesMap = getPhasesWithDurationInDays(planningPeriods.stream().map(PlanningPeriod::getUnitId).collect(Collectors.toList()));
            for (PlanningPeriod planningPeriod : planningPeriods) {
                try {
                    LocalDate startDate = planningPeriod.getEndDate().plusDays(1);
                    LocalDate endDate = startDate.plusMonths(1);
                    while (startDate.isBefore(endDate)) {
                        LocalDate planningPeriodEndDate = planningPeriod.getDurationType().equals(DurationType.WEEKS) ? startDate.plusWeeks(planningPeriod.getDuration()).minusDays(1) : startDate.plusMonths(planningPeriod.getDuration()).minusDays(1);
                        String name = DateUtils.formatLocalDate(startDate, AppConstants.DATE_FORMET_STRING) + "  " + DateUtils.formatLocalDate(planningPeriodEndDate, AppConstants.DATE_FORMET_STRING);
                        PlanningPeriod planningPeriodOfUnit = new PlanningPeriod(name, startDate, planningPeriodEndDate, planningPeriod.getUnitId(), planningPeriod.getDurationType(), planningPeriod.getDuration());
                        planningPeriodOfUnit = setPhaseFlippingDatesForPlanningPeriod(startDate, unitIdAndPhasesMap.get(planningPeriod.getUnitId()), planningPeriodOfUnit);
                        planningPeriodsViaJob.add(planningPeriodOfUnit);
                        startDate = planningPeriod.getDurationType().equals(DurationType.WEEKS) ? startDate.plusWeeks(planningPeriod.getDuration()) : startDate.plusMonths(planningPeriod.getDuration());
                    }
                } catch (Exception e) {
                    LOGGER.info("error while adding planning period via job for Unit Id " + planningPeriod.getUnitId());
                    e.printStackTrace();
                }
            }
            if (isCollectionNotEmpty(planningPeriodsViaJob)) {
                planningPeriodMongoRepository.saveEntities(planningPeriodsViaJob);
                createScheduleJobOfPanningPeriod(planningPeriodsViaJob);
                LOGGER.info("successfully added planning period via job");
            } else {
                LOGGER.info("Planning Periods not created via job");
            }
        }
        return true;
    }

    // use for publish shift after flipping planning period CONSTRUCTION to DRAFT phase
    public void publishShiftsAfterFlippingPhaseConstructionToDraft(PlanningPeriod planningPeriod, Long unitId, List<Long> employmentTypeIds) {
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(employmentTypeIds, unitId, planningPeriod.getStartDate().toString(), planningPeriod.getEndDate().toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        List<Long> employmentIds=staffKpiFilterDTOS.stream().flatMap(k->k.getEmployment().stream().map(v->v.getId())).collect(Collectors.toList());
        LOGGER.info("publish shift after flipping planning period contruction to draft phase");
        List<Shift> shifts = shiftMongoRepository.findAllUnPublishShiftByPlanningPeriodAndUnitId(planningPeriod.getId(), unitId, employmentIds, Arrays.asList(ShiftStatus.PUBLISH, ShiftStatus.PENDING , ShiftStatus.REQUEST));
        if (isCollectionNotEmpty(shifts)) {
            for (Shift shift : shifts) {
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    if (!shiftActivity.getStatus().contains(ShiftStatus.PUBLISH))
                        shiftActivity.getStatus().add(ShiftStatus.PUBLISH);
                }
            }
            shiftMongoRepository.saveEntities(shifts);
            timeBankService.updateDailyTimeBankEntriesForStaffs(shifts);
            LOGGER.info("successfully publish shift after flipping planning period contruction to draft phase");

        }
    }
}
