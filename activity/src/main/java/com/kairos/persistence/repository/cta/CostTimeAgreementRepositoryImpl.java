package com.kairos.persistence.repository.cta;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * @author pradeep
 * @date - 3/8/18
 */

public class CostTimeAgreementRepositoryImpl implements CustomCostTimeAgreementRepository {

    @Inject private MongoTemplate mongoTemplate;


    @Override
    public CTAResponseDTO getOneCtaById(BigInteger ctaId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(ctaId).and("deleted").is(false).and("disabled").is(false)),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation,CostTimeAgreement.class,CTAResponseDTO.class);
        return result.getMappedResults().isEmpty()? null : result.getMappedResults().get(0);
    }

    @Override
    public CostTimeAgreement getCTAByIdAndOrganizationSubTypeAndCountryId(Long organizationSubTypeId, Long countryId, BigInteger ctaId) {
        Query query = new Query(Criteria.where("organizationSubType._id").is(organizationSubTypeId).and("_id").is(ctaId).and("countryId").is(countryId).and("deleted").is(false).and("disabled").is(false));
        return mongoTemplate.findOne(query,CostTimeAgreement.class);
    }

    @Override
    public List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long countryId,Long organizationSubTypeId) {
        Query query = new Query(Criteria.where("organizationSubType._id").is(organizationSubTypeId).and("countryId").is(countryId).and("deleted").is(false).and("disabled").is(false));
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query,CostTimeAgreement.class),CTAResponseDTO.class);
    }

    @Override
    public List<CTAResponseDTO> findCTAByUnitId(Long unitId) {
        Query query = new Query(Criteria.where("organization._id").is(unitId).and("deleted").is(false).and("disabled").is(false));
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query,CostTimeAgreement.class),CTAResponseDTO.class);
    }

    @Override
    public Boolean isCTAExistWithSameNameInUnit(Long unitId, String name, BigInteger ctaId) {
        Query query = new Query(Criteria.where("organization._id").is(unitId).and("name").is(name).and("_id").ne(ctaId).and("deleted").is(false).and("disabled").is(false));
        return mongoTemplate.exists(query,CostTimeAgreement.class);
    }

    @Override
    public List<CTAResponseDTO> getDefaultCTA(Long unitId, Long expertiseId) {
        Query query = new Query(Criteria.where("organization._id").is(unitId).and("expertise._id").is(expertiseId).and("deleted").is(false));
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query,CostTimeAgreement.class),CTAResponseDTO.class);
    }
}
