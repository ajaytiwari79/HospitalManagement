package com.planner.service.shift_planning;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import org.joda.time.chrono.ZonedChronology;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CTAService {
    @Inject
    private ActivityMongoService activityMongoService;

    /**************************************Logic to fetch CTA******Start****************************************/
/*****************************************************************************************************/
    /**
     * @param longCTAResponseDTOMap
     * @param unitPositionId
     * @return
     */
//TODO might be in ShiftPlanningInitializationService
    public Map<LocalDate, CTAResponseDTO> getLocalDateCTAMapByunitPositionId(Map<Long, Map<LocalDate, CTAResponseDTO>> longCTAResponseDTOMap, Long unitPositionId) {
        Map<LocalDate, CTAResponseDTO> applicableLocalDateCTAPerStaff=new HashMap<>();
        if (!longCTAResponseDTOMap.containsKey(unitPositionId)){}
            return longCTAResponseDTOMap.get(unitPositionId);

    }

    /**
     * <ul>
     * <li>This method will bind each unitPositionId with its Map containing all related [CTA/s datewise].</li>
     * <li>This may happen that there always exist some CTA(either List or single) for particular unitPositionId but not applicable
     * in this{@param fromPlanningDate},{@param toPlanningDate} range(All CTA's) hence might be empty
     * so we need to skip this staff for further planning</li>
     * </ul>
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public Map<Long, Map<LocalDate, CTAResponseDTO>> getunitPositionIdWithLocalDateCTAMap(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        Map<Long, Map<LocalDate, CTAResponseDTO>> unitPositionIdWithLocalDateCTAMap = new HashMap<>();
        List<CTAResponseDTO> ctaResponseDTOS = activityMongoService.getCTARuleTemplateByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
        //Group CTA with unitPosition
        if (ctaResponseDTOS.size() > 0) {
            Map<Long, List<CTAResponseDTO>> unitPositionIdCTAMap = ctaResponseDTOS.stream().collect(Collectors.groupingBy(cta -> cta.getUnitPositionId(), Collectors.toList()));
            unitPositionIdCTAMap.forEach((unitPositionId, groupedCTAList) -> {
                Map<LocalDate, CTAResponseDTO> localDateCTAMap = filterOverlapedCTA(groupedCTAList, getApplicableIntervals(fromPlanningDate, toPlanningDate), toPlanningDate);
                unitPositionIdWithLocalDateCTAMap.put(unitPositionId, localDateCTAMap);
            });
        }
        return unitPositionIdWithLocalDateCTAMap;
    }
/*************************************************************************************************/
    /**
     * <ul><li>
     * This method will bind each CTA
     * with applicable LocalDate one-to-one<li/>
     * <li>Note:- It is assumed that no any 2 CTA Date will contradict each other
     * </li></ul>
     * @param ctaResponseDTOS
     * @param localDateApplicableIntervals
     * @param toPlanningDate
     * @return
     */
    public Map<LocalDate, CTAResponseDTO> filterOverlapedCTA(List<CTAResponseDTO> ctaResponseDTOS, Set<LocalDate> localDateApplicableIntervals, Date toPlanningDate) {
        Map<LocalDate, CTAResponseDTO> localDateCTAMap=new HashMap<>();

        for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOS) {
            DateTimeInterval dateTimeIntervalPerCTA = createCTADateTimeInterval(ctaResponseDTO, toPlanningDate);
           LocalDate startLocalDate=ctaResponseDTO.getStartDate();
            LocalDate endLocalDate=dateTimeIntervalPerCTA.getEndLocalDate();
            while(!startLocalDate.equals(endLocalDate) && localDateApplicableIntervals.contains(startLocalDate))
            {
                localDateCTAMap.put(startLocalDate,ctaResponseDTO);
                startLocalDate=startLocalDate.plusDays(1l);//TODO optimization reuse {localDateApplicableIntervals}
            }
        }
        return localDateCTAMap;
    }
/*************************************************************************************************/
    /**
     * This method is used to create Interval
     * for each CTA
     *
     * @param ctaResponseDTO
     * @param toPlanningDate
     * @return
     */
    public DateTimeInterval createCTADateTimeInterval(CTAResponseDTO ctaResponseDTO, Date toPlanningDate) {
        Date ctaStartDate = DateUtils.asDate(ctaResponseDTO.getStartDate());
        Date ctaEndDate = ctaResponseDTO.getEndDate() == null || ctaResponseDTO.getEndDate().isAfter(DateUtils.asLocalDate(toPlanningDate)) ? toPlanningDate : DateUtils.asDate(ctaResponseDTO.getEndDate());
        DateTimeInterval dateTimeInterval = new DateTimeInterval(ctaStartDate, ctaEndDate);
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

/*********************************fetch CTA logic *****End***********************************************************/
/*******************************************************************************************************************/

}
