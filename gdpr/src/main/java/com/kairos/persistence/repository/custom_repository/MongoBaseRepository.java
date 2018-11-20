package com.kairos.persistence.repository.custom_repository;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@NoRepositoryBean
public interface MongoBaseRepository<T extends MongoBaseEntity, ID extends Serializable> extends MongoRepository<T, ID> {

     T findOne(ID id);

     T safeDeleteById(ID id);

     boolean safeDeleteByIds(Set<ID> ids);

      <T extends MongoBaseEntity> List<T> safeDeleteAll(List<T> entities);


}
