package com.kairos.service.scheduler;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.ApiConstants.CREATE_SCHEDULER_PANEL;
import static com.kairos.constants.ApiConstants.DELETE_SCHEDULER_PANEL;
import static com.kairos.constants.ApiConstants.UPDATE_SCHEDULER_PANEL;

@Service
public class UserToSchedulerQueueService {

    @Inject
    private SchedulerServiceRestClient schedulerServiceRestClient;

    @Inject
    private KafkaProducer kafkaProducer;
    public void pushToJobQueueOnEmploymentEnd(Long employmentEndDate, Long currentEmploymentEndDate,Long organiationId,Long employmentId, ZoneId unitTimeZone) throws Exception {

        String url = "";
        if ((Optional.ofNullable(employmentEndDate).isPresent() && !employmentEndDate.equals(currentEmploymentEndDate)) ||
                (!Optional.ofNullable(employmentEndDate).isPresent() && Optional.ofNullable(currentEmploymentEndDate).isPresent())) {
            KairosScheduleJobDTO scheduledJob;

            IntegrationOperation operation = null;
            if (Optional.ofNullable(currentEmploymentEndDate).isPresent() && Optional.ofNullable(employmentEndDate).isPresent()) {

                operation = IntegrationOperation.UPDATE;
                url = UPDATE_SCHEDULER_PANEL;

            } else if (Optional.ofNullable(currentEmploymentEndDate).isPresent() && !Optional.ofNullable(employmentEndDate).isPresent()) {
                operation = IntegrationOperation.DELETE;
                url = DELETE_SCHEDULER_PANEL;
            } else if (!Optional.ofNullable(currentEmploymentEndDate).isPresent() && Optional.ofNullable(employmentEndDate).isPresent()) {

                operation = IntegrationOperation.CREATE;
                url = CREATE_SCHEDULER_PANEL;

            }


            Long oneTimeTriggerDateMillis = null;
            if (Optional.ofNullable(employmentEndDate).isPresent()) {
                oneTimeTriggerDateMillis = DateUtils.getEndOfDayMillisforUnitFromEpoch(unitTimeZone, employmentEndDate);
            }
            scheduledJob = new KairosScheduleJobDTO(organiationId, JobType.FUNCTIONAL, JobSubType.EMPLOYMENT_END, BigInteger.valueOf(employmentId),
                    operation, oneTimeTriggerDateMillis, true);
            schedulerServiceRestClient.publishRequest(operation.equals(IntegrationOperation.CREATE)? Arrays.asList(scheduledJob) : scheduledJob,organiationId,true,operation,url,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {});
            //Todo yatharth stop kafka for a while
            //kafkaProducer.pushToJobQueue(scheduledJob);

            //scheduledJob.setOneTimeTriggerDateString();

        }
    }
}