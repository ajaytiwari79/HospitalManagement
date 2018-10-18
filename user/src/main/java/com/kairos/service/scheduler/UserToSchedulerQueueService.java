package com.kairos.service.scheduler;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class UserToSchedulerQueueService {

    @Inject
    private KafkaProducer kafkaProducer;
    public void pushToJobQueueOnEmploymentEnd(Long employmentEndDate, Long currentEmploymentEndDate,Long organiationId,Long employmentId, ZoneId unitTimeZone) throws Exception {

        if ((Optional.ofNullable(employmentEndDate).isPresent() && !employmentEndDate.equals(currentEmploymentEndDate)) ||
                (!Optional.ofNullable(employmentEndDate).isPresent() && Optional.ofNullable(currentEmploymentEndDate).isPresent())) {
            KairosScheduleJobDTO scheduledJob;

            IntegrationOperation operation = null;
            if (Optional.ofNullable(currentEmploymentEndDate).isPresent() && Optional.ofNullable(employmentEndDate).isPresent()) {

                operation = IntegrationOperation.UPDATE;

            } else if (Optional.ofNullable(currentEmploymentEndDate).isPresent() && !Optional.ofNullable(employmentEndDate).isPresent()) {
                operation = IntegrationOperation.DELETE;
            } else if (!Optional.ofNullable(currentEmploymentEndDate).isPresent() && Optional.ofNullable(employmentEndDate).isPresent()) {

                operation = IntegrationOperation.CREATE;
            }

            Long oneTimeTriggerDateMillis = null;
            if (Optional.ofNullable(employmentEndDate).isPresent()) {
                oneTimeTriggerDateMillis = DateUtils.getEndOfDayMillisforUnitFromEpoch(unitTimeZone, employmentEndDate);
            }
            scheduledJob = new KairosScheduleJobDTO(organiationId, JobType.FUNCTIONAL, JobSubType.EMPLOYMENT_END, BigInteger.valueOf(employmentId),
                    operation, oneTimeTriggerDateMillis, true);
            kafkaProducer.pushToJobQueue(scheduledJob);
            //scheduledJob.setOneTimeTriggerDateString();

        }
    }
}