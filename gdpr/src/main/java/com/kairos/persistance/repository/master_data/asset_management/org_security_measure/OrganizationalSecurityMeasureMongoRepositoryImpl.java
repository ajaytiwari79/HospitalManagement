package com.kairos.persistance.repository.master_data.asset_management.org_security_measure;

import com.kairos.persistance.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.metadata.OrganizationalSecurityMeasureResponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ReplaceRootOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.DELETED;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;


public class OrganizationalSecurityMeasureMongoRepositoryImpl implements CustomOrganizationalSecurityRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public List<OrganizationalSecurityMeasureResponseDTO> getAllNotInheritedFromParentOrgAndUnitOrgSecurityMeasure(Long countryId, Long parentOrganizationId, Long organizationId) {
        Document groupOPerationForDuplicateDataOnInheritingFromParentOrg = Document.parse(CustomAggregationQuery.metaDataGroupInheritParentOrgMetaDataAndOrganizationMetadata());
        Document projectionForFilteringDuplicateDataOfOrgAndParentOrg = Document.parse(CustomAggregationQuery.metaDataProjectionForRemovingDuplicateInheritedMetaData(organizationId));
        Document projectionOperation = Document.parse(CustomAggregationQuery.metaDataProjectionforAddingFinalDataObject());
        Document replaceRootOperation = Document.parse(CustomAggregationQuery.metaDataReplaceRoot());


        List<Long> orgIdList = new ArrayList<>();
        orgIdList.add(organizationId);
        orgIdList.add(parentOrganizationId);

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(ORGANIZATION_ID).in(orgIdList)),
                new CustomAggregationOperation(groupOPerationForDuplicateDataOnInheritingFromParentOrg),
                new CustomAggregationOperation(projectionForFilteringDuplicateDataOfOrgAndParentOrg),
                new CustomAggregationOperation(projectionOperation),
                new CustomAggregationOperation(replaceRootOperation)

        );

        AggregationResults<OrganizationalSecurityMeasureResponseDTO> results = mongoTemplate.aggregate(aggregation, OrganizationalSecurityMeasure.class, OrganizationalSecurityMeasureResponseDTO.class);
        return results.getMappedResults();
    }
}
