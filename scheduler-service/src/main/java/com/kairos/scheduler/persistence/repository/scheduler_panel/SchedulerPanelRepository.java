package com.kairos.scheduler.persistence.repository.scheduler_panel;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.scheduler.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface SchedulerPanelRepository extends MongoBaseRepository<SchedulerPanel, BigInteger> {

    List<SchedulerPanel> findByUnitId(long unitId);
    List<SchedulerPanel> findByActive(boolean active);

    List<SchedulerPanel> findAllByDeletedFalse();
    SchedulerPanel findByJobSubTypeAndEntityIdAndUnitId(JobSubType jobSubType, BigInteger entityId, Long unitId);
    @Query("{_id:{$in:?0}, deleted:false}")
    List<SchedulerPanel> findByIdsIn(Set<BigInteger> schedulerPanelIds);

    SchedulerPanel findByIdAndDeletedFalse(BigInteger schedulerPanelId);

}
