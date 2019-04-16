package com.kairos.persistence.repository.task_type;


import com.kairos.persistence.model.task_demand.TaskDemand;

import java.util.List;

/**
 * Created by oodles on 10/8/17.
 */
public interface CustomTaskDemandMongoRepository {
    List<TaskDemand> getTaskDemandWhichTaskCreatedTillDateNotNull();
}
