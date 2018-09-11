package com.planner.service.shift_planning;


import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import com.planner.domain.wta.templates.WorkingTimeAgreement;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WTAService {
    @Inject
    private ActivityMongoService activityMongoService;


    /**************************************Logic to fetch WTA******Start****************************************/
/*****************************************************************************************************/
    /**
     *
     * @param localDateWTAMap
     * @param unitPositionId
     * @return
     */
//TODO might be in ShiftPlanningInitializationService
    public Map<LocalDate, WorkingTimeAgreement> getLocalDateWTAMapByunitPositionId(Map<Long, Map<LocalDate, WorkingTimeAgreement>> localDateWTAMap, Long unitPositionId)
    {
        if(localDateWTAMap.containsKey(unitPositionId))
            return localDateWTAMap.get(unitPositionId);
        return null;
    }
    /**
     * This method will bind each unitPositionId
     * with its Map containing all related WTA
     *
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public Map<Long, Map<LocalDate, WorkingTimeAgreement>> getunitPositionIdWithLocalDateWTAMap(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        Map<Long, Map<LocalDate, WorkingTimeAgreement>> unitPositionIdWithLocalDateWTAMap = new HashMap<>();
        List<WorkingTimeAgreement> workingTimeAgreementS = activityMongoService.getWTARuleTemplateByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
        //Group WTA with unitPosition
        Map<Long, List<WorkingTimeAgreement>> unitPositionIdWTAMap = workingTimeAgreementS.stream().collect(Collectors.groupingBy(cta -> cta.getUnitPositionId(), Collectors.toList()));
        unitPositionIdWTAMap.forEach((unitPositionId, groupedWTAList) -> {
            Map<LocalDate, WorkingTimeAgreement> localDateWTAMap = filterOverlapedWTA(groupedWTAList, getInitialMapWithAllIntervals(fromPlanningDate, toPlanningDate), toPlanningDate);
            unitPositionIdWithLocalDateWTAMap.put(unitPositionId, localDateWTAMap);
        });
        return unitPositionIdWithLocalDateWTAMap;
    }
/*************************************************************************************************/
    /**
     * This method will bind each WTA
     * with applicable LocalDate one-to-one
     *
     * @param workingTimeAgreementS
     * @param localDateWTAMap
     * @param toPlanningDate
     * @return
     */
    public Map<LocalDate, WorkingTimeAgreement> filterOverlapedWTA(List<WorkingTimeAgreement> workingTimeAgreementS, Map<LocalDate, WorkingTimeAgreement> localDateWTAMap, Date toPlanningDate) {
        for (LocalDate applicableLocalDate : localDateWTAMap.keySet()) {
            for (WorkingTimeAgreement workingTimeAgreement : workingTimeAgreementS) {
                DateTimeInterval dateTimeIntervalPerWTA = createWTADateTimeInterval(workingTimeAgreement, toPlanningDate);
                if (dateTimeIntervalPerWTA.contains(DateUtils.asDate(applicableLocalDate))) {
                    localDateWTAMap.put(applicableLocalDate, workingTimeAgreement);
                }
            }
        }
        return localDateWTAMap;
    }
/*************************************************************************************************/
    /**
     * This method is used to create Interval
     * for each WTA
     *
     * @param workingTimeAgreement
     * @param toPlanningDate
     * @return
     */
    public DateTimeInterval createWTADateTimeInterval(WorkingTimeAgreement workingTimeAgreement, Date toPlanningDate) {
        Date ctaStartDate = DateUtils.asDate(workingTimeAgreement.getStartDate());
        Date ctaEndDate = workingTimeAgreement.getEndDate() == null || workingTimeAgreement.getEndDate().isAfter(DateUtils.asLocalDate(toPlanningDate)) ? toPlanningDate : DateUtils.asDate(workingTimeAgreement.getEndDate());
        DateTimeInterval dateTimeInterval = new DateTimeInterval(ctaStartDate, ctaEndDate);
        return dateTimeInterval;
    }
/*************************************************************************************************/
    /**
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public Map<LocalDate, WorkingTimeAgreement> getInitialMapWithAllIntervals(Date fromPlanningDate, Date toPlanningDate) {
        LocalDate fromPlanningLocalDate = ZonedDateTime.ofInstant(fromPlanningDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocalDate toPlanningLocalDate = ZonedDateTime.ofInstant(toPlanningDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        ;
        Map<LocalDate, WorkingTimeAgreement> localDateWorkingTimeAgreementMap = new HashMap<>();
        while (!fromPlanningLocalDate.equals(toPlanningLocalDate)) {
            localDateWorkingTimeAgreementMap.put(fromPlanningLocalDate, null);
            fromPlanningLocalDate = fromPlanningLocalDate.plusDays(1l);
        }
        return localDateWorkingTimeAgreementMap;
    }

/*********************************fetch WTA logic *****End***********************************************************/
/*******************************************************************************************************************/

}
