package com.kairos.persistence.repository.pay_out;

import com.kairos.enums.payout.PayOutStatus;
import com.kairos.persistence.model.pay_out.PayOut;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author pradeep
 * @date - 24/7/18
 */
public class PayoutMongoRepositoryImpl implements CustomPayoutMongoRepository{


    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public PayOut findLastPayoutByUnitPositionId(Long unitPositionId) {
        Query query = new Query(Criteria.where("unitPositionId").is(unitPositionId).and("payOutStatus").is(PayOutStatus.APPROVED).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC,"date"));
        //query.limit(1);
        return mongoTemplate.findOne(query,PayOut.class);
    }
}
