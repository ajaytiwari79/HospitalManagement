package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.AssetType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import javax.inject.Inject;
import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;



public class AssetTypeMongoRepositoryImpl implements CustomAssetTypeRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public AssetType findByName(Long countryId,Long organizationId, String name) {

        Query query=new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and("deleted").is(false).and("name").is(name).and(ORGANIZATION_ID).is(organizationId).and("isSubAsset").is(false));;
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.findOne(query,AssetType.class);



    }
}
