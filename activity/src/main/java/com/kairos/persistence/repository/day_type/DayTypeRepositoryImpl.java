package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.persistence.model.day_type.DayType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public class DayTypeRepositoryImpl implements CustomDayTypeRepository {

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<DayTypeDTO> findAllByIdInAndDeletedFalse(Collection<BigInteger> ids) {
        return getDayTypeDTOS(Criteria.where("_id").in(ids).and("deleted").is(false));
    }

    @Override
    public List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long countryId) {
        return getDayTypeDTOS(Criteria.where("countryId").in(countryId).and("deleted").is(false));
    }

    private List<DayTypeDTO> getDayTypeDTOS(Criteria criteria) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup("countryHolidayCalender","_id","dayTypeId","countryHolidayCalenderData")
        );
        return mongoTemplate.aggregate(aggregation, DayType.class,DayTypeDTO.class).getMappedResults();
    }
}
