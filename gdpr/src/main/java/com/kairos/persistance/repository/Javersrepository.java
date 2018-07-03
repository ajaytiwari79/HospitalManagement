package com.kairos.persistance.repository;


import com.kairos.persistance.model.JaversTest;
import org.javers.spring.annotation.JaversAuditable;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@JaversSpringDataAuditable
public interface Javersrepository extends MongoRepository<JaversTest,BigInteger> {
}
