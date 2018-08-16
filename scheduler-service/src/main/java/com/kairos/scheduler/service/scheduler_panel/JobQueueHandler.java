package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.KairosScheduleJobDTO;

public interface JobQueueHandler {

    public void handleJob(KairosScheduleJobDTO job);
}
