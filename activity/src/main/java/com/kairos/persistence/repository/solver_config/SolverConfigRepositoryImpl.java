/*
package com.kairos.persistence.repository.solver_config;

import com.kairos.persistence.model.solver_config.SolverConfig;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

*/
/**
 * @author pradeep
 * @date - 20/6/18
 *//*


public class SolverConfigRepositoryImpl implements CustomSolverConfigRepository {
    @Inject private MongoTemplate mongoTemplate;

    @Override
    public SolverConfigDTO getOneById(BigInteger solverConfigId) {
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("id").is(solverConfigId).and("deleted").is(false))
                //lookup("constraint","constraints._id","_id","constraints")
        );
        AggregationResults< SolverConfigDTO > result =mongoTemplate.aggregate(agg,SolverConfig.class,SolverConfigDTO.class);
        return result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);
    }

    */
/*@Override
    public List<SolverConfigDTO> getAllByUnitId(Long unitId) {
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                lookup("constraint","constraints._id","_id","constraints")
        );
        AggregationResults< SolverConfigDTO > result =mongoTemplate.aggregate(agg,SolverConfig.class,SolverConfigDTO.class);
        return result.getMappedResults();
    }*//*


    public Boolean existsSolverConfigByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)));
        return mongoTemplate.exists(query, SolverConfig.class);
    }



    public Boolean existsSolverConfigByNameAndUnitIdAndSolverConfigId(Long unitId, String name, BigInteger solverConfigId) {
        Query query = new Query(Criteria.where("deleted").is(false).and("id").is(solverConfigId).and("unitId").is(unitId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)));
        return mongoTemplate.exists(query, SolverConfig.class);
    }

}
*/
