package com.planner.repository.solver_config;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.domain.solverconfig.common.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * @author pradeep
 * @date - 12/11/18
 */
@Repository
public class SolverConfigRepositoryImpl implements CustomSolverConfigRepository {
    private static final Logger logger = LoggerFactory.getLogger(SolverConfigRepositoryImpl.class);
    @Inject
    private MongoTemplate mongoTemplate;



        public SolverConfigDTO getSolverConfigWithConstraints(BigInteger solverConfigId){
            Aggregation aggregation = Aggregation.newAggregation(
                    match(Criteria.where("_id").is(solverConfigId)),
                    lookup("solverConfig", "constraintIds", "_id", "constraints"));
            AggregationResults<SolverConfigDTO> result = mongoTemplate.aggregate(aggregation, SolverConfig.class, SolverConfigDTO.class);
            return result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);
        }

}
