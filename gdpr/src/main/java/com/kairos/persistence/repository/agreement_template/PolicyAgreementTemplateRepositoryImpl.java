package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public List<AgreementSectionResponseDTO> getAgreementTemplateWithSectionsAndSubSections(Long countryId, BigInteger agreementTemplateId) {

        String replaceRoot = "{ '$replaceRoot': { 'newRoot': '$agreementSections' } }";
        String sortSubSections = " {$sort:{'subSections.orderedIndex':-1}}";
        String sortAgreementSection = "{$sort:{'orderedIndex':1}}";
        String groupSubSections = "{$group:{_id: '$_id', subSections:{'$addToSet':'$subSections'},'clauseIdOrderedIndex':{'$first':'$clauseIdOrderedIndex'},clauses:{$first:'$clauses'},orderedIndex:{$first:'$orderedIndex'},title:{$first:'$title' }}}";

        Document replaceRootOperation = Document.parse(replaceRoot);
        Document groupOperation = Document.parse(groupSubSections);
        Document sortSubSectionsOperation = Document.parse(sortSubSections);


        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(agreementTemplateId).and(DELETED).is(false)),
                lookup("agreement_section", "agreementSections", "_id", "agreementSections"),
                unwind("agreementSections"),
                new CustomAggregationOperation(replaceRootOperation),
                lookup("clause", "clauseIdOrderedIndex", "_id", "clauses"),
                lookup("agreement_section", "subSections", "_id", "subSections"),
                unwind("subSections", true),
                lookup("clause", "subSections.clauseIdOrderedIndex", "_id", "subSections.clauses"),
                new CustomAggregationOperation(sortSubSectionsOperation),
                new CustomAggregationOperation(groupOperation),
                new CustomAggregationOperation(Document.parse(sortAgreementSection))

        );

        AggregationResults<AgreementSectionResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, AgreementSectionResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public PolicyAgreementTemplate findByName(Long countryId, String templateName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(templateName).and(DELETED).is(false).and(COUNTRY_ID).is(countryId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, PolicyAgreementTemplate.class);

    }

    @Override
    public List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementTemplateByCountryId(Long countryId) {

        Document projectionForTemplateTypeElementAtIndexZeroOperation = Document.parse(CustomAggregationQuery.agreementTemplateProjectionBeforeGroupOperationForTemplateTypeAtIndexZero());
        Document addNonDeletedTemplateTypeOperation = Document.parse(CustomAggregationQuery.addNonDeletedTemplateTyeField());

        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("template_type", "templateType", "_id", "templateType"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation),
                new CustomAggregationOperation(projectionForTemplateTypeElementAtIndexZeroOperation),
                sort(Sort.Direction.DESC, "createdAt")
        );

        AggregationResults<PolicyAgreementTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, PolicyAgreementTemplateResponseDTO.class);
        return result.getMappedResults();

    }
}