package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.AssetType;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AssetTypeMongoRepositoryImpl implements CustomStorageTypeRepository {


    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public List<AssetType> getAllStorageListByNamesAndCountryList(Long countryId, String name) {

        Locale locale=LocaleContextHolder.getLocale();
        Collation collation=Collation.of("en");
        collation.strength(2);

        Query query=new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("name").is(name));;
        query.collation(collation);
        return mongoTemplate.find(new BasicQuery("{'countryId':"+countryId+",'deleted':false,'name':'string'}").collation(collation),AssetType.class);



    }
}
