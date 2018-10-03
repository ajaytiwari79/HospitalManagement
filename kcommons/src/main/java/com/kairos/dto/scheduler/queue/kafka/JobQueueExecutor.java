package com.kairos.dto.scheduler.queue.kafka;

import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;

public interface JobQueueExecutor {
    void execute(KairosSchedulerExecutorDTO job);
}
