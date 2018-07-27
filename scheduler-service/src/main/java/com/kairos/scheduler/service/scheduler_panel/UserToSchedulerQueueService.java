package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.KairosScheduleJobDTO;
import com.kairos.dto.SchedulerPanelDTO;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserToSchedulerQueueService implements JobQueueHandler {

    @Inject
    private SchedulerPanelService schedulerPanelService;

    public void handleJob(KairosScheduleJobDTO scheduleJobDTO) {

        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO();
        ObjectMapperUtils.copyProperties(scheduleJobDTO,schedulerPanelDTO);
        if(schedulerPanelDTO.isOneTimeTrigger()) {
            schedulerPanelDTO.setOneTimeTriggerDate(DateUtils.getLocalDatetimeFromLong(scheduleJobDTO.getOneTimeTriggerDateMillis()));
        }

        switch (scheduleJobDTO.getIntegrationOperation()) {
            case CREATE:
                schedulerPanelService.createSchedulerPanel(scheduleJobDTO.getUnitId(), schedulerPanelDTO, null);
                break;
            case UPDATE:
                schedulerPanelService.updateSchedulerPanelByJobSubTypeAndEntityId(schedulerPanelDTO);
                break;
            case DELETE:
                schedulerPanelService.deleteJobBySubTypeAndEntityId(schedulerPanelDTO);
                break;

        }
    }
}
