package com.kairos.persistence.repository.time_bank;

import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public class TimeBankRepositoryImpl implements CustomTimeBankRepository{

    public static final String EMPLOYMENT_ID = "employmentId";
    @Inject private MongoTemplate mongoTemplate;


    @Override
    public DailyTimeBankEntry findLastTimeBankByEmploymentId(Long employmentId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(EMPLOYMENT_ID).in(employmentId).and("deleted").is(false)),
                group(EMPLOYMENT_ID).last("date").as("date")

        );
        AggregationResults<DailyTimeBankEntry> results = mongoTemplate.aggregate(aggregation,DailyTimeBankEntry.class,DailyTimeBankEntry.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }



    @Override
    public List<DailyTimeBankEntry> findAllDailyTimeBankByEmploymentIdAndBetweenDates(Long employmentId, Date startDate, Date endDate){
        Criteria criteria = Criteria.where(EMPLOYMENT_ID).is(employmentId).and("deleted").is(false).and("date").gte(startDate);
        if(endDate!=null){
            criteria.lte(endDate);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query,DailyTimeBankEntry.class);
    }
}
