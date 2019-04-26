package com.kairos.service.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementRuleTemplateBalancesDTO;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.ChildCareDaysCheckWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.SeniorDaysPerYearWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.VetoAndStopBricksWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.WTAForCareDays;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
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
import static com.kairos.constants.AppConstants.STOP_BRICK_BLOCKING_POINT;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


@Service
public class WorkTimeAgreementBalancesCalculationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkTimeAgreementBalancesCalculationService.class);

    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;


    public DateTimeInterval getIntervalByRuletemplates(Map<BigInteger, ActivityWrapper> activityWrapperMap, List<WTABaseRuleTemplate> WTARuleTemplates, LocalDate startDate, LocalDate planningPeriodEndDate) {
        DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
        for (WTABaseRuleTemplate ruleTemplate : WTARuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                    validateRuleTemplate(vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate());
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate(), planningPeriodEndDate));
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate));
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate));
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    interval = interval.addInterval(getIntervalByWTACareDaysRuleTemplate(startDate, wtaForCareDays, activityWrapperMap, planningPeriodEndDate));
                    break;
            }
        }
        return interval;
    }

    public Set<BigInteger> getActivityIdsByRuletemplates(List<WTABaseRuleTemplate> WTARuleTemplates) {
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

    public WorkTimeAgreementBalance getWorktimeAgreementBalance(Long unitId, Long unitPositionId, LocalDate startDate, LocalDate endDate) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByUnitPositionId(unitId, startDate, ORGANIZATION, unitPositionId, new HashSet<>());
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException("message.staff.notfound");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.actionNotPermittedException("message.unit.position");
        }
        if (!staffAdditionalInfoDTO.getUnitPosition().isPublished()) {
            exceptionService.invalidRequestException("message.shift.not.published");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException("message.staff.unit", staffAdditionalInfoDTO.getId(), unitId);
        }
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDates(unitPositionId, asDate(startDate), asDate(endDate));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).collect(Collectors.toList());
        Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates);
        List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);
        DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(activityWrapperMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndDate());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPositionAndByactivityIds(unitPositionId, dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
        Set<BigInteger> timeTypeIds = activityWrappers.stream().map(activityWrapper -> activityWrapper.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()).collect(Collectors.toSet());
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByTimeTypeIds(timeTypeIds);
        Map<BigInteger, TimeType> timeTypeMap = timeTypes.stream().collect(Collectors.toMap(TimeType::getId, v -> v));
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        WorkTimeAgreementBalance workTimeAgreementBalance = new WorkTimeAgreementBalance(workTimeAgreementRuleTemplateBalances);

        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                    workTimeAgreementRuleTemplateBalancesDTO = getVetoRuleTemplateBalance(vetoAndStopBricksWTATemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, timeTypeMap, planningPeriod.getEndDate());
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    workTimeAgreementRuleTemplateBalancesDTO = getseniorDayRuleTemplateBalance(seniorDaysPerYearWTATemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, timeTypeMap, planningPeriod.getEndDate());
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    workTimeAgreementRuleTemplateBalancesDTO = getchildCareDayRuleTemplateBalance(childCareDaysCheckWTATemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, timeTypeMap, planningPeriod.getEndDate());
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    workTimeAgreementRuleTemplateBalancesDTO = getWtaForCareDayRuleTemplateBalance(wtaForCareDays, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, timeTypeMap, planningPeriod.getEndDate());
                    break;
                default:
                    workTimeAgreementRuleTemplateBalancesDTO = null;
                    break;
            }
            if (isNotNull(workTimeAgreementRuleTemplateBalancesDTO)) {
                workTimeAgreementRuleTemplateBalances.add(workTimeAgreementRuleTemplateBalancesDTO);
            }
        }
        return workTimeAgreementBalance;
    }


    private WorkTimeAgreementRuleTemplateBalancesDTO getVetoRuleTemplateBalance(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        if (isNotNull(vetoAndStopBricksWTATemplate.getVetoActivityId()) && isNotNull(vetoAndStopBricksWTATemplate.getStopBrickActivityId())) {
            Activity activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getVetoActivityId()).getActivity();
            if (isNull(activity)) {
                activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getStopBrickActivityId()).getActivity();
            }
            activityName = activity.getName();
            timetypeColor = timeTypeMap.get(activity.getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (startDate.isBefore(vetoAndStopBricksWTATemplate.getValidationStartDate())) {
                    startDate = startDate.plusDays(1);
                    continue;
                }
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate(), planningPeriodEndDate);
                    float scheduledActivityCount = 0;
                    float approveActivityCount = 0;
                    for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
                        for (ShiftActivityDTO shiftActivityDTO : shiftWithActivityDTO.getActivities()) {
                            if ((shiftActivityDTO.getStartLocalDate().equals(dateTimeInterval.getStartLocalDate()) || dateTimeInterval.contains(shiftActivityDTO.getStartDate()))) {
                                if (shiftActivityDTO.getActivityId().equals(vetoAndStopBricksWTATemplate.getStopBrickActivityId())) {
                                    scheduledActivityCount = scheduledActivityCount + STOP_BRICK_BLOCKING_POINT;
                                    if(shiftActivityDTO.getStatus().contains(ShiftStatus.APPROVE)){
                                        approveActivityCount = approveActivityCount + STOP_BRICK_BLOCKING_POINT;
                                    }
                                } else if (shiftActivityDTO.getActivityId().equals(vetoAndStopBricksWTATemplate.getVetoActivityId())) {
                                    scheduledActivityCount++;
                                    if (shiftActivityDTO.getStatus().contains(ShiftStatus.APPROVE)) {
                                        approveActivityCount++;
                                    }
                                }
                            }
                        }
                    }
                    intervalBalances.add(new IntervalBalance(vetoAndStopBricksWTATemplate.getTotalBlockingPoints(), scheduledActivityCount, vetoAndStopBricksWTATemplate.getTotalBlockingPoints() - scheduledActivityCount, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), approveActivityCount));
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityName, timetypeColor, intervalBalances, CutOffIntervalUnit.WEEKS);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }


    private WorkTimeAgreementRuleTemplateBalancesDTO getseniorDayRuleTemplateBalance(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(seniorDaysPerYearWTATemplate.getActivityIds())) {
            Activity activity = activityWrapperMap.get(seniorDaysPerYearWTATemplate.getActivityIds().get(0)).getActivity();
            activityName = activity.getName();
            cutOffIntervalUnit = activity.getRulesActivityTab().getCutOffIntervalUnit();
            timetypeColor = timeTypeMap.get(activity.getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate);
                    if (isNotNull(dateTimeInterval)) {
                        int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, new HashSet<>(seniorDaysPerYearWTATemplate.getActivityIds()));
                        CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                        if (isNotNull(careDays)) {
                            intervalBalances.add(new IntervalBalance(careDays.getLeavesAllowed(), scheduledAndApproveActivityCount[0], careDays.getLeavesAllowed() - scheduledAndApproveActivityCount[0], dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                        }
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityName, timetypeColor, intervalBalances, cutOffIntervalUnit);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getchildCareDayRuleTemplateBalance(ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(childCareDaysCheckWTATemplate.getActivityIds())) {
            Activity activity = activityWrapperMap.get(childCareDaysCheckWTATemplate.getActivityIds().get(0)).getActivity();
            activityName = activity.getName();
            timetypeColor = timeTypeMap.get(activity.getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor();
            cutOffIntervalUnit = activity.getRulesActivityTab().getCutOffIntervalUnit();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate);
                    if (isNotNull(dateTimeInterval)) {
                        int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, new HashSet(childCareDaysCheckWTATemplate.getActivityIds()));
                        CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getStaffAge());
                        if (isNotNull(careDays)) {
                            intervalBalances.add(new IntervalBalance(careDays.getLeavesAllowed(), scheduledAndApproveActivityCount[0], careDays.getLeavesAllowed() - scheduledAndApproveActivityCount[0], dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                        }
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityName, timetypeColor, intervalBalances, cutOffIntervalUnit);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getWtaForCareDayRuleTemplateBalance(WTAForCareDays wtaForCareDays, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(wtaForCareDays.getCareDayCounts())) {
            Activity activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId()).getActivity();
            activityName = activity.getName();
            cutOffIntervalUnit = activity.getRulesActivityTab().getCutOffIntervalUnit();
            timetypeColor = timeTypeMap.get(activity.getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), Arrays.asList(wtaForCareDays.getCareDayCounts().get(0).getActivityId()), planningPeriodEndDate);
                    if (isNotNull(dateTimeInterval)) {
                        int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, newHashSet(wtaForCareDays.getCareDayCounts().get(0).getActivityId()));
                        intervalBalances.add(new IntervalBalance(wtaForCareDays.getCareDayCounts().get(0).getCount(), scheduledAndApproveActivityCount[0], wtaForCareDays.getCareDayCounts().get(0).getCount() - scheduledAndApproveActivityCount[0], dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityName, timetypeColor, intervalBalances, cutOffIntervalUnit);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private int[] getShiftsActivityCountByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Set<BigInteger> activityIds) {
        int activityCount = 0;
        int approveCount = 0;
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                if ((dateTimeInterval.contains(activity.getStartDate())) && activityIds.contains(activity.getActivityId())) {
                    activityCount++;
                    if (activity.getStatus().contains(ShiftStatus.APPROVE)) {
                        approveCount++;
                    }
                }
            }
        }
        return new int[]{activityCount, approveCount};
    }


    private boolean containsInInterval(List<IntervalBalance> intervalBalances, LocalDate startDate) {
        boolean contains = false;
        for (IntervalBalance intervalBalance : intervalBalances) {
            if (intervalBalance.getStartDate().equals(startDate) || intervalBalance.getEndDate().equals(startDate) || (intervalBalance.getStartDate().isBefore(startDate) && intervalBalance.getEndDate().isAfter(startDate))) {
                contains = true;
            }
        }
        return contains;
    }

    public static DateTimeInterval getIntervalByWTACareDaysRuleTemplate(LocalDate startDate, WTAForCareDays wtaForCareDays, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate planningPeriodEndDate) {
        DateTimeInterval dateTimeInterval = new DateTimeInterval(asDate(startDate), asDate(startDate.plusDays(1)));
        if (isCollectionNotEmpty(wtaForCareDays.getCareDayCounts()) && activityWrapperMap.containsKey(wtaForCareDays.getCareDayCounts().get(0).getActivityId())) {
            Activity activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId()).getActivity();
            dateTimeInterval = getCutoffInterval(activity.getRulesActivityTab().getCutOffStartFrom(), activity.getRulesActivityTab().getCutOffIntervalUnit(), activity.getRulesActivityTab().getCutOffdayValue(), asDate(startDate), planningPeriodEndDate);
        }
        return dateTimeInterval;
    }


    public static DateTimeInterval getIntervalByActivity(Map<BigInteger, ActivityWrapper> activityWrapperMap, Date shiftStartDate, List<BigInteger> activityIds, LocalDate planningPeriodEndDate) {
        DateTimeInterval dateTimeInterval = null;
        for (BigInteger activityId : activityIds) {
            if (activityWrapperMap.containsKey(activityId)) {
                Activity activity = activityWrapperMap.get(activityId).getActivity();
                dateTimeInterval = getCutoffInterval(activity.getRulesActivityTab().getCutOffStartFrom(), activity.getRulesActivityTab().getCutOffIntervalUnit(), activity.getRulesActivityTab().getCutOffdayValue(), shiftStartDate, planningPeriodEndDate);
            }
        }
        return dateTimeInterval;
    }


    public static DateTimeInterval getCutoffInterval(LocalDate dateFrom, CutOffIntervalUnit cutOffIntervalUnit, Integer dayValue, Date shiftDate, LocalDate planningPeriodEndDate) {
        LocalDate startDate = dateFrom;
        DateTimeInterval dateTimeInterval = null;
        LocalDate endDate = planningPeriodEndDate;
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            LocalDate nextEndDate = startDate;
            switch (cutOffIntervalUnit) {
                case DAYS:
                    nextEndDate = startDate.plusDays(dayValue - 1);
                    break;
                case HALF_YEARLY:
                    nextEndDate = startDate.plusMonths(6);
                    break;
                case WEEKS:
                    nextEndDate = startDate.plusWeeks(1);
                    break;
                case MONTHS:
                    nextEndDate = startDate.plusMonths(1);
                    break;
                case QUARTERS:
                    nextEndDate = startDate.plusMonths(3);
                    break;
                case YEARS:
                    nextEndDate = startDate.plusYears(1);
                    break;
            }

            if (new DateTimeInterval(startDate, nextEndDate).contains(shiftDate)) {
                dateTimeInterval = new DateTimeInterval(startDate, nextEndDate);
                break;
            }
            startDate = nextEndDate;
        }
        return dateTimeInterval;
    }


}
