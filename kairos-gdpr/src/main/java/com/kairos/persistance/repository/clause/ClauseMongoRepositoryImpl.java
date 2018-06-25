package com.kairos.persistance.repository.clause;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.enums.FilterType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.ID;
import static com.kairos.constants.AppConstant.DELETED;


public class ClauseMongoRepositoryImpl implements CustomClauseRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public Clause findByTitle(Long countryId, Long organizationId, String title) {

        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("title").is(title).and(ORGANIZATION_ID).is(organizationId));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Clause.class);
    }

    @Override
    public List<Clause> getClauseDataWithFilterSelection(Long countryId, Long organizationId, FilterSelectionDTO filterSelectionDto) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).is(organizationId));
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {

            if (filterSelection.getValue().size() != 0) {
                query.addCriteria(buildQuery(filterSelection, filterSelection.getName(), query));
            }
        });

        return mongoTemplate.find(query, Clause.class);

    }


    @Override
    public Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query) {

        switch (filterType) {
            case ACCOUNT_TYPES:
                List<BigInteger> ids = new ArrayList<>();
                for (Long id : filterSelection.getValue()) {
                    ids.add(BigInteger.valueOf(id));
                }
                return Criteria.where(filterType.value + ID).in(ids);
            case ORGANIZATION_TYPES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_TYPES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());
            case ORGANIZATION_SERVICES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_SERVICES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());
            default:
                throw new InvalidRequestException("data not found for Filtertype " + filterType);

        }


    }


}
