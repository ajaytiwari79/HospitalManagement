package com.kairos.persistence.repository.custom_repository;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.math.BigInteger;

@NoRepositoryBean
public interface MongoBaseRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
	 T findOne(ID id);
	 void safeDeleteById(ID id);
	<T extends MongoBaseEntity> void safeDelete(T object);
	<S extends T> Iterable<S> saveEntities(Iterable<S> entities);
	//<T extends MongoBaseEntity> T save(T entity);
	boolean existsByName(String name);
	boolean existsByNameAndNotEqualToId(String name,BigInteger id);
	<T extends MongoBaseEntity> T findLastOrFirstByField(Sort sort);
	BigInteger nextSequence(String sequenceName);


}
