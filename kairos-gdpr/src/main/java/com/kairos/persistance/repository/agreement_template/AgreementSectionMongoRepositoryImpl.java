package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.response.dto.master_data.AgreementSectionResponseDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class AgreementSectionMongoRepositoryImpl implements CustomAgreementSectionRepository {


    @Inject
    private MongoTemplate mongoTemplate;



    @Override
    public AgreementSectionResponseDto getAgreementSectionWithDataById(BigInteger id) {
        Aggregation aggregation=Aggregation.newAggregation(
                match(Criteria.where("_id").is(id).and("deleted").is(false)),
                lookup("clause","clauseIds","_id","clauses")
        );
        AggregationResults<AgreementSectionResponseDto> response=mongoTemplate.aggregate(aggregation,AgreementSection.class,AgreementSectionResponseDto.class);
        return  response.getUniqueMappedResult();

    }
    @Override
    public List<AgreementSectionResponseDto> getAllAgreementSectionWithData(Long countryId) {
        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where("deleted").is(false)),
                lookup("clause","clauseIds","_id","clauses")
        );
        AggregationResults<AgreementSectionResponseDto> response=mongoTemplate.aggregate(aggregation,AgreementSection.class,AgreementSectionResponseDto.class);
        return  response.getMappedResults();
    }

    @Override
    public List<AgreementSectionResponseDto> getAgreementSectionWithDataList(Long countryId,Set<BigInteger> ids) {
        Aggregation aggregation=Aggregation.newAggregation(

                match(Criteria.where("deleted").is(false).and("_id").in(ids).and("countryId").is(countryId)),
                lookup("clause","clauseIds","_id","clauses")
        );
        AggregationResults<AgreementSectionResponseDto> response=mongoTemplate.aggregate(aggregation,AgreementSection.class,AgreementSectionResponseDto.class);
        return  response.getMappedResults();

    }
}
