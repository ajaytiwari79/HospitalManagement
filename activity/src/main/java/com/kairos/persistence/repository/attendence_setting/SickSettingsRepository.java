package com.kairos.persistence.repository.attendence_setting;

import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.response.dto.web.attendance.SickSettingsDTO;
import com.kairos.util.DateUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Repository
public class SickSettingsRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    public void markUserAsFine(Long staffId, Long unitId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("staffId").is(staffId).and("endDate").is(null));
        Update update = new Update().set("endDate", DateUtils.getCurrentLocalDate());
        mongoTemplate.findAndModify(query, update, SickSettings.class);
    }

    public SickSettingsDTO checkUserIsSick(Long userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("userId").is(userId).and("startDate").lte(DateUtils.getCurrentLocalDate()).and("endDate").is(null))
        );
        AggregationResults<SickSettingsDTO> results = mongoTemplate.aggregate(aggregation, SickSettings.class, SickSettingsDTO.class);
        return (results.getMappedResults() != null && results.getMappedResults().size() > 0) ? results.getMappedResults().get(0) : null;
    }
}
