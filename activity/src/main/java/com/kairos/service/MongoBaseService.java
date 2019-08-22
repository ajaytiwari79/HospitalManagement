package com.kairos.service;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.utils.user_context.UserContext;
import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Created by Pankaj on 12/4/17.
 */
@Service
public class MongoBaseService {

    @Inject
    protected MongoSequenceRepository mongoSequenceRepository;

    @Inject
    protected MongoTemplate mongoTemplate;
    @Inject
    protected DB database;

    private static final Logger logger = LoggerFactory.getLogger(MongoBaseService.class);


    @Deprecated
    public <T extends MongoBaseEntity> T save(@Valid T entity){

        Assert.notNull(entity, "Entity must not be null!");
        /**
        *  Get class name for sequence class
        * */
        String className = entity.getClass().getSimpleName();

        /**
         *  Set Id if entity don't have Id
         * */
        if(entity.getId() == null){
            if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
                //Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
                className = entity.getClass().getSuperclass().getSimpleName();
            }
            entity.setCreatedAt(DateUtils.getDate());
            entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
            entity.setId(mongoSequenceRepository.nextSequence(className));
        }else {
            entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
        }
        /**
         *  Set updatedAt time as current time
         * */
        entity.setUpdatedAt(DateUtils.getDate());
        mongoTemplate.save(entity);
        return entity;
    }

    @Deprecated
    public <T extends MongoBaseEntity> List<T> save(@Valid List<T> entities){
        Assert.notNull(entities, "Entity must not be null!");
        Assert.notEmpty(entities, "Entity must not be Empty!");

        String collectionName = mongoTemplate.getCollectionName(entities.get(0).getClass());

        /**
         *  Creating BulkWriteOperation object
         * */

        BulkWriteOperation bulkWriteOperation= database.getCollection(collectionName).initializeUnorderedBulkOperation();

        /**
         *  Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObject)
         * */
        MongoConverter converter = mongoTemplate.getConverter();

        BasicDBObject dbObject;

        /**
         *  Handling bulk write exceptions
         * */
        try{

            for (T entity: entities) {
                /**
                 *  Get class name for sequence class
                 * */
                String className = entity.getClass().getSimpleName();
                /**
                 *  Set updatedAt time as current time
                 * */
                entity.setUpdatedAt(DateUtils.getDate());


                if(entity.getId() == null){
                    entity.setCreatedAt(DateUtils.getDate());
                    /**
                     *  Set Id if entity don't have Id
                     * */
                    if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
                        //Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
                        className = entity.getClass().getSuperclass().getSimpleName();
                    }
                    entity.setId(mongoSequenceRepository.nextSequence(className));
                    entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
                    dbObject = new BasicDBObject();

                    /*
                    *  Converting entity object to BasicDBObject
                    * */
                    converter.write(entity, dbObject);

                    /*
                    *  Adding entity (BasicDBObject)
                    * */
                    bulkWriteOperation.insert(dbObject);
                }else {
                    entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
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
            }

            /**
             * Executing the Operation
             * */
            bulkWriteOperation.execute();
            return entities;

        } catch(Exception ex){
            logger.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }

    @Deprecated
    public <T extends MongoBaseEntity> Set<T> save(@Valid Set<T> entities){
        Assert.notNull(entities, "Entity must not be null!");
        Assert.notEmpty(entities, "Entity must not be Empty!");

        String collectionName = mongoTemplate.getCollectionName(entities.iterator().next().getClass());

        /**
         *  Creating BulkWriteOperation object
         * */

        BulkWriteOperation bulkWriteOperation= database.getCollection(collectionName).initializeUnorderedBulkOperation();

        /**
         *  Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObject)
         * */
        MongoConverter converter = mongoTemplate.getConverter();

        BasicDBObject dbObject;

        /**
         *  Handling bulk write exceptions
         * */
        try{

            for (T entity: entities) {
                /**
                 *  Get class name for sequence class
                 * */
                String className = entity.getClass().getSimpleName();

                /**
                 *  Set updatedAt time as current time
                 * */
                entity.setUpdatedAt(DateUtils.getDate());


                if(entity.getId() == null){
                    entity.setCreatedAt(DateUtils.getDate());
                    /**
                     *  Set Id if entity don't have Id
                     * */
                    if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
                        //Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
                        className = entity.getClass().getSuperclass().getSimpleName();
                    }
                    entity.setId(mongoSequenceRepository.nextSequence(className));

                    dbObject = new BasicDBObject();

                    /*
                     *  Converting entity object to BasicDBObject
                     * */
                    converter.write(entity, dbObject);

                    /*
                     *  Adding entity (BasicDBObject)
                     * */
                    bulkWriteOperation.insert(dbObject);
                }else {

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
            }

            /**
             * Executing the Operation
             * */
            bulkWriteOperation.execute();
            return entities;

        } catch(Exception ex){
            logger.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }
}
