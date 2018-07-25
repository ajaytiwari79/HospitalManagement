package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import java.math.BigInteger;
import java.util.List;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.COUNTRY_ID;


public class PolicyAgreementTemplateRepositoryImpl implements CustomPolicyAgreementTemplateRepository {


    @Autowired
    private MongoTemplate mongoTemplate;


    Document addNonDeletedAgreementSectionOperation = Document.parse(CustomAggregationQuery.addNonDeletedAgreementSectionToAgreementTemplate());
    Document projectionForTemplateTypeElementAtIndexZeroOperation = Document.parse(CustomAggregationQuery.agreementTemplateProjectionBeforeGroupOperationForTemplateTypeAtIndexZero());
    Document agreementTemplateGroupOperation = Document.parse(CustomAggregationQuery.agreementTemplateGroupOperation());
    Document addNonDeletedTemplateTypeOperation = Document.parse(CustomAggregationQuery.addNonDeletedTemplateTyeField());

    @Override
    public PolicyAgreementTemplateResponseDTO getPolicyAgreementWithSectionsAndClausesById(Long countryId, Long orgId, BigInteger id) {


        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(orgId).and("_id").is(id).and(DELETED).is(false)),
                lookup("agreement_section", "agreementSections", "_id", "agreementSections"),
                lookup("template_type", "templateType", "_id", "templateType"),
                new CustomAggregationOperation(addNonDeletedAgreementSectionOperation),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation),
                unwind("agreementSections", true),
                lookup("clause", "agreementSections.clauses", "_id", "agreementSections.clauses"),
                new CustomAggregationOperation(projectionForTemplateTypeElementAtIndexZeroOperation),
                new CustomAggregationOperation(agreementTemplateGroupOperation)
        );
        AggregationResults<PolicyAgreementTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, PolicyAgreementTemplateResponseDTO.class);
        return result.getUniqueMappedResult();

    }

    @Override
    public List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementWithSectionsAndClauses(Long countryId, Long orgId) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(orgId).and(DELETED).is(false)),
                lookup("agreement_section", "agreementSections", "_id", "agreementSections"),
                lookup("template_type", "templateType", "_id", "templateType"),
                new CustomAggregationOperation(addNonDeletedAgreementSectionOperation),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation),
                unwind("agreementSections", true),
                lookup("clause", "agreementSections.clauses", "_id", "agreementSections.clauses"),
                new CustomAggregationOperation(projectionForTemplateTypeElementAtIndexZeroOperation),
                new CustomAggregationOperation(agreementTemplateGroupOperation)
        );


        AggregationResults<PolicyAgreementTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, PolicyAgreementTemplateResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public PolicyAgreementTemplate findByName(Long countryId, Long organizationId, String templateName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(templateName).and(DELETED).is(false).and(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(organizationId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, PolicyAgreementTemplate.class);

    }
}