package com.kairos.persistance.repository.master_data.processing_activity_masterdata.accessor_party;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistance.repository.client_aggregator.CustomAggregationOperation;
import com.kairos.persistance.repository.common.CustomAggregationQuery;
import com.kairos.response.dto.common.AccessorPartyReponseDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstant.*;

public class AccessorPartyMongoRepositoryImpl implements CustomAccessorPartyRepository {


    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public List<AccessorPartyReponseDTO> getAllNotInheritedAccesorPartyFromParentOrgAndUnitAccesorParty(Long countryId, Long parentOrganizationId, Long organizationId) {

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

        AggregationResults<AccessorPartyReponseDTO> results = mongoTemplate.aggregate(aggregation, AccessorParty.class, AccessorPartyReponseDTO.class);
        return results.getMappedResults();
    }

}
