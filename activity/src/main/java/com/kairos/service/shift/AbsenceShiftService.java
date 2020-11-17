package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithViolatedInfoDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.staff_settings.StaffActivitySettingService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_WTA_NOTFOUND;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;

@Service
public class AbsenceShiftService {

    @Inject
    private PhaseService phaseService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;
    @Inject
    private ShiftStatusService shiftStatusService;
    @Inject
    private StaffActivitySettingService staffActivitySettingService;

    public List<ShiftWithViolatedInfoDTO> createAbsenceTypeShift(ActivityWrapper activityWrapper, ShiftDTO shiftDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Object[] shiftOverlapInfo, ShiftActionType shiftActionType) {
        List<ShiftWithViolatedInfoDTO>  shiftWithViolatedInfoDTOS = new ArrayList<>();
        BigInteger absenceReasonCodeId = shiftDTO.getActivities().get(0).getAbsenceReasonCodeId();
        if (activityWrapper.getActivity().getActivityTimeCalculationSettings().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
            Date endDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            Date startDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).minusWeeks(activityWrapper.getActivity().getActivityTimeCalculationSettings().getHistoryDuration()).withTimeAtStartOfDay().toDate();
            List<ShiftDTO> shifts = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
            ShiftDTO newShiftDTO;
            if (TimeCalaculationType.MIDNIGHT_TO_MIDNIGHT_TYPE.equals(activityWrapper.getActivity().getActivityTimeCalculationSettings().getFullDayCalculationType())) {
                shiftDTO.setStartDate(asDate(shiftDTO.getShiftDate().atTime(LocalTime.MIN)));
                shiftDTO.getActivities().get(0).setStartDate(asDate(shiftDTO.getShiftDate().atTime(LocalTime.MIN)));
                shiftDTO.getActivities().get(0).setEndDate(asDate(shiftDTO.getShiftDate().plusDays(1).atTime(LocalTime.MIN)));
                shiftDTO.setEndDate(asDate(shiftDTO.getShiftDate().plusDays(1).atTime(LocalTime.MIN)));
                newShiftDTO = shiftDTO;
            } else {
                newShiftDTO = calculateAverageShiftByActivity(shifts, activityWrapper.getActivity(),staffAdditionalInfoDTO, absenceReasonCodeId,shiftDTO.getShiftDate(),shiftDTO.getActivities().get(0));
            }
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(staffAdditionalInfoDTO.getUnitId(), newShiftDTO.getActivities().get(0).getStartDate(), newShiftDTO.getActivities().get(newShiftDTO.getActivities().size() - 1).getEndDate());
            newShiftDTO.setId(shiftDTO.getId());
            newShiftDTO.setShiftType(ShiftType.ABSENCE);
            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftService.saveShift(staffAdditionalInfoDTO, newShiftDTO, phase, shiftOverlapInfo, shiftActionType);
            shiftService.addReasonCode(shiftWithViolatedInfoDTO.getShifts());
            shiftWithViolatedInfoDTOS.add(shiftWithViolatedInfoDTO);
        } else {
            shiftWithViolatedInfoDTOS = getAverageOfShiftByActivity(staffAdditionalInfoDTO, activityWrapper.getActivity(), shiftDTO, absenceReasonCodeId, shiftActionType);
        }
        return shiftWithViolatedInfoDTOS;
    }

    private List<ShiftWithViolatedInfoDTO> getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, ShiftDTO shiftDTO, BigInteger absenceReasonCodeId, ShiftActionType shiftActionType) {
        Date fromDate = asDate(shiftDTO.getShiftDate());
        Date endDate = new DateTime(fromDate).withTimeAtStartOfDay().plusDays(8).toDate();
        Date startDate = new DateTime(fromDate).minusWeeks(activity.getActivityTimeCalculationSettings().getHistoryDuration()).toDate();
        List<ShiftDTO> shiftQueryResultsInInterval = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        //As we support create Fullweek Shift from Monday to sunday
        if (!shiftDTO.getShiftDate().getDayOfWeek().equals(activity.getActivityTimeCalculationSettings().getFullWeekStart())) {
            exceptionService.actionNotPermittedException("error.activity.fullweek.start", StringUtils.capitalize(activity.getActivityTimeCalculationSettings().getFullWeekStart().toString().toLowerCase()));
        }
        for (int day = 0; day < 7; day++) {
            ShiftDTO newShiftDTO;
            LocalDate shiftDate = shiftDTO.getShiftDate().plusDays(day);
            if (TimeCalaculationType.MIDNIGHT_TO_MIDNIGHT_TYPE.equals(activity.getActivityTimeCalculationSettings().getFullWeekCalculationType())) {
                startDate = asDate(shiftDate.atTime(LocalTime.MIN));
                endDate = asDate(shiftDate.plusDays(1).atTime(LocalTime.MIN));
                shiftDTO.setStartDate(startDate);
                shiftDTO.getActivities().get(0).setStartDate(startDate);
                shiftDTO.getActivities().get(0).setEndDate(endDate);

                shiftDTO.setEndDate(endDate);
                newShiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftDTO.class);
            } else {
                newShiftDTO = calculateAverageShiftByActivity(shiftQueryResultsInInterval, activity,
                        staffAdditionalInfoDTO, absenceReasonCodeId,shiftDate,shiftDTO.getActivities().get(0));
            }
            newShiftDTO.setId(shiftDTO.getId());
            shiftDTO.setId(null);
            newShiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            newShiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftDTOS.add(newShiftDTO);
        }
        shiftValidatorService.validateShifts(shiftDTOS);
        List<ShiftWithViolatedInfoDTO>  shiftWithViolatedInfoDTOS = new ArrayList<>();
        if (!shiftDTOS.isEmpty()) {
            shiftWithViolatedInfoDTOS = saveShifts(activity, staffAdditionalInfoDTO, shiftDTOS, shiftActionType);
        }
        return shiftWithViolatedInfoDTOS;
    }

    private ShiftDTO calculateAverageShiftByActivity(List<ShiftDTO> shifts, Activity activity,
                                                     StaffAdditionalInfoDTO staffAdditionalInfoDTO, BigInteger absenceReasonCodeId, LocalDate shiftDate, ShiftActivityDTO shiftActivityDTO) {
        int contractualMinutesInADay = staffAdditionalInfoDTO.getEmployment().getTotalWeeklyMinutes() / staffAdditionalInfoDTO.getEmployment().getWorkingDaysInWeek();
        ShiftActivityDTO shiftActivity = new ShiftActivityDTO(activity.getId(), activity.getName(),shiftActivityDTO.getStatus());
        shiftActivity.setRemarks(shiftActivityDTO.getRemarks());
        Integer startAverageMin = null;
        Date fromDate = asDate(shiftDate);
        if (shifts != null && !shifts.isEmpty() && activity.getActivityTimeCalculationSettings().getHistoryDuration() != 0) {
            startAverageMin = getStartAverage(new DateTime(fromDate).getDayOfWeek(), shifts);

        }
        StaffActivitySettingDTO staffActivitySetting = staffActivitySettingService.getStaffActivitySettingsByActivityId(activity.getUnitId(), activity.getId(),staffAdditionalInfoDTO.getId());
        LocalTime defaultStartTime = isNotNull(staffActivitySetting) && isNotNull(staffActivitySetting.getDefaultStartTime()) ? staffActivitySetting.getDefaultStartTime() : activity.getActivityTimeCalculationSettings().getDefaultStartTime();
        DateTime startDateTime = (startAverageMin != null) ?
                new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes(startAverageMin) :
                new DateTime(fromDate).withTimeAtStartOfDay().plusMinutes((defaultStartTime.getHour() * 60) + defaultStartTime.getMinute());
        Date startDate = startDateTime.toDate();
        Date endDate = startDateTime.plusMinutes(contractualMinutesInADay).toDate();
        shiftActivity.setStartDateAndEndDate(startDate,endDate);
        shiftActivity.setActivityName(activity.getName());
        shiftActivity.setAbsenceReasonCodeId(absenceReasonCodeId);
        return new ShiftDTO(Arrays.asList(shiftActivity), staffAdditionalInfoDTO.getUnitId(), staffAdditionalInfoDTO.getId(), staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
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

    public List<ShiftWithViolatedInfoDTO> saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS, ShiftActionType shiftActionType) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        Set<LocalDateTime> dates = shiftDTOS.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
        Map<Date, Phase> phaseMapByDate = phaseService.getPhasesByDates(shiftDTOS.get(0).getUnitId(), dates);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shiftDTOS.get(0).getActivities().get(0).getStartDate());
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
        }
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getActivityBalanceSettings().getTimeTypeId());
        Map<BigInteger, ActivityWrapper> activityWrapperMap = new HashMap<>();
        activityWrapperMap.put(activity.getId(), new ActivityWrapper(activity, timeType.getTimeTypes().toValue(),timeType));
        List<ShiftWithViolatedInfoDTO>  shiftWithViolatedInfoDTOS = new ArrayList<>();
        shiftDTOS.sort(Comparator.comparing(ShiftDTO::getStartDate));
        Date startDate = shiftDTOS.get(0).getStartDate();
        Date endDate = shiftDTOS.get(shiftDTOS.size() - 1).getEndDate();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdAndBetweenDates(staffAdditionalInfoDTO.getUnitId(), startDate, endDate);
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(shiftDTO, activityWrapperMap,null);
            ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phaseMapByDate.get(shiftDTO.getActivities().get(0).getStartDate()), wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, false, true);
            Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
            Optional<PlanningPeriod> planningPeriodByShift = planningPeriods.stream().filter(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate())).contains(shift.getStartDate()) || planningPeriod.getEndDate().equals(asLocalDate(shift.getStartDate()))).findAny();
            if (!planningPeriodByShift.isPresent()) {
                exceptionService.actionNotPermittedException("message.shift.planning.period.exits", shift.getStartDate());
            }
            shift.setPlanningPeriodId(planningPeriodByShift.get().getId());
            shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            shiftStatusService.updateStatusOfShiftIfPhaseValid(planningPeriodByShift.get(),phaseMapByDate.get(shiftDTO.getActivities().get(0).getStartDate()), shift, activityWrapperMap, staffAdditionalInfoDTO);
            shifts.add(shift);
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            shiftWithViolatedInfoDTOS.add(updatedShiftWithViolatedInfoDTO);
            /*shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getActivities());
            shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());*/
        }
        Phase phase = phaseMapByDate.get(shiftDTOS.get(0).getActivities().get(0).getStartDate());
        if (PhaseDefaultName.TIME_ATTENDANCE.equals(phase.getPhaseEnum()) || !isRuleViolated(shiftWithViolatedInfoDTOS)) {
            shiftService.saveShiftWithActivity(phaseMapByDate, shifts, staffAdditionalInfoDTO);
            shiftDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(shifts, ShiftDTO.class);
            shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS);
            shiftDTOS = timeBankService.updateTimebankDetailsInShiftDTO(shiftDTOS);
            shiftDTOS.forEach(shiftDTO -> shiftWithViolatedInfoDTOS.add(new ShiftWithViolatedInfoDTO(newArrayList(shiftDTO))));
        }
        //shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
        return shiftWithViolatedInfoDTOS;
    }

    public boolean isRuleViolated(List<ShiftWithViolatedInfoDTO>  shiftWithViolatedInfoDTOS){
        for (ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO : shiftWithViolatedInfoDTOS) {
            if(!(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty())){
                return true;
            }
        }
        return false;
    }

}
