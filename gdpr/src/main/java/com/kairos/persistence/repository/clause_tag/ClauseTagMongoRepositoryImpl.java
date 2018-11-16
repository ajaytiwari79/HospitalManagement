package com.kairos.persistence.repository.clause_tag;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;

public class ClauseTagMongoRepositoryImpl implements CustomClauseTagRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<ClauseTag> findByCountryIdAndTitles(Long countryId, Set<String> titles) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and("deleted").is(false).and("name").in(titles));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, ClauseTag.class);
    }

    @Override
    public List<ClauseTag> findByUnitIdAndTitles(Long unitId, Set<String> titles) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ORGANIZATION_ID).is(unitId).and("deleted").is(false).and("name").in(titles));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, ClauseTag.class);
    }
}
