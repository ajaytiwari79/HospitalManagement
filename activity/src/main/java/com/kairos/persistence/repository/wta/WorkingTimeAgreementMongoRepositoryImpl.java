package com.kairos.persistence.repository.wta;

import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.wrapper.wta.CTAWTADTO;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")

        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WTAQueryResultDTO getOne(BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").is(wtaId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public WTAQueryResultDTO getWTAByUnitPosition(Long unitPositionId, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").is(unitPositionId).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByOrganizationTypeId(long organizationTypeId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationType.id").is(organizationTypeId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByCountryId(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationSubType.id").is(organizationSubTypeId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds, Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationSubType._id").in(subTypeIds).and("countryId").is(countryId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAWithOrganization(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").is(wtaId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    public WTAQueryResultDTO getWTAByCountryId(long countryId, BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId).and("id").is(wtaId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
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
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organization.id").is(unitId).and("expertise.Id").is(expertiseId).and("unitPositionId").exists(false)),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description")
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
    public List<WTAQueryResultDTO> getAllWTAByUpIds(List<Long> upIds, Date date) {
        //.orOperator(Criteria.where("startDate").gte(date).and("endDate").lte(date),Criteria.where("endDate").exists(false).and("startDate").gte(date)
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").in(upIds);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),organizationParentId
                project("name", "description", "unitPositionId","startDate","endDate","parentId","organizationParentId")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    /**
     * @Auhor vipul
     * @Date 6 July
     * @NOTE PLEASE DON'T REMOVE BELOW COMMENTED CODE.
     **/
    // TODO --> PLEASE DON'T REMOVE BELOW COMMENTED CODE.
    /*db.workingTimeAgreement.aggregate([ {"$match":{"_id":{"$in":["41"]}}},
    {"$graphLookup": {from:"workingTimeAgreement",startWith:"$parentWTA","connectFromField":"parentId","connectToField":"_id",as:"pvpv"}},
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
    public List<WTAQueryResultDTO> getWTAWithVersionIds(List<Long> upIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(upIds).and("deleted").is(false).and("disabled").is(true)),
                //graphLookup("workingTimeAgreement").startWith("parentId").connectFrom("parentId").connectTo("_id").as("versions"),
                // unwind("versions"),
                // project("versions").andExclude("_id"),
                // replaceRoot("versions"),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllParentWTAByIds(List<Long> upIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitPositionId").in(upIds).and("disabled").is(false)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<WTAQueryResultDTO> getWTAByUnitPositionIds(List<Long> unitPositionIds, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").in(unitPositionIds).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organization", "parentId", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates", "unitPositionId")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WorkingTimeAgreement getWTABasicByUnitPositionAndDate(Long unitPositionId, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").is(unitPositionId).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        WorkingTimeAgreement result = mongoTemplate.findOne(new Query(criteria), WorkingTimeAgreement.class);
        return result;
    }

    @Override
    public void disableOldWta(BigInteger oldctaId, LocalDate endDate) {
        Update update = Update.update("endDate", DateUtils.asDate(endDate)).set("disabled", true);
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(oldctaId)), update, WorkingTimeAgreement.class);
        }

}
