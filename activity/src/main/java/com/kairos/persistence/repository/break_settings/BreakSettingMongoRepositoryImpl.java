package com.kairos.persistence.repository.break_settings;

import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.dto.activity.break_settings.BreakSettingsResponseDTO;
import com.kairos.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.persistence.model.break_settings.BreakSettings;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
public class BreakSettingMongoRepositoryImpl implements CustomBreakSettingsMongoRepository{
    @Inject
    MongoTemplate mongoTemplate;

    public List<BreakSettingsResponseDTO> findAllBreakSettingsByExpertise(Long expertiseId){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("expertiseId").is(expertiseId)),
                lookup("activities","activityId","_id","activity"),
                project("shiftDurationInMinute","breakDurationInMinute","createdAt").and("activity").arrayElementAt(0).as("activity"),
                sort(Sort.Direction.DESC,"createdAt")
        );
        AggregationResults<BreakSettingsResponseDTO> result = mongoTemplate.aggregate(aggregation, BreakSettings.class, BreakSettingsResponseDTO.class);
        return result.getMappedResults();

    }
}
