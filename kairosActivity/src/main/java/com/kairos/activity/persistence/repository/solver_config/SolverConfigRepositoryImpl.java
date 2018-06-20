package com.kairos.activity.persistence.repository.solver_config;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.solver_config.SolverConfig;
import com.kairos.dto.solverconfig.SolverConfigDTO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public class SolverConfigRepositoryImpl implements CustomSolverConfigRepository {

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<SolverConfigDTO> getAllByUnitId(Long unitId) {
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                lookup("constraint","constraints.constraintId","_id","constraints"),
                project().and("constraints._id").as("constraints.constraintId")
        );
        AggregationResults< SolverConfigDTO > result =mongoTemplate.aggregate(agg,SolverConfig.class,SolverConfigDTO.class);
        return result.getMappedResults();
    }

    public Boolean existsSolverConfigByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)));
        return mongoTemplate.exists(query, SolverConfig.class);
    }



    public Boolean existsSolverConfigByNameAndUnitIdAndSolverConfigId(Long unitId, String name, BigInteger solverConfigId) {
        Query query = new Query(Criteria.where("deleted").is(false).and("id").is(solverConfigId).and("unitId").is(unitId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)));
        return mongoTemplate.exists(query, SolverConfig.class);
    }

}
