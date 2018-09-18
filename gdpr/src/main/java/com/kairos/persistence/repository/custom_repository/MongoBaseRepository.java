package com.kairos.persistence.repository.custom_repository;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface MongoBaseRepository<T extends MongoBaseEntity, ID extends Serializable> extends MongoRepository<T, ID> {
    // T findOne(ID id);

     boolean findByIdAndSafeDelete(ID id);

}
