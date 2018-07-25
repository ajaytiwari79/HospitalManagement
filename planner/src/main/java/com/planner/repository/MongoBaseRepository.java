package com.planner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.math.BigInteger;
import java.util.Optional;
@NoRepositoryBean
public interface MongoBaseRepository<T,ID> extends MongoRepository<T,ID> {
    Optional<T> findByKairosId(BigInteger kairosId);
}
