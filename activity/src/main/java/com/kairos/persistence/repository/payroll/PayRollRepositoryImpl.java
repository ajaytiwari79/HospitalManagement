package com.kairos.persistence.repository.payroll;/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.Bank;
import com.kairos.persistence.model.payroll.PayRoll;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PayRollRepositoryImpl implements CustomPayRollRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public PayRoll findByNameOrCode(String name, int code) {
        List<Criteria> criteriaList=prepareCriteriaList(name,code);
        Query query = new Query(Criteria.where("deleted").is(false).orOperator(criteriaList.toArray(new Criteria[0])));
        return mongoTemplate.findOne(query,PayRoll.class);
    }

    @Override
    public PayRoll findNameOrCodeExcludingById(BigInteger id, String name, int code) {
        List<Criteria> criteriaList=prepareCriteriaList(name,code);
        Query query = new Query(Criteria.where("deleted").is(false).and("_id").ne(id).orOperator(criteriaList.toArray(new Criteria[0])));
        return mongoTemplate.findOne(query,PayRoll.class);
    }

    private List<Criteria> prepareCriteriaList(String name,  int code){
        Criteria nameCriteria=Criteria.where("name").is(name).regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        Criteria codeCriteria=Criteria.where("code").is(code);
        return Arrays.asList(nameCriteria,codeCriteria);

    }
}
