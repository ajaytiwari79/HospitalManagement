package com.kairos.service.shift;

import com.kairos.commons.utils.*;
import com.kairos.dto.activity.shift.RequestAbsenceDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.activity.*;
import com.kairos.persistence.model.shift.*;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;

/**
 * Created by pradeep
 * Created at 13/6/19
 **/
@Service
public class RequestAbsenceService {

    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private AbsenceShiftService absenceShiftService;
    @Inject private UserIntegrationService userIntegrationService;

    public RequestAbsenceDTO createOrUpdateRequestAbsence(RequestAbsenceDTO requestAbsenceDTO){
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(requestAbsenceDTO.getShiftId());
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,requestAbsenceDTO.getActivityId());
        }
        if(isNotNull(shiftOptional.get().getRequestAbsence()) && shiftOptional.get().getRequestAbsence().getTodoStatus().equals(TodoStatus.APPROVED)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_APPROVED);
        }
        TimeTypeEnum timeTypeEnum = activityMongoRepository.findTimeTypeByActivityId(requestAbsenceDTO.getActivityId());
        if(!timeTypeEnum.equals(TimeTypeEnum.ABSENCE)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_ACTIVITY_TYPE);
        }
        RequestAbsence requestAbsence = copyPropertiesByMapper(requestAbsenceDTO,RequestAbsence.class);
        Shift shift = shiftOptional.get();
        shift.setRequestAbsence(requestAbsence);
        shiftMongoRepository.save(shift);
        return requestAbsenceDTO;
    }

    public void approveRequestAbsence(){
        BigInteger shiftId = new BigInteger("");
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(shiftId);
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,shiftId);
        }
        if(isNotNull(shiftOptional.get().getRequestAbsence()) && shiftOptional.get().getRequestAbsence().getTodoStatus().equals(TodoStatus.APPROVED)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_APPROVED);
        }
        Shift shift = shiftOptional.get();
        ActivityWrapper activityWrapper = activityMongoRepository.findActivityAndTimeTypeByActivityId(new BigInteger(""));
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getEmploymentId(), new HashSet<>());
        if(FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())){
            approveFullDayFullWeekRequestAbsence(activityWrapper,shift,staffAdditionalInfoDTO);
        }else {
            updateShiftWithRequestAbsence(activityWrapper,shift,staffAdditionalInfoDTO);
        }
    }

    private void approveFullDayFullWeekRequestAbsence(ActivityWrapper activityWrapper, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        Date startDate;
        Date endDate;
        if(FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())){
            startDate = getStartOfDay(shift.getStartDate());
            endDate = asDate(asZoneDateTime(shift.getStartDate()).plusWeeks(1).plusDays(1).truncatedTo(ChronoUnit.DAYS));
        }else{
            startDate = getStartOfDay(shift.getStartDate());
            endDate = asDate(asZoneDateTime(shift.getStartDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS));
        }
        shiftMongoRepository.deleteShiftBetweenDatesByEmploymentId(shift.getEmploymentId(),startDate,endDate);
        ShiftDTO shiftDTO = new ShiftDTO();
        absenceShiftService.createAbsenceTypeShift(activityWrapper,shiftDTO,staffAdditionalInfoDTO, false,ShiftActionType.SAVE);
    }

    private void updateShiftWithRequestAbsence(ActivityWrapper activityWrapper,Shift shift,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        RequestAbsence requestAbsence = shift.getRequestAbsence();
        DateTimeInterval dateTimeInterval = new DateTimeInterval(requestAbsence.getStartDate(),requestAbsence.getEndDate());
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            List<ShiftActivity> childActivities = new ArrayList<>();
            for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                updateShiftActivity(activityWrapper,dateTimeInterval, childActivities, childActivity);
            }
            updateShiftActivity(activityWrapper,dateTimeInterval,shiftActivities,shiftActivity);
            shiftActivity.setChildActivities(childActivities);
        }
    }

    private void updateShiftActivity(ActivityWrapper activityWrapper,DateTimeInterval dateTimeInterval, List<ShiftActivity> shiftActivities, ShiftActivity shiftActivity) {
        ShiftActivity absenceActivity = new ShiftActivity(activityWrapper.getActivity().getName(),dateTimeInterval.getStartDate(),dateTimeInterval.getEndDate(),activityWrapper.getActivity().getId(),activityWrapper.getTimeType());
        if(shiftActivity.getInterval().overlaps(dateTimeInterval)){
            List<DateTimeInterval> dateTimeIntervals = shiftActivity.getInterval().minusInterval(dateTimeInterval);
            for (DateTimeInterval timeInterval : dateTimeIntervals) {
                shiftActivities.add(new ShiftActivity(shiftActivity.getActivityName(),timeInterval.getStartDate(),timeInterval.getEndDate(),shiftActivity.getActivityId(),shiftActivity.getTimeType()));
            }
            shiftActivities.add(absenceActivity);
        }else {
            shiftActivities.add(shiftActivity);
        }
        Collections.sort(shiftActivities);
    }
}
