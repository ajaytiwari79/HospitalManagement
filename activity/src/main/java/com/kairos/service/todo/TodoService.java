package com.kairos.service.todo;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivitiesIdDTO;
import com.kairos.dto.activity.shift.ShiftPublishDTO;
import com.kairos.dto.activity.todo.TodoDTO;
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
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.RequestAbsenceService;
import com.kairos.service.shift.ShiftStatusService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.asLocalDateString;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.shift.TodoStatus.*;

/**
 * Created by pradeep
 * Created at 25/6/19
 **/
@Service
public class TodoService {

    @Inject private TodoRepository todoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private RequestAbsenceService requestAbsenceService;
    @Inject private ShiftStatusService shiftStatusService;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private PhaseService phaseService;

    public void createTodo(Shift shift, TodoType todoType){
        List<Todo> todos = new ArrayList<>();
        if(todoType.equals(TodoType.APPROVAL_REQUIRED)){
            Set<BigInteger> activityIds = shift.getActivities().stream().map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toSet());
            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(),shift.getStartDate(),shift.getEndDate());
            activities.stream().filter(activity -> activity.getRulesActivityTab().getApprovalAllowedPhaseIds().contains(phase.getId())).forEach(activity -> {
                String description = "An activity <span class='activity-details'>"+activity.getName()+"</span> has been requested for <span class='activity-details'>"+asLocalDateString(shift.getStartDate(),"dd LLLL yyyy")+"</span>";
                todos.add(new Todo(TodoType.APPROVAL_REQUIRED,TodoSubtype.APPROVAL,shift.getId(),activity.getId(),PENDING,asLocalDate(shift.getStartDate()),description,shift.getStaffId(),shift.getEmploymentId(),shift.getUnitId()));
            });
        }
        if(isCollectionNotEmpty(todos)){
            todoRepository.saveEntities(todos);
        }
    }

    public List<TodoDTO> getAllTodo(Long unitId){
        return todoRepository.findAllByNotApproved(unitId,newArrayList(APPROVE,DISAPPROVE));
    }

    public TodoDTO updateTodoStatus(BigInteger todoId, TodoStatus status){
        Todo todo = todoRepository.findOne(todoId);
        if(isNull(todo)){
            exceptionService.dataNotFoundException("");
        }
        if(newHashSet(APPROVE,DISAPPROVE).contains(status)){
            approveAndDisapproveTodo(todo);
        }
        todo.setStatus(status);
        todoRepository.save(todo);
        return ObjectMapperUtils.copyPropertiesByMapper(todo,TodoDTO.class);
    }


    private void approveAndDisapproveTodo(Todo todo){
        switch (todo.getType()){
            case REQUEST_ABSENCE:requestAbsenceService.approveRequestAbsence(todo);
                break;
            case APPROVAL_REQUIRED:
                Shift shift = shiftMongoRepository.findOne(todo.getEntityId());
                List<BigInteger> shiftActivityIds = shift.getActivities().stream().filter(shiftActivity -> shiftActivity.getActivityId().equals(todo.getSubEntityId())).map(shiftActivity -> shiftActivity.getId()).collect(Collectors.toList());
                shiftStatusService.updateStatusOfShifts(todo.getUnitId(), new ShiftPublishDTO(newArrayList(new ShiftActivitiesIdDTO(todo.getEntityId(),shiftActivityIds)), todo.getStatus().equals(DISAPPROVE) ? ShiftStatus.DISAPPROVE : ShiftStatus.APPROVE));
                break;
            default:break;
        }
    }
}
