package com.kairos.kafka;

import com.kairos.dto.KairosSchedulerExecutorDTO;

public interface JobQueueExecutor {
    public void execute(KairosSchedulerExecutorDTO job);
}
