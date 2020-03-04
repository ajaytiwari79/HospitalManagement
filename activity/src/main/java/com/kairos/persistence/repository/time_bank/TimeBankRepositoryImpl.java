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

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public class TimeBankRepositoryImpl implements CustomTimeBankRepository{

    public static final String EMPLOYMENT_ID = "employmentId";
    public static final String DELETED = "deleted";
    public static final String TIME_BANK_OFF_MINUTES = "timeBankOffMinutes";
    @Inject private MongoTemplate mongoTemplate;


    @Override
    public DailyTimeBankEntry findLastTimeBankByEmploymentId(Long employmentId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(EMPLOYMENT_ID).in(employmentId).and(DELETED).is(false)),
                group(EMPLOYMENT_ID).last("date").as("date")

        );
        AggregationResults<DailyTimeBankEntry> results = mongoTemplate.aggregate(aggregation,DailyTimeBankEntry.class,DailyTimeBankEntry.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }



    @Override
    public List<DailyTimeBankEntry> findAllDailyTimeBankByEmploymentIdAndBetweenDates(Long employmentId, Date startDate, Date endDate){
        Criteria criteria = Criteria.where(EMPLOYMENT_ID).is(employmentId).and(DELETED).is(false).and("date").gte(startDate);
        if(endDate!=null){
            criteria.lte(endDate);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query,DailyTimeBankEntry.class);
    }

    public long getTimeBankOffMinutes(Long employmentId){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(EMPLOYMENT_ID).is(employmentId).and(DELETED).is(false)),
                group(EMPLOYMENT_ID).sum(TIME_BANK_OFF_MINUTES).as(TIME_BANK_OFF_MINUTES),
                project(TIME_BANK_OFF_MINUTES).andExclude("_id")

        );
        AggregationResults<DailyTimeBankEntry> results = mongoTemplate.aggregate(aggregation,DailyTimeBankEntry.class,DailyTimeBankEntry.class);
        return isCollectionEmpty(results.getMappedResults()) ? 0 : results.getMappedResults().get(0).getTimeBankOffMinutes();
    }

    @Override
    public void deleteDailyTimeBank(List<Long> employmentIds, Date startDate, Date endDate){
        mongoTemplate.remove(new Query(Criteria.where(EMPLOYMENT_ID).in(employmentIds).and("date").gte(startDate).lt(endDate)),DailyTimeBankEntry.class);
    }
}
