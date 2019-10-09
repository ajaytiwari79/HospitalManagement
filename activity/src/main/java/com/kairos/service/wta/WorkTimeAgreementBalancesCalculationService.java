package com.kairos.service.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.ProtectedDaysOffSetting;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementRuleTemplateBalancesDTO;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.constants.AppConstants.STOP_BRICK_BLOCKING_POINT;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.UNUSED_DAYOFF_LEAVES;
import static com.kairos.enums.cta.AccountType.PAID_OUT;
import static com.kairos.enums.cta.AccountType.TIMEBANK_ACCOUNT;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;


@Service
public class WorkTimeAgreementBalancesCalculationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkTimeAgreementBalancesCalculationService.class);

    @Inject
    private TimeBankRepository timeBankRepository;
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
    @Inject
    private ProtectedDaysOffService protectedDaysOffService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private WorkingTimeAgreementMongoRepository wtaRepository;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private PayOutService payOutService;



    public DateTimeInterval getIntervalByRuletemplates(Map<BigInteger, ActivityWrapper> activityWrapperMap, List<WTABaseRuleTemplate> WTARuleTemplates, LocalDate startDate, LocalDate planningPeriodEndDate, Long unitId) {
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
                case PROTECTED_DAYS_OFF:
                    ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplate) ruleTemplate;
                    ProtectedDaysOffSettingDTO protectedDaysOffSetting = protectedDaysOffService.getProtectedDaysOffByUnitId(unitId);
                    interval = interval.addInterval(getIntervalByProtectedDaysOffRuleTemplate(startDate, protectedDaysOffWTATemplate, activityWrapperMap, protectedDaysOffSetting, planningPeriodEndDate));
                default:
                    break;
            }
        }
        return interval;
    }

    public static DateTimeInterval getIntervalByProtectedDaysOffRuleTemplate(LocalDate startDate, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, Map<BigInteger, ActivityWrapper> activityWrapperMap, ProtectedDaysOffSettingDTO protectedDaysOffSetting, LocalDate planningPeriodEndDate) {
        ActivityWrapper activityWrapper = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
        DateTimeInterval dateTimeInterval = getCutoffInterval(activityWrapper.getActivity().getRulesActivityTab().getCutOffStartFrom(), activityWrapper.getActivity().getRulesActivityTab().getCutOffIntervalUnit(), activityWrapper.getActivity().getRulesActivityTab().getCutOffdayValue(), asDate(startDate), ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffSetting.getProtectedDaysOffUnitSettings()) ? planningPeriodEndDate : DateUtils.getLocalDate());
        return dateTimeInterval;
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
                case PROTECTED_DAYS_OFF:
                    ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplate) ruleTemplate;
                    activityIds.add(protectedDaysOffWTATemplate.getActivityId());
                default:
                    break;
            }
        }
        return activityIds;
    }

    public WorkTimeAgreementBalance getWorktimeAgreementBalance(Long unitId, Long employmentId, LocalDate startDate, LocalDate endDate) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByEmploymentId(unitId, startDate, ORGANIZATION, employmentId, new HashSet<>());
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_NOTFOUND);
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_ABSENT);
        }
        if (!staffAdditionalInfoDTO.getEmployment().isPublished()) {
            exceptionService.invalidRequestException("message.shift.not.published");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_UNIT, staffAdditionalInfoDTO.getId(), unitId);
        }
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDates(employmentId, asDate(startDate), asDate(endDate));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).collect(Collectors.toList());
        Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates);
        List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);
        DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(activityWrapperMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndDate(), unitId);
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentAndActivityIds(employmentId, dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
        Set<BigInteger> timeTypeIds = activityWrappers.stream().map(activityWrapper -> activityWrapper.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()).collect(Collectors.toSet());
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByTimeTypeIds(timeTypeIds);
        Map<BigInteger, TimeType> timeTypeMap = timeTypes.stream().collect(Collectors.toMap(TimeType::getId, v -> v));
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO;
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
                case PROTECTED_DAYS_OFF:
                    ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplate) ruleTemplate;
                    workTimeAgreementRuleTemplateBalancesDTO = getProtectedDaysOffBalance(unitId, protectedDaysOffWTATemplate, shiftWithActivityDTOS, activityWrapperMap, timeTypeMap, staffAdditionalInfoDTO, startDate, endDate, planningPeriod.getEndDate());
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


    public WorkTimeAgreementRuleTemplateBalancesDTO getProtectedDaysOffBalance(Long unitId, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, Map<BigInteger, TimeType> timeTypeMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate) {
        ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit = protectedDaysOffService.getProtectedDaysOffByUnitId(unitId);
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        if (!ProtectedDaysOffUnitSettings.UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR.equals(protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings())) {
            ActivityWrapper activityWrapper = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
            CutOffIntervalUnit cutOffIntervalUnit = activityWrapper.getActivity().getRulesActivityTab().getCutOffIntervalUnit();
            List<ProtectedDaysOffSetting> protectedDaysOffSettings = staffAdditionalInfoDTO.getEmployment().getExpertise().getProtectedDaysOffSettings();
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.getPublicHolidayDate().isAfter(staffAdditionalInfoDTO.getEmployment().getStartDate())).collect(Collectors.toList());
            String activityName = activityWrapper.getActivity().getName();
            String timetypeColor = timeTypeMap.containsKey(activityWrapper.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) ? timeTypeMap.get(activityWrapper.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor() : "";
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getCutoffInterval(activityWrapper.getActivity().getRulesActivityTab().getCutOffStartFrom(), activityWrapper.getActivity().getRulesActivityTab().getCutOffIntervalUnit(), activityWrapper.getActivity().getRulesActivityTab().getCutOffdayValue(), asDate(startDate), planningPeriodEndDate);
                    if (isNotNull(dateTimeInterval)) {
                        Object[] countAndDate = getProtectedDaysOffCountAndDate(protectedDaysOffSettings, dateTimeInterval, protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings(), cutOffIntervalUnit, activityWrapper.getActivity().getRulesActivityTab().getCutOffdayValue(), startDate);
                        long count = Long.valueOf(countAndDate[0].toString());
                        LocalDate protectedStartDate = (LocalDate) countAndDate[1];
                        if (isNotNull(protectedStartDate)) {
                            shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentAndActivityIds(staffAdditionalInfoDTO.getEmployment().getId(), asDate(protectedStartDate), dateTimeInterval.getEndDate(), newHashSet(protectedDaysOffWTATemplate.getActivityId()));
                        }
                        int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, newHashSet(protectedDaysOffWTATemplate.getActivityId()));
                        intervalBalances.add(new IntervalBalance(count, count > 0 ? scheduledAndApproveActivityCount[0] : count, (count - scheduledAndApproveActivityCount[0]) > 0 ? count - scheduledAndApproveActivityCount[0] : 0, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), count > 0 ? scheduledAndApproveActivityCount[1] : count));
                    }
                }
                startDate = startDate.plusDays(1);
            }
            if (isCollectionNotEmpty(intervalBalances)) {
                workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityName, timetypeColor, intervalBalances, cutOffIntervalUnit);
            }
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    public Object[] getProtectedDaysOffCountAndDate(List<ProtectedDaysOffSetting> protectedDaysOffSettings, DateTimeInterval dateTimeInterval, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings, CutOffIntervalUnit cutOffIntervalUnit, Integer cutOffdayValue, LocalDate startDate) {
        long count;
        LocalDate protectedDaysOfDate = null;
        if (ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffUnitSettings)) {
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && dateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).collect(Collectors.toList());
            count=protectedDaysOffSettings.size();
        } else {
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && protectedDaysOffSetting.getPublicHolidayDate().isBefore(startDate) && isNotNull(getCutoffInterval(protectedDaysOffSetting.getPublicHolidayDate(), cutOffIntervalUnit, cutOffdayValue, asDate(startDate), protectedDaysOffSetting.getPublicHolidayDate().plusDays(1)))).collect(Collectors.toList());
            count = protectedDaysOffSettings.size();
            protectedDaysOffSettings.sort((protectedDaysOffSetting, t1) -> protectedDaysOffSetting.getPublicHolidayDate().compareTo(t1.getPublicHolidayDate()));
            protectedDaysOfDate = isCollectionNotEmpty(protectedDaysOffSettings) ? protectedDaysOffSettings.get(0).getPublicHolidayDate() : protectedDaysOfDate;
        }
        return new Object[]{count, protectedDaysOfDate , protectedDaysOffSettings};
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
                                    if (shiftActivityDTO.getStatus().contains(ShiftStatus.APPROVE)) {
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

    public int[] getShiftsActivityCountByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Set<BigInteger> activityIds) {
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
                default:
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


    public boolean setProtectedDaysOffHoursViaJob() {
        List<DailyTimeBankEntry> dailyTimeBankEntriesToSave = new ArrayList<>();
        List<PayOutPerShift> payOutOfStaffs=new ArrayList<>();
        Date startDate = asDate(getLocalDate());
        Date endDate = asDate(getLocalDate());
        Object[] dailyTimeBankAndPayoutByOnceInAYear;
        List<StaffEmploymentDetails> staffEmploymentDetails = userIntegrationService.getStaffsMainEmployment();
        Set<Long> unitIds = staffEmploymentDetails.stream().map(staffEmploymentDetail -> staffEmploymentDetail.getUnitId()).collect(Collectors.toSet());
        List<ProtectedDaysOffSettingDTO> protectedDaysOffSettingOfUnit = protectedDaysOffService.getAllProtectedDaysOffByUnitIds(new ArrayList<>(unitIds));
        Map<Long, ProtectedDaysOffSettingDTO> unitIdAndProtectedDaysOffSettingDTOMap = protectedDaysOffSettingOfUnit.stream().collect(Collectors.toMap(k -> k.getUnitId(), v -> v));
        Map<Long, List<StaffEmploymentDetails>> unitAndStaffEmploymentDetailsMap = staffEmploymentDetails.stream().collect(groupingBy(StaffEmploymentDetails::getUnitId));
        Set<Long> employmentIds = staffEmploymentDetails.stream().map(staffEmploymentDetail -> staffEmploymentDetail.getId()).collect(toSet());
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankService.findAllByEmploymentIdsAndBetweenDate(employmentIds, startDate, endDate);
        Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k -> k.getEmploymentId(), v -> v));
        List<Activity> activities = activityMongoRepository.findAllByUnitIdsAndSecondLevelTimeType(TimeTypeEnum.PROTECTED_DAYS_OFF,unitIds);
        Set<BigInteger> activityIds = activities.stream().map(activity -> activity.getId()).collect(Collectors.toSet());
        Map<BigInteger, Activity> activityMap = activities.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        Map<Long,Activity> unitIdAndActivityMap=activities.stream().collect(Collectors.toMap(k->k.getUnitId(),v->v));
        Map[] activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap = getActivityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap(employmentIds, activityIds, activityMap);
        Map<BigInteger,DateTimeInterval> activityIdDateTimeIntervalMap=activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap[0];
        Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap=activityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap[1];
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByEmploymentIdsAndDate(new ArrayList<>(employmentIds),startDate,endDate);
        Map<Long,List<CTAResponseDTO>> employmentIdAndCtaResponseDTOMap=ctaResponseDTOS.stream().collect(groupingBy(ctaResponseDTO -> ctaResponseDTO.getEmploymentId()));
        for (Long unitId : unitIds) {
            try {
                ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO = unitIdAndProtectedDaysOffSettingDTOMap.get(unitId);
                if (isNotNull(protectedDaysOffSettingDTO) ) {
                    List<StaffEmploymentDetails> staffEmploymentDetailsList = unitAndStaffEmploymentDetailsMap.get(unitId);
                    for (StaffEmploymentDetails employmentDetails : staffEmploymentDetailsList) {
                        try {
                            switch (protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings()) {
                                case UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR:
                                        DateTimeInterval dateTimeInterval = activityIdDateTimeIntervalMap.get(unitIdAndActivityMap.get(unitId).getId());
                                        if (dateTimeInterval.getStartLocalDate().equals(getLocalDate())) {
                                            long count = employmentDetails.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && dateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).count();
                                            DailyTimeBankEntry dailyTimeBankEntry = isNullOrElse(employmentIdAndDailyTimeBankEntryMap.get(employmentDetails.getId()), new DailyTimeBankEntry(employmentDetails.getId(), employmentDetails.getStaffId(), dateTimeInterval.getStartLocalDate()));
                                            int contractualMinutes = timeBankCalculationService.getContractualMinutesByDate(dateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
                                            timeBankCalculationService.resetDailyTimebankEntry(dailyTimeBankEntry,contractualMinutes);
                                            dailyTimeBankEntry.setProtectedDaysOffMinutes(dailyTimeBankEntry.getProtectedDaysOffMinutes() + (int)(count * contractualMinutes));
                                            dailyTimeBankEntriesToSave.add(dailyTimeBankEntry);
                                        }
                                    break;
                                case ONCE_IN_A_YEAR:
                                    dailyTimeBankAndPayoutByOnceInAYear= getDailyTimeBankAndPayoutByOnceInAYear(employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, employmentIdAndCtaResponseDTOMap, unitId, employmentDetails);
                                    dailyTimeBankEntriesToSave.add((DailyTimeBankEntry) dailyTimeBankAndPayoutByOnceInAYear[0]);
                                    payOutOfStaffs.add((PayOutPerShift)dailyTimeBankAndPayoutByOnceInAYear[1]);
                                    break;
                                case ACTIVITY_CUT_OFF_INTERVAL:
                                     dailyTimeBankAndPayoutByOnceInAYear = getDailyTimeBankEntryAndPayoutByCutOffInterval(employmentIdAndCtaResponseDTOMap,employmentIdAndDailyTimeBankEntryMap, unitIdAndActivityMap, activityIdDateTimeIntervalMap, employmentIdAndShiftMap, unitId, employmentDetails);
                                    dailyTimeBankEntriesToSave.add((DailyTimeBankEntry) dailyTimeBankAndPayoutByOnceInAYear[0]);
                                    payOutOfStaffs.add((PayOutPerShift)dailyTimeBankAndPayoutByOnceInAYear[1]);
                                    break;
                                default:
                                    break;
                            }
                        }catch (Exception e){
                            LOGGER.error("error while add protected days off time bank in staff  {} ,\n {}  ",employmentDetails.getStaffId(),e);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("error while add protected days off time bank in unit  {} ,\n {}  ",unitId,e);
                e.printStackTrace();
            }
        }
        if(isCollectionNotEmpty(dailyTimeBankEntriesToSave))  timeBankRepository.saveEntities(dailyTimeBankEntriesToSave);
        if(isCollectionNotEmpty(payOutOfStaffs))  payOutService.savePayout(payOutOfStaffs);
        return true;
    }

    private Object[] getDailyTimeBankEntryAndPayoutByCutOffInterval(Map<Long, List<CTAResponseDTO>> employmentIdAndCtaResponseDTOMap, Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Long unitId, StaffEmploymentDetails employmentDetails) {
        int[] scheduledAndApproveActivityCount;
        Activity activity = unitIdAndActivityMap.get(unitId);
        DateTimeInterval activityDateTimeInterval=activityIdDateTimeIntervalMap.get(unitIdAndActivityMap.get(unitId).getId());
        List<ProtectedDaysOffSetting> protectedDaysOffSettings=new ArrayList<>();
        for (ProtectedDaysOffSetting protectedDaysOffSetting : employmentDetails.getProtectedDaysOffSettings()) {
            DateTimeInterval dateTimeInterval=getCutoffInterval(protectedDaysOffSetting.getPublicHolidayDate(), activity.getRulesActivityTab().getCutOffIntervalUnit(), activity.getRulesActivityTab().getCutOffdayValue(), asDate(getLocalDate().minusDays(1)), protectedDaysOffSetting.getPublicHolidayDate().plusDays(1));
            if(isNotNull(dateTimeInterval) && !dateTimeInterval.getEndLocalDate().isAfter(getLocalDate()) && protectedDaysOffSetting.isProtectedDaysOff() && protectedDaysOffSetting.getPublicHolidayDate().isBefore(getLocalDate()) ){
                protectedDaysOffSettings.add(protectedDaysOffSetting);
            }
        }
        int count = protectedDaysOffSettings.size();
        protectedDaysOffSettings.sort((protectedDaysOffSetting, t1) -> protectedDaysOffSetting.getPublicHolidayDate().compareTo(t1.getPublicHolidayDate()));
        DateTimeInterval protectedDaysDateTimeInterval=new DateTimeInterval(protectedDaysOffSettings.get(0).getPublicHolidayDate(),getLocalDate());
        scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(activityDateTimeInterval, isNotNull(employmentIdAndShiftMap.get(employmentDetails.getId()))?employmentIdAndShiftMap.get(employmentDetails.getId()):new ArrayList<>(), newHashSet(unitIdAndActivityMap.get(unitId).getId()));
        count=count-scheduledAndApproveActivityCount[0];
        DailyTimeBankEntry dailyTimeBankEntry = getDailyTimeBankEntry(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, protectedDaysDateTimeInterval, count);
        PayOutPerShift payOutPerShift=getPayoutData(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, protectedDaysDateTimeInterval, count);
        return new Object[]{dailyTimeBankEntry,payOutPerShift};
    }

    private Object[] getDailyTimeBankAndPayoutByOnceInAYear(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, Activity> unitIdAndActivityMap, Map<BigInteger, DateTimeInterval> activityIdDateTimeIntervalMap, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap, Map<Long, List<CTAResponseDTO>> employmentIdAndCtaResponseDTOMap, Long unitId, StaffEmploymentDetails employmentDetails) {
        int[] scheduledAndApproveActivityCount;
        DateTimeInterval activityDateTimeInterval=activityIdDateTimeIntervalMap.get(unitIdAndActivityMap.get(unitId).getId());
        int count=(int)employmentDetails.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && activityDateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).count();
        scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(activityDateTimeInterval, isNotNull(employmentIdAndShiftMap.get(employmentDetails.getId()))?employmentIdAndShiftMap.get(employmentDetails.getId()):new ArrayList<>(), newHashSet(unitIdAndActivityMap.get(unitId).getId()));
        count=count-scheduledAndApproveActivityCount[0];
        DailyTimeBankEntry dailyTimeBankEntry = getDailyTimeBankEntry(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, activityDateTimeInterval, count);
        PayOutPerShift payOutPerShift=getPayoutData(employmentIdAndDailyTimeBankEntryMap, employmentIdAndCtaResponseDTOMap, employmentDetails, activityDateTimeInterval, count);
        return new Object[]{dailyTimeBankEntry,payOutPerShift};
    }

    private DailyTimeBankEntry getDailyTimeBankEntry(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, List<CTAResponseDTO>> employmentIdAndCtaResponseDTOMap, StaffEmploymentDetails employmentDetails, DateTimeInterval activityDateTimeInterval, int count) {
        DailyTimeBankEntry dailyTimeBankEntry = employmentIdAndDailyTimeBankEntryMap.get(employmentDetails.getId());
        if (isNull(dailyTimeBankEntry)) {
            dailyTimeBankEntry = new DailyTimeBankEntry(employmentDetails.getId(), employmentDetails.getStaffId(), getLocalDate());
            int contractualMinutes = timeBankCalculationService.getContractualMinutesByDate(activityDateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
            timeBankCalculationService.resetDailyTimebankEntry(dailyTimeBankEntry, contractualMinutes);
        }
        List<CTAResponseDTO> ctaResponseDTOs=employmentIdAndCtaResponseDTOMap.get(employmentDetails.getId());
        int contractualMinutes=timeBankCalculationService.getContractualMinutesByDate(activityDateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
        for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOs) {
            int value = 0;
            for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
                if (TIMEBANK_ACCOUNT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                    int bonusByRuletemplate = getBonusOfUnusedDaysOff(activityDateTimeInterval, employmentDetails, contractualMinutes, ruleTemplate);
                    value += (bonusByRuletemplate * count);
                    dailyTimeBankEntry.getTimeBankCTADistributionList().add(new TimeBankCTADistribution(ruleTemplate.getName(), bonusByRuletemplate, ruleTemplate.getId()));
                }
            }
                dailyTimeBankEntry.setPlannedMinutesOfTimebank(dailyTimeBankEntry.getPlannedMinutesOfTimebank() + value);
                dailyTimeBankEntry.setDeltaTimeBankMinutes(dailyTimeBankEntry.getDeltaTimeBankMinutes() + value);
                dailyTimeBankEntry.setProtectedDaysOffMinutes(dailyTimeBankEntry.getProtectedDaysOffMinutes() + value);
                dailyTimeBankEntry.setDeltaAccumulatedTimebankMinutes(dailyTimeBankEntry.getDeltaAccumulatedTimebankMinutes() + dailyTimeBankEntry.getProtectedDaysOffMinutes());
        }
        return dailyTimeBankEntry;
    }

    private PayOutPerShift getPayoutData(Map<Long, DailyTimeBankEntry> employmentIdAndDailyTimeBankEntryMap, Map<Long, List<CTAResponseDTO>> employmentIdAndCtaResponseDTOMap, StaffEmploymentDetails employmentDetails, DateTimeInterval activityDateTimeInterval, int count) {
        PayOutPerShift payOutPerShift = new PayOutPerShift(new BigInteger("-1"), employmentDetails.getId(), employmentDetails.getStaffId(), getLocalDate(), employmentDetails.getUnitId());
        List<CTAResponseDTO> ctaResponseDTOs=employmentIdAndCtaResponseDTOMap.get(employmentDetails.getId());
        int contractualMinutes=timeBankCalculationService.getContractualMinutesByDate(activityDateTimeInterval, getLocalDate(), employmentDetails.getEmploymentLines());
        for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOs) {
            int value = 0;
            for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
                if (PAID_OUT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                    int bonusByRuletemplate = getBonusOfUnusedDaysOff(activityDateTimeInterval, employmentDetails, contractualMinutes, ruleTemplate);
                    value += (bonusByRuletemplate * count);
                    payOutPerShift.getPayOutPerShiftCTADistributions().add(new PayOutPerShiftCTADistribution(ruleTemplate.getName(), bonusByRuletemplate, ruleTemplate.getId()));
                }
            }
                payOutPerShift.setCtaBonusMinutesOfPayOut(value);
                payOutPerShift.setScheduledMinutes(0);
                payOutPerShift.setTotalPayOutMinutes(value);

        }
        return payOutPerShift;
    }

    private Map[] getActivityIdDateTimeIntervalMapAndEmploymentIdAndShiftMap(Set<Long> employmentIds, Set<BigInteger> activityIds, Map<BigInteger, Activity> activityWrapperMap) {
        Map<BigInteger,DateTimeInterval> activityIdDateTimeIntervalMap=new HashMap<>();
        for (BigInteger activityId : activityWrapperMap.keySet()) {
            Activity activityWrapper = activityWrapperMap.get(activityId);
            activityIdDateTimeIntervalMap.putIfAbsent(activityId, getCutoffInterval(activityWrapper.getRulesActivityTab().getCutOffStartFrom(), activityWrapper.getRulesActivityTab().getCutOffIntervalUnit(), activityWrapper.getRulesActivityTab().getCutOffdayValue(), asDate(getLocalDate().minusDays(1)), getLocalDate()));
        }
        List<DateTimeInterval> dateTimeIntervals=new ArrayList<>(activityIdDateTimeIntervalMap.values());
        dateTimeIntervals.sort((dateTimeInterval, t1) -> dateTimeInterval.getStartLocalDate().compareTo(t1.getStartLocalDate()));
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmployments(employmentIds, dateTimeIntervals.get(0).getStartDate(), dateTimeIntervals.get(dateTimeIntervals.size()-1).getEndDate(), activityIds);
        Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftMap = shiftWithActivityDTOS.stream().collect(groupingBy(ShiftWithActivityDTO::getEmploymentId));
        return new Map[]{activityIdDateTimeIntervalMap,employmentIdAndShiftMap};
    }

    private int getBonusOfUnusedDaysOff(DateTimeInterval dateTimeInterval, StaffEmploymentDetails staffEmploymentDetails, int contractualMinutes, CTARuleTemplateDTO ruleTemplate) {
        int value = 0;
        if(ruleTemplate.getCalculationFor().equals(UNUSED_DAYOFF_LEAVES)){
            if(CompensationMeasurementType.FIXED_VALUE.equals(ruleTemplate.getCompensationTable().getUnusedDaysOffType())){
                value+= timeBankCalculationService.getHourlyCostByDate(staffEmploymentDetails.getEmploymentLines(), dateTimeInterval.getStartLocalDate()).equals(new BigDecimal(0)) ? new BigDecimal(ruleTemplate.getCalculateValueAgainst().getFixedValue().getAmount()).divide(staffEmploymentDetails.getHourlyCost(), 6, RoundingMode.HALF_UP).multiply(new BigDecimal(60)).intValue() : 0;
            }else if (CompensationMeasurementType.PERCENT.equals(ruleTemplate.getCompensationTable().getUnusedDaysOffType()) ){
                value+=contractualMinutes * ruleTemplate.getCompensationTable().getUnusedDaysOffvalue()/100;
            }
        }
        return value;
    }

}
