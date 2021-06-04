package com.kairos.service.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
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
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
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
import com.kairos.service.counter.KPICalculationRelatedInfo;
import com.kairos.service.counter.KPIService;
import com.kairos.service.day_type.DayTypeService;
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
import static com.kairos.constants.AppConstants.EVERYDAY;
import static com.kairos.constants.AppConstants.STOP_BRICK_BLOCKING_POINT;
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
    private DayTypeService dayTypeService;
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



    public DateTimeInterval getIntervalByRuletemplates(Set<WTATemplateType> wtaTemplateTypes, Map<BigInteger, ActivityDTO> activityMap, List<WTABaseRuleTemplate> WTARuleTemplates, LocalDate startDate, LocalDate planningPeriodEndDate, Long unitId) {
        DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
        for (WTABaseRuleTemplate ruleTemplate : WTARuleTemplates) {
            if(isCollectionEmpty(wtaTemplateTypes) || wtaTemplateTypes.contains(ruleTemplate.getWtaTemplateType())){
                switch (ruleTemplate.getWtaTemplateType()) {
                    case VETO_AND_STOP_BRICKS:
                        VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                        validateRuleTemplate(vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate());
                        interval = interval.addInterval(getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate(), planningPeriodEndDate));
                        break;
                    case SENIOR_DAYS_PER_YEAR:
                        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                        interval = interval.addInterval(getIntervalByActivity(activityMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate));
                        break;
                    case CHILD_CARE_DAYS_CHECK:
                        ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                        interval = interval.addInterval(getIntervalByActivity(activityMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate));
                        break;
                    case WTA_FOR_CARE_DAYS:
                        WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                        interval = interval.addInterval(getIntervalByWTACareDaysRuleTemplate(startDate, wtaForCareDays, activityMap, planningPeriodEndDate));
                        break;
                    case PROTECTED_DAYS_OFF:
                        ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplate) ruleTemplate;
                        ProtectedDaysOffSettingDTO protectedDaysOffSetting = protectedDaysOffService.getProtectedDaysOffByUnitId(unitId);
                        interval = interval.addInterval(getIntervalByProtectedDaysOffRuleTemplate(startDate, protectedDaysOffWTATemplate, activityMap, protectedDaysOffSetting, planningPeriodEndDate));
                        break;
                    default:
                        break;
                }
            }
        }
        return interval;
    }

    private DateTimeInterval getIntervalByProtectedDaysOffRuleTemplate(LocalDate startDate, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, Map<BigInteger, ActivityDTO> activityWrapperMap, ProtectedDaysOffSettingDTO protectedDaysOffSetting, LocalDate planningPeriodEndDate) {
        if(isNull(protectedDaysOffWTATemplate.getActivityId())){
            exceptionService.invalidRequestException(ACTIVITY_NOT_ASSIGN_IN_PROTECTED_DAYS_OFF_RULE_TEMPLATE,protectedDaysOffWTATemplate.getName(),protectedDaysOffWTATemplate.getId());
        }
        ActivityDTO activity = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
        return getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffSetting.getProtectedDaysOffUnitSettings()) ? planningPeriodEndDate : DateUtils.getLocalDate());
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
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.getStaffDetailsForBalances(unitId, startDate,employmentId);
        staffAdditionalInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_NOTFOUND);
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_ABSENT);
        }
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDates(employmentId, asDate(startDate), asDate(endDate));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).collect(Collectors.toList());
        Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates, activityId);
        List<ActivityDTO> activityDTOS = activityMongoRepository.findByDeletedFalseAndIdsInForBalances(activityIds);
        Map<BigInteger, ActivityDTO> activityMap = activityDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);
        DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(wtaTemplateTypes,activityMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndDate(), unitId);
        List<ShiftActivityDTO> validShiftActivityDTOSByDayType = getshiftActivityDTOS(employmentId, staffAdditionalInfoDTO, activityIds, activityMap, dateTimeInterval);
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances = getWorkTimeAgreementRuleTemplateBalances(unitId, startDate, endDate, staffAdditionalInfoDTO, wtaBaseRuleTemplates, activityMap, planningPeriod, validShiftActivityDTOSByDayType, wtaTemplateTypes);
        WorkTimeAgreementBalance workTimeAgreementBalance = new WorkTimeAgreementBalance(workTimeAgreementRuleTemplateBalances);
        return workTimeAgreementBalance;
    }

    private List<ShiftActivityDTO> getshiftActivityDTOS(Long employmentId, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Set<BigInteger> activityIds, Map<BigInteger, ActivityDTO> activityWrapperMap, DateTimeInterval dateTimeInterval) {
        List<ShiftActivityDTO> shiftActivityDTOS = shiftMongoRepository.findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(employmentId, dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
        Map<BigInteger , DayTypeDTO> dayTypeDTOMap =staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, dayTypeDTO -> dayTypeDTO));
        return shiftActivityDTOS.parallelStream().filter(shiftActivityDTO -> (activityWrapperMap.containsKey(shiftActivityDTO.getActivityId()) && isDayTypeValid(shiftActivityDTO.getStartDate(), activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivityTimeCalculationSettings().getDayTypes(), dayTypeDTOMap))).collect(Collectors.toList());
    }


    public List<WorkTimeAgreementRuleTemplateBalancesDTO> getWorkTimeAgreementRuleTemplateBalances(Long unitId, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<WTABaseRuleTemplate> wtaBaseRuleTemplates, Map<BigInteger, ActivityDTO> activityMap, PlanningPeriod planningPeriod, List<ShiftActivityDTO> shiftActivityDTOS, Set<WTATemplateType> wtaTemplateTypes) {
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances=new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO;
        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            if(isCollectionEmpty(wtaTemplateTypes) || wtaTemplateTypes.contains(ruleTemplate.getWtaTemplateType())) {
                switch (ruleTemplate.getWtaTemplateType()) {
                    case VETO_AND_STOP_BRICKS:
                        workTimeAgreementRuleTemplateBalancesDTO = getVetoRuleTemplateBalance((VetoAndStopBricksWTATemplate) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate,  planningPeriod.getEndDate());
                        break;
                    case SENIOR_DAYS_PER_YEAR:
                        workTimeAgreementRuleTemplateBalancesDTO = getseniorDayRuleTemplateBalance((SeniorDaysPerYearWTATemplate) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate, staffAdditionalInfoDTO,  planningPeriod.getEndDate());
                        break;
                    case CHILD_CARE_DAYS_CHECK:
                        workTimeAgreementRuleTemplateBalancesDTO = getchildCareDayRuleTemplateBalance((ChildCareDaysCheckWTATemplate) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate, staffAdditionalInfoDTO,  planningPeriod.getEndDate());
                        break;
                    case WTA_FOR_CARE_DAYS:
                        workTimeAgreementRuleTemplateBalancesDTO = getWtaForCareDayRuleTemplateBalance((WTAForCareDays) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate,  planningPeriod.getEndDate());
                        break;
                    case PROTECTED_DAYS_OFF:
                        workTimeAgreementRuleTemplateBalancesDTO = getProtectedDaysOffBalance(unitId, (ProtectedDaysOffWTATemplate) ruleTemplate, shiftActivityDTOS, activityMap,  staffAdditionalInfoDTO, startDate, endDate, planningPeriod.getEndDate());
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

    public boolean isLeaveCountAvailable(Activity activity, ShiftWithActivityDTO shift, DateTimeInterval dateTimeInterval, LocalDate lastPlanningPeriodEndDat, WTABaseRuleTemplate wtaBaseRuleTemplate, long leaveCount,int staffAge,List<CareDaysDTO> seniorDays,List<CareDaysDTO> childCareDays) {
        boolean isLeaveCountAvailable = false;
        if (!shift.getStartDate().before(getDate()) && activity.getActivityRulesSettings().isBorrowLeave()) {
            DateTimeInterval nextCutOffdateTimeInterval = getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(dateTimeInterval.getEndLocalDate().plusDays(1)), lastPlanningPeriodEndDat);
            List<ShiftActivityDTO> shiftActivityDTOS = shiftMongoRepository.findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(shift.getEmploymentId(), DateUtils.asDate(dateTimeInterval.getStart()), DateUtils.asDate(nextCutOffdateTimeInterval.getEnd()), newHashSet(activity.getId()));
            switch (wtaBaseRuleTemplate.getWtaTemplateType()) {
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) wtaBaseRuleTemplate;
                    isLeaveCountAvailable = leaveAvailableForCareDaysAndSeniorDaysWTA(leaveCount, staffAge, isLeaveCountAvailable, nextCutOffdateTimeInterval, shiftActivityDTOS, seniorDays, seniorDaysPerYearWTATemplate.getActivityCutOffCounts());
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) wtaBaseRuleTemplate;
                    isLeaveCountAvailable = leaveAvailableForCareDaysAndSeniorDaysWTA(leaveCount, staffAge, isLeaveCountAvailable, nextCutOffdateTimeInterval, shiftActivityDTOS, childCareDays, childCareDaysCheckWTATemplate.getActivityCutOffCounts());
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) wtaBaseRuleTemplate;
                    ActivityCareDayCount careDayCount = wtaForCareDays.careDaysCountMap().get(activity.getId());
                    isLeaveCountAvailable = leaveAvailableForCareDaysWTA(leaveCount, isLeaveCountAvailable, nextCutOffdateTimeInterval, shiftActivityDTOS, careDayCount);
                    break;
                default:
                    break;
            }
        }
        wtaBaseRuleTemplateRepository.save(wtaBaseRuleTemplate);
        return isLeaveCountAvailable;
    }

    private boolean leaveAvailableForCareDaysAndSeniorDaysWTA(long leaveCount, int staffAge, boolean isLeaveCountAvailable, DateTimeInterval nextCutOffdateTimeInterval, List<ShiftActivityDTO> shiftActivityDTOS, List<CareDaysDTO> childCareDays, List<ActivityCutOffCount> activityCutOffCounts) {
        CareDaysDTO careDays = getCareDays(childCareDays, staffAge);
        Optional<ActivityCutOffCount> activityLeaveCountOptional = activityCutOffCounts.stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(nextCutOffdateTimeInterval.getStartLocalDate())).findFirst();
        if (activityLeaveCountOptional.isPresent() && leaveCount + careDays.getLeavesAllowed() + activityLeaveCountOptional.get().getTransferLeaveCount() - activityLeaveCountOptional.get().getBorrowLeaveCount() < shiftActivityDTOS.size()) {
            activityLeaveCountOptional.get().setBorrowLeaveCount(activityLeaveCountOptional.get().getBorrowLeaveCount() + 1);
            isLeaveCountAvailable = true;
        }
        return isLeaveCountAvailable;
    }

    private boolean leaveAvailableForCareDaysWTA(long leaveCount, boolean isLeaveCountAvailable, DateTimeInterval nextCutOffdateTimeInterval, List<ShiftActivityDTO> shiftActivityDTOS, ActivityCareDayCount careDayCount) {
        ActivityCutOffCount activityLeaveCount = careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(nextCutOffdateTimeInterval.getStartLocalDate())).findFirst().get();
        if (leaveCount + activityLeaveCount.getCount() + activityLeaveCount.getTransferLeaveCount() - activityLeaveCount.getBorrowLeaveCount() > shiftActivityDTOS.size()) {
            activityLeaveCount.setBorrowLeaveCount(activityLeaveCount.getBorrowLeaveCount() + 1);
            isLeaveCountAvailable = true;
        }
        return isLeaveCountAvailable;
    }

    public WorkTimeAgreementRuleTemplateBalancesDTO getProtectedDaysOffBalance(Long unitId, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityMap,  StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate) {
        ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit = protectedDaysOffService.getProtectedDaysOffByUnitId(unitId);
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        if (!ProtectedDaysOffUnitSettings.UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR.equals(protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings())) {
            ActivityDTO activity = activityMap.get(protectedDaysOffWTATemplate.getActivityId());
            CutOffIntervalUnit cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings = protectedDaysOffService.getProtectedDaysOffByExpertiseId(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> isEqualOrAfter(protectedDaysOffSetting.getPublicHolidayDate(),staffAdditionalInfoDTO.getEmployment().getStartDate()) && (isNull(staffAdditionalInfoDTO.getEmployment().getEndDate()) || !protectedDaysOffSetting.getPublicHolidayDate().isAfter(staffAdditionalInfoDTO.getEmployment().getEndDate()))).collect(Collectors.toList());
            String activityName = activity.getName();
            String timetypeColor = activity.getActivityGeneralSettings().getBackgroundColor();
            getProtectedDaysOffIntervalbalance(protectedDaysOffWTATemplate, shiftActivityDTOS, staffAdditionalInfoDTO, startDate, endDate, planningPeriodEndDate, protectedDaysOffSettingOfUnit, intervalBalances, activity, cutOffIntervalUnit, protectedDaysOffSettings);
            if (isCollectionNotEmpty(intervalBalances)) {
                int sequence = activity.getRanking()==null?Integer.MAX_VALUE:activity.getRanking();
                workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activity.getId(), activityName, timetypeColor, intervalBalances, cutOffIntervalUnit, false,activity.getActivityBalanceSettings().getTimeType().toString(),sequence);
            }
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getProtectedDaysOffIntervalbalance(ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate, ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit, List<IntervalBalance> intervalBalances, ActivityDTO activity, CutOffIntervalUnit cutOffIntervalUnit, List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings) {
        while (!startDate.isAfter(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), planningPeriodEndDate);
                getProtectedDaysOfCountByInterval(protectedDaysOffWTATemplate, shiftActivityDTOS, staffAdditionalInfoDTO, startDate, protectedDaysOffSettingOfUnit, intervalBalances, activity, cutOffIntervalUnit, protectedDaysOffSettings, dateTimeInterval);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private void getProtectedDaysOfCountByInterval(ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit, List<IntervalBalance> intervalBalances, ActivityDTO activity, CutOffIntervalUnit cutOffIntervalUnit, List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings, DateTimeInterval dateTimeInterval) {
        if (isNotNull(dateTimeInterval)) {
            Object[] countAndDate = getProtectedDaysOffCountAndDate(protectedDaysOffSettings, dateTimeInterval, protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings(), cutOffIntervalUnit, activity.getActivityRulesSettings().getCutOffdayValue(), startDate);
            long count = Long.parseLong(countAndDate[0].toString());
            LocalDate protectedStartDate = (LocalDate) countAndDate[1];
            if (isNotNull(protectedStartDate)) {
                shiftActivityDTOS = shiftMongoRepository.findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(staffAdditionalInfoDTO.getEmployment().getId(), asDate(protectedStartDate), dateTimeInterval.getEndDate(), newHashSet(protectedDaysOffWTATemplate.getActivityId()));
            }
            int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftActivityDTOS, newHashSet(protectedDaysOffWTATemplate.getActivityId()));
            long scheduled = count > 0 ? scheduledAndApproveActivityCount[0] : count;
            long available = (count - scheduledAndApproveActivityCount[0]) > 0 ? count - scheduledAndApproveActivityCount[0]+scheduledAndApproveActivityCount[1] : 0;
            long approved = count > 0 ? scheduledAndApproveActivityCount[1] : count;
                if(count!=0) {
                    intervalBalances.add(new IntervalBalance(count, scheduled, available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), approved));
                }
        }
    }

    public Object[] getProtectedDaysOffCountAndDate(List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings, DateTimeInterval dateTimeInterval, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings, CutOffIntervalUnit cutOffIntervalUnit, Integer cutOffdayValue, LocalDate startDate) {
        long count;
        LocalDate protectedDaysOfDate = null;
        if (ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffUnitSettings)) {
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && dateTimeInterval.contains(protectedDaysOffSetting.getPublicHolidayDate())).collect(Collectors.toList());
            count = protectedDaysOffSettings.size();
        } else {
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.isProtectedDaysOff() && protectedDaysOffSetting.getPublicHolidayDate().isBefore(startDate) && isNotNull(getCutoffInterval(protectedDaysOffSetting.getPublicHolidayDate(), cutOffIntervalUnit, cutOffdayValue, asDate(startDate), protectedDaysOffSetting.getPublicHolidayDate().plusDays(1)))).collect(Collectors.toList());
            count = protectedDaysOffSettings.size();
            protectedDaysOffSettings.sort(Comparator.comparing(ProtectedDaysOffSettingDTO::getPublicHolidayDate));
            protectedDaysOfDate = isCollectionNotEmpty(protectedDaysOffSettings) ? protectedDaysOffSettings.get(0).getPublicHolidayDate() : protectedDaysOfDate;
        }
        return new Object[]{count, protectedDaysOfDate, protectedDaysOffSettings};
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getVetoRuleTemplateBalance(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate,  LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        BigInteger activityId = BigInteger.ZERO;
        String activityColor = "";
        boolean borrowLeave = false;
        if (isNotNull(vetoAndStopBricksWTATemplate.getVetoActivityId()) && isNotNull(vetoAndStopBricksWTATemplate.getStopBrickActivityId())) {
            ActivityDTO activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getVetoActivityId());
            if (isNull(activity)) {
                activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getStopBrickActivityId());
            }
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            activityColor = activity.getActivityGeneralSettings().getBackgroundColor();
            getVetoIntervalBalance(vetoAndStopBricksWTATemplate, shiftActivityDTOS, startDate, endDate, planningPeriodEndDate, intervalBalances);
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            int sequence = activityWrapperMap.get(activityId).getRanking()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getRanking();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, activityColor, intervalBalances, CutOffIntervalUnit.WEEKS, borrowLeave,activityWrapperMap.get(activityId).getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getVetoIntervalBalance(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (startDate.isBefore(vetoAndStopBricksWTATemplate.getValidationStartDate())) {
                startDate = startDate.plusDays(1);
                continue;
            }
            if (!containsInInterval(intervalBalances, startDate)) {
                updateBalanceByInterval(vetoAndStopBricksWTATemplate, shiftActivityDTOS, startDate, planningPeriodEndDate, intervalBalances);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private void updateBalanceByInterval(VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, LocalDate startDate, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        DateTimeInterval dateTimeInterval = getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate(), planningPeriodEndDate);
        float scheduledActivityCount = 0;
        float approveActivityCount = 0;
        for (ShiftActivityDTO shiftActivityDTO : shiftActivityDTOS) {
            if ((shiftActivityDTO.getStartLocalDate().equals(dateTimeInterval.getStartLocalDate()) || dateTimeInterval.contains(shiftActivityDTO.getStartDate()))) {
                float[] approveAndScheduleCount = getCountOfVetoAndStopBricks(shiftActivityDTO, vetoAndStopBricksWTATemplate);
                approveActivityCount += approveAndScheduleCount[0];
                scheduledActivityCount += approveAndScheduleCount[1];
            }
        }
        float available = vetoAndStopBricksWTATemplate.getTotalBlockingPoints() - scheduledActivityCount;
        intervalBalances.add(new IntervalBalance(vetoAndStopBricksWTATemplate.getTotalBlockingPoints(), scheduledActivityCount, available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), approveActivityCount));
    }

    private float[] getCountOfVetoAndStopBricks(ShiftActivityDTO shiftActivityDTO, VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate) {
        float scheduledActivityCount = 0;
        float approveActivityCount = 0;
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
        return new float[]{approveActivityCount,scheduledActivityCount};
    }


    private WorkTimeAgreementRuleTemplateBalancesDTO getseniorDayRuleTemplateBalance(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO,  LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String activityColor = "";
        BigInteger activityId = BigInteger.ZERO;
        boolean borrowLeave = false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(seniorDaysPerYearWTATemplate.getActivityIds()) && activityWrapperMap.containsKey(seniorDaysPerYearWTATemplate.getActivityIds().get(0))) {
            ActivityDTO activity = activityWrapperMap.get(seniorDaysPerYearWTATemplate.getActivityIds().get(0));
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            activityColor = activity.getActivityGeneralSettings().getBackgroundColor();
            getSeniorDayIntevalBalance(seniorDaysPerYearWTATemplate, shiftActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, planningPeriodEndDate, intervalBalances);
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            int sequence = activityWrapperMap.get(activityId).getRanking()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getRanking();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, activityColor, intervalBalances, cutOffIntervalUnit, borrowLeave,activityWrapperMap.get(activityId).getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getSeniorDayIntevalBalance(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate);
                getSeniorDayCountByInterval(seniorDaysPerYearWTATemplate, shiftActivityDTOS, startDate, staffAdditionalInfoDTO, intervalBalances, dateTimeInterval);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private void getSeniorDayCountByInterval(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, LocalDate startDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<IntervalBalance> intervalBalances, DateTimeInterval dateTimeInterval) {
        if (isNotNull(dateTimeInterval)) {
            int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftActivityDTOS, new HashSet<>(seniorDaysPerYearWTATemplate.getActivityIds()));
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

    private WorkTimeAgreementRuleTemplateBalancesDTO getchildCareDayRuleTemplateBalance(ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO,  LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String activityColor = "";
        BigInteger activityId = BigInteger.ZERO;
        boolean borrowLeave = false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(childCareDaysCheckWTATemplate.getActivityIds()) &&  activityWrapperMap.containsKey(childCareDaysCheckWTATemplate.getActivityIds().get(0))) {
            ActivityDTO activity = activityWrapperMap.get(childCareDaysCheckWTATemplate.getActivityIds().get(0));
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            activityColor = activity.getActivityGeneralSettings().getBackgroundColor();
            cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            getChildCareIntervalBalance(childCareDaysCheckWTATemplate, shiftActivityDTOS, activityWrapperMap, startDate, endDate, staffAdditionalInfoDTO, planningPeriodEndDate, intervalBalances);
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            int sequence = activityWrapperMap.get(activityId).getRanking()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getRanking();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, activityColor, intervalBalances, cutOffIntervalUnit, borrowLeave,activityWrapperMap.get(activityId).getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getChildCareIntervalBalance(ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate);
                if (isNotNull(dateTimeInterval)) {
                    int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftActivityDTOS, new HashSet(childCareDaysCheckWTATemplate.getActivityIds()));
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

    private WorkTimeAgreementRuleTemplateBalancesDTO getWtaForCareDayRuleTemplateBalance(WTAForCareDays wtaForCareDays, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String activityColor = "";
        BigInteger activityId = BigInteger.ZERO;
        boolean borrowLeave = false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(wtaForCareDays.getCareDayCounts()) && activityWrapperMap.containsKey(wtaForCareDays.getCareDayCounts().get(0).getActivityId()) ) {
            ActivityDTO activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId());
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave = activity.getActivityRulesSettings().isBorrowLeave();
            cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            activityColor = activity.getActivityGeneralSettings().getBackgroundColor();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), Arrays.asList(wtaForCareDays.getCareDayCounts().get(0).getActivityId()), planningPeriodEndDate);
                    getWtACareDayCountByInterval(wtaForCareDays, shiftActivityDTOS, intervalBalances, dateTimeInterval);
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {


            int sequence = activityWrapperMap.get(activityId).getRanking()==null?Integer.MAX_VALUE:activityWrapperMap.get(activityId).getRanking();
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId, activityName, activityColor, intervalBalances, cutOffIntervalUnit, borrowLeave,activityWrapperMap.get(activityId).getActivityBalanceSettings().getTimeType().toString(),sequence);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private void getWtACareDayCountByInterval(WTAForCareDays wtaForCareDays, List<ShiftActivityDTO> shiftActivityDTOS, List<IntervalBalance> intervalBalances, DateTimeInterval dateTimeInterval) {
        if (isNotNull(dateTimeInterval)) {
            int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftActivityDTOS, newHashSet(wtaForCareDays.getCareDayCounts().get(0).getActivityId()));
            ActivityCutOffCount activityLeaveCount = wtaForCareDays.getCareDayCounts().get(0).getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(dateTimeInterval.getStartLocalDate())).findFirst().orElse(new ActivityCutOffCount());
            int total = activityLeaveCount.getCount() + activityLeaveCount.getTransferLeaveCount();
            int available = (activityLeaveCount.getCount() + activityLeaveCount.getTransferLeaveCount() - activityLeaveCount.getBorrowLeaveCount()) - (scheduledAndApproveActivityCount[0]+scheduledAndApproveActivityCount[1]);
            intervalBalances.add(new IntervalBalance(total, scheduledAndApproveActivityCount[0], available, dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
        }
    }

    public int[] getShiftsActivityCountByInterval(DateTimeInterval dateTimeInterval, List<ShiftActivityDTO> shiftActivityDTOS, Set<BigInteger> activityIds) {
        int activityCount = 0;
        int approveCount = 0;
        Set<ShiftStatus> shiftStatuses = newHashSet(ShiftStatus.APPROVE,ShiftStatus.PUBLISH);
            for (ShiftActivityDTO activity : shiftActivityDTOS) {
                if ((dateTimeInterval.contains(activity.getStartDate())) && activityIds.contains(activity.getActivityId())) {
                    if (CollectionUtils.containsAny(shiftStatuses,activity.getStatus())) {
                        approveCount++;
                    }
                    if(activity.getStatus().contains(ShiftStatus.REQUEST)){
                        activityCount++;
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

    public static DateTimeInterval getIntervalByWTACareDaysRuleTemplate(LocalDate startDate, WTAForCareDays wtaForCareDays, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate planningPeriodEndDate) {
        DateTimeInterval dateTimeInterval = new DateTimeInterval(asDate(startDate), asDate(startDate.plusDays(1)));
        if (isCollectionNotEmpty(wtaForCareDays.getCareDayCounts()) && activityWrapperMap.containsKey(wtaForCareDays.getCareDayCounts().get(0).getActivityId())) {
            ActivityDTO activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId());
            dateTimeInterval = getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), planningPeriodEndDate);
        }
        return dateTimeInterval;
    }


    public static DateTimeInterval getIntervalByActivity(Map<BigInteger, ActivityDTO> activityMap, Date shiftStartDate, List<BigInteger> activityIds, LocalDate planningPeriodEndDate) {
        DateTimeInterval dateTimeInterval = null;
        for (BigInteger activityId : activityIds) {
            if (activityMap.containsKey(activityId)) {
                ActivityDTO activity = activityMap.get(activityId);
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
                List<ActivityDTO> activityDTOS = activityMongoRepository.findByDeletedFalseAndIdsInForBalances(new ArrayList<>(activityIds));
                Map<BigInteger, ActivityDTO> activityMap = activityDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                Map<Long, List<WTAQueryResultDTO>> employmentIdAndWtaMap = wtaQueryResultDTOS.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getEmploymentId(), Collectors.toList()));
                PlanningPeriod planningPeriod = planningPeriodMongoRepository.findLastPlaningPeriodEndDate(unitId);
                DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(null, activityMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndDate(), unitId);
                List<ShiftWithActivityDTO> shiftActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmployments(employmentIdAndWtaMap.keySet(), dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
                Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftsMap = shiftActivityDTOS.stream().collect(Collectors.groupingBy(shiftWithActivityDTO -> shiftWithActivityDTO.getEmploymentId(), Collectors.toList()));
                Map<Long, StaffAdditionalInfoDTO> employmentIdAndStaffAdditionalInfoMap = new HashMap<>();
                updateRuletemplateByEmployments(ruleTemplates, employmentIds, staffAdditionalInfoDTOS, activityMap, employmentIdAndWtaMap, planningPeriod, employmentIdAndShiftsMap, employmentIdAndStaffAdditionalInfoMap);
            } catch (Exception e) {
                LOGGER.error("error while update wta leave count in unit  {} ,\n {}  ", unitId, e);
            }
        }
        if (isCollectionNotEmpty(ruleTemplates)) {
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
        }
        return true;
    }

    private void updateRuletemplateByEmployments(List<WTABaseRuleTemplate> ruleTemplates, List<Long> employmentIds, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Map<BigInteger, ActivityDTO> activityMap, Map<Long, List<WTAQueryResultDTO>> employmentIdAndWtaMap, PlanningPeriod planningPeriod, Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftsMap, Map<Long, StaffAdditionalInfoDTO> employmentIdAndStaffAdditionalInfoMap) {
        for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
            employmentIdAndStaffAdditionalInfoMap.putIfAbsent(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO);
        }
        for (Long employmentId : employmentIds) {
            try {
                if (employmentIdAndWtaMap.containsKey(employmentId)) {
                    List<WTAQueryResultDTO> wtaQueryResuls = employmentIdAndWtaMap.get(employmentId);
                    for (WTAQueryResultDTO wtaQueryResul : wtaQueryResuls) {
                        ruleTemplates.addAll(getRuleTemplates(activityMap, wtaQueryResul.getRuleTemplates(), employmentIdAndShiftsMap.getOrDefault(employmentId,new ArrayList<>()), employmentIdAndStaffAdditionalInfoMap.get(employmentId), planningPeriod.getEndDate()));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("error while update wta leave count in employment  {} ,\n {}  ", employmentId, e);
            }
        }
    }

    public List<WTABaseRuleTemplate> getRuleTemplates(Map<BigInteger, ActivityDTO> activityMap, List<WTABaseRuleTemplate> ruleTemplates, List<ShiftWithActivityDTO> shiftActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate) {
        Date date = asDate(getLocalDate().minusDays(1));
        LocalDate currentDate = getLocalDate().minusDays(1);
        for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    CareDaysDTO seniorDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                    updateLeaveCountInSeniorDaysOrChildCareDaysWTA(activityMap, shiftActivityDTOS, planningPeriodEndDate, date, currentDate, seniorDays, seniorDaysPerYearWTATemplate.getActivityIds(), seniorDaysPerYearWTATemplate.getActivityCutOffCounts());
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getStaffAge());
                    updateLeaveCountInSeniorDaysOrChildCareDaysWTA(activityMap, shiftActivityDTOS, planningPeriodEndDate, date, currentDate, careDays, childCareDaysCheckWTATemplate.getActivityIds(), childCareDaysCheckWTATemplate.getActivityCutOffCounts());
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    updateLeaveCountCareDaysWTA(activityMap, shiftActivityDTOS, date, currentDate, wtaForCareDays);
                    break;
                default:
                    break;
            }
        }
        return ruleTemplates;
    }

    private void updateLeaveCountCareDaysWTA(Map<BigInteger, ActivityDTO> activityWrapperMap, List<ShiftWithActivityDTO> shiftActivityDTOS, Date date, LocalDate currentDate, WTAForCareDays wtaForCareDays) {
        ActivityDTO activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId());
        ActivityCareDayCount careDayCount = wtaForCareDays.careDaysCountMap().get(activity.getId());
        ActivityCutOffCount activityLeaveCount = careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).containsAndEqualsEndDate(date)).findFirst().orElse(null);
        if (isNotNull(activityLeaveCount)) {
            DateTimeInterval  dateTimeInterval = new DateTimeInterval(activityLeaveCount.getStartDate(), activityLeaveCount.getEndDate());
            if (activityLeaveCount.getEndDate().equals(currentDate) && CutOffIntervalUnit.CutOffBalances.TRANSFER.equals(activity.getActivityRulesSettings().getCutOffBalances())) {
                shiftActivityDTOS = filterShiftsByDateTimeIntervalAndActivityId(shiftActivityDTOS, dateTimeInterval, activity.getId());
                int  pendingLeave = getPendingLeave(shiftActivityDTOS, activity, dateTimeInterval, activityLeaveCount.getCount(), activityLeaveCount.getTransferLeaveCount(), activityLeaveCount.getBorrowLeaveCount());
                activityLeaveCount = careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).containsAndEqualsEndDate(getDate())).findFirst().orElse(null);
                if (isNotNull(activityLeaveCount)) {
                    activityLeaveCount.setTransferLeaveCount(pendingLeave);
                }

            }
        }
    }

    private void updateLeaveCountInSeniorDaysOrChildCareDaysWTA(Map<BigInteger, ActivityDTO> activityWrapperMap, List<ShiftWithActivityDTO> shiftActivityDTOS, LocalDate planningPeriodEndDate, Date date, LocalDate currentDate, CareDaysDTO seniorDays, List<BigInteger> activityIds, List<ActivityCutOffCount> activityCutOffCounts) {
        if (isNotNull(seniorDays)) {
            ActivityDTO activity = activityWrapperMap.get(activityIds.get(0));
            ActivityCutOffCount activityLeaveCount = activityCutOffCounts.stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).containsAndEqualsEndDate(date)).findFirst().orElse(new ActivityCutOffCount());
            DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, date, activityIds, planningPeriodEndDate);
            if (dateTimeInterval.getEndLocalDate().minusDays(1).equals(currentDate) && CutOffIntervalUnit.CutOffBalances.TRANSFER.equals(activity.getActivityRulesSettings().getCutOffBalances())) {
                dateTimeInterval = getIntervalByActivity(activityWrapperMap, getDate(), activityIds, planningPeriodEndDate);
                int pendingLeave=getPendingLeave(shiftActivityDTOS, activity, dateTimeInterval, seniorDays.getLeavesAllowed(), activityLeaveCount.getTransferLeaveCount(), activityLeaveCount.getBorrowLeaveCount());
                activityLeaveCount = activityCutOffCounts.stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).contains(getLocalDate())).findFirst().orElse(new ActivityCutOffCount(dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate()));
                activityLeaveCount.setTransferLeaveCount(pendingLeave);
                activityCutOffCounts.add(activityLeaveCount);
            }
        }
    }

    public int getPendingLeave(List<ShiftWithActivityDTO> shiftActivityDTOS, ActivityDTO activity, DateTimeInterval dateTimeInterval, int allowedLeave, int transferLeave, int borrowLeave) {
        int pendingLeave = 0;
        shiftActivityDTOS = filterShiftsByDateTimeIntervalAndActivityId(shiftActivityDTOS, dateTimeInterval, activity.getId());
        int totalCount = allowedLeave + transferLeave - borrowLeave;
        int count = totalCount - shiftActivityDTOS.size();
        pendingLeave = allowedLeave < count ? allowedLeave : count;
        return count > 0 ? activity.getActivityRulesSettings().isTransferAll() ? pendingLeave : activity.getActivityRulesSettings().getNoOfTransferLeave() > pendingLeave ? pendingLeave : activity.getActivityRulesSettings().getNoOfTransferLeave() : 0;
    }

    public List<ShiftWithActivityDTO> filterShiftsByDateTimeIntervalAndActivityId(List<ShiftWithActivityDTO> shiftActivityDTOS, DateTimeInterval dateTimeInterval, BigInteger activityId) {
        return shiftActivityDTOS.stream().filter(shiftWithActivityDTO -> dateTimeInterval.contains(shiftWithActivityDTO.getStartDate()) && shiftWithActivityDTO.getActivities().stream().anyMatch(shiftActivityDTO -> shiftActivityDTO.getActivityId().equals(activityId))).collect(Collectors.toList());
    }

    public static boolean isDayTypeValid(Date shiftDate, List<BigInteger> daytypeIds, Map<BigInteger, DayTypeDTO> dayTypeDTOMap) {
        List<DayTypeDTO> dayTypeDTOS =dayTypeDTOMap.values().stream().filter(k->daytypeIds.contains(k.getId())).collect(Collectors.toList());
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

    public int getLeaveCount(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, YAxisConfig yAxisConfig) {
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

    public IntervalBalance getProtectedDaysOffCount(Long unitId, LocalDate localDate, Long staffId, BigInteger activityId) {
        localDate = isNotNull(localDate) ? localDate : DateUtils.getCurrentLocalDate();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        StaffEmploymentDetails staffEmploymentDetails = userIntegrationService.mainUnitEmploymentOfStaff(staffId, unitId);
        if (isNotNull(staffEmploymentDetails)) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(staffEmploymentDetails);
            ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = new ProtectedDaysOffWTATemplate(activityId, WTATemplateType.PROTECTED_DAYS_OFF);
            List<ActivityDTO> activityDTOS = activityMongoRepository.findByDeletedFalseAndIdsInForBalances(newArrayList(activityId));
            Map<BigInteger, ActivityDTO> activityWrapperMap = activityDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            PlanningPeriod planningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);
            DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(null,activityWrapperMap, Arrays.asList(protectedDaysOffWTATemplate), localDate, planningPeriod.getEndDate(), unitId);
            List<ShiftActivityDTO> shiftActivityDTOS = shiftMongoRepository.findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(staffAdditionalInfoDTO.getEmployment().getId(), dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), newHashSet(activityId));
            workTimeAgreementRuleTemplateBalancesDTO = getProtectedDaysOffBalance(unitId, protectedDaysOffWTATemplate, shiftActivityDTOS, activityWrapperMap, staffAdditionalInfoDTO, localDate, localDate, planningPeriod.getEndDate());
        }
        return isNotNull(workTimeAgreementRuleTemplateBalancesDTO) ? workTimeAgreementRuleTemplateBalancesDTO.getIntervalBalances().get(0) : new IntervalBalance();
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getLeaveCount(staffId, dateTimeInterval, kpiCalculationRelatedInfo, (YAxisConfig) t);
    }
}
