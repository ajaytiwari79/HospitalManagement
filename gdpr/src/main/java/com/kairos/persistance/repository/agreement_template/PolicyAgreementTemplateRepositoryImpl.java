package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
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


    @Override
    public List<AgreementSectionResponseDTO> getAgreementTemplateAllSectionAndSubSectons(Long countryId, Long unitId, BigInteger agreementTemplateId) {

        String replaceRoot = "{ '$replaceRoot': { 'newRoot': '$agreementSections' } }";
        String groupSubSections = "{$group:{_id: '$_id', subSections:{'$addToSet': '$subSections'},clauses:{$first:'$clauses'},title:{$first:'$title' }}}";
        String addNonDeletedSubSections = "{  '$addFields':{'subSections': {'$filter' : {'input': '$subSections', 'as': 'subSections','cond': {'$eq': ['$$subSections.deleted', false ]}}}}} ";

        Document replaceRootOperation = Document.parse(replaceRoot);
        Document groupOperation = Document.parse(groupSubSections);
        Document subSectionNonDeletedOperation = Document.parse(addNonDeletedSubSections);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(COUNTRY_ID).is(countryId).and("_id").is(agreementTemplateId).and(DELETED).is(false)),
                lookup("agreement_section", "agreementSections", "_id", "agreementSections"),
                unwind("agreementSections"),
                match(Criteria.where("agreementSections.deleted").is(false)),
                lookup("clause", "agreementSections.clauses", "_id", "agreementSections.clauses"),
                lookup("agreement_section", "agreementSections.subSections", "_id", "agreementSections.subSections"),
                unwind("agreementSections.subSections", true),
                lookup("clause", "agreementSections.subSections.clauses", "_id", "agreementSections.subSections.clauses"),
                new CustomAggregationOperation(replaceRootOperation),
                new CustomAggregationOperation(groupOperation),
                new CustomAggregationOperation(subSectionNonDeletedOperation)


        );

        AggregationResults<AgreementSectionResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, AgreementSectionResponseDTO.class);
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

    @Override
    public List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementTemplateByCountryId(Long countryId, Long unitId) {

        Document projectionForTemplateTypeElementAtIndexZeroOperation = Document.parse(CustomAggregationQuery.agreementTemplateProjectionBeforeGroupOperationForTemplateTypeAtIndexZero());
        Document addNonDeletedTemplateTypeOperation = Document.parse(CustomAggregationQuery.addNonDeletedTemplateTyeField());

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(ORGANIZATION_ID).is(unitId).and(DELETED).is(false)),
                lookup("template_type", "templateType", "_id", "templateType"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation),
                new CustomAggregationOperation(projectionForTemplateTypeElementAtIndexZeroOperation)
        );

        AggregationResults<PolicyAgreementTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, PolicyAgreementTemplateResponseDTO.class);
        return result.getMappedResults();

    }
}