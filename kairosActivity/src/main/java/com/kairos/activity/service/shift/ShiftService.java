package com.kairos.activity.service.shift;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.period.PlanningPeriod;

import com.kairos.activity.persistence.model.break_settings.BreakSettings;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.persistence.model.wta.StaffWTACounter;
import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.activity.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.activity.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.activity.persistence.repository.time_bank.TimeBankMongoRepository;
import com.kairos.activity.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.activity.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.shift.ShiftPublishDTO;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.WTARuleTemplateValidatorUtility;
import com.kairos.client.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.enums.shift.ShiftState;
import com.kairos.activity.response.dto.shift.StaffUnitPositionDetails;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.locale.LocaleService;
import com.kairos.activity.service.pay_out.PayOutService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.time_bank.TimeBankService;
import com.kairos.activity.service.wta.WTAService;
import com.kairos.activity.shift.*;
import com.kairos.activity.shift.ShiftFunctionWrapper;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.shift.ShiftWrapper;
import com.kairos.activity.spec.*;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.event.ShiftNotificationEvent;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.enums.shift.ShiftState;
import com.kairos.response.dto.web.AppliedFunctionDTO;
import com.kairos.response.dto.web.FunctionDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.activity.constants.AppConstants.*;
import static com.kairos.activity.util.DateUtils.MONGODB_QUERY_DATE_FORMAT;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getIntervalByRuleTemplates;
import static com.kairos.activity.util.DateUtils.ONLY_DATE;
import static javax.management.timer.Timer.ONE_MINUTE;

/**
 * Created by vipul on 30/8/17.
 */
@Service
public class ShiftService extends MongoBaseService {
    Logger logger = LoggerFactory.getLogger(ShiftService.class);

    @Inject
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
    private TimeBankMongoRepository timeBankMongoRepository;
    @Inject
    private PayOutService payOutService;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;

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
    private LocaleService localeService;


    public List<ShiftQueryResult> createShift(Long organizationId, ShiftDTO shiftDTO, String type, boolean bySubShift) {

        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
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
                ShiftQueryResult shiftQueryResult = saveShift(activity, staffAdditionalInfoDTO, shiftDTO);
                shiftQueryResults = Arrays.asList(shiftQueryResult);
            }
            if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
                Date shiftFromDate = DateUtils.toJodaDateTime(shiftDTO.getShiftDate()).withTimeAtStartOfDay().toDate();
                shiftQueryResults = getAverageOfShiftByActivity(staffAdditionalInfoDTO, activity, shiftFromDate);
            }
        } else {
            List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPosition(shiftDTO.getUnitPositionId(), shiftDTO.getStartDate(), shiftDTO.getEndDate());
            if (!shifts.isEmpty()) {
                exceptionService.duplicateDataException("message.shift.date.startandend", shifts.get(0).getStartDate(), shifts.get(0).getEndDate());
            }

            if (shiftDTO.getStartDate().after(shiftDTO.getEndDate())) {
                exceptionService.invalidRequestException("message.date.startandend");
            }
            ShiftQueryResult shiftQueryResult = saveShift(activity, staffAdditionalInfoDTO, shiftDTO);
            shiftQueryResults = Arrays.asList(shiftQueryResult);

        }
        return shiftQueryResults;
    }

    private ShiftQueryResult saveShift(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO) {
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>();
        Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getStartDate());
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        ShiftWithActivityDTO shiftWithActivityDTO = shiftDTO.buildResponse(activity);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        validateShiftWithActivity(wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO);
        Shift shift = buildShift(shiftWithActivityDTO);
        shift.setMainShift(true);

        List<Integer> activityDayTypes = new ArrayList<>();
        if (staffAdditionalInfoDTO.getDayTypes() != null && !staffAdditionalInfoDTO.getDayTypes().isEmpty()) {
            activityDayTypes = WTARuleTemplateValidatorUtility.getValidDays(staffAdditionalInfoDTO.getDayTypes(), activity.getTimeCalculationActivityTab().getDayTypes());
        }
        if (activityDayTypes.contains(new DateTime(shiftDTO.getStartDate()).getDayOfWeek())) {
            timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
        }
        Long shiftDurationInMinute = (shift.getEndDate().getTime() - shift.getStartDate().getTime()) / ONE_MINUTE;
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(shift.getUnitId(), shiftDurationInMinute);
        logger.info(breakSettings + "");
        Activity breakActivity = null;
        if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting()).isPresent() &&
                staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID)) {
            breakActivity = activityRepository.findByNameIgnoreCaseAndUnitIdAndDeletedFalse(PAID_BREAK, shift.getUnitId());

        } else if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting()).isPresent() &&
                staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.UNPAID)) {
            breakActivity = activityRepository.findByNameIgnoreCaseAndUnitIdAndDeletedFalse(UNPAID_BREAK, shift.getUnitId());
        }
        logger.debug(breakActivity + "");
        if (Optional.ofNullable(breakActivity).isPresent() && Optional.ofNullable(breakSettings).isPresent() && breakSettings.size() > 0) {
            shiftQueryResults = addBreakInShifts(shift, shiftDTO, breakActivity, breakSettings, shiftDurationInMinute);
        }

        save(shift);
        ShiftQueryResult shiftQueryResult = shift.getShiftQueryResult();
        shiftQueryResult.setSubShifts(shiftQueryResults);
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);


        //anil m2 notify event for updating staffing level
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));

        applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift, false, null, isShiftForPreence));
        return shiftQueryResult;
    }

    private Shift getShiftObject(ShiftDTO shiftDTO, String name, BigInteger activityId, Date startDate, Date endDate, Long allowedBreakDurationInMinute) {
        Shift childShift = new Shift(null, name, startDate, endDate, shiftDTO.getBid(), shiftDTO.getpId(), shiftDTO.getBonusTimeBank()
                , shiftDTO.getAmount(), shiftDTO.getProbability(), shiftDTO.getAccumulatedTimeBankInMinutes(), shiftDTO.getRemarks(), activityId, shiftDTO.getStaffId(), shiftDTO.getUnitId(), shiftDTO.getUnitPositionId());
        childShift.setShiftState(ShiftState.UNPUBLISHED);
        childShift.setMainShift(false);
        childShift.setAllowedBreakDurationInMinute(allowedBreakDurationInMinute);
        return childShift;

    }

    private List<ShiftQueryResult> addBreakInShifts(Shift mainShift, ShiftDTO shiftDTO, Activity breakActivity, List<BreakSettings> breakSettings, Long shiftDurationInMinute) {
        logger.info("ShiftDurationInMinute = {}", shiftDurationInMinute);
        Long startDateMillis = mainShift.getStartDate().getTime();
        Long endDateMillis = null;
        Long breakAllowedAfterMinute = 0L;
        Long allowedBreakDurationInMinute = 0L;
        String lastItemAdded = null;
        List<Shift> shifts = new ArrayList<>();
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>();
        for (int i = 0; i < breakSettings.size(); i++) {

            /**
             * The first eligible break hours after.It specifies you can take first break after this duration
             **/
            breakAllowedAfterMinute = (i == 0) ? breakSettings.get(i).getShiftDurationInMinute() : breakSettings.get(i).getShiftDurationInMinute() - breakSettings.get(i - 1).getShiftDurationInMinute();
            endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
            if (shiftDurationInMinute > breakAllowedAfterMinute) {
                shifts.add(getShiftObject(shiftDTO, shiftDTO.getName(), shiftDTO.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));
                // we have added a sub shift now adding the break for remaining period
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                // if still after subtraction the shift is greater than
                allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();
                startDateMillis = endDateMillis;
                logger.info("Remainig for ShiftDurationInMinute = {}", shiftDurationInMinute);
                lastItemAdded = SHIFT;
                if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                    endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                    shifts.add(getShiftObject(shiftDTO, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));

                    shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                    startDateMillis = endDateMillis;
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
        while (shiftDurationInMinute > breakAllowedAfterMinute) {
            // last end date is now start date
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
            shifts.add(getShiftObject(shiftDTO, shiftDTO.getName(), shiftDTO.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));
            shiftDurationInMinute = shiftDurationInMinute - breakAllowedAfterMinute;
            lastItemAdded = SHIFT;
            if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                startDateMillis = endDateMillis;
                endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                shifts.add(getShiftObject(shiftDTO, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));
                shiftDurationInMinute = shiftDurationInMinute - breakAllowedAfterMinute;
                lastItemAdded = BREAK;
            } else {
                logger.info("Remaing shift duration " + shiftDurationInMinute + " And we need to add break for {} ", allowedBreakDurationInMinute);
                // add break and increase main shift duration by remaining minute
            }

        }
        // Sometimes the break is
        if (shiftDurationInMinute >= 0 && shiftDurationInMinute < breakAllowedAfterMinute && lastItemAdded == SHIFT) {
            // handle later
            startDateMillis = endDateMillis;
            endDateMillis = endDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject(shiftDTO, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));


        } else if (shiftDurationInMinute >= 0 && shiftDurationInMinute < breakAllowedAfterMinute && lastItemAdded == BREAK) {
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject(shiftDTO, shiftDTO.getName(), shiftDTO.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));

        }
        save(shifts);
        mainShift.setSubShifts(shifts.stream().map(Shift::getId).collect(Collectors.toSet()));
        shifts.stream().forEach(s -> shiftQueryResults.add(s.getShiftQueryResult()));
        return shiftQueryResults;
    }


    private List<ShiftQueryResult> saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>(shiftDTOS.size());
        List<Integer> activityDayTypes = new ArrayList<>();
        if (staffAdditionalInfoDTO.getDayTypes() != null && !staffAdditionalInfoDTO.getDayTypes().isEmpty()) {
            activityDayTypes = WTARuleTemplateValidatorUtility.getValidDays(staffAdditionalInfoDTO.getDayTypes(), activity.getTimeCalculationActivityTab().getDayTypes());
        }
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        for (ShiftDTO shiftDTO : shiftDTOS) {
            Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getStartDate());
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            ShiftWithActivityDTO shiftQueryResult = shiftDTO.buildResponse(activity);
            shiftQueryResult.setActivity(activity);
            validateShiftWithActivity(wtaQueryResultDTO, shiftQueryResult, staffAdditionalInfoDTO);
            Shift shift = buildShift(shiftQueryResult);
            shift.setMainShift(true);
            if (activityDayTypes.contains(new DateTime(shiftDTO.getStartDate()).getDayOfWeek())) {
                timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
            }
            shifts.add(shift);
            timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
            boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));

            applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift, false, null, isShiftForPreence));
        }
        save(shifts);
        timeBankService.saveTimeBanks(staffAdditionalInfoDTO, shifts);
        payOutService.savePayOuts(staffAdditionalInfoDTO.getUnitPosition().getId(), shifts);
        shifts.stream().forEach(s -> shiftQueryResults.add(s.getShiftQueryResult()));
        return shiftQueryResults;
    }


    public ShiftWithActivityDTO updateShift(Long organizationId, ShiftDTO shiftDTO, String type) {

        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }

        if (shift.getShiftState().equals(ShiftState.FIXED) || shift.getShiftState().equals(ShiftState.PUBLISHED) || shift.getShiftState().equals(ShiftState.LOCKED)) {
            exceptionService.actionNotPermittedException("message.shift.state.update", shift.getShiftState());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        WTAQueryResultDTO wtaResponseDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }

        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());

        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }
        Activity activityOld = activityRepository.findActivityByIdAndEnabled(shift.getActivityId());
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        //copy old state of activity object
        Shift oldStateOfShift = new Shift();
        BeanUtils.copyProperties(shift, oldStateOfShift);
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        ShiftWithActivityDTO shiftWithActivityDTO = shiftDTO.buildResponse(activity);
        validateShiftWithActivity(wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO);
        shift = buildShift(shiftWithActivityDTO);
        shift.setMainShift(true);
        timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
        save(shift);
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);
        Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
        //anil m2 notify event for updating staffing level
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_WEEK"));

        applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift,
                true, oldStateOfShift, isShiftForPreence, false, activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_ABSENCE_TO_PRESENCE
                , activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_PRESENCE_TO_ABSENCE));
        shiftWithActivityDTO.setDurationMinutes(shift.getDurationMinutes());
        shiftWithActivityDTO.setScheduledMinutes(shift.getScheduledMinutes());
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
        List<ShiftQueryResult> activities = shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, startDateInISO, endDateInISO, staffAdditionalInfoDTO.getUnitId());
        activities.stream().map(s -> s.sortShifts()).collect(Collectors.toList());


        List<AppliedFunctionDTO> appliedFunctionDTOs = staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions();

        Map<LocalDate, FunctionDTO> funcitonDTOMap = new HashMap();
        if (appliedFunctionDTOs != null && !appliedFunctionDTOs.isEmpty()) {
            for (AppliedFunctionDTO appliedFunctionDTO : appliedFunctionDTOs) {
                if (appliedFunctionDTO.getAppliedDates() != null && !appliedFunctionDTO.getAppliedDates().isEmpty()) {
                    FunctionDTO functionDTO = new FunctionDTO(appliedFunctionDTO.getId(), appliedFunctionDTO.getName(), appliedFunctionDTO.getIcon());
                    for (Long date : appliedFunctionDTO.getAppliedDates()) {
                        funcitonDTOMap.put(Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate(), functionDTO);
                    }
                }
            }
        }
        return new ShiftFunctionWrapper(activities, funcitonDTOMap);
    }

    public void deleteShift(BigInteger shiftId) {
        Shift shift = shiftMongoRepository.findOne(shiftId);
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftId);
        }
        if (!shift.getShiftState().equals(ShiftState.UNPUBLISHED)) {
            exceptionService.actionNotPermittedException("message.shift.delete", shift.getShiftState());
        }
        shift.setDeleted(true);
        save(shift);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shift.getStaffId(), AppConstants.ORGANIZATION, shift.getUnitPositionId());
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);
        Activity activity = activityRepository.findActivityByIdAndEnabled(shift.getActivityId());
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        applicationContext.publishEvent(new ShiftNotificationEvent(shift.getUnitId(), DateUtils.onlyDate(shift.getStartDate()), shift,
                false, null, isShiftForPreence, true, false, false));

    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }

    public void validateShiftWithActivity(WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.position");
        }
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta.notFound");
        }
        if (wtaQueryResultDTO.getEndDate() != null && new DateTime(wtaQueryResultDTO.getEndDate()).isBefore(shift.getEndDate().getTime())) {
            throw new ActionNotPermittedException("WTA is Expired for unit employment.");
        }
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(shift, wtaQueryResultDTO, staffAdditionalInfoDTO);
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkills());
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffAdditionalInfoDTO.getUnitPosition().getExpertise());
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        /* List<Long> dayTypeIds = activity.getRulesActivityTab().getDayTypes();
        if (dayTypeIds != null) {
            List<DayType> dayTypes = countryRestClient.getDayTypes(dayTypeIds);
            Specification<Activity> activityDayTypeSpec = new DayTypeSpecification(dayTypes, shift.getStartDate());
            specification.and(activityDayTypeSpec);
        }*/
        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification.and(activityExpertiseSpecification).and(activitySkillSpec).and(wtaRulesSpecification);
        activitySpecification.isSatisfied(shift);
        updateWTACounter(ruleTemplateSpecificInfo, staffAdditionalInfoDTO);
    //TODO Pradeep will look into dayType

//        List<Long> dayTypeIds = activity.getRulesActivityTab().getDayTypes();
//        if (dayTypeIds != null) {
//            List<DayType> dayTypes = countryRestClient.getDayTypes(dayTypeIds);
//            ActivitySpecification<Activity> activityDayTypeSpec = new ActivityDayTypeSpecification(dayTypes, shift.getStartDate());
//            activitySpecification.and(activityDayTypeSpec);
//        }

        activitySpecification.isSatisfied(shift);
    }
    private RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(ShiftWithActivityDTO shift,WTAQueryResultDTO wtaQueryResultDTO,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        Phase phase = phaseService.getPhaseCurrentByUnit(shift.getUnitId(), shift.getStartDate());
        logger.info("Current phase is " + phase.getName() + " for date " + new DateTime(shift.getStartDate()));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(),DateUtils.asLocalDate(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(),DateUtils.asDate(planningPeriod.getStartDate()),DateUtils.asDate(planningPeriod.getEndDate()),phase.getName());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift,wtaQueryResultDTO.getRuleTemplates());
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(staffAdditionalInfoDTO.getUnitPosition().getId(),DateUtils.getDateByZonedDateTime(intervalByRuleTemplates.getStart()),DateUtils.getDateByZonedDateTime(intervalByRuleTemplates.getEnd()));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankMongoRepository.findAllByUnitPositionAndBeforeDate(staffAdditionalInfoDTO.getUnitPosition().getId(),shift.getStartDate());
        Map<BigInteger,Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(sc->sc.getRuleTemplateId(),sc->sc.getCount()));
        Interval interval = new Interval(staffAdditionalInfoDTO.getUnitPosition().getStartDateMillis(),staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis()==null ? shift.getEndDate().getTime() : staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis());
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(staffAdditionalInfoDTO.getUnitPosition().getId(),staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes(),staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek(),DateUtils.asLocalDate(new Date(staffAdditionalInfoDTO.getUnitPosition().getStartDateMillis())),staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis()!=null ? DateUtils.asLocalDate(new Date(staffAdditionalInfoDTO.getUnitPosition().getEndDateMillis())) : null);
        int totalTimeBank = - timeBankCalculationService.calculateTimeBankForInterval(interval,unitPositionWithCtaDetailsDTO,false,dailyTimeBankEntries,false);
        return new RuleTemplateSpecificInfo(shifts,shift,staffAdditionalInfoDTO.getTimeSlotSets(),phase.getName(),new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(),DateUtils.asDate(planningPeriod.getEndDate()).getTime()),staffWTACounterMap,staffAdditionalInfoDTO.getDayTypes(),staffAdditionalInfoDTO.getUser(),totalTimeBank);
    }

    private void updateWTACounter(RuleTemplateSpecificInfo infoWrapper,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(),infoWrapper.getPlanningPeriod().getStartDate(),infoWrapper.getPlanningPeriod().getEndDate(),infoWrapper.getPhase());
        for (StaffWTACounter staffWTACounter : staffWTACounters) {
            staffWTACounter.setCount(infoWrapper.getCounterMap().get(staffWTACounter.getRuleTemplateId()));
            infoWrapper.getCounterMap().remove(staffWTACounter.getRuleTemplateId());
        }
        List<StaffWTACounter> newStaffWTACounter = infoWrapper.getCounterMap().entrySet().stream().map(s->new StaffWTACounter(infoWrapper.getPlanningPeriod().getStartLocalDate(),infoWrapper.getPlanningPeriod().getEndLocalDate(),s.getKey(),staffAdditionalInfoDTO.getUnitPosition().getId(),staffAdditionalInfoDTO.getUnitId(),s.getValue(),infoWrapper.getPhase())).collect(Collectors.toList());
        staffWTACounters.addAll(newStaffWTACounter);
        if(!staffWTACounters.isEmpty()) {
            save(staffWTACounters);
        }
    }

    public ShiftQueryResult addSubShift(Long unitId, ShiftDTO shiftDTO, String type) {
        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }

        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        WTAQueryResultDTO wtaResponseDTO = wtaMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getStaffId());
        }
        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }

        shift = shiftDTO.buildShift();
        shift.setUnitId(unitId);
        ShiftWithActivityDTO shiftWithActivityDTO = shiftDTO.buildResponse(activity);
        shiftWithActivityDTO.setActivity(activity);
        WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getOne(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        validateShiftWithActivity(wtaQueryResultDTO,shiftWithActivityDTO, staffAdditionalInfoDTO);
        ShiftQueryResult shiftQueryResult;
        if (shiftDTO.getSubShifts().size() == 0) {
            shift = shiftDTO.buildShift();
            shift.setMainShift(true);
            save(shift);
            shiftQueryResult = shift.getShiftQueryResult();
        } else {
            List<Shift> shifts;
            shifts = verifyCompositeShifts(shiftDTO, shiftDTO.getId());
            save(shifts);
            Set<BigInteger> subShiftsIds = shifts.parallelStream().map(Shift::getId).collect(Collectors.toSet());
            shift = shiftDTO.buildShift();
            shift.setMainShift(true);
            shift.setUnitId(unitId);
            shift.setSubShifts(subShiftsIds);
            save(shift);
            shiftQueryResult = geSubShiftResponse(shift, shifts);
        }
        timeBankService.saveTimeBank(staffAdditionalInfoDTO, shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);
        return shiftQueryResult;
    }

    /**
     * This method is used to check the timings overlap of sub shifts
     */
    protected void validateTimingOfShifts(ShiftDTO shiftDTO) {
        Date parentShiftStartDateTime = shiftDTO.getStartDate();
        Date parentShiftEndDateTime = shiftDTO.getEndDate();
        logger.info(shiftDTO.getSubShifts().size() + "");
        for (int i = 0; i < shiftDTO.getSubShifts().size(); i++) {
            ShiftDTO subShifts = shiftDTO.getSubShifts().get(i);
            if (i == 0) {
                if ((!parentShiftStartDateTime.equals(subShifts.getStartDate())) || (parentShiftEndDateTime.before(subShifts.getEndDate()))) {
                    logger.info("start " + parentShiftStartDateTime + "-" + subShifts.getStartDate()
                            + "end " + parentShiftEndDateTime + "-" + subShifts.getEndDate() + "shift data");
                    exceptionService.invalidRequestException("message.shift.date.startandend.incorrect", (i - 1));
                }
            } else {
                if ((!parentShiftEndDateTime.equals(subShifts.getStartDate())) || (shiftDTO.getEndDate().before(subShifts.getEndDate()))) {
                    logger.info("start " + (parentShiftStartDateTime) + "-" + subShifts.getStartDate()
                            + "end " + (parentShiftEndDateTime) + "-" + subShifts.getEndDate() + "shift data");
                    exceptionService.invalidRequestException("message.shift.date.startandend.incorrect", (i - 1));
                }
            }
            // making the calculating the previous  object as parent
            parentShiftEndDateTime = subShifts.getEndDate();
        }
    }

    private List<Shift> verifyCompositeShifts(ShiftDTO shiftDTO, BigInteger shiftId) {
        if (shiftDTO.getSubShifts().size() == 0) {
            exceptionService.invalidRequestException("message.sub-shift.create");
        }
        Activity activity = activityRepository.findOne(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent() || !Optional.ofNullable(activity.getCompositeActivities()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.sub-shift.activity.create");
        }
        validateTimingOfShifts(shiftDTO);
        List<ShiftDTO> subShiftDTOS = shiftDTO.getSubShifts();

        Set<BigInteger> activityIds = subShiftDTOS.parallelStream().map(ShiftDTO::getActivityId).collect(Collectors.toSet());

        activity.getCompositeActivities().add(shiftDTO.getActivityId());
        if (!activity.getCompositeActivities().containsAll(activityIds)) {
            exceptionService.invalidRequestException("message.activity.multishift");
        }

        List<Shift> shifts = new ArrayList<>(shiftDTO.getSubShifts().size());
        for (int i = 0; i < shiftDTO.getSubShifts().size(); i++) {
            Shift subShifts = shiftDTO.getSubShifts().get(i).buildShift();
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

    public List<ShiftQueryResult> getAverageOfShiftByActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Activity activity, Date fromDate) {
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
            shiftQueryResults = saveShifts(activity, staffAdditionalInfoDTO, shiftDTOS);
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

    public Integer getStartAverage(int day, List<ShiftQueryResult> shifts) {
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

    public Map<String, List<BigInteger>> publishShifts(ShiftPublishDTO shiftPublishDTO) {
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalse(shiftPublishDTO.getShiftIds());

        List<BigInteger> success = new ArrayList<>();
        List<BigInteger> error = new ArrayList<>();
        Map<String, List<BigInteger>> response = new HashMap<>();
        response.put("success", success);
        response.put("error", error);
        if (!shifts.isEmpty()) {
            shifts.forEach(shift -> {
                if (!shift.isDeleted()) {
                    shift.setShiftState(shiftPublishDTO.getShiftState());
                    success.add(shift.getId());
                } else {
                    error.add(shift.getId());

                }
            });
            save(shifts);
        }

        return response;
    }

    public Shift buildShift(ShiftWithActivityDTO shift){
        return ObjectMapperUtils.copyPropertiesByMapper(shift,Shift.class);
    }

    public ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, Date selectedDate) throws ParseException {
        Date endDate = new Date(selectedDate.toString());
        endDate.setDate(endDate.getDate() + 1);
        List<ShiftQueryResult> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, selectedDate, endDate);
        List<OpenShiftResponseDTO> openShifts = openShiftMongoRepository.getOpenShiftsByUnitIdAndSelectedDate(unitId, selectedDate);
        return new ShiftWrapper(assignedShifts, openShifts);
    }

    public CopyShiftResponse copyShifts(Long unitId, CopyShiftDTO copyShiftDTO) {


        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalse(copyShiftDTO.getShiftIds());

        Set<BigInteger> activityIds = shifts.parallelStream().map(shift -> shift.getActivityId()).collect(Collectors.toSet());
        List<Activity> activities = activityRepository.findAllActivitiesByIds(activityIds);

        List<StaffUnitPositionDetails> staffDataList = restClient.getStaffsUnitPosition(unitId, copyShiftDTO.getStaffIds(), copyShiftDTO.getExpertiseId());
        Set<BigInteger> wtaIds = staffDataList.parallelStream().map(wta -> wta.getWorkingTimeAgreementId()).collect(Collectors.toSet());

        List<WorkingTimeAgreement> workingTimeAgreements = wtaService.findAllByIdAndDeletedFalse(wtaIds);
        List<Phase> phases = phaseService.getAllPhasesOfUnit(unitId);

        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();

        copyShiftDTO.getStaffIds().forEach(currentStaffId -> {
            StaffUnitPositionDetails staffUnitPosition = staffDataList.parallelStream().filter(unitPosition -> unitPosition.getStaff().getId().equals(currentStaffId)).findFirst().get();
            WorkingTimeAgreement workingTimeAgreement = workingTimeAgreements.stream().filter(wta -> wta.getId().equals(staffUnitPosition.getWorkingTimeAgreementId())).findAny().get();

            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffUnitPosition, activities, workingTimeAgreement, phases, copyShiftDTO);

            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("error"));

            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);


        });

        return copyShiftResponse;
    }

    private Map<String, List<ShiftResponse>> copyForThisStaff(List<Shift> shifts, StaffUnitPositionDetails staffUnitPosition, List<Activity> activities, WorkingTimeAgreement workingTimeAgreement, List<Phase> phases, CopyShiftDTO copyShiftDTO) {

        List<Shift> newShifts = new ArrayList<>(shifts.size());
        Map<String, List<ShiftResponse>> statusMap = new HashMap<>();
        List<ShiftResponse> successfullyCopiedShifts = new ArrayList<>();
        List<ShiftResponse> errorInCopyingShifts = new ArrayList<>();
        shifts.forEach(shift -> {

            LocalTime startTime = LocalDateTime.ofInstant(shift.getStartDate().toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime endTime = LocalDateTime.ofInstant(shift.getEndDate().toInstant(), ZoneId.systemDefault()).toLocalTime();

            logger.info("Shift  start from {} ",startTime," shift end from {} ",endTime);


            Activity currentActivity = activities.parallelStream().filter(activity -> activity.getId().equals(shift.getActivityId())).findAny().get();
            ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(shift,ShiftWithActivityDTO.class);
            shiftWithActivityDTO.setActivity(currentActivity);
            List<String> responseMessages = validateShiftWhileCopy(shiftWithActivityDTO, staffUnitPosition, workingTimeAgreement, phases, copyShiftDTO);
            if (responseMessages.isEmpty()) {
                Shift copiedShift = new Shift(shift.getName(), shift.getStartDate(), shift.getEndDate(), shift.getRemarks(), shift.getActivityId(), staffUnitPosition.getStaff().getId(), shift.getPhase(), shift.getUnitId(),
                        shift.getScheduledMinutes(), shift.getDurationMinutes(), shift.isMainShift(), shift.getExternalId(), staffUnitPosition.getId(), shift.getShiftState(), shift.getParentOpenShiftId(), shift.getAllowedBreakDurationInMinute(), shift.getId());
                newShifts.add(copiedShift);
                successfullyCopiedShifts.add(new ShiftResponse(shift.getId(), shift.getName(), Arrays.asList(NO_CONFLICTS)));
            } else {
                List<String> errors = new ArrayList<>();
                responseMessages.forEach(responseMessage -> {
                    errors.add(localeService.getMessage(responseMessage));
                });
                errorInCopyingShifts.add(new ShiftResponse(shift.getId(), shift.getName(), errors));
            }

        });
        statusMap.put("success", successfullyCopiedShifts);
        statusMap.put("error", errorInCopyingShifts);
        save(newShifts);
        return statusMap;
    }

    public List<String> validateShiftWhileCopy(ShiftWithActivityDTO shift, StaffUnitPositionDetails staffUnitPositionDetails, WorkingTimeAgreement workingTimeAgreement, List<Phase> phases, CopyShiftDTO copyShiftDTO) {
        Phase phase = phaseService.getCurrentPhaseInUnitByDate(phases, DateUtils.asDate(copyShiftDTO.getStartDate()));
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffUnitPositionDetails.getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffUnitPositionDetails.getExpertise());

        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification;//.and(activityExpertiseSpecification);
        List<String> messages = activitySpecification.isSatisfiedString(shift);
        return messages;

    }

    public List<ShiftQueryResult> getShiftOfStaffByExpertiseId(Long unitId, Long staffId, String startDateAsString, String endDateAsString, Long expertiseId) throws ParseException {
        Long unitPositionId = restClient.getUnitPositionId(unitId, staffId, expertiseId);
        Date startDateInISO = DateUtils.getDate();
        Date endDateInISO = DateUtils.getDate();
        if (startDateAsString != null) {
            DateFormat dateISOFormat = new SimpleDateFormat(ONLY_DATE);
            Date startDate = dateISOFormat.parse(startDateAsString);
            startDateInISO = new DateTime(startDate).toDate();
            if (endDateAsString != null) {
                Date endDate = dateISOFormat.parse(endDateAsString);
                endDateInISO = new DateTime(endDate).toDate();
            }

        }
        List<ShiftQueryResult> activities = shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, startDateInISO, endDateInISO, unitId);
        activities.stream().map(s -> s.sortShifts()).collect(Collectors.toList());

        return activities;
    }
}