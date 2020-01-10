package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.RequestAbsence;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
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
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.enums.TimeTypeEnum.TIME_BANK;
import static com.kairos.enums.shift.TodoStatus.DISAPPROVE;
import static com.kairos.enums.shift.TodoStatus.PENDING;
import static org.apache.commons.collections.CollectionUtils.containsAny;

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
    @Inject private ShiftStatusService shiftStatusService;
    @Inject private PhaseService phaseService;
    @Inject private ShiftValidatorService shiftValidatorService;
    @Inject private LocaleService localeService;
    @Inject private ShiftService shiftService;


    public List<ShiftWithActivityDTO> createOrUpdateRequestAbsence(RequestAbsenceDTO requestAbsenceDTO){
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(requestAbsenceDTO.getShiftId());
        verifyRequestAbsence(requestAbsenceDTO, shiftOptional);
        RequestAbsence requestAbsence = copyPropertiesByMapper(requestAbsenceDTO,RequestAbsence.class);
        Activity activity = activityMongoRepository.findOne(requestAbsence.getActivityId());
        requestAbsence.setActivityName(activity.getName());
        Shift shift = shiftOptional.get();
        shift.setRequestAbsence(requestAbsence);
        shiftMongoRepository.save(shift);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getStartDate()), shift.getStaffId(), shift.getEmploymentId(), newHashSet());
        todoService.createOrUpdateTodo(shift, TodoType.REQUEST_ABSENCE,true,staffAdditionalInfoDTO);
        return shiftDetailsService.shiftDetailsById(shift.getUnitId(), newArrayList(shift.getId()), false);
    }

    private void verifyRequestAbsence(RequestAbsenceDTO requestAbsenceDTO, Optional<Shift> shiftOptional) {
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,requestAbsenceDTO.getShiftId());
        }
        if(isNotNull(shiftOptional.get().getRequestAbsence()) && shiftOptional.get().getRequestAbsence().getTodoStatus().equals(TodoStatus.APPROVE)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_APPROVED);
        }
        TimeTypeEnum timeTypeEnum = activityMongoRepository.findTimeTypeByActivityId(requestAbsenceDTO.getActivityId());
        if(!timeTypeEnum.equals(TimeTypeEnum.ABSENCE) && !TIME_BANK.equals(timeTypeEnum)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_ACTIVITY_TYPE);
        }
        if(isNotNull(requestAbsenceDTO.getStartDate()) && isNotNull(requestAbsenceDTO.getEndDate())){
            DateTimeInterval dateTimeInterval=new DateTimeInterval(shiftOptional.get().getStartDate(),shiftOptional.get().getEndDate());
            if(!dateTimeInterval.containsInterval(new DateTimeInterval(requestAbsenceDTO.getStartDate(),requestAbsenceDTO.getEndDate()))){
                exceptionService.actionNotPermittedException(REQUEST_ABSENCE_REQUESTED);
            }
        }
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

    public <T> T approveRequestAbsence(Todo todo){
        T response = (T)todo;
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(todo.getEntityId());
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,todo.getEntityId());
        }
        ActivityWrapper activityWrapper = activityMongoRepository.findActivityAndTimeTypeByActivityId(todo.getSubEntityId());
        if(TodoStatus.APPROVE.equals(todo.getStatus())){
            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = null;
            if(isNull(shiftOptional.get().getRequestAbsence())){
                exceptionService.actionNotPermittedException(REQUEST_ABSENCE_APPROVED);
            }
            Shift shift = shiftOptional.get();
            ShiftAndActivtyStatusDTO shiftAndActivtyStatusDTO = validateAccessGroupForUpdateStatus(todo, shift);
            if(isNotNull(shiftAndActivtyStatusDTO)){
                todo.setStatus(TodoStatus.REQUESTED);
                return (T)shiftAndActivtyStatusDTO;
            }
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getStartDate()), shift.getStaffId(), shift.getEmploymentId(), new HashSet<>());
            if(CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())){
                Date startDate = getStartOfDay(shift.getStartDate());
                Date endDate = CommonConstants.FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? asDate(asZoneDateTime(shift.getStartDate()).plusWeeks(1).truncatedTo(ChronoUnit.DAYS)) : asDate(asZoneDateTime(shift.getStartDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS));
                ShiftDTO shiftDTO = new ShiftDTO(asLocalDate(startDate),newArrayList(new ShiftActivityDTO(activityWrapper.getActivity().getId(),activityWrapper.getActivity().getName(),newHashSet(ShiftStatus.REQUEST))),shift.getId());
                shiftDTO.setUnitId(shift.getUnitId());
                shiftWithViolatedInfoDTO = absenceShiftService.createAbsenceTypeShift(activityWrapper,shiftDTO,staffAdditionalInfoDTO, false,ShiftActionType.SAVE);
                shiftMongoRepository.deleteShiftBetweenDatesByEmploymentId(shift.getEmploymentId(),startDate,endDate,shiftWithViolatedInfoDTO.getShifts().stream().filter(shiftDTO1->isNotNull(shiftDTO1.getId())).map(shiftDTO1->shiftDTO1.getId()).collect(Collectors.toList()));
            }else {
                shiftWithViolatedInfoDTO =  updateShiftWithRequestAbsence(activityWrapper,shift,staffAdditionalInfoDTO);
            }
            response = updateStatusAfterUpdateShift(todo, shiftWithViolatedInfoDTO);
            shiftStatusService.sendMailToStaffWhenStatusChange(shiftOptional.get(), activityWrapper.getActivity().getName(), ShiftStatus.valueOf(todo.getStatus().toString()) , todo.getComment());
        }else if(DISAPPROVE.equals(todo.getStatus())){
            shiftOptional.get().setRequestAbsence(null);
            //todo.setDeleted(true);
            shiftMongoRepository.save(shiftOptional.get());
            shiftStatusService.sendMailToStaffWhenStatusChange(shiftOptional.get(), activityWrapper.getActivity().getName(), ShiftStatus.valueOf(todo.getStatus().toString()) , todo.getComment());
        }else if(PENDING.equals(todo.getStatus())){
            shiftStatusService.sendMailToStaffWhenStatusChange(shiftOptional.get(), activityWrapper.getActivity().getName(), ShiftStatus.valueOf(todo.getStatus().toString()) , todo.getComment());
        }
        return response;
    }

    private <T> T updateStatusAfterUpdateShift(Todo todo, ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO) {
        T response;
        Optional<Shift> shiftOptional;
        if(isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements()) || isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getActivities())){
            todo.setStatus(TodoStatus.REQUESTED);
            response = (T)shiftWithViolatedInfoDTO;
        }else {
            List<ShiftActivitiesIdDTO> shiftActivitiesIdDTOS = new ArrayList<>();
            for (ShiftDTO shiftDTO : shiftWithViolatedInfoDTO.getShifts()) {
                shiftActivitiesIdDTOS.add(new ShiftActivitiesIdDTO(shiftDTO.getId(),shiftDTO.getActivities().stream().filter(shiftActivityDTO -> !containsAny(newHashSet(ShiftStatus.APPROVE,ShiftStatus.PUBLISH),shiftActivityDTO.getStatus())).map(shiftActivityDTO -> shiftActivityDTO.getId()).collect(Collectors.toList())));
            }
            response = (T)shiftStatusService.updateStatusOfShifts(todo.getUnitId(), new ShiftPublishDTO(shiftActivitiesIdDTOS,ShiftStatus.APPROVE,todo.getComment()));
            shiftOptional = shiftMongoRepository.findById(todo.getEntityId());
            shiftOptional.get().setRequestAbsence(null);
            shiftMongoRepository.save(shiftOptional.get());
        }
        return response;
    }

    private ShiftAndActivtyStatusDTO validateAccessGroupForUpdateStatus(Todo todo, Shift shift) {
        StaffAccessGroupDTO staffAccessGroupDTO = userIntegrationService.getStaffAccessGroupDTO(shift.getUnitId());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), null);
        List<Activity> activities = activityMongoRepository.findAllPhaseSettingsByActivityIds(newArrayList(todo.getSubEntityId()));
        PhaseSettingsActivityTab phaseSettingsActivityTab = activities.get(0).getPhaseSettingsActivityTab();
        PhaseTemplateValue phaseTemplateValue = phaseSettingsActivityTab.getPhaseTemplateValues().stream().filter(p -> p.getPhaseId().equals(phase.getId())).findFirst().get();
        ActivityShiftStatusSettings activityShiftStatusSettings = shiftStatusService.getActivityShiftStatusSettingByStatus(phaseTemplateValue, ShiftStatus.APPROVE);
        boolean validAccessGroup = shiftValidatorService.validateAccessGroup(activityShiftStatusSettings, staffAccessGroupDTO);
        ShiftAndActivtyStatusDTO shiftAndActivtyStatusDTO = null;
        if(!validAccessGroup){
            ShiftActivityResponseDTO shiftActivityResponseDTO = new ShiftActivityResponseDTO(shift.getId());
            shiftActivityResponseDTO.getActivities().add(new ShiftActivityDTO(activities.get(0).getName(),shift.getStartDate(), shift.getEndDate(), shift.getId(), localeService.getMessage(ACCESS_GROUP_NOT_MATCHED), false));
            shiftAndActivtyStatusDTO = new ShiftAndActivtyStatusDTO(new ArrayList<>(), newArrayList(shiftActivityResponseDTO));
        }
        return shiftAndActivtyStatusDTO;
    }

    private ShiftWithViolatedInfoDTO updateShiftWithRequestAbsence(ActivityWrapper activityWrapper,Shift shift,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        RequestAbsence requestAbsence = shift.getRequestAbsence();
        DateTimeInterval dateTimeInterval = new DateTimeInterval(requestAbsence.getStartDate(),requestAbsence.getEndDate());
        List<ShiftActivity> shiftActivityList = shift.getActivities();
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            List<ShiftActivity> childActivities = new ArrayList<>();
            for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                shiftActivity.setChildActivities(updateShiftActivity(activityWrapper,dateTimeInterval, childActivities, childActivity));
            }
            shiftActivityList = updateShiftActivity(activityWrapper,dateTimeInterval,shiftActivities,shiftActivity);
        }
        shift.setActivities(shiftActivityList);
        ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift,ShiftDTO.class);
        shiftDTO.setShiftDate(asLocalDate(shift.getStartDate()));
        return shiftService.updateShift(shiftDTO,false,false,null);
    }

    private List<ShiftActivity> updateShiftActivity(ActivityWrapper activityWrapper,DateTimeInterval dateTimeInterval, List<ShiftActivity> shiftActivities, ShiftActivity shiftActivity) {
        ShiftActivity absenceActivity = new ShiftActivity(activityWrapper.getActivity().getName(),dateTimeInterval.getStartDate(),dateTimeInterval.getEndDate(),activityWrapper.getActivity().getId(),activityWrapper.getTimeType());
        absenceActivity.getStatus().add(ShiftStatus.REQUEST);
        if(shiftActivity.getInterval().overlaps(dateTimeInterval)){
            List<DateTimeInterval> dateTimeIntervals = shiftActivity.getInterval().minusInterval(dateTimeInterval);
            for (DateTimeInterval timeInterval : dateTimeIntervals) {
                ShiftActivity updatedShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(shiftActivity,ShiftActivity.class);
                updatedShiftActivity.setPlannedTimes(new ArrayList<>());
                updatedShiftActivity.setId(null);
                updatedShiftActivity.setStartDate(timeInterval.getStartDate());
                updatedShiftActivity.setEndDate(timeInterval.getEndDate());
                if(!updatedShiftActivity.getStartDate().equals(updatedShiftActivity.getEndDate())) {
                    shiftActivities.add(updatedShiftActivity);
                }
            }
            if(shiftActivities.stream().noneMatch(activity->activity.getActivityId().equals(absenceActivity.getActivityId()))) {
                shiftActivities.add(absenceActivity);
            }
        }else {
            shiftActivities.add(shiftActivity);
        }
        Collections.sort(shiftActivities);
        return shiftActivities;
    }
}
