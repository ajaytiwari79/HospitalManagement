package com.kairos.dto.scheduler.kafka;

import com.kairos.dto.scheduler.KairosSchedulerExecutorDTO;

public interface JobQueueExecutor {
    public void execute(KairosSchedulerExecutorDTO job);
}
