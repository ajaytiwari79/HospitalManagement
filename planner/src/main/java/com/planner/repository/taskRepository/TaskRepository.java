package com.planner.repository.taskRepository;

import com.planner.domain.task.Task;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Repository
public interface TaskRepository extends MongoBaseRepository<Task,String> {
}
