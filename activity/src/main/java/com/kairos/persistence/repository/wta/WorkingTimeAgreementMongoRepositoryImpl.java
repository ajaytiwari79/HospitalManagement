package com.kairos.persistence.repository.wta;

import com.kairos.activity.wta.version.WTAVersionDTO;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author pradeep
 * @date - 21/4/18
 */

public class WorkingTimeAgreementMongoRepositoryImpl implements CustomWorkingTimeAgreementMongoRepostory {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<WTAQueryResultDTO> getWtaByOrganization(Long organizationId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organization.id").is(organizationId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")

        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WTAQueryResultDTO getOne(BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").is(wtaId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByOrganizationTypeId(long organizationTypeId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationType.id").is(organizationTypeId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByCountryId(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationSubType.id").is(organizationSubTypeId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds, Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationSubType._id").in(subTypeIds).and("countryId").is(countryId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAWithOrganization(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").is(wtaId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    public WTAQueryResultDTO getWTAByCountryId(long countryId, BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId).and("id").is(wtaId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
                // project().andInclude("ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().get(0);
    }

    @Override
    public WTAQueryResultDTO getVersionOfWTA(BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").is(wtaId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organization.id").is(unitId).and("expertise.Id").is(expertiseId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, BigInteger wtaId, Long organizationTypeId, Long subOrganizationTypeId) {
        return mongoTemplate.findOne(new Query(Criteria.where("name").is(wtaName).and("countryId").is(countryId).and("deleted").is(false).and("organizationType.Id").is(organizationTypeId).and("organizationSubType.Id").is(subOrganizationTypeId).and("_id").ne(wtaId)), WorkingTimeAgreement.class);
    }

    @Override
    public boolean checkUniqueWTANameInOrganization(String name, Long unitId, BigInteger wtaId) {
        return mongoTemplate.exists(new Query(Criteria.where("name").is(name).and("organization.id").is(unitId).and("_id").ne(wtaId).and("deleted").is(false)), WorkingTimeAgreement.class);
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByIds(List<BigInteger> wtaIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("_id").in(wtaIds)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentWTA", "countryParentWTA", "organizationParentWTA", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    /** @Auhor vipul
      * @Date 6 July
      * @NOTE PLEASE DON'T REMOVE BELOW COMMENTED CODE.
     **/
    // TODO --> PLEASE DON'T REMOVE BELOW COMMENTED CODE.
    /*db.workingTimeAgreement.aggregate([ {"$match":{"_id":{"$in":["41"]}}},
    {"$graphLookup": {from:"workingTimeAgreement",startWith:"$parentWTA","connectFromField":"parentWTA","connectToField":"_id",as:"pvpv"}},
    {"$project":{"pvpv":1}},
    {"$unwind":"$pvpv"},{"$unwind":"$pvpv.ruleTemplateIds"},
    {"$lookup":{from:"wtaBaseRuleTemplate",localField:"pvpv.ruleTemplateIds","foreignField":"_id",as:"dataOFRT"}},
    {"$unwind":"$dataOFRT"},
     ,{"$group":{"_id":{parentId:"$_id",wta:"$pvpv","rules":"$dataOFRT"}}},
     {"$group":{"_id":{"parentId":"$_id.parentId","wta":{"id":"$_id.wta._id","name":"$_id.wta.name","startDate":"$_id.wta.startDate",'endDate':'$_id.wta.endDate'}},
            ruleTemp:{$push:"$_id.rules"}}},
     {"$group":{"_id":"$_id.parentId", data:{$push:{wta:"$_id.wta",ruleTemp:"$ruleTemp"}}}}]).pretty();
     */


    @Override
    public List<WTAVersionDTO> getWTAWithVersionIds(List<BigInteger> wtaIds) {
        String groupString = "{'$group':{'_id':{parentId:'$_id',wta:'$versions','rules':'$ruleTemplatesInfo'}}}";
        String groupStringWTA = "{'$group':{'_id':{'parentId':'$_id.parentId','wta':{'_id':'$_id.wta._id','disabled':'$_id.wta.disabled','name':'$_id.wta.name','startDate':'$_id.wta.startDate','expertise':'$_id.wta.expertise','endDate':'$_id.wta.endDate'}},ruleTemp:{$push:'$_id.rules'}}}";
        String groupAll = "{'$group':{'_id':'$_id.parentId', versions:{$push:{'_id':'$_id.wta._id','disabled':'$_id.wta.disabled','name':'$_id.wta.name','startDate':'$_id.wta.startDate','expertise':'$_id.wta.expertise','endDate':'$_id.wta.endDate',ruleTemplates:'$ruleTemp'}}}}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("_id").in(wtaIds)),
                graphLookup("workingTimeAgreement").startWith("$parentWTA").connectFrom("parentWTA").connectTo("_id").as("versions"),
                unwind("versions"),
                unwind("versions.ruleTemplateIds"),
                lookup("wtaBaseRuleTemplate", "versions.ruleTemplateIds", "_id", "ruleTemplatesInfo"),
                unwind("ruleTemplatesInfo"),
                new CustomAggregationOperation(Document.parse(groupString)),
                new CustomAggregationOperation(Document.parse(groupStringWTA)),
                new CustomAggregationOperation(Document.parse(groupAll))
        );
        AggregationResults<WTAVersionDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAVersionDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAVersionDTO> getAllParentWTAByIds(List<BigInteger> wtaIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("_id").in(wtaIds)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAVersionDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAVersionDTO.class);
        return result.getMappedResults();
    }
}
