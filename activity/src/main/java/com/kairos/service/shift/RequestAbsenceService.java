package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.*;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.todo.TodoService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;

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
    @Inject private TodoService todoService;
    @Inject private ShiftDetailsService shiftDetailsService;


    public List<ShiftWithActivityDTO> createOrUpdateRequestAbsence(RequestAbsenceDTO requestAbsenceDTO){
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(requestAbsenceDTO.getShiftId());
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,requestAbsenceDTO.getShiftId());
        }
        if(isNotNull(shiftOptional.get().getRequestAbsence()) && shiftOptional.get().getRequestAbsence().getTodoStatus().equals(TodoStatus.APPROVE)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_APPROVED);
        }
        TimeTypeEnum timeTypeEnum = activityMongoRepository.findTimeTypeByActivityId(requestAbsenceDTO.getActivityId());
        if(!timeTypeEnum.equals(TimeTypeEnum.ABSENCE)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_ACTIVITY_TYPE);
        }
        RequestAbsence requestAbsence = copyPropertiesByMapper(requestAbsenceDTO,RequestAbsence.class);
        Activity activity = activityMongoRepository.findOne(requestAbsence.getActivityId());
        requestAbsence.setActivityName(activity.getName());
        Shift shift = shiftOptional.get();
        shift.setRequestAbsence(requestAbsence);
        shiftMongoRepository.save(shift);
        todoService.createOrUpdateTodo(shift, TodoType.REQUEST_ABSENCE,null,true);
        return shiftDetailsService.shiftDetailsById(shift.getUnitId(), newArrayList(shift.getId()), false);
    }

    public Long deleteRequestAbsence(BigInteger shiftId){
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(shiftId);
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,shiftId);
        }
        shiftOptional.get().setRequestAbsence(null);
        shiftMongoRepository.save(shiftOptional.get());
        return todoService.deleteTodo(shiftId,TodoType.REQUEST_ABSENCE);
    }

    public ShiftWithViolatedInfoDTO approveRequestAbsence(Todo todo){
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(todo.getEntityId());
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,todo.getEntityId());
        }
        if(isNull(shiftOptional.get().getRequestAbsence())){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_APPROVED);
        }
        Shift shift = shiftOptional.get();
        ActivityWrapper activityWrapper = activityMongoRepository.findActivityAndTimeTypeByActivityId(todo.getSubEntityId());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getEmploymentId(), new HashSet<>());
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = null;
        if(CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())){
            Date startDate = getStartOfDay(shift.getStartDate());
            Date endDate = CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? asDate(asZoneDateTime(shift.getStartDate()).plusWeeks(1).plusDays(1).truncatedTo(ChronoUnit.DAYS)) : asDate(asZoneDateTime(shift.getStartDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS));
            ShiftDTO shiftDTO = new ShiftDTO(asLocalDate(startDate),newArrayList(new ShiftActivityDTO(activityWrapper.getActivity().getId(),activityWrapper.getActivity().getName())),shift.getId());
            shiftDTO.setUnitId(shift.getUnitId());
            shiftWithViolatedInfoDTO = absenceShiftService.createAbsenceTypeShift(activityWrapper,shiftDTO,staffAdditionalInfoDTO, false,ShiftActionType.SAVE);
            shiftMongoRepository.deleteShiftBetweenDatesByEmploymentId(shift.getEmploymentId(),startDate,endDate,shiftWithViolatedInfoDTO.getShifts().stream().filter(shiftDTO1->isNotNull(shiftDTO1.getId())).map(shiftDTO1->shiftDTO1.getId()).collect(Collectors.toList()));
        }else {
            shiftWithViolatedInfoDTO =  updateShiftWithRequestAbsence(activityWrapper,shift,staffAdditionalInfoDTO);
        }
        if(isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements()) || isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getActivities())){
            todo.setStatus(TodoStatus.PENDING);
        }
        return shiftWithViolatedInfoDTO;
    }

    private ShiftWithViolatedInfoDTO updateShiftWithRequestAbsence(ActivityWrapper activityWrapper,Shift shift,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
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
        return null;
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
