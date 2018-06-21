package com.kairos.activity.service.shift;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.break_settings.BreakSettings;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevel;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelActivity;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelInterval;
import com.kairos.activity.persistence.model.unit_settings.PhaseSettings;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.query_result.DateWiseShiftResponse;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.activity.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.activity.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.response.dto.shift.StaffUnitPositionDetails;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.locale.LocaleService;
import com.kairos.activity.service.pay_out.PayOutService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.time_bank.TimeBankService;
import com.kairos.activity.service.unit_settings.PhaseSettingsService;
import com.kairos.activity.service.wta.WTAService;
import com.kairos.activity.shift.*;
import com.kairos.activity.spec.*;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.event.ShiftNotificationEvent;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.enums.shift.ShiftState;
import com.kairos.persistence.model.user.access_permission.AccessGroupRole;
import com.kairos.response.dto.web.AppliedFunctionDTO;
import com.kairos.response.dto.web.FunctionDTO;
import com.kairos.response.dto.web.access_group.UserAccessRoleDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import com.kairos.response.dto.web.staff.StaffAccessRoleDTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.activity.constants.AppConstants.*;
import static com.kairos.activity.util.DateUtils.MONGODB_QUERY_DATE_FORMAT;
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
    private PayOutService payOutService;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private WTAService wtaService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OpenShiftMongoRepository openShiftMongoRepository;
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private PhaseSettingsService phaseSettingsService;
    @Inject
    private PhaseSettingsRepository phaseSettingsRepository;
    @Inject
    private GenericIntegrationService restClient;
    @Inject
    private LocaleService localeService;
    @Inject
    private GenericIntegrationService genericIntegrationService;


    public List<ShiftQueryResult> createShift(Long organizationId, ShiftDTO shiftDTO, String type, boolean bySubShift) {

        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), activity.getTimeCalculationActivityTab().getDayTypes());
        WTAResponseDTO wtaResponseDTO = wtaService.getWta(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setWorkingTimeAgreement(wtaResponseDTO);
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
        Shift mainShift = shiftDTO.buildShift();
        mainShift.setMainShift(true);
        mainShift.setName(activity.getName());
        validateStaffingLevel(mainShift, activity, true, staffAdditionalInfoDTO);
        validateShiftWithActivity(activity, mainShift, staffAdditionalInfoDTO);
        List<Integer> activityDayTypes = new ArrayList<>();
        if (staffAdditionalInfoDTO.getActivityDayTypes() != null && !staffAdditionalInfoDTO.getActivityDayTypes().isEmpty()) {
            activityDayTypes = staffAdditionalInfoDTO.getActivityDayTypes().stream().map(ad -> ad.getValue()).collect(Collectors.toList());
        }
        if (activityDayTypes.contains(new DateTime(shiftDTO.getStartDate()).getDayOfWeek())) {
            timeBankCalculationService.calculateScheduleAndDurationHour(mainShift, activity, staffAdditionalInfoDTO.getUnitPosition());
        }
        logger.info("break Allowed = {}", activity.getRulesActivityTab().isBreakAllowed());
        if (activity.getRulesActivityTab().isBreakAllowed()) {

            Long shiftDurationInMinute = (mainShift.getEndDate().getTime() - mainShift.getStartDate().getTime()) / ONE_MINUTE;
            List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(mainShift.getUnitId(), shiftDurationInMinute);

            Activity breakActivity = null;
            if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting()).isPresent() &&
                    staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID)) {
                breakActivity = activityRepository.findByNameIgnoreCaseAndUnitIdAndDeletedFalse(PAID_BREAK, mainShift.getUnitId());

            } else if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting()).isPresent() &&
                    staffAdditionalInfoDTO.getUnitPosition().getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.UNPAID)) {
                breakActivity = activityRepository.findByNameIgnoreCaseAndUnitIdAndDeletedFalse(UNPAID_BREAK, mainShift.getUnitId());
            }
            logger.info("BreakActivity for unit found= {}", breakActivity);
            if (Optional.ofNullable(breakActivity).isPresent() && Optional.ofNullable(breakSettings).isPresent() && breakSettings.size() > 0) {
                shiftQueryResults = addBreakInShifts(mainShift, shiftDTO, breakActivity, breakSettings, shiftDurationInMinute);
            }
        }
        save(mainShift);
        ShiftQueryResult shiftQueryResult = mainShift.getShiftQueryResult();
        shiftQueryResult.setSubShifts(shiftQueryResults);
        timeBankService.saveTimeBank(mainShift.getUnitPositionId(), mainShift);
        payOutService.savePayOut(mainShift.getUnitPositionId(), mainShift);


        //anil m2 notify event for updating staffing level
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));

        applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, mainShift, false, null, isShiftForPreence));
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
        Long totalBreakAllotedInMinute = 0L;
        String lastItemAdded = null;
        List<Shift> shifts = new ArrayList<>();
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>();
        for (int i = 0; i < breakSettings.size(); i++) {
            /**
             * The first eligible break hours after.It specifies you can take first break after this duration
             **/
            breakAllowedAfterMinute = breakSettings.get(i).getShiftDurationInMinute();

            if (shiftDurationInMinute > breakAllowedAfterMinute) {
                endDateMillis = startDateMillis + (breakAllowedAfterMinute * ONE_MINUTE);
                shifts.add(getShiftObject(shiftDTO, mainShift.getName(), shiftDTO.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));
                // we have added a sub shift now adding the break for remaining period
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                // if still after subtraction the shift is greater than
                allowedBreakDurationInMinute = breakSettings.get(i).getBreakDurationInMinute();
                startDateMillis = endDateMillis;
                lastItemAdded = SHIFT;
                if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                    endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                    shifts.add(getShiftObject(shiftDTO, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));

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
            shifts.add(getShiftObject(shiftDTO, mainShift.getName(), shiftDTO.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));
            shiftDurationInMinute = shiftDurationInMinute - breakAllowedAfterMinute;
            lastItemAdded = SHIFT;
            if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                startDateMillis = endDateMillis;
                endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                shifts.add(getShiftObject(shiftDTO, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                totalBreakAllotedInMinute += allowedBreakDurationInMinute;
                lastItemAdded = BREAK;
              } else {
                logger.info("Remaing shift duration " + shiftDurationInMinute + " And we need to add break for {} ", allowedBreakDurationInMinute);
                // add break and increase main shift duration by remaining minute
            }

        }
        // Sometimes the break is
        if (shiftDurationInMinute > 0 && shiftDurationInMinute < breakAllowedAfterMinute && lastItemAdded == SHIFT) {
            // handle later
            startDateMillis = endDateMillis;
            endDateMillis = endDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            totalBreakAllotedInMinute += allowedBreakDurationInMinute;
            shifts.add(getShiftObject(shiftDTO, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));


        } else if (shiftDurationInMinute > 0 && shiftDurationInMinute < breakAllowedAfterMinute && lastItemAdded == BREAK) {
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject(shiftDTO, mainShift.getName(), shiftDTO.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));

        }
        save(shifts);
        if (breakActivity.getName().equalsIgnoreCase(UNPAID_BREAK)) {
            mainShift.setScheduledMinutes(mainShift.getScheduledMinutes() - totalBreakAllotedInMinute.intValue());
        }
        mainShift.setSubShifts(shifts.stream().map(Shift::getId).collect(Collectors.toSet()));
        shifts.stream().forEach(s -> shiftQueryResults.add(s.getShiftQueryResult()));
        return shiftQueryResults;
    }


    private List<ShiftQueryResult> saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>(shiftDTOS.size());
        List<Integer> activityDayTypes = new ArrayList<>();
        if (staffAdditionalInfoDTO.getActivityDayTypes() != null && !staffAdditionalInfoDTO.getActivityDayTypes().isEmpty()) {
            activityDayTypes = staffAdditionalInfoDTO.getActivityDayTypes().stream().map(ad -> ad.getValue()).collect(Collectors.toList());
        }
        for (ShiftDTO shiftDTO : shiftDTOS) {
            Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getStartDate());
            shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
            Shift shift = shiftDTO.buildShift();
            shift.setMainShift(true);
            shift.setName(activity.getName());
            validateShiftWithActivity(activity, shift, staffAdditionalInfoDTO);
            if (activityDayTypes.contains(new DateTime(shiftDTO.getStartDate()).getDayOfWeek())) {
                timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
            }
            shifts.add(shift);
            timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
            boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));

            applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift, false, null, isShiftForPreence));
        }
        save(shifts);
        timeBankService.saveTimeBanks(staffAdditionalInfoDTO.getUnitPosition().getId(), shifts);
        payOutService.savePayOuts(staffAdditionalInfoDTO.getUnitPosition().getId(), shifts);
        shifts.stream().forEach(s -> shiftQueryResults.add(s.getShiftQueryResult()));
        return shiftQueryResults;
    }


    public ShiftQueryResult updateShift(Long organizationId, ShiftDTO shiftDTO, String type) {

        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }

        if (shift.getShiftState().equals(ShiftState.FIXED) || shift.getShiftState().equals(ShiftState.PUBLISHED) || shift.getShiftState().equals(ShiftState.LOCKED)) {
            exceptionService.actionNotPermittedException("message.shift.state.update", shift.getShiftState());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), null);
        WTAResponseDTO wtaResponseDTO = wtaService.getWta(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setWorkingTimeAgreement(wtaResponseDTO);
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }

        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());

        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }
        Activity activityOld = activityRepository.findActivityByIdAndEnabled(shift.getActivityId());


        //copy old state of activity object
        Shift oldStateOfShift = new Shift();
        BeanUtils.copyProperties(shift, oldStateOfShift);
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());

        shift.setActivityId(shiftDTO.getActivityId());
        shift.setBid(shiftDTO.getBid());
        shift.setpId(shiftDTO.getpId());
        shift.setBonusTimeBank(shiftDTO.getBonusTimeBank());
        shift.setAmount(shiftDTO.getAmount());
        shift.setRemarks(shiftDTO.getRemarks());
        shift.setStartDate(DateUtils.getDateByLocalDateAndLocalTime(shiftDTO.getStartLocalDate(), shiftDTO.getStartTime()));
        shift.setEndDate(DateUtils.getDateByLocalDateAndLocalTime(shiftDTO.getEndLocalDate(), shiftDTO.getEndTime()));
        shift.setName(activity.getName());
        shift.setDurationMinutes(shiftDTO.getDurationMinutes());

        validateShiftWithActivity(activity, shift, staffAdditionalInfoDTO);
        timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
        save(shift);
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);
        Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
        //anil m2 notify event for updating staffing level
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_WEEK"));

        applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift,
                true, oldStateOfShift, isShiftForPreence, false, activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_ABSENCE_TO_PRESENCE
                , activityChangeStatus(activityOld, activity) == ACTIVITY_CHANGED_FROM_PRESENCE_TO_ABSENCE));
        ShiftQueryResult shiftQueryResult = shift.getShiftQueryResult();
        shiftQueryResult.setName(activity.getName());
        shiftQueryResult.setDurationMinutes(shift.getDurationMinutes());
        shiftQueryResult.setScheduledMinutes(shift.getScheduledMinutes());
        return shiftQueryResult;
    }

    public ShiftFunctionWrapper getShiftByStaffId(Long id, Long staffId, String startDateAsString, String endDateAsString, Long week, Long unitPositionId, String type) throws ParseException {
        //StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, type);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, type, unitPositionId, null);
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
        List<ShiftQueryResult> activities = (Optional.ofNullable(unitPositionId).isPresent()) ? shiftMongoRepository.findAllShiftsBetweenDuration(unitPositionId, staffId, startDateInISO, endDateInISO, staffAdditionalInfoDTO.getUnitId()) :
                shiftMongoRepository.findAllShiftsBetweenDurationOfUnitAndStaffId(staffId, startDateInISO, endDateInISO, staffAdditionalInfoDTO.getUnitId());
        activities.stream().map(s -> s.sortShifts()).collect(Collectors.toList());

        List<AppliedFunctionDTO> appliedFunctionDTOs = null;
        if (Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            appliedFunctionDTOs = staffAdditionalInfoDTO.getUnitPosition().getAppliedFunctions();
        }

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
        Activity activity = activityRepository.findActivityByIdAndEnabled(shift.getActivityId());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shift.getStaffId(), ORGANIZATION, shift.getUnitPositionId(), activity.getTimeCalculationActivityTab().getDayTypes());
        validateStaffingLevel(shift, activity, false, staffAdditionalInfoDTO);
        shift.setDeleted(true);
        save(shift);
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);

        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK));
        applicationContext.publishEvent(new ShiftNotificationEvent(shift.getUnitId(), DateUtils.onlyDate(shift.getStartDate()), shift,
                false, null, isShiftForPreence, true, false, false));

    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }

    public void validateShiftWithActivity(Activity activity, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {

        Phase phase = phaseService.getPhaseCurrentByUnit(shift.getUnitId(), shift.getStartDate());
        logger.info("Current phase is " + phase.getName() + " for date " + new DateTime(shift.getStartDate()));


        ActivitySpecification<Activity> activitySkillSpec = new StaffAndActivitySkillSpecification(staffAdditionalInfoDTO.getSkills());

        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.position");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreement()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.wta.notFound");
        }
        ActivitySpecification<Activity> activityEmploymentTypeSpecification = new ActivityEmploymentTypeSpecification(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType());
        ActivitySpecification<Activity> activityExpertiseSpecification = new ActivityExpertiseSpecification(staffAdditionalInfoDTO.getUnitPosition().getExpertise());
        ActivitySpecification<Activity> activityWTARulesSpecification = new ActivityWTARulesSpecification(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreement(), phase, shift, staffAdditionalInfoDTO);

        ActivitySpecification<Activity> activitySpecification = activityEmploymentTypeSpecification.and(activityExpertiseSpecification).and(activitySkillSpec).and(activityWTARulesSpecification);

        //TODO Pradeep will look into dayType/shift/staff/


//        List<Long> dayTypeIds = activity.getRulesActivityTab().getDayTypes();
//        if (dayTypeIds != null) {
//            List<DayType> dayTypes = countryRestClient.getDayTypes(dayTypeIds);
//            ActivitySpecification<Activity> activityDayTypeSpec = new ActivityDayTypeSpecification(dayTypes, shift.getStartDate());
//            activitySpecification.and(activityDayTypeSpec);
//        }

        activitySpecification.isSatisfied(activity);


    }

    private void validateStaffingLevel(Shift shift, Activity activity, boolean checkOverStaffing, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {

        if (activity.getRulesActivityTab().isEligibleForStaffingLevel()) {
            Phase phase = phaseService.getPhaseCurrentByUnit(shift.getUnitId(), shift.getStartDate());
            PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
            if (!Optional.ofNullable(phaseSettings).isPresent()) {
                exceptionService.dataNotFoundException("message.phaseSettings.absent");
            }
            if (!phaseSettings.isManagementEligibleForOverStaffing() || !phaseSettings.isManagementEligibleForUnderStaffing() || !phaseSettings.isStaffEligibleForOverStaffing() || !phaseSettings.isStaffEligibleForUnderStaffing()) {
                Date startDate1 = DateUtils.getDateByZoneDateTime(DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS));
                Date endDate1 = DateUtils.getDateByZoneDateTime(DateUtils.getZoneDateTime(shift.getEndDate()).truncatedTo(ChronoUnit.DAYS));
                List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndCurrentDate(shift.getUnitId(), startDate1, endDate1);
                if (!Optional.ofNullable(staffingLevels).isPresent() || staffingLevels.isEmpty()) {
                    exceptionService.actionNotPermittedException("message.staffingLevel.absent");
                }
                List<Shift> shifts = shiftMongoRepository.findShiftBetweenDuration(shift.getStartDate(), shift.getEndDate(), shift.getUnitId());
                boolean isUpdated = !checkOverStaffing ? shifts.removeIf(s -> s.getId().equals(shift.getId())) : shifts.add(shift);
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
                            if ((checkOverStaffing && totalCount > 0)) {
                                if ((checkForStaff && checkForManagement) && (!phaseSettings.isManagementEligibleForOverStaffing() && !phaseSettings.isStaffEligibleForOverStaffing())) {
                                    exceptionService.actionNotPermittedException("message.shift.overStaffing");
                                } else if (checkForStaff ? !phaseSettings.isStaffEligibleForOverStaffing() : !phaseSettings.isManagementEligibleForOverStaffing()) {
                                    exceptionService.actionNotPermittedException("message.shift.overStaffing");
                                }
                            }
                            if (!checkOverStaffing && checkForUnderStaffing && totalCount < 0) {
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
        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), null);
        WTAResponseDTO wtaResponseDTO = wtaService.getWta(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setWorkingTimeAgreement(wtaResponseDTO);
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.shift.id", shiftDTO.getStaffId());
        }
        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDTO.getActivityId());
        }
        shift = shiftDTO.buildShift();
        shift.setUnitId(unitId);
        validateShiftWithActivity(activity, shift, staffAdditionalInfoDTO);
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
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
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
        List<Shift> shifts = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftPublishDTO.getShiftIds());

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


    public ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, Date startDate, Date endDate) {
        List<ShiftQueryResult> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, startDate, endDate);

        List<OpenShift> openShifts = openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate, endDate);
        UserAccessRoleDTO userAccessRoleDTO = genericIntegrationService.getAccessRolesOfStaff(unitId);
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
        if (userAccessRoleDTO.getManagement()) {
            roles.add(AccessGroupRole.MANAGEMENT);
        }
        if (userAccessRoleDTO.getStaff()) {
            roles.add(AccessGroupRole.STAFF);
        }
        StaffAccessRoleDTO staffAccessRoleDTO = new StaffAccessRoleDTO(userAccessRoleDTO.getStaffId(), roles);
        return new ShiftWrapper(assignedShifts, openShiftResponseDTOS, staffAccessRoleDTO);
    }

    public CopyShiftResponse copyShifts(Long unitId, CopyShiftDTO copyShiftDTO) {
        List<DateWiseShiftResponse> shifts = shiftMongoRepository.findAllByIdGroupByDate(copyShiftDTO.getShiftIds());
        if (!Optional.ofNullable(shifts).isPresent() || shifts.isEmpty()) {

        }
        Set<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getShifts().stream().map(ss -> ss.getActivityId())).collect(Collectors.toSet());

        List<Activity> activities = activityRepository.findAllActivitiesByIds(activityIds);

        List<StaffUnitPositionDetails> staffDataList = restClient.getStaffsUnitPosition(unitId, copyShiftDTO.getStaffIds(), copyShiftDTO.getExpertiseId());
        Set<BigInteger> wtaIds = staffDataList.parallelStream().map(wta -> wta.getWorkingTimeAgreementId()).collect(Collectors.toSet());

        List<WorkingTimeAgreement> workingTimeAgreements = wtaService.findAllByIdAndDeletedFalse(wtaIds);
        List<Phase> phases = phaseService.getAllPhasesOfUnit(unitId);
        List<BreakSettings> breakSettings = breakSettingMongoRepository.findAllByUnitIdAndDeletedFalseOrderByCreatedAtAsc(unitId);
        activities.addAll(activityRepository.findAllByUnitIdAndNameInIgnoreCaseAndDeletedFalse(unitId, PAID_BREAK, UNPAID_BREAK));

        Integer unCopiedShiftCount = 0;
        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();

        for (Long currentStaffId : copyShiftDTO.getStaffIds()) {

            StaffUnitPositionDetails staffUnitPosition = staffDataList.parallelStream().filter(unitPosition -> unitPosition.getStaff().getId().equals(currentStaffId)).findFirst().get();
            String breakActivityForStaff = staffUnitPosition.getExpertise().getBreakPaymentSetting().equals(BreakPaymentSetting.PAID) ? PAID_BREAK : UNPAID_BREAK;
            Activity breakActivity = activities.stream().filter(activity -> activity.getName().equalsIgnoreCase(breakActivityForStaff)).findFirst().get();
            WorkingTimeAgreement workingTimeAgreement = workingTimeAgreements.stream().filter(wta -> wta.getId().equals(staffUnitPosition.getWorkingTimeAgreementId())).findAny().get();

            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffUnitPosition, activities, workingTimeAgreement, phases, copyShiftDTO, breakSettings, breakActivity);

            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffUnitPosition.getStaff(), response.get("error"));
            unCopiedShiftCount += response.get("error").size();
            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);
        }
        copyShiftResponse.setUnCopiedShiftCount(unCopiedShiftCount);

        return copyShiftResponse;
    }

    private Map<String, List<ShiftResponse>> copyForThisStaff(List<DateWiseShiftResponse> shifts, StaffUnitPositionDetails staffUnitPosition, List<Activity> activities, WorkingTimeAgreement workingTimeAgreement, List<Phase> phases, CopyShiftDTO copyShiftDTO, List<BreakSettings> breakSettings, Activity breakActivity) {

        List<Shift> newShifts = new ArrayList<>(shifts.size());
        Map<String, List<ShiftResponse>> statusMap = new HashMap<>();
        List<ShiftResponse> successfullyCopiedShifts = new ArrayList<>();
        List<ShiftResponse> errorInCopyingShifts = new ArrayList<>();
        int counter = 0;
        LocalDate shiftCreationDate = copyShiftDTO.getStartDate();
        LocalDate shiftCreationLastDate = copyShiftDTO.getEndDate();
        ShiftResponse shiftResponse = null;

        while (shiftCreationLastDate.isAfter(shiftCreationDate) || shiftCreationLastDate.equals(shiftCreationDate)) {
            DateWiseShiftResponse dateWiseShiftResponse = shifts.get(counter);
            for (Shift sourceShift : dateWiseShiftResponse.getShifts()) {
                BigInteger activityId = sourceShift.getActivityId();
                Activity currentActivity = activities.parallelStream().filter(activity -> activity.getId().equals(activityId)).findAny().get();
                List<String> validationMessages = validateShiftWhileCopy(currentActivity, staffUnitPosition, workingTimeAgreement, phases, copyShiftDTO);
                shiftResponse = addShift(validationMessages, sourceShift, staffUnitPosition, shiftCreationDate, newShifts, breakActivity, breakSettings, currentActivity);
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
        if (!shifts.isEmpty()) save(newShifts);
        return statusMap;
    }

    private ShiftResponse addShift(List<String> responseMessages, Shift sourceShift, StaffUnitPositionDetails staffUnitPosition, LocalDate shiftCreationFirstDate, List<Shift> newShifts, Activity breakActivity, List<BreakSettings> breakSettings, Activity activity) {
        if (responseMessages.isEmpty()) {
            Long shiftDurationInMinute = (sourceShift.getEndDate().getTime() - sourceShift.getStartDate().getTime()) / ONE_MINUTE;

            Shift copiedShift = new Shift(sourceShift.getName(), DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getStartDate())), DateUtils.getDateByLocalDateAndLocalTime(shiftCreationFirstDate, DateUtils.asLocalTime(sourceShift.getEndDate())),
                    sourceShift.getRemarks(), sourceShift.getActivityId(), staffUnitPosition.getStaff().getId(), sourceShift.getPhase(), sourceShift.getUnitId(),
                    sourceShift.getScheduledMinutes(), sourceShift.getDurationMinutes(), sourceShift.isMainShift(), sourceShift.getExternalId(), staffUnitPosition.getId(), sourceShift.getShiftState(), sourceShift.getParentOpenShiftId(), sourceShift.getAllowedBreakDurationInMinute(), sourceShift.getId());
            if (activity.getRulesActivityTab().isBreakAllowed()) {
                Set<BigInteger> breakShiftIds = addBreakInCopiedShifts(copiedShift, breakActivity, breakSettings, shiftDurationInMinute);
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

    public List<String> validateShiftWhileCopy(Activity activity, StaffUnitPositionDetails staffUnitPositionDetails, WorkingTimeAgreement workingTimeAgreement, List<Phase> phases, CopyShiftDTO copyShiftDTO) {
        Phase phase = phaseService.getCurrentPhaseInUnitByDate(phases, DateUtils.asDate(copyShiftDTO.getStartDate()));
        ActivitySpecification<Activity> activityEmploymentTypeSpecification = new ActivityEmploymentTypeSpecification(staffUnitPositionDetails.getEmploymentType());
        ActivitySpecification<Activity> activityExpertiseSpecification = new ActivityExpertiseSpecification(staffUnitPositionDetails.getExpertise());

        ActivitySpecification<Activity> activitySpecification = activityEmploymentTypeSpecification.and(activityExpertiseSpecification);
        List<String> messages = activitySpecification.isSatisfiedString(activity);
        return messages;

    }

    private Set<BigInteger> addBreakInCopiedShifts(Shift mainShift, Activity breakActivity, List<BreakSettings> breakSettings, Long shiftDurationInMinute) {
        logger.info("ShiftDurationInMinute = {}", shiftDurationInMinute);
        Long startDateMillis = mainShift.getStartDate().getTime();
        Long endDateMillis = null;
        Long breakAllowedAfterMinute = 0L;
        Long allowedBreakDurationInMinute = 0L;
        Long totalBreakAllotedInMinute = 0L;
        String lastItemAdded = null;
        List<Shift> shifts = new ArrayList<>();
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>();
        for (int i = 0; i < breakSettings.size(); i++) {

            /**
             * The first eligible break hours after.It specifies you can take first break after this duration
             **/
            breakAllowedAfterMinute = breakSettings.get(i).getShiftDurationInMinute();
            if (shiftDurationInMinute > breakAllowedAfterMinute) {
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
                    totalBreakAllotedInMinute += allowedBreakDurationInMinute;
                    startDateMillis = endDateMillis;
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
            shifts.add(getShiftObject(mainShift, mainShift.getName(), mainShift.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));
            shiftDurationInMinute = shiftDurationInMinute - breakAllowedAfterMinute;
            lastItemAdded = SHIFT;
            if (shiftDurationInMinute >= allowedBreakDurationInMinute) {
                startDateMillis = endDateMillis;
                endDateMillis = endDateMillis + (allowedBreakDurationInMinute * ONE_MINUTE);
                shifts.add(getShiftObject(mainShift, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));
                // reduce the shift
                shiftDurationInMinute = shiftDurationInMinute - ((endDateMillis - startDateMillis) / ONE_MINUTE);
                totalBreakAllotedInMinute += allowedBreakDurationInMinute;
                lastItemAdded = BREAK;
            } else {
                logger.info("Remaining shift duration " + shiftDurationInMinute + " And we need to add break for {} ", allowedBreakDurationInMinute);
                // add break and increase main shift duration by remaining minute
            }

        }
        // Sometimes the break is
        if (shiftDurationInMinute > 0 && shiftDurationInMinute < breakAllowedAfterMinute && lastItemAdded == SHIFT) {
            // handle later
            startDateMillis = endDateMillis;
            endDateMillis = endDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject(mainShift, breakActivity.getName(), breakActivity.getId(), new Date(startDateMillis), new Date(endDateMillis), allowedBreakDurationInMinute));
            totalBreakAllotedInMinute += allowedBreakDurationInMinute;

        } else if (shiftDurationInMinute > 0 && shiftDurationInMinute < breakAllowedAfterMinute && lastItemAdded == BREAK) {
            startDateMillis = endDateMillis;
            endDateMillis = startDateMillis + (shiftDurationInMinute * ONE_MINUTE);
            shifts.add(getShiftObject(mainShift, mainShift.getName(), mainShift.getActivityId(), new Date(startDateMillis), new Date(endDateMillis), null));

        }
        if (breakActivity.getName().equalsIgnoreCase(UNPAID_BREAK)) {
            mainShift.setScheduledMinutes(mainShift.getScheduledMinutes() - totalBreakAllotedInMinute.intValue());
        }
        if (!shifts.isEmpty())
            save(shifts);
        shifts.stream().map(Shift::getId).collect(Collectors.toSet());
        return shifts.stream().map(Shift::getId).collect(Collectors.toSet());
    }

    private Shift getShiftObject(Shift shift, String name, BigInteger activityId, Date startDate, Date endDate, Long allowedBreakDurationInMinute) {
        Shift childShift = new Shift(null, name, startDate, endDate, shift.getBid(), shift.getpId(), shift.getBonusTimeBank()
                , shift.getAmount(), shift.getProbability(), shift.getAccumulatedTimeBankInMinutes(), shift.getRemarks(), activityId, shift.getStaffId(), shift.getUnitId(), shift.getUnitPositionId());
        childShift.setShiftState(ShiftState.UNPUBLISHED);
        childShift.setMainShift(false);
        childShift.setAllowedBreakDurationInMinute(allowedBreakDurationInMinute);
        return childShift;

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