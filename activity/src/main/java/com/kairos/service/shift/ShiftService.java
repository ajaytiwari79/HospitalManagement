package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.ArrayUtil;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.attendance.AttendanceTimeSlotDTO;
import com.kairos.dto.activity.attendance.TimeAndAttendanceDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.experties.AppliedFunctionDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.Day;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.enums.shift.ShiftFilterParam;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.enums.shift.ViewType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.template_types.BreakWTATemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelActivityRankRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.persistence.repository.unit_settings.TimeAttendanceGracePeriodRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.ShiftAllowedToDelete;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.attendence_setting.TimeAndAttendanceService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.unit_settings.PhaseSettingsService;
import com.kairos.service.user_service_data.UnitDataService;
import com.kairos.service.wta.WTAService;
import com.kairos.utils.ShiftValidatorService;
import com.kairos.utils.event.ShiftNotificationEvent;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.shift.ShiftStatus.*;
import static com.kairos.utils.ShiftValidatorService.getValidDays;
import static java.util.stream.Collectors.groupingBy;


/**
 * Created by vipul on 30/8/17.
 */
@Service
public class ShiftService extends MongoBaseService {
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private TimeAndAttendanceRepository timeAndAttendanceRepository;
    @Inject
    private ApplicationContext applicationContext;
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
    private PhaseSettingsService phaseSettingsService;
    @Inject
    private PhaseSettingsRepository phaseSettingsRepository;
    @Inject
    private LocaleService localeService;
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
    private ShiftBreakService shiftBreakService;
    @Inject
    private StaffingLevelActivityRankRepository staffingLevelActivityRankRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeAndAttendanceService timeAndAttendanceService;
    @Inject
    private ShiftReminderService shiftReminderService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private ShiftStateService shiftStateService;

    public ShiftWithViolatedInfoDTO createShift(Long unitId, ShiftDTO shiftDTO, String type, boolean byTandAPhase) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivities().get(0).getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.invalidRequestException("message.activity.id", shiftDTO.getActivities().get(0).getActivityId());
        }
        Set<Long> reasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(ShiftActivityDTO::getAbsenceReasonCodeId).collect(Collectors.toSet());

        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(shiftDTO.getShiftDate(), shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), reasonCodeIds);
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException("message.staff.notfound");
        }
        if (!staffAdditionalInfoDTO.getUnitPosition().isPublished()) {
            exceptionService.invalidRequestException("message.shift.not.published");
        }

        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.actionNotPermittedException("message.unit.position");
        }
        if(!staffAdditionalInfoDTO.getUnitPosition().isPublished()){
            exceptionService.invalidRequestException("message.shift.not.published");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), asDate(shiftDTO.getShiftDate()));
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.invalidRequestException("error.cta.notFound", asDate(shiftDTO.getShiftDate()));
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        if ((FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            shiftWithViolatedInfoDTO = createAbsenceTypeShift(activityWrapper, shiftDTO, staffAdditionalInfoDTO);
        } else {
            boolean shiftExists;
            if (!byTandAPhase) {
                shiftExists = shiftMongoRepository.existShiftsBetweenDurationByStaffUserId(staffAdditionalInfoDTO.getStaffUserId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate(), ShiftType.PRESENCE);
            } else {
                shiftExists = shiftMongoRepository.findShiftBetweenDurationByUnitPositionNotEqualToShiftId(shiftDTO.getShiftId(), staffAdditionalInfoDTO.getStaffUserId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate(), ShiftType.PRESENCE);
            }
            if (shiftExists) {
                exceptionService.invalidRequestException("message.shift.date.startandend", shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
            }
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, shiftDTO.getActivities().get(0).getStartDate(), null);
            if (phase == null) {
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            shiftDTO.setShiftType(ShiftType.PRESENCE);
            shiftWithViolatedInfoDTO = saveShift(activityWrapper, staffAdditionalInfoDTO, shiftDTO, phase, byTandAPhase);

        }
        addReasonCode(shiftWithViolatedInfoDTO.getShifts(), staffAdditionalInfoDTO.getReasonCodes());
        return shiftWithViolatedInfoDTO;
    }

    private void addReasonCode(List<ShiftDTO> shiftDTOS, List<ReasonCodeDTO> reasonCodes) {
        Map<Long, ReasonCodeDTO> reasonCodeDTOMap = reasonCodes.stream().collect(Collectors.toMap(reasoncodeDTO -> reasoncodeDTO.getId(), v -> v));
        for (ShiftDTO shift : shiftDTOS) {
            Set<BigInteger> multipleActivityCount = new HashSet<>();
            for (ShiftActivityDTO activity : shift.getActivities()) {
                activity.setReasonCode(reasonCodeDTOMap.get(activity.getAbsenceReasonCodeId()));
                if (!activity.isBreakShift()) {
                    multipleActivityCount.add(activity.getActivityId());
                }
            }
            shift.setMultipleActivity(multipleActivityCount.size() > MULTIPLE_ACTIVITY);
        }
    }

    private ShiftWithViolatedInfoDTO createAbsenceTypeShift(ActivityWrapper activityWrapper, ShiftDTO shiftDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        Long absenceReasonCodeId = shiftDTO.getActivities().get(0).getAbsenceReasonCodeId();
        if (activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
            Date endDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            Date startDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).minusWeeks(activityWrapper.getActivity().getTimeCalculationActivityTab().getHistoryDuration()).withTimeAtStartOfDay().toDate();
            List<ShiftDTO> shifts = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
            shiftDTO = calculateAverageShiftByActivity(shifts, activityWrapper.getActivity(), staffAdditionalInfoDTO, DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate(), absenceReasonCodeId);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate(), null);
            if (isNull(phase)) {
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            shiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftWithViolatedInfoDTO = saveShift(activityWrapper, staffAdditionalInfoDTO, shiftDTO, phase, false);
        } else {
            Date shiftFromDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate();
            shiftWithViolatedInfoDTO = getAverageOfShiftByActivity(staffAdditionalInfoDTO, activityWrapper.getActivity(), shiftFromDate, absenceReasonCodeId);
        }
        return shiftWithViolatedInfoDTO;
    }


    private ShiftWithViolatedInfoDTO saveShift(ActivityWrapper activityWrapper, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO, Phase phase, boolean byTandAPhase) {
        ShiftState shiftState = null;
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shiftDTO.getUnitId(), DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()));
        Shift mainShift;
        if (byTandAPhase) {
            shiftState = shiftStateMongoRepository.findOne(shiftDTO.getId());
            if (shiftState != null) {
                mainShift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
                mainShift.setId(shiftState.getId());
                ((ShiftState) mainShift).setAccessGroupRole(shiftState.getAccessGroupRole());
                ((ShiftState) mainShift).setValidated(shiftState.getValidated());
            } else {
                mainShift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
            }
        } else {
            mainShift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
        }
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.onlyDate(shiftDTO.getActivities().get(0).getStartDate()));
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException("message.wta.notFound");
        }
        List<BigInteger> activityIds = shiftDTO.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);

        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));

        ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap);
        boolean isUpdate = byTandAPhase;
        mainShift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
        mainShift.setPlanningPeriodId(planningPeriod.getId());
        mainShift.setPhaseId(planningPeriod.getCurrentPhaseId());
        shiftValidatorService.validateStaffingLevel(phase, mainShift, activityWrapperMap, true, staffAdditionalInfoDTO);
        List<ShiftActivity> breakActivities = updateBreakInShift(mainShift, isUpdate, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets());
        if (!breakActivities.isEmpty()) {
            mainShift.setActivities(breakActivities);
        }
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, byTandAPhase);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            mainShift = saveShiftWithActivity(activityWrapperMap, mainShift, staffAdditionalInfoDTO, isUpdate);
            payOutService.savePayOut(staffAdditionalInfoDTO, mainShift, activityWrapperMap);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(mainShift, ShiftDTO.class);
            shiftDTO = timeBankService.updateShiftDTOWithTimebankDetails(shiftDTO, staffAdditionalInfoDTO);
            ShiftViolatedRules shiftViolatedRules = ObjectMapperUtils.copyPropertiesByMapper(shiftWithViolatedInfoDTO.getViolatedRules(), ShiftViolatedRules.class);
            shiftViolatedRules.setShift(mainShift);
            save(shiftViolatedRules);
            activityWrapperMap.put(activityWrapper.getActivity().getId(), activityWrapper);
            shiftReminderService.setReminderTrigger(activityWrapperMap, mainShift);
        }
        shiftWithViolatedInfoDTO.setShifts(Arrays.asList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }


    void updateTimeBankAndAvailableCountOfStaffingLevel(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift);
        ActivityWrapper activityWrapper = activityWrapperMap.get(shift.getActivities().get(0).getActivityId());
        boolean presenceTypeShift = !(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        if (TimeTypes.WORKING_TYPE.toString().equals(activityWrapper.getTimeType())) {
            staffingLevelService.updateStaffingLevelAvailableStaffCount(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shift.getStartDate(), shift, false, null, presenceTypeShift));
        }
    }


    Shift saveShiftWithActivity(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift,
                                StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean updateShift) {
        int scheduledMinutes = 0;
        int durationMinutes = 0;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            if (shiftActivity.getId() == null) {
                shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            }
            ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
            shiftActivity.setTimeType(activityWrapper.getTimeType());
            if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
                Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, activityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
                if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                    timeBankCalculationService.calculateScheduledAndDurationMinutes(shiftActivity, activityWrapper.getActivity(), staffAdditionalInfoDTO.getUnitPosition());
                    scheduledMinutes += shiftActivity.getScheduledMinutes();
                    durationMinutes += shiftActivity.getDurationMinutes();
                }
            }
            shiftActivity.setBackgroundColor(activityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
            shiftActivity.setActivityName(activityWrapper.getActivity().getName());
            shiftActivity.setPlannedTimeId(addPlannedTimeInShift(shift.getUnitId(), shift.getPhaseId(), activityWrapper.getActivity(), staffAdditionalInfoDTO));
        }
        //As discuss with Arvind Presence and Absence type of activity cann't be perform in a Shift
        Activity activity = activityWrapperMap.get(shift.getActivities().get(0).getActivityId()).getActivity();
        if ((FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            shift.setShiftType(ShiftType.ABSENCE);
        } else {
            shift.setShiftType(ShiftType.PRESENCE);
        }
        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
        shiftMongoRepository.save(shift);
        if (!updateShift) {
            updateTimeBankAndAvailableCountOfStaffingLevel(activityWrapperMap, shift, staffAdditionalInfoDTO);
        }
        return shift;
    }

    List<ShiftActivity> updateBreakInShift(Shift shift, boolean updateShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, BreakWTATemplate breakWTATemplate, List<TimeSlotWrapper> timeSlot) {
        shift.setStartDate(shift.getActivities().get(0).getStartDate());
        shift.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        List<ShiftActivity> breakActivities;
        if (updateShift) {
            breakActivities = shiftBreakService.updateBreakInShifts(activityWrapperMap, shift, staffAdditionalInfoDTO.getUnitPosition(), breakWTATemplate, timeSlot);
        } else {
            breakActivities = shiftBreakService.addBreakInShifts(activityWrapperMap, shift, staffAdditionalInfoDTO.getUnitPosition(), breakWTATemplate, timeSlot);
        }
        return breakActivities;

    }

    public void saveShiftWithActivity(Map<Date, Phase> phaseListByDate, List<Shift> shifts, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        List<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getActivities().stream().map(a -> a.getActivityId())).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        for (Shift shift : shifts) {
            int scheduledMinutes = 0;
            int durationMinutes = 0;
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
                ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
                shiftActivity.setTimeType(activityWrapper.getTimeType());
                if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
                    Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                    Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, activityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
                    if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                        timeBankCalculationService.calculateScheduledAndDurationMinutes(shiftActivity, activityWrapper.getActivity(), staffAdditionalInfoDTO.getUnitPosition());
                        scheduledMinutes += shiftActivity.getScheduledMinutes();
                        durationMinutes += shiftActivity.getDurationMinutes();
                    }
                }
                shiftActivity.setBackgroundColor(activityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
                shiftActivity.setActivityName(activityWrapper.getActivity().getName());
                shiftActivity.setPlannedTimeId(addPlannedTimeInShift(staffAdditionalInfoDTO.getUnitId(), phaseListByDate.get(shiftActivity.getStartDate()).getId(), activityWrapperMap.get(shiftActivity.getActivityId()).getActivity(), staffAdditionalInfoDTO));
            }
            shift.setPhaseId(phaseListByDate.get(shift.getActivities().get(0).getStartDate()).getId());
            shift.setScheduledMinutes(scheduledMinutes);
            shift.setDurationMinutes(durationMinutes);
            shift.setStartDate(shift.getActivities().get(0).getStartDate());
            shift.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());

        }
        shiftMongoRepository.saveEntities(shifts);
        shifts.forEach(shift -> updateTimeBankAndAvailableCountOfStaffingLevel(activityWrapperMap, shift, staffAdditionalInfoDTO));

    }

    public ShiftWithViolatedInfoDTO saveShiftAfterValidation(ShiftWithViolatedInfoDTO shiftWithViolatedInfo, String type, Boolean validatedByStaff, boolean updateShiftState, Long unitId) {
        Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithViolatedInfo.getShifts().get(0), Shift.class);
        // replace id from shift id if request come form detailed view and compact view
        if (isNotNull(shiftWithViolatedInfo.getShifts().get(0).getShiftId())) {
            shift.setId(shiftWithViolatedInfo.getShifts().get(0).getShiftId());
        }
        Date shiftStartDate = DateUtils.onlyDate(shift.getActivities().get(0).getStartDate());
        //reason code will be sanem for all shifts.
        Set<Long> reasonCodeIds = shiftWithViolatedInfo.getShifts().get(0).getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(shiftActivity -> shiftActivity.getAbsenceReasonCodeId()).collect(Collectors.toSet());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftStartDate), shift.getStaffId(), type, shift.getUnitPositionId(), reasonCodeIds);
        List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        if((FULL_WEEK.equals(activityWrapperMap.get(shift.getActivities().get(0).getActivityId()).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activityWrapperMap.get(shift.getActivities().get(0).getActivityId()).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            boolean shiftExists = shiftMongoRepository.existShiftsBetweenDurationByStaffUserId(staffAdditionalInfoDTO.getStaffUserId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate(), ShiftType.PRESENCE);
            if (shiftExists) {
                exceptionService.invalidRequestException("message.shift.date.startandend", shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
            }
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftStartDate);
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.cta.notFound", ctaResponseDTO.getId());
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        shift.setPhaseId(phase.getId());

        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.onlyDate(shiftWithViolatedInfo.getShifts().get(0).getActivities().get(0).getStartDate()));
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException("message.wta.notFound");
        }
        List<ShiftActivity> breakActivities = updateBreakInShift(shift, false, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets());
        if (!breakActivities.isEmpty()) {
            shift.setActivities(breakActivities);
        }
        shift = saveShiftWithActivity(activityWrapperMap, shift, staffAdditionalInfoDTO, false);
        payOutService.savePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
        ShiftViolatedRules shiftViolatedRules = ObjectMapperUtils.copyPropertiesByMapper(shiftWithViolatedInfo.getViolatedRules(), ShiftViolatedRules.class);
        shiftViolatedRules.setShift(shift);
        save(shiftViolatedRules);
        ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
        shiftDTO = timeBankService.updateShiftDTOWithTimebankDetails(shiftDTO, staffAdditionalInfoDTO);
        updateWTACounter(staffAdditionalInfoDTO, shiftWithViolatedInfo, shift);
        if (updateShiftState) {
            shiftDTO = updateShiftStateAfterValidatingWtaRule(shiftWithViolatedInfo.getShifts().get(0), shiftWithViolatedInfo.getShifts().get(0).getShiftId(), shiftWithViolatedInfo.getShifts().get(0).getShiftStatePhaseId());
        } else if (isNotNull(validatedByStaff)) {
            ShiftState shiftState = null;
            Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
            shiftDTO = validateShiftStateAfterValidatingWtaRule(shiftWithViolatedInfo.getShifts().get(0), shiftState, validatedByStaff, actualPhases, shiftWithViolatedInfo.getShifts().get(0).getId());
        }
        shiftWithViolatedInfo.setShifts(Arrays.asList(shiftDTO));
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


    public void setDayTypeToCTARuleTemplate(StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
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
                /*List<LocalDate> publicHoliday = staffAdditionalInfoDTO.getPublicHoliday().get(dayTypeId);
                if (CollectionUtils.isNotEmpty(publicHoliday)) {
                    publicHolidays.addAll(publicHoliday);
                }*/
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


    private ShiftWithViolatedInfoDTO saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        Set<LocalDateTime> dates = shiftDTOS.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
        Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(shiftDTOS.get(0).getUnitId(), dates);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTOS.get(0).getActivities().get(0).getStartDate());
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException("message.wta.notFound");
        }
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        Map<BigInteger, ActivityWrapper> activityWrapperMap = new HashMap<>();
        activityWrapperMap.put(activity.getId(), new ActivityWrapper(activity, timeType.getTimeTypes().toValue()));
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap);
            ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phaseListByDate.get(shiftDTO.getActivities().get(0).getStartDate()), wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, false);
            Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, Shift.class);
            shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            shifts.add(shift);
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getActivities());
            shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());
        }
        if (shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            saveShiftWithActivity(phaseListByDate, shifts, staffAdditionalInfoDTO);
            shiftDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
            shiftDTOS.sort(Comparator.comparing(ShiftDTO::getStartDate));
            LocalDate startDate = staffAdditionalInfoDTO.getUnitPosition().getStartDate();
            LocalDate endDate = asLocalDate(shiftDTOS.get(shiftDTOS.size() - 1).getStartDate());
            shiftDTOS = timeBankService.updateTimebankDetailsInShiftDTO(shiftDTOS, startDate, endDate, staffAdditionalInfoDTO);
        }
        shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
        return shiftWithViolatedInfoDTO;
    }


    public ShiftWithViolatedInfoDTO updateShift(ShiftDTO shiftDTO, String type, boolean byTAndAView) {
        Set<Long> reasonCodeIds = shiftDTO.getActivities().stream().filter(shiftActivity -> shiftActivity.getAbsenceReasonCodeId() != null).map(shiftActivity -> shiftActivity.getAbsenceReasonCodeId()).collect(Collectors.toSet());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()), shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), reasonCodeIds);
        Shift shift = shiftMongoRepository.findOne(byTAndAView ? shiftDTO.getShiftId() : shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        if (phase == null) {
            exceptionService.actionNotPermittedException("message.shift.planning.period.exits", shiftDTO.getActivities().get(0).getStartDate());
        }
        Set<BigInteger> activityIdsSet = ArrayUtil.getUnionOfList(shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()), shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        List<BigInteger> activityIds = new ArrayList<>(activityIdsSet);
        shiftValidatorService.validateStatusOfShiftOnUpdate(shift, shiftDTO);
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        Activity firstActivity = activityWrapperMap.get(shiftDTO.getActivities().get(0).getActivityId()).getActivity();
        if (!(FULL_WEEK.equals(firstActivity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(firstActivity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            boolean shiftExists = shiftMongoRepository.findShiftBetweenDurationByUnitPositionNotEqualToShiftId(byTAndAView ? shiftDTO.getShiftId() : shiftDTO.getId(), staffAdditionalInfoDTO.getStaffUserId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate(), ShiftType.PRESENCE);
            if (shiftExists) {
                exceptionService.invalidRequestException("message.shift.date.startandend", shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
            }
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        ShiftActivityIdsDTO shiftActivityIdsDTO = getActivitiesToProcess(shift.getActivities(), shiftDTO.getActivities());
        Map<BigInteger, PhaseTemplateValue> activityPerPhaseMap = constructMapOfActivityAndPhaseTemplateValue(phase, activities);

        List<ShiftActivityDTO> shiftActivities = findShiftActivityToValidateStaffingLevel(shift.getActivities(), shiftDTO.getActivities());
        shiftValidatorService.verifyShiftActivities(staffAdditionalInfoDTO.getRoles(), staffAdditionalInfoDTO.getUnitPosition().getEmploymentType().getId(), activityPerPhaseMap, shiftActivityIdsDTO);
        shiftValidatorService.verifyRankAndStaffingLevel(shiftActivities, shiftDTO.getUnitId(), activities, phase, staffAdditionalInfoDTO.getUserAccessRoleDTO());
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.cta.notFound", shiftDTO.getStartDate());
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        BigInteger activityId = shift.getActivities().get(0).getActivityId();
        Activity activityOld = activities.stream().filter(k -> k.getActivity().getId().equals(activityId)).findFirst().get().getActivity();
        //Activity activityOld = activityRepository.findActivityByIdAndEnabled(shift.getActivities().get(0).getActivityId());
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException("message.wta.notFound");
        }
        //copy old state of activity object
        Shift oldStateOfShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
        shift.setPhaseId(phase.getId());
        if (byTAndAView) {
            shift.setId(shiftDTO.getShiftId());
        }
        shift.setPlanningPeriodId(oldStateOfShift.getPlanningPeriodId());
        List<ShiftActivity> breakActivities = updateBreakInShift(shift, true, activityWrapperMap, staffAdditionalInfoDTO, wtaQueryResultDTO.getBreakRule(), staffAdditionalInfoDTO.getTimeSlotSets());
        if (!breakActivities.isEmpty()) {
            shift.setActivities(breakActivities);
        }
        ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class), activityWrapperMap);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, shift, activityWrapperMap, true, false);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            shift = saveShiftWithActivity(activityWrapperMap, shift, staffAdditionalInfoDTO, true);
            payOutService.updatePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
            timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
            shiftDTO = timeBankService.updateShiftDTOWithTimebankDetails(shiftDTO, staffAdditionalInfoDTO);
            // TODO VIPUL WE WILL UNCOMMENTS AFTER FIX mailing servive
            //shiftReminderService.updateReminderTrigger(activityWrapperMap,shift);
            Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
            //anil m2 notify event for updating staffing level
            boolean presenceTypeShift = !(firstActivity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || firstActivity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
            if (activityWrapperMap.get(firstActivity.getId()).getTimeType().equals(TimeTypes.WORKING_TYPE.toString())) {
                staffingLevelService.updateStaffingLevelAvailableStaffCount(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift,
                        true, oldStateOfShift, presenceTypeShift, false, activityChangeStatus(activityOld, firstActivity) == ACTIVITY_CHANGED_FROM_ABSENCE_TO_PRESENCE
                        , activityChangeStatus(activityOld, firstActivity) == ACTIVITY_CHANGED_FROM_PRESENCE_TO_ABSENCE));
            }

        }
        shiftWithViolatedInfoDTO.setShifts(Arrays.asList(shiftDTO));
        addReasonCode(shiftWithViolatedInfoDTO.getShifts(), staffAdditionalInfoDTO.getReasonCodes());
        return shiftWithViolatedInfoDTO;
    }

    private ShiftFunctionWrapper getShiftByStaffId(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long unitPositionId) {
        if (staffId == null) {
            exceptionService.actionNotPermittedException("staff_id.null");
        }
        Map<LocalDate, FunctionDTO> functionDTOMap = new HashMap();
        List<ReasonCodeDTO> reasonCodeDTOS;
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = null;
        if (Optional.ofNullable(unitPositionId).isPresent()) {
            staffAdditionalInfoDTO = userIntegrationService.verifyUnitPositionAndFindFunctionsAfterDate(startDate, staffId, unitPositionId);
            if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.staff.belongs", staffId);
            }
            if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
                exceptionService.actionNotPermittedException("message.unit.position", startDate.toString());
            }
            reasonCodeDTOS = staffAdditionalInfoDTO.getReasonCodes();
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
        } else {
            List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.TIME_TYPE.toString()));
            reasonCodeDTOS = userIntegrationService.getReasonCodeDTOList(unitId, requestParam);
      }
        Map<Long, ReasonCodeDTO> reasonCodeMap = reasonCodeDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        //When UnitPositionID is not present then we are retreiving shifts for all staffs(NOT only for UnitPosition).
        if (endDate == null) {
            endDate = DateUtils.getLocalDate();
        }
        List<ShiftDTO> shifts;
        if (Optional.ofNullable(unitPositionId).isPresent()) {
            shifts = shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, asDate(startDate), asDate(endDate), unitId);
        } else {
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationOfUnitAndStaffId(staffId, asDate(startDate), asDate(endDate), unitId);
        }
        addReasonCode(shifts, reasonCodeDTOS);
        for (ShiftDTO shift : shifts) {
            for (ShiftActivityDTO activity : shift.getActivities()) {
                activity.setReasonCode(reasonCodeMap.get(activity.getAbsenceReasonCodeId()));
                activity.setPlannedMinutes(activity.getScheduledMinutes() + activity.getTimeBankCtaBonusMinutes());
            }
        }
        if (isNotNull(staffAdditionalInfoDTO)) {
            shifts = timeBankService.updateTimebankDetailsInShiftDTO(shifts, staffAdditionalInfoDTO.getUnitPosition().getStartDate(), endDate, staffAdditionalInfoDTO);
        }
        Map<LocalDate, List<ShiftDTO>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        return new ShiftFunctionWrapper(shiftsMap, functionDTOMap);
    }

    public void updateShiftDailyTimeBankAndPaidOut(List<Shift> shifts, List<Shift> shiftsList, Long unitId) {
        if (!Optional.ofNullable(shifts).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.ids");
        }
        List<Long> staffIds = shifts.stream().map(shift -> shift.getStaffId()).collect(Collectors.toList());
        List<Long> unitPositionIds = shifts.stream().map(shift -> shift.getUnitPositionId()).collect(Collectors.toList());
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
        requestParam.add(new BasicNameValuePair("unitPositionIds", unitPositionIds.toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(unitId, requestParam);
        List<BigInteger> activityIdsList = shifts.stream().flatMap(s -> s.getActivities().stream().map(ShiftActivity::getActivityId)).distinct().collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIdsList);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shifts.sort(Comparator.comparing(Shift::getStartDate));
        shiftsList.sort((shift, shiftSecond) -> shift.getStartDate().compareTo(shiftSecond.getStartDate()));
        Date startDate = shifts.get(0).getStartDate();
        Date endDate = shifts.get(shifts.size() - 1).getEndDate();
        Date shiftStartDate = shiftsList.get(0).getStartDate();
        Date shiftEndDate = shiftsList.get(shiftsList.size() - 1).getEndDate();
        startDate = startDate.before(shiftStartDate) ? startDate : shiftStartDate;
        endDate = endDate.after(shiftEndDate) ? endDate : shiftEndDate;
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdsAndDate(unitPositionIds, startDate, endDate);
        Map<Long, List<CTAResponseDTO>> unitPositionAndCTAResponseMap = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getUnitPositionId));
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            if (unitPositionAndCTAResponseMap.get(staffAdditionalInfoDTO.getUnitPosition().getId()) != null) {
                List<CTAResponseDTO> ctaResponseDTOSList = unitPositionAndCTAResponseMap.get(staffAdditionalInfoDTO.getUnitPosition().getId());
                List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ctaResponseDTOSList.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
                staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaRuleTemplateDTOS);
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            }
        });
        timeBankService.saveTimeBanksAndPayOut(staffAdditionalInfoDTOS, shifts, activityWrapperMap, startDate, endDate);

    }


    public ShiftDTO deleteShift(BigInteger shiftId) {
        ShiftDTO shiftDTO = new ShiftDTO();
        Shift shift = shiftMongoRepository.findOne(shiftId);
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftId);
        }
        shiftValidatorService.validateStatusOfShiftOnDelete(shift);
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId());
        List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));

        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getUnitPositionId(), Collections.emptySet());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), null);
        shiftValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, false, staffAdditionalInfoDTO);
        Specification<BigInteger> activitySpecification = new ShiftAllowedToDelete(activityWrapper.getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues(), staffAdditionalInfoDTO.getUserAccessRoleDTO());
        List<String> messages = activitySpecification.isSatisfiedString(phase.getId());
        if (!messages.isEmpty()) {
            exceptionService.actionNotPermittedException(messages.get(0));
        }
        shift.setDeleted(true);
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getStartDate());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        Long functionId = null;
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationBystaffId(shift.getStaffId(), DateUtils.getStartOfDay(shift.getStartDate()), DateUtils.getEndOfDay(shift.getEndDate()));
        if (shifts.size() == 1 && CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions()) && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)
                && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
            functionId = userIntegrationService.removeFunctionFromUnitPositionByDate(shift.getUnitId(), shift.getUnitPositionId(), shift.getStartDate());
            shiftDTO.setFunctionDeleted(true);
            shift.setFunctionId(functionId);

        }
        shiftMongoRepository.save(shift);
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        timeBankService.updateTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.deletePayOut(shift.getId());

        boolean isShiftForPresence = !(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        staffingLevelService.updateStaffingLevelAvailableStaffCount(new ShiftNotificationEvent(shift.getUnitId(), DateUtils.onlyDate(shift.getStartDate()), shift,
                false, null, isShiftForPresence, true, false, false));
        List<BigInteger> jobIds = shift.getActivities().stream().map(ShiftActivity::getId).collect(Collectors.toList());
        shiftReminderService.deleteReminderTrigger(jobIds, shift.getUnitId());
        return shiftDTO;

    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }


    private void updateWTACounter(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftWithViolatedInfoDTO shiftWithViolatedInfo, Shift shift) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), DateUtils.asLocalDate(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(), asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate()), staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff());
        Map<String, StaffWTACounter> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(k -> k.getRuleTemplateName(), v -> v));
        List<StaffWTACounter> updatedStaffCounters = new ArrayList<>();
        shiftWithViolatedInfo.getViolatedRules().getWorkTimeAgreements().forEach(workTimeAgreementRuleViolation -> {
            int count = workTimeAgreementRuleViolation.getCounter() - 1;
            StaffWTACounter staffWTACounter = staffWTACounterMap.getOrDefault(workTimeAgreementRuleViolation.getName(), new StaffWTACounter(planningPeriod.getStartDate(), planningPeriod.getEndDate(), workTimeAgreementRuleViolation.getRuleTemplateId(), workTimeAgreementRuleViolation.getName(), staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitId()));
            staffWTACounter.setUserHasStaffRole(staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff());
            staffWTACounter.setCount(count);
            updatedStaffCounters.add(staffWTACounter);
        });
        if (!updatedStaffCounters.isEmpty()) {
            save(updatedStaffCounters);
        }
    }


    private ShiftWithViolatedInfoDTO getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, Date fromDate, Long absenceReasonCodeId) {
        Date endDate = new DateTime(fromDate).withTimeAtStartOfDay().plusDays(8).toDate();
        Date startDate = new DateTime(fromDate).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).toDate();
        List<ShiftDTO> shiftQueryResultsInInterval = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        Date shiftDate = fromDate;
        for (int day = 0; day < 7; day++) {
            ShiftDTO shiftDTO = calculateAverageShiftByActivity(shiftQueryResultsInInterval, activity, staffAdditionalInfoDTO, shiftDate, absenceReasonCodeId);
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            shiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftDTOS.add(shiftDTO);
            shiftDate = new DateTime(shiftDate).plusDays(1).toDate();
        }
        validateShifts(shiftDTOS);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
        if (!shiftDTOS.isEmpty()) {
            shiftWithViolatedInfoDTO = saveShifts(activity, staffAdditionalInfoDTO, shiftDTOS);
        }
        return shiftWithViolatedInfoDTO;
    }

    private void validateShifts(List<ShiftDTO> shiftDTOS) {
        Date shiftsStartDate = shiftDTOS.get(0).getActivities().get(0).getStartDate();
        Date shiftsEndDate = shiftDTOS.get(shiftDTOS.size() - 1).getActivities().get(shiftDTOS.get(shiftDTOS.size() - 1).getActivities().size() - 1).getEndDate();
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPosition(shiftDTOS.get(0).getUnitPositionId(), shiftsStartDate, shiftsEndDate);
        if (!shifts.isEmpty()) {
            exceptionService.duplicateDataException("message.shift.date.startandend", shiftsStartDate, shiftsEndDate);
        }
    }

    private ShiftDTO calculateAverageShiftByActivity(List<ShiftDTO> shifts, Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date fromDate, Long absenceReasonCodeId) {
        int contractualMinutesInADay = staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes() / staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek();
        ShiftActivityDTO shiftActivity = new ShiftActivityDTO(activity.getId(), activity.getName());
        Integer startAverageMin = null;
        if (shifts != null && !shifts.isEmpty() && activity.getTimeCalculationActivityTab().getHistoryDuration() != 0) {
            startAverageMin = getStartAverage(new DateTime(fromDate).getDayOfWeek(), shifts);

        }
        DateTime startDateTime = (startAverageMin != null) ?
                new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes(startAverageMin) :
                new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes((activity.getTimeCalculationActivityTab().getDefaultStartTime().getHour() * 60) + activity.getTimeCalculationActivityTab().getDefaultStartTime().getMinute());

        shiftActivity.setStartDate(startDateTime.toDate());
        shiftActivity.setEndDate(startDateTime.plusMinutes(contractualMinutesInADay).toDate());
        shiftActivity.setActivityName(activity.getName());
        shiftActivity.setAbsenceReasonCodeId(absenceReasonCodeId);
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
        Set<BigInteger> shiftActivitiyIds = ((Set<BigInteger>) objects[1]);
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc((List<BigInteger>) objects[0]);
        List<ShiftActivityResponseDTO> shiftActivityResponseDTOS = new ArrayList<>(shifts.size());
        Set<BigInteger> allActivities = shifts.stream().flatMap(s -> s.getActivities().stream().map(a -> a.getActivityId())).collect(Collectors.toSet());
        List<Activity> activities = activityRepository.findAllPhaseSettingsByActivityIds(allActivities);
        Map<BigInteger, PhaseSettingsActivityTab> activityPhaseSettingMap = activities.stream().collect(Collectors.toMap(Activity::getId, Activity::getPhaseSettingsActivityTab));
        if (isCollectionNotEmpty(shifts) && objects[1] != null) {
            Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(unitId, dates);
            StaffAccessGroupDTO staffAccessGroupDTO = userIntegrationService.getStaffAccessGroupDTO(unitId);
            for (Shift shift : shifts) {
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    if (shiftActivitiyIds.contains(shiftActivity.getId())) {
                        Phase phase = phaseListByDate.get(shift.getActivities().get(0).getStartDate());
                        PhaseSettingsActivityTab phaseSettingsActivityTab = activityPhaseSettingMap.get(shiftActivity.getActivityId());
                        PhaseTemplateValue phaseTemplateValue = phaseSettingsActivityTab.getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
                        ActivityShiftStatusSettings activityShiftStatusSettings = getActivityShiftStatusSettingByStatus(phaseTemplateValue, shiftPublishDTO.getStatus());
                        boolean validAccessGroup = validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
                        ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(shift.getId());
                        if (validAccessGroup) {
                            removeOppositeStatus(shiftActivity, shiftPublishDTO.getStatus());
                            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage("message.shift.status.added"), true, shiftActivity.getStatus()));
                        } else {
                            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(shiftActivity.getActivityName(), shiftActivity.getId(), localeService.getMessage("access.group.not.matched"), false));
                        }
                        shiftActivityResponseDTOS.add(shiftActivityResponseDTO);
                    }
                }
            }
            save(shifts);
            timeBankService.updateDailyTimeBankEntriesForStaffs(shifts);
        }
        return shiftActivityResponseDTOS;
    }

    private void removeOppositeStatus(ShiftActivity shiftActivity, ShiftStatus shiftStatus) {
        switch (shiftStatus) {
            case LOCK:
                shiftActivity.getStatus().removeAll(Arrays.asList(UNLOCK,UNPUBLISH,REQUEST));
                shiftActivity.getStatus().add(LOCK);
                break;
            case FIX:
                shiftActivity.getStatus().removeAll(Arrays.asList(UNFIX,REQUEST));
                shiftActivity.getStatus().add(FIX);
                break;
            case UNFIX:
                shiftActivity.getStatus().removeAll(Arrays.asList(FIX,REQUEST));
                break;
            case APPROVE:
                shiftActivity.getStatus().removeAll(Arrays.asList(REJECT,UNPUBLISH,REQUEST));
                shiftActivity.getStatus().add(APPROVE);
                break;
            case REJECT:
            shiftActivity.getStatus().removeAll(Arrays.asList(APPROVE,REQUEST));
                break;
            case UNLOCK:
        shiftActivity.getStatus().removeAll(Arrays.asList(LOCK,REQUEST));
                break;
            case PUBLISH:
        shiftActivity.getStatus().removeAll(Arrays.asList(REQUEST,UNPUBLISH,REJECT));
                shiftActivity.getStatus().add(PUBLISH);
                break;
            case UNPUBLISH:
        shiftActivity.getStatus().removeAll(Arrays.asList(REQUEST,PUBLISH));
                break;
            case VALIDATE:
                shiftActivity.getStatus().add(VALIDATE);
                break;
        }
    }

    private ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate, ViewType viewType) {
        if (endLocalDate == null) {
            exceptionService.actionNotPermittedException("endDate.null");
        }
        Date startDate = asDate(startLocalDate);
        Date endDate = asDate(endLocalDate);
        List<ShiftDTO> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, startDate, endDate);
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessRolesOfStaff(unitId);
        List<OpenShift> openShifts = userAccessRoleDTO.getManagement() ? openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate, endDate) :
                openShiftNotificationMongoRepository.findValidOpenShiftsForStaff(userAccessRoleDTO.getStaffId(), startDate, endDate);
        ButtonConfig buttonConfig = null;

        if (Optional.ofNullable(viewType).isPresent() && viewType.toString().equalsIgnoreCase(ViewType.WEEKLY.toString())) {
            buttonConfig = findButtonConfig(assignedShifts, startDate, endDate, userAccessRoleDTO.getManagement());
        }
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
        return new ShiftWrapper(assignedShifts, openShiftResponseDTOS, staffAccessRoleDTO, buttonConfig);
    }

    public ButtonConfig findButtonConfig(List<ShiftDTO> shifts, Date startDate, Date endDate, boolean management) {
        ButtonConfig buttonConfig = new ButtonConfig();
        if (management) {
            if (!DateUtils.getLocalDateFromDate(startDate).getDayOfWeek().equals(DayOfWeek.MONDAY) ||
                    !DateUtils.getLocalDateFromDate(endDate).getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                exceptionService.invalidRequestException("message.weeklyview.incorrect.date");
            }
            Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
            List<ShiftState> shiftStates = shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds, AccessGroupRole.MANAGEMENT);
            Set<BigInteger> shiftStateIds = shiftStates.stream().map(shiftState -> shiftState.getShiftId()).collect(Collectors.toSet());
            for (BigInteger shiftId : shiftIds) {
                if (!shiftStateIds.contains(shiftId)) {
                    buttonConfig.setSendToPayrollEnabled(false);
                    break;
                }
                buttonConfig.setSendToPayrollEnabled(true);
            }
        }
        return buttonConfig;
    }

    private List<ShiftDTO> getShiftOfStaffByExpertiseId(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long expertiseId) {
        if (staffId == null || endDate == null || expertiseId == null) {
            exceptionService.actionNotPermittedException("staff_id.end_date.null");
        }
        Long unitPositionId = userIntegrationService.getUnitPositionId(unitId, staffId, expertiseId);
        return shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, asDate(startDate), asDate(endDate), unitId);
    }


    private ShiftWithActivityDTO buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(ShiftDTO shiftDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftWithActivityDTO.class);
        shiftDTO.getActivities().forEach(shiftActivityDTO ->
                shiftActivityDTO.setActivityName(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity().getName())
        );
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO ->
                shiftActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), ActivityDTO.class))
        );
        shiftWithActivityDTO.setStartDate(shiftDTO.getActivities().get(0).getStartDate());
        shiftWithActivityDTO.setEndDate(shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        shiftWithActivityDTO.setStatus(Arrays.asList(REQUEST));
        return shiftWithActivityDTO;
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
        return activityShiftStatusSettings != null && staffAccessGroupDTO != null && CollectionUtils.containsAny(activityShiftStatusSettings.getAccessGroupIds(), staffAccessGroupDTO.getAccessGroupIds());
    }

    public List<ShiftState> checkAndCreateRealtimeAndDraftState(List<Shift> shifts, List<ShiftState> shiftStates, Map<String, Phase> phaseMap) {
        List<ShiftState> newShiftStates = new ArrayList<>();
        newShiftStates = shiftStateService.createRealTimeShiftState(newShiftStates, shiftStates, shifts, phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        newShiftStates.addAll(shiftStateService.createDraftShiftState(newShiftStates, shifts, phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId()));
        if (!newShiftStates.isEmpty()) shiftStateMongoRepository.saveEntities(newShiftStates);
        return newShiftStates;
    }

    //TODO need to optimize this method
    public ShiftWithViolatedInfoDTO updateShiftByTandA(Long unitId, ShiftDTO shiftDTO,
                                                       String type, Boolean validatedByStaff) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        if (!userAccessRoleDTO.getStaff() && validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.save.access");
        } else if (!userAccessRoleDTO.getManagement() && !validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.save.access");
        }
        Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.REALTIME.toString());
        Map<String, Phase> phaseMap = new HashMap<String, Phase>() {{
            put(phase.getPhaseEnum().toString(), phase);
        }};
        if (shiftDTO.getShiftStatePhaseId().equals(phase.getId())) {
            validateRealTimeShift(unitId, shiftDTO, phaseMap);
        }
        if (isNull(shiftDTO.getShiftId())) {
            shiftDTO.setShiftId(shiftDTO.getId());
        }
        BigInteger shiftStateId = shiftDTO.getId();
        BigInteger shiftStatePhaseId = shiftDTO.getShiftStatePhaseId();
        shiftDTO.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = updateShift(shiftDTO, type, true);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
//            shiftDTO = shiftWithViolatedInfoDTO.getShifts().get(0);
            updateShiftStateAfterValidatingWtaRule(shiftDTO, shiftStateId, shiftStatePhaseId);
        }
        shiftWithViolatedInfoDTO.getShifts().get(0).setEditable(true);
        shiftWithViolatedInfoDTO.getShifts().get(0).setDurationMinutes((int) shiftWithViolatedInfoDTO.getShifts().get(0).getInterval().getMinutes());
        return shiftWithViolatedInfoDTO;
    }

    public ShiftDTO updateShiftStateAfterValidatingWtaRule(ShiftDTO shiftDTO, BigInteger shiftStateId, BigInteger shiftStatePhaseId) {
        shiftDTO.setShiftStatePhaseId(shiftStatePhaseId);
        ShiftState shiftState = shiftStateMongoRepository.findOne(shiftDTO.getId());
        if (shiftState != null) {
            shiftDTO.setId(shiftState.getId());
            shiftDTO.setAccessGroupRole(shiftState.getAccessGroupRole());
            shiftDTO.setValidated(shiftState.getValidated());
            shiftDTO.setShiftId(shiftState.getShiftId());
            shiftDTO.setStartDate(shiftState.getActivities().get(0).getStartDate());
            shiftDTO.setEndDate(shiftState.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
        }
        shiftState = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
        shiftMongoRepository.save(shiftState);
        return shiftDTO;
    }

    public ShiftWithViolatedInfoDTO validateShift(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId, String type) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();

        if (!userAccessRoleDTO.getStaff() && validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.validation.access");
        } else if (!userAccessRoleDTO.getManagement() && !validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.validation.access");
        }
        Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        ShiftState shiftState = null;
        ShiftDTO staffShiftDTO = null;
        //StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()),shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(),Collections.emptySet());
        if (!validatedByStaff) {
            shiftState = shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhaseAndRole(shiftDTO.getShiftId(), shiftDTO.getShiftStatePhaseId(), AccessGroupRole.STAFF);
            staffShiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        }
        shiftValidatorService.validateGracePeriod(shiftDTO, validatedByStaff, unitId, staffShiftDTO);
        BigInteger shiftStateId = shiftDTO.getId();
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = updateShift(shiftDTO, type, true);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            shiftDTO = validateShiftStateAfterValidatingWtaRule(shiftDTO, shiftState, validatedByStaff, actualPhases, shiftStateId);
        }
        shiftWithViolatedInfoDTO.setShifts(Arrays.asList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }

    public ShiftDTO validateShiftStateAfterValidatingWtaRule(ShiftDTO shiftDTO, ShiftState shiftState, Boolean validatedByStaff, Phase actualPhases, BigInteger shiftStateId) {
        shiftState = shiftStateMongoRepository.findOne(shiftStateId);
        if (shiftState == null) {
            Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
            if (shift != null) {
                shiftDTO.setId(null);
            }
        }
        if (shiftState != null) {
            shiftDTO.setId(shiftStateId);
            shiftDTO.setAccessGroupRole(shiftState.getAccessGroupRole());
            shiftDTO.setValidated(shiftState.getValidated());
            shiftDTO.setShiftStatePhaseId(shiftState.getShiftStatePhaseId());
            shiftDTO.setShiftId(shiftState.getShiftId());
        }
        shiftState = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
        List<BigInteger> activityIds = shiftState.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shiftState.setShiftId(shiftDTO.getShiftId());
        shiftState.setStartDate(shiftState.getActivities().get(0).getStartDate());
        shiftState.setEndDate(shiftState.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
        shiftState.setValidated(LocalDate.now());
        shiftState.setShiftStatePhaseId(actualPhases.getId());
        shiftState.getActivities().forEach(activity -> {
            activity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            activity.setActivityName(activityWrapperMap.get(activity.getActivityId()).getActivity().getName());
        });
        save(shiftState);
        if (validatedByStaff) {
            shiftState.setAccessGroupRole(AccessGroupRole.MANAGEMENT);
            shiftState.setShiftStatePhaseId(actualPhases.getId());
            shiftState.setId(null);
            shiftState.setValidated(null);
            shiftState.getActivities().forEach(activity -> activity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
            save(shiftState);
        }
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        if (validatedByStaff) {
            shiftDTO.setEditable(true);
        }
        shiftDTO.setDurationMinutes((int) shiftDTO.getInterval().getMinutes());

        return shiftDTO;
    }

    public void validateRealTimeShift(Long unitId, ShiftDTO shiftDTO, Map<String, Phase> phaseMap) {
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        ShiftState shiftState = shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhase(shiftDTO.getShiftId(), phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        Map<BigInteger, ShiftActivity> activityMap = shiftState.getActivities().stream().filter(distinctByKey(a -> a.getId())).collect(Collectors.toMap(k -> k.getId(), v -> v));
        boolean realtime = phaseService.shiftEdititableInRealtime(timeZone, phaseMap, shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        if (realtime) {
            shiftDTO.getActivities().forEach(shiftActivity -> {
                ShiftActivity shiftActivity1 = activityMap.get(shiftActivity.getId());
                if (shiftActivity1 != null &&
                        ((!shiftActivity.getStartDate().equals(shiftActivity1.getStartDate()) && shiftActivity.getStartDate().before(asDate(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone)))))
                                || (!shiftActivity.getEndDate().equals(shiftActivity1.getEndDate()) && shiftActivity.getEndDate().before(asDate(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone))))))) {
                    exceptionService.actionNotPermittedException("error.activity.startdate", shiftActivity.getActivityName());
                }
            });
        }
    }

    private ShiftDetailViewDTO getShiftDetailsOfStaff(String timeZone, Map<String, Phase> phaseMap, List<Shift> shifts, List<ShiftState> shiftStatesList) {
        List<ShiftDTO> plannedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        if (isCollectionEmpty(plannedShifts)) {
            plannedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
        }
        List<ShiftDTO> realTimeShift = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        List<ShiftDTO> shiftStateDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList, ShiftDTO.class);
        List<ShiftDTO> staffValidatedShifts = shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(AccessGroupRole.STAFF) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList());
        Map<String, ShiftDTO> staffAndShiftMap = staffValidatedShifts.stream().collect(Collectors.toMap(k -> k.getStaffId() + "" + k.getId(), v -> v));
        DateTimeInterval graceInterval;
        List<ShiftDTO> updateRealTime = new ArrayList<>();
        for (ShiftDTO shiftDTO : realTimeShift) {
            if (!Optional.ofNullable(staffAndShiftMap.get(shiftDTO.getStaffId() + "" + shiftDTO.getId())).isPresent() && shiftDTO.getValidated() == null && phaseService.shiftEdititableInRealtime(timeZone, phaseMap, shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate())) {
                shiftDTO.setEditable(true);
            }
            updateRealTime.add(shiftDTO);
        }
        if (!staffValidatedShifts.isEmpty()) {
            Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(staffValidatedShifts.get(0).getUnitId(), PhaseDefaultName.TIME_ATTENDANCE.toString());
            graceInterval = shiftValidatorService.getGracePeriodInterval(phase, staffValidatedShifts.get(0).getStartDate(), false);
            for (ShiftDTO staffValidatedShift : staffValidatedShifts) {
                if (staffValidatedShift.getValidated() == null && graceInterval.contains(staffValidatedShift.getStartDate())) {
                    staffValidatedShift.setEditable(true);
                }
            }
        }
        List<ShiftDTO> plannerValidatedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(AccessGroupRole.MANAGEMENT) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        //change id because id was same and issue on FE side and this is only for show FE side
        for (ShiftDTO shiftDTO : plannerValidatedShifts) {
            if (shiftDTO.getValidated() == null) {
                shiftDTO.setEditable(true);
            }
            // shiftDTO.setId(new BigInteger("" + shiftDTO.getStartDate().getTime()));
            //shiftDTO.getActivities().forEach(shiftActivity -> shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
        }
        return new ShiftDetailViewDTO(plannedShifts, updateRealTime, staffValidatedShifts, plannerValidatedShifts);
    }

    public ShiftWithActivityDTO convertIntoShiftWithActivity(Shift sourceShift, Map<BigInteger, ActivityWrapper> activityMap) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift, ShiftWithActivityDTO.class);
        shiftWithActivityDTO.getActivities().forEach(s -> {
            ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityMap.get(s.getActivityId()).getActivity(), ActivityDTO.class);
            s.setActivity(activityDTO);
        });
        return shiftWithActivityDTO;
    }

    public CompactViewDTO getDetailedAndCompactViewData(Long selectedStaffId, Long unitId, Date shiftStartDate) {
        List<Long> staffIds;
        if (isNull(selectedStaffId)) {
            List<StaffDTO> staffResponseDTOS = userIntegrationService.getStaffListByUnit();
            staffIds = staffResponseDTOS.stream().map(s -> s.getId()).collect(Collectors.toList());
        } else {
            staffIds = Arrays.asList(selectedStaffId);
        }
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<String, Phase> phaseMap = phases.stream().collect(Collectors.toMap(p -> p.getPhaseEnum().toString(), Function.identity()));
        Date endDate = asDate(DateUtils.asZoneDateTime(shiftStartDate).plusDays(1));
        List<TimeAndAttendanceDTO> timeAndAttendance = timeAndAttendanceRepository.findAllAttendanceByStaffIds(staffIds, unitId, asDate(DateUtils.asLocalDate(shiftStartDate).minusDays(1)), shiftStartDate);
        Map<Long, List<AttendanceTimeSlotDTO>> staffsTimeAndAttendance = (CollectionUtils.isNotEmpty(timeAndAttendance)) ? timeAndAttendance.stream().collect(Collectors.toMap(k -> k.getStaffId(), v -> v.getAttendanceTimeSlot())) : new HashMap<>();
        List<Shift> shifts = shiftMongoRepository.findShiftByStaffIdsAndDate(staffIds, shiftStartDate, endDate);
        shifts.forEach(shift -> shift.setDurationMinutes((int) shift.getInterval().getMinutes()));
        List<ShiftState> shiftStates = shiftStateMongoRepository.getAllByStaffsByIdsBetweenDate(staffIds, shiftStartDate, endDate);
        List<ShiftState> realTimeShiftStatesList = checkAndCreateRealtimeAndDraftState(shifts, shiftStates.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()), phaseMap);
        shiftStates.addAll(realTimeShiftStatesList);
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.TIME_TYPE.toString()));
        List<ReasonCodeDTO> reasonCodeDTOS = userIntegrationService.getReasonCodeDTOList(unitId, requestParam);
        Map<Long, List<Shift>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        Map<Long, List<ShiftState>> shiftStateMap = shiftStates.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        List<DetailViewDTO> shiftDetailViewDTOMap = staffIds.stream().map(staffId -> new DetailViewDTO(staffId, getShiftDetailsOfStaff(timeZone, phaseMap, shiftsMap.getOrDefault(staffId, new ArrayList<>()), shiftStateMap.getOrDefault(staffId, new ArrayList<>())), staffsTimeAndAttendance.getOrDefault(staffId, new ArrayList<>()))).collect(Collectors.toList());
        return new CompactViewDTO(shiftDetailViewDTOMap, reasonCodeDTOS);
    }

    /**
     * @param existingShiftActivities
     * @param arrivedShiftActivities
     * @return shifActivityDTO
     * @Auther PAVAN
     * @LastModifiedBy Pavan
     * @Desc used to filter the ShiftActivities for Add , Edit and Delete
     */
    private ShiftActivityIdsDTO getActivitiesToProcess(List<ShiftActivity> existingShiftActivities, List<ShiftActivityDTO> arrivedShiftActivities) {
        Set<BigInteger> allExistingShiftActivities = existingShiftActivities.stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet());
        Set<BigInteger> allArrivedShiftActivities = arrivedShiftActivities.stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet());
        Map<BigInteger, ShiftActivity> existingShiftActivityMap = existingShiftActivities.stream().collect(Collectors.toMap(ShiftActivity::getActivityId, Function.identity(), (previous, current) -> previous));
        Set<BigInteger> activitiesToEdit = new HashSet<>();
        Set<BigInteger> activitiesToAdd = new HashSet<>();
        Set<BigInteger> activitiesToDelete = new HashSet<>();
        for (ShiftActivityDTO shiftActivity : arrivedShiftActivities) {
            if (allExistingShiftActivities.contains(shiftActivity.getActivityId())) {
                ShiftActivity existingActivity = existingShiftActivityMap.get(shiftActivity.getActivityId());
                if (!shiftActivity.getStartDate().equals(existingActivity.getStartDate()) || !shiftActivity.getEndDate().equals(existingActivity.getEndDate())) {
                    activitiesToEdit.add(shiftActivity.getActivityId());
                }
            } else {
                activitiesToAdd.add(shiftActivity.getActivityId());
            }
        }
        for (BigInteger current : allExistingShiftActivities) {
            if (!allArrivedShiftActivities.contains(current)) {
                activitiesToDelete.add(current);
            }
        }
        return new ShiftActivityIdsDTO(activitiesToAdd, activitiesToEdit, activitiesToDelete);
    }

    private Map<BigInteger, PhaseTemplateValue> constructMapOfActivityAndPhaseTemplateValue(Phase phase, List<ActivityWrapper> activities) {
        Map<BigInteger, PhaseTemplateValue> phaseTemplateValueMap = new HashMap<>();
        for (ActivityWrapper activityWrapper : activities) {
            for (PhaseTemplateValue phaseTemplateValue : activityWrapper.getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues()) {
                if (phaseTemplateValue.getPhaseId().equals(phase.getId())) {
                    phaseTemplateValueMap.put(activityWrapper.getActivity().getId(), phaseTemplateValue);
                    break;
                }
            }
        }
        return phaseTemplateValueMap;
    }

    private List<ShiftActivityDTO> findShiftActivityToValidateStaffingLevel(List<ShiftActivity> existingShiftActivities, List<ShiftActivityDTO> arrivedShiftActivities) {
        List<ShiftActivityDTO> shiftActivities = new ArrayList<>();
        try {
            for (int i = 0; i < arrivedShiftActivities.size(); i++) {
                ShiftActivityDTO currentShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(arrivedShiftActivities.get(i), ShiftActivityDTO.class);
                ShiftActivityDTO existingShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(existingShiftActivities.get(i), ShiftActivityDTO.class);
                if (!currentShiftActivity.getActivityId().equals(existingShiftActivity.getActivityId())) {
                    currentShiftActivity.setStartDate(existingShiftActivity.getStartDate());
                    shiftActivities.add(existingShiftActivity);
                    shiftActivities.add(currentShiftActivity);
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            //Intentionally left blank to avoid ArrayIndexOutOfBoundsException
        }
        return shiftActivities;
    }

    private ActivityShiftStatusSettings getActivityShiftStatusSettingByStatus(PhaseTemplateValue phaseTemplateValue, ShiftStatus status) {
        ActivityShiftStatusSettings activityShiftStatusSettings = null;
        for (ActivityShiftStatusSettings statusSettings : phaseTemplateValue.getActivityShiftStatusSettings()) {
            if (status.equals(statusSettings.getShiftStatus())) {
                activityShiftStatusSettings = statusSettings;
                break;
            }
        }
        return activityShiftStatusSettings;
    }

    /**
     * @param unitId
     * @param staffId
     * @param startDate
     * @param endDate
     * @param unitPositionId
     * @param viewType
     * @param shiftFilterParam
     * @param expertiseId      * @Description this method will fetch all shifts / open shifts and shift states based on the above request param
     * @return shifts
     */
    public Object getAllShiftAndStates(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long unitPositionId, ViewType viewType,
                                       ShiftFilterParam shiftFilterParam, Long expertiseId) {
        Object object = null;
        if (endDate != null) {
            endDate = endDate.plusDays(1);
        }
        switch (shiftFilterParam) {
            case INDIVIDUAL_VIEW:
                object = getShiftByStaffId(unitId, staffId, startDate, endDate, unitPositionId);
                break;
            case OPEN_SHIFT:
                object = getAllShiftsOfSelectedDate(unitId, startDate, endDate, viewType);
                break;
            case EXPERTISE:
                object = getShiftOfStaffByExpertiseId(unitId, staffId, startDate, endDate, expertiseId);
                break;
            case SHIFT_STATE:
                object = getDetailedAndCompactViewData(staffId, unitId, asDate(startDate));
                break;
            default:
                exceptionService.actionNotPermittedException("please.select.valid.criteria");
        }
        return object;
    }
}