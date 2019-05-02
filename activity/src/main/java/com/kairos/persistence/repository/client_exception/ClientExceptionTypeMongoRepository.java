package com.kairos.persistence.repository.client_exception;

import com.kairos.persistence.model.client_exception.ClientExceptionType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Created by oodles on 14/2/17.
 */
@Repository
public interface ClientExceptionTypeMongoRepository extends MongoBaseRepository<ClientExceptionType,BigInteger> {

}
