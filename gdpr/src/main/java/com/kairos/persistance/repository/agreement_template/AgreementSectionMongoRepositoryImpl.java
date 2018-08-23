package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;

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
