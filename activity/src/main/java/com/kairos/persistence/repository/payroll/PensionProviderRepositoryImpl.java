package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.PensionProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class PensionProviderRepositoryImpl implements CustomPensionProviderRepository{
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public PensionProvider findByNameOrPaymentNumber(String name, String paymentNumber) {
        Criteria[] criterias=prepareCriteriaList(name,paymentNumber);
        Query query = new Query(Criteria.where("deleted").is(false).orOperator(criterias));
        return mongoTemplate.findOne(query,PensionProvider.class);
    }

    @Override
    public PensionProvider findByNameOrPaymentNumberAndIdNot(String name, String paymentNumber, BigInteger id) {
        Criteria[] criterias=prepareCriteriaList(name,paymentNumber);
        Query query = new Query(Criteria.where("deleted").is(false).and("_id").ne(id).orOperator(criterias));
        return mongoTemplate.findOne(query,PensionProvider.class);
    }

    private Criteria[] prepareCriteriaList(String name, String paymentNumber){
        Criteria nameCriteria=Criteria.where("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        Criteria paymentNumberCriteria=Criteria.where("paymentNumber").regex(Pattern.compile("^" + paymentNumber + "$", Pattern.CASE_INSENSITIVE));
        return new Criteria[]{nameCriteria,paymentNumberCriteria};
    }
}
