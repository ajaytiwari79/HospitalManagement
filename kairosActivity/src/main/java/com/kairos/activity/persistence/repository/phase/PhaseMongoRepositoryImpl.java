package com.kairos.activity.persistence.repository.phase;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.activity.persistence.model.period.PlanningPeriod;
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
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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

    public Phase getNextApplicablePhasesOfUnitBySequence(Long unitId, int sequence)
    {
        Query query = Query.query(Criteria.where("organizationId").is(unitId).and("sequence").gt(sequence));
        query.with(new Sort(Sort.Direction.ASC,"sequence"));
        query.limit(1);
        return mongoTemplate.findOne(query, Phase.class,"phases");

    }

    /*public List<ShiftQueryResultWithActivity> findAllShiftsBetweenDurationByUEP(Long unitEmploymentPositionId,Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isMainShift").is(true).and("unitPositionId").is(unitEmploymentPositionId)
                        .and("startDate").lte(endDate).and("endDate").gte(startDate)),
                graphLookup("shifts").startWith("$subShifts").connectFrom("subShifts").connectTo("_id").as("subShift"),unwind("subShifts",true),
                lookup("activities","subShift.activityId","_id","subShift.activity"),
                lookup("activities","activityId","_id","activity")
                ,project("unitId")
                        .andInclude("deleted")
                        .andInclude("startDate")
                        .andInclude("endDate").andInclude("scheduledMinutes").andInclude("durationMinutes")
                        .andInclude("isMainShift").andInclude("subShift")
                        //.andInclude("subShift.startDate").andInclude("subShift.endDate")
                        .andInclude("subShift.activity")
                        .and("activity").arrayElementAt(0).as("activity")
        );
        AggregationResults<ShiftQueryResultWithActivity> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftQueryResultWithActivity.class);
        return result.getMappedResults();
    }*/

    public List<OrganizationPhaseDTO> getPhasesGroupByOrganization(){
        Aggregation aggregation = Aggregation.newAggregation(group("$organizationId").push("$$ROOT").as("phases"));
        AggregationResults<OrganizationPhaseDTO> result = mongoTemplate.aggregate(aggregation,Phase.class ,OrganizationPhaseDTO.class);
        return result.getMappedResults();

    }

    public  Boolean checkPhaseByName(BigInteger phaseId, String name){
        Query query = Query.query(Criteria.where("name").is(name).and("id").is(phaseId));
        return mongoTemplate.exists(query, Phase.class);
    }
}
