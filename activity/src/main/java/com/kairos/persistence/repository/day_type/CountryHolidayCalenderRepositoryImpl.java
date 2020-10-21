package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.persistence.model.day_type.CountryHolidayCalender;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.CommonConstants.DELETED;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class CountryHolidayCalenderRepositoryImpl implements CustomCountryHolidayCalenderRepository{

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<CountryHolidayCalenderDTO> getCountryAllHolidays(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and(DELETED).is(false)),
                lookup("DayType", "dayTypeId", "_id", "dayTypes"),
                project("dayTypes.name","dayTypes.allowTimeSettings","dayTypes.colorCode","dayTypes._id")
        );
        AggregationResults<CountryHolidayCalenderDTO> result = mongoTemplate.aggregate(aggregation, CountryHolidayCalender.class, CountryHolidayCalenderDTO.class);
        return result.getMappedResults();
    }

}
