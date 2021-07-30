package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementRuleTemplateBalancesDTO;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.templates.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.ExceptionService;
import com.kairos.persistence.repository.counter.CounterHelperRepository;
import com.kairos.utils.CPRUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.EVERYDAY;
import static com.kairos.constants.AppConstants.STOP_BRICK_BLOCKING_POINT;
import static com.kairos.constants.KPIMessagesConstants.*;
import static com.kairos.enums.wta.WTATemplateType.*;
import static com.kairos.service.TimeBankService.isPublicHolidayValid;
import static com.kairos.utils.CPRUtil.getAgeByCPRNumberAndStartDate;

@Service
public class WorkTimeAgreementBalancesCalculationService implements KPIService{

    @Inject private CounterHelperRepository counterHelperRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private UserIntegrationService userIntegrationService;

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getLeaveCount(staffId, dateTimeInterval, kpiCalculationRelatedInfo, (YAxisConfig) t);
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
    public DateTimeInterval getIntervalByRuletemplates(Set<WTATemplateType> wtaTemplateTypes, Map<BigInteger, ActivityDTO> activityMap, List<WTABaseRuleTemplateDTO> WTARuleTemplates, LocalDate startDate, LocalDate planningPeriodEndDate, Long unitId) {
        DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplates) {
            if(isCollectionEmpty(wtaTemplateTypes) || wtaTemplateTypes.contains(ruleTemplate.getWtaTemplateType())){
                switch (ruleTemplate.getWtaTemplateType()) {
                    case VETO_AND_STOP_BRICKS:
                        VetoAndStopBricksWTATemplateDTO vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplateDTO) ruleTemplate;
                        validateRuleTemplate(vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate());
                        interval = interval.addInterval(getIntervalByNumberOfWeeks(asDate(startDate), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate(), planningPeriodEndDate));
                        break;
                    case SENIOR_DAYS_PER_YEAR:
                        SeniorDaysPerYearWTATemplateDTO seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplateDTO) ruleTemplate;
                        interval = interval.addInterval(getIntervalByActivity(activityMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate));
                        break;
                    case CHILD_CARE_DAYS_CHECK:
                        ChildCareDaysCheckWTATemplateDTO childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplateDTO) ruleTemplate;
                        interval = interval.addInterval(getIntervalByActivity(activityMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate));
                        break;
                    case WTA_FOR_CARE_DAYS:
                        WTAForCareDaysDTO wtaForCareDays = (WTAForCareDaysDTO) ruleTemplate;
                        interval = interval.addInterval(getIntervalByWTACareDaysRuleTemplate(startDate, wtaForCareDays, activityMap, planningPeriodEndDate));
                        break;
                    case PROTECTED_DAYS_OFF:
                        ProtectedDaysOffWTATemplateDTO protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplateDTO) ruleTemplate;
                        ProtectedDaysOffSettingDTO protectedDaysOffSetting = counterHelperRepository.getProtectedDaysOffByUnitId(unitId);
                        interval = interval.addInterval(getIntervalByProtectedDaysOffRuleTemplate(startDate, protectedDaysOffWTATemplate, activityMap, protectedDaysOffSetting, planningPeriodEndDate));
                        break;
                    default:
                        break;
                }
            }
        }
        return interval;
    }

    public static void validateRuleTemplate(int numberOfWeeks, LocalDate validationStartDate) {
        if (numberOfWeeks == 0 || validationStartDate == null) {
            throwException(MESSAGE_RULETEMPLATE_WEEKS_NOTNULL);
        }
    }

    private DateTimeInterval getIntervalByProtectedDaysOffRuleTemplate(LocalDate startDate, ProtectedDaysOffWTATemplateDTO protectedDaysOffWTATemplate, Map<BigInteger, ActivityDTO> activityWrapperMap, ProtectedDaysOffSettingDTO protectedDaysOffSetting, LocalDate planningPeriodEndDate) {
        if(isNull(protectedDaysOffWTATemplate.getActivityId())){
            exceptionService.invalidRequestException(ACTIVITY_NOT_ASSIGN_IN_PROTECTED_DAYS_OFF_RULE_TEMPLATE,protectedDaysOffWTATemplate.getName(),protectedDaysOffWTATemplate.getId());
        }
        ActivityDTO activity = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
        return getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffSetting.getProtectedDaysOffUnitSettings()) ? planningPeriodEndDate : DateUtils.getLocalDate());
    }

    public Set<BigInteger> getActivityIdsByRuletemplates(List<WTABaseRuleTemplateDTO> WTARuleTemplates, BigInteger activityId) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : WTARuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplateDTO vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplateDTO) ruleTemplate;
                    activityIds.add(vetoAndStopBricksWTATemplate.getStopBrickActivityId());
                    activityIds.add(vetoAndStopBricksWTATemplate.getVetoActivityId());
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplateDTO seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplateDTO) ruleTemplate;
                    if(isNull(activityId) || seniorDaysPerYearWTATemplate.getActivityIds().contains(activityId)) {
                        activityIds.addAll(seniorDaysPerYearWTATemplate.getActivityIds());
                    }
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplateDTO childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplateDTO) ruleTemplate;
                    if(isNull(activityId) || childCareDaysCheckWTATemplate.getActivityIds().contains(activityId)) {
                        activityIds.addAll(childCareDaysCheckWTATemplate.getActivityIds());
                    }
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDaysDTO wtaForCareDays = (WTAForCareDaysDTO) ruleTemplate;
                    Set<BigInteger> wtaForCareDayActivityIds = wtaForCareDays.getCareDayCounts().stream().map(activityCareDayCount -> activityCareDayCount.getActivityId()).collect(Collectors.toSet());
                    if(isNull(activityId) || wtaForCareDayActivityIds.contains(activityId)) {
                        activityIds.addAll(wtaForCareDayActivityIds);
                    }
                    break;
                case PROTECTED_DAYS_OFF:
                    ProtectedDaysOffWTATemplateDTO protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplateDTO) ruleTemplate;
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
        staffAdditionalInfoDTO.setDayTypes(counterHelperRepository.findAllByCountryIdAndDeletedFalse(UserContext.getUserDetails().getCountryId()));
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException(MESSAGE_STAFF_NOTFOUND);
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_ABSENT);
        }
        List<WTAResponseDTO> wtaQueryResultDTOS = counterHelperRepository.getWTAByEmploymentIdAndDates(employmentId, asDate(startDate), asDate(endDate));
        List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).collect(Collectors.toList());
        Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates, activityId);
        List<ActivityDTO> activityDTOS = counterHelperRepository.findAllActivitiesByIds(activityIds);
        Map<BigInteger, ActivityDTO> activityMap = activityDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        DateTimeInterval planningPeriod = counterHelperRepository.getPlanningPeriodIntervalByUnitId(unitId);
        DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(wtaTemplateTypes,activityMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndLocalDate(), unitId);
        List<ShiftActivityDTO> validShiftActivityDTOSByDayType = getshiftActivityDTOS(employmentId, staffAdditionalInfoDTO, activityIds, activityMap, dateTimeInterval);
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances = getWorkTimeAgreementRuleTemplateBalances(unitId, startDate, endDate, staffAdditionalInfoDTO, wtaBaseRuleTemplates, activityMap, planningPeriod, validShiftActivityDTOSByDayType, wtaTemplateTypes);
        WorkTimeAgreementBalance workTimeAgreementBalance = new WorkTimeAgreementBalance(workTimeAgreementRuleTemplateBalances);
        return workTimeAgreementBalance;
    }

    private List<ShiftActivityDTO> getshiftActivityDTOS(Long employmentId, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Set<BigInteger> activityIds, Map<BigInteger, ActivityDTO> activityWrapperMap, DateTimeInterval dateTimeInterval) {
        List<ShiftActivityDTO> shiftActivityDTOS = counterHelperRepository.findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(employmentId, dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
        Map<BigInteger , DayTypeDTO> dayTypeDTOMap =staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, dayTypeDTO -> dayTypeDTO));
        return shiftActivityDTOS.parallelStream().filter(shiftActivityDTO -> (activityWrapperMap.containsKey(shiftActivityDTO.getActivityId()) && isDayTypeValid(shiftActivityDTO.getStartDate(), activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivityTimeCalculationSettings().getDayTypes(), dayTypeDTOMap))).collect(Collectors.toList());
    }


    public List<WorkTimeAgreementRuleTemplateBalancesDTO> getWorkTimeAgreementRuleTemplateBalances(Long unitId, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplates, Map<BigInteger, ActivityDTO> activityMap, DateTimeInterval planningPeriod, List<ShiftActivityDTO> shiftActivityDTOS, Set<WTATemplateType> wtaTemplateTypes) {
        List<WorkTimeAgreementRuleTemplateBalancesDTO> workTimeAgreementRuleTemplateBalances=new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO;
        for (WTABaseRuleTemplateDTO ruleTemplate : wtaBaseRuleTemplates) {
            if(isCollectionEmpty(wtaTemplateTypes) || wtaTemplateTypes.contains(ruleTemplate.getWtaTemplateType())) {
                switch (ruleTemplate.getWtaTemplateType()) {
                    case VETO_AND_STOP_BRICKS:
                        workTimeAgreementRuleTemplateBalancesDTO = getVetoRuleTemplateBalance((VetoAndStopBricksWTATemplateDTO) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate,  planningPeriod.getEndLocalDate());
                        break;
                    case SENIOR_DAYS_PER_YEAR:
                        workTimeAgreementRuleTemplateBalancesDTO = getseniorDayRuleTemplateBalance((SeniorDaysPerYearWTATemplateDTO) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate, staffAdditionalInfoDTO,  planningPeriod.getEndLocalDate());
                        break;
                    case CHILD_CARE_DAYS_CHECK:
                        workTimeAgreementRuleTemplateBalancesDTO = getchildCareDayRuleTemplateBalance((ChildCareDaysCheckWTATemplateDTO) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate, staffAdditionalInfoDTO,  planningPeriod.getEndLocalDate());
                        break;
                    case WTA_FOR_CARE_DAYS:
                        workTimeAgreementRuleTemplateBalancesDTO = getWtaForCareDayRuleTemplateBalance((WTAForCareDaysDTO) ruleTemplate, shiftActivityDTOS, activityMap, startDate, endDate,  planningPeriod.getEndLocalDate());
                        break;
                    case PROTECTED_DAYS_OFF:
                        workTimeAgreementRuleTemplateBalancesDTO = getProtectedDaysOffBalance(unitId, (ProtectedDaysOffWTATemplateDTO) ruleTemplate, shiftActivityDTOS, activityMap,  staffAdditionalInfoDTO, startDate, endDate, planningPeriod.getEndLocalDate());
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

    public WorkTimeAgreementRuleTemplateBalancesDTO getProtectedDaysOffBalance(Long unitId, ProtectedDaysOffWTATemplateDTO protectedDaysOffWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityMap,  StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate) {
        ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit = counterHelperRepository.getProtectedDaysOffByUnitId(unitId);
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        if (!ProtectedDaysOffUnitSettings.UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR.equals(protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings())) {
            ActivityDTO activity = activityMap.get(protectedDaysOffWTATemplate.getActivityId());
            CutOffIntervalUnit cutOffIntervalUnit = activity.getActivityRulesSettings().getCutOffIntervalUnit();
            List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings = counterHelperRepository.getProtectedDaysOffByExpertiseId(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
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

    private void getProtectedDaysOffIntervalbalance(ProtectedDaysOffWTATemplateDTO protectedDaysOffWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate, ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit, List<IntervalBalance> intervalBalances, ActivityDTO activity, CutOffIntervalUnit cutOffIntervalUnit, List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings) {
        while (!startDate.isAfter(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getCutoffInterval(activity.getActivityRulesSettings().getCutOffStartFrom(), activity.getActivityRulesSettings().getCutOffIntervalUnit(), activity.getActivityRulesSettings().getCutOffdayValue(), asDate(startDate), planningPeriodEndDate);
                getProtectedDaysOfCountByInterval(protectedDaysOffWTATemplate, shiftActivityDTOS, staffAdditionalInfoDTO, startDate, protectedDaysOffSettingOfUnit, intervalBalances, activity, cutOffIntervalUnit, protectedDaysOffSettings, dateTimeInterval);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private void getProtectedDaysOfCountByInterval(ProtectedDaysOffWTATemplateDTO protectedDaysOffWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit, List<IntervalBalance> intervalBalances, ActivityDTO activity, CutOffIntervalUnit cutOffIntervalUnit, List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings, DateTimeInterval dateTimeInterval) {
        if (isNotNull(dateTimeInterval)) {
            Object[] countAndDate = getProtectedDaysOffCountAndDate(protectedDaysOffSettings, dateTimeInterval, protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings(), cutOffIntervalUnit, activity.getActivityRulesSettings().getCutOffdayValue(), startDate);
            long count = Long.parseLong(countAndDate[0].toString());
            LocalDate protectedStartDate = (LocalDate) countAndDate[1];
            if (isNotNull(protectedStartDate)) {
                shiftActivityDTOS = counterHelperRepository.findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(staffAdditionalInfoDTO.getEmployment().getId(), asDate(protectedStartDate), dateTimeInterval.getEndDate(), newHashSet(protectedDaysOffWTATemplate.getActivityId()));
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

    private WorkTimeAgreementRuleTemplateBalancesDTO getVetoRuleTemplateBalance(VetoAndStopBricksWTATemplateDTO vetoAndStopBricksWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate,  LocalDate planningPeriodEndDate) {
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

    private void getVetoIntervalBalance(VetoAndStopBricksWTATemplateDTO vetoAndStopBricksWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
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

    private void updateBalanceByInterval(VetoAndStopBricksWTATemplateDTO vetoAndStopBricksWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, LocalDate startDate, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
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

    public static DateTimeInterval getIntervalByNumberOfWeeks(Date startDate, int numberOfWeeks, LocalDate validationStartDate, LocalDate planningPeriodEndDate) {
        if (numberOfWeeks == 0 || validationStartDate == null) {
            throwException(MESSAGE_RULETEMPLATE_WEEKS_NOTNULL);
        }
        DateTimeInterval dateTimeInterval = null;
        while (validationStartDate.isBefore(planningPeriodEndDate) || validationStartDate.equals(planningPeriodEndDate)) {
            dateTimeInterval = new DateTimeInterval(asDate(validationStartDate), asDate(validationStartDate.plusWeeks(numberOfWeeks)));
            if (dateTimeInterval.contains(startDate)) {
                break;
            }
            validationStartDate = validationStartDate.plusWeeks(numberOfWeeks);

        }
        return dateTimeInterval;
    }

    private float[] getCountOfVetoAndStopBricks(ShiftActivityDTO shiftActivityDTO, VetoAndStopBricksWTATemplateDTO vetoAndStopBricksWTATemplate) {
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


    private WorkTimeAgreementRuleTemplateBalancesDTO getseniorDayRuleTemplateBalance(SeniorDaysPerYearWTATemplateDTO seniorDaysPerYearWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO,  LocalDate planningPeriodEndDate) {
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

    private void getSeniorDayIntevalBalance(SeniorDaysPerYearWTATemplateDTO seniorDaysPerYearWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate);
                getSeniorDayCountByInterval(seniorDaysPerYearWTATemplate, shiftActivityDTOS, startDate, staffAdditionalInfoDTO, intervalBalances, dateTimeInterval);
            }
            startDate = startDate.plusDays(1);
        }
    }

    private void getSeniorDayCountByInterval(SeniorDaysPerYearWTATemplateDTO seniorDaysPerYearWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, LocalDate startDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<IntervalBalance> intervalBalances, DateTimeInterval dateTimeInterval) {
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

    private WorkTimeAgreementRuleTemplateBalancesDTO getchildCareDayRuleTemplateBalance(ChildCareDaysCheckWTATemplateDTO childCareDaysCheckWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO,  LocalDate planningPeriodEndDate) {
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

    private void getChildCareIntervalBalance(ChildCareDaysCheckWTATemplateDTO childCareDaysCheckWTATemplate, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate, List<IntervalBalance> intervalBalances) {
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            if (!containsInInterval(intervalBalances, startDate)) {
                DateTimeInterval dateTimeInterval = getIntervalByActivity(activityMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate);
                if (isNotNull(dateTimeInterval)) {
                    int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftActivityDTOS, new HashSet(childCareDaysCheckWTATemplate.getActivityIds()));
                    long totalLeaves = calculateChildCareDaysLeaveCount(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), getChildAges(asDate(startDate), staffAdditionalInfoDTO));
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

    public long calculateChildCareDaysLeaveCount(List<CareDaysDTO> careDaysDTOS, List<Integer> staffChildAges){
        long leaveCount = 0L;
        if (isCollectionNotEmpty(staffChildAges)) {
            for (Integer staffChildAge : staffChildAges) {
                CareDaysDTO careDaysDTO = getCareDays(careDaysDTOS,staffChildAge);
                if(isNotNull(careDaysDTO)){
                    leaveCount+=careDaysDTO.getLeavesAllowed();
                }
            }
        }
        return leaveCount;
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getWtaForCareDayRuleTemplateBalance(WTAForCareDaysDTO wtaForCareDays, List<ShiftActivityDTO> shiftActivityDTOS, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate) {
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

    public List<Integer> getChildAges(Date shiftStartDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<Integer> staffChildAges = new ArrayList<>();
        for (StaffChildDetailDTO staffChildDetailDTO : staffAdditionalInfoDTO.getStaffChildDetails()) {
            staffChildAges.add(getAgeByCPRNumberAndStartDate(staffChildDetailDTO.getCprNumber(), asLocalDate(shiftStartDate)));
        }
        return staffChildAges;
    }

    private void getWtACareDayCountByInterval(WTAForCareDaysDTO wtaForCareDays, List<ShiftActivityDTO> shiftActivityDTOS, List<IntervalBalance> intervalBalances, DateTimeInterval dateTimeInterval) {
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

    public static DateTimeInterval getIntervalByWTACareDaysRuleTemplate(LocalDate startDate, WTAForCareDaysDTO wtaForCareDays, Map<BigInteger, ActivityDTO> activityWrapperMap, LocalDate planningPeriodEndDate) {
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

    public List<WTABaseRuleTemplateDTO> getRuleTemplates(Map<BigInteger, ActivityDTO> activityMap, List<WTABaseRuleTemplateDTO> ruleTemplates, List<ShiftWithActivityDTO> shiftActivityDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate planningPeriodEndDate) {
        Date date = asDate(getLocalDate().minusDays(1));
        LocalDate currentDate = getLocalDate().minusDays(1);
        for (WTABaseRuleTemplateDTO ruleTemplate : ruleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplateDTO seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplateDTO) ruleTemplate;
                    CareDaysDTO seniorDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                    updateLeaveCountInSeniorDaysOrChildCareDaysWTA(activityMap, shiftActivityDTOS, planningPeriodEndDate, date, currentDate, seniorDays, seniorDaysPerYearWTATemplate.getActivityIds(), seniorDaysPerYearWTATemplate.getActivityCutOffCounts());
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplateDTO childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplateDTO) ruleTemplate;
                    CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getStaffAge());
                    updateLeaveCountInSeniorDaysOrChildCareDaysWTA(activityMap, shiftActivityDTOS, planningPeriodEndDate, date, currentDate, careDays, childCareDaysCheckWTATemplate.getActivityIds(), childCareDaysCheckWTATemplate.getActivityCutOffCounts());
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDaysDTO wtaForCareDays = (WTAForCareDaysDTO) ruleTemplate;
                    updateLeaveCountCareDaysWTA(activityMap, shiftActivityDTOS, date, currentDate, wtaForCareDays);
                    break;
                default:
                    break;
            }
        }
        return ruleTemplates;
    }

    private void updateLeaveCountCareDaysWTA(Map<BigInteger, ActivityDTO> activityWrapperMap, List<ShiftWithActivityDTO> shiftActivityDTOS, Date date, LocalDate currentDate, WTAForCareDaysDTO wtaForCareDays) {
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

    public static CareDaysDTO getCareDays(List<CareDaysDTO> careDaysDTOS, int age) {
        CareDaysDTO staffCareDaysDTO = null;
        for (CareDaysDTO careDaysDTO : careDaysDTOS) {
            if (careDaysDTO.getTo() == null && age >= careDaysDTO.getFrom() || (isNotNull(careDaysDTO.getTo()) && careDaysDTO.getFrom() <= age && careDaysDTO.getTo() > age)) {
                staffCareDaysDTO = careDaysDTO;
            }
        }
        return staffCareDaysDTO;
    }

}
