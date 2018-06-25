package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.response.dto.master_data.PolicyAgreementTemplateResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;
import java.util.List;

public class PolicyAgreementTemplateRepositoryImpl implements CustomPolicyAgreementTemplateRepository {


    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public PolicyAgreementTemplateResponseDTO getPolicyAgreementWithDataById(Long countryId, BigInteger id) {


        Aggregation aggregation=Aggregation.newAggregation(


                match(Criteria.where("_id").is(id).and("deleted").is(false)),
                lookup("account_type","accountTypes","_id","accountTypes"),
                lookup("agreement_section","agreementSections","_id","agreementSections"),
                unwind("agreementSections"),
                lookup("clause","agreementSections.clauseIds","_id","agreementSections.clauses"),
                group("$id").
                        first("$name").as("name").
                        first("$description").as("description").
                        first("accountTypes").as("accountTypes").
                        addToSet("agreementSections").as("agreementSections")


        );


        AggregationResults<PolicyAgreementTemplateResponseDTO> result=mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, PolicyAgreementTemplateResponseDTO.class);
      return  result.getUniqueMappedResult();
    }


    @Override
    public List<PolicyAgreementTemplateResponseDTO> getPolicyAgreementWithData(Long countryId) {
        Aggregation aggregation=Aggregation.newAggregation(
        match(Criteria.where("deleted").is(false)),
                lookup("account_type","accountTypes","_id","accountTypes"),
                lookup("agreement_section","agreementSections","_id","agreementSections"),
                unwind("agreementSections"),
                lookup("clause","agreementSections.clauseIds","_id","agreementSections.clauses"),
                group("$id").
                        first("$name").as("name").
                        first("$description").as("description").
                        first("accountTypes").as("accountTypes").
                        addToSet("agreementSections").as("agreementSections")


        );


        AggregationResults<PolicyAgreementTemplateResponseDTO> result=mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, PolicyAgreementTemplateResponseDTO.class);
        return  result.getMappedResults();
    }
}