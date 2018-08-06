package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.repository.unit_settings.CustomActivityConfigurationRepository;
import com.kairos.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class ActivityConfigurationRepositoryImpl implements CustomActivityConfigurationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ActivityConfigurationRepositoryImpl.class);
    @Inject
    private MongoTemplate mongoTemplate;

    public ActivityConfiguration findPresenceConfigurationByUnitIdAndPhaseId(Long unitId, BigInteger phaseId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("presencePlannedTime.phaseId").is(phaseId));
        return mongoTemplate.findOne(query, ActivityConfiguration.class);
    }


    public List<ActivityConfigurationDTO> findPresenceConfigurationByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("presencePlannedTime").exists(true)),
                project().and("presencePlannedTime.phaseId").as("phaseId")
                        .and("presencePlannedTime.staffPlannedTimeId").as("staffPlannedTimeId")
                        .and("presencePlannedTime.managementPlannedTimeId").as("managementPlannedTimeId"),
                lookup("phases", "phaseId", "_id", "phases"),
                lookup("plannedTimeType", "managementPlannedTimeId", "_id", "managementPlannedTimes"),
                lookup("plannedTimeType", "staffPlannedTimeId", "_id", "staffPlannedTimes"),
                project().and("phases").arrayElementAt(0).as("phase")
                        .and("staffPlannedTimes").arrayElementAt(0).as("staffPlannedTime")
                        .and("managementPlannedTimes").arrayElementAt(0).as("managementPlannedTime")

        );
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("absencePlannedTime").exists(true)),
                project().and("absencePlannedTime.phaseId").as("phaseId")
                        .and("absencePlannedTime.exception").as("exception")
                        .and("absencePlannedTime.plannedTimeId").as("plannedTimeId")
                        .and("absencePlannedTime.timeTypeId").as("timeTypeId"),
                lookup("phases", "phaseId", "_id", "phases"),
                lookup("plannedTimeType", "plannedTimeId", "_id", "plannedTimes"),
                lookup("time_Type", "timeTypeId", "_id", "timeTypes"),
                project("exception")
                        .and("phases").arrayElementAt(0).as("phase")
                        .and("timeTypes").arrayElementAt(0).as("timeType")
                        .and("plannedTimes").arrayElementAt(0).as("plannedTime"));
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
                match(Criteria.where("countryId").is(countryId).and("presencePlannedTime").exists(true)),
                project().and("presencePlannedTime.phaseId").as("phaseId")
                        .and("presencePlannedTime.staffPlannedTimeId").as("staffPlannedTimeId")
                        .and("presencePlannedTime.managementPlannedTimeId").as("managementPlannedTimeId"),
                lookup("phases", "phaseId", "_id", "phases"),
                lookup("plannedTimeType", "managementPlannedTimeId", "_id", "managementPlannedTimes"),
                lookup("plannedTimeType", "staffPlannedTimeId", "_id", "staffPlannedTimes"),
                project().and("phases").arrayElementAt(0).as("phase")
                        .and("staffPlannedTimes").arrayElementAt(0).as("staffPlannedTime")
                        .and("managementPlannedTimes").arrayElementAt(0).as("managementPlannedTime")

        );
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityConfigurationDTO> findAbsenceConfigurationByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and("absencePlannedTime").exists(true)),
                project().and("absencePlannedTime.phaseId").as("phaseId")
                        .and("absencePlannedTime.exception").as("exception")
                        .and("absencePlannedTime.plannedTimeId").as("plannedTimeId")
                        .and("absencePlannedTime.timeTypeId").as("timeTypeId"),
                lookup("phases", "phaseId", "_id", "phases"),
                lookup("plannedTimeType", "plannedTimeId", "_id", "plannedTimes"),
                lookup("time_Type", "timeTypeId", "_id", "timeTypes"),
                project("exception")
                        .and("phases").arrayElementAt(0).as("phase")
                        .and("timeTypes").arrayElementAt(0).as("timeType")
                        .and("plannedTimes").arrayElementAt(0).as("plannedTime"));
        AggregationResults<ActivityConfigurationDTO> result = mongoTemplate.aggregate(aggregation, ActivityConfiguration.class, ActivityConfigurationDTO.class);
        return result.getMappedResults();
    }
}

