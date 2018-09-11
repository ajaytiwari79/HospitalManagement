package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.scheduler.KairosScheduleJobDTO;
import com.kairos.dto.scheduler.SchedulerPanelDTO;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                schedulerPanelService.createSchedulerPanel(scheduleJobDTO.getUnitId(), Stream.of(schedulerPanelDTO).collect(Collectors.toList()));
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
