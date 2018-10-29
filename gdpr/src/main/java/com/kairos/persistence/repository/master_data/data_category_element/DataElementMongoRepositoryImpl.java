package com.kairos.persistence.repository.master_data.data_category_element;

import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;

public class DataElementMongoRepositoryImpl implements CustomDataElementRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<DataElement> findByCountryIdAndNames(Long countryId, Set<String> names) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DELETED).is(false).and("name").in(names).and(COUNTRY_ID).is(countryId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, DataElement.class);
    }

    @Override
    public List<DataElement> findByUnitIdAndNames(Long unitId, Set<String> names) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DELETED).is(false).and("name").in(names).and(ORGANIZATION_ID).is(unitId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, DataElement.class);

    }
}
