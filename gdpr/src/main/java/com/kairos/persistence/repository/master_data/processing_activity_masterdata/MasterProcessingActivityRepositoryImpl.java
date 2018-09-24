package com.kairos.persistence.repository.master_data.processing_activity_masterdata;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationMetaDataDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityRiskResponseDTO;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.ID;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class MasterProcessingActivityRepositoryImpl implements CustomMasterProcessingActivity {


    @Inject
    private MongoTemplate mongoTemplate;


    Document projectionOperation = Document.parse(CustomAggregationQuery.processingActivityWithSubProcessingNonDeletedData());


    @Override
    public MasterProcessingActivity findByName(Long countryId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").is(name).and("subProcess").is(false));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, MasterProcessingActivity.class);


    }

    @Override
    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessingActivity(Long countryId,BigInteger id) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and("_id").is(id).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("master_processing_activity", "subProcessingActivityIds", "_id", "subProcessingActivities")
                , new CustomAggregationOperation(projectionOperation)
        );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getUniqueMappedResult();
    }

    @Override
    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("subProcess").is(false)),
                lookup("master_processing_activity", "subProcessingActivityIds", "_id", "subProcessingActivities")
                , new CustomAggregationOperation(projectionOperation),
                sort(Sort.Direction.DESC,"id")
        );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getMappedResults();

    }

    @Override
    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityWithFilterSelection(Long countryId, FilterSelectionDTO filterSelectionDto) {


        Criteria criteria = Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("subProcess").is(false);
        List<Criteria> processingActivityCriteriaList = new ArrayList<>(filterSelectionDto.getFiltersData().size());
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {
            if (filterSelection.getValue().size() != 0) {
                processingActivityCriteriaList.add(buildMatchCriteria(filterSelection, filterSelection.getName()));
            }
        });

        if (!processingActivityCriteriaList.isEmpty()) {
            criteria = criteria.andOperator(processingActivityCriteriaList.toArray(new Criteria[processingActivityCriteriaList.size()]));

        }
        Aggregation aggregation = Aggregation.newAggregation(

                match(criteria),
                lookup("master_processing_activity", "subProcessingActivityIds", "_id", "subProcessingActivities")
                , new CustomAggregationOperation(projectionOperation),
                sort(Sort.Direction.DESC, "id")

                );
        AggregationResults<MasterProcessingActivityResponseDTO> result = mongoTemplate.aggregate(aggregation, MasterProcessingActivity.class, MasterProcessingActivityResponseDTO.class);
        return result.getMappedResults();


    }

    @Override
    public Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType) {
        switch (filterType) {
            case ACCOUNT_TYPES:
                return Criteria.where("accountTypes" + ID).in(filterSelection.getValue());
            case ORGANIZATION_TYPES:
                return Criteria.where("organizationTypes" + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_TYPES:
                return Criteria.where("organizationSubTypes" + ID).in(filterSelection.getValue());
            case ORGANIZATION_SERVICES:
                return Criteria.where("organizationServices" + ID).in(filterSelection.getValue());

            case ORGANIZATION_SUB_SERVICES:
                return Criteria.where("organizationSubServices" + ID).in(filterSelection.getValue());
            default:
                throw new InvalidRequestException("data not found for FilterType " + filterType);


        }
    }


    @Override
    public List<MasterProcessingActivity> getMasterProcessingActivityByOrgTypeSubTypeCategoryAndSubCategory(Long countryId,  OrganizationMetaDataDTO organizationMetaDataDTO) {
        Query query = new Query(Criteria.where(COUNTRY_ID).is(countryId)
                .and(DELETED).is(false));
        query.addCriteria(Criteria.where("organizationTypes._id").in(organizationMetaDataDTO.getOrganizationService().getId()));
        query.addCriteria(Criteria.where("organizationSubTypes._id").in(organizationMetaDataDTO.getOrganizationSubType().getId()));
        query.addCriteria(Criteria.where("organizationServices._id").in(organizationMetaDataDTO.getOrganizationService().getId()));
        query.addCriteria(Criteria.where("organizationSubServices._id").in(organizationMetaDataDTO.getOrganizationSubService().getId()));
        return mongoTemplate.find(query, MasterProcessingActivity.class);

    }


    @Override
    public List<MasterProcessingActivityRiskResponseDTO> getAllProcessingActivityWithLinkedRisksAndSubProcessingActivitiesByCountryId(Long countryId) {

        String addNonDeletedRisks="{'$addFields':{'risks':{'$filter':{ 'input':'$risks', 'as':'risk','cond':{'$eq':['$$risk.deleted',false]} }} }}";
        String groupSubProcessingActivities="{'$group':{'_id':'$_id','processingActivities':{'$addToSet':'$subProcessingActivities'},'name':{'$first':'$name'},'risks':{'$first':'$risks'}}}"
;      Aggregation aggregation=Aggregation.newAggregation(
              match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("subProcess").is(false)),
              lookup("risk","risks","_id","risks"),
              lookup("master_processing_activity","subProcessingActivityIds","_id","subProcessingActivities"),
              unwind("subProcessingActivities",true),
              lookup("risk","subProcessingActivities.risks","_id","subProcessingActivities.risks"),
              new CustomAggregationOperation(Document.parse(groupSubProcessingActivities)),
              sort(Sort.Direction.DESC,"id"),
                new CustomAggregationOperation(Document.parse(addNonDeletedRisks))
      );
      AggregationResults<MasterProcessingActivityRiskResponseDTO> result=mongoTemplate.aggregate(aggregation,MasterProcessingActivity.class,MasterProcessingActivityRiskResponseDTO.class);
      return result.getMappedResults();
    }

    
}
