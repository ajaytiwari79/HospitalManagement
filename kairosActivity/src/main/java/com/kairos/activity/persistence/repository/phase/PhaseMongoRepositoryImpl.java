package com.kairos.activity.persistence.repository.phase;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.query_result.PhaseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

/**
 * Created by vipul on 26/9/17.
 */
public class PhaseMongoRepositoryImpl implements CustomPhaseMongoRepository{
    private static final Logger logger = LoggerFactory.getLogger(PhaseMongoRepositoryImpl.class);

    @Inject
    private MongoTemplate mongoTemplate;

    public List<PhaseWrapper> getAllOrganizationIdsHavingPhases(){
        Aggregation aggregation = Aggregation.newAggregation(group("$organizationId").addToSet("$organizationId").as("organizationId"));
        AggregationResults<PhaseWrapper> result = mongoTemplate.aggregate(aggregation,Phase.class ,PhaseWrapper.class);
        return result.getMappedResults();

    }
    public  List<PhaseDTO> getPhasesByUnit(Long unitId)
    {
        Query query = Query.query(Criteria.where("organizationId").is(unitId));
        query.with(new Sort(Sort.DEFAULT_DIRECTION,"sequence"));
        return mongoTemplate.find(query,PhaseDTO.class,"phases");
    }

    public  List<PhaseDTO> getApplicablePhasesByUnit(Long unitId)
    {
        Query query = Query.query(Criteria.where("organizationId").is(unitId).and("duration").gt(0));
        query.with(new Sort(Sort.Direction.DESC,"sequence"));
        return mongoTemplate.find(query,PhaseDTO.class,"phases");
    }

    public List<OrganizationPhaseDTO> getPhasesGroupByOrganization(){
        Aggregation aggregation = Aggregation.newAggregation(group("$organizationId").push("$$ROOT").as("phases"));
        AggregationResults<OrganizationPhaseDTO> result = mongoTemplate.aggregate(aggregation,Phase.class ,OrganizationPhaseDTO.class);
        return result.getMappedResults();

    }
}
