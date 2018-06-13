package com.kairos.persistance.repository.clause;

import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.enums.FilterType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constant.AppConstant.COUNTRY_ID;
import static com.kairos.constant.AppConstant.ID;
import static com.kairos.constant.AppConstant.DELETED;


public class ClauseMongoRepositoryImpl implements CustomClauseRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public List<Clause> getClauseDataWithFilterSelection(Long countryId, FilterSelectionDto filterSelectionDto) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false));
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {

            if (filterSelection.getValue().size()!=0) {
                query.addCriteria(buildQuery(filterSelection, filterSelection.getName(), query));
            }
        });

        return mongoTemplate.find(query, Clause.class);

    }


    @Override
    public Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query) {

        switch (filterType) {
            case ACCOUNT_TYPES:
                return Criteria.where(filterType.value + ID).in(filterSelection.getValue());
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
