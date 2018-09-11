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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CTAService {
    @Inject
    private ActivityMongoService activityMongoService;

    /**************************************Logic to fetch CTA******Start****************************************/
/*****************************************************************************************************/
    /**
     *
     * @param longCTAResponseDTOMap
     * @param unitPositionId
     * @return
     */
//TODO might be in ShiftPlanningInitializationService
public Map<LocalDate, CTAResponseDTO> getLocalDateCTAMapByunitPositionId( Map<Long, Map<LocalDate, CTAResponseDTO>> longCTAResponseDTOMap, Long unitPositionId)
{
    if(longCTAResponseDTOMap.containsKey(unitPositionId))
    return longCTAResponseDTOMap.get(unitPositionId);
return null;
}
    /**
     * This method will bind each unitPositionId
     * with its Map containing all related CTA
     *
     * @param unitPositionIds
     * @param fromPlanningDate
     * @param toPlanningDate
     * @return
     */
    public Map<Long, Map<LocalDate, CTAResponseDTO>> getunitPositionIdWithLocalDateCTAMap(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        Map<Long, Map<LocalDate, CTAResponseDTO>> unitPositionIdWithLocalDateCTAMap = new HashMap<>();
        List<CTAResponseDTO> ctaResponseDTOS = activityMongoService.getCTARuleTemplateByUnitPositionIds(unitPositionIds, fromPlanningDate, toPlanningDate);
        //Group CTA with unitPosition
        Map<Long, List<CTAResponseDTO>> unitPositionIdCTAMap = ctaResponseDTOS.stream().collect(Collectors.groupingBy(cta -> cta.getUnitPositionId(), Collectors.toList()));
        unitPositionIdCTAMap.forEach((unitPositionId, groupedCTAList) -> {
            Map<LocalDate, CTAResponseDTO> localDateCTAMap = filterOverlapedCTA(groupedCTAList, getInitialMapWithAllIntervals(fromPlanningDate, toPlanningDate), toPlanningDate);
            unitPositionIdWithLocalDateCTAMap.put(unitPositionId, localDateCTAMap);
        });
        return unitPositionIdWithLocalDateCTAMap;
    }
/*************************************************************************************************/
    /**
     * This method will bind each CTA
     * with applicable LocalDate one-to-one
     *
     * @param ctaResponseDTOS
     * @param localDateCTAMap
     * @param toPlanningDate
     * @return
     */
    public Map<LocalDate, CTAResponseDTO> filterOverlapedCTA(List<CTAResponseDTO> ctaResponseDTOS, Map<LocalDate, CTAResponseDTO> localDateCTAMap, Date toPlanningDate) {
        for (LocalDate applicableLocalDate : localDateCTAMap.keySet()) {
            for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOS) {
                DateTimeInterval dateTimeIntervalPerCTA = createCTADateTimeInterval(ctaResponseDTO, toPlanningDate);
                if (dateTimeIntervalPerCTA.contains(DateUtils.asDate(applicableLocalDate))) {
                    localDateCTAMap.put(applicableLocalDate, ctaResponseDTO);
                }
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
    public Map<LocalDate, CTAResponseDTO> getInitialMapWithAllIntervals(Date fromPlanningDate, Date toPlanningDate) {
        LocalDate fromPlanningLocalDate = ZonedDateTime.ofInstant(fromPlanningDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocalDate toPlanningLocalDate = ZonedDateTime.ofInstant(toPlanningDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        ;
        Map<LocalDate, CTAResponseDTO> localDateCTAResponseDTOMap = new HashMap<>();
        while (!fromPlanningLocalDate.equals(toPlanningLocalDate)) {
            localDateCTAResponseDTOMap.put(fromPlanningLocalDate, null);
            fromPlanningLocalDate = fromPlanningLocalDate.plusDays(1l);
        }
        return localDateCTAResponseDTOMap;
    }

/*********************************fetch CTA logic *****End***********************************************************/
/*******************************************************************************************************************/

}
