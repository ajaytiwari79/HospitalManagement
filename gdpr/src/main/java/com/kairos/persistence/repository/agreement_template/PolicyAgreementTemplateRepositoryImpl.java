package com.kairos.persistence.repository.agreement_template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateBasicResponseDTO;
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

import javax.inject.Inject;

import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.COUNTRY_ID;


public class PolicyAgreementTemplateRepositoryImpl implements CustomPolicyAgreementTemplateRepository {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Inject
    private ObjectMapper objectMapper;

    private String replaceRoot = "{'$replaceRoot': { 'newRoot': '$agreementSections' } }";


    @Override
    public List<AgreementSectionResponseDTO> getAllAgreementSectionsAndSubSectionByReferenceIdAndAgreementTemplateId(Long referenceId, boolean isUnitId, BigInteger agreementTemplateId) {

        String sortSubSections = " {$sort:{'subSections.orderedIndex':-1}}";
        String sortAgreementSection = "{$sort:{'orderedIndex':1}}";
        String groupSubSections = "{$group:{_id: '$_id', subSections:{'$addToSet':'$subSections'},'clauseIdOrderedIndex':{'$first':'$clauseIdOrderedIndex'},'clauseCkEditorVOS':{'$first':'$clauseCkEditorVOS'},clauses:{$first:'$clauses'},orderedIndex:{$first:'$orderedIndex'},title:{$first:'$title' },titleHtml:{$first:'$titleHtml' }}}";

        Criteria criteria = isUnitId ? Criteria.where(ORGANIZATION_ID).is(referenceId).and("_id").is(agreementTemplateId).and(DELETED).is(false) : Criteria.where(COUNTRY_ID).is(referenceId).and("_id").is(agreementTemplateId).and(DELETED).is(false);

        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("agreementSection", "agreementSections", "_id", "agreementSections"),
                unwind("agreementSections"),
                new CustomAggregationOperation(Document.parse(replaceRoot)),
                lookup("clause", "clauseIdOrderedIndex", "_id", "clauses"),
                lookup("agreementSection", "subSections", "_id", "subSections"),
                unwind("subSections", true),
                lookup("clause", "subSections.clauseIdOrderedIndex", "_id", "subSections.clauses"),
                new CustomAggregationOperation(Document.parse(sortSubSections)),
                new CustomAggregationOperation(Document.parse(groupSubSections)),
                new CustomAggregationOperation(Document.parse(sortAgreementSection))

        );

        AggregationResults<AgreementSectionResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, AgreementSectionResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public PolicyAgreementTemplate findByCountryIdAndName(Long countryId, String templateName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(templateName).and(DELETED).is(false).and(COUNTRY_ID).is(countryId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, PolicyAgreementTemplate.class);

    }


    @Override
    public PolicyAgreementTemplate findByUnitIdAndName(Long unitId, String templateName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(templateName).and(DELETED).is(false).and(ORGANIZATION_ID).is(unitId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, PolicyAgreementTemplate.class);
    }

    @Override
    public List<PolicyAgreementTemplateResponseDTO> findAllTemplateByCountryIdOrUnitId(Long referenceId, boolean isUnitId) {

        Criteria criteria = isUnitId ? Criteria.where(ORGANIZATION_ID).is(referenceId).and(DELETED).is(false) : Criteria.where(COUNTRY_ID).is(referenceId).and(DELETED).is(false);

        Aggregation aggregation = Aggregation.newAggregation(

                match(criteria),
                lookup("templateType", "templateTypeId", "_id", "templateType"),
                new CustomAggregationOperation(Document.parse(CustomAggregationQuery.addNonDeletedTemplateTyeField())),
                new CustomAggregationOperation(Document.parse(CustomAggregationQuery.agreementTemplateProjectionBeforeGroupOperationForTemplateTypeAtIndexZero())),
                sort(Sort.Direction.DESC, "createdAt")
        );

        AggregationResults<PolicyAgreementTemplateResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, PolicyAgreementTemplateResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<AgreementTemplateBasicResponseDTO> findAgreementTemplateListByReferenceIdAndClauseId(Long refrenceId, boolean isUnitId, BigInteger clauseId) {

        String projectionOperation = "{'$project':{ '_id':1,'name':1 }}";
        Criteria criteria;
        if (isUnitId)
            criteria = Criteria.where(ORGANIZATION_ID).is(refrenceId).and(DELETED).is(false);
        else
            criteria = Criteria.where(COUNTRY_ID).is(refrenceId).and(DELETED).is(false);


        Aggregation aggregation = Aggregation.newAggregation(
                lookup("agreementSection", "agreementSections", "_id", "agreementSections"),
                unwind("agreementSections"),
                lookup("agreementSection", "agreementSections.subSections", "_id", "agreementSections.subSections"),
                match(criteria.orOperator(Criteria.where("agreementSections.subSections.clauseIdOrderedIndex").is(clauseId), Criteria.where("agreementSections.clauseIdOrderedIndex").is(clauseId))),
                new CustomAggregationOperation(Document.parse(projectionOperation))
        );

        AggregationResults<AgreementTemplateBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, AgreementTemplateBasicResponseDTO.class);
        return result.getMappedResults();

    }


    @Override
    public List<AgreementSection> getAllAgreementSectionAndSubSectionByCountryIdAndClauseId(Long countryId, Set<BigInteger> agreementTemplateIds, BigInteger clauseId) {

        String groupOperation = "{'$group':{ '_id':'$_id','agreementSections':{$addToSet:'$agreementSections'},subSections:{$first:'$subSections'}}}";
        String projectionOperation = "{ '$project': {  'agreementSections': { '$setUnion': [ '$agreementSections', '$subSections' ] } } }";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").in(agreementTemplateIds).and(DELETED).is(false)),
                lookup("agreement_section", "agreementSections", "_id", "agreementSections"),
                unwind("agreementSections", true),
                lookup("agreement_section", "agreementSections.subSections", "_id", "subSections"),
                new CustomAggregationOperation(Document.parse(groupOperation)),
                new CustomAggregationOperation(Document.parse(projectionOperation)),
                unwind("agreementSections"),
                new CustomAggregationOperation(Document.parse(replaceRoot)),
                match(Criteria.where(DELETED).is(false).and("clauseIdOrderedIndex").is(clauseId))
        );
        AggregationResults<AgreementSection> result = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, AgreementSection.class);
        return result.getMappedResults();
    }

    @Override
    public Set<BigInteger> getListOfClausePresentInOtherAgreementTemplateSectionByCountryIdAndClauseId(Long countryId, BigInteger templateId, Set<BigInteger> clauseIds) {


        String addNonDeletedSubSection = "{  '$addFields':" +
                "{'subSections':" +
                "{$filter : { " +
                "'input': '$subSections'," +
                "as: 'subSections', " +
                "cond: {$eq: ['$$subSections.deleted'," + false + "]}" +
                "}}}} ";
        String projectionOperation = "{'$project':{'_id':0,'clauseIds':{" +
                "                '$cond': [" +
                "                {'$not': ['$subSections']}," +
                "                {'$setUnion':['$clauseIdOrderedIndex',[]]}, " +
                "                {'$setUnion':['$clauseIdOrderedIndex','$subSections.clauseIdOrderedIndex']}]}}}}}";
        String groupOperation = "{ '$group' : { '_id' : '$_id' , 'clauseIds':{ '$addToSet' : '$clauseIds'}}}";

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(COUNTRY_ID).is(countryId).and("_id").ne(templateId)),
                lookup("agreementSection", "agreementSections", "_id", "agreementSections"),
                unwind("agreementSections"),
                new CustomAggregationOperation(Document.parse(replaceRoot)),
                match(Criteria.where(DELETED).is(false)),
                lookup("agreementSection", "subSections", "_id", "subSections"),
                new CustomAggregationOperation(Document.parse(addNonDeletedSubSection)),
                unwind("subSections", true),
                new CustomAggregationOperation(Document.parse(projectionOperation)),
                new CustomAggregationOperation(Document.parse(groupOperation))
        );
        AggregationResults<Map> response = mongoTemplate.aggregate(aggregation, PolicyAgreementTemplate.class, Map.class);
        Set<BigInteger> clauseIdList = new HashSet<>();
        if (Optional.ofNullable(response.getUniqueMappedResult()).isPresent()) {
            ArrayList<ArrayList<BigInteger>> arrayLists = (ArrayList<ArrayList<BigInteger>>) response.getUniqueMappedResult().get("clauseIds");
            arrayLists.forEach(bigIntegers -> clauseIdList.addAll(new HashSet<BigInteger>(bigIntegers)));
            return clauseIds.stream().filter(clauseId -> clauseIdList.contains(clauseId.toString())).collect(Collectors.toSet());
        }
        return clauseIdList;
    }


}