package com.kairos.service.scheduler;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.enums.scheduler.Result;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.utils.external_plateform_shift.Transstatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.ApiConstants.JOB_DETAILS;

@Service
public class UserSchedulerJobService  {

    @Inject
    private SchedulerServiceRestClient schedulerRestClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSchedulerJobService.class);


    public void createJobForAddPlanningPeriod(OrganizationBaseEntity organization){
        try {
            List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(days, LocalTime.of(23, 59), JobType.FUNCTIONAL, JobSubType.ATTENDANCE_SETTING, String.valueOf(organization.getTimeZone()));
            // create job for auto clock out and create realtime/draft shiftstate
            schedulerRestClient.publishRequest(Arrays.asList(schedulerPanelDTO), organization.getId(), true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<com.kairos.commons.client.RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
            });
        } catch (Exception e) {
            LOGGER.info("schedular is not running , unable to create job");
        }
    }

    public void createJobForSeniorityLevel() {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(newArrayList(DayOfWeek.values()), LocalTime.of(0, 1), JobType.SYSTEM, JobSubType.SENIORITY_LEVEL, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
    }

    public void createJobForPositionEnd() {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(newArrayList(DayOfWeek.values()), LocalTime.of(0, 1), JobType.SYSTEM, JobSubType.POSITION_END, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
    }

    public void updateJobForTimecareShift(KairosSchedulerExecutorDTO job, LocalDateTime started, LocalDateTime stopped, Transstatus transstatus, String unzipped) {
        KairosSchedulerLogsDTO logs = new KairosSchedulerLogsDTO(Result.SUCCESS,unzipped,job.getId(),job.getUnitId(), DateUtils.getMillisFromLocalDateTime(started),DateUtils.getMillisFromLocalDateTime(stopped),job.getJobSubType());
        if(transstatus.getResult().getNr_errors() > 0) {
            logs.setResult(Result.ERROR);
        }
        schedulerRestClient.publishRequest(logs,null,false,IntegrationOperation.CREATE,JOB_DETAILS,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {});
    }

}


