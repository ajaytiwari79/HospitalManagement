package com.kairos.persistance.repository.agreement_template;

import com.kairos.response.dto.agreement_template.PolicyAgreementTemplateResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import static  org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;

public class PolicyAgreementTemplateRepositoryImpl implements CustomPolicyAgreementTemplateRepository {


    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public PolicyAgreementTemplateResponseDto getpolicyAgreementWithData(BigInteger id) {
      /*  Aggregation aggregation=Aggregation.newAggregation(


                match(Criteria.where("_id").is(id).and("deleted").is(false)),
                lookup("account_type","accountTypes","_id","accountTypes"),
                unwind("agreement_section",true),
                lookup("clause","agreementSections","_id","agreementSections")




        );*/
      return  null;
    }
}