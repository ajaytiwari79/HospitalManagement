package com.planner.service.shift_planning;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.shiftplanning.domain.wta.updated_wta.WorkingTimeAgreement;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WTAService {
    @Inject
    private ActivityMongoService activityMongoService;


    /**************************************Logic to fetch WTA******Start****************************************/
/*****************************************************************************************************/
    /**
     * @param longWorkingTimeAgreementMap
     * @param unitPositionId
     * @return
     */
//TODO might be in ShiftPlanningInitializationService
    public Map<LocalDate, WorkingTimeAgreement> getLocalDateWTAMapByunitPositionId(Map<Long, Map<LocalDate, WorkingTimeAgreement>> longWorkingTimeAgreementMap, Long unitPositionId) {
        Map<LocalDate, WorkingTimeAgreement> applicableLocalDateWTAPerStaff=new HashMap<>();
        if (!longWorkingTimeAgreementMap.containsKey(unitPositionId)){}
        return longWorkingTimeAgreementMap.get(unitPositionId);

    }

    /**
     * <ul>
     * <li>This method will bind each unitPositionId with its Map containing all related [WTA/s datewise].</li>
     * <li>This may happen that there always exist some WTA(either List or single) for particular unitPositionId but not applicable
     * in this{@param fromPlanningDate},{@param toPlanningDate} range(All WTA's) hence might be empty
     * so we need to skip this staff for further planning</li>
     * </ul>
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public Map<Long, Map<LocalDate, WorkingTimeAgreement>> getunitPositionIdWithLocalDateWTAMap(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        Map<Long, Map<LocalDate, WorkingTimeAgreement>> unitPositionIdWithLocalDateWTAMap = new HashMap<>();
        List<WorkingTimeAgreement> workingTimeAgreement = activityMongoService.getWTARuleTemplateByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
        //Group WTA with unitPosition
        if (workingTimeAgreement.size() > 0) {
            Map<Long, List<WorkingTimeAgreement>> unitPositionIdWTAMap = workingTimeAgreement.stream().collect(Collectors.groupingBy(wta -> wta.getUnitPositionId(), Collectors.toList()));
            unitPositionIdWTAMap.forEach((unitPositionId, groupedWTAList) -> {
                Map<LocalDate, WorkingTimeAgreement> localDateWTAMap = filterOverlapedWTA(groupedWTAList, getApplicableIntervals(fromPlanningDate, toPlanningDate), toPlanningDate);
                unitPositionIdWithLocalDateWTAMap.put(unitPositionId, localDateWTAMap);
            });
        }
        return unitPositionIdWithLocalDateWTAMap;
    }
/*************************************************************************************************/
    /**
     * <ul><li>
     * This method will bind each WTA
     * with applicable LocalDate one-to-one<li/>
     * <li>Note:- It is assumed that no any 2 WTA Date will contradict each other
     * </li></ul>
     * @param workingTimeAgreement
     * @param localDateApplicableIntervals
     * @param toPlanningDate
     * @return
     */
    public Map<LocalDate, WorkingTimeAgreement> filterOverlapedWTA(List<WorkingTimeAgreement> workingTimeAgreement, Set<LocalDate> localDateApplicableIntervals, Date toPlanningDate) {
        Map<LocalDate, WorkingTimeAgreement> localDateWTAMap=new HashMap<>();

        for (WorkingTimeAgreement wtaResponseDTO : workingTimeAgreement) {
            DateTimeInterval dateTimeIntervalPerWTA = createWTADateTimeInterval(wtaResponseDTO, toPlanningDate);
            LocalDate startLocalDate=wtaResponseDTO.getStartDate();
            LocalDate endLocalDate=dateTimeIntervalPerWTA.getEndLocalDate();
            while(!startLocalDate.equals(endLocalDate) && localDateApplicableIntervals.contains(startLocalDate))
            {
                localDateWTAMap.put(startLocalDate,wtaResponseDTO);
                startLocalDate=startLocalDate.plusDays(1l);//TODO optimization reuse {localDateApplicableIntervals}
            }
        }
        return localDateWTAMap;
    }
/*************************************************************************************************/
    /**
     * This method is used to create Interval
     * for each WTA
     *
     * @param wtaResponseDTO
     * @param toPlanningDate
     * @return
     */
    public DateTimeInterval createWTADateTimeInterval(WorkingTimeAgreement wtaResponseDTO, Date toPlanningDate) {
        Date wtaStartDate = DateUtils.asDate(wtaResponseDTO.getStartDate());
        Date wtaEndDate = wtaResponseDTO.getEndDate() == null || wtaResponseDTO.getEndDate().isAfter(DateUtils.asLocalDate(toPlanningDate)) ? toPlanningDate : DateUtils.asDate(wtaResponseDTO.getEndDate());
        DateTimeInterval dateTimeInterval = new DateTimeInterval(wtaStartDate, wtaEndDate);
        return dateTimeInterval;
    }
/*************************************************************************************************/
    /**
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public Set<LocalDate> getApplicableIntervals(Date fromPlanningDate, Date toPlanningDate) {
        LocalDate fromPlanningLocalDate = ZonedDateTime.ofInstant(fromPlanningDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocalDate toPlanningLocalDate = ZonedDateTime.ofInstant(toPlanningDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        Set<LocalDate> localDateSet= new TreeSet<>();
        while (!fromPlanningLocalDate.equals(toPlanningLocalDate)) {
            localDateSet.add(fromPlanningLocalDate);
            fromPlanningLocalDate = fromPlanningLocalDate.plusDays(1l);
        }
        return localDateSet;
    }

/*********************************fetch WTA logic *****End***********************************************************/
/*******************************************************************************************************************/

}
