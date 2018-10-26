package com.kairos.scheduler.persistence.repository.custom_repository;

import com.kairos.scheduler.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface MongoBaseRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
	 T findOne(ID id);
	 T safeDeleteById(ID id);
	<T extends MongoBaseEntity> void safeDelete(T object);
	<T extends MongoBaseEntity> List<T> saveEntities(List<T> entities);
	//<T extends MongoBaseEntity> T save(T entity);


}
