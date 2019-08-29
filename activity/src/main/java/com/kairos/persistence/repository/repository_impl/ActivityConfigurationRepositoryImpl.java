package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.repository.unit_settings.CustomActivityConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class ActivityConfigurationRepositoryImpl implements CustomActivityConfigurationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ActivityConfigurationRepositoryImpl.class);
    @Inject
    private MongoTemplate mongoTemplate;
/*
    public ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("presencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.findOne(query, ActivityConfiguration.class);
    }


    public List<ActivityConfigurationDTO> findPresenceConfigurationByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("presencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("absencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityConfiguration> findAllAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("absencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.find(query, ActivityConfiguration.class);
    }

    public ActivityConfiguration findPresenceConfigurationByCountryIdAndPhaseId(Long countryId, BigInteger phaseId) {
        Query query = new Query(Criteria.where("countryId").is(countryId).and("presencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.findOne(query, ActivityConfiguration.class);
    }

    public List<ActivityConfigurationDTO> findPresenceConfigurationByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and("presencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("countryId").is(countryId).and("absencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }*/

}

