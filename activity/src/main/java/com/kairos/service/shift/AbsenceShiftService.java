package com.kairos.service.shift;

import com.google.common.base.Stopwatch;
import com.kairos.commons.utils.*;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.*;
import com.kairos.persistence.model.activity.*;
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
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_WTA_NOTFOUND;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

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


    public ShiftWithViolatedInfoDTO createAbsenceTypeShift(ActivityWrapper activityWrapper, ShiftDTO shiftDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO, boolean shiftOverlappedWithNonWorkingType, ShiftActionType shiftActionType) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        Long absenceReasonCodeId = shiftDTO.getActivities().get(0).getAbsenceReasonCodeId();
        if (activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
            Date endDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            Date startDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).minusWeeks(activityWrapper.getActivity().getTimeCalculationActivityTab().getHistoryDuration()).withTimeAtStartOfDay().toDate();
            List<ShiftDTO> shifts = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
            ShiftDTO newShiftDTO;
            if (TimeCalaculationType.MIDNIGHT_TO_MIDNIGHT_TYPE.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getFullDayCalculationType())) {
                shiftDTO.setStartDate(asDate(shiftDTO.getShiftDate().atTime(LocalTime.MIN)));
                shiftDTO.getActivities().get(0).setStartDate(asDate(shiftDTO.getShiftDate().atTime(LocalTime.MIN)));
                shiftDTO.getActivities().get(0).setEndDate(asDate(shiftDTO.getShiftDate().plusDays(1).atTime(LocalTime.MIN)));
                shiftDTO.setEndDate(asDate(shiftDTO.getShiftDate().plusDays(1).atTime(LocalTime.MIN)));
                newShiftDTO = shiftDTO;
            } else {
                newShiftDTO = calculateAverageShiftByActivity(shifts, activityWrapper.getActivity(),
                        staffAdditionalInfoDTO, absenceReasonCodeId, shiftDTO.getShiftDate(), shiftDTO.getActivities().get(0).getStatus());
            }
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), newShiftDTO.getActivities().get(0).getStartDate(), newShiftDTO.getActivities().get(newShiftDTO.getActivities().size() - 1).getEndDate());
            newShiftDTO.setId(shiftDTO.getId());
            newShiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftWithViolatedInfoDTO = shiftService.saveShift(staffAdditionalInfoDTO, newShiftDTO, phase, shiftOverlappedWithNonWorkingType, shiftActionType);
        } else {
            shiftWithViolatedInfoDTO = getAverageOfShiftByActivity(staffAdditionalInfoDTO, activityWrapper.getActivity(), shiftDTO, absenceReasonCodeId, shiftActionType);
        }
        return shiftWithViolatedInfoDTO;
    }

    private ShiftWithViolatedInfoDTO getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, ShiftDTO shiftDTO, Long absenceReasonCodeId, ShiftActionType shiftActionType) {
        Date fromDate = asDate(shiftDTO.getShiftDate());
        Date endDate = new DateTime(fromDate).withTimeAtStartOfDay().plusDays(8).toDate();
        Date startDate = new DateTime(fromDate).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).toDate();
        List<ShiftDTO> shiftQueryResultsInInterval = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        //As we support create Fullweek Shift from Monday to sunday
        if (!shiftDTO.getShiftDate().getDayOfWeek().equals(activity.getTimeCalculationActivityTab().getFullWeekStart())) {
            exceptionService.actionNotPermittedException("error.activity.fullweek.start", StringUtils.capitalize(activity.getTimeCalculationActivityTab().getFullWeekStart().toString().toLowerCase()));
        }
        for (int day = 0; day < 7; day++) {
            ShiftDTO newShiftDTO;
            LocalDate shiftDate = shiftDTO.getShiftDate().plusDays(day);
            if (TimeCalaculationType.MIDNIGHT_TO_MIDNIGHT_TYPE.equals(activity.getTimeCalculationActivityTab().getFullWeekCalculationType())) {
                startDate = asDate(shiftDate.atTime(LocalTime.MIN));
                endDate = asDate(shiftDate.plusDays(1).atTime(LocalTime.MIN));
                shiftDTO.setStartDate(startDate);
                shiftDTO.getActivities().get(0).setStartDate(startDate);
                shiftDTO.getActivities().get(0).setEndDate(endDate);
                shiftDTO.setEndDate(endDate);
                newShiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftDTO.class);
            } else {
                newShiftDTO = calculateAverageShiftByActivity(shiftQueryResultsInInterval, activity,
                        staffAdditionalInfoDTO, absenceReasonCodeId, shiftDate, shiftDTO.getActivities().get(0).getStatus());
            }
            newShiftDTO.setId(shiftDTO.getId());
            shiftDTO.setId(null);
            newShiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            newShiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftDTOS.add(newShiftDTO);
        }
        shiftValidatorService.validateShifts(shiftDTOS);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
        if (!shiftDTOS.isEmpty()) {
            shiftWithViolatedInfoDTO = saveShifts(activity, staffAdditionalInfoDTO, shiftDTOS, shiftActionType);

        }
        return shiftWithViolatedInfoDTO;
    }

    private ShiftDTO calculateAverageShiftByActivity(List<ShiftDTO> shifts, Activity activity,
                                                     StaffAdditionalInfoDTO staffAdditionalInfoDTO, Long absenceReasonCodeId, LocalDate shiftDate, Set<ShiftStatus> statuses) {
        int contractualMinutesInADay = staffAdditionalInfoDTO.getEmployment().getTotalWeeklyMinutes() / staffAdditionalInfoDTO.getEmployment().getWorkingDaysInWeek();
        ShiftActivityDTO shiftActivity = new ShiftActivityDTO(activity.getId(), activity.getName(), statuses);
        Integer startAverageMin = null;
        Date fromDate = asDate(shiftDate);
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

        return new ShiftDTO(Arrays.asList(shiftActivity), staffAdditionalInfoDTO.getUnitId(), staffAdditionalInfoDTO.getId(), staffAdditionalInfoDTO.getEmployment().getId(), startDateTime.toDate(), startDateTime.plusMinutes(contractualMinutesInADay).toDate());
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

    private ShiftWithViolatedInfoDTO saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS, ShiftActionType shiftActionType) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        Set<LocalDateTime> dates = shiftDTOS.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
        Map<Date, Phase> phaseMapByDate = phaseService.getPhasesByDates(shiftDTOS.get(0).getUnitId(), dates);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shiftDTOS.get(0).getActivities().get(0).getStartDate());
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
        }
        TimeType timeType = timeTypeMongoRepository.findOneById(activity.getBalanceSettingsActivityTab().getTimeTypeId());
        Map<BigInteger, ActivityWrapper> activityWrapperMap = new HashMap<>();
        activityWrapperMap.put(activity.getId(), new ActivityWrapper(activity, timeType.getTimeTypes().toValue()));
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
        shiftDTOS.sort(Comparator.comparing(ShiftDTO::getStartDate));
        Date startDate = shiftDTOS.get(0).getStartDate();
        Date endDate = shiftDTOS.get(shiftDTOS.size() - 1).getEndDate();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdAndBetweenDates(staffAdditionalInfoDTO.getUnitId(), startDate, endDate);
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap);
            ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phaseMapByDate.get(shiftDTO.getActivities().get(0).getStartDate()), wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, false);
            Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, Shift.class);
            Optional<PlanningPeriod> planningPeriodByShift = planningPeriods.stream().filter(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()), asDate(planningPeriod.getEndDate())).contains(shift.getStartDate()) || planningPeriod.getEndDate().equals(asLocalDate(shift.getStartDate()))).findAny();
            if (!planningPeriodByShift.isPresent()) {
                exceptionService.actionNotPermittedException("message.shift.planning.period.exits", shift.getStartDate());
            }
            shift.setPlanningPeriodId(planningPeriodByShift.get().getId());
            shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            shiftStatusService.updateStatusOfShiftIfPhaseValid(phaseMapByDate.get(shiftDTO.getActivities().get(0).getStartDate()), shift, activityWrapperMap, staffAdditionalInfoDTO.getUserAccessRoleDTO(), shiftActionType);
            shifts.add(shift);
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getActivities());
            shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());
        }
        Phase phase = phaseMapByDate.get(shiftDTOS.get(0).getActivities().get(0).getStartDate());
        if (PhaseDefaultName.TIME_ATTENDANCE.equals(phase.getPhaseEnum()) || shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            shiftService.saveShiftWithActivity(phaseMapByDate, shifts, staffAdditionalInfoDTO);
            shiftDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
            shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS, staffAdditionalInfoDTO.getUserAccessRoleDTO());
            shiftDTOS = timeBankService.updateTimebankDetailsInShiftDTO(shiftDTOS);
        }
        shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
        return shiftWithViolatedInfoDTO;
    }

}
