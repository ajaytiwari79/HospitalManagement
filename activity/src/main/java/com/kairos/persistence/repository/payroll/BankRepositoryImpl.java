package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.Bank;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BankRepositoryImpl implements CustomBankRepository{
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public Bank findByNameOrAccountNumber(String name, String internationalAccountNumber, String registrationNumber, String swiftCode) {
        Criteria [] criterias=prepareCriteriaList(name,internationalAccountNumber,registrationNumber,swiftCode);
        Query query = new Query(Criteria.where("deleted").is(false).orOperator(criterias));
        return mongoTemplate.findOne(query,Bank.class);
    }

    @Override
    public Bank findByNameOrAccountNumberAndIdNot(BigInteger id, String name, String internationalAccountNumber, String registrationNumber, String swiftCode) {
        Criteria [] criterias=prepareCriteriaList(name,internationalAccountNumber,registrationNumber,swiftCode);
        Query query = new Query(Criteria.where("deleted").is(false).and("_id").ne(id).orOperator(criterias));
        return mongoTemplate.findOne(query,Bank.class);
    }

    private Criteria[] prepareCriteriaList(String name, String internationalAccountNumber, String registrationNumber, String swiftCode){
        Criteria nameCriteria=Criteria.where("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        Criteria accountNumberCriteria=Criteria.where("internationalAccountNumber").regex(Pattern.compile("^" + internationalAccountNumber + "$", Pattern.CASE_INSENSITIVE));
        Criteria registrationNumberCriteria=Criteria.where("registrationNumber").regex(Pattern.compile("^" + registrationNumber + "$", Pattern.CASE_INSENSITIVE));
        Criteria swiftCodeCriteria=Criteria.where("swiftCode").regex(Pattern.compile("^" + swiftCode + "$", Pattern.CASE_INSENSITIVE));
        return new Criteria[]{nameCriteria,accountNumberCriteria,registrationNumberCriteria,swiftCodeCriteria};

    }
}
