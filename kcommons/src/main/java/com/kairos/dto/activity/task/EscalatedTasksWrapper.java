package com.kairos.dto.activity.task;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EscalatedTasksWrapper {

    private long id;
    private List<EscalateTaskWrapper> tasks;

}
