package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.KairosScheduleJobDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.kafka.JobQueueExecutor;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserToSchedulerQueueService implements JobQueueHandler {

    @Inject
    private SchedulerPanelService schedulerPanelService;

    public void handleJob(KairosScheduleJobDTO scheduleJobDTO) {

        SchedulerPanel schedulerPanel = new SchedulerPanel();
        ObjectMapperUtils.copyProperties(scheduleJobDTO,schedulerPanel);
        if(schedulerPanel.isOneTimeTrigger()) {
            schedulerPanel.setOneTimeTriggerDate(DateUtils.getLocalDatetimeFromLong(scheduleJobDTO.getOneTimeTriggerDateMillis()));
        }

        switch (scheduleJobDTO.getIntegrationOperation()) {
            case CREATE:
                schedulerPanelService.createSchedulerPanel(scheduleJobDTO.getUnitId(), schedulerPanel, null);
                break;
            case UPDATE:
                schedulerPanelService.updateSchedulerPanelByJobSubTypeAndEntityId(schedulerPanel);
            case DELETE:
                schedulerPanelService.deleteJobBySubTypeAndEntityId(schedulerPanel);

        }
    }
}
