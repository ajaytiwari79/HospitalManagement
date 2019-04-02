package com.kairos.persistence.repository.cta;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author pradeep
 * @date - 3/8/18
 */

public class CostTimeAgreementRepositoryImpl implements CustomCostTimeAgreementRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override

    public List<CTAResponseDTO> findCTAByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("disabled").is(false)),
                lookup("tag", "tags", "_id", "tags")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public CTAResponseDTO getOneCtaById(BigInteger ctaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(ctaId).and("deleted").is(false)),
                lookup("tag", "tags", "_id", "tags"),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);
    }

    @Override
    public CostTimeAgreement getCTAByIdAndOrganizationSubTypeAndCountryId(Long organizationSubTypeId, Long countryId, BigInteger ctaId) {
        Query query = new Query(Criteria.where("organizationSubType._id").is(organizationSubTypeId).and("_id").is(ctaId).and("countryId").is(countryId).and("deleted").is(false).and("disabled").is(false));
        return mongoTemplate.findOne(query, CostTimeAgreement.class);
    }

    @Override
    public List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long countryId, Long organizationSubTypeId) {
        Query query = new Query(Criteria.where("organizationSubType._id").is(organizationSubTypeId).and("countryId").is(countryId).and("deleted").is(false).and("disabled").is(false));
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, CostTimeAgreement.class), CTAResponseDTO.class);
    }

    @Override
    public List<CTAResponseDTO> findCTAByUnitId(Long unitId) {
        Query query = new Query(Criteria.where("organization._id").is(unitId).and("deleted").is(false).and("disabled").is(false).and("unitPositionId").exists(false));
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, CostTimeAgreement.class), CTAResponseDTO.class);
    }

    @Override
    public Boolean isCTAExistWithSameNameInUnit(Long unitId, String name, BigInteger ctaId) {
        Query query = new Query(Criteria.where("organization._id").is(unitId).and("name").is(name).and("_id").ne(ctaId).and("deleted").is(false).and("disabled").is(false));
        return mongoTemplate.exists(query, CostTimeAgreement.class);
    }

    @Override
    public List<CTAResponseDTO> getDefaultCTAOfExpertiseAndDate(Long unitId, Long expertiseId, LocalDate selectedDate) {
        Query query = new Query(Criteria.where("organization._id").is(unitId).and("expertise._id").is(expertiseId).and("deleted").is(false).and("unitPositionId").exists(false).orOperator(Criteria.where("startDate").lte(selectedDate).and("endDate").gte(selectedDate), Criteria.where("endDate").exists(false).and("startDate").lte(selectedDate)));
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, CostTimeAgreement.class), CTAResponseDTO.class);
    }

    @Override
    public List<CTAResponseDTO> getDefaultCTA(Long unitId, Long expertiseId) {
        Query query = new Query(Criteria.where("organization._id").is(unitId).and("expertise._id").is(expertiseId).and("deleted").is(false).and("unitPositionId").exists(false));
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, CostTimeAgreement.class), CTAResponseDTO.class);
    }

    @Override
    public List<CTAResponseDTO> getCTAByUpIds(Set<Long> unitPositionIds) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitPositionId").in(unitPositionIds));
        query.fields().include("name").include("description").include("unitPositionId").include("startDate").include("endDate").include("parentId").include("organizationParentId");
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, CostTimeAgreement.class), CTAResponseDTO.class);
    }


    @Override
    public List<CTAResponseDTO> getParentCTAByUpIds(List<Long> unitPositionIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitPositionId").in(unitPositionIds).and("disabled").is(false)),
                lookup("tag", "tags", "_id", "tags"),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public CTAResponseDTO getCTAByUnitPositionIdAndDate(Long unitPositionId, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").is(unitPositionId).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("tag", "tags", "_id", "tags"),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);
    }


    @Override
    public List<CTAResponseDTO> getVersionsCTA(List<Long> upIds) {
        String query = "{\n" +
                "      $graphLookup: {\n" +
                "         from: \"costTimeAgreement\",\n" +
                "         startWith: \"$parentId\",\n" +
                "         connectFromField: \"parentId\",\n" +
                "         connectToField: \"_id\",\n" +
                "         maxDepth: 10000,\n" +
                "         depthField: \"numConnections\",\n" +
                "         as: \"versions\"\n" +
                "      }\n" +
                "   },{\n" +
                "       $unwind:\"$versions\"\n" +
                "       },\n" +
                "       { $project:{\"versions\":1,\"_id\":0}},\n" +
                "       {\n" +
                "     $replaceRoot: { newRoot: \"$versions\" }\n" +
                "   },{\n" +
                "       $lookup:{\n" +
                "           from:\"cTARuleTemplate\",\n" +
                "           localField:\"ruleTemplateIds\",\n" +
                "           foreignField:\"_id\",\n" +
                "           as:\"ruleTemplates\"\n" +
                "           }\n" +
                "       }";
        Document document = Document.parse(query);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(upIds).and("deleted").is(false).and("disabled").is(true)),
                new CustomAggregationOperation(document)
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<CTAResponseDTO> getCTAByUnitPositionIdBetweenDate(Long unitPositionId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").is(unitPositionId).orOperator(Criteria.where("startDate").lte(endDate).and("endDate").gte(startDate), Criteria.where("endDate").exists(false).and("startDate").lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<CTAResponseDTO> getCTAByUnitPositionIds(List<Long> unitPositionIds, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").in(unitPositionIds).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationParentId", "organization", "parentId", "startDate", "endDate", "ruleTemplates", "unitPositionId")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<CTAResponseDTO> getCTAByUnitPositionIdsAndDate(List<Long> unitPositionIds, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").in(unitPositionIds).orOperator(Criteria.where("startDate").lte(endDate).and("endDate").gte(startDate), Criteria.where("endDate").exists(false).and("startDate").lte(endDate));
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates"),
                project("name", "description", "disabled", "expertise", "organizationParentId", "organization", "parentId", "startDate", "endDate", "ruleTemplates", "unitPositionId")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation, CostTimeAgreement.class, CTAResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public CostTimeAgreement getCTABasicByUnitPositionAndDate(Long unitPositionId, Date date) {
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").is(unitPositionId).orOperator(Criteria.where("startDate").lte(date).and("endDate").gte(date), Criteria.where("endDate").exists(false).and("startDate").lte(date));
        Query query = new Query(criteria);
        CostTimeAgreement result = mongoTemplate.findOne(query, CostTimeAgreement.class);
        return result;
    }

    @Override
    public void disableOldCta(BigInteger oldctaId, LocalDate endDate) {
        Update update = Update.update("endDate", DateUtils.asDate(endDate)).set("disabled", true);
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(oldctaId)), update, CostTimeAgreement.class);


    }

    @Override
    public void setEndDateToCTAOfUnitPosition(Long unitPositionId, LocalDate endDate) {
        Update update = Update.update("endDate", DateUtils.asDate(endDate));
        mongoTemplate.findAndModify(new Query(Criteria.where("unitPositionId").is(unitPositionId).and("endDate").exists(false)), update, CostTimeAgreement.class);
    }

    //find Overlap wta of unitPositionId
    @Override
    public boolean ctaExistsByUnitPositionIdAndDates(Long unitPositionId, Date startDate, Date endDate) {
        Criteria endDateCriteria = Criteria.where("endDate").exists(false).and("startDate").lte(startDate);
        Criteria criteria = Criteria.where("deleted").is(false).and("unitPositionId").is(unitPositionId).orOperator(Criteria.where("startDate").lte(startDate).and("endDate").gte(startDate), endDateCriteria);
        return mongoTemplate.exists(new Query(criteria), CostTimeAgreement.class);
    }


    @Override
    public boolean ctaExistsByUnitPositionIdAndDatesAndNotEqualToId(BigInteger ctaId, Long unitPositionId, Date startDate, Date endDate) {
        Criteria endDateCriteria = Criteria.where("endDate").exists(false).and("startDate").lte(startDate);
        Criteria criteria = Criteria.where("deleted").is(false).and("id").ne(ctaId).and("unitPositionId").is(unitPositionId).orOperator(Criteria.where("startDate").lte(startDate).and("endDate").gte(startDate), endDateCriteria);
        return mongoTemplate.exists(new Query(criteria), CostTimeAgreement.class);
    }

}
