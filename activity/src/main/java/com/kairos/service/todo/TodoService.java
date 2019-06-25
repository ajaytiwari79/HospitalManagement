package com.kairos.service.todo;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivitiesIdDTO;
import com.kairos.dto.activity.shift.ShiftPublishDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.RequestAbsenceService;
import com.kairos.service.shift.ShiftStatusService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.shift.TodoStatus.APPROVE;
import static com.kairos.enums.shift.TodoStatus.DISAPPROVE;

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
