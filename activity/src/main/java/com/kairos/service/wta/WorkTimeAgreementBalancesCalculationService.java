package com.kairos.service.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ProtectedDaysOffSetting;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementRuleTemplateBalancesDTO;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.dto.activity.wta.templates.ActivityCutOffCount;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.KPIService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftValidatorService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import com.kairos.utils.CPRUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.wta.WTATemplateType.*;
import static com.kairos.service.shift.ShiftValidatorService.throwException;
import static com.kairos.service.time_bank.TimeBankCalculationService.isPublicHolidayValid;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


@Service
public class WorkTimeAgreementBalancesCalculationService implements KPIService {

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
    private WorkingTimeAgreementMongoRepository wtaRepository;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateRepository;



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
                    break;
                default:
                    break;
            }
        }
        return interval;
    }

    private DateTimeInterval getIntervalByProtectedDaysOffRuleTemplate(LocalDate startDate, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, Map<BigInteger, ActivityWrapper> activityWrapperMap, ProtectedDaysOffSettingDTO protectedDaysOffSetting, LocalDate planningPeriodEndDate) {
        if(isNull(protectedDaysOffWTATemplate.getActivityId())){
            exceptionService.invalidRequestException(ACTIVITY_NOT_ASSIGN_IN_PROTECTED_DAYS_OFF_RULE_TEMPLATE);
        }
        ActivityWrapper activityWrapper = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
        return getCutoffInterval(activityWrapper.getActivity().getActivityRulesSettings().getCutOffStartFrom(), activityWrapper.getActivity().getActivityRulesSettings().getCutOffIntervalUnit(), activityWrapper.getActivity().getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffSetting.getProtectedDaysOffUnitSettings()) ? planningPeriodEndDate : DateUtils.getLocalDate());
    }

    public Set<BigInteger> getActivityIdsByRuletemplates(List<WTABaseRuleTemplate> WTARuleTemplates, BigInteger activityId) {
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
                    if(isNull(activityId) || seniorDaysPerYearWTATemplate.getActivityIds().contains(activityId)) {
                        activityIds.addAll(seniorDaysPerYearWTATemplate.getActivityIds());
                    }
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    if(isNull(activityId) || childCareDaysCheckWTATemplate.getActivityIds().contains(activityId)) {
                        activityIds.addAll(childCareDaysCheckWTATemplate.getActivityIds());
                    }
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    Set<BigInteger> wtaForCareDayActivityIds = wtaForCareDays.getCareDayCounts().stream().map(activityCareDayCount -> activityCareDayCount.getActivityId()).collect(Collectors.toSet());
                    if(isNull(activityId) || wtaForCareDayActivityIds.contains(activityId)) {
                        activityIds.addAll(wtaForCareDayActivityIds);
                    }
                    break;
                case PROTECTED_DAYS_OFF:
                    ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplate) ruleTemplate;
                    activityIds.add(protectedDaysOffWTATemplate.getActivityId());
                    break;
                default:
                    break;
            }
        }
        return activityIds;
    }

    public WorkTimeAgreementBalance getWorkTimeAgreementBalance(Long unitId, Long employmentId, LocalDate startDate, LocalDate endDate, Set<WTATemplateType> wtaTemplateTypes, BigInteger activityId) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByEmploymentId(unitId, startDate, ORGANIZATION, employmentId, new HashSet<>(),endDate);
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_NOTFOUND);
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_ABSENT);
        }
        if (!staffAdditionalInfoDTO.getEmployment().isPublished() && isCollectionEmpty(wtaTemplateTypes)) {
            exceptionService.invalidRequestException("message.shift.not.published");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_UNIT, staffAdditionalInfoDTO.getId(), unitId);
        }
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDates(employmentId, asDate(startDate), asDate(endDate));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).collect(Collectors.toList());
        Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates, activityId);
        List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);
        DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(activityWrapperMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndDate(), unitId);
        List<ShiftWithActivityDTO> validShiftActivityDTOSByDayType = getShiftWithActivityDTOS(employmentId, staffAdditionalInfoDTO, activityIds, activityWrapperMap, dateTimeInterval);
        Set<BigInteger> timeTypeIds = activityWrappers.stream().map(activityWrapper -> activityWrapper.getActivity().getActivityBalanceSettings().getTimeTypeId()).collect(Collectors.toSet());
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByTimeTypeIds(timeTypeIds);
        Map<BigInteger, TimeType> timeTypeMap = timeTypes.stream().collect(Collectors.toMap(TimeType::getId, v -> v));
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances = getWorkTimeAgreementRuleTemplateBalances(unitId, startDate, endDate, staffAdditionalInfoDTO, wtaBaseRuleTemplates, activityWrapperMap, planningPeriod, validShiftActivityDTOSByDayType, timeTypeMap,wtaTemplateTypes);
        WorkTimeAgreementBalance workTimeAgreementBalance = new WorkTimeAgreementBalance(workTimeAgreementRuleTemplateBalances);
        return workTimeAgreementBalance;
    }

    private List<ShiftWithActivityDTO> getShiftWithActivityDTOS(Long employmentId, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Set<BigInteger> activityIds, Map<BigInteger, ActivityWrapper> activityWrapperMap, DateTimeInterval dateTimeInterval) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentAndActivityIds(employmentId, dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
        List<ShiftWithActivityDTO> validShiftActivityDTOSByDayType =new ArrayList<>();
        Map<Long , DayTypeDTO> dayTypeDTOMap =staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, dayTypeDTO -> dayTypeDTO));
        for(ShiftWithActivityDTO shiftWithActivityDTO :shiftWithActivityDTOS){
            for(ShiftActivityDTO shiftActivityDTO :shiftWithActivityDTO.getActivities()) {
                if (activityWrapperMap.containsKey(shiftActivityDTO.getActivityId()) && isDayTypeValid(shiftWithActivityDTO.getStartDate(), activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity().getActivityTimeCalculationSettings().getDayTypes(),dayTypeDTOMap)) {
                   validShiftActivityDTOSByDayType.add(shiftWithActivityDTO);
                }
            }
        }
        return validShiftActivityDTOSByDayType;
    }


    public List<WorkTimeAgreementRuleTemplateBalancesDTO> getWorkTimeAgreementRuleTemplateBalances(Long unitId, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<WTABaseRuleTemplate> wtaBaseRuleTemplates, Map<BigInteger, ActivityWrapper> activityWrapperMap, PlanningPeriod planningPeriod, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, TimeType> timeTypeMap, Set<WTATemplateType> wtaTemplateTypes) {
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances=new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO;
        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            if(isCollectionEmpty(wtaTemplateTypes) || wtaTemplateTypes.contains(ruleTemplate.getWtaTemplateType())) {
                switch (ruleTemplate.getWtaTemplateType()) {
                    case VETO_AND_STOP_BRICKS:
                        workTimeAgreementRuleTemplateBalancesDTO = getVetoRuleTemplateBalance((VetoAndStopBricksWTATemplate) ruleTemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, timeTypeMap, planningPeriod.getEndDate());
                        break;
                    case SENIOR_DAYS_PER_YEAR:
                        workTimeAgreementRuleTemplateBalancesDTO = getseniorDayRuleTemplateBalance((SeniorDaysPerYearWTATemplate) ruleTemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, timeTypeMap, planningPeriod.getEndDate());
                        break;
                    case CHILD_CARE_DAYS_CHECK:
                        workTimeAgreementRuleTemplateBalancesDTO = getchildCareDayRuleTemplateBalance((ChildCareDaysCheckWTATemplate) ruleTemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, timeTypeMap, planningPeriod.getEndDate());
                        break;
                    case WTA_FOR_CARE_DAYS:
                        workTimeAgreementRuleTemplateBalancesDTO = getWtaForCareDayRuleTemplateBalance((WTAForCareDays) ruleTemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, timeTypeMap, planningPeriod.getEndDate());
                        break;
                    case PROTECTED_DAYS_OFF:
                        workTimeAgreementRuleTemplateBalancesDTO = getProtectedDaysOffBalance(unitId, (ProtectedDaysOffWTATemplate) ruleTemplate, shiftWithActivityDTOS, activityWrapperMap, timeTypeMap, staffAdditionalInfoDTO, startDate, endDate, planningPeriod.getEndDate());
                        break;
                    default:
                        workTimeAgreementRuleTemplateBalancesDTO = null;
                        break;
                }
                if (isNotNull(workTimeAgreementRuleTemplateBalancesDTO)) {
                    workTimeAgreementRuleTemplateBalances.add(workTimeAgreementRuleTemplateBalancesDTO);
                }
            }
        }
        Collections.sort(workTimeAgreementRuleTemplateBalances);
        return workTimeAgreementRuleTemplateBalances;
    }

    public boolean isLeaveCountAvailable(Map<BigInteger, ActivityWrapper> activityWrapperMap, BigInteger activityId, ShiftWithActivityDTO shift, DateTimeInterval dateTimeInterval, LocalDate lastPlanningPeriodEndDat, WTATemplateType wtaTemplateType, long leaveCount,LocalDate endDateOfActivityCutOff) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByEmploymentId(shift.getUnitId(), dateTimeInterval.getStartLocalDate().plusDays(1), ORGANIZATION, shift.getEmploymentId(), new HashSet<>(),endDateOfActivityCutOff);
        boolean isLeaveCountAvailable = false;
        List<WTAQueryResultDTO> workingTimeAgreements = wtaRepository.getWTAByEmploymentIdAndDatesWithRuleTemplateType(shift.getEmploymentId(), shift.getStartDate(), shift.getEndDate(), wtaTemplateType);
        List<WTABaseRuleTemplate> ruleTemplates = workingTimeAgreements.get(0).getRuleTemplates();
        Activity activity = activityWrapperMap.get(activityId).getActivity();
        if (!shift.getStartDate().before(getDate()) && activity.getActivityRulesSettings().isBorrowLeave()) {
            DateTimeInterval nextCutOffdateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(dateTimeInterval.getEndLocalDate().plusDays(1)), newArrayList(activityId), lastPlanningPeriodEndDat);
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentAndActivityIds(shift.getEmploymentId(), DateUtils.asDate(dateTimeInterval.getStart()), DateUtils.asDate(nextCutOffdateTimeInterval.getEnd()), newHashSet(activityId));
            for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
                switch (ruleTemplate.getWtaTemplateType()) {
                    case SENIOR_DAYS_PER_YEAR:
                        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                        isLeaveCountAvailable = leaveAvailableForCareDaysAndSeniorDaysWTA(leaveCount, staffAdditionalInfoDTO, isLeaveCountAvailable, nextCutOffdateTimeInterval, shiftWithActivityDTOS, staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), seniorDaysPerYearWTATemplate.getActivityCutOffCounts());
                        break;
                    case CHILD_CARE_DAYS_CHECK:
                        ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                        isLeaveCountAvailable = leaveAvailableForCareDaysAndSeniorDaysWTA(leaveCount, staffAdditionalInfoDTO, isLeaveCountAvailable, nextCutOffdateTimeInterval, shiftWithActivityDTOS, staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), childCareDaysCheckWTATemplate.getActivityCutOffCounts());
                        break;
                    case WTA_FOR_CARE_DAYS:
                        WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                        ActivityCareDayCount careDayCount = wtaForCareDays.careDaysCountMap().get(activity.getId());
                        isLeaveCountAvailable = leaveAvailableForCareDaysWTA(leaveCount, isLeaveCountAvailable, nextCutOffdateTimeInterval, shiftWithActivityDTOS, careDayCount);
                        break;
                    default:
                        break;
                }
            }
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
        }
        return isLeaveCountAvailable;
    }

    private boolean leaveAvailableForCareDaysAndSeniorDaysWTA(long leaveCount, StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean isLeaveCountAvailable, DateTimeInterval nextCutOffdateTimeInterval, List<ShiftWithActivityDTO> shiftWithActivityDTOS, List<CareDaysDTO> childCareDays, List<ActivityCutOffCount> activityCutOffCounts) {
        CareDaysDTO careDays = getCareDays(childCareDays, staffAdditionalInfoDTO.getStaffAge());
        Optional<ActivityCutOffCount> activityLeaveCountOptional = activityCutOffCounts.stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(nextCutOffdateTimeInterval.getStartLocalDate())).findFirst();
        if (activityLeaveCountOptional.isPresent() && leaveCount + careDays.getLeavesAllowed() + activityLeaveCountOptional.get().getTransferLeaveCount() - activityLeaveCountOptional.get().getBorrowLeaveCount() < shiftWithActivityDTOS.size()) {
            activityLeaveCountOptional.get().setBorrowLeaveCount(activityLeaveCountOptional.get().getBorrowLeaveCount() + 1);
            isLeaveCountAvailable = true;
        }
        return isLeaveCountAvailable;
    }

    private boolean leaveAvailableForCareDaysWTA(long leaveCount, boolean isLeaveCountAvailable, DateTimeInterval nextCutOffdateTimeInterval, List<ShiftWithActivityDTO> shiftWithActivityDTOS, ActivityCareDayCount careDayCount) {
        ActivityCutOffCount activityLeaveCount = careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(nextCutOffdateTimeInterval.getStartLocalDate())).findFirst().get();
        if (leaveCount + activityLeaveCount.getCount() + activityLeaveCount.getTransferLeaveCount() - activityLeaveCount.getBorrowLeaveCount() > shiftWithActivityDTOS.size()) {
            activityLeaveCount.setBorrowLeaveCount(activityLeaveCount.getBorrowLeaveCount() + 1);
            isLeaveCountAvailable = true;
        }
        return isLeaveCountAvailable;
    }

    public WorkTimeAgreementRuleTemplateBalancesDTO getProtectedDaysOffBalance(Long unitId, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, Map<BigInteger, TimeType> timeTypeMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate) {
        ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit = protectedDaysOffService.getProtectedDaysOffByUnitId(unitId);
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        if (!ProtectedDaysOffUnitSettings.UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR.equals(protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings())) {
            ActivityWrapper activityWrapper = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
            CutOffIntervalUnit cutOffIntervalUnit = activityWrapper.getActivity().getActivityRulesSettings().getCutOffIntervalUnit();
            List<ProtectedDaysOffSetting> protectedDaysOffSettings = staffAdditionalInfoDTO.getEmployment().getExpertise().getProtectedDaysOffSettings();
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> isEqualOrAfter(protectedDaysOffSetting.getPublicHolidayDate(),staffAdditionalInfoDTO.getEmployment().getStartDate()) && (isNull(staffAdditionalInfoDTO.getEmployment().getEndDate()) || !protectedDaysOffSetting.getPublicHolidayDate().isAfter(staffAdditionalInfoDTO.getEmployment().getEndDate()))).collect(Collectors.toList());
            String activityName = activityWrapper.getActivity().getName();
            String timetypeColor = timeTypeMap.containsKey(activityWrapper.getActivity().getActivityBalanceSettings().getTimeTypeId()) ? timeTypeMap.get(activityWrapper.getActivity().getActivityBalanceSettings().getTimeTypeId()).getBackgroundColor() : "";
            getProtectedDaysOffIntervalbalance(protectedDaysOffWTATemplate, shiftWithActivityDTOS, staffAdditionalInfoDTO, startDate, endDate, planningPeriodEndDate, protectedDaysOffSettingOfUnit, intervalBalances, activityWrapper, cutOffIntervalUnit, protectedDaysOffSettings);
            if (isCollectionNotEmpty(intervalBalances)) {
                int sequence = activityWrapper.getActivityPriority()==null?Integer.MAX_VALUE:activityWrapper.getActivityPriority().getSequence();
                workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityWrapper.getActivity().getId(), activityName, timetypeColor, intervalBalances, cutOffIntervalUnit, false,activityWrapper.getActivity().getActivityBalanceSettings().getTimeType().toString(),sequence);
            }
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getProtectedDaysOffIntervalbalance(ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate, ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit, List<IntervalBalance> intervalBalances, ActivityWrapper activityWrapper, CutOffIntervalUnit cutOffIntervalUnit, List<ProtectedDaysOffSetting> protectedDaysOffSettings) {
        while (!startDate.isAfter(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getCutoffInterval(activityWrapper.getActivity().getActivityRulesSettings().getCutOffStartFrom(), activityWrapper.getActivity().getActivityRulesSettings().getCutOffIntervalUnit(), activityWrapper.getActivity().getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), planningPeriodEndDate);
                getProtectedDaysOfCountByInterval(protectedDaysOffWTATemplate, shiftWithActivityDTOS, staffAdditionalInfoDTO, startDate, protectedDaysOffSettingOfUnit, intervalBalances, activityWrapper, cutOffIntervalUnit, protectedDaysOffSettings, dateTimeInterval);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private List<ShiftWithActivityDTO> getProtectedDaysOfCountByInterval(ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit, List<IntervalBalance> intervalBalances, ActivityWrapper activityWrapper, CutOffIntervalUnit cutOffIntervalUnit, List<ProtectedDaysOffSetting> protectedDaysOffSettings, DateTimeInterval dateTimeInterval) {
        if (isNotNull(dateTimeInterval)) {
            Object[] countAndDate = getProtectedDaysOffCountAndDate(protectedDaysOffSettings, dateTimeInterval, protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings(), cutOffIntervalUnit, activityWrapper.getActivity().getActivityRulesSettings().getCutOffdayValue(), startDate);
            long count = Long.valueOf(countAndDate[0].toString());
            LocalDate protectedStartDate = (LocalDate) countAndDate[1];
            if (isNotNull(protectedStartDate)) {
                shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentAndActivityIds(staffAdditionalInfoDTO.getEmployment().getId(), asDate(protectedStartDate), dateTimeInterval.getEndDate(), newHashSet(protectedDaysOffWTATemplate.getActivityId()));
            }
            int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, newHashSet(protectedDaysOffWTATemplate.getActivityId()));
            long scheduled = count > 0 ? scheduledAndApproveActivityCount[0] : count;
            long available = (count - scheduledAndApproveActivityCount[0]) > 0 ? count - scheduledAndApproveActivityCount[0]+scheduledAndApproveActivityCount[1] : 0;
            long approved = count > 0 ? scheduledAndApproveActivityCount[1] : count;
                if(count!=0) {
                    intervalBalances.add(new IntervalBalance(count, scheduled, available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), approved));
                }
        }
        return shiftWithActivityDTOS;
    }

    public Object[] getProtectedDaysOffCountAndDate(List<ProtectedDaysOffSetting> protectedDaysOffSettings, DateTimeInterval dateTimeInterval, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings, CutOffIntervalUnit cutOffIntervalUnit, Integer cutOffdayValue, LocalDate startDate) {
        long count;
        LocalDate protectedDaysOfDate = null;
        if (ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffUnitSettings)) {
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && dateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).collect(Collectors.toList());
            count = protectedDaysOffSettings.size();
        } else {
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && protectedDaysOffSetting.getPublicHolidayDate().isBefore(startDate) && isNotNull(getCutoffInterval(protectedDaysOffSetting.getPublicHolidayDate(), cutOffIntervalUnit, cutOffdayValue, asDate(startDate), protectedDaysOffSetting.getPublicHolidayDate().plusDays(1)))).collect(Collectors.toList());
            count = protectedDaysOffSettings.size();
            protectedDaysOffSettings.sort((protectedDaysOffSetting, t1) -> protectedDaysOffSetting.getPublicHolidayDate().compareTo(t1.getPublicHolidayDate()));
            protectedDaysOfDate = isCollectionNotEmpty(protectedDaysOffSettings) ? protectedDaysOffSettings.get(0).getPublicHolidayDate() : protectedDaysOfDate;
        }
        return new Object[]{count, protectedDaysOfDate, protectedDaysOffSettings};
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getVetoRuleTemplateBalance(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        BigInteger activityId = BigInteger.ZERO;
        String timetypeColor = "";
        boolean borrowLeave = false;
        if (isNotNull(vetoAndStopBricksWTATemplate.getVetoActivityId()) && isNotNull(vetoAndStopBricksWTATemplate.getStopBrickActivityId())) {
            Activity activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getVetoActivityId()).getActivity();
            if (isNull(activity)) {
                activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getStopBrickActivityId()).getActivity();
            }
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            timetypeColor = timeTypeMap.get(activity.getActivityBalanceSettings().getTimeTypeId()).getBackgroundColor();
            getVetoIntervalBalance(vetoAndStopBricksWTATemplate, shiftWithActivityDTOS, startDate, endDate, planningPeriodEndDate, intervalBalances);
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            int sequence = activityWrapperMap.get(activityId).getActivityPriority()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getActivityPriority().getSequence();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, timetypeColor, intervalBalances, CutOffIntervalUnit.WEEKS, borrowLeave,activityWrapperMap.get(activityId).getActivity().getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getVetoIntervalBalance(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (startDate.isBefore(vetoAndStopBricksWTATemplate.getValidationStartDate())) {
                startDate = startDate.plusDays(1);
                continue;
            }
            if (!containsInInterval(intervalBalances, startDate)) {
                updateBalanceByInterval(vetoAndStopBricksWTATemplate, shiftWithActivityDTOS, startDate, planningPeriodEndDate, intervalBalances);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private void updateBalanceByInterval(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, LocalDate startDate, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        DateTimeInterval dateTimeInterval = getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate(), planningPeriodEndDate);
        float scheduledActivityCount = 0;
        float approveActivityCount = 0;
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            for (ShiftActivityDTO shiftActivityDTO : shiftWithActivityDTO.getActivities()) {
                if ((shiftActivityDTO.getStartLocalDate().equals(dateTimeInterval.getStartLocalDate()) || dateTimeInterval.contains(shiftActivityDTO.getStartDate()))){
                    float[] approveAndScheduleCount = getCountOfVetoAndStopBricks(shiftActivityDTO,vetoAndStopBricksWTATemplate,shiftWithActivityDTO);
                    approveActivityCount += approveAndScheduleCount[0];
                    scheduledActivityCount += approveAndScheduleCount[1];
                }
            }
        }
        float available = vetoAndStopBricksWTATemplate.getTotalBlockingPoints() - scheduledActivityCount;
        intervalBalances.add(new IntervalBalance(vetoAndStopBricksWTATemplate.getTotalBlockingPoints(), scheduledActivityCount, available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), approveActivityCount));
    }

    private float[] getCountOfVetoAndStopBricks(ShiftActivityDTO shiftActivityDTO, VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate,ShiftWithActivityDTO shiftWithActivityDTO) {
        float scheduledActivityCount = 0;
        float approveActivityCount = 0;
        if (shiftActivityDTO.getActivityId().equals(vetoAndStopBricksWTATemplate.getStopBrickActivityId())) {
            scheduledActivityCount = scheduledActivityCount + STOP_BRICK_BLOCKING_POINT;
            if (shiftActivityDTO.getStatus().contains(ShiftStatus.APPROVE)&&!shiftWithActivityDTO.isDraft()) {
                approveActivityCount = approveActivityCount + STOP_BRICK_BLOCKING_POINT;
            }
        } else if (shiftActivityDTO.getActivityId().equals(vetoAndStopBricksWTATemplate.getVetoActivityId())) {
            scheduledActivityCount++;
            if (shiftActivityDTO.getStatus().contains(ShiftStatus.APPROVE)&&!shiftWithActivityDTO.isDraft()) {
                approveActivityCount++;
            }
        }
        return new float[]{approveActivityCount,scheduledActivityCount};
    }


    private WorkTimeAgreementRuleTemplateBalancesDTO getseniorDayRuleTemplateBalance(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        BigInteger activityId = BigInteger.ZERO;
        boolean borrowLeave = false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(seniorDaysPerYearWTATemplate.getActivityIds()) && activityWrapperMap.containsKey(seniorDaysPerYearWTATemplate.getActivityIds().get(0))) {
            Activity activity = activityWrapperMap.get(seniorDaysPerYearWTATemplate.getActivityIds().get(0)).getActivity();
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            timetypeColor = timeTypeMap.containsKey(activity.getActivityBalanceSettings().getTimeTypeId()) ? timeTypeMap.get(activity.getActivityBalanceSettings().getTimeTypeId()).getBackgroundColor() : "";
            getSeniorDayIntevalBalance(seniorDaysPerYearWTATemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, planningPeriodEndDate, intervalBalances);
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            int sequence = activityWrapperMap.get(activityId).getActivityPriority()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getActivityPriority().getSequence();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, timetypeColor, intervalBalances, cutOffIntervalUnit, borrowLeave,activityWrapperMap.get(activityId).getActivity().getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getSeniorDayIntevalBalance(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate);
                getSeniorDayCountByInterval(seniorDaysPerYearWTATemplate, shiftWithActivityDTOS, startDate, staffAdditionalInfoDTO, intervalBalances, dateTimeInterval);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private void getSeniorDayCountByInterval(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, LocalDate startDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<IntervalBalance> intervalBalances, DateTimeInterval dateTimeInterval) {
        if (isNotNull(dateTimeInterval)) {
            int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, new HashSet<>(seniorDaysPerYearWTATemplate.getActivityIds()));
            CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), CPRUtil.getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(),startDate));
            if (isNotNull(careDays)) {
                ActivityCutOffCount activityLeaveCount = seniorDaysPerYearWTATemplate.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(dateTimeInterval.getStartLocalDate())).findFirst().orElse(new ActivityCutOffCount());
                int total = careDays.getLeavesAllowed() + activityLeaveCount.getTransferLeaveCount();
                int available = (careDays.getLeavesAllowed() + activityLeaveCount.getTransferLeaveCount() - activityLeaveCount.getBorrowLeaveCount()) - (scheduledAndApproveActivityCount[0]+scheduledAndApproveActivityCount[1]);
                if(total !=0) {
                    intervalBalances.add(new IntervalBalance(total, scheduledAndApproveActivityCount[0], available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                }
            }
        }
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getchildCareDayRuleTemplateBalance(ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        BigInteger activityId = BigInteger.ZERO;
        boolean borrowLeave = false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(childCareDaysCheckWTATemplate.getActivityIds()) &&  activityWrapperMap.containsKey(childCareDaysCheckWTATemplate.getActivityIds().get(0))) {
            Activity activity = activityWrapperMap.get(childCareDaysCheckWTATemplate.getActivityIds().get(0)).getActivity();
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            timetypeColor = timeTypeMap.containsKey(activity.getActivityBalanceSettings().getTimeTypeId()) ? timeTypeMap.get(activity.getActivityBalanceSettings().getTimeTypeId()).getBackgroundColor() : "";
            cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            getChildCareIntervalBalance(childCareDaysCheckWTATemplate, shiftWithActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, planningPeriodEndDate, intervalBalances);
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            int sequence = activityWrapperMap.get(activityId).getActivityPriority()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getActivityPriority().getSequence();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, timetypeColor, intervalBalances, cutOffIntervalUnit, borrowLeave,activityWrapperMap.get(activityId).getActivity().getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getChildCareIntervalBalance(ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate);
                if (isNotNull(dateTimeInterval)) {
                    int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, new HashSet(childCareDaysCheckWTATemplate.getActivityIds()));
                    long totalLeaves = childCareDaysCheckWTATemplate.calculateChildCareDaysLeaveCount(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), shiftValidatorService.getChildAges(asDate(startDate), staffAdditionalInfoDTO));
                    ActivityCutOffCount activityLeaveCount = childCareDaysCheckWTATemplate.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(dateTimeInterval.getStartLocalDate())).findFirst().orElse(new ActivityCutOffCount());
                    long total = totalLeaves + activityLeaveCount.getTransferLeaveCount();
                    long available = (totalLeaves + activityLeaveCount.getTransferLeaveCount() - activityLeaveCount.getBorrowLeaveCount()) - (scheduledAndApproveActivityCount[0]+scheduledAndApproveActivityCount[1]);
                    if (total != 0) {
                        intervalBalances.add(new IntervalBalance(total, scheduledAndApproveActivityCount[0], available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                    }
                }
            }
            startDate = startDate.plusDays(1);
        }
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getWtaForCareDayRuleTemplateBalance(WTAForCareDays wtaForCareDays, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        BigInteger activityId = BigInteger.ZERO;
        boolean borrowLeave = false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(wtaForCareDays.getCareDayCounts()) && activityWrapperMap.containsKey(wtaForCareDays.getCareDayCounts().get(0).getActivityId()) ) {
            Activity activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId()).getActivity();
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            timetypeColor = timeTypeMap.containsKey(activity.getActivityBalanceSettings().getTimeTypeId()) ? timeTypeMap.get(activity.getActivityBalanceSettings().getTimeTypeId()).getBackgroundColor() : "";
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), Arrays.asList(wtaForCareDays.getCareDayCounts().get(0).getActivityId()), planningPeriodEndDate);
                    getWtACareDayCountByInterval(wtaForCareDays, shiftWithActivityDTOS, intervalBalances, dateTimeInterval);
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            int sequence = activityWrapperMap.get(activityId).getActivityPriority()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getActivityPriority().getSequence();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, timetypeColor, intervalBalances, cutOffIntervalUnit, borrowLeave,activityWrapperMap.get(activityId).getActivity().getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getWtACareDayCountByInterval(WTAForCareDays wtaForCareDays, List<ShiftWithActivityDTO> shiftWithActivityDTOS, List<IntervalBalance> intervalBalances, DateTimeInterval dateTimeInterval) {
        if (isNotNull(dateTimeInterval)) {
            int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, newHashSet(wtaForCareDays.getCareDayCounts().get(0).getActivityId()));
            ActivityCutOffCount activityLeaveCount = wtaForCareDays.getCareDayCounts().get(0).getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(dateTimeInterval.getStartLocalDate())).findFirst().orElse(new ActivityCutOffCount());
            int total = activityLeaveCount.getCount() + activityLeaveCount.getTransferLeaveCount();
            int available = (activityLeaveCount.getCount() + activityLeaveCount.getTransferLeaveCount() - activityLeaveCount.getBorrowLeaveCount()) - (scheduledAndApproveActivityCount[0]+scheduledAndApproveActivityCount[1]);
            intervalBalances.add(new IntervalBalance(total, scheduledAndApproveActivityCount[0], available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
        }
    }

    public int[] getShiftsActivityCountByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Set<BigInteger> activityIds) {
        int activityCount = 0;
        int approveCount = 0;
        Set<ShiftStatus> shiftStatuses = newHashSet(ShiftStatus.APPROVE,ShiftStatus.PUBLISH);
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                if ((dateTimeInterval.contains(activity.getStartDate())) && activityIds.contains(activity.getActivityId())) {
                    if (!shiftWithActivityDTO.isDraft() && CollectionUtils.containsAny(shiftStatuses,activity.getStatus())) {
                        approveCount++;
                    }
                    if(activity.getStatus().contains(ShiftStatus.REQUEST)&&!shiftWithActivityDTO.isDraft()){
                        activityCount++;
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
            dateTimeInterval = getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), planningPeriodEndDate);
        }
        return dateTimeInterval;
    }


    public static DateTimeInterval getIntervalByActivity(Map<BigInteger, ActivityWrapper> activityWrapperMap, Date shiftStartDate, List<BigInteger> activityIds, LocalDate planningPeriodEndDate) {
        DateTimeInterval dateTimeInterval = null;
        for (BigInteger activityId : activityIds) {
            if (activityWrapperMap.containsKey(activityId)) {
                Activity activity = activityWrapperMap.get(activityId).getActivity();
                dateTimeInterval = getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), shiftStartDate, planningPeriodEndDate);
            }
        }
        return dateTimeInterval;
    }


    public static DateTimeInterval getCutoffInterval(LocalDate dateFrom, CutOffIntervalUnit cutOffIntervalUnit, Integer dayValue, Date shiftDate, LocalDate planningPeriodEndDate) {
        if(isNull(dateFrom) || isNull(cutOffIntervalUnit)){
            throwException(CUT_OFF_CONFIGUATION);
        }
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

    public boolean updateWTALeaveCountByJob(Long countryId) {
        List<Long> units = userIntegrationService.getUnitIds(countryId);
        LocalDate startDate = getLocalDate();
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        for (Long unitId : units) {
            try {
                List<Long> staffIds = new ArrayList<>();
                List<Long> employmentIds = new ArrayList<>();
                List<NameValuePair> requestParam = new ArrayList<>();
                requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
                requestParam.add(new BasicNameValuePair("employmentIds", employmentIds.toString()));
                List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAndEmploymentDetails(unitId, requestParam);
                employmentIds = staffAdditionalInfoDTOS.stream().map(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getEmployment().getId()).collect(Collectors.toList());
                List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getWTAByEmploymentIds(employmentIds, getDate());
                List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).filter(wtaBaseRuleTemplate -> newHashSet(CHILD_CARE_DAYS_CHECK, SENIOR_DAYS_PER_YEAR, WTA_FOR_CARE_DAYS).contains(wtaBaseRuleTemplate.getWtaTemplateType())).collect(Collectors.toList());
                Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates,null);
                List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
                Map<BigInteger, ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
                Map<Long, List<WTAQueryResultDTO>> employmentIdAndWtaMap = wtaQueryResultDTOS.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getEmploymentId(), Collectors.toList()));
                PlanningPeriod planningPeriod = planningPeriodMongoRepository.findLastPlaningPeriodEndDate(unitId);
                DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(activityWrapperMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndDate(), unitId);
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmployments(employmentIdAndWtaMap.keySet(), dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
                Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftsMap = shiftWithActivityDTOS.stream().collect(Collectors.groupingBy(shiftWithActivityDTO -> shiftWithActivityDTO.getEmploymentId(), Collectors.toList()));
                Map<Long, StaffAdditionalInfoDTO> employmentIdAndStaffAdditionalInfoMap = new HashMap<>();
                updateRuletemplateByEmployments(ruleTemplates, employmentIds, staffAdditionalInfoDTOS, activityWrapperMap, employmentIdAndWtaMap, planningPeriod, employmentIdAndShiftsMap, employmentIdAndStaffAdditionalInfoMap);
            } catch (Exception e) {
                LOGGER.error("error while update wta leave count in unit  {} ,\n {}  ", unitId, e);
            }
        }
        if (isCollectionNotEmpty(ruleTemplates)) {
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
        }
        return true;
    }

    private void updateRuletemplateByEmployments(List<WTABaseRuleTemplate> ruleTemplates, List<Long> employmentIds, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, Map<Long, List<WTAQueryResultDTO>> employmentIdAndWtaMap, PlanningPeriod planningPeriod, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftsMap, Map<Long, StaffAdditionalInfoDTO> employmentIdAndStaffAdditionalInfoMap) {
        for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
            employmentIdAndStaffAdditionalInfoMap.putIfAbsent(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO);
        }
        for (Long employmentId : employmentIds) {
            try {
                if (employmentIdAndWtaMap.containsKey(employmentId)) {
                    List<WTAQueryResultDTO> wtaQueryResuls = employmentIdAndWtaMap.get(employmentId);
                    for (WTAQueryResultDTO wtaQueryResul : wtaQueryResuls) {
                        ruleTemplates.addAll(getRuleTemplates(activityWrapperMap, wtaQueryResul.getRuleTemplates(), employmentIdAndShiftsMap.getOrDefault(employmentId,new ArrayList<>()), employmentIdAndStaffAdditionalInfoMap.get(employmentId), planningPeriod.getEndDate()));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("error while update wta leave count in employment  {} ,\n {}  ", employmentId, e);
            }
        }
    }

    public List<WTABaseRuleTemplate> getRuleTemplates(Map<BigInteger, ActivityWrapper> activityWrapperMap, List<WTABaseRuleTemplate> ruleTemplates, List<ShiftWithActivityDTO> shiftWithActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate) {
        Date date = asDate(getLocalDate().minusDays(1));
        LocalDate currentDate = getLocalDate().minusDays(1);
        for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    CareDaysDTO seniorDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                    updateLeaveCountInSeniorDaysOrChildCareDaysWTA(activityWrapperMap, shiftWithActivityDTOS, planningPeriodEndDate, date, currentDate, seniorDays, seniorDaysPerYearWTATemplate.getActivityIds(), seniorDaysPerYearWTATemplate.getActivityCutOffCounts());
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getStaffAge());
                    updateLeaveCountInSeniorDaysOrChildCareDaysWTA(activityWrapperMap, shiftWithActivityDTOS, planningPeriodEndDate, date, currentDate, careDays, childCareDaysCheckWTATemplate.getActivityIds(), childCareDaysCheckWTATemplate.getActivityCutOffCounts());
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    updateLeaveCountCareDaysWTA(activityWrapperMap, shiftWithActivityDTOS, date, currentDate, wtaForCareDays);
                    break;
                default:
                    break;
            }
        }
        return ruleTemplates;
    }

    private void updateLeaveCountCareDaysWTA(Map<BigInteger, ActivityWrapper> activityWrapperMap, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Date date, LocalDate currentDate, WTAForCareDays wtaForCareDays) {
        Activity activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId()).getActivity();
        ActivityCareDayCount careDayCount = wtaForCareDays.careDaysCountMap().get(activity.getId());
        ActivityCutOffCount activityLeaveCount = careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).containsAndEqualsEndDate(date)).findFirst().orElse(null);
        if (isNotNull(activityLeaveCount)) {
            DateTimeInterval  dateTimeInterval = new DateTimeInterval(activityLeaveCount.getStartDate(), activityLeaveCount.getEndDate());
            if (activityLeaveCount.getEndDate().equals(currentDate) && CutOffIntervalUnit.CutOffBalances.TRANSFER.equals(activity.getActivityRulesSettings().getCutOffBalances())) {
                shiftWithActivityDTOS = filterShiftsByDateTimeIntervalAndActivityId(shiftWithActivityDTOS, dateTimeInterval, activity.getId());
                int  pendingLeave = getPendingLeave(shiftWithActivityDTOS, activity, dateTimeInterval, activityLeaveCount.getCount(), activityLeaveCount.getTransferLeaveCount(), activityLeaveCount.getBorrowLeaveCount());
                activityLeaveCount = careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).containsAndEqualsEndDate(getDate())).findFirst().orElse(null);
                if (isNotNull(activityLeaveCount)) {
                    activityLeaveCount.setTransferLeaveCount(pendingLeave);
                }

            }
        }
    }

    private void updateLeaveCountInSeniorDaysOrChildCareDaysWTA(Map<BigInteger, ActivityWrapper> activityWrapperMap, List<ShiftWithActivityDTO> shiftWithActivityDTOS, LocalDate planningPeriodEndDate, Date date, LocalDate currentDate, CareDaysDTO seniorDays, List<BigInteger> activityIds, List<ActivityCutOffCount> activityCutOffCounts) {
        if (isNotNull(seniorDays)) {
            Activity activity = activityWrapperMap.get(activityIds.get(0)).getActivity();
            ActivityCutOffCount activityLeaveCount = activityCutOffCounts.stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).containsAndEqualsEndDate(date)).findFirst().orElse(new ActivityCutOffCount());
            DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, date, activityIds, planningPeriodEndDate);
            if (dateTimeInterval.getEndLocalDate().minusDays(1).equals(currentDate) && CutOffIntervalUnit.CutOffBalances.TRANSFER.equals(activity.getActivityRulesSettings().getCutOffBalances())) {
                dateTimeInterval = getIntervalByActivity(activityWrapperMap, getDate(), activityIds, planningPeriodEndDate);
                int pendingLeave=getPendingLeave(shiftWithActivityDTOS, activity, dateTimeInterval, seniorDays.getLeavesAllowed(), activityLeaveCount.getTransferLeaveCount(), activityLeaveCount.getBorrowLeaveCount());
                activityLeaveCount = activityCutOffCounts.stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(getLocalDate())).findFirst().orElse(new ActivityCutOffCount(dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate()));
                activityLeaveCount.setTransferLeaveCount(pendingLeave);
                activityCutOffCounts.add(activityLeaveCount);
            }
        }
    }

    public int getPendingLeave(List<ShiftWithActivityDTO> shiftWithActivityDTOS, Activity activity, DateTimeInterval dateTimeInterval, int allowedLeave, int transferLeave, int borrowLeave) {
        int pendingLeave = 0;
        shiftWithActivityDTOS = filterShiftsByDateTimeIntervalAndActivityId(shiftWithActivityDTOS, dateTimeInterval, activity.getId());
        int totalCount = allowedLeave + transferLeave - borrowLeave;
        int count = totalCount - shiftWithActivityDTOS.size();
        pendingLeave = allowedLeave < count ? allowedLeave : count;
        return count > 0 ? activity.getActivityRulesSettings().isTransferAll() ? pendingLeave : activity.getActivityRulesSettings().getNoOfTransferLeave() > pendingLeave ? pendingLeave : activity.getActivityRulesSettings().getNoOfTransferLeave() : 0;
    }

    public List<ShiftWithActivityDTO> filterShiftsByDateTimeIntervalAndActivityId(List<ShiftWithActivityDTO> shiftWithActivityDTOS, DateTimeInterval dateTimeInterval, BigInteger activityId) {
        return shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> dateTimeInterval.contains(shiftWithActivityDTO.getStartDate()) && shiftWithActivityDTO.getActivities().stream().anyMatch(shiftActivityDTO -> shiftActivityDTO.getActivityId().equals(activityId))).collect(Collectors.toList());
    }

    public static boolean isDayTypeValid(Date shiftDate, List<Long> daytypeIds, Map<Long, DayTypeDTO> dayTypeDTOMap) {
        List<DayTypeDTO> dayTypeDTOS = daytypeIds.stream().map(dayTypeDTOMap::get).collect(Collectors.toList());
        boolean valid = false;
        for (DayTypeDTO dayTypeDTO : dayTypeDTOS) {
            if (dayTypeDTO.isHolidayType()) {
                valid = isPublicHolidayValid(shiftDate, valid, dayTypeDTO);
            } else {
                List<DayOfWeek> dayOfWeeks = new ArrayList<>();
                dayTypeDTO.getValidDays().forEach(day -> {
                    if (!day.name().equals(EVERYDAY)) {
                        dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
                    }
                });
                valid = dayOfWeeks.contains(asLocalDate(shiftDate).getDayOfWeek());
            }
            if (valid) {
                break;
            }
        }
        return valid;
    }

    public int getLeaveCount(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, YAxisConfig yAxisConfig) {
        int count = 0;
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        if (isCollectionNotEmpty(staffKpiFilterDTOS)) {
            for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
                for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                    WorkTimeAgreementBalance workTimeAgreementBalance = getWorkTimeAgreementBalance(kpiCalculationRelatedInfo.getUnitId(),employmentWithCtaDetailsDTO.getId(), dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate(),kpiCalculationRelatedInfo.getWtaTemplateTypes(yAxisConfig),null);
                    count += workTimeAgreementBalance.getWorkTimeAgreementRuleTemplateBalances().stream().flatMap(workTimeAgreementRuleTemplateBalancesDTO -> workTimeAgreementRuleTemplateBalancesDTO.getIntervalBalances().stream()).mapToInt(intervalBalance -> (int) intervalBalance.getAvailable()).sum();
                }
            }
        }
        return count;
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getLeaveCount(staffId, dateTimeInterval, kpiCalculationRelatedInfo, (YAxisConfig) t);
    }
}
