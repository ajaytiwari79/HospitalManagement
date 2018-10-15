package com.kairos.commons.service.scheduler.queue;

import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;

public interface JobQueueExecutor {
    void execute(KairosSchedulerExecutorDTO job);
}
