package com.kairos.scheduler.persistence.repository.scheduler_panel;

import com.kairos.enums.scheduler.JobSubType;

import java.math.BigInteger;

/**
 * Created By G.P.Ranjan on 10/2/20
 **/
public interface CustomSchedulerPanelRepository {

    void deleteJobBySubTypeAndEntityId(Long unitId, BigInteger entityId, JobSubType jobSubType);
}
