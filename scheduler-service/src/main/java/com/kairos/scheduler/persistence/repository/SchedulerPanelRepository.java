package com.kairos.scheduler.persistence.repository;

import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface SchedulerPanelRepository extends MongoRepository<SchedulerPanel, BigInteger> {

    List<SchedulerPanel> findByUnitId(long unitId);
    List<SchedulerPanel> findByActive(boolean active);

}
