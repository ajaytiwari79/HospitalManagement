package com.kairos.persistance.repository.clause_tag;

import com.kairos.persistance.model.clause_tag.ClauseTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ClauseTagMongoRepository extends MongoRepository<ClauseTag,BigInteger> {

    ClauseTag findByid(BigInteger id);

    ClauseTag findByName(String name);

}
