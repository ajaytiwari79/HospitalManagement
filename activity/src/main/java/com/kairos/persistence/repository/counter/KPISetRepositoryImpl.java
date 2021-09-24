package com.kairos.persistence.repository.counter;
/*
 *Created By Pavan on 30/4/19
 *
 */

import com.kairos.persistence.model.counter.KPISet;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.AppConstants.DELETED;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class KPISetRepositoryImpl implements CustomKPISetRepository{
    public static final String REFERENCE_ID = "referenceId";
    public static final String ORG_TYPE_KPI_ENTRY = "orgTypeKPIEntry";
    public static final String KPI_IDS = "kpiIds";
    public static final String TIME_TYPE = "timeType";
    public static final String PHASE_ID = "phaseId";
    public static final String CONF_LEVEL = "confLevel";
    @Inject
    private MongoTemplate mongoTemplate;
    @Override
    public List<KPISet> findAllByCountryIdAndDeletedFalse(List<Long> orgSubTypeIds, Long countryId) {
        Criteria criteria=Criteria.where(REFERENCE_ID).is(countryId).and(DELETED).is(false);
        Aggregation aggregation=Aggregation.
                newAggregation(
                match(criteria),
                lookup(ORG_TYPE_KPI_ENTRY, KPI_IDS,"kpiId", ORG_TYPE_KPI_ENTRY),
                unwind(ORG_TYPE_KPI_ENTRY),
                match(Criteria.where("orgTypeKPIEntry.orgTypeId").in(orgSubTypeIds)),
                group("id", "name", KPI_IDS, TIME_TYPE, PHASE_ID, REFERENCE_ID, CONF_LEVEL).addToSet("orgTypeKPIEntry.kpiId").as(KPI_IDS),
                project().and(KPI_IDS).as(KPI_IDS).and("name").as("name").and(TIME_TYPE).as(TIME_TYPE)
                        .and(PHASE_ID).as(PHASE_ID).and(REFERENCE_ID).as(REFERENCE_ID).and(CONF_LEVEL).as(CONF_LEVEL)
        );
        AggregationResults<KPISet> result = mongoTemplate.aggregate(aggregation, KPISet.class, KPISet.class);
        return result.getMappedResults();
    }
}
