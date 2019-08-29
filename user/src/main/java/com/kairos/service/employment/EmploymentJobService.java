package com.kairos.service.employment;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.dto.user.employment.EmploymentIdDTO;
import com.kairos.dto.user.employment.PositionDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.staff.position.EmploymentAndPositionDTO;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.staff.position.PositionQueryResult;
import com.kairos.persistence.model.user.employment.Employment;
import com.kairos.persistence.model.user.employment.EmploymentLine;
import com.kairos.persistence.model.user.employment.EmploymentLineEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.employment.query_result.EmploymentSeniorityLevelQueryResult;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentAndEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.scheduler.UserToSchedulerQueueService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_POSITION_END_DATE_GREATER_THAN_EMPLOYMENT_START_DATE;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_REASONCODE_ID_NOTFOUND;

/**
 * CreatedBy vipulpandey on 27/10/18
 **/
@Service
@Transactional
public class EmploymentJobService {
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
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private UserToSchedulerQueueService userToSchedulerQueueService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private EmploymentService employmentService;
    @Inject private ActivityIntegrationService activityIntegrationService;

    public void updateSeniorityLevelOnJobTrigger(BigInteger schedulerPanelId, Long unitId) {

        LocalDateTime started = LocalDateTime.now();
        LocalDate todaysDate = DateUtils.getCurrentLocalDate();
        KairosSchedulerLogsDTO schedulerLogsDTO;
        LocalDateTime stopped;
        String log = null;
        Result result = Result.SUCCESS;
        try {
            List<EmploymentSeniorityLevelQueryResult> employmentSeniorityLevelQueryResults = employmentGraphRepository.findEmploymentSeniorityLeveltoUpdate();
            if (!employmentSeniorityLevelQueryResults.isEmpty()) {

                Map<Long, EmploymentSeniorityLevelQueryResult> employmentSeniorityLevelQueryResultMap
                        = employmentSeniorityLevelQueryResults.stream().collect(Collectors.toMap(EmploymentSeniorityLevelQueryResult::getEmploymentId, java.util.function.Function.identity()));

                Set<Long> employmentIds = employmentSeniorityLevelQueryResultMap.keySet();
                Iterable<Employment> employments = employmentGraphRepository.findAllById(employmentIds, 2);

                Map<EmploymentIdDTO, EmploymentLine> newEmploymentLineWithParentId = new HashMap<>();

                for (Employment currentEmployment : employments) {
                    Optional<EmploymentLine> employmentLine = currentEmployment.getEmploymentLines().stream()
                            .filter(pl -> (todaysDate.isAfter(pl.getStartDate()) || todaysDate.isEqual(pl.getStartDate()) && (pl.getEndDate() == null || pl.getEndDate().isBefore(todaysDate) || pl.getEndDate().isEqual(todaysDate))))
                            .findAny();
                    if (employmentLine.isPresent()) {
                        EmploymentLine newEmploymentLine = new EmploymentLine.EmploymentLineBuilder()
                                .setAvgDailyWorkingHours(employmentLine.get().getAvgDailyWorkingHours())
                                .setTotalWeeklyMinutes(employmentLine.get().getTotalWeeklyMinutes())
                                .setHourlyCost(employmentLine.get().getHourlyCost())
                                .setStartDate(todaysDate.plusDays(1))
                                .setFunctions(employmentLine.get().getFunctions())
                                .setFullTimeWeeklyMinutes(employmentLine.get().getFullTimeWeeklyMinutes())
                                .setWorkingDaysInWeek(employmentLine.get().getWorkingDaysInWeek())
                                .setEndDate(employmentLine.get().getEndDate())
                                .setSeniorityLevel(employmentSeniorityLevelQueryResultMap.get(currentEmployment.getId()).getSeniorityLevel())
                                .build();
                        employmentLine.get().setEndDate(todaysDate);
                        currentEmployment.getEmploymentLines().add(newEmploymentLine);
                        newEmploymentLineWithParentId.put(new EmploymentIdDTO(currentEmployment.getId(), null, employmentLine.get().getId()), newEmploymentLine);
                    }

                }
                List<EmploymentLineEmploymentTypeRelationShip> employmentLineEmploymentTypeRelationShips = new ArrayList<>();

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

            log = ex.getMessage();
            result = Result.ERROR;
        }

        stopped = LocalDateTime.now();

        schedulerLogsDTO = new KairosSchedulerLogsDTO(result, log, schedulerPanelId, unitId, DateUtils.getMillisFromLocalDateTime(started), DateUtils.getMillisFromLocalDateTime(stopped), JobSubType.SENIORITY_LEVEL);

        kafkaProducer.pushToSchedulerLogsQueue(schedulerLogsDTO);


    }

    public EmploymentAndPositionDTO updateEmploymentEndDateFromPosition(Long staffId, Long unitId, PositionDTO positionDTO) {
        Long endDateMillis = DateUtils.getIsoDateInLong(positionDTO.getEndDate());
        String employmentStartDateMax = employmentGraphRepository.getMaxEmploymentStartDate(staffId);
        if (Optional.ofNullable(employmentStartDateMax).isPresent() && DateUtils.getDateFromEpoch(endDateMillis).isBefore(LocalDate.parse(employmentStartDateMax))) {
            exceptionService.actionNotPermittedException(MESSAGE_POSITION_END_DATE_GREATER_THAN_EMPLOYMENT_START_DATE, employmentStartDateMax);

        }
        List<Employment> employments = employmentGraphRepository.getEmploymentsFromEmploymentEndDate(staffId, DateUtils.getDateFromEpoch(endDateMillis).toString());
        Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(positionDTO.getReasonCodeId(), 0);
        if (!reasonCode.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_REASONCODE_ID_NOTFOUND, positionDTO.getReasonCodeId());
        }
        for (Employment employment : employments) {
            employment.setEndDate(DateUtils.getLocalDate(endDateMillis));
            if (!Optional.ofNullable(employment.getReasonCode()).isPresent()) {
                employment.setReasonCode(reasonCode.get());
            }
        }
        if (CollectionUtils.isNotEmpty(employments)) {
            employmentGraphRepository.updateEmploymentLineEndDateByEmploymentIds(employments.stream().map(Employment::getId).collect(Collectors.toSet()), DateUtils.getLocalDate(endDateMillis).toString());
        }
        Position position = positionGraphRepository.findByStaffId(staffId);
//        userToSchedulerQueueService.pushToJobQueueOnEmploymentEnd(endDateMillis, position.getEndDateMillis(), unit.getId(), position.getId(),
//                unit.getTimeZone());

        position.setEndDateMillis(endDateMillis);
        positionGraphRepository.deletePositionReasonCodeRelation(staffId);

        position.setReasonCode(reasonCode.get());
        position.setAccessGroupIdOnPositionEnd(positionDTO.getAccessGroupIdOnPositionEnd());
        employmentGraphRepository.saveAll(employments);
        positionGraphRepository.save(position);
        User user = userGraphRepository.getUserByStaffId(staffId);
        PositionQueryResult positionQueryResult = new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), position.getReasonCode().getId(), position.getAccessGroupIdOnPositionEnd());
        return employmentService.getEmploymentsOfStaff(unitId,staffId,true);

    }

    public void updateNightWorkers(){
        List<Map> employments = employmentGraphRepository.findStaffsWithEmploymentIds();
        activityIntegrationService.updateNightWorkers(employments);

    }



}
