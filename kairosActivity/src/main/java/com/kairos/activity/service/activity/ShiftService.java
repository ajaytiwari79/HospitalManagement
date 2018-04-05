package com.kairos.activity.service.activity;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.client.dto.staff.UnitPositionDTO;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.response.dto.shift.ShiftQueryResult;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.time_bank.TimeBankService;
import com.kairos.activity.spec.*;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.event.ShiftNotificationEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.Weeks;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private StaffingLevelMongoRepository staffingLevelMongoRepository;

    public ShiftQueryResult createShift(Long organizationId, ShiftDTO shiftDTO, String type) {
        Date shiftStartDate = DateUtils.onlyDate(shiftDTO.getStartDate());
        /*boolean valid=staffingLevelMongoRepository.existsByUnitIdAndCurrentDateAndDeletedFalse(UserContext.getUnitId(),shiftStartDate);
        if(!valid){
            throw new DataNotFoundByIdException("Staffing level not found for this Day");
*/
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            throw new DataNotFoundByIdException(shiftDTO.getStaffId() + " Staff Do not belong to unit ->" + shiftDTO.getUnitId());
        }
        shiftDTO.setStartDate(shiftDTO.getStartDate());
        shiftDTO.setEndDate(shiftDTO.getEndDate());

        Activity activity = activityRepository.findActivityByIdAndEnabled(shiftDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            throw new DataNotFoundByIdException("Invalid activity Id ." + shiftDTO.getActivityId());
        }
        shiftDTO.setUnitId(staffAdditionalInfoDTO.getUnitId());
        Shift shift = shiftDTO.buildShift();

        if (shift.getStartDate().after(shift.getEndDate())) {
            throw new InvalidRequestException(" Start date can't be greater than endDate");
        }

        shift.setMainShift(true);
        shift.setName(activity.getName());
        validateShiftWithActivity(activity, shift, staffAdditionalInfoDTO);


        save(shift);
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);

        //anil m2 notify event for updating staffing level
        applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift, false, null));
        ShiftQueryResult shiftQueryResult = shiftDTO.buildResponse();
        shiftQueryResult.setId(shift.getId());
        shiftQueryResult.setName(shift.getName());

        return shiftQueryResult;
    }

    public ShiftQueryResult updateShift(Long organizationId, ShiftDTO shiftDTO, String type) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
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

        validateShiftWithActivity(activity, shift, staffAdditionalInfoDTO);

        save(shift);
        timeBankService.saveTimeBank(shift.getUnitPositionId(), shift);
        Date shiftStartDate = DateUtils.onlyDate(shift.getStartDate());
        //anil m2 notify event for updating staffing level
        applicationContext.publishEvent(new ShiftNotificationEvent(staffAdditionalInfoDTO.getUnitId(), shiftStartDate, shift,
                true, oldStateOfShift));
        ShiftQueryResult shiftQueryResult = shiftDTO.buildResponse();
        shiftQueryResult.setName(activity.getName());

        return shiftQueryResult;
    }

    public List<ShiftQueryResult> getShiftByStaffId(Long id, Long staffId, String startDateAsString, String endDateAsString, Long week, String type) throws ParseException {
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
        List<ShiftQueryResult> activities = shiftMongoRepository.findAllActivityBetweenDuration(staffId, startDateInISO, endDateInISO, staffAdditionalInfoDTO.getUnitId());
        activities.stream().map(s->s.sortShifts()).collect(Collectors.toList());
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
    }

    public Long countByActivityId(BigInteger activityId) {
        return shiftMongoRepository.countByActivityId(activityId);
    }

    public void validateShiftWithActivity(Activity activity, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {

        Phase phase = phaseService.getPhaseCurrentByUnit(shift.getUnitId(), shift.getStartDate());
        logger.info("Current phase is " + phase.getName() + " for date " + new DateTime(shift.getStartDate()));

        ActivitySpecification<Activity> activitySkillSpec = new StaffAndActivitySkillSpecification(staffAdditionalInfoDTO.getSkills());

        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitEmploymentPosition()).isPresent()) {
            throw new DataNotFoundByIdException("UEP not created");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getUnitEmploymentPosition().getWorkingTimeAgreement()).isPresent()) {
            throw new DataNotFoundByIdException("WTA NOT found");
        }
        ActivitySpecification<Activity> activityEmploymentTypeSpecification = new ActivityEmploymentTypeSpecification(staffAdditionalInfoDTO.getUnitEmploymentPosition().getEmploymentType());
        ActivitySpecification<Activity> activityExpertiseSpecification = new ActivityExpertiseSpecification(staffAdditionalInfoDTO.getUnitEmploymentPosition().getExpertise());
        ActivitySpecification<Activity> activityWTARulesSpecification = new ActivityWTARulesSpecification(staffAdditionalInfoDTO.getUnitEmploymentPosition().getWorkingTimeAgreement(), phase, shift, staffAdditionalInfoDTO);

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
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(shiftDTO.getStaffId(), type, shiftDTO.getUnitPositionId());
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
            shiftQueryResult = shiftDTO.buildResponse();
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
            ShiftQueryResult shiftQueryResult = createShift(unitId, shiftDTO, "Organization");
            shiftDTO.setId(shiftQueryResult.getId());
        }

        shiftDTOS.forEach(shiftDTO -> {
            if (shiftDTO.getSubShifts() != null && !shiftDTO.getSubShifts().isEmpty()) {
                addSubShift(unitId, shiftDTO, type);
            }
        });
        return true;
    }

    public List<ShiftDTO> getAverageOfShiftByActivity(Long unitId,Long staffId,String activityId, Date fromDate,String type,Long unitPositionId){
        Activity activity = activityRepository.findActivityByIdAndEnabled(new BigInteger(activityId));
        Date endDate = fromDate;
        Date startDate = new DateTime(endDate).minusWeeks(activity.getTimeCalculationActivityTab().getHistoryDuration()).toDate();
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRestClient.verifyUnitEmploymentOfStaff(staffId, type,unitPositionId);
        List<ShiftQueryResult> shifts = shiftMongoRepository.findAllShiftBetweenDuration(staffAdditionalInfoDTO.getUnitEmploymentPosition().getId(),startDate,endDate);
        int contractualMinutesInADay = staffAdditionalInfoDTO.getUnitEmploymentPosition().getTotalWeeklyHours()*60/staffAdditionalInfoDTO.getUnitEmploymentPosition().getWorkingDaysInWeek();
        List<ShiftDTO> shiftDTOS = new ArrayList<>(7);
        IntStream.range(DateTimeConstants.MONDAY,DateTimeConstants.SUNDAY+1).forEachOrdered(day->{
            logger.info("day "+day);
            if(day<=staffAdditionalInfoDTO.getUnitEmploymentPosition().getWorkingDaysInWeek() || staffAdditionalInfoDTO.getUnitEmploymentPosition().getWorkingDaysInWeek()==7) {
                ShiftDTO shiftDTO = new ShiftDTO(new BigInteger(activityId), unitId, staffId, staffAdditionalInfoDTO.getUnitEmploymentPosition().getId());
                if (shifts != null && !shifts.isEmpty()) {
                    Integer startAverageMin = getStartAverage(new DateTime(endDate).plusDays(day-1).getDayOfWeek(), shifts);
                    if(startAverageMin!=null) {
                        DateTime startDateTime = new DateTime(endDate).plusDays(day-1).withTimeAtStartOfDay().plusMinutes(startAverageMin);
                        shiftDTO.setStartDate(startDateTime.toDate());
                        shiftDTO.setEndDate(startDateTime.plusMinutes(contractualMinutesInADay).toDate());
                        shiftDTOS.add(shiftDTO);
                    }
                }

            }
        });
        return shiftDTOS;
    }

    public Integer getStartAverage(int day, List<ShiftQueryResult> shifts){
        List<ShiftQueryResult> updatedShifts = shifts.stream().filter(s->new DateTime(s.getStartDate()).getDayOfWeek()==day).collect(Collectors.toList());
        updatedShifts = getFilteredShiftsByStartTime(updatedShifts);
        Integer startAverageMin = null;
        if(updatedShifts!=null && !updatedShifts.isEmpty()){
             startAverageMin = updatedShifts.stream().mapToInt(s->new DateTime(s.getStartDate()).getMinuteOfDay()).sum()/updatedShifts.size();
        }
        return startAverageMin;
    }

    public List<ShiftQueryResult> getFilteredShiftsByStartTime(List<ShiftQueryResult> shifts){
        shifts.sort((s1,s2)->s1.getStartDate().compareTo(s2.getStartDate()));
        List<ShiftQueryResult> shiftQueryResults = new ArrayList<>();
        LocalDate localDate = null;
        for (ShiftQueryResult shift : shifts) {
            if(!DateUtils.asLocalDate(new Date(shift.getStartDate())).equals(localDate)) {
                localDate = DateUtils.asLocalDate(new Date(shift.getStartDate()));
                shiftQueryResults.add(shift);
            }
        }
        return shiftQueryResults;
    }

}
