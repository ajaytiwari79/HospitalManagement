package com.kairos.persistence.repository.custom_repository;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.commons.utils.DateUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class MongoBaseRepositoryImpl<T extends MongoBaseEntity, ID extends Serializable>
        extends SimpleMongoRepository<T, ID> implements MongoBaseRepository<T, ID> {


    private final Logger LOGGER = LoggerFactory.getLogger(MongoBaseRepositoryImpl.class);
    private final MongoSequenceRepository mongoSequenceRepository;
    private final MongoEntityInformation<T, ID> entityInformation;
    private final MongoOperations mongoOperations;

    public MongoBaseRepositoryImpl(MongoEntityInformation<T, ID> entityInformation,
                                   MongoOperations mongoOperations) {
        super(entityInformation, mongoOperations);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.mongoOperations = mongoOperations;
        this.entityInformation = entityInformation;
        this.mongoSequenceRepository = new MongoSequenceRepository(mongoOperations);
    }


    @Override
    public <S extends T> S save(S entity) {

        Assert.notNull(entity, "Entity must not be null!");

        // Get class name for sequence class
        String className = entity.getClass().getSimpleName();

        // Set Id if entity don't have Id
        if (entity.getId() == null) {
            entity.setId(mongoSequenceRepository.nextSequence(className));
        }

        // Set createdAt if entity don't have createdAt
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(DateUtils.getDate());
        }
        // Set updatedAt time as current time
        entity.setUpdatedAt(DateUtils.getDate());
        mongoOperations.save(entity);
        return entity;
    }


    @Override
    public T findOne(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        return mongoOperations.findOne(new Query(Criteria.where("deleted").is(false).and("_id").is(id)), entityInformation.getJavaType());
    }


    @Override
    public T safeDeleteById(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        Query query = new Query(Criteria.where("_id").is(id).and("deleted").is(false));
        Update update = new Update().set("deleted", true);
        update.set("updatedAt", DateUtils.getDate());
        T entity = mongoOperations.findAndModify(query, update, entityInformation.getJavaType());
        if (!Optional.ofNullable(entity).isPresent()) {
            throw new DataNotFoundByIdException("invalid request " + entityInformation.getJavaType().getSimpleName() + " id " + id);
        }
        return entity;
    }


    @Override
    public boolean safeDeleteByIds(Set<ID> ids) {
        Assert.notEmpty(ids, "Id List cant be Empty !");
        Update update = new Update().set("deleted", true);
        update.set("updatedAt", DateUtils.getDate());
        mongoOperations.updateMulti(new Query(Criteria.where("_id").in(ids).and("deleted").is(false)), update, entityInformation.getJavaType());
        return true;
    }


    @Override
    public <T extends MongoBaseEntity> List<T> safeDeleteAll(List<T> entities) {
        Assert.notNull(entities, "Entity must not be null!");
        String collectionName = mongoOperations.getCollectionName(entities.get(0).getClass());

        BulkWriteOperation bulkWriteOperation = ((MongoTemplate) mongoOperations).getMongoDbFactory().getLegacyDb().getCollection(collectionName).initializeUnorderedBulkOperation();

        MongoConverter converter = mongoOperations.getConverter();


        BasicDBObject dbObject;

        //  Get class name for sequence class
        for (T entity : entities) {


            entity.setDeleted(true);
            entity.setUpdatedAt(DateUtils.getDate());
            dbObject = new BasicDBObject();
            /*
             *  Converting entity object to BasicDBObject
             * */
            converter.write(entity, dbObject);
            /**
             *  Creating BasicDbObject for find query
             * */
            BasicDBObject query = new BasicDBObject();
            /**
             *  Adding query (find by ID)
             * */
            query.put("_id", dbObject.get("_id"));
            /**
             *  Replacing whole Object
             * */
            bulkWriteOperation.find(query).replaceOne(dbObject);
        }

        bulkWriteOperation.execute();
        return entities;
    }

}