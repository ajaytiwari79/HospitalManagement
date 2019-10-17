package com.kairos.dto.activity.task;

import com.kairos.dto.planner.vrp.TaskAddress;
import com.kairos.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class TaskWrapper {
    private String id;
    private String name;
    private int duration;
    private TaskAddress address;
    private Date dateFrom;
    private Date dateTo;
    private Status status;
}