package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOut;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author pradeep
 * @date - 24/7/18
 */
public class PayOutRepositoryImpl implements CustomPayOutRepository {


    @Inject
    private MongoTemplate mongoTemplate;



    public PayOut findLastPayoutByUnitPositionId(Long unitPositionId, Date date) {
        Query query = new Query(Criteria.where("unitPositionId").is(unitPositionId).and("date").lt(date).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC,"date"));
        return mongoTemplate.findOne(query,PayOut.class);
    }


    public void updatePayOut(Long unitPositionId, int payOut) {
        Query query = new Query(Criteria.where("unitPositionId").is(unitPositionId).and("deleted").is(false));
        Update update = new Update().inc("payoutBeforeThisDate",payOut);
        mongoTemplate.updateMulti(query,update,PayOut.class);

    }
}
