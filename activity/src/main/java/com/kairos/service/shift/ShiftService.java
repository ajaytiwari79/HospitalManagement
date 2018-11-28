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
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
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
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.enums.shift.ViewType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.template_types.BreakWTATemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelActivityRankRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.persistence.repository.unit_settings.TimeAttendanceGracePeriodRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.ShiftAllowedToDelete;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.attendence_setting.TimeAndAttendanceService;
import com.kairos.service.exception.ExceptionService;
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
import com.kairos.utils.user_context.UserContext;
import com.kairos.wrapper.DateWiseShiftResponse;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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

import static com.kairos.commons.utils.DateUtils.ONLY_DATE;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.phase.PhaseType.ACTUAL;
import static com.kairos.utils.ShiftValidatorService.getValidDays;
import static java.util.stream.Collectors.groupingBy;


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
    private TimeAndAttendanceRepository timeAndAttendanceRepository;

    @Inject
    private StaffRestClient staffRestClient;

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
    private GenericIntegrationService restClient;
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
    private ShiftBreakActivityService shiftBreakActivityService;
    @Inject
    private StaffingLevelActivityRankRepository staffingLevelActivityRankRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject private TimeAndAttendanceService timeAndAttendanceService;
    @Inject private ShiftReminderService shiftReminderService;
    @Inject private PhaseMongoRepository phaseMongoRepository;

    public ShiftWithViolatedInfoDTO createShift(Long unitId, ShiftDTO shiftDTO, String type, boolean byTandAPhase) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shiftDTO.getActivities().get(0).getActivityId());
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.invalidRequestException("message.activity.id", shiftDTO.getActivities().get(0).getActivityId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(shiftDTO.getShiftDate(), shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException("message.staff.notfound");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.actionNotPermittedException("message.unit.position");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.asDate(shiftDTO.getShiftDate()));
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.invalidRequestException("error.cta.notFound", DateUtils.asDate(shiftDTO.getShiftDate()));
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        if ((FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            shiftWithViolatedInfoDTO = createAbsenceTypeShift(activityWrapper, shiftDTO, staffAdditionalInfoDTO);
        } else {
            boolean shiftExists;
            if (!byTandAPhase) {
                shiftExists = shiftMongoRepository.existShiftsBetweenDurationByStaffUserId(staffAdditionalInfoDTO.getStaffUserId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate(),ShiftType.PRESENCE);
            } else {
                shiftExists = shiftMongoRepository.findShiftBetweenDurationByUnitPositionNotEqualToShiftId(shiftDTO.getShiftId(), staffAdditionalInfoDTO.getStaffUserId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate(),ShiftType.PRESENCE);
            }
            if (shiftExists) {
                exceptionService.invalidRequestException("message.shift.date.startandend", shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
            }
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate(),null);
            if(phase==null){
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            shiftDTO.setShiftType(ShiftType.PRESENCE);
            shiftWithViolatedInfoDTO = saveShift(activityWrapper, staffAdditionalInfoDTO, shiftDTO,phase, byTandAPhase);

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
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate(),null);
            if(phase==null){
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            shiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftWithViolatedInfoDTO = saveShift(activityWrapper, staffAdditionalInfoDTO, shiftDTO,phase, false);
        }
        else {
            Date shiftFromDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate();
            shiftWithViolatedInfoDTO = getAverageOfShiftByActivity(staffAdditionalInfoDTO, activityWrapper.getActivity(), activityWrapper.getTimeType(), shiftFromDate);
        }
        return shiftWithViolatedInfoDTO;
    }



    private ShiftWithViolatedInfoDTO saveShift(ActivityWrapper activityWrapper, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO,Phase phase, boolean byTandAPhase) {
        ShiftState shiftState=null;
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityWrapper.getActivity(), ActivityDTO.class);

        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shiftDTO.getUnitId(), DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()));
        Shift mainShift;
        if(byTandAPhase){
            shiftState=shiftStateMongoRepository.findOne(shiftDTO.getId());
            if(shiftState!=null){
                ObjectMapperUtils.copyProperties(shiftDTO,shiftState,"id","accessGroupRole","actualPhaseState","validated");
            }else{
                shiftState= ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
            }
            mainShift = shiftState;
        }else {
            mainShift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
        }
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.onlyDate(shiftDTO.getActivities().get(0).getStartDate()));
        List<BigInteger> activityIds = shiftDTO.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activityWrapperMap);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null,activityWrapperMap,false,byTandAPhase);
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> shiftActivityDTO.setPlannedTimeId(addPlannedTimeInShift(mainShift.getUnitId(), phase.getId(), activityWrapper.getActivity(), staffAdditionalInfoDTO)));
        mainShift.setPlanningPeriodId(planningPeriod.getId());
        mainShift.setPhaseId(planningPeriod.getCurrentPhaseId());
        validateStaffingLevel(phase, mainShift, activityWrapper.getActivity(), true, staffAdditionalInfoDTO);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            //mainShift.setPlanningPeriodId(shiftWithActivityDTO.getPlanningPeriodId());
            mainShift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            saveShiftWithActivity(wtaQueryResultDTO.getBreakRule(), activityIds, activityWrapperMap, mainShift, staffAdditionalInfoDTO,false,staffAdditionalInfoDTO.getTimeSlotSets());
            payOutService.savePayOut(staffAdditionalInfoDTO, mainShift, activityWrapperMap);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(mainShift, ShiftDTO.class);
            ShiftViolatedRules shiftViolatedRules = ObjectMapperUtils.copyPropertiesByMapper(shiftWithViolatedInfoDTO.getViolatedRules(), ShiftViolatedRules.class);
            shiftViolatedRules.setShift(mainShift);
            save(shiftViolatedRules);
            activityWrapperMap.put(activityWrapper.getActivity().getId(),activityWrapper);
            shiftReminderService.setReminderTrigger( activityWrapperMap,mainShift);

        }
        shiftWithViolatedInfoDTO.setShifts(Arrays.asList(shiftDTO));
        return shiftWithViolatedInfoDTO;
    }


    public void updateTimeBankAndPublishNotification(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        ActivityWrapper activityWrapper = activityWrapperMap.get(shift.getActivities().get(0).getActivityId());
        boolean presenceTypeShift = !(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        if (TimeTypes.WORKING_TYPE.toString().equals(activityWrapper.getTimeType())) {
            applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shift.getStartDate(), shift, false, null, presenceTypeShift));

        }
    }


    public void saveShiftWithActivity(BreakWTATemplate breakWTATemplate, List<BigInteger> activityIds, Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift,
                                      StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean updateShift,List<TimeSlotWrapper> timeSlot) {
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByStaffIdAndActivityIdInAndDeletedFalse(shift.getStaffId(), activityIds);

        int scheduledMinutes = 0;
        int durationMinutes = 0;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            if (shiftActivity.getId() == null) {
                shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            }
            ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
            if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
                Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, activityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
                if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                    timeBankCalculationService.calculateScheduleAndDurationHour(shiftActivity, activityWrapper.getActivity(), staffAdditionalInfoDTO.getUnitPosition());
                    scheduledMinutes += shiftActivity.getScheduledMinutes();
                    durationMinutes += shiftActivity.getDurationMinutes();
                }
            }
            shiftActivity.setBackgroundColor(activityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
            shiftActivity.setActivityName(activityWrapper.getActivity().getName());
            shiftActivity.setTimeType(activityWrapper.getTimeType());
        }

        shift.setStartDate(shift.getActivities().get(0).getStartDate());
        shift.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
        List<ShiftActivity> breakActivities = shiftBreakActivityService.addBreakInShifts(activityWrapperMap, shift, staffAdditionalInfoDTO.getUnitPosition(),breakWTATemplate,timeSlot);
        if (!breakActivities.isEmpty()) {
            shift.getActivities().remove(0);
            shift.getActivities().addAll(breakActivities);
        }
        shift.getActivities().sort(Comparator.comparing(ShiftActivity::getStartDate));
        shiftMongoRepository.save(shift);
        if(!updateShift) {
            updateTimeBankAndPublishNotification(activityWrapperMap, shift, staffAdditionalInfoDTO);
        }
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
                if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
                    Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                    Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, activityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
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
            shift.setCreatedBy(UserContext.getUserDetails().getId());
            shift.setPhaseId(phaseListByDate.get(shift.getActivities().get(0).getStartDate()).getId());
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
        Date shiftStartDate = DateUtils.onlyDate(shift.getActivities().get(0).getStartDate());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftStartDate), shift.getStaffId(), type, shift.getUnitPositionId());

        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftStartDate);
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.cta.notFound", ctaResponseDTO.getId());
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.onlyDate(shiftWithViolatedInfo.getShifts().get(0).getActivities().get(0).getStartDate()));
        saveShiftWithActivity(wtaQueryResultDTO.getBreakRule(), activityIds, activityWrapperMap, shift, staffAdditionalInfoDTO,false,null);
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


    private ShiftWithViolatedInfoDTO saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        Set<LocalDateTime> dates = shiftDTOS.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
        Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(shiftDTOS.get(0).getUnitId(), dates);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTOS.get(0).getActivities().get(0).getStartDate());
        List<BigInteger> activityIds = Arrays.asList(activity.getId());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activityWrapperMap);
            shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> shiftActivityDTO.setPlannedTimeId(addPlannedTimeInShift(staffAdditionalInfoDTO.getUnitId(), phaseListByDate.get(shiftDTO.getActivities().get(0).getStartDate()).getId(), activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), staffAdditionalInfoDTO)));
            ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phaseListByDate.get(shiftDTO.getActivities().get(0).getStartDate()), wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null,activityWrapperMap,false,false);
            Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, Shift.class);
            shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            shifts.add(shift);
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getActivities());
            shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());
        }
        if(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            saveShiftWithActivity(phaseListByDate, shifts, staffAdditionalInfoDTO);
        }
        shiftDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
        shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
        return shiftWithViolatedInfoDTO;
    }


    public ShiftWithViolatedInfoDTO updateShift(ShiftDTO shiftDTO, String type) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()), shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());

        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), shiftDTO.getActivities().get(0).getStartDate(),shiftDTO.getActivities().get(shiftDTO.getActivities().size()-1).getEndDate());
        if (phase == null) {
            exceptionService.actionNotPermittedException("message.shift.planning.period.exits", shiftDTO.getActivities().get(0).getStartDate());
        }
        Set<BigInteger> activityIdsSet=ArrayUtil.getUnionOfList(shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()),shiftDTO.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        List<BigInteger> activityIds = new ArrayList<>(activityIdsSet);
        shiftValidatorService.validateStatusOfShiftOnUpdate(shift, shiftDTO);
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        Activity firstActivity = activityWrapperMap.get(shiftDTO.getActivities().get(0).getActivityId()).getActivity();
        if(!(FULL_WEEK.equals(firstActivity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(firstActivity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))) {
            boolean shiftExists = shiftMongoRepository.findShiftBetweenDurationByUnitPositionNotEqualToShiftId(shiftDTO.getId(), staffAdditionalInfoDTO.getStaffUserId(), shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate(),ShiftType.PRESENCE);
            if (shiftExists) {
                exceptionService.invalidRequestException("message.shift.date.startandend", shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
            }
        }
        List<Activity> activityList=activities.stream().map(ActivityWrapper::getActivity).collect(Collectors.toList());
        organizationActivityService.verifyBreakAllowedOfActivities(activities.get(0).getActivity().getRulesActivityTab().isBreakAllowed(),activityList);
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        ShiftActivityIdsDTO shiftActivityIdsDTO = getActivitiesToProcess(shift.getActivities(), shiftDTO.getActivities());
        // Validating Shift to eligibility
        Map<BigInteger, PhaseTemplateValue> activityPerPhaseMap = constructMapOfActivityAndPhaseTemplateValue(phase, activities);

        List<ShiftActivity> shiftActivities = findShiftActivityToValidateStaffingLevel(shift.getActivities(), shiftDTO.getActivities());
        shiftValidatorService.verifyShiftActivities(staffAdditionalInfoDTO.getRoles(), staffAdditionalInfoDTO.getUnitPosition().getEmploymentType().getId(), activityPerPhaseMap, shiftActivityIdsDTO);
        shiftValidatorService.verifyRankAndStaffingLevel(shiftActivities, shiftDTO.getUnitId(), activities,phase,staffAdditionalInfoDTO.getUserAccessRoleDTO());


        // End Here

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
        BigInteger activityId = shift.getActivities().get(0).getActivityId();
        Activity activityOld = activities.stream().filter(k -> k.getActivity().getId().equals(activityId)).findFirst().get().getActivity();
        //Activity activityOld = activityRepository.findActivityByIdAndEnabled(shift.getActivities().get(0).getActivityId());
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shiftDTO.getActivities().get(0).getStartDate());
        //copy old state of activity object
        Shift oldStateOfShift = ObjectMapperUtils.copyPropertiesByMapper(shift, Shift.class);
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());

        ShiftWithActivityDTO shiftWithActivityDTO = buildResponse(shiftDTO, activityWrapperMap);
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> shiftActivityDTO.setPlannedTimeId(addPlannedTimeInShift(staffAdditionalInfoDTO.getUnitId(), phase.getId(), activityWrapper.getActivity(), staffAdditionalInfoDTO)));
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO,shift,activityWrapperMap, true,false);
        shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            shift.setUpdatedBy(UserContext.getUserDetails().getId());
            shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            saveShiftWithActivity(wtaQueryResultDTO.getBreakRule(), activityIds, activityWrapperMap, shift, staffAdditionalInfoDTO,true,staffAdditionalInfoDTO.getTimeSlotSets());
            payOutService.updatePayOut(staffAdditionalInfoDTO, shift, activityWrapperMap);
            timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
            shiftReminderService.updateReminderTrigger(activityWrapperMap,shift);
            shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
            Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
            //anil m2 notify event for updating staffing level
            boolean presenceTypeShift = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
            if (activityWrapper.getTimeType().equals(TimeTypes.WORKING_TYPE.toString())) {
                applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift,
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
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitPositionAndFindFunctionsAfterDate(DateUtils.asLocalDate(startDate), staffId, unitPositionId);
            if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.staff.belongs", staffId, type);
            }
            if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
                exceptionService.actionNotPermittedException("message.unit.position",startDate.toString());
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

    public void updateShiftDailyTimeBankAndPaidOut(List<Shift> shifts, List<Shift> shiftsList, Long unitId){
        if (!Optional.ofNullable(shifts).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.ids");
        }
        List<Long> staffIds=shifts.stream().map(shift -> shift.getStaffId()).collect(Collectors.toList());
        List<Long> unitPositionIds=shifts.stream().map(shift -> shift.getUnitPositionId()).collect(Collectors.toList());
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds",staffIds.toString()));
        requestParam.add(new BasicNameValuePair("unitPositionIds",unitPositionIds.toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = genericIntegrationService.getStaffAditionalDTOS(unitId,requestParam);
        Set<BigInteger> activityIds = shifts.stream().flatMap(s->s.getActivities().stream().map(activity -> activity.getActivityId())).collect(Collectors.toSet());
          List<BigInteger> activityIdsList=new ArrayList<>(activityIds);
          List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIdsList);
          Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shifts.sort((shift, shiftSecond) -> shift.getStartDate().compareTo(shiftSecond.getStartDate()));
        shiftsList.sort((shift, shiftSecond) -> shift.getStartDate().compareTo(shiftSecond.getStartDate()));
        Date startDate=shifts.get(0).getStartDate();
        Date endDate=shifts.get(shifts.size()-1).getEndDate();
        Date shiftStartDate=shiftsList.get(0).getStartDate();
        Date shiftEndDate =shiftsList.get(shiftsList.size()-1).getEndDate();
        startDate=startDate.before(shiftStartDate)?startDate:shiftStartDate;
        endDate=endDate.after(shiftEndDate)?endDate:shiftEndDate;
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdsAndDate(unitPositionIds, startDate,endDate);
        Map<Long,List<CTAResponseDTO>> unitPositionAndCTAResponseMap=ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getUnitPositionId));
        staffAdditionalInfoDTOS.stream().forEach(staffAdditionalInfoDTO -> {
            if(unitPositionAndCTAResponseMap.get(staffAdditionalInfoDTO.getUnitPosition().getId())!=null){
                List<CTAResponseDTO> ctaResponseDTOSList=unitPositionAndCTAResponseMap.get(staffAdditionalInfoDTO.getUnitPosition().getId());
                List<CTARuleTemplateDTO> ctaRuleTemplateDTOS=ctaResponseDTOSList.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
                staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaRuleTemplateDTOS);
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            }
        });
        timeBankService.saveTimeBanksAndPayOut(staffAdditionalInfoDTOS, shifts,activityWrapperMap,startDate,endDate);

    }


    public ShiftDTO deleteShift(BigInteger shiftId) {
        ShiftDTO shiftDTO=new ShiftDTO();
        Shift shift = shiftMongoRepository.findOne(shiftId);
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftId);
        }
        shiftValidatorService.validateStatusOfShiftOnDelete(shift);
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getUnitPositionId());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(),  shift.getActivities().get(0).getStartDate(),null);
        validateStaffingLevel(phase, shift, activityWrapper.getActivity(), false, staffAdditionalInfoDTO);
        Specification<BigInteger> shiftAllowedToDelete = new ShiftAllowedToDelete(activityWrapper.getActivity().getPhaseSettingsActivityTab().getPhaseTemplateValues(), staffAdditionalInfoDTO.getUserAccessRoleDTO());
        Specification<BigInteger> activitySpecification = shiftAllowedToDelete;
        List<String> messages = activitySpecification.isSatisfiedString(phase.getId());
        if (!messages.isEmpty()) {
            exceptionService.actionNotPermittedException(messages.get(0));
        }
        shift.setDeleted(true);
        List<BigInteger> activityIds = shift.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getStartDate());
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        Long functionId = null;
        List<Shift> shifts=shiftMongoRepository.findShiftBetweenDurationBystaffId(shift.getStaffId(), DateUtils.getStartOfDay(shift.getStartDate()), DateUtils.getEndOfDay(shift.getEndDate()));
        if (shifts.size()==1 && CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions()) && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)
                && !activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
            functionId = genericIntegrationService.removeFunctionFromUnitPositionByDate(shift.getUnitId(), shift.getUnitPositionId(), shift.getStartDate());
            shiftDTO.setFunctionDeleted(true);
            shift.setFunctionId(functionId);

        }
        shiftMongoRepository.save(shift);
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.deletePayOut(shift.getId());

        boolean isShiftForPreence = !(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        applicationContext.publishEvent(new ShiftNotificationEvent(shift.getUnitId(), DateUtils.onlyDate(shift.getStartDate()), shift,
                false, null, isShiftForPreence, true, false, false));
        List<BigInteger> jobIds=shift.getActivities().stream().map(ShiftActivity::getId).collect(Collectors.toList());
        shiftReminderService.deleteReminderTrigger(jobIds,shift.getUnitId());
        return shiftDTO;

    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }


    private void updateWTACounter(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftWithViolatedInfoDTO shiftWithViolatedInfo, Shift shift) {
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), DateUtils.asLocalDate(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff());
        Map<String, StaffWTACounter> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(k -> k.getRuleTemplateName(), v -> v));
        List<StaffWTACounter> updatedStaffCounters = new ArrayList<>();
        shiftWithViolatedInfo.getViolatedRules().getWorkTimeAgreements().forEach(workTimeAgreementRuleViolation -> {
            int count = workTimeAgreementRuleViolation.getCounter() - 1;
            StaffWTACounter staffWTACounter = staffWTACounterMap.getOrDefault(workTimeAgreementRuleViolation.getName(), new StaffWTACounter(planningPeriod.getStartDate(), planningPeriod.getEndDate(), workTimeAgreementRuleViolation.getRuleTemplateId(), workTimeAgreementRuleViolation.getName(),staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitId()));
            staffWTACounter.setCount(count);
            updatedStaffCounters.add(staffWTACounter);
        });
        if (!updatedStaffCounters.isEmpty()) {
            save(updatedStaffCounters);
        }
    }

    private void validateStaffingLevel(Phase phase, Shift shift, Activity activity, boolean checkOverStaffing, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Date shiftStartDate = shift.getActivities().get(0).getStartDate();
        Date shiftEndDate = shift.getActivities().get(shift.getActivities().size() - 1).getEndDate();
        if (activity.getRulesActivityTab().isEligibleForStaffingLevel()) {
            PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
            if (!Optional.ofNullable(phaseSettings).isPresent()) {
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            if (!phaseSettings.isManagementEligibleForOverStaffing() || !phaseSettings.isManagementEligibleForUnderStaffing() || !phaseSettings.isStaffEligibleForOverStaffing() || !phaseSettings.isStaffEligibleForUnderStaffing()) {
                Date startDate1 = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftStartDate).truncatedTo(ChronoUnit.DAYS));
                Date endDate1 = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftEndDate).truncatedTo(ChronoUnit.DAYS));
                List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(shift.getUnitId(), startDate1, endDate1);
                if (!Optional.ofNullable(staffingLevels).isPresent() || staffingLevels.isEmpty()) {
                    exceptionService.actionNotPermittedException("message.staffingLevel.absent");
                }
                List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(shiftStartDate, shiftEndDate, shift.getUnitId());
                for (StaffingLevel staffingLevel : staffingLevels) {
                    List<StaffingLevelInterval> staffingLevelIntervals = (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) ||
                            activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) ? staffingLevel.getAbsenceStaffingLevelInterval() : staffingLevel.getPresenceStaffingLevelInterval();
                    for (StaffingLevelInterval staffingLevelInterval : staffingLevelIntervals) {
                        int shiftsCount = 0;
                        boolean checkForUnderStaffing = false;
                        Optional<StaffingLevelActivity> staffingLevelActivity = staffingLevelInterval.getStaffingLevelActivities().stream().filter(sa -> sa.getActivityId().equals(activity.getId())).findFirst();
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

    private ShiftWithViolatedInfoDTO getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, String timeType, Date fromDate) {
        Date endDate = new DateTime(fromDate).withTimeAtStartOfDay().plusDays(8).toDate();
        Date startDate = new DateTime(fromDate).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).toDate();
        List<ShiftDTO> shiftQueryResultsInInterval = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        Date shiftDate = fromDate;
        for (int day = 0; day < 7; day++) {
            ShiftDTO shiftDTO = calculateAverageShiftByActivity(shiftQueryResultsInInterval, activity, staffAdditionalInfoDTO, shiftDate);
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
        Set<BigInteger> shiftActivitiyIds = ((Set<BigInteger>) objects[1]);
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc((List<BigInteger>) objects[0]);
        List<ShiftActivityResponseDTO> shiftActivityResponseDTOS = new ArrayList<>(shifts.size());
        Set<BigInteger> allActivities = shiftPublishDTO.getShifts().stream().flatMap(s -> s.getActivityIds().stream().map(a -> a)).collect(Collectors.toSet());
        List<Activity> activities = activityRepository.findAllPhaseSettingsByActivityIds(allActivities);
        Map<BigInteger, PhaseSettingsActivityTab> activityPhaseSettingMap = activities.stream().collect(Collectors.toMap(Activity::getId, Activity::getPhaseSettingsActivityTab));
        if (!shifts.isEmpty() && objects[1] != null) {
            Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseListByDate = phaseService.getPhasesByDates(unitId, dates);
            StaffAccessGroupDTO staffAccessGroupDTO = genericIntegrationService.getStaffAccessGroupDTO(unitId);
            for (Shift shift : shifts) {
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    if (shiftActivitiyIds.contains(shiftActivity.getId())) {
                        Phase phase = phaseListByDate.get(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()));
                        PhaseSettingsActivityTab phaseSettingsActivityTab = activityPhaseSettingMap.get(shiftActivity.getActivityId());
                        PhaseTemplateValue phaseTemplateValue = phaseSettingsActivityTab.getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
                        ActivityShiftStatusSettings activityShiftStatusSettings = phaseTemplateValue.getActivityShiftStatusSettings().stream().filter(a -> a.getShiftStatus().equals(shiftPublishDTO.getStatus())).findAny().get();
                        boolean validAccessGroup = validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
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


    public ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, Date startDate, Date endDate,ViewType viewType) {
        List<ShiftDTO> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, startDate, endDate);
        UserAccessRoleDTO userAccessRoleDTO = genericIntegrationService.getAccessRolesOfStaff(unitId);
        List<OpenShift> openShifts = userAccessRoleDTO.getManagement() ? openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate, endDate) :
                openShiftNotificationMongoRepository.findValidOpenShiftsForStaff(userAccessRoleDTO.getStaffId(), startDate, endDate);
        ButtonConfig buttonConfig = null;

        if(Optional.ofNullable(viewType).isPresent()&&viewType.toString().equalsIgnoreCase(ViewType.WEEKLY.toString())) {
            buttonConfig = findButtonConfig(assignedShifts,startDate,endDate,userAccessRoleDTO.getManagement());
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
        return new ShiftWrapper(assignedShifts, openShiftResponseDTOS, staffAccessRoleDTO,buttonConfig);
    }

    public ButtonConfig findButtonConfig(List<ShiftDTO> shifts,Date startDate,Date endDate,boolean management) {
        if(!DateUtils.getLocalDateFromDate(startDate).getDayOfWeek().equals(DayOfWeek.MONDAY)||
                !DateUtils.getLocalDateFromDate(endDate).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            exceptionService.invalidRequestException("message.weeklyview.incorrect.date");
        }
        ButtonConfig buttonConfig = new ButtonConfig();
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
        if(management){
            List<ShiftState> shiftStates = shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT);
            Set<BigInteger> shiftStateIds = shiftStates.stream().map(shiftState -> shiftState.getShiftId()).collect(Collectors.toSet());
            for(BigInteger shiftId:shiftIds){
                if(!shiftStateIds.contains(shiftId))  {
                    buttonConfig.setSendToPayrollEnabled(false);
                    break;
                }
                buttonConfig.setSendToPayrollEnabled(true);
            }
        }

        return buttonConfig;
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

        //
        List<Long> expertiseIds=staffDataList.stream().map(staffUnitPositionDetails -> staffUnitPositionDetails.getExpertise().getId()).collect(Collectors.toList());

        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdInOrderByCreatedAtAsc(expertiseIds);
        Map<BigInteger, ActivityWrapper> breakActivitiesMap = shiftBreakActivityService.getBreakActivities(breakSettings,unitId);
        Integer unCopiedShiftCount = 0;
        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();

        for (Long currentStaffId : copyShiftDTO.getStaffIds()) {

            StaffUnitPositionDetails staffUnitPosition = staffDataList.parallelStream().filter(unitPosition -> unitPosition.getStaff().getId().equals(currentStaffId)).findFirst().get();
            boolean paid = staffUnitPosition.getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID);
            // TODO PAVAN handle error
            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffUnitPosition, activityMap, copyShiftDTO);
            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("error"));
            unCopiedShiftCount += response.get("error").size();
            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);
        }
        copyShiftResponse.setUnCopiedShiftCount(unCopiedShiftCount);

        return copyShiftResponse;
    }

    private Map<String, List<ShiftResponse>> copyForThisStaff(List<DateWiseShiftResponse> shifts, StaffUnitPositionDetails staffUnitPosition, Map<BigInteger, Activity> activityMap, CopyShiftDTO copyShiftDTO) {
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
            List<ShiftActivity> shiftActivities = shiftBreakActivityService.addBreakInShifts(activityWrapperMap, copiedShift, staffUnitPosition,null,null);
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
        return shifts;
    }



    private ShiftWithActivityDTO buildResponse(ShiftDTO shiftDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftWithActivityDTO.class);
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO->
            shiftActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(),ActivityDTO.class))
        );
        shiftWithActivityDTO.setStartDate(shiftDTO.getActivities().get(0).getStartDate());
        shiftWithActivityDTO.setEndDate(shiftDTO.getActivities().get(0).getEndDate());
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


    public CompactViewDTO getDetailViewInfo(Long unitId, Long staffId, Date shiftStartDate) {
        String timeZone=genericIntegrationService.getTimeZoneByUnitId(unitId);
        List<Phase> actualPhases = phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalse(unitId, ACTUAL.toString());
        Map<String, Phase> phaseMap = actualPhases.stream().collect(Collectors.toMap(p->p.getPhaseEnum().toString(), Function.identity()));
        Date endDate = DateUtils.asDate(DateUtils.asZoneDateTime(shiftStartDate).plusDays(1));
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationBystaffId(staffId, shiftStartDate, endDate);
        shifts.forEach(shift -> shift.setDurationMinutes((int) shift.getInterval().getMinutes()));
        List<ShiftState> shiftStatesList=shiftStateMongoRepository.getAllByStaffsByIdsBetweenDate(shifts.stream().map(shift -> shift.getStaffId()).collect(Collectors.toList()),shiftStartDate,endDate);
        List<ShiftState> realTimeShiftStatesList=checkAndCreateRealtimeState(shifts,shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()),phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        shiftStatesList.addAll(realTimeShiftStatesList);
        shiftStatesList.forEach(shiftState -> shiftState.setDurationMinutes((int) shiftState.getInterval().getMinutes()));
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.ABSENCE.toString()));
        List<ReasonCodeDTO> reasonCodeDTOS = genericIntegrationService.getReasonCodeDTOList(unitId, requestParam);
        ShiftDetailViewDTO shiftDetailViewDTO = getShiftDetailsOfStaff(timeZone,phaseMap,timeAttendanceGracePeriod, shifts, shiftStatesList);
        List<TimeAndAttendanceDTO> timeAndAttendance=timeAndAttendanceRepository.findAllAttendanceByStaffIds(Arrays.asList(staffId),unitId,DateUtils.asDate(DateUtils.asLocalDate(shiftStartDate).minusDays(1)),shiftStartDate);
        return new CompactViewDTO(Arrays.asList(new DetailViewDTO(staffId,shiftDetailViewDTO,(Optional.ofNullable(timeAndAttendance).isPresent())?timeAndAttendance.get(0).getAttendanceTimeSlot():new ArrayList<>())),reasonCodeDTOS);
    }


    public List<ShiftState> checkAndCreateRealtimeState(List<Shift> shifts,List<ShiftState> shiftStates,BigInteger phaseId){
        List<ShiftState> newShiftStates=new ArrayList<>();
        newShiftStates=timeAndAttendanceService.createRealTimeShiftState(newShiftStates,shiftStates,shifts,phaseId);
       if(!newShiftStates.isEmpty()) shiftStateMongoRepository.saveEntities(newShiftStates);
        return newShiftStates;

    }

    public ShiftWithViolatedInfoDTO updateShift(Long unitId, ShiftDTO shiftDTO,
                                                String type) {
        Phase phase=phaseMongoRepository.findByUnitIdAndName(unitId,PhaseDefaultName.REALTIME.toString());
        Map<String,Phase> phaseMap=new HashMap<>();
        phaseMap.put(phase.getPhaseEnum().toString(),phase);
        if(shiftDTO.getShiftStatePhaseId().equals(phase.getId())) {
            validateRealTimeShift(unitId,shiftDTO,phaseMap);
        }
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        DateTimeInterval graceInterval = shiftValidatorService.getGracePeriodInterval(timeAttendanceGracePeriod, DateUtils.getDate(), true);
        if (!graceInterval.contains(shiftDTO.getActivities().get(0).getStartDate())) {
            exceptionService.invalidRequestException("message.shift.cannot.update");
        }
        if (shiftDTO.getShiftId() == null) {
            shiftDTO.setShiftId(shiftDTO.getId());
        }
        shiftDTO.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = createShift(unitId, shiftDTO, type, true);
        shiftWithViolatedInfoDTO.getShifts().get(0).setEditable(true);
        shiftWithViolatedInfoDTO.getShifts().get(0).setDurationMinutes((int) shiftWithViolatedInfoDTO.getShifts().get(0).getInterval().getMinutes());
        return shiftWithViolatedInfoDTO;
    }


    public ShiftDTO validateShift(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId,String type) {
        Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        ShiftState shiftState;
        ShiftDTO staffShiftDTO=null;
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()),shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        if(!validatedByStaff){
            shiftState=shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhaseAndRole(shiftDTO.getShiftId(),shiftDTO.getShiftStatePhaseId(),AccessGroupRole.STAFF);
            staffShiftDTO=ObjectMapperUtils.copyPropertiesByMapper(shiftState,ShiftDTO.class);
        }
        shiftValidatorService.validateGracePeriod(shiftDTO, validatedByStaff, unitId,staffShiftDTO);
        if (!staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff() && validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.validation.access");
        } else if (!staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement() && !validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.validation.access");
        }
        createShift(unitId, shiftDTO, type, true);
        shiftState= shiftStateMongoRepository.findOne(shiftDTO.getId());
        if (shiftState == null) {
            Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
            if (shift != null) {
                shiftDTO.setId(null);
            }
        }
        if(shiftState!=null){
            ObjectMapperUtils.copyProperties(shiftDTO,shiftState,"id","accessGroupRole","shiftStatePhaseId");
        }else {
            shiftState = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
        }
        List<BigInteger> activityIds = shiftState.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shiftState.setShiftId(shiftDTO.getShiftId());
        shiftState.setStartDate(shiftState.getActivities().get(0).getStartDate());
        shiftState.setEndDate(shiftState.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
        shiftState.setValidated(LocalDate.now());
        shiftState.setShiftStatePhaseId(actualPhases.getId());
        shiftState.getActivities().forEach(a -> {
            a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            a.setActivityName(activityWrapperMap.get(a.getActivityId()).getActivity().getName());
        });
        save(shiftState);
        if (validatedByStaff) {
            shiftState.setAccessGroupRole(AccessGroupRole.MANAGEMENT);
            shiftState.setShiftStatePhaseId(actualPhases.getId());
            shiftState.setId(null);
            shiftState.setValidated(null);
            shiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
            save(shiftState);
        }
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        if (validatedByStaff) {
            shiftDTO.setEditable(true);
        }
        shiftDTO.setDurationMinutes((int) shiftDTO.getInterval().getMinutes());
        return shiftDTO;
    }

    private void validateRealTimeShift(Long unitId,ShiftDTO shiftDTO,Map<String,Phase> phaseMap){
        String timeZone=genericIntegrationService.getTimeZoneByUnitId(unitId);
        ShiftState shiftState = shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhase(shiftDTO.getShiftId(), phaseMap.get(PhaseDefaultName.REALTIME).getId());
        Map<BigInteger,ShiftActivity> activityMap=shiftState.getActivities().stream().collect(Collectors.toMap(k->k.getActivityId(),v->v));
        boolean realtime=phaseService.isShiftIdEditInRealtime(timeZone,phaseMap,shiftDTO.getActivities().get(0).getStartDate(),shiftDTO.getActivities().get(shiftDTO.getActivities().size()-1).getEndDate());
        if(realtime){
            shiftDTO.getActivities().forEach(shiftActivity -> {
                ShiftActivity shiftActivity1=activityMap.get(shiftActivity.getActivityId());
                if(shiftActivity1!=null&&!shiftActivity.getStartDate().equals(shiftActivity1.getStartDate())&&shiftActivity.getStartDate().before(DateUtils.asDate(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone))))){
                    exceptionService.actionNotPermittedException("error.activity.startdate",shiftActivity.getActivityName());
                }
            });
        }
    }
    private ShiftDetailViewDTO getShiftDetailsOfStaff(String timeZone,Map<String, Phase> phaseMap,TimeAttendanceGracePeriod timeAttendanceGracePeriod, List<Shift> shifts, List<ShiftState> shiftStatesList) {
        List<ShiftDTO> plannedShifts = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
        List<ShiftDTO> realTimeShift = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()),ShiftDTO.class);
        List<ShiftDTO> shiftStateDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStatesList, ShiftDTO.class);
        List<ShiftDTO> staffValidatedShifts = shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole()!=null&&s.getAccessGroupRole().equals(AccessGroupRole.STAFF)&& s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList());
        Map<Long ,ShiftDTO> staffAndShiftMap=staffValidatedShifts.stream().collect(Collectors.toMap(k->k.getStaffId(),v->v));
        DateTimeInterval graceInterval = shiftValidatorService.getGracePeriodInterval(timeAttendanceGracePeriod, DateUtils.getDate(), true);
        List<ShiftDTO> updateRealTime = new ArrayList<>();
        for (ShiftDTO shiftDTO : realTimeShift) {
            if (!Optional.ofNullable(staffAndShiftMap.get(shiftDTO.getStaffId())).isPresent()&&shiftDTO.getValidated() == null&&phaseService.isShiftIdEditInRealtime(timeZone,phaseMap,shiftDTO.getActivities().get(0).getStartDate(),shiftDTO.getActivities().get(shiftDTO.getActivities().size()-1).getEndDate())) {
                    shiftDTO.setEditable(true);
            }
            updateRealTime.add(shiftDTO);
        }
        if (!staffValidatedShifts.isEmpty()) {
            graceInterval = shiftValidatorService.getGracePeriodInterval(timeAttendanceGracePeriod, staffValidatedShifts.get(0).getStartDate(), false);
            for (ShiftDTO staffValidatedShift : staffValidatedShifts) {
                if (staffValidatedShift.getValidated() == null&&graceInterval.contains(staffValidatedShift.getStartDate())) {
                        staffValidatedShift.setEditable(true);
                    }
                }
            }
        List<ShiftDTO> plannerValidatedShifts =ObjectMapperUtils.copyPropertiesOfListByMapper(shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole()!=null&&s.getAccessGroupRole().equals(AccessGroupRole.MANAGEMENT)&&s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList()),ShiftDTO.class);
        //change id becouse id was same and issue on FE side and this is only for show FE side
        for(ShiftDTO shiftDTO:plannerValidatedShifts){
            if (shiftDTO.getValidated() == null) {
                shiftDTO.setEditable(true);
            }
            // shiftDTO.setId(new BigInteger("" + shiftDTO.getStartDate().getTime()));
            //shiftDTO.getActivities().forEach(shiftActivity -> shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
        }
        return new ShiftDetailViewDTO(plannedShifts, updateRealTime, staffValidatedShifts, plannerValidatedShifts);
    }


    public CompactViewDTO getCompactViewDetails(Long unitId, Date shiftStartDate) {
        String timeZone=genericIntegrationService.getTimeZoneByUnitId(unitId);
        List<Phase> actualPhases = phaseMongoRepository.findByOrganizationIdAndPhaseTypeAndDeletedFalse(unitId, ACTUAL.toString());
        Map<String, Phase> phaseMap = actualPhases.stream().collect(Collectors.toMap(p->p.getPhaseEnum().toString(), Function.identity()));
        Date endDate = DateUtils.asDate(DateUtils.asZoneDateTime(shiftStartDate).plusDays(1));
        List<StaffDTO> staffResponseDTOS = genericIntegrationService.getStaffListByUnit();
        List<Long> staffIds = staffResponseDTOS.stream().map(s -> s.getId()).collect(Collectors.toList());
        List<TimeAndAttendanceDTO> timeAndAttendance=timeAndAttendanceRepository.findAllAttendanceByStaffIds(staffIds,unitId,DateUtils.asDate(DateUtils.asLocalDate(shiftStartDate).minusDays(1)),shiftStartDate);
        Map<Long,List<AttendanceTimeSlotDTO>> staffsTimeAndAttendance=timeAndAttendance.stream().collect(Collectors.toMap(k->k.getStaffId(), v->v.getAttendanceTimeSlot()));
        List<Shift> shifts = shiftMongoRepository.findShiftByStaffIdsAndDate(staffIds, shiftStartDate, endDate);
        List<ShiftState> shiftStates = shiftStateMongoRepository.getAllByStaffsByIdsBetweenDate(staffIds, shiftStartDate, endDate);
        List<ShiftState> realTimeShiftStatesList=checkAndCreateRealtimeState(shifts,shiftStates.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()),phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        shiftStates.addAll(realTimeShiftStatesList);
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", ReasonCodeType.ABSENCE.toString()));
        List<ReasonCodeDTO> reasonCodeDTOS = genericIntegrationService.getReasonCodeDTOList(unitId, requestParam);
        Map<Long, List<Shift>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        Map<Long, List<ShiftState>> shiftStateMap = shiftStates.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        List<DetailViewDTO> shiftDetailViewDTOMap =staffIds.stream().map(staffId->new DetailViewDTO(staffId,getShiftDetailsOfStaff(timeZone,phaseMap,timeAttendanceGracePeriod, shiftsMap.getOrDefault(staffId, new ArrayList<>()), shiftStateMap.getOrDefault(staffId, new ArrayList<>())),staffsTimeAndAttendance.get(staffId))).collect(Collectors.toList());
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
    private ShiftActivityIdsDTO getActivitiesToProcess(List<ShiftActivity> existingShiftActivities, List<ShiftActivity> arrivedShiftActivities) {
        Set<BigInteger> allExistingShiftActivities = existingShiftActivities.stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet());
        Set<BigInteger> allArrivedShiftActivities = arrivedShiftActivities.stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet());
        Map<BigInteger, ShiftActivity> existingShiftActivityMap = existingShiftActivities.stream().collect(Collectors.toMap(ShiftActivity::getActivityId, Function.identity(), (previous, current) -> previous));
        Set<BigInteger> activitiesToEdit = new HashSet<>();
        Set<BigInteger> activitiesToAdd = new HashSet<>();
        Set<BigInteger> activitiesToDelete = new HashSet<>();
        for (ShiftActivity shiftActivity : arrivedShiftActivities) {
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

    private List<ShiftActivity> findShiftActivityToValidateStaffingLevel(List<ShiftActivity> existingShiftActivities, List<ShiftActivity> arrivedShiftActivities) {
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        try {
            for (int i = 0; i < arrivedShiftActivities.size(); i++) {
                ShiftActivity currentShiftActivity = arrivedShiftActivities.get(i);
                ShiftActivity existingShiftActivity = existingShiftActivities.get(i);
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

}
