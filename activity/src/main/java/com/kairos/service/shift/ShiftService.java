package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.country.experties.AppliedFunctionDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.Day;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.tabs.CompositeActivity;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.ActivityShiftStatusSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.TimeTypeMongoRepository;
import com.kairos.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.*;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.persistence.repository.unit_settings.TimeAttendanceGracePeriodRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.*;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.ShiftAllowedToDelete;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.locale.LocaleService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.unit_settings.PhaseSettingsService;
import com.kairos.service.user_service_data.UnitDataService;
import com.kairos.service.wta.WTAService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.utils.event.ShiftNotificationEvent;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.DateWiseShiftResponse;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.ONLY_DATE;
import static com.kairos.constants.ApiConstants.GET_REASONCODE;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.ShiftValidatorService.getValidDays;


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
    private AttendanceSettingRepository attendanceSettingRepository;

    @Inject
    private StaffRestClient staffRestClient;

    @Inject
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
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private ShiftTemplateRepository shiftTemplateRepository;
    @Inject
    private IndividualShiftTemplateRepository individualShiftTemplateRepository;
    @Inject
    private ActivityConfigurationRepository activityConfigurationRepository;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;

    @Inject
    private OpenShiftNotificationMongoRepository openShiftNotificationMongoRepository;

    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ActivityShiftStatusSettingsRepository activityAndShiftStatusSettingsRepository;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private ActivityService activityService;
    @Inject
    private OrganizationActivityService organizationActivityService;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private UnitDataService unitDataService;
    @Inject
    private StaffActivitySettingRepository staffActivitySettingRepository;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private TimeAttendanceGracePeriodRepository timeAttendanceGracePeriodRepository;
    @Inject
    private ShiftBreakActivityService shiftBreakActivityService;


    public ShiftWithViolatedInfoDTO createShift(Long unitId, ShiftDTO shiftDTO, String type, boolean byTandAPhase) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivities().get(0).getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.invalidRequestException("message.activity.id", shiftDTO.getActivities().get(0).getActivityId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException("message.staff.notfound");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.actionNotPermittedException("message.unit.position");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.invalidRequestException("error.cta.notFound", shiftDTO.getStartDate());
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        if ((activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK))) {
            shiftWithViolatedInfoDTO = createAbsenceTypeShift(activityWrapper, shiftDTO, staffAdditionalInfoDTO);
        } else {
            List<Shift> shifts;
            if (!byTandAPhase) {
                shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPosition(shiftDTO.getUnitPositionId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
            } else {
                shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPositionNotEqualToShiftId(shiftDTO.getId(), shiftDTO.getUnitPositionId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
                shiftDTO.setId(null);
            }
            if (!shifts.isEmpty()) {
                exceptionService.invalidRequestException("message.shift.date.startandend", shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
            }

            shiftWithViolatedInfoDTO = saveShift(activityWrapper, staffAdditionalInfoDTO, shiftDTO, byTandAPhase);

        }
        return shiftWithViolatedInfoDTO;
    }


    private ShiftWithViolatedInfoDTO createAbsenceTypeShift(ActivityWrapper activityWrapper, ShiftDTO shiftDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = null;
        if (activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
            Date endDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            Date startDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).minusWeeks(activityWrapper.getActivity().getTimeCalculationActivityTab().getHistoryDuration()).withTimeAtStartOfDay().toDate();
            List<ShiftDTO> shifts = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
            shiftDTO = calculateAverageShiftByActivity(shifts, activityWrapper.getActivity(), staffAdditionalInfoDTO, DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate());
            shiftWithViolatedInfoDTO = saveShift(activityWrapper, staffAdditionalInfoDTO, shiftDTO, false);
        }
        if (activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
            Date shiftFromDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate();
            getAverageOfShiftByActivity(staffAdditionalInfoDTO, activityWrapper.getActivity(), activityWrapper.getTimeType(), shiftFromDate);
        }
        return shiftWithViolatedInfoDTO;
    }


    private ShiftWithViolatedInfoDTO saveShift(ActivityWrapper activityWrapper, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO, boolean byTandAPhase) {
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityWrapper.getActivity(), ActivityDTO.class);
        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activityDTO);
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate());
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shiftDTO.getUnitId(), DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()));
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.onlyDate(shiftDTO.getActivities().get(0).getStartDate()));
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, byTandAPhase);
        Shift mainShift = byTandAPhase ? ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class) : ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
        mainShift.setPlanningPeriodId(planningPeriod.getId());
        validateStaffingLevel(phase, mainShift, activityWrapper.getActivity(), true, staffAdditionalInfoDTO);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            List<BigInteger> activityIds = mainShift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
            List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
            setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
            saveShiftWithActivity(phase, activityIds, activityWrapperMap, mainShift, staffAdditionalInfoDTO);
            payOutService.savePayOut(staffAdditionalInfoDTO, mainShift, activityWrapperMap);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(mainShift, ShiftDTO.class);
            ShiftViolatedRules shiftViolatedRules = ObjectMapperUtils.copyPropertiesByMapper(shiftWithViolatedInfoDTO.getViolatedRules(), ShiftViolatedRules.class);
            shiftViolatedRules.setShift(mainShift);
            save(shiftViolatedRules);

        }
        shiftWithViolatedInfoDTO.setShifts(Arrays.asList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }


    private void updateTimeBankAndPublishNotification(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        ActivityWrapper activityWrapper = activityWrapperMap.get(shift.getActivities().get(0).getActivityId());
        boolean presenceTypeShift = !(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        if (TimeTypes.WORKING_TYPE.toString().equals(activityWrapper.getTimeType())) {
            applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shift.getStartDate(), shift, false, null, presenceTypeShift));

        }
    }

    public void saveShiftWithActivity(Phase phase, List<BigInteger> activityIds, Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByStaffIdAndActivityIdInAndDeletedFalse(shift.getStaffId(), activityIds);
        Map<BigInteger, StaffActivitySetting> staffActivitySettingMap = staffActivitySettings.stream().collect(Collectors.toMap(StaffActivitySetting::getActivityId, v -> v));
        int scheduledMinutes = 0;
        int durationMinutes = 0;
        Map<BigInteger, ShiftTimeDetails> shiftTimeDetailsMap = new HashMap<>();

        for (ShiftActivity shiftActivity : shift.getActivities()) {
            shiftTimeDetailsMap.put(shiftActivity.getActivityId(), prepareShiftTimeDetails(shiftActivity, shiftTimeDetailsMap));
            if (shiftActivity.getId() == null) {
                shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            }
            ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
            if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
                Set<DayOfWeek> activityDayTypes = getValidDays(staffAdditionalInfoDTO.getDayTypes(), activityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
                if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                    timeBankCalculationService.calculateScheduleAndDurationHour(shiftActivity, activityWrapper.getActivity(), staffAdditionalInfoDTO.getUnitPosition());
                    scheduledMinutes += shiftActivity.getScheduledMinutes();
                    durationMinutes += shiftActivity.getDurationMinutes();
                }
            }
            shiftActivity.setPlannedTimeId(addPlannedTimeInShift(shift.getUnitId(), phase.getId(), activityWrapper.getActivity(), staffAdditionalInfoDTO));
            shiftActivity.setBackgroundColor(activityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
            shiftActivity.setActivityName(activityWrapper.getActivity().getName());
            shiftActivity.setTimeType(activityWrapper.getTimeType());
        }
        shiftValidatorService.validateActivityTiming(staffActivitySettingMap, shiftTimeDetailsMap, activityWrapperMap);
        shift.setStartDate(shift.getActivities().get(0).getStartDate());
        shift.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
        List<ShiftActivity> breakActvities = shiftBreakActivityService.addBreakInShifts(activityWrapperMap, shift, staffAdditionalInfoDTO.getUnitPosition());
        if (!breakActvities.isEmpty()) {
            shift.getActivities().remove(0);
            shift.getActivities().addAll(breakActvities);
        }
        shift.setPhaseId(phase.getId());
        shiftMongoRepository.save(shift);
        updateTimeBankAndPublishNotification(activityWrapperMap, shift, staffAdditionalInfoDTO);
    }

    public void saveShiftWithActivity(Phase phase, List<Shift> shifts, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getActivities().stream().map(a -> a.getActivityId())).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        for (Shift shift : shifts) {
            int scheduledMinutes = 0;
            int durationMinutes = 0;
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
                ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
                if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
                    Set<DayOfWeek> activityDayTypes = getValidDays(staffAdditionalInfoDTO.getDayTypes(), activityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
                    if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                        timeBankCalculationService.calculateScheduleAndDurationHour(shiftActivity, activityWrapper.getActivity(), staffAdditionalInfoDTO.getUnitPosition());
                        scheduledMinutes += shiftActivity.getScheduledMinutes();
                        durationMinutes += shiftActivity.getDurationMinutes();
                    }
                }
                shiftActivity.setBackgroundColor(activityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
                shiftActivity.setTimeType(activityWrapper.getTimeType());
                shiftActivity.setActivityName(activityWrapper.getActivity().getName());
            }
            shift.setPhaseId(phase.getId());
            shift.setScheduledMinutes(scheduledMinutes);
            shift.setDurationMinutes(durationMinutes);
            shift.setStartDate(shift.getActivities().get(0).getStartDate());
            shift.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
            updateTimeBankAndPublishNotification(activityWrapperMap, shift, staffAdditionalInfoDTO);
        }
        shiftMongoRepository.saveEntities(shifts);
    }

    public ShiftWithViolatedInfoDTO saveShiftAfterValidation(ShiftWithViolatedInfoDTO shiftWithViolatedInfo, String type) {
        Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithViolatedInfo.getShifts().get(0), Shift.class);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shift.getStaffId(), type, shift.getUnitPositionId());
        Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftStartDate);
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.cta.notFound", ctaResponseDTO.getId());
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
        List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate());
        saveShiftWithActivity(phase, activityIds, activityWrapperMap, shift, staffAdditionalInfoDTO);
        payOutService.savePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
        ShiftViolatedRules shiftViolatedRules = ObjectMapperUtils.copyPropertiesByMapper(shiftWithViolatedInfo.getViolatedRules(), ShiftViolatedRules.class);
        shiftViolatedRules.setShift(shift);
        save(shiftViolatedRules);
        updateWTACounter(staffAdditionalInfoDTO, shiftWithViolatedInfo, shift);
        return shiftWithViolatedInfo;
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
            plannedTimeId = activityConfiguration.getAbsencePlannedTime().getPlannedTimeId();
            break;
        }
        // checking weather this is allowed to staff or not
        if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getIncludedPlannedTime()).isPresent() && plannedTimeId.equals(staffAdditionalInfoDTO.getUnitPosition().getExcludedPlannedTime())) {
            plannedTimeId = staffAdditionalInfoDTO.getUnitPosition().getIncludedPlannedTime();
        }
        return plannedTimeId;
    }

    private BigInteger getPresencePlannedTime(Long unitId, BigInteger phaseId, Boolean managementPerson, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ActivityConfiguration activityConfiguration = activityConfigurationRepository.findPresenceConfigurationByUnitIdAndPhaseId(unitId, phaseId);
        if (!Optional.ofNullable(activityConfiguration).isPresent() || !Optional.ofNullable(activityConfiguration.getPresencePlannedTime()).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.activityConfiguration.notFound");
        }
        return (managementPerson) ? getApplicablePlannedType(staffAdditionalInfoDTO.getUnitPosition(), activityConfiguration.getPresencePlannedTime().getManagementPlannedTimeId())
                : getApplicablePlannedType(staffAdditionalInfoDTO.getUnitPosition(), activityConfiguration.getPresencePlannedTime().getStaffPlannedTimeId());
    }


    private void setDayTypeTOCTARuleTemplate(StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<Long, List<Day>> daytypesMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getValidDays()));
        staffAdditionalInfoDTO.getUnitPosition().getCtaRuleTemplates().forEach(ctaRuleTemplateDTO -> {
            Set<DayOfWeek> dayOfWeeks = new HashSet<>();
            List<LocalDate> publicHolidays = new ArrayList<>();
            for (Long dayTypeId : ctaRuleTemplateDTO.getDayTypeIds()) {
                List<Day> currentDay = daytypesMap.get(dayTypeId);
                if (currentDay == null) {
                    exceptionService.dataNotFoundByIdException("error.dayType.notFound", dayTypeId);
                }
                currentDay.forEach(day -> {
                    if (!day.name().equals(EVERYDAY)) {
                        dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
                    } else {
                        dayOfWeeks.addAll(Arrays.asList(DayOfWeek.values()));
                    }
                });
                List<LocalDate> publicHoliday = staffAdditionalInfoDTO.getPublicHoliday().get(dayTypeId);
                if (CollectionUtils.isNotEmpty(publicHoliday)) {
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


    private void saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shifts.get(0).getUnitId(), shifts.get(0).getStartDate());
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTOS.get(0).getStartDate());
        ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activity, ActivityDTO.class);
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activityDTO);
            shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, false);
            Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, Shift.class);
            shifts.add(shift);
            setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
        }
        saveShiftWithActivity(phase, shifts, staffAdditionalInfoDTO);
        ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
    }


    public ShiftWithViolatedInfoDTO updateShift( ShiftDTO shiftDTO, String type) {

        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }
        shiftValidatorService.validateStatusOfShiftOnUpdate(shift, shiftDTO);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.cta.notFound", shiftDTO.getStartDate());
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivities().get(0).getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivities().get(0).getActivityId());
        }
        Activity activityOld = activityRepository.findActivityByIdAndEnabled(shift.getActivities().get(0).getActivityId());
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        //copy old state of activity object
        Shift oldStateOfShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activity, ActivityDTO.class);
        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activityDTO);
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate());
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, false);
        shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
            List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
            setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
            saveShiftWithActivity(phase, activityIds, activityWrapperMap, shift, staffAdditionalInfoDTO);

            payOutService.updatePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
            boolean presenceTypeShift = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
            if (activityWrapper.getTimeType().equals(TimeTypes.WORKING_TYPE.toString())) {
                applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shift.getStartDate(), shift,
                        true, oldStateOfShift, presenceTypeShift, false, activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_ABSENCE_TO_PRESENCE
                        , activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_PRESENCE_TO_ABSENCE));
            }

        }
        shiftWithViolatedInfoDTO.setShifts(Arrays.asList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }

    public ShiftFunctionWrapper getShiftByStaffId(Long unitId, Long staffId, Date startDate, Date endDate, Long week, Long unitPositionId, String type) {

        Map<LocalDate, FunctionDTO> functionDTOMap = new HashMap();
        if(Optional.ofNullable(unitPositionId).isPresent()){
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, type, unitPositionId);
            if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent() || staffAdditionalInfoDTO.getUnitId() == null) {
                exceptionService.dataNotFoundByIdException("message.staff.belongs", staffId, type);
            }
            List<AppliedFunctionDTO> appliedFunctionDTOs = null;
            if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
                appliedFunctionDTOs = staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions();
            }

            if (CollectionUtils.isNotEmpty(appliedFunctionDTOs)) {
                for (AppliedFunctionDTO appliedFunctionDTO : appliedFunctionDTOs) {
                    if (CollectionUtils.isNotEmpty(appliedFunctionDTO.getAppliedDates())) {
                        FunctionDTO functionDTO = new FunctionDTO(appliedFunctionDTO.getId(), appliedFunctionDTO.getName(), appliedFunctionDTO.getIcon());
                        for (LocalDate date : appliedFunctionDTO.getAppliedDates()) {
                            functionDTOMap.put(date, functionDTO);
                        }
                    }
                }
            }
        }
        //When UnitPositionID is not present then we are retreiving shifts for all staffs(NOT only for UnitPosition).
        if (startDate == null) {
            startDate = DateUtils.getDate();
            if (endDate == null) {
                endDate = DateUtils.getDate();
            }
        }
        List<ShiftDTO> shifts = (Optional.ofNullable(unitPositionId).isPresent()) ? shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, startDate, endDate, unitId) :
                shiftMongoRepository.findAllShiftsBetweenDurationOfUnitAndStaffId(staffId, startDate, endDate, unitId);

        Map<LocalDate, List<ShiftDTO>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        return new ShiftFunctionWrapper(shiftsMap, functionDTOMap);
    }

    public void deleteShift(BigInteger shiftId) {
        Shift shift = shiftMongoRepository.findOne(shiftId);
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftId);
        }
        shiftValidatorService.validateStatusOfShiftOnDelete(shift);
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shift.getStaffId(), ORGANIZATION, shift.getUnitPositionId());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getStartDate());
        validateStaffingLevel(phase, shift, activityWrapper.getActivity(), false, staffAdditionalInfoDTO);
        Specification<BigInteger> shiftAllowedToDelete = new ShiftAllowedToDelete(activityWrapper.getActivity().getRulesActivityTab().getEligibleForSchedules(), staffAdditionalInfoDTO.getUserAccessRoleDTO());
        Specification<BigInteger> activitySpecification = shiftAllowedToDelete;
        List<String> messages = activitySpecification.isSatisfiedString(phase.getId());
        if (!messages.isEmpty()) {
            exceptionService.actionNotPermittedException(messages.get(0));
        }
        shift.setDeleted(true);

        List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getStartDate());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
        Long functionId=null;
        if(CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions()) && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)
                && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)){
            functionId= genericIntegrationService.removeFunctionFromUnitPositionByDate(shift.getUnitId(),shift.getUnitPositionId(),shift.getStartDate());

        }
        shift.setFunctionId(functionId);
        saveShiftWithActivity(phase, activityIds, activityWrapperMap, shift, staffAdditionalInfoDTO);
        payOutService.deletePayOut(shift.getId());

    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }


    private void updateWTACounter(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftWithViolatedInfoDTO shiftWithViolatedInfo, Shift shift) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), DateUtils.asLocalDate(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff());
        Map<BigInteger, StaffWTACounter> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(k -> k.getRuleTemplateId(), v -> v));
        List<StaffWTACounter> updatedStaffCounters = new ArrayList<>();
        shiftWithViolatedInfo.getViolatedRules().getWorkTimeAgreements().forEach(workTimeAgreementRuleViolation -> {

            StaffWTACounter staffWTACounter = staffWTACounterMap.getOrDefault(workTimeAgreementRuleViolation.getRuleTemplateId(), new StaffWTACounter(planningPeriod.getStartDate(), planningPeriod.getEndDate(), workTimeAgreementRuleViolation.getRuleTemplateId(), staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitId(), 0));
            staffWTACounter.setCount(workTimeAgreementRuleViolation.getCounter() - 1);
            updatedStaffCounters.add(staffWTACounter);
        });
        if (!updatedStaffCounters.isEmpty()) {
            save(updatedStaffCounters);
        }
    }

    private void validateStaffingLevel(Phase phase, Shift shift, Activity activity, boolean checkOverStaffing, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Date shitfStartDate = shift.getActivities().get(0).getStartDate();
        Date shiftEndDate = shift.getActivities().get(shift.getActivities().size()-1).getEndDate();
        if (activity.getRulesActivityTab().isEligibleForStaffingLevel()) {
            PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
            if (!Optional.ofNullable(phaseSettings).isPresent()) {
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            if (!phaseSettings.isManagementEligibleForOverStaffing() || !phaseSettings.isManagementEligibleForUnderStaffing() || !phaseSettings.isStaffEligibleForOverStaffing() || !phaseSettings.isStaffEligibleForUnderStaffing()) {
                Date startDate1 = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shitfStartDate).truncatedTo(ChronoUnit.DAYS));
                Date endDate1 = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftEndDate).truncatedTo(ChronoUnit.DAYS));
                List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(shift.getUnitId(), startDate1, endDate1);
                if (!Optional.ofNullable(staffingLevels).isPresent() || staffingLevels.isEmpty()) {
                    exceptionService.actionNotPermittedException("message.staffingLevel.absent");
                }
                List<Shift> shifts = shiftMongoRepository.findShiftBetweenDuration(shitfStartDate, shiftEndDate, shift.getUnitId());
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
                                if (shift1.getActivities().get(0).getActivityId().equals(activity.getId()) && interval.overlaps(shift1.getInterval())) {
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

    public ShiftWithViolatedInfoDTO addSubShift(Long unitId, ShiftDTO shiftDTO, String type) {
        Shift shift;
        if (shiftDTO.getId() != null) {
            shift = shiftMongoRepository.findOne(shiftDTO.getId());
            if (!Optional.ofNullable(shift).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
            }
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getStaffId());
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivities().get(0).getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivities().get(0).getActivityId());
        }
        shift = buildShift(shiftDTO);
        shift.setUnitId(unitId);
        ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activity, ActivityDTO.class);
        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activityDTO);
        shiftWithActivityDTO.setUnitId(unitId);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate());
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, false);
        verifyCompositeShifts(shiftDTO, activity);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
            List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
            setDayTypeTOCTARuleTemplate(staffAdditionalInfoDTO);
            saveShiftWithActivity(phase, activityIds, activityWrapperMap, shift, staffAdditionalInfoDTO);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
            payOutService.savePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);

        }
        shiftWithViolatedInfoDTO.setShifts(Arrays.asList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }

    /**
     * This method is used to check the timings overlap of sub shifts
     */


    private void verifyCompositeShifts(ShiftDTO shiftDTO, Activity activity) {
        if (!Optional.ofNullable(activity.getCompositeActivities()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.sub-shift.activity.create");
        }
        Set<BigInteger> activityIds = shiftDTO.getActivities().stream()
                .filter(act -> !(PAID_BREAK.equalsIgnoreCase(act.getActivityName()) || UNPAID_BREAK.equalsIgnoreCase(act.getActivityName())))
                .map(act -> act.getActivityId())
                .collect(Collectors.toSet());

        Set<BigInteger> allowedActivities = activity.getCompositeActivities().stream().map(CompositeActivity::getActivityId).collect(Collectors.toSet());
        if (!allowedActivities.containsAll(activityIds)) {
            exceptionService.invalidRequestException("message.activity.multishift");
        }

    }


    public Boolean addSubShifts(Long unitId, List<ShiftDTO> shiftDTOS, String type) {
        for (ShiftDTO shiftDTO : shiftDTOS) {
            ShiftDTO shiftQueryResult = createShift(unitId, shiftDTO, "Organization", true).getShifts().get(0);
            shiftDTO.setId(shiftQueryResult.getId());
            if (shiftDTO.getActivities().size() > 1) {
                addSubShift(unitId, shiftDTO, type);
            }
        }
        return true;
    }

    private void getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, String timeType, Date fromDate) {
        Date endDate = new DateTime(fromDate).withTimeAtStartOfDay().plusDays(8).toDate();
        Date startDate = new DateTime(fromDate).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).toDate();
        List<ShiftDTO> shiftQueryResultsInInterval = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        Date shiftDate = fromDate;
        for (int day = 0; day < 7; day++) {
            ShiftDTO shiftDTO = calculateAverageShiftByActivity(shiftQueryResultsInInterval, activity, staffAdditionalInfoDTO, shiftDate);
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            shiftDTOS.add(shiftDTO);
            shiftDate = new DateTime(shiftDate).plusDays(1).toDate();
        }
        validateShifts(shiftDTOS);
        if (!shiftDTOS.isEmpty()) {
            saveShifts(activity, staffAdditionalInfoDTO, shiftDTOS);
        }
    }

    private void validateShifts(List<ShiftDTO> shiftDTOS) {
        Date shiftsStartDate = shiftDTOS.get(0).getActivities().get(0).getStartDate();
        Date shiftsEndDate = shiftDTOS.get(shiftDTOS.size() - 1).getActivities().get(shiftDTOS.get(shiftDTOS.size() - 1).getActivities().size() - 1).getEndDate();
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPosition(shiftDTOS.get(0).getUnitPositionId(), shiftsStartDate, shiftsEndDate);
        if (!shifts.isEmpty()) {
            exceptionService.duplicateDataException("message.shift.date.startandend", shiftsStartDate, shiftsEndDate);
        }
    }

    private ShiftDTO calculateAverageShiftByActivity(List<ShiftDTO> shifts, Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date fromDate) {
        int contractualMinutesInADay = staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes() / staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek();
        ShiftActivity shiftActivity = new ShiftActivity(activity.getId(), activity.getName());
        Integer startAverageMin = null;
        if (shifts != null && !shifts.isEmpty() && activity.getTimeCalculationActivityTab().getHistoryDuration() != 0) {
            startAverageMin = getStartAverage(new DateTime(fromDate).getDayOfWeek(), shifts);

        }
        DateTime startDateTime;
        if (startAverageMin != null) {
            startDateTime = new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes(startAverageMin);
        } else {
            startDateTime = new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes((activity.getTimeCalculationActivityTab().getDefaultStartTime().getHour() * 60) + activity.getTimeCalculationActivityTab().getDefaultStartTime().getMinute());
        }
        shiftActivity.setStartDate(startDateTime.toDate());
        shiftActivity.setEndDate(startDateTime.plusMinutes(contractualMinutesInADay).toDate());
        if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
            Interval shiftInterval = new Interval(new DateTime(shiftActivity.getStartDate()), new DateTime(shiftActivity.getEndDate()));
            Optional<ShiftDTO> shift = shifts.stream().filter(s -> shiftInterval.overlaps(new Interval(s.getStartDate().getTime(), s.getEndDate().getTime()))).findFirst();
            if (shift.isPresent()) {
                exceptionService.actionNotPermittedException("message.shift.date.startandend");
            }
        }
        return new ShiftDTO(Arrays.asList(shiftActivity), staffAdditionalInfoDTO.getUnitId(), staffAdditionalInfoDTO.getId(), staffAdditionalInfoDTO.getUnitPosition().getId());
    }

    private Integer getStartAverage(int day, List<ShiftDTO> shifts) {
        List<ShiftDTO> updatedShifts = shifts.stream().filter(s -> new DateTime(s.getStartDate()).getDayOfWeek() == day).collect(Collectors.toList());
        updatedShifts = getFilteredShiftsByStartTime(updatedShifts);
        Integer startAverageMin = null;
        if (updatedShifts != null && !updatedShifts.isEmpty()) {
            startAverageMin = updatedShifts.stream().mapToInt(s -> new DateTime(s.getStartDate()).getMinuteOfDay()).sum() / updatedShifts.size();
        }
        return startAverageMin;
    }

    private List<ShiftDTO> getFilteredShiftsByStartTime(List<ShiftDTO> shifts) {
        shifts.sort(Comparator.comparing(ShiftDTO::getStartDate));
        List<ShiftDTO> shiftQueryResults = new ArrayList<>();
        LocalDate localDate = null;
        for (ShiftDTO shift : shifts) {
            if (!DateUtils.asLocalDate(shift.getStartDate()).equals(localDate)) {
                localDate = DateUtils.asLocalDate(shift.getStartDate());
                shiftQueryResults.add(shift);
            }
        }
        return shiftQueryResults;
    }


    private int activityChangeStatus(Activity activityOld, Activity activityCurrent) {
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

    private Object[] getActivitiesAndShiftIds(List<ShiftActivitiesIdDTO> shifts) {
        List<BigInteger> shiftIds = new ArrayList<>();
        Set<BigInteger> activitiesIds = new HashSet<>();
        shifts.forEach(s -> {
            shiftIds.add(s.getShiftId());
            activitiesIds.addAll(s.getActivityIds());
        });
        return new Object[]{shiftIds, activitiesIds};
    }

    public List<ShiftActivityResponseDTO> updateStatusOfShifts(Long unitId, ShiftPublishDTO shiftPublishDTO) {
        Object[] objects = getActivitiesAndShiftIds(shiftPublishDTO.getShifts());
        Map<BigInteger, BigInteger> activityIdsMap = ((Set<BigInteger>) objects[1]).stream().collect(Collectors.toMap(k -> k, v -> v));
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc((List<BigInteger>) objects[0]);
        List<ShiftActivityResponseDTO> shiftActivityResponseDTOS = new ArrayList<>(shifts.size());
        if (!shifts.isEmpty() && objects[1] != null) {
            Set<BigInteger> activityIds = (Set<BigInteger>) objects[1];
            Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getStartDate())).collect(Collectors.toSet());
            Map<LocalDate, Phase> phaseListByDate = phaseService.getPhasesByDates(unitId, dates);
            List<BigInteger> phaseIds = phaseListByDate.values().stream().map(p -> p.getId()).collect(Collectors.toList());
            List<ActivityShiftStatusSettings> activityShiftStatusSettings = activityAndShiftStatusSettingsRepository.findByPhaseIdsAndActivityIdsAndShiftStatus(phaseIds, activityIds, shiftPublishDTO.getStatus());
            StaffAccessGroupDTO staffAccessGroupDTO = genericRestClient.publishRequest(null, null, true, IntegrationOperation.GET, "/staff/access_groups", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAccessGroupDTO>>() {
            });
            Map<StringBuffer, ActivityShiftStatusSettings> activityShiftStatusSettingsMap = activityShiftStatusSettings.stream().collect(Collectors.toMap(k -> new StringBuffer(k.getActivityId().toString()).append(k.getPhaseId()), v -> v));
            for (Shift shift : shifts) {
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    if (activityIdsMap.containsKey(shiftActivity.getId())) {
                        Phase phase = phaseListByDate.get(DateUtils.asLocalDate(shift.getStartDate()));
                        ActivityShiftStatusSettings activityShiftStatusSettings1 = activityShiftStatusSettingsMap.get(new StringBuffer(shiftActivity.getActivityId().toString()).append(phase.getId()));
                        boolean validAccessGroup = validateAccessGroup(activityShiftStatusSettings1, staffAccessGroupDTO);
                        ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(shift.getId());
                        if (validAccessGroup) {
                            shiftActivity.getStatus().add(shiftPublishDTO.getStatus());
                            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage("message.shift.status.added"), true));
                        } else {
                            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage("access.group.not.matched"), false));
                        }
                        shiftActivityResponseDTOS.add(shiftActivityResponseDTO);
                    }
                }
            }
            save(shifts);
        }
        return shiftActivityResponseDTOS;
    }


    public ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, Date startDate, Date endDate) {
        List<ShiftDTO> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, startDate, endDate);
        UserAccessRoleDTO userAccessRoleDTO = genericIntegrationService.getAccessRolesOfStaff(unitId);
        List<OpenShift> openShifts = userAccessRoleDTO.getManagement() ? openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate, endDate) :
                openShiftNotificationMongoRepository.findValidOpenShiftsForStaff(userAccessRoleDTO.getStaffId(), startDate, endDate);

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
        return new ShiftWrapper(assignedShifts, openShiftResponseDTOS, staffAccessRoleDTO);
    }

    public CopyShiftResponse copyShifts(Long unitId, CopyShiftDTO copyShiftDTO) {
        List<DateWiseShiftResponse> shifts = shiftMongoRepository.findAllByIdGroupByDate(copyShiftDTO.getShiftIds());
        if (!Optional.ofNullable(shifts).isPresent() || shifts.isEmpty()) {
            exceptionService.invalidOperationException("message.shift.notBlank");
        }
        Set<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getShifts().stream().flatMap(ss -> ss.getActivities().stream().map(a -> a.getActivityId()))).collect(Collectors.toSet());

        List<Activity> activities = activityRepository.findAllActivitiesByIds(activityIds);
        Map<BigInteger, Activity> activityMap = activities.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<StaffUnitPositionDetails> staffDataList = restClient.getStaffsUnitPosition(unitId, copyShiftDTO.getStaffIds(), copyShiftDTO.getExpertiseId());
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByUnitIdAndDeletedFalseOrderByCreatedAtAsc(unitId);

        Map<BigInteger, ActivityWrapper> breakActivitiesMap = shiftBreakActivityService.getBreakActivities(breakSettings);
        Integer unCopiedShiftCount = 0;
        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();

        for (Long currentStaffId : copyShiftDTO.getStaffIds()) {

            StaffUnitPositionDetails staffUnitPosition = staffDataList.parallelStream().filter(unitPosition -> unitPosition.getStaff().getId().equals(currentStaffId)).findFirst().get();
            boolean paid = staffUnitPosition.getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID);
            // TODO PAVAN handle error
            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffUnitPosition, activityMap, copyShiftDTO, breakSettings, breakActivitiesMap, paid);
            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("error"));
            unCopiedShiftCount += response.get("error").size();
            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);
        }
        copyShiftResponse.setUnCopiedShiftCount(unCopiedShiftCount);

        return copyShiftResponse;
    }

    private Map<String, List<ShiftResponse>> copyForThisStaff(List<DateWiseShiftResponse> shifts, StaffUnitPositionDetails staffUnitPosition, Map<BigInteger, Activity> activityMap, CopyShiftDTO copyShiftDTO, List<BreakSettings> breakSettings, Map<BigInteger, ActivityWrapper> breakActivitiesMap, Boolean paid) {
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
                ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift, ShiftWithActivityDTO.class);
                shiftWithActivityDTO.getActivities().forEach(s -> {
                    ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityMap.get(s.getActivityId()), ActivityDTO.class);
                    s.setActivity(activityDTO);
                });
                List<String> validationMessages = shiftValidatorService.validateShiftWhileCopy(shiftWithActivityDTO, staffUnitPosition);
                shiftResponse = addShift(validationMessages, sourceShift, staffUnitPosition, shiftCreationDate, newShifts);
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
        if (!newShifts.isEmpty()) {
            save(newShifts);
        }
        return statusMap;
    }

    private ShiftResponse addShift(List<String> responseMessages, Shift sourceShift, StaffUnitPositionDetails staffUnitPosition, LocalDate shiftCreationFirstDate, List<Shift> newShifts) {
        List<BigInteger> activityIds = sourceShift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        if (responseMessages.isEmpty()) {
            Shift copiedShift = new Shift(DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getStartDate())), DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getEndDate())),
                    sourceShift.getRemarks(), sourceShift.getActivities(), staffUnitPosition.getStaff().getId(), sourceShift.getUnitId(),
                    sourceShift.getScheduledMinutes(), sourceShift.getDurationMinutes(), sourceShift.getExternalId(), staffUnitPosition.getId(), sourceShift.getParentOpenShiftId(), sourceShift.getAllowedBreakDurationInMinute(), sourceShift.getId());
            List<ShiftActivity> shiftActivities = shiftBreakActivityService.addBreakInShifts(activityWrapperMap, copiedShift, staffUnitPosition);
           copiedShift.getActivities().addAll(shiftActivities);
            newShifts.add(copiedShift);
            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), Arrays.asList(NO_CONFLICTS), true, shiftCreationFirstDate);

        } else {
            List<String> errors = responseMessages.stream().map(responseMessage -> localeService.getMessage(responseMessage)).collect(Collectors.toList());
            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), errors, false, shiftCreationFirstDate);
        }
    }


    public List<ShiftDTO> getShiftOfStaffByExpertiseId(Long unitId, Long staffId, String startDateAsString, String endDateAsString, Long expertiseId) throws ParseException {
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

        List<ShiftDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, startDateInISO, endDateInISO, unitId);
        shifts.stream().map(s -> s.sortShifts()).collect(Collectors.toList());
        return shifts;
    }


    private Shift buildShift(ShiftDTO shiftDTO) {
        Shift shift = new Shift(shiftDTO.getId(), shiftDTO.getStartDate(),
                shiftDTO.getEndDate(), shiftDTO.getBid(), shiftDTO.getpId(), shiftDTO.getBonusTimeBank(), shiftDTO.getAmount(),
                shiftDTO.getProbability(), shiftDTO.getAccumulatedTimeBankInMinutes(), shiftDTO.getRemarks(), shiftDTO.getActivities(), shiftDTO.getStaffId(), shiftDTO.getUnitId(), shiftDTO.getUnitPositionId());
        shift.setAllowedBreakDurationInMinute(shiftDTO.getAllowedBreakDurationInMinute());
        return shift;
    }


    private ShiftWithActivityDTO buildResponse(ShiftDTO shiftDTO, ActivityDTO activity) {
        //TODO It should work on Multiple activity
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftWithActivityDTO.class);
        shiftWithActivityDTO.getActivities().get(0).setActivity(activity);
        shiftWithActivityDTO.setStatus(Arrays.asList(ShiftStatus.UNPUBLISHED));
        return shiftWithActivityDTO;
    }


    public List<Shift> getAllShiftByIds(List<String> shiftIds) {
        return shiftMongoRepository.findAllByIds(shiftIds);
    }

    public void deleteShiftsAndOpenShiftsOnEmploymentEnd(Long staffId, LocalDateTime employmentEndDate, Long unitId) {
        shiftMongoRepository.deleteShiftsAfterDate(staffId, employmentEndDate);
        List<OpenShift> openShifts = openShiftMongoRepository.findAllOpenShiftsByInterestedStaff(staffId, employmentEndDate);
        if (!openShifts.isEmpty()) {
            for (OpenShift openShift : openShifts) {
                openShift.getInterestedStaff().remove(staffId);
                openShift.getAssignedStaff().remove(staffId);
            }
            save(openShifts);
        }

    }

    public void deleteShiftsAfterEmploymentEndDate(Long staffId, Long unitId, LocalDate employmentEndDate) {
        shiftMongoRepository.deleteShiftsAfterDate(staffId, employmentEndDate.atStartOfDay());
    }


    private boolean validateAccessGroup(ActivityShiftStatusSettings activityShiftStatusSettings, StaffAccessGroupDTO staffAccessGroupDTO) {
        return activityShiftStatusSettings != null && CollectionUtils.containsAny(activityShiftStatusSettings.getAccessGroupIds(), staffAccessGroupDTO.getAccessGroupIds());
    }


    private ShiftTimeDetails prepareShiftTimeDetails(ShiftActivity shiftActivity, Map<BigInteger, ShiftTimeDetails> shiftTimeDetailsMap) {
        Long shiftDurationInMinute = new DateTimeInterval(shiftActivity.getStartDate(), shiftActivity.getEndDate()).getMinutes();
        ShiftTimeDetails shiftTimeDetails = shiftTimeDetailsMap.get(shiftActivity.getActivityId());
        if (shiftTimeDetails == null) {
            shiftTimeDetails = new ShiftTimeDetails(shiftActivity.getActivityId(), DateUtils.asLocalTime(shiftActivity.getStartDate()), shiftDurationInMinute.shortValue());
        } else {
            shiftTimeDetails.setTotalTime((short) (shiftDurationInMinute.shortValue() + shiftTimeDetails.getTotalTime()));
        }
        shiftTimeDetailsMap.put(shiftActivity.getActivityId(), shiftTimeDetails);
        return shiftTimeDetails;
    }

    public DetailViewDTO getDetailViewInfo(Long unitId, Long staffId, Date shiftStartDate) {
        Date endDate = DateUtils.asDate(DateUtils.asZoneDateTime(shiftStartDate).plusDays(1));
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationBystaffId(staffId, shiftStartDate, endDate);
        List<ShiftState> shiftStates = shiftStateMongoRepository.getAllByStaffBetweenDate(staffId, shiftStartDate, endDate);
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.ABSENCE.toString()));
        List<ReasonCodeDTO> reasonCodeDTOS = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_REASONCODE, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ReasonCodeDTO>>>() {
        });
        ShiftDetailViewDTO shiftDetailViewDTO = getShiftDetailsOfStaff(timeAttendanceGracePeriod,shifts, shiftStates);
        return new DetailViewDTO(shiftDetailViewDTO, reasonCodeDTOS);
    }


    public ShiftWithViolatedInfoDTO updateShift(Long unitId, ShiftDTO shiftDTO,
             String type) {
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        DateTimeInterval graceInterval = shiftValidatorService.getGracePeriodInterval(timeAttendanceGracePeriod, DateUtils.getDate(), true);
        if (!graceInterval.contains(shiftDTO.getActivities().get(0).getStartDate())) {
            exceptionService.invalidRequestException("message.shift.cannot.update");
        }
        shiftStateMongoRepository.deleteShiftStateByShiftId(shiftDTO.getShiftId());
        if(shiftDTO.getShiftId()==null){
            shiftDTO.setShiftId(shiftDTO.getId());
        }
        shiftDTO.setId(null);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = createShift(unitId, shiftDTO, type, true);
        shiftWithViolatedInfoDTO.getShifts().get(0).setEditable(true);
        return shiftWithViolatedInfoDTO;
    }

    public ShiftDTO validateShift(BigInteger shiftId, Boolean validatedByStaff, Long unitId) {
        ShiftState shiftState = shiftStateMongoRepository.findOne(shiftId);
        ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        shiftValidatorService.validateGracePeriod(shiftDTO, validatedByStaff, unitId);
        if (validatedByStaff) {
            shiftState.setValidatedByStaffDate(LocalDate.now());
            save(shiftState);
        } else {
            shiftState.setValidatedByPlannerDate(LocalDate.now());
        }
        shiftState.setId(null);
        shiftState.setShiftId(null);
        shiftState.getActivities().forEach(a->a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
        save(shiftState);
        ShiftDTO shiftDTO1 = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        if(validatedByStaff){
            shiftDTO1.setEditable(true);
        }
        return shiftDTO1;
    }

    private ShiftDetailViewDTO getShiftDetailsOfStaff(TimeAttendanceGracePeriod timeAttendanceGracePeriod,List<Shift> shifts,List<ShiftState> shiftStates){
        List<ShiftDTO> plannedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
        List<ShiftDTO> realTimeShift = plannedShifts.stream().filter(s -> s.getAttendanceDuration() != null).collect(Collectors.toList());
        List<ShiftDTO> shiftStateDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStates, ShiftDTO.class);
        Map<BigInteger, ShiftDTO> shiftDTOMap = shiftStateDTOs.stream().filter(s->s.getShiftId()!=null).collect(Collectors.toMap(ShiftDTO::getShiftId, v -> v));
        DateTimeInterval graceInterval = shiftValidatorService.getGracePeriodInterval(timeAttendanceGracePeriod, DateUtils.getDate(), true);
        List<ShiftDTO> updateRealTime = new ArrayList<>();
        for (ShiftDTO shift : realTimeShift) {
            ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftDTOMap.getOrDefault(shift.getId(), shift), ShiftDTO.class);
            shiftDTO.setClockIn(shiftDTO.getAttendanceDuration().getFrom());
            shiftDTO.setClockOut(shiftDTO.getAttendanceDuration().getTo());
            if (shiftDTO.getValidatedByStaffDate() == null) {
                if (graceInterval.contains(shift.getStartDate())) {
                    shiftDTO.setEditable(true);
                }
            }
            updateRealTime.add(shiftDTO);
        }
        List<ShiftDTO> staffValidatedShifts = shiftStateDTOs.stream().filter(s -> s.getShiftId()==null && s.getValidatedByStaffDate() != null && s.getValidatedByPlannerDate()==null).collect(Collectors.toList());
        if(!staffValidatedShifts.isEmpty()) {
            graceInterval = shiftValidatorService.getGracePeriodInterval(timeAttendanceGracePeriod, staffValidatedShifts.get(0).getStartDate(), false);
            for (ShiftDTO staffValidatedShift : staffValidatedShifts) {
                if (graceInterval.contains(staffValidatedShift.getStartDate())) {
                    staffValidatedShift.setEditable(true);
                }
            }
        }
        List<ShiftDTO> plannerValidatedShifts = shiftStateDTOs.stream().filter(s -> s.getValidatedByPlannerDate() != null).collect(Collectors.toList());
        return new ShiftDetailViewDTO(plannedShifts, updateRealTime, staffValidatedShifts, plannerValidatedShifts);
    }

    public CompactViewDTO getCompactViewDetails(Long unitId,Date shiftStartDate){
        Date endDate = DateUtils.asDate(DateUtils.asZoneDateTime(shiftStartDate).plusDays(1));
        List<StaffDTO> staffResponseDTOS = staffRestClient.getStaffListByUnit();
        List<Long> staffIds = staffResponseDTOS.stream().map(s->s.getId()).collect(Collectors.toList());
        List<Shift> shifts = shiftMongoRepository.findShiftByStaffIdsAndDate(staffIds, shiftStartDate, endDate);
        List<ShiftState> shiftStates = shiftStateMongoRepository.getAllByStaffsByIdsBetweenDate(staffIds, shiftStartDate, endDate);
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.ABSENCE.toString()));

        List<ReasonCodeDTO> reasonCodeDTOS = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_REASONCODE, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ReasonCodeDTO>>>(){});
        Map<Long,List<Shift>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId,Collectors.toList()));
        Map<Long,List<ShiftState>> shiftStateMap = shiftStates.stream().collect(Collectors.groupingBy(Shift::getStaffId,Collectors.toList()));
        Map<Long,ShiftDetailViewDTO> shiftDetailViewDTOMap = staffIds.stream().collect(Collectors.toMap(staffId->staffId,staffId->getShiftDetailsOfStaff(timeAttendanceGracePeriod,shiftsMap.getOrDefault(staffId,new ArrayList<>()),shiftStateMap.getOrDefault(staffId,new ArrayList<>()))));
        return new CompactViewDTO(shiftDetailViewDTOMap,reasonCodeDTOS);
    }

}
