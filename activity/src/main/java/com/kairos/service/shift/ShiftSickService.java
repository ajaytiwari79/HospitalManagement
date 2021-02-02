package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithViolatedInfoDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.SicknessSetting;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_slot.TimeSlotRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.dashboard.SickService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.time_slot.TimeSlotSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.shift.ShiftType.SICK;
import static com.kairos.enums.sickness.ReplaceSickShift.PROTECTED_DAYS_OFF;


/**
 * CreatedBy vipulpandey on 31/8/18
 **/
@Service
public class ShiftSickService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(ShiftSickService.class);

    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private SickSettingsRepository sickSettingsRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject
    private AbsenceShiftService absenceShiftService;
    @Inject
    private ActivityService activityService;
    @Inject
    private ShiftDetailsService shiftDetailsService;
    @Inject
    private SickService sickService;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private TimeSlotRepository timeSlotRepository;


    public List<ShiftWithViolatedInfoDTO> createSicknessShiftsOfStaff(ShiftDTO shiftDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ActivityWrapper activityWrapper) {
        boolean alreadySickReported = shiftMongoRepository.alreadySickReportedForStaff(shiftDTO.getStaffId(), SICK, asDate(shiftDTO.getShiftDate()));
        if (alreadySickReported) {
            exceptionService.actionNotPermittedException(STAFF_ALREADY_SICK);
        }
        byte shiftNeedsToAddForNumberOfDays = activityWrapper.getActivity().getActivityRulesSettings().isAllowedAutoAbsence() ? activityWrapper.getActivity().getActivityRulesSettings().getRecurrenceDays() : 0;
        if (shiftNeedsToAddForNumberOfDays == 0) {
            shiftNeedsToAddForNumberOfDays = 1;
        }
        LocalDate endDate = shiftDTO.getShiftDate().plusDays(shiftNeedsToAddForNumberOfDays - 1);

        validateAndUpdateSicknessShift(activityWrapper, shiftDTO, staffAdditionalInfoDTO, endDate);
        Date startDate = asDate(shiftDTO.getShiftDate(), LocalTime.MIDNIGHT);
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), startDate, asDate(shiftDTO.getShiftDate().plusDays(1), LocalTime.MIDNIGHT));
        String timeZone = userIntegrationService.getTimeZoneByUnitId(shiftDTO.getUnitId());
        Date currentDate = DateUtils.getDateFromTimeZone(timeZone);
        Shift realTimeShift = shiftMongoRepository.findRealTimeShiftByStaffId(shiftDTO.getStaffId(), currentDate);
        int shiftAddedForNumberOfDays = 0;
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        if (PhaseDefaultName.REALTIME.equals(phase.getPhaseEnum()) && isNotNull(realTimeShift)) {
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activityService.getActivityWrapperMap(Arrays.asList(realTimeShift), shiftDTO);
            replaceWithSick(realTimeShift, activityWrapperMap.get(shiftDTO.getActivities().get(0).getActivityId()), currentDate);
            List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoList = shiftService.updateShift(ObjectMapperUtils.copyPropertiesByMapper(realTimeShift, ShiftDTO.class), false, false, ShiftActionType.SAVE);
            shiftWithViolatedInfoDTOS.add(shiftWithViolatedInfoList.get(0));
            shiftAddedForNumberOfDays = 1;
        }
        while (shiftAddedForNumberOfDays < shiftNeedsToAddForNumberOfDays) {
            ShiftDTO shiftDTO1 = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftDTO.class);
            shiftDTO1.setShiftDate(shiftDTO1.getShiftDate().plusDays(shiftAddedForNumberOfDays));
            setStartAndEndDate(shiftDTO1);
            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftService.saveShift(staffAdditionalInfoDTO, shiftDTO1, phase, new Object[]{false, null}, ShiftActionType.SAVE);
            shiftWithViolatedInfoDTOS.add(shiftWithViolatedInfoDTO);
            shiftAddedForNumberOfDays++;
        }
        return shiftWithViolatedInfoDTOS;
    }

    public void setStartAndEndDate(ShiftDTO shiftDTO) {
        shiftDTO.setStartDate(asDate(shiftDTO.getShiftDate(), LocalTime.MIDNIGHT));
        Date endDate = asDate(shiftDTO.getShiftDate().plusDays(1), LocalTime.MIDNIGHT);
        shiftDTO.setEndDate(endDate);
        shiftDTO.getActivities().get(0).setStartDate(asDate(shiftDTO.getShiftDate(), LocalTime.MIDNIGHT));
        shiftDTO.getActivities().get(0).setEndDate(endDate);
    }

    private void replaceWithSick(Shift shift, ActivityWrapper sickActivityWrapper, Date currentDate) {
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        for (int i = 0; i < shift.getActivities().size(); i++) {
            if (shift.getActivities().get(i).getInterval().contains(currentDate)) {
                shift.getActivities().get(i).setEndDate(currentDate);
                shiftActivities.add(shift.getActivities().get(i));
                ShiftActivity shiftActivity = new ShiftActivity(sickActivityWrapper.getActivity().getName(), shift.getActivities().get(i).getEndDate(), getStartOfDay(asDate(asLocalDate(shift.getStartDate()).plusDays(1))), sickActivityWrapper.getActivity().getId(), null, sickActivityWrapper.getActivity().getActivityGeneralSettings().getUltraShortName(), sickActivityWrapper.getActivity().getActivityGeneralSettings().getShortName());
                shiftActivities.add(shiftActivity);
                break;
            } else {
                shiftActivities.add(shift.getActivities().get(i));
            }
        }
        shift.setActivities(shiftActivities);
        shift.setStartDate(shiftActivities.get(0).getStartDate());
        shift.setEndDate(shiftActivities.get(shiftActivities.size() - 1).getEndDate());
    }


    public void disableSicknessShiftsOfStaff(Long staffId, Long unitId, LocalDate localDate, BigInteger activityId) {
        Activity sicknessActivity = activityRepository.findOne(new BigInteger("4859"));
        LocalDate endDate = localDate.plusDays(sicknessActivity.getActivityRulesSettings().getRecurrenceDays());
        List<Shift> shifts = shiftMongoRepository.findAllSicknessShiftByEmploymentIdAndActivityIds(staffId, asDate(localDate), asDate(endDate));
        if (isCollectionEmpty(shifts) || shifts.stream().noneMatch(k -> SICK.equals(k.getShiftType()))) {
            exceptionService.dataNotFoundException("Data not found");
        }
        Map<LocalDate, List<Shift>> shiftMap = shifts.stream().collect(Collectors.groupingBy(shift -> asLocalDate(shift.getStartDate())));
        List<ActivityWrapper> protectDaysOffActivity;
        if (PROTECTED_DAYS_OFF.equals(sicknessActivity.getActivityRulesSettings().getSicknessSetting().getReplaceSickShift())) {
            protectDaysOffActivity = activityRepository.getAllActivityWrapperBySecondLevelTimeType(TimeTypeEnum.PROTECTED_DAYS_OFF, unitId);
            if (isCollectionEmpty(protectDaysOffActivity)) {
                exceptionService.dataNotFoundException(MESSAGE_PROTECTEDDAYSOFF_ACTIVITY_NOT_FOUND);
            }
        }
        updateSickShift(sicknessActivity, shiftMap);
    }


    private void updateSickShift(Activity activity, Map<LocalDate, List<Shift>> shiftMap) {
        List<Shift> allShiftsToDelete = new ArrayList<>();
        List<Shift> allShiftsToUpdate = new ArrayList<>();
        shiftMap.forEach((date, shifts) -> {
            Shift sickShift = shifts.stream().filter(k -> k.getShiftType().equals(SICK)).findAny().orElse(null);
            if (sickShift != null) {
                allShiftsToDelete.add(sickShift);
                sickShift.setDeleted(true);
                if (isNull(activity.getActivityRulesSettings().getSicknessSetting().getReplaceSickShift())) {
                    exceptionService.actionNotPermittedException(PLEASE_SELECT_REPLACE_SETTINGS);
                }
                switch (activity.getActivityRulesSettings().getSicknessSetting().getReplaceSickShift()) {
                    case PROTECTED_DAYS_OFF:
                        List<ActivityWrapper> protectDaysOffActivity = activityRepository.getAllActivityWrapperBySecondLevelTimeType(TimeTypeEnum.PROTECTED_DAYS_OFF, activity.getUnitId());
                        if (isCollectionEmpty(protectDaysOffActivity)) {
                            exceptionService.dataNotFoundException(MESSAGE_PROTECTEDDAYSOFF_ACTIVITY_NOT_FOUND);
                        }
                        shifts.forEach(s -> s.setDeleted(true));
                        ShiftActivity shiftActivity = new ShiftActivity(protectDaysOffActivity.get(0).getActivity().getName(), asDate(date.atStartOfDay()), asDate(date.plusDays(1).atStartOfDay()), protectDaysOffActivity.get(0).getActivity().getId(), null, protectDaysOffActivity.get(0).getActivity().getActivityGeneralSettings().getUltraShortName(), protectDaysOffActivity.get(0).getActivity().getActivityGeneralSettings().getUltraShortName());
                        Shift protectedShift = new Shift(asDate(date.atStartOfDay()), asDate(date.plusDays(1).atStartOfDay()),
                                sickShift.getStaffId(), newArrayList(shiftActivity), sickShift.getEmploymentId(), sickShift.getUnitId(), sickShift.getPhaseId(), sickShift.getPlanningPeriodId());
                        allShiftsToUpdate.add(protectedShift);
                        allShiftsToDelete.addAll(shifts);
                        break;
                    case FREE_DAY:
                        shifts.forEach(s -> s.setDeleted(true));
                        allShiftsToDelete.addAll(shifts);
                        break;
                    case PUBLISHED_ACTIVITY:
                        List<Shift> shiftList = shifts.stream().filter(k -> !k.getActivities().get(0).getStatus().contains(ShiftStatus.PUBLISH)).collect(Collectors.toList());
                        shiftList.forEach(shift1 -> shift1.setDeleted(true));
                        allShiftsToDelete.addAll(shiftList);
                        break;
                    default:
                        break;
                }
            }

        });
        if (isCollectionNotEmpty(allShiftsToDelete)) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(allShiftsToDelete.get(0).getStartDate()), allShiftsToDelete.get(0).getStaffId(), allShiftsToDelete.get(0).getEmploymentId());
            staffAdditionalInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
            staffAdditionalInfoDTO.setTimeSlotSets(timeSlotRepository.findByUnitIdAndTimeSlotTypeOrderByStartDate(UserContext.getUserDetails().getLastSelectedOrganizationId(), TimeSlotType.SHIFT_PLANNING).getTimeSlots());
            shiftService.deleteShifts(new ArrayList<>(), allShiftsToDelete, staffAdditionalInfoDTO);
            shiftMongoRepository.saveEntities(allShiftsToDelete);
        }
        shiftService.createShifts(activity.getUnitId(), ObjectMapperUtils.copyCollectionPropertiesByMapper(allShiftsToUpdate, ShiftDTO.class), ShiftActionType.SAVE);


    }

    public void validateAndUpdateSicknessShift(ActivityWrapper activityWrapper, ShiftDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate endDate) {
        if (activityWrapper.getActivity().getActivityRulesSettings().isSicknessSettingValid()) {
            List<Shift> shifts = shiftMongoRepository.findAllShiftByIntervalAndEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate() == null ? DateUtils.getStartDateOfWeekFromDate(shift.getShiftDate()) : getStartOfDay(shift.getStartDate()), DateUtils.getEndOfDay(asDate(endDate)));
            validateSicknessShift(shift, staffAdditionalInfoDTO, activityWrapper, shifts);
        }
    }


    public void validateSicknessShift(ShiftDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ActivityWrapper activityWrapper, List<Shift> shifts) {
        List<String> errorMessages = new ArrayList<>();
        SicknessSetting sicknessSetting = activityWrapper.getActivity().getActivityRulesSettings().getSicknessSetting();
        sickService.validateSickSettings(staffAdditionalInfoDTO, activityWrapper, shifts, errorMessages, sicknessSetting);
        if (sicknessSetting.isUsedOnProtecedDaysOff()) {
            List<Activity> protectedDaysOffActivities = activityRepository.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.PROTECTED_DAYS_OFF, newHashSet(shift.getUnitId()));
            Set<BigInteger> protectedDayOffActivityIds = protectedDaysOffActivities.stream().map(MongoBaseEntity::getId).collect(Collectors.toSet());
            Set<BigInteger> activityIds = shifts.stream().flatMap(k -> k.getActivities().stream().map(ShiftActivity::getActivityId)).collect(Collectors.toSet());
            if (!CollectionUtils.containsAny(protectedDayOffActivityIds, activityIds)) {
                exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_USEDON_PROTECTEDDAYSOFF);
            }
        }
        if (sicknessSetting.isCanNotUsedTopOfApprovedAbsences()) {
            for (Shift currentElement : shifts) {
                if (ShiftType.ABSENCE.equals(currentElement.getShiftType()) && shift.getActivities().stream().anyMatch(shiftActivity -> shiftActivity.getStatus().contains(ShiftStatus.APPROVE))) {
                    exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_USEDON_APPROVEABSENCES);
                }
            }
        }
    }


}









