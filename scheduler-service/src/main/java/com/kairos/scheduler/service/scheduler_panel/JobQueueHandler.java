package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;

public interface JobQueueHandler {

    public void handleJob(KairosScheduleJobDTO job);
}
