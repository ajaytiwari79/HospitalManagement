package com.planner.repository.common;

import com.planner.domain.MongoBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface MongoBaseRepository<T, ID> extends MongoRepository<T, ID> {
    Optional<T> findByKairosId(BigInteger kairosId);

    List<T> findAllNotDeleted();

    <T extends MongoBaseEntity> T saveObject(T entity);

    boolean safeDeleteById(String id);

    boolean isNameExists(String name);

    <T extends MongoBaseEntity> boolean safeDeleteByObject(T o);
}
