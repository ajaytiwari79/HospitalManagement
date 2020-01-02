package com.kairos.service.scheduler_service;

import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.service.MongoBaseService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;

@Service
public class ActivitySchedulerJobService extends MongoBaseService {

    @Inject
    private SchedulerServiceRestClient schedulerRestClient;

    public void registerJobForWTALeaveCount(Long countryId) {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(BigInteger.valueOf(countryId),newArrayList(DayOfWeek.values()), LocalTime.of(0, 10), JobType.SYSTEM, JobSubType.WTA_LEAVE_COUNT, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
    }

    public void registerJobForProtectedDaysOff() {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(newArrayList(DayOfWeek.values()), LocalTime.of(0, 5), JobType.SYSTEM, JobSubType.PROTECTED_DAYS_OFF, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
    }

}


