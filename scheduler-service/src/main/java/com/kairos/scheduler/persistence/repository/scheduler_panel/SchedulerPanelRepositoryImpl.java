package com.kairos.scheduler.persistence.repository.scheduler_panel;

import com.kairos.enums.scheduler.JobSubType;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created By G.P.Ranjan on 10/2/20
 **/
public class SchedulerPanelRepositoryImpl implements CustomSchedulerPanelRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public void deleteJobBySubTypeAndEntityId(Long unitId, BigInteger entityId, JobSubType jobSubType) {
        Query query = new Query(where("unitId").is(unitId).and("entityId").is(entityId).and("jobSubType").is(jobSubType));
        mongoTemplate.remove(query, SchedulerPanel.class);
    }
}
