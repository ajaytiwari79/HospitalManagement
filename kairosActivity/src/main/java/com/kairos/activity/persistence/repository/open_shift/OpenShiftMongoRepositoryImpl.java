package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * Created by vipul on 14/5/18.
 */
public class OpenShiftMongoRepositoryImpl implements CustomOpenShiftMongoRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    public List<OpenShiftResponseDTO> getOpenShiftsByUnitIdAndSelectedDate(Long unitId, Date selectedDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("startDate").is(selectedDate)));
        AggregationResults<OpenShiftResponseDTO> result = mongoTemplate.aggregate(aggregation, OpenShift.class, OpenShiftResponseDTO.class);
        return result.getMappedResults();
    }
}

