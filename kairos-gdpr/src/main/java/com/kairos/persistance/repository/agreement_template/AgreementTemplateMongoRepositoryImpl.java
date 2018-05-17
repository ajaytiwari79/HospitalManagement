package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.AgreementTemplate;
import com.kairos.response.dto.AgreementQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class AgreementTemplateMongoRepositoryImpl implements CustomAgreementTemplateRepository {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public AgreementQueryResult findById(BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(id)),
                lookup("clause", "clauses", "_id", "clauses")

        );
        AggregationResults<AgreementQueryResult> results = mongoTemplate.aggregate(aggregation, AgreementTemplate.class, AgreementQueryResult.class);
        return  results.getUniqueMappedResult();
    }
}