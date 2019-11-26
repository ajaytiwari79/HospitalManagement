package com.kairos.service.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.shift.ProtectedDaysOffSetting;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementRuleTemplateBalancesDTO;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.dto.activity.wta.templates.ActivityCutOffCount;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
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
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftValidatorService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.constants.AppConstants.STOP_BRICK_BLOCKING_POINT;
import static com.kairos.enums.wta.WTATemplateType.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


@Service
public class WorkTimeAgreementBalancesCalculationService {

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
    @Inject private ShiftValidatorService shiftValidatorService;
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

    private static DateTimeInterval getIntervalByProtectedDaysOffRuleTemplate(LocalDate startDate, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, Map<BigInteger, ActivityWrapper> activityWrapperMap, ProtectedDaysOffSettingDTO protectedDaysOffSetting, LocalDate planningPeriodEndDate) {
        ActivityWrapper activityWrapper = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
        return getCutoffInterval(activityWrapper.getActivity().getRulesActivityTab().getCutOffStartFrom(), activityWrapper.getActivity().getRulesActivityTab().getCutOffIntervalUnit(), activityWrapper.getActivity().getRulesActivityTab().getCutOffdayValue(), asDate(startDate), ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR.equals(protectedDaysOffSetting.getProtectedDaysOffUnitSettings()) ? planningPeriodEndDate : DateUtils.getLocalDate());
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
                    break;
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

    public boolean isLeaveCountAvailable(Map<BigInteger,ActivityWrapper> activityWrapperMap,BigInteger activityId ,ShiftWithActivityDTO shift ,DateTimeInterval dateTimeInterval ,LocalDate lastPlanningPeriodEndDat , WTATemplateType wtaTemplateType , long leaveCount){
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByEmploymentId(shift.getUnitId(), dateTimeInterval.getStartLocalDate().plusDays(1), ORGANIZATION, shift.getEmploymentId(), new HashSet<>());
        boolean isLeaveCountAvailable=false;
        List<WTAQueryResultDTO> workingTimeAgreements = wtaRepository.getWTAByEmploymentIdAndDatesWithRuleTemplateType(shift.getEmploymentId(), shift.getStartDate(), shift.getEndDate(), wtaTemplateType);
        List<WTABaseRuleTemplate> ruleTemplates=workingTimeAgreements.get(0).getRuleTemplates();
        Activity activity = activityWrapperMap.get(activityId).getActivity();
        if(activity.getRulesActivityTab().isBorrowLeave()) {
            DateTimeInterval nextCutOffdateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(dateTimeInterval.getEndLocalDate().plusDays(1)), newArrayList(activityId), lastPlanningPeriodEndDat);
            List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentAndActivityIds(shift.getEmploymentId(), DateUtils.asDate(dateTimeInterval.getStart()), DateUtils.asDate(nextCutOffdateTimeInterval.getEnd()), newHashSet(activityId));
            for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
                switch (ruleTemplate.getWtaTemplateType()) {
                    case SENIOR_DAYS_PER_YEAR:
                        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                        CareDaysDTO seniorDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                        if(leaveCount+seniorDays.getLeavesAllowed()+seniorDaysPerYearWTATemplate.getTransferLeaveCount()-seniorDaysPerYearWTATemplate.getBorrowLeaveCount()<shiftWithActivityDTOS.size()){
                            seniorDaysPerYearWTATemplate.setBorrowLeaveCount(seniorDaysPerYearWTATemplate.getBorrowLeaveCount()+1);
                            isLeaveCountAvailable=true;
                        }
                        break;
                    case CHILD_CARE_DAYS_CHECK:
                        ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                        CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getStaffAge());
                        if(leaveCount+careDays.getLeavesAllowed()+childCareDaysCheckWTATemplate.getTransferLeaveCount()-childCareDaysCheckWTATemplate.getBorrowLeaveCount()<shiftWithActivityDTOS.size()){
                            childCareDaysCheckWTATemplate.setBorrowLeaveCount(childCareDaysCheckWTATemplate.getBorrowLeaveCount()+1);
                            isLeaveCountAvailable=true;
                        }
                        break;
                    case WTA_FOR_CARE_DAYS:
                        WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                        ActivityCareDayCount careDayCount = wtaForCareDays.careDaysCountMap().get(activity.getId());
                        ActivityCutOffCount activityLeaveCount=careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(),activityCutOffCount.getEndDate()).contains(nextCutOffdateTimeInterval.getStartLocalDate())).findFirst().get();
                        if(leaveCount+activityLeaveCount.getCount()+activityLeaveCount.getTransferLeaveCount()-activityLeaveCount.getBorrowLeaveCount()>shiftWithActivityDTOS.size()){
                            isLeaveCountAvailable=true;
                            activityLeaveCount.setBorrowLeaveCount(activityLeaveCount.getBorrowLeaveCount()+1);
                        }
                        break;
                    default:
                        break;
                }
            }
            wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);
        }
        return isLeaveCountAvailable;
    }

    public WorkTimeAgreementRuleTemplateBalancesDTO getProtectedDaysOffBalance(Long unitId, ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, Map<BigInteger, TimeType> timeTypeMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, LocalDate planningPeriodEndDate) {
        ProtectedDaysOffSettingDTO protectedDaysOffSettingOfUnit = protectedDaysOffService.getProtectedDaysOffByUnitId(unitId);
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        if (!ProtectedDaysOffUnitSettings.UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR.equals(protectedDaysOffSettingOfUnit.getProtectedDaysOffUnitSettings())) {
            ActivityWrapper activityWrapper = activityWrapperMap.get(protectedDaysOffWTATemplate.getActivityId());
            CutOffIntervalUnit cutOffIntervalUnit = activityWrapper.getActivity().getRulesActivityTab().getCutOffIntervalUnit();
            List<ProtectedDaysOffSetting> protectedDaysOffSettings = staffAdditionalInfoDTO.getEmployment().getExpertise().getProtectedDaysOffSettings();
            protectedDaysOffSettings = protectedDaysOffSettings.stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.getPublicHolidayDate().isAfter(staffAdditionalInfoDTO.getEmployment().getStartDate()) && (isNull(staffAdditionalInfoDTO.getEmployment().getEndDate()) ||  !protectedDaysOffSetting.getPublicHolidayDate().isAfter(staffAdditionalInfoDTO.getEmployment().getEndDate()))).collect(Collectors.toList());
            String activityName = activityWrapper.getActivity().getName();
            String timetypeColor = timeTypeMap.containsKey(activityWrapper.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) ? timeTypeMap.get(activityWrapper.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor() : "";
            while (!startDate.isAfter(endDate)) {
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
                workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityWrapper.getActivity().getId(),activityName, timetypeColor, intervalBalances, cutOffIntervalUnit,false);
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
        BigInteger activityId=BigInteger.ZERO;
        String timetypeColor = "";
        boolean borrowLeave=false;
        if (isNotNull(vetoAndStopBricksWTATemplate.getVetoActivityId()) && isNotNull(vetoAndStopBricksWTATemplate.getStopBrickActivityId())) {
            Activity activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getVetoActivityId()).getActivity();
            if (isNull(activity)) {
                activity = activityWrapperMap.get(vetoAndStopBricksWTATemplate.getStopBrickActivityId()).getActivity();
            }
            activityName = activity.getName();
            activityId=activity.getId();
            borrowLeave=activity.getRulesActivityTab().isBorrowLeave();
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
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId,activityName, timetypeColor, intervalBalances, CutOffIntervalUnit.WEEKS,borrowLeave);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }


    private WorkTimeAgreementRuleTemplateBalancesDTO getseniorDayRuleTemplateBalance(SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        BigInteger activityId=BigInteger.ZERO;
        boolean borrowLeave=false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(seniorDaysPerYearWTATemplate.getActivityIds())) {
            Activity activity = activityWrapperMap.get(seniorDaysPerYearWTATemplate.getActivityIds().get(0)).getActivity();
            activityName = activity.getName();
            activityId = activity.getId();
            borrowLeave=activity.getRulesActivityTab().isBorrowLeave();
            cutOffIntervalUnit = activity.getRulesActivityTab().getCutOffIntervalUnit();
            timetypeColor = timeTypeMap.get(activity.getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate);
                    if (isNotNull(dateTimeInterval)) {
                        int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, new HashSet<>(seniorDaysPerYearWTATemplate.getActivityIds()));
                        CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                        if (isNotNull(careDays)) {
                            intervalBalances.add(new IntervalBalance(careDays.getLeavesAllowed()+seniorDaysPerYearWTATemplate.getTransferLeaveCount()-seniorDaysPerYearWTATemplate.getBorrowLeaveCount(), scheduledAndApproveActivityCount[0], (careDays.getLeavesAllowed()+seniorDaysPerYearWTATemplate.getTransferLeaveCount()) - scheduledAndApproveActivityCount[0], dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                        }
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId,activityName, timetypeColor, intervalBalances, cutOffIntervalUnit,borrowLeave);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getchildCareDayRuleTemplateBalance(ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        BigInteger activityId=BigInteger.ZERO;
        boolean borrowLeave=false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(childCareDaysCheckWTATemplate.getActivityIds())) {
            Activity activity = activityWrapperMap.get(childCareDaysCheckWTATemplate.getActivityIds().get(0)).getActivity();
            activityName = activity.getName();
            activityId=activity.getId();
            borrowLeave=activity.getRulesActivityTab().isBorrowLeave();
            timetypeColor = timeTypeMap.get(activity.getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor();
            cutOffIntervalUnit = activity.getRulesActivityTab().getCutOffIntervalUnit();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate);
                    if (isNotNull(dateTimeInterval)) {
                        int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, new HashSet(childCareDaysCheckWTATemplate.getActivityIds()));
                        long totalLeaves = childCareDaysCheckWTATemplate.calculateChildCareDaysLeaveCount(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), shiftValidatorService.getChildAges(asDate(startDate), staffAdditionalInfoDTO));
                        intervalBalances.add(new IntervalBalance(totalLeaves+childCareDaysCheckWTATemplate.getTransferLeaveCount()-childCareDaysCheckWTATemplate.getBorrowLeaveCount(), scheduledAndApproveActivityCount[0], totalLeaves+childCareDaysCheckWTATemplate.getTransferLeaveCount() - scheduledAndApproveActivityCount[0], dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId,activityName, timetypeColor, intervalBalances, cutOffIntervalUnit,borrowLeave);
        }
        return workTimeAgreementRuleTemplateBalancesDTO;
    }

    private WorkTimeAgreementRuleTemplateBalancesDTO getWtaForCareDayRuleTemplateBalance(WTAForCareDays wtaForCareDays, List<ShiftWithActivityDTO> shiftWithActivityDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate startDate, LocalDate endDate, Map<BigInteger, TimeType> timeTypeMap, LocalDate planningPeriodEndDate) {
        List<IntervalBalance> intervalBalances = new ArrayList<>();
        WorkTimeAgreementRuleTemplateBalancesDTO workTimeAgreementRuleTemplateBalancesDTO = null;
        //TODO We will remove that when TimeType functionality implement in WTARuletemplate
        String activityName = "";
        String timetypeColor = "";
        BigInteger activityId=BigInteger.ZERO;
        boolean borrowLeave=false;
        CutOffIntervalUnit cutOffIntervalUnit = null;
        if (isCollectionNotEmpty(wtaForCareDays.getCareDayCounts())) {
            Activity activity = activityWrapperMap.get(wtaForCareDays.getCareDayCounts().get(0).getActivityId()).getActivity();
            activityName = activity.getName();
            activityId=activity.getId();
            borrowLeave=activity.getRulesActivityTab().isBorrowLeave();
            cutOffIntervalUnit = activity.getRulesActivityTab().getCutOffIntervalUnit();
            timetypeColor = timeTypeMap.get(activity.getBalanceSettingsActivityTab().getTimeTypeId()).getBackgroundColor();
            while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
                if (!containsInInterval(intervalBalances, startDate)) {
                    DateTimeInterval dateTimeInterval = getIntervalByActivity(activityWrapperMap, asDate(startDate), Arrays.asList(wtaForCareDays.getCareDayCounts().get(0).getActivityId()), planningPeriodEndDate);
                    if (isNotNull(dateTimeInterval)) {
                        int[] scheduledAndApproveActivityCount = getShiftsActivityCountByInterval(dateTimeInterval, shiftWithActivityDTOS, newHashSet(wtaForCareDays.getCareDayCounts().get(0).getActivityId()));
                        ActivityCutOffCount activityLeaveCount=wtaForCareDays.getCareDayCounts().get(0).getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(),activityCutOffCount.getEndDate()).contains(dateTimeInterval.getStartLocalDate())).findFirst().get();
                        intervalBalances.add(new IntervalBalance(activityLeaveCount.getCount()+activityLeaveCount.getTransferLeaveCount()-activityLeaveCount.getBorrowLeaveCount(), scheduledAndApproveActivityCount[0], activityLeaveCount.getCount()+activityLeaveCount.getTransferLeaveCount() - scheduledAndApproveActivityCount[0], dateTimeInterval.getStartLocalDate(), dateTimeInterval.getEndLocalDate().minusDays(1), scheduledAndApproveActivityCount[1]));
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (isCollectionNotEmpty(intervalBalances)) {
            workTimeAgreementRuleTemplateBalancesDTO = new WorkTimeAgreementRuleTemplateBalancesDTO(activityId,activityName, timetypeColor, intervalBalances, cutOffIntervalUnit,borrowLeave);
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

    public boolean updateLeaveCountByJob(){
        LocalDate startDate=getLocalDate();
        List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaRepository.getAllWTAByDate(new Date());
        Map<Long,List<WTAQueryResultDTO>> unitIdAndWtaMap=wtaQueryResultDTOS.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getOrganization().getId(),Collectors.toList()));
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream()).filter(wtaBaseRuleTemplate -> newHashSet(CHILD_CARE_DAYS_CHECK,SENIOR_DAYS_PER_YEAR,WTA_FOR_CARE_DAYS).contains(wtaBaseRuleTemplate.getWtaTemplateType())).collect(Collectors.toList());
        Set<BigInteger> activityIds = getActivityIdsByRuletemplates(wtaBaseRuleTemplates);
        List<ActivityWrapper> activityWrappers = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityWrappers.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        List<WTABaseRuleTemplate> ruleTemplates=new ArrayList<>();
        for (Long unitId : unitIdAndWtaMap.keySet()) {
            try {
                List<WTAQueryResultDTO> wtaQueryResultDTOSOfUnit = unitIdAndWtaMap.get(unitId);
                Map<Long, List<WTAQueryResultDTO>> employmentIdAndWtaMap = wtaQueryResultDTOSOfUnit.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getEmploymentId(), Collectors.toList()));
                PlanningPeriod planningPeriod = planningPeriodMongoRepository.findLastPlaningPeriodEndDate(unitId);
                DateTimeInterval dateTimeInterval = getIntervalByRuletemplates(activityWrapperMap, wtaBaseRuleTemplates, startDate, planningPeriod.getEndDate(), unitId);
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmployments(employmentIdAndWtaMap.keySet(), dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate(), activityIds);
                Map<Long, List<ShiftWithActivityDTO>> staffIdAndShiftsMap = shiftWithActivityDTOS.stream().collect(Collectors.groupingBy(shiftWithActivityDTO -> shiftWithActivityDTO.getStaffId(), Collectors.toList()));
                Map<Long, List<ShiftWithActivityDTO>> employmentIdAndShiftsMap = shiftWithActivityDTOS.stream().collect(Collectors.groupingBy(shiftWithActivityDTO -> shiftWithActivityDTO.getEmploymentId(), Collectors.toList()));
                List<Long> staffIds = new ArrayList<>(staffIdAndShiftsMap.keySet());
                List<Long> employmentIds = new ArrayList<>(employmentIdAndWtaMap.keySet());
                List<NameValuePair> requestParam = new ArrayList<>();
                requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
                requestParam.add(new BasicNameValuePair("employmentIds", employmentIds.toString()));
                List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(unitId, requestParam);
                Map<Long, StaffAdditionalInfoDTO> employmentIdAndStaffAdditionalInfoMap = new HashMap<>();
                for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
                    employmentIdAndStaffAdditionalInfoMap.putIfAbsent(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO);
                }
                for (Long employmentId : employmentIdAndWtaMap.keySet()) {
                    try {
                        List<WTAQueryResultDTO> wtaQueryResuls = employmentIdAndWtaMap.get(employmentId);
                        for (WTAQueryResultDTO wtaQueryResul : wtaQueryResuls) {
                            ruleTemplates.addAll(getRuleTemplates(activityWrapperMap, wtaQueryResul.getRuleTemplates(), employmentIdAndShiftsMap.get(employmentId), employmentIdAndStaffAdditionalInfoMap.get(employmentId), planningPeriod.getEndDate()));
                        }
                    }catch (Exception e){

                    }
                }
            }catch (Exception e){

            }
        }
        if(isCollectionNotEmpty(ruleTemplates)){wtaBaseRuleTemplateRepository.saveEntities(ruleTemplates);}
        return true;
    }

    public  List<WTABaseRuleTemplate> getRuleTemplates(Map<BigInteger, ActivityWrapper> activityWrapperMap, List<WTABaseRuleTemplate> ruleTemplates,List<ShiftWithActivityDTO> shiftWithActivityDTOS,StaffAdditionalInfoDTO staffAdditionalInfoDTO ,LocalDate planningPeriodEndDate){
        Activity activity = null;
        DateTimeInterval dateTimeInterval=null;
        for (WTABaseRuleTemplate ruleTemplate : ruleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    CareDaysDTO seniorDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), staffAdditionalInfoDTO.getStaffAge());
                    activity=activityWrapperMap.get(seniorDaysPerYearWTATemplate.getActivityIds().get(0)).getActivity();
                    dateTimeInterval = getIntervalByActivity(activityWrapperMap, getDate(), seniorDaysPerYearWTATemplate.getActivityIds(), planningPeriodEndDate);
                    if(dateTimeInterval.getEndDate().equals(getLocalDate()) && CutOffIntervalUnit.CutOffBalances.TRANSFER.equals(activity.getRulesActivityTab().getCutOffBalances())) {
                        shiftWithActivityDTOS = filterShiftsByDateTimeIntervalAndActivityId(shiftWithActivityDTOS, dateTimeInterval, activity.getId());
                        int totalCount = seniorDays.getLeavesAllowed()+seniorDaysPerYearWTATemplate.getTransferLeaveCount()-seniorDaysPerYearWTATemplate.getBorrowLeaveCount();
                        int count=totalCount-shiftWithActivityDTOS.size();
                        seniorDaysPerYearWTATemplate.setTransferLeaveCount(seniorDays.getLeavesAllowed()<count ? seniorDays.getLeavesAllowed():count);
                    }
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    CareDaysDTO careDays = getCareDays(staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getStaffAge());
                    activity=activityWrapperMap.get(childCareDaysCheckWTATemplate.getActivityIds().get(0)).getActivity();
                    dateTimeInterval = getIntervalByActivity(activityWrapperMap, getDate(), childCareDaysCheckWTATemplate.getActivityIds(), planningPeriodEndDate);
                    if(dateTimeInterval.getEndDate().equals(getLocalDate()) && CutOffIntervalUnit.CutOffBalances.TRANSFER.equals(activity.getRulesActivityTab().getCutOffBalances())) {
                        shiftWithActivityDTOS = filterShiftsByDateTimeIntervalAndActivityId(shiftWithActivityDTOS, dateTimeInterval, activity.getId());
                        int totalCount = careDays.getLeavesAllowed()+childCareDaysCheckWTATemplate.getTransferLeaveCount()-childCareDaysCheckWTATemplate.getBorrowLeaveCount();
                        int count=totalCount-shiftWithActivityDTOS.size();
                        childCareDaysCheckWTATemplate.setTransferLeaveCount(careDays.getLeavesAllowed()<count ? careDays.getLeavesAllowed():count);
                    }
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    ActivityCareDayCount careDayCount = wtaForCareDays.careDaysCountMap().get(activity.getId());
                    ActivityCutOffCount activityLeaveCount=careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(),activityCutOffCount.getEndDate()).contains(getLocalDate())).findFirst().get();
                    activity = activityWrapperMap.get(careDayCount.getActivityId()).getActivity();
                    if(activityLeaveCount.getEndDate().equals(getLocalDate()) && CutOffIntervalUnit.CutOffBalances.TRANSFER.equals(activity.getRulesActivityTab().getCutOffBalances())) {
                        shiftWithActivityDTOS = filterShiftsByDateTimeIntervalAndActivityId(shiftWithActivityDTOS, dateTimeInterval, activity.getId());
                        int totalCount =activityLeaveCount.getCount()+activityLeaveCount.getTransferLeaveCount()-activityLeaveCount.getBorrowLeaveCount();
                        int count=totalCount-shiftWithActivityDTOS.size();
                        activityLeaveCount.setTransferLeaveCount(activityLeaveCount.getCount()<count ? activityLeaveCount.getCount():count);

                    }
                    break;
                default:
                    break;
            }
        }
        return ruleTemplates;
    }

    public List<ShiftWithActivityDTO> filterShiftsByDateTimeIntervalAndActivityId(List<ShiftWithActivityDTO> shiftWithActivityDTOS,DateTimeInterval dateTimeInterval,BigInteger activityId){
        return shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> dateTimeInterval.contains(shiftWithActivityDTO.getStartDate())&& shiftWithActivityDTO.getActivities().stream().anyMatch(shiftActivityDTO -> shiftActivityDTO.getActivityId().equals(activityId))).collect(Collectors.toList());
    }

}
