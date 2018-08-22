package com.kairos.persistance.repository.custom_repository;

import com.kairos.config.SpringContext;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.repository.common.MongoSequenceRepository;
import com.kairos.utils.DateUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class MongoBaseRepositoryImpl<T extends MongoBaseEntity, ID extends Serializable>
        extends SimpleMongoRepository<T, ID> implements MongoBaseRepository<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(MongoBaseRepositoryImpl.class);

    private final MongoSequenceRepository mongoSequenceRepository;
    private final MongoEntityInformation<T, ID> entityInformation;
    private final MongoOperations mongoOperations;
    private DB database;

    public MongoBaseRepositoryImpl(MongoEntityInformation<T, ID> entityInformation,
                                   MongoOperations mongoOperations) throws Exception {
        super(entityInformation, mongoOperations);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.mongoOperations = mongoOperations;
        this.entityInformation = entityInformation;
        this.mongoSequenceRepository = new MongoSequenceRepository(mongoOperations);
        ApplicationContext context = SpringContext.getAppContext();
        this.database = (DB)context.getBean("mongoDbBean");
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
    public <S extends T> List<S> saveAll(Iterable<S> entitieList) {


        Assert.notNull(entitieList, "Entity must not be null!");
        Streamable<S> source = Streamable.of(entitieList);
        List<S> entities = source.stream().collect(Collectors.toList());
        Assert.notEmpty(entities, "Entity must not be Empty!");

        String collectionName = mongoOperations.getCollectionName(entities.get(0).getClass());

        // Creating BulkWriteOperation object
        BulkWriteOperation bulkWriteOperation = database.getCollection(collectionName).initializeUnorderedBulkOperation();

        // Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObjectCode)
        MongoConverter converter = mongoOperations.getConverter();

        BasicDBObject dbObject;

        // Handling bulk write exceptions
        try {

            for (T entity : entities) {
                //  Get class name for sequence class
                String className = entity.getClass().getSimpleName();

                //  Set createdAt if entity don't have createdAt
                if (entity.getCreatedAt() == null) {
                    entity.setCreatedAt(DateUtils.getDate());
                }
                //  Set updatedAt time as current time
                entity.setUpdatedAt(DateUtils.getDate());


                if (entity.getId() == null) {
                    //  Set Id if entity don't have Id
                    entity.setId(mongoSequenceRepository.nextSequence(className));

                    dbObject = new BasicDBObject();

                    //  Converting entity object to BasicDBObject
                    converter.write(entity, dbObject);

                    //  Adding entity (BasicDBObject)
                    bulkWriteOperation.insert(dbObject);
                } else {

                    dbObject = new BasicDBObject();

                    //  Converting entity object to BasicDBObject
                    converter.write(entity, dbObject);

                    //  Creating BasicDbObjectCode for find query
                    BasicDBObject query = new BasicDBObject();

                    //  Adding query (find by ID)
                    query.put("_id", dbObject.get("_id"));

                    //  Replacing whole Object
                    bulkWriteOperation.find(query).replaceOne(dbObject);
                }
            }

            // Executing the Operation
          bulkWriteOperation.execute();
            return entities;

        } catch (Exception ex) {
            logger.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }
}