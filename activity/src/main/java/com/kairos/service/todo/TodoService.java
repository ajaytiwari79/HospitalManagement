package com.kairos.service.todo;

import com.kairos.dto.activity.shift.ShiftActivitiesIdDTO;
import com.kairos.dto.activity.shift.ShiftAndActivtyStatusDTO;
import com.kairos.dto.activity.shift.ShiftPublishDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoSubtype;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.RequestAbsenceService;
import com.kairos.service.shift.ShiftStatusService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.asLocalDateString;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.SHIFT_NOT_EXISTS;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.CommonConstants.FULL_WEEK;
import static com.kairos.enums.shift.TodoStatus.*;
import static org.apache.commons.collections.CollectionUtils.containsAny;

/**
 * Created by pradeep
 * Created at 25/6/19
 **/
@Service
public class TodoService {

    public static final String MMM_DD_YYYY = "MMM dd,yyyy";
    @Inject
    private TodoRepository todoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private RequestAbsenceService requestAbsenceService;
    @Inject
    private ShiftStatusService shiftStatusService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private UserIntegrationService userIntegrationService;

    public void createOrUpdateTodo(Shift shift, TodoType todoType, UserAccessRoleDTO userAccessRoleDTO, boolean shiftUpdate) {
        List<Todo> todos = new ArrayList<>();
        if (todoType.equals(TodoType.APPROVAL_REQUIRED)) {
            Set<BigInteger> activityIds = shift.getActivities().stream().filter(shiftActivity -> !containsAny(newHashSet(ShiftStatus.APPROVE,ShiftStatus.PUBLISH),shiftActivity.getStatus())).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toSet());
            activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toSet()));
            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getStartDate(), shift.getEndDate());
            Set<BigInteger> approvalRequiredActivityIds = activities.stream().filter(activity -> activity.getRulesActivityTab().getApprovalAllowedPhaseIds().contains(phase.getId())).map(activity -> activity.getId()).collect(Collectors.toSet());
            if (!shiftUpdate && userAccessRoleDTO.getManagement()) {
                shift.getActivities().forEach(shiftActivity -> {
                    updateStatusIfApprovalRequired(approvalRequiredActivityIds, shiftActivity);
                    shiftActivity.getChildActivities().forEach(childActivity -> updateStatusIfApprovalRequired(approvalRequiredActivityIds, childActivity));
                });
                Activity activity = activityMongoRepository.findOne(shift.getActivities().get(0).getActivityId());
                TodoSubtype todoSubtype = FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? TodoSubtype.FULL_DAY : FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? TodoSubtype.FULL_WEEK : TodoSubtype.ABSENCE_WITH_TIME;
                String description = "An activity <span class='activity-details'>" + activity.getName() + "</span> has been requested for <span class='activity-details'>" + asLocalDateString(shift.getStartDate(), MMM_DD_YYYY) + "</span>";
                Todo todo = new Todo(TodoType.APPROVAL_REQUIRED, todoSubtype, shift.getId(), activity.getId(), activity.getName(), APPROVE, asLocalDate(shift.getStartDate()), description, shift.getStaffId(), shift.getEmploymentId(), shift.getUnitId(),shift.getActivities().get(0).getRemarks());
                todo.setApprovedOn(new Date());
                todoRepository.save(todo);
                shiftMongoRepository.save(shift); } else {
                List<Todo> todoList = todoRepository.findAllByNotApprovedAndEntityId(shift.getId(), TodoType.APPROVAL_REQUIRED, newArrayList(PENDING, VIEWED, REQUESTED));
                Set<BigInteger> subEntitiyIds = todoList.stream().map(todo -> todo.getSubEntityId()).collect(Collectors.toSet());
                updateRemark(todoList, shift);
                todoList.removeIf(todo -> activityIds.contains(todo.getSubEntityId()));
                activityIds.removeIf(activityId -> subEntitiyIds.contains(activityId));
                activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
                if (isCollectionNotEmpty(activityIds)) {
                    todos.addAll(
                            createTodoForActivityApproval(shift, activities));
                }
                todoList.forEach(todo -> todo.setDeleted(true));
                todos.addAll(todoList);
            }
        } else {
            createOrUpdateTodoForRequestApproval(shift, todos);
        }
        if (isCollectionNotEmpty(todos)) {
            todoRepository.saveEntities(todos);
            updateRemark(todos, shift);
        }

    }

    private void createOrUpdateTodoForRequestApproval(Shift shift, List<Todo> todos) {
        if (shift.isDeleted()) {
            todoRepository.deleteByEntityId(shift.getId());
        } else {
            Activity activity = activityMongoRepository.findOne(shift.getRequestAbsence().getActivityId());
            TodoSubtype todoSubtype = FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? TodoSubtype.FULL_DAY : FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? TodoSubtype.FULL_WEEK : TodoSubtype.ABSENCE_WITH_TIME;
            String description = "Absence request has been genereated for <span class='activity-details'>" + asLocalDateString(shift.getStartDate(), MMM_DD_YYYY) + "</span>";
            todos.add(new Todo(TodoType.REQUEST_ABSENCE, todoSubtype, shift.getId(), activity.getId(), activity.getName(), REQUESTED, asLocalDate(shift.getStartDate()), description, shift.getStaffId(), shift.getEmploymentId(), shift.getUnitId(),shift.getActivities().get(0).getRemarks()));
        }
    }

    private void updateStatusIfApprovalRequired(Set<BigInteger> approvalRequiredActivityIds, ShiftActivity shiftActivity) {
        if (approvalRequiredActivityIds.contains(shiftActivity.getActivityId())) {
            shiftActivity.getStatus().add(ShiftStatus.APPROVE);
        }
    }

    public Long deleteTodo(BigInteger shiftId, TodoType todoType) {
        Long deletedCount;
        if (isNotNull(todoType)) {
            deletedCount = todoRepository.deleteByEntityIdAndTypeAndStatus(shiftId, todoType, newArrayList(PENDING, VIEWED, REQUESTED));
        } else {
            deletedCount = todoRepository.deleteByEntityIdAndStatus(shiftId, newArrayList(PENDING, VIEWED, REQUESTED));
        }
        return deletedCount;
    }

    private List<Todo> createTodoForActivityApproval(Shift shift, List<Activity> activities) {
        List<Todo> todos = new ArrayList<>();
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getStartDate(), shift.getEndDate());
        activities.stream().filter(activity -> activity.getRulesActivityTab().getApprovalAllowedPhaseIds().contains(phase.getId())).forEach(activity -> {
            TodoSubtype todoSubtype = FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? TodoSubtype.FULL_DAY : FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) ? TodoSubtype.FULL_WEEK : TodoSubtype.ABSENCE_WITH_TIME;
            String description = "An activity <span class='activity-details'>" + activity.getName() + "</span> has been requested for <span class='activity-details'>" + asLocalDateString(shift.getStartDate(), MMM_DD_YYYY) + "</span>";
            todos.add(new Todo(TodoType.APPROVAL_REQUIRED, todoSubtype, shift.getId(), activity.getId(), activity.getName(), REQUESTED, asLocalDate(shift.getStartDate()), description, shift.getStaffId(), shift.getEmploymentId(), shift.getUnitId(),shift.getActivities().get(0).getRemarks()));
        });
        return todos;
    }

    public List<TodoDTO> getAllTodo(Long unitId) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessRolesOfStaff(unitId);
        List<TodoDTO> todoDTOS = new ArrayList<>();
        if (userAccessRoleDTO.getManagement()) {
            todoDTOS = todoRepository.findAllByNotApproved(unitId, newArrayList(PENDING, VIEWED, REQUESTED));
        }
        return todoDTOS;
    }

    public <T> T updateTodoStatus(BigInteger todoId, TodoStatus status, BigInteger shiftId,String comment) {
        T response = null;
        Todo todo = isNotNull(todoId) ? todoRepository.findOne(todoId) : todoRepository.findByEntityIdAndType(shiftId, TodoType.REQUEST_ABSENCE);
        if (isNull(todo)) {
            exceptionService.dataNotFoundException(SHIFT_NOT_EXISTS);
        }
        todo.setStatus(status);
        if (status.equals(APPROVE)) {
            todo.setApprovedOn(new Date());
        }
        if (status.equals(DISAPPROVE)) {
            todo.setComment(comment);
        }
        if (newHashSet(APPROVE, DISAPPROVE,PENDING).contains(status)) {
            response = approveAndDisapproveTodo(todo);
        }
        todoRepository.save(todo);
        return response;
    }

    private <T> T approveAndDisapproveTodo(Todo todo) {
        T response = null;
        switch (todo.getType()) {
            case REQUEST_ABSENCE:
                response = (T) requestAbsenceService.approveRequestAbsence(todo);
                break;
            case APPROVAL_REQUIRED:
                ShiftStatus shiftStatus = getShiftStatusByTodoStatus(todo.getStatus());
                response = updateStatusOfApprovalRequiredTodo(todo, shiftStatus);
                break;
            default:
                break;
        }
        return response;
    }

    private ShiftStatus getShiftStatusByTodoStatus(TodoStatus status) {
        ShiftStatus shiftStatus = null;
        switch (status) {
            case APPROVE:
                shiftStatus = ShiftStatus.APPROVE;
                break;
            case PENDING:
                shiftStatus = ShiftStatus.PENDING;
                break;
            case DISAPPROVE:
                shiftStatus = ShiftStatus.DISAPPROVE;
                break;
             default:
                 break;
        }
        return shiftStatus;
    }

    private <T> T updateStatusOfApprovalRequiredTodo(Todo todo, ShiftStatus shiftStatus) {
        T response;
        Shift shift = shiftMongoRepository.findOne(todo.getEntityId());
        Activity activity = activityMongoRepository.findOne(todo.getSubEntityId());
        List<BigInteger> shiftActivityIds = shift.getActivities().stream().filter(shiftActivity -> shiftActivity.getActivityId().equals(todo.getSubEntityId())).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList());
        List<ShiftActivitiesIdDTO> shiftActivitiesIdDTOS = new ArrayList<>();
        if (FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
            List<Shift> shifts = shiftMongoRepository.findShiftByShiftActivityIdAndBetweenDate(newArrayList(shift.getActivities().get(0).getActivityId()), asLocalDate(shift.getStartDate()), asLocalDate(shift.getStartDate()).plusDays(7), shift.getStaffId());
            for (Shift shift1 : shifts) {
                shiftActivityIds = new ArrayList<>();
                for (ShiftActivity shiftActivity : shift1.getActivities()) {
                    shiftActivityIds.add(shiftActivity.getId());
                    shiftActivity.getStatus().add(ShiftStatus.REQUEST);
                }
                shiftActivitiesIdDTOS.add(new ShiftActivitiesIdDTO(shift1.getId(), shiftActivityIds));
            }
            shiftMongoRepository.saveEntities(shifts);
        } else {
            shiftActivitiesIdDTOS.add(new ShiftActivitiesIdDTO(todo.getEntityId(), shiftActivityIds));
        }
        ShiftAndActivtyStatusDTO shiftAndActivtyStatusDTO = shiftStatusService.updateStatusOfShifts(todo.getUnitId(), new ShiftPublishDTO(shiftActivitiesIdDTOS, shiftStatus));
        boolean allUpdated = shiftAndActivtyStatusDTO.getShiftActivityStatusResponse().stream().flatMap(shiftActivityResponseDTO -> shiftActivityResponseDTO.getActivities().stream()).filter(shiftActivityDTO -> !shiftActivityDTO.isSuccess()).findAny().isPresent();
        if (allUpdated) {
            todo.setStatus(REQUESTED);
        }
        response = (T) shiftAndActivtyStatusDTO;
        return response;
    }

    //
    public List<TodoDTO> getAllTodoOfStaff(Long staffId) {
        List<TodoDTO> todoDTOS = todoRepository.findAllTodoByStaffId(staffId);


        return todoDTOS;
    }

    public void updateRemark(List<Todo> todoList, Shift shift) {
        List<ShiftActivity> shiftActivities = shift.getActivities();
        for (Todo todo : todoList) {
            for (ShiftActivity shiftActivity : shiftActivities) {
                if (todo.getSubEntityId().equals(shiftActivity.getActivityId())) {
                    todo.setRemark(shiftActivity.getRemarks());

                }
            }
        }
        todoRepository.saveEntities(todoList);

    }
}
