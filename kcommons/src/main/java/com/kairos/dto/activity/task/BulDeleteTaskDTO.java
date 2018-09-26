package com.kairos.dto.activity.task;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 13/7/17.
 */
public class BulDeleteTaskDTO {

    List<BigInteger> taskIds;

    public List<BigInteger> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<BigInteger> taskIds) {
        this.taskIds = taskIds;
    }
}
