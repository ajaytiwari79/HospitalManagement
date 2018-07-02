package com.kairos.service.common;


import com.kairos.persistance.model.common.JaversBaseEntity;
import com.kairos.utils.DateUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.List;

@Service
public class JaversBaseService {


    @Inject
    MongoTemplate mongoTemplate;

    @Inject
    DB database;

    private static final Logger logger = LoggerFactory.getLogger(JaversBaseService.class);


    public <T extends JaversBaseEntity> T save(T entity) {

        Assert.notNull(entity, "Entity must not be null!");
        /**
         *  Get class name for sequence class
         * */
        String className = entity.getClass().getSimpleName();
        /**
         *  Set createdAt if entity don't have createdAt
         * */
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(DateUtils.getDate());
        }
        /**
         *  Set updatedAt time as current time
         * */
        entity.setUpdatedAt(DateUtils.getDate());
        mongoTemplate.save(entity);
        return entity;
    }


    public <T extends JaversBaseEntity> List<T> save(List<T> entities) {
        Assert.notNull(entities, "Entity must not be null!");
        Assert.notEmpty(entities, "Entity must not be Empty!");

        String collectionName = mongoTemplate.getCollectionName(entities.get(0).getClass());

        /**
         *  Creating BulkWriteOperation object
         * */

        BulkWriteOperation bulkWriteOperation = database.getCollection(collectionName).initializeUnorderedBulkOperation();

        /**
         *  Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObject)
         * */
        MongoConverter converter = mongoTemplate.getConverter();

        BasicDBObject dbObject;

        /**
         *  Handling bulk write exceptions
         * */
        try {

            for (T entity : entities) {
                /**
                 *  Get class name for sequence class
                 * */
                String className = entity.getClass().getSimpleName();

                /**
                 *  Set createdAt if entity don't have createdAt
                 * */
                if (entity.getCreatedAt() == null) {
                    entity.setCreatedAt(DateUtils.getDate());
                }
                /**
                 *  Set updatedAt time as current time
                 * */
                entity.setUpdatedAt(DateUtils.getDate());

            }

            /**
             * Executing the Operation
             * */
            return entities;

        } catch (Exception ex) {
            logger.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }


}
