package com.kairos.persistence.repository.clause;

import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationTypeAndSubTypeIdDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.persistence.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistence.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.response.dto.clause.UnitLevelClauseResponseDTO;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstant.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class ClauseMongoRepositoryImpl implements CustomClauseRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    Document addNonDeletedTemplateTypeOperation = Document.parse(CustomAggregationQuery.addNonDeletedTemplateTyeField());


    @Override
    public Clause findByCountryIdAndTitleAndDescription(Long countryId, String title, String description) {

        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("title").is(title).and("description").is(description));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Clause.class);
    }


    @Override
    public Clause findByUnitIdAndTitleAndDescription(Long unitId, String title, String description) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false).and("title").is(title).and("description").is(description));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query, Clause.class);
    }

    @Override
    public List<Clause> findClauseByReferenceIdAndTitles(Long referenceId, boolean isUnitId, List<String> clauseTitles) {

        Criteria criteria = isUnitId ? Criteria.where(ORGANIZATION_ID).is(referenceId).and(DELETED).is(false).and("title").in(clauseTitles) : Criteria.where(COUNTRY_ID).is(referenceId).and(DELETED).is(false).and("title").in(clauseTitles);
        Query query = new Query();
        query.addCriteria(criteria);
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, Clause.class);
    }


    @Override
    public List<ClauseResponseDTO> findAllClauseByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup("templateType", "templateTypes", "_id", "templateTypes"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation),
                sort(Sort.Direction.DESC, "createdAt")
        );
        AggregationResults<ClauseResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, ClauseResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<UnitLevelClauseResponseDTO> findAllClauseByUnitId(Long unitId) {
        String groupOperationOnClauseAndTemplateType = "{'$group' : {'_id':{'_id':'$_id','title' : '$title','tags' : '$tags','description' : '$description','createdAt' : '$createdAt'},'templateTypes' :{$push: { $arrayElemAt: [ '$templateTypes', 0 ] }}}}";
        String projectOperationOnClauseAndTemplateType = "{ '$project' : {'_id':'$_id._id','title' : '$_id.title','tags' : '$_id.tags','description' :'$_id.description','createdAt' :'$_id.createdAt','templateTypes' : '$templateTypes'}}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).is(unitId).and(DELETED).is(false)),
                unwind("templateTypes"),
                lookup("templateType", "templateTypes", "_id", "templateTypes"),
                new CustomAggregationOperation(Document.parse(groupOperationOnClauseAndTemplateType)),
                new CustomAggregationOperation(Document.parse(projectOperationOnClauseAndTemplateType)),
                //project(Fields.fields("id", "title", "tags", "description", "createdAt")),
                sort(Sort.Direction.DESC, "createdAt")
        );
        AggregationResults<UnitLevelClauseResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, UnitLevelClauseResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public ClauseResponseDTO findClauseWithTemplateTypeById(Long countryId, BigInteger id) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("_id").is(id)),
                lookup("templateType", "templateTypes", "_id", "templateTypes"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation)


        );
        AggregationResults<ClauseResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, ClauseResponseDTO.class);
        return result.getUniqueMappedResult();
    }


    @Override
    public List<ClauseResponseDTO> getClauseDataWithFilterSelection(Long countryId, FilterSelectionDTO filterSelectionDto) {

        Criteria criteria = Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false);
        List<Criteria> clauseCriteria = new ArrayList<>(filterSelectionDto.getFiltersData().size());
        filterSelectionDto.getFiltersData().forEach(filterSelection -> {
            if (filterSelection.getValue().size() != 0) {
                clauseCriteria.add(buildMatchCriteria(filterSelection, filterSelection.getName()));
            }
        });
        if (!clauseCriteria.isEmpty()) {
            criteria = criteria.andOperator(clauseCriteria.toArray(new Criteria[clauseCriteria.size()]));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("templateType", "templateTypes", "_id", "templateTypes"),
                new CustomAggregationOperation(addNonDeletedTemplateTypeOperation),
                sort(Sort.Direction.DESC, "createdAt")

        );

        AggregationResults<ClauseResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, ClauseResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<ClauseBasicResponseDTO> findAllClauseByAgreementTemplateMetadataAndCountryId(Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO, BigInteger templateTypeId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("templateTypes").in(templateTypeId).and("organizationTypes._id").in(organizationMetaDataDTO.getOrganizationTypeId())
                        .and("organizationSubTypes._id").in(organizationMetaDataDTO.getOrganizationSubTypeIds()).and(("organizationServices._id")).in(organizationMetaDataDTO.getServiceCategoryIds())
                        .and("organizationSubServices._id").in(organizationMetaDataDTO.getSubServiceCategoryIds())),
                //lookup("templateType","templateTypeId","_id","templateTypes"),
                sort(Sort.Direction.DESC, "createdAt")
        );
        AggregationResults<ClauseBasicResponseDTO> result = mongoTemplate.aggregate(aggregation, Clause.class, ClauseBasicResponseDTO.class);
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
    public List<Clause> getClauseByCountryIdAndOrgTypeSubTypeCategoryAndSubCategory(Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO) {
        Aggregation aggregation = Aggregation.newAggregation(

                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("organizationTypes._id").in(organizationMetaDataDTO.getOrganizationTypeId())
                        .and("organizationSubTypes._id").in(organizationMetaDataDTO.getOrganizationSubTypeIds()).and(("organizationServices._id")).in(organizationMetaDataDTO.getServiceCategoryIds())
                        .and("organizationSubServices._id").in(organizationMetaDataDTO.getSubServiceCategoryIds()))
        );
        return mongoTemplate.aggregate(aggregation, Clause.class, Clause.class).getMappedResults();
    }
}
