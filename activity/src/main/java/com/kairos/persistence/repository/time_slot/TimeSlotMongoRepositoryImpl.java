package com.kairos.persistence.repository.time_slot;

import com.kairos.constants.AppConstants;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.unit_settings.UnitSetting;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.AppConstants.TIME_SLOT_SET;

public class TimeSlotMongoRepositoryImpl implements CustomTimeSlotMongoRepository {

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<TimeSlotSetDTO> findByUnitIdAndTimeSlotType(Long unitId, TimeSlotType timeSlotType){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(AppConstants.UNIT_ID).is(unitId)),
                Aggregation.lookup(TIME_SLOT_SET, AppConstants.UNIT_ID, AppConstants.UNIT_ID, TIME_SLOT_SET),
                Aggregation.unwind(TIME_SLOT_SET),
                Aggregation.replaceRoot(TIME_SLOT_SET),
                Aggregation.match(Criteria.where("timeSlotType").is(timeSlotType.toString()))
        );
        return mongoTemplate.aggregate(aggregation, UnitSetting.class,TimeSlotSetDTO.class).getMappedResults();
    }
}
