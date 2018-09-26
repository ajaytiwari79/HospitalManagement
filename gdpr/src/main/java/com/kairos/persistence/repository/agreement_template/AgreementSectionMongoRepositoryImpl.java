package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;

public class AgreementSectionMongoRepositoryImpl implements CustomAgreementSectionRepository {


    @Inject
    private MongoTemplate mongoTemplate;



    @Override
    public AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId,BigInteger id) {
        Aggregation aggregation=Aggregation.newAggregation(
                match(Criteria.where("_id").is(id).and(DELETED).is(false).and(COUNTRY_ID).is(countryId)),
                lookup("clause","clauseIds","_id","clauses")
        );
        AggregationResults<AgreementSectionResponseDTO> response=mongoTemplate.aggregate(aggregation,AgreementSection.class,AgreementSectionResponseDTO.class);
        return  response.getUniqueMappedResult();

    }


}
