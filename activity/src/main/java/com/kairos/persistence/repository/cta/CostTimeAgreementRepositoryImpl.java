package com.kairos.persistence.repository.cta;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;

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
                match(Criteria.where("id").is(ctaId).and("deleted").is(false)),
                lookup("cTARuleTemplate", "ruleTemplateIds", "_id", "ruleTemplates")
        );
        AggregationResults<CTAResponseDTO> result = mongoTemplate.aggregate(aggregation,CostTimeAgreement.class,CTAResponseDTO.class);
        return result.getMappedResults().isEmpty()? null : result.getMappedResults().get(0);
    }
}
