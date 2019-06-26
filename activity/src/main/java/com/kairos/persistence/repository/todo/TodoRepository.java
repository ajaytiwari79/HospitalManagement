package com.kairos.persistence.repository.todo;

import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.shift.TodoStatus;
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
public interface TodoRepository extends MongoBaseRepository<Todo, BigInteger> {

    @Query(value = "{unitId:?0,deleted:false,status:{$in:?1}}")
    List<TodoDTO> findAllByNotApproved(Long unitId, Collection<TodoStatus> statuses);

    @Query(value = "{entityId:?0,deleted:false,status:{$in:?1}}")
    List<Todo> findAllByNotApprovedAndEntityId(BigInteger entityId, Collection<TodoStatus> statuses);
}
