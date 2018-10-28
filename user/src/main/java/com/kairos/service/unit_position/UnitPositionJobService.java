package com.kairos.service.unit_position;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.dto.user.employment.UnitPositionIdDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionLine;
import com.kairos.persistence.model.user.unit_position.UnitPositionLineEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionSeniorityLevelQueryResult;
import com.kairos.persistence.repository.user.unit_position.UnitPositionEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CreatedBy vipulpandey on 27/10/18
 **/
@Service
@Transactional
public class UnitPositionJobService {
    @Inject private UnitPositionGraphRepository unitPositionGraphRepository;
    @Inject
    private KafkaProducer kafkaProducer;
    @Inject private UnitPositionEmploymentTypeRelationShipGraphRepository unitPositionEmploymentTypeRelationShipGraphRepository;
    public void updateSeniorityLevelOnJobTrigger(BigInteger schedulerPanelId, Long unitId) {

        LocalDateTime started = LocalDateTime.now();
        LocalDate todaysDate = DateUtils.getCurrentLocalDate();
        KairosSchedulerLogsDTO schedulerLogsDTO;
        LocalDateTime stopped;
        String log = null;
        Result result = Result.SUCCESS;
        try {
            List<UnitPositionSeniorityLevelQueryResult> unitPositionSeniorityLevelQueryResults = unitPositionGraphRepository.findUnitPositionSeniorityLeveltoUpdate();
            if (!unitPositionSeniorityLevelQueryResults.isEmpty()) {

                Map<Long, UnitPositionSeniorityLevelQueryResult> unitPositionSeniorityLevelQueryResultMap
                        = unitPositionSeniorityLevelQueryResults.stream().collect(Collectors.toMap(t -> t.getUnitPositionId(), java.util.function.Function.identity()));

                Set<Long> unitPositionIds = unitPositionSeniorityLevelQueryResultMap.keySet();
                Iterable<UnitPosition> unitPositions = unitPositionGraphRepository.findAllById(unitPositionIds, 2);

                Map<UnitPositionIdDTO, UnitPositionLine> newPositionLineWithParentId = new HashMap<>();

                for (UnitPosition currentUnitPosition : unitPositions) {
                    Optional<UnitPositionLine> positionLine = currentUnitPosition.getUnitPositionLines().stream()
                            .filter(pl -> (todaysDate.isAfter(pl.getStartDate()) || todaysDate.isEqual(pl.getStartDate()) && (pl.getEndDate() == null || pl.getEndDate().isBefore(todaysDate) || pl.getEndDate().isEqual(todaysDate))))
                            .findAny();
                    if (positionLine.isPresent()) {
                        UnitPositionLine newUnitPositionLine = new UnitPositionLine.UnitPositionLineBuilder()
                                .setAvgDailyWorkingHours(positionLine.get().getAvgDailyWorkingHours())
                                .setTotalWeeklyMinutes(positionLine.get().getTotalWeeklyMinutes())
                                .setHourlyWages(positionLine.get().getHourlyWages())
                                .setStartDate(todaysDate.plusDays(1))
                                .setFunctions(positionLine.get().getFunctions())
                                .setFullTimeWeeklyMinutes(positionLine.get().getFullTimeWeeklyMinutes())
                                .setWorkingDaysInWeek(positionLine.get().getWorkingDaysInWeek())
                                .setEndDate(positionLine.get().getEndDate())
                                .setSeniorityLevel(unitPositionSeniorityLevelQueryResultMap.get(currentUnitPosition.getId()).getSeniorityLevel())
                                .build();
                        positionLine.get().setEndDate(todaysDate);
                        currentUnitPosition.getUnitPositionLines().add(newUnitPositionLine);
                        newPositionLineWithParentId.put(new UnitPositionIdDTO(currentUnitPosition.getId(), null, positionLine.get().getId()), newUnitPositionLine);
                    }

                }
                List<UnitPositionLineEmploymentTypeRelationShip> unitPositionLineEmploymentTypeRelationShips = new ArrayList<>();

                for (Map.Entry<UnitPositionIdDTO, UnitPositionLine> currentMap : newPositionLineWithParentId.entrySet()) {
                    UnitPositionSeniorityLevelQueryResult currentObject = unitPositionSeniorityLevelQueryResultMap.get(currentMap.getKey());
                    if (currentObject != null) {
                        UnitPositionLineEmploymentTypeRelationShip unitPositionLineEmploymentTypeRelationShip =
                                new UnitPositionLineEmploymentTypeRelationShip(currentMap.getValue(), currentObject.getEmploymentType(),
                                        currentObject.getUnitPositionLineEmploymentTypeRelationShip().getEmploymentTypeCategory());
                        unitPositionLineEmploymentTypeRelationShips.add(unitPositionLineEmploymentTypeRelationShip);
                    }
                }

                unitPositionGraphRepository.saveAll(unitPositions);
                unitPositionEmploymentTypeRelationShipGraphRepository.saveAll(unitPositionLineEmploymentTypeRelationShips);

            }

        } catch (Exception ex) {

            log = ex.getMessage();
            result = Result.ERROR;
        }

        stopped = LocalDateTime.now();

        schedulerLogsDTO = new KairosSchedulerLogsDTO(result, log, schedulerPanelId, unitId, DateUtils.getMillisFromLocalDateTime(started), DateUtils.getMillisFromLocalDateTime(stopped), JobSubType.SENIORITY_LEVEL);

        kafkaProducer.pushToSchedulerLogsQueue(schedulerLogsDTO);

        // List<CTAWTAResponseDTO> ctaWTAs =  activityIntegrationService.copyWTACTA(unitPositionNewOldIds);

    }

}
