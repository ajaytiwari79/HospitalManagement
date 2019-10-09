package com.kairos.persistence.repository.attendence_setting;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.attendance.SickSettingsDTO;
import com.kairos.persistence.model.attendence_setting.SickSettings;
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
    public static final String END_DATE = "endDate";
    @Inject
    private MongoTemplate mongoTemplate;

    public void markUserAsFine(Long staffId, Long unitId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and("staffId").is(staffId).and(END_DATE).is(null));
        Update update = new Update().set(END_DATE, DateUtils.getCurrentLocalDate());
        mongoTemplate.findAndModify(query, update, SickSettings.class);
    }

    public SickSettingsDTO checkUserIsSick(Long userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("userId").is(userId).and("startDate").lte(DateUtils.getCurrentLocalDate()).and(END_DATE).is(null))
        );
        AggregationResults<SickSettingsDTO> results = mongoTemplate.aggregate(aggregation, SickSettings.class, SickSettingsDTO.class);
        return results.getMappedResults().size() > 0 ? results.getMappedResults().get(0) : null;
    }

    public List<SickSettings> findAllSickUsersOfUnit(Long unitId) {
        Query query = new Query(Criteria.where("unitId").is(unitId).and(END_DATE).is(null));
        return mongoTemplate.find(query, SickSettings.class);
    }
}
