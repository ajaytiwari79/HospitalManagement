package com.kairos.activity.persistence.repository.repository_impl;

import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.activity.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.activity.persistence.repository.unit_settings.CustomActivityConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

@Repository
public class ActivityConfigurationRepositoryImpl implements CustomActivityConfigurationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ActivityConfigurationRepositoryImpl.class);
    @Inject
    private MongoTemplate mongoTemplate;

    public ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("presencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.findOne(query, ActivityConfiguration.class);
    }

    public ActivityConfiguration findAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("absencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.findOne(query, ActivityConfiguration.class);
    }

    public List<ActivityConfiguration> findPresenceConfigurationByUnitIdAndPhaseId(Long unitId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("presencePlannedTime").exists(true));
        return mongoTemplate.find(query, ActivityConfiguration.class);
    }

    public List<ActivityConfiguration> findAbsenceConfigurationByUnitIdAndPhaseId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("unitId").is(unitId).and("absencePlannedTime").exists(true)));
        AggregationResults<Object> result = mongoTemplate.aggregate(aggregation,ActivityConfiguration.class ,Object.class);

        Query query = new Query();
        return mongoTemplate.find(query, ActivityConfiguration.class);
    }

}

