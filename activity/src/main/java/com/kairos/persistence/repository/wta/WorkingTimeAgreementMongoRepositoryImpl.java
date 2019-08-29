package com.kairos.persistence.repository.wta;

import com.kairos.commons.utils.DateUtils;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
                lookup("tag","tags","_id","tags"),
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
                lookup("tag","tags","_id","tags"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public WTAQueryResultDTO getWTAByEmploymentIdAndDate(Long employmentId, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                lookup("tag","tags","_id","tags"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByCountryId(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("countryId").is(countryId)),
                lookup("tag","tags","_id","tags"),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")

        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByOrganizationSubTypeIdAndCountryId(long organizationSubTypeId, long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationSubType.id").is(organizationSubTypeId).and("countryId").is(countryId)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                lookup("tag","tags","_id","tags"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds, Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organizationSubType._id").in(subTypeIds).and("countryId").is(countryId)),
                lookup("tag","tags","_id","tags"),
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
                lookup("tag","tags","_id","tags"),
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
                lookup("tag","tags","_id","tags"),
                project("name", "description", "disabled", "expertise", "organizationType", "organizationSubType", "countryId", "organization", "parentId", "countryParentWTA", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId,LocalDate selectedDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("organization.id").is(unitId).and("expertise.id").is(expertiseId).and("employmentId").exists(false).orOperator(Criteria.where("startDate").lte(selectedDate).and("endDate").gte(selectedDate), Criteria.where("endDate").exists(false).and("startDate").lte(selectedDate))),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaOfEmploymentIdAndDate(Long employmentId,LocalDate selectedDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("employmentId").is(employmentId).orOperator(Criteria.where("startDate").lte(selectedDate).and("endDate").gte(selectedDate), Criteria.where("endDate").exists(false).and("startDate").lte(selectedDate))),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description","organizationParentId")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaByIds(List<BigInteger> ids) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").in(ids)),
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
    public WorkingTimeAgreement checkUniqueWTANameInOrganization(String name, Long unitId, BigInteger wtaId) {
        return mongoTemplate.findOne(new Query(Criteria.where("name").is(name).and("organization.id").is(unitId).and("id").ne(wtaId).and("deleted").is(false)), WorkingTimeAgreement.class);
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByUpIds(Set<Long> employmentIds, Date date) {
        //.orOperator(Criteria.where("startDate").gte(date).and("endDate").lte(date),Criteria.where("endDate").exists(false).and("startDate").gte(date)
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").in(employmentIds);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),organizationParentId
                project("name", "description", "employmentId","startDate","endDate","parentId","organizationParentId")
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
    public List<WTAQueryResultDTO> getWTAWithVersionIds(List<Long> employmentIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("employmentId").in(employmentIds).and("deleted").is(false).and("disabled").is(true)),
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
    public List<WTAQueryResultDTO> getAllParentWTAByIds(List<Long> employmentIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("employmentId").in(employmentIds).and("disabled").is(false)),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIds(List<Long> employmentIds, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").in(employmentIds).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organization", "parentId", "organizationParentId", "tags", "startDate", "endDate", "expiryDate", "ruleTemplates", "employmentId")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WorkingTimeAgreement getWTABasicByEmploymentAndDate(Long employmentId, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        WorkingTimeAgreement result = mongoTemplate.findOne(new Query(criteria), WorkingTimeAgreement.class);
        return result;
    }

    @Override
    public void disableOldWta(BigInteger oldctaId, LocalDate endDate) {
        Update update = Update.update("endDate", DateUtils.asDate(endDate)).set("disabled", true);
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(oldctaId)), update, WorkingTimeAgreement.class);
    }

    @Override
    public void setEndDateToWTAOfEmployment(Long employmentId, LocalDate endDate){
        Update update = Update.update("endDate", DateUtils.asDate(endDate));
        mongoTemplate.findAndModify(new Query(Criteria.where("employmentId").is(employmentId).and("endDate").exists(false)),update,WorkingTimeAgreement.class);
    }

    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIdsAndDates(List<Long> employmentIds, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").in(employmentIds).orOperator(Criteria.where("startDate").lte(endDate).and("endDate").gte(startDate),Criteria.where("endDate").exists(false).and("startDate").lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled",  "startDate", "endDate", "expiryDate", "ruleTemplates", "employmentId")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }



    @Override
    public boolean wtaExistsByEmploymentIdAndDatesAndNotEqualToId(BigInteger wtaId, Long employmentId, Date startDate, Date endDate){
        Criteria endDateCriteria = Criteria.where("endDate").exists(false).and("startDate").lte(startDate);
        Criteria criteria = Criteria.where("deleted").is(false).and("id").ne(wtaId).and("employmentId").is(employmentId).orOperator(Criteria.where("startDate").lte(startDate).and("endDate").gte(startDate),endDateCriteria);
        return mongoTemplate.exists(new Query(criteria),WorkingTimeAgreement.class);
    }

    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDates(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").in(employmentId).orOperator(Criteria.where("startDate").lte(endDate).and("endDate").gte(startDate),Criteria.where("endDate").exists(false).and("startDate").lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled",  "startDate", "endDate", "expiryDate", "ruleTemplates", "employmentId")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDatesWithRuleTemplateType(Long employmentId, Date startDate, Date endDate, WTATemplateType templateType) {
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).orOperator(Criteria.where("startDate").lte(endDate).and("endDate").gte(startDate),Criteria.where("endDate").exists(false).and("startDate").lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                new CustomAggregationOperation(Document.parse(getProjectionWithFilter(templateType)))

        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByEmploymentIds(Collection<Long> employmentIds) {
        //.orOperator(Criteria.where("startDate").gte(date).and("endDate").lte(date),Criteria.where("endDate").exists(false).and("startDate").gte(date)
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").in(employmentIds);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    private String getProjectionWithFilter(WTATemplateType templateType){
       return  "{  \n" +
                "      \"$project\":{  \n" +
                "         \"startDate\":1,\n" +
                "         \"endDate\":1,\n" +
                "          \"ruleTemplates\":{\n" +
                "              \"$filter\":{\n" +
                "                  \"input\":\"$ruleTemplates\",\n" +
                "                  \"as\":\"ruleTemplates\",\n" +
                "                  \"cond\":{\"$eq\":[\"$$ruleTemplates.wtaTemplateType\",'"+templateType.toString()+"']}\n" +
                "                  }\n" +
                "              }\n" +
                "      }\n" +
                "   }";
    }


}
