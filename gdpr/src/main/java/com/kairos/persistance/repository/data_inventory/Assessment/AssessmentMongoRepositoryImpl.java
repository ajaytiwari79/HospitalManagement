package com.kairos.persistance.repository.data_inventory.Assessment;

import com.kairos.persistance.model.data_inventory.assessment.Assessment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;

import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;

public class AssessmentMongoRepositoryImpl implements CustomAssessmentRepository {



    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public Assessment findAssessmentByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Assessment.class);
    }
}
