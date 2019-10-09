package com.kairos.persistence.repository.phase;


import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import com.kairos.persistence.model.phase.Phase;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * Created by vipul on 26/9/17.
 */
public class PhaseMongoRepositoryImpl implements CustomPhaseMongoRepository {
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String SEQUENCE = "sequence";
    public static final String PHASE_TYPE = "phaseType";
    public static final String PHASES = "phases";
    public static final String COUNTRY_ID = "countryId";
    public static final String DURATION = "duration";

    @Inject
    private MongoTemplate mongoTemplate;


    public List<PhaseDTO> getPhasesByUnit(Long unitId, Sort.Direction direction) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(unitId));
        query.with(new Sort(direction, SEQUENCE).and(new Sort(Sort.Direction.DESC, PHASE_TYPE)));
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);
    }

    public List<PhaseDTO> getPhasesByCountryId(Long countryId, Sort.Direction direction) {
        Query query = Query.query(Criteria.where(COUNTRY_ID).is(countryId));
        query.with(new Sort(direction, SEQUENCE).and(new Sort(Sort.Direction.DESC, PHASE_TYPE)));
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);
    }


    public List<PhaseDTO> getPlanningPhasesByUnit(Long unitId, Sort.Direction direction) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(unitId).and(PHASE_TYPE).is(PhaseType.PLANNING));
        query.with(new Sort(direction, SEQUENCE));
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);
    }

    public List<PhaseDTO> getApplicablePlanningPhasesByUnit(Long unitId, Sort.Direction direction) {

        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DURATION).gt(0).and(PHASE_TYPE).is(PhaseType.PLANNING));
        query.with(new Sort(direction, SEQUENCE));
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);
    }

    @Override
    public List<PhaseDTO> getApplicablePlanningPhasesByUnitIds(List<Long> unitIds, Sort.Direction direction) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).in(unitIds).and(DURATION).gt(0).and(PHASE_TYPE).is(PhaseType.PLANNING));
        query.with(new Sort(direction, SEQUENCE));
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);
    }

    public List<PhaseDTO> getActualPhasesByUnit(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(ORGANIZATION_ID).is(unitId).and(PHASE_TYPE).is(PhaseType.ACTUAL)));
        AggregationResults<PhaseDTO> result = mongoTemplate.aggregate(aggregation, Phase.class, PhaseDTO.class);
        return result.getMappedResults();
    }

    public List<PhaseResponseDTO> getAllPlanningPhasesByUnit(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DURATION).gt(0).and(PHASE_TYPE).is(PhaseType.PLANNING))
        );
        AggregationResults<PhaseResponseDTO> result = mongoTemplate.aggregate(aggregation, Phase.class, PhaseResponseDTO.class);
        return result.getMappedResults();
    }

    public List<PhaseDTO> getNextApplicablePhasesOfUnitBySequence(Long unitId, int sequence) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(unitId).and(SEQUENCE).gt(sequence).and(DURATION).gt(0));
        query.with(new Sort(Sort.Direction.ASC, SEQUENCE));
        query.limit(1);
        return mongoTemplate.find(query, PhaseDTO.class, PHASES);

    }

    public Boolean checkPhaseByPhaseIdAndPhaseEnum(BigInteger phaseId, PhaseDefaultName phaseEnum) {
        Query query = Query.query(Criteria.where("phaseEnum").is(phaseEnum).and("id").is(phaseId));
        return mongoTemplate.exists(query, Phase.class);
    }

    public Boolean checkPhaseBySequence(BigInteger phaseId, int sequence) {
        Query query = Query.query(Criteria.where(SEQUENCE).is(sequence).and("id").is(phaseId));
        return mongoTemplate.exists(query, Phase.class);
    }

    public List<Phase> getPlanningPhasesByUnit(Long unitId) {
        Query query = Query.query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DURATION).gt(0).and(PHASE_TYPE).is(PhaseType.PLANNING));
        query.with(new Sort(Sort.Direction.DESC, SEQUENCE));
        return mongoTemplate.find(query, Phase.class, PHASES);
    }
    public List<Phase> getPlanningPhasesByCountry(Long countryId) {
        Query query = Query.query(Criteria.where(COUNTRY_ID).is(countryId).and(DURATION).gt(0).and(PHASE_TYPE).is(PhaseType.PLANNING));
        query.with(new Sort(Sort.Direction.DESC, SEQUENCE));
        return mongoTemplate.find(query, Phase.class, PHASES);
    }

    public List<PhaseResponseDTO> findPlanningPhasesByCountry(Long countryId) {
        Query query = Query.query(Criteria.where(COUNTRY_ID).is(countryId).and(DURATION).gt(0).and(PHASE_TYPE).is(PhaseType.PLANNING));
        query.with(new Sort(Sort.Direction.DESC, SEQUENCE));
        query.fields().include("id").include("name");
        return mongoTemplate.find(query, PhaseResponseDTO.class, PHASES);
    }


}
