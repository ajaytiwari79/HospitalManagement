package com.planner.repository.common;

import com.planner.domain.MongoBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.math.BigInteger;
import java.util.Optional;
@NoRepositoryBean
public interface MongoBaseRepository<T,ID> extends MongoRepository<T,ID> {
    Optional<T> findByKairosId(BigInteger kairosId);

    boolean safeDeleteById(BigInteger id,Class clasName);

    <T extends MongoBaseEntity>boolean safeDeleteByObject(T o);
}
