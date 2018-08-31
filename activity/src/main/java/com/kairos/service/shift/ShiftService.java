package com.kairos.service.shift;

import com.kairos.activity.cta.CTABasicDetailsDTO;
import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.activity.shift.*;
import com.kairos.activity.staffing_level.StaffingLevelActivity;
import com.kairos.activity.staffing_level.StaffingLevelInterval;
import com.kairos.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.enums.Day;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.ActivityAndShiftStatusSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.activity.tabs.CompositeActivity;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.TimeTypeMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ActivityAndShiftStatusSettingsRepository;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.response.dto.web.shift.IndividualShiftTemplateDTO;
import com.kairos.rest_client.*;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.*;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.locale.LocaleService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.unit_settings.PhaseSettingsService;
import com.kairos.service.wta.WTAService;
import com.kairos.user.access_group.UserAccessRoleDTO;
import com.kairos.user.access_permission.AccessGroupRole;
import com.kairos.user.access_permission.StaffAccessGroupDTO;
import com.kairos.user.country.experties.AppliedFunctionDTO;
import com.kairos.user.staff.staff.StaffAccessRoleDTO;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.WTARuleTemplateValidatorUtility;
import com.kairos.util.event.ShiftNotificationEvent;
import com.kairos.util.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.DateWiseShiftResponse;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.util.DateUtils.MONGODB_QUERY_DATE_FORMAT;
import static com.kairos.util.DateUtils.ONLY_DATE;
import static com.kairos.util.WTARuleTemplateValidatorUtility.getIntervalByRuleTemplates;
import static javafx.scene.input.KeyCode.V;
import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * Created by vipul on 30/8/17.
 */
@Service
public class ShiftService extends MongoBaseService {
    private final Logger logger = LoggerFactory.getLogger(ShiftService.class);

    @Inject
    private
    ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityMongoRepository activityRepository;

    @Inject
    private StaffRestClient staffRestClient;
    @Autowired
    private ApplicationContext applicationContext;
    @Inject
    private CountryRestClient countryRestClient;

    @Inject
    private PhaseService phaseService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private PayOutService payOutService;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private WTAService wtaService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OpenShiftMongoRepository openShiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private StaffWTACounterRepository wtaCounterRepository;
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private WorkingTimeAgreementMongoRepository wtaMongoRepository;
    @Inject
    private GenericIntegrationService restClient;
    @Inject
    private PhaseSettingsService phaseSettingsService;
    @Inject
    private PhaseSettingsRepository phaseSettingsRepository;
    @Inject
    private LocaleService localeService;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ShiftTemplateRepository shiftTemplateRepository;
    @Inject
    private IndividualShiftTemplateRepository individualShiftTemplateRepository;
    @Inject
    private ActivityConfigurationRepository activityConfigurationRepository;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;

    @Inject private OpenShiftNotificationMongoRepository openShiftNotificationMongoRepository;

    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private ActivityAndShiftStatusSettingsRepository activityAndShiftStatusSettingsRepository;
    @Inject private GenericRestClient genericRestClient;



    public List<ShiftQueryResult> createShift(Long organizationId, ShiftDTO shiftDTO, String type, boolean bySubShift) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        List<ShiftQueryResult> shiftQueryResults = null;
        if ((activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) && (!bySubShift)) {
            if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
                Date endDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).plusDays(1).withTimeAtStartOfDay().toDate();
                Date startDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).withTimeAtStartOfDay().toDate();
                List<ShiftQueryResult> shifts = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
                shiftDTO = calculateAverageShiftByActivity(shifts, activity, staffAdditionalInfoDTO, DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate());
                ShiftQueryResult shiftQueryResult = saveShift(activity, activityWrapper.getTimeType(), staffAdditionalInfoDTO, shiftDTO);
                shiftQueryResults = Collections.singletonList(shiftQueryResult);
            }
            if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
                Date shiftFromDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate();
                shiftQueryResults = getAverageOfShiftByActivity(staffAdditionalInfoDTO, activity, activityWrapper.getTimeType(), shiftFromDate);
            }
        } else {
            List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPosition(shiftDTO.getUnitPositionId(), shiftDTO.getStartDate(), shiftDTO.getEndDate());
            if (!shifts.isEmpty()) {
                exceptionService.duplicateDataException("message.shift.date.startandend", shifts.get(0).getStartDate(), shifts.get(0).getEndDate());
            }

            if (shiftDTO.getStartDate().after(shiftDTO.getEndDate())) {
                exceptionService.invalidRequestException("message.date.startandend");
            }
            ShiftQueryResult shiftQueryResult = saveShift(activity, activityWrapper.getTimeType(), staffAdditionalInfoDTO, shiftDTO);
            shiftQueryResults = Arrays.asList(shiftQueryResult);

        }
        return shiftQueryResults;
    }

    private ShiftQueryResult saveShift(Activity activity, String timeType, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO) {
        Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getStartDate());
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activity);

        Phase phase = phaseService.getPhaseCurrentByUnit(shiftDTO.getUnitId(), shiftDTO.getStartDate());
        shiftWithActivityDTO.setPlannedTypeId(addPlannedTimeInShift(shiftDTO.getUnitId(), phase.getId(), activity, staffAdditionalInfoDTO));
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(),shiftStartDate);
        validateShiftWithActivity(phase,wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO);

        Shift mainShift = buildShift(shiftWithActivityDTO);
        mainShift.setMainShift(true);
        mainShift.setPlannedTimeId(shiftWithActivityDTO.getPlannedTypeId());
        validateStaffingLevel(phase,mainShift, activity, true, staffAdditionalInfoDTO);
        // Break Settings
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>(addBreakInShifts(activity, mainShift, staffAdditionalInfoDTO));
        List<Integer> activityDayTypes = new ArrayList<>();
        if (staffAdditionalInfoDTO.getDayTypes() != null && !staffAdditionalInfoDTO.getDayTypes().isEmpty()) {
            activityDayTypes = WTARuleTemplateValidatorUtility.getValidDays(staffAdditionalInfoDTO.getDayTypes(), activity.getTimeCalculationActivityTab().getDayTypes());
        }
        if (activityDayTypes.contains(new DateTime(shiftDTO.getStartDate()).getDayOfWeek())) {
            timeBankCalculationService.calculateScheduleAndDurationHour(mainShift, activity, staffAdditionalInfoDTO.getUnitPosition());
        }
        save(mainShift);
        ShiftQueryResult shiftQueryResult = mainShift.getShiftQueryResult();
        shiftQueryResult.setSubShifts(shiftQueryResults);
        setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, mainShift);
        payOutService.savePayOut(staffAdditionalInfoDTO, mainShift,activity);

        shiftQueryResult.setTimeType(timeType);
        //anil m2 notify event for updating staffing level
        boolean isShiftForPresence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        if (timeType.equals(TimeTypes.WORKING_TYPE.toString())) {
            applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, mainShift, false, null, isShiftForPresence));
        }
        return shiftQueryResult;
    }

    private BigInteger addPlannedTimeInShift(Long unitId, BigInteger phaseId, Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        /**
         * This is used for checking the activity is for presence type
         **/
        Boolean managementPerson = Optional.ofNullable(staffAdditionalInfoDTO.getUserAccessRoleDTO()).isPresent() && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement();

        return (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)
                || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK))
                ? getAbsencePlannedTime(unitId, phaseId, staffAdditionalInfoDTO, activity)
                : getPresencePlannedTime(unitId, phaseId, managementPerson, staffAdditionalInfoDTO);
    }

    private BigInteger getAbsencePlannedTime(Long unitId, BigInteger phaseId, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity) {
        List<ActivityConfiguration> activityConfigurations = activityConfigurationRepository.findAllAbsenceConfigurationByUnitIdAndPhaseId(unitId, phaseId);
        BigInteger plannedTimeId = null;
        for (ActivityConfiguration activityConfiguration : activityConfigurations) {
            if (!Optional.ofNullable(activityConfiguration.getAbsencePlannedTime()).isPresent()) {
                exceptionService.dataNotFoundByIdException("error.activityConfiguration.notFound");
            }

            if (activityConfiguration.getAbsencePlannedTime().isException() && activity.getBalanceSettingsActivityTab().getTimeTypeId().equals(activityConfiguration.getAbsencePlannedTime().getTimeTypeId())) {
                plannedTimeId = activityConfiguration.getAbsencePlannedTime().getPlannedTimeId();
                break;
            } else {
                plannedTimeId = activityConfiguration.getAbsencePlannedTime().getPlannedTimeId();
            }
        }
        // checking weather this is allowed to staff or not
        plannedTimeId = getApplicablePlannedType(staffAdditionalInfoDTO.getUnitPosition(), plannedTimeId);
        //staffAdditionalInfoDTO.getUnitPosition().getExcludedPlannedTime().equals(plannedTimeId) ? staffAdditionalInfoDTO.getUnitPosition().getIncludedPlannedTime() : plannedTimeId;
        return plannedTimeId;
    }

    private BigInteger getPresencePlannedTime(Long unitId, BigInteger phaseId, Boolean managementPerson, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId, phaseId);
        BigInteger plannedTimeId;
        if (!Optional.ofNullable(activityConfiguration).isPresent() && !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.activityConfiguration.notFound");
        }
        plannedTimeId = (managementPerson) ? getApplicablePlannedType(staffAdditionalInfoDTO.getUnitPosition(), activityConfiguration.getPresencePlannedTime().getManagementPlannedTimeId())
                : getApplicablePlannedType(staffAdditionalInfoDTO.getUnitPosition(), activityConfiguration.getPresencePlannedTime().getStaffPlannedTimeId());
        return plannedTimeId;
    }


    private void setDayTypeTOCTARuleTemplate(StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        Map<Long,List<Day>> daytypesMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k->k.getId(), v->v.getValidDays()));
        staffAdditionalInfoDTO.getUnitPosition().getCtaRuleTemplates().forEach(ctaRuleTemplateDTO -> {
            Set<DayOfWeek> dayOfWeeks = new HashSet<>();
            List<LocalDate> publicHolidays = new ArrayList<>();
            for (Long dayTypeId : ctaRuleTemplateDTO.getDayTypeIds()) {
                daytypesMap.get(dayTypeId).forEach(day -> {
                    if(!day.name().equals(EVERYDAY)){
                        dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
                    }else {
                        dayOfWeeks.addAll(Arrays.asList(DayOfWeek.values()));
                    }
                });
                List<LocalDate> publicHoliday = staffAdditionalInfoDTO.getPublicHoliday().get(dayTypeId);
                if(publicHoliday!=null && !publicHoliday.isEmpty()){
                    publicHolidays.addAll(publicHoliday);
                }
            }
            ctaRuleTemplateDTO.setPublicHolidays(publicHolidays);
            ctaRuleTemplateDTO.setDays(new ArrayList<>(dayOfWeeks));
        });
    }
    private BigInteger getApplicablePlannedType(StaffUnitPositionDetails staffUnitPositionDetails, BigInteger plannedTypeId) {
        if (Optional.ofNullable(staffUnitPositionDetails.getIncludedPlannedTime()).isPresent()) {
            plannedTypeId = plannedTypeId.equals(staffUnitPositionDetails.getExcludedPlannedTime()) ? staffUnitPositionDetails.getIncludedPlannedTime() : plannedTypeId;
        }
        return plannedTypeId;

    }

    private List<ShiftQueryResult> addBreakInShifts(Activity activity, Shift mainShift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        logger.info("break Allowed = {}", activity.getRulesActivityTab().isBreakAllowed());
        if (activity.getRulesActivityTab().isBreakAllowed()) {
            Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
            List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(mainShift.getUnitId(), shiftDurationInMinute);
            Map<BigInteger,Activity> breakActivitiesMap=getBreakActivities(breakSettings);

            boolean paid=false;
            if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting()).isPresent() &&
                    staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID)) {
                paid=true;

            }
            if (Optional.ofNullable(breakSettings).isPresent() && breakSettings.size() > 0) {
                return addBreakInShifts(mainShift, breakSettings, shiftDurationInMinute,breakActivitiesMap,paid);
            }
        }
        return Collections.emptyList();

    }

    private List<ShiftQueryResult> addBreakInShifts(Shift mainShift,  List<BreakSettings> breakSettings, Long shiftDurationInMinute,Map<BigInteger,Activity> breakActivitiesMap,Boolean paid) {
        logger.info("ShiftDurationInMinute = {}", shiftDurationInMinute);
        Long startDateMillis = mainShift.getStartDate().getTime();
        Long endDateMillis = null;
        Long breakAllowedAfterMinute = 0L;
        Long allowedBreakDurationInMinute = 0L;
        Long totalBreakAllotedInMinute = 0L;
        String lastItemAdded = null;
        List<Shift> shifts = new ArrayList<>();
        Activity breakActivity=null;
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>();

        for (int i = 0; i < breakSettings.size(); i++) {
            /**
             * The first eligible break hours after.It specifies you can take first break after this duration
             **/
            breakAllowedAfterMinute = breakSettings.get(i).getShiftDurationInMinute();

            if (shiftDurationInMinute > breakAllowedAfterMinute) {
                if(paid!=null){
                    breakActivity=paid?breakActivitiesMap.get(breakSettings.get(i).getPaidActivityId()):breakActivitiesMap.get(breakSettings.get(i).getUnpaidActivityId());
                }
                endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
                shifts.add(getShiftObject(mainShift, mainShift.getName(), mainShift.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));
                // we have added a sub shift now adding the break for remaining period
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                // if still after subtraction the shift is greater than
                allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();
                startDateMillis = endDateMillis;
                lastItemAdded = SHIFT;
                if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                    endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                    shifts.add(getShiftObject(mainShift, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));

                    shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                    startDateMillis = endDateMillis;
                    totalBreakAllotedInMinute += allowedBreakDurationInMinute;
                    logger.info("Remaining shift length after break = {}", shiftDurationInMinute);
                    lastItemAdded = BREAK;
                } else {
                    logger.info("Remaining shift duration {}  And we need to add break for {}", shiftDurationInMinute, breakSettings.get(i).getBreakDurationInMinute());
                    // add break and increase main shift duration by remaining minute
                }
            } else {
                break;
            }
        }
        /**
         * still shift is greater than break We need to repeat last break until shift duration is less
         **/
        while (shiftDurationInMinute > breakAllowedAfterMinute && allowedBreakDurationInMinute > 0) {
            // last end date is now start date
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
            shifts.add(getShiftObject(mainShift, mainShift.getName(), mainShift.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));
            shiftDurationInMinute = shiftDurationInMinute - breakAllowedAfterMinute;
            lastItemAdded = SHIFT;
            if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                startDateMillis = endDateMillis;
                endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                shifts.add(getShiftObject(mainShift, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                totalBreakAllotedInMinute += allowedBreakDurationInMinute;
                lastItemAdded = BREAK;
            } else {
                logger.info("Remaing shift duration " + shiftDurationInMinute + " And we need to add break for {} ", allowedBreakDurationInMinute);
                // add break and increase main shift duration by remaining minute
            }
        }
        // Sometimes the break is
        if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedAfterMinute && SHIFT.equals(lastItemAdded)) {
            // handle later
            startDateMillis = endDateMillis;
            endDateMillis = endDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            totalBreakAllotedInMinute += ((endDateMillis - startDateMillis) / ONE_MINUTE);
            shifts.add(getShiftObject(mainShift, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));


        } else if (shiftDurationInMinute > 0 && shiftDurationInMinute <= breakAllowedAfterMinute && BREAK.equals(lastItemAdded)) {
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject(mainShift, mainShift.getName(), mainShift.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));

        }
        if (!shifts.isEmpty())
            save(shifts);
        if (breakActivity.getName().equalsIgnoreCase(UNPAID_BREAK)) {
            mainShift.setScheduledMinutes(mainShift.getScheduledMinutes() - totalBreakAllotedInMinute.intValue());
        }
        mainShift.setSubShifts(shifts.stream().map(Shift::getId).collect(Collectors.toSet()));
        shifts.forEach(s -> shiftQueryResults.add(s.getShiftQueryResult()));
        return shiftQueryResults;
    }


    private List<ShiftQueryResult> saveShifts(Activity activity, String timeType, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>(shiftDTOS.size());
        List<Integer> activityDayTypes = new ArrayList<>();
        Phase phase = phaseService.getPhaseCurrentByUnit(shifts.get(0).getUnitId(), shifts.get(0).getStartDate());
        if (staffAdditionalInfoDTO.getDayTypes() != null && !staffAdditionalInfoDTO.getDayTypes().isEmpty()) {
            activityDayTypes = WTARuleTemplateValidatorUtility.getValidDays(staffAdditionalInfoDTO.getDayTypes(), activity.getTimeCalculationActivityTab().getDayTypes());
        }
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        for (ShiftDTO shiftDTO : shiftDTOS) {
            Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getStartDate());
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftQueryResult = buildResponse(shiftDTO, activity);
            shiftQueryResult.setActivity(activity);
            validateShiftWithActivity(phase,wtaQueryResultDTO, shiftQueryResult, staffAdditionalInfoDTO);
            Shift shift = buildShift(shiftQueryResult);
            shift.setMainShift(true);
            if (activityDayTypes.contains(new DateTime(shiftDTO.getStartDate()).getDayOfWeek())) {
                timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
            }
            shifts.add(shift);
            setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
            timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
            boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
            if (timeType.equals(TimeTypes.WORKING_TYPE.toString())) {
                applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift, false, null, isShiftForPreence));
            }
        }
        save(shifts);
        timeBankService.saveTimeBanks(staffAdditionalInfoDTO, shifts);
        payOutService.savePayOuts(staffAdditionalInfoDTO, shifts,Arrays.asList(activity));
        shifts.forEach(s -> shiftQueryResults.add(s.getShiftQueryResult()));
        return shiftQueryResults;
    }


    public ShiftWithActivityDTO updateShift(Long organizationId, ShiftDTO shiftDTO, String type) {

        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }

        if (shift.getStatus().contains(ShiftStatus.FIXED) || shift.getStatus().contains(ShiftStatus.PUBLISHED) || shift.getStatus().contains(ShiftStatus.LOCKED)) {
            exceptionService.actionNotPermittedException("message.shift.state.update", shift.getStatus());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(staffAdditionalInfoDTO.getUnitPosition().getCostTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }
        Activity activityOld = activityRepository.findActivityByIdAndEnabled(shift.getActivityId());
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        //copy old state of activity object
        Shift oldStateOfShift = new Shift();
        BeanUtils.copyProperties(shift, oldStateOfShift);
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activity);
        Phase phase = phaseService.getPhaseCurrentByUnit(shiftDTO.getUnitId(), shiftDTO.getStartDate());
        shiftWithActivityDTO.setPlannedTypeId(addPlannedTimeInShift(organizationId, phase.getId(), activity, staffAdditionalInfoDTO));
        validateShiftWithActivity(phase,wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO);
        shift = buildShift(shiftWithActivityDTO);
        shift.setMainShift(true);
        shift.setPlannedTimeId(shiftWithActivityDTO.getPlannedTypeId());
        timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
        save(shift);
        setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.updatePayOut(staffAdditionalInfoDTO, shift,activity);
        Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
        //anil m2 notify event for updating staffing level
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_WEEK"));
        if (activityWrapper.getTimeType().equals(TimeTypes.WORKING_TYPE.toString())) {
            applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift,
                    true, oldStateOfShift, isShiftForPreence, false, activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_ABSENCE_TO_PRESENCE
                    , activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_PRESENCE_TO_ABSENCE));
        }
        shiftWithActivityDTO.setDurationMinutes(shift.getDurationMinutes());
        shiftWithActivityDTO.setScheduledMinutes(shift.getScheduledMinutes());
        shiftWithActivityDTO.setTimeType(activityWrapper.getTimeType());
        return shiftWithActivityDTO;
    }

    public ShiftFunctionWrapper getShiftByStaffId(Long id, Long staffId, String startDateAsString, String endDateAsString, Long week, Long unitPositionId, String type) throws ParseException {
        //StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, type);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, type, unitPositionId);
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent() || staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.dataNotFoundByIdException("message.staff.belongs", staffId, type);
        }
        Date startDateInISO = DateUtils.getDate();
        Date endDateInISO = DateUtils.getDate();
        if (startDateAsString != null) {
            DateFormat dateISOFormat = new SimpleDateFormat(MONGODB_QUERY_DATE_FORMAT);
            Date startDate = dateISOFormat.parse(startDateAsString);
            startDateInISO = new DateTime(startDate).toDate();
            if (endDateAsString != null) {
                Date endDate = dateISOFormat.parse(endDateAsString);
                endDateInISO = new DateTime(endDate).toDate();
            }

        }
        List<ShiftQueryResult> shifts = (Optional.ofNullable(unitPositionId).isPresent()) ? shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, startDateInISO, endDateInISO, staffAdditionalInfoDTO.getUnitId()) :
                shiftMongoRepository.findAllShiftsBetweenDurationOfUnitAndStaffId(staffId, startDateInISO, endDateInISO, staffAdditionalInfoDTO.getUnitId());
        shifts.stream().map(ShiftQueryResult::sortShifts).collect(Collectors.toList());
        setShiftTimeType(shifts);
        List<AppliedFunctionDTO> appliedFunctionDTOs = null;
        if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            appliedFunctionDTOs = staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions();
        }

        Map<LocalDate, FunctionDTO> functionDTOMap = new HashMap();
        if (appliedFunctionDTOs != null && !appliedFunctionDTOs.isEmpty()) {
            for (AppliedFunctionDTO appliedFunctionDTO : appliedFunctionDTOs) {
                if (appliedFunctionDTO.getAppliedDates() != null && !appliedFunctionDTO.getAppliedDates().isEmpty()) {
                    FunctionDTO functionDTO = new FunctionDTO(appliedFunctionDTO.getId(), appliedFunctionDTO.getName(), appliedFunctionDTO.getIcon());
                    for (Long date : appliedFunctionDTO.getAppliedDates()) {
                        functionDTOMap.put(Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate(), functionDTO);
                    }
                }
            }
        }
        return new ShiftFunctionWrapper(shifts, functionDTOMap);
    }

    public void deleteShift(BigInteger shiftId) {
        Shift shift = shiftMongoRepository.findOne(shiftId);
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftId);
        }
        if (!shift.getStatus().contains(ShiftStatus.UNPUBLISHED)) {
            exceptionService.actionNotPermittedException("message.shift.delete", shift.getStatus());
        }
        Activity activity = activityRepository.findActivityByIdAndEnabled(shift.getActivityId());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shift.getStaffId(), ORGANIZATION, shift.getUnitPositionId());
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(staffAdditionalInfoDTO.getUnitPosition().getCostTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        Phase phase = phaseService.getPhaseCurrentByUnit(shift.getUnitId(), shift.getStartDate());
        validateStaffingLevel(phase,shift, activity, false, staffAdditionalInfoDTO);
        Specification<BigInteger> shiftAllowedToDelete = new ShiftAllowedToDelete(activity.getRulesActivityTab().getEligibleForSchedules(), staffAdditionalInfoDTO.getUserAccessRoleDTO());
        Specification<BigInteger> activitySpecification = shiftAllowedToDelete;
        List<String> messages = activitySpecification.isSatisfiedString(phase.getId());
        if (!messages.isEmpty()) {
            List<String> errors = new ArrayList<>();
            messages.forEach(responseMessage -> errors.add(localeService.getMessage(responseMessage)));
            exceptionService.actionNotPermittedException(errors.get(0));
        }
        shift.setDeleted(true);
        save(shift);
        setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.deletePayOut(shift.getId());

        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        applicationContext.publishEvent(new ShiftNotificationEvent(shift.getUnitId(), DateUtils.onlyDate(shift.getStartDate()), shift,
                false, null, isShiftForPreence, true, false, false));

    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }

    private void validateShiftWithActivity(Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.position");
        }
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta.notFound");
        }
        if (wtaQueryResultDTO.getEndDate() != null && new DateTime(wtaQueryResultDTO.getEndDate()).isBefore(shift.getEndDate().getTime())) {
            throw new ActionNotPermittedException("WTA is Expired for unit employment.");
        }
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase,shift, wtaQueryResultDTO, staffAdditionalInfoDTO);
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkills());
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffAdditionalInfoDTO.getUnitPosition().getExpertise());
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        Specification<ShiftWithActivityDTO> staffEmploymentSpecification = new StaffEmploymentSpecification(phase, shift.getActivity(), staffAdditionalInfoDTO);
        Specification<ShiftWithActivityDTO> shiftTimeLessThan = new ShiftStartTimeLessThan(staffAdditionalInfoDTO.getUnitTimeZone(), shift.getStartDate(), shift.getActivity().getRulesActivityTab().getPlannedTimeInAdvance());


        /* List<Long> dayTypeIds = activity.getRulesActivityTab().getDayTypes();
        if (dayTypeIds != null) {
            List<DayType> dayTypes = countryRestClient.getDayTypes(dayTypeIds);
            Specification<Activity> activityDayTypeSpec = new DayTypeSpecification(dayTypes, shift.getStartDate());
            rule_validator.and(activityDayTypeSpec);
        }*/
        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification
                .and(activityExpertiseSpecification)
                .and(activitySkillSpec)
                .and(wtaRulesSpecification)
                .and(staffEmploymentSpecification)
                .and(shiftTimeLessThan);


        //activitySpecification.isSatisfied(shift);
        // updateWTACounter(ruleTemplateSpecificInfo, staffAdditionalInfoDTO);
        //.and(wtaRulesSpecification);
        List<String> messages = activitySpecification.isSatisfiedString(shift);
        if (!messages.isEmpty()) {
            List<String> errors = new ArrayList<>(messages);
            exceptionService.actionNotPermittedException(errors.get(0),errors.size()==2 ? errors.get(1) : "");
        }
        // updateWTACounter(ruleTemplateSpecificInfo, staffAdditionalInfoDTO);
        //TODO Pradeep will look into dayType
//        List<Long> dayTypeIds = activity.getRulesActivityTab().getDayTypes();
//        if (dayTypeIds != null) {
//            List<DayType> dayTypes = countryRestClient.getDayTypes(dayTypeIds);
//            ActivitySpecification<Activity> activityDayTypeSpec = new ActivityDayTypeSpecification(dayTypes, shift.getStartDate());
//            activitySpecification.and(activityDayTypeSpec);
//        }
    }

    private RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(Phase phase,ShiftWithActivityDTO shift, WTAQueryResultDTO wtaQueryResultDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        logger.info("Current phase is " + phase.getName() + " for date " + new DateTime(shift.getStartDate()));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), DateUtils.asLocalDate(shift.getStartDate()));
        if(planningPeriod==null){
            exceptionService.actionNotPermittedException("message.shift.planning.period.exit",shift.getStartDate());
        }
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), phase.getName());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates());
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.getDateByZonedDateTime(intervalByRuleTemplates.getStart()), DateUtils.getDateByZonedDateTime(intervalByRuleTemplates.getEnd()));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndBeforeDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getStartDate());
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, sc -> sc.getCount()));
        Date endTimeOfInterval = Date.from(ZonedDateTime.ofInstant(shift.getEndDate().toInstant(),ZoneId.systemDefault()).plusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant());
        Interval interval = new Interval(staffAdditionalInfoDTO.getUnitPosition().getStartDateMillis(), staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis() == null ? endTimeOfInterval.getTime() : staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis());
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes(), staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek(), DateUtils.asLocalDate(new Date(staffAdditionalInfoDTO.getUnitPosition().getStartDateMillis())), staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis() != null ? DateUtils.asLocalDate(new Date(staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis())) : null);
        int totalTimeBank = -timeBankCalculationService.calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
        return new RuleTemplateSpecificInfo(shifts, shift, staffAdditionalInfoDTO.getTimeSlotSets(), phase.getName(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, staffAdditionalInfoDTO.getDayTypes(), staffAdditionalInfoDTO.getUser(), totalTimeBank);
    }

    private void updateWTACounter(RuleTemplateSpecificInfo infoWrapper, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(), infoWrapper.getPlanningPeriod().getStartDate(), infoWrapper.getPlanningPeriod().getEndDate(), infoWrapper.getPhase());
        for (StaffWTACounter staffWTACounter : staffWTACounters) {
            staffWTACounter.setCount(infoWrapper.getCounterMap().get(staffWTACounter.getRuleTemplateId()));
            infoWrapper.getCounterMap().remove(staffWTACounter.getRuleTemplateId());
        }
        List<StaffWTACounter> newStaffWTACounter = infoWrapper.getCounterMap().entrySet().stream().map(s -> new StaffWTACounter(infoWrapper.getPlanningPeriod().getStartLocalDate(), infoWrapper.getPlanningPeriod().getEndLocalDate(), s.getKey(), staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitId(), s.getValue())).collect(Collectors.toList());
        staffWTACounters.addAll(newStaffWTACounter);
        if (!staffWTACounters.isEmpty()) {
            save(staffWTACounters);
        }
    }

    private void validateStaffingLevel(Phase phase,Shift shift, Activity activity, boolean checkOverStaffing, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        if (activity.getRulesActivityTab().isEligibleForStaffingLevel()) {
            PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
            if (!Optional.ofNullable(phaseSettings).isPresent()) {
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            if (!phaseSettings.isManagementEligibleForOverStaffing() || !phaseSettings.isManagementEligibleForUnderStaffing() || !phaseSettings.isStaffEligibleForOverStaffing() || !phaseSettings.isStaffEligibleForUnderStaffing()) {
                Date startDate1 = DateUtils.getDateByZoneDateTime(DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS));
                Date endDate1 = DateUtils.getDateByZoneDateTime(DateUtils.getZoneDateTime(shift.getEndDate()).truncatedTo(ChronoUnit.DAYS));
                List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(shift.getUnitId(), startDate1, endDate1);
                if (!Optional.ofNullable(staffingLevels).isPresent() || staffingLevels.isEmpty()) {
                    exceptionService.actionNotPermittedException("message.staffingLevel.absent");
                }
                List<Shift> shifts = shiftMongoRepository.findShiftBetweenDuration(shift.getStartDate(), shift.getEndDate(), shift.getUnitId());
                for (StaffingLevel staffingLevel : staffingLevels) {
                    List<StaffingLevelInterval> staffingLevelIntervals = (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) ||
                            activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) ? staffingLevel.getAbsenceStaffingLevelInterval() : staffingLevel.getPresenceStaffingLevelInterval();
                    for (StaffingLevelInterval staffingLevelInterval : staffingLevelIntervals) {
                        int shiftsCount = 0;
                        boolean checkForUnderStaffing = false;
                        Optional<StaffingLevelActivity> staffingLevelActivity = staffingLevelInterval.getStaffingLevelActivities().stream().filter(sa -> new BigInteger(sa.getActivityId().toString()).equals(activity.getId())).findFirst();
                        if (staffingLevelActivity.isPresent()) {
                            ZonedDateTime startDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelInterval.getStaffingLevelDuration().getFrom());
                            ZonedDateTime endDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelInterval.getStaffingLevelDuration().getTo());
                            DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
                            for (Shift shift1 : shifts) {
                                if (shift1.getActivityId().equals(activity.getId()) && interval.overlaps(shift1.getInterval())) {
                                    if (!checkOverStaffing) {
                                        checkForUnderStaffing = true;
                                    }
                                    shiftsCount++;
                                }
                            }
                            int totalCount = shiftsCount - (checkOverStaffing ? staffingLevelActivity.get().getMaxNoOfStaff() : staffingLevelActivity.get().getMinNoOfStaff());
                            boolean checkForStaff = staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff();
                            boolean checkForManagement = staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement();
                            if ((checkOverStaffing && totalCount >= 0)) {
                                if ((checkForStaff && checkForManagement) && (!phaseSettings.isManagementEligibleForOverStaffing() && !phaseSettings.isStaffEligibleForOverStaffing())) {
                                    exceptionService.actionNotPermittedException("message.shift.overStaffing");
                                } else if (checkForStaff ? !phaseSettings.isStaffEligibleForOverStaffing() : !phaseSettings.isManagementEligibleForOverStaffing()) {
                                    exceptionService.actionNotPermittedException("message.shift.overStaffing");
                                }
                            }
                            if (!checkOverStaffing && checkForUnderStaffing && totalCount <= 0) {
                                if ((checkForStaff && checkForManagement) && (!phaseSettings.isStaffEligibleForUnderStaffing() && !phaseSettings.isManagementEligibleForUnderStaffing())) {
                                    exceptionService.actionNotPermittedException("message.shift.underStaffing");
                                } else if (checkForStaff ? !phaseSettings.isStaffEligibleForUnderStaffing() : !phaseSettings.isManagementEligibleForUnderStaffing()) {
                                    exceptionService.actionNotPermittedException("message.shift.underStaffing");
                                }
                            }
                        } else {
                            exceptionService.actionNotPermittedException("message.staffingLevel.activity");
                        }

                    }
                }
            }
        }

    }

    public ShiftQueryResult addSubShift(Long unitId, ShiftDTO shiftDTO, String type) {
        Shift shift;
        if (shiftDTO.getId() != null) {
            shift = shiftMongoRepository.findOne(shiftDTO.getId());
            if (!Optional.ofNullable(shift).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
            }
        } else {
            shift = new Shift();
        }

        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getStaffId());
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(staffAdditionalInfoDTO.getUnitPosition().getCostTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }

        shift = buildShift(shiftDTO);
        shift.setUnitId(unitId);
        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activity);
        shiftWithActivityDTO.setActivity(activity);
        shiftWithActivityDTO.setUnitId(unitId);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        Phase phase = phaseService.getPhaseCurrentByUnit(shift.getUnitId(), shift.getStartDate());
        validateShiftWithActivity(phase,wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO);
        ShiftQueryResult shiftQueryResult;
        if (shiftDTO.getSubShifts().size() == 0) {
            shift = buildShift(shiftDTO);
            shift.setUnitId(unitId);
            shift.setMainShift(true);
            save(shift);
            shiftQueryResult = shift.getShiftQueryResult();
        } else {
            List<Shift> shifts;
            shifts = verifyCompositeShifts(shiftDTO, shiftDTO.getId(), activity);
            save(shifts);
            Set<BigInteger> subShiftsIds = shifts.parallelStream().map(Shift::getId).collect(Collectors.toSet());
            shift = buildShift(shiftDTO);
            shift.setMainShift(true);
            shift.setUnitId(unitId);
            shift.setSubShifts(subShiftsIds);
            save(shift);
            shiftQueryResult = geSubShiftResponse(shift, shifts);
        }
        setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.savePayOut(staffAdditionalInfoDTO, shift,activity);
        shiftQueryResult.setTimeType(activityWrapper.getTimeType());
        return shiftQueryResult;
    }

    /**
     * This method is used to check the timings overlap of sub shifts
     */
    private void validateTimingOfShifts(ShiftDTO shiftDTO, Activity activity) {
        Date parentShiftStartDateTime = shiftDTO.getStartDate();
        Date parentShiftEndDateTime = shiftDTO.getEndDate();
        Map<BigInteger, CompositeActivity> compositeActivityMap = activity.getCompositeActivities().stream().collect(Collectors.toMap(CompositeActivity::getActivityId, Function.identity()));
        logger.info(shiftDTO.getSubShifts().size() + "");

        for (int i = 0; i < shiftDTO.getSubShifts().size(); i++) {
            ShiftDTO subShifts = shiftDTO.getSubShifts().get(i);
            CompositeActivity compositeActivity = compositeActivityMap.get(subShifts.getActivityId());
            if (i == 0) {
                if ((!parentShiftStartDateTime.equals(subShifts.getStartDate())) || (parentShiftEndDateTime.before(subShifts.getEndDate()))) {
                    logger.info("start " + parentShiftStartDateTime + "-" + subShifts.getStartDate()
                            + "end " + parentShiftEndDateTime + "-" + subShifts.getEndDate() + "shift data");
                    exceptionService.invalidRequestException("message.shift.date.startandend.incorrect", (i - 1));
                }

                if (compositeActivity != null && !compositeActivity.getActivityId().equals(activity.getId()) && !compositeActivity.isAllowedBefore()) {
                    exceptionService.invalidRequestException("message.shift.notAllowedBefore", subShifts.getName(), activity.getName());
                }


            } else {
                if ((!parentShiftEndDateTime.equals(subShifts.getStartDate())) || (shiftDTO.getEndDate().before(subShifts.getEndDate()))) {
                    logger.info("start " + (parentShiftStartDateTime) + "-" + subShifts.getStartDate()
                            + "end " + (parentShiftEndDateTime) + "-" + subShifts.getEndDate() + "shift data");
                    exceptionService.invalidRequestException("message.shift.date.startandend.incorrect", (i - 1));
                }
                if (compositeActivity != null && !compositeActivity.getActivityId().equals(activity.getId()) && !compositeActivity.isAllowedAfter()) {
                    exceptionService.invalidRequestException("message.shift.notAllowedAfter", subShifts.getName(), activity.getName());
                }
            }
            // making the calculating the previous  object as parent
            parentShiftEndDateTime = subShifts.getEndDate();
        }
    }


    private List<Shift> verifyCompositeShifts(ShiftDTO shiftDTO, BigInteger shiftId, Activity activity) {
        if (shiftDTO.getSubShifts().size() == 0) {
            exceptionService.invalidRequestException("message.sub-shift.create");
        }
        if (!Optional.ofNullable(activity.getCompositeActivities()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.sub-shift.activity.create");
        }
        validateTimingOfShifts(shiftDTO, activity);
        List<ShiftDTO> subShiftDTOS = shiftDTO.getSubShifts();

        Set<BigInteger> activityIds = subShiftDTOS.parallelStream()
                .filter(act -> !(act.getName().equalsIgnoreCase(PAID_BREAK) || act.getName().equalsIgnoreCase(UNPAID_BREAK)))
                .map(act -> act.getActivityId())
                .collect(Collectors.toSet());

        Set<BigInteger> allowedActivities = activity.getCompositeActivities().stream().map(CompositeActivity::getActivityId).collect(Collectors.toSet());
        allowedActivities.add(shiftDTO.getActivityId());
        if (!allowedActivities.containsAll(activityIds)) {
            exceptionService.invalidRequestException("message.activity.multishift");
        }
        List<Activity> activities=activityRepository.findAllActivitiesByIds(activityIds);
        Map<BigInteger,String> activityMap=activities.stream().collect(Collectors.toMap(Activity::getId,Activity::getName));
        List<Shift> shifts = new ArrayList<>(shiftDTO.getSubShifts().size());
        for (int i = 0; i < shiftDTO.getSubShifts().size(); i++) {
            Shift subShifts = buildShift(shiftDTO.getSubShifts().get(i));
            subShifts.setName(activityMap.get(shiftDTO.getSubShifts().get(i).getActivityId()));
            subShifts.setMainShift(false);
            shifts.add(subShifts);
        }
        return shifts;

    }

    private ShiftQueryResult geSubShiftResponse(Shift shift, List<Shift> shifts) {
        ShiftQueryResult shiftQueryResult = shift.getShiftQueryResult();
        List<ShiftQueryResult> subShifts = new ArrayList<>();
        for (int i = 0; i < shifts.size(); i++) {
            subShifts.add(shifts.get(i).getShiftQueryResult());
        }
        shiftQueryResult.setSubShifts(subShifts);
        return shiftQueryResult;
    }


    public Boolean addSubShifts(Long unitId, List<ShiftDTO> shiftDTOS, String type) {
        for (ShiftDTO shiftDTO : shiftDTOS) {
            ShiftQueryResult shiftQueryResult = createShift(unitId, shiftDTO, "Organization", true).get(0);
            shiftDTO.setId(shiftQueryResult.getId());
        }

        shiftDTOS.forEach(shiftDTO -> {
            if (shiftDTO.getSubShifts() != null && !shiftDTO.getSubShifts().isEmpty()) {
                addSubShift(unitId, shiftDTO, type);
            }
        });
        return true;
    }

    private List<ShiftQueryResult> getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, String timeType, Date fromDate) {
        Date endDate = new DateTime(fromDate).withTimeAtStartOfDay().plusDays(8).toDate();
        Date startDate = new DateTime(fromDate).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).toDate();
        List<ShiftQueryResult> shiftQueryResultsInInterval = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        Date shiftDate = fromDate;
        for (int day = 0; day < 7; day++) {
            /*if (staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes() <= totalContractualMinOfShift) {
                break;
            }*/
            ShiftDTO shiftDTO = calculateAverageShiftByActivity(shiftQueryResultsInInterval, activity, staffAdditionalInfoDTO, shiftDate);
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            shiftDTOS.add(shiftDTO);
            shiftDate = new DateTime(shiftDate).plusDays(1).toDate();
        }
        validateShifts(shiftQueryResultsInInterval, shiftDTOS);
        List<ShiftQueryResult> shiftQueryResults = null;
        if (!shiftDTOS.isEmpty()) {
            shiftQueryResults = saveShifts(activity, timeType, staffAdditionalInfoDTO, shiftDTOS);
        }
        return shiftQueryResults;
    }

    private void validateShifts(List<ShiftQueryResult> shiftQueryResultsInInterval, List<ShiftDTO> shiftDTOS) {
        Long shiftsStartDate = shiftDTOS.get(0).getStartDate().getTime();
        Long shiftsEndDate = shiftDTOS.get(shiftDTOS.size() - 1).getEndDate().getTime();
        Interval interval = new Interval(shiftsStartDate, shiftsEndDate);
        Optional<ShiftQueryResult> shiftInInterval = shiftQueryResultsInInterval.stream().filter(s -> interval.contains(s.getStartDate()) || interval.contains(s.getEndDate())).findFirst();
        if (shiftInInterval.isPresent()) {
            exceptionService.actionNotPermittedException("message.shift.date.startandend");
        }

    }

    public ShiftDTO calculateAverageShiftByActivity(List<ShiftQueryResult> shifts, Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date fromDate) {
        int contractualMinutesInADay = staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes() / staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek();

        ShiftDTO shiftDTO = new ShiftDTO(activity.getId(), staffAdditionalInfoDTO.getUnitId(), staffAdditionalInfoDTO.getId(), staffAdditionalInfoDTO.getUnitPosition().getId());

        Integer startAverageMin = null;
        if (shifts != null && !shifts.isEmpty() && activity.getTimeCalculationActivityTab().getHistoryDuration() != 0) {
            startAverageMin = getStartAverage(new DateTime(fromDate).getDayOfWeek(), shifts);

        }
        if (startAverageMin != null) {
            DateTime startDateTime = new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes(startAverageMin);
            shiftDTO.setStartLocalDate(DateUtils.toLocalDate(startDateTime));
            shiftDTO.setStartTime(DateUtils.toLocalTime(startDateTime));
            shiftDTO.setEndLocalDate(DateUtils.toLocalDate(startDateTime.plusMinutes(contractualMinutesInADay)));
            shiftDTO.setEndTime(DateUtils.toLocalTime(startDateTime.plusMinutes(contractualMinutesInADay)));
        } else {
            DateTime startDateTime = new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes((activity.getTimeCalculationActivityTab().getDefaultStartTime().getHour() * 60) + activity.getTimeCalculationActivityTab().getDefaultStartTime().getMinute());
            shiftDTO.setStartLocalDate(DateUtils.toLocalDate(startDateTime));
            shiftDTO.setStartTime(DateUtils.toLocalTime(startDateTime));
            shiftDTO.setEndLocalDate(DateUtils.toLocalDate(startDateTime.plusMinutes(contractualMinutesInADay)));
            shiftDTO.setEndTime(DateUtils.toLocalTime(startDateTime.plusMinutes(contractualMinutesInADay)));

        }
        if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
            Interval shiftInterval = new Interval(new DateTime(shiftDTO.getStartDate()), new DateTime(shiftDTO.getEndDate()));
            Optional<ShiftQueryResult> shift = shifts.stream().filter(s -> shiftInterval.contains(s.getStartDate()) || shiftInterval.contains(s.getEndDate())).findFirst();
            if (shift.isPresent()) {
                exceptionService.actionNotPermittedException("message.shift.date.startandend");
            }
        }

        return shiftDTO;
    }

    private Integer getStartAverage(int day, List<ShiftQueryResult> shifts) {
        List<ShiftQueryResult> updatedShifts = shifts.stream().filter(s -> new DateTime(s.getStartDate()).getDayOfWeek() == day).collect(Collectors.toList());
        updatedShifts = getFilteredShiftsByStartTime(updatedShifts);
        Integer startAverageMin = null;
        if (updatedShifts != null && !updatedShifts.isEmpty()) {
            startAverageMin = updatedShifts.stream().mapToInt(s -> new DateTime(s.getStartDate()).getMinuteOfDay()).sum() / updatedShifts.size();
        }
        return startAverageMin;
    }

    public List<ShiftQueryResult> getFilteredShiftsByStartTime(List<ShiftQueryResult> shifts) {
        shifts.sort((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>();
        LocalDate localDate = null;
        for (ShiftQueryResult shift : shifts) {
            if (!DateUtils.asLocalDate(new Date(shift.getStartDate())).equals(localDate)) {
                localDate = DateUtils.asLocalDate(new Date(shift.getStartDate()));
                shiftQueryResults.add(shift);
            }
        }
        return shiftQueryResults;
    }


    public int activityChangeStatus(Activity activityOld, Activity activityCurrent) {
        boolean isShiftOldForPresence = !(activityOld.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityOld.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        boolean isShiftCurrentForAbsence = (activityCurrent.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityCurrent.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        int activityChangeStatus = 0;
        if (isShiftOldForPresence && isShiftCurrentForAbsence) {
            activityChangeStatus = 1;
        } else if (!isShiftOldForPresence && !isShiftCurrentForAbsence) {
            activityChangeStatus = 2;
        }

        return activityChangeStatus;
    }

    public Map<String, List<ShiftResponse>> updateStatusOfShifts(Long unitId, ShiftPublishDTO shiftPublishDTO) {

        List<ShiftResponse> success = new ArrayList<>();
        List<ShiftResponse> error = new ArrayList<>();
        Map<String, List<ShiftResponse>> response = new HashMap<>();
        response.put("success", success);
        response.put("error", error);

        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftPublishDTO.getShiftIds());

        if (!shifts.isEmpty()) {
            Set<LocalDate> dates = shifts.stream().map(s -> DateUtils.asLocalDate(s.getStartDate())).collect(Collectors.toSet());
            Map<LocalDate, Phase> phaseListByDate = phaseService.getStatusByDates(unitId, dates);
            for (Shift shift : shifts) {
                List<ShiftStatus> phaseStatuses = phaseListByDate.get(DateUtils.asLocalDate(shift.getStartDate())).getStatus();
                Phase phase=phaseListByDate.get(DateUtils.asLocalDate(shift.getStartDate()));
                 boolean validAccessGroup= validateAccessGroup(phase,shiftPublishDTO.getStatus().get(0),shift.getActivityId());
                 boolean validStatus=phaseStatuses.containsAll(shiftPublishDTO.getStatus());
                if (validStatus && validAccessGroup ) {
                    shift.getStatus().addAll(shiftPublishDTO.getStatus());
                    success.add(new ShiftResponse(shift.getId(), shift.getName(), Collections.singletonList(localeService.getMessage("message.shift.status.added")), true));
                } else {
                    List<Object> errorMessages = new ArrayList<>();
                    List<String> messages=new ArrayList<>();
                    if(!validStatus) {
                        errorMessages.addAll(shiftPublishDTO.getStatus());
                        errorMessages.addAll(phaseStatuses);
                        messages.add(localeService.getMessage("error.shift.status", errorMessages.toArray()));
                    }
                    if(!validAccessGroup){
                        messages.add(localeService.getMessage("access.group.not.matched"));
                    }
                    error.add(new ShiftResponse(shift.getId(), shift.getName(),messages, false));
                }

            }
            save(shifts);
        }
        return response;
    }

    public Shift buildShift(ShiftWithActivityDTO shift) {
        return ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
    }


    public ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, Date startDate, Date endDate) {
        List<ShiftQueryResult> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, startDate, endDate);
        UserAccessRoleDTO userAccessRoleDTO = genericIntegrationService.getAccessRolesOfStaff(unitId);
        List<OpenShift> openShifts=userAccessRoleDTO.getManagement()?openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate, endDate):
                openShiftNotificationMongoRepository.findValidOpenShiftsForStaff(userAccessRoleDTO.getStaffId(),startDate,endDate);

        List<OpenShiftResponseDTO> openShiftResponseDTOS = new ArrayList<>();
        openShifts.forEach(openShift -> {
            OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift, openShiftResponseDTO, openShift.getStartDate().toString(), openShift.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });
        List<AccessGroupRole> roles = new ArrayList<>();
        if (Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent() && userAccessRoleDTO.getManagement()) {
            roles.add(AccessGroupRole.MANAGEMENT);
        }
        if (Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent() && userAccessRoleDTO.getStaff()) {
            roles.add(AccessGroupRole.STAFF);
        }
        StaffAccessRoleDTO staffAccessRoleDTO = new StaffAccessRoleDTO(userAccessRoleDTO.getStaffId(), roles);
        setShiftTimeType(assignedShifts);
        return new ShiftWrapper(assignedShifts, openShiftResponseDTOS, staffAccessRoleDTO);
    }

    public CopyShiftResponse copyShifts(Long unitId, CopyShiftDTO copyShiftDTO) {
        List<DateWiseShiftResponse> shifts = shiftMongoRepository.findAllByIdGroupByDate(copyShiftDTO.getShiftIds());
        if (!Optional.ofNullable(shifts).isPresent() || shifts.isEmpty()) {
            exceptionService.invalidOperationException("message.shift.notBlank");
        }
        Set<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getShifts().stream().map(ss -> ss.getActivityId())).collect(Collectors.toSet());

        List<Activity> activities = activityRepository.findAllActivitiesByIds(activityIds);

        List<StaffUnitPositionDetails> staffDataList = restClient.getStaffsUnitPosition(unitId, copyShiftDTO.getStaffIds(), copyShiftDTO.getExpertiseId());
        Set<BigInteger> wtaIds = staffDataList.parallelStream().map(wta -> wta.getWorkingTimeAgreementId()).collect(Collectors.toSet());

        List<WorkingTimeAgreement> workingTimeAgreements = wtaService.findAllByIdAndDeletedFalse(wtaIds);
        List<Phase> phases = phaseService.getAllPhasesOfUnit(unitId);
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByUnitIdAndDeletedFalseOrderByCreatedAtAsc(unitId);

        Map<BigInteger,Activity> breakActivitiesMap=getBreakActivities(breakSettings);
        Integer unCopiedShiftCount = 0;
        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();

        for (Long currentStaffId : copyShiftDTO.getStaffIds()) {

            StaffUnitPositionDetails staffUnitPosition = staffDataList.parallelStream().filter(unitPosition -> unitPosition.getStaff().getId().equals(currentStaffId)).findFirst().get();
            boolean paid=staffUnitPosition.getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID);
            WorkingTimeAgreement workingTimeAgreement = workingTimeAgreements.stream().filter(wta -> wta.getId().equals(staffUnitPosition.getWorkingTimeAgreementId())).findAny().get();

            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffUnitPosition, activities, workingTimeAgreement, phases, copyShiftDTO, breakSettings,breakActivitiesMap, paid);

            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("error"));
            unCopiedShiftCount += response.get("error").size();
            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);
        }
        copyShiftResponse.setUnCopiedShiftCount(unCopiedShiftCount);

        return copyShiftResponse;
    }

    private Map<String, List<ShiftResponse>> copyForThisStaff(List<DateWiseShiftResponse> shifts, StaffUnitPositionDetails staffUnitPosition, List<Activity> activities, WorkingTimeAgreement workingTimeAgreement, List<Phase> phases, CopyShiftDTO copyShiftDTO, List<BreakSettings> breakSettings,Map<BigInteger,Activity> breakActivitiesMap, Boolean paid) {

        List<Shift> newShifts = new ArrayList<>(shifts.size());
        Map<String, List<ShiftResponse>> statusMap = new HashMap<>();
        List<ShiftResponse> successfullyCopiedShifts = new ArrayList<>();
        List<ShiftResponse> errorInCopyingShifts = new ArrayList<>();
        int counter = 0;
        LocalDate shiftCreationDate = copyShiftDTO.getStartDate();
        LocalDate shiftCreationLastDate = copyShiftDTO.getEndDate();
        ShiftResponse shiftResponse;

        while (shiftCreationLastDate.isAfter(shiftCreationDate) || shiftCreationLastDate.equals(shiftCreationDate)) {
            DateWiseShiftResponse dateWiseShiftResponse = shifts.get(counter);
            for (Shift sourceShift : dateWiseShiftResponse.getShifts()) {
                BigInteger activityId = sourceShift.getActivityId();
                Activity currentActivity = activities.parallelStream().filter(activity -> activity.getId().equals(activityId)).findAny().get();
                ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift, ShiftWithActivityDTO.class);
                shiftWithActivityDTO.setActivity(currentActivity);
                List<String> validationMessages = validateShiftWhileCopy(shiftWithActivityDTO, staffUnitPosition, workingTimeAgreement, phases, copyShiftDTO);
                shiftResponse = addShift(validationMessages, sourceShift, staffUnitPosition, shiftCreationDate, newShifts, breakSettings, currentActivity,breakActivitiesMap,paid);

                if (shiftResponse.isSuccess()) {
                    successfullyCopiedShifts.add(shiftResponse);
                } else {
                    errorInCopyingShifts.add(shiftResponse);
                }
            }
            shiftCreationDate = shiftCreationDate.plusDays(1L);
            if (counter++ == shifts.size() - 1) {
                counter = 0;
            }

        }
        statusMap.put("success", successfullyCopiedShifts);
        statusMap.put("error", errorInCopyingShifts);
        if (!newShifts.isEmpty()) save(newShifts);
        return statusMap;
    }

    private ShiftResponse addShift(List<String> responseMessages, Shift sourceShift, StaffUnitPositionDetails staffUnitPosition, LocalDate shiftCreationFirstDate, List<Shift> newShifts, List<BreakSettings> breakSettings, Activity activity,Map<BigInteger,Activity> breakActivitiesMap,boolean paid) {
        if (responseMessages.isEmpty()) {
            Long shiftDurationInMinute = (sourceShift.getEndDate().getTime() - sourceShift.getStartDate().getTime()) / ONE_MINUTE;

            Shift copiedShift = new Shift(sourceShift.getName(), DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getStartDate())), DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getEndDate())),
                    sourceShift.getRemarks(), sourceShift.getActivityId(), staffUnitPosition.getStaff().getId(), sourceShift.getPhase(), sourceShift.getUnitId(),
                    sourceShift.getScheduledMinutes(), sourceShift.getDurationMinutes(), sourceShift.isMainShift(), sourceShift.getExternalId(), staffUnitPosition.getId(), sourceShift.getStatus(), sourceShift.getParentOpenShiftId(), sourceShift.getAllowedBreakDurationInMinute(), sourceShift.getId());
            if (activity.getRulesActivityTab().isBreakAllowed()) {

                List<ShiftQueryResult> shiftQueryResults = addBreakInShifts(copiedShift, breakSettings, shiftDurationInMinute,breakActivitiesMap,paid);
                Set<BigInteger> breakShiftIds = shiftQueryResults.stream().map(s -> s.getId()).collect(Collectors.toSet());
                copiedShift.setSubShifts(breakShiftIds);
            }
            newShifts.add(copiedShift);
            return new ShiftResponse(sourceShift.getId(), sourceShift.getName(), Arrays.asList(NO_CONFLICTS), true, shiftCreationFirstDate);

        } else {
            List<String> errors = new ArrayList<>();
            responseMessages.forEach(responseMessage -> {
                errors.add(localeService.getMessage(responseMessage));
            });
            return new ShiftResponse(sourceShift.getId(), sourceShift.getName(), errors, false, shiftCreationFirstDate);
        }
    }

    public List<String> validateShiftWhileCopy(ShiftWithActivityDTO shiftWithActivityDTO, StaffUnitPositionDetails staffUnitPositionDetails, WorkingTimeAgreement workingTimeAgreement, List<Phase> phases, CopyShiftDTO copyShiftDTO) {
        Phase phase = phaseService.getCurrentPhaseInUnitByDate(phases, DateUtils.asDate(copyShiftDTO.getStartDate()));
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffUnitPositionDetails.getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffUnitPositionDetails.getExpertise());

        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification.and(activityExpertiseSpecification);
        return  activitySpecification.isSatisfiedString(shiftWithActivityDTO);
        }

    private Shift getShiftObject(Shift shift, String name, BigInteger activityId, Date startDate, Date endDate, Long allowedBreakDurationInMinute) {
        Shift childShift = new Shift(null, name, startDate, endDate, shift.getBid(), shift.getpId(), shift.getBonusTimeBank()
                , shift.getAmount(), shift.getProbability(), shift.getAccumulatedTimeBankInMinutes(), shift.getRemarks(), activityId, shift.getStaffId(), shift.getUnitId(), shift.getUnitPositionId());
        childShift.setStatus(Collections.singleton(ShiftStatus.UNPUBLISHED));
        childShift.setMainShift(false);
        childShift.setAllowedBreakDurationInMinute(allowedBreakDurationInMinute);
        return childShift;

    }


    public List<ShiftQueryResult> getShiftOfStaffByExpertiseId(Long unitId, Long staffId, String startDateAsString, String endDateAsString, Long expertiseId) throws ParseException {
        Date startDateInISO = DateUtils.getDate();
        Date endDateInISO = DateUtils.getDate();
        if (startDateAsString != null) {
            DateFormat dateISOFormat = new SimpleDateFormat(ONLY_DATE);
            Date startDate = dateISOFormat.parse(startDateAsString);
            startDateInISO = new DateTime(startDate).toDate();
            if (endDateAsString != null) {
                Date endDate = dateISOFormat.parse(endDateAsString);
                endDateInISO = new DateTime(endDate).plusDays(1).toDate();
            }
        }
        Long unitPositionId = restClient.getUnitPositionId(unitId, staffId, expertiseId, startDateInISO.getTime());

        List<ShiftQueryResult> shifts = shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, startDateInISO, endDateInISO, unitId);
        shifts.stream().map(s -> s.sortShifts()).collect(Collectors.toList());
        setShiftTimeType(shifts);
        return shifts;
    }


    public List<ShiftQueryResult> createShiftUsingTemplate(Long unitId, ShiftDTO shiftDTO) {
        List<ShiftQueryResult> shifts = new ArrayList<>();
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftDTO.getTemplateId());
        Set<BigInteger> individualShiftTemplateIds = shiftTemplate.getIndividualShiftTemplateIds();
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS = individualShiftTemplateRepository.getAllIndividualShiftTemplateByIdsIn(individualShiftTemplateIds);
        individualShiftTemplateDTOS.forEach(individualShiftTemplateDTO -> {
            ShiftDTO shiftDTO1 = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO, ShiftDTO.class);
            shiftDTO1.setId(null);
            if (Optional.ofNullable(shiftDTO1.getSubShifts()).isPresent() && shiftDTO1.getSubShifts().size() > 0) {
                shiftDTO1.getSubShifts().forEach(subShifts -> {
                    subShifts.setId(null);
                    subShifts.setStartLocalDate(shiftDTO.getStartLocalDate());
                    subShifts.setEndLocalDate(shiftDTO.getEndLocalDate());
                });
            }
            shiftDTO1.setStaffId(shiftDTO.getStaffId());
            shiftDTO1.setUnitPositionId(shiftDTO.getUnitPositionId());
            shiftDTO1.setStartLocalDate(shiftDTO.getStartLocalDate());
            shiftDTO1.setEndLocalDate(shiftDTO.getEndLocalDate());

            ShiftQueryResult shiftQueryResult = addSubShift(unitId, shiftDTO1, "Organization");
            shifts.add(shiftQueryResult);

        });
        return shifts;
    }

    public Shift buildShift(ShiftDTO shiftDTO) {

        Shift shift = new Shift(shiftDTO.getId(), shiftDTO.getName(), DateUtils.getDateByLocalDateAndLocalTime(shiftDTO.getStartLocalDate(), shiftDTO.getStartTime()),
                DateUtils.getDateByLocalDateAndLocalTime(shiftDTO.getEndLocalDate(), shiftDTO.getEndTime()), shiftDTO.getBid(), shiftDTO.getpId(), shiftDTO.getBonusTimeBank(), shiftDTO.getAmount(),
                shiftDTO.getProbability(), shiftDTO.getAccumulatedTimeBankInMinutes(), shiftDTO.getRemarks(), shiftDTO.getActivityId(), shiftDTO.getStaffId(), shiftDTO.getUnitId(), shiftDTO.getUnitPositionId());
        shift.setDurationMinutes(shiftDTO.getDurationMinutes());
        shift.setScheduledMinutes(shiftDTO.getScheduledMinutes());
        shift.setStatus(Collections.singleton(ShiftStatus.UNPUBLISHED));
        shift.setAllowedBreakDurationInMinute(shiftDTO.getAllowedBreakDurationInMinute());
        return shift;
    }


    public ShiftWithActivityDTO buildResponse(ShiftDTO shiftDTO, Activity activity) {
        ShiftWithActivityDTO shiftWithActivityDTO = new ShiftWithActivityDTO(shiftDTO.getId(), activity.getName(), shiftDTO.getStartDate(), shiftDTO.getEndDate(), shiftDTO.getBonusTimeBank(), shiftDTO.getAmount(),
                shiftDTO.getProbability(), shiftDTO.getAccumulatedTimeBankInMinutes(), shiftDTO.getRemarks(), shiftDTO.getActivityId(), shiftDTO.getStaffId(), shiftDTO.getUnitPositionId(), shiftDTO.getUnitId(), activity);
        shiftWithActivityDTO.setDurationMinutes(shiftDTO.getDurationMinutes());
        shiftWithActivityDTO.setScheduledMinutes(shiftDTO.getScheduledMinutes());
        shiftWithActivityDTO.setStatus(Arrays.asList(ShiftStatus.UNPUBLISHED));
        return shiftWithActivityDTO;
    }


    public List<Shift> getAllShiftByIds(List<String> shiftIds) {
        return shiftMongoRepository.findAllByIds(shiftIds);
    }

    public ShiftQueryResult getShiftByStaffIdAndDate(List<Long> staffIds, Date date) {
        return shiftMongoRepository.findShiftByStaffIdsAndDate(staffIds, date);
    }

    public void setShiftTimeType(List<ShiftQueryResult> shifts) {
        Set<BigInteger> activityIds = shifts.stream().map(shift -> shift.getActivityId()).collect(Collectors.toSet());
        activityIds.addAll(shifts.stream().flatMap(activitie -> activitie.getSubShifts().stream().map(subShiftId -> subShiftId.getActivityId())).collect(Collectors.toSet()));
        List<TimeTypeAndActivityIdDTO> timeTypeAndActivityIdDTOS = activityRepository.findAllTimeTypeByActivityIds(activityIds);
        Map<BigInteger, String> activityIdAndtimeTypes = timeTypeAndActivityIdDTOS.stream().collect(Collectors.toMap(timeTypeAndActivityIdDTO -> timeTypeAndActivityIdDTO.getActivityId(), timeTypeAndActivityIdDTO -> timeTypeAndActivityIdDTO.getTimeType()));
        shifts.forEach(shift -> {
            if (activityIdAndtimeTypes.containsKey(shift.getActivityId())) {
                shift.setTimeType(activityIdAndtimeTypes.get(shift.getActivityId()));
            }
            if (!shift.getSubShifts().isEmpty()) {
                shift.getSubShifts().forEach(subShift -> {
                    if (activityIdAndtimeTypes.containsKey(subShift.getActivityId())) {
                        subShift.setTimeType(activityIdAndtimeTypes.get(subShift.getActivityId()));
                    }
                });
            }
        });
    }

    public void deleteShiftsAndOpenShiftsOnEmploymentEnd(Long staffId, LocalDateTime employmentEndDate, Long unitId) {

        shiftMongoRepository.deleteShiftsAfterDate(staffId,employmentEndDate);
        List<OpenShift> openShifts = openShiftMongoRepository.findAllOpenShiftsByInterestedStaff(staffId,employmentEndDate);
        if(!openShifts.isEmpty()) {
            for(OpenShift openShift:openShifts) {
                openShift.getInterestedStaff().remove(staffId);
                openShift.getAssignedStaff().remove(staffId);
            }
            save(openShifts);
        }

    }
    public void deleteShiftsAfterEmploymentEndDate(Long staffId, Long unitId, LocalDate employmentEndDate) {
        shiftMongoRepository.deleteShiftsAfterDate(staffId,employmentEndDate.atStartOfDay());
    }

    private Map<BigInteger,Activity> getBreakActivities(List<BreakSettings> breakSettings){
        Set<BigInteger> breakActivityIds=breakSettings.stream().map(BreakSettings::getPaidActivityId).collect(Collectors.toSet());
        breakActivityIds.addAll(breakSettings.stream().map(BreakSettings::getUnpaidActivityId).collect(Collectors.toSet()));
        List<Activity> breakActivities=activityRepository.findAllActivitiesByIds(breakActivityIds);
        return breakActivities.stream().collect(Collectors.toMap(Activity::getId,v->v));
    }

    private boolean validateAccessGroup(Phase phase,ShiftStatus status,BigInteger activityId){
        if(activityId!=null){
            ActivityAndShiftStatusSettings activityAndShiftStatusSettings= activityAndShiftStatusSettingsRepository.findByPhaseIdAndActivityIdAndShiftStatus(phase.getId(),activityId,status);
            StaffAccessGroupDTO staffAccessGroupDTO=genericRestClient.publishRequest(null, null, true, IntegrationOperation.GET, "/staff/access_groups", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAccessGroupDTO>>() {});
            return CollectionUtils.containsAny(activityAndShiftStatusSettings.getAccessGroupIds(),staffAccessGroupDTO.getAccessGroupIds());
        }
        return false;
    }


}
