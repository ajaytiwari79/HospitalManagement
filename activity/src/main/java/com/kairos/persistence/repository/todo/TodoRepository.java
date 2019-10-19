package com.kairos.persistence.repository.todo;

import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.todo.Todo;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * Created by pradeep
 * Created at 25/6/19
 **/
@Repository
public interface TodoRepository extends MongoBaseRepository<Todo, BigInteger>,CustomTodoRepository {

    @Query(value = "{unitId:?0,deleted:false,status:{$in:?1}}")
    List<TodoDTO> findAllByNotApproved(Long unitId, Collection<TodoStatus> statuses);

    @Query(value = "{entityId:?0,deleted:false,type:?1,status:{$in:?2}}")
    List<Todo> findAllByNotApprovedAndEntityId(BigInteger entityId, TodoType todoType, Collection<TodoStatus> statuses);

    @Query(value = "{entityId:?0,deleted:false}",delete = true)
    void deleteByEntityId(BigInteger entityId);

    @Query(value = "{entityId:?0,deleted:false,type:?1,status:{$in:?2}}",delete = true)
    Long deleteByEntityIdAndTypeAndStatus(BigInteger entityId, TodoType todoType, Collection<TodoStatus> statuses);

    @Query(value = "{entityId:?0,deleted:false,status:{$in:?1}}",delete = true)
    Long deleteByEntityIdAndStatus(BigInteger entityId, Collection<TodoStatus> statuses);

    @Query(value = "{entityId:?0,deleted:false,type:?1,status:{$in:?2},subEntityId:?3}")
    Todo findAllByEntityIdAndSubEntityAndTypeAndStatus(BigInteger entityId, TodoType todoType, Collection<TodoStatus> statuses,BigInteger subEntityId);

    @Query(value = "{entityId:?0,deleted:false,type:?1}")
    Todo findByEntityIdAndType(BigInteger entityId, TodoType todoType);

    @Query(value = "{staffId:?0,deleted:false}")
    List<TodoDTO> findAllTodoByStaffId(Long staffId);

//    @Query(value = "{unitId:?0,shiftDate:{$gte:?1,$lte:?2}},staffId:{$in:?3},status:{$in:?4}}")
//    List<TodoDTO> findAllByKpiFilter(Long unit, Date startDate, Date endDate, List<Long> staffIds, Collection<String> statuses);

    @Query(value ="{subEntityId:?0,deleted:false,entityId:?1,status:{$in:?2}}")
    Todo findTodoBySubEntityId(BigInteger shiftActivityId,BigInteger shiftId,Collection<TodoStatus> statuses);
}
