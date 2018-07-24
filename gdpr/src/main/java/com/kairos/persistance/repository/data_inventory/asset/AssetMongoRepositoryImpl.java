package com.kairos.persistance.repository.data_inventory.asset;

import com.kairos.persistance.model.data_inventory.asset.Asset;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.DELETED;



public class AssetMongoRepositoryImpl implements CustomAssetRepository {



    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public Asset findByName(Long countryid, Long organizationId, String name) {
        Query query=new Query(Criteria.where(COUNTRY_ID).is(countryid).and(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("name").is(name));
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return  mongoTemplate.findOne(query,Asset.class);

    }
}
