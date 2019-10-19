package com.kairos.dto.activity.task_type;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by prabjot on 16/5/17.
 */
@Getter
@Setter
public class TaskTypeAggregateResult {

    private long id;
    private List<Integer> taskTypeIds;

    @Override
    public String toString() {
        return "TaskTypeAggregateResult{" +
                "id=" + id +
                ", taskTypeIds=" + taskTypeIds +
                '}';
    }
}
