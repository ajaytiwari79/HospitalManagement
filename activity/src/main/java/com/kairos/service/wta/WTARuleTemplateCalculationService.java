package com.kairos.service.wta;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.enums.shift.ShiftEscalationReason;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.template_types.DurationBetweenShiftsWTATemplate;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValueByPhase;
import static java.util.Comparator.comparing;

@Service
public class WTARuleTemplateCalculationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WTARuleTemplateCalculationService.class);

    @Inject private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;

    public List<ShiftDTO> updateRestingTimeInShifts(List<ShiftDTO> shifts, UserAccessRoleDTO userAccessRole){
        if(isCollectionNotEmpty(shifts)){
            if(!(shifts instanceof ArrayList)){
                shifts = new ArrayList<>(shifts);
            }
            shifts.sort(comparing(ShiftDTO::getStartDate));
            Date startDate = getStartOfDay(shifts.get(0).getStartDate());
            Date endDate = getStartOfDay(plusDays(shifts.get(shifts.size()-1).getEndDate(),1));
            List<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toList());
            Map<BigInteger,ShiftViolatedRules> shiftViolatedRulesMap = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds).stream().collect(Collectors.toMap(k->k.getShiftId(),v->v));
            List<WTAQueryResultDTO> workingTimeAgreements = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDatesWithRuleTemplateType(shifts.get(0).getUnitPositionId(),startDate,endDate, WTATemplateType.DURATION_BETWEEN_SHIFTS);
            List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdAndBetweenDates(shifts.get(0).getUnitId(),startDate,endDate);
            Map<DateTimeInterval,List<DurationBetweenShiftsWTATemplate>> intervalWTARuletemplateMap = getIntervalWTARuletemplateMap(workingTimeAgreements,asLocalDate(endDate).plusDays(1));
            Map<LocalDate, BigInteger> dateAndPhaseMap = getMapofDateAndMap(planningPeriods,shifts);
            for (ShiftDTO shift : shifts) {
                int restingMinutes = 0;
                Map.Entry<DateTimeInterval,List<DurationBetweenShiftsWTATemplate>> dateTimeIntervalListEntry = intervalWTARuletemplateMap.entrySet().stream().filter(dateTimeIntervalList -> dateTimeIntervalList.getKey().contains(shift.getStartDate()) || dateTimeIntervalList.getKey().getEndLocalDate().equals(shift.getStartDate())).findAny().orElse(null);
                if(isNotNull(dateTimeIntervalListEntry)) {
                    List<DurationBetweenShiftsWTATemplate> durationBetweenShiftsWTATemplates = dateTimeIntervalListEntry.getValue();
                    for (DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate : durationBetweenShiftsWTATemplates) {
                        Integer currentRuletemplateRestingMinutes = getValueByPhase(userAccessRole, durationBetweenShiftsWTATemplate.getPhaseTemplateValues(), dateAndPhaseMap.get(asLocalDate(shift.getStartDate())));
                        if(isNotNull(currentRuletemplateRestingMinutes) && restingMinutes<currentRuletemplateRestingMinutes){
                            restingMinutes = currentRuletemplateRestingMinutes;
                        }
                    }
                }
                shift.setRestingMinutes(restingMinutes);
                shift.setEscalationReasons(shiftViolatedRulesMap.containsKey(shift.getId()) ? newHashSet(ShiftEscalationReason.WORK_TIME_AGREEMENT) : new HashSet<>());
            }
        }
        return shifts;
    }

    private Map<LocalDate, BigInteger> getMapofDateAndMap(List<PlanningPeriod> planningPeriods,List<ShiftDTO> shifts){
        Map<LocalDate, BigInteger> dateAndPhaseMap = new HashMap<>(shifts.size());
        for (ShiftDTO shift : shifts) {
            for (PlanningPeriod planningPeriod : planningPeriods) {
                if(planningPeriod.contains(asLocalDate(shift.getStartDate()))){
                    dateAndPhaseMap.put(asLocalDate(shift.getStartDate()),planningPeriod.getCurrentPhaseId());
                }
            }
        }
        return dateAndPhaseMap;
    }

    private Map<DateTimeInterval,List<DurationBetweenShiftsWTATemplate>> getIntervalWTARuletemplateMap(List<WTAQueryResultDTO> workingTimeAgreements,LocalDate endDate){
        Map<DateTimeInterval,List<DurationBetweenShiftsWTATemplate>> dateTimeIntervalListMap = new HashMap<>(workingTimeAgreements.size());
        for (WTAQueryResultDTO workingTimeAgreement : workingTimeAgreements) {
            DateTimeInterval dateTimeInterval = new DateTimeInterval(workingTimeAgreement.getStartDate(),isNotNull(workingTimeAgreement.getEndDate()) ? workingTimeAgreement.getEndDate() : endDate);
            dateTimeIntervalListMap.put(dateTimeInterval,workingTimeAgreement.getRuleTemplates().stream().map(wtaBaseRuleTemplate -> (DurationBetweenShiftsWTATemplate)wtaBaseRuleTemplate).collect(Collectors.toList()));
        }
        return dateTimeIntervalListMap;
    }

}
