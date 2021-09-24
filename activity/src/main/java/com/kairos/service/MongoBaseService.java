package com.kairos.service;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
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
import javax.validation.Valid;
import java.util.List;

/**
 * Created by Pankaj on 12/4/17.
 */
@Service
public class MongoBaseService {

    public static final String ENTITY_MUST_NOT_BE_NULL = "Entity must not be null!";
    @Inject
    protected MongoSequenceRepository mongoSequenceRepository;

    @Inject
    protected MongoTemplate mongoTemplate;
    @Inject
    protected DB database;

    private static final Logger logger = LoggerFactory.getLogger(MongoBaseService.class);


    @Deprecated
    public <T extends MongoBaseEntity> T save(@Valid T entity){

        Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);
        /**
        *  Get class name for sequence class
        * */
        String className = entity.getClass().getSimpleName();

        /**
         *  Set Id if entity don't have Id
         * */
        updateEntityDetails(entity, className);
        /**
         *  Set updatedAt time as current time
         * */
        entity.setUpdatedAt(DateUtils.getDate());
        mongoTemplate.save(entity);
        return entity;
    }

    private <T extends MongoBaseEntity> void updateEntityDetails(@Valid T entity, String className) {
        if(entity.getId() == null){
            if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
                //Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
                className = entity.getClass().getSuperclass().getSimpleName();
            }
            entity.setCreatedAt(DateUtils.getDate());
            entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName(),UserContext.getUserDetails().isManagement() ? AccessGroupRole.MANAGEMENT : AccessGroupRole.STAFF));
            entity.setId(mongoSequenceRepository.nextSequence(className));
        }else {
            entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName(),UserContext.getUserDetails().isManagement() ? AccessGroupRole.MANAGEMENT : AccessGroupRole.STAFF));
        }
    }

    @Deprecated
    public <T extends MongoBaseEntity> List<T> save(@Valid List<T> entities){
        Assert.notNull(entities, ENTITY_MUST_NOT_BE_NULL);
        Assert.notEmpty(entities, "Entity must not be Empty!");
        String collectionName = mongoTemplate.getCollectionName(entities.get(0).getClass());
        BulkWriteOperation bulkWriteOperation= database.getCollection(collectionName).initializeUnorderedBulkOperation();
        MongoConverter converter = mongoTemplate.getConverter();
        BasicDBObject dbObject;
        try{
            for (T entity: entities) {
                updateBulkWriteOperation(bulkWriteOperation, converter, entity);
            }
            bulkWriteOperation.execute();
            return entities;

        } catch(Exception ex){
            logger.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }

    private <T extends MongoBaseEntity> void updateBulkWriteOperation(BulkWriteOperation bulkWriteOperation, MongoConverter converter, T entity) {
        /**
         *  Get class name for sequence class
         * */
        String className = entity.getClass().getSimpleName();
        /**
         *  Set updatedAt time as current time
         * */
        entity.setUpdatedAt(DateUtils.getDate());


        if(entity.getId() == null){
            updateEntityDetailsWithBulkOperation(bulkWriteOperation, converter, entity, className);
        }else {
            updateBasicDBObject(bulkWriteOperation, converter, entity);
        }
    }

    private <T extends MongoBaseEntity> void updateBasicDBObject(BulkWriteOperation bulkWriteOperation, MongoConverter converter, T entity) {
        BasicDBObject dbObject;
        entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName(),UserContext.getUserDetails().isManagement() ? AccessGroupRole.MANAGEMENT : AccessGroupRole.STAFF));
        dbObject = new BasicDBObject();
        converter.write(entity, dbObject);
        BasicDBObject query = new BasicDBObject();
        query.put("_id", dbObject.get("_id"));
        bulkWriteOperation.find(query).replaceOne(dbObject);
    }

    private <T extends MongoBaseEntity> void updateEntityDetailsWithBulkOperation(BulkWriteOperation bulkWriteOperation, MongoConverter converter, T entity, String className) {
        BasicDBObject dbObject;
        entity.setCreatedAt(DateUtils.getDate());
        if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
            //Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
            className = entity.getClass().getSuperclass().getSimpleName();
        }
        entity.setId(mongoSequenceRepository.nextSequence(className));
        entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName(),UserContext.getUserDetails().isManagement() ? AccessGroupRole.MANAGEMENT : AccessGroupRole.STAFF));
        dbObject = new BasicDBObject();
         converter.write(entity, dbObject);
        bulkWriteOperation.insert(dbObject);
    }
}
