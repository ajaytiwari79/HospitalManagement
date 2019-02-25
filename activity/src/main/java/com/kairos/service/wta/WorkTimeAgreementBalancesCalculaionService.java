package com.kairos.service.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.activity_tabs.CutOffInterval;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementRuleTemplateBalancesDTO;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.ChildCareDaysCheckWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.SeniorDaysPerYearWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.VetoAndStopBricksWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.WTAForCareDays;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.utils.ShiftValidatorService.*;


@Service
public class WorkTimeAgreementBalancesCalculaionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkTimeAgreementBalancesCalculaionService.class);

    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ExceptionService exceptionService;

    @Inject
    private UserIntegrationService userIntegrationService;


    public DateTimeInterval getIntervalByRuletemplates(Map<BigInteger,ActivityWrapper> activityWrapperMap,List<WTABaseRuleTemplate> WTARuleTemplates, LocalDate startDate) {
        DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
        for (WTABaseRuleTemplate ruleTemplate : WTARuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                    validateRuleTemplate(vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate());
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate()));
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds()));
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap,  asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds()));
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    interval = interval.addInterval(getIntervalByWTACareDaysRuleTemplate(startDate, wtaForCareDays,activityWrapperMap));
                   break;
            }
        }
        return interval;
    }

    public Set<BigInteger> getActivityIdsByRuletemplates(List<WTABaseRuleTemplate> WTARuleTemplates){
        Set<BigInteger> activityIds = new HashSet<>();
        for (WTABaseRuleTemplate ruleTemplate : WTARuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                    activityIds.add(vetoAndStopBricksWTATemplate.getStopBrickActivityId());
                    activityIds.add(vetoAndStopBricksWTATemplate.getVetoActivityId());
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    activityIds.addAll(seniorDaysPerYearWTATemplate.getActivityIds());
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    activityIds.addAll(childCareDaysCheckWTATemplate.getActivityIds());
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    activityIds.addAll(wtaForCareDays.getCareDayCounts().stream().map(activityCareDayCount -> activityCareDayCount.getActivityId()).collect(Collectors.toSet()));
                    break;
            }
        }
        return activityIds;
    }

    public WorkTimeAgreementBalance getWorktimeAgreementBalance(Long unitId,Long unitPositionId,LocalDate startDate,LocalDate endDate){
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByUnitPositionId(unitId,startDate,ORGANIZATION,unitPositionId,new HashSet<>());
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException("message.staff.notfound");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.actionNotPermittedException("message.unit.position");
        }
        if(!staffAdditionalInfoDTO.getUnitPosition().isPublished()){
            exceptionService.invalidRequestException("message.shift.not.published");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException("message.staff.unit", staffAdditionalInfoDTO.getId(), unitId);
        }
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDates(unitPositionId,asDate(startDate),asDate(endDate));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).collect(Collectors.toList());
        Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates);
        List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger,ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k->k.getActivity().getId(),v->v));
        DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(activityWrapperMap,wtaBaseRuleTemplates,startDate);
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPositionAndByactivityIds(unitPositionId,dateTimeInterval.getStartDate(),dateTimeInterval.getEndDate(),activityIds);
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances = new ArrayList<>();
        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                    workTimeAgreementRuleTemplateBalances.add(getVetoRuleTemplateBalance(vetoAndStopBricksWTATemplate,shiftWithActivityDTOS,activityWrapperMap,startDate,endDate));
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    workTimeAgreementRuleTemplateBalances.add(getseniorDayRuleTemplateBalance(seniorDaysPerYearWTATemplate,shiftWithActivityDTOS,activityWrapperMap,startDate,endDate,staffAdditionalInfoDTO.getStaffAge()));
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    workTimeAgreementRuleTemplateBalances.add(getchildCareDayRuleTemplateBalance(childCareDaysCheckWTATemplate,shiftWithActivityDTOS,activityWrapperMap,startDate,endDate,staffAdditionalInfoDTO.getStaffAge()));
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    workTimeAgreementRuleTemplateBalances.add(getWtaForCareDayRuleTemplateBalance(wtaForCareDays,shiftWithActivityDTOS,activityWrapperMap,startDate,endDate));
                    break;
            }
        }
    }


    private WorkTimeAgreementRuleTemplateBalancesDTO getVetoRuleTemplateBalance(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate,List<ShiftWithActivityDTO> shiftWithActivityDTOS,Map<BigInteger,ActivityWrapper> activityWrapperMap,LocalDate startDate,LocalDate endDate){
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        while (startDate.isBefore(endDate) || startDate.equals(endDate)){
            if(!containsInInterval(intervalBalances,startDate)){
                DateTimeInterval dateTimeInterval = getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate())
                float scheduledActivityCount = 0;
                for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
                    for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                        if((activity.getStartLocalDate().equals(dateTimeInterval.getStartLocalDate()) || dateTimeInterval.contains(activity.getStartDate()))){
                            if(vetoAndStopBricksWTATemplate.getStopBrickActivityId().equals(activity.getActivityId())) {
                                scheduledActivityCount = scheduledActivityCount + 0.5f;
                            }else if(vetoAndStopBricksWTATemplate.getVetoActivityId().equals(activity.getActivityId())){
                                scheduledActivityCount++;
                            }
                        }
                    }
                }
                intervalBalances.add(new IntervalBalance(vetoAndStopBricksWTATemplate.getTotalBlockingPoints(),scheduledActivityCount,vetoAndStopBricksWTATemplate.getTotalBlockingPoints()-scheduledActivityCount,dateTimeInterval.getStartLocalDate(),dateTimeInterval.getEndLocalDate()))
            }
            startDate = startDate.plusDays(1);
        }
        return new WorkTimeAgreementRuleTemplateBalancesDTO("","",intervalBalances);
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getseniorDayRuleTemplateBalance(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate,List<ShiftWithActivityDTO> shiftWithActivityDTOS,Map<BigInteger,ActivityWrapper> activityWrapperMap,LocalDate startDate,LocalDate endDate,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        while (startDate.isBefore(endDate) || startDate.equals(endDate)){
            if(!containsInInterval(intervalBalances,startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds());
                float scheduledActivityCount = getShiftsActivityCountByInterval(dateTimeInterval,shiftWithActivityDTOS,new HashSet<>(seniorDaysPerYearWTATemplate.getActivityIds()));
                CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                intervalBalances.add(new IntervalBalance(careDays.getLeavesAllowed(),scheduledActivityCount,careDays.getLeavesAllowed() - scheduledActivityCount,dateTimeInterval.getStartLocalDate(),dateTimeInterval.getEndLocalDate()));
            }
        }
        return new WorkTimeAgreementRuleTemplateBalancesDTO("","",intervalBalances);
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getchildCareDayRuleTemplateBalance(ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate,List<ShiftWithActivityDTO> shiftWithActivityDTOS,Map<BigInteger,ActivityWrapper> activityWrapperMap,LocalDate startDate,LocalDate endDate,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        while (startDate.isBefore(endDate) || startDate.equals(endDate)){
            if(!containsInInterval(intervalBalances,startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds());
                float scheduledActivityCount = getShiftsActivityCountByInterval(dateTimeInterval,shiftWithActivityDTOS,newHashSet(childCareDaysCheckWTATemplate.getActivityIds().toArray()));
                CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getStaffAge());
                intervalBalances.add(new IntervalBalance(careDays.getLeavesAllowed(),scheduledActivityCount,careDays.getLeavesAllowed() - scheduledActivityCount,dateTimeInterval.getStartLocalDate(),dateTimeInterval.getEndLocalDate()));
            }
        }
        return new WorkTimeAgreementRuleTemplateBalancesDTO("","",intervalBalances);
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getWtaForCareDayRuleTemplateBalance(WTAForCareDays wtaForCareDays,List<ShiftWithActivityDTO> shiftWithActivityDTOS,Map<BigInteger,ActivityWrapper> activityWrapperMap,LocalDate startDate,LocalDate endDate){
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        while (startDate.isBefore(endDate) || startDate.equals(endDate)){
            if(!containsInInterval(intervalBalances,startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByWTACareDaysRuleTemplate(startDate, wtaForCareDays, activityWrapperMap);
                float scheduledActivityCount = getShiftsActivityCountByInterval(dateTimeInterval,shiftWithActivityDTOS,newHashSet(wtaForCareDays.getCareDayCounts().get(0).getActivityId()));
            }
        }
        return new WorkTimeAgreementRuleTemplateBalancesDTO("","",intervalBalances);
    }

    private int getShiftsActivityCountByInterval(DateTimeInterval dateTimeInterval,List<ShiftWithActivityDTO> shiftWithActivityDTOS,Set<BigInteger> activityIds){
        int activityCount = 0;
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                if((activity.getStartLocalDate().equals(dateTimeInterval.getStartLocalDate()) || dateTimeInterval.contains(activity.getStartDate())) && activityIds.contains(activity.getActivityId())){
                    activityCount++;
                }
            }
        }
        return activityCount;
    }




    private boolean containsInInterval(List<IntervalBalance> intervalBalances,LocalDate startDate){
        boolean contains = false;
        for (IntervalBalance intervalBalance : intervalBalances) {
            if(intervalBalance.getStartDate().equals(startDate) || intervalBalance.getEndDate().equals(startDate) || (intervalBalance.getStartDate().isBefore(startDate) && intervalBalance.getEndDate().isAfter(startDate))){
                contains = true;
            }
        }
        return false;
    }

    public static DateTimeInterval getIntervalByWTACareDaysRuleTemplate(LocalDate startDate, WTAForCareDays wtaForCareDays,Map<BigInteger,ActivityWrapper> activityWrapperMap) {
        LocalDate shiftDate = startDate;
        DateTimeInterval dateTimeInterval = new DateTimeInterval(asDate(startDate), asDate(startDate.plusDays(1)));
        if (isCollectionNotEmpty(wtaForCareDays.getCareDayCounts()) && activityWrapperMap.containsKey(wtaForCareDays.getCareDayCounts().get(0).getActivityId())) {
                Optional<CutOffInterval> cutOffIntervalOptional = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId()).getActivity().getRulesActivityTab().getCutOffIntervals().stream().filter(interval -> ((interval.getStartDate().isBefore(shiftDate) || interval.getStartDate().isEqual(shiftDate)) && (interval.getEndDate().isAfter(shiftDate) || interval.getEndDate().isEqual(shiftDate)))).findAny();
                if (cutOffIntervalOptional.isPresent()) {
                    CutOffInterval cutOffInterval = cutOffIntervalOptional.get();
                    dateTimeInterval = dateTimeInterval.addInterval(new DateTimeInterval(DateUtils.asDate(cutOffInterval.getStartDate()), DateUtils.asDate(cutOffInterval.getEndDate().plusDays(1))));
                }
        }
        return dateTimeInterval;
    }


}
