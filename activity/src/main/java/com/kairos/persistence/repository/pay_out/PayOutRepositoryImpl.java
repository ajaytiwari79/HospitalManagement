package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOutPerShift;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author pradeep
 * @date - 24/7/18
 */
public class PayOutRepositoryImpl implements CustomPayOutRepository {


    @Inject
    private MongoTemplate mongoTemplate;



    public PayOutPerShift findLastPayoutByEmploymentId(Long employmentId, Date date) {
        Query query = new Query(Criteria.where("employmentId").is(employmentId).and("date").lt(date).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC,"date"));
        return mongoTemplate.findOne(query, PayOutPerShift.class);
    }


    public void updatePayOut(Long employmentId, int payOut) {
        Query query = new Query(Criteria.where("employmentId").is(employmentId).and("deleted").is(false));
        Update update = new Update().inc("payoutBeforeThisDate",payOut);
        mongoTemplate.updateMulti(query,update, PayOutPerShift.class);

    }
}
