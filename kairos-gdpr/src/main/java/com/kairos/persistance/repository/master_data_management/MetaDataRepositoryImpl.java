package com.kairos.persistance.repository.master_data_management;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class MetaDataRepositoryImpl<T, ID extends Serializable,E extends Serializable> extends SimpleMongoRepository<T, ID> implements MetaDataRepository<T, ID> {


    private final MongoTemplate mongoTemplate;
    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;


    public MetaDataRepositoryImpl(MongoEntityInformation<T, ID> entityInformation,
                                  MongoOperations mongoOperations, T object, MongoTemplate mongoTemplate) {
        super(entityInformation, mongoOperations);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.mongoOperations = mongoOperations;
        this.entityInformation = entityInformation;
        this.mongoTemplate = mongoTemplate;

    }

    @Override
    public T getMetedataByIdAndDeletedIsFalse(Long countryId, ID id) {
        Assert.notNull(id, "The given id must not be null!");

        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("_id").is(id).and("deleted").is(false));
        return mongoTemplate.findOne(query, entityInformation.getJavaType());

    }

    @Override
    public T getMetaDataByName(Long countryId, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("name").is(name).and("deleted").is(false));
        return mongoOperations.findOne(query, entityInformation.getJavaType());

    }


    @Override
    public List<T> getMetaDataListByCountryId(Long countryId){

        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("deleted").is(false));
        return mongoOperations.find(query, entityInformation.getJavaType());

    }

    @Override
    public List<T> getMetaDataListByCountryIdAndName(Long countryId, Set<String> name) {

        Query query = new Query();
        query.addCriteria(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("name").in(name));
        return mongoOperations.find(query, entityInformation.getJavaType());


    }
}
