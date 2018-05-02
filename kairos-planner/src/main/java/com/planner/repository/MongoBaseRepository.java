package com.planner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface MongoBaseRepository<T,ID> extends MongoRepository<T,ID> {
    Optional<T> findByKairosId(BigInteger kairosId);
}
