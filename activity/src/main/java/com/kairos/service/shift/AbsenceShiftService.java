package com.kairos.service.shift;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithViolatedInfoDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
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
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;

@Service
public class AbsenceShiftService {

    @Inject private PhaseService phaseService;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ShiftService shiftService;
    @Inject private ShiftValidatorService shiftValidatorService;
    @Inject private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject private TimeBankService timeBankService;
    @Inject private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;

    public ShiftWithViolatedInfoDTO createAbsenceTypeShift(ActivityWrapper activityWrapper, ShiftDTO shiftDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO;
        Long absenceReasonCodeId = shiftDTO.getActivities().get(0).getAbsenceReasonCodeId();
        if (activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION)) {
            Date endDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            Date startDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).minusWeeks(activityWrapper.getActivity().getTimeCalculationActivityTab().getHistoryDuration()).withTimeAtStartOfDay().toDate();
            List<ShiftDTO> shifts = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
            ShiftDTO newShiftDTO = calculateAverageShiftByActivity(shifts, activityWrapper.getActivity(), staffAdditionalInfoDTO, shiftDTO, absenceReasonCodeId);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shiftDTO.getUnitId(), newShiftDTO.getActivities().get(0).getStartDate(), null);
            newShiftDTO.setId(shiftDTO.getId());
            newShiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftWithViolatedInfoDTO = shiftService.saveShift(staffAdditionalInfoDTO, newShiftDTO, phase);
        } else {
            shiftWithViolatedInfoDTO = getAverageOfShiftByActivity(staffAdditionalInfoDTO, activityWrapper.getActivity(), shiftDTO, absenceReasonCodeId);
        }
        return shiftWithViolatedInfoDTO;
    }

    private ShiftWithViolatedInfoDTO getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, ShiftDTO shiftDTO, Long absenceReasonCodeId) {
        Date fromDate = asDate(shiftDTO.getShiftDate());
        Date endDate = new DateTime(fromDate).withTimeAtStartOfDay().plusDays(8).toDate();
        Date startDate = new DateTime(fromDate).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).toDate();
        List<ShiftDTO> shiftQueryResultsInInterval = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        //As we support create Fullweek Shift from Monday to sunday
        if(!shiftDTO.getShiftDate().getDayOfWeek().equals(activity.getTimeCalculationActivityTab().getFullWeekStart())){
            exceptionService.actionNotPermittedException("error.activity.fullweek.start",activity.getTimeCalculationActivityTab().getFullWeekStart());
        }
        for (int day = 0; day < 7; day++) {
            ShiftDTO newShiftDTO = calculateAverageShiftByActivity(shiftQueryResultsInInterval, activity, staffAdditionalInfoDTO, shiftDTO, absenceReasonCodeId);
            newShiftDTO.setId(shiftDTO.getId());
            shiftDTO.setId(null);
            newShiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            newShiftDTO.setShiftType(ShiftType.ABSENCE);
            shiftDTOS.add(newShiftDTO);
            shiftDTO.setShiftDate(shiftDTO.getShiftDate().plusDays(1));
        }
        shiftValidatorService.validateShifts(shiftDTOS);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
        if (!shiftDTOS.isEmpty()) {
            shiftWithViolatedInfoDTO = saveShifts(activity, staffAdditionalInfoDTO, shiftDTOS);

        }
        return shiftWithViolatedInfoDTO;
    }

    private ShiftDTO calculateAverageShiftByActivity(List<ShiftDTO> shifts, Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO, Long absenceReasonCodeId) {
        int contractualMinutesInADay = staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes() / staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek();
        ShiftActivityDTO shiftActivity = new ShiftActivityDTO(activity.getId(), activity.getName());
        Integer startAverageMin = null;
        Date fromDate = asDate(shiftDTO.getShiftDate());
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
        Date endDate = plusDays(fromDate, activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) ? 1 : 7);
        boolean shiftExists = shiftMongoRepository.existShiftsBetweenDurationByUnitPositionId(shiftDTO.getId(),staffAdditionalInfoDTO.getUnitPosition().getId(), fromDate,endDate,null);
        if (shiftExists) {
            exceptionService.actionNotPermittedException("message.shift.date.startandend", fromDate, endDate);
        }
        return new ShiftDTO(Arrays.asList(shiftActivity), staffAdditionalInfoDTO.getUnitId(), staffAdditionalInfoDTO.getId(), staffAdditionalInfoDTO.getUnitPosition().getId(), startDateTime.toDate(), startDateTime.plusMinutes(contractualMinutesInADay).toDate());
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
        shiftDTOS.sort(Comparator.comparing(ShiftDTO::getStartDate));
        Date startDate = shiftDTOS.get(0).getStartDate();
        Date endDate = shiftDTOS.get(shiftDTOS.size()-1).getEndDate();
        List<PlanningPeriod> planningPeriods = planningPeriodMongoRepository.findAllByUnitIdAndBetweenDates(staffAdditionalInfoDTO.getUnitId(),startDate,endDate);
        for (ShiftDTO shiftDTO : shiftDTOS) {
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityWrapperMap,staffAdditionalInfoDTO,phaseListByDate.get(shiftDTO.getStartDate()));
            ShiftWithViolatedInfoDTO updatedShiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phaseListByDate.get(shiftDTO.getActivities().get(0).getStartDate()), wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, false);
            Shift shift = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, Shift.class);
            Optional<PlanningPeriod> planningPeriodByShift = planningPeriods.stream().filter(planningPeriod -> new DateTimeInterval(asDate(planningPeriod.getStartDate()),asDate(planningPeriod.getEndDate())).contains(shift.getStartDate()) || planningPeriod.getEndDate().equals(asLocalDate(shift.getStartDate()))).findAny();
            if(!planningPeriodByShift.isPresent()){
                exceptionService.actionNotPermittedException("message.shift.planning.period.exits", shift.getStartDate());
            }
            shift.setPlanningPeriodId(planningPeriodByShift.get().getId());
            shift.setStaffUserId(staffAdditionalInfoDTO.getStaffUserId());
            shifts.add(shift);
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getActivities());
            shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().addAll(updatedShiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());
        }
        if (shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty()) {
            shiftService.saveShiftWithActivity(phaseListByDate, shifts, staffAdditionalInfoDTO);
            shiftDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class);
            shiftDTOS = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS,staffAdditionalInfoDTO.getUserAccessRoleDTO());
            LocalDate startLocalDate = staffAdditionalInfoDTO.getUnitPosition().getStartDate();
            LocalDate endLocalDate = asLocalDate(shiftDTOS.get(shiftDTOS.size() - 1).getStartDate());
            shiftDTOS = timeBankService.updateTimebankDetailsInShiftDTO(shiftDTOS, startLocalDate, endLocalDate, staffAdditionalInfoDTO);
        }
        shiftWithViolatedInfoDTO.setShifts(shiftDTOS);
        return shiftWithViolatedInfoDTO;
    }

}
