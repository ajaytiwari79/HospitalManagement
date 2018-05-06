package com.kairos.activity.service.activity;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.response.dto.shift.ShiftQueryResult;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.pay_out.PayOutService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.time_bank.TimeBankService;
import com.kairos.activity.service.wta.WTAService;
import com.kairos.activity.spec.*;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.event.ShiftNotificationEvent;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.activity.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.activity.constants.AppConstants.FULL_WEEK;
import static com.kairos.activity.util.DateUtils.MONGODB_QUERY_DATE_FORMAT;

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
    @Inject private WTAService wtaService;

    public List<ShiftQueryResult> createShift(Long organizationId, ShiftDTO shiftDTO, String type, boolean bySubShift) {
        /*boolean valid=staffingLevelMongoRepository.existsByUnitIdAndCurrentDateAndDeletedFalse(UserContext.getUnitId(),shiftStartDate);
        if(!valid){
            throw new DataNotFoundByIdException("Staffing level not found for this Day");
*/
        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid activity Id ." + shiftDTO.getActivityId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), activity.getTimeCalculationActivityTab().getDayTypes());
        WTAResponseDTO wtaResponseDTO = wtaService.getWta(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setWorkingTimeAgreement(wtaResponseDTO);
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            throw new DataNotFoundByIdException(shiftDTO.getStaffId() + " Staff Do not belong to unit ->" + shiftDTO.getUnitId());
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
            List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPosition(shiftDTO.getUnitPositionId(),shiftDTO.getStartDate(),shiftDTO.getEndDate());
            if(!shifts.isEmpty()) {
                throw new DuplicateDataException(" Shift already exists between duration- startDate: "+shifts.get(0).getStartDate()+" endDate: "+shifts.get(0).getEndDate());
            }

            if (shiftDTO.getStartDate().after(shiftDTO.getEndDate())) {
                throw new InvalidRequestException(" Start date can't be greater than endDate");
            }
            ShiftQueryResult shiftQueryResult = saveShift(activity, staffAdditionalInfoDTO, shiftDTO);
            shiftQueryResults = Arrays.asList(shiftQueryResult);
        }
        return shiftQueryResults;
    }

    private ShiftQueryResult saveShift(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO shiftDTO) {
        Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getStartDate());
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        Shift shift = shiftDTO.buildShift();
        shift.setMainShift(true);
        shift.setName(activity.getName());
        validateShiftWithActivity(activity, shift, staffAdditionalInfoDTO);
        List<Integer> activityDayTypes = new ArrayList<>();
        if(staffAdditionalInfoDTO.getActivityDayTypes()!=null && !staffAdditionalInfoDTO.getActivityDayTypes().isEmpty()) {
            activityDayTypes = staffAdditionalInfoDTO.getActivityDayTypes().stream().map(ad -> ad.getValue()).collect(Collectors.toList());
        }
        if (activityDayTypes.contains(new DateTime(shiftDTO.getStartDate()).getDayOfWeek())) {
            timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
        }
        save(shift);
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);


        //anil m2 notify event for updating staffing level
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_DAY")||activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_WEEK"));

                applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift, false, null,isShiftForPreence));
        return shift.getShiftQueryResult();
    }


    private List<ShiftQueryResult> saveShifts(Activity activity, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ShiftDTO> shiftDTOS) {
        List<Shift> shifts = new ArrayList<>(shiftDTOS.size());
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>(shiftDTOS.size());
        List<Integer> activityDayTypes = new ArrayList<>();
        if(staffAdditionalInfoDTO.getActivityDayTypes()!=null && !staffAdditionalInfoDTO.getActivityDayTypes().isEmpty()) {
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
            //timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
            boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_DAY")||activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_WEEK"));

            applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift, false, null,isShiftForPreence));
        }
        save(shifts);
        timeBankService.saveTimeBanks(staffAdditionalInfoDTO.getUnitPosition().getId(), shifts);
        payOutService.savePayOuts(staffAdditionalInfoDTO.getUnitPosition().getId(), shifts);
        shifts.stream().forEach(s -> shiftQueryResults.add(s.getShiftQueryResult()));
        return shiftQueryResults;
    }


    public ShiftQueryResult updateShift(Long organizationId, ShiftDTO shiftDTO, String type) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), null);
        WTAResponseDTO wtaResponseDTO = wtaService.getWta(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreementId());
        staffAdditionalInfoDTO.getUnitPosition().setWorkingTimeAgreement(wtaResponseDTO);
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            throw new DataNotFoundByIdException(shiftDTO.getStaffId() + " Staff Do not belong to unit ->" + shiftDTO.getUnitId());
        }

        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid activity  Id : " + shiftDTO.getActivityId());
        }

        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            throw new DataNotFoundByIdException("Invalid activity  Id : " + shiftDTO.getId());
        }
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
        shift.setStartDate(shiftDTO.getStartDate());
        shift.setEndDate(shiftDTO.getEndDate());
        shift.setName(activity.getName());
        shift.setDurationMinutes(shiftDTO.getDurationMinutes());

        validateShiftWithActivity(activity, shift, staffAdditionalInfoDTO);
        timeBankCalculationService.calculateScheduleAndDurationHour(shift, activity, staffAdditionalInfoDTO.getUnitPosition());
        save(shift);
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);
        Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
        //anil m2 notify event for updating staffing level
        boolean isShiftForPreence = !(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_DAY")||activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals("FULL_WEEK"));
        applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift,
                true, oldStateOfShift,isShiftForPreence));
        ShiftQueryResult shiftQueryResult = shift.getShiftQueryResult();
        shiftQueryResult.setName(activity.getName());
        shiftQueryResult.setDurationMinutes(shift.getDurationMinutes());
        shiftQueryResult.setScheduledMinutes(shift.getScheduledMinutes());
        return shiftQueryResult;
    }

    public List<ShiftQueryResult> getShiftByStaffId(Long id, Long staffId, String startDateAsString, String endDateAsString, Long week,Long unitPositionId, String type) throws ParseException {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, type);
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent() || staffAdditionalInfoDTO.getUnitId() == null) {
            throw new DataNotFoundByIdException(staffId + " Staff Do not belong to " + type);
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
        List<ShiftQueryResult> activities = shiftMongoRepository.findAllActivityBetweenDuration(unitPositionId,staffId, startDateInISO, endDateInISO, staffAdditionalInfoDTO.getUnitId());
        activities.stream().map(s -> s.sortShifts()).collect(Collectors.toList());
        return activities;
    }

    public void deleteShift(BigInteger shiftId) {
        Shift shift = shiftMongoRepository.findOne(shiftId);
        if (!Optional.ofNullable(shift).isPresent()) {
            throw new DataNotFoundByIdException("Invalid shift  Id : " + shiftId);
        }
        shift.setDeleted(true);
        save(shift);
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
        payOutService.savePayOut(shift.getUnitPositionId(), shift);
    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }

    public void validateShiftWithActivity(Activity activity, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {

        Phase phase = phaseService.getPhaseCurrentByUnit(shift.getUnitId(), shift.getStartDate());
        logger.info("Current phase is " + phase.getName() + " for date " + new DateTime(shift.getStartDate()));

        ActivitySpecification<Activity> activitySkillSpec = new StaffAndActivitySkillSpecification(staffAdditionalInfoDTO.getSkills());

        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition()).isPresent()) {
            throw new DataNotFoundByIdException("Unit Position not found");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreement()).isPresent()) {
            throw new DataNotFoundByIdException("WTA NOT found");
        }
        ActivitySpecification<Activity> activityEmploymentTypeSpecification = new ActivityEmploymentTypeSpecification(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType());
        ActivitySpecification<Activity> activityExpertiseSpecification = new ActivityExpertiseSpecification(staffAdditionalInfoDTO.getUnitPosition().getExpertise());
        ActivitySpecification<Activity> activityWTARulesSpecification = new ActivityWTARulesSpecification(staffAdditionalInfoDTO.getUnitPosition().getWorkingTimeAgreement(), phase, shift, staffAdditionalInfoDTO);

        ActivitySpecification<Activity> activitySpecification = activityEmploymentTypeSpecification.and(activityExpertiseSpecification).and(activitySkillSpec).and(activityWTARulesSpecification); //

        List<Long> dayTypeIds = activity.getRulesActivityTab().getDayTypes();
        if (dayTypeIds != null) {
            List<DayType> dayTypes = countryRestClient.getDayTypes(dayTypeIds);
            ActivitySpecification<Activity> activityDayTypeSpec = new ActivityDayTypeSpecification(dayTypes, shift.getStartDate());
            activitySpecification.and(activityDayTypeSpec);
        }

        activitySpecification.isSatisfied(activity);


    }

    public ShiftQueryResult addSubShift(Long unitId, ShiftDTO shiftDTO, String type) {
        Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
        if (!Optional.ofNullable(shift).isPresent()) {
            throw new DataNotFoundByIdException("Invalid shift id : " + shiftDTO.getId());
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId(), null);
        if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
            throw new DataNotFoundByIdException("Invalid staff id : " + shiftDTO.getStaffId());
        }
        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid activity id : " + shiftDTO.getActivityId());
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
                    throw new InvalidRequestException("incorrect start date or end date of {}" + (i - 1) + "shift data.");
                }
            } else {
                if ((!parentShiftEndDateTime.equals(subShifts.getStartDate())) || (shiftDTO.getEndDate().before(subShifts.getEndDate()))) {
                    logger.info("start " + (parentShiftStartDateTime) + "-" + subShifts.getStartDate()
                            + "end " + (parentShiftEndDateTime) + "-" + subShifts.getEndDate() + "shift data");
                    throw new InvalidRequestException("incorrect start date or end date of {}" + (i - 1) + "shift data");
                }
            }
            // making the calculating the previous  object as parent
            parentShiftEndDateTime = subShifts.getEndDate();
        }
    }

    private List<Shift> verifyCompositeShifts(ShiftDTO shiftDTO, BigInteger shiftId) {
        if (shiftDTO.getSubShifts().size() == 0) {
            throw new InvalidRequestException("Unable to create sub-shift. Incorrect data");
        }
        Activity activity = activityRepository.findOne(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent() || !Optional.ofNullable(activity.getCompositeActivities()).isPresent()) {
            throw new DataNotFoundByIdException("Unable to create sub shift on this activity");
        }
        validateTimingOfShifts(shiftDTO);
        List<ShiftDTO> subShiftDTOS = shiftDTO.getSubShifts();

        Set<BigInteger> activityIds = subShiftDTOS.parallelStream().map(ShiftDTO::getActivityId).collect(Collectors.toSet());

        activity.getCompositeActivities().add(shiftDTO.getActivityId());
        if (!activity.getCompositeActivities().containsAll(activityIds)) {
            throw new InvalidRequestException("All Activities doesn't support multi shift");
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
        for (int day =0;day<7; day++) {
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
            throw new ActionNotPermittedException("Shifts Already Exists in this interval");
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
                throw new ActionNotPermittedException("Shifts Already Exists in this interval");
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

}