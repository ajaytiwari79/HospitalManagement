package com.kairos.persistence.repository.time_bank;

import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public class TimeBankRepositoryImpl implements CustomTimeBankRepository{

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public DailyTimeBankEntry findLastTimeBankByEmploymentId(Long employmentId, Date date) {
        Query query = new Query(Criteria.where("employmentId").is(employmentId).and("date").lt(date).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC,"date"));
        return mongoTemplate.findOne(query,DailyTimeBankEntry.class);
    }

    @Override
    public List<DailyTimeBankEntry> findAllDailyTimeBankByEmploymentIdAndBetweenDates(Long employmentId, Date startDate, Date endDate){
        Criteria criteria = Criteria.where("employmentId").is(employmentId).and("deleted").is(false).and("date").gte(startDate);
        if(endDate!=null){
            criteria.lte(endDate);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query,DailyTimeBankEntry.class);
    }
}
