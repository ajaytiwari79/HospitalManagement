package com.kairos.persistence.repository.time_bank;

import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public class TimeBankMongoRepositoryImpl implements CustomTimeBankRepository{

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public DailyTimeBankEntry findLastTimeBankByUnitPositionId(Long unitPositionId, Date date) {
        Query query = new Query(Criteria.where("unitPositionId").is(unitPositionId).and("date").lt(date).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC,"date"));
        //query.limit(1);
        return mongoTemplate.findOne(query,DailyTimeBankEntry.class);
    }

    @Override
    public void updateAccumulatedTimeBank(Long unitPositionId, int timeBank) {
        Query query = new Query(Criteria.where("unitPositionId").is(unitPositionId).and("deleted").is(false));
        Update update = new Update().inc("accumultedTimeBankMin",timeBank);
        mongoTemplate.updateMulti(query,update,DailyTimeBankEntry.class);

    }
}
