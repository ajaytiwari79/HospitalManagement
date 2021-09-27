package com.kairos.persistence.model.task;

/**
 * Created by oodles on 16/11/16.
 */
public enum TaskStatus implements Serializable {
        VISITATED,GENERATED,PLANNED,DELIVERED,CANCELLED,CONFIRMED,INCOMPLETE,RECORDED,
        FIXED,
        DRIVING,
        ARRIVED,
        FINISHED,
        CUSTOMER_ABSENT,REFUSED,ABORTED;
}
