package com.kairos.persistence.repository.custom_repository;

import com.kairos.commons.audit_logging.AuditLogging;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.common.MongoSequence;
import com.kairos.persistence.model.counter.FibonacciKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import javax.validation.Valid;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.regex.Pattern;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

public class MongoBaseRepositoryImpl<T extends MongoBaseEntity, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements MongoBaseRepository<T, ID> {
	public static final String THE_GIVEN_ID_MUST_NOT_BE_NULL = "The given id must not be null!";
	public static final String DELETED = "deleted";
	private final MongoOperations mongoOperations;
	private final MongoEntityInformation<T, ID> entityInformation;
	MongoDatabase mongoDatabase;

	/**
	 *  Sequence collection name prefix
	 * */
	public static final String SEQUENCE_POST_FIX = "Sequence";

	public MongoBaseRepositoryImpl(MongoEntityInformation<T, ID>  entityInformation,
							  MongoOperations mongoOperations) {
		super(entityInformation, mongoOperations);
		mongoDatabase = ((MongoTemplate) mongoOperations).getDb();
		// Keep the EntityManager around to used from the newly introduced methods.
		this.mongoOperations = mongoOperations;
		this.entityInformation=entityInformation;
	}
	@Override
	public T findOne(ID  id) {
		Assert.notNull(id, THE_GIVEN_ID_MUST_NOT_BE_NULL);
		return mongoOperations.findById(id, entityInformation.getJavaType(), entityInformation.getCollectionName());
	}

	@Override
	public void safeDeleteById(ID id){
		Assert.notNull(id, THE_GIVEN_ID_MUST_NOT_BE_NULL);
		mongoOperations.findAndModify(new Query(Criteria.where("_id").is(id)),Update.update(DELETED,true),entityInformation.getJavaType(),entityInformation.getCollectionName());
	}

	@Override
	public <T extends MongoBaseEntity> void safeDelete(T object){
		Assert.notNull(object.getId(), THE_GIVEN_ID_MUST_NOT_BE_NULL);
		mongoOperations.updateFirst(new Query(Criteria.where("_id").is(object.getId())),Update.update(DELETED,true),entityInformation.getJavaType());
	}

	@Override
	public boolean existsByName(String name){
		Assert.notNull(name, "The given name must not be null!");
		return mongoOperations.exists(new Query(Criteria.where("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false)),entityInformation.getJavaType());
	}

	@Override
	public boolean existsByNameAndNotEqualToId(String name,BigInteger id){
		Assert.notNull(name, "The given name must not be null!");
		Assert.notNull(id, THE_GIVEN_ID_MUST_NOT_BE_NULL);
		return mongoOperations.exists(new Query(Criteria.where("_id").ne(id).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false)),entityInformation.getJavaType());
	}

	@Override
	public <T extends MongoBaseEntity> T findLastOrFirstByField(Sort sort){
		Assert.notNull(sort, "The given sort must not be null!");
		Query query = new Query(Criteria.where(DELETED).is(false));
		query.with(sort);
		return (T)mongoOperations.findOne(query,entityInformation.getJavaType());
	}


	/**
	 * @decription This method in used to generate mongodb sequence
	 * @param sequenceName
	 * @return sequenceNumber
	 * */
	public BigInteger nextSequence(String sequenceName){
		/**
		 * adding sequence postfix into class name
		 * */
		sequenceName = sequenceName + SEQUENCE_POST_FIX;

		/**
		 *  Find query
		 * */
		String findQuery = "{'sequenceName':'"+sequenceName+"'}";
		/**
		 *  Update query
		 * */
		String updateQuery = "{'$inc':{'sequenceNumber':1}}";
		FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();

		/**
		 *  return updated value
		 * */
		findAndModifyOptions.returnNew(true);

		/**
		 *  create new if not exists
		 * */
		findAndModifyOptions.upsert(true);
		MongoSequence mongoSequence = mongoOperations.findAndModify(new BasicQuery(findQuery), new BasicUpdate(updateQuery), findAndModifyOptions, MongoSequence.class);
		return new BigInteger(mongoSequence.getSequenceNumber()+"");
	}

	@Override
	public <S extends T> S save(@Valid S entity) {
		Assert.notNull(entity, "Entity must not be null!");
		/**
		 *  Get class name for sequence class
		 * */
		String className = entity.getClass().getSimpleName();

		/**
		 *  Set Id if entity don't have Id
		 * */
		S oldEntity = null;
		if(isNotNull(entity.getId())){
			oldEntity = (S)this.findOne((ID)entity.getId());
			oldEntity = isNull(oldEntity) ? createEntity(entity) : oldEntity;
		}else {
			oldEntity = createEntity(entity);
		}
		if(entity.getId() == null){
			if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
				//Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
				className = entity.getClass().getSuperclass().getSimpleName();
			}
			if(entity.getClass().equals(FibonacciKPI.class)){
				className = KPI.class.getSimpleName();
			}
			entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
			entity.setCreatedAt(DateUtils.getDate());
			entity.setId(nextSequence(className));
		}else {
			entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
		}
		/**
		 *  Set updatedAt time as current time
		 * */
		entity.setUpdatedAt(DateUtils.getDate());
		mongoOperations.save(entity);
		AuditLogging.doAudit(oldEntity,entity);
		return entity;
	}

	private <S> S createEntity(S entity){
		S oldEntity = null;
		try {
			oldEntity = (S) entity.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return oldEntity;
	}

	public <S extends T> Iterable<S> saveEntities(Iterable<S> entities) {
		for (S entity : entities) {
			save(entity);
		}
		return entities;
	}





}
