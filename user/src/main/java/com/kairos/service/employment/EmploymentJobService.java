package com.kairos.service.employment;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.employment.EmploymentIdDTO;
import com.kairos.dto.user.employment.PositionDTO;
import com.kairos.persistence.model.staff.position.EmploymentAndPositionDTO;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.user.employment.Employment;
import com.kairos.persistence.model.user.employment.EmploymentLine;
import com.kairos.persistence.model.user.employment.EmploymentLineEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.employment.query_result.EmploymentSeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentAndEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.scheduler.SchedulerToUserQueueService;
import com.kairos.service.scheduler.UserSchedulerJobService;
import com.kairos.service.scheduler.UserToSchedulerQueueService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_POSITION_END_DATE_GREATER_THAN_EMPLOYMENT_START_DATE;

/**
 * CreatedBy vipulpandey on 27/10/18
 **/
@Service
@Transactional
public class EmploymentJobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerToUserQueueService.class);
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private KafkaProducer kafkaProducer;
    @Inject
    private EmploymentAndEmploymentTypeRelationShipGraphRepository employmentAndEmploymentTypeRelationShipGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserToSchedulerQueueService userToSchedulerQueueService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private EmploymentService employmentService;
    @Inject private ActivityIntegrationService activityIntegrationService;
    @Inject private UserSchedulerJobService userSchedulerJobService;

    public void updateSeniorityLevelOnJobTrigger(BigInteger schedulerPanelId, Long unitId) {
        LocalDate todayDate = getCurrentLocalDate();
        try {
            List<EmploymentSeniorityLevelQueryResult> employmentSeniorityLevelQueryResults = employmentGraphRepository.findEmploymentSeniorityLeveltoUpdate();
            if (isCollectionNotEmpty(employmentSeniorityLevelQueryResults)) {
                Map<Long, EmploymentSeniorityLevelQueryResult> employmentSeniorityLevelQueryResultMap = employmentSeniorityLevelQueryResults.stream().collect(Collectors.toMap(EmploymentSeniorityLevelQueryResult::getEmploymentId, java.util.function.Function.identity()));
                Set<Long> employmentIds = employmentSeniorityLevelQueryResultMap.keySet();
                Iterable<Employment> employments = employmentGraphRepository.findAllById(employmentIds, 2);
                Map<EmploymentIdDTO, EmploymentLine> newEmploymentLineWithParentId = new HashMap<>();
                List<EmploymentLineEmploymentTypeRelationShip> employmentLineEmploymentTypeRelationShips = new ArrayList<>();
                for (Employment currentEmployment : employments) {
                    getEmploymentLineAndSetDetails(todayDate, employmentSeniorityLevelQueryResultMap, newEmploymentLineWithParentId, currentEmployment);
                }
                for (Map.Entry<EmploymentIdDTO, EmploymentLine> currentMap : newEmploymentLineWithParentId.entrySet()) {
                    EmploymentSeniorityLevelQueryResult currentObject = employmentSeniorityLevelQueryResultMap.get(currentMap.getKey().getOldEmploymentId());
                    if (currentObject != null) {
                        EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip =
                                new EmploymentLineEmploymentTypeRelationShip(currentMap.getValue(), currentObject.getEmploymentType(),
                                        currentObject.getEmploymentLineEmploymentTypeRelationShip().getEmploymentTypeCategory());
                        employmentLineEmploymentTypeRelationShips.add(employmentLineEmploymentTypeRelationShip);
                    }
                }
                employmentGraphRepository.saveAll(employments);
                employmentAndEmploymentTypeRelationShipGraphRepository.saveAll(employmentLineEmploymentTypeRelationShips);
            }
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
    }

    private void getEmploymentLineAndSetDetails(LocalDate todayDate, Map<Long, EmploymentSeniorityLevelQueryResult> employmentSeniorityLevelQueryResultMap, Map<EmploymentIdDTO, EmploymentLine> newEmploymentLineWithParentId, Employment currentEmployment) {
        Optional<EmploymentLine> employmentLine = currentEmployment.getEmploymentLines().stream()
                .filter(pl -> !todayDate.isBefore(pl.getStartDate()) && (isNull(pl.getEndDate()) || !todayDate.isAfter(pl.getEndDate())))
                .findAny();
        if (employmentLine.isPresent()) {
            EmploymentLine newEmploymentLine = EmploymentLine.builder()
                    .avgDailyWorkingHours(employmentLine.get().getAvgDailyWorkingHours())
                    .totalWeeklyMinutes(employmentLine.get().getTotalWeeklyMinutes())
                    .hourlyCost(employmentLine.get().getHourlyCost())
                    .startDate(todayDate)
                    .functions(employmentLine.get().getFunctions())
                    .fullTimeWeeklyMinutes(employmentLine.get().getFullTimeWeeklyMinutes())
                    .workingDaysInWeek(employmentLine.get().getWorkingDaysInWeek())
                    .endDate(employmentLine.get().getEndDate())
                    .seniorityLevel(employmentSeniorityLevelQueryResultMap.get(currentEmployment.getId()).getSeniorityLevel())
                    .build();
            employmentLine.get().setEndDate(todayDate.minusDays(1));
            currentEmployment.getEmploymentLines().add(newEmploymentLine);
            updateSeniorityLevelOfAllValidEmploymentLine(currentEmployment,employmentSeniorityLevelQueryResultMap.get(currentEmployment.getId()).getSeniorityLevel(),employmentLine.get().getEndDate(),employmentSeniorityLevelQueryResultMap.get(currentEmployment.getId()).getExpertiseEndDate());
            newEmploymentLineWithParentId.put(new EmploymentIdDTO(currentEmployment.getId(), null, employmentLine.get().getId()), newEmploymentLine);
        }
    }

    private void updateSeniorityLevelOfAllValidEmploymentLine(Employment currentEmployment, SeniorityLevel seniorityLevel, LocalDate startDate, LocalDate endDate) {
        List<EmploymentLine> employmentLines = currentEmployment.getEmploymentLines().stream()
                .filter(pl -> startDate.isBefore(pl.getStartDate()) && (isNull(pl.getEndDate()) || isNull(endDate) || !endDate.isBefore(pl.getEndDate()))).collect(Collectors.toList());
        employmentLines.forEach(employmentLine -> employmentLine.setSeniorityLevel(seniorityLevel));
    }

    public EmploymentAndPositionDTO updateEmploymentEndDateFromPosition(Long staffId, Long unitId, PositionDTO positionDTO) {
        Long endDateMillis = DateUtils.getIsoDateInLong(positionDTO.getEndDate());
        String employmentStartDateMax = employmentGraphRepository.getMaxEmploymentStartDate(staffId);
        if (Optional.ofNullable(employmentStartDateMax).isPresent() && DateUtils.getDateFromEpoch(endDateMillis).isBefore(LocalDate.parse(employmentStartDateMax))) {
            exceptionService.actionNotPermittedException(MESSAGE_POSITION_END_DATE_GREATER_THAN_EMPLOYMENT_START_DATE, employmentStartDateMax);
        }
        List<Employment> employments = employmentGraphRepository.getEmploymentsFromEmploymentEndDate(staffId, DateUtils.getDateFromEpoch(endDateMillis).toString());
        for (Employment employment : employments) {
            employment.setEndDate(DateUtils.getLocalDate(endDateMillis));
            if (!Optional.ofNullable(employment.getReasonCodeId()).isPresent()) {
                employment.setReasonCodeId(positionDTO.getReasonCodeId());
            }
        }
        if (CollectionUtils.isNotEmpty(employments)) {
            employmentGraphRepository.updateEmploymentLineEndDateByEmploymentIds(employments.stream().map(Employment::getId).collect(Collectors.toSet()), DateUtils.getLocalDate(endDateMillis).toString());
        }
        Position position = positionGraphRepository.findByStaffId(staffId);
        position.setEndDateMillis(endDateMillis);
        position.setReasonCodeId(positionDTO.getReasonCodeId());
        position.setAccessGroupIdOnPositionEnd(positionDTO.getAccessGroupIdOnPositionEnd());
        employmentGraphRepository.saveAll(employments);
        positionGraphRepository.save(position);
        return employmentService.getEmploymentsOfStaff(unitId,staffId,true);
    }

    public void updateNightWorkers(){
        List<Map> employments = employmentGraphRepository.findStaffsWithEmploymentIds();
        activityIntegrationService.updateNightWorkers(employments);
    }
}
