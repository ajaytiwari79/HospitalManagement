package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.dto.activity.shift.IndividualShiftTemplateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

public class IndividualShiftTemplateRepositoryImpl implements CustomIndividualShiftTemplateRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<IndividualShiftTemplateDTO> getAllIndividualShiftTemplateByIdsIn(Set<BigInteger> individualShiftTemplateIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").in(individualShiftTemplateIds)),
                lookup("individualShiftTemplate", "subShiftIds", "_id", "subShifts"));
        AggregationResults<IndividualShiftTemplateDTO> result = mongoTemplate.aggregate(aggregation, IndividualShiftTemplate.class, IndividualShiftTemplateDTO.class);
        return result.getMappedResults();

    }
}
