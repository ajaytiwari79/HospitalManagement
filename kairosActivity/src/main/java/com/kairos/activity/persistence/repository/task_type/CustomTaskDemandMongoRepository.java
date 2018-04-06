package com.kairos.activity.persistence.repository.task_type;


import com.kairos.activity.persistence.model.task_demand.TaskDemand;
import com.mongodb.DBCursor;

import java.util.List;

/**
 * Created by oodles on 10/8/17.
 */
public interface CustomTaskDemandMongoRepository {
    List<TaskDemand> getTaskDemandWhichTaskCreatedTillDateNotNull();
}
