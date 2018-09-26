package com.kairos.dto.scheduler.kafka;

import com.kairos.dto.scheduler.KairosSchedulerExecutorDTO;

public interface JobQueueExecutor {
    void execute(KairosSchedulerExecutorDTO job);
}
