package com.kairos.persistence.repository.time_slot;

import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.unit_settings.UnitSetting;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

public class TimeSlotMongoRepositoryImpl implements CustomTimeSlotMongoRepository {

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<TimeSlotSetDTO> findByUnitIdAndTimeSlotType(Long unitId, TimeSlotType timeSlotType){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitId").is(unitId)),
                Aggregation.lookup("timeSlotSet","unitId","unitId","timeSlotSet"),
                Aggregation.replaceRoot("timeSlotSet"),
                Aggregation.match(Criteria.where("timeSlotType").is(timeSlotType))
        );
        return mongoTemplate.aggregate(aggregation, UnitSetting.class,TimeSlotSetDTO.class).getMappedResults();
    }
}
