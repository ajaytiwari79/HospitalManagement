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

    public static final String DELETED = "deleted";
    public static final String ORGANIZATION_ID = "organization.id";
    public static final String WTA_BASE_RULE_TEMPLATE = "wtaBaseRuleTemplate";
    public static final String RULE_TEMPLATE_IDS = "ruleTemplateIds";
    public static final String RULE_TEMPLATES = "ruleTemplates";
    public static final String DESCRIPTION = "description";
    public static final String DISABLED = "disabled";
    public static final String EXPERTISE = "expertise";
    public static final String ORGANIZATION_TYPE = "organizationType";
    public static final String ORGANIZATION_SUB_TYPE = "organizationSubType";
    public static final String COUNTRY_ID = "countryId";
    public static final String ORGANIZATION = "organization";
    public static final String PARENT_ID = "parentId";
    public static final String COUNTRY_PARENT_WTA = "countryParentWTA";
    public static final String ORGANIZATION_PARENT_ID = "organizationParentId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String EXPIRY_DATE = "expiryDate";
    public static final String EMPLOYMENT_ID = "employmentId";
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<WTAQueryResultDTO> getWtaByOrganization(Long organizationId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId)),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                lookup("tag","tags","_id","tags"),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)

        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WTAQueryResultDTO getOne(BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and("id").is(wtaId)),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                lookup("tag","tags","_id","tags"),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public WTAQueryResultDTO getWTAByEmploymentIdAndDate(Long employmentId, Date date) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(date).and(END_DATE).gte(date), Criteria.where(END_DATE).exists(false).and(START_DATE).lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                lookup("tag","tags","_id","tags"),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults().size() > 0 ? result.getMappedResults().get(0) : null;
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByCountryId(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(COUNTRY_ID).is(countryId)),
                lookup("tag","tags","_id","tags"),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)

        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByOrganizationSubTypeIdAndCountryId(long organizationSubTypeId, long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and("organizationSubType.id").is(organizationSubTypeId).and(COUNTRY_ID).is(countryId)),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                lookup("tag","tags","_id","tags"),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds, Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and("organizationSubType._id").in(subTypeIds).and(COUNTRY_ID).is(countryId)),
                lookup("tag","tags","_id","tags"),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAWithOrganization(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(COUNTRY_ID).is(countryId)),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                lookup("tag","tags","_id","tags"),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and("id").is(wtaId)),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                lookup("tag","tags","_id","tags"),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION_TYPE, ORGANIZATION_SUB_TYPE, COUNTRY_ID, ORGANIZATION, PARENT_ID, COUNTRY_PARENT_WTA, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId,LocalDate selectedDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(ORGANIZATION_ID).is(unitId).and("expertise.id").is(expertiseId).and(EMPLOYMENT_ID).exists(false).orOperator(Criteria.where(START_DATE).lte(selectedDate).and(END_DATE).gte(selectedDate), Criteria.where(END_DATE).exists(false).and(START_DATE).lte(selectedDate))),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", DESCRIPTION)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaOfEmploymentIdAndDate(Long employmentId,LocalDate selectedDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(selectedDate).and(END_DATE).gte(selectedDate), Criteria.where(END_DATE).exists(false).and(START_DATE).lte(selectedDate))),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", DESCRIPTION, ORGANIZATION_PARENT_ID)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWtaByIds(List<BigInteger> ids) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and("id").in(ids)),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", DESCRIPTION)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, BigInteger wtaId, Long organizationTypeId, Long subOrganizationTypeId) {
        return mongoTemplate.findOne(new Query(Criteria.where("name").is(wtaName).and(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("organizationType.Id").is(organizationTypeId).and("organizationSubType.Id").is(subOrganizationTypeId).and("_id").ne(wtaId)), WorkingTimeAgreement.class);
    }

    @Override
    public WorkingTimeAgreement checkUniqueWTANameInOrganization(String name, Long unitId, BigInteger wtaId) {
        return mongoTemplate.findOne(new Query(Criteria.where("name").is(name).and(ORGANIZATION_ID).is(unitId).and("id").ne(wtaId).and(DELETED).is(false)), WorkingTimeAgreement.class);
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByUpIds(Set<Long> employmentIds, Date date) {
        //.orOperator(Criteria.where("startDate").gte(date).and("endDate").lte(date),Criteria.where("endDate").exists(false).and("startDate").gte(date)
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                //lookup("wtaBaseRuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),organizationParentId
                project("name", DESCRIPTION, EMPLOYMENT_ID, START_DATE, END_DATE, PARENT_ID, ORGANIZATION_PARENT_ID)
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
                match(Criteria.where(EMPLOYMENT_ID).in(employmentIds).and(DELETED).is(false).and(DISABLED).is(true)),
                //graphLookup("workingTimeAgreement").startWith("parentId").connectFrom("parentId").connectTo("_id").as("versions"),
                // unwind("versions"),
                // project("versions").andExclude("_id"),
                // replaceRoot("versions"),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllParentWTAByIds(List<Long> employmentIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false)),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIds(List<Long> employmentIds, Date date) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).orOperator(Criteria.where(START_DATE).lte(date).and(END_DATE).gte(date), Criteria.where(END_DATE).exists(false).and(START_DATE).lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION, PARENT_ID, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES, EMPLOYMENT_ID)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public WorkingTimeAgreement getWTABasicByEmploymentAndDate(Long employmentId, Date date) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(date).and(END_DATE).gte(date), Criteria.where(END_DATE).exists(false).and(START_DATE).lte(date));
        return mongoTemplate.findOne(new Query(criteria), WorkingTimeAgreement.class);
    }

    @Override
    public void disableOldWta(BigInteger oldctaId, LocalDate endDate) {
        Update update = Update.update(END_DATE, DateUtils.asDate(endDate)).set(DISABLED, true);
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(oldctaId)), update, WorkingTimeAgreement.class);
    }

    @Override
    public void setEndDateToWTAOfEmployment(Long employmentId, LocalDate endDate){
        Update update = Update.update(END_DATE, DateUtils.asDate(endDate));
        mongoTemplate.findAndModify(new Query(Criteria.where(EMPLOYMENT_ID).is(employmentId).and(END_DATE).exists(false)),update,WorkingTimeAgreement.class);
    }

    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIdsAndDates(List<Long> employmentIds, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).orOperator(Criteria.where(START_DATE).lte(endDate).and(END_DATE).gte(startDate),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                project("name", DESCRIPTION, DISABLED, START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES, EMPLOYMENT_ID)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getProtectedWTAByEmploymentIdsAndDates(List<Long> employmentIds, Date startDate, Date endDate,WTATemplateType templateType) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).orOperator(Criteria.where(START_DATE).lte(endDate).and(END_DATE).gte(startDate),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                new CustomAggregationOperation(Document.parse(getProjectionWithFilter(templateType)))
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public boolean wtaExistsByEmploymentIdAndDatesAndNotEqualToId(BigInteger wtaId, Long employmentId, Date startDate, Date endDate){
        Criteria endDateCriteria = Criteria.where(END_DATE).exists(false).and(START_DATE).lte(startDate);
        Criteria criteria = Criteria.where(DELETED).is(false).and("id").ne(wtaId).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(startDate).and(END_DATE).gte(startDate),endDateCriteria);
        return mongoTemplate.exists(new Query(criteria),WorkingTimeAgreement.class);
    }

    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDates(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(endDate).and(END_DATE).gte(startDate),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                project("name", DESCRIPTION, DISABLED, START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES, EMPLOYMENT_ID)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getWTAByEmploymentIdAndDatesWithRuleTemplateType(Long employmentId, Date startDate, Date endDate, WTATemplateType templateType) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).orOperator(Criteria.where(START_DATE).lte(endDate).and(END_DATE).gte(startDate),Criteria.where(END_DATE).exists(false).and(START_DATE).lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                new CustomAggregationOperation(Document.parse(getProjectionWithFilter(templateType)))

        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByEmploymentIds(Collection<Long> employmentIds) {
        //.orOperator(Criteria.where("startDate").gte(date).and("endDate").lte(date),Criteria.where("endDate").exists(false).and("startDate").gte(date)
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<WTAQueryResultDTO> getAllWTAByDate(Date date) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).exists(true).orOperator(Criteria.where(START_DATE).lte(date).and(END_DATE).gte(date), Criteria.where(END_DATE).exists(false).and(START_DATE).lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(WTA_BASE_RULE_TEMPLATE, RULE_TEMPLATE_IDS, "_id", RULE_TEMPLATES),
                project("name", DESCRIPTION, DISABLED, EXPERTISE, ORGANIZATION, PARENT_ID, ORGANIZATION_PARENT_ID, "tags", START_DATE, END_DATE, EXPIRY_DATE, RULE_TEMPLATES, EMPLOYMENT_ID)
        );
        AggregationResults<WTAQueryResultDTO> result = mongoTemplate.aggregate(aggregation, WorkingTimeAgreement.class, WTAQueryResultDTO.class);
        return result.getMappedResults();

    }

    private String getProjectionWithFilter(WTATemplateType templateType){
       return  "{  \n" +
                "      \"$project\":{  \n" +
                "         \"startDate\":1,\n" +
                "         \"endDate\":1,\n" +
               "         \"employmentId\":1,\n" +
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
