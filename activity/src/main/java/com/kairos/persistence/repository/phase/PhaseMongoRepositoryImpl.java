package com.kairos.persistence.repository.phase;


import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.enums.phase.PhaseType;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.activity.phase.PhaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * Created by vipul on 26/9/17.
 */
public class PhaseMongoRepositoryImpl implements CustomPhaseMongoRepository {
    private static final Logger logger = LoggerFactory.getLogger(PhaseMongoRepositoryImpl.class);

    @Inject
    private MongoTemplate mongoTemplate;


    public List<PhaseDTO> getPhasesByUnit(Long unitId, Sort.Direction direction) {
        Query query = Query.query(Criteria.where("organizationId").is(unitId));
        query.with(new Sort(direction, "sequence"));
        return mongoTemplate.find(query, PhaseDTO.class, "phases");
    }


    public List<PhaseDTO> getPlanningPhasesByUnit(Long unitId, Sort.Direction direction) {
        Query query = Query.query(Criteria.where("organizationId").is(unitId).and("phaseType").is(PhaseType.PLANNING));
        query.with(new Sort(direction, "sequence"));
        return mongoTemplate.find(query, PhaseDTO.class, "phases");
    }

    public List<PhaseDTO> getApplicablePlanningPhasesByUnit(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("organizationId").is(unitId).and("duration").gt(0).and("phaseType").is(PhaseType.PLANNING)));
        AggregationResults<PhaseDTO> result = mongoTemplate.aggregate(aggregation, Phase.class, PhaseDTO.class);
        return result.getMappedResults();
    }

    public List<PhaseResponseDTO> getAllPlanningPhasesByUnit(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("organizationId").is(unitId).and("duration").gt(0).and("phaseType").is(PhaseType.PLANNING)));
        AggregationResults<PhaseResponseDTO> result = mongoTemplate.aggregate(aggregation, Phase.class, PhaseResponseDTO.class);
        return result.getMappedResults();
    }

    public List<PhaseDTO> getNextApplicablePhasesOfUnitBySequence(Long unitId, int sequence) {
        Query query = Query.query(Criteria.where("organizationId").is(unitId).and("sequence").gt(sequence).and("duration").gt(0));
        query.with(new Sort(Sort.Direction.ASC, "sequence"));
        query.limit(1);
        return mongoTemplate.find(query, PhaseDTO.class, "phases");

    }

    public List<OrganizationPhaseDTO> getPhasesGroupByOrganization() {
        Aggregation aggregation = Aggregation.newAggregation(group("$organizationId").push("$$ROOT").as("phases"));
        AggregationResults<OrganizationPhaseDTO> result = mongoTemplate.aggregate(aggregation, Phase.class, OrganizationPhaseDTO.class);
        return result.getMappedResults();

    }

    public Boolean checkPhaseByName(BigInteger phaseId, String name) {
        Query query = Query.query(Criteria.where("name").is(name).and("id").is(phaseId));
        return mongoTemplate.exists(query, Phase.class);
    }

    public Boolean checkPhaseBySequence(BigInteger phaseId, int sequence) {
        Query query = Query.query(Criteria.where("sequence").is(sequence).and("id").is(phaseId));
        return mongoTemplate.exists(query, Phase.class);
    }

    public List<Phase> getPlanningPhasesByUnit(Long unitId) {
        Query query = Query.query(Criteria.where("organizationId").is(unitId).and("duration").gt(0).and("phaseType").is(PhaseType.PLANNING));
        query.with(new Sort(Sort.Direction.DESC, "sequence"));
        return mongoTemplate.find(query, Phase.class, "phases");
    }
}
