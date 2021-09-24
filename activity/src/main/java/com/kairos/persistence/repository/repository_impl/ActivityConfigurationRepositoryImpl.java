package com.kairos.persistence.repository.repository_impl;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.unit_settings.CustomActivityConfigurationRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Repository
public class ActivityConfigurationRepositoryImpl implements CustomActivityConfigurationRepository {
    public static final String UNIT_ID = "unitId";
    public static final String COUNTRY_ID = "countryId";
    @Inject
    private MongoTemplate mongoTemplate;

    public ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId) {
        Query query = new Query(Criteria.where(UNIT_ID).is(unitId).and("presencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.findOne(query, ActivityConfiguration.class);
    }


    public List<ActivityConfigurationDTO> findPresenceConfigurationByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and("presencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and("absencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityConfiguration> findAllAbsenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId) {
        Query query = new Query(Criteria.where(UNIT_ID).is(unitId).and("absencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.find(query, ActivityConfiguration.class);
    }

    public ActivityConfiguration findPresenceConfigurationByCountryIdAndPhaseId(Long countryId, BigInteger phaseId) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId).and("presencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.findOne(query, ActivityConfiguration.class);
    }

    public List<ActivityConfigurationDTO> findPresenceConfigurationByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and("presencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(COUNTRY_ID).is(countryId).and("absencePlannedTime").exists(true)));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }

}

